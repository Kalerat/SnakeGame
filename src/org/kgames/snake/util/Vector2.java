package org.kgames.snake.util;

public class Vector2 {
    public int x;
    public int y;

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Vector2 v) {
            return x == v.x && y == v.y;
        }
        else {
            return false;
        }
    }
}
