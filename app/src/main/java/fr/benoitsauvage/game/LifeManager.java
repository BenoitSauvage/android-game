package fr.benoitsauvage.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

public class LifeManager {

    GameView parent;
    Character character;

    Bitmap image;

    int NB_COLUMNS = 5;
    int IMAGE_SIZE;

    int MAX_LIFE = 6;

    public LifeManager(GameView view, Character character) {
        this.parent = view;
        this.character = character;

        image = BitmapFactory.decodeResource(parent.getResources(), R.drawable.spritesheet);
        IMAGE_SIZE = image.getWidth() / NB_COLUMNS;
    }

    public void render(Canvas canvas) {
        int line_x = 3;
        int line_y = 1;
        int pos_x = 1;

        for (int i = 2; i <= MAX_LIFE; i += 2) {
            line_y = getImageLineY(i);

            int bmp_x = (line_y - 1) * IMAGE_SIZE;
            int bmp_y = (line_x - 1) * IMAGE_SIZE;
            Rect src = new Rect(bmp_x, bmp_y, bmp_x + IMAGE_SIZE, bmp_y + IMAGE_SIZE);

            int x = (pos_x - 1) * parent.GRID_CELL_SIZE;
            RectF dest = new RectF(x, 0, x + parent.GRID_CELL_SIZE, parent.GRID_CELL_SIZE);

            canvas.drawBitmap(image, src, dest, null);

            ++pos_x;
        }
    }

    private int getImageLineY(int index) {
        if (character.LIFE >= index)
            return 1;
        else if (character.LIFE < index && character.LIFE > (index - 2))
            return 2;

        return 3;
    }

}
