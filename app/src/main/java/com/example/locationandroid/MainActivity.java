package com.example.locationandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private TextView AddressText;
    private Button getLocationButton, moveToMapsButton;
    private LocationRequest locationRequest;
    double latitude, longitude;
    Intent maps;
    Bundle bundle;
    LocationObj locationObj;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AddressText = findViewById(R.id.addressText);
        getLocationButton = findViewById(R.id.getLocationButton);
        moveToMapsButton = findViewById(R.id.moveToMapsButton);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        maps = new Intent(this, MapsActivity.class);
        locationObj = new LocationObj();

        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                // Kiểm tra phiên bản Android
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                    // Kiểm tra xem thử đã grant quyền truy cập GPS chưa
                    if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // Nếu rồi thì...
                        if(isGPSEnabled()){
                            LocationServices.getFusedLocationProviderClient(MainActivity.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                        LocationServices.getFusedLocationProviderClient(MainActivity.this).removeLocationUpdates(this);
                                        if(locationResult != null && locationResult.getLocations().size()>0){
                                            int index = locationResult.getLocations().size()-1;
                                            latitude = locationResult.getLocations().get(index).getLatitude();
                                            longitude = locationResult.getLocations().get(index).getLongitude();
                                            locationObj.setLatitude(latitude);
                                            locationObj.setLongitude(longitude);
                                            AddressText.setText("Latitude: "+ latitude + "\n" + "Longitude: "+ longitude);
                                        }
                                }
                            }, Looper.getMainLooper());
                        }
                     else {
                            turnOnGPS();
                     }
                    }else{
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
       }
            }
        });
        moveToMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putDouble("Latitude",latitude);
                bundle.putDouble("Longitude",longitude);
                maps.putExtras(bundle);
                startActivity(maps);
            }
        });
    }

    // Hàm kiểm tra xem GPS đã được bật chưa
   private boolean isGPSEnabled(){
        LocationManager locationManager = null; // Tạo một biến để truy cập vào dịch vụ vị trí của hệ thống
        boolean isEnabled = false;

        if(locationManager==null){
            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); // Trả về true/false khi check GPS đã được bật chưa

        return isEnabled;
    }

    private void turnOnGPS(){


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MainActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MainActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });
    }

}