package com.example.lostandfound;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class CreateAdvertActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private EditText editName, editPhone, editDescription, editDate, editLocation;
    private LostFoundDatabaseHelper databaseHelper;

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

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAdvert();
            }
        });
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
    }

}
