import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Driver class for rough plotting using JFreeChart
 * @author nderr
 */
public class SkyPlot {
	
	public static final int FILE_SYS = 0;
	public static final int INTERNET = 1;

	/**
	 * Reads in data from hygdata_v3.csv and plots star positions at Madison
	 */
	public static void main(String[] args) {

		// cutoff - maximum magnitude (minimum brightness)
		final double MAG_CUTOFF = 3.4; // dimmest star in big dipper is 3.3

		// Madison lat and lon
		final double LAT = 43.067*Math.PI/180;
		final double LON = -89.4*Math.PI/180;
		final int SOURCE = FILE_SYS;
		
		// strings
		String filename = "/Users/nderr/Dropbox/hygdata_v3.csv";
		String urlname = "https://raw.githubusercontent.com/astronexus/" 
				+ "HYG-Database/master/hygdata_v3.csv";

		// get scanner with star info
		Scanner s = null;
		URL hyg = null;
		Sky sky = null;
		try {
			
			// use desired source
			switch (SOURCE) {
			case FILE_SYS: s = new Scanner(new File(filename)); break;
			case INTERNET: s = new Scanner(new URL(urlname).openStream()); break;
			}

			// csv using comma delimiters
			s.useDelimiter(",");

			// skip two lines
			s.nextLine(); // string headers
			s.nextLine(); // the Sun

			// instantiate sky
			sky = new Sky(s,MAG_CUTOFF); // sky
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		} finally {
			s.close();
		}
		
		XYSeries ser = new XYSeries("XYGraph"); //series of points
		// coordinate transfer object at Madison's latitude and longitude
		CoordTrans ct = new CoordTrans(LAT,LON);
		double[] xy; // array for points

		// go through visible stars in sky and get coordinate on flat surface
		for (Star st : sky) {
			xy = ct.getXY(st, 0, -0.15, 0);

			// add coordinates to point series
			ser.add(xy[0],xy[1]);
		}

		// add series to xy data
		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(ser);

		// make plot of positions
		JFreeChart chart = ChartFactory.createScatterPlot(
				"Local Sidereal Time = 00 00 00; Madison, WI; Alt = 0, Azimuth = 0","X","Y",data,PlotOrientation.VERTICAL,
				false,false,false);
		ChartFrame frame = new ChartFrame("first",chart);
		frame.pack();
		frame.setBounds(0, 0, 700, 700);
		frame.setVisible(true);
	}
}
