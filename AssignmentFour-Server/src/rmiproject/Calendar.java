package rmiproject;

/*
 * The Calendar class represents the calendar object
 * that each user uses to schedule events. This class
 * will store the user that owns the calendar and a
 * list of events that the user is attending.
 *
 * This class will need to be synchronus. The
 * CalendarManager will need to be able read
 * and write to a calendar from different
 * threads. The owner may need to write to
 * their calendar while another user is reading.
 *
 * ------- From the PDF ------------------------------
 * Each Calendar object maintains a database of events
 * for its user. As such, it should support the following
 * classes of services that essentially query and modify its database
 * and that of other users. Optional parameters appear in square
 * brackets.
 *
 * Retrieve Event [usertime-range]
 * Retrieve the schedule of a user for the specified
 * time-range. If user is omitted, it defaults to the
 * owner of the calendar. The Calendar object will
 * need to communicate with another object to
 * retrieve a schedule if it does not maintain the
 * calendar of the specified user. Note that the calendar
 * only returns events that the requesting user has
 * privileges to view.
 *
 * Schedule Event [user-list event]
 * Schedule an event in calendars of each user
 * specified in user-list. If user-list is omitted,
 * schedule the event in the local calendar.
 * A group event may be scheduled in the calendar of each proposed
 * user only if the Access Control field of the respective event in
 * every specified calendar is currently set to Open
 * ---------------------------------------------------
 */

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface Calendar extends Remote {

    /**
     * Retrieves event for passed user with passed start and end
     *
     * @param start The start time of the event to retrieve
     * @param end   The stop time of the event to retrieve
     * @return The event
     */
    Event retrieveEvent(Timestamp start, Timestamp end) throws RemoteException;

    /**
     * Schedules an event with a list of attendees specified by users
     *
     * @param owner     The owner of the new event
     * @param attendees The list of users that are attending
     * @param title     The name of the new event
     * @param start     The start time of the new event
     * @param stop      The end time of the new event
     * @param type      Is the event public or private
     * @return
     * @throws RemoteException
     */
    boolean scheduleEvent(Client owner, List<Client> attendees, String title, Timestamp start, Timestamp stop, boolean type) throws RemoteException;

    /**
     * Schedules an open event
     *
     * @param owner The owner of the event
     * @param start The start time of the open event
     * @param stop  The stop time of the open event
     * @return
     * @throws RemoteException
     */
    boolean insertOpenEvent(Client owner, Timestamp start, Timestamp stop) throws RemoteException;

    boolean startClock(Client owner) throws RemoteException;

    void killClock(Client owner) throws RemoteException;

    ConcurrentLinkedQueue<Event> getEventList() throws RemoteException;

    Client getOwner() throws RemoteException;

    void setOwner(Client client) throws RemoteException;

}
