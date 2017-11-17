package fr.benoitsauvage.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class TileGenerator {

    GameView parent;
    Bitmap image;

    int IMAGE_SIZE;
    int GRID_CELL_SIZE;
    int NB_COLUMNS = 5;

    String json;

    ArrayList<Tile> tiles = new ArrayList<>();

    public TileGenerator(GameView view, String jsonString) {
        parent = view;
        image = BitmapFactory.decodeResource(view.getResources(), R.drawable.spritesheet);
        IMAGE_SIZE = image.getWidth() / NB_COLUMNS;

        json = jsonString;
    }

    public void generateTiles() throws JSONException {
        JSONArray array = new JSONArray(json);

        for (int i = 0; i < array.length(); ++i) {
            JSONObject object = array.getJSONObject(i);

            int x = object.getInt("x") * GRID_CELL_SIZE;
            int y = object.getInt("y") * GRID_CELL_SIZE;

            int lx = object.getInt("lx");
            int ly = object.getInt("ly");

            Tile tile = new Tile(image, lx, ly, x, y, IMAGE_SIZE, GRID_CELL_SIZE);
            tiles.add(tile);
        }

        parent.character.onTile = tiles.get(0);

        parent.drawBackground(tiles);
    }

    public void renderTiles(Canvas canvas) {
        for (Tile tile : tiles) {
            tile.render(canvas);
        }
    }
}
