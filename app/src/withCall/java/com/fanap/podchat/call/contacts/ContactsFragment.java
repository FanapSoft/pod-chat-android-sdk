package com.fanap.podchat.call.contacts;

import android.content.Context;
import android.os.Bundle;
import androidx.core.app.Fragment;
import androidx.v7.widget.LinearLayoutManager;
import androidx.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fanap.podchat.example.R;

import java.util.ArrayList;


public class ContactsFragment extends Fragment {

    public interface IContactsFragment {

        void onContactsSelected(ContactsWrapper contact, int callType);
    }


    IContactsFragment callback;

    RecyclerView recyclerView;

    ArrayList<ContactsWrapper> contacts = new ArrayList<>();
    ContactsAdaptor adaptor;

    View viewAddContact;

    View viewCreateGroupCall;

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

        viewAddContact.setOnClickListener(v-> Toast.makeText(getContext(), "فعلا این قابلیت اضافه نشده", Toast.LENGTH_SHORT).show());
        viewCreateGroupCall.setOnClickListener(v-> Toast.makeText(getContext(), "فعلا این قابلیت اضافه نشده", Toast.LENGTH_SHORT).show());

    }

    private void setRecyclerView(View view) {
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adaptor = new ContactsAdaptor(contacts, view.getContext(), false,
                (contactsWrapper, callType) -> {
                    Log.d("TAGG", "Selected " + contactsWrapper.getFirstName());
                    if (callback != null) {
                        callback.onContactsSelected(contactsWrapper,callType);
                    }
                });

        recyclerView.setAdapter(adaptor);
    }

    private void initial(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewContacts);
        viewAddContact = view.findViewById(R.id.viewAddContact);
        viewCreateGroupCall = view.findViewById(R.id.viewCreateGroupCall);
    }


    public void updateList(ArrayList<ContactsWrapper> contactsWrappers) {
        contacts.clear();
        contacts.addAll(contactsWrappers);
        adaptor.notifyDataSetChanged();
    }
}