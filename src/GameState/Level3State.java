package GameState;

import Entity.Audio.AudioPlayer;
import Entity.Enemies.Boss;
import Entity.Enemies.Soldier;
import Entity.Enemy;
import Entity.HUD;
import Entity.Heart;
import Entity.Player;
import Exceptions.*;
import Handler.Keys;
import Main.GamePanel;
import TileMap.*;

import javax.imageio.ImageIO;

import java.awt.*;

import java.awt.image.BufferedImage;

import java.sql.*;
import java.util.ArrayList;



import static GameState.GameStateManager.MENUSTATE;

public class Level3State extends GameState{
    private TileMap tileMap;
    private Background bg;
    private Background ocean;
    private Player player;
    private HUD hud;
    private ArrayList<Enemy> enemies;
    private ArrayList<Enemy> boss;
    private ArrayList<Heart> hearts;
    private AudioPlayer bgMusic;
    private BufferedImage bossheart;//inimile boss-ului
    public Level3State(GameStateManager gsm) {
        super(gsm);
        init();
    }

    public void init() {

        tileMap = new TileMap(30);
        try {
            tileMap.loadTiles("/Tilesets/SpaceShipPort.png");
            tileMap.loadMap("/Maps/map3.map");
            tileMap.setPosition(0, 0);
            tileMap.setTween(1);
        }
        catch (InvalidTilesException e)
        {
            gsm.setState(MENUSTATE);
            System.out.println(e);
        }
        catch (InvalidMatrixException  e)
        {
            gsm.setState(MENUSTATE);
            System.out.println(e);
        }
        try {
            bg = new Background("/Backgrounds/sky.png", 0.1);
            bg.setPosition(0, 0);

            ocean = new Background("/Backgrounds/ocean.png", 0.1);
            ocean.setPosition(1, 300);
        }
        catch (InvalidBackgroundException e)
        {
            System.out.println(e);
        }
        try {
            player = new Player(tileMap);
            player.setSoundOn(gsm.getSoundOn());
            if (!gsm.getContinuing()) {
                player.setPosition(40, 185);
                player.setScore(gsm.getScore());
                try {
                    hearts = new ArrayList<Heart>();
                    Point[] points = new Point[]{
                            new Point(7286, 215),
                            new Point(2967, 95),
                            new Point(4606, 425)
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
                    player.setHealth(h);
                    player.setScore(rs.getInt("SCORE"));
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
                catch(SQLException | ClassNotFoundException e){
                    e.printStackTrace();
                    System.err.println(e.getClass().getName()+": "+e.getMessage());
                }
                try {
                    Class.forName("org.sqlite.JDBC");
                    boss = new ArrayList<Enemy>();
                    c = DriverManager.getConnection("jdbc:sqlite:Resources\\Databases\\boss.db");
                    c.setAutoCommit(false);
                    stmt = c.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM BOSS;");
                    while (rs.next()) {
                        try {
                            Boss b = new Boss(tileMap, player);
                            int h = rs.getInt("HEALTH");
                            b.setHealth(h);
                            b.setSoundOn(gsm.getSoundOn());
                            int bx = rs.getInt("POSITIONX");
                            int by = rs.getInt("POSITIONY");
                            b.setPosition(bx, by);
                            boss.add(b);
                        } catch (InvalidEnemySpritesException e) {
                            System.out.println(e);
                        }
                    }
                    rs.close();
                    stmt.close();
                    c.close();
                }
                catch(SQLException | ClassNotFoundException e){
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
            BufferedImage origin = ImageIO.read(
                    getClass().getResourceAsStream(
                            "/HUD/hud.png"
                    )
            );
            bossheart = origin.getSubimage(0, 0, 37, 37);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        try {
            bgMusic=new AudioPlayer("/Music/port.wav");
            if(gsm.getState()==GameStateManager.LEVEL3STATE) {
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
        player.update();
        if(player!=null) {
            tileMap.setPosition(
                    GamePanel.WIDTH / 2 - player.getx(),
                    GamePanel.HEIGHT / 2 - player.gety()
            );
            //setam imaginile pt parallax scrolling
            if (bg != null)
                bg.setPosition(tileMap.getx(), tileMap.gety());
            if (ocean != null)
                ocean.setPosition(tileMap.getx() * 3 - 1500, tileMap.gety() * 2 + 600);
            player.checkAttack(enemies);
            player.checkAttack(boss);
            for (int i = 0; i < enemies.size(); i++) {
                Enemy e = enemies.get(i);
                e.update();
                e.checkAttack(player);
                if (e.isDead() || e.gety() >= 450) {
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
            for (int i = 0; i < boss.size(); i++) {
                boss.get(i).update();
                boss.get(i).checkAttack(player);
                if (boss.get(i).isDead()) {
                    boss.remove(i);
                    i--;
                }
            }
            if (player.getx() >= 9050) {
                gsm.setScore(0);
                if (bgMusic != null && bgMusic.isRunning())
                    bgMusic.stop();
                gsm.setState(GameStateManager.MENUSTATE);
            }
            if (player.gety() >= 450 || player.getHealth() == 0) {
                if (bgMusic != null && bgMusic.isRunning())
                    bgMusic.stop();
                init();
            }
        }
    }
    private void populateEnemies() {

        enemies = new ArrayList<Enemy>();
        boss = new ArrayList<Enemy>();
        Soldier s;
        Point[] points = new Point[] {
                new Point(7455, 240),
                new Point(6408, 180),
                new Point(5912, 215),
                new Point(5279, 270),
                new Point(4800, 270),
                new Point(3900, 170),
                new Point(3000, 200),
                new Point(1525, 100),
                new Point(1680, 100),
                new Point(1800, 100),
                new Point(1128,145)
        };
        try {
            for (int i = 0; i < points.length; i++) {
                s = new Soldier(tileMap, player);
                s.setPosition(points[i].x, points[i].y);
                s.setSoundOn(gsm.getSoundOn());
                enemies.add(s);
            }
            Boss b=new Boss(tileMap,player);
            b.setSoundOn(gsm.getSoundOn());
            b.setPosition(8781,240);
            boss.add(b);
        }
        catch (InvalidEnemySpritesException e)
        {
            System.out.println(e);
        }

    }
    public void draw(Graphics2D g) {

        // curatam ecranul screen
        Color col=new Color(94, 126, 181);
        g.setColor(col);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        if(bg!=null)
            bg.draw(g);
        if(ocean!=null)
            ocean.draw(g);
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
        for(int i = 0; i < boss.size(); i++) {
            boss.get(i).draw(g);
        }
        if(player.getx()>8000)//vom desena starea de viata a inamicului final dupa ce e aproape de final
        {
            for(int i = 0; i < boss.size(); i++) {
                {
                    for(int j = 0; j < boss.get(i).getHealth()/5; j++) {
                        if(bossheart!=null)
                            g.drawImage(bossheart, 10 + j * 37, 385, null);
                    }
                }
            }
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
            String sql = "DROP TABLE IF EXISTS SAVE;"+  //pt a salva,stergem datele initiale
                    "CREATE TABLE IF NOT EXISTS SAVE " +//si inlocuim cu tabela  noua
                    "(LEVEL INT PRIMARY KEY NOT NULL, " +
                    "SCORE INT NOT NULL, " +
                    "HEALTH INT  NOT NULL, " +
                    "POSITIONX INT NOT NULL, " +
                    "POSITIONY INT NOT NULL)";
            stmt.executeUpdate(sql);
            c.setAutoCommit(false);
            sql="INSERT OR REPLACE INTO SAVE (LEVEL,SCORE,HEALTH,POSITIONX,POSITIONY)"+
                    "VALUES (3, "+player.getScore()+", "+player.getHealth()+", "+player.getx()+", "+player.gety()+" );";
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
            c = DriverManager.getConnection("jdbc:sqlite:Resources\\Databases\\boss.db");
            stmt= c.createStatement();
            sql = "DROP TABLE IF EXISTS BOSS;"+
                        "CREATE TABLE IF NOT EXISTS BOSS " +
                        "(HEALTH INT PRIMARY KEY NOT NULL, " +
                        "POSITIONX INT NOT NULL, " +
                        "POSITIONY INT NOT NULL)";
            stmt.executeUpdate(sql);
            c.setAutoCommit(false);
            for(int i=0;i<boss.size();i++) {
                sql = "INSERT OR REPLACE INTO BOSS (HEALTH,POSITIONX,POSITIONY)" +
                        "VALUES (" + boss.get(i).getHealth() + ", " + boss.get(i).getx() + ", " + boss.get(i).gety() + " );";
            }
            stmt.executeUpdate(sql);
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