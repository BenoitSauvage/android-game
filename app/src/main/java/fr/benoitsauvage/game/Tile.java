package fr.benoitsauvage.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

class Tile {

    Bitmap bmp;
    Rect src;

    int pos_x, pos_y;
    int GRID_CELL_SIZE;

    public Tile(Bitmap image, int lx, int ly, int x, int y, int image_size, int cell_size) {
        int bmp_x = (ly - 1) * image_size;
        int bmp_y = (lx - 1) * image_size;

        pos_x = x;
        pos_y = y;

        bmp = image;
        src = new Rect(bmp_x, bmp_y, bmp_x + image_size, bmp_y + image_size);

        GRID_CELL_SIZE = cell_size;
    }

    public void render(Canvas canvas) {
        RectF dest = new RectF(pos_x, pos_y - GRID_CELL_SIZE, pos_x + GRID_CELL_SIZE, pos_y);
        canvas.drawBitmap(bmp, src, dest, null);
    }
}