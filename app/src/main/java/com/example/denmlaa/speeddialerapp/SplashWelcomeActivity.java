package com.example.denmlaa.speeddialerapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashWelcomeActivity extends AppCompatActivity {

    private TextView splash_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_welcome);

        splash_text = findViewById(R.id.splash_text);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        splash_text.startAnimation(anim);

        // Delayed check for permissions for 2000 milisec. Check starts after animation is done
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkForPermissions();
            }
        }, 2000);

    }

    private void checkForPermissions() {
        // Simple check for permissions
        // Passing requestCode = 1 for all permissions and String[] with permissions.
        final int PERMISSION_ALL = 1;
        final String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE};

        if (!hasPermissions(SplashWelcomeActivity.this, PERMISSIONS)) {
            // Simple permission description is shown before we start permission dialog
            AlertDialog.Builder warning_msg = new AlertDialog.Builder(SplashWelcomeActivity.this)
                    .setMessage("Allow Speed Dialer to access your contacts and manage calls. This will allow you to add contacts and make quick calls")
                    .setCancelable(false)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // On confirm we request permissions
                            ActivityCompat.requestPermissions(SplashWelcomeActivity.this, PERMISSIONS, PERMISSION_ALL);
                        }
                    });
            AlertDialog alert = warning_msg.create();
            alert.setTitle("Permissions");
            alert.show();

        } else {
            // If permissions are granted, user is redirected to MainActivity
            Intent mainActivityIntent = new Intent(SplashWelcomeActivity.this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }

    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // If permissions are granted, user is redirected to MainActivity
                    Intent mainActivityIntent = new Intent(SplashWelcomeActivity.this, MainActivity.class);
                    startActivity(mainActivityIntent);
                    finish();
                } else {
                    // If permissions are not granted, another dialog is shown and application shuts down
                    AlertDialog.Builder warning_msg = new AlertDialog.Builder(this)
                            .setMessage("In order to use this application, please turn on permissions")
                            .setCancelable(false)
                            .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                    AlertDialog alert = warning_msg.create();
                    alert.setTitle("Enable permissions");
                    alert.show();
                }
            }
        }
    }
}
