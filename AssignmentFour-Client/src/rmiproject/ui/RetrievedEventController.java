package rmiproject.ui;

import rmiproject.Client;
import rmiproject.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;

public class RetrievedEventController {
    @FXML
    public Text titleText;
    @FXML
    public Text ownerText;
    @FXML
    public Text startText;
    @FXML
    public Text stopText;
    @FXML
    public ListView attendees;
    @FXML
    public Button closeButton;

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
                    event = utils.retrieveEventForClient(utils.getSelectedClient(),utils.getRetrievedEventRow().getStart(), utils.getRetrievedEventRow().getStop());
                    titleText.setText(event.getTitle());
                    ownerText.setText(event.getOwner().getName());
                    startText.setText(event.getStart().toString());
                    stopText.setText(event.getStop().toString());
                    List<Client> aList = event.getAttendees();
                    if (aList != null) {
                        for (Client c : event.getAttendees()) {
                            attendeeList.add(c.getName());
                        }
                    }
                } catch (RemoteException e) {
                    AlertBox.display("Error", e.getMessage());
                }
            } else {
                AlertBox.display("Error", "No event selected");
            }
        }
    }

    public void close(MouseEvent mouseEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public void setStop(Timestamp stop) {
        this.stop = stop;
    }
}
