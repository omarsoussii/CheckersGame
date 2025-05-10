package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    public King(PieceColor color, Point position) {
        super(color, position);
    }

    @Override
    public List<Move> getValidMoves(Board board) {
        List<Move> moves = new ArrayList<>();

        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] dir : directions) {
            addCaptureMove(board, moves, dir[0], dir[1]);
            addSlidingMoves(board, moves, dir[0], dir[1]);
        }
        return moves;
    }

    private void addSlidingMoves(Board board, List<Move> moves, int rowDir, int colDir) {
        int row = position.x + rowDir;
        int col = position.y + colDir;
        while (board.isValidPosition(new Point(row, col)) && board.getPiece(new Point(row, col)) == null) {
            moves.add(new Move(new Point(position), new Point(row, col)));
            row += rowDir;
            col += colDir;
        }
    }
}