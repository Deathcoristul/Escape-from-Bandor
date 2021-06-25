package Entity.Audio;

import Exceptions.InvalidAudioException;

import javax.sound.sampled.*;

import java.io.IOException;

public class AudioPlayer {
    public Clip clip;

    public AudioPlayer(String s) throws InvalidAudioException {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(AudioPlayer.class.getResourceAsStream(s));//preia cu input din resources
            clip = AudioSystem.getClip();//preia clipul din sistem
            clip.open(ais);//deschide clipul fara a-l rula
        }
        catch(NullPointerException|UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            throw new InvalidAudioException();
        }
    }
    public void play() {
        if(clip==null) return;
        if(clip.isRunning()) stop();//se opreste daca clipul este in rulare
        clip.setFramePosition(0);//setam pe pozitia 0 a clipului
        while(!clip.isRunning()) clip.start();//cat timp nu e in rulare,dam start la clip
    }
    public boolean isRunning(){return clip.isRunning();}
    public void stop() {
        if(clip==null)
            return;
        if(clip.isRunning())
            clip.stop();
    }
    public void close()
    {
        stop();
        clip.close();//inchidem clipul
    }
}
