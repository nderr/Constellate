import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Constellation {
	
	private List<Line> lines;
	private String name;
	private String code;
	private Set<Integer> starIDs;
	private transient Set<Star> stars;
	
	public Constellation() { }
	
	public Constellation(List<Line> lines, String name, String code) {
		this.lines = lines;
		this.name = name;
		this.code = code;
		starIDs = new HashSet<Integer>();
		for (Line line : lines) {
			starIDs.add(line.ID1);
			starIDs.add(line.ID2);
		}
	}
	
	public void linkStars(Sky sky) {
		stars = new HashSet<Star>();
		for (Integer id : starIDs) {
			stars.add(sky.getStar(id));
		}
	}
	
	public Set<Star> getStars() {
		return stars;
	}
	
	public void setPlot() {
		
	}
	
	public Star getStar(int id) {
		for (Star s : stars) {
			if (s.ID_NUM == id)
				return s;
		}
		return null;
	}
	
	public Set<Integer> getStarIDs() {
		return starIDs;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCode() {
		return code;
	}
	
	public static void main(String[] args) {
		
	}
	
	public int[] plotLines(int w, int h) {
		int[] coords = new int[4 * lines.size()];
		int index = 0;
		for (Line l : lines) {
			Star s1 = getStar(l.ID1);
			Star s2 = getStar(l.ID2);
			coords[index] = s1.getX(w,h);
			coords[index + 1] = s1.getY(w,h);
			coords[index + 2] = s2.getX(w,h);
			coords[index + 3] = s2.getY(w,h);
			index += 4;
		}
		return coords;
	}
}
