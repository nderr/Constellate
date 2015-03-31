import java.text.DecimalFormat;

/**
 * Represents a three dimensional Euclidean vector using an array of size 3
 * @author nderr
 */
public class Vector {
	
	// constants
	public static final int DIM = 3;
	public static final int X = 0;
	public static final int Y = 1;
	public static final int Z = 2;

	// the vector itself
	private double[] elem;
	
	/**
	 * Instantiates a vector with zenith and azimuth on a unit sphere
	 * @param zen zenith angle (0 through pi)
	 * @param azi azimuthal angle (0 though 2 pi)
	 */
	public Vector(double zen, double azi) {
		elem = new double[DIM];
		elem[X] = Math.cos(azi) * Math.sin(zen);
		elem[Y] = Math.sin(azi) * Math.sin(zen);
		elem[Z] = Math.cos(zen);
	}
	
	/**
	 * Instantiates a vector with the provided cartesian coordinates
	 * @param x cartesian x
	 * @param y cartesian y
	 * @param z cartesian z
	 */
	public Vector(double x, double y, double z) {
		elem = new double[DIM];
		elem[X] = x;
		elem[Y] = y;
		elem[Z] = z;
	}
	
	/**
	 * The vector's x-coordinate
	 */
	public double getX() {
		return elem[X];
	}
	
	/**
	 * The vector's y-coordinate
	 */
	public double getY() {
		return elem[Y];
	}
	
	/**
	 * The vector's z-coordinate
	 */
	public double getZ() {
		return elem[Z];
	}

	/**
	 * Sets the vector's x-coordinate
	 */
	public void setX(double val) {
		elem[X] = val;
	}
	
	/**
	 * Sets the vector's y-coordinate
	 */
	public void setY(double val) {
		elem[Y] = val;
	}
	
	/**
	 * Sets the vector's z-coordinate
	 */
	public void setZ(double val) {
		elem[Z] = val;
	}
	
	/**
	 * Calculates the dot product of this vector with the provided vector
	 * @param that vector to be dotted with
	 * @return the dot product of the two vectors
	 */
	public double dot(Vector that) {
		return this.getX() * that.getX() + this.getY() * that.getY() 
				+ this.getZ() * that.getZ();
	}
	
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#0.00");
		return "[ " + df.format(getX()) + " , " + df.format(getY()) + " , " + 
				df.format(getZ()) + " ] ";
	}
	
}
