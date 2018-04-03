package com.example.picmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class editPhotosOptions extends locationOptions {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_photo_options);
    }

    public void add(View view) {
        Intent myIntent = new Intent(view.getContext(), photoSelect.class);
        startActivity(myIntent);
        finish();
    }
}
