package info.devexchanges.uimotion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_DRAWABLE = "drawable";
    public static final String EXTRA_TITLE = "title";

    private View btnShare;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        int drawable = getIntent().getExtras().getInt(EXTRA_DRAWABLE);
        CharSequence title = getIntent().getExtras().getCharSequence(EXTRA_TITLE);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(title);

        ImageView pictureView = (ImageView) findViewById(R.id.picture);
        pictureView.setImageResource(drawable);
        pictureView.setContentDescription(title);

        btnShare = findViewById(R.id.btn_share);
        textView = (TextView) findViewById(R.id.text);

        if (drawable == R.drawable.rose) {
            textView.setText(getString(R.string.rose));
        } else if (drawable == R.drawable.tulip) {
            textView.setText(getString(R.string.tulip));
        } else textView.setText(getString(R.string.sunflower));

        if (savedInstanceState == null) {
            btnShare.setScaleX(0);
            btnShare.setScaleY(0);
            getWindow().getEnterTransition().addListener(new TransitionAdapter() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    getWindow().getEnterTransition().removeListener(this);
                    btnShare.animate().scaleX(1).scaleY(1);
                }
            });
        }

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DetailsActivity.this,
                        btnShare, getString(R.string.share_transition_name));
                Intent intent = new Intent(DetailsActivity.this, SharingActivity.class);
                startActivity(intent, options.toBundle());
            }
        });
    }

    @Override
    public void onBackPressed() {
        btnShare.animate().scaleX(0).scaleY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                supportFinishAfterTransition();
            }
        });
    }

    public static class CustomFloatingActionButtonBehavior extends FloatingActionButton.Behavior {

        public CustomFloatingActionButtonBehavior(Context context, AttributeSet attrs) {
            super();
        }

        @Override
        public boolean onLayoutChild(CoordinatorLayout parent, FloatingActionButton child, int layoutDirection) {
            // Prevent the FAB disappears when return from called Activity
            return false;
        }

    }
}