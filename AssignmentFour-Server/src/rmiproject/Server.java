package rmiproject;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    private static Registry registry;

    //TODO: Test this?
    //This is a testing class
    public static void main(String argv[]) {
        try {
            registry = LocateRegistry.createRegistry(6246);
            System.setSecurityManager(new SecurityManager());
            System.out.println("Server: Registering Calendar Service");
            CalendarManager cm = new CalendarManagerImpl();
            Naming.rebind("rmi://localhost:6246/CalendarService", cm);
            System.out.println("Server: Ready...");
        } catch (Exception e) {
            System.out.println("Server-Error: " + e);
        }
    }

}
