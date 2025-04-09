package org.kgames.snake.game;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Leaderboard {
    private static final String SAVES_DIR = "saves";
    private static final String BASE_FILEPATH = SAVES_DIR + File.separator + "leaderboard_";
    private static final int MAX_SCORES = 10;
    
    private List<Score> scores;
    private int difficulty;
    
    public Leaderboard(int difficulty) {
        this.difficulty = difficulty;
        this.scores = new ArrayList<>();
        loadScores();
    }
    
    public boolean isHighScore(int score) {
        if (scores.size() < MAX_SCORES) {
            return true;
        }
        return score > scores.get(scores.size() - 1).getScore();
    }
    
    public void addScore(String name, int score) {
        scores.add(new Score(name, score));
        Collections.sort(scores);
        
        // Trim the list if it exceeds MAX_SCORES
        if (scores.size() > MAX_SCORES) {
            scores = scores.subList(0, MAX_SCORES);
        }
        
        saveScores();
    }
    
    public List<Score> getScores() {
        return Collections.unmodifiableList(scores);
    }
    
    public int getHighestScore() {
        if (scores.isEmpty()) {
            return 0;
        }
        return scores.get(0).getScore();
    }
    
    public void loadScores() {
        // Create saves directory if it doesn't exist
        File savesDir = new File(SAVES_DIR);
        if (!savesDir.exists()) {
            savesDir.mkdir();
        }

        String filepath = BASE_FILEPATH + difficulty + ".dat";
        File file = new File(filepath);
        
        if (!file.exists()) {
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            List<Score> loadedScores = (List<Score>) ois.readObject();
            scores = loadedScores;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading leaderboard: " + e.getMessage());
            scores = new ArrayList<>();
        }
    }
    
    private void saveScores() {
        // Create saves directory if it doesn't exist
        File savesDir = new File(SAVES_DIR);
        if (!savesDir.exists()) {
            savesDir.mkdir();
        }

        String filepath = BASE_FILEPATH + difficulty + ".dat";
        try {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filepath))) {
                oos.writeObject(scores);
            }
        } catch (IOException e) {
            System.err.println("Error saving leaderboard: " + e.getMessage());
        }
    }
    
    public static class Score implements Comparable<Score>, Serializable {
        private static final long serialVersionUID = 1L;
        
        private final String name;
        private final int score;
        
        public Score(String name, int score) {
            this.name = name;
            this.score = score;
        }
        
        public String getName() {
            return name;
        }
        
        public int getScore() {
            return score;
        }
        
        @Override
        public int compareTo(Score other) {
            return Integer.compare(other.score, this.score);
        }
    }
}
