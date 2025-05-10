package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final int SIZE = 8;
    private final Piece[][] grid = new Piece[SIZE][SIZE];

    public Board() {
        initializeBoard();
    }

    private void initializeBoard() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < SIZE; col++) {
                if ((row + col) % 2 == 1) {
                    grid[row][col] = new Checker(PieceColor.BLACK, new Point(row, col));
                }
            }
        }
        for (int row = 5; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if ((row + col) % 2 == 1) {
                    grid[row][col] = new Checker(PieceColor.WHITE, new Point(row, col));
                }
            }
        }
    }

    public Piece getPiece(Point pos) {
        return isValidPosition(pos) ? grid[pos.x][pos.y] : null;
    }

    public void setPiece(Point pos, Piece piece) {
        if (isValidPosition(pos)) {
            grid[pos.x][pos.y] = piece;
            if (piece != null) piece.setPosition(pos);
        }
    }

    public void movePiece(Point from, Point to) {
        Piece piece = getPiece(from);
        if (piece != null) {
            setPiece(from, null);
            setPiece(to, piece);
            if (shouldPromote(piece, to)) {
                setPiece(to, new King(piece.getColor(), to));
            }
        }
    }

    public void removePiece(Point pos) {
        setPiece(pos, null);
    }

    public boolean isValidPosition(Point pos) {
        return pos.x >= 0 && pos.x < SIZE && pos.y >= 0 && pos.y < SIZE;
    }

    public List<Move> getValidMoves(PieceColor color) {
        List<Move> moves = new ArrayList<>();
        boolean capturesExist = false;

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Point pos = new Point(row, col);
                Piece piece = getPiece(pos);
                if (piece != null && piece.getColor() == color) {
                    List<Move> pieceMoves = piece.getValidMoves(this);
                    for (Move move : pieceMoves) {
                        if (move.isCapture()) {
                            if (!capturesExist) {
                                moves.clear();
                                capturesExist = true;
                            }
                            moves.add(move);
                        } else if (!capturesExist) {
                            moves.add(move);
                        }
                    }
                }
            }
        }
        return moves;
    }

    public Board copy() {
        Board copy = new Board();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Point pos = new Point(row, col);
                Piece piece = getPiece(pos);
                if (piece != null) {
                    copy.setPiece(pos, piece instanceof King ? 
                        new King(piece.getColor(), pos) : 
                        new Checker(piece.getColor(), pos));
                }
            }
        }
        return copy;
    }

    public int evaluate(PieceColor color) {
        int score = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Piece piece = getPiece(new Point(row, col));
                if (piece != null) {
                    int value = piece instanceof King ? 3 : 1;
                    score += piece.getColor() == color ? value : -value;
                }
            }
        }
        return score;
    }

    private boolean shouldPromote(Piece piece, Point to) {
        return piece.getColor() == PieceColor.WHITE && to.x == 0 ||
               piece.getColor() == PieceColor.BLACK && to.x == SIZE - 1;
    }
}