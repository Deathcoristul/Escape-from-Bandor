package Entity;

import Entity.Audio.AudioPlayer;
import Exceptions.InvalidAudioException;
import Exceptions.InvalidPlayerSpritesException;
import Exceptions.InvalidProjectileException;
import TileMap.TileMap;

import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Player extends MapObject {
    // atributele jucatorului
    private int health;
    private int maxHealth;
    private int fire;
    private int maxFire;
    private boolean dead;
    private boolean flinching;
    private long flinchTimer;
    private int score;
    // firegun
    private boolean firing;
    private int bulletCost;
    private int bulletDamage;
    private ArrayList<Bullet> bullets;

    // alergare
    private boolean running;

    // animatii
    private ArrayList<BufferedImage[]> sprites;
    private final int[] numFrames = {
            1, 4, 1, 1, 6, 2
    };

    // actiuni
    private static final int IDLE = 0;
    private static final int WALKING = 1;
    private static final int JUMPING = 2;
    private static final int FALLING = 3;
    private static final int RUNNING = 4;
    private static final int FIRE = 5;
    //sunete
    private HashMap<String, AudioPlayer> sounds;
    private boolean soundOn;
    public Player(TileMap tm) throws InvalidPlayerSpritesException {

        super(tm);
        x=40;
        y=100;
        score=0;
        width = 40;
        height = 51;
        cwidth = 20;
        cheight = 51;
        dead=false;
        moveSpeed = 0.3;
        maxSpeed = 1.6;
        stopSpeed = 0.4;//frana
        fallSpeed = 0.16;
        maxFallSpeed = 4.0;//viteza maxima de cadere
        jumpStart = -5.5;
        stopJumpSpeed = 0.3;//frana saritura

        facingRight = true;

        health = maxHealth = 5;
        fire = maxFire = 2500;
        soundOn=true;
        bulletCost = 200;//costul glontului
        bulletDamage = 5;
        bullets = new ArrayList<Bullet>();

        // incarcam sprites
        try {

            BufferedImage spritesheet = ImageIO.read(
                    getClass().getResourceAsStream(
                            "/Sprites/Player/player_sprites.png"
                    )
            );

            sprites = new ArrayList<BufferedImage[]>();
            for(int i = 0; i < 6; i++) {

                BufferedImage[] bi =
                        new BufferedImage[numFrames[i]];

                for(int j = 0; j < numFrames[i]; j++) {


                    if(i==5) {
                        bi[j] = spritesheet.getSubimage(
                                j * width + 5*j,
                                i * height - 4,
                                width-1,
                                height
                        );
                    }
                    else if(i==4)
                    {
                        bi[j] = spritesheet.getSubimage(
                                j * width+5*j,
                                i * height - 3,
                                width,
                                height-1
                        );
                    }
                    else if(i==1)
                    {
                        bi[j] = spritesheet.getSubimage(
                                j * width+5*j,
                                i * height,
                                width,
                                height
                        );
                    }
                    else{
                        bi[j] = spritesheet.getSubimage(
                                j * width,
                                i * height,
                                width,
                                height
                        );
                    }
                }

                sprites.add(bi);

            }

        }
        catch(Exception e) {
            e.printStackTrace();
            throw new InvalidPlayerSpritesException();
        }

        animation = new Animation();
        currentAction = IDLE;//actiunea curenta
        animation.setFrames(sprites.get(IDLE));//setam frame-uri din sprite-uri pentru IDLE
        animation.setDelay(400);//cat dureaza un frame

        sounds = new HashMap<String, AudioPlayer>();
        try {
            sounds.put("run",new AudioPlayer("/Sounds/running.wav"));
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
            sounds.put("hurt",new AudioPlayer("/Sounds/hurt.wav"));
        } catch (InvalidAudioException e) {
            e.printStackTrace();
        }
    }

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getFire() { return fire; }
    public int getMaxFire() { return maxFire; }
    public boolean getsoundOn(){return soundOn;}
    public void setSoundOn(boolean b){soundOn=b;}
    public void setFiring(boolean b) {
        firing = b;
    }
    public void setRunning(boolean b) {
        running = b;
    }
    public void checkAttack(ArrayList<Enemy> enemies) {
        for(int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            // gloante
            for(int j = 0; j < bullets.size(); j++) {
                if(bullets.get(j).intersects(e) && !e.flinching) {//daca glontul intersecteaza inamicul si nu e in stare de revenire
                    e.hit(bulletDamage);
                    bullets.get(j).setHit();
                    score +=100;
                    break;
                }
            }
            // verificam coliziunea cu inamicul
            if(intersects(e)) {
                hit(e.getDamage());//pierde din viata daca este atins de inamic
            }
        }
    }
    public void hit(int damage) {
        if(flinching) return;
        if(sounds.get("hurt")!=null && soundOn)
            sounds.get("hurt").play();
        health -= damage;//jucatorul este lovit
        if(health < 0) health = 0;
        if(health == 0) dead = true;
        flinching = true;//atata timp cat nu e mort, jucatorul e ranit
        flinchTimer = System.nanoTime();//incepe timpul de revenire
        if(facingRight) dx=-2;//vom misca putin jucatorul cat timp e lovit
        else dx=2;
        dy=-3;
    }
    private void getNextPosition() {
        // miscare
        if(left) {
            if(running)
            {
                if(soundOn && sounds.get("run")!=null && !sounds.get("run").isRunning() && !falling)
                    sounds.get("run").play();
                dx -= 2*moveSpeed;
                if(dx < -2*maxSpeed) {
                    dx = -2*maxSpeed;
                }
            }
            else
            {
                dx -= moveSpeed;
                if(dx < -maxSpeed) {
                    dx = -maxSpeed;
                }
            }
        }
        else if(right) {
            if(running)
            {
                if(soundOn && sounds.get("run")!=null && !sounds.get("run").isRunning() && !falling)
                    sounds.get("run").play();
                dx += 2*moveSpeed;
                if(dx > 2*maxSpeed) {
                    dx = 2*maxSpeed;
                }
            }
            else
            {
                dx += moveSpeed;
                if(dx > maxSpeed) {
                    dx = maxSpeed;
                }
            }

        }
        else {
            if(dx > 0) {
                dx -= stopSpeed;//frana la mers
                if(dx < 0) {
                    dx = 0;
                }
            }
            else if(dx < 0) {
                dx += stopSpeed;
                if(dx > 0) {
                    dx = 0;
                }
            }
        }
        // cat timp ataci,n-ai cum sa te misti,doar daca esti in aer
        if((currentAction == FIRE) && !(jumping || falling) )
        {
            dx = 0;
        }

        // salt
        if(jumping && !falling) {
            if(soundOn && sounds.get("jump")!=null)
                sounds.get("jump").play();
            dy = jumpStart;
            falling = true;
        }

        // cadere
        if(falling) {

            if(dy > 0) dy += fallSpeed * 1.0;
            else dy += fallSpeed;

            if(dy > 0) jumping = false;
            if(dy < 0 && !jumping) dy += stopJumpSpeed;//cat se misca sus si opreste saritura, frana la saritura

            if(dy > maxFallSpeed) dy = maxFallSpeed;//sa nu depasim viteza maxima de cadere

        }

    }
    public void update() {

        // actualizam pozitia
        getNextPosition();//urmatoarea miscare
        checkTileMapCollision();//verificam coliziunea
        setPosition(xtemp, ytemp);//urmatoarea posibila pozitie

        if(currentAction == FIRE) {
            if(animation.hasPlayedOnce()) firing = false;
        }

        fire += 1;//arma se reincarca atata timp cand n-o mai folosim
        if(fire > maxFire) fire = maxFire;//sa nu depasim capacitatea maxima
        try {
            if (firing && currentAction != FIRE) {
                if (fire > bulletCost) {
                    fire -= bulletCost;//capacitatea actuala scade cu costul
                    Bullet bl = new Bullet(tileMap, facingRight);//un nou glont dreapta sau stanga
                    if(soundOn && sounds.get("fire")!=null)
                        sounds.get("fire").play();
                    bl.setPosition(x, y);//in aceeasi pozitie cu jucatorul
                    bullets.add(bl);//adaugam in vectorul cu gloantele ce inca nu sunt eliminate
                }
            }
            for (int i = 0; i < bullets.size(); i++) {
                bullets.get(i).update();
                if (bullets.get(i).shouldRemove()) {
                    bullets.remove(i);//scoatem din vector daca glontul e folosit
                    i--;
                }
            }
        }
        catch (InvalidProjectileException e)
        {
            System.out.println(e);
        }
        // verificam daca s-a tresarit
        if(flinching) {
            long elapsed =
                    (System.nanoTime() - flinchTimer) / 1000000;
            if(elapsed > 1000) {
                flinching = false;//revine la normal
            }
        }

        // setam animatia
        if(firing) {
            if(currentAction != FIRE) {//daca starea e adevarata,dar actiunea curenta nu e cel precizat
                currentAction = FIRE;
                animation.setFrames(sprites.get(FIRE));
                animation.setDelay(75);//cat timp sa dureze un frame
                width = 39;//unele actiuni au diferite latimi si de aceea trebuie setat in felul asta
            }
        }
        else if(dy > 0) {
            if(currentAction != FALLING) {
                currentAction = FALLING;
                animation.setFrames(sprites.get(FALLING));
                animation.setDelay(-1);
                width = 40;
            }
        }
        else if(dy < 0) {
            if(currentAction != JUMPING) {
                currentAction = JUMPING;
                animation.setFrames(sprites.get(JUMPING));
                animation.setDelay(-1);
                width = 40;
            }
        }
        else if(right || left) {
            if(running) {
                if (currentAction != RUNNING) {
                    currentAction = RUNNING;
                    animation.setFrames(sprites.get(RUNNING));
                    animation.setDelay(80);
                    width = 40;
                }
            }
            else {
                if (currentAction != WALKING) {
                    currentAction = WALKING;
                    animation.setFrames(sprites.get(WALKING));
                    animation.setDelay(100);
                    width = 40;
                }
            }
        }
        else {
            if(currentAction != IDLE) {
                currentAction = IDLE;
                animation.setFrames(sprites.get(IDLE));
                animation.setDelay(400);
                width = 40;
            }
        }

        animation.update();//actualizam animatia

        // setam directia/orientarea
        if(currentAction != FIRE) {
            if(right) facingRight = true;
            if(left) facingRight = false;
        }

    }

    public void draw(Graphics2D g) {
        setMapPosition();//setam pozitia pe mapa
        // desenam gloante
        for(int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(g);
        }

        // desenam jucator
        if(flinching) {
            long elapsed =
                    (System.nanoTime() - flinchTimer) / 1000000;
            if(elapsed / 100 % 2 == 0) {
                return; //dispare imaginea jucatorului intr-o unitate para de timp daca el isi revine
                //acest lucru va fi si la clasele ce extind Enemy
            }
        }
        super.draw(g);
    }

    public void setHealth(int h) {
        health=h;
    }
    public void setScore(int sc)
    {
        score=sc;
    }
    public int getScore()
    {
        return score;
    }
}
