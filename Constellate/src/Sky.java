import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Iterable collection of stars
 * @author nderr
 */
public class Sky implements Iterable<Star> {

	// ordered lists of stars brighter than cutoff point
	private List<Star> visible;
	
	// remainder of stars in priority queue ordered by magnitude
	private Queue<Star> invisible;
	private double magLimit; // magnitude cutoff
	
	/**
	 * Instantiates sky from a scanner of star data with a given magnitude
	 * cutoff
	 * @param stars Scanner from file of star data
	 * @param magL cutoff level for apparent magnitude
	 */
	public Sky(Scanner stars, double magL) {
		
		// instantiate list/queue
		visible = new LinkedList<Star>();
		invisible = new PriorityQueue<Star>();
		
		// start with no cutoff
		magLimit = Double.MIN_VALUE;
		
		// go through stars
		while (stars.hasNextLine()) {
			
			// grab data
			int id = Integer.parseInt(stars.next());
			for (int i = 0; i < 12; i++) stars.next();
			double mag = Double.parseDouble(stars.next());
			for (int i = 0; i < 9; i++) stars.next();
			double ra = Double.parseDouble(stars.next());
			double dec = Double.parseDouble(stars.next());
			stars.nextLine(); // done with line
			
			// make star, add to queue
			Star s = new Star(id,ra,dec,mag);
			invisible.add(s);
		}
		
		// set cutoff level to provided level
		resetMag(magL);
	}
	
	/**
	 * Returns an iterator of the sky's visible stars
	 */
	public Iterator<Star> iterator() {
		return visible.iterator();
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
	
	/**
	 * driver method for testing
	 */
	public static void main(String[] args) {
		Scanner s = null;
		
		try {
			s = new Scanner(new File("/Users/nderr/Dropbox/hygdata_v3.csv"));
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		
		s.useDelimiter(",");
		s.nextLine();
		s.nextLine();
		
		Sky sky = new Sky(s,2);
		System.out.println("Visible: " + sky.visible.size());
		System.out.println("Hidden: " + sky.invisible.size());
		
		System.out.println();
		sky.resetMag(0);
		System.out.println("Visible: " + sky.visible.size());
		System.out.println("Hidden: " + sky.invisible.size());
		
		System.out.println();
		sky.resetMag(5.0);
		System.out.println("Visible: " + sky.visible.size());
		System.out.println("Hidden: " + sky.invisible.size());
		
		System.out.println();
		sky.resetMag(2.0);
		System.out.println("Visible: " + sky.visible.size());
		System.out.println("Hidden: " + sky.invisible.size());
	}
	
}
