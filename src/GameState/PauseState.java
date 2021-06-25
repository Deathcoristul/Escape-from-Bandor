package GameState;

import Handler.Keys;
import Main.GamePanel;

import java.awt.*;


import static GameState.GameStateManager.MENUSTATE;

public class PauseState extends GameState{
    private Font font;
    private int currentChoice = 0;
    private String[] options = {
            "Continue",
            "Exit",
            "Exit Game"
    };
    public PauseState(GameStateManager gsm) {
        super(gsm);
        // font
        font = new Font("Arial Black", Font.PLAIN, 50);

    }
    public void init() {}


    public void update() {
        handleInput();
    }


    public void draw(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 2));
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString("Game Paused", 350, 90);
        for(int i = 0; i < options.length; i++) {
            g.setPaint(new Color(1f,0f,0f,.5f));
            g.fillRect(50+i*285,350,23*options[i].length(),100);
            if(i == currentChoice) {
                g.setColor(Color.ORANGE);
            }
            else {
                g.setColor(Color.BLACK);
            }
            g.setFont(new Font("Arial Black", Font.PLAIN, 35));
            g.drawString(options[i], 50+i*285, 400);
            g.drawRect(50+i*285,350,23*options[i].length(),100);
        }
    }

    public void select() {
        if (currentChoice==0) {
            if (gsm.getPaused())//daca jocul e in starea de pauza
                gsm.setPaused(false);
        }
        if (currentChoice==2) {
            if (gsm.getPaused()) {
                gsm.getStates().get(gsm.getState()).saveFile();
                System.exit(0);
            }
        }
        if (currentChoice==1) {
            if (gsm.getPaused()) {
                gsm.getStates().get(gsm.getState()).saveFile();
                gsm.setPaused(false);
                if(gsm.getStates().get(gsm.getState()).getMusic()!=null && gsm.getStates().get(gsm.getState()).getMusic().isRunning() )
                    gsm.getStates().get(gsm.getState()).getMusic().stop();
                gsm.setState(MENUSTATE);
            }
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
