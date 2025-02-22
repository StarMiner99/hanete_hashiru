import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Main {

    public static final String originalMap = "assets/world/originalLevel.world";
    public static final String worldSave = "saves/savedLevel.world";
    private static Image backgroundImage;
    private static JPanel menuPanel;

    public static void main(String[] args) throws InterruptedException {
        File save = new File(worldSave);

        if (!save.exists()) { // create a new save file if not already existing
            try {
                Files.copy(new File(originalMap).toPath(),
                        new File(worldSave).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to copy world file. Working Directory wrong or files missing!");
                System.err.println("Current Working Dir: " + System.getProperty("user.dir"));
                System.err.println("Please Change the Working Directory in your launch options to the project root and make sure the directory world/ exists!");
                System.exit(-1);
            }
        }



        JFrame mainWindow = new JFrame("跳ねて走る"); // "Hanete Hashiru" Japanese for Jump and Run
        mainWindow.setSize(mainWindow.getMaximumSize());
        mainWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainWindow.setUndecorated(true);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            backgroundImage = ImageIO.read(new File("assets/mainMenuBackground.png"));
        } catch (IOException e) {
            System.err.println("Background Image not Found!");
        }
        menuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                double scale = Math.min((double)menuPanel.getWidth()/(double)backgroundImage.getWidth(null),(double)menuPanel.getHeight()/(double)backgroundImage.getHeight(null));
                super.paintComponent(g);
                g.drawImage(backgroundImage, 30, 0, (int) (scale*backgroundImage.getWidth(null)), (int) (scale*backgroundImage.getHeight(null)),null);
            }
        };
        mainWindow.getContentPane().add(menuPanel);

        GameGrid gameGrid = LevelLoader.loadWorld(worldSave);

        JButton startButton = new JButton("Start!");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

        startButton.setBorder(new Border() { // rounded corners
            private static final int radius = 15;
            @Override
            public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
                graphics.drawRoundRect(x, y, width-1, height-1, radius, radius);
            }

            @Override
            public Insets getBorderInsets(Component component) {
                return new Insets(radius+1, radius+1, radius+2, radius);
            }

            @Override
            public boolean isBorderOpaque() {
                return true;
            }
        });
        startButton.addActionListener(e -> {
            mainWindow.getContentPane().removeAll();
            mainWindow.getContentPane().revalidate();
            mainWindow.getContentPane().repaint();

            mainWindow.getContentPane().add(gameGrid);
            mainWindow.requestFocus();

        });

        menuPanel.add(startButton);

        mainWindow.setVisible(true);
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);

                LevelLoader.saveWorld(gameGrid, worldSave);
            }
        });


        boolean[] sigExit = {false, false};
        boolean[] keys = new boolean[3];
        boolean[] specialKeys = new boolean[2];
        mainWindow.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                sigExit[0] = e.getKeyCode() == KeyEvent.VK_ESCAPE;
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    PlayerHelper.dash();
                }
                if (e.getKeyCode() == KeyEvent.VK_F3) {
                    specialKeys[0] = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_U) {
                    specialKeys[1] = true;
                }
                switch (e.getKeyChar()) {
                    case 'o':
                    case 'O':
                        gameGrid.particlesAmount++;
                        break;
                    case 'p':
                    case 'P':
                        gameGrid.particlesAmount--;
                        break;
                    case 'a':
                    case 'A':
                        keys[0] = true;
                        break;
                    case 'd':
                    case 'D':
                        keys[1] = true;
                        break;
                    case ' ':
                    case 'w':
                    case 'W':
                        PlayerHelper.jump();
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case 'a':
                    case 'A':
                        keys[0] = false;
                        break;
                    case 'd':
                    case 'D':
                        keys[1] = false;
                        break;
                }
            }
        });

        PlayerHelper playerHelper = new PlayerHelper(gameGrid);

        long lastTime = System.currentTimeMillis();
        while (!sigExit[0] && !sigExit[1]) {
            Thread.sleep(1000/60); // speed on which the game should run

            playerHelper.checkForCheckpoint();
            playerHelper.checkForLos();
            sigExit[1] = playerHelper.checkForWin();

            gameGrid.repaint(); // update screen
            gameGrid.showDebugMessage("particles",String.valueOf(gameGrid.ps.particles.size()));
            playerHelper.update(keys); // update player physics
            playerHelper.updateSpecial(specialKeys);

            double fps = 1000.0 /  (System.currentTimeMillis() - lastTime); // calculate real fps
            gameGrid.showDebugMessage("fps",String.valueOf(fps));
            lastTime = System.currentTimeMillis();
        }

        if (sigExit[0]) {
            mainWindow.dispose(); // close window
            LevelLoader.saveWorld(gameGrid, worldSave); // save current state
        } else {
            while (!sigExit[0]) { gameGrid.repaint(); } // repaint to be able to detect esc key press
            mainWindow.dispose();

            try {
                LevelLoader.saveWorld(gameGrid, worldSave + ".bak");
                Files.delete(Paths.get(worldSave));
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed deleting old save");
            }
        }
    }
}