package com.myers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class AppGraphWork2 extends JFrame {

	private static final long serialVersionUID = 2645035245848030795L;

	public void main(String[] args) throws IOException, ParseException {

		if (args == null || args.length == 0) {
			System.out.println("No parameters found. Run com.BioRhythmsMain instead");
			System.exit(0);
		}

		for (int x = 0; x < args.length; x++) {
			System.out.println("args[" + x + "] = " + args[x]);
		}

		initialize(args);
		pack();
		setVisible(true);
	}

	private void initialize(String[] args) throws IOException, ParseException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());

		// allow the escape key to end the program
		contentPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Escape");
		contentPane.getActionMap().put("Escape", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		// add the content pane to the frame
		add(contentPane, BorderLayout.CENTER);

		// add the graph to the content pane
		contentPane.add(new SineWaveNT2(args), BorderLayout.CENTER);

		// create panel for button(s)
		JPanel btnPanel = new JPanel();
		// add button pane to the content pane
		contentPane.add(btnPanel, BorderLayout.SOUTH);

		// create the OK button button
		JButton btnOk = new JButton("OK");
		btnOk.setActionCommand("OK");
		btnOk.setMnemonic(KeyEvent.VK_O);
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		// add the button to the button pane
		btnPanel.add(btnOk, BorderLayout.SOUTH);

		// enable ENTER as the default key
		InputMap btnOkInputMap = btnOk.getInputMap();
		btnOkInputMap.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
		btnOkInputMap.put(KeyStroke.getKeyStroke("released ENTER"), "released");
	}

}

class SineWaveNT2 extends JPanel {

	private static final long serialVersionUID = -8105637392134732004L;
	// use constants for better readability
	private static final double WIDTH_600 = 600, HEIGHT_700 = 700, AMPLITUDE = HEIGHT_700 / 3, INTERVAL_50 = 50;
	private static final int MARGINS_30 = 30, GAP_15 = 15, DOT_SIZE_3 = 3;
	// starting x coordinate
	private static final double xAxisHeight = HEIGHT_700 / 2;
	private static final double RIGHT_LIMIT = WIDTH_600 - MARGINS_30;
	// x increment
	private final int dX = 1;
	private FileOutputStream fileOut;
	// use doubles to avoid rounding errors and to have non-integer coordinates
	private final List<Point2D.Double> pPoints;
	private final List<Point2D.Double> ePoints;
	private final List<Point2D.Double> iPoints;

	public SineWaveNT2(String[] args) throws IOException, ParseException {

		SimpleDateFormat argsDateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");

		// StartDate - args[0]
		Date parsedStartDate = argsDateFormat.parse(args[0]);
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(parsedStartDate);

		// endDate - args[1]
		Calendar endCalendar = Calendar.getInstance();
		Date parsedEndDate = argsDateFormat.parse(args[1]);
		endCalendar.setTime(parsedEndDate);

		// dayRange - args[2]
		Integer dayRange = Integer.parseInt(args[2]);

		SimpleDateFormat dispDateFormat = new SimpleDateFormat("MM-dd-yyyy");
		System.out.println("The Start date is " + dispDateFormat.format(startCalendar.getTime()));
		System.out.println("The End date is.. " + dispDateFormat.format(endCalendar.getTime()));

		long daysSpan = (endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / (1000 * 60 * 60 * 24);
		System.out.println("There are " + daysSpan + " days between the start and end dates");

		endCalendar.add(Calendar.DAY_OF_MONTH, dayRange * -1);
		daysSpan -= dayRange;

		// File in which to save each x,y coordinate
		File file = new File("src/com/myers/BiorhythmsPoints.txt");
		if (!file.exists()) {
			file.createNewFile();
		}

		// Always start with an empty file
		fileOut = new FileOutputStream(file, true);

		setPreferredSize(new Dimension((int) WIDTH_600, (int) HEIGHT_700));
		pPoints = new ArrayList<>();
		ePoints = new ArrayList<>();
		iPoints = new ArrayList<>();
		addPoints(args, dayRange, daysSpan, endCalendar);
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

		paintPPoints(g2);
		paintEPoints(g2);
		paintIPoints(g2);
	}

	private void addPoints(String[] args, Integer dayRange, long daysSpan, Calendar endCalendar) throws IOException {

		// line prefix
		SimpleDateFormat dispDateFormat = new SimpleDateFormat("MM-dd-yyyy");

		System.out.println("daySpan=" + daysSpan + ", dayRange=" + dayRange);
		
		int counter = 0;
		while (counter < (2 * dayRange + 1)) {

			System.out.println(String.format(
					String.format("%1$7s", daysSpan) + " " + dispDateFormat.format(endCalendar.getTime()) + " "));
			
			double angle = 2 * Math.PI * daysSpan / 23;
			double y = xAxisHeight - AMPLITUDE * Math.sin(angle);
			double x = (counter + MARGINS_30) * (WIDTH_600 / (dayRange * 4));
			pPoints.add(new Point2D.Double(x, y));
			
			

			endCalendar.add(Calendar.DAY_OF_MONTH, 1);
			daysSpan++;
			counter++;

		}

			
			
			
		
		for (int x = MARGINS_30; x < RIGHT_LIMIT; x += dX) {
			// angle in radians
//			double angle = 2 * Math.PI * (((x / .23) - MARGINS_30) / (WIDTH_600 - 2 * MARGINS_30));
//			double y = xAxisHeight - AMPLITUDE * Math.sin(angle);
//			pPoints.add(new Point2D.Double(x, y));
	
			double angle = 2 * Math.PI * (((x / .28) - MARGINS_30) / (WIDTH_600 - 2 * MARGINS_30));
			double y = xAxisHeight - AMPLITUDE * Math.sin(angle);
			ePoints.add(new Point2D.Double(x, y));
	
			angle = 2 * Math.PI * (((x / .33) - MARGINS_30) / (WIDTH_600 - 2 * MARGINS_30));
			y = xAxisHeight - AMPLITUDE * Math.sin(angle);
			iPoints.add(new Point2D.Double(x, y));
	
			repaint();
		}
	}

	private void paintPPoints(Graphics2D g2) {
		// color of graph
		g2.setColor(Color.blue);
		for (Point2D.Double p : pPoints) {
			Shape point = new Ellipse2D.Double(p.getX(), p.getY(), DOT_SIZE_3, DOT_SIZE_3);
			g2.draw(point);

			savePointToFile("P", p);

			// display the coordinates on the curve, at x intervals of 50
			if (p.getX() % INTERVAL_50 == 0) {
				DrawCoords(g2, p);
			}
		}
	}

	private void paintEPoints(Graphics2D g2) {
		// color of graph
		g2.setColor(Color.red);
		for (Point2D.Double e : ePoints) {
			Shape point = new Ellipse2D.Double(e.getX(), e.getY(), DOT_SIZE_3, DOT_SIZE_3);
			g2.draw(point);

			savePointToFile("E", e);

			// display the coordinates on the curve, at x intervals of 50
			if (e.getX() % INTERVAL_50 == 0) {
				DrawCoords(g2, e);
			}
		}
	}

	private void paintIPoints(Graphics2D g2) {
		// color of graph
		g2.setColor(Color.green);
		for (Point2D.Double i : iPoints) {
			Shape point = new Ellipse2D.Double(i.getX(), i.getY(), DOT_SIZE_3, DOT_SIZE_3);
			g2.draw(point);

			savePointToFile("I", i);

			// display the coordinates on the curve, at x intervals of 50
			if (i.getX() % INTERVAL_50 == 0) {
				DrawCoords(g2, i);
			}
		}
	}

	private void DrawCoords(Graphics2D g2, Point2D.Double i) {
		String xAsAString = String.valueOf(i.getX()).substring(0, String.valueOf(i.getX()).indexOf("."));
		String yAsAString = String.valueOf(i.getY()).substring(0, String.valueOf(i.getY()).indexOf("."));
		char[] coords = ("(" + xAsAString + "," + yAsAString + ")").toCharArray();
		g2.drawChars(coords, 0, coords.length, (int) i.getX(), (int) i.getY());
	}

	private void savePointToFile(String whichPoint, Point2D.Double p) {
		try {
			// save x,y dimensions
			String xyCoordinates = whichPoint + ": x=" + p.getX() + " y=" + p.getY() + "\n";
			fileOut.write(xyCoordinates.getBytes());
		} catch (IOException e) {
			System.out.println("Write failed: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void addPoints2(String[] args, Integer dayRange, long daysSpan, Calendar endCalendar) {
		SimpleDateFormat dispDateFormat = new SimpleDateFormat("MM-dd-yyyy");
		short halfPageSpan = 40;
		daysSpan -= dayRange;
		int counter = 0;
		while (counter < 2 * dayRange + 1) {

			// line prefix
			System.out.print(String.format(
					String.format("%1$7s", daysSpan) + " " + dispDateFormat.format(endCalendar.getTime()) + " "));

			short physical = (short) (Math.sin((2 * Math.PI * daysSpan) / 23));
			short emotional = (short) (Math.sin((2 * Math.PI * daysSpan) / 28));
			short intellectual = (short) (Math.sin((2 * Math.PI * daysSpan) / 33));

			//
			// TreeMap - Automatically sorted, no duplicates allowed
			// key = sin(x)
			// Value = marker (*, P, E, I, X, &, $
			// Array size = 4 (or less)

			Map<Short, String> plots = new TreeMap<Short, String>();

			//
			//

			// Add the center line position to the map, with its corresponding "*"
			plots.put(halfPageSpan, "*");

			// If the position of the plot point for the Physical wave is the same as the
			// position of the center-line, change the asterisk to an X to show crossed
			// lines
			if (plots.containsKey(physical)) {
				plots.put(physical, "X");
			} else {
				plots.put(physical, "P");
			}

			// etc
			if (plots.containsKey(emotional)) {
				if (plots.get(emotional) == "*" || plots.get(emotional) == "P") {
					plots.put(emotional, "X");
				} else if (plots.get(emotional) == "X") {
					plots.put(emotional, "&");
				}
			} else {
				plots.put(emotional, "E");
			}

			// An "X" means that two waves cross at this point
			// An "&" means that three waves cross at this point
			// A "$" means that four waves cross at this point
			if (plots.containsKey(intellectual)) {
				if (plots.get(intellectual) == "*" || plots.get(intellectual) == "E"
						|| plots.get(intellectual) == "P") {
					plots.put(intellectual, "X");
				} else if (plots.get(intellectual) == "X") {
					plots.put(intellectual, "&");
				} else if (plots.get(intellectual) == "&") {
					plots.put(intellectual, "$");
				}
			} else {
				plots.put(intellectual, "I");
			}

			int sp = 1;

//			// Display the four values - testing only
//			int fourCnt = 0;
//			for (Short position : plots.keySet()) {
//				System.out.format("%02d ", position);
//				fourCnt++;
//			}
//			for (; fourCnt < 4; fourCnt++) {
//				System.out.print("   ");
//			}

			// plot the four data points (I, E, P, and the central axis)
			for (Short position : plots.keySet()) {
				String marker = plots.get(position);

				// print spaces before first point and between other points
				for (; sp < position; sp++) {
					System.out.print(" ");
				}
				System.out.print(marker);
				sp++;
			}

			System.out.println("");
			endCalendar.add(Calendar.DAY_OF_MONTH, 1);
			daysSpan++;
			counter++;
		}

	}
}