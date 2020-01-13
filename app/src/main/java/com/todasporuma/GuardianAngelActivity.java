package com.todasporuma;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.todasporuma.adapter.AngelsAdapter;
import com.todasporuma.common.Constants;
import com.todasporuma.helper.Permission;
import com.todasporuma.helper.ToastHelper;
import com.todasporuma.model.Angel;

import java.util.ArrayList;
import java.util.List;

import static com.todasporuma.common.Constants.CONTACT_LIST_CODE;

public class GuardianAngelActivity extends AppCompatActivity {

    private RecyclerView recyclerAngels;
    private AngelsAdapter adapterAngels;
    private Switch hiddenButton;

    private List<Angel> angelList = new ArrayList<>();

    public static Intent createIntent(Context context) {
        return new Intent(context, GuardianAngelActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian_angel);

        hiddenButton = findViewById(R.id.hiddenButton);

        recyclerAngels = findViewById(R.id.recyclerViewAngels);
        FloatingActionButton btnAddContact = findViewById(R.id.btnAddContact);

        btnAddContact.setOnClickListener(openContactList());

        adapterAngels = new AngelsAdapter(angelList);
        recyclerAngels.setAdapter(adapterAngels);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerAngels.setLayoutManager(layoutManager);


        hiddenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Intent serviceIntent = new Intent(GuardianAngelActivity.this, ForegroundService.class);
                    serviceIntent.putExtra("inputExtra", "Número de correio de voz desconhecido");
                    startService(serviceIntent);

                    ContextCompat.startForegroundService(GuardianAngelActivity.this, serviceIntent);
                } else {
                    Intent serviceIntent = new Intent(GuardianAngelActivity.this, ForegroundService.class);
                    stopService(serviceIntent);         }
            }
        });
    }



    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode == CONTACT_LIST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getContact(data);
            }
        }
    }

    private void getContact(Intent data) {
        Cursor numbersCursor = null;
        Cursor contactCursor = null;
        String photoPath;
        String contactNumber = null;
        String contactName = null;
        Uri photoUri = null;
        try {
            Uri contactData = data.getData();
            if (contactData != null) {
                contactCursor = getContentResolver().query(contactData, null, null, null, null);
                if (contactCursor != null) {
                    if (contactCursor.moveToFirst()) {
                        String contactId = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
                        boolean hasNumber = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)).equals("1");

                        contactName = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        photoPath = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));

                        if (photoPath != null) {
                            photoUri = Uri.parse(photoPath);
                        }

                        if (hasNumber) {
                            numbersCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            if (numbersCursor != null) {
                                while (numbersCursor.moveToNext()) {
                                    contactNumber = numbersCursor.getString(numbersCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                }
                            }
                        }
                    }
                }
                setContact(contactName, contactNumber, photoUri);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (contactCursor != null)
                contactCursor.close();

            if (numbersCursor != null)
                numbersCursor.close();
        }
    }

    private void setContact(String contactName, String contactNumber, Uri photoUri) {
        Angel angel = new Angel(contactName, contactNumber);
        if (angelList.size() < 5) {
            angelList.add(angel);
            adapterAngels.setAngelList(angelList);
            adapterAngels.notifyDataSetChanged();
        } else {
            ToastHelper.make(this, "Apenas 5 anjos podem ser cadastrados");
        }
    }

    private View.OnClickListener openContactList() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(GuardianAngelActivity.this, Manifest.permission.READ_CONTACTS);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, CONTACT_LIST_CODE);
                } else {
                    ActivityCompat.requestPermissions(GuardianAngelActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, Constants.CONTACT_LIST_PERMISSION_CODE);
                }
            }
        };
    }

}
