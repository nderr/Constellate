import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Iterable collection of stars
 * @author nderr
 */
public class Sky {
	
	public static final int DEFAULT_MAG = 5;

	// ordered lists of stars brighter than cutoff point
	private List<Star> fov;
	private LinkedList<Star> visible;
	private List<Constellation> fovConst;
	private HashMap<String,Constellation> visConst;
	private HashMap<String,Constellation> allConst;
	private HashMap<Integer,Star> directory;
	
	// remainder of stars in priority queue ordered by magnitude
	private PriorityQueue<Star> invisible = null;
	private double magLimit = DEFAULT_MAG; // magnitude cutoff
	
	public Sky() { }
	
	public Sky(List<Star> stars, List<Constellation> cons, double magL) {
		
		// instantiate list/queue
		directory = new HashMap<Integer,Star>(stars.size()*10/7);
		invisible = new PriorityQueue<Star>();
		magLimit = Double.MIN_VALUE;
		for (Star st : stars) {
			directory.put(st.ID_NUM, st);
			invisible.add(st);
		}
		visible = new LinkedList<Star>();
		allConst = new HashMap<String,Constellation>(cons.size()*10/7);
		for (Constellation c : cons) {
			allConst.put(c.getCode(), c);
			c.linkStars(this);
		}
		
		visConst = new HashMap<String,Constellation>();
		// start with no cutoff
		resetMag(magL);
	}
	
	public boolean addViewConst(String code) {
		Constellation c = allConst.get(code);
		
		if (c != null) {
			visConst.put(code, c);
			return true;
		}
			
		return false;
	}
	
	public Constellation removeViewConst(String code) {
		return visConst.remove(code);
	}
	
	/**
	 * Returns an iterator of the sky's visible stars
	 */
	public List<Star> getVisible() {
		return visible;
	}
	
	public List<Star> getStarFOV() {
		return fov;
	}
	
	public List<Constellation> getConstFOV() {
		return fovConst;
	}
	
	public HashMap<Integer,Star> getStars() {
		return directory;
	}
	
	public HashMap<String,Constellation> getConst() {
		return allConst;
	}
	
	/**
	 * Resets the cutoff magnitude
	 * @param newMag new maximum apparent magnitude
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
	
	public Star getStar(int id) {
		return directory.get(id);
	}
	
	public void lookAt(CoordTrans ct) {
		Vector hat = ct.getHat();
		
		fov = new ArrayList<Star>();
		double angSep;
		
		for (Star st : visible) {
			angSep = Math.acos(hat.dot(st.getHat()));
			if (angSep < ct.getAngDiam()) {
				fov.add(st);
				st.setPlot(ct);
			}
		}
		
		fovConst = new ArrayList<Constellation>();
		for (Constellation c : visConst.values()) {
			for (Star st : c.getStars()) {
				angSep = Math.acos(hat.dot(st.getHat()));
				if (angSep < ct.getAngDiam()) {
					fovConst.add(c);
					for (Star stIn : c.getStars()) {
						stIn.setPlot(ct);
					}
					break;
				}
			}
		}
	}
	
	public class Entry {
		public Star star;
		public Constellation cons;
		
		public Entry() { }
		
		public Entry(Star s, Constellation c) {
			star = s;
			cons = c;
		}
	}
	
	/**
	 * driver method for testing
	 */
	public static void main(String[] args) {
		
	}
	
}
