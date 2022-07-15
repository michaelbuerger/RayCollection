package main.math;

public class Vec3 {
    public double x, y, z;

    public Vec3(double x, double y, double z, boolean normalize) { // to create normalized final vectors
        double mag = 1;
        
        if(normalize)
            mag = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));

        this.x = x / mag;
        this.y = y / mag;
        this.z = z / mag;
    }

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(double x) {
        this.x = x;
        this.y = x;
        this.z = x;
    }

    public Vec3(Vec3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public Vec3 add(Vec3 other) {
        return new Vec3(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vec3 sub(Vec3 other) {
        return new Vec3(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Vec3 negative() {
        return new Vec3(-this.x, -this.y, -this.z);
    }

    public Vec3 mult(Vec3 other) {
        return new Vec3(this.x * other.x, this.y * other.y, this.z * other.z);
    }

    public Vec3 mult(double other) {
        return new Vec3(this.x * other, this.y * other, this.z * other);
    }

    public Vec3 div(Vec3 other) {
        return new Vec3(this.x / other.x, this.y / other.y, this.z / other.z);
    }

    public Vec3 div(double other) {
        return new Vec3(this.x / other, this.y / other, this.z / other);
    }

    public double distance(Vec3 other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2) + Math.pow(this.z - other.z, 2));
    }

    public double dot(Vec3 other) {
        return (this.x * other.x) + (this.y * other.y) + (this.z * other.z);
    }

    public Vec3 cross(Vec3 other) {
        return new Vec3(this.y * other.z - this.z * other.y,
                this.z * other.x - this.x * other.z,
                this.x * other.y - this.y * other.x);
    }
    
    public double magnitude() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    public Vec3 normalized() {
        double mag = magnitude();
        return new Vec3(this.mult(1 / mag));
    }

    public void normalize() {
        Vec3 normalized = this.normalized();
        x = normalized.x;
        y = normalized.y;
        z = normalized.z;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }
}