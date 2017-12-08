package rmiproject;

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
     * Edits the event at times start & stop with the other parameters
     *
     * @param ownerName The owner of the edited event
     * @param title     The new title of the event
     * @param start     The old start time of the event
     * @param stop      The old stop time of the event
     * @param newStart  The new start time of the event
     * @param newStop   The new stop time of the event
     * @param type      The privace of the event
     * @return True if the event was successful
     * @throws RemoteException Thrown if the
     */
    boolean editEvent(String ownerName, String title, Timestamp start, Timestamp stop, Timestamp newStart, Timestamp newStop, boolean type) throws RemoteException;

    /**
     * Starts the clock for the passed owner
     *
     * @param owner The owner to start the clock for
     * @return True if the clock was started
     * @throws RemoteException
     */
    boolean startClock(Client owner) throws RemoteException;

    /**
     * Stops the clock for the passed owner
     *
     * @param owner The owner of the clock to stop
     * @throws RemoteException
     */
    void killClock(Client owner) throws RemoteException;

    /**
     * Gets the list of events for this calendar
     *
     * @return A list of the events
     * @throws RemoteException
     */
    ConcurrentLinkedQueue<Event> getEventList() throws RemoteException;

    /**
     * Gets the owner of this calendar
     *
     * @return The owner of this calendar
     * @throws RemoteException
     */
    Client getOwner() throws RemoteException;

    /**
     * Sets the owner of this calendar to the passed client
     *
     * @param client The owner of this calendar
     * @throws RemoteException
     */
    void setOwner(Client client) throws RemoteException;

    /**
     * Deletes the event at the passed time
     * @param username The name of the owner or attendee
     * @param start The start time of the event
     * @param stop The stop time of the event
     * @return The success of the deletion
     * @throws RemoteException
     */
    boolean deleteEvent(String username, Timestamp start, Timestamp stop) throws RemoteException;
}
