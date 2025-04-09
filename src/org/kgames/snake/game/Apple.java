package org.kgames.snake.game;

import org.kgames.snake.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Apple {
    private Vector2 position;
    private final TextureManager textureManager;

    public Apple(int boardSize, TextureManager tm) {
        this.position = new Vector2(boardSize - (boardSize / 4), boardSize / 2);
        this.textureManager = tm;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void place(LinkedList<Vector2> snakeElements) {
        ArrayList<Vector2> freeCells = new ArrayList<>();
        for(int i = 0; i < SnakeGame.BOARD_SIZE; i++) {
            for(int j = 0; j < SnakeGame.BOARD_SIZE; j++) {
                freeCells.add(new Vector2(i, j));
            }
        }

        freeCells.removeAll(snakeElements);

        Random rand = new Random();

        int randomIndex = rand.nextInt(freeCells.size());
        position = freeCells.get(randomIndex);
    }

    public boolean isEaten(Vector2 snakeHead) {
        return snakeHead.x == position.x && snakeHead.y == position.y;
    }

    public void draw(Graphics g, int elementSize, int drawOffset) {
        g.drawImage(textureManager.APPLE_IMAGE, position.x * elementSize, position.y * elementSize + drawOffset, elementSize, elementSize, null);
    }
}
