package fr.benoitsauvage.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.UUID;

public class GameOverActivity extends Activity {

    Context context;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    EditText input;

    long start_time;
    long end_time;

    int score;
    final int MAX_TIME = 120000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        context = this;

        sharedPref = this.getSharedPreferences("scores", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        Intent intent = getIntent();
        start_time = intent.getLongExtra("time", 0);
        end_time = System.currentTimeMillis();
        String title = intent.getStringExtra("title");
        int life = intent.getIntExtra("life", 0);

        Button menu = findViewById(R.id.game_over_menu);
        Button ok = findViewById(R.id.input_ok);
        TextView score_text = findViewById(R.id.score);
        TextView title_text = findViewById(R.id.game_over_title);
        input = findViewById(R.id.player_name);

        score = (int) (MAX_TIME - (end_time - start_time)) / 100;
        score += life * 50;

        if (intent.getBooleanExtra("win", false))
            score += 1000;
        else
            score = (score - 1000) > 0 ? (score - 1000) : 0;

        score_text.setText(Integer.toString(score));
        title_text.setText(title.toUpperCase());

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = input.getText().toString().trim();

                if (name.length() != 0) {
                    UUID uuid = UUID.randomUUID();
                    editor.putString(uuid.toString(), name + ";" + Integer.toString(score));
                    editor.commit();
                    backToHome();
                } else {
                    Toast.makeText(context, getString(R.string.no_name), Toast.LENGTH_SHORT).show();
                }
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               backToHome();
            }
        });
    }

    public void backToHome() {
        Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
