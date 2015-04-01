import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.google.gson.Gson;

public class Constellation {
	
	// array size and indices
	public static final int DIM = 2;
	public static final int START = 0;
	public static final int END = 1;
	
	public int ID_NUM; // id of const
	private int creatorId; // user id of creator
	private List<int[]> lines; // each entry is one line
	private String name; // name of const
	private String code; // constellation code
	private transient List<Star[]> starLines; // each entry is one line
	private transient Set<Star> stars; // all included stars
	
	/**
	 * Blank constructor for json
	 */
	public Constellation() { }
	
	/**
	 * Makes new constellation
	 */
	public Constellation(int idNum, int creatorId, List<int[]> lines, String name, String code) {
		this.ID_NUM = idNum;
		this.creatorId = creatorId;
		this.lines = lines;
		this.name = name;
		this.code = code;
	}
	
	/**
	 * Links ID nums to their stars as defined by given Sky object
	 */
	public void linkStars(Sky sky) {
		stars = new HashSet<Star>();
		starLines = new ArrayList<Star[]>();
		for (int[] line : lines) {
			Star[] starLine = new Star[DIM];
			starLine[START] = sky.getStar(line[START]);
			starLine[END] = sky.getStar(line[END]);
			stars.add(starLine[START]);
			stars.add(starLine[END]);
			starLines.add(starLine);
		}
	}
	
	/**
	 * Returns set of stars in constellation
	 */
	public Set<Star> getStars() {
		return stars;
	}
	
	/**
	 * Updates all constituent stars' plotting coords
	 */
	public void setPlot(CoordTrans ct) {
		for (Star st : stars) {
			st.setPlot(ct);
		}
	}
	
	/**
	 * Get user id of creator.
	 */
	public int getCreator() {
		return creatorId;
	}
	
	/**
	 * Get star of given id, if contained in this constellation
	 */
	public Star getStar(int id) {
		for (Star s : stars) {
			if (s.ID_NUM == id)
				return s;
		}
		return null;
	}
	
	/**
	 * Get name of constellation
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get constellation code
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * Get array of lines to plot, in the form of [x1,y1,x2,y2,...], where each
	 * four elements (two points) define one line, for screen size w x h
	 */
	public int[] getLines(int w, int h) {
		
		// make array
		int[] coords = new int[4 * lines.size()];
		
		// start at beginning, store each line
		int index = 0;
		for (int[] l : lines) {
			Star s1 = getStar(l[START]);
			Star s2 = getStar(l[END]);
			coords[index] = s1.getX(w,h);
			coords[index + 1] = s1.getY(w,h);
			coords[index + 2] = s2.getX(w,h);
			coords[index + 3] = s2.getY(w,h);
			index += 4;
		}
		return coords;
	}
	
	/**
	 * Reads in list of constellations from const_v6.csv, writes to json file
	 */
	public static void main(String[] args) {
		
		// get scanner
		Scanner s = null;
		try {
			s = new Scanner(new File("const_v6.csv"));
		} catch (IOException e) {
			System.out.println("Problem with const file");
			System.exit(-1);
		}
		
		// for const params
		String[] info = null;
		
		// get list
		List<Constellation> cons = new ArrayList<Constellation>();
		
		// get variables of params
		int id1, id2, idNum = 1;
		int user = 0;
		String name = null, code = null, curr = "AND"; // start with andromeda
		List<int[]> lines = new ArrayList<int[]>();
		
		// for each line
		while (s.hasNextLine()) {
			
			// get info
			info = s.nextLine().split(",");
			
			// check if new constellation
			code = info[0];
			if (!curr.equals(code)) {
				
				// if new, write the one we were building
				cons.add(new Constellation(idNum,user,lines,name,curr));
				
				// then make new list
				lines = new ArrayList<int[]>();
				
				// get other info
				curr = code;
				name = info[1];
				idNum++; // update id number
			}
			
			// get star ids and name
			id1 = Integer.parseInt(info[2]);
			id2 = Integer.parseInt(info[3]);
			name = info[1];
			
			// store line info
			int[] line = new int[DIM];
			line[START] = id1;
			line[END] = id2;
			lines.add(line);
		}
		
		// when loop ends, add final constellation
		cons.add(new Constellation(idNum,user,lines,name,curr));
		
		// use gson
		Gson gson = new Gson();
		
		// get file to write
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File("const.json"));
		} catch (IOException e) {
			System.out.println("error with file");
			System.exit(-1);
		}
		
		// write each constellation
		for (Constellation c : cons)
			pw.println(gson.toJson(c));
		
		pw.close();
		System.out.println("written to JSON");
	}
}
