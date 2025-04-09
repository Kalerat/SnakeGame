package org.kgames.snake.game;

import org.kgames.snake.util.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.ArrayDeque;
import java.util.Deque;

public class Snake {
    private LinkedList<Vector2> snakeElements = new LinkedList<>();
    private int velX = 0;
    private int velY = 0;
    private final int boardSize;
    
    // Speed factor (1.0 = normal speed, higher values = faster)
    private double speedFactor = 1.0;

    // Direction queue to handle rapid direction changes
    private Deque<Vector2> directionQueue = new ArrayDeque<>();
    private boolean hasMoved = false;

    private int headDirection = 0;
    private int tailAnimationFrame = 0;

    private boolean isEating = false;
    private int eatingFramesLeft = 0;
    private static final int EATING_ANIMATION_FRAMES = 10;

    private final TextureManager textureManager;

    // Constants for corner type identification
    private static final int CORNER_TOP_RIGHT = 1001;
    private static final int CORNER_TOP_LEFT = 1002;
    private static final int CORNER_BOTTOM_RIGHT = 1003;
    private static final int CORNER_BOTTOM_LEFT = 1004;

    public Snake(int boardSize, TextureManager tm) {
        this.boardSize = boardSize;
        this.textureManager = tm;
        init();
    }

    public void init() {
        snakeElements.clear();
        velX = 0;
        velY = 0;
        directionQueue.clear();
        hasMoved = false;
        headDirection = 0;
        tailAnimationFrame = 0;
        speedFactor = 1.0;
        isEating = false;
        eatingFramesLeft = 0;

        Vector2 head = new Vector2(boardSize / 4, boardSize / 2);
        snakeElements.add(head);
        snakeElements.add(new Vector2(head.x - 1, head.y));
        snakeElements.add(new Vector2(head.x - 2, head.y));
    }

    public void move() {
        if(velX == 0 && velY == 0) return;

        for(int i = snakeElements.size() - 1; i > 0; i--) {
            Vector2 current = snakeElements.get(i);
            Vector2 ahead = snakeElements.get(i-1);
            current.x = ahead.x;
            current.y = ahead.y;
        }

        Vector2 head = snakeElements.getFirst();
        head.x += velX;
        head.y += velY;
        
        // Update head direction based on velocity
        updateHeadDirection();

        tailAnimationFrame = (tailAnimationFrame + 1) % 5; // Cycle through tail animation frames
        // Mark that we've moved in the current direction
        hasMoved = true;
        
        // If we have a queued direction change, apply it now
        if (!directionQueue.isEmpty()) {
            Vector2 nextDir = directionQueue.poll();
            this.velX = nextDir.x;
            this.velY = nextDir.y;
            hasMoved = false; // Reset for the new direction
        }

    }

    public void setEating() {
        isEating = true;
        eatingFramesLeft = EATING_ANIMATION_FRAMES;
    }

    public boolean isEating() {
        return isEating;
    }

    private void updateHeadDirection() {
        if (velX > 0) headDirection = 270;       // Right
        else if (velX < 0) headDirection = 90; // Left
        else if (velY > 0) headDirection = 0;  // Down
        else if (velY < 0) headDirection = 180; // Up
    }

    public boolean checkCollision() {
        Vector2 head = snakeElements.getFirst();

        if (head.x >= boardSize || head.x < 0 || head.y >= boardSize || head.y < 0) {
            return true;
        }
        for (int i = 1; i < snakeElements.size(); i++) {
            Vector2 segment = snakeElements.get(i);
            if (head.x == segment.x && head.y == segment.y) {
                return true;
            }
        }
        
        return false;
    }

    public void grow() {
        Vector2 tail = snakeElements.getLast();
        snakeElements.addLast(new Vector2(tail.x, tail.y));
    }

    public void setDirection(int velX, int velY) {
        // If there's no change in direction, ignore the input
        if (this.velX == velX && this.velY == velY) return;
        
        // Prevent 180-degree turns
        if ((this.velX != 0 && velX == -this.velX) || 
            (this.velY != 0 && velY == -this.velY)) {
            return;
        }
        
        // If we haven't moved since last direction change, add to queue
        // This prevents rapid direction changes causing 180-degree turns
        if (!hasMoved && (this.velX != 0 || this.velY != 0)) {
            // Check if this would create an illegal turn from the queued direction
            if (!directionQueue.isEmpty()) {
                Vector2 lastQueued = directionQueue.peekLast();
                if ((lastQueued.x != 0 && velX == -lastQueued.x) || 
                    (lastQueued.y != 0 && velY == -lastQueued.y)) {
                    return;
                }
            }
            
            // Only keep the most recent direction change
            if (!directionQueue.isEmpty()) {
                directionQueue.clear();
            }
            directionQueue.add(new Vector2(velX, velY));
            return;
        }
        
        // Apply the direction change immediately if we've moved since last change
        this.velX = velX;
        this.velY = velY;
        hasMoved = false;
    }

    public Vector2 getHead() {
        return snakeElements.getFirst();
    }

    public LinkedList<Vector2> getSnakeElements() {
        return snakeElements;
    }

    private int calculateBodySegmentAngle(Vector2 before, Vector2 current, Vector2 after) {
        // Determine the direction vectors
        int beforeDirX = current.x - before.x;
        int beforeDirY = current.y - before.y;
        int afterDirX = after.x - current.x;
        int afterDirY = after.y - current.y;
        
        // Determine if there's a turn (before and after have different directions)
        boolean isTurn = (beforeDirX != afterDirX || beforeDirY != afterDirY);
        
        // Default angle
        int angle = 0;
        
        if (isTurn) {
            if (beforeDirX > 0) { // Moving right then...
                if (afterDirY < 0) { // ... up = top-left corner
                    return CORNER_TOP_LEFT;
                } else if (afterDirY > 0) { // ... down = bottom-left corner
                    return CORNER_BOTTOM_LEFT;
                }
            } else if (beforeDirX < 0) { // Moving left then...
                if (afterDirY < 0) { // ... up = top-right corner
                    return CORNER_TOP_RIGHT;
                } else if (afterDirY > 0) { // ... down = bottom-right corner
                    return CORNER_BOTTOM_RIGHT;
                }
            } else if (beforeDirY < 0) { // Moving up then...
                if (afterDirX > 0) { // ... right = bottom-right corner
                    return CORNER_BOTTOM_RIGHT;
                } else if (afterDirX < 0) { // ... left = bottom-left corner
                    return CORNER_BOTTOM_LEFT;
                }
            } else if (beforeDirY > 0) { // Moving down then...
                if (afterDirX > 0) { // ... right = top-right corner
                    return CORNER_TOP_RIGHT;
                } else if (afterDirX < 0) { // ... left = top-left corner
                    return CORNER_TOP_LEFT;
                }
            }
        } else {
            if (beforeDirX != 0 || afterDirX != 0) {
                angle = (beforeDirX > 0 || afterDirX > 0) ? 0 : 180;  // Horizontal
            } else if (beforeDirY != 0 || afterDirY != 0) {
                angle = (beforeDirY > 0 || afterDirY > 0) ? 90 : 270;  // Vertical
            }
        }
        
        return angle;
    }
    
    // Calculate the appropriate rotation angle for the tail
    private int calculateTailAngle(Vector2 secondLast, Vector2 last) {
        if (secondLast.x < last.x) return 90;  // Tail points left
        if (secondLast.x > last.x) return 270; // Tail points right
        if (secondLast.y < last.y) return 180; // Tail points up
        if (secondLast.y > last.y) return 0;   // Tail points down
        
        return 0;
    }

    public void draw(Graphics g, int elementSize, int drawOffset) {
        Graphics2D g2d = (Graphics2D) g;

        Vector2 head = snakeElements.getFirst();
        BufferedImage headImage = isEating ?
                                  textureManager.SNAKE_HEAD_EATING_IMAGE :
                                  textureManager.SNAKE_HEAD_IMAGE;

        drawRotatedImage(g2d, headImage, head.x * elementSize,
                         head.y * elementSize + drawOffset, elementSize, elementSize, headDirection);

        if (isEating) {
            eatingFramesLeft--;
            if (eatingFramesLeft <= 0) {
                isEating = false;
            }
        }

        for (int i = 1; i < snakeElements.size() - 1; i++) {
            Vector2 segment = snakeElements.get(i);
            Vector2 before = snakeElements.get(i - 1);
            Vector2 after = snakeElements.get(i + 1);

            int bodyAngleOrCornerType = calculateBodySegmentAngle(before, segment, after);

            BufferedImage bodyImage;
            int rotationAngle = 0;

            // Check if this is a corner segment
            if (bodyAngleOrCornerType >= 1000) {
                // Use the appropriate corner texture and rotation based on the corner type
                rotationAngle = switch (bodyAngleOrCornerType) {
                    case CORNER_TOP_LEFT -> {
                        bodyImage = textureManager.SNAKE_BODY_CORNER_LEFT;
                        yield 180;
                    }
                    case CORNER_TOP_RIGHT -> {
                        bodyImage = textureManager.SNAKE_BODY_CORNER_RIGHT;
                        yield 180;
                    }
                    case CORNER_BOTTOM_LEFT -> {
                        bodyImage = textureManager.SNAKE_BODY_CORNER_LEFT;
                        yield 90;
                    }
                    case CORNER_BOTTOM_RIGHT -> {
                        bodyImage = textureManager.SNAKE_BODY_CORNER_RIGHT;
                        yield 270;
                    }
                    default -> {
                        bodyImage = i % 2 == 0 ?
                                textureManager.SNAKE_BODY_IMAGE : textureManager.SNAKE_BODY_IMAGE_2;
                        yield 0;
                    }
                };
            } else {
                // For straight segments, use alternating body textures
                bodyImage = i % 2 == 0 ?
                    textureManager.SNAKE_BODY_IMAGE : textureManager.SNAKE_BODY_IMAGE_2;
                rotationAngle = bodyAngleOrCornerType;
            }

            drawRotatedImage(g2d, bodyImage, segment.x * elementSize,
                          segment.y * elementSize + drawOffset, 
                          elementSize, elementSize, rotationAngle);
        }
        
        // Draw the tail segment if snake has at least 2 segments
        if (snakeElements.size() >= 2) {
            int lastIndex = snakeElements.size() - 1;
            Vector2 tail = snakeElements.getLast();
            Vector2 secondLast = snakeElements.get(lastIndex - 1);

            int tailAngle = calculateTailAngle(secondLast, tail);

            BufferedImage tailImage = switch (tailAnimationFrame) {
                case 0 -> textureManager.SNAKE_TAIL_IMAGE_1;
                case 1 -> textureManager.SNAKE_TAIL_IMAGE_2;
                case 2 -> textureManager.SNAKE_TAIL_IMAGE_3;
                case 3 -> textureManager.SNAKE_TAIL_IMAGE_4;
                case 4 -> textureManager.SNAKE_TAIL_IMAGE_5;
                default -> textureManager.SNAKE_TAIL_IMAGE_1;
            };

            drawRotatedImage(g2d, tailImage, tail.x * elementSize,
                           tail.y * elementSize + drawOffset, 
                           elementSize, elementSize, tailAngle);
        }
    }

    private void drawRotatedImage(Graphics2D g2d, BufferedImage image, int x, int y,
                                 int width, int height, int angleDegrees) {
        AffineTransform oldTransform = g2d.getTransform();

        AffineTransform transform = new AffineTransform();
        transform.translate(x + width/2, y + height/2);
        transform.rotate(Math.toRadians(angleDegrees));
        transform.translate(-width/2, -height/2);

        g2d.setTransform(transform);
        g2d.drawImage(image, 0, 0, width, height, null);

        g2d.setTransform(oldTransform);
    }

    public double getSpeedFactor() {
        return speedFactor;
    }

    public void setSpeedFactor(double speedFactor) {
        this.speedFactor = speedFactor;
    }
}
