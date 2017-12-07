package rmiproject.ui;

import javafx.scene.control.*;
import rmiproject.Client;
import rmiproject.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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

    private Util utils;
    private Timestamp start;
    private Timestamp stop;
    private Event event;
    private ObservableList<String> attendeeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        utils = Util.getInstance();
        if (utils != null) {
            EventRow er = utils.getRetrievedEventRow();
            if (er != null) {
                try {
                    if (utils.getSelectedClient() == null)
                        utils.setSelectedClient(utils.getOwner().getName());
                    event = utils.retrieveEventForClient(utils.getSelectedClient(), utils.getRetrievedEventRow().getStart(), utils.getRetrievedEventRow().getStop());
                    if (event != null) {
                        if (!event.isType() && !event.getOwnerName().equals(utils.getOwner().getName())) {
                            titleText.setText("Private Event");
                            titleTextField.setText("Private Event");
                        } else if (event.getAttendees() != null && event.getAttendees().size() != 0) {
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
                            }
                        } else {
                            titleText.setText(event.getTitle());
                            titleTextField.setText(event.getTitle());
                        }
                        if (!event.isType()) {
                            yesRadio.setSelected(true);
                        } else noRadio.setSelected(true);
                        ownerText.setText(event.getOwnerName());
                        startText.setText(event.getStart().toString());
                        stopText.setText(event.getStop().toString());
                        startTextField.setText(event.getStart().toString());
                        stopTextField.setText(event.getStop().toString());
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
                        } else { // The client isn't the owner and isn't in attendees
                            titleTextField.setVisible(false);
                            startTextField.setVisible(false);
                            stopTextField.setVisible(false);
                            yesRadio.setDisable(true);
                            noRadio.setDisable(true);
                            saveButton.setVisible(false);
                            saveButton.setDisable(true);
                        }

                    } else {
                        startText.setText(er.getStart().toString());
                        stopText.setText(er.getStop().toString());
                        ownerText.setVisible(false);
                        titleTextField.setVisible(false);
                        startTextField.setVisible(false);
                        stopTextField.setVisible(false);
                        attendees.setVisible(false);
                        yesRadio.setVisible(false);
                        noRadio.setVisible(false);
                        er.getStop();
                        AlertBox.display("Error", "No event selected", false);
                    }
                } catch (RemoteException e) {
                    AlertBox.display("Error", e.getMessage(), false);
                }
            }
        }
    }

    public void close(MouseEvent mouseEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void save(MouseEvent mouseEvent) {
        Timestamp newStart = utils.convertTime(startTextField.getText());
        Timestamp newStop = utils.convertTime(stopTextField.getText());
        Event edittedEvent = new Event(titleTextField.getText(), newStart, newStop, event.getOwner(), event.getOwnerName(), event.getAttendees(), !yesRadio.isSelected(), event.isOpen());
        utils.editEvent(edittedEvent, event.getStart(), event.getStop());
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public void setStop(Timestamp stop) {
        this.stop = stop;
    }
}
