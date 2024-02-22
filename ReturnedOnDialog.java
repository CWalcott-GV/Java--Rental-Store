package Project2;


import com.sun.deploy.security.SelectableSecurityManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class ReturnedOnDialog extends JDialog implements ActionListener {

	private JTextField txtDate;

	private JButton okButton;
	private JButton cancelButton;
	private int closeStatus;
	private Rental unit;

	static final int OK = 0;
	static final int CANCEL = 1;

	/*********************************************************
	 Instantiate a Custom Dialog as 'modal' and wait for the
	 user to provide data and click on a button.

	 @param parent reference to the JFrame application
	 @param unit an instantiated object to be filled with data
	 *********************************************************/

	public ReturnedOnDialog(JFrame parent, Rental unit) {
		// call parent and create a 'modal' dialog
		super(parent, true);

		this.unit = unit;
		setTitle("Returned dialog box");
		closeStatus = CANCEL;
		setSize(300,100);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setLenient(false);

        txtDate = new JTextField(dateFormat.format(unit.
                getDueBack().getTime()),30);

		JPanel textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(1,2));
		textPanel.add(new JLabel("Returned Date: "));
		textPanel.add(txtDate);

		getContentPane().add(textPanel, BorderLayout.CENTER);

		// Instantiate and display two buttons
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);

		setVisible (true);
	}

	/**************************************************************
	 Respond to either button clicks
	 @param e the action event that was just fired
	 **************************************************************/
	public void actionPerformed(ActionEvent e) {

		JButton button = (JButton) e.getSource();

		// if OK clicked the fill the object
		if (button == okButton) {
			// save the information in the object
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			df.setLenient(false);
			GregorianCalendar gTemp = new GregorianCalendar();
			gTemp.setLenient(false);

			Date d = null;
			try {
				d = df.parse(txtDate.getText());
				gTemp.setTime(d);
				unit.setActualDateReturned(gTemp);

				if (gTemp.after(unit.rentedOn)) {
					unit.setActualDateReturned(gTemp);
					closeStatus = OK;
				}
				else {
					throw new IllegalArgumentException();
				}

			} catch (ParseException e1) {
				closeStatus = CANCEL;
				JOptionPane.showMessageDialog(null,
						"Error formatting date. Please " +
								"try again with a " +
								"valid date.");
			} catch (IllegalArgumentException improperDates) {
				JOptionPane.showMessageDialog(null,
						"Return date cannot be before rental date. " +
								"Please try again.");
				closeStatus = CANCEL;
			}

		}

		// make the dialog disappear
		dispose();
	}

	/**************************************************************
	 Return a String to let the caller know which button
	 was clicked

	 @return an int representing the option OK or CANCEL
	 **************************************************************/
	public int getCloseStatus(){
		return closeStatus;
	}

}

