package rmiproject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The Event class will contain information
 * about an event such as: Title, Name, Time,
 * and Attendees.
 * <p>
 * This class will need to be synchronous. The owner
 * may need to write to their calendar while another
 * user attempting to reading.
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

    public ReadWriteLock rwLock = new ReentrantReadWriteLock();

    /** Constructor
     * @param title This Event's title
     * @param start This Event's start time
     * @param stop This Event's stop time
     * @param owner This Event's Client owner
     * @param attendees This Event's attendees
     * @param type This Event's privacy type
     * @param open This Event's open condition
     */
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

    /**
     * Returns this Event's title. Uses locking mechanisms to make safe read.
     * @return This Event's title
     */
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

    /**
     * Set's this Event's title. Uses locking mechanisms to safely write.
     * @param title This Event's title.
     */
    public void setTitle(String title) {
        try {
            rwLock.writeLock().lock();
            this.title = title;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Return's this Event's start time. Uses locking mechanisms to safely read.
     * @return This Event's start time.
     */
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

    /**
     * Sets this Event's start time. Uses locking mechanisms to safely write.
     * @param start This Event's new start time.
     */
    public void setStart(Timestamp start) {
        try {
            rwLock.writeLock().lock();
            this.start = start;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Returns this Event's stop time. Uses locking mechanisms to safely read.
     * @return This Event's stop time.
     */
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

    /**
     * Sets this Event's stop time. Uses locking mechanisms to safely write.
     * @param stop This Event's new stop time
     */
    public void setStop(Timestamp stop) {
        try {
            rwLock.writeLock().lock();
            this.stop = stop;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Returns this Event's owner. Uses locking mechanisms to safely read.
     * @return This Event's owner
     */
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

    /**
     * Sets this Event's Client owner. Uses locking mechanisms to safely write.
     * @param owner This Event's new Client owner.
     */
    public void setOwner(Client owner) {
        try {
            rwLock.writeLock().lock();
            this.owner = owner;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Returns a list of this Event's attendees. Uses locking mechanisms to safely read.
     * @return A list of this Event's attendees.
     */
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

    /**
     * Sets this Event's attendees. Uses locking mechanisms to safely write.
     * @param attendees This Event's new attendees.
     */
    public void setAttendees(List<String> attendees) {
        try {
            rwLock.writeLock().lock();
            this.attendees = attendees;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Checks whether this Event is an open Event. Uses locking mechanisms to safely read.
     * @return true if this Event is open, false otherwise.
     */
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

    /**
     * Set whether this Event is open or regular. Uses locking mechanisms to safely write.
     * @param open Whether this Event will be open or regular.
     */
    public void setOpen(boolean open) {
        try {
            rwLock.writeLock().lock();
            this.open = open;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Returns whether this Event is private or public. Uses locking mechanisms to safely read.
     * @return True if this Event is public, false otherwise.
     */
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

    /**
     * Set whether this Event is public or private. Uses locking mechanisms to safely write.
     * @param type Whether this Event will be public or private.
     */
    public void setType(boolean type) {
        try {
            rwLock.writeLock().lock();
            this.type = type;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Checks whether this Event has already been notified to the user. Uses locking mechanisms to safely read.
     * @return Whether this Event has already been notified to the user.
     */
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

    /**
     * Sets whether this Event has already been notified to the user. Uses locking mechanisms to safely write.
     * @param passed Whether this Event was notified to the user.
     */
    public void setPassed(boolean passed) {
        try {
            rwLock.writeLock().lock();
            this.passed = passed;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Returns this Event's owner's name.
     * @return This Event's owner's name.
     */
    public String getOwnerName() {
        try {
            rwLock.writeLock().lock();
            return this.ownerName;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Sets this Event's owner's name.
     * @param ownerName This Event's new owner's name.
     */
    public void setOwnerName(String ownerName) {
        try {
            rwLock.writeLock().lock();
            this.ownerName = ownerName;
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}