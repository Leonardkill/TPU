package com.todasporuma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.todasporuma.common.Constants;
import com.todasporuma.helper.SharedPreferenceHelper;
import com.todasporuma.helper.ToastHelper;
import com.todasporuma.model.Angel;

import static com.todasporuma.common.Constants.EMAIL_KEY;
import static com.todasporuma.common.Constants.NAME_ANGEL;
import static com.todasporuma.common.Constants.PHONE_ANGEL;

public class AngelActivity extends AppCompatActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, AngelActivity.class);
    }

    private static final int CONTACT_LIST_CODE = 1;

    private Button btn_agenda, btn_salvar_anjo;
    private EditText nome_anjo, telefone_anjo;
    public String phone = null;
    public String nome = null;
    private Switch hiddenButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_angel);

        btn_agenda = findViewById(R.id.btnAgenda);
        btn_salvar_anjo = findViewById(R.id.btnSalvarAnjo);
        nome_anjo = findViewById(R.id.nomeAnjo);
        telefone_anjo = findViewById(R.id.telefoneAnjo);
        hiddenButton = findViewById(R.id.hiddenButton);


        nome_anjo.setText(SharedPreferenceHelper.getSharedPreferenceString(AngelActivity.this,NAME_ANGEL,null));
        telefone_anjo.setText(SharedPreferenceHelper.getSharedPreferenceString(AngelActivity.this,PHONE_ANGEL,null));

        btn_agenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, CONTACT_LIST_CODE);
            }
        });

        btn_salvar_anjo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nome_anjo == null || telefone_anjo == null){
                    ToastHelper.make(AngelActivity.this,"Adcione um anjo");
                }else {
                    nome = nome_anjo.getText().toString();
                    phone = telefone_anjo.getText().toString();

                    SharedPreferenceHelper.setSharedPreferenceString(AngelActivity.this, Constants.NAME_ANGEL,nome);
                    SharedPreferenceHelper.setSharedPreferenceString(AngelActivity.this, Constants.PHONE_ANGEL,phone);

                }
            }
        });

        hiddenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Intent serviceIntent = new Intent(AngelActivity.this, ForegroundService.class);
                    serviceIntent.putExtra("inputExtra", "Número de correio de voz desconhecido");
                    startService(serviceIntent);

                    ContextCompat.startForegroundService(AngelActivity.this, serviceIntent);
                } else {
                    Intent serviceIntent = new Intent(AngelActivity.this, ForegroundService.class);
                    stopService(serviceIntent);         }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch(requestCode) {
                case CONTACT_LIST_CODE:
                    contactPicked(data);
                    break;}
        }else{
            Toast.makeText(AngelActivity.this,"Erro ao selecionar anjo", Toast.LENGTH_SHORT).show();
        }
    }

    private void contactPicked(Intent data){
        Cursor contactCursor = null;
        try{
            Uri contactData = data.getData();
            contactCursor = getContentResolver().query(contactData, null, null, null, null);
            contactCursor.moveToFirst();
            int phoneIndex = contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            nome = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            phone = contactCursor.getString(phoneIndex);

            nome_anjo.setText(nome);
            telefone_anjo.setText(phone);




        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.angel_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.my_account:
                startActivity(EditProfileActivity.createIntent(AngelActivity.this));
                return true;
            case R.id.home:
                startActivity(MapsActivity.createIntent(AngelActivity.this));
                return true;
            case R.id.report_event:
                startActivity(ReportEventActivity.createIntent(AngelActivity.this));
                return true;
            case R.id.about:
                startActivity(AboutActivity.createIntent(AngelActivity.this));
                return true;
            case R.id.exit:
                SharedPreferenceHelper.setSharedPreferenceString(AngelActivity.this, EMAIL_KEY,null);
                SharedPreferenceHelper.setSharedPreferenceString(AngelActivity.this, Constants.PASSWORD_KEY,null);
                FirebaseAuth.getInstance().signOut();
                startActivity(LoginActivity.createIntent(AngelActivity.this));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
