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
     * Returns the Calendar whose owner has the specified username.
     * @param username The owner of the requested Calendar
     * @return The Calendar for the owner who requested it.
     * @throws RemoteException
     */
    Calendar getCalendar(String username) throws RemoteException;

    /**
     * Creates an instance of a new Calendar for the specified Client.
     * @param user The specified Client.
     * @return The Calendar for the specified Client.
     * @throws RemoteException
     */
    Calendar makeCalendar(Client user) throws RemoteException;

    /**
     * This method will return all of the users in this CalendarManager
     * @return
     * @throws RemoteException
     */
    List<String> allUsers() throws RemoteException;

    /**
     * Returns the specified Client which is searched by username.
     * @param username The username of the Client sought for.
     * @return The Client that has passed in username.
     * @throws RemoteException
     */
    Client lookup(String username) throws RemoteException;

}
