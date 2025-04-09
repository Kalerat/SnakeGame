package org.kgames.snake.game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class SnakeGameUI{
    private boolean showMenu = true;
    private int selectedMenuItem = 0; // Track currently selected menu item
    private final String[] menuItems = {"Start Game", "Leaderboard", "Options", "Exit"};
    private final Rectangle[] menuItemBounds = new Rectangle[4]; // Store menu item bounds for mouse interaction

    private boolean showDifficultyMenu = false; // Track if difficulty menu is shown
    private int selectedDifficultyItem = 0; // Track currently selected difficulty item
    private final String[] difficultyItems = {"Easy", "Medium", "Hard", "Impossible"};
    private final Rectangle[] difficultyItemBounds = new Rectangle[4]; // Store difficulty item bounds for mouse interaction

    // Leaderboard states
    private boolean showLeaderboard = false;
    private boolean showNameInput = false;
    private StringBuilder playerName = new StringBuilder();
    private int pendingScore = 0;

    private final SnakeGame game;

    public SnakeGameUI(SnakeGame game) {
        this.game = game;
    }

    public void paint(Graphics g, Snake snake, Apple apple, int score, int highScore) {
        if (showNameInput) {
            drawNameInputScreen(g, score);
            return;
        }
        
        if (showLeaderboard) {
            drawLeaderboardScreen(g);
            return;
        }

        if(showMenu) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, game.getWidth(), game.getHeight());
            g.setFont(new Font("TimesRoman", Font.BOLD, 24));
            
            // Draw centered title
            g.setColor(Color.GREEN);
            String titleText = "SNAKE GAME";
            g.setFont(new Font("TimesRoman", Font.BOLD, 36));
            FontMetrics titleMetrics = g.getFontMetrics();
            int titleWidth = titleMetrics.stringWidth(titleText);
            g.drawString(titleText, game.getWidth() / 2 - titleWidth / 2, 60);
            
            // Reset font for menu items
            g.setFont(new Font("TimesRoman", Font.BOLD, 24));
            FontMetrics metrics = g.getFontMetrics();
            
            int yPosition = 150;
            for (int i = 0; i < menuItems.length; i++) {
                // Set color based on selection status
                if (i == selectedMenuItem) {
                    g.setColor(Color.GREEN); // Highlight selected item
                } else {
                    g.setColor(Color.WHITE);
                }
                
                String menuText = menuItems[i];
                int menuWidth = metrics.stringWidth(menuText);
                int menuHeight = metrics.getHeight();
                
                // Center the menu item horizontally
                int xPosition = game.getWidth() / 2 - menuWidth / 2;
                
                // Draw the menu item
                g.drawString(menuText, xPosition, yPosition);
                
                // Store the bounds for mouse interaction
                menuItemBounds[i] = new Rectangle(xPosition, yPosition - menuHeight + 5, menuWidth, menuHeight);
                
                yPosition += 50;
            }
            return;
        }

        if(showDifficultyMenu) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, game.getWidth(), game.getHeight());
            g.setFont(new Font("TimesRoman", Font.BOLD, 24));

            // Draw centered title
            g.setColor(Color.GREEN);
            String titleText = "SELECT DIFFICULTY";
            g.setFont(new Font("TimesRoman", Font.BOLD, 36));
            FontMetrics titleMetrics = g.getFontMetrics();
            int titleWidth = titleMetrics.stringWidth(titleText);
            g.drawString(titleText, game.getWidth() / 2 - titleWidth / 2, 60);

            // Reset font for difficulty items
            g.setFont(new Font("TimesRoman", Font.BOLD, 24));
            FontMetrics metrics = g.getFontMetrics();

            int yPosition = 150;
            for (int i = 0; i < difficultyItems.length; i++) {
                // Set color based on selection status
                if (i == selectedDifficultyItem) {
                    g.setColor(Color.GREEN); // Highlight selected item
                } else {
                    g.setColor(Color.WHITE);
                }

                String menuText = difficultyItems[i];
                int menuWidth = metrics.stringWidth(menuText);
                int menuHeight = metrics.getHeight();

                // Center the menu item horizontally
                int xPosition = game.getWidth() / 2 - menuWidth / 2;

                // Draw the menu item
                g.drawString(menuText, xPosition, yPosition);

                // Store the bounds for mouse interaction
                difficultyItemBounds[i] = new Rectangle(xPosition, yPosition - menuHeight + 5, menuWidth, menuHeight);

                yPosition += 50;
            }
            return;
        }

        game.setBackground(Color.BLACK);
        snake.draw(g, game.calculatedElementSize, SnakeGame.DRAW_OFFSET);
        apple.draw(g, game.calculatedElementSize, SnakeGame.DRAW_OFFSET);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, SnakeGame.BOARD_SIZE * game.calculatedElementSize, SnakeGame.DRAW_OFFSET);

        g.setColor(Color.WHITE);

        g.setFont(new Font("TimesRoman", Font.PLAIN, 32));
        FontMetrics metrics = g.getFontMetrics();

        String scoreString = "Score: " + score +  "   HighScore: " + highScore;

        int textWidth = metrics.stringWidth(scoreString);
        int textHeight = metrics.getHeight();

        g.drawString(scoreString, SnakeGame.BOARD_SIZE / 2 * game.calculatedElementSize - (textWidth/2), SnakeGame.DRAW_OFFSET /2 + textHeight/2);
    }

    private void drawLeaderboardScreen(Graphics g) {
        // Get the current leaderboard
        Leaderboard leaderboard = game.getLeaderboard();
        List<Leaderboard.Score> scores = leaderboard.getScores();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, game.getWidth(), game.getHeight());
        
        // Draw title
        g.setColor(Color.GREEN);
        g.setFont(new Font("TimesRoman", Font.BOLD, 36));
        String title = "LEADERBOARD - " + difficultyItems[game.getDifficulty()];
        FontMetrics titleMetrics = g.getFontMetrics();
        int titleWidth = titleMetrics.stringWidth(title);
        g.drawString(title, game.getWidth() / 2 - titleWidth / 2, 60);
        
        // Draw scores
        g.setFont(new Font("TimesRoman", Font.BOLD, 20));
        int yPos = 120;
        
        if (scores.isEmpty()) {
            g.setColor(Color.WHITE);
            g.drawString("No scores recorded yet!", game.getWidth() / 2 - 100, yPos);
        } else {
            // Table headers
            g.setColor(Color.YELLOW);
            g.drawString("RANK", 100, yPos);
            g.drawString("NAME", 200, yPos);
            g.drawString("SCORE", game.getWidth() - 150, yPos);
            
            yPos += 30;
            g.setColor(Color.WHITE);
            
            for (int i = 0; i < scores.size(); i++) {
                Leaderboard.Score score = scores.get(i);
                g.drawString(String.valueOf(i + 1), 100, yPos);
                g.drawString(score.getName(), 200, yPos);
                g.drawString(String.valueOf(score.getScore()), game.getWidth() - 150, yPos);
                yPos += 30;
            }
        }
        
        // Draw instructions
        g.setColor(Color.GRAY);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 18));
        g.drawString("Press ESC to return to menu", game.getWidth() / 2 - 100, game.getHeight() - 50);
    }

    private void drawNameInputScreen(Graphics g, int score) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, game.getWidth(), game.getHeight());
        
        // Draw title
        g.setColor(Color.GREEN);
        g.setFont(new Font("TimesRoman", Font.BOLD, 36));
        String title = "HIGH SCORE!";
        FontMetrics titleMetrics = g.getFontMetrics();
        int titleWidth = titleMetrics.stringWidth(title);
        g.drawString(title, game.getWidth() / 2 - titleWidth / 2, 60);
        
        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("TimesRoman", Font.BOLD, 28));
        String scoreText = "Your score: " + score;
        FontMetrics scoreMetrics = g.getFontMetrics();
        int scoreWidth = scoreMetrics.stringWidth(scoreText);
        g.drawString(scoreText, game.getWidth() / 2 - scoreWidth / 2, 120);
        
        // Draw prompt
        g.setFont(new Font("TimesRoman", Font.PLAIN, 24));
        String prompt = "Enter your name:";
        FontMetrics promptMetrics = g.getFontMetrics();
        int promptWidth = promptMetrics.stringWidth(prompt);
        g.drawString(prompt, game.getWidth() / 2 - promptWidth / 2, 180);
        
        // Draw input field
        g.setColor(Color.BLACK);
        g.fillRect(game.getWidth() / 2 - 150, 200, 300, 40);
        g.setColor(Color.WHITE);
        g.drawRect(game.getWidth() / 2 - 150, 200, 300, 40);
        
        // Draw name input
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g.drawString(playerName.toString(), game.getWidth() / 2 - 140, 225);
        
        // Draw blinking cursor
        if (System.currentTimeMillis() / 500 % 2 == 0) {
            g.setColor(Color.WHITE);
            int nameWidth = g.getFontMetrics().stringWidth(playerName.toString());
            g.fillRect(game.getWidth() / 2 - 140 + nameWidth, 205, 2, 30);
        }
        
        // Draw instructions
        g.setColor(Color.GRAY);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 16));
        g.drawString("Press ENTER to submit", game.getWidth() / 2 - 80, 270);
    }

    public void handleMenuKeyPress(KeyEvent e, SnakeGame game) {
        int keyCode = e.getKeyCode();
        switch(keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                selectedMenuItem = (selectedMenuItem > 0) ? selectedMenuItem - 1 : menuItems.length - 1;
                game.repaint();
                break;
                
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                selectedMenuItem = (selectedMenuItem < menuItems.length - 1) ? selectedMenuItem + 1 : 0;
                game.repaint();
                break;
                
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                game.executeMenuItem(selectedMenuItem);
                break;
                
            case KeyEvent.VK_1:
            case KeyEvent.VK_2:
            case KeyEvent.VK_3:
            case KeyEvent.VK_4:
                // Keep legacy number selection for convenience
                int menuIndex = keyCode - KeyEvent.VK_1;
                if (menuIndex >= 0 && menuIndex < menuItems.length) {
                    game.executeMenuItem(menuIndex);
                }
                break;
                
            case KeyEvent.VK_ESCAPE:
                // Optional: Allow ESC to exit
                System.exit(0);
                break;
        }
    }

    public void handleDifficultyMenuKeyPress(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch(keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                selectedDifficultyItem = (selectedDifficultyItem > 0) ? selectedDifficultyItem - 1 : difficultyItems.length - 1;
                game.repaint();
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                selectedDifficultyItem = (selectedDifficultyItem < difficultyItems.length - 1) ? selectedDifficultyItem + 1 : 0;
                game.repaint();
                break;

            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                game.setDifficulty(selectedDifficultyItem);
                setShowDifficultyMenu(false);
                setShowMenu(true);
                game.repaint();
                break;

            case KeyEvent.VK_ESCAPE:
                setShowDifficultyMenu(false);
                setShowMenu(true);
                game.repaint();
                break;
        }
    }

    public void handleLeaderboardKeyPress(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            setShowLeaderboard(false);
            setShowMenu(true);
            game.repaint();
        }
    }

    public void handleNameInputKeyPress(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        if (keyCode == KeyEvent.VK_ENTER) {
            // Submit name if not empty
            String name = playerName.toString().trim();
            if (!name.isEmpty()) {
                game.submitHighScore(name, pendingScore);
                setShowNameInput(false);
                setShowLeaderboard(true);
                game.repaint();
            }
        } else if (keyCode == KeyEvent.VK_BACK_SPACE && !playerName.isEmpty()) {
            // Handle backspace
            playerName.deleteCharAt(playerName.length() - 1);
            game.repaint();
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            // Cancel input
            setShowNameInput(false);
            setShowMenu(true);
            game.repaint();
        } else {
            // Add typed character if it's a letter, number or space
            char c = e.getKeyChar();
            if ((Character.isLetterOrDigit(c) || c == ' ') && playerName.length() < 15) {
                playerName.append(c);
                game.repaint();
            }
        }
    }
    
    // Getters and setters for encapsulation
    public boolean isShowMenu() {
        return showMenu;
    }
    
    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }
    
    public boolean isShowDifficultyMenu() {
        return showDifficultyMenu;
    }
    
    public void setShowDifficultyMenu(boolean showDifficultyMenu) {
        this.showDifficultyMenu = showDifficultyMenu;
    }
    
    public boolean isShowLeaderboard() {
        return showLeaderboard;
    }
    
    public void setShowLeaderboard(boolean showLeaderboard) {
        this.showLeaderboard = showLeaderboard;
    }
    
    public boolean isShowNameInput() {
        return showNameInput;
    }
    
    public void setShowNameInput(boolean showNameInput) {
        this.showNameInput = showNameInput;
    }
    
    public Rectangle[] getMenuItemBounds() {
        return menuItemBounds;
    }
    
    public Rectangle[] getDifficultyItemBounds() {
        return difficultyItemBounds;
    }
    
    public void setPlayerName(StringBuilder playerName) {
        this.playerName = playerName;
    }
    
    public StringBuilder getPlayerName() {
        return playerName;
    }
    
    public void setPendingScore(int pendingScore) {
        this.pendingScore = pendingScore;
    }
    
    public int getPendingScore() {
        return pendingScore;
    }
    
    public String[] getDifficultyItems() {
        return difficultyItems;
    }
}
