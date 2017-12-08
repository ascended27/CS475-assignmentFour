package rmiproject.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import rmiproject.Event;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;

public class RetrievedEventController {
    @FXML
    public Text ownerText;
    @FXML
    public Text titleText;
    @FXML
    public Text startText;
    @FXML
    public Text stopText;
    @FXML
    public TextField titleTextField;
    @FXML
    public TextField startTextField;
    @FXML
    public TextField stopTextField;
    @FXML
    public ListView attendees;
    @FXML
    public Button closeButton;
    @FXML
    public Button saveButton;
    @FXML
    public RadioButton yesRadio;
    @FXML
    public ToggleGroup privateGroup;
    @FXML
    public RadioButton noRadio;
    @FXML
    public Button deleteButton;

    private Util utils;
    private Timestamp start;
    private Timestamp stop;
    private Event event;
    private ObservableList<String> attendeeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Get an instance of the utils
        utils = Util.getInstance();
        if (utils != null) {
            // Get the selected event row from utils
            EventRow er = utils.getRetrievedEventRow();
            if (er != null) {
                try {
                    // If the selected client isn't null then get its name
                    if (utils.getSelectedClient() == null)
                        utils.setSelectedClient(utils.getOwner().getName());
                    // Get the event for the selected client
                    event = utils.retrieveEventForClient(utils.getSelectedClient(), utils.getRetrievedEventRow().getStart(), utils.getRetrievedEventRow().getStop());
                    // If the event was retrieved then display it
                    if (event != null) {
                        // If it is a private event and this user isn't an owner then display private event for the title
                        if (!event.isType() && !event.getOwnerName().equals(utils.getOwner().getName())) {
                            titleText.setText("Private Event");
                            titleTextField.setText("Private Event");
                        }
                        // If the event has attendees
                        else if (event.getAttendees() != null && event.getAttendees().size() != 0) {
                            // and if the user is isn't in those attendees don't show them anything besides time
                            if (!event.getOwnerName().equals(utils.getOwner().getName()) && !event.getAttendees().contains(utils.getOwner().getName())) {
                                titleText.setText("Group Event");
                                titleTextField.setText("Group Event");
                                ownerText.setVisible(false);
                                titleTextField.setVisible(false);
                                startTextField.setVisible(false);
                                stopTextField.setVisible(false);
                                attendees.setVisible(false);
                                yesRadio.setVisible(false);
                                noRadio.setVisible(false);
                            } else{
                                titleText.setText(event.getTitle());
                                titleTextField.setText(event.getTitle());
                            }
                        }
                        // Else they can see everything
                        else {
                            titleText.setText(event.getTitle());
                            titleTextField.setText(event.getTitle());
                        }
                        // Set the private field
                        if (!event.isType()) {
                            yesRadio.setSelected(true);
                        } else noRadio.setSelected(true);
                        // Set the other fields
                        ownerText.setText(event.getOwnerName());
                        startText.setText(event.getStart().toString());
                        stopText.setText(event.getStop().toString());
                        startTextField.setText(event.getStart().toString());
                        stopTextField.setText(event.getStop().toString());
                        // Set the attendees
                        List<String> aList = event.getAttendees();
                        if (aList != null) {
                            attendeeList.addAll(event.getAttendees());
                            attendees.setItems(attendeeList);
                        }
                        // This client owns the event allow edit
                        if (event.getOwnerName().equals(utils.getOwner().getName())) {
                            titleText.setVisible(false);
                            startText.setVisible(false);
                            stopText.setVisible(false);
                        } else if (event.getAttendees() != null && event.getAttendees().contains(utils.getOwner().getName())) { // This client is an attendee
                            titleText.setVisible(false);
                            startText.setVisible(false);
                            stopText.setVisible(false);
                            deleteButton.setVisible(false);
                        } else { // The client isn't the owner and isn't in attendees
                            titleTextField.setVisible(false);
                            startTextField.setVisible(false);
                            stopTextField.setVisible(false);
                            yesRadio.setDisable(true);
                            noRadio.setDisable(true);
                            saveButton.setVisible(false);
                            saveButton.setDisable(true);
                            deleteButton.setVisible(false);
                        }

                    } else {
                        // If the event is null then just print the event row
                        startText.setText(er.getStart().toString());
                        stopText.setText(er.getStop().toString());
                        ownerText.setVisible(false);
                        titleTextField.setVisible(false);
                        startTextField.setVisible(false);
                        stopTextField.setVisible(false);
                        attendees.setVisible(false);
                        yesRadio.setVisible(false);
                        noRadio.setVisible(false);
                    }
                } catch (RemoteException e) {
                    AlertBox.display("Error", e.getMessage(), false);
                }
            }
        }
    }

    // Closes the window
    public void close(MouseEvent mouseEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    // Saves the event after editing
    public void save(MouseEvent mouseEvent) {
        // Get the new time
        Timestamp newStart = utils.convertTime(startTextField.getText());
        Timestamp newStop = utils.convertTime(stopTextField.getText());
        if(newStart == null || newStop == null){
            AlertBox.display("Error","Time must be of format MM/DD/YYYY HH:SS",false);
        } else {
            // Make a new event that has the edited values
            Event editedEvent = new Event(titleTextField.getText(), newStart, newStop, event.getOwner(), event.getOwnerName(), event.getAttendees(), !yesRadio.isSelected(), event.isOpen());
            // Save the edited event
            utils.editEvent(editedEvent, event.getStart(), event.getStop());

            // Close the stage
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        }
    }

    // Set the start of the event
    public void setStart(Timestamp start) {
        this.start = start;
    }

    // Set the stop of the event
    public void setStop(Timestamp stop) {
        this.stop = stop;
    }

    // Deletes the selected event
    public void delete(MouseEvent mouseEvent) {
        // Get start and stop
        Timestamp start = utils.convertTime(startTextField.getText());
        Timestamp stop = utils.convertTime(stopTextField.getText());
        try {
            // Delete the event
            utils.deleteEvent(utils.getOwner().getName(), start, stop);

            List<EventRow> eventTable = utils.getTableList();
            eventTable.clear();
            for(Event event : utils.getEventList(utils.getOwner())){
                eventTable.add(new EventRow(event.getOwnerName(),event.getTitle(),event.getStart(),event.getStop()));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            AlertBox.display("Error", "Failed to delete event", false);
        }

        // Close the stage
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
