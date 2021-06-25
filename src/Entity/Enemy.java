package Entity;

import Entity.Audio.AudioPlayer;
import Exceptions.InvalidAudioException;
import TileMap.TileMap;

import java.util.HashMap;

public class Enemy extends MapObject{
    protected int health;
    protected int maxHealth;
    protected boolean dead;
    protected int damage;

    protected boolean flinching;
    protected long flinchTimer;
    private HashMap<String, AudioPlayer> sounds;
    private boolean soundOn;
    public Enemy(TileMap tm) {
        super(tm);
        sounds = new HashMap<String, AudioPlayer>();
        try {
            sounds.put("running",new AudioPlayer("/Sounds/running.wav"));
        } catch (InvalidAudioException e) {
            e.printStackTrace();
        }
        try {
            sounds.put("jump",new AudioPlayer("/Sounds/jump.wav"));
        } catch (InvalidAudioException e) {
            e.printStackTrace();
        }
        try {
            sounds.put("fire",new AudioPlayer("/Sounds/blaster.wav"));
        } catch (InvalidAudioException e) {
            e.printStackTrace();
        }
        try {
            sounds.put("dissapear",new AudioPlayer("/Sounds/dissapear.wav"));
        } catch (InvalidAudioException e) {
            e.printStackTrace();
        }
        try {
            sounds.put("hurt",new AudioPlayer("/Sounds/bosshurt.wav"));
        } catch (InvalidAudioException e) {
            e.printStackTrace();
        }
        try {
            sounds.put("purpleball",new AudioPlayer("/Sounds/purpleball.wav"));
        } catch (InvalidAudioException e) {
            e.printStackTrace();
        }
    }

    public boolean isDead() { return dead; }
    public boolean getsoundOn(){return soundOn;}
    public void setSoundOn(boolean b){soundOn=b;}
    public int getDamage() { return damage; }

    public void hit(int damage) {
        if(dead || flinching) return;//daca e ranit sau mort n-are rost continuarea functiei
        health -= damage;
        if(health < 0) health = 0;
        if(health == 0) {
            if(sounds.get("dissapear")!=null && soundOn)
                sounds.get("dissapear").play();
            dead = true;
        }
        flinching = true;
        flinchTimer = System.nanoTime();//dupa ce e atins de glont, incepe timpul de tresarire
    }

    public void update() {}

    public void checkAttack(Player player) { }
    public void setHealth(int x)
    {
        health=x;
    }
    public int getHealth()
    {
        return health;
    }
    public HashMap<String, AudioPlayer> getSounds()
    {
        return sounds;
    }
}
