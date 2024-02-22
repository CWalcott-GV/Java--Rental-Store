package projPack2;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public abstract class
Rental implements Serializable {

    /** What is the purpose of this variable (search google) */
    private static final long serialVersionUID = 1L;

    /** The Name of person that is reserving the Rental*/
    protected String nameOfRenter;

    /** The date the Rental was rented on */
    protected GregorianCalendar rentedOn;

    /** The date the Rental was dueBack on */
    protected GregorianCalendar dueBack;

    /** The actual date the Rental was returned on */
    protected GregorianCalendar actualDateReturned;

    public Rental() {
    }

    public abstract double getCost(GregorianCalendar checkOut);

    public Rental(String nameOfRenter,
                  GregorianCalendar rentedOn,
                  GregorianCalendar dueBack,
                  GregorianCalendar actualDateReturned) {
        this.nameOfRenter = nameOfRenter;
        this.rentedOn = rentedOn;
        this.dueBack = dueBack;
        this.actualDateReturned = actualDateReturned;

        if(rentedOn.after(dueBack)){
            throw new IllegalArgumentException();
        }
    }

    public String getNameOfRenter() {
        return nameOfRenter;
    }

    public void setNameOfRenter(String nameOfRenter) {
        this.nameOfRenter = nameOfRenter;
    }

    public GregorianCalendar getRentedOn() {
        return rentedOn;
    }

    public void setRentedOn(GregorianCalendar rentedOn) {
        this.rentedOn = rentedOn;
    }

    public GregorianCalendar getActualDateReturned() {
        return actualDateReturned;
    }

    public void setActualDateReturned(GregorianCalendar actualDateReturned) {
        this.actualDateReturned = actualDateReturned;
    }

    public GregorianCalendar getDueBack() {
        return dueBack;
    }

    public void setDueBack(GregorianCalendar dueBack) {
        this.dueBack = dueBack;
    }

    // following code used for debugging only
    // IntelliJ using the toString for displaying in debugger.
    @Override
    public String toString() {
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        String rentedOnStr;
        if (getRentedOn() == null)
            rentedOnStr = "";
        else
            rentedOnStr = formatter.format(getRentedOn().getTime());

        String estdueBackStr;
        if (getDueBack() == null)
            estdueBackStr = "";
        else
            estdueBackStr = formatter.format(getDueBack().getTime());

        String acutaulDateReturnedStr;
        if (getActualDateReturned() == null)
            acutaulDateReturnedStr = "";
        else
            acutaulDateReturnedStr = formatter.format(getActualDateReturned().getTime());

        return "RentUnit{" +
                "guestName='" + nameOfRenter + ' ' +
                ", rentedOn =" + rentedOnStr +
                ", dueBack =" + estdueBackStr +
                ", actualDateReturned =" + acutaulDateReturnedStr +
                '}';
    }
}
