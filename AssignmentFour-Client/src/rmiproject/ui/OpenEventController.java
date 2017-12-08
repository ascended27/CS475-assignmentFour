package rmiproject.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import rmiproject.Event;

import java.rmi.RemoteException;
import java.sql.Timestamp;

public class OpenEventController {
    @FXML
    public TextField startTF;
    @FXML
    public TextField stopTF;
    @FXML
    public Button cancelButton;
    @FXML
    public Button savedButton;

    private Util utils;

    @FXML
    public void initialize() {
        // Gets an instance of the utils
        this.utils = Util.getInstance();
    }

    // Closes this window
    public void cancel(MouseEvent mouseEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    // Saves the event
    public void save(MouseEvent mouseEvent) {
        // Verify that all the required values are filled in
        if (isAllFilledIn()) {
            // Get the start and stop times
            Timestamp start = utils.convertTime(startTF.getText());
            Timestamp stop = utils.convertTime(stopTF.getText());
            if (start == null || stop == null) {
                AlertBox.display("Error", "Time must be of format MM/DD/YYYY HH:SS", false);
            } else {
                if(start.compareTo(stop) <= 0) {

                    try {
                        // Make a new event and attempt to insert it
                        Event event = new Event("Open Event", start, stop, utils.getOwner(), utils.getOwner().getName(), null, true, true);
                        if (!utils.insertOpenEvent(event)) {
                            AlertBox.display("Error", "Failed to schedule open event", false);
                        }
                    } catch (RemoteException ex) {
                        AlertBox.display("Error", "Failed to schedule open event", false);
                    }

                    // Close the stage after saving
                    Stage stage = (Stage) savedButton.getScene().getWindow();
                    stage.close();
                } else{
                    AlertBox.display("Error","Start must be before End",false);
                }
            }
        } else {
            AlertBox.display("Error", "All fields must be filled in", false);
        }
    }

    // Verifies all required fields are filled in
    private boolean isAllFilledIn() {
        return !startTF.getText().isEmpty() && !stopTF.getText().isEmpty();
    }
}
