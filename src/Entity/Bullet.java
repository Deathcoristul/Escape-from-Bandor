package Entity;

import Exceptions.InvalidProjectileException;
import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Bullet extends MapObject{
    private boolean hit;
    private boolean remove;
    private BufferedImage[] sprites;
    private BufferedImage[] hitSprites;
    public Bullet(TileMap tm, boolean right) throws InvalidProjectileException {
        super(tm);
        facingRight = right;
        moveSpeed = 4.8;
        if(right) dx = moveSpeed;
        else dx = -moveSpeed;
        width = 30;
        height = 30;
        cwidth = 30;
        cheight = 15;
        // incarcam sprites
        try {
            BufferedImage spritesheet = ImageIO.read(
                    getClass().getResourceAsStream(
                            "/Sprites/Player/laser.png"
                    )
            );
            sprites = new BufferedImage[4];//sprite-uri de miscare a glontului
            for(int i = 0; i < sprites.length; i++) {
                sprites[i] = spritesheet.getSubimage(
                        i * width,
                        0,//randul 0
                        width,
                        height
                );
            }
            hitSprites = new BufferedImage[3];//sprite-uri de lovit a glontului
            for(int i = 0; i < hitSprites.length; i++) {
                hitSprites[i] = spritesheet.getSubimage(
                        i * width,
                        height,//randul 1
                        width,
                        height
                );
            }
            animation = new Animation();
            animation.setFrames(sprites);//mai intai se afla in starea de miscare
            animation.setDelay(70);
        }
        catch(Exception e) {
            e.printStackTrace();
            throw new InvalidProjectileException();
        }
    }
    public void setHit() {
        if(hit) return;
        hit = true;
        animation.setFrames(hitSprites);//setam animatia de lovit
        animation.setDelay(70);
        dx = 0;
    }
    public Rectangle getRectangle() {
        return new Rectangle((int)x-cwidth/2, (int)y - cheight, cwidth, cheight);
    }//pozitiile la MapObject-uri pot diferi
    public boolean shouldRemove() { return remove; }
    public void update() {
        checkTileMapCollision();
        setPosition(xtemp, ytemp);
        if(dx == 0 && !hit) {//daca nu se misca si inca nu se vede ca e lovit
            setHit();
        }
        animation.update();
        if(hit && animation.hasPlayedOnce()) {
            remove = true;//eliminam glontul
        }
    }
    public void draw(Graphics2D g) {
        setMapPosition();
        super.draw(g);
    }
}
