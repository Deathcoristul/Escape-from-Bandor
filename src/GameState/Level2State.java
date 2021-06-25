package GameState;
import java.sql.*;

import Entity.Audio.AudioPlayer;
import Entity.Enemies.Soldier;
import Entity.Enemy;
import Entity.HUD;
import Entity.Heart;
import Entity.Player;
import Exceptions.*;
import Handler.Keys;
import Main.GamePanel;
import TileMap.*;


import java.awt.*;

import java.util.ArrayList;

import static GameState.GameStateManager.MENUSTATE;

public class Level2State extends GameState{
    private TileMap tileMap;
    private Background bg;
    private Background building1;
    private Background building2;
    private Player player;
    private HUD hud;
    private ArrayList<Enemy> enemies;
    private ArrayList<Heart> hearts;
    private AudioPlayer bgMusic;
    public Level2State(GameStateManager gsm) {
        super(gsm);
        init();
    }

    public void init() {

        tileMap = new TileMap(30);
        try {
            tileMap.loadTiles("/Tilesets/City.png");
            tileMap.loadMap("/Maps/map2.map");
            tileMap.setPosition(0, 0);
            tileMap.setTween(1);
        }
        catch (InvalidTilesException e)
        {
            gsm.setState(MENUSTATE);
            System.out.println(e);
        }
        catch (InvalidMatrixException e) {
            gsm.setState(MENUSTATE);
            System.out.println(e);
        }

        try {
            bg = new Background("/Backgrounds/sky.png", 0.1);
            bg.setPosition(0, 0);

            building1 = new Background("/Backgrounds/blocuri.png", 0.1);
            building1.setPosition(1, 1200);

            building2 = new Background("/Backgrounds/blocuri2.png", 0.1);
            building2.setPosition(2, -300);
        }
        catch (InvalidBackgroundException e)
        {
            System.out.println(e);
        }
        try {
            player = new Player(tileMap);
            player.setSoundOn(gsm.getSoundOn());
            if (!gsm.getContinuing()) {
                player.setPosition(30, 245);
                player.setScore(gsm.getScore());
                try {
                    hearts = new ArrayList<Heart>();
                    Point[] points = new Point[]{
                            new Point(2733, 245),
                            new Point(5733, 245),
                            new Point(8475, 245),
                            new Point(4040, 275)
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
                            s.setSoundOn(gsm.getSoundOn());
                            int ex = rs.getInt("POSITIONX");
                            int ey = rs.getInt("POSITIONY");
                            s.setPosition(ex, ey);
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
            try {
                hud = new HUD(player);
            }
            catch (InvalidHUDException e)
            {
                System.out.println(e);
            }
        }
        catch (InvalidPlayerSpritesException e) {
            gsm.setState(MENUSTATE);
            System.out.println(e);
        }
        try {
            bgMusic=new AudioPlayer("/Music/City.wav");
            if(gsm.getState()==GameStateManager.LEVEL2STATE) {
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
            tileMap.setPosition(
                    GamePanel.WIDTH / 2 - player.getx(),
                    GamePanel.HEIGHT / 2 - player.gety()
            );
            //setam imaginile pt parallax scrolling
            if (bg != null)
                bg.setPosition(tileMap.getx(), tileMap.gety());
            if (building1 != null)
                building1.setPosition(tileMap.getx() * 3 - 1500, tileMap.gety() * 2 + 1200);
            if (building2 != null)
                building2.setPosition(tileMap.getx() * 2, tileMap.gety() - 400);
            player.checkAttack(enemies);
            for (int i = 0; i < enemies.size(); i++) {
                Enemy e = enemies.get(i);
                e.update();
                e.checkAttack(player);
                if (e.isDead() || e.gety() >= 400) {
                    enemies.remove(i);
                    i--;
                }
            }
            for (int i = 0; i < hearts.size(); i++) {
                Heart h = hearts.get(i);
                h.checkIntersected();
                if (h.isTaken()) {
                    hearts.remove(i);
                    i--;
                }
            }
            if (player.getx() >= 9545) {
                gsm.setScore(player.getScore());
                gsm.setContinuing(false);
                if (bgMusic != null && bgMusic.isRunning())
                    bgMusic.stop();
                gsm.setState(GameStateManager.LEVEL3STATE);
            }
            if (player.gety() >= 400 || player.getHealth() == 0) {
                if (bgMusic != null && bgMusic.isRunning())
                    bgMusic.stop();
                init();
            }
        }
    }
    private void populateEnemies() {

        enemies = new ArrayList<Enemy>();

        Soldier s;
        Point[] points = new Point[] {
                new Point(1000, 200),
                new Point(1577, 100),
                new Point(1376, 230),
                new Point(2141, 230),
                new Point(2957, 200),
                new Point(3638, 200),
                new Point(4267, 200),
                new Point(4770, 200),
                new Point(5133, 200),
                new Point(5815, 200),
                new Point(6537, 200),
                new Point(7244, 200),
                new Point(8004, 200),
                new Point(8953, 200),
                new Point(9177, 150)
        };
        try {
            for (int i = 0; i < points.length; i++) {
                s = new Soldier(tileMap, player);
                s.setSoundOn(gsm.getSoundOn());
                s.setPosition(points[i].x, points[i].y);
                enemies.add(s);
            }
        }
        catch (InvalidEnemySpritesException e)
        {
            System.out.println(e);
        }
    }
    public void draw(Graphics2D g) {

        // curatam ecranul screen
        Color col=new Color(169, 156, 191);
        g.setColor(col);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        if(bg!=null)
            bg.draw(g);
        if(building2!=null)
            building2.draw(g);
        if(building1!=null)
            building1.draw(g);
        // desenam tilemap-ul
        if(tileMap!=null)
            tileMap.draw(g);
        // desenam inamicii
        for(int i = 0; i < enemies.size(); i++) {
            if(enemies.get(i)!=null)
                enemies.get(i).draw(g);
        }
        for(int i = 0; i < hearts.size(); i++) {
            if(hearts.get(i)!=null)
                hearts.get(i).draw(g);
        }
        // desenam jucatorul
        if(player!=null)
            player.draw(g);
        // desenam interfata
        if(hud!=null)
            hud.draw(g);
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
                    "VALUES (2, "+player.getScore()+", "+player.getHealth()+", "+player.getx()+", "+player.gety()+" );";
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
    public void handleInput() {
        player.setLeft(Keys.keyState[Keys.LEFT]);
        player.setRight(Keys.keyState[Keys.RIGHT]);
        player.setUp(Keys.keyState[Keys.UP]);
        player.setDown(Keys.keyState[Keys.DOWN]);
        player.setJumping(Keys.keyState[Keys.UP]);
        player.setRunning(Keys.keyState[Keys.SHIFT]);
        player.setFiring(Keys.keyState[Keys.SPACE]);
        if (Keys.isPressed(Keys.ESCAPE)) {
            if (gsm.getSoundOn() && gsm.getSounds().get("select") != null)
                gsm.getSounds().get("select").play();
            if (!gsm.getPaused())
                gsm.setPaused(true);
        }
    }
    public AudioPlayer getMusic(){return bgMusic;}
}
