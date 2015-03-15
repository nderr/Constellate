import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Object for transforming coordinates between rotated reference systems
 * @author nderr
 */
public class CoordTrans {
	
	// time data
	public static final long J2000 = 946728000000L; // UTC time at J2000 epoch
	public static final double MS_PER_DAY = 86400000.0;
	
	// latitude and longitude
	private double lat;
	private double lon;
	
	/**
	 * Creates coordinate transform object at given latitude and longitude
	 * @param lat latitude
	 * @param lon longitude
	 */
	public CoordTrans(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
	//TODO easier way to do this: divide lst by 360, cast as int, then multiply by 2 pi
	/**
	 * @param time current UTC time (milliseconds since UTC epoch)
	 * @return local sidereal time at provided time
	 */
	public double lmst(long time) {
		double lst = gmst(time) + lon;
		while (lst > 360) lst -= 360;
		return lst;
	}
	
	/**
	 * @return current local sidereal time
	 */
	public double lmst() {
		return lmst(System.currentTimeMillis());
	}
	
	//TODO easier way to do this: divide gst by 360, cast as int, then multiply by 2 pi!
	/**
	 * @param time current UTC time (milliseconds since UTC epoch)
	 * @return greenwich sidereal time at provided time
	 */
	public double gmst(long time) {
		long durMil = time - J2000;
		double days = durMil / MS_PER_DAY;
		double gst = 280.46061837 + 360.98564736629 * days;
		while (gst > 360) gst -= 360;
		return gst;
	}
	
	/**
	 * @return current greenwich sidereal time
	 */
	public double gmst() {
		return gmst(System.currentTimeMillis());
	}
	
	//TODO NOTE THAT LST IS SET TO ZERO HERE FOR A FIXED VIEW OF SKY FOR TESTING
	/**
	 * Get current x and y coordinates of star on flat surface at location of
	 * this coordinate transform with normal pointing at altitude (above 
	 * horizon, in radians) and azimuth (clockwise from north, in radians), 
	 * with certain spin around normal (in radians)
	 * 
	 * @param s Star to get position of
	 * @param alt altitude (above horizon) of line of sight in radians
	 * @param azi azimuth (clockwise from north) of line of sight in radians
	 * @param spin rotation of surface (clockwise around line of sight) in radians
	 * @return x and y coordinates of star on surface
	 */
	public double[] getXY(Star s, double alt, double azi, double spin) {
		Vector dir = s.getHat();
		double lst = 0;//lmst();
		return getXY(dir.getX(),dir.getY(),dir.getZ(),alt,azi,spin,lat,lst);
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
	public static double[] getXY(double x, double y, double z, double alt, 
			double azi, double spin, double lat, double lst) {
		
		// trig functions
		double calt = cos(alt);
		double cazi = cos(azi);
		double cspin = cos(spin);
		double clat = cos(lat);
		double clst = cos(lst);
		double salt = sin(alt);
		double sazi = sin(azi);
		double sspin = sin(spin);
		double slat = sin(lat);
		double slst = sin(lst);
		
		// get array for x and y
		double[] coords = new double[2];
		
		// x coordinate
		coords[0] = cspin*(cazi*(slst*x + clst*y) - sazi*(slat*(-clst*x 
				+ slst*y) + clat*z)) + sspin*(-calt*(clat*(clst*x - slst*y) 
				+ slat*z) + salt*(sazi*(slst*x + clst*y) + cazi*(-clst*slat*x 
				+ slat*slst*y + clat*z)));
		
		// y coordinate
		coords[1] = calt*cspin*(clat*(clst*x - slst*y) + slat*z) 
				- cspin*salt*(sazi*(slst*x + clst*y) + cazi*(-clst*slat*x 
				+ slat*slst*y + clat*z)) + sspin*(cazi*(slst*x + clst*y) 
				- sazi*(-clst*slat*x + slat*slst*y + clat*z));
		
		return coords;
	}
}
