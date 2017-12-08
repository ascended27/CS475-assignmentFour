package rmiproject;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Clock implements Runnable {

    //The Calendar whose Events this Clock will be checking
    private Calendar calendar;

    //Synchronization mechanism
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

    //Constructor
    public Clock(Calendar calendar) {
        this.calendar = calendar;
    }

    //Flags and constants
    private boolean running = true;
    private final long MINS15 = 15*60*1000;

    //This method will check if a future event is within 15 min from the current time
    private boolean checkNear(Timestamp ts, Timestamp future)
    {
        return future.after(ts) && timeDiff(ts, future) <= MINS15;
    }

    //Computes the time difference of two Timestamps
    private long timeDiff(Timestamp one, Timestamp two)
    {
        return Math.abs(one.getTime() - two.getTime());
    }

    @Override
    public void run() {
        // Do it until the thread is stopped
        try {

            //The list of events this clock will be monitoring.
            ConcurrentLinkedQueue<Event> eventList = calendar.getEventList();

            //Loop infinitely to check the events every five seconds
            while (running) {
                // Get the current time
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                System.out.println("Time: " + ts.toString());

                // Look for events that start at this time

                if (eventList.size() > 0)
                    for (Event event : eventList) {

                        //Check if event's start time is close to now
                        if (checkNear(ts, event.getStart()) && !event.hasPassed()) {
                            // Notify user
                            calendar.getOwner().notify(event);

                            //Change event to passed (in this case, setNotified)
                            event.setPassed(true);
                        }

                    }
                // Sleep the thread for 5 seconds then start over
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    System.out.println("Clock was interrupted for User: " + calendar.getOwner().getName());
                    running = false;
                }
            }
        } catch (
                RemoteException e)

        {
            System.out.println("Remote Exception: " + e);
        }
    }
}