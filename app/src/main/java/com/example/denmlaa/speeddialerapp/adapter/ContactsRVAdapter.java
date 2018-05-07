package com.example.denmlaa.speeddialerapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.denmlaa.speeddialerapp.R;
import com.example.denmlaa.speeddialerapp.model.Contact;

import java.util.List;

public class ContactsRVAdapter extends RecyclerView.Adapter<ContactsRVAdapter.ViewHolder> {

    private Context context;
    private List<Contact> contacts;

    public ContactsRVAdapter(Context context, List<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
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

        if (contact.getContactImage() != null) {
            holder.contact_image.setImageURI(Uri.parse(contact.getContactImage()));
        } else {
            holder.contact_image.setImageResource(R.drawable.contact);
        }

        holder.contact_call.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:" + contact.getContactNumber());
                Intent callIntent = new Intent(Intent.ACTION_CALL, number);
                context.startActivity(callIntent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.itemView);
                popupMenu.inflate(R.menu.contact_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // TODO Set the theme for menu, current color white
                        switch (item.getItemId()) {
                            case R.id.menu_call:
                                Uri number = Uri.parse("tel:" + contact.getContactNumber());
                                Intent callContactIntent = new Intent(Intent.ACTION_CALL, number);
                                context.startActivity(callContactIntent);
                                break;
                            case R.id.menu_widget:
                                Toast.makeText(context, "Widget", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return true;
            }
        });
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

        public ViewHolder(View itemView) {
            super(itemView);

            contact_image = itemView.findViewById(R.id.contact_image);
            contact_name = itemView.findViewById(R.id.contact_name);
            contact_number = itemView.findViewById(R.id.contact_number);
            contact_call = itemView.findViewById(R.id.contact_call);
        }
    }
}
