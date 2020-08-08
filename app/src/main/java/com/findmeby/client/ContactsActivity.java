package com.findmeby.client;

import android.database.Cursor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.findmeby.client.adapter.ContactListAdapter;
import com.findmeby.client.db.SQLiteDB;
import com.findmeby.client.listener.RecyclerItemClickListener;
import com.findmeby.client.model.Contact;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_main)
public class ContactsActivity extends AppCompatActivity implements RecyclerItemClickListener {

    @ViewById(R.id.lvContact)
    RecyclerView lvContact;

    @ViewById(R.id.add)
    FloatingActionButton btnAdd;

    private ContactListAdapter contactListAdapter;
    private LinearLayoutManager linearLayoutManager;

    private SQLiteDB sqLiteDB;

    @AfterViews
    public void init(){
        linearLayoutManager = new LinearLayoutManager(this);
        contactListAdapter = new ContactListAdapter(this);
        contactListAdapter.setOnItemClickListener(this);

        lvContact.setLayoutManager(linearLayoutManager);
        lvContact.setAdapter(contactListAdapter);
    }

    @Click(R.id.add)
    void onAdd() {
        ContactItemActivity_.intent(ContactsActivity.this).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    void loadData() {
        sqLiteDB = new SQLiteDB(this);

        List<Contact> contactList = new ArrayList<>();

        Cursor cursor = sqLiteDB.retrieve();
        Contact contact;

        if (cursor.moveToFirst()) {
            do {

                contact = new Contact();

                contact.setId(cursor.getInt(0));
                contact.setEmail(cursor.getString(1));

                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        contactListAdapter.clear();
        contactListAdapter.addAll(contactList);
        contactListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position, View view) {
        ContactItemActivity_.start(this, contactListAdapter.getItem(position));
    }
}
