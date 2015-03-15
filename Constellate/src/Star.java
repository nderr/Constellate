/**
 * Represents a star with a unique ID number
 * @author nderr
 */
public class Star implements Comparable<Star> {
	
	// constant unique id
	public final int ID;
	
	// in RADIANS
	private double ra;
	private double dec;
	
	// position on unit celestial sphere
	private Vector hat;
	
	// apparent magnitude (smaller is brighter!)
	private double mag;
	
	/**
	 * Creates star with given id, right ascension, declination, and apparent
	 * magnitude
	 * @param id unique ID number
	 * @param ra right ascension
	 * @param dec declination
	 * @param mag apparent magnitude
	 */
	public Star(int id, double ra, double dec, double mag) {
		ID = id;
		this.ra = ra;
		this.dec = dec;
		this.mag = mag;
		hat = new Vector(Math.PI/2 - dec, ra); // use Vector constructor
	}
	
	/**
	 * @return position on celestial sphere in cartesian space
	 */
	public Vector getHat() {
		return hat;
	}
	
	/**
	 * @param v other vector
	 * @return angular separation in radians between this 
	 * star's direction on the celestial sphere and a provided unit vector
	 */
	public double angularSep(Vector v) {
		return Math.acos(v.dot(hat));
	}

	/**
	 * @return apparent magnitude
	 */
	public double getMag() {
		return mag;
	}

	/**
	 * For sorting. Returns -1 if this magnitude less than that (if this
	 * star is brighter than that star)
	 */
	public int compareTo(Star that) {
		if (this.mag < that.mag) {
			return -1;
		} else {
			return 1;
		}
	}
	
	/**
	 * @return right ascension
	 */
	public double getRA() {
		return ra;
	}
	
	/**
	 * @return declination
	 */
	public double getDec() {
		return dec;
	}
	
}
