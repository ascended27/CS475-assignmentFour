package rmiproject.ui;

import rmiproject.Client;
import rmiproject.ClientImpl;
import rmiproject.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class NewEventController {
    @FXML
    public ToggleGroup privateGroup;
    @FXML
    public RadioButton yesPrivate;
    @FXML
    public RadioButton noPrivate;
    @FXML
    public TextField titleTF;
    @FXML
    public TextField startTF;
    @FXML
    public TextField stopTF;
    @FXML
    public Button cancelButton;
    @FXML
    public Button savedButton;
    @FXML
    public ListView clientList;
    @FXML
    public ListView selectedClients;
    @FXML
    public Button selectClient;
    @FXML
    public Button removeClient;

    private Util utils;
    private ObservableList<String> selectedClientsList = FXCollections.observableArrayList();
    private ObservableList<String> notSelectedClientsList = FXCollections.observableArrayList();

    public NewEventController() throws RemoteException {
    }

    @FXML
    public void initialize() {
        utils = Util.getInstance();

        for (String c : utils.getUsers()) {
            try {
                if (!c.equals(utils.getOwner().getName())) {
                    notSelectedClientsList.add(c);
                }
            } catch (RemoteException e) {
                AlertBox.display("Error", "Failed to load users",false);
            }
        }

        clientList.setItems(notSelectedClientsList);
        selectedClients.setItems(selectedClientsList);
    }

    public void cancel(MouseEvent mouseEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void save(MouseEvent mouseEvent) {
        if (!allFilledIn()) {
            AlertBox.display("Error", "All text fields must be filled in",false);
        } else {
            try {
                String title = titleTF.getText();
                Timestamp start = utils.convertTime(startTF.getText());
                Timestamp stop = utils.convertTime(stopTF.getText());
                boolean isPrivate = !yesPrivate.isSelected();
                ArrayList<String> attendees = new ArrayList<>();

                for (String user : selectedClientsList) {
                    attendees.add(user);
                }

                if (!utils.scheduleEvent(new Event(title, start, stop, utils.getOwner(), utils.getOwner().getName(), attendees, isPrivate, false)))
                    AlertBox.display("Error", "Failed to save event",false);

                Stage stage = (Stage) savedButton.getScene().getWindow();
                stage.close();
            } catch (RemoteException e) {
                AlertBox.display("Error", "Failed to save event",false);
            }
        }
    }

    public void moveClientRight(MouseEvent mouseEvent) {
        String selected = (String) clientList.getSelectionModel().getSelectedItem();
        for (int i = 0; i < notSelectedClientsList.size(); i++) {
            if (notSelectedClientsList.get(i).equals(selected)) {
                notSelectedClientsList.remove(i);
                selectedClientsList.add(selected);
                break;
            }
        }
        clientList.getSelectionModel().clearSelection();
    }

    public void moveClientLeft(MouseEvent mouseEvent) {
        String selected = (String) selectedClients.getSelectionModel().getSelectedItem();
        for (int i = 0; i < selectedClientsList.size(); i++) {
            if (selectedClientsList.get(i).equals(selected)) {
                selectedClientsList.remove(i);
                notSelectedClientsList.add(selected);
                break;
            }
        }
        selectedClients.getSelectionModel().clearSelection();
    }

    private boolean allFilledIn() {
        return !startTF.getText().isEmpty() && !stopTF.getText().isEmpty() && !titleTF.getText().isEmpty();
    }
}
