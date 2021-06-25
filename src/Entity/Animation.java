package Entity;

import java.awt.image.BufferedImage;

public class Animation {
    private BufferedImage[] frames;
    private int currentFrame;

    private long startTime;
    private long delay;

    private boolean playedOnce;

    public void Animation() {
        playedOnce = false;
    }

    public void setFrames(BufferedImage[] frames) {
        this.frames = frames;
        currentFrame = 0;
        startTime = System.nanoTime();
        playedOnce = false;
    }

    public void setDelay(long d) { delay = d; }
    public void setFrame(int i) { currentFrame = i; }

    public void update() {

        if(delay == -1) return;

        long elapsed = (System.nanoTime() - startTime) / 1000000;
        if(elapsed > delay) {//daca frame-ul a durat delay-ul dorit
            currentFrame++;//atunci putem trece la alta
            startTime = System.nanoTime();//inceputul timpului altui frame
        }
        if(currentFrame == frames.length) {
            currentFrame = 0;
            playedOnce = true;//luam de la capat sirul cu frame-urile animatiei
        }

    }

    public int getFrame() { return currentFrame; }
    public BufferedImage getImage() { return frames[currentFrame]; }
    public boolean hasPlayedOnce() { return playedOnce; }
}
