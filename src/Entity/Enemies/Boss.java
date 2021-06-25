package Entity.Enemies;

import Entity.*;
import Exceptions.InvalidEnemySpritesException;
import Exceptions.InvalidProjectileException;
import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Boss extends Enemy {
    private Player player;
    private boolean firing;
    private int purpleBallDamage;
    public ArrayList<PurpleBall> purpleBalls;
    private ArrayList<BufferedImage[]> sprites;
    private final int[] numFrames = {
            1, 6, 1, 1, 11
    };
    private static final int IDLE = 0;
    private static final int WALKING = 1;
    private static final int JUMPING = 2;
    private static final int FALLING = 3;
    private static final int FIRE = 4;
    private long shootTime;
    public Boss(TileMap tm, Player p) throws InvalidEnemySpritesException {
        super(tm);
        player=p;
        width = 60;
        height = 61;
        cwidth = 60;
        cheight = 60;
        //E acelasi lucru cu Soldier in mare parte dar vraja si atributele difera
        moveSpeed = 1.2;
        maxSpeed = 1.2;
        fallSpeed = 0.2;
        maxFallSpeed = 4.0;
        jumpStart = -10.5;
        stopJumpSpeed = 0.3;
        health=25;
        purpleBallDamage = 1;
        damage=purpleBallDamage;
        purpleBalls = new ArrayList<PurpleBall>();
        try {

            BufferedImage spritesheet = ImageIO.read(
                    getClass().getResourceAsStream(
                            "/Sprites/Enemies/Boss.png"
                    )
            );

            sprites = new ArrayList<BufferedImage[]>();
            for(int i = 0; i < 5; i++) {

                BufferedImage[] bi =
                        new BufferedImage[numFrames[i]];

                for(int j = 0; j < numFrames[i]; j++) {

                    if(i==4) {
                        bi[j] = spritesheet.getSubimage(
                                j * (width+31),
                                i * height,
                                width+31,
                                height
                        );
                    }
                    else if(i==1)
                    {
                        bi[j] = spritesheet.getSubimage(
                                j * (width+8)+1,
                                i * height+2,
                                width+8,
                                height-1
                        );
                    }
                    else{
                        bi[j] = spritesheet.getSubimage(
                                j * width,
                                i * height+1,
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
            throw new InvalidEnemySpritesException();
        }
        animation = new Animation();
        currentAction = IDLE;
        animation.setFrames(sprites.get(IDLE));
        animation.setDelay(400);
    }
    public void checkAttack(Player p) {
        // vraja boss-ului
        for(int j = 0; j < purpleBalls.size(); j++) {
            if(purpleBalls.get(j).intersects(p)) {
                p.hit(purpleBallDamage);
                purpleBalls.get(j).setHit();
                break;
            }
        }
    }
    public void hit(int damage) {
        if(dead || flinching) return;
        if(getSounds().get("hurt")!=null && getsoundOn())
            getSounds().get("hurt").play();
        health -= damage;
        if(health < 0) health = 0;
        if(health == 0) {
            if(getSounds().get("dissapear")!=null && getsoundOn())
                getSounds().get("dissapear").play();
            dead = true;
        }
        flinching = true;
        flinchTimer = System.nanoTime();
        if(facingRight) dx=-2;
        else dx=2;
        dy=-3;
    }
    public void setFiring(boolean b) {
        firing = b;
    }
    private void getNextPosition() {
        if(left) {
            dx -= moveSpeed;
            if(dx < -maxSpeed) {
                dx = -maxSpeed;
            }
        }
        else if(right) {
            dx += moveSpeed;
            if(dx > maxSpeed) {
                dx = maxSpeed;
            }
        }
        else {
            if(dx > 0) {
                dx -= stopSpeed;
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
            dy = jumpStart;
            falling = true;
        }

        // cadere
        if(falling) {
            if(dy > 0) dy += fallSpeed * 1.0;
            else dy += fallSpeed;
            if(dy > 0) jumping = false;
            if(dy < 0 && !jumping) dy += stopJumpSpeed;
            if(dy > maxFallSpeed) dy = maxFallSpeed;
        }
    }
    public void update() {
        // verificam daca s-a tresarit
        if(flinching) {
            long elapsed =
                    (System.nanoTime() - flinchTimer) / 1000000;
            if(elapsed > 1000) {
                flinching = false;
            }
        }
        // actualizam pozitia
        getNextPosition();
        checkTileMapCollision();
        setPosition(xtemp, ytemp);
        if(player.getx() < x) facingRight = false;
        else facingRight = true;

        if(currentAction == FIRE) {
            if(animation.hasPlayedOnce()){
                firing = false;
                shootTime=System.nanoTime();
            }
        }
        if(dx==0) {
            if (right) {
                right = false;
                left = true;
                facingRight = false;
            } else if (left) {
                right = true;
                left = false;
                facingRight = true;
            }
        }
        if (Math.abs(player.getx() - x) >= 510) {
            dx = 0;
            firing = false;
            left = false;
            right = false;
        } else if (Math.abs(player.getx() - x) < 210) {
            dx = 0;
            left = false;
            right = false;
            if (Math.abs(player.gety() - gety()) < 25) {
                if((System.nanoTime()-shootTime) / 1000000 >1000)
                    firing = true;
                else
                {   //La Soldier, dupa ce impusca,devine IDLE
                    //Dar la Boss, dupa ce arunca vraja, se indreapta spre protagonist
                    if (player.getx() < x)
                        left = true;
                    else if (player.getx() >= x)
                        right = true;
                }
            }
        } else if (Math.abs(player.getx() - x) < 510 && Math.abs(player.getx() - x) >= 210) {
            firing = false;
            if (Math.abs(player.gety() - y) <= 10) {
                if (player.getx() < x)
                    left = true;
                else if (player.getx() >= x)
                    right = true;
            }
            if(player.gety()+60<y && player.gety()+80>y)
                jumping=true;
            else
                jumping=false;
        }
        try {
            if (firing && currentAction != FIRE) {
                PurpleBall bl = new PurpleBall(tileMap, facingRight);
                bl.setPosition(x, y);
                if(getsoundOn() && getSounds().get("purpleball")!=null)
                    getSounds().get("purpleball").play();
                purpleBalls.add(bl);
            }
            for (int i = 0; i < purpleBalls.size(); i++) {
                purpleBalls.get(i).update();
                if (purpleBalls.get(i).shouldRemove()) {
                    purpleBalls.remove(i);
                    i--;
                }
            }
        }
        catch (InvalidProjectileException e)
        {
            System.out.println(e);
        }
        // setam animatia
        if(firing) {
            if(currentAction != FIRE) {
                currentAction = FIRE;
                animation.setFrames(sprites.get(FIRE));
                animation.setDelay(10);
                width = 91;
            }
        }
        else if(dy > 0) {
            if(currentAction != FALLING) {
                currentAction = FALLING;
                animation.setFrames(sprites.get(FALLING));
                animation.setDelay(100);
                width = 60;
            }
        }
        else if(dy < 0) {
            if(currentAction != JUMPING) {
                currentAction = JUMPING;
                animation.setFrames(sprites.get(JUMPING));
                animation.setDelay(-1);
                width = 60;
            }
        }
        else if(right || left) {
            if (currentAction != WALKING) {
                currentAction = WALKING;
                animation.setFrames(sprites.get(WALKING));
                animation.setDelay(100);
                width = 68;
            }
        }
        else {
            if(currentAction != IDLE) {
                currentAction = IDLE;
                animation.setFrames(sprites.get(IDLE));
                animation.setDelay(400);
                width = 60;
            }
        }

        animation.update();

        // setam directia
        if(currentAction != FIRE) {
            if(right) facingRight = true;
            if(left) facingRight = false;
        }

    }
    public void draw(Graphics2D g) {
        setMapPosition();
        // desenam mingi magice
        for(int i = 0; i < purpleBalls.size(); i++) {
            purpleBalls.get(i).draw(g);
        }
        if(flinching) {
            long elapsed =
                    (System.nanoTime() - flinchTimer) / 1000000;
            if(elapsed/100 %2==0) return;//imaginea dispare la unitati pare de timp
        }
        super.draw(g);
    }

}
