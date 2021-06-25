package Entity.Enemies;

import Entity.Animation;

import Entity.Bullet;
import Entity.Enemy;
import Entity.Player;

import Exceptions.InvalidEnemySpritesException;
import Exceptions.InvalidProjectileException;
import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Soldier extends Enemy {
    private Player player;
    private boolean firing;
    private int bulletDamage;
    private long shootTime;
    public ArrayList<Bullet> bullets;
    private ArrayList<BufferedImage[]> sprites;
    private final int[] numFrames = {
            1, 2, 1, 1, 1
    };
    private static final int IDLE = 0;
    private static final int WALKING = 1;
    private static final int JUMPING = 2;
    private static final int FALLING = 3;
    private static final int FIRE = 4;

    public Soldier(TileMap tm, Player p) throws InvalidEnemySpritesException {
        super(tm);
        player = p;
        width = 39;
        height = 40;
        cwidth = 29;
        cheight = 40;

        moveSpeed = 1.5;
        maxSpeed = 1.5;
        fallSpeed = 0.2;
        maxFallSpeed = 4.0;
        jumpStart = -5.5;
        stopJumpSpeed = 0.3;

        bulletDamage = 1;
        damage = bulletDamage;
        shootTime=0;
        bullets = new ArrayList<Bullet>();
        try {

            BufferedImage spritesheet = ImageIO.read(
                    getClass().getResourceAsStream(
                            "/Sprites/Enemies/Soldier.png"
                    )
            );

            sprites = new ArrayList<BufferedImage[]>();
            for (int i = 0; i < 5; i++) {

                BufferedImage[] bi =
                        new BufferedImage[numFrames[i]];

                for (int j = 0; j < numFrames[i]; j++) {

                    if (i == 4) {
                        bi[j] = spritesheet.getSubimage(
                                j * width + j * 10,
                                i * height + 6,
                                width,
                                height
                        );
                    } else if (i == 1) {
                        bi[j] = spritesheet.getSubimage(
                                j * width - 10 * j,
                                i * height,
                                width - 10,
                                height
                        );
                    } else {
                        bi[j] = spritesheet.getSubimage(
                                j * width,
                                i * height + i,
                                width,
                                height
                        );
                    }
                }
                sprites.add(bi);

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidEnemySpritesException();
        }

        animation = new Animation();
        currentAction = IDLE;//ca la Player,valabil si pentru Boss
        animation.setFrames(sprites.get(IDLE));
        animation.setDelay(400);
    }

    public void checkAttack(Player p) {
        // gloante
        for (int j = 0; j < bullets.size(); j++) {
            if (bullets.get(j).intersects(p)) {
                p.hit(bulletDamage);//jucatorul este lovit
                bullets.get(j).setHit();
                break;
            }
        }
    }

    public void setFiring(boolean b) {
        firing = b;
    }

    private void getNextPosition() {
        if (left) {//totul similar ca la Player,valabil si pentru Boss
            dx -= moveSpeed;
            if (dx < -maxSpeed) {
                dx = -maxSpeed;
            }
        } else if (right) {
            dx += moveSpeed;
            if (dx > maxSpeed) {
                dx = maxSpeed;
            }
        } else {
            if (dx > 0) {
                dx -= stopSpeed;
                if (dx < 0) {
                    dx = 0;
                }
            } else if (dx < 0) {
                dx += stopSpeed;
                if (dx > 0) {
                    dx = 0;
                }
            }
        }
        // cat timp ataci,n-ai cum sa te misti,doar daca esti in aer
        if ((currentAction == FIRE) && !(jumping || falling)) {
            dx = 0;
        }

        // salt
        if (jumping && !falling) {
            dy = jumpStart;
            falling = true;
        }

        // cadere
        if (falling) {

            if (dy > 0) dy += fallSpeed * 1.0;
            else dy += fallSpeed;

            if (dy > 0) jumping = false;
            if (dy < 0 && !jumping) dy += stopJumpSpeed;

            if (dy > maxFallSpeed) dy = maxFallSpeed;

        }

    }

    public void update() {
        // verificam daca s-a tresarit
        if (flinching) {
            long elapsed =
                    (System.nanoTime() - flinchTimer) / 1000000;
            if (elapsed > 1000) {
                flinching = false;//si-a revenit
            }
        }
        // actualizam pozitia
        getNextPosition();//urmatoarea miscare
        checkTileMapCollision();//verificam coliziunea
        setPosition(xtemp, ytemp);//urmatoarea posibila pozitie

        if (player.getx() < x) facingRight = false;
        else facingRight = true;

        if (currentAction == FIRE) {
            if (animation.hasPlayedOnce()) {
                firing = false;
                shootTime = System.nanoTime();//daca animatia de shoot a rulat o data, revine la Idle,incepe timer-ul si asteapta sa mai impuste  inca o data
            }
        }
        if (dx == 0) {//daca nu se misca deloc dreapta sau stanga,isi va schimba directia(daca este vreo dala in fata lui)
            if (left) {
                right = true;
                left = false;
                facingRight = true;
            } else if (right) {
                right = false;
                left = true;
                facingRight = false;
            }
        }
        if (Math.abs(player.getx() - x) >= 510) {
            dx = 0;
            firing = false;         //va sta pana vine jucatorul
            left = false;
            right = false;
        } else if (Math.abs(player.getx() - x) < 210) {
            dx = 0;
            left = false;           //va ataca daca nu e mai sus sau  mai jos
            right = false;
            if (Math.abs(player.gety() - gety()) < 25 && (System.nanoTime()-shootTime) / 1000000 >500)
                firing = true;//va impusca dupa un anumit timp
        } else if (Math.abs(player.getx() - x) < 510 && Math.abs(player.getx() - x) >= 210) { //[210,510)
            firing = false;         //se va apropia de jucator daca nu e mai sus sau mai jos
            if (Math.abs(player.gety() - y) <= 10) {
                if (player.getx()<x)
                    left = true;//se va duce spre jucator(vaneaza)
                else
                    right = true;
            }
            if(player.gety()+50<y && player.gety()+80>y)
                jumping=true;
            else//daca jucatorul e in aer,va incerca sa sara si el
                jumping=false;
        }
        try {
            if (firing && currentAction != FIRE) {//daca porneste focul
                Bullet bl = new Bullet(tileMap, facingRight);
                if (getSounds().get("fire") != null && getsoundOn())
                    getSounds().get("fire").play();
                bl.setPosition(x, y);
                bullets.add(bl);//adaugam glontul nou
            }
            for (int i = 0; i < bullets.size(); i++) {
                bullets.get(i).update();
                if (bullets.get(i).shouldRemove()) {
                    bullets.remove(i);//eliminam din vector
                    i--;
                }
            }
        } catch (InvalidProjectileException e) {
            System.out.println(e);
        }
        // setam animatia
        if (firing) {
            if (currentAction != FIRE) {
                currentAction = FIRE;
                animation.setFrames(sprites.get(FIRE));
                animation.setDelay(100);
                width = 39;
            }
        } else if (dy > 0) {
            if (currentAction != FALLING) {
                currentAction = FALLING;
                animation.setFrames(sprites.get(FALLING));
                animation.setDelay(-1);
                width = 39;
            }
        } else if (dy < 0) {
            if (currentAction != JUMPING) {
                currentAction = JUMPING;
                animation.setFrames(sprites.get(JUMPING));
                animation.setDelay(-1);
                width = 39;
            }
        } else if (right || left) {
            if (currentAction != WALKING) {
                currentAction = WALKING;
                animation.setFrames(sprites.get(WALKING));
                animation.setDelay(100);
                width = 29;

            }
        } else {
            if (currentAction != IDLE) {
                currentAction = IDLE;
                animation.setFrames(sprites.get(IDLE));
                animation.setDelay(400);
                width = 39;
            }
        }

        animation.update();

        // setam directia/orientarea
        if (currentAction != FIRE) {
            if (right) facingRight = true;
            if (left) facingRight = false;
        }
    }
    public void draw (Graphics2D g){
        setMapPosition();
        // desenam gloante
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(g);
        }
        if (flinching) {
            long elapsed =
                    (System.nanoTime() - flinchTimer) / 1000000;
            if(elapsed/100 %2==0) return;
        }
        super.draw(g);
    }
}
