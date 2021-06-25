package Main;

import javax.swing.*;

public class Game{

    public static void main(String[] args) {
        JFrame window = new JFrame("Escape from Bandor");
        window.setContentPane(new GamePanel());//incarcam contentul in fereastra
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//X-ul din colt va apela System.exit(0)
        window.setResizable(false);//daca dorim sa marim/micsoram fereastra
        window.pack();//mareste fereastra pentru a incapea in ea contentul
        window.setVisible(true);//fereastra sa fie vizibila
    }
}
