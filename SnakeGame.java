import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {

    final int WIDTH = 800;
    final int HEIGHT = 600;
    final int UNIT = 20;
    final int DELAY = 100;

    int[] x = new int[1000];
    int[] y = new int[1000];

    int bodyParts;
    int score;
    int foodCount;

    int foodX;
    int foodY;

    boolean bonusFood;

    char direction;
    boolean running;

    Timer timer;
    Random random;

    JButton startButton;
    JButton restartButton;

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        setLayout(null);

        random = new Random();

        startButton = new JButton("START");
        startButton.setBounds(WIDTH / 2 - 70, HEIGHT / 2 - 30, 140, 50);
        add(startButton);

        restartButton = new JButton("RESTART");
        restartButton.setBounds(WIDTH / 2 - 70, HEIGHT / 2 + 40, 140, 50);
        restartButton.setVisible(false);
        add(restartButton);

        startButton.addActionListener(e -> startGame());

        restartButton.addActionListener(e -> {
            restartButton.setVisible(false);
            startGame();
        });
    }

    void startGame() {
        bodyParts = 5;
        score = 0;
        foodCount = 0;
        direction = 'R';

        for (int i = 0; i < bodyParts; i++) {
            x[i] = 200 - i * UNIT;
            y[i] = 200;
        }

        spawnFood();

        running = true;

        startButton.setVisible(false);

        if (timer != null) timer.stop();

        timer = new Timer(DELAY, this);
        timer.start();

        requestFocusInWindow();
    }

    void spawnFood() {
        foodCount++;
        bonusFood = foodCount % 10 == 0;

        foodX = random.nextInt(WIDTH / UNIT) * UNIT;
        foodY = random.nextInt(HEIGHT / UNIT) * UNIT;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!running && score == 0 && startButton.isVisible()) {
            drawStartScreen(g);
            return;
        }

        drawGame(g);

        if (!running && score > 0) {
            drawGameOver(g);
        }
    }

    void drawStartScreen(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        drawCentered(g, "SNAKE GAME", HEIGHT / 3);
    }

    void drawGame(Graphics g) {

        if (bonusFood) {
            g.setColor(Color.RED);
            g.fillOval(foodX - 5, foodY - 5, UNIT + 10, UNIT + 10);
        } else {
            g.setColor(Color.WHITE);
            g.fillOval(foodX + 5, foodY + 5, 10, 10);
        }

        for (int i = 0; i < bodyParts; i++) {
            g.setColor(Color.RED);
            g.fillRect(x[i], y[i], UNIT, UNIT);

            if (i == 0) {
                drawEyes(g);
            }
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("Score : " + score, 20, 35);
    }

    void drawEyes(Graphics g) {
        g.setColor(Color.WHITE);

        switch (direction) {
            case 'R':
                g.fillOval(x[0] + 11, y[0] + 3, 5, 5);
                g.fillOval(x[0] + 11, y[0] + 12, 5, 5);
                break;

            case 'L':
                g.fillOval(x[0] + 3, y[0] + 3, 5, 5);
                g.fillOval(x[0] + 3, y[0] + 12, 5, 5);
                break;

            case 'U':
                g.fillOval(x[0] + 3, y[0] + 3, 5, 5);
                g.fillOval(x[0] + 12, y[0] + 3, 5, 5);
                break;

            case 'D':
                g.fillOval(x[0] + 3, y[0] + 12, 5, 5);
                g.fillOval(x[0] + 12, y[0] + 12, 5, 5);
                break;
        }
    }

    void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U': y[0] -= UNIT; break;
            case 'D': y[0] += UNIT; break;
            case 'L': x[0] -= UNIT; break;
            case 'R': x[0] += UNIT; break;
        }
    }

    void checkFood() {
        if (Math.abs(x[0] - foodX) < UNIT && Math.abs(y[0] - foodY) < UNIT) {
            bodyParts++;

            if (bonusFood)
                score += 5;
            else
                score++;

            spawnFood();
        }
    }

    void checkCollision() {

        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }

        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
            restartButton.setVisible(true);
        }
    }

    void drawGameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        drawCentered(g, "GAME OVER", HEIGHT / 2 - 60);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        drawCentered(g, "Final Score : " + score, HEIGHT / 2);
    }

    void drawCentered(Graphics g, String text, int yPos) {
        FontMetrics metrics = g.getFontMetrics();
        int xPos = (WIDTH - metrics.stringWidth(text)) / 2;
        g.drawString(text, xPos, yPos);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollision();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {

            case KeyEvent.VK_LEFT:
                if (direction != 'R') direction = 'L';
                break;

            case KeyEvent.VK_RIGHT:
                if (direction != 'L') direction = 'R';
                break;

            case KeyEvent.VK_UP:
                if (direction != 'D') direction = 'U';
                break;

            case KeyEvent.VK_DOWN:
                if (direction != 'U') direction = 'D';
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {

        JFrame frame = new JFrame("Snake Game");
        

        SnakeGame game = new SnakeGame();

        frame.add(game);
        frame.pack();

        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}