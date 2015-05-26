package com.nderr.jconstellate;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.google.gson.Gson;

/**
 * Represents a star with a unique ID number
 * @author nderr
 */
public class Star implements Comparable<Star> {

	// constant unique id
	public int ID_NUM;

	// in RADIANS
	private double ra;
	private double dec;

	private transient double x = 0;
	private transient double y = 0;

	private String name;
	private String bayer;
	private Integer flam;
	private String cons;

	// position on unit celestial sphere
	private transient Vector hat = null;

	// apparent magnitude (smaller is brighter!)
	private double mag = 0;

	/**
	 * Empty constructor for GSON.
	 */
	public Star() { }

	/**
	 * Creates star with given id, right ascension, declination, and apparent
	 * magnitude
	 * @param id unique ID number
	 * @param ra right ascension
	 * @param dec declination
	 * @param mag apparent magnitude
	 */
	public Star(int id, double ra, double dec, double mag, String name, 
			String bayer, Integer flam, String cons) {
		this.ID_NUM = id;
		this.ra = ra;
		this.dec = dec;
		this.mag = mag;
		this.name = name;
		this.bayer = bayer;
		this.flam = flam;
		this.cons = cons;
	}

	/**
	 * Returns whether star has a Bayer identifier
	 */
	public boolean hasBayer() {
		return bayer != null;
	}

	/**
	 * Returns whether star has a Flamsteed identifiers
	 */
	public boolean hasFlamsteed() {
		return flam != null;
	}

	/**
	 * Returns whether star has a proper name
	 */
	public boolean hasName() {
		return name != null;
	}

	/**
	 * Retruns whether star has a constellation code
	 */
	public boolean hasCode() {
		return cons != null;
	}

	/**
	 * Returns the star's proper name, or null if none
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the star's constellation code, or null if none
	 */
	public String getCode() {
		return cons;
	}

	/**
	 * Returns the star's Flamsteed identifier, or null if none
	 */
	public int getFlamsteed() {
		if (flam == null)
			return -1;

		return flam;
	}

	/**
	 * Returns the star's Bayer identifier, or null if none
	 */
	public String getBayer() {
		return bayer;
	}

	/**
	 * Sets the star's hat vector.
	 */
	public void setHat() {
		hat = new Vector(Math.PI/2 - dec, ra); // use Vector constructor
	}

	/**
	 * @return position on celestial sphere in cartesian space
	 */
	public Vector getHat() {
		if (hat == null) {
			setHat();
		}
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

	/**
	 * Gets x-coordinate for plotting on screen of size w x h
	 */
	public int getX(int w, int h) {
		double d = Math.sqrt(Math.pow(w,2) + Math.pow(h,2));
		return (int) ((d/2)*x + w/2);
	}

	/**
	 * Gets y-coordinate for plotting on screen of size w x h
	 */
	public int getY(int w, int h) {
		double d = Math.sqrt(Math.pow(w,2) + Math.pow(h,2));
		return (int) (-(d/2)*y + h/2);
	}

	/**
	 * Sets the normalized x and y coordinates given a coordinate transfer object
	 */
	public boolean setPlot(CoordTrans ct) {
		Vector coords = ct.getXY(this);
		x = coords.getX();
		y = coords.getY();
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

	/**
	 * Writes a JSON file of star objects from hygdata_v3.csv
	 * @param args
	 */
	public static void main(String[] args) {
		
		Set<Integer> starIDs = new HashSet<Integer>();

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

		// get variables of params
		int id1, id2;
		List<int[]> lines = new ArrayList<int[]>();

		// for each line
		while (s.hasNextLine()) {

			// get info
			info = s.nextLine().split(",");

			// get star ids and name
			id1 = Integer.parseInt(info[2]);
			id2 = Integer.parseInt(info[3]);
			
			starIDs.add(id1);
			starIDs.add(id2);
		}
		
		s.close();

		// get scanner with star info
		s = null;
		try {
			s = new Scanner(new File("hygdata_v3.csv"));
			s.nextLine();
			s.nextLine();
		} catch (IOException e) {
			System.out.println("Problem with hyg file");
			System.exit(-1);
		}

		// for star parameters
		info = null;

		// instantiate list with fields
		List<Star> stars = new ArrayList<Star>();
		int id;
		String name, bayer, cons;
		Integer flam;
		double ra, dec, mag;

		// go through file
		while (s.hasNextLine()) {

			// split info
			info = s.nextLine().split(",");

			// get id
			id = Integer.parseInt(info[0]);

			// get name
			if (info[6].equals("")) {
				name = null;
			} else {
				name = info[6];
			}

			// get bayer
			if (info[27].equals("")) {
				bayer = null;
			} else {
				bayer = info[27];
			}

			// get flamsteed
			if (info[28].equals("")) {
				flam = null;
			} else {
				flam = Integer.parseInt(info[28]);
			}

			// get constellation code
			if (info[29].equals("")) {
				cons = null;
			} else {
				cons = info[29].toUpperCase();
			}

			// get ra (in radians)
			ra = Double.parseDouble(info[23]);

			// get dec (in radians)
			dec = Double.parseDouble(info[24]);

			// get magnitude
			mag = Double.parseDouble(info[13]);

			// add star to list
			stars.add(new Star(id,ra,dec,mag,name,bayer,flam,cons));
		}

		// use gson
		Gson gson = new Gson();

		// start writing new JSON file
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File("stars.json"));
		} catch (IOException e) {
			System.out.println("error with file");
			System.exit(-1);
		}

		// write each object to json file
		int num = 0;
		for (Star st : stars) {
			if (st.getMag() < 4 || starIDs.contains(st.ID_NUM)) {
				pw.println(gson.toJson(st));
				num++;
			}
		}
		pw.close();
		System.out.println(num + " written to JSON");
	}

}
