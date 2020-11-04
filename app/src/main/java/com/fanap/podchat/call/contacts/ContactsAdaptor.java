package com.fanap.podchat.call.contacts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fanap.podchat.example.R;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;

class ContactsAdaptor extends RecyclerView.Adapter<ContactsAdaptor.ViewHolder> {


    public interface IContactInterface {
        void onCantactSelected(ContactsWrapper wrapper);
    }

    ArrayList<ContactsWrapper> contacts;

    Context context;

    boolean isMultiSelect = false;

    IContactInterface iContactInterface;

    public ContactsAdaptor(ArrayList<ContactsWrapper> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    public ContactsAdaptor(ArrayList<ContactsWrapper> contacts, Context context, boolean isMultiSelect, IContactInterface iContactInterface) {
        this.contacts = contacts;
        this.context = context;
        this.isMultiSelect = isMultiSelect;
        this.iContactInterface = iContactInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if (!contacts.isEmpty()) {

            ContactsWrapper contact = contacts.get(viewHolder.getAdapterPosition());

            viewHolder.tvName.setText(contact.getFirstName() + " " + contact.getLastName());

            if (Util.isNotNullOrEmpty(contact.getProfileImage()))
                Glide.with(context)
                        .load(contact.getProfileImage())
                        .apply(RequestOptions.circleCropTransform())
                        .into(viewHolder.imageViewProfile);

            if (contact.isSelected())
                viewHolder.imageViewDone.setVisibility(View.VISIBLE);
            else viewHolder.imageViewDone.setVisibility(View.INVISIBLE);


            if(isMultiSelect)
                viewHolder.imageButtonCall.setVisibility(View.GONE);
            else {
                viewHolder.imageButtonCall.setOnClickListener(v->{
                    if (iContactInterface != null)
                        iContactInterface.onCantactSelected(contact);
                });
            }

            viewHolder.itemView.setOnClickListener(v -> {
                if (isMultiSelect) {
                    contact.setSelected(!contact.isSelected());
                    notifyDataSetChanged();
                    if (iContactInterface != null)
                        iContactInterface.onCantactSelected(contact);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView imageViewProfile;
        ImageButton imageButtonCall;
        ImageView imageViewDone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvContactName);
            imageViewProfile = itemView.findViewById(R.id.imageContact);
            imageButtonCall = itemView.findViewById(R.id.imgBtnCallContact);
            imageViewDone = itemView.findViewById(R.id.imageDone);
        }

    }
}
