package fr.benoitsauvage.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;

public class Mob implements Runnable {

    GameView parent;
    Handler handler;

    boolean is_alive = true;
    boolean go_right = true;

    int pos_x, move_x;
    int start_x, end_x, height;

    int GRID_X = 1;
    int GRID_Y = 5;

    Rect src;
    Bitmap image;

    public Mob(GameView view, Handler h, int start, int end, int height) {
        parent = view;
        handler = h;

        this.start_x = start * parent.GRID_CELL_SIZE;
        this.end_x = end * parent.GRID_CELL_SIZE;
        this.height = height;

        pos_x = (start - 1) * parent.GRID_CELL_SIZE;

        src = new Rect(
                (GRID_X - 1) * parent.IMAGE_SIZE,
                (GRID_Y - 1) * parent.IMAGE_SIZE,
                GRID_X * parent.IMAGE_SIZE,
                GRID_Y * parent.IMAGE_SIZE
        );

        image = parent.image;
    }

    @Override
    public void run() {
        if (is_alive) {
            parent.invalidate();

            if (go_right && end_x > pos_x) {
                move_x = +5;

                if (end_x - parent.GRID_CELL_SIZE <= pos_x + move_x)
                    go_right = false;
            }

            if (!go_right && start_x - parent.GRID_CELL_SIZE < pos_x) {
                move_x = -5;

                if (start_x - parent.GRID_CELL_SIZE >= pos_x + move_x)
                    go_right = true;
            }

            pos_x += move_x;

            handler.post(this);
        }
    }

    public void render(Canvas canvas) {
        RectF dest = new RectF(
                pos_x,
                (height - 1) * parent.GRID_CELL_SIZE,
                pos_x + parent.GRID_CELL_SIZE,
                height * parent.GRID_CELL_SIZE
        );

        canvas.drawBitmap(image, src, dest, null);
    }
}
