package fr.benoitsauvage.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

class GameView extends View {

    Bitmap background;
    Rect backgroundSrc;
    RectF backgroundDest;

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

    int height, width;

    long start_time;

    public GameView(Context c, Handler h) {
        super(c);
        context = c;
        handler = h;

        start_time = System.currentTimeMillis();

        image = BitmapFactory.decodeResource(getResources(), R.drawable.spritesheet);
        IMAGE_SIZE = image.getWidth() / NB_COLUMNS;

        character = new Character(this, handler);

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
        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;

        GRID_CELL_SIZE = height / 10;
        GROUND_HEIGHT = GRID_CELL_SIZE * 7;
        int PLAYER_HEIGHT = GROUND_HEIGHT - GRID_CELL_SIZE;

        setMeasuredDimension(width, GROUND_HEIGHT);
        backgroundSrc = new Rect(0, 0, width, height);
        backgroundDest = new RectF(0, 0, width, height);

        character.PLAYER_HEIGHT = PLAYER_HEIGHT;
        character.y = PLAYER_HEIGHT;
        character.w = height / 10;
        character.h = character.w;

        tileGenerator.GRID_CELL_SIZE = GRID_CELL_SIZE;

        try {
            tileGenerator.generateTiles();
            Log.d("TILE", "GENERATE");
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

        canvas.drawBitmap(background, backgroundSrc, backgroundDest, null);
        mobGenerator.renderMobs(canvas);
        lifeManager.render(canvas);
        character.render(canvas);

        if (character.x >= 300) {
            character.has_to_move = false;
        }
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

        for (Mob mob : mobGenerator.mobs) {

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
            handler.removeCallbacksAndMessages(null);
            gameOver();
        }
    }

    private void gameOver() {
        Intent intent = new Intent(getContext(), GameOverActivity.class);
        intent.putExtra("time", start_time);
        getContext().startActivity(intent);
    }

    public void drawBackground(ArrayList<Tile> tiles) {
        int w = 8000;

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(w, height, conf); // this creates a MUTABLE bitmap
        Canvas canvas = new Canvas(bmp);

        for (Tile tile : tiles) {
            tile.render(canvas);
        }

        background = bmp;
    }

    public void moveBackground() {

        int delta = 0;

        if (character.is_moving) {
            if (character.is_moving_right && character.can_move_right) {
                delta = 10;
            }

            if (character.is_moving_left && character.can_move_left)
                delta = -10;

            backgroundSrc = new Rect(backgroundSrc.left + delta, 0, backgroundSrc.right + delta, height);
            character.move_x = delta;

            if (backgroundSrc.left <= 0) {
                character.has_to_move = true;
                character.x -= 10;
            }

            checkColission();

            for (Tile tile : tileGenerator.tiles) {
                tile.pos_x -= delta;
            }

            for (Mob mob : mobGenerator.mobs) {
                mob.start_x -= delta;
                mob.end_x -= delta;
                mob.pos_x -= delta;
            }
        }
    }
}