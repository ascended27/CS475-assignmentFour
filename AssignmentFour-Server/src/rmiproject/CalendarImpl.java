package rmiproject;

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
            for (Event event : CalendarManagerImpl.getInstance().getCalendar(userName).getEventList()) {
                if (event.getStart().equals(start) && event.getStop().equals(end)) {
                    if (event.getOwnerName().equals(userName) || event.getAttendees().contains(userName))
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
                for (Event event : events) {
                    if (event.getStart().equals(toEdit.getStart()) && event.getStop().equals(toEdit.getStop()))
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

    //This method will change this Calendar's owner.
    @Override
    public void setOwner(Client client) throws RemoteException {
        this.owner = client;
    }

    @Override
    public boolean deleteEvent(String username, Timestamp start, Timestamp stop) throws RemoteException {
        Event toDelete = retrieveEvent(username, start, stop);

        if (toDelete == null)
            return false;

        // If the user is the owner or in the attendees they can delete
        if (toDelete.getOwnerName().equals(username) || (toDelete.getAttendees() != null && toDelete.getAttendees().contains(username))) {
            for (Event event : CalendarManagerImpl.getInstance().getCalendar(username).getEventList()) {
                if (event.getStart().equals(start) && event.getStop().equals(stop))
                    CalendarManagerImpl.getInstance().getCalendar(username).getEventList().remove(event);
            }

            // If username is the owner of this calendar then check attendees.
            if (username.equals(ownerName)) {
                if (toDelete.getAttendees() != null && toDelete.getAttendees().size() > 0) {
                    for (String clientName : toDelete.getAttendees()) {
                        deleteEvent(clientName, start, stop);
                    }
                }
            }
            return true;
        }

        return false;
    }

    //This method will start a new Thread that will run the Clock
    public boolean startClock(Client owner) throws RemoteException {
        if (owner.getName().equals(this.owner.getName())) {
            Clock c = new Clock(this);
            clockThread = new Thread(c);
            clockThread.start();
            return true;
        }
        return false;
    }

    //This method will kill the Clock for the owner passed in
    public void killClock(Client owner) throws RemoteException {
        if (owner.getName().equals(this.owner.getName())) {
            clockThread.interrupt();
        }
    }

    //This method will return this Calendar's owner's name
    public String getOwnerName() {
        return ownerName;
    }

    //This method will change this Calendar's owner's name
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
