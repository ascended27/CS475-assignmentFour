package rmiproject;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Clock implements Runnable {

    private Calendar calendar;

    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public Clock(Calendar calendar) {
        this.calendar = calendar;
    }

    private boolean running = true;
    private final long MINS15 = 15*60*1000;

    private boolean checkNear(Timestamp ts, Timestamp future)
    {
        return future.after(ts) && timeDiff(ts, future) <= MINS15;
    }

    private long timeDiff(Timestamp one, Timestamp two)
    {
        return Math.abs(one.getTime() - two.getTime());
    }

    @Override
    public void run() {
        // Do it until the thread is stopped
        try {
            ConcurrentLinkedQueue<Event> eventList = calendar.getEventList();



            while (running) {
                // Get the current time
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                System.out.println("Time: " + ts.toString());

                // Look for events that start at this time

                if (eventList.size() > 0)
                    for (Event event : eventList) {
                        if (checkNear(ts, event.getStart()) && !event.hasPassed()) {
                            // Notify user
                            calendar.getOwner().notify(event);

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