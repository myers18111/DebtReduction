package com.myers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class BioRhythmsPlot {

	/**
	 * @param args
	 * @throws ParseException
	 */
	public void main(String[] args) throws ParseException {

		if (args.length == 0) {
			System.out.println("No parameters found. Run com.BioRhythmsMain instead");
			System.exit(0);
		}

		for (int x = 0; x < args.length; x++) {
			System.out.println("args[" + x + "] = " + args[x]);
		}

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
		int counter = 0;
		short halfPageSpan = 40;
		while (counter < 2 * dayRange + 1) {

			// line prefix
			System.out.print(String.format(
					String.format("%1$7s", daysSpan) + " " + dispDateFormat.format(endCalendar.getTime()) + " "));

			short physical = (short) (halfPageSpan * Math.sin((2 * Math.PI * daysSpan) / 23) + halfPageSpan);
			short emotional = (short) (halfPageSpan * Math.sin((2 * Math.PI * daysSpan) / 28) + halfPageSpan);
			short intellectual = (short) (halfPageSpan * Math.sin((2 * Math.PI * daysSpan) / 33) + halfPageSpan);
			
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