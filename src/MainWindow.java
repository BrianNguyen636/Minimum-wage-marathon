import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

public class MainWindow extends JFrame {

    public MainWindow() {
        setVisible(true);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        var menuPanel = mainMenu();

        add(menuPanel);
        pack();
    }


    private JPanel mainMenu() {
        var menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        var textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        var title = new JLabel("Minimum-wage Marathon");
        title.setFont(new Font("Serif",Font.PLAIN,60));
        var subTitle = new JLabel("Pay your bills without the skills!");
        subTitle.setFont(new Font("Serif",Font.PLAIN,20));
        var author = new JLabel("by Brian Nguyen");
        author.setFont(new Font("Serif",Font.PLAIN,20));

        textPanel.add(title);
        textPanel.add(subTitle);
        textPanel.add(author);

        var buttonPanel = new JPanel();

        var startButton = new JButton("Start Game");
        var helpButton = new JButton("How to Play");

        startButton.addActionListener(e -> {
            var gaming = new gameWindow();
            gaming.setVisible(true);
        });

        String text = "Use the keys [D-F-J-K] to press the squares. \n" + "Press the black squares to earn money, " +
                "but if you press the wrong square you will lose money " +
                "to unforeseen circumstances.\n" + "The money you lose increases with each penalty. " +
                "You have to pay your bills every 10 seconds. If you don't meet the bill, it's game over." +
                "\nYour debt interest makes your bills more expensive" +
                " with each one you pay, however every bill you pay decreases your penalty.";
        helpButton.addActionListener(e -> javax.swing.JOptionPane.showMessageDialog(null, text,
                "How to Play", JOptionPane.PLAIN_MESSAGE, new ImageIcon("board.png")));

        buttonPanel.add(startButton);
        buttonPanel.add(helpButton);

        menuPanel.add(textPanel);
        menuPanel.add(buttonPanel);

        return menuPanel;
    }
}

class gameWindow extends JFrame {
    public static List<Integer> correct = new LinkedList<>();
    private static final List<JLabel> squares = new ArrayList<>(20);
    public static Random random = new Random();
    private static int totalEarned = 0, score = 0, totalPenalty = 0, penalty = 50, totalBill = 0,
                                bill = 600, seconds = 0, milli = 0;
    private static JLabel scoreLabel, penaltyLabel, timeLabel, billLabel;
    private static Timer timer;
    public static boolean gameActive = true;

    public gameWindow() {
        setTitle("Minimum-Wage Marathon");
        setVisible(false);
        setFocusable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        var main = new JPanel();
        add(main);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.add(textPanel());
        GameBoard gameBoard = new GameBoard();
        main.add(gameBoard);
        var theTime = new Time();
        var t = new Thread(theTime);
        t.start();
        addKeyListener(new Key());
        pack();
    }
    private JPanel textPanel() {
        JPanel text = new JPanel();
        text.setLayout(new GridLayout(2,2));

        timeLabel = new JLabel("Next bill in: ");
        scoreLabel = new JLabel("Wallet: $" + score);
        penaltyLabel = new JLabel("Penalty: -$" + penalty);
        billLabel = new JLabel("Next bill: $" + bill);

        List<JLabel> labelList = Arrays.asList(timeLabel, scoreLabel, penaltyLabel, billLabel);
        for (JLabel l : labelList) {
            l.setFont(new Font("Serif",Font.PLAIN,30));
            text.add(l);
        }
        return text;
    }

    private static class GameBoard extends JPanel {

        public GameBoard() {
            setLayout(new GridLayout(5, 4));

            for (int i = 0; i < 20; i++) {
                var square = new JLabel();
                squares.add(square);
                add(square);
            }
            for (int row = 0; row < 5; row++) {
                int rand = random.nextInt(4);
                correct.add(rand);
            }
            writeBoard();
        }


    }
    public static void writeBoard() {
        var blackTile = new ImageIcon("black.png");
        var whiteTile = new ImageIcon("white.png");
        if (gameActive) {
            for (JLabel t : squares) t.setIcon(whiteTile);
            for (int row = 0; row < 5; row++) {
                int x = row * 4 + correct.get(4 - row);
                squares.get(x).setIcon(blackTile);
            }
        }
    }

    public static void setBill(int amount) {
        bill += amount;
        billLabel.setText("Next bill: $" + bill);
    }
    public static void setScore(int points) {
        if (gameActive) {
            score += points;
            totalEarned += points;
            scoreLabel.setText("Wallet: $" + score);
        }
    }
    public static void tickPenalty() {
        if (gameActive) {
            setScore(-penalty);
            totalPenalty += penalty;
            penalty += 20;
            penaltyLabel.setText("Penalty: -$" + penalty);
        }
    }
    public static void setTime() {
        DecimalFormat df = new DecimalFormat("00");
        timeLabel.setText("Time: " + seconds + "." + df.format(milli));
        if (seconds > 7) {
            timeLabel.setForeground(Color.RED);
        } else {
            timeLabel.setForeground(Color.black);
        }
    }

    public static class Time implements Runnable {

        @Override
        public void run() {
            timer = new Timer(10, e -> {
                    milli++;
                    if (milli == 100) {
                        milli = 0;
                        seconds++;
                    }
                    if (seconds == 10) {
                        if (score >= bill) {
                            totalBill += bill;
                            setBill(100);
                            seconds = 0;
                            score -= 600;;
                            penalty -= 40;
                            if (penalty < 50) penalty = 50;
                        } else {
                            timer.stop();
                            gameActive = false;
                            javax.swing.JOptionPane.showMessageDialog(null,
                                    "GAME OVER: You were $" + (bill - score) + " short of your bill. \n" +
                                    "Total Money Earned: $" + totalEarned + "\nTotal Bills Paid: $" + totalBill +
                                            "\nTotal Incurred Penalty: -$" + totalPenalty +
                                    "\nDid you know?: " + Facts.fact());
                        }
                    }
                    setTime();

            });
            timer.start();
        }

    }

    public static class Key implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {

            char dfjk = e.getKeyChar();
            int pressed = 5;
            switch (dfjk) {
                case ('d'):
                    pressed = 0;
                    break;
                case ('f'):
                    pressed = 1;
                    break;
                case ('j'):
                    pressed = 2;
                    break;
                case ('k'):
                    pressed = 3;
                    break;
            }

            if (pressed == correct.get(0)) {
                correct.remove(0);
                correct.add(random.nextInt(4));
                writeBoard();
                setScore(10);
            } else {
                tickPenalty();
            }

        }
        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}
