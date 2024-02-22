package projPack2;

import javax.swing.table.AbstractTableModel;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/****************************************************************
 * Rental Store Project 2
 * CIS163 - 07 Ferguson
 * @author Corey Walcott and Wilson Armstrong
 *
 * @version V3.5 
 *
 * This project is a rental store that, "creates a program
 * that helps camping owners manage their inventory...should be
 * able to rent games and console sites from your program."
 ***************************************************************/

public class ListModel extends AbstractTableModel {

    /**
     * holds all the rentals
     */
    private ArrayList<Rental> listOfRentals;

    /**
     * holds only the rentals that are to be displayed
     */
    public ArrayList<Rental> filteredListRentals;

    /**
     * current screen being displayed
     */
    private ScreenDisplay display =
            ScreenDisplay.CurrentRentalStatus;

    /**
     * Below are string arrays of the column names for the
     * different screens
     */
    private String[] columnNamesCurrentRentals = {"Renter\'s Name",
            "Est. Cost", "Rented On", "Due Date ", "Console",
            "Name of the Game"};
    private String[] columnNamesReturned = {"Renter\'s Name",
            "Rented On Date", "Due Date", "Actual date returned ",
            "Est. Cost", " Real Cost"};
    private String[] columnNamesEverything = {"Renter\'s Name",
            "Rented On Date", "Due Date", "Actual Date Returned",
            "Est. Cost", "Real Cost", "Console", "Name of the Game"};

    /**
     * Formatter for the date format for rental dates
     */
    private DateFormat formatter =
            new SimpleDateFormat("MM/dd/yyyy");

    /****************************************************************
     * Constructor that sets display to initially be
     * CurrentRentalStatus, creates the list of rentals array and
     * filtered list of rentals array, then it updates the screen
     * and creates the list of GUI
     ***************************************************************/
    public ListModel() {
        display = ScreenDisplay.CurrentRentalStatus;
        listOfRentals = new ArrayList<>();
        filteredListRentals = new ArrayList<>();
        updateScreen();
        createList();
    }

    /****************************************************************
     * Sets display to specified screen, then updates screen
     * @param selected
     ***************************************************************/
    public void setDisplay(ScreenDisplay selected) {
        display = selected;
        updateScreen();
    }

    /****************************************************************
     * Updates screen based on current display
     * This is where the program sorts and filters the list of
     * rentals to display the correct information
     ***************************************************************/
    public void updateScreen() {
        switch (display) {
            case CurrentRentalStatus:
                filteredListRentals = (ArrayList<Rental>) listOfRentals.stream()
                        .filter(n -> n.actualDateReturned == null)
                        .collect(Collectors.toList());

                Collections.sort(filteredListRentals,
                        (n1, n2) -> n1.nameOfRenter.compareTo(n2.nameOfRenter));
                break;

            case ReturnedItems:
                filteredListRentals = (ArrayList<Rental>) listOfRentals.stream()
                        .filter(n -> n.actualDateReturned != null)
                        .collect(Collectors.toList());

                Collections.sort(filteredListRentals, new Comparator<Rental>() {
                    @Override
                    public int compare(Rental n1, Rental n2) {
                        return n1.nameOfRenter.compareTo(n2.nameOfRenter);
                    }
                });
                break;

            /********************************************************
             * The lambda function below uses two filters initially.
             *  -if the current rental item has a returned date that
             *   is null, then show them
             *  -a method created below that checks if there are 7
             *   days between the due date and rented on date.
             *******************************************************/

            case DueWithInWeek:

                filteredListRentals = (ArrayList<Rental>) listOfRentals.stream()
                        .filter(n -> n.actualDateReturned == null)
                        .filter(n -> dueIn7DaysFilter(n))
                        .collect(Collectors.toList());
            break;

            /********************************************************
             * The lambda function below uses two filters initially.
             *  -if the current rental item has a returned date that
             *   is null, then show them
             *  -a method created below that checks if there are 7
             *   days between the due date and rented on date.

             * Sort Function: sorts the name of renter
             * alphabetically.

             * @Override using an anonymous class to override the
             * default object compareTo method.

             * @return integers -1, 0, 1 comparing consoles and
             * game object names alphabetically AFTER the filter.
             *******************************************************/

            case DueWithinWeekGamesFirst:
                filteredListRentals = (ArrayList<Rental>) listOfRentals.stream()
                        .filter(n -> n.actualDateReturned == null)
                        .filter(n -> dueIn7DaysFilter(n))
                        .collect(Collectors.toList());

                Collections.sort(filteredListRentals, (n1, n2) -> n2.getNameOfRenter()
                                .compareTo(n1.nameOfRenter));

                Collections.sort(filteredListRentals, new Comparator<Rental>(){
                    @Override
                    public int compare(Rental n1, Rental n2){
                        if(n1 instanceof Game){
                            n1.getNameOfRenter().compareTo(n2.nameOfRenter);
                            return -1;
                        }
                        if(n1 instanceof Console){
                            n1.getNameOfRenter().compareTo(n2.nameOfRenter);
                            return 1;
                        }
                        else {
                            return 0;
                        }
                    }
                });
                break;

            /********************************************************
             * Filters by rentals that do not have an actual
             * returned date.
             *
             * Uses the default compareTo method to sort
             * alphabetically. Then, using two separate lists;
             *  -filters the first list by rentals that have between
             *   7 and 14 days between the rented on date and
             *   the due date.
             *   -filters the second list by rentals that have more
             *   than 14 days between the rented on date and due
             *   date, then, capitalizes every letter in the
             *   renter's name.
             *
             * Finally, this lambda function puts both lists,
             * one after the other into the
             * output of 'filteredlistrentals'.
             *******************************************************/

            case Cap14DaysOverdue:

                filteredListRentals = (ArrayList<Rental>)
                        listOfRentals.stream()
                        .filter(n -> n.actualDateReturned == null)
                        .collect(Collectors.toList());

                Collections.sort(filteredListRentals,
                        (n1,n2) -> n1.nameOfRenter.compareTo(n2.nameOfRenter));

                ArrayList<Rental> list1 = (ArrayList<Rental>)
                        filteredListRentals.stream()
                        .filter(n -> dueIn7To14DaysFilter(n))
                        .collect(Collectors.toList());


                ArrayList<Rental> list2= (ArrayList<Rental>)
                        filteredListRentals.stream()
                        .filter(n -> dueIn14DaysOrMoreFilter(n))
                        .collect(Collectors.toList());

                list2.addAll(list1);
                filteredListRentals = list2;

                break;

            /********************************************************
             * This lambda function shows every rental item available.
             * Sort Function: sorts alphabetically using the default
             * compareTo method.
             *******************************************************/

            case everythingScreen:
                filteredListRentals = (ArrayList<Rental>)
                        listOfRentals.stream()
                        .collect(Collectors.toList());


                Collections.sort(filteredListRentals,
                        (n1, n2) -> n1.nameOfRenter.compareTo(n2.nameOfRenter));


                break;

            /********************************************************
             * Filters Function: passes current rental object to the
             * created method that filters by if the current rental
             * item is has consoleType "Playstation 4".
             *
             * Then, using two separate lists;
             *  -filters the first list by rentals that have less
             *  than 7 days between the rented on date and due date.
             *  -filters the second list by rentals that are outside
             *  the previous filter, everything else basically.
             *
             * Finally, this lambda function puts both lists,
             * one after the other into the
             * output of 'filteredlistrentals'.
             *
             * Uses the default compareTo method
             * to sort alphabetically.
             *******************************************************/

            case playstation4Screen:
                filteredListRentals = (ArrayList<Rental>)
                        listOfRentals.stream()
                        .filter(n -> playstation4Filter(n))
                        .collect(Collectors.toList());

                list1 = (ArrayList<Rental>)
                        filteredListRentals.stream()
                        .filter(n -> dueIn7DaysFilter(n))
                        .collect(Collectors.toList());

                list2 = (ArrayList<Rental>)
                        filteredListRentals.stream()
                        .filter(n -> !dueIn7DaysFilter(n))
                        .collect(Collectors.toList());

                list2.addAll(list1);

                filteredListRentals = list2;
                Collections.sort(filteredListRentals,
                        (n1, n2) -> n1.nameOfRenter.compareTo(n2.nameOfRenter));

                break;

            /********************************************************
             * Filters Function: passes current rental object to the
             * created method that filters by if the current rental
             * item is has consoleType "XboxOneS".
             *
             * Then, using two separate lists;
             * -filters the first list by rentals that have less
             * than 7 days between the rented on date and due date.
             * -filters the second list by rentals that are outside
             * the previous filter, everything else basically.
             *
             * Finally, this lambda function puts both lists,
             * one after the other into
             * the output of 'filteredlistrentals'.
             *
             * Uses the default compareTo method to
             * sort alphabetically.
             *******************************************************/

            case xboxOneSScreen:
                filteredListRentals = (ArrayList<Rental>)
                        listOfRentals.stream()
                        .filter(n -> xboxOneSFilter(n))
                        .collect(Collectors.toList());

                list1 = (ArrayList<Rental>)
                        filteredListRentals.stream()
                        .filter(n -> dueIn7DaysFilter(n))
                        .collect(Collectors.toList());

                list2 = (ArrayList<Rental>)
                        filteredListRentals.stream()
                        .filter(n -> !dueIn7DaysFilter(n))
                        .collect(Collectors.toList());

                list2.addAll(list1);

                filteredListRentals = list2;
                Collections.sort(filteredListRentals,
                        (n1, n2) -> n1.nameOfRenter.compareTo(n2.nameOfRenter));

                break;

            /********************************************************
             * Filters Function: passes current rental object to the
             * created method that filters by if the current rental
             * item is has consoleType "Playstation4Pro".
             *
             * Then, using two separate lists;
             * -filters the first list by rentals that have less
             * than 7 days between the rented on date and due date.
             * -filters the second list by rentals that are outside
             * the previous filter, everything else basically.
             *
             * Finally, this lambda function puts both lists,
             * one after the other into
             * the output of 'filteredlistrentals'.
             *
             * Uses the default compareTo method to
             * sort alphabetically.
             *******************************************************/

            case playstation4ProScreen:
                filteredListRentals = (ArrayList<Rental>)
                        listOfRentals.stream()
                        .filter(n -> playstation4ProFilter(n))
                        .collect(Collectors.toList());

                list1 = (ArrayList<Rental>)
                        filteredListRentals.stream()
                        .filter(n -> dueIn7DaysFilter(n))
                        .collect(Collectors.toList());

                list2 = (ArrayList<Rental>)
                        filteredListRentals.stream()
                        .filter(n -> !dueIn7DaysFilter(n))
                        .collect(Collectors.toList());

                list2.addAll(list1);

                filteredListRentals = list2;
                Collections.sort(filteredListRentals, (n1, n2) -> n1.nameOfRenter.compareTo(n2.nameOfRenter));

                break;

            /********************************************************
             * Filters Function: passes current rental object to the
             * created method that filters by if the current rental
             * item is has consoleType "NintendoSwitch".
             *
             * Then, using two separate lists;
             * -filters the first list by rentals that have less
             * than 7 days between the rented on date and due date.
             * -filters the second list by rentals that are outside
             * the previous filter, everything else basically.
             *
             * Finally, this lambda function puts both lists, one
             * after the other into
             * the output of 'filteredlistrentals'.
             *
             * Uses the default compareTo method to
             * sort alphabetically.
             *******************************************************/

            case nintendoSwitchScreen:
                filteredListRentals = (ArrayList<Rental>)
                        listOfRentals.stream()
                        .filter(n -> nintendoSwitchFilter(n))
                        .collect(Collectors.toList());

                list1 = (ArrayList<Rental>)
                        filteredListRentals.stream()
                        .filter(n -> dueIn7DaysFilter(n))
                        .collect(Collectors.toList());

                list2 = (ArrayList<Rental>)
                        filteredListRentals.stream()
                        .filter(n -> !dueIn7DaysFilter(n))
                        .collect(Collectors.toList());

                list2.addAll(list1);

                filteredListRentals = list2;
                Collections.sort(filteredListRentals,
                        (n1, n2) -> n1.nameOfRenter.compareTo(n2.nameOfRenter));

                break;

            /********************************************************
             * Filters Function: passes current rental object to the
             * created method that filters by if the current rental
             * item is has consoleType "SegaGenesisMini".
             *
             * Then, using two separate lists;
             * -filters the first list by rentals that have less
             * than 7 days between the rented on date and due date.
             * -filters the second list by rentals that are outside
             * the previous filter, everything else basically.
             *
             * Finally, this lambda function puts both lists, one
             * after the other into the
             * output of 'filteredlistrentals'.
             *
             * Uses the default compareTo method to sort
             * alphabetically.
             *******************************************************/

            case segaGenesisMiniScreen:
                filteredListRentals = (ArrayList<Rental>)
                        listOfRentals.stream()
                        .filter(n -> segaGenesisMiniFilter(n))
                        .collect(Collectors.toList());

                list1 = (ArrayList<Rental>)
                        filteredListRentals.stream()
                        .filter(n -> dueIn7DaysFilter(n))
                        .collect(Collectors.toList());

                list2 = (ArrayList<Rental>)
                        filteredListRentals.stream()
                        .filter(n -> !dueIn7DaysFilter(n))
                        .collect(Collectors.toList());

                list2.addAll(list1);

                filteredListRentals = list2;
                Collections.sort(filteredListRentals,
                        (n1, n2) -> n1.nameOfRenter.compareTo(n2.nameOfRenter));

                break;

            default:
                throw new RuntimeException("upDate is in undefined state: " + display);
        }
        fireTableStructureChanged();
    }

    /****************************************************************
     * Private helper method to count the number of days between two
     * GregorianCalendar dates
     * Note that this is the proper way to do this; trying to use other
     * classes/methods likely won't properly account for leap days
     * @param startDate - the beginning/starting day
     * @param endDate   - the last/ending day
     * @return int for the number of days between startDate and endDate
     ***************************************************************/
    private int daysBetween(GregorianCalendar startDate, GregorianCalendar endDate) {
        // Determine how many days the Game was rented out
        GregorianCalendar gTemp = new GregorianCalendar();
        gTemp = (GregorianCalendar) endDate.clone(); //  gTemp = dueBack;  does not work!!
        int daysBetween = 0;
        while (gTemp.compareTo(startDate) > 0) {
            gTemp.add(Calendar.DATE, -1);// this subtracts one day from gTemp
            daysBetween++;
        }

        return daysBetween;
    }

    /****************************************************************
     * Gets the column name for the indicated column depending on
     * the current display screen
     * @param col
     * @return column name based on screen and row
     ****************************************************************/
    @Override
    public String getColumnName(int col) {
        switch (display) {
            case CurrentRentalStatus:
                return columnNamesCurrentRentals[col];
            case ReturnedItems:
                return columnNamesReturned[col];
            case DueWithInWeek:
                return columnNamesCurrentRentals[col];
            case Cap14DaysOverdue:
                return columnNamesCurrentRentals[col];
            case DueWithinWeekGamesFirst:
                return columnNamesCurrentRentals[col];
            case everythingScreen:
                return columnNamesEverything[col];
            case playstation4Screen:
                return columnNamesEverything[col];
            case xboxOneSScreen:
                return columnNamesEverything[col];
            case playstation4ProScreen:
                return columnNamesEverything[col];
            case nintendoSwitchScreen:
                return columnNamesEverything[col];
            case segaGenesisMiniScreen:
                return columnNamesEverything[col];

        }
        throw new RuntimeException("Undefined state for Col Names: "
                + display);
    }

    /****************************************************************
     * Gets column count for each display screen using the length
     * of the column names array
     * @return number of columns based on display
     ***************************************************************/
    @Override
    public int getColumnCount() {
        switch (display) {
            case CurrentRentalStatus:
                return columnNamesCurrentRentals.length;
            case ReturnedItems:
                return columnNamesReturned.length;
            case DueWithInWeek:
                return columnNamesCurrentRentals.length;
            case Cap14DaysOverdue:
                return columnNamesCurrentRentals.length;
            case DueWithinWeekGamesFirst:
                return columnNamesCurrentRentals.length;
            case everythingScreen:
                return columnNamesEverything.length;
            case playstation4Screen:
                return columnNamesEverything.length;
            case xboxOneSScreen:
                return columnNamesEverything.length;
            case playstation4ProScreen:
                return columnNamesEverything.length;
            case nintendoSwitchScreen:
                return columnNamesEverything.length;
            case segaGenesisMiniScreen:
                return columnNamesEverything.length;


        }
        throw new IllegalArgumentException();
    }


    /****************************************************************
     * Gets number of rows (number of rental units)
     * @return size of filtered list rentals which is the number
     * of rental units in the array
     ***************************************************************/
    @Override
    public int getRowCount() {
        return filteredListRentals.size();
    }

    /****************************************************************
     * Gets the value at specified row and column
     * @param row row of table
     * @param col column of table
     * @return calls a screen function depending on the current
     * display which returns the values at that row and column
     ***************************************************************/
    @Override
    public Object getValueAt(int row, int col) {

        switch (display) {
            case CurrentRentalStatus:
                return currentRentScreen(row, col);
            case ReturnedItems:
                return rentedOutScreen(row, col);
            case DueWithInWeek:
                return currentRentScreen(row, col);
            case Cap14DaysOverdue:
                return cap14DaysScreen(row, col);
            case DueWithinWeekGamesFirst:
                return currentRentScreen(row, col);
            case everythingScreen:
                return everythingScreen(row, col);
            case playstation4Screen:
                return gamesScreen(row, col);
            case xboxOneSScreen:
                return gamesScreen(row, col);
            case playstation4ProScreen:
                return gamesScreen(row, col);
            case nintendoSwitchScreen:
                return gamesScreen(row, col);
            case segaGenesisMiniScreen:
                return gamesScreen(row, col);


        }
        throw new IllegalArgumentException();
    }

    /****************************************************************
     * Gets the specific information for each row depending
     * on the specified column
     * @param row
     * @param col
     * @return information for specified row and column
     ***************************************************************/
    private Object currentRentScreen(int row, int col) {
        switch (col) {
            case 0:
                return (filteredListRentals.get(row)
                        .nameOfRenter);

            case 1:
                return (filteredListRentals.get(row)
                        .getCost(filteredListRentals.
                        get(row).dueBack));

            case 2:
                return (formatter.format(filteredListRentals.get(row)
                        .rentedOn.getTime()));

            case 3:
                if (filteredListRentals.get(row).dueBack == null)
                    return "-";

                return (formatter.format(filteredListRentals.get(row)
                        .dueBack.getTime()));
            case 4:
                if (filteredListRentals.get(row) instanceof Console)
                    return (((Console) filteredListRentals.get(row))
                            .getConsoleType());

                else {
                    if (filteredListRentals.get(row) instanceof Game)
                        if (((Game) filteredListRentals.get(row)).getConsole() != null)
                            return ((Game) filteredListRentals.get(row)).getConsole();
                        else
                            return "";
                }

            case 5:
                if (filteredListRentals.get(row) instanceof Game)
                    return (((Game) filteredListRentals.get(row)).getNameGame());
                else
                    return "";
            default:
                throw new RuntimeException("Row,col out of range: " + row + " " + col);
        }
    }

    /****************************************************************
     * Copied Current Rental Items screen and added functionality
     * that capitalizes the name (column zero) if the Rental object
     * passed to the dueIn14DaysOrMoreFilter method, meets the
     * criteria that passes a true or false, either capitalizing
     * the name or not.
     * @return Information specified by passed row - column
     * combination. ex:
     *   column 0 : Name of renter
     *   column 1 : Cost of rental
     ***************************************************************/

    private Object cap14DaysScreen(int row, int col) {
        switch (col) {
            case 0:
                if(dueIn14DaysOrMoreFilter(filteredListRentals.get(row))) {
                    return (filteredListRentals.get(row).nameOfRenter.toUpperCase());
                }
                return filteredListRentals.get(row).nameOfRenter;

            case 1:
                return (filteredListRentals.get(row).getCost(filteredListRentals.
                        get(row).dueBack));

            case 2:
                return (formatter.format(filteredListRentals.get(row).rentedOn.getTime()));

            case 3:
                if (filteredListRentals.get(row).dueBack == null)
                    return "-";

                return (formatter.format(filteredListRentals.get(row).dueBack.getTime()));

            case 4:
                if (filteredListRentals.get(row) instanceof Console)
                    return (((Console) filteredListRentals.get(row)).getConsoleType());
                else {
                    if (filteredListRentals.get(row) instanceof Game)
                        if (((Game) filteredListRentals.get(row)).getConsole() != null)
                            return ((Game) filteredListRentals.get(row)).getConsole();
                        else
                            return "";
                }

            case 5:
                if (filteredListRentals.get(row) instanceof Game)
                    return (((Game) filteredListRentals.get(row)).getNameGame());
                else
                    return "";
            default:
                throw new RuntimeException("Row,col out of range: " + row + " " + col);
        }
    }

    /****************************************************************
     * Copied Current Rental Items screen and added functionality
     * that capitalizes the name (column zero) if the Rental object
     * passed to the dueIn7DaysFilter method, meets the
     * criteria that passes a true or false, either capitalizing
     * the name or not.
     * @return Information specified by passed row - column
     * combination. ex:
     *   column 0 : Name of renter
     *   column 1 : Cost of rental
     ***************************************************************/

    private Object gamesScreen(int row, int col) {
        switch (col) {
            case 0:
                if(dueIn7DaysFilter(filteredListRentals.get(row))) {
                    return (filteredListRentals.get(row).nameOfRenter.toUpperCase());
                }
                return filteredListRentals.get(row).nameOfRenter;
            case 1:
                return (formatter.format(filteredListRentals.get(row).rentedOn.
                        getTime()));
            case 2:
                if (filteredListRentals.get(row).dueBack == null)
                    return "-";

                return (formatter.format(filteredListRentals.get(row).dueBack.
                        getTime()));
            case 3:
                if (filteredListRentals.get(row).actualDateReturned == null)
                    return "Not Returned";
                return (formatter.format(filteredListRentals.get(row).
                        actualDateReturned.getTime()));
            case 4:
                return (filteredListRentals.get(row).getCost(filteredListRentals.
                        get(row).dueBack));
            case 5:
                if (filteredListRentals.get(row).actualDateReturned == null)
                    return "Not Returned";
                return (filteredListRentals.
                        get(row).getCost(filteredListRentals.get(row).
                        actualDateReturned));
            case 6:
                if (filteredListRentals.get(row) instanceof Console)
                    return (((Console) filteredListRentals.get(row)).getConsoleType());
                else {
                    if (filteredListRentals.get(row) instanceof Game)
                        if (((Game) filteredListRentals.get(row)).getConsole() != null)
                            return ((Game) filteredListRentals.get(row)).getConsole();
                        else
                            return "";
                }
            case 7:
                if (filteredListRentals.get(row) instanceof Game)
                    return (((Game) filteredListRentals.get(row)).getNameGame());
                else
                    return "";
            default:
                throw new RuntimeException("Row,col out of range: " + row + " " + col);
        }
    }

    /****************************************************************
     * Gets the information necessary for the rented out screen,
     * which includes all the units currently rented out and
     * displays the renter's name, rental date, due date, as well
     * as the rental unit details
     * @param row
     * @param col
     * @return specific information for the specified row and column
     ***************************************************************/
    private Object rentedOutScreen(int row, int col) {
        switch (col) {
            case 0:
                return (filteredListRentals.get(row).nameOfRenter);

            case 1:
                return (formatter.format(filteredListRentals.get(row).rentedOn.
                        getTime()));
            case 2:
                return (formatter.format(filteredListRentals.get(row).dueBack.
                        getTime()));
            case 3:
                return (formatter.format(filteredListRentals.get(row).
                        actualDateReturned.getTime()));

            case 4:
                return (filteredListRentals.
                        get(row).getCost(filteredListRentals.get(row).dueBack));

            case 5:
                return (filteredListRentals.
                        get(row).getCost(filteredListRentals.get(row).
                        actualDateReturned
                ));

            default:
                throw new RuntimeException("Row,col out of range: " + row + " " + col);
        }
    }

    /****************************************************************
     * This object returns a parameter of the rental object (row)
     * based on which information that column needs, ex:
     *   column 0 : Name of renter
     *   column 1 : Rented on date
     * this screen shows all rentals, returned or not.
     ***************************************************************/

    private Object everythingScreen(int row, int col) {
        switch (col) {
            case 0:
                return (filteredListRentals.get(row).nameOfRenter);
            case 1:
                return (formatter.format(filteredListRentals.get(row).rentedOn.
                        getTime()));
            case 2:
                if (filteredListRentals.get(row).dueBack == null)
                    return "-";

                return (formatter.format(filteredListRentals.get(row).dueBack.
                        getTime()));
            case 3:
                if (filteredListRentals.get(row).actualDateReturned == null)
                    return "Not Returned";
                return (formatter.format(filteredListRentals.get(row).
                        actualDateReturned.getTime()));
            case 4:
                return (filteredListRentals.get(row).getCost(filteredListRentals.
                        get(row).dueBack));
            case 5:
                if (filteredListRentals.get(row).actualDateReturned == null)
                    return "Not Returned";
                return (filteredListRentals.
                        get(row).getCost(filteredListRentals.get(row).
                        actualDateReturned));
            case 6:
                if (filteredListRentals.get(row) instanceof Console)
                    return (((Console) filteredListRentals.get(row)).getConsoleType());
                else {
                    if (filteredListRentals.get(row) instanceof Game)
                        if (((Game) filteredListRentals.get(row)).getConsole() != null)
                            return ((Game) filteredListRentals.get(row)).getConsole();
                        else
                            return "";
                }
            case 7:
                if (filteredListRentals.get(row) instanceof Game)
                    return (((Game) filteredListRentals.get(row)).getNameGame());
                else
                    return "";
            default:
                throw new RuntimeException("Row,col out of range: " + row + " " + col);
        }
    }

    /****************************************************************
     * Sets list of rentals equal to another array list of rentals
     * Only used for testing
     * @param listOfRentals
     ***************************************************************/
    public void setListOfRentals(ArrayList<Rental> listOfRentals) {
        this.listOfRentals = listOfRentals;
    }

    /****************************************************************
     * Adds given rental unit to array list of rentals
     * @param a
     ***************************************************************/
    public void add(Rental a) {
        listOfRentals.add(a);
        updateScreen();
    }

    /****************************************************************
     * Gets the rental unit in filtered list of rentals at specified
     * index
     * @param i
     * @return rental unit at index 'i'
     ***************************************************************/
    public Rental get(int i) {
        return filteredListRentals.get(i);
    }

    /****************************************************************
     * Calls the update screen method to update filtered list
     * of rentals depending on the display
     * @param index
     * @param unit
     ***************************************************************/
    public void update(int index, Rental unit) {
        updateScreen();
    }

    /****************************************************************
     * Method used to save
     * @param filename
     ***************************************************************/
    public void saveDatabase(String filename) {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            System.out.println(listOfRentals.toString());
            os.writeObject(listOfRentals);
            os.close();
        } catch (IOException ex) {
            throw new RuntimeException("Saving problem! " + display);
        }
    }

    /****************************************************************
     * Filters lambda function passed rental object.
     * @param r Rental object from lambda function in listRentals.
     * @return True if there is less than 7 days between the
     * due date and rented on date. False if otherwise.
     ****************************************************************/

    public boolean dueIn7DaysFilter(Rental r){
        GregorianCalendar dTemp = (GregorianCalendar) r.dueBack.clone();

        dTemp.add(Calendar.DATE, -7);

        if(r.rentedOn.after(dTemp))
            return true;
        else
            return false;
    }

    /****************************************************************
     * Filters lambda function passed rental object.
     * @param r Rental object from lambda function in listRentals.
     * @return True if there is more than 7 days and less than
     * 14 days between the due date and rented on date.
     * False if otherwise.
     ***************************************************************/

    public boolean dueIn7To14DaysFilter(Rental r){

        if(daysBetween(r.getRentedOn(), r.getDueBack()) > 7
                && daysBetween(r.getRentedOn(), r.getDueBack()) < 14)
            return true;
        else
            return false;
    }

    /****************************************************************
     * Filters lambda function passed rental object.
     * @param r Rental object from lambda function in listRentals.
     * @return True if there are more than 14 days between the
     * due date and rented on date. False if otherwise.
     ****************************************************************/

    public boolean dueIn14DaysOrMoreFilter(Rental r){

        if(daysBetween(r.getRentedOn(), r.getDueBack()) >= 14)
            return true;
        else
            return false;
    }

    /****************************************************************
     * Filters lambda function passed rental object.
     * @param r Rental object from lambda function in listRentals.
     * @return True if consoleType of passed rental is of type
     * 'Playstation4'. False if otherwise.
     ****************************************************************/

    public boolean playstation4Filter(Rental r){
        if (r instanceof Console)
            if (((Console) r).getConsoleType() == ConsoleTypes.PlayStation4)
                return true;
        if (r instanceof Game)
            if (((Game) r).getConsole() == ConsoleTypes.PlayStation4)
                return true;

        return false;
    }

    /****************************************************************
     * Filters lambda function passed rental object.
     * @param r Rental object from lambda function in listRentals.
     * @return True if consoleType of passed rental is of type
     * 'XboxOneS'. False if otherwise.
     ****************************************************************/

    public boolean xboxOneSFilter(Rental r){
        if (r instanceof Console)
            if (((Console) r).getConsoleType() == ConsoleTypes.XBoxOneS)
                return true;
        if (r instanceof Game)
            if (((Game) r).getConsole() == ConsoleTypes.XBoxOneS)
                return true;

        return false;
    }

    /****************************************************************
     * Filters lambda function passed rental object.
     * @param r Rental object from lambda function in listRentals.
     * @return True if consoleType of passed rental is of type
     * 'Playstation4Pro'. False if otherwise.
     ***************************************************************/

    public boolean playstation4ProFilter(Rental r){
        if (r instanceof Console)
            if (((Console) r).getConsoleType() == ConsoleTypes.PlayStation4Pro)
                return true;
        if (r instanceof Game)
            if (((Game) r).getConsole() == ConsoleTypes.PlayStation4Pro)
                return true;

        return false;
    }

    /****************************************************************
     * Filters lambda function passed rental object.
     * @param r Rental object from lambda function in listRentals.
     * @return True if consoleType of passed rental is of type
     * 'NintendoSwitch'. False if otherwise.
     ***************************************************************/

    public boolean nintendoSwitchFilter(Rental r){
        if (r instanceof Console)
            if (((Console) r).getConsoleType() == ConsoleTypes.NintendoSwitch)
                return true;
        if (r instanceof Game)
            if (((Game) r).getConsole() == ConsoleTypes.NintendoSwitch)
                return true;

        return false;
    }

    /****************************************************************
     * Filters lambda function passed rental object.
     * @param r Rental object from lambda function in listRentals.
     * @return True if consoleType of passed rental is of type
     * 'SegaGenesisMini'. False if otherwise.
     ***************************************************************/

    public boolean segaGenesisMiniFilter(Rental r){
        if (r instanceof Console)
            if (((Console) r).getConsoleType() == ConsoleTypes.SegaGenesisMini)
                return true;
        if (r instanceof Game)
            if (((Game) r).getConsole() == ConsoleTypes.SegaGenesisMini)
                return true;

        return false;
    }

    /****************************************************************
     * Method used to load a new database of rental units and data
     * @param filename
     ***************************************************************/
    public void loadDatabase(String filename) {
        listOfRentals.clear();

        try {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream is = new ObjectInputStream(fis);

            listOfRentals = (ArrayList<Rental>) is.readObject();
            updateScreen();
            is.close();
        } catch (Exception ex) {
            throw new RuntimeException("Loading problem: " + display);

        }
    }

    /****************************************************************
     * Method used to save database as a simple text file
     * @param filename
     * @return true if save is successful, false if not
     ***************************************************************/
    public boolean saveAsText(String filename) {
        if (filename.equals("")) {
            throw new IllegalArgumentException();
        }

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new FileWriter(filename)));
            out.println(listOfRentals.size());
            for (int i = 0; i < listOfRentals.size(); i++) {
                Rental unit = listOfRentals.get(i);
                out.println(unit.getClass().getName());
                out.println("Name is " + unit.getNameOfRenter());
                out.println("Rented on " + formatter.format(unit.rentedOn.getTime()));
                out.println("DueDate " + formatter.format(unit.dueBack.getTime()));

                if (unit.getActualDateReturned() == null)
                    out.println("Not returned!");
                else
                    out.println(formatter.format(unit.actualDateReturned.getTime()));

                if (unit instanceof Game) {
                    out.println(((Game) unit).getNameGame());
                    if (((Game) unit).getConsole() != null)
                        out.println(((Game) unit).getConsole());
                    else
                        out.println("No Console");
                }

                if (unit instanceof Console)
                    out.println(((Console) unit).getConsoleType());
            }
            out.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    /****************************************************************
     * Method used to load new database of rental units
     * from text file
     * @param filename
     ***************************************************************/
    public void loadFromText(String filename) {
        listOfRentals.clear();

        String notReturned = new String("Not returned!");
        String isGame = new String("projPack2.Game");
        String isConsole = new String("projPack2.Console");

        String tempString2;
        String tempString3;
        String tempString4;
        String dateReturned;
        String nameGame = null;
        String consoleType = null;

        try {
            Scanner scanner = new Scanner(new File(filename));
            String input = scanner.nextLine();  // from console input example above.
            int count = Integer.parseInt(input);  // converts a String into an int value

            for (int i = 0; i < count; i++) {
                String rentalType = scanner.nextLine();
                tempString2 = scanner.nextLine();
                String[] renterName = tempString2.split(" ");   //need index 2
                tempString3 = scanner.nextLine();
                String[] rentedOn = tempString3.split(" ");     // need index 2
                tempString4 = scanner.nextLine();
                String[] dueBack = tempString4.split(" ");      //need index 1
                dateReturned = scanner.nextLine();
                if(rentalType.equals(isGame)){
                    nameGame = scanner.nextLine();
                    consoleType = scanner.nextLine();
                    if (consoleType.equals("No Console"))
                        consoleType = "NoSelection";
                }
                else if (rentalType.equals(isConsole)) {
                    consoleType = scanner.nextLine();
                }

                Date d1 = formatter.parse(rentedOn[2]);
                GregorianCalendar gTempRentedOn = new GregorianCalendar();
                gTempRentedOn.setTime(d1);
                Date d2 = formatter.parse(dueBack[1]);
                GregorianCalendar gTempDueBack = new GregorianCalendar();
                gTempDueBack.setTime(d2);

                GregorianCalendar gTempActualReturned = new GregorianCalendar();

                if (dateReturned.equals("Not returned!")) {
                    gTempActualReturned = null;
                }
                else {
                    Date d3 = formatter.parse(dateReturned);

                    gTempActualReturned.setTime(d3);
                }

                if (rentalType.equals(isGame)) {
                    Game game = new Game(renterName[2], gTempRentedOn, gTempDueBack, gTempActualReturned, nameGame, ConsoleTypes.valueOf(consoleType));
                    listOfRentals.add(game);
                }
                else if (rentalType.equals(isConsole)){
                    Console console = new Console(renterName[2], gTempRentedOn, gTempDueBack, gTempActualReturned, ConsoleTypes.valueOf(consoleType));
                    listOfRentals.add(console);
                }
            }
            scanner.close();


        } catch (Exception ex) {
            throw new RuntimeException("Loading text file problem" + display);
        }
        updateScreen();


    }

    /****************************************************************
     *  Creates example list of rental units used for the project
     *  DO NOT MODIFY THIS METHOD!!!!!!
     ***************************************************************/
    public void createList() {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        GregorianCalendar g1 = new GregorianCalendar();
        GregorianCalendar g2 = new GregorianCalendar();
        GregorianCalendar g3 = new GregorianCalendar();
        GregorianCalendar g4 = new GregorianCalendar();
        GregorianCalendar g5 = new GregorianCalendar();
        GregorianCalendar g6 = new GregorianCalendar();
        GregorianCalendar g7 = new GregorianCalendar();
        GregorianCalendar g8 = new GregorianCalendar();

        try {
            Date d1 = df.parse("1/20/2020");
            g1.setTime(d1);
            Date d2 = df.parse("12/22/2020");
            g2.setTime(d2);
            Date d3 = df.parse("12/20/2019");
            g3.setTime(d3);
            Date d4 = df.parse("7/02/2020");
            g4.setTime(d4);
            Date d5 = df.parse("1/20/2010");
            g5.setTime(d5);
            Date d6 = df.parse("9/29/2020");
            g6.setTime(d6);
            Date d7 = df.parse("7/25/2020");
            g7.setTime(d7);
            Date d8 = df.parse("7/29/2020");
            g8.setTime(d8);

            Console console1 = new Console("Person1", g4, g6, null, ConsoleTypes.PlayStation4);
            Console console2 = new Console("Person2", g5, g3, null, ConsoleTypes.PlayStation4);
            Console console3 = new Console("Person5", g4, g8, null, ConsoleTypes.SegaGenesisMini);
            Console console4 = new Console("Person6", g4, g7, null, ConsoleTypes.SegaGenesisMini);
            Console console5 = new Console("Person1", g5, g4, g3, ConsoleTypes.XBoxOneS);

            Game game1 = new Game("Person1", g3, g2, null, "title1", ConsoleTypes.PlayStation4);
            Game game2 = new Game("Person1", g3, g1, null, "title2", ConsoleTypes.PlayStation4);
            Game game3 = new Game("Person1", g5, g3, null, "title2", ConsoleTypes.SegaGenesisMini);
            Game game4 = new Game("Person7", g4, g8, null, "title2", null);
            Game game5 = new Game("Person3", g3, g1, g1, "title2", ConsoleTypes.XBoxOneS);
            Game game6 = new Game("Person6", g4, g7, null, "title1", ConsoleTypes.NintendoSwitch);
            Game game7 = new Game("Person5", g4, g8, null, "title1", ConsoleTypes.NintendoSwitch);

            add(game1);
            add(game4);
            add(game5);
            add(game2);
            add(game3);
            add(game6);
            add(game7);

            add(console1);
            add(console2);
            add(console5);
            add(console3);
            add(console4);

            // create a bunch of them.
            int count = 0;
            Random rand = new Random(13);
            String guest = null;

            while (count < 30) {  // change this number to 300 for a complete test of your code
                Date date = df.parse("7/" + (rand.nextInt(10) + 2) + "/2020");
                GregorianCalendar g = new GregorianCalendar();
                g.setTime(date);
                if (rand.nextBoolean()) {
                    guest = "Game" + rand.nextInt(5);
                    Game game;
                    if (count % 2 == 0)
                        game = new Game(guest, g4, g, null, "title2", ConsoleTypes.NintendoSwitch);
                    else
                        game = new Game(guest, g4, g, null, "title2", null);
                    add(game);


                } else {
                    guest = "Console" + rand.nextInt(5);
                    date = df.parse("7/" + (rand.nextInt(20) + 2) + "/2020");
                    g.setTime(date);
                    Console console = new Console(guest, g4, g, null, getOneRandom(rand));
                    add(console);
                }

                count++;
            }
        } catch (ParseException e) {
            throw new RuntimeException("Error in testing, creation of list");
        }
    }

    /****************************************************************
     * Gets a random console type for making example database
     * @param rand
     * @return random console type
     ***************************************************************/
    public ConsoleTypes getOneRandom(Random rand) {

        int number = rand.nextInt(ConsoleTypes.values().length - 1);
        switch (number) {
            case 0:
                return ConsoleTypes.PlayStation4;
            case 1:
                return ConsoleTypes.XBoxOneS;
            case 2:
                return ConsoleTypes.PlayStation4Pro;
            case 3:
                return ConsoleTypes.NintendoSwitch;
            default:
                return ConsoleTypes.SegaGenesisMini;
        }
    }
}