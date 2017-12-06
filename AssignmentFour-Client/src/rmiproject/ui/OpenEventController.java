package rmiproject.ui;

import rmiproject.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

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
        this.utils = Util.getInstance();
    }

    public void cancel(MouseEvent mouseEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void save(MouseEvent mouseEvent) {
        if (isAllFilledIn()) {
            Timestamp start = utils.convertTime(startTF.getText());
            Timestamp stop = utils.convertTime(stopTF.getText());
            Event event = new Event("Open Event", start, stop, utils.getOwner(), null, true, true);
            if (!utils.insertOpenEvent(event)) {
                AlertBox.display("Error", "Failed to schedule open event");
            }
            Stage stage = (Stage) savedButton.getScene().getWindow();
            stage.close();
        } else {
            AlertBox.display("Error", "All fields must be filled in");
        }
    }

    private boolean isAllFilledIn() {
        return !startTF.getText().isEmpty() && !stopTF.getText().isEmpty();
    }
}
