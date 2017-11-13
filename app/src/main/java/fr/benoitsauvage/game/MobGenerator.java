package fr.benoitsauvage.game;

import android.graphics.Canvas;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class MobGenerator {

    GameView parent;

    int IMAGE_SIZE;
    int NB_COLUMNS = 5;

    String json;

    ArrayList<Mob> mobs = new ArrayList<>();

    public MobGenerator(GameView view, String jsonString) {
        parent = view;
        IMAGE_SIZE = parent.image.getWidth() / NB_COLUMNS;

        json = jsonString;
    }

    public void generateMobs() throws JSONException {
        JSONArray array = new JSONArray(json);

        for (int i = 0; i < array.length(); ++i) {
            JSONObject object = array.getJSONObject(i);

            int start = object.getInt("start");
            int end = object.getInt("end");
            int height = object.getInt("height");

            Mob mob = new Mob(parent, parent.handler, start, end, height);
            mobs.add(mob);

            parent.handler.post(mob);
        }
    }

    public void renderMobs(Canvas canvas) {
        for (Mob mob : mobs) {
            mob.render(canvas);
        }
    }

}
