package rmiproject;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class CalendarManagerImpl extends UnicastRemoteObject implements CalendarManager {

    List<CalendarImpl> calendars;
    List<String> users;

    //CalendarManager should be a Singleton. Don't want extraneous instances hanging around
    private static CalendarManagerImpl theInstance;

    //This method returns null for now, since it's not being used.
    public Calendar makeCalendar(Client user) {
        return null;
    }

    //Constructor for class. Inits calendar list and user list.
    protected CalendarManagerImpl() throws RemoteException {
        theInstance = this;
        calendars = new ArrayList<>();
        users = new ArrayList<>();
        System.out.println("New CalendarManager");
    }

    //This method returns the Singleton instance
    public static CalendarManager getInstance() throws RemoteException {
        if (theInstance == null)
            theInstance = new CalendarManagerImpl();

        return theInstance;
    }

    /**
     * Gets the user's calendar if it exists otherwise creates a new
     * calendar and returns that.
     *
     * @param user The user of the calendar to get
     * @return The calendar
     * @throws RemoteException
     */

    public CalendarImpl getCalendar(Client user) throws RemoteException {

        CalendarImpl toReturn = null;
        System.out.println("New Client Request: " + user.getName());
        for (CalendarImpl cal : calendars) {

            //Check if have same name
            if (cal.getOwnerName().equals(user.getName())) {
                toReturn = cal;
            }
        }

        if (toReturn == null) {

            //If no existing Calendars, make new instance and add it to Calendar.
            toReturn = new CalendarImpl(user);
            users.add(user.getName());
            System.out.println("New User: " + user.getName());
            calendars.add(toReturn);
        }

        return toReturn;
    }

    //Overloaded method will simply return the given instance if given a
    //username String.
    public CalendarImpl getCalendar(String username) throws RemoteException {

        CalendarImpl toReturn = null;
        System.out.println("New Client Request: " + username);
        for (CalendarImpl cal : calendars) {
            if (cal.getOwnerName().equals(username)) {
                toReturn = cal;
            }
        }
        return toReturn;
    }

    //Returns this CalendarManager's username Strings.
    @Override
    public List<String> allUsers() throws RemoteException {
        return users;
    }

    //Search and return Client by username
    public Client lookup(String username) throws RemoteException{
        for(CalendarImpl cal : calendars){
            if(cal.getOwnerName().equals(username))
                return cal.getOwner();
        }
        return null;
    }

}
