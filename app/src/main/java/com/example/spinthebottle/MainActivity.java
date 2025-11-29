package com.example.spinthebottle;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class MainActivity extends AppCompatActivity {


    private ImageView bottle;
    private TextView timerText, bigCountdown, resultText;
    private RelativeLayout gameContainer;
    private KonfettiView konfettiView;

    private View splashOverlay;
    private ImageView splashLogo;
    private TextView splashText;

    private boolean isSpinning = false;
    private long gameDuration = 10000;
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gameContainer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        bottle = findViewById(R.id.bottleImage);
        timerText = findViewById(R.id.timerText);
        bigCountdown = findViewById(R.id.bigCountdownText);
        resultText = findViewById(R.id.resultText);
        gameContainer = findViewById(R.id.gameContainer);
        konfettiView = findViewById(R.id.konfettiView);
        ImageButton menuBtn = findViewById(R.id.menuButton);

        splashOverlay = findViewById(R.id.splashOverlay);
        splashLogo = findViewById(R.id.splashLogo);
        splashText = findViewById(R.id.splashText);

        runSplashAnimation();

        menuBtn.setOnClickListener(v -> showSettingsDialog());

        bottle.setOnClickListener(v -> {
            if (!isSpinning) {
                startGame();
            }
        });

        resultText.setOnClickListener(v -> {
            if (!isSpinning) {
                startGame();
            }
        });
    }


    private void runSplashAnimation() {
        String[] messages = {"Drink Responsibly", "Party Time!", "Loading Fun...", "Spin to Win!"};
        splashText.setText(messages[random.nextInt(messages.length)]);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(splashLogo, "scaleX", 1f, 1.2f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(splashLogo, "scaleY", 1f, 1.2f);
        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleX.setRepeatMode(ObjectAnimator.REVERSE);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatMode(ObjectAnimator.REVERSE);

        AnimatorSet set = new AnimatorSet();
        set.play(scaleX).with(scaleY);
        set.setDuration(800);
        set.start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> splashOverlay.animate()
                .alpha(0f)
                .setDuration(500)
                .withEndAction(() -> splashOverlay.setVisibility(View.GONE))
                .start(), 3000);
    }


    private void startGame() {
        isSpinning = true;
        resultText.setVisibility(View.GONE);
        bigCountdown.setVisibility(View.GONE);

        RotateAnimation rotate = new RotateAnimation(0, 3600,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(2000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());
        bottle.startAnimation(rotate);

        new CountDownTimer(gameDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                String secondsStr = String.format(Locale.US, "%02d", seconds);
                timerText.setText(getString(R.string.timer_display, secondsStr));

                gameContainer.setBackgroundColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));

                if (seconds <= 3 && seconds > 0) {
                    bigCountdown.setVisibility(View.VISIBLE);
                    bigCountdown.setText(String.valueOf(seconds));
                }
            }

            @Override
            public void onFinish() {
                endGame();
            }
        }.start();
    }

    private void endGame() {
        isSpinning = false;
        bottle.clearAnimation();
        bigCountdown.setVisibility(View.GONE);


        resultText.setVisibility(View.VISIBLE);
        triggerConfetti();

        bottle.setRotation(random.nextInt(360));
    }


    private void showSettingsDialog() {
        String[] options = {"3 Seconds", "5 Seconds", "10 Seconds", "Reset"};
        new AlertDialog.Builder(this)
                .setTitle("Timer Settings")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: gameDuration = 3000; break;
                        case 1: gameDuration = 5000; break;
                        case 2: gameDuration = 10000; break;
                        case 3:
                            gameDuration = 10000;
                            timerText.setText(R.string.default_timer);
                            break;
                    }
                    if(!isSpinning) {
                        String secondsStr = String.format(Locale.US, "%02d", gameDuration/1000);
                        timerText.setText(getString(R.string.timer_display, secondsStr));
                    }
                })
                .show();
    }

    private void triggerConfetti() {
        EmitterConfig emitterConfig = new Emitter(300, TimeUnit.MILLISECONDS).max(300);
        konfettiView.start(
                new PartyFactory(emitterConfig)
                        .spread(360)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE))
                        .colors(Arrays.asList(0xffff00, 0xff0000, 0xff00ff, 0x00ffff))
                        .setSpeedBetween(10f, 30f)
                        .position(new Position.Relative(0.5, 0.5))
                        .build()
        );
    }
}