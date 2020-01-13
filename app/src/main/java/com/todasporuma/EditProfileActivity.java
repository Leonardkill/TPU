package com.todasporuma;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.todasporuma.helper.Base64Helper;
import com.todasporuma.helper.Permission;

import static com.todasporuma.common.Constants.CAMERA_CODE;
import static com.todasporuma.common.Constants.GALLERY_CODE;


public class EditProfileActivity extends AppCompatActivity {

    private ImageView image_user;

    //Firebase
    private StorageReference storageReference;

    private String[] neededPermissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public static Intent createIntent(Context context) {
        return new Intent(context, EditProfileActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        storageReference = FirebaseConfiguration.getFiresabseStorage();

        ImageButton btnCamera = findViewById(R.id.imageButtonCamera);
        ImageButton btnGallery = findViewById(R.id.imageButtonGaleria);
        image_user = findViewById(R.id.circleImageViewFotoPerfil);

        Permission.validatePermissions(neededPermissions, this, 1);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, CAMERA_CODE);
                }
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, GALLERY_CODE);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;
            try {
                switch (requestCode) {
                    case CAMERA_CODE:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case GALLERY_CODE:
                        Uri localDaImagem = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localDaImagem);
                        break;
                }
                if (imagem != null) {

                    image_user.setImageBitmap(imagem);

                    //Recuperando qual usuario está logado e transformando seu email em base 64
                    FirebaseAuth auth = FirebaseConfiguration.getFirebaseAutenticacao();
                    String email = auth.getCurrentUser().getEmail();
                    String userId = Base64Helper.encodeBase64(email);

                    // salvar foto no firebase
                    StorageReference imageRef = storageReference
                            .child("imagens")
                            .child("Perfil")
                            .child(userId)
                            .child("perfil.jpeg");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissionResult : grantResults) {
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                deniedPermissionAlert();
            }
        }

    }

    private void deniedPermissionAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Você precisa aceitar as permissões para utilizar o aplicativo");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
