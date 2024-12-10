package com.myers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class AppGraphWork extends JFrame {

	private static final long serialVersionUID = 2645035245848030795L;

	public AppGraphWork() throws IOException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(new SineWaveNT3());
		pack();
		setVisible(true);
	}

	public static void main(String[] args) throws IOException {
		new AppGraphWork();
	}
}

class SineWaveNT3 extends JPanel {

	private static final long serialVersionUID = -8105637392134732004L;
	// use constants for better readability
	private static final double WIDTH_600 = 600, HEIGHT_700 = 700, AMPLITUDE = HEIGHT_700 / 3, INTERVAL_50 = 50;
	private static final int MARGINS_30 = 30, GAP_15 = 15, DOT_SIZE_3 = 3;
	// starting x coordinate
	private final double xAxisHeight = HEIGHT_700 / 2;
	// x increment
	private final int dX = 1;
	private FileOutputStream fileOut;
	// use doubles to avoid rounding errors and to have non-integer coordinates
	private final List<Point2D.Double> points;

	public SineWaveNT3() throws IOException {
		// File in which to save each x,y coordinate
		File file = new File("src/com/BiorhythmsPoints.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		
		// Always start with an empty file
		fileOut = new FileOutputStream(file, true);
		
		setPreferredSize(new Dimension((int) WIDTH_600, (int) HEIGHT_700));
		points = new ArrayList<>();
		addPoints();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;

		Shape xAxis = new Line2D.Double(MARGINS_30, HEIGHT_700 / 2, WIDTH_600 - MARGINS_30, HEIGHT_700 / 2);
		g2.draw(xAxis);

		Shape yAxis = new Line2D.Double(MARGINS_30, MARGINS_30, MARGINS_30, HEIGHT_700 - MARGINS_30);
		g2.draw(yAxis);

		// Label this point with its values
		g.drawString("I", (int) (WIDTH_600 / 2 - GAP_15), MARGINS_30);
		g.drawString("V'", (int) (WIDTH_600 - GAP_15), (int) (HEIGHT_700 / 2 + GAP_15));

		// color of graph
		g2.setColor(Color.blue);
		for (Point2D.Double p : points) {
			Shape point = new Ellipse2D.Double(p.getX(), p.getY(), DOT_SIZE_3, DOT_SIZE_3);
			g2.draw(point);
			
			// save x,y dimensions
			try {
				String xyCoordinates = "x=" + p.getX() + " y=" + p.getY() + "\n";
				fileOut.write(xyCoordinates.getBytes());
			} catch (IOException e) {
				System.out.println("Write failed: " + e.getMessage());
				e.printStackTrace();
				System.exit(0);
			}
			
			// draw coordinates on the curve, at x intervals of 50
			if (p.getX() % INTERVAL_50 == 0) {
		        String xAsAString = String.valueOf(p.getX()).substring(0, String.valueOf(p.getX()).indexOf("."));
		        String yAsAString = String.valueOf(p.getY()).substring(0, String.valueOf(p.getY()).indexOf("."));
		        char[] coords = ("(" +  xAsAString +  "," + yAsAString + ")").toCharArray();
				g2.drawChars(coords, 0, coords.length, (int) p.getX(), (int) p.getY());
			}
		}
	}

	private void addPoints() throws IOException {
		
		for (int x = MARGINS_30; x < WIDTH_600 - MARGINS_30; x += dX) {
			// angle in radians
			double angle = 2 * Math.PI * ((x - MARGINS_30) / (WIDTH_600 - 2 * MARGINS_30));
			double y = xAxisHeight - AMPLITUDE * Math.sin(angle);
			points.add(new Point2D.Double(x, y));
			repaint();
		}
	}
	
}