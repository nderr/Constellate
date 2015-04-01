import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.google.gson.Gson;

/**
 * Driver class for rough plotting using JFreeChart
 * @author nderr
 */
public class SkyPlot {
	
	/**
	 * Reads in data from hygdata_v3.csv and plots star positions at Madison
	 */
	public static void main(String[] args) {
		long t1 = System.currentTimeMillis();
		Gson gson = new Gson();
		Scanner s = null;
		try {
			s = new Scanner(new File("stars.json"));
		} catch (IOException e) {
			System.out.println("Problem with stars.json");
			System.exit(-1);
		}
		
		List<Star> stars = new ArrayList<Star>();
		while (s.hasNextLine()) {
			stars.add(gson.fromJson(s.nextLine(),Star.class));
		}
		long t2 = System.currentTimeMillis();
		
		try {
			s = new Scanner(new File("const.json"));
		} catch (IOException e) {
			System.out.println("Problem with const.json");
			System.exit(-1);
		}
		
		List<Constellation> cons = new ArrayList<Constellation>();
		while (s.hasNextLine()) {
			cons.add(gson.fromJson(s.nextLine(),Constellation.class));
		}
		
		long t3 = System.currentTimeMillis();
		
		final Sky sky = new Sky(stars,cons,6.5);
		
		sky.addViewConst();
		
		long t4 = System.currentTimeMillis();
		
		CoordTrans ct = new CoordTrans(43.07*Math.PI/180,-89.4*Math.PI/180,Math.PI/7,0,0,Math.PI/1.5);
		
		sky.lookAt(ct);
		
		long t5 = System.currentTimeMillis();
		
		final SkyPlot sp = new SkyPlot();
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                Plot p = sp.new Plot(sky);
                p.setVisible(true);
            }
        });
		
		
		long t6 = System.currentTimeMillis();
		int d1 = (int) (t2 - t1);
		int d2 = (int) (t3 - t2);
		int d3 = (int) (t4 - t3);
		int d4 = (int) (t5 - t4);
		int d5 = (int) (t6 - t5);
		System.out.println("Loading stars: " + d1);
		System.out.println("Loading constellations: " + d2);
		System.out.println("Making sky: " + d3);
		System.out.println("Pointing to sky: " + d4);
		System.out.println("Plotting: " + d5);
	}
	
	@SuppressWarnings("serial")
	class Plot extends JFrame {

	    public Plot(Sky sky) {

	        initUI(sky);
	    }

	    public void initUI(Sky sky) {
	        
	        setTitle("Points");
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	        add(new Screen(sky));

	        setSize(350, 250);
	        setLocationRelativeTo(null);
	    }
	}
	
	@SuppressWarnings("serial")
	class Screen extends JPanel {
		
		private Sky sky;
		
		public Screen(Sky sky) {
			this.sky = sky;
		}
		
		private void doDrawing(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			
			Dimension size = getSize();
	        Insets insets = getInsets();

	        int w = size.width - insets.left - insets.right;
	        int h = size.height - insets.top - insets.bottom;
	        
	        setBackground(Color.BLUE);
	        
	        for (Star s : sky.getStarFOV()) {
	        	int x = s.getX(w,h);
	        	int y = s.getY(w,h);
	        	int r = (int) (6 - s.getMag());
	        	g2d.setColor(Color.YELLOW);
	        	g2d.fillOval(x-r, y-r, 2*r, 2*r);
	        }
	        
	        for (Constellation c : sky.getConstFOV()) {
	        	int[] coords = c.getLines(w,h);
	        	for (int i = 0; i < coords.length; i += 4) {
	        		int x1 = coords[i];
	        		int y1 = coords[i + 1];
	        		int x2 = coords[i + 2];
	        		int y2 = coords[i + 3];
	        		g2d.setColor(Color.WHITE);
	        		g2d.drawLine(x1, y1, x2, y2);
	        	}
	        }
	        
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			doDrawing(g);
		}
	}
}
