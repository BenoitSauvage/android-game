package fr.benoitsauvage.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GameOverActivity extends Activity {

    Context context;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    EditText input;

    long start_time;
    long end_time;

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

        Button menu = findViewById(R.id.game_over_menu);
        Button ok = findViewById(R.id.input_ok);
        TextView score = findViewById(R.id.score);
        input = findViewById(R.id.player_name);

        score.setText(Long.toString((end_time - start_time) / 100));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = input.getText().toString().trim();

                if (name.length() != 0) {
                    editor.putLong(name, (end_time - start_time) / 100);
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
