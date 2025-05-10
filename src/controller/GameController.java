package controller;

import java.awt.Point;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import model.*;
import utils.SoundManager;
import view.*;

public class GameController {
    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());
    private GameState gameState;
    private AI ai;
    private final MainView view;
    private final SoundManager soundManager;
    private User currentUser;
    private int playerKills = 0;
    private int aiKills = 0;
    private GameStateFileHandler fileHandler;

    private final UserDAO userDAO = new UserDAO();

    public GameController(MainView view) {
        this.view = view;
        this.soundManager = new SoundManager();
        this.fileHandler = new GameStateFileHandler(null); // Will be updated in setCurrentUser
        initializeSoundEffects();
        soundManager.playBackgroundMusic("/sounds/music.wav");
    }

    private int calculateDistance(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private boolean isMoveFromFar(Point from, Point kingPos) {
        return calculateDistance(from, kingPos) > 2;
    }

    private Piece findKingToCapture(Board board, Point from) {
        Piece fromPiece = board.getPiece(from);
        if (fromPiece == null) {
            LOGGER.severe("No piece found at move origin: " + from);
            return null;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                Point pos = new Point(row, col);
                Piece piece = board.getPiece(pos);
                if (piece instanceof King && piece.getColor() != fromPiece.getColor()) {
                    return piece;
                }
            }
        }
        return null;
    }

    public int getPlayerKills() { return playerKills; }
    public int getAIKills() { return aiKills; }
    public GameState getGameState() { return gameState; }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        this.fileHandler = new GameStateFileHandler(user); // Update fileHandler with user
        resetGame();
    }

    public void setDifficulty(AIDifficulty difficulty) {
        this.ai = AIFactory.createAI(difficulty);
        resetGame();
    }

    private void resetGame() {
        if (currentUser != null && ai != null) {
            this.gameState = new GameState(currentUser, ai.getDifficulty());
            playerKills = 0;
            aiKills = 0;
            updateView();
        }
    }

    private void initializeSoundEffects() {
        soundManager.loadSoundEffects("player_kill", "/sounds/helotv_f.wav", "/sounds/jaw.wav", "/sounds/galbi.wav", "/sounds/enajit.wav");
        soundManager.loadSoundEffects("ai_kill", "/sounds/ha9i_ff.wav", "/sounds/yalalali.wav", "/sounds/nomine.wav", "/sounds/habs.wav");
        soundManager.loadSoundEffects("player_win", "/sounds/helotv_f.wav");
        soundManager.loadSoundEffects("ai_win", "/sounds/ha9i_ff.wav");
    }

    public void handleSquareClick(int row, int col) {
        if (!isHumanTurn()) return;
        GameView gameView = view.getGameView();
        if (gameView == null) return;

        Point pos = new Point(row, col);
        Piece piece = gameState.getBoard().getPiece(pos);
        if (piece != null && piece.getColor() == gameState.getCurrentPlayer()) {
            gameView.highlightValidMoves(piece.getValidMoves(gameState.getBoard()));
        } else {
            gameView.clearHighlights();
        }
    }

    public void handleMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isHumanTurn()) return;
        GameView gameView = view.getGameView();
        if (gameView == null) return;

        Move move = findValidMove(fromRow, fromCol, toRow, toCol);
        if (move != null) {
            LOGGER.info("Executing move from " + move.getFrom() + " to " + move.getTo());
            synchronized (gameState) {
                executeMove(move);
                gameView.clearHighlights();
                if (!gameState.isGameOver() && !gameState.isCurrentPlayerHuman()) {
                    makeAIMove();
                }
            }
        }
    }

    private Move findValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        Point from = new Point(fromRow, fromCol);
        Point to = new Point(toRow, toCol);
        Piece piece = gameState.getBoard().getPiece(from);
        if (piece == null) {
            LOGGER.severe("No piece found at starting position: " + from);
            return null;
        }
        if (piece.getColor() != gameState.getCurrentPlayer()) {
            LOGGER.severe("Piece at " + from + " does not belong to current player: " + piece.getColor());
            return null;
        }
        return piece.getValidMoves(gameState.getBoard()).stream()
                .filter(m -> m.getTo().equals(to))
                .findFirst()
                .orElse(null);
    }

    private void executeMove(Move move) {
        Board board = gameState.getBoard();
        Point from = move.getFrom();
        Point to = move.getTo();

        board.movePiece(from, to);

        if (move.isCapture()) {
            board.removePiece(move.getCaptured());
            if (gameState.isCurrentPlayerHuman()) {
                soundManager.playRandomSoundEffect("player_kill");
                playerKills++;
            } else {
                soundManager.playRandomSoundEffect("ai_kill");
                aiKills++;
            }

            Piece piece = board.getPiece(move.getTo());
            List<Move> captures = piece.getValidMoves(board).stream()
                    .filter(Move::isCapture)
                    .collect(Collectors.toList());
            if (!captures.isEmpty()) {
                if (gameState.isCurrentPlayerHuman()) {
                    view.getGameView().highlightValidMoves(captures);
                } else {
                    executeMove(captures.get(0));
                }
                return;
            }
        }

        gameState.switchPlayer();
        checkGameOver();
        updateView();
    }

    public void makeAIMove() {
        if (gameState == null || ai == null) return;
        Move aiMove = ai.makeMove(gameState.getBoard(), gameState.getCurrentPlayer());
        if (aiMove == null) {
            endGame(gameState.getCurrentPlayer().opposite());
            return;
        }

        GameView gameView = view.getGameView();
        if (gameView != null) gameView.showAIMove(aiMove);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                synchronized (gameState) {
                    executeMove(aiMove);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void checkGameOver() {
        if (gameState.getBoard().getValidMoves(gameState.getCurrentPlayer()).isEmpty()) {
            endGame(gameState.getCurrentPlayer().opposite());
        }
    }

    private void endGame(PieceColor winner) {
        gameState.endGame(winner);
        soundManager.playRandomSoundEffect(winner == PieceColor.WHITE ? "player_win" : "ai_win");

        if (currentUser != null) {
            currentUser.setTotalGames(currentUser.getTotalGames() + 1);
            if (winner == PieceColor.WHITE) {
                currentUser.setWins(currentUser.getWins() + 1);
            } else {
                currentUser.setLosses(currentUser.getLosses() + 1);
            }
            AIDifficulty difficulty = ai.getDifficulty();
            currentUser.setGamesByDifficulty(difficulty, currentUser.getGamesByDifficulty().getOrDefault(difficulty, 0) + 1);
            userDAO.updateStats(currentUser);
        }

        GameView gameView = view.getGameView();
        if (gameView != null) {
            gameView.showGameOver(winner);
        }

        fileHandler.deleteSaveFile();
        view.showMainMenu();
    }

    public void setFieldsAfterLoad(User user, AI ai, GameState gameState, int playerKills, int aiKills) {
        this.currentUser = user;
        this.ai = ai;
        this.gameState = gameState;
        this.playerKills = playerKills;
        this.aiKills = aiKills;
        updateView();
        if (!gameState.isGameOver() && !gameState.isCurrentPlayerHuman()) {
            makeAIMove();
        }
    }

    private boolean isHumanTurn() {
        return gameState != null && !gameState.isGameOver() && gameState.isCurrentPlayerHuman();
    }

    private void updateView() {
        GameView gameView = view.getGameView();
        if (gameView != null) gameView.updateView();
    }

    public void cleanup() {
        soundManager.stopBackgroundMusic();
        soundManager.cleanup();
        gameState = null;
        playerKills = 0;
        aiKills = 0;
    }

    public boolean hasSavedGame() {
        return fileHandler.hasSavedGame();
    }

    public void deleteSaveFile() {
        fileHandler.deleteSaveFile();
    }

    public void saveGameState() {
        fileHandler.saveGameState(gameState, playerKills, aiKills);
    }

    public boolean loadGameState() {
        return fileHandler.loadGameState(this);
    }
}