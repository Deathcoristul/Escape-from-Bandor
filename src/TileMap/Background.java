package TileMap;

import Exceptions.InvalidBackgroundException;
import Main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Background {
    private BufferedImage image;

    private double x;
    private double y;

    private double moveScale;//viteza de miscare

    public Background(String s, double ms) throws InvalidBackgroundException{

        try {
            image = ImageIO.read(
                    getClass().getResourceAsStream(s)//cauta in folderul Resources calea spre fisier/imagine si il/o citeste
            );
            moveScale = ms;
        }
        catch(Exception e) {
            e.printStackTrace();
            throw new InvalidBackgroundException();
        }

    }
    public void setPosition(double x, double y) {
        this.x = (x * moveScale) % GamePanel.WIDTH;//setam pozitia astfel incat sa dubleze imaginea in caz ca prima se duce cu minim o celula
        this.y = (y * moveScale) % GamePanel.HEIGHT;//daca x*moveScale>WIDTH ,se ia de la 0
    }
    public void draw(Graphics2D g) {

        g.drawImage(image, (int)x, (int)y, null);//g deseneaza imaginea

    }
}
