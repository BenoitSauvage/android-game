package fr.benoitsauvage.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class ScoresActivity extends Activity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        sharedPref = this.getSharedPreferences("scores", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        LinearLayout layout = findViewById(R.id.scores_layout);

        Map<String, ?> allEntries = sharedPref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            layout.addView(generateLayout(entry));
        }

        Button clear = findViewById(R.id.scores_clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear().apply();
                finish();
            }
        });
    }

    private LinearLayout generateLayout(final Map.Entry<String, ?> entry) {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.score_layout, null, false);

        TextView score_name = layout.findViewById(R.id.score_name);
        TextView score = layout.findViewById(R.id.score_score);

        Log.d("VALUE", entry.getValue().toString());

        String[] value = entry.getValue().toString().split(";");
        score_name.setText(value[0]);
        score.setText(value[1]);

        return layout;
    }
}
