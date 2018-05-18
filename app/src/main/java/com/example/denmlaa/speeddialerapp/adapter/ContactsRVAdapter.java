package com.example.denmlaa.speeddialerapp.adapter;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.denmlaa.speeddialerapp.ContactWidgetProvider;
import com.example.denmlaa.speeddialerapp.R;
import com.example.denmlaa.speeddialerapp.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactsRVAdapter extends RecyclerView.Adapter<ContactsRVAdapter.ViewHolder> {

    private Context context;
    private List<Contact> contacts;
    private View.OnClickListener onClickListener;

    public ContactsRVAdapter(Context context, List<Contact> contacts, View.OnClickListener onClickListener) {
        this.context = context;
        this.contacts = contacts;
        this.onClickListener = onClickListener;
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
        holder.itemView.setTag(contact);

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                    PopupMenu popupMenu = new PopupMenu(wrapper, holder.itemView);
                    popupMenu.inflate(R.menu.contact_menu);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_widget:
                                    // TODO Setup the widget

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        AppWidgetManager mAppWidgetManager = context.getSystemService(AppWidgetManager.class);
                                        ComponentName myProvider = new ComponentName(context, ContactWidgetProvider.class);

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            mAppWidgetManager.requestPinAppWidget(myProvider, null, null);

                                        }
                                    }
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
