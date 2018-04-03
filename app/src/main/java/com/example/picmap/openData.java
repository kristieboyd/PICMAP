package com.example.picmap;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ListView;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.widget.DataBufferAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

public class openData extends LoginActivity {
    private static final String TAG = "openData";

    private static DriveFolder parent;
    private ListView mFileContents;
    private DataBufferAdapter<Metadata> mResultsAdapter;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_activity);
        mFileContents = findViewById(R.id.list);
        mResultsAdapter = new ResultsAdapter(this);
        mFileContents.setAdapter(mResultsAdapter);

        final Task<DriveFolder> appFolderTask = getDriveResourceClient().getAppFolder();

        // Wait for tasks to complete
        Tasks.whenAll(appFolderTask)
                .continueWithTask(new Continuation<Void, Task<DriveFolder>>() {
                    @Override
                    public Task<DriveFolder> then(@NonNull Task<Void> task) throws Exception {
                        parent = appFolderTask.getResult();
                        retrieve();
                        return appFolderTask;
                    }});

    }

    private void retrieve() {
        Log.i(TAG, "Opening...");

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "photo.png"))
                .build();
        Task<MetadataBuffer> queryTask = getDriveResourceClient().queryChildren(parent, query);

        queryTask
                .addOnSuccessListener(this,
                        new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {
                                mResultsAdapter.append(metadataBuffer);
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error retrieving files", e);
                        finish();
                    }
                });
    }
}
