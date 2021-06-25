package GameState;

import Entity.Audio.AudioPlayer;
import Exceptions.InvalidAudioException;

import java.util.ArrayList;
import java.util.HashMap;

public class GameStateManager {
    private ArrayList<GameState> gameStates;
    private int currentState;

    private PauseState pauseState;
    private boolean paused;
    private boolean continuing;//util pentru salvarea si incarcarea in baza de date
    private int scoreReminder;
    private boolean soundOn;
    private boolean musicOn;
    private AudioPlayer Music;//muzica meniului
    private HashMap<String, AudioPlayer> sounds;//sunetele de navigare
    public static final int MENUSTATE = 0;
    public static final int NEWGAMESTATE = 1;
    public static final int OPTIONSSTATE = 2;
    public static final int LEVEL1STATE = 3;
    public static final int LEVEL2STATE = 4;
    public static final int LEVEL3STATE = 5;
    public GameStateManager() {
        scoreReminder=0;//variabila cu care se va lucra intre nivele pentru retinerea scorului
        gameStates = new ArrayList<GameState>();
        pauseState = new PauseState(this);
        paused = false;
        continuing = false;
        soundOn=true;
        musicOn=true;
        currentState = MENUSTATE;
        gameStates.add(new MenuState(this));
        gameStates.add(new Menu1State(this));
        gameStates.add(new OptionsState(this));
        gameStates.add(new Level1State(this));
        gameStates.add(new Level2State(this));
        gameStates.add(new Level3State(this));
        sounds=new HashMap<String, AudioPlayer>();
        try {
            sounds.put("select",new AudioPlayer("/Sounds/select.wav"));
        } catch (InvalidAudioException e) {
            e.printStackTrace();
        }
        try {
            sounds.put("selected",new AudioPlayer("/Sounds/selected.wav"));
        } catch (InvalidAudioException e) {
            e.printStackTrace();
        }
    }

    public void setState(int state) {
        currentState = state;
        gameStates.get(currentState).init();
    }
    public int getState(){return currentState;}
    public void update() {
        if(paused) {
            pauseState.update();
            return; //daca nu inseram acest return,nu ingheata starea(nu ne referim la ecranul starii,ci de fapt ca update-ul starii curente continua dar fara sa se vada)
        }//cat timp e in pauza,retine starea curenta a jocului
        if(gameStates.get(currentState) !=null) gameStates.get(currentState).update();
    }
    public HashMap<String, AudioPlayer> getSounds(){return sounds;}
    public void setMusicOn(boolean b) { musicOn = b; }
    public boolean getMusicOn() {return musicOn;}
    public void setSoundOn(boolean b) { soundOn= b; }
    public boolean getSoundOn() {return soundOn;}
    public void setPaused(boolean b) { paused = b; }
    public boolean getPaused() {return paused;}
    public void setContinuing(boolean b) {continuing=b;}
    public boolean getContinuing() {return continuing;}
    public void setMusic(String s){
        try {
            Music = new AudioPlayer(s);
        }
        catch (InvalidAudioException e)
        {
            System.out.println(e);
        }
    }
    public AudioPlayer getMusic(){ return Music;}
    public void draw(java.awt.Graphics2D g) {
        if(paused) {
            pauseState.draw(g);
            return;
        }
        if(gameStates.get(currentState) !=null) gameStates.get(currentState).draw(g);
    }
    public int getScore(){ return scoreReminder;}
    public void setScore(int sc){ scoreReminder=sc;}
    public ArrayList<GameState> getStates(){return gameStates;}

}
