package Handler;

import java.awt.event.KeyEvent;

public class Keys {
    public static final int NUM_KEYS = 10;

    public static boolean keyState[] = new boolean[NUM_KEYS];//actiunea curenta
    public static boolean prevKeyState[] = new boolean[NUM_KEYS];//actiunea trecuta

    public static int UP = 0;
    public static int LEFT = 1;
    public static int DOWN = 2;
    public static int RIGHT = 3;
    public static int SHIFT = 4;
    public static int Q = 5;
    public static int X = 6;
    public static int SPACE = 7;
    public static int ENTER = 8;
    public static int ESCAPE = 9;

    public static void keySet(int i, boolean b) {
        if(i == KeyEvent.VK_UP) keyState[UP] = b;
        else if(i == KeyEvent.VK_LEFT) keyState[LEFT] = b;
        else if(i == KeyEvent.VK_DOWN) keyState[DOWN] = b;
        else if(i == KeyEvent.VK_RIGHT) keyState[RIGHT] = b;
        else if(i == KeyEvent.VK_SHIFT) keyState[SHIFT] = b;
        else if(i == KeyEvent.VK_Q) keyState[Q] = b;
        else if(i == KeyEvent.VK_X) keyState[X] = b;
        else if(i == KeyEvent.VK_SPACE) keyState[SPACE] = b;
        else if(i == KeyEvent.VK_ENTER) keyState[ENTER] = b;
        else if(i == KeyEvent.VK_ESCAPE) keyState[ESCAPE] = b;
    }

    public static void update() {//update cat timp e in rularea aplicatiei
        for(int i = 0; i < NUM_KEYS; i++) {
            prevKeyState[i] = keyState[i];
        }
    }

    public static boolean isPressed(int i) {
        return keyState[i] && !prevKeyState[i];
    }//daca este apasat
}
