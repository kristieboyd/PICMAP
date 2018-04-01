package com.example.picmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class locationOptions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_options);
    }

    public void display(View view) {
        //Toast.makeText(this, "Coming Soon!", Toast.LENGTH_LONG).show();
        //Intent myIntent = new Intent(view.getContext(), openData.class);
        //startActivity(myIntent);
        //finish();
    }

    public void editPhotos(View view) {
        Intent myIntent = new Intent(view.getContext(), editPhotosOptions.class);
        startActivity(myIntent);
        finish();
    }

}
