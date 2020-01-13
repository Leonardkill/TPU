package com.todasporuma;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.todasporuma.common.Constants;
import com.todasporuma.helper.SharedPreferenceHelper;
import com.todasporuma.model.Angel;
import com.todasporuma.model.Event;
import com.todasporuma.model.RiskArea;
import com.todasporuma.model.User;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.todasporuma.common.Constants.EMAIL_KEY;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
   //Maps
    private GoogleMap mMap;
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private FloatingActionButton btn_Socorro;
    private Marker marcador;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private Address address;
    private LocationRequest mLocationRequest;
    private LocationManager locationManager;
    private LatLng latLng;
    private boolean isPermission;
    private int cont = 1;
    private long UPDATE_INTERVAL = 20 * 1000;   /* 10 secs */
    private long FASTEST_INTERVAL = 1000;      /* 20 sec */

    private User user;
    private Angel angel;

    private String nomeAnjo;
    private String telefoneAnjo;


    private double latitude = 0.0;
    private double longitude = 0.0;
    private String txtAddress;


    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    private static final String TAG = "MainActivity";
    private com.google.android.gms.location.LocationListener listener;




    public static Intent createIntent(Context context) {
        return new Intent(context, MapsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        btn_Socorro = findViewById(R.id.btnHelp);

        //Clique do botão que envia a mensagem
        btn_Socorro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendSMS();
            }
        });



        if (requestSinglePermission()) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            //it was pre written
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

//            mLatitudeTextView = (TextView) findViewById((R.id.latitude_textview));
//            mLongitudeTextView = (TextView) findViewById((R.id.longitude_textview));

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            checkLocation(); //check whether location service is enable or not in your  phone


        }else if(!requestSinglePermission()){
            finish();
        }
    }


    //Métods para gerar o endereço da localização
    public Address findAddress(double latitude, double longitude)
        throws IOException{
            Geocoder geocoder;
            Address address = null;
            List<Address> addresses;

            geocoder = new Geocoder(getApplicationContext());

            addresses = geocoder.getFromLocation(latitude,longitude,1);
            if(addresses.size() > 0){
                address = addresses.get(0);
            }
        return address;
        }




    //Métodos para enviar as mensagens

    public void Mensage(){

        try{
            address = findAddress(latitude, longitude);
            txtAddress = String.format("%s, %s", address.getThoroughfare(), address.getSubThoroughfare());
        }catch (IOException e){
            Log.i("GPS", e.getMessage());
        }

        telefoneAnjo = SharedPreferenceHelper.getSharedPreferenceString(MapsActivity.this, Constants.PHONE_ANGEL, null);
        nomeAnjo = SharedPreferenceHelper.getSharedPreferenceString(MapsActivity.this, Constants.NAME_ANGEL, null);

        if(telefoneAnjo == null || nomeAnjo == null){
            Toast.makeText(MapsActivity.this,"Adicione um Anjo", Toast.LENGTH_SHORT).show();
            startActivity(AngelActivity.createIntent(MapsActivity.this));
        }else{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("+5511976453451",null,nomeAnjo + " Me Ajude estou na " + txtAddress,null,null);

            Toast.makeText(MapsActivity.this, "Seu pedido foi enviado", Toast.LENGTH_LONG ).show();

            DatabaseReference Events = reference.child("Event");

            Event event = new Event();
            event.setAddress(txtAddress);
            event.setNameReciver(nomeAnjo);
            event.setPhoneReciver(telefoneAnjo);
            event.setLatitude(latitude);
            event.setLongitude(longitude);

            Events.child("001").setValue(event);
        }

    }


    public void SendSMS(){


        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            Mensage();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.SEND_SMS},
                    0);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 0:
                if (grantResults.length>= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Mensage();
                }else{
                    Toast.makeText(MapsActivity.this, "SEM PERMISSÂO", Toast.LENGTH_SHORT).show();;
                }
        }
    }


    //Métodos para o mapa

    @Override
    public void onMapReady(GoogleMap googleMap) {
        DatabaseReference riskAreaFirebase = reference.child("RiskArea");
        mMap = googleMap;
        cont = 0;
        Double latitudeEtec =  -23.520803;
        Double longitudeEtec = -46.594461;
        if (latLng != null) {

            marcador = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Você está aqui"));
            riskAreaFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data: dataSnapshot.getChildren()){
                        RiskArea riskArea = data.getValue(RiskArea.class);
                        LatLng latLng = new LatLng(riskArea.getLatitude(),riskArea.getLongitude());
                        mMap.addCircle(new CircleOptions()
                                .center(latLng)
                                .radius(20)
                                .strokeWidth(2)
                                .strokeColor(Color.BLACK)
                                .fillColor(Color.argb(25,255,96,112)));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }



        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng locationRisk) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
                dialog.setTitle("Areas de Risco")
                        .setMessage("Você deseja marcar uma area perigosa aqui?")
                        .setPositiveButton("Marcar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                                mMap.addCircle(new CircleOptions()
                                        .center(locationRisk)
                                        .radius(20)
                                        .strokeWidth(2)
                                        .strokeColor(Color.BLACK)
                                        .fillColor(Color.argb(25,255,96,112)));

                                DatabaseReference riskAreaFirebase = reference.child("RiskArea");
                                String uniqueID = UUID.randomUUID().toString();

                                RiskArea riskArea = new RiskArea();
                                riskArea.setLatitude(locationRisk.latitude);
                                riskArea.setLongitude(locationRisk.longitude);

                                riskAreaFirebase.child(uniqueID).setValue(riskArea);



                            }
                        })
                        .setNegativeButton("Não Desejo", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                            }
                        });
                dialog.show();

            }
        });

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {

//             mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
//            mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();

        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        while ( cont < 2) {
            cont = cont + 1;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        }

        latitude = latLng.latitude;
        longitude = latLng.longitude;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Habilite sua localização")
                .setMessage("Por favor ative a localização do dipositivo para usar este aplicativo")
                .setPositiveButton("Ativar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }


    private void markRiskArea() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Habilite sua localização")
                .setMessage("Você deseja marcar uma area perigosa aqui?")
                .setPositiveButton("Marcar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {


                    }
                })
                .setNegativeButton("Não Desejo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean requestSinglePermission() {

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        isPermission = true;

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            isPermission = false;
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        return isPermission;

    }


    //Metodos para o menu

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.my_account:
                startActivity(EditProfileActivity.createIntent(MapsActivity.this));
                return true;
            case R.id.my_angels:
                startActivity(AngelActivity.createIntent(MapsActivity.this));
                return true;
            case R.id.about:
                startActivity(AboutActivity.createIntent(MapsActivity.this));
                return true;
            case R.id.report_event:
                startActivity(ReportEventActivity.createIntent(MapsActivity.this));
                return true;
            case R.id.exit:
                SharedPreferenceHelper.setSharedPreferenceString(MapsActivity.this, EMAIL_KEY,null);
                SharedPreferenceHelper.setSharedPreferenceString(MapsActivity.this, Constants.PASSWORD_KEY,null);
                FirebaseAuth.getInstance().signOut();
                startActivity(LoginActivity.createIntent(MapsActivity.this));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
