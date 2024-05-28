package com.example.lostandfound;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CreateAdvertActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private EditText editName, editPhone, editDescription, editDate, editLocation;
    private LostFoundDatabaseHelper databaseHelper;
    private String selectedLocation;
    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        databaseHelper = new LostFoundDatabaseHelper(this);

        radioGroup = findViewById(R.id.radioGroup);
        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editDescription = findViewById(R.id.editDescription);
        editDate = findViewById(R.id.editDate);
        editLocation = findViewById(R.id.editLocation);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize the Places API
        Places.initialize(getApplicationContext(), "AIzaSyB4KVc0i9NJAYpnPIIoKj1LzE-SDbtMkwg");
        placesClient = Places.createClient(this);

        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAutocompleteIntent();
            }
        });

        Button btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);
        btnGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentPlace();
            }
        });

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAdvert();
            }
        });
    }

    private void launchAutocompleteIntent() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startAutocomplete.launch(intent);
    }

    private final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Place place = Autocomplete.getPlaceFromIntent(result.getData());
                    setSelectedLocation(place.getAddress());
                } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
                    com.google.android.gms.common.api.Status status = Autocomplete.getStatusFromIntent(result.getData());
                    Toast.makeText(this, "An error occurred: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            });

    private void getCurrentPlace() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            List<Place.Field> placeFields = Collections.singletonList(Place.Field.NAME);
            FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

            placesClient.findCurrentPlace(request).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FindCurrentPlaceResponse response = task.getResult();
                    if (response != null) {
                        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                            Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                    placeLikelihood.getPlace().getName(),
                                    placeLikelihood.getLikelihood()));
                            setSelectedLocation(placeLikelihood.getPlace().getName());
                            break; // Consider the most likely place
                        }
                    } else {
                        Toast.makeText(CreateAdvertActivity.this, "No places found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                    Toast.makeText(CreateAdvertActivity.this, "Failed to get current place", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentPlace();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setSelectedLocation(String location) {
        editLocation.setText(location);
        selectedLocation = location;
    }

    private void saveAdvert() {
        RadioButton selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        String post = selectedRadioButton.getText().toString();
        String name = editName.getText().toString();
        String phone = editPhone.getText().toString();
        String description = editDescription.getText().toString();
        String date = editDate.getText().toString();
        String location = editLocation.getText().toString();

        if (name.isEmpty() || phone.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the advert to the database
        long result = databaseHelper.insertAdvert(post, name, phone, description, date, location);

        if (result != -1) {
            String advertString = result + "," + post + "," + name + "," + phone + "," + description + "," + date + "," + location;
            Toast.makeText(this, "Advert saved successfully", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Saved Advert: " + advertString); // Log to Logcat
            clearFields();
        } else {
            Toast.makeText(this, "Failed to save advert", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        editName.setText("");
        editPhone.setText("");
        editDescription.setText("");
        editDate.setText("");
        editLocation.setText("");
        selectedLocation = null;
    }

}









