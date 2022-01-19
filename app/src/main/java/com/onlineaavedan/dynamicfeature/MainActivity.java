package com.onlineaavedan.dynamicfeature;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    Button btn_downloadand_start, btn_startactivity;

    private int mySessionId;
    private static final String TAG = "MainActivity";
    private SplitInstallManager splitInstallManager;
    ProgressDialog progressDialog;
    public static String CLASSPATH = "com.onlineaavedan.picturemodule.PictureActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_downloadand_start = findViewById(R.id.btn_download_and_start);
        btn_startactivity = findViewById(R.id.btn_start);

        splitInstallManager = SplitInstallManagerFactory.create(getApplicationContext());

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Module Downloading ....");
        btn_downloadand_start.setOnClickListener(view -> {
            progressDialog.show();
            downloadDynamicModule();
        });

        btn_startactivity.setOnClickListener(V ->
        {
            if (isClass(CLASSPATH)) {
                StartNextClass();
            } else {
                Toast.makeText(getApplicationContext(), "Class Not Found", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void StartNextClass() {
        Intent i = new Intent();
        i.setClassName(BuildConfig.APPLICATION_ID, CLASSPATH);
        i.putExtra("ExtraInt", 3); // Test intent for Dynamic feature
        startActivity(i);

    }


    private void downloadDynamicModule() {


        SplitInstallRequest request = SplitInstallRequest.newBuilder().addModule(getString(R.string.title_picturemodule)).build();

        SplitInstallStateUpdatedListener listener = new SplitInstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(SplitInstallSessionState splitInstallSessionState) {
                if (splitInstallSessionState.sessionId() == mySessionId) {
                    switch (splitInstallSessionState.status()) {
                        case SplitInstallSessionStatus.DOWNLOADING:
                            Toast.makeText(MainActivity.this, "Dynamic Module downloading", Toast.LENGTH_SHORT).show();
                            // Update progress bar.
                            break;
                        case SplitInstallSessionStatus.INSTALLED:
                            Log.d(TAG, "Dynamic Module downloaded");
                            Toast.makeText(MainActivity.this, "Dynamic Module downloaded", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Session Not Created", Toast.LENGTH_SHORT).show();
                }
            }
        };

        splitInstallManager.registerListener(listener);

        splitInstallManager.startInstall(request).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.this, "Failed to Install " + e, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "ExceptionV: " + e);
            }
        }).addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer sessionId) {
                mySessionId = sessionId;
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Success" + sessionId, Toast.LENGTH_SHORT).show();
                if (isClass(CLASSPATH)) {
                    StartNextClass();
                } else {
                    Toast.makeText(getApplicationContext(), "Class Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}