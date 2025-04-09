package org.kgames.snake.game;

import org.kgames.snake.util.Vector2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SnakeGame extends JPanel implements KeyListener, MouseListener, ComponentListener {

    public static final int BOARD_SIZE = 20;
    public static final int ELEMENT_SIZE = 25;
    public static final int DRAW_OFFSET = ELEMENT_SIZE * 2;
    
    private static final int TARGET_FPS = 60; // Target frames per second for rendering

    private static final double[] BASE_SPEEDS = {5.0, 7.0, 9.0, 12.0}; // Easy, Medium, Hard, Impossible
    private static final double[] MAX_SPEEDS = {9.0, 12.0, 17.0, 25.0}; // Easy, Medium, Hard, Impossible
    private static final int SPEED_INCREASE_THRESHOLD = 5;
    private static final double SPEED_INCREASE_FACTOR = 0.1;


    public JFrame mainFrame;

    private int displayWidth;
    private int displayHeight;
    public int calculatedElementSize;

    private Snake snake;
    private Apple apple;
    private final TextureManager textureManager;
    private final AudioManager audioManager;

    private boolean inGame;
    private Thread gameThread;

    private int score = 0;
    private int difficulty = 1; // Default to Medium, moved from SnakeGameUI

    private SnakeGameUI ui;
    private Leaderboard leaderboard;

    public SnakeGame() {

        this.mainFrame = new JFrame("Snake Game");

        // Add Panel Components
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addComponentListener(this);
        this.setFocusable(true);
        this.requestFocusInWindow();

        // Initialize the game components
        textureManager = new TextureManager();
        textureManager.init();
        audioManager = new AudioManager();
        audioManager.loadSounds();

        // Set the initial size of the game window
        updateDisplayDimensions();
        ui = new SnakeGameUI(this);
        leaderboard = new Leaderboard(difficulty);
        this.mainFrame.add(this);
        this.mainFrame.pack();
        this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainFrame.setLocationRelativeTo(null);
        this.mainFrame.setVisible(true);
    }

    // Update the display dimensions based on the current window size
    private void updateDisplayDimensions() {
        this.displayWidth = this.getWidth();
        this.displayHeight = this.getHeight();
        this.calculatedElementSize = Math.min(displayWidth / BOARD_SIZE, displayHeight / (BOARD_SIZE));
        this.calculatedElementSize = Math.max(calculatedElementSize, ELEMENT_SIZE);
        this.setPreferredSize(new Dimension(calculatedElementSize*BOARD_SIZE, (calculatedElementSize * BOARD_SIZE) + DRAW_OFFSET));
        this.mainFrame.pack();
    }

    // Initialize the game state
    public void init() {
        snake = new Snake(BOARD_SIZE, textureManager);
        score = 0;

        updateSnakeSpeed();

        apple = new Apple(BOARD_SIZE, textureManager);
    }

    // Update the snake's speed based on the current difficulty and score
    private void updateSnakeSpeed() {
        double baseSpeed = BASE_SPEEDS[difficulty];
        double maxSpeed = MAX_SPEEDS[difficulty];

        double currentSpeed = baseSpeed;

        if (score > 0) {
            int speedIncrements = score / SPEED_INCREASE_THRESHOLD;
            double speedMultiplier = 1.0 + (speedIncrements * SPEED_INCREASE_FACTOR);
            currentSpeed = Math.min(baseSpeed * speedMultiplier, maxSpeed);
        }

        if (snake != null) {
            snake.setSpeedFactor(currentSpeed);
        }
    }

    // Handle game over state
    private void gameOver() {
        System.out.println("You lose");
        inGame = false;

        if (leaderboard.isHighScore(score)) {
            ui.setShowMenu(false);
            ui.setShowNameInput(true);
            ui.setPlayerName(new StringBuilder());
            ui.setPendingScore(score);
        } else {
            ui.setShowMenu(true);
        }
        
        repaint();
    }

    public void submitHighScore(String name, int score) {
        leaderboard.addScore(name, score);
    }

    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    public int getHighScore() {
        return leaderboard.getHighestScore();
    }
    
    // Getter and setter for difficulty
    public int getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
        // Update leaderboard when changing difficulty
        leaderboard = new Leaderboard(difficulty);
        
        // Update snake speed if we're in a game
        if (snake != null) {
            updateSnakeSpeed();
        }
    }

    // Check if the snake has eaten the apple
    private void checkApple() {
        Vector2 head = snake.getHead();
        if(apple.isEaten(head)) {
            // Play eating sound
            audioManager.playEatSound();
            snake.grow();
            // Set eating state when apple is eaten
            snake.setEating();
            score++;
            
            // Check if we need to increase speed
            if (score % SPEED_INCREASE_THRESHOLD == 0) {
                updateSnakeSpeed();
            }
            
            apple.place(snake.getSnakeElements());
        }
    }

    // Start the game loop
    private void run() {
        inGame = true;
        gameThread = new Thread(this::processGameLoop);
        gameThread.start();
    }

    // Main game loop
    protected void processGameLoop() {
        final long nsToSecScale = 1_000_000_000;
        final long frameInterval = nsToSecScale / TARGET_FPS;

        long lastUpdateTime = System.nanoTime();
        long lastRenderTime = System.nanoTime();
        long accumulatedTime = 0;

        while(inGame) {
            long currentTime = System.nanoTime();
            long elapsedTime = currentTime - lastUpdateTime;
            lastUpdateTime = currentTime;
            accumulatedTime += elapsedTime;

            long updateInterval = (long)(nsToSecScale / snake.getSpeedFactor());

            while (accumulatedTime >= updateInterval) {
                snake.move();
                if(snake.checkCollision()) {
                    gameOver();
                    break;
                } else {
                    checkApple();
                }
                accumulatedTime -= updateInterval;
            }

            if (!inGame) break;

            if (currentTime - lastRenderTime >= frameInterval) {
                repaint();
                lastRenderTime = currentTime;
            } else {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Execute the selected menu item
    public void executeMenuItem(int index) {
        switch(index) {
            case 0: // Start Game
                ui.setShowMenu(false);
                ui.setShowLeaderboard(false);
                ui.setShowNameInput(false);
                init();
                run();
                break;
            case 1: // Leaderboard
                leaderboard = new Leaderboard(difficulty);
                ui.setShowMenu(false);
                ui.setShowLeaderboard(true);
                repaint();
                break;
            case 2: // Options
                ui.setShowMenu(false);
                ui.setShowDifficultyMenu(true);
                repaint();
                break;
            case 3: // Exit
                System.exit(0);
                break;
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ui.paint(g, snake, apple, score, getHighScore());
    }

    public static void main(String[] args) {
        SnakeGame game = new SnakeGame();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(ui.isShowMenu()) {
            ui.handleMenuKeyPress(e, this);
        } else if (ui.isShowDifficultyMenu()) {
            ui.handleDifficultyMenuKeyPress(e);
        } else if (ui.isShowLeaderboard()) {
            ui.handleLeaderboardKeyPress(e);
        } else if (ui.isShowNameInput()) {
            ui.handleNameInputKeyPress(e);
        } else {
            handleGameKeyPress(e);
        }
    }

    private void handleGameKeyPress(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch(keyCode) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                snake.setDirection(0, -1);
                break;
                
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                snake.setDirection(-1, 0);
                break;
                
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                snake.setDirection(0, 1);
                break;
                
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                snake.setDirection(1, 0);
                break;
                
            case KeyEvent.VK_ESCAPE:
                // Allow returning to menu with ESC
                gameOver();
                break;
        }
    }

    // Mouse listener methods
    @Override
    public void mouseClicked(MouseEvent e) {
        if (ui.isShowMenu()) {
            Point clickPoint = e.getPoint();
            Rectangle[] menuItemBounds = ui.getMenuItemBounds();
            for (int i = 0; i < menuItemBounds.length; i++) {
                if (menuItemBounds[i].contains(clickPoint)) {
                    executeMenuItem(i);
                    break;
                }
            }
        } else if (ui.isShowDifficultyMenu()) {
            Point clickPoint = e.getPoint();
            Rectangle[] difficultyItemBounds = ui.getDifficultyItemBounds();
            for (int i = 0; i < difficultyItemBounds.length; i++) {
                if (difficultyItemBounds[i].contains(clickPoint)) {
                    setDifficulty(i);
                    ui.setShowDifficultyMenu(false);
                    ui.setShowMenu(true);
                    repaint();
                    break;
                }
            }
        }
    }

    // Unused key and mouse listener methods
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void componentResized(ComponentEvent e) {
        updateDisplayDimensions();
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
