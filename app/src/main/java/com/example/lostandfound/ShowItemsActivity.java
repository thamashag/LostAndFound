package com.example.lostandfound;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class ShowItemsActivity extends AppCompatActivity implements AdvertListAdapter.OnAdvertClickListener {

    private RecyclerView recyclerView;
    private AdvertListAdapter adapter;
    private LostFoundDatabaseHelper databaseHelper;
    private List<String> advertList; // Change to match your database structure

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_items);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        advertList = new ArrayList<>();
        databaseHelper = new LostFoundDatabaseHelper(this);

        // Populate advertList from the database
        advertList.addAll(databaseHelper.getAllAdverts());

        adapter = new AdvertListAdapter(advertList, this);
        recyclerView.setAdapter(adapter);

        if (getIntent().getBooleanExtra("REFRESH_LIST", false)) {
            refreshAdvertList();
        }

        Button btnDeleteAll = findViewById(R.id.btnDeleteAll);
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllAdverts();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (getIntent().getBooleanExtra("REFRESH_LIST", false)) {
            refreshAdvertList();
        }
    }

    private void deleteAllAdverts() {
        // Delete all adverts from the database
        databaseHelper.deleteAllAdverts();
        // Clear the list and notify the adapter
        advertList.clear();
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "All adverts deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdvertClick(int position) {
        String advert = advertList.get(position);
        String[] advertDetails = advert.split(",");

        if (advertDetails.length >= 2) {
            try {
                long advertId = Long.parseLong(advertDetails[0]);

                Intent intent = new Intent(this, AdvertDetailActivity.class);
                intent.putExtra("ADVERT_ID", advertId);
                startActivity(intent);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid advert ID", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid advert format", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshAdvertList() {
        advertList.clear();
        advertList.addAll(databaseHelper.getAllAdverts());
        adapter.notifyDataSetChanged();
    }

}


