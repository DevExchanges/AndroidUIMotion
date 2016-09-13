package info.devexchanges.uimotion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SharingActivity extends AppCompatActivity implements View.OnClickListener {

    private int defaultAnimDuration;

    private ViewGroup rootView;
    private ImageView backgroundView;
    private View btnTwitter, btnFacebook, btnInstagram, btnGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);

        defaultAnimDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        // Find view references
        rootView = (ViewGroup) findViewById(R.id.content_root);
        backgroundView = (ImageView) findViewById(R.id.background);
        btnFacebook = findViewById(R.id.facebook);
        btnInstagram = findViewById(R.id.instagram);
        btnTwitter = findViewById(R.id.twitter);
        btnGoogle = findViewById(R.id.google_plus);

        if (savedInstanceState == null) {
            // Setup initial states
            backgroundView.setVisibility(View.INVISIBLE);
            btnGoogle.setAlpha(0);
            btnTwitter.setAlpha(0);
            btnFacebook.setAlpha(0);
            btnInstagram.setAlpha(0);
        }

        btnGoogle.setOnClickListener(this);
        btnFacebook.setOnClickListener(this);
        btnInstagram.setOnClickListener(this);
        btnTwitter.setOnClickListener(this);


        getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                getWindow().getSharedElementEnterTransition().removeListener(this);
                revealTheBackground();
                showTheItems();
            }
        });
    }

    @Override
    public void onBackPressed() {
        hideTheItems();
        hideTheBackground();
    }

    /**
     * Reveal the background
     */
    private void revealTheBackground() {
        backgroundView.setVisibility(View.VISIBLE);
        Animator reveal = createRevealAnimator(true);
        reveal.start();
    }

    private void hideTheBackground() {
        Animator hide = createRevealAnimator(false);
        hide.setStartDelay(defaultAnimDuration);
        hide.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                backgroundView.setVisibility(View.INVISIBLE);
                supportFinishAfterTransition();
            }
        });
        hide.start();
    }

    private void showTheItems() {
        View[] itemViews = {btnFacebook, btnInstagram, btnTwitter, btnGoogle};
        for (int i = 0; i < itemViews.length; i++) {
            View itemView = itemViews[i];
            long startDelay = (defaultAnimDuration / itemViews.length) * (i + 1);
            itemView.animate().alpha(1).setStartDelay(startDelay);
        }
    }

    private void hideTheItems() {
        View[] itemViews = {btnFacebook, btnInstagram, btnTwitter, btnGoogle};
        for (int i = 0; i < itemViews.length; i++) {
            View itemView = itemViews[i];
            long startDelay = (defaultAnimDuration / itemViews.length) * (itemViews.length - i);
            itemView.animate().alpha(0).setStartDelay(startDelay);
        }
    }

    private Animator createRevealAnimator(boolean show) {
        final int cx = backgroundView.getWidth() / 2;
        final int cy = backgroundView.getHeight() / 2;
        // A lit bit more than half the width and half the height because this view is a square
        // and it's not going to perfectly align with a circle.
        final int radius = (int) Math.hypot(cx, cy);
        final Animator animator;
        if (show) {
            animator = ViewAnimationUtils.createCircularReveal(backgroundView, cx, cy, 0, radius);
            animator.setInterpolator(new DecelerateInterpolator());
        } else {
            animator = ViewAnimationUtils.createCircularReveal(backgroundView, cx, cy, radius, 0);
            animator.setInterpolator(new AccelerateInterpolator());
        }
        animator.setDuration(defaultAnimDuration);
        return animator;
    }

    @Override
    public void onClick(View view) {
        showToast(view.getId());
        // Load the transition
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.sharing_item_chosen);
        // Finish this Activity when the transition is ended
        transition.addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                finish();
                // Override default transition to fade out
                overridePendingTransition(0, android.R.anim.fade_out);
            }
        });
        // Capture current values in the scene root and then post a request to run a transition on the next frame
        TransitionManager.beginDelayedTransition(rootView, transition);

        // 1. Item chosen
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        view.setLayoutParams(layoutParams);

        // 2. Rest of items
        View[] itemViews = {btnFacebook, btnInstagram, btnTwitter, btnGoogle};
        for (View itemView : itemViews) {
            if (itemView != view) {
                itemView.setVisibility(View.INVISIBLE);
            }
        }

        // 3. Background
        double diagonal = Math.sqrt(rootView.getHeight() * rootView.getHeight() + rootView.getWidth() * rootView.getWidth());
        float radius = (float) (diagonal / 2f);
        int h = backgroundView.getDrawable().getIntrinsicHeight();
        float scale = radius / (h / 2f);
        Matrix matrix = new Matrix(backgroundView.getImageMatrix());
        matrix.postScale(scale, scale, backgroundView.getWidth() / 2f, backgroundView.getHeight() / 2f);
        backgroundView.setScaleType(ImageView.ScaleType.MATRIX);
        backgroundView.setImageMatrix(matrix);
    }

    private void showToast(int viewId) {
        switch (viewId) {
            case R.id.facebook:
                Toast.makeText(this, "You have shared this content to Facebook", Toast.LENGTH_SHORT).show();
                break;

            case R.id.twitter:
                Toast.makeText(this, "You have shared this content to Twitter", Toast.LENGTH_SHORT).show();
                break;

            case R.id.google_plus:
                Toast.makeText(this, "You have shared this content to Google+", Toast.LENGTH_SHORT).show();
                break;

            case R.id.instagram:
                Toast.makeText(this, "You have shared this content to Instagram", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }
}


