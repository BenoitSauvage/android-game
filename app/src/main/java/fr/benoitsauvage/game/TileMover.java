package fr.benoitsauvage.game;


import android.os.Handler;

public class TileMover implements Runnable {

    GameView parent;
    Handler handler;
    Character player;

    boolean has_to_move = false;

    boolean is_moving;
    boolean can_move_right;
    boolean can_move_left;

    int x, y;
    int move_x;

    public TileMover(GameView view, Handler h, Character p) {
        parent = view;
        handler = h;
        player = p;
    }

    @Override
    public void run() {
        if (has_to_move) {
            parent.invalidate();

            if (is_moving) {
                parent.checkColission();

                if (move_x > 0 && parent.getWidth() >= (x + player.w + player.move_x) && can_move_right)
                    x += move_x;

                if (move_x < 0 && (x + move_x) >= 0  && can_move_left)
                    x += move_x;
            }

            handler.post(this);
        }
    }
}
