package view;

import model.AIDifficulty;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainView extends JFrame {
    private User currentUser;
    private final AuthFrame authFrame;
    private final InscriFrame inscriFrame;
    private final JPanel mainMenuPanel;
    private final JComboBox<AIDifficulty> difficultyCombo;
    private final JLabel welcomeLabel;
    public final JPanel cards;
    public final CardLayout cardLayout;
    private Image backgroundImage;

    public MainView() {
        setTitle("Checkers Game");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Changed from EXIT_ON_CLOSE
        setSize(900, 700);
        setLocationRelativeTo(null);

        backgroundImage = new ImageIcon(getClass().getResource("/images/bg.png")).getImage();

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        authFrame = new AuthFrame();
        inscriFrame = new InscriFrame();
        mainMenuPanel = new BackgroundPanel();
        mainMenuPanel.setLayout(new GridBagLayout());

        cards.add(authFrame, "auth");
        cards.add(inscriFrame, "register");
        cards.add(mainMenuPanel, "mainMenu");
        add(cards);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        welcomeLabel = new JLabel("Welcome!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Verdana", Font.BOLD, 36));
        welcomeLabel.setForeground(new Color(0, 255, 255));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainMenuPanel.add(welcomeLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel difficultyLabel = new JLabel("Difficulty:");
        difficultyLabel.setForeground(Color.WHITE);
        difficultyLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
        mainMenuPanel.add(difficultyLabel, gbc);

        difficultyCombo = new JComboBox<>(AIDifficulty.values());
        difficultyCombo.setFont(new Font("Verdana", Font.PLAIN, 20));
        difficultyCombo.setBackground(new Color(50, 50, 50));
        difficultyCombo.setForeground(Color.WHITE);
        gbc.gridx = 1;
        mainMenuPanel.add(difficultyCombo, gbc);

        JButton newGameButton = createStyledButton("New Game");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainMenuPanel.add(newGameButton, gbc);

        JButton statsButton = createStyledButton("View Stats");
        gbc.gridy = 3;
        mainMenuPanel.add(statsButton, gbc);

        newGameButton.addActionListener(e -> fireNewGame());
        statsButton.addActionListener(e -> fireStats());
    }

    public void showStats(User user) {
        StatsPanel statsPanel = new StatsPanel(user);
        cards.add(statsPanel, "stats");
        cardLayout.show(cards, "stats");
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Verdana", Font.BOLD, 24));
        button.setBackground(new Color(0, 150, 150));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 255), 2));
        button.setPreferredSize(new Dimension(250, 60));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 200, 200));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 150, 150));
            }
        });
        return button;
    }

    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    private class StatsPanel extends JPanel {
        public StatsPanel(User user) {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel titleLabel = new JLabel("Player Stats", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Verdana", Font.BOLD, 36));
            titleLabel.setForeground(new Color(0, 255, 255));
            gbc.gridx = 0;
            gbc.gridy = 0;
            add(titleLabel, gbc);

            JTextArea statsArea = new JTextArea();
            statsArea.setFont(new Font("Verdana", Font.PLAIN, 18));
            statsArea.setBackground(new Color(30, 30, 30));
            statsArea.setForeground(Color.WHITE);
            statsArea.setEditable(false);
            StringBuilder stats = new StringBuilder();
            stats.append("Username: ").append(user.getUsername()).append("\n")
                 .append("Total Games: ").append(user.getTotalGames()).append("\n")
                 .append("Wins: ").append(user.getWins()).append("\n")
                 .append("Losses: ").append(user.getLosses()).append("\n")
                 .append("Win Percentage: ").append(String.format("%.2f%%", user.getWinPercentage())).append("\n")
                 .append("Games by Difficulty:\n");
            user.getGamesByDifficulty().forEach((diff, count) ->
                stats.append(diff).append(": ").append(count).append("\n"));
            statsArea.setText(stats.toString());
            gbc.gridy = 1;
            add(statsArea, gbc);

            JButton backButton = createStyledButton("Back");
            backButton.addActionListener(e -> cardLayout.show(cards, "mainMenu"));
            gbc.gridy = 2;
            add(backButton, gbc);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public AuthFrame getAuthFrame() { return authFrame; }
    public InscriFrame getInscriFrame() { return inscriFrame; }
    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) { this.currentUser = user; }

    public AIDifficulty getSelectedDifficulty() {
        return (AIDifficulty) difficultyCombo.getSelectedItem();
    }

    public void setWelcomeMessage(String message) {
        welcomeLabel.setText(message);
    }

    public void showMainMenu() {
        cardLayout.show(cards, "mainMenu");
    }

    public void showGamePanel(GameView gameView) {
        cards.add(gameView, "game");
        cardLayout.show(cards, "game");
    }

    public GameView getGameView() {
        for (Component comp : cards.getComponents()) {
            if (comp instanceof GameView) return (GameView) comp;
        }
        return null;
    }

    public void addNewGameListener(ActionListener listener) {
        for (Component comp : mainMenuPanel.getComponents()) {
            if (comp instanceof JButton btn && btn.getText().equals("New Game")) {
                btn.addActionListener(listener);
            }
        }
    }

    public void addStatsListener(ActionListener listener) {
        for (Component comp : mainMenuPanel.getComponents()) {
            if (comp instanceof JButton btn && btn.getText().equals("View Stats")) {
                btn.addActionListener(listener);
            }
        }
    }

    private void fireNewGame() {
        for (ActionListener listener : getListeners(ActionListener.class)) {
            if (listener.toString().contains("MainController")) {
                listener.actionPerformed(null);
            }
        }
    }

    private void fireStats() {
        StatsPanel statsPanel = new StatsPanel(currentUser);
        cards.add(statsPanel, "stats");
        cardLayout.show(cards, "stats");
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}