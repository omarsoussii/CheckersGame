package view;

import controller.GameController;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.logging.Logger;

public class GameView extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(GameView.class.getName());
    private static final int SQUARE_SIZE = 80;
    private static final int BOARD_SIZE = Board.SIZE * SQUARE_SIZE;
    private static final int PANEL_WIDTH = BOARD_SIZE + 300;
    private static final int PANEL_HEIGHT = BOARD_SIZE + 60; // Increased to accommodate quit button
    private static final int BOARD_X_OFFSET = (PANEL_WIDTH - BOARD_SIZE) / 2;
    private GameController controller;
    private Point selectedSquare;
    private List<Move> validMoves;
    private Image playerNormal, playerKing, aiNormal, aiKing;

    public GameView() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(new BorderLayout());
        validMoves = List.of();

        playerNormal = new ImageIcon(getClass().getResource("/images/user.png")).getImage();
        playerKing = new ImageIcon(getClass().getResource("/images/userK.png")).getImage();
        aiNormal = new ImageIcon(getClass().getResource("/images/ai.png")).getImage();
        aiKing = new ImageIcon(getClass().getResource("/images/aiK.png")).getImage();

        // Add mouse listener for board interaction
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = e.getY() / SQUARE_SIZE;
                int col = (e.getX() - BOARD_X_OFFSET) / SQUARE_SIZE;
                if (col >= 0 && col < Board.SIZE && row >= 0 && row < Board.SIZE) {
                    if (selectedSquare == null) {
                        controller.handleSquareClick(row, col);
                    } else {
                        controller.handleMove(selectedSquare.x, selectedSquare.y, row, col);
                        selectedSquare = null;
                    }
                }
            }
        });

        // Add quit button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton quitButton = createStyledButton("Quit");
        quitButton.addActionListener(e -> {
            if (controller.getGameState() != null && !controller.getGameState().isGameOver()) {
                int choice = JOptionPane.showConfirmDialog(this, "Do you want to save the game before quitting?", "Save Game", JOptionPane.YES_NO_CANCEL_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    controller.saveGameState();
                    returnToMainMenu();
                } else if (choice == JOptionPane.NO_OPTION) {
                    returnToMainMenu();
                }
                // Cancel does nothing
            } else {
                returnToMainMenu();
            }
        });
        buttonPanel.add(quitButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Verdana", Font.BOLD, 18));
        button.setBackground(new Color(0, 150, 150));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 255), 2));
        button.setPreferredSize(new Dimension(150, 40));
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

    private void returnToMainMenu() {
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainView)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainView mainView) {
            mainView.showMainMenu();
        }
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        drawBoard(g);
        drawPieces(g);
        highlightValidMoves(g);
        drawSelectedSquare(g);
        drawCaptureCounters(g);
    }

    private void drawBoard(Graphics g) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                g.setColor((row + col) % 2 == 0 ? new Color(200, 150, 100) : new Color(100, 50, 0));
                g.fillRect(BOARD_X_OFFSET + col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    private void drawPieces(Graphics g) {
        GameState state = controller.getGameState();
        if (state == null) return;
        Board board = state.getBoard();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Piece piece = board.getPiece(new Point(row, col));
                if (piece != null) {
                    Image img = piece.getColor() == PieceColor.WHITE ?
                            (piece instanceof King ? playerKing : playerNormal) :
                            (piece instanceof King ? aiKing : aiNormal);
                    if (img != null) {
                        g.drawImage(img, BOARD_X_OFFSET + col * SQUARE_SIZE + 15, row * SQUARE_SIZE + 15, SQUARE_SIZE - 30, SQUARE_SIZE - 30, this);
                    } else {
                        g.setColor(piece.getColor() == PieceColor.WHITE ? Color.WHITE : Color.BLACK);
                        g.fillOval(BOARD_X_OFFSET + col * SQUARE_SIZE + SQUARE_SIZE / 4, row * SQUARE_SIZE + SQUARE_SIZE / 4, SQUARE_SIZE / 2, SQUARE_SIZE / 2);
                        if (piece instanceof King) {
                            g.setColor(Color.YELLOW);
                            g.drawString("K", BOARD_X_OFFSET + col * SQUARE_SIZE + SQUARE_SIZE / 3, row * SQUARE_SIZE + SQUARE_SIZE / 3);
                        }
                    }
                }
            }
        }
    }

    private void drawSelectedSquare(Graphics g) {
        if (selectedSquare != null) {
            g.setColor(new Color(0, 255, 0, 100));
            g.fillRect(BOARD_X_OFFSET + selectedSquare.y * SQUARE_SIZE, selectedSquare.x * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
        }
    }

    private void highlightValidMoves(Graphics g) {
        g.setColor(new Color(255, 255, 0, 100));
        for (Move move : validMoves) {
            Point to = move.getTo();
            g.fillRect(BOARD_X_OFFSET + to.y * SQUARE_SIZE, to.x * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
        }
    }

    private void drawCaptureCounters(Graphics g) {
        g.setFont(new Font("Verdana", Font.BOLD, 24));
        g.setColor(new Color(0, 255, 255));
        g.drawString("Player: " + controller.getPlayerKills(), 10, BOARD_SIZE / 2);
        g.drawString("AI: " + controller.getAIKills(), BOARD_X_OFFSET + BOARD_SIZE + 10, BOARD_SIZE / 2);
    }

    public void highlightValidMoves(List<Move> moves) {
        this.validMoves = moves;
        selectedSquare = moves.isEmpty() ? null : moves.get(0).getFrom();
        repaint();
    }

    public void clearHighlights() {
        validMoves = List.of();
        selectedSquare = null;
        repaint();
    }

    public void showAIMove(Move move) {
        selectedSquare = move.getFrom();
        validMoves = List.of(move);
        repaint();
        new Thread(() -> {
            try {
                Thread.sleep(500);
                clearHighlights();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void showGameOver(PieceColor winner) {
        String message = winner == PieceColor.WHITE ? "You win!" : "AI wins!";
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    public void updateView() {
        repaint();
    }
}