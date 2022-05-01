/*
 * @author: Min Tran
 * @description: This fragment handles the sharing interface and functionality.
 */

package com.example.chess;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import java.io.File;
import java.util.ArrayList;

public class ShareFragment extends Fragment {

    private static final int LAYOUT = R.layout.fragment_share;
    private int attempts = 0;

    private Activity containerActivity; // activity that contains fragment
    private View inflatedView; // fragment view

    private ListView contactsListView; // contacts list view
    ArrayAdapter<String> contactsAdapter; // contacts adapter
    private final ArrayList<String> contacts = new ArrayList<String>(); // list of contacts

    /**
     * Sets container activity.
     * @param containerActivity - activity that fragment is contained in
     */
    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    /**
     * Upon view creation, sets layout, inflated view, and sets up contact list view.
     * @param inflater - layout inflater
     * @param container - view group container
     * @param savedInstanceState - saved instance state
     * @return Inflated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get inflated view
        inflatedView = inflater.inflate(LAYOUT, container, false);

        // set up contact list view
        getContacts();
        setUpContactsAdapter();
        ListView listView = inflatedView.findViewById(R.id.contact_list);

        // set up list view on click functionality
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            /**
             * Gets email address and starts email intent.
             * @param adapterView - view of adapter
             * @param view - view that was clicked on
             * @param pos - position of list item
             * @param id - list item id
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                startEmailIntent(getEmailAddress(view));
            }
        });
        return inflatedView;
    }

    /**
     * Retrieves email address from contact in view and returns email address.
     * @param view - view to retrieve email address from
     * @return Email address.
     */
    public String getEmailAddress(View view) {
        // get id and initialize email address string
        String text = ((TextView) view.findViewById(R.id.contact_text)).getText().toString();
        String id = text.substring(text.indexOf(" :: ") + 4);
        String emailAddress = "";

        // look for email address
        Cursor emails = containerActivity.getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID
                        + " = " + id, null, null);
        while (emails.moveToNext()) {
            int index = emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
            emailAddress = emails.getString(index);
        }
        emails.close();

        // return email address
        return emailAddress;
    }

    /**
     * Starts email intent populated with email address and canvas image attached.
     * @param emailAddress - address to send email to
     */
    public void startEmailIntent(String emailAddress) {
        // initialize intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("vnd.android.cursor.dir/email");

        // email text
        String text = "I beat today's daily puzzle in "+attempts+" attempts!";
        if (attempts == 0) text = "I beat today's daily puzzle perfectly!";

        // add email address
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { emailAddress });
        intent.putExtra(android.content.Intent.EXTRA_TEXT,text);

        // start intent
        containerActivity.startActivity(intent);
    }

    /**
     * Constructs a list of all contacts.
     */
    public void getContacts() {
        Cursor cursor = containerActivity.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,null,
                null, null, null);
        // search for and add contacts
        while (cursor.moveToNext()) {
            int indexId = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            int indexName = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            String id = cursor.getString(indexId);
            String given = cursor.getString(indexName);
            contacts.add(given + " :: " + id);
        }
        cursor.close();
    }

    /**
     * Sets up contacts adapter using contacts list.
     */
    private void setUpContactsAdapter() {
        contactsListView = inflatedView.findViewById(R.id.contact_list);
        contactsAdapter = new
                ArrayAdapter(containerActivity, R.layout.contact_row,
                R.id.contact_text, contacts);
        contactsListView.setAdapter(contactsAdapter);
    }
}