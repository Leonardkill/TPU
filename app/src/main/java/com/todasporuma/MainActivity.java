package com.todasporuma;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        SendSMS();

    }

    public void Mensage() {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("+5511976453451", null, "FUNCIONA", null, null);
        ;

        Toast.makeText(MainActivity.this, "FUNCIONA", Toast.LENGTH_LONG).show();
    }

    public void SendSMS() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Mensage();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.SEND_SMS},
                    0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Mensage();
                } else {
                    Toast.makeText(MainActivity.this, "SEM PERMISSÂO", Toast.LENGTH_SHORT).show();
                    ;
                }
        }
    }
}