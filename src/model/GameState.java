package model;

public class GameState {
    private final Board board;
    private PieceColor currentPlayer;
    private final User user;
    private final AIDifficulty difficulty;
    private boolean gameOver;
    private PieceColor winner;

    public GameState(User user, AIDifficulty difficulty) {
        this(user, difficulty, new Board(), PieceColor.WHITE);
    }

    public GameState(User user, AIDifficulty difficulty, Board board, PieceColor currentPlayer) {
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.user = user;
        this.difficulty = difficulty;
        this.gameOver = false;
    }

    public Board getBoard() { return board; }
    public PieceColor getCurrentPlayer() { return currentPlayer; }
    public User getUser() { return user; }
    public AIDifficulty getDifficulty() { return difficulty; }
    public boolean isGameOver() { return gameOver; }
    public PieceColor getWinner() { return winner; }

    public void switchPlayer() {
        currentPlayer = currentPlayer.opposite();
    }

    public boolean isCurrentPlayerHuman() {
        return currentPlayer == PieceColor.WHITE;
    }

    public void endGame(PieceColor winner) {
        this.gameOver = true;
        this.winner = winner;
        if (user != null) {
            user.addGameResult(winner == PieceColor.WHITE, difficulty);
            new UserDAO().updateStats(user);
        }
    }
}