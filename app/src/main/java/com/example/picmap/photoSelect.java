package com.example.picmap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class photoSelect extends LoginActivity{
    private static final String TAG = "photoSelect";
    private static final int PHOTO_SELECT_CODE = 1;

    private static Bitmap bitmapToSave;
    private ImageView pic;
    Uri imageUri;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayPhotos();
    }

    private void displayPhotos() {
        Log.i(TAG, "Displaying photos..");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PHOTO_SELECT_CODE);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_SELECT_CODE:

                if (resultCode == Activity.RESULT_OK) {
                    Log.i(TAG, "Image captured successfully.");
                    try {
                        imageUri = data.getData();
                        InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        bitmapToSave = BitmapFactory.decodeStream(imageStream);

                        selectedPhoto(); //saveFile(); //saveFileToDrive();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                break;
        }}
    }

    private void selectedPhoto() {
        setContentView(R.layout.photo);
        pic = (ImageView) findViewById(R.id.pic);
        pic.setImageURI(imageUri);

    }

    public static Bitmap getImageBitmap() {
            return bitmapToSave;
    }

    public void setImage(Bitmap newImage) {
        System.out.println("HELLO");
        System.out.println(newImage);
        bitmapToSave = newImage;

    }

    public void filters(View view) {
        Intent myIntent = new Intent(view.getContext(), addFilter.class);
        startActivityForResult(myIntent, 0);
        finish();
    }

    public void saveFile(View view) {
        Log.i(TAG, "Creating new contents.");
        Toast.makeText(this, "Image Saved", Toast.LENGTH_LONG).show();
        finish();

        final Bitmap image = bitmapToSave;
        final Task<DriveFolder> appFolderTask = getDriveResourceClient().getAppFolder();
        final Task<DriveContents> createContentsTask = getDriveResourceClient().createContents();
        System.out.println("1");

        //Wait for task completion
        Tasks.whenAll(appFolderTask, createContentsTask)
                .continueWithTask(new Continuation<Void, Task<DriveFile>>() {
                    @Override
                    public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
                        System.out.println("2");
                        DriveFolder parent = appFolderTask.getResult();
                        DriveContents contents = createContentsTask.getResult();
                        System.out.println("3");
                        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
                        try {
                            contents.getOutputStream().write(bitmapStream.toByteArray());
                        } catch (IOException e) {
                            Log.w(TAG, "Unable to write file contents.", e);
                        }
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setMimeType("image/jpeg")
                                .setTitle("photo.png")
                                .setStarred(true)
                                .build();
                        System.out.println("4");
                        Task<DriveFile> file = getDriveResourceClient().createFile(parent, changeSet, contents);
                        finish();

                        return file;
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to create file", e);
                        finish();
                    }
                });
    }
}