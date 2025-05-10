package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Checker extends Piece {
    public Checker(PieceColor color, Point position) {
        super(color, position);
    }

    @Override
    public List<Move> getValidMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        int dir = color == PieceColor.WHITE ? -1 : 1;

        addCaptureMove(board, moves, dir, -1);
        addCaptureMove(board, moves, dir, 1);
        if (moves.isEmpty()) {
            addRegularMove(board, moves, dir, -1);
            addRegularMove(board, moves, dir, 1);
        }
        return moves;
    }
}