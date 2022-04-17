package com.fanap.podchat.call.contacts;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintHelper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.fanap.podchat.example.R;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;


public class ContactsFragment extends Fragment {

    public interface IContactsFragment {

        void onContactsSelected(ContactsWrapper contact, int callType);

        void onAddContactSelected(String name, String lastName, String id, int idType);

        void onListOfContactsSelectedForAudioCall(String callName, ArrayList<Long> selectContactIds);

        void onListOfContactsSelectedForVideoCall(String callName, ArrayList<Long> selectContactIds);
    }


    IContactsFragment callback;

    RecyclerView recyclerView;

    ArrayList<ContactsWrapper> contacts = new ArrayList<>();
    ArrayList<ContactsWrapper> selected = new ArrayList<>();
    ContactsAdaptor adaptor;

    View viewAddContact;

    View viewCreateGroupCall;

    ViewSwitcher switcher;

    RadioGroup radioGroup;
    RadioButton rbPhone, rbEmail, rbUserName;
    EditText etName, etLastName, etIdentity, etGroupName;
    Button btnAddContact;
    ImageButton imgBtnVideoCall, imgBtnAudioCall;
    ConstraintHelper gpGroupCallViews;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callback = (IContactsFragment) context;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Attach failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            contacts = getArguments().getParcelableArrayList("CONTACTS");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        initial(view);

        setRecyclerView(view);

        setListeners();

        return view;
    }

    private void setListeners() {

        viewAddContact.setOnClickListener(v -> switcher.showNext());
        viewCreateGroupCall.setOnClickListener(v -> {
            if (adaptor != null) {
                adaptor.switchMultiSelect();
                gpGroupCallViews.setVisibility(gpGroupCallViews.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            } else {
                Toast.makeText(getContext(), "فعلا این قابلیت اضافه نشده", Toast.LENGTH_SHORT).show();
            }
        });
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> findSelectedIdType(group));

        btnAddContact.setOnClickListener(v -> {

            String name = etName.getText().toString();
            String lastName = etLastName.getText().toString();
            String id = etIdentity.getText().toString();
            int idType = etIdentity.getInputType();

            callback.onAddContactSelected(name, lastName, id, idType);

        });

        imgBtnVideoCall.setOnClickListener(v -> {

            String gpName = etGroupName.getText().toString();

            ArrayList<Long> selectContactIds = new ArrayList<>();

            String callName = gpName;
            for (ContactsWrapper contact :
                    selected) {
                selectContactIds.add(contact.getId());
                if (Util.isNullOrEmpty(callName)) {
                    callName += contact.getFirstName() + ", ";
                }
            }

            callback.onListOfContactsSelectedForVideoCall(callName, selectContactIds);

        });
        imgBtnAudioCall.setOnClickListener(v -> {

            String gpName = etGroupName.getText().toString();

            ArrayList<Long> selectContactIds = new ArrayList<>();

            String callName = gpName;
            for (ContactsWrapper contact :
                    selected) {
                selectContactIds.add(contact.getId());
                if (Util.isNullOrEmpty(callName)) {
                    callName += contact.getFirstName() + " " + (contact.getLastName()!=null?contact.getLastName():"") + ", ";
                }
            }

            callback.onListOfContactsSelectedForAudioCall(callName, selectContactIds);

        });
    }


    private void findSelectedIdType(RadioGroup group) {

        if (group.getCheckedRadioButtonId() == rbPhone.getId()) {
            etIdentity.setHint("شماره همراه");
            etIdentity.setInputType(EditorInfo.TYPE_CLASS_PHONE);
            return;
        }

        if (group.getCheckedRadioButtonId() == rbEmail.getId()) {
            etIdentity.setHint("ایمیل");
            etIdentity.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            return;
        }

        if (group.getCheckedRadioButtonId() == rbUserName.getId()) {
            etIdentity.setHint("نام کاربری");
            etIdentity.setInputType(EditorInfo.TYPE_CLASS_TEXT);
            return;
        }

        etIdentity.setHint("شماره همراه");
        etIdentity.setInputType(EditorInfo.TYPE_CLASS_PHONE);

    }

    private void setRecyclerView(View view) {
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adaptor = new ContactsAdaptor(contacts, view.getContext(), false,
                new ContactsAdaptor.IContactInterface() {
                    @Override
                    public void onContactSelected(ContactsWrapper wrapper, int callType) {
                        if (callback != null) {
                            callback.onContactsSelected(wrapper, callType);
                        }
                    }

                    @Override
                    public void onContactAddedToGroupCallList(ContactsWrapper wrapper) {
                        selected.add(wrapper);
                    }

                    @Override
                    public void onContactRemovedFromGroupCallList(ContactsWrapper wrapper) {
                        selected.remove(wrapper);
                    }
                });

        recyclerView.setAdapter(adaptor);
    }

    private void initial(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewContacts);
        viewAddContact = view.findViewById(R.id.viewAddContact);
        switcher = view.findViewById(R.id.viewSwitcherAddContact);
        viewCreateGroupCall = view.findViewById(R.id.viewCreateGroupCall);
        radioGroup = view.findViewById(R.id.rgIdentityType);
        rbEmail = view.findViewById(R.id.rbEmail);
        rbPhone = view.findViewById(R.id.rbCellPhoneNumber);
        rbUserName = view.findViewById(R.id.rbUserName);

        etIdentity = view.findViewById(R.id.etId);
        etName = view.findViewById(R.id.etName);
        etLastName = view.findViewById(R.id.etLastName);
        btnAddContact = view.findViewById(R.id.btnAddContact);

        etGroupName = view.findViewById(R.id.etGroupName);
        gpGroupCallViews = view.findViewById(R.id.gpGroupCallState);
        imgBtnVideoCall = view.findViewById(R.id.imgBtnVideoCallGroup);
        imgBtnAudioCall = view.findViewById(R.id.imgBtnCallGroup);

    }


    public void updateList(ArrayList<ContactsWrapper> contactsWrappers) {
        contacts.clear();
        contacts.addAll(contactsWrappers);
        adaptor.notifyDataSetChanged();
    }
}