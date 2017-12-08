package rmiproject;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client extends Remote {

    /**
     * Notifies the User that an event has started.
     *
     * @param event The event to notify the user with
     */
    void notify(Event event) throws RemoteException;

    /**
     * Gets the name of the client
     *
     * @return The name of the client
     * @throws RemoteException
     */
    String getName() throws RemoteException;

    /**
     * Sets the name of the client
     *
     * @param name The name to set the client to
     * @throws RemoteException
     */
    void setName(String name) throws RemoteException;
}