import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Collection of Stars and Constellations
 */
public class Sky {
	
	public static final int DEFAULT_MAG = 5;

	private List<Star> fov; // stars the screen could see. plot them
	private LinkedList<Star> visible; // stars we could see if looking at them
	private List<Constellation> fovConst; // constellations containing a star in fov
	private HashMap<String,Constellation> visConst; // constellations we're looking at
	private HashMap<String,Constellation> allConst; // every constellation to look at
	private HashMap<Integer,Star> directory; // ever star we have
	private PriorityQueue<Star> invisible; // stars too dim to see
	private double magLimit = DEFAULT_MAG; // magnitude cutoff
	
	// blank for json, if maybe needed?
	public Sky() { }
	
	public Sky(List<Star> stars, List<Constellation> cons, double magL) {
		
		// instantiate map/queue
		directory = new HashMap<Integer,Star>(stars.size()*10/7);
		visible = new LinkedList<Star>();
		invisible = new PriorityQueue<Star>();
		
		// star with all invisible
		magLimit = Double.MIN_VALUE;
		
		// put each star in directory and invisible queue
		for (Star st : stars) {
			st.setHat();
			directory.put(st.ID_NUM, st);
			invisible.add(st);
		}
		
		// get maps for constellations
		visConst = new HashMap<String,Constellation>();
		allConst = new HashMap<String,Constellation>(cons.size()*10/7);
		
		// put each constellation in map and link its stars to directory stars
		for (Constellation c : cons) {
			allConst.put(c.getCode(), c);
			c.linkStars(this);
		}
		
		// set magnitude cutoff
		resetMag(magL);
	}
	
	/**
	 * Adds the given constellation to the glabal list
	 */
	public void addConst(Constellation c) {
		allConst.put(c.getCode(), c);
		c.linkStars(this);
	}
	
	/**
	 * Makes all constellations viewable
	 */
	public void addViewConst() {
		for (Constellation c : allConst.values())
			addViewConst(c.getCode());
	}
	
	/**
	 * Adds constellation of given code to viewable set
	 */
	public boolean addViewConst(String code) {
		
		// get const
		Constellation c = allConst.get(code);
		
		// check if null
		if (c != null) {
			visConst.put(code, c);
			return true;
		}
		return false;
	}
	
	/**
	 * Remove constellation of given code from view list
	 */
	public Constellation removeViewConst(String code) {
		return visConst.remove(code);
	}
	
	/**
	 * Returns the visible stars (above min mag)
	 */
	public List<Star> getVisible() {
		return visible;
	}
	
	/**
	 * Returns the stars in the FOV
	 */
	public List<Star> getStarFOV() {
		return fov;
	}
	
	/**
	 * Returns the constellations in the FOV
	 */
	public List<Constellation> getConstFOV() {
		return fovConst;
	}
	
	/**
	 * Get the star directory (all stars)
	 */
	public HashMap<Integer,Star> getStars() {
		return directory;
	}
	
	/**
	 * Get all constellations in this sky
	 */
	public HashMap<String,Constellation> getConst() {
		return allConst;
	}
	
	/**
	 * Resets the cutoff magnitude to provided value
	 */
	public void resetMag(double newMag) {
		
		// if making higher
		if (newMag > magLimit) {
			
			// add new visible stars from queue
			magLimit = newMag;
			while (invisible.peek().getMag() < magLimit) 
				visible.add(0,invisible.poll());
		} else {
			// remove no-longer-visible stars from list
			magLimit = newMag;
			while (visible.get(0).getMag() > magLimit)
				invisible.add(visible.remove(0));
		}
	}
	
	/**
	 * Gets star of provided id
	 */
	public Star getStar(int id) {
		return directory.get(id);
	}
	
	/**
	 * Determines which stars and constellations are in fov of provided coord
	 * transfer object, and sets their normalized plotting coordinates
	 */
	public void lookAt(CoordTrans ct) {
		
		// get direction of coord trans
		Vector hat = ct.getHat();
		
		// make list of stars in fov
		fov = new ArrayList<Star>();
		
		double angSep; // angular separation
		double dotProd; // dot product

		// for each star
		for (Star st : visible) {
			
			// dot with axis of view, check on angular separation
			dotProd = hat.dot(st.getHat());
			angSep = Math.acos(dotProd);
			
			// if within field of view, add star to list and set its coords
			if (angSep < ct.getAngDiam() && dotProd > 0) {
				fov.add(st);
				st.setPlot(ct);
			}
		}
		
		// make list for constellations
		fovConst = new ArrayList<Constellation>();
		
		// for each constelltaion
		for (Constellation c : visConst.values()) {
			
			// for each star in constellation
			for (Star st : c.getStars()) {
				
				// repeat above
				dotProd = hat.dot(st.getHat());
				angSep = Math.acos(dotProd);
				
				// if within field of view, add to list and set up for plot
				if (angSep < ct.getAngDiam() && dotProd > 0) {
					fovConst.add(c);
					c.setPlot(ct);
					break;
				}
			}
		}
	}
	
	/**
	 * Updates all contstituents' plotting coordinates for a screen of size w x h
	 */
	public void setPlot(int w, int h) {
		
	}
	
	/**
	 * driver method for testing
	 */
	public static void main(String[] args) {
		
	}
	
}
