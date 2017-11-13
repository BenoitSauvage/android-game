package fr.benoitsauvage.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class GameActivity extends Activity {

    Handler handler;
    GameView game;

    ImageButton control_left, control_right, control_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.activity_game);

        control_left = findViewById(R.id.control_left);
        control_right = findViewById(R.id.control_right);
        control_up = findViewById(R.id.control_up);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        game = new GameView(this, handler);

        ConstraintLayout layout = findViewById(R.id.layout);
        layout.addView(game, params);

        this.generateControls();
    }

    private void generateControls() {
        control_left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch ((event.getAction() & MotionEvent.ACTION_MASK)) {
                    case MotionEvent.ACTION_DOWN:
                        game.character.moveLeft();
                        break;
                    case MotionEvent.ACTION_UP:
                        game.character.is_moving = false;
                        game.character.is_moving_left = false;
                        game.character.move_x = 0;
                        break;
                }

                return false;
            }
        });

        control_right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch ((event.getAction() & MotionEvent.ACTION_MASK)) {
                    case MotionEvent.ACTION_DOWN:
                        game.character.moveRight();
                        break;
                    case MotionEvent.ACTION_UP:
                        game.character.is_moving = false;
                        game.character.is_moving_right = false;
                        game.character.move_x = 0;
                        break;
                }

                return false;
            }
        });

        control_up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch ((event.getAction() & MotionEvent.ACTION_MASK)) {
                    case MotionEvent.ACTION_DOWN:
                        game.character.moveUp();
                        break;
                    case MotionEvent.ACTION_UP:
                        game.character.is_jumping = false;
                        break;
                }

                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        String title = getResources().getString(R.string.dialog_title);
        String message = getResources().getString(R.string.dialog_message);

        game.character.has_to_move = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        game.character.has_to_move = true;
                        game.handler.post(game.character);
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}