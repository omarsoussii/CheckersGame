package model;

import java.awt.Point;
import java.util.List;

public abstract class Piece {
    protected final PieceColor color;
    protected Point position;

    public Piece(PieceColor color, Point position) {
        this.color = color;
        this.position = position;
    }

    public PieceColor getColor() { return color; }
    public Point getPosition() { return position; }
    public void setPosition(Point position) { this.position = position; }
    public abstract List<Move> getValidMoves(Board board);

    protected boolean canCapture(Board board, int rowDir, int colDir) {
        Point jumpPos = new Point(position.x + rowDir * 2, position.y + colDir * 2);
        if (!board.isValidPosition(jumpPos)) return false;

        Point middlePos = new Point(position.x + rowDir, position.y + colDir);
        Piece middlePiece = board.getPiece(middlePos);
        return middlePiece != null && middlePiece.getColor() != color && board.getPiece(jumpPos) == null;
    }

    protected void addCaptureMove(Board board, List<Move> moves, int rowDir, int colDir) {
        if (canCapture(board, rowDir, colDir)) {
            moves.add(new Move(
                new Point(position),
                new Point(position.x + rowDir * 2, position.y + colDir * 2),
                new Point(position.x + rowDir, position.y + colDir)
            ));
        }
    }

    protected void addRegularMove(Board board, List<Move> moves, int rowDir, int colDir) {
        Point newPos = new Point(position.x + rowDir, position.y + colDir);
        if (board.isValidPosition(newPos) && board.getPiece(newPos) == null) {
            moves.add(new Move(new Point(position), newPos));
        }
    }
}