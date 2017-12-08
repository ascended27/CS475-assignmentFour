package rmiproject;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface CalendarManager extends Remote {

    /**
     * Gets the user's calendar if it exists otherwise creates a new
     * calendar and returns that.
     *
     * @param user The user of the calendar to get
     * @return The calendar
     * @throws RemoteException
     */
    Calendar getCalendar(Client user) throws RemoteException;

    /**
     * Gets the user's, by username, calendar if it exists otherwise creates a new
     * calendar and returns that.
     *
     * @param username The username of the user who's calendar to retrieve
     * @return The calendar
     * @throws RemoteException
     */
    Calendar getCalendar(String username) throws RemoteException;

    /**
     * Makes a new calendar for the passed user
     *
     * @param user The owner of the the new calendar
     * @return The new calendar
     * @throws RemoteException
     */
    Calendar makeCalendar(Client user) throws RemoteException;

    /**
     * Retrieves all the users of the calendar service
     *
     * @return A list of all the users' usernames
     * @throws RemoteException
     */
    List<String> allUsers() throws RemoteException;

    /**
     * Returns the Client object for the passed username
     *
     * @param username The username to lookup
     * @return The Client object for that username
     * @throws RemoteException
     */
    Client lookup(String username) throws RemoteException;
}
