package org.graphics;

import org.game.Game;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.VolatileImage;

public class Renderer {

    private static Frame frame;
    private static Canvas canvas;

    private static int canvasWidth = 0;
    private static int canvasHeight = 0;

    private static final int GAME_WIDTH = 400;
    private static final int GAME_HEIGHT = 250;

    private static int gameWidth = 0;
    private static int gameHeight = 0;


    private static void getBestSize() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        boolean done = false;

        while (!done) {
            canvasWidth += GAME_WIDTH;
            canvasHeight += GAME_HEIGHT;

            if (canvasWidth > screenSize.width || canvasHeight > screenSize.height) {
                canvasWidth -= GAME_WIDTH;
                canvasHeight -= GAME_HEIGHT;

                done = true;
            }

        }

        int XDiff = screenSize.width - canvasWidth;
        int YDiff = screenSize.height - canvasHeight;
        int factor = canvasWidth / GAME_WIDTH;

        gameWidth = canvasWidth / factor + XDiff / factor;
        gameHeight = canvasHeight / factor + YDiff / factor;

        canvasWidth = gameWidth * factor;
        canvasHeight = gameHeight * factor;

    }

    private static void makeFullScreen() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = env.getDefaultScreenDevice();

        if (gd.isFullScreenSupported()) {
            frame.setUndecorated(true);
            gd.setFullScreenWindow(frame);
        }
    }

    public static void init() {
        getBestSize();

        frame = new Frame();
        canvas = new Canvas();

        canvas.setPreferredSize(new Dimension(canvasWidth, canvasHeight));

        frame.add(canvas);

        makeFullScreen();

        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);


        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                Game.quit();
            }


        });

        frame.setVisible(true);

        startRendering();

    }

    private static void startRendering() {
        Thread thread = new Thread() {
            public void run() {

                GraphicsConfiguration gc = canvas.getGraphicsConfiguration();
                VolatileImage vImage = gc.createCompatibleVolatileImage(gameWidth, gameHeight);

                while (true) {
                    if (vImage.validate(gc) == VolatileImage.IMAGE_INCOMPATIBLE) {
                        vImage = gc.createCompatibleVolatileImage(gameWidth, gameHeight);
                    }

                    Graphics g = vImage.getGraphics();

                    //Start//

                    g.setColor(Color.black);
                    g.fillRect(0, 0, gameWidth, gameHeight);

                    //end//

                    g.dispose();

                    g = canvas.getGraphics();
                    g.drawImage(vImage, 0, 0, canvasWidth, canvasHeight, null);

                    g.dispose();


                }
            }
        };
        thread.setName("Render Thread");
        thread.start();
    }
}
