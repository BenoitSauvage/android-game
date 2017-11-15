package fr.benoitsauvage.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

class GameView extends View {

    Context context;
    Handler handler;
    Character character;

    LifeManager lifeManager;

    TileGenerator tileGenerator;
    MobGenerator mobGenerator;

    Bitmap image;

    float density;
    int GROUND_HEIGHT;
    int GRID_CELL_SIZE;
    final int NB_COLUMNS = 5;
    int IMAGE_SIZE;

    public GameView(Context context, Handler h) {
        super(context);
        this.context = context;
        handler = h;

        image = BitmapFactory.decodeResource(getResources(), R.drawable.spritesheet);
        IMAGE_SIZE = image.getWidth() / NB_COLUMNS;

        character = new Character(this, handler);

        // Init player life
        character.LIFE = 6;

        // Init player deplacement
        character.has_to_move = true;

        handler.post(character);

        // Generate tiles
        String tilesJson = loadTilesJSON();
        tileGenerator = new TileGenerator(this, tilesJson);

        // Generate mobs
        String mobsJson = loadMobsJSON();
        mobGenerator = new MobGenerator(this, mobsJson);

        // Init life manager
        lifeManager = new LifeManager(this, character);
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

        try {
            mobGenerator.generateMobs();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        checkGameOver();

        tileGenerator.renderTiles(canvas);
        mobGenerator.renderMobs(canvas);
        lifeManager.render(canvas);
        character.render(canvas);
    }

    public void checkColission() {
        character.can_move_left = true;
        character.can_move_right = true;

        for (Tile tile : tileGenerator.tiles) {
            if (character.is_moving_right && checkTileRight(tile))
                character.can_move_right = false;

            if (character.is_moving_left && checkTileLeft(tile))
                character.can_move_left = false;
        }

        checkTileDown();
    }

    public void checkMobs() {
        if (character.INVICIBILITY > 0)
            return;

        Rect playerRect = new Rect(
                character.x,
                character.y,
                character.x + GRID_CELL_SIZE,
                character.y + GRID_CELL_SIZE
        );

        // Log.d("COLLISION", Integer.toString(character.x) + " " + Integer.toString(character.y));

        for (Mob mob : mobGenerator.mobs) {
            // Log.d("COLLISION", Integer.toString(mob.pos_x) + " " + Integer.toString(mob.height * GRID_CELL_SIZE));

            Rect mobRect = new Rect(
                    mob.pos_x,
                    mob.height * GRID_CELL_SIZE,
                    mob.pos_x + GRID_CELL_SIZE,
                    mob.height * GRID_CELL_SIZE + GRID_CELL_SIZE
            );
            if (playerRect.intersect(mobRect)) {
                character.LIFE -= 1;
                character.INVICIBILITY = 100;
            }
        }
    }

    private boolean checkTileRight(Tile tile) {
        boolean x = (character.x + GRID_CELL_SIZE + character.move_x) / GRID_CELL_SIZE == tile.pos_x / GRID_CELL_SIZE;
        boolean y = (character.y + GRID_CELL_SIZE - 10) / GRID_CELL_SIZE == tile.pos_y / GRID_CELL_SIZE;

        return x && y;
    }

    private boolean checkTileLeft(Tile tile) {
        boolean x = (character.x + character.move_x) / GRID_CELL_SIZE == (tile.pos_x + GRID_CELL_SIZE) / GRID_CELL_SIZE - 1;
        boolean y = (character.y + GRID_CELL_SIZE - 10) / GRID_CELL_SIZE == tile.pos_y / GRID_CELL_SIZE;

        return x && y;
    }

    private void checkTileDown() {
        ArrayList<Tile> underTiles = new ArrayList<>();

        int x1 = character.x / GRID_CELL_SIZE + 1;
        int x2 = (character.x + GRID_CELL_SIZE) / GRID_CELL_SIZE + 1;

        int y = (character.y + GRID_CELL_SIZE + 10) / GRID_CELL_SIZE;

        for (Tile tile : tileGenerator.tiles) {
            if ((tile.pos_x / GRID_CELL_SIZE) + 1 == x1 || (tile.pos_x / GRID_CELL_SIZE) + 1 == x2) {
                underTiles.add(tile);
            }
        }

        Tile underTile = null;

        for (Tile tile : underTiles) {
            if (null == underTile || tile.pos_y < underTile.pos_y) {
                underTile = tile;
            }
        }

        if (underTile == null)
            character.PLAYER_HEIGHT = GRID_CELL_SIZE * 10;
        else
            character.PLAYER_HEIGHT = ((underTile.pos_y / GRID_CELL_SIZE) - 1) * GRID_CELL_SIZE;

        Log.d("TILE", Integer.toString(character.PLAYER_HEIGHT));
    }

    private String loadTilesJSON() {
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

    private String loadMobsJSON() {
        String json;

        try {
            InputStream is = getContext().getAssets().open("mobs.json");

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

    private void checkGameOver() {
        if (character.LIFE <= 0) {
            gameOver();
        }
    }

    private void gameOver() {
        Intent intent = new Intent(context, GameOverActivity.class);
        context.startActivity(intent);
    }
}