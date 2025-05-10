package model;

public interface AI {
    Move makeMove(Board board, PieceColor color);
    AIDifficulty getDifficulty();
}