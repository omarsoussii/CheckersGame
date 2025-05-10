package model;

import java.util.List;

public class MediumAI implements AI {
    @Override
    public Move makeMove(Board board, PieceColor color) {
        List<Move> moves = board.getValidMoves(color);
        if (moves.isEmpty()) return null;

        return moves.stream()
                .filter(Move::isCapture)
                .findFirst()
                .orElse(moves.get((int) (Math.random() * moves.size())));
    }

    @Override
    public AIDifficulty getDifficulty() {
        return AIDifficulty.MEDIUM;
    }
}