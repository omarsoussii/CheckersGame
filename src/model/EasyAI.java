package model;

import java.util.List;
import java.util.Random;

public class EasyAI implements AI {
    private final Random random = new Random();

    @Override
    public Move makeMove(Board board, PieceColor color) {
        List<Move> moves = board.getValidMoves(color);
        return moves.isEmpty() ? null : moves.get(random.nextInt(moves.size()));
    }

    @Override
    public AIDifficulty getDifficulty() {
        return AIDifficulty.EASY;
    }
}