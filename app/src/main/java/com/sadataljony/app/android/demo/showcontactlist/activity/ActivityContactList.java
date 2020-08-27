package com.sadataljony.app.android.demo.showcontactlist.activity;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sadataljony.app.android.demo.showcontactlist.R;
import com.sadataljony.app.android.demo.showcontactlist.adapter.AdapterContact;
import com.sadataljony.app.android.demo.showcontactlist.model.Contact;

import java.util.ArrayList;

public class ActivityContactList extends AppCompatActivity implements AdapterContact.AdapterListener {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private RecyclerView mRecyclerView;
    private AdapterContact mAdapter;
    private ArrayList<Contact> mArrayList;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        mArrayList = new ArrayList<>();
        mAdapter = new AdapterContact(mArrayList, this.getApplicationContext(), this, ActivityContactList.this);
        mRecyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        loadContacts();
    }

    private void loadContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            cursor = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            LoadContact loadContact = new LoadContact();
            loadContact.execute();
        }
    }

    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (cursor != null) {
                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
//                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));// ID
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (phoneNumber.length() >= 11) {
                            Contact contact = new Contact(name, phoneNumber);
                            mArrayList.add(contact);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayList<Contact> arrayListRemoved = new ArrayList<>();
            ArrayList<Contact> arrayListContact = new ArrayList<>();
            for (int i = 0; i < mArrayList.size(); i++) {
                Contact contact = mArrayList.get(i);
                if (contact.getName().matches("\\d+(?:\\.\\d+)?") || contact.getName().trim().length() == 0) {
                    arrayListRemoved.add(contact);
                } else {
                    arrayListContact.add(contact);
                }
            }
            arrayListContact.addAll(arrayListRemoved);
            mArrayList = arrayListContact;
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                searchQuery(query);
                return false;
            }
        });
        return true;
    }

    public void searchQuery(String searchQuery) {
        mAdapter.getFilter().filter(searchQuery);
    }

    @Override
    public void onContactSelected(Contact contact) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}