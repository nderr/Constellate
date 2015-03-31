import com.google.gson.Gson;

/**
 * Represents a star with a unique ID number
 * @author nderr
 */
public class Star implements Comparable<Star> {
	
	// constant unique id
	public int ID_NUM = -1;
	
	// in RADIANS
	private double ra = 0;
	private double dec = 0;
	
	private double x = 0;
	private double y = 0;
	
	// position on unit celestial sphere
	private Vector hat = null;
	
	// apparent magnitude (smaller is brighter!)
	private double mag = 0;
	
	public Star() { }
	
	/**
	 * Creates star with given id, right ascension, declination, and apparent
	 * magnitude
	 * @param id unique ID number
	 * @param ra right ascension
	 * @param dec declination
	 * @param mag apparent magnitude
	 */
	public Star(int id, double ra, double dec, double mag) {
		this.ID_NUM = id;
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
	
	public int getX(int w, int h) {
		double d = Math.sqrt(Math.pow(w,2) + Math.pow(h,2));
		int xx = (int) ((d/2)*x + w/2);
		return xx;
	}
	
	public int getY(int w, int h) {
		double d = Math.sqrt(Math.pow(w,2) + Math.pow(h,2));
		int yy = (int) (-(d/2)*y + h/2);
		return yy;
	}
	
	public boolean setPlot(CoordTrans ct) {
		Vector coords = ct.getXY(this);
		double rad = Math.sin(ct.getAngDiam() / 2);
		x = coords.getX() / rad;
		y = coords.getY() / rad;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ID : " + ID_NUM);
		sb.append("\nRA : " + ra);
		sb.append("\nDec: " + dec);
		sb.append("\nMag: " + mag);
		return sb.toString();
	}
	
	public static void main(String[] args) {
		Gson gson = new Gson();
		Star s = new Star(1,0,0,0);
		String j = gson.toJson(s);
		System.out.println(j);
		Star s2 = gson.fromJson(j,Star.class);
		System.out.println(s2);
		System.out.println(Integer.MAX_VALUE);
	}
	
}
