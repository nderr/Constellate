import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.PI;

/**
 * Object for transforming coordinates between rotated reference systems
 * @author nderr
 */
public class CoordTrans {
	
	// time data
	public static final long J2000 = 946728000000L; // UTC time at J2000 epoch
	public static final double MS_PER_DAY = 86400000.0;
	
	private double lat; // latitude in radians
	private double lon; // longitude in radians
	private double el; // elevation in radians
	private double az; // azimuth (clockwise from north) in radians
	private double spin; // spin (clockwise) of screen
	private double angDiam; // angular diameter of portion of sky on screen
	
	/**
	 * Creates coordinate transform object at given lat,lon, elevation, azimuth,
	 * spin, and angular diameter of target sky piece
	 */
	public CoordTrans(double lat, double lon, double el, double az, double spin, double ang) {
		this.lat = lat;
		this.lon = lon;
		this.el = el;
		this.az = az;
		this.spin = spin;
		this.angDiam = ang;
	}
	
	/**
	 * @param time current UTC time (milliseconds since UTC epoch)
	 * @return local sidereal time at provided time
	 */
	public double lmst(long time) {
		double lst = gmst(time) + lon;
		if (lst >= 2*PI) {
			lst -= 2*PI;
		} else if (lst < 0) {
			lst += 2*PI;
		}
		return lst;
	}
	
	/**
	 * @return current local sidereal time
	 */
	public double lmst() {
		return lmst(System.currentTimeMillis());
	}
	
	/**
	 * @param time current UTC time (milliseconds since UTC epoch)
	 * @return greenwich sidereal time at provided time
	 */
	public double gmst(long time) {
		long durMil = time - J2000;
		double days = durMil / MS_PER_DAY;
		double gstRev = (280.46061837 + 360.98564736629 * days) / 360;
		double gst = (gstRev - (long) gstRev)*2*Math.PI;
		return gst;
	}
	
	/**
	 * @return current greenwich sidereal time
	 */
	public double gmst() {
		return gmst(System.currentTimeMillis());
	}
	
	/**
	 * Gets the celestial direction normal to the screen's current orientation
	 * @param alt altitude
	 * @param azi azimuth
	 * @return cartesian vector normal to the screen's current orientation
	 */
	public Vector getHat() {
		double lst = lmst();
		return getHat(el,az,lat,lst);
	}
	
	/**
	 * Get direction vector at arbitrary place and orientation
	 */
	public static Vector getHat(double el, double az, double lat, double lst) {

		// trig functions
		double cel = cos(el);
		double caz = cos(az);
		double clat = cos(lat);
		double clst = cos(lst);
		double sel = sin(el);
		double saz = sin(az);
		double slat = sin(lat);
		double slst = sin(lst);

		return new Vector(
			clat * clst * sel + cel * (-caz * clst * slat + saz * slst), // X
			-clat * sel * slst + cel * (saz * clst + caz * slat * slst), // Y
			caz * cel * clat + sel * slat // Z
		);
	}
	
	/**
	 * Get current x and y coordinates of star on flat surface at location of
	 * this coordinate transform with normal pointing at altitude (above 
	 * horizon, in radians) and azimuth (clockwise from north, in radians), 
	 * with certain spin around normal (in radians)
	 * 
	 * @param s Star to get position of
	 * @param alt alitude (above horizon) of line of sight in radians
	 * @param azi azmuth (clockwise from north) of line of sight in radians
	 * @param spin rotation of surface (clockwise around line of sight) in radians
	 * @return x and y coordinates of star on surface
	 */
	public Vector getXY(Star s) {
		Vector dir = s.getHat();
		double lst = lmst();
		return getXY(dir.getX(),dir.getY(),dir.getZ(),el,az,spin,lat,lst,angDiam);
	}
	
	public double getAngDiam() {
		return angDiam;
	}
	
	/**
	 * Get current x and y coordinates of position (x,y,z) on celestial sphere
	 * on flat surface at provided latitude and longitude with normal pointing 
	 * at altitude (above horizon, in radians) and azimuth (clockwise from 
	 * north, in radians), with certain spin around normal (in radians)
	 * @param x x coordinate on celestial sphere
	 * @param y y coordinate on celestial sphere
	 * @param z z coordinate on celestial sphere
	 * @param alt altitude (above horizon) of line of sight in radians
	 * @param azi azimuth (clockwise from north) of line of sight in radians
	 * @param spin rotation of surface (clockwise around line of sight) in radians
	 * @param lat latitude on earth
	 * @param lst local sidereal time of location
	 * @return x and y coordinates of location on surface
	 */
	public static Vector getXY(double x, double y, double z, double el, 
			double az, double spin, double lat, double lst, double angDiam) {
		
		// trig functions
		double cel = cos(el);
		double caz = cos(az);
		double cspin = cos(spin);
		double clat = cos(lat);
		double clst = cos(lst);
		double sel = sin(el);
		double saz = sin(az);
		double sspin = sin(spin);
		double slat = sin(lat);
		double slst = sin(lst);
		
		// get array for x and y
		double xx, yy;
		
		// x coordinate
		xx = cspin*(caz*(slst*x + clst*y) - saz*(slat*(-clst*x 
				+ slst*y) + clat*z)) + sspin*(-cel*(clat*(clst*x - slst*y) 
				+ slat*z) + sel*(saz*(slst*x + clst*y) + caz*(-clst*slat*x 
				+ slat*slst*y + clat*z)));
		
		// y coordinate
		yy = cel*cspin*(clat*(clst*x - slst*y) + slat*z) 
				- cspin*sel*(saz*(slst*x + clst*y) + caz*(-clst*slat*x 
				+ slat*slst*y + clat*z)) + sspin*(caz*(slst*x + clst*y) 
				- saz*(-clst*slat*x + slat*slst*y + clat*z));
		
		// if need z coordinate, this should be it
		/*
		zz = cel*sazi*(clst*y + slst*x) + sel*(clat*clst*x 
				+ slat*z - clat*slst*y) + cazi*cel*(clat*z + slat*(-clst*x 
				+ slst*y));
		*/
		
		return new Vector(2 * xx / angDiam, 2 * yy / angDiam,0);
	}
	
	/**
	 * driver method for testing
	 */
	public static void main(String[] args) {
		CoordTrans ct = new CoordTrans(40,-89,0,0,0,1);
		double gst = ct.gmst()*180/Math.PI;
		System.out.println(gst);
		int d = (int) gst;
		int m = (int) ((gst - d)*60);
		System.out.println(d + ":" + m);
	}
}
