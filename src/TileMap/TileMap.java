package TileMap;

import Exceptions.InvalidMatrixException;
import Exceptions.InvalidTilesException;
import Main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileMap {
    // pozitia
    private double x;
    private double y;

    // limite
    private int xmin;
    private int ymin;
    private int xmax;
    private int ymax;

    private double tween;

    // matricea pt harta
    private int[][] map;
    private int tileSize;
    private int numRows;
    private int numCols;
    private int width;
    private int height;

    // tileset
    private BufferedImage tileset;
    private int numTilesAcross;
    private Tile[][] tiles;

    // offset-uri
    private int rowOffset;
    private int colOffset;
    private int numRowsToDraw;
    private int numColsToDraw;

    public TileMap(int tileSize) {
        this.tileSize = tileSize;//marimea tile-ului
        numRowsToDraw = GamePanel.HEIGHT / tileSize + 2;//randuri de desenat pe ecran
        numColsToDraw = GamePanel.WIDTH / tileSize + 2;//coloane de desenat pe ecran
        tween = 0.07;
    }

    public void loadTiles(String s) throws InvalidTilesException {

        try {
            tileset = ImageIO.read(
                    getClass().getResourceAsStream(s)
            );
            numTilesAcross = tileset.getWidth() / tileSize;//cate dale sunt pe un rand
            tiles = new Tile[2][numTilesAcross];//alocam matricea pt dale

            BufferedImage subimage;
            for(int col = 0; col < numTilesAcross; col++) {
                subimage = tileset.getSubimage(
                        col * tileSize,
                        0,//randul 0
                        tileSize,
                        tileSize
                );
                tiles[0][col] = new Tile(subimage, Tile.NORMAL);//dala care se poate trece prin ea
                subimage = tileset.getSubimage(
                        col * tileSize,
                        tileSize,//randul 1
                        tileSize,
                        tileSize
                );
                tiles[1][col] = new Tile(subimage, Tile.BLOCKED);//dala care nu se poate trece prin ea
            }

        }
        catch(Exception e) {
            e.printStackTrace();
            throw new InvalidTilesException();
        }

    }

    public void setTween(double d) { tween = d; }//echilibrul la centrul mapei si jucator

    public void loadMap(String s) throws InvalidMatrixException {
        try{
            InputStream in = getClass().getResourceAsStream(s);//ia fisierul din Resources
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(in)
            );//reader pt fisier

            numCols = Integer.parseInt(br.readLine());//citeste numarul de coloane
            numRows = Integer.parseInt(br.readLine());//citeste numarul de randuri
            map = new int[numRows][numCols];//alocam o matrice care o vom citi din fisier
            width = numCols * tileSize;//latimea totala a hartii
            height = numRows * tileSize;//inaltimea totala a hartii

            xmin = GamePanel.WIDTH - width;//pozitia x minima a hartii
            xmax = 0; //pozitia x maxima a hartii
            ymin = GamePanel.HEIGHT - height;//pozitia y minima a hartii
            ymax = 0;//pozitia y maxima a hartii

            String delims = "\\s+";
            for(int row = 0; row < numRows; row++) {
                String line = br.readLine();//pt fiecare rand,citim un sir pana la caracterul terminal
                String[] tokens = line.split(delims);//vector ce contine numerele din sir impartite in spatii albe sau mai multe
                for(int col = 0; col < numCols; col++) {//pt fiecare coloana
                    map[row][col] = Integer.parseInt(tokens[col]);//convertim un numar care a fost original un cuvant,si va fi element al matricii alocate
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            throw new InvalidMatrixException();
        }

    }

    public int getTileSize() { return tileSize; }//marimea dalei
    public double getx() { return x; }//pozitia mapei
    public double gety() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public int getType(int row, int col) {
        int rc = map[row][col];
        int r = rc / numTilesAcross;//randul 0 sau 1
        int c = rc % numTilesAcross;
        return tiles[r][c].getType();//intoarce tipul unei dale
    }

    public void setPosition(double x, double y) {
        this.x += (x - this.x) * tween;
        this.y += (y - this.y) * tween;

        fixBounds();

        colOffset = (int)-this.x / tileSize;//offset(pozitiv)(in afara ecranului)
        rowOffset = (int)-this.y / tileSize;

    }

    private void fixBounds() {
        if(x < xmin) x = xmin;
        if(y < ymin) y = ymin;
        if(x > xmax) x = xmax;
        if(y > ymax) y = ymax;//necesar ca sa nu iasa din limitele precizate in clasa
    }

    public void draw(Graphics2D g) {
        for(int row = rowOffset; row < rowOffset + numRowsToDraw; row++) {
            if(row >= numRows) break;//sa nu se deseneze in plus ceva nealocat
            for(int col = colOffset; col < colOffset + numColsToDraw; col++) {
                if(col >= numCols) break;//sa nu se deseneze in plus ceva nealocat

                if(map[row][col] == 0) continue;//valorile 0 din matrice nu se deseneaza

                int rc = map[row][col];
                int r = rc / numTilesAcross;
                int c = rc % numTilesAcross;

                g.drawImage(
                        tiles[r][c].getImage(),
                        (int)x + col * tileSize,
                        (int)y + row * tileSize,
                        null
                );

            }

        }

    }
}
