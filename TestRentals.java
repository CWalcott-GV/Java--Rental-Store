package projPack2;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.ParseException;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class TestRentals {

    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

    ListModel lModel = new ListModel();


    @Test
    public void testPlaystationScreen() {

        /*********************************************************
         * This test method is creating 6 rental objects and checking
         * if the two created rental objects that have ConsoleType
         * "Playstation4" will be filtered correctly.
         */

        ArrayList<Rental> tempList = new ArrayList();

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

            Console console1 = new Console("Adam", g4, g6,
                    null, ConsoleTypes.PlayStation4);
            Console console2 = new Console("Carter", g5, g3,
                    null, ConsoleTypes.NintendoSwitch);
            Console console3 = new Console("Barry", g4, g8,
                    null, ConsoleTypes.SegaGenesisMini);

            Game game1 = new Game("Abraham", g3, g2, null,
                    "God of War", ConsoleTypes.PlayStation4);
            Game game2 = new Game("Colson", g3, g1, null,
                    "Overwatch", ConsoleTypes.PlayStation4Pro);
            Game game3 = new Game("Barnabas", g5, g3, null,
                    "Halo 4", ConsoleTypes.XBoxOneS);

            tempList.add(console1);
            tempList.add(console2);
            tempList.add(console3);
            tempList.add(game1);
            tempList.add(game2);
            tempList.add(game3);

        } catch (ParseException e) {
            throw new RuntimeException("Error in testing, creation of list");
        }

        lModel.setListOfRentals(tempList);
        lModel.setDisplay(ScreenDisplay.playstation4Screen);
        lModel.updateScreen();

        assertTrue(lModel.filteredListRentals.size() == 2);
        assertEquals("Abraham", lModel.filteredListRentals.get(0).nameOfRenter);
        assertEquals("Adam", lModel.filteredListRentals.get(1).nameOfRenter);

    }

    /*********************************************************
     * This test method creates one rental object with rented
     * on date and due back date backwards, otherwise would
     * break code, but handled in the correct way.
     */

    @Test (expected = IllegalArgumentException.class)
    public void testMisOrderedDates(){

        GregorianCalendar g1 = new GregorianCalendar();
        GregorianCalendar g2 = new GregorianCalendar();

        try {

            Date d1 = df.parse("12/20/2020");
            g1.setTime(d1);
            Date d2 = df.parse("12/22/2020");
            g2.setTime(d2);

            Rental game1 = new Game("Person1", g2, g1, null,
                    "title1", ConsoleTypes.PlayStation4);

        }
        catch (ParseException e){
            throw new IllegalArgumentException("Wasn't supposed to fail but ok");
        }

    }

    /*********************************************************
     * This test method is creating 5 rental objects, and running
     * a lambda function to confirm that the filtering of said list
     * of rentals is working correctly, there is only supposed
     * to be one rental object that matches the criteria.
     */

    @Test
    public void testListMaker(){

        ArrayList<Rental> List = new ArrayList<Rental>();

        GregorianCalendar g1 = new GregorianCalendar();
        GregorianCalendar g2 = new GregorianCalendar();
        GregorianCalendar g3 = new GregorianCalendar();
        GregorianCalendar g4 = new GregorianCalendar();
        GregorianCalendar g5 = new GregorianCalendar();

        try {

            Date d1 = df.parse("12/10/2020");
            g1.setTime(d1);
            Date d2 = df.parse("12/11/2020");
            g2.setTime(d2);
            Date d3 = df.parse("12/15/2020");
            g3.setTime(d3);
            Date d4 = df.parse("12/17/2020");
            g4.setTime(d4);
            Date d5 = df.parse("12/30/2020");
            g5.setTime(d5);

            Game game1 = new Game("First", g1, g2, null,
                    "title1", ConsoleTypes.PlayStation4);
            Game game2 = new Game("Second", g1, g3, null,
                    "title1", ConsoleTypes.XBoxOneS);
            Game game3 = new Game("Anna", g1, g4, null,
                    "title1", ConsoleTypes.PlayStation4Pro);
            Console console4 = new Console("Aaron", g2, g5, null,
                    ConsoleTypes.SegaGenesisMini);  //this one is supposed to be the only rental that fits
            Console console5 = new Console("Person1", g1, g3,
                    g4, ConsoleTypes.NintendoSwitch);

            List.add(game1);
            List.add(game2);
            List.add(game3);
            List.add(console4);
            List.add(console5);

            ArrayList<Rental> tempList = (ArrayList<Rental>)
            List.stream()
                    .filter(n -> lModel.dueIn14DaysOrMoreFilter(n))
                    .collect(Collectors.toList());

            assertEquals("Aaron", tempList.get(0).getNameOfRenter());

        }
        catch (ParseException e){
            throw new IllegalArgumentException("Wasn't supposed to fail but ok");
        }

    }

}
