package info.devexchanges.uimotion;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View rose = findViewById(R.id.rose);
        View sunflower = findViewById(R.id.sunflower);
        View tulip = findViewById(R.id.tulip);

        tulip.setOnClickListener(this);
        sunflower.setOnClickListener(this);
        rose.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.rose) {
            openDetailActivity(R.drawable.rose, "Rose", view);
        } else if (view.getId() == R.id.sunflower) {
            openDetailActivity(R.drawable.sunflower, "Sunflower", view);
        } else {
            openDetailActivity(R.drawable.tulip, "Tulip", view);
        }
    }

    private void openDetailActivity(int drawable, String title, View view) {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, view, getString(R.string.picture_transition_name));
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.EXTRA_DRAWABLE, drawable);
        intent.putExtra(DetailsActivity.EXTRA_TITLE, title);

        startActivity(intent, options.toBundle());
    }
}
