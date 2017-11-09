package fr.benoitsauvage.game;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

class GameView extends View {

    Context context;
    Handler handler;
    Character character;

    TileGenerator tileGenerator;

    float density;
    int GROUND_HEIGHT;
    int GRID_CELL_SIZE;

    int DIRECTION_RIGHT = 0;
    int DIRECTION_LEFT = 1;

    public GameView(Context context, Handler h) {
        super(context);
        this.context = context;
        handler = h;

        String json = loadJSON();

        character = new Character(this, handler);
        tileGenerator = new TileGenerator(this, json);
        handler.post(character);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        density = getResources().getDisplayMetrics().density;
        int width = getResources().getDisplayMetrics().widthPixels;

        GRID_CELL_SIZE = getResources().getDisplayMetrics().heightPixels / 10;
        GROUND_HEIGHT = GRID_CELL_SIZE * 7;
        int PLAYER_HEIGHT = GROUND_HEIGHT - GRID_CELL_SIZE;

        setMeasuredDimension(width, GROUND_HEIGHT);

        character.PLAYER_HEIGHT = PLAYER_HEIGHT;
        character.y = PLAYER_HEIGHT;
        character.w = getResources().getDisplayMetrics().heightPixels / 10;
        character.h = character.w;

        tileGenerator.GRID_CELL_SIZE = GRID_CELL_SIZE;

        try {
            tileGenerator.generateTiles();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        tileGenerator.renderTiles(canvas);
        character.render(canvas);
    }

    public void checkColission() {
        character.can_move_left = true;
        character.can_move_right = true;

        for (Tile tile : tileGenerator.tiles) {
            if (character.is_moving_right) {
                if (checkTileRight(tile))
                    character.can_move_right = false;
            }

            if (character.is_moving_left) {
                if (checkTileLeft(tile))
                    character.can_move_left = false;
            }

            if (character.is_jumping_down) {

            }
        }
    }

    private boolean checkTileRight(Tile tile) {
        boolean x = (int) ((character.x + GRID_CELL_SIZE + character.move_x) / GRID_CELL_SIZE) == (int) (tile.pos_x / GRID_CELL_SIZE);
        boolean y = (int) ((character.y + GRID_CELL_SIZE - 10) / GRID_CELL_SIZE) == (int) (tile.pos_y / GRID_CELL_SIZE);

        return x && y;
    }

    private boolean checkTileLeft(Tile tile) {
        boolean x = (int) ((character.x + character.move_x) / GRID_CELL_SIZE) == (int) ((tile.pos_x + GRID_CELL_SIZE) / GRID_CELL_SIZE - 1);
        boolean y = (int) ((character.y + GRID_CELL_SIZE - 10) / GRID_CELL_SIZE) == (int) (tile.pos_y / GRID_CELL_SIZE);

        return x && y;
    }

    private String loadJSON() {
        String json;

        try {
            InputStream is = getContext().getAssets().open("tiles.json");

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }
}