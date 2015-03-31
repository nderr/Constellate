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

/**
 * Driver class for rough plotting using JFreeChart
 * @author nderr
 */
public class SkyPlot {
	
	/**
	 * Reads in data from hygdata_v3.csv and plots star positions at Madison
	 */
	public static void main(String[] args) {
		Scanner s = null;
		try {
			s = new Scanner(new File("hygdata_v3.csv"));
			s.nextLine();
			s.nextLine();
		} catch (IOException e) {
			System.out.println("Problem with hyg file");
			System.exit(-1);
		}
		String[] info = null;
		
		List<Star> stars = new ArrayList<Star>();
		while (s.hasNextLine()) {
			info = s.nextLine().split(",");
			int id = Integer.parseInt(info[0]);
			double ra = Double.parseDouble(info[23]);
			double dec = Double.parseDouble(info[24]);
			double mag = Double.parseDouble(info[13]);
			stars.add(new Star(id,ra,dec,mag));
		}
		
		try {
			s = new Scanner(new File("const6.csv"));
		} catch (IOException e) {
			System.out.println("Problem with constellation file");
			System.exit(-1);
		}
		
		List<Constellation> cons = new ArrayList<Constellation>();
		String curr = "AND";
		List<Line> lines = new ArrayList<Line>();
		String code = null, name = null;
		int id1, id2;
		while (s.hasNextLine()) {
			info = s.nextLine().split(",");
			code = info[0];
			if (!curr.equals(code)) {
				cons.add(new Constellation(lines,name,curr));
				lines = new ArrayList<Line>();
				curr = code;
			}
			name = info[1];
			id1 = Integer.parseInt(info[2]);
			id2 = Integer.parseInt(info[3]);
			lines.add(new Line(id1,id2));
		}
		cons.add(new Constellation(lines,name,code));
		
		System.out.println("Stars: " + stars.size() + "\nConstellations: " + cons.size());
		
		final Sky sky = new Sky(stars,cons,5);
		System.out.println("Stars: " + sky.getStars().size() + "\nConstellations: " + sky.getConst().size());
		CoordTrans ct = new CoordTrans(43.07*Math.PI/180,-89.4*Math.PI/180,Math.PI/7,-.1,0,Math.PI/1.5);
		sky.addViewConst("UMA");
		sky.addViewConst("UMI");
		sky.addViewConst("DRA");
		
		sky.lookAt(ct);
		final SkyPlot sp = new SkyPlot();
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                Plot p = sp.new Plot(sky);
                p.setVisible(true);
            }
        });
		
		
		
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
			
			g2d.setColor(Color.blue);
			
			Dimension size = getSize();
	        Insets insets = getInsets();

	        int w = size.width - insets.left - insets.right;
	        int h = size.height - insets.top - insets.bottom;
	        
	        for (Star s : sky.getStarFOV()) {
	        	int x = s.getX(w,h);
	        	int y = s.getY(w,h);
	        	g2d.drawOval(x-1, y-1, 2, 2);
	        }
	        
	        for (Constellation c : sky.getConstFOV()) {
	        	int[] coords = c.plotLines(w,h);
	        	for (int i = 0; i < coords.length; i += 4) {
	        		int x1 = coords[i];
	        		int y1 = coords[i + 1];
	        		int x2 = coords[i + 2];
	        		int y2 = coords[i + 3];
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
