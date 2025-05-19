import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

public class DinoGame extends JPanel implements ActionListener, KeyListener {
    private static final int WIDTH = 800, HEIGHT = 600;
    private Rectangle dino;
    private int velY = 0;
    private boolean onGround = true;
    private int gravity = 1;
    private BufferedImage dinoImg;
    private BufferedImage obstacleImg;
    private ArrayList<Rectangle> obstacles = new ArrayList<>();
    private int obstacleTimer = 0;
    private int speed = 6;
    private int speedTimer = 0;
    private int score = 0;
    private Timer timer;
    private static final int OBSTACLE_WIDTH = 30;
    private static final int OBSTACLE_HEIGHT = 30;

    public DinoGame() {
        dino = new Rectangle(100, HEIGHT - 150, 50, 50);
        try {
            dinoImg = ImageIO.read(new File("Downloads/Dino.png"));
            dinoImg = resizeImage(dinoImg, dino.width, dino.height);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            obstacleImg = ImageIO.read(new File("Downloads/Cacti.png"));
            obstacleImg = resizeImage(obstacleImg, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setBackground(Color.WHITE);
        timer = new Timer(16, this); // ~60 FPS
        timer.start();
        setFocusable(true);
        addKeyListener(this);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        return resizedImage;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        if (dinoImg != null) {
            g.drawImage(dinoImg, dino.x, dino.y, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(dino.x, dino.y, dino.width, dino.height);
        }

        for (Rectangle obs : obstacles) {
            if (obstacleImg != null) {
                g.drawImage(obstacleImg, obs.x, obs.y, obs.width, obs.height, null);
            } else {
                g.setColor(new Color(139, 69, 19)); // brown
                g.fillRect(obs.x, obs.y, obs.width, obs.height);
            }
        }

        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, 10, 20);
    }

    public void actionPerformed(ActionEvent e) {
        // Gravity & movement
        if (!onGround) {
            velY += gravity;
        }
        dino.y += velY;

        if (dino.y >= HEIGHT - 100) {
            dino.y = HEIGHT - 100;
            velY = 0;
            onGround = true;
        }

        // Spawn obstacles every ~0.75 sec
        obstacleTimer++;
        if (obstacleTimer > 45) {
            Random rand = new Random();
            int width = rand.nextInt(31) + 20; // 20-50 px width
            int y = HEIGHT - 100; // ground level
            obstacles.add(new Rectangle(WIDTH, y, width, OBSTACLE_HEIGHT));
            obstacleTimer = 0;
        }

        // Move obstacles left
        for (int i = 0; i < obstacles.size(); i++) {
            Rectangle obs = obstacles.get(i);
            obs.x -= speed;
            if (obs.x + obs.width < 0) {
                obstacles.remove(i);
                i--;
            }
        }

        // Collision check - Game over on any contact with obstacle
        for (Rectangle obs : obstacles) {
            if (dino.intersects(obs)) {
                JOptionPane.showMessageDialog(this, "Game Over!", "Game Over", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }

        if (!onGround && dino.y >= HEIGHT - 100) {
            onGround = true;
        }

        // Speed up every 3 seconds
        speedTimer++;
        if (speedTimer >= 180) {
            speed++;
            speedTimer = 0;
        }

        // Increase score
        score++;

        repaint();
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && onGround) {
            velY = -18;
            onGround = false;
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Dino Game");
        DinoGame game = new DinoGame();
        frame.add(game);
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
