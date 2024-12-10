package com.myers;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

/**
 * @see BuildPlan
 */
public class BioRhythmsMain extends JFrame implements PropertyChangeListener, ItemListener {

	private static final long serialVersionUID = 1L;
	private JFrame jFrame;
	private Integer dayRange;
	private JFormattedTextField ftxtDayRange;
	private Calendar today = Calendar.getInstance();
	private Calendar startDateCal;
	private Calendar endDateCal;
	private JDatePickerImpl startDatePicker;
	private JDatePickerImpl endDatePicker;
	private String bioPropertiesFile = "src/com/myers/biorhythms.properties";
	private SimpleDateFormat argsDateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
	private Integer dayRangeDefault = 45;
	private BioRhythmsPlot biorhythmsPlot;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BioRhythmsMain verifyWindow = new BioRhythmsMain(args);
					verifyWindow.jFrame.setVisible(true);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "An error has occurred. /n");
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public BioRhythmsMain(String[] args) throws IOException, ParseException {

		initialize();
		biorhythmsPlot = new BioRhythmsPlot();
	}

	private void initialize() {
		Properties bioProperties = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream(bioPropertiesFile);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File " + bioProperties + " not found.");
			e.printStackTrace();
			System.exit(ERROR);
		}
		try {
			bioProperties.load(in);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Properties file load failed");
			e.printStackTrace();
			System.exit(ERROR);
		}
		try {
			in.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "File close failed");
			e.printStackTrace();
			System.exit(ERROR);
		}

		startDateCal = Calendar.getInstance();
		endDateCal = Calendar.getInstance();

		String startDateStr = bioProperties.getProperty("startDate");
		if (startDateStr == null) {
			bioProperties.put("startDate", today.toString());
			startDateCal = today;
		} else {
			try {
				startDateCal.setTime(argsDateFormat.parse(startDateStr));
			} catch (ParseException e) {
				JOptionPane.showMessageDialog(null, "Start date parse failed. ");
				e.printStackTrace();
				System.exit(ERROR);
			}
		}

		String endDateStr = bioProperties.getProperty("endDate");
		if (endDateStr == null) {
			bioProperties.put("endDate", today.toString());
			endDateCal = today;
		} else {
			try {
				endDateCal.setTime(argsDateFormat.parse(endDateStr));
			} catch (ParseException e) {
				JOptionPane.showMessageDialog(null, "End date parse failed. ");
				e.printStackTrace();
				System.exit(ERROR);
			}
		}

		String dayRangeStr = bioProperties.getProperty("dayRange");
		if (dayRangeStr == null) {
			dayRange = dayRangeDefault;
		} else {
			dayRange = Integer.parseInt(dayRangeStr);
		}

		// Don't re-arrange these elements.

		jFrame = new JFrame();
		jFrame.setFont(new Font("Tahoma", Font.BOLD, 12));
		jFrame.getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 12));
		jFrame.setTitle("Bio Rhythms - Main");
		jFrame.setResizable(false);

		jFrame.setBounds(0, 256, 475, 386);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.getContentPane().setLayout(null);

		JLabel lblDayRange = new JLabel("Days to Display:");
		lblDayRange.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDayRange.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblDayRange.setBounds(21, 60, 92, 24);
		jFrame.getContentPane().add(lblDayRange);

		JLabel lblMessage = new JLabel("Please enter correct data in proper fields");
		lblMessage.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
		lblMessage.setBounds(30, 238, 395, 28);
		jFrame.getContentPane().add(lblMessage);

		Properties dateProperties = new Properties();
		dateProperties.put("text.today", "Today");
		dateProperties.put("text.month", "Month");
		dateProperties.put("text.year", "Year");

		JButton btnReset = new JButton("Reset");
		btnReset.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnReset.setBounds(187, 170, 85, 21);
		jFrame.getContentPane().add(btnReset);
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// startDatePicker.getModel().setDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
				//		today.get(Calendar.DAY_OF_MONTH));
				endDatePicker.getModel().setDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
						today.get(Calendar.DAY_OF_MONTH));
				// String startDateStr = startDatePicker.getModel().getValue().toString();
				String endDateStr = endDatePicker.getModel().getValue().toString();
				ftxtDayRange.setValue(dayRangeDefault);
				String dayRangeStr = String.valueOf(dayRangeDefault);
				updateProperties(bioProperties, startDateStr, endDateStr, dayRangeStr);
				lblMessage.setText("Reset successful. All values returned to factory defaults.");
			}
		});

		JButton btnExit = new JButton("Exit");
		btnExit.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					submit(bioProperties);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			private void submit(Properties bioProperties) throws IOException {

				String startDateStr = startDatePicker.getModel().getValue().toString();
				String endDateStr = endDatePicker.getModel().getValue().toString();
				try {
					startDateCal.setTime(argsDateFormat.parse(startDateStr));
				} catch (ParseException e) {
					JOptionPane.showMessageDialog(null, "Start Date format failed");
					e.printStackTrace();
					System.exit(ERROR);
				}
				try {
					endDateCal.setTime(argsDateFormat.parse(endDateStr));
				} catch (ParseException e) {
					JOptionPane.showMessageDialog(null, "End Date format failed");
					e.printStackTrace();
					System.exit(ERROR);
				}

				// End date must not be earlier than start date
				if (endDateCal.compareTo(startDateCal) < 0) {
					JOptionPane.showMessageDialog(null, "End date must be the same as or greater than the start date");
					return;
				}

				String dayRangeStr = ftxtDayRange.getText();
				if (dayRange < 1) {
					JOptionPane.showMessageDialog(null, "Days to Display must be greater than zero");
					return;
				}

				String[] args = { startDateStr, endDateStr, dayRangeStr };
				try {
					biorhythmsPlot = new BioRhythmsPlot();
					biorhythmsPlot.main(args);
				} catch (ParseException e1) {
					e1.printStackTrace();
					System.exit(ERROR);
				}
				updateProperties(bioProperties, startDateStr, endDateStr, dayRangeStr);

				lblMessage.setText("Graph Complete");
			}
		});
		btnSubmit.setBounds(57, 170, 73, 21);
		jFrame.getContentPane().add(btnSubmit);

		btnExit.setBounds(329, 170, 73, 21);
		jFrame.getContentPane().add(btnExit);

		JLabel lblStartdate = new JLabel("Start Date:");
		lblStartdate.setHorizontalAlignment(SwingConstants.CENTER);
		lblStartdate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblStartdate.setBounds(22, 94, 78, 21);
		jFrame.getContentPane().add(lblStartdate);

		UtilDateModel startDateModel = new UtilDateModel();
		startDateModel.setDate(LocalDate.now().getYear(), LocalDate.now().getMonthValue(),
				LocalDate.now().getDayOfMonth());
		startDateModel.setSelected(true);
		JDatePanelImpl startDatePanel = new JDatePanelImpl(startDateModel, dateProperties);
		startDatePicker = new JDatePickerImpl(startDatePanel, new DateLabelFormatter());
		startDatePicker.setVisible(true);
		startDatePicker.setBounds(31, 120, 160, 121);
		startDatePicker.getModel().setDate(startDateCal.get(Calendar.YEAR), startDateCal.get(Calendar.MONTH),
				startDateCal.get(Calendar.DAY_OF_MONTH));
		jFrame.getContentPane().add(startDatePicker);

		JLabel lblEndDate = new JLabel("End Date:");
		lblEndDate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblEndDate.setHorizontalAlignment(SwingConstants.CENTER);
		lblEndDate.setBounds(260, 101, 60, 14);
		jFrame.getContentPane().add(lblEndDate);

		UtilDateModel endDateModel = new UtilDateModel();
		endDateModel.setDate(LocalDate.now().getYear(), LocalDate.now().getMonthValue(),
				LocalDate.now().getDayOfMonth());
		endDateModel.setSelected(true);
		JDatePanelImpl endDatePanel = new JDatePanelImpl(endDateModel, dateProperties);
		endDatePicker = new JDatePickerImpl(endDatePanel, new DateLabelFormatter());
		endDatePicker.setVisible(true);
		endDatePicker.setBounds(265, 121, 160, 121);
		endDatePicker.getModel().setDate(endDateCal.get(Calendar.YEAR), endDateCal.get(Calendar.MONTH),
				endDateCal.get(Calendar.DAY_OF_MONTH));
		jFrame.getContentPane().add(endDatePicker);

		JLabel lblTitle = new JLabel("Biorhythms");
		lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(142, 23, 139, 28);
		jFrame.getContentPane().add(lblTitle);

		ftxtDayRange = new JFormattedTextField();
		ftxtDayRange.setBounds(123, 64, 68, 19);
		ftxtDayRange.setValue(dayRange);
		jFrame.getContentPane().add(ftxtDayRange);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	}

	public void itemStateChanged(ItemEvent e) {
	}

	private void updateProperties(Properties bioProperties, String startDateStr, String endDateStr,
			String dayRangeStr) {
		bioProperties.setProperty("startDate", startDateStr);
		bioProperties.setProperty("endDate", endDateStr);
		bioProperties.setProperty("dayRange", dayRangeStr);

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(bioPropertiesFile);
		} catch (FileNotFoundException e2) {
			JOptionPane.showMessageDialog(null, "Open prop file for output failed");
			e2.printStackTrace();
			System.exit(ERROR);
		}
		try {
			bioProperties.store(out, "Save updated properties");
		} catch (IOException e4) {
			JOptionPane.showMessageDialog(null, "Prop file save failed");
			e4.printStackTrace();
			System.exit(ERROR);
		}
	}
}
