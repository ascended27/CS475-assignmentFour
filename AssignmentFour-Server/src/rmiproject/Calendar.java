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
    Event retrieveEvent(String userName, Timestamp start, Timestamp end) throws RemoteException;

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
    boolean scheduleEvent(Client owner, String ownerName, List<String> attendees, String title, Timestamp start, Timestamp stop, boolean type) throws RemoteException;

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

    /**
     * This method will serve to edit an event by changing its state with the passed in values.
     * @param title The new title for the event
     * @param ownerName The owner of the event
     * @param start The start time of the open event
     * @param stop  The stop time of the open event
     * @param newStart The old start time of the open event
     * @param newStop The old stop time of the open event
     * @param type Whether the event should be public or not
     * @return true if the edit was successful. False otherwise.
     * @throws RemoteException
     */
    public boolean editEvent(String ownerName, String title, Timestamp start, Timestamp stop, Timestamp newStart, Timestamp newStop, boolean type) throws RemoteException;

    /**
     * This method will start a new Clock instance that will run in a separate Thread, to see if any
     * events are in the close future.
     * @param owner The owner of this Calendar
     * @return Returns true upon successful starting, false otherwise. False is usually returned
     * if the owner passed in is not actually the owner of this Calendar.
     * @throws RemoteException
     */
    boolean startClock(Client owner) throws RemoteException;

    /**
     * This method will kill the Clock and its related Thread.
     * @param owner The owner of the Calendar where the Clock resides.
     * @throws RemoteException
     */
    void killClock(Client owner) throws RemoteException;

    /**
     * This method will return this Calendar's eventList
     * @return This Clandar's eventList
     * @throws RemoteException
     */
    ConcurrentLinkedQueue<Event> getEventList() throws RemoteException;

    /**
     * Returns this Calendar's owner.
     * @return This Calendar's Client owner.
     * @throws RemoteException
     */
    Client getOwner() throws RemoteException;

    /**
     * Will change this Calendar's Client owner to the new Client passed in.
     * @param client The new Client owner for this Calendar.
     * @throws RemoteException
     */
    void setOwner(Client client) throws RemoteException;

}
