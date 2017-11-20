package fr.benoitsauvage.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
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

    boolean is_alive = true;

    Tile onTile;

    int JUMP_HEIGHT = 500;
    int PLAYER_HEIGHT;
    int INVICIBILITY = 0;

    int LIFE = 6;

    int x = 0, y, jump_start;
    int w, h;

    int move_x = 0;

    int GRID_X = 1;
    int GRID_Y = 4;

    Bitmap image;
    Rect src;

    public Character(GameView view, Handler h) {
        parent = view;
        handler = h;

        is_moving = false;

        image = parent.image;
        src = new Rect(
                (GRID_X - 1) * parent.IMAGE_SIZE,
                (GRID_Y - 1) * parent.IMAGE_SIZE,
                GRID_X * parent.IMAGE_SIZE,
                GRID_Y * parent.IMAGE_SIZE
        );
    }

    @Override
    public void run() {
        if (is_alive) {
            parent.invalidate();

            parent.checkMobs();

            if (INVICIBILITY > 0) {
                --INVICIBILITY;
            }

            if (has_to_move) {
                if (is_moving) {
                    parent.checkColission();

                    if (move_x > 0 && parent.getWidth() >= (x + w + move_x) && can_move_right)
                        x += move_x;

                    if (move_x < 0 && (x + move_x) >= 0  && can_move_left)
                        x += move_x;
                }
            } else {
                parent.moveBackground();
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

            handler.post(this);
        }
    }

    public void render(Canvas canvas) {
        Paint p = new Paint();
        p.setAlpha(255);

        if (INVICIBILITY > 0) {
            if (INVICIBILITY % 10 == 0)
                p.setAlpha(0);
        }

        RectF dest = new RectF(x, y - h, x + w, y);
        canvas.drawBitmap(image, src, dest, p);
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
