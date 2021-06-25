package GameState;

import Exceptions.InvalidBackgroundException;

import Handler.Keys;
import Main.GamePanel;
import TileMap.Background;
import java.sql.*;

import java.lang.String;
import java.awt.*;


public class MenuState extends GameState{
    private Background bg;
    private int currentChoice = 0;
    private String[] options = {
            "New Game",
            "Continue Game",
            "Options",
            "Exit"
    };

    private Font font;
    public MenuState(GameStateManager gsm) {
        super(gsm);
        try {

            bg = new Background("/Backgrounds/MainMenu.jpg", 1);

            font = new Font("Arial Black", Font.PLAIN, 30);

        }
        catch(InvalidBackgroundException e) {
            System.out.println(e);
        }
        Connection c=null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:Resources\\Databases\\options.db");
            c.setAutoCommit(false);
            stmt= c.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT * FROM OPTIONS;");
            int s= rs.getInt("SOUND");
            if(s==0)
                gsm.setSoundOn(false);
            else
                gsm.setSoundOn(true);
            int m= rs.getInt("MUSIC");
            if(m==0)
                gsm.setMusicOn(false);
            else
                gsm.setMusicOn(true);
            rs.close();
            stmt.close();
            c.close();
        }
        catch(SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            gsm.setSoundOn(true);
            gsm.setMusicOn(true);
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        gsm.setMusic("/Music/menu.wav");
        if(gsm.getMusicOn() && gsm.getMusic()!=null)
            gsm.getMusic().play();

    }
    public void init() {
        if (gsm.getMusic()!=null) {
            if (gsm.getMusicOn() && !gsm.getMusic().isRunning())
                gsm.getMusic().play();
            else if (!gsm.getMusicOn() && gsm.getMusic().isRunning())
                gsm.getMusic().stop();
        }
    }

    public void update() {
        //verificam apasarile
        handleInput();
    }

    public void draw(Graphics2D g) {
        //curatam
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        // desenam backgound-ul pt meniu

        if(bg!=null)
            bg.draw(g);

        // desenam optiunile
        g.setFont(font);
        for(int i = 0; i < options.length; i++) {
            g.setPaint(new Color(1f,0f,0f,.5f));
            if(options[i]=="Continue Game")
                g.fillRect(50+i*270,350,250,100);
            else
                g.fillRect(50+i*270,350,22*options[i].length(),100);
            if(i == currentChoice) {
                g.setColor(Color.ORANGE);
            }
            else {
                g.setColor(Color.BLACK);
            }
            g.drawString(options[i], 50+i*270, 400);
            if(options[i]=="Continue Game")
                g.drawRect(50+i*270,350,250,100);
            else
                g.drawRect(50+i*270,350,22*options[i].length(),100);

        }

    }
    private void select() {
        if(currentChoice == 0) {
            // meniu pt niveluri
            gsm.setContinuing(false);
            gsm.setState(GameStateManager.NEWGAMESTATE);
        }
        if(currentChoice == 1) {
            // continue
            Connection c=null;
            Statement stmt = null;
            try {//La continue vom prelua din data de baza.Daca nu exista sau nu citeste corect,revine la meniu.
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:Resources\\Databases\\save.db");
                c.setAutoCommit(false);
                stmt= c.createStatement();
                ResultSet rs=stmt.executeQuery("SELECT * FROM SAVE;");
                int lvl = 0;
                lvl=rs.getInt("LEVEL");
                rs.close();
                stmt.close();
                c.close();
                gsm.setContinuing(true);
                if(gsm.getMusic()!=null && gsm.getMusicOn() && gsm.getMusic().isRunning())
                    gsm.getMusic().stop();
                if(lvl==3)
                    gsm.setState(GameStateManager.LEVEL3STATE);
                else if (lvl==2)
                    gsm.setState(GameStateManager.LEVEL2STATE);
                else if(lvl==1)
                    gsm.setState(GameStateManager.LEVEL1STATE);
                else {
                    gsm.setContinuing(false);
                    gsm.setState(GameStateManager.MENUSTATE);
                    return;
                }
            }
            catch(Exception e) {
                gsm.setContinuing(false);
                e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
            }
        }
        if(currentChoice == 2) {
            // options
            gsm.setState(GameStateManager.OPTIONSSTATE);
        }
        if(currentChoice == 3) {
            //iesire din aplicatie
            System.exit(0);
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
