package rmiproject;

import javafx.application.Platform;
import rmiproject.ui.AlertBox;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

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
        // If the event is open notify the user with the open event message
        if (event.isOpen()) {
            // Prepare the message
            String s = String.format("Open Event: \n\tStart: %s\n\tStop: %s\n\n", event.getStart().toString(), event.getStop().toString());

            // Alert the user
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    AlertBox.display("Reminder", s, false);
                }
            });
        }
        // Otherwise use the normal event message
        else {
            // Prepare the message
            String s = String.format("Event: %s\n\tStart: %s\n\tStop: %s\n\tOwner: %s\n\tOpen: %b\n\tPublic: %b\n\tAttendees: %s\n\n",
                    event.getTitle(), event.getStart().toString(), event.getStop().toString(),
                    event.getOwner().getName(), event.isOpen(), event.isType(), event.getAttendees().toString());

            // Alert the user
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    AlertBox.display("Reminder", s, false);
                }
            });
        }

    }

    // Get the name of the user of this clock
    public String getName() throws RemoteException {
        return name;
    }

    // Set the name of the user of this clock
    public void setName(String name) throws RemoteException {
        this.name = name;
    }

}