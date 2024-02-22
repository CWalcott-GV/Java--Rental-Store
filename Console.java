package projPack2;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Console extends Rental {

    /** Represents the type of Console, see enum type. */
    private ConsoleTypes consoleType;

    public Console() {
    }

    public Console(String nameOfRenter,
                   GregorianCalendar rentedOn,
                   GregorianCalendar dueBack,
                   GregorianCalendar actualDateReturned,
                   ConsoleTypes consoleType) {
        super(nameOfRenter, rentedOn, dueBack, actualDateReturned);
        this.consoleType = consoleType;
    }

    public ConsoleTypes getConsoleType() {
        return consoleType;
    }

    public void setConsoleType(ConsoleTypes consoleType) {
        this.consoleType = consoleType;
    }

    @Override
    public double getCost(GregorianCalendar dueBack) {
        GregorianCalendar gTemp = new GregorianCalendar();
        double cost = 5;

        gTemp = (GregorianCalendar) dueBack.clone();     //  gTemp = dueBack;  does not work!!

        for (int days = 0; days < 7; days++)  // or             gTemp.add(Calendar.DATE, -7);
            gTemp.add(Calendar.DATE, -1);

        while (gTemp.after(rentedOn)) {
            while (gTemp.after(rentedOn)) {
                if ((this.consoleType == ConsoleTypes.NintendoSwitch) ||
                        (this.consoleType == ConsoleTypes.PlayStation4Pro) ||
                        (this.consoleType == ConsoleTypes.SegaGenesisMini))
                    cost += 1.5;

                if ((this.consoleType == ConsoleTypes.PlayStation4) ||
                        (this.consoleType == ConsoleTypes.XBoxOneS))
                    cost += 1;
                gTemp.add(Calendar.DATE, -1);

            }
        }
        return cost;
    }

    @Override
    public String toString() {
        return "Console{" +
                " consoleType=" + consoleType + " " + super.toString() +
                '}';
    }
}


