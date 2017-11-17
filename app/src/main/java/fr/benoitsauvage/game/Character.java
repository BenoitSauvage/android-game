package fr.benoitsauvage.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;

public class Character implements Runnable {

    GameView parent;
    Handler handler;

    boolean has_to_move = false;

    boolean is_moving;
    boolean is_jumping;
    boolean is_moving_jump;

    boolean is_moving_right;
    boolean is_moving_left;

    boolean can_move_right;
    boolean can_move_left;

    Tile onTile;

    int JUMP_HEIGHT = 500;
    int PLAYER_HEIGHT;
    int INVICIBILITY = 0;

    int LIFE = 6;

    int x = 0, y, jump_start;
    int w, h;

    int move_x = 0;

    public Character(GameView view, Handler h) {
        parent = view;
        handler = h;

        is_moving = false;
    }

    @Override
    public void run() {
        parent.invalidate();

        if (has_to_move) {
            // parent.checkMobs();

            if (INVICIBILITY > 0) {
                --INVICIBILITY;
            }

            if (is_moving) {
                parent.checkColission();

                if (move_x > 0 && parent.getWidth() >= (x + w + move_x) && can_move_right)
                    x += move_x;

                if (move_x < 0 && (x + move_x) >= 0  && can_move_left)
                    x += move_x;
            }

            if (is_jumping) {
                if (y > (jump_start - JUMP_HEIGHT))
                    goUp();
                else {
                    is_jumping = false;
                }
            } else {
                if (y < PLAYER_HEIGHT)
                    goDown();
                else {
                    is_moving_jump = false;
                }
            }
        } else {
            parent.moveBackground();
        }

        handler.post(this);
    }

    public void render(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.RED);

        if (INVICIBILITY > 0) {
            p.setColor(Color.BLUE);
        }

        canvas.drawRect(x, y - h, x + w, y, p);
    }

    public void moveRight() {
        is_moving_left = false;
        is_moving_right = true;
        is_moving = true;
        move_x = 10;
    }

    public void moveLeft() {
        is_moving_right = false;
        is_moving_left = true;
        is_moving = true;
        move_x = -10;
    }

    public void moveUp() {
        if (!is_moving_jump) {
            jump_start = y;
            is_moving_jump = true;
            is_jumping = true;
        }
    }

    private void goUp() {
        y -= 40;
    }

    private void goDown() {
        if (y + 20 > PLAYER_HEIGHT)
            y += (PLAYER_HEIGHT - y);
        else
            y += 20;
    }
}
