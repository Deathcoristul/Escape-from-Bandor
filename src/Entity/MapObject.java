package Entity;

import Main.GamePanel;
import TileMap.TileMap;
import TileMap.Tile;

import java.awt.*;

public abstract class MapObject {
    // atributele tile-ului
    protected TileMap tileMap;
    protected int tileSize;
    protected double xmap;
    protected double ymap;

    // pozitia si vector
    protected double x;
    protected double y;
    protected double dx;
    protected double dy;

    // dimensiuni
    protected int width;
    protected int height;

    // pt cutia de coliziuni
    protected int cwidth;
    protected int cheight;

    // coliziune
    protected int currRow;
    protected int currCol;
    protected double xdest;
    protected double ydest;
    protected double xtemp;
    protected double ytemp;
    protected boolean topLeft;
    protected boolean topRight;
    protected boolean bottomLeft;
    protected boolean bottomRight;

    // animatii
    protected Animation animation;
    protected int currentAction;
    protected boolean facingRight;

    // miscare
    protected boolean left;
    protected boolean right;
    protected boolean up;
    protected boolean down;
    protected boolean jumping;
    protected boolean falling;

    // atributele miscarii
    protected double moveSpeed;
    protected double maxSpeed;
    protected double stopSpeed;
    protected double fallSpeed;
    protected double maxFallSpeed;
    protected double jumpStart;
    protected double stopJumpSpeed;

    public MapObject(TileMap tm) {
        tileMap = tm;
        tileSize = tm.getTileSize();//marimea dalei
    }
    //dreptunghi pt coliziune
    public Rectangle getRectangle() {
        return new Rectangle((int)x-cwidth/2-1, (int)y - cheight/2-2, cwidth, cheight);
    }
    //daca obiectele coincid in mapa
    public boolean intersects(MapObject o) {
        Rectangle r1 = getRectangle();
        Rectangle r2 = o.getRectangle();
        return r1.intersects(r2);
    }

    public void calculateCorners(double x, double y) {//calculam cornere
        int leftTile = (int)(x - cwidth / 2) / tileSize;//tile-ul stang
        int rightTile = (int)(x + cwidth / 2 - 1) / tileSize;//tile-ul drept
        int topTile = (int)(y - cheight / 2) / tileSize;//tile-ul sus
        int bottomTile = (int)(y + cheight / 2 - 1) / tileSize;//tile-ul jos

        int tl = tileMap.getType(topTile, leftTile);//tile-ul top left
        int tr = tileMap.getType(topTile, rightTile);//tile-ul top right
        int bl = tileMap.getType(bottomTile, leftTile);//tile-ul bottom left
        int br = tileMap.getType(bottomTile, rightTile);//tile-ul bottom right

        topLeft = tl == Tile.BLOCKED;//daca tl este blocat sau nu
        topRight = tr == Tile.BLOCKED;//daca tr este blocat sau nu
        bottomLeft = bl == Tile.BLOCKED;//daca bl este blocat sau nu
        bottomRight = br == Tile.BLOCKED;//daca br este blocat sau nu
    }
    public void checkTileMapCollision() {
        currCol = (int)x / tileSize;//coloana curenta
        currRow = (int)y / tileSize;//randul curent

        xdest = x + dx;//x-ul destinatiei
        ydest = y + dy;//y-ul destinatiei

        xtemp = x;//vom salva pozitia pentru verificari
        ytemp = y;//si ca sa nu modificam pozitia actuala a obiectului fara sa stim ce s-ar intampla
        calculateCorners(x, ydest);//mai intai calculam cornere pentru x-ul actual y-ul destinatiei
        if(dy < 0) {//daca se misca sus
            if(topLeft || topRight) {//daca dintre topLeft si topRight,macar una din ele sa fie blocata
                dy = 0;//nu se mai poate misca sus
                ytemp = currRow * tileSize + cheight / 2;//urmatorul y va fi undeva jos
            }
            else {
                ytemp += dy;//se poate misca sus
            }
        }
        if(dy > 0) {//daca se misca jos
            if(bottomLeft || bottomRight) {//daca dintre bottomLeft si bottomRight,macar una din ele sa fie blocata
                dy = 0;//nu se mai poate misca jos
                falling = false;//nu mai cade
                ytemp = (currRow + 1) * tileSize - cheight / 2;//urmatorul y va fi undeva sus
            }
            else {
                ytemp += dy;//se poate misca jos
            }
        }
        calculateCorners(xdest, y);//si pe urma calculam cornere pentru y-ul actual si x-ul destinatiei
        if(dx < 0) {//daca se misca stanga
            if(topLeft || bottomLeft) {//daca dintre topLeft si bottomLeft,macar una din ele sa fie blocata
                dx = 0;//nu se poate misca stanga
                xtemp = currCol * tileSize + cwidth / 2;//urmatorul x va fi o molecula la dreapta
            }
            else {
                xtemp += dx;//se poate misca stanga
            }
        }
        if(dx > 0) {//daca se misca dreapta
            if(topRight || bottomRight) {//daca dintre topRight si bottomRight,macar una din ele sa fie blocata
                dx = 0;//nu se poate misca dreapta
                xtemp = (currCol + 1) * tileSize - cwidth / 2;//urmatorul y va fi o molecula la stanga
            }
            else
            {
                xtemp+=dx;//se poate misca dreapta
            }
        }
        if(!falling) {//daca nu cade
            calculateCorners(x, ydest + 1);//verificam pt pozitia de mai jos
            if(!bottomLeft && !bottomRight) {//daca bottomLeft si bottomRight nu sunt blocate(se pot trece prin ele pana la urma)
                falling = true;//va cade daca asa
            }
        }
    }
    public int getx() { return (int)x; }
    public int gety() { return (int)y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getCWidth() { return cwidth; }
    public int getCHeight() { return cheight; }
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public void setVector(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public void setMapPosition() {
        xmap = tileMap.getx();//setam pozitia hartii cat timp jucatorul se misca
        ymap = tileMap.gety();
    }
    public void setLeft(boolean b) { left = b; }
    public void setRight(boolean b) { right = b; }
    public void setUp(boolean b) { up = b; }
    public void setDown(boolean b) { down = b; }
    public void setJumping(boolean b) { jumping = b; }
    public boolean notOnScreen() {
        return x + xmap + width < 0 ||
                x + xmap - width > GamePanel.WIDTH ||
                y + ymap + height < 0 ||
                y + ymap - height > GamePanel.HEIGHT;//daca nu este in ecran
    }
    public void draw(java.awt.Graphics2D g) {
        setMapPosition();
        if(facingRight) {
            g.drawImage(
                    animation.getImage(),
                    (int)(x + xmap - width / 2), //impartim latimea si inaltimea la 2 pentru a nu pleca din alt punct decat cel curent(in cazul Player sau Enemy)
                    (int)(y + ymap - height / 2),
                    null
            );
        }
        else {
            g.drawImage(
                    animation.getImage(),
                    (int)(x + xmap - width / 2 + width),//setam x-ul pt desenare inversa pentru a preveni dublarea
                    (int)(y + ymap - height / 2),
                    -width,//desenare inversa pentru a preveni oglindirea
                    height,
                    null
            );
        }

    }
}
