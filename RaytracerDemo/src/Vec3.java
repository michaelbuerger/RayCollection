/*
 * TODO:
 * Sub, Div
 * AddEquals, SubEquals, MultEquals, DivEquals
 */

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

        public Vec3 Add(Vec3 other) {
            return new Vec3(this.x + other.x, this.y + other.y, this.z + other.z);
        }

        public Vec3 Negative() {
            return new Vec3(-this.x, -this.y, -this.z);
        }

        public Vec3 Mult(Vec3 other) {
            return new Vec3(this.x * other.x, this.y * other.y, this.z * other.z);
        }

        public Vec3 Mult(double other) {
            return new Vec3(this.x * other, this.y * other, this.z * other);
        }

        public double Distance(Vec3 other) {
            return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2) + Math.pow(this.z - other.z, 2));
        }

        public double Dot(Vec3 other) {
            return (this.x * other.x) + (this.y * other.y) + (this.z * other.z);
        }
        
        public double Magnitude() {
            return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        }

        public Vec3 Normalized() {
            double mag = Magnitude();
            return new Vec3(this.Mult(1 / mag));
        }

        public void Normalize() {
            Vec3 normalized = this.Normalized();
            x = normalized.x;
            y = normalized.y;
            z = normalized.z;
        }

        public Vec3 ReflectedAcross(Vec3 N) {
            return this.Add(
                N.Mult(-2.0 * N.Dot(this))
            );
        }

        @Override
        public String toString() {
            return "[" + x + ", " + y + ", " + z + "]";
        }
	}