package Entity;

import Entity.Audio.AudioPlayer;
import Exceptions.InvalidAudioException;
import Exceptions.InvalidPickupHeart;
import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Heart extends MapObject{
    private Player player;
    private BufferedImage origin;
    private BufferedImage heart;
    private boolean taken;
    private AudioPlayer took;
    public Heart(TileMap tm,Player p) throws InvalidPickupHeart {
        super(tm);
        width=37;
        height=37;
        cwidth=37;
        cheight=37;
        taken=false;
        try {
            origin = ImageIO.read(
                    getClass().getResourceAsStream(
                            "/HUD/hud.png"
                    )
            );
            heart = origin.getSubimage(0, 0, 37, 37);
        }
        catch(Exception e) {
            e.printStackTrace();
            throw new InvalidPickupHeart();
        }
        try{
            took=new AudioPlayer("/Sounds/pickup.wav");
        }
        catch(InvalidAudioException e)
        {
            e.printStackTrace();
        }
        player=p;
    }
    public void checkIntersected()
    {
        if (player.intersects(this))//daca jucatorul se intersecteaza cu inima
        {
            int h=player.getHealth()+1;
            if(h-1!=player.getMaxHealth()) {//daca viata lui e plina,n-are sens s-o mai ia
                player.setHealth(h);
                if(player.getsoundOn() && took!=null)
                    took.play();
                taken = true;
            }
        }
    }
    public boolean isTaken()
    {
        return taken;
    }
    public void draw(Graphics2D g) {
        setMapPosition();
        g.drawImage(heart, (int)(x + xmap - width / 2), (int)(y + ymap - height / 2), null);
    }
}
