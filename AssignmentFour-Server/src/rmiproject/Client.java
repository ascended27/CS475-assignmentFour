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
     * Returns this Client's name.
     * @return This Client's name
     * @throws RemoteException
     */
    String getName() throws RemoteException;

    /**
     * Sets this Client's name.
     * @param name This Client's new name.
     * @throws RemoteException
     */
    void setName(String name) throws RemoteException;
}
