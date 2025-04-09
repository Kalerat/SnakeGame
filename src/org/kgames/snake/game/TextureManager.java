package org.kgames.snake.game;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class TextureManager {
    public static final String BASE_FILEPATH = "/textures/";

    // Head textures
    public static final String SNAKE_HEAD_TEXTURE_PATH = BASE_FILEPATH + "snake_head.png";
    public static final String SNAKE_HEAD_EATING_TEXTURE_PATH = BASE_FILEPATH + "snake_head_eating.png";
    public static final String SNAKE_HEAD_DEAD_TEXTURE_PATH = BASE_FILEPATH + "snake_head_dead.png";

    // Body textures
    public static final String SNAKE_BODY_TEXTURE_PATH = BASE_FILEPATH + "snake_body.png";
    public static final String SNAKE_BODY_TEXTURE_2_PATH = BASE_FILEPATH + "snake_body_2.png";
    public static final String SNAKE_BODY_CORNER_LEFT_PATH = BASE_FILEPATH + "snake_corner_left.png";
    public static final String SNAKE_BODY_CORNER_RIGHT_PATH = BASE_FILEPATH + "snake_corner_right.png";

    // Tail textures
    public static final String SNAKE_TAIL_TEXTURE_1_PATH = BASE_FILEPATH + "snake_tail_0.png";
    public static final String SNAKE_TAIL_TEXTURE_2_PATH = BASE_FILEPATH + "snake_tail_1.png";
    public static final String SNAKE_TAIL_TEXTURE_3_PATH = BASE_FILEPATH + "snake_tail_2.png";
    public static final String SNAKE_TAIL_TEXTURE_4_PATH = BASE_FILEPATH + "snake_tail_3.png";
    public static final String SNAKE_TAIL_TEXTURE_5_PATH = BASE_FILEPATH + "snake_tail_4.png";

    // Apple texture
    public static final String APPLE_TEXTURE_PATH = BASE_FILEPATH + "apple.png";

    public BufferedImage SNAKE_HEAD_IMAGE;
    public BufferedImage SNAKE_HEAD_EATING_IMAGE;
    public BufferedImage SNAKE_HEAD_DEAD_IMAGE;

    public BufferedImage SNAKE_BODY_IMAGE;
    public BufferedImage SNAKE_BODY_IMAGE_2;
    public BufferedImage SNAKE_BODY_CORNER_LEFT;
    public BufferedImage SNAKE_BODY_CORNER_RIGHT;
    public BufferedImage SNAKE_TAIL_IMAGE_1;
    public BufferedImage SNAKE_TAIL_IMAGE_2;
    public BufferedImage SNAKE_TAIL_IMAGE_3;
    public BufferedImage SNAKE_TAIL_IMAGE_4;
    public BufferedImage SNAKE_TAIL_IMAGE_5;
    public BufferedImage APPLE_IMAGE;

    public boolean init() {
        SNAKE_HEAD_IMAGE = loadImage(SNAKE_HEAD_TEXTURE_PATH);
        SNAKE_HEAD_EATING_IMAGE = loadImage(SNAKE_HEAD_EATING_TEXTURE_PATH);
        SNAKE_HEAD_DEAD_IMAGE = loadImage(SNAKE_HEAD_DEAD_TEXTURE_PATH);

        SNAKE_BODY_IMAGE = loadImage(SNAKE_BODY_TEXTURE_PATH);
        SNAKE_BODY_IMAGE_2 = loadImage(SNAKE_BODY_TEXTURE_2_PATH);
        SNAKE_BODY_CORNER_LEFT = loadImage(SNAKE_BODY_CORNER_LEFT_PATH);
        SNAKE_BODY_CORNER_RIGHT = loadImage(SNAKE_BODY_CORNER_RIGHT_PATH);

        SNAKE_TAIL_IMAGE_1 = loadImage(SNAKE_TAIL_TEXTURE_1_PATH);
        SNAKE_TAIL_IMAGE_2 = loadImage(SNAKE_TAIL_TEXTURE_2_PATH);
        SNAKE_TAIL_IMAGE_3 = loadImage(SNAKE_TAIL_TEXTURE_3_PATH);
        SNAKE_TAIL_IMAGE_4 = loadImage(SNAKE_TAIL_TEXTURE_4_PATH);
        SNAKE_TAIL_IMAGE_5 = loadImage(SNAKE_TAIL_TEXTURE_5_PATH);

        APPLE_IMAGE = loadImage(APPLE_TEXTURE_PATH);


        return SNAKE_HEAD_IMAGE != null && SNAKE_HEAD_EATING_IMAGE != null && SNAKE_HEAD_DEAD_IMAGE != null &&
                SNAKE_BODY_IMAGE != null && SNAKE_BODY_IMAGE_2 != null && SNAKE_BODY_CORNER_LEFT != null && SNAKE_BODY_CORNER_RIGHT != null
                && SNAKE_TAIL_IMAGE_1 != null && SNAKE_TAIL_IMAGE_2 != null && SNAKE_TAIL_IMAGE_3 != null
                && SNAKE_TAIL_IMAGE_4 != null && SNAKE_TAIL_IMAGE_5 != null
                && APPLE_IMAGE != null;
    }

    private BufferedImage loadImage(String imagePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath));
            if (image == null) {
                System.err.println("Failed to load image: " + imagePath);
            }
        } catch (Exception e) {
            System.err.println("Error loading image " + imagePath + ": " + e.getMessage());
            e.printStackTrace();
        }
        return image;
    }
}
