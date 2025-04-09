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
        Vector2 v = (Vector2)o;
        return x == v.x && y == v.y;
    }
}
