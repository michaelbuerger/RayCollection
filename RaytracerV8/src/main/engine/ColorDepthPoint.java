package main.engine;

public class ColorDepthPoint {
    public int color; // color of point
    public double depth; // distance from sensor to point

    public ColorDepthPoint(int color, double depth) {
        this.color = color;
        this.depth = depth;
    }
}

