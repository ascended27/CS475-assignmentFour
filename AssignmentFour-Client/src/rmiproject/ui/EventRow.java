package rmiproject.ui;

import java.sql.Timestamp;

public class EventRow {
    private String ownerName;
    private String title;
    private Timestamp start;
    private Timestamp stop;

    public EventRow(String ownerName, String title, Timestamp start, Timestamp stop) {
        this.ownerName = ownerName;
        this.title = title;
        this.start = start;
        this.stop = stop;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getStop() {
        return stop;
    }

    public void setStop(Timestamp stop) {
        this.stop = stop;
    }
}
