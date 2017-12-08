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

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CalendarImpl extends UnicastRemoteObject implements Calendar {

    public Client owner;
    private String ownerName;
    private Thread clockThread;
    private ConcurrentLinkedQueue<Event> eventList;
    public ReadWriteLock rwLock = new ReentrantReadWriteLock();

    protected CalendarImpl(Client owner) throws RemoteException {
        this.owner = owner;
        this.ownerName = owner.getName();
        this.eventList = new ConcurrentLinkedQueue<>();
        startClock(owner);
    }

    /**
     * Retrieves event for passed user with passed start and end
     *
     * @param start The starting time of the event to retrieve
     * @param end   The ending time of the event to retrieve
     * @return The event
     */
    public Event retrieveEvent(String userName, Timestamp start, Timestamp end) throws RemoteException {
        try {
            rwLock.readLock().lock();
            Event toReturn = null;
            for (Event event : eventList) {
                if (event.getStart().equals(start) && event.getStop().equals(end)) {
                    if(event.getOwnerName().equals(userName) || event.getAttendees().contains(userName))
                        toReturn = event;
                    break;
                }
            }
            return toReturn;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * Schedules an event with a list of attendees specified by users
     *
     * @param owner     The owner of the new event
     * @param attendees The list of users that are attending
     * @param title     The name of the new event
     * @param start     The start time of the new event
     * @param stop      The end time of the new event
     * @param type      Is the event public or private
     * @return True if the event was scheduled, otherwise false
     * @throws RemoteException If the connection was lost
     */
    public boolean scheduleEvent(Client owner, String ownerName, List<String> attendees, String title, Timestamp start, Timestamp stop, boolean type) throws RemoteException {
        try {
            rwLock.writeLock().lock();
            boolean canSchedule = false;
            ArrayList<CalendarImpl> calendars = new ArrayList<>();

        /*
         * If owner is the owner of this calendar then we are creating a new event for that user.
         * If there are any attendees then we need to check to see if they are available for this event.
         *      If they are then flip a flag saying everyone is good.
         * For each event x in this calendar. Check to see if there are any conflicting events with this one.
         *      If there are then return false. This event can't be scheduled.
         *      Otherwise if there are attendees and everyone is available then call this method on their calendars
         *          with the same parameters. Then check this calendar's open events. If there is an open event that has
         *          a start before this new event and an end after then split that open event and insert this new event
         *          between. Otherwise the event would have to have the exact time range as the new event. So just copy
         *          over the params for this call to that event.
         */
            // Schedule locally
            if ((attendees == null || attendees.size() == 0) && this.ownerName.equals(ownerName)) {
                if (eventList.size() > 0) {
                    for (Event event : eventList) {
                        if (event.getStart().compareTo(stop) >= 0 || event.getStop().compareTo(start) <= 0) {
                            eventList.add(new Event(title, start, stop, owner, ownerName, attendees, type, false));
                            return true;
                        }
                    }
                } else {
                    eventList.add(new Event(title, start, stop, owner, ownerName, attendees, type, false));
                    return true;
                }
            } else {
                // Find an open event
                Event found = null;
                for (Event event : eventList) {
                    if (event.isOpen() && event.getStart().compareTo(start) <= 0 && event.getStop().compareTo(stop) >= 0) {
                        found = event;
                        canSchedule = true;
                        break;
                    }
                }
                // If found is null then there is no open event
                if (found == null)
                    return false;

                // If we have attendees we are in a group event.
                // If this calendar owner is the same as the event owner then we are need to invite the attendees
                if (attendees != null && attendees.size() != 0 && ownerName.equals(this.ownerName)) {
                    CalendarManagerImpl cm = (CalendarManagerImpl) CalendarManagerImpl.getInstance();

                    for (String c : attendees) {
                        calendars.add(cm.getCalendar(c));
                    }

                    for (Calendar cal : calendars) {
                        for (Event event : cal.getEventList()) {
                            // If the event is open and it starts before or the same time as the new event
                            // and it ends after or the same time as the new event
                            if (event.isOpen() && event.getStart().compareTo(start) <= 0 && event.getStop().compareTo(stop) >= 0) {
                                canSchedule = true;
                            } else {
                                canSchedule = false;
                                break;
                            }
                        }
                        if (!canSchedule) {
                            // If canSchedule == false then this user is not available so abandon the event.
                            break;
                        }
                    }
                }

                if (canSchedule && ownerName.equals(this.ownerName)) {
                    for (Calendar cal : calendars) {
                        cal.scheduleEvent(owner, ownerName, attendees, title, start, stop, type);
                    }
                }

                // If the event is open and matches the time range then just use this event
                if (canSchedule && found.isOpen() && found.getStart().equals(start) && found.getStop().equals(stop)) {
                    // Copy the values to the event
                    found.setOwner(owner);
                    found.setTitle(title);
                    found.setOpen(false);
                    found.setType(type);
                    found.setAttendees(attendees);
                    return true;
                }

                // If the event is open and has a start before the new event and a stop after the new event then split it
                else if (canSchedule && found.isOpen() && found.getStart().compareTo(start) < 0 && found.getStop().compareTo(stop) > 0) {
                    // Create two new events that are a split between the event and the new event
                    Event before = new Event(found.getTitle(), found.getStart(), start, found.getOwner(), found.getOwnerName(), null, found.isType(), true);
                    Event after = new Event(found.getTitle(), stop, found.getStop(), found.getOwner(), found.getOwnerName(), null, found.isType(), true);
                    // Remove the old event
                    eventList.remove(found);
                    // Insert the split open event
                    eventList.add(before);
                    eventList.add(after);
                    // Add the new event
                    eventList.add(new Event(title, start, stop, owner, owner.getName(), attendees, type, false));
                    return true;
                } else if (canSchedule && found.isOpen() && found.getStart().equals(start) && found.getStop().compareTo(stop) > 0) {
                    Event after = new Event(found.getTitle(), stop, found.getStop(), found.getOwner(), found.getOwnerName(), attendees, found.isType(), true);
                    found.setOwner(owner);
                    found.setOwnerName(owner.getName());
                    found.setStop(stop);
                    found.setAttendees(attendees);
                    found.setTitle(title);
                    found.setType(type);
                    found.setOpen(false);
                    eventList.add(after);
                    return true;
                } else if (canSchedule && found.isOpen() && found.getStart().compareTo(start) < 0 && found.getStop().equals(stop)) {
                    Event before = new Event(found.getTitle(), found.getStart(), start, found.getOwner(), found.getOwnerName(), attendees, found.isType(), true);
                    found.setStart(start);
                    found.setOwner(owner);
                    found.setOwnerName(owner.getName());
                    found.setStop(stop);
                    found.setAttendees(attendees);
                    found.setTitle(title);
                    found.setType(type);
                    found.setOpen(false);
                    eventList.add(before);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rwLock.writeLock().unlock();
        }
        return false;
    }

    public boolean editEvent(String ownerName, String title, Timestamp start, Timestamp stop, Timestamp newStart, Timestamp newStop, boolean type) throws RemoteException {
        CalendarManager cm = CalendarManagerImpl.getInstance();
        Event toEdit = retrieveEvent(ownerName, start, stop);
        boolean canSchedule = false;

        // If the owner or an attendee isn't the one editing then quit
        if (!toEdit.getOwnerName().equals(ownerName) && !toEdit.getAttendees().contains(ownerName)) {
            return false;
        }

        // Can the user schedule
        for (Event event : eventList) {
            // If new stop is before this event starts
            if (event.getStart().compareTo(newStop) >= 0) {
                canSchedule = true;
            } else if (event.getStop().compareTo(newStart) <= 0) { // If new start is after this event ends
                canSchedule = true;
            } else if (event.isOpen()) {
                canSchedule = true;
            }

        }
        // Can the attendees schedule
        for (String attendee : toEdit.getAttendees()) {
            for (Event event : cm.getCalendar(attendee).getEventList()) {
                // If new stop is before this event starts
                if (event.getStart().compareTo(newStop) >= 0) {
                    canSchedule = true;
                } else if (event.getStop().compareTo(newStart) <= 0) { // If new start is after this event ends
                    canSchedule = true;
                } else if (event.isOpen()) {
                    canSchedule = true;
                }

            }
        }

        if (canSchedule) {
            // Update attendees' calendars
            for (String attendee : toEdit.getAttendees()) {
                ConcurrentLinkedQueue<Event> events = cm.getCalendar(attendee).getEventList();
                for(Event event : events){
                    if(event.getStart().equals(toEdit.getStart()) && event.getStop().equals(toEdit.getStop()))
                        events.remove(event);

                }
                if (toEdit.getStart().equals(newStart) && toEdit.getStop().equals(newStop)) {
                    toEdit.setTitle(title);
                    toEdit.setType(type);
                    cm.getCalendar(attendee).getEventList().add(toEdit);
                } else {
                    cm.getCalendar(attendee).scheduleEvent(toEdit.getOwner(), toEdit.getOwnerName(), toEdit.getAttendees(), title, newStart, newStop, type);
                }
            }
            // Remove the old event
            eventList.remove(toEdit);
            // Schedule the new event
            if (toEdit.getStart().equals(newStart) && toEdit.getStop().equals(newStop)) {
                toEdit.setTitle(title);
                toEdit.setType(type);
                cm.getCalendar(toEdit.getOwnerName()).getEventList().add(toEdit);
                return true;
            } else {
                return scheduleEvent(toEdit.getOwner(), toEdit.getOwnerName(), toEdit.getAttendees(), title, newStart, newStop, type);
            }
        } else return false;


    }

    /**
     * @param owner The owner of the event to schedule, should match the owner of this calendar
     * @param start The start time of the open event
     * @param stop  The end time of the open event
     * @return True if the event was scheduled, otherwise false
     * @throws RemoteException If the connection was lost
     */
    public boolean insertOpenEvent(Client owner, Timestamp start, Timestamp stop) throws RemoteException {
        try {
            rwLock.writeLock().lock();
        /*
        * For each event x in the event list check to see:
        *       if x.start is within start and stop. If it is then
        *           return false. The events conflict.
        *       if x.end is within start and stop. If it is then
        *           return false. The events conflict.
        */
            if (eventList.size() > 0) {
                for (Event event : eventList) {
                    // If event is within start and stop then we can't create an event
                    if (event.getStart().compareTo(start) < 0 && event.getStart().compareTo(stop) < 0 ||
                            event.getStop().compareTo(start) > 0 && event.getStop().compareTo(stop) > 0) {
                        eventList.add(new Event("Open Event", start, stop, owner, owner.getName(), null, true, true));
                        return true;
                    }
                }
            } else {
                eventList.add(new Event("Open Event", start, stop, owner, owner.getName(), null, true, true));
                return true;
            }

            return false;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Used to retrieve the event list when another calendar is trying to schedule a group event
     *
     * @return The list of events
     */
    public ConcurrentLinkedQueue<Event> getEventList() {
        try {
            rwLock.readLock().lock();
            return eventList;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * Used to retrieve the owner of the calendar
     *
     * @return The user that owns the calendar
     */
    public Client getOwner() {
        try {
            rwLock.readLock().lock();
            System.out.println("Getting owner");
            return owner;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public void setOwner(Client client) throws RemoteException {
        this.owner = client;
    }

    public boolean startClock(Client owner) throws RemoteException {
        if (owner.getName().equals(this.owner.getName())) {
            Clock c = new Clock(this);
            clockThread = new Thread(c);
            clockThread.start();
            return true;
        }
        return false;
    }

    public void killClock(Client owner) throws RemoteException {
        if (owner.getName().equals(this.owner.getName())) {
            clockThread.interrupt();
        }
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
