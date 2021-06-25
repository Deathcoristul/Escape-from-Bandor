package GameState;

import Entity.Audio.AudioPlayer;

abstract public class GameState {
    protected GameStateManager gsm;

    public GameState(GameStateManager gsm) {
        this.gsm = gsm;
    }

    public abstract void init();
    public abstract void update();
    public abstract void draw(java.awt.Graphics2D g);
    public abstract void handleInput();

    public void saveFile() { }
    public AudioPlayer getMusic()
    {
        return gsm.getMusic();
    }
}
