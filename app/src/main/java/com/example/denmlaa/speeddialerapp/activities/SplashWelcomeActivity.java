package com.example.denmlaa.speeddialerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.denmlaa.speeddialerapp.R;

public class SplashWelcomeActivity extends AppCompatActivity {

    private TextView splash_text;
    private ImageView splash_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_welcome);

        splash_image = findViewById(R.id.splash_image);
        splash_text = findViewById(R.id.splash_text);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        splash_text.startAnimation(anim);
        splash_image.startAnimation(anim);

        // Delayed intent for MainActivity for 2000 milisec. User is redirected after splash animation is done
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainActivityIntent = new Intent(SplashWelcomeActivity.this, MainActivity.class);
                startActivity(mainActivityIntent);
                finish();
            }
        }, 2000);

    }

}
