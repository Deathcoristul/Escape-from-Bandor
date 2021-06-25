package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import GameState.GameStateManager;
import Handler.Keys;

public class GamePanel  extends JPanel implements Runnable, KeyListener {
    // dimensiuni
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 425;
    public static final int SCALE = 2;

    // game thread
    private Thread thread;
    private boolean running;
    private int FPS = 60;
    private long targetTime = 1000 / FPS;

    // imagini
    private BufferedImage image;
    private Graphics2D g;

    // game state manager
    private GameStateManager gsm;

    public GamePanel() {
        super();//constructor JFrame
        setPreferredSize(
                new Dimension(WIDTH * SCALE, HEIGHT * SCALE));//setam dimensiunile
        setFocusable(true);//pentru a receptiona evenimentele
        requestFocus();//cerere pt focus
    }
    public void addNotify() {
        super.addNotify();//util pentru deschiderea aplicatiei dupa putin timp de white-screen
        if(thread == null) {
            thread = new Thread(this);//initializam thread-ul
            addKeyListener(this);//fereastra va primi dreptul de acces la butoane
            thread.start();//si-l pornim
        }
    }
    private void init()
    {
        image = new BufferedImage(
                WIDTH, HEIGHT,
                BufferedImage.TYPE_INT_RGB
        );//imaginea alba
        g = (Graphics2D) image.getGraphics();//g preia graficile imaginei
        running = true;//rulare
        gsm = new GameStateManager();//setam un nou manager(Pentru Modelul State)
    }
    private void update()
    {
        gsm.update();
        Keys.update();
    }
    private void draw()
    {
        gsm.draw(g);
    }
    private void drawToScreen() {
        Graphics g2 = getGraphics();//apeleaza metoda din JPanel
        g2.drawImage(image, 0, 0,
                WIDTH * SCALE, HEIGHT * SCALE,
                null);//deseneaza pe ecran din draw()
        g2.dispose();//elibereaza obiectele graficii
    }

    public void run()
    {
        init();

        long start;//inceput
        long elapsed;//scurs
        long wait;//asteptare

        // game loop
        while(running) {

            start = System.nanoTime();

            update();
            draw();
            drawToScreen();

            elapsed = System.nanoTime() - start;

            wait = targetTime - elapsed / 1000000;//timpul de rulare a unui thread sa nu depaseasca targetTime
            if(wait < 0) wait = 5;
            try {
                Thread.sleep(wait);
            }
            catch(Exception e) {
                e.printStackTrace();//afiseaza pe terminal erorile
            }
        }
    }
    public void keyTyped(KeyEvent key) {}
    public void keyPressed(KeyEvent key) {
        Keys.keySet(key.getKeyCode(), true);
    }
    public void keyReleased(KeyEvent key) {
        Keys.keySet(key.getKeyCode(), false);
    }
}
