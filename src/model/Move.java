package model;

import java.awt.Point;

public class Move {
    private final Point from;
    private final Point to;
    private final Point captured;

    public Move(Point from, Point to) {
        this(from, to, null);
    }

    public Move(Point from, Point to, Point captured) {
        this.from = from;
        this.to = to;
        this.captured = captured;
    }

    public Point getFrom() { return from; }
    public Point getTo() { return to; }
    public Point getCaptured() { return captured; }
    public boolean isCapture() { return captured != null; }
}