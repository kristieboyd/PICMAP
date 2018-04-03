package com.example.picmap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.widget.DataBufferAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;

import java.io.InputStream;

public class ResultsAdapter extends DataBufferAdapter<Metadata> {

    LayoutInflater mInflater;

    public ResultsAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    private class ViewHolder {
        ImageView pic;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        System.out.println("view");


        if (convertView == null) {

            System.out.println("if");
            convertView = mInflater.inflate(R.layout.display_photo, null);
            holder = new ViewHolder();
            holder.pic = (ImageView) convertView
                    .findViewById(R.id.imageView);

            final Metadata metadata = getItem(position);
            DriveFile file = metadata.getDriveId().asDriveFile();
            final Task<DriveContents> openFileTask = LoginActivity.getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY);

            openFileTask
                    .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                        @Override
                        public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {

                            System.out.println("in");
                            DriveContents contents = task.getResult();
                            System.out.println(contents);
                            InputStream is = contents.getInputStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(is);

                            holder.pic.setImageBitmap(bitmap);
                            System.out.println("open");

                            Task<Void> discardTask = LoginActivity.getDriveResourceClient().discardContents(contents);
                            return discardTask;

                        }});
        }

        //final ImageView titleTextView = (ImageView) convertView.findViewById(android.R.id.text1);

        return convertView;
    }
}