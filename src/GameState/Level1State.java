package GameState;

import Entity.*;
import Entity.Audio.AudioPlayer;
import Entity.Enemies.Soldier;

import Exceptions.*;
import Handler.Keys;
import Main.GamePanel;
import TileMap.TileMap;
import Entity.Enemy;
import Entity.HUD;
import Entity.Player;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

import static GameState.GameStateManager.LEVEL1STATE;
import static GameState.GameStateManager.MENUSTATE;

public class Level1State extends GameState{
    private TileMap tileMap;
    private Player player;
    private HUD hud;
    private ArrayList<Enemy> enemies;
    private ArrayList<Heart> hearts;
    private AudioPlayer bgMusic;
    private String[] tutorial = {
            "LEFT/RIGHT=movement",
            "UP=jump",
            "SPACE=shoot",
            "LEFT/RIGHT + SHIFT=run",
            "ESC=pause"
    };//un mic tutorial la inceput

    private Font font;
    public Level1State(GameStateManager gsm) {
        super(gsm);
        font = new Font("Arial Black", Font.PLAIN, 15);
        init();
    }

    public void init() {

        tileMap = new TileMap(30);
        try {
            tileMap.loadTiles("/Tilesets/Prison.png");
            tileMap.loadMap("/Maps/map1.map");
            tileMap.setPosition(0, 0);
            tileMap.setTween(1);
        }
        catch (InvalidTilesException e)
        {
            gsm.setState(MENUSTATE);
            System.out.println(e);
        }
        catch (InvalidMatrixException e)
        {
            gsm.setState(MENUSTATE);
            System.out.println(e);
        }
        try {
            player = new Player(tileMap);
            player.setSoundOn(gsm.getSoundOn());
            try {
                hud = new HUD(player);
            }
            catch (InvalidHUDException e)
            {
                System.out.println(e);
            }
            if(!gsm.getContinuing()) {
                player.setPosition(155, 275);
                try {
                    hearts = new ArrayList<Heart>();
                    Point[] points = new Point[]{
                            new Point(4531, 450),
                            new Point(7585, 365)
                    };
                    for (int i = 0; i < points.length; i++) {
                        Heart h = new Heart(tileMap, player);
                        h.setPosition(points[i].x, points[i].y);
                        hearts.add(h);
                    }
                }
                catch(InvalidPickupHeart e)
                {
                    System.out.println(e);
                }
                populateEnemies();
            }
            else
            {
                Connection c=null;
                Statement stmt = null;
                try {
                    gsm.setContinuing(true);
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection("jdbc:sqlite:Resources\\Databases\\save.db");
                    c.setAutoCommit(false);
                    stmt = c.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM SAVE;");
                    int h = rs.getInt("HEALTH");
                    player.setScore(rs.getInt("SCORE"));
                    player.setHealth(h);
                    int sx = rs.getInt("POSITIONX");
                    int sy = rs.getInt("POSITIONY");
                    player.setPosition(sx, sy);
                    rs.close();
                    stmt.close();
                    c.close();
                }
                catch(SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    System.err.println(e.getClass().getName()+": "+e.getMessage());
                    gsm.setState(MENUSTATE);
                }
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection("jdbc:sqlite:Resources\\Databases\\enemies.db");
                    c.setAutoCommit(false);
                    stmt = c.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM ENEMIES;");
                    enemies = new ArrayList<Enemy>();
                    Soldier s;
                    while (rs.next()) {
                        try {
                            s = new Soldier(tileMap, player);
                            int ex = rs.getInt("POSITIONX");
                            int ey = rs.getInt("POSITIONY");
                            s.setPosition(ex, ey);
                            s.setSoundOn(gsm.getSoundOn());
                            enemies.add(s);
                        } catch (InvalidEnemySpritesException e) {
                            System.out.println(e);
                        }
                    }
                    rs.close();
                    stmt.close();
                    c.close();
                }
                catch(SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    System.err.println(e.getClass().getName()+": "+e.getMessage());
                }
                try{
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection("jdbc:sqlite:Resources\\Databases\\hearts.db");
                    c.setAutoCommit(false);
                    stmt= c.createStatement();
                    ResultSet rs=stmt.executeQuery("SELECT * FROM HEARTS;");
                    hearts=new ArrayList<Heart>();
                    Heart elh;
                    while(rs.next()) {
                        try {
                            elh = new Heart(tileMap, player);
                            int hx = rs.getInt("POSITIONX");
                            int hy = rs.getInt("POSITIONY");
                            elh.setPosition(hx, hy);
                            hearts.add(elh);
                        }
                        catch (InvalidPickupHeart e)
                        {
                            System.out.println(e);
                        }
                    }
                    rs.close();
                    stmt.close();
                    c.close();
                }
                catch(SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    System.err.println(e.getClass().getName()+": "+e.getMessage());
                }
            }
        }
        catch (InvalidPlayerSpritesException e) {
            gsm.setState(MENUSTATE);
            System.out.println(e);
        }
        try {
            bgMusic=new AudioPlayer("/Music/prison.wav");
            if(gsm.getState()==LEVEL1STATE) {
                if (gsm.getMusicOn()) {
                    bgMusic.play();
                }
            }
        } catch (InvalidAudioException e) {
            e.printStackTrace();
        }
    }


    public void update() {
        handleInput();
        // update jucator
        if(player!=null) {
            player.update();
            if (tileMap != null)
                tileMap.setPosition(
                        GamePanel.WIDTH / 2 - player.getx(),
                        GamePanel.HEIGHT / 2 - player.gety()
                );
            //setam imaginile pt parallax scrolling
            player.checkAttack(enemies);//verificam atacurile jucatorului asupra inamicilor
            for (int i = 0; i < enemies.size(); i++) {
                Enemy e = enemies.get(i);
                e.update();
                e.checkAttack(player);//verificam atacurile inamicului asupra jucatorului
                if (e.isDead()) {
                    enemies.remove(i);//scoatem din vector
                    i--;
                }
            }
            for (int i = 0; i < hearts.size(); i++) {
                Heart h = hearts.get(i);
                h.checkIntersected();
                if (h.isTaken()) {
                    hearts.remove(i);//daca inima e luata,scoatem din vector,valabil si pentru celelalte nivele
                    i--;
                }
            }
            if (player.getx() >= 9945) {
                gsm.setScore(player.getScore());
                gsm.setContinuing(false);//daca am incarcat o stare din baza de date, si daca trecem la nivelul urmator, vom lucra cu setari implicite
                if (bgMusic != null && bgMusic.isRunning())
                    bgMusic.stop();
                gsm.setState(GameStateManager.LEVEL2STATE);
            }
            if (player.gety() >= 500 || player.getHealth() == 0) {
                if (bgMusic != null && bgMusic.isRunning())
                    bgMusic.stop();
                init();//o luam de la capat daca acesta cade in prapastie sau moare,valabil si pentru celelalte nivele
            }
        }
    }
    private void populateEnemies() {

        enemies = new ArrayList<Enemy>();

        Soldier s;
        Point[] points = new Point[] {
                new Point(860, 200),
                new Point(1525, 100),
                new Point(1680, 100),
                new Point(1800, 100),
                new Point(2166,100),
                new Point(2943,100),
                new Point(3502,190),
                new Point(3892,190),
                new Point(4712,190),
                new Point(5695,300),
                new Point(6184,300),
                new Point(6923,100),
                new Point(7821,100),
                new Point(8463,100),
                new Point(9067,100),
                new Point(9785,400)
        };
        try {
            for (int i = 0; i < points.length; i++) {
                s = new Soldier(tileMap, player);
                s.setPosition(points[i].x, points[i].y);
                s.setSoundOn(gsm.getSoundOn());
                enemies.add(s);
            }
        }
        catch (InvalidEnemySpritesException e)
        {
            System.out.println(e);
        }

    }
    public void saveFile()
    {
        Connection c=null;
        Statement stmt = null;
        try{
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Resources\\Databases\\save.db");
            stmt= c.createStatement();
            String sql = "DROP TABLE IF EXISTS SAVE;"+
                    "CREATE TABLE IF NOT EXISTS SAVE " +
                    "(LEVEL INT PRIMARY KEY NOT NULL, " +
                    "SCORE INT NOT NULL, " +
                    "HEALTH INT  NOT NULL, " +
                    "POSITIONX INT NOT NULL, " +
                    "POSITIONY INT NOT NULL)";
            stmt.executeUpdate(sql);
            c.setAutoCommit(false);
            sql="INSERT OR REPLACE INTO SAVE (LEVEL,SCORE,HEALTH,POSITIONX,POSITIONY)"+
                    "VALUES (1, "+player.getScore()+", "+player.getHealth()+", "+player.getx()+", "+player.gety()+" );";
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            c.close();
            c = DriverManager.getConnection("jdbc:sqlite:Resources\\Databases\\enemies.db");
            stmt= c.createStatement();
            sql = "DROP TABLE IF EXISTS ENEMIES;"+
                    "CREATE TABLE IF NOT EXISTS ENEMIES " +
                    "(ID INT PRIMARY KEY NOT NULL, " +
                    "POSITIONX INT NOT NULL, " +
                    "POSITIONY INT NOT NULL)";
            stmt.executeUpdate(sql);
            c.setAutoCommit(false);
            for(int i=0;i<enemies.size();i++)
            {
                sql="INSERT OR REPLACE INTO ENEMIES (ID,POSITIONX,POSITIONY)"+
                        "VALUES ("+i+", "+enemies.get(i).getx()+", "+enemies.get(i).gety()+" );";
                stmt.executeUpdate(sql);
            }
            stmt.close();
            c.commit();
            c.close();
            c = DriverManager.getConnection("jdbc:sqlite:Resources\\Databases\\hearts.db");
            stmt= c.createStatement();
            sql = "DROP TABLE IF EXISTS HEARTS;"+
                    "CREATE TABLE IF NOT EXISTS HEARTS " +
                    "(ID INT PRIMARY KEY NOT NULL, " +
                    "POSITIONX INT NOT NULL, " +
                    "POSITIONY INT NOT NULL)";
            stmt.executeUpdate(sql);
            c.setAutoCommit(false);
            for(int i=0;i<hearts.size();i++)
            {
                sql="INSERT OR REPLACE INTO HEARTS (ID,POSITIONX,POSITIONY)"+
                        "VALUES ("+i+", "+hearts.get(i).getx()+", "+hearts.get(i).gety()+" );";
                stmt.executeUpdate(sql);
            }
            stmt.close();
            c.commit();
            c.close();
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            gsm.setState(MENUSTATE);
        }
    }
    public void draw(Graphics2D g) {

        // curatam ecranul screen
        Color col=new Color(24,31,32,255);
        g.setColor(col);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        // desenam tilemap-ul
        if(tileMap!=null)
            tileMap.draw(g);
        // desenam inamicii
        for(int i = 0; i < enemies.size(); i++) {
            enemies.get(i).draw(g);
        }
        //desenez inimile
        for(int i = 0; i < hearts.size(); i++) {
            if(hearts.get(i)!=null)
                hearts.get(i).draw(g);
        }
        // desenam jucatorul
        if(player!=null) {
            player.draw(g);
            // desenam interfata
            if (hud != null)
                hud.draw(g);
            g.setFont(font);
            g.setColor(Color.WHITE);
            if (player.getx() <= 400) {
                g.drawString(tutorial[0], 375, 400);
                g.drawString(tutorial[1], 375, 415);
            }
            if (player.getx() > 400 && player.getx() < 800) {
                g.drawString(tutorial[2], 375, 400);
                g.drawString(tutorial[3], 375, 415);
            }
            if (player.getx() >= 800 && player.getx() < 1200) {
                g.drawString(tutorial[4], 375, 400);
            }
        }
    }

    public void handleInput() {
        player.setLeft(Keys.keyState[Keys.LEFT]);
        player.setRight(Keys.keyState[Keys.RIGHT]);
        player.setUp(Keys.keyState[Keys.UP]);
        player.setDown(Keys.keyState[Keys.DOWN]);
        player.setJumping(Keys.keyState[Keys.UP]);
        player.setRunning(Keys.keyState[Keys.SHIFT]);
        player.setFiring(Keys.keyState[Keys.SPACE]);
        if (Keys.isPressed(Keys.ESCAPE)) {
            if(gsm.getSoundOn() && gsm.getSounds().get("select")!=null)
                gsm.getSounds().get("select").play();
            if (!gsm.getPaused())
                gsm.setPaused(true);
        }
    }
    public AudioPlayer getMusic(){return bgMusic;}
}
