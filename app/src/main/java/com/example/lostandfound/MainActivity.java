package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCreateAdvert = findViewById(R.id.btnCreateAdvert);
        Button btnShowItems = findViewById(R.id.btnShowItems);
        Button btnShowMap = findViewById(R.id.btnShowMap);



        btnCreateAdvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start activity for creating new advert
                startActivity(new Intent(MainActivity.this, CreateAdvertActivity.class));
            }
        });

        btnShowItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start activity for showing all lost and found items
                startActivity(new Intent(MainActivity.this, ShowItemsActivity.class));
            }
        });

        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShowMapActivity.class));
            }
        });
    }


    }