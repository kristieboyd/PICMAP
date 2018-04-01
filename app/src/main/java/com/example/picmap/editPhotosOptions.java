package com.example.picmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Dell on 2018-03-29.
 */

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

    public void delete(View view) {
        Toast.makeText(this, "Coming Soon!", Toast.LENGTH_LONG).show();
    }

}
