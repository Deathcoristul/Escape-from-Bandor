package GameState;

import Exceptions.InvalidBackgroundException;
import Handler.Keys;
import Main.GamePanel;
import TileMap.Background;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class OptionsState extends GameState{
    private Background bg;
    private int currentChoice = 0;
    private String[] options = {
            "Sound",
            "Music",
            "Back"
    };

    private Font font;
    public OptionsState(GameStateManager gsm) {
        super(gsm);
        try {

            bg = new Background("/Backgrounds/MainMenu.jpg", 1);

            font = new Font("Arial Black", Font.PLAIN, 30);

        }
        catch(InvalidBackgroundException e) {
            System.out.println(e);
        }
    }
    public void init() { }
    public void update() {handleInput(); }
    public void draw(Graphics2D g) {
        //curatam ecranul
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        // desenam backgound-ul pentru meniu
        if(bg!=null)
            bg.draw(g);
        // desenam optiunile
        g.setFont(font);
        if(gsm.getSoundOn())//culorile reprezinta starile pentru sunet si muzica
            g.setPaint(new Color(0x7702FA02, true));
        else
            g.setPaint(new Color(1f,0f,0f,.5f));
        g.fillRect(50,350,110,100);
        if(gsm.getMusicOn())
            g.setPaint(new Color(0x7702FA02, true));
        else
            g.setPaint(new Color(1f,0f,0f,.5f));
        g.fillRect(400,350,110,100);
        for(int i = 0; i < options.length; i++) {
            g.setPaint(new Color(1f,0f,0f,.5f));
            if(i==2)//buton de back
                g.fillRect(50+i*350,350,22*options[i].length(),100);
            if(i == currentChoice) {
                g.setColor(Color.YELLOW);
            }
            else {
                g.setColor(Color.BLACK);
            }
            g.drawString(options[i], 50+i*350, 400);
            g.drawRect(50+i*350,350,22*options[i].length(),100);
        }

    }
    public void saveSettings()
    {
        int sO=gsm.getSoundOn()?1:0;
        int mO=gsm.getMusicOn()?1:0;
        Connection c=null;
        Statement stmt = null;
        try{
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Resources\\Databases\\options.db");
            stmt= c.createStatement();
            String sql = "DROP TABLE IF EXISTS OPTIONS;"+  //pt a salva,stergem datele initiale
                    "CREATE TABLE IF NOT EXISTS OPTIONS " +//si inlocuim cu tabela  noua
                    "(SOUND INT PRIMARY KEY NOT NULL, " +
                    "MUSIC INT NOT NULL)";
            stmt.executeUpdate(sql);
            c.setAutoCommit(false);
            sql="INSERT OR REPLACE INTO OPTIONS (SOUND,MUSIC)"+
                    "VALUES ("+sO+", "+mO+" );";//vom salva setarile sub forma de int in loc de boolean
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            c.close();
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
    }
    private void select() {
        if(currentChoice == 0) {
            // sound
            gsm.setSoundOn(!gsm.getSoundOn());
            saveSettings();
        }
        if(currentChoice == 1) {
            // music
            gsm.setMusicOn(!gsm.getMusicOn());
            if(gsm.getMusic()!=null) {
                if (gsm.getMusicOn() && !gsm.getMusic().isRunning())
                    gsm.getMusic().play();//ia de la capat muzica
                else if (!gsm.getMusicOn() && gsm.getMusic().isRunning())
                    gsm.getMusic().stop();//opreste muzica instant
            }
            saveSettings();
        }
        if(currentChoice == 2) {
            //inapoi la meniu
            gsm.setState(GameStateManager.MENUSTATE);
        }
    }
    public void handleInput() {
        if(Keys.isPressed(Keys.ENTER)){
            if(gsm.getSoundOn() && gsm.getSounds().get("selected")!=null)
                gsm.getSounds().get("selected").play();
            select();
        }
        if(Keys.isPressed(Keys.LEFT)) {
            if(gsm.getSoundOn() && gsm.getSounds().get("select")!=null)
                gsm.getSounds().get("select").play();
            currentChoice--;
            if(currentChoice == -1) {
                currentChoice = options.length - 1;
            }
        }
        if(Keys.isPressed(Keys.RIGHT)) {
            if(gsm.getSoundOn() && gsm.getSounds().get("select")!=null)
                gsm.getSounds().get("select").play();
            currentChoice++;
            if(currentChoice == options.length) {
                currentChoice = 0;
            }
        }
    }

}
