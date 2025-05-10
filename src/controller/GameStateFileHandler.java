package controller;

import model.*;

import java.awt.Point;
import java.io.*;
import java.util.Scanner;
import java.util.logging.Logger;

public class GameStateFileHandler {
    private static final Logger LOGGER = Logger.getLogger(GameStateFileHandler.class.getName());
    private final User currentUser;

    public GameStateFileHandler(User currentUser) {
        this.currentUser = currentUser;
    }

    private String getSaveFilePath() {
        if (currentUser == null) {
            throw new IllegalStateException("Cannot save game state without a logged-in user");
        }
        String userHome = System.getProperty("user.home");
        String path = userHome + "/checkers_data/state_" + currentUser.getUsername() + ".txt";
        LOGGER.info("Save file path: " + new File(path).getAbsolutePath());
        return path;
    }

    public boolean hasSavedGame() {
        if (currentUser == null) {
            return false;
        }
        File file = new File(getSaveFilePath());
        boolean exists = file.exists();
        LOGGER.info("Checking for saved game for user " + currentUser.getUsername() + ": " + exists);
        return exists;
    }

    public void deleteSaveFile() {
        if (currentUser != null) {
            File file = new File(getSaveFilePath());
            if (file.exists()) {
                file.delete();
                LOGGER.info("Save file deleted for user " + currentUser.getUsername());
            }
        }
    }

    public void saveGameState(GameState gameState, int playerKills, int aiKills) {
        if (gameState == null || gameState.isGameOver()) {
            return;
        }

        String path = getSaveFilePath();
        File file = new File(path);
        file.getParentFile().mkdirs(); // Create directories if needed

        try (PrintWriter writer = new PrintWriter(file)) {
            Board board = gameState.getBoard();
            for (int row = 0; row < Board.SIZE; row++) {
                StringBuilder sb = new StringBuilder();
                for (int col = 0; col < Board.SIZE; col++) {
                    Piece piece = board.getPiece(new Point(row, col));
                    if (piece == null) {
                        sb.append(' ');
                    } else if (piece.getColor() == PieceColor.WHITE) {
                        sb.append(piece instanceof King ? 'W' : 'w');
                    } else {
                        sb.append(piece instanceof King ? 'B' : 'b');
                    }
                }
                writer.println(sb.toString());
            }
            writer.println(gameState.getCurrentPlayer().name());
            writer.println(playerKills);
            writer.println(aiKills);
            writer.println(gameState.getDifficulty().name());
            LOGGER.info("Game state saved successfully to " + path);
            if (file.exists()) {
                LOGGER.info("Save file exists after saving.");
            } else {
                LOGGER.severe("Save file does not exist after saving.");
            }
        } catch (FileNotFoundException e) {
            LOGGER.severe("Error saving game state: " + e.getMessage());
        }
    }

    public boolean loadGameState(GameController controller) {
        File file = new File(getSaveFilePath());
        if (!file.exists()) {
            LOGGER.info("No saved game state found at " + getSaveFilePath());
            return false;
        }

        try (Scanner scanner = new Scanner(file)) {
            Board board = new Board();
            for (int row = 0; row < Board.SIZE; row++) {
            	String line = scanner.nextLine();
                if (line.length() != Board.SIZE) {
                    throw new IllegalArgumentException("Invalid board row length: " + line);
                }
                for (int col = 0; col < Board.SIZE; col++) {
                    char c = line.charAt(col);
                    Point pos = new Point(row, col);
                    switch (c) {
                        case 'w': board.setPiece(pos, new Checker(PieceColor.WHITE, pos)); break;
                        case 'W': board.setPiece(pos, new King(PieceColor.WHITE, pos)); break;
                        case 'b': board.setPiece(pos, new Checker(PieceColor.BLACK, pos)); break;
                        case 'B': board.setPiece(pos, new King(PieceColor.BLACK, pos)); break;
                        case ' ': board.setPiece(pos, null); break;
                        default: throw new IllegalArgumentException("Invalid piece character: " + c);
                    }
                }
            }
            String currentPlayerStr = scanner.nextLine().trim();
            PieceColor currentPlayer = PieceColor.valueOf(currentPlayerStr);
            int playerKills = Integer.parseInt(scanner.nextLine().trim());
            int aiKills = Integer.parseInt(scanner.nextLine().trim());
            String difficultyStr = scanner.nextLine().trim();
            AIDifficulty difficulty = AIDifficulty.valueOf(difficultyStr);

            controller.setFieldsAfterLoad(currentUser, AIFactory.createAI(difficulty), new GameState(currentUser, difficulty, board, currentPlayer), playerKills, aiKills);
            LOGGER.info("Game state loaded successfully from " + getSaveFilePath());
            return true;
        } catch (Exception e) {
            LOGGER.severe("Error loading game state: " + e.getMessage());
            return false;
        }
    }
    
}