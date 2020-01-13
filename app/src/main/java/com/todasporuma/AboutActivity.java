package com.todasporuma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.google.firebase.auth.FirebaseAuth;
import com.todasporuma.common.Constants;
import com.todasporuma.helper.SharedPreferenceHelper;

import static com.todasporuma.common.Constants.EMAIL_KEY;

public class AboutActivity extends AppCompatActivity {

    private WebView webView;

    public static Intent createIntent(Context context) {
        return new Intent(context, AboutActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initView();
        initWebView();
    }

    private void initWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/index.html");
    }

    private void initView() {
        webView = findViewById(R.id.about);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(MapsActivity.createIntent(AboutActivity.this));
                finish();
                return true;
            case R.id.my_account:
                startActivity(EditProfileActivity.createIntent(AboutActivity.this));
                finish();
                return true;
            case R.id.my_angels:
                startActivity(AngelActivity.createIntent(AboutActivity.this));
                finish();
                return true;
            case R.id.report_event:
                startActivity(ReportEventActivity.createIntent(AboutActivity.this));
                finish();
                return true;
            case R.id.exit:
                SharedPreferenceHelper.setSharedPreferenceString(AboutActivity.this, EMAIL_KEY,null);
                SharedPreferenceHelper.setSharedPreferenceString(AboutActivity.this, Constants.PASSWORD_KEY,null);
                FirebaseAuth.getInstance().signOut();
                startActivity(LoginActivity.createIntent(AboutActivity.this));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

