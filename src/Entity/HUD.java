package Entity;

import Exceptions.InvalidHUDException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class HUD {
    private Player player;

    private BufferedImage hud;
    private BufferedImage heart;
    private BufferedImage bar;
    private Font font;
    public HUD(Player p) throws InvalidHUDException {
        player = p;
        try {
            hud = ImageIO.read(
                    getClass().getResourceAsStream(
                            "/HUD/hud.png"
                    )
            );
            heart = hud.getSubimage(0, 0, 37, 37);
            bar = hud.getSubimage(0, 37, 74, 37);
            font = new Font("Arial Black", Font.PLAIN, 18);
        }
        catch(Exception e) {
            e.printStackTrace();
            throw new InvalidHUDException();
        }
    }
    public void draw(Graphics2D g) {
        int i;
        for(i = 0; i < player.getHealth(); i++) {
            g.drawImage(heart, 10 + i * 37, 10, null);
        }
        g.drawImage(bar,0,47,null);
        g.setColor(Color.DARK_GRAY);
        g.setFont(font);
        g.drawString(
                player.getFire() / 100 + "/" + player.getMaxFire() / 100,//capacitatea actuala gloante/capacitate maxima gloante
                19,
                79
        );
        g.setColor(Color.YELLOW);
        g.setFont(font);
        g.drawString(
                String.format("%d", player.getScore()),//scorul
                900,
                20
        );
    }
}
