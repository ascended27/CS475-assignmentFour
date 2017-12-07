package rmiproject.ui;

import rmiproject.*;
import javafx.collections.ObservableList;

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


        // Get a remote reference to the EchoImpl class
//        String strName = "rmi://localhost/CalendarService";
        String strName = "rmi://localhost:6246/CalendarService";
        System.out.println("Client: Looking up " + strName + "...");
        cm = null;

        try {
            cm = (CalendarManager) Naming.lookup(strName);
        } catch (Exception e) {
            System.out.println("Client: Exception thrown looking up " + strName);
            e.printStackTrace();
            System.exit(1);
        }
    }

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

    public ArrayList<Event> getEventList(Client client) {
        try {
            if (owner != null) {
                Calendar cal = cm.getCalendar(client);
                ArrayList<Event> toReturn = new ArrayList<>();
                toReturn.addAll(cal.getEventList());
                return toReturn;
            } else
                return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public Client getClient(String username) {
        try {
            return cm.lookup(username);
        } catch (RemoteException e) {
            e.printStackTrace();
            AlertBox.display("Error","Failed to lookup client: " + username);
        }
        return null;
    }

    public boolean scheduleEvent(Event e) {
        try {
            if (owner != null) {

                boolean toReturn = cm.getCalendar(owner).scheduleEvent(e.getOwner(), e.getAttendees(), e.getTitle(), e.getStart(), e.getStop(), e.isType());

                if (toReturn && observableList != null) {
                    observableList.clear();
                    for (Event event : cm.getCalendar(owner).getEventList()) {
                        EventRow er = new EventRow(event.getOwnerName(), event.getTitle(), event.getStart(), event.getStop());
                        observableList.addAll(er);
                    }
                }

                return toReturn;
            } else return false;
        } catch (RemoteException ex) {
            return false;
        }
    }

    public boolean insertOpenEvent(Event e) {
        try {
            if (owner != null) {
                boolean toReturn = cm.getCalendar(owner).insertOpenEvent(e.getOwner(), e.getStart(), e.getStop());
                if (toReturn && observableList != null)
                    observableList.add(new EventRow(e.getOwner().getName(), e.getTitle(), e.getStart(), e.getStop()));
                return toReturn;
            } else
                return false;
        } catch (RemoteException ex) {
            return false;
        }
    }

    public Event retrieveEvent(Timestamp start, Timestamp stop) {
        try {
            return cm.getCalendar(owner).retrieveEvent(start, stop);
        } catch (RemoteException e) {
            return null;
        }
    }

    public Event retrieveEventForClient(String username, Timestamp start, Timestamp stop){
        try{
            return cm.getCalendar(username).retrieveEvent(start,stop);
        } catch (RemoteException e){
            return null;
        }
    }

    public boolean killClock() {
        try {
            if (owner != null) {
                Calendar cal = cm.getCalendar(owner);
                if (cal != null) {
                    cal.killClock(owner);
                    return true;
                } else
                    return false;
            } else return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    public ArrayList<String> getUsers() {
        try {
            return (ArrayList<String>) cm.allUsers();
        } catch (RemoteException e) {
            return new ArrayList<>();
        }
    }

    public boolean checkUser(String username) {
        try {
            for (String name : cm.allUsers()) {
                if (name.equals(username)) {
                    owner = new ClientImpl(name);
                    break;
                }
            }
            if (owner == null) {
                owner = new ClientImpl(username);
            }
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public Calendar getCalendar(String username){
        try {
            return cm.getCalendar(new ClientImpl(username));
        } catch (RemoteException e) {
            AlertBox.display("Error","Failed to get calendar for: " + username);
            e.printStackTrace();
        }
        return null;
    }


    public Timestamp convertTime(String time) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
            Timestamp toReturn;
            Date parsedDate;
            parsedDate = dateFormat.parse(time);
            toReturn = new Timestamp(parsedDate.getTime());
            return toReturn;
        } catch (ParseException e) {
            return null;
        }
    }

    public void registerTableList(ObservableList<EventRow> list) {
        this.observableList = list;
    }

    public ObservableList<EventRow> getTableList() {
        return observableList;
    }

    public void setOwner(Client client) {
        this.owner = client;
    }

    public Client getOwner() {
        return this.owner;
    }

    public void setRetrievedEventRow(EventRow retrievedEventRow) {
        this.retrievedEventRow = retrievedEventRow;
    }

    public EventRow getRetrievedEventRow() {
        return retrievedEventRow;
    }

    public void setSelectedClient(String selectedClient) {
        this.selectedClient = selectedClient;
    }

    public String getSelectedClient() {
        return this.selectedClient;
    }
}
