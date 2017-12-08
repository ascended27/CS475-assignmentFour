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
    boolean editEvent(String ownerName, String title, Timestamp start, Timestamp stop, Timestamp newStart, Timestamp newStop, boolean type) throws RemoteException;

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
