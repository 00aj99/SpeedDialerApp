package com.example.denmlaa.speeddialerapp.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.denmlaa.speeddialerapp.R;
import com.example.denmlaa.speeddialerapp.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactsRVAdapter extends RecyclerView.Adapter<ContactsRVAdapter.ViewHolder> {

    private Context context;
    private List<Contact> contacts;
    private View.OnClickListener onClickListener;
    private List<Contact> contactsFromDb;

    public ContactsRVAdapter(Context context, List<Contact> contacts, View.OnClickListener onClickListener, List<Contact> contactsFromDb) {
        this.context = context;
        this.contacts = contacts;
        this.onClickListener = onClickListener;
        this.contactsFromDb = contactsFromDb;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Contact contact = contacts.get(position);

        holder.contact_name.setText(contact.getContactName());
        holder.contact_number.setText(contact.getContactNumber());
        holder.contact_favorites.setTag(contact);

        // Check if contact is in database already. If contact is in database, asign star_white drawable
        if (contactsFromDb != null) {
            for (Contact contactFromDb : contactsFromDb) {
                if (contactFromDb.getId() == contact.getId()) {
                    holder.contact_favorites.setImageResource(R.drawable.ic_star_yellow_24dp);
                }
            }
        }

        if (contact.getContactImage() != null) {
            holder.contact_image.setImageURI(Uri.parse(contact.getContactImage()));
        } else {
            holder.contact_image.setImageResource(R.drawable.contact_default);
        }

        holder.contact_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:" + contact.getContactNumber());
                Intent callIntent = new Intent(Intent.ACTION_CALL, number);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Application is missing permission to call", Toast.LENGTH_SHORT).show();
                    return;
                }
                context.startActivity(callIntent);
            }
        });

        holder.contact_favorites.setOnClickListener(onClickListener);

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView contact_image;
        private TextView contact_name;
        private TextView contact_number;
        private ImageView contact_call;
        private ImageView contact_favorites;

        public ViewHolder(View itemView) {
            super(itemView);

            contact_image = itemView.findViewById(R.id.contact_image);
            contact_name = itemView.findViewById(R.id.contact_name);
            contact_number = itemView.findViewById(R.id.contact_number);
            contact_call = itemView.findViewById(R.id.contact_call);
            contact_favorites = itemView.findViewById(R.id.contact_favorites);
        }
    }

    public void setFilter(List<Contact> filterContacts) {
        contacts = new ArrayList<>();
        contacts.addAll(filterContacts);
        notifyDataSetChanged();
    }

}
