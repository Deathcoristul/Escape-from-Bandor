package GameState;
import Exceptions.InvalidBackgroundException;
import Handler.Keys;
import Main.GamePanel;
import TileMap.Background;
import java.lang.String;
import java.awt.*;


public class Menu1State extends GameState{
    private Background bg;

    private int currentChoice = 0;
    private String[] options = {
            "Level 1",
            "Level 2",
            "Level 3",
            "Back"
    };

    private Font font;
    public Menu1State(GameStateManager gsm) {

        super(gsm);

        try {

            bg = new Background("/Backgrounds/MainMenu.jpg", 1);

            font = new Font("Arial Black", Font.PLAIN, 30);

        }
        catch(InvalidBackgroundException e) {
            System.out.println(e);
        }

    }
    public void init() {}
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
        for(int i = 0; i < options.length; i++) {
            g.setPaint(new Color(1f,0f,0f,.5f));
            g.fillRect(50+i*270,350,22*options[i].length(),100);
            if(i == currentChoice) {
                g.setColor(Color.ORANGE);
            }
            else {
                g.setColor(Color.BLACK);
            }
            g.drawString(options[i], 50+i*270, 400);
            g.drawRect(50+i*270,350,22*options[i].length(),100);
        }

    }
    private void select() {
        if(currentChoice == 0) {
            // level1
            if(gsm.getMusic()!=null && gsm.getMusicOn() && gsm.getMusic().isRunning())
                gsm.getMusic().stop();
            gsm.setState(GameStateManager.LEVEL1STATE);
        }
        if(currentChoice == 1) {
            // level2
            if(gsm.getMusic()!=null && gsm.getMusicOn() && gsm.getMusic().isRunning())
                gsm.getMusic().stop();
            gsm.setState(GameStateManager.LEVEL2STATE);
        }
        if(currentChoice == 2) {
            //level 3
            if(gsm.getMusic()!=null && gsm.getMusicOn() && gsm.getMusic().isRunning())
                gsm.getMusic().stop();
            gsm.setState(GameStateManager.LEVEL3STATE);
        }
        if(currentChoice == 3) {
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
