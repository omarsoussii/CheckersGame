package model;

import java.util.List;
import java.util.stream.Collectors;

public class HardAI implements AI {
    private static final int DEPTH = 4;

    @Override
    public Move makeMove(Board board, PieceColor color) {
        List<Move> moves = board.getValidMoves(color);
        if (moves.isEmpty()) return null;

        Move bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        for (Move move : moves) {
            Board copy = board.copy();
            applyMove(copy, move);
            int value = minimax(copy, DEPTH - 1, false, color);
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }
        return bestMove;
    }

    @Override
    public AIDifficulty getDifficulty() {
        return AIDifficulty.HARD;
    }

    private int minimax(Board board, int depth, boolean isMaximizing, PieceColor aiColor) {
        if (depth == 0 || board.getValidMoves(aiColor).isEmpty() || board.getValidMoves(aiColor.opposite()).isEmpty()) {
            return board.evaluate(aiColor);
        }

        PieceColor currentColor = isMaximizing ? aiColor : aiColor.opposite();
        List<Move> moves = board.getValidMoves(currentColor);
        int bestValue = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move move : moves) {
            Board copy = board.copy();
            applyMove(copy, move);
            int value = minimax(copy, depth - 1, !isMaximizing, aiColor);
            bestValue = isMaximizing ? Math.max(bestValue, value) : Math.min(bestValue, value);
        }
        return bestValue;
    }

    private void applyMove(Board board, Move move) {
        board.movePiece(move.getFrom(), move.getTo());
        if (move.isCapture()) {
            board.removePiece(move.getCaptured());
            Piece piece = board.getPiece(move.getTo());
            List<Move> captures = piece.getValidMoves(board).stream()
                    .filter(Move::isCapture)
                    .collect(Collectors.toList()); 
            if (!captures.isEmpty()) {
                applyMove(board, captures.get(0));
            }
        }
    }
}