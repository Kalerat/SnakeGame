package org.kgames.snake.game;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    public static final String BASE_FILEPATH = "/audio/";

    // Sound effects
    public static final String SNAKE_EAT_SOUND_PATH = BASE_FILEPATH + "eat.wav";
    public static final String SNAKE_DEATH_SOUND_PATH = BASE_FILEPATH + "death.wav";
    public static final String SNAKE_MOVE_SOUND_PATH = BASE_FILEPATH + "move.wav";

    public static final String BACKGROUND_MUSIC_PATH = BASE_FILEPATH + "background_music.wav";
    
    private Map<String, Clip> soundCache = new HashMap<>();
    private Clip backgroundMusic;
    
    public boolean loadSounds() {
        try {
            // Load all sound effects
            loadSound(SNAKE_EAT_SOUND_PATH);
            //loadSound(SNAKE_DEATH_SOUND_PATH);
            //loadSound(SNAKE_MOVE_SOUND_PATH);
            //loadSound(BACKGROUND_MUSIC_PATH);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to load sounds: " + e.getMessage());
            return false;
        }
    }
    
    private void loadSound(String path) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            throw new IOException("Sound file not found: " + path);
        }
        
        BufferedInputStream bis = new BufferedInputStream(is);
        AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
        Clip clip = AudioSystem.getClip();
        clip.open(ais);
        soundCache.put(path, clip);
    }
    
    private void playSound(String path) {
        Clip clip = soundCache.get(path);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    public void playEatSound() {
        playSound(SNAKE_EAT_SOUND_PATH);
    }
    
    public void playDeathSound() {
        playSound(SNAKE_DEATH_SOUND_PATH);
    }
    
    public void playMoveSound() {
        playSound(SNAKE_MOVE_SOUND_PATH);
    }
    
    public void playBackgroundMusic() {
        Clip clip = soundCache.get(BACKGROUND_MUSIC_PATH);
        if (clip != null) {
            backgroundMusic = clip;
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        }
    }
    
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }
}
