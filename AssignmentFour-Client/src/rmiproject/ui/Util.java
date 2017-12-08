package rmiproject.ui;

import javafx.collections.ObservableList;
import rmiproject.*;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Util {

    private static Util instance;
    private CalendarManager cm;
    private Client owner;
    private ObservableList<EventRow> observableList;
    private EventRow retrievedEventRow;
    private String selectedClient;

    private Util() throws RemoteException {

        // Get a remote reference to the CalendarService
        String strName = "rmi://localhost:6246/CalendarService";
        System.out.println("Client: Looking up " + strName + "...");
        cm = null;

        try {
            // Get a calendar manager
            cm = (CalendarManager) Naming.lookup(strName);
        } catch (Exception e) {
            System.out.println("Client: Exception thrown looking up " + strName);
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Get the instance of Util or a new one if it doesn't exist
    public static Util getInstance() {
        if (instance == null) {
            try {
                instance = new Util();
            } catch (RemoteException ex) {
                return null;
            }
        }
        return instance;
    }

    // Retrieve the list of events for the specified client
    public ArrayList<Event> getEventList(Client client) {
        try {
            if (owner != null) {
                // Get the calendar
                Calendar cal = cm.getCalendar(client);
                // Extract the event list
                ArrayList<Event> toReturn = new ArrayList<>();
                toReturn.addAll(cal.getEventList());
                // Return the event list
                return toReturn;
            } else
                return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    // Schedules the passed event
    public boolean scheduleEvent(Event e) {
        try {
            if (owner != null) {
                // Schedules the passed event
                boolean toReturn = cm.getCalendar(owner).scheduleEvent(e.getOwner(), e.getOwnerName(), e.getAttendees(), e.getTitle(), e.getStart(), e.getStop(), e.isType());

                // Adds the event to the event table if it was successfully added to the calendar
                if (toReturn && observableList != null) {
                    observableList.clear();
                    for (Event event : cm.getCalendar(owner).getEventList()) {
                        EventRow er = new EventRow(event.getOwnerName(), event.getTitle(), event.getStart(), event.getStop());
                        observableList.addAll(er);
                    }
                }
                // Return if the schedule was successfully or not
                return toReturn;
            } else return false;
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Edits the event at oldStart and oldStop with the passed event
    public boolean editEvent(Event e, Timestamp oldStart, Timestamp oldStop) {
        if (owner != null) {
            try {
                // Attempts to edit the event
                boolean toReturn = cm.getCalendar(owner).editEvent(e.getOwnerName(), e.getTitle(), oldStart, oldStop, e.getStart(), e.getStop(), e.isType());

                // Updates the event table if the event was successfully edited
                if (toReturn && observableList != null) {
                    observableList.clear();
                    for (Event event : cm.getCalendar(owner).getEventList()) {
                        EventRow er = new EventRow(event.getOwnerName(), event.getTitle(), event.getStart(), event.getStop());
                        observableList.addAll(er);
                    }
                }
                // Return if the edit was successfull or not
                return toReturn;
            } catch (RemoteException e1) {
                e1.printStackTrace();
                return false;
            }
        }
        return false;
    }

    // Inserts the passed open event
    public boolean insertOpenEvent(Event e) {
        try {
            if (owner != null) {
                // Attempts to schedule the open event
                boolean toReturn = cm.getCalendar(owner).insertOpenEvent(e.getOwner(), e.getStart(), e.getStop());
                // If it was successful then update the event table
                if (toReturn && observableList != null)
                    observableList.add(new EventRow(e.getOwner().getName(), e.getTitle(), e.getStart(), e.getStop()));
                // Return if the insert was successful or not
                return toReturn;
            } else
                return false;
        } catch (RemoteException ex) {
            return false;
        }
    }

    // Gets the event at start and stop for the passed username
    public Event retrieveEventForClient(String username, Timestamp start, Timestamp stop) {
        try {
            // Returns the event if it can be found
            return cm.getCalendar(username).retrieveEvent(username, start, stop);
        } catch (RemoteException e) {
            return null;
        }
    }

    public boolean deleteEvent(String username, Timestamp start, Timestamp stop){
        try{
            return cm.getCalendar(username).deleteEvent(username,start,stop);
        } catch(RemoteException e){
            return  false;
        }
    }

    // Kills the clients server clock that notifies of an upcoming event
    public boolean killClock() {
        try {
            if (owner != null) {
                // Get the owner's calendar
                Calendar cal = cm.getCalendar(owner);
                if (cal != null) {
                    // Kill the clock for the calendar
                    cal.killClock(owner);
                    return true;
                } else
                    return false;
            } else return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    // Returns an array list of user names
    public ArrayList<String> getUsers() {
        try {
            return (ArrayList<String>) cm.allUsers();
        } catch (RemoteException e) {
            return new ArrayList<>();
        }
    }

    // Get the passed user's calendar
    public Calendar getCalendar(String username) {
        try {
            // Returns the user's calendar
            return cm.getCalendar(new ClientImpl(username));
        } catch (RemoteException e) {
            AlertBox.display("Error", "Failed to get calendar for: " + username, false);
            e.printStackTrace();
        }
        return null;
    }

    // converts a string time to a Timestamp time
    public Timestamp convertTime(String time) {
        try {
            // Create a dateformat for both normal user input and Timestamp format
            SimpleDateFormat dateFormat;
            if (time.contains("/"))
                dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
            else if (time.contains("-"))
                return Timestamp.valueOf(time);
            else return null;

            Timestamp toReturn;
            Date parsedDate;

            // Parse the time and return it
            parsedDate = dateFormat.parse(time);
            toReturn = new Timestamp(parsedDate.getTime());
            return toReturn;
        } catch (ParseException e) {
            return null;
        }
    }

    // Setter for the event table
    public void registerTableList(ObservableList<EventRow> list) {
        this.observableList = list;
    }

    // Getter for the event table
    public ObservableList<EventRow> getTableList() {
        return this.observableList;
    }


    // Setter for owner
    public void setOwner(Client client) {
        this.owner = client;
    }

    // Getter for owner
    public Client getOwner() {
        return this.owner;
    }

    // Setter for the retrieved event row
    public void setRetrievedEventRow(EventRow retrievedEventRow) {
        this.retrievedEventRow = retrievedEventRow;
    }

    // Getter for the retrieved event row
    public EventRow getRetrievedEventRow() {
        return retrievedEventRow;
    }

    // Setter for the selected client
    public void setSelectedClient(String selectedClient) {
        this.selectedClient = selectedClient;
    }

    // Getter for the selected client
    public String getSelectedClient() {
        return this.selectedClient;
    }
}
