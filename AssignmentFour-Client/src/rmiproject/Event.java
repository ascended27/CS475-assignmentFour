package rmiproject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The Event class is a container for all the event attributes.
 * Contains setters and getters for the event attributes which
 * have read/write locks to assist with synchronicity.
 */
public class Event implements Serializable {

    private String title;
    private Timestamp start;
    private Timestamp stop;
    private Client owner;
    private String ownerName;
    private List<String> attendees;
    private boolean open;

    // True for public False for private
    private boolean type;
    private boolean passed;

    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public Event(String title, Timestamp start, Timestamp stop, Client owner, String ownerName, List<String> attendees, boolean type, boolean open) {
        this.title = title;
        this.start = start;
        this.stop = stop;
        this.owner = owner;
        this.ownerName = ownerName;
        this.attendees = attendees;
        this.type = type;
        this.open = open;
        this.passed = false;
    }

    public String getTitle() {
        String toReturn;
        try {
            rwLock.readLock().lock();
            toReturn = title;
            return toReturn;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void setTitle(String title) {
        try {
            rwLock.writeLock().lock();
            this.title = title;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public Timestamp getStart() {
        Timestamp toReturn;
        try {
            rwLock.readLock().lock();
            toReturn = start;
            return toReturn;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void setStart(Timestamp start) {
        try {
            rwLock.writeLock().lock();
            this.start = start;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public Timestamp getStop() {
        Timestamp toReturn;
        try {
            rwLock.readLock().lock();
            toReturn = stop;
            return toReturn;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void setStop(Timestamp stop) {
        try {
            rwLock.writeLock().lock();
            this.stop = stop;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public Client getOwner() {
        Client toReturn;
        try {
            rwLock.readLock().lock();
            toReturn = owner;
            return toReturn;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void setOwner(Client owner) {
        try {
            rwLock.writeLock().lock();
            this.owner = owner;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public List<String> getAttendees() {
        List<String> toReturn;
        try {
            rwLock.readLock().lock();
            toReturn = attendees;
            return toReturn;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void setAttendees(List<String> attendees) {
        try {
            rwLock.writeLock().lock();
            this.attendees = attendees;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public boolean isOpen() {
        boolean toReturn;
        try {
            rwLock.readLock().lock();
            toReturn = open;
            return toReturn;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void setOpen(boolean open) {
        try {
            rwLock.writeLock().lock();
            this.open = open;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public boolean isType() {
        boolean toReturn;
        try {
            rwLock.readLock().lock();
            toReturn = type;
            return toReturn;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void setType(boolean type) {
        try {
            rwLock.writeLock().lock();
            this.type = type;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public boolean hasPassed() {
        boolean toReturn;
        try {
            rwLock.readLock().lock();
            toReturn = passed;
            return toReturn;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void setPassed(boolean passed) {
        try {
            rwLock.writeLock().lock();
            this.passed = passed;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}