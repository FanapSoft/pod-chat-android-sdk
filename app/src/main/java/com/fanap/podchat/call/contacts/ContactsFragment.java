package com.fanap.podchat.call.contacts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fanap.podchat.example.R;

import java.util.ArrayList;


public class ContactsFragment extends Fragment {


    RecyclerView recyclerView;

    ArrayList<ContactsWrapper> contacts = new ArrayList<>();
    ContactsAdaptor adaptor;

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

        recyclerView = view.findViewById(R.id.recyclerViewContacts);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adaptor = new ContactsAdaptor(contacts, view.getContext(), false, contactsWrapper -> Log.d("TAGG", "Selected " + contactsWrapper.getFirstName()));

        recyclerView.setAdapter(adaptor);

        return view;
    }


    public void updateList(ArrayList<ContactsWrapper> contactsWrappers) {
        contacts.clear();
        contacts.addAll(contactsWrappers);
        adaptor.notifyDataSetChanged();
    }
}