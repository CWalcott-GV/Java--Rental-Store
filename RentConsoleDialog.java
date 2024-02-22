package Project2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class RentConsoleDialog extends JDialog implements
        ActionListener {

    private JTextField txtRenterName;
    private JTextField txtDateRentedOn;
    private JTextField txtDateDueDate;
    private JComboBox<ConsoleTypes> comBoxConsoleType;
    private JButton okButton;
    private JButton cancelButton;
    private int closeStatus;
    private Console console;
    public static final int OK = 0;
    public static final int CANCEL = 1;

    /****************************************************************
     Instantiate a Custom Dialog as 'modal' and wait for the
     user to provide data and click on a button.

     @param parent reference to the JFrame application
     @param console an instantiated object to be filled with data
     */

    public RentConsoleDialog(JFrame parent, Console console) {
        // call parent and create a 'modal' dialog
        super(parent, true);
        this.console = console;

        setTitle("Console dialog box");
        closeStatus = CANCEL;
        setSize(500, 200);

        // prevent user from closing window
        setDefaultCloseOperation
                (WindowConstants.DO_NOTHING_ON_CLOSE);

        txtRenterName = new JTextField("Roger", 30);
        txtDateRentedOn = new JTextField(25);
        txtDateDueDate = new JTextField(25);
        comBoxConsoleType = new JComboBox<>(ConsoleTypes.values());

        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter =
                new SimpleDateFormat("MM/dd/yyyy");
        String dateNow = formatter.format(currentDate.getTime());
        currentDate.add(Calendar.DATE, 1);
        String datetomorrow =
                formatter.format(currentDate.getTime());

        txtDateRentedOn.setText(dateNow);
        txtDateDueDate.setText(datetomorrow);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(4, 2));

        textPanel.add(new JLabel("Name of Renter: "));
        textPanel.add(txtRenterName);
        textPanel.add(new JLabel("Date Rented On "));
        textPanel.add(txtDateRentedOn);
        textPanel.add(new JLabel("Date Due (est.): "));
        textPanel.add(txtDateDueDate);
        textPanel.add(new JLabel("ConsoleType"));
        textPanel.add(comBoxConsoleType);

        getContentPane().add(textPanel, BorderLayout.CENTER);

        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

        setVisible(true);
    }

    /****************************************************************
     Respond to either button clicks
     @param e the action event that was just fired
     */
    public void actionPerformed(ActionEvent e) {

        JButton button = (JButton) e.getSource();

        // if OK clicked the fill the object
        if (button == okButton) {
            // save the information in the object
            closeStatus = OK;
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            df.setLenient(false);


            Date d1 = null;
            Date d2 = null;
            try {
                GregorianCalendar gregTemp = new GregorianCalendar();
                gregTemp.setLenient(false);
                d1 = df.parse(txtDateRentedOn.getText());
                gregTemp.setTime(d1);
                console.setRentedOn(gregTemp);

                gregTemp = new GregorianCalendar();
                gregTemp.setLenient(false);
                d2 = df.parse(txtDateDueDate.getText());
                gregTemp.setTime(d2);
                console.setDueBack(gregTemp);

                console.setNameOfRenter(txtRenterName.getText());
                console.setConsoleType((ConsoleTypes)
                        comBoxConsoleType.getSelectedItem());

                if ((ConsoleTypes) comBoxConsoleType.getSelectedItem()
                        == ConsoleTypes.NoSelection) {
                    JOptionPane.showMessageDialog(null,
                            "Select Console.");
                    closeStatus = CANCEL;
                }
                if (console.dueBack.before(console.rentedOn))
                    throw new IllegalArgumentException();

            } catch (IllegalArgumentException improperDates) {
                closeStatus = CANCEL;
                JOptionPane.showMessageDialog(null,
                        "Due date cannot be before rental date. " +
                                "Please try again.");

            } catch (ParseException e1) {
                closeStatus = CANCEL;
                JOptionPane.showMessageDialog(null,
                        "Error formatting date. Please " +
                                "try again with a " +
                                "valid date.");
            }
        }

        // make the dialog disappear
        dispose();
    }

    /****************************************************************
     Return a String to let the caller know which button
     was clicked

     @return an int representing the option OK or CANCEL
     */
    public int getCloseStatus() {
        return closeStatus;
    }
}