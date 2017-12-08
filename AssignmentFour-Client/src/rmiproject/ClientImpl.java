package rmiproject;

import javafx.application.Platform;
import rmiproject.ui.AlertBox;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * ------- From the PDF ------------------------------
 * The User Interface program lets the user perform the operations
 * defined in the previous section. In particular, this program
 * presents the user with an interface that permits her to view
 * calendars, to modify appropriate events, and to schedule group
 * events. The user interface usually communicates with its
 * Calendar object to perform these services, but occasionally,
 * the Calendar object might need to notify the user of scheduled events
 * such as appointments. In this system, the user interface interacts
 * solely with its designated Calendar which acts as proxy for any operations that
 * require the services of other Calendars. This assignment is not
 * about user interface design. We want you to focus on the
 * distributed computing aspects of the problem. A simple text-based
 * interface will suffice. However, if you are familiar with Java,
 * it is relatively straightforward to design a simple GUI for this application
 * ---------------------------------------------------
 */
public class ClientImpl extends UnicastRemoteObject implements Client {
    private String name;

    public ClientImpl(String name) throws RemoteException {
        this.name = name;
    }

    /**
     * Notifies the User that an event has started.
     *
     * @param event The event to notify the user with
     */
    public void notify(Event event) throws RemoteException {
        if (event.isOpen()) {
            String s = String.format("Open Event: \n\tStart: %s\n\tStop: %s\n\n", event.getStart().toString(), event.getStop().toString());

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    AlertBox.display("Reminder", s, false);
                }
            });
        }
        else {
            String s = String.format("Event: %s\n\tStart: %s\n\tStop: %s\n\tOwner: %s\n\tOpen: %b\n\tPublic: %b\n\tAttendees: %s\n\n",
                    event.getTitle(), event.getStart().toString(), event.getStop().toString(),
                    event.getOwner().getName(), event.isOpen(), event.isType(), event.getAttendees().toString());

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    AlertBox.display("Reminder", s, false);
                }
            });
        }

    }

    public String getName() throws RemoteException {
        return name;
    }

    public void setName(String name) throws RemoteException {
        this.name = name;
    }

}