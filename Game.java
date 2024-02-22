package projPack2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Game extends Rental {

    /**
     * represents the name of the game
     */
    private String nameGame;

    /**
     * Represents the console the person rented to play the game,
     * null if no console was rented.
     */
    private ConsoleTypes console;

    public Game() {
    }

    public Game(String nameOfRenter,
                GregorianCalendar rentedOn,
                GregorianCalendar dueBack,
                GregorianCalendar actualDateReturned,
                String nameGame,
                ConsoleTypes console) {
        super(nameOfRenter, rentedOn, dueBack, actualDateReturned);
        this.nameGame = nameGame;
        this.console = console;
    }

    public String getNameGame() {
        return nameGame;
    }

    public void setNameGame(String nameGame) {
        this.nameGame = nameGame;
    }

    public ConsoleTypes getConsole() {
        return console;
    }

    public void setConsole(ConsoleTypes console) {
        this.console = console;
    }

    @Override
    public double getCost(GregorianCalendar dueBack) {
        double cost = 0;
        if (console != null) {
            Console temp = new Console(this.nameOfRenter, rentedOn, this.dueBack,
                    actualDateReturned, console);
            cost += temp.getCost(dueBack);
        }

        GregorianCalendar gTemp = new GregorianCalendar();
        cost += 3;

        gTemp = (GregorianCalendar) dueBack.clone();     //       gTemp = dueBack;  does not work!!

        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        //     System.out.println(formatter.format(gTemp.getTime()));

        for (int days = 0; days < 7; days++)
            gTemp.add(Calendar.DATE, -1);

//        System.out.println(formatter.format(gTemp.getTime()));
//        System.out.println(formatter.format(rentedOn.getTime()));

        while (gTemp.after(rentedOn)) {
            cost += .5;
            gTemp.add(Calendar.DATE, -1);
        }

        return cost;
    }

    @Override
    public String toString() {
        return "Game{" +
                "name='" + nameGame + '\'' +
                ", player=" + console + super.toString() +
                '}';
    }
}
