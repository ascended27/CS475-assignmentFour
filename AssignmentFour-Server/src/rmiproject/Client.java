package rmiproject;

import java.rmi.Remote;
import java.rmi.RemoteException;

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
public interface Client extends Remote {

    /**
     * Notifies the User that an event has started.
     *
     * @param event The event to notify the user with
     */
    void notify(Event event) throws RemoteException;

    String getName() throws RemoteException;

    void setName(String name) throws RemoteException;
}
