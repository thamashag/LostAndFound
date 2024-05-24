package com.example.lostandfound;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdvertDetailActivity extends AppCompatActivity {

    private TextView textPost, textName, textPhone, textDescription, textDate, textLocation;
    private LostFoundDatabaseHelper databaseHelper;
    private long advertId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advert_detail);

        databaseHelper = new LostFoundDatabaseHelper(this);

        textPost = findViewById(R.id.textPost);
        textName = findViewById(R.id.textName);
        textPhone = findViewById(R.id.textPhone);
        textDescription = findViewById(R.id.textDescription);
        textDate = findViewById(R.id.textDate);
        textLocation = findViewById(R.id.textLocation);

        advertId = getIntent().getLongExtra("ADVERT_ID", -1);
        if (advertId != -1) {
            Cursor cursor = databaseHelper.getAdvertById(advertId);
            if (cursor.moveToFirst()) {
                String post = cursor.getString(cursor.getColumnIndex(LostFoundDatabaseHelper.COLUMN_POST));
                String name = cursor.getString(cursor.getColumnIndex(LostFoundDatabaseHelper.COLUMN_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(LostFoundDatabaseHelper.COLUMN_PHONE));
                String description = cursor.getString(cursor.getColumnIndex(LostFoundDatabaseHelper.COLUMN_DESCRIPTION));
                String date = cursor.getString(cursor.getColumnIndex(LostFoundDatabaseHelper.COLUMN_DATE));
                String location = cursor.getString(cursor.getColumnIndex(LostFoundDatabaseHelper.COLUMN_LOCATION));

                textPost.setText(post);
                textName.setText(name);
                textPhone.setText(phone);
                textDescription.setText(description);
                textDate.setText(date);
                textLocation.setText(location);
            }
            cursor.close();
        }

        Button btnRemove = findViewById(R.id.btnRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAdvert();
            }
        });
    }

    private void removeAdvert() {
        if (advertId != -1) {
            int deletedRows = databaseHelper.deleteAdvert(advertId);
            if (deletedRows > 0) {
                Toast.makeText(this, "Advert removed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, ShowItemsActivity.class);
                intent.putExtra("REFRESH_LIST", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to remove advert", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to remove advert", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshAdvertListInShowItemsActivity() {
        Intent intent = new Intent(this, ShowItemsActivity.class);
        intent.putExtra("REFRESH_LIST", true);
        startActivity(intent);
        finish();
    }
}

