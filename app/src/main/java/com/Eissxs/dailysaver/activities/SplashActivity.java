package com.Eissxs.dailysaver.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import androidx.appcompat.app.AppCompatActivity;
import com.Eissxs.dailysaver.R;

public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_DELAY = 2500; // 2.5 seconds
    private static final long ANIMATION_DURATION = 1000; // 1 second
    private static final long LOADING_DELAY = 500; // 0.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Find views
        View logoView = findViewById(R.id.splashLogo);
        View titleView = findViewById(R.id.splashTitle);
        View loadingView = findViewById(R.id.loadingIndicator);

        // Create animations
        ObjectAnimator logoFadeIn = ObjectAnimator.ofFloat(logoView, "alpha", 0f, 1f);
        ObjectAnimator logoScaleX = ObjectAnimator.ofFloat(logoView, "scaleX", 0.8f, 1f);
        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(logoView, "scaleY", 0.8f, 1f);
        
        ObjectAnimator titleFadeIn = ObjectAnimator.ofFloat(titleView, "alpha", 0f, 1f);
        ObjectAnimator titleTranslateY = ObjectAnimator.ofFloat(titleView, "translationY", 50f, 0f);
        
        ObjectAnimator loadingFadeIn = ObjectAnimator.ofFloat(loadingView, "alpha", 0f, 1f);

        // Set up logo animation set
        AnimatorSet logoAnimSet = new AnimatorSet();
        logoAnimSet.playTogether(logoFadeIn, logoScaleX, logoScaleY);
        logoAnimSet.setDuration(ANIMATION_DURATION);
        logoAnimSet.setInterpolator(new DecelerateInterpolator());

        // Set up title animation set
        AnimatorSet titleAnimSet = new AnimatorSet();
        titleAnimSet.playTogether(titleFadeIn, titleTranslateY);
        titleAnimSet.setDuration(ANIMATION_DURATION);
        titleAnimSet.setStartDelay(LOADING_DELAY);
        titleAnimSet.setInterpolator(new DecelerateInterpolator());

        // Set up loading animation
        loadingFadeIn.setDuration(ANIMATION_DURATION);
        loadingFadeIn.setStartDelay(LOADING_DELAY * 2);
        loadingFadeIn.setInterpolator(new AccelerateDecelerateInterpolator());

        // Play all animations together
        AnimatorSet mainAnimSet = new AnimatorSet();
        mainAnimSet.playTogether(logoAnimSet, titleAnimSet, loadingFadeIn);
        mainAnimSet.start();

        // Navigate to HomeActivity after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DELAY);
    }
} 