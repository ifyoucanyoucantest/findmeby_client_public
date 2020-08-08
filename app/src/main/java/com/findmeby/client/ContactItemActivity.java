package com.findmeby.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.findmeby.client.db.SQLiteDB;
import com.findmeby.client.model.Contact;

/**
 * Created by docotel on 5/2/16.
 */
@EActivity(R.layout.activity_act)
public class ContactItemActivity extends AppCompatActivity {


    @ViewById(R.id.emailText)
    EditText email;
    @ViewById(R.id.btnAdd)
    Button btnAdd;
    @ViewById(R.id.btnEdit)
    Button btnEdit;
    @ViewById(R.id.btnDelete)
    Button btnDelete;

    private SQLiteDB sqLiteDB;
    private Contact contact;

    @AfterViews
    void init() {
        contact = getIntent().getParcelableExtra(ContactItemActivity_.class.getSimpleName());
        if (contact != null) {
            btnAdd.setVisibility(View.GONE);
            email.setText(contact.getEmail());
        } else {
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }

        sqLiteDB = new SQLiteDB(this);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, ContactItemActivity_.class);
        context.startActivity(intent);
    }

    public static void start(Context context, Contact contact) {
        Intent intent = new Intent(context, ContactItemActivity_.class);
        intent.putExtra(ContactItemActivity_.class.getSimpleName(), contact);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Click(R.id.btnAdd)
    void onAdd() {
        if (validate()) {
            contact = new Contact();
            contact.setEmail(email.getText().toString());
            sqLiteDB.create(contact);
            Toast.makeText(this, "Inserted!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public boolean validate() {
        boolean valid = true;

        String emailText = email.getText().toString();

        if (emailText.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("enter a valid email address");
            valid = false;
        } else {
            email.setError(null);
        }
        return valid;
    }

    @Click(R.id.btnEdit)
    void onEdit() {
        if (validate()) {
            contact.setEmail(email.getText().toString());
            sqLiteDB.update(contact);
            Toast.makeText(this, "Edited!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Click(R.id.btnDelete)
    void onDelete() {
        sqLiteDB.delete(contact.getId());
        Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
