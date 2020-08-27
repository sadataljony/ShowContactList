package com.sadataljony.app.android.demo.showcontactlist.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.sadataljony.app.android.demo.showcontactlist.R;
import com.sadataljony.app.android.demo.showcontactlist.activity.ActivityContactList;
import com.sadataljony.app.android.demo.showcontactlist.model.Contact;

import java.util.ArrayList;
import java.util.Random;

public class AdapterContact extends RecyclerView.Adapter<AdapterContact.ViewHolder> implements Filterable {
    private ArrayList<Contact> list;
    private ArrayList<Contact> listFiltered;
    private Context pContext;
    private AdapterContact.AdapterListener listener;
    private ActivityContactList activity;

    public AdapterContact(ArrayList<Contact> data, Context context, AdapterContact.AdapterListener listener, ActivityContactList activity) {
        this.pContext = context;
        this.listener = listener;
        this.list = data;
        this.listFiltered = data;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_contact, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256),
                random.nextInt(256));
        String name = (listFiltered.get(i).getName());
        String firstCharacterOfName = String.valueOf(name.charAt(0)).toUpperCase();
        String phoneNumber = listFiltered.get(i).getPhone();
        viewHolder.background.setBackgroundColor(color);
        viewHolder.textViewFirstCharacter.setText(firstCharacterOfName);
        viewHolder.textViewName.setText(name);
        viewHolder.textViewPhoneNumber.setText(phoneNumber);

        viewHolder.list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n = viewHolder.getAdapterPosition();
                Toast.makeText(v.getContext(), "Name: " + listFiltered.get(n).getName() + "\n" + "Number: " + listFiltered.get(n).getPhone(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout list_item, background;
        public TextView textViewFirstCharacter, textViewName, textViewPhoneNumber;

        public ViewHolder(View view) {
            super(view);
            list_item = itemView.findViewById(R.id.list_contact);
            background = itemView.findViewById(R.id.background);
            textViewFirstCharacter = itemView.findViewById(R.id.firstCharacter);
            textViewName = itemView.findViewById(R.id.name);
            textViewPhoneNumber = itemView.findViewById(R.id.phoneNumber);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(listFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (listFiltered == null) ? 0 : listFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listFiltered = list;
                } else {
                    ArrayList<Contact> filteredList = new ArrayList<>();
                    for (Contact row : list) {
                        // name match condition. this might differ depending on your requirement
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    listFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listFiltered = (ArrayList<Contact>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface AdapterListener {
        void onContactSelected(Contact contact);
    }

}
