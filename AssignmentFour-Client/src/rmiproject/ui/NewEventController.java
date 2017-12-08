package rmiproject.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import rmiproject.Event;

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
        // Get an instance of the utils
        utils = Util.getInstance();

        // Loop over the users in the server and add all of them that are not this user
        // to the not selected clients list for the ui.
        for (String c : utils.getUsers()) {
            try {
                if (!c.equals(utils.getOwner().getName())) {
                    notSelectedClientsList.add(c);
                }
            } catch (RemoteException e) {
                AlertBox.display("Error", "Failed to load users", false);
            }
        }

        // Put the data in the ListViews
        clientList.setItems(notSelectedClientsList);
        selectedClients.setItems(selectedClientsList);
    }

    // Closes the new event window
    public void cancel(MouseEvent mouseEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    // Saves the event to the server
    public void save(MouseEvent mouseEvent) {
        // If all the required fields are not filled in then notify the user
        if (!allFilledIn()) {
            AlertBox.display("Error", "All text fields must be filled in", false);
        } else {
            try {

                // Get the fields
                String title = titleTF.getText();
                Timestamp start = utils.convertTime(startTF.getText());
                Timestamp stop = utils.convertTime(stopTF.getText());

                if(start == null || stop == null){
                    AlertBox.display("Error","Time must be of format MM/DD/YYYY HH:SS",false);
                } else {

                    boolean isPrivate = !yesPrivate.isSelected();
                    ArrayList<String> attendees = new ArrayList<>();

                    // Get the selected clients
                    for (String user : selectedClientsList) {
                        attendees.add(user);
                    }

                    // Attempt to schedule the event and display an error if it fails
                    if (!utils.scheduleEvent(new Event(title, start, stop, utils.getOwner(), utils.getOwner().getName(), attendees, isPrivate, false)))
                        AlertBox.display("Error", "Failed to save event", false);

                    // Close the window
                    Stage stage = (Stage) savedButton.getScene().getWindow();
                    stage.close();
                }
            } catch (RemoteException e) {
                AlertBox.display("Error", "Failed to save event", false);
            }
        }
    }

    // Moves the selected client from the not selected list to the selected list
    public void moveClientRight(MouseEvent mouseEvent) {
        // Get the selected user
        String selected = (String) clientList.getSelectionModel().getSelectedItem();
        // Find the user and remove it from the not selected list and add it to the selected list
        for (int i = 0; i < notSelectedClientsList.size(); i++) {
            if (notSelectedClientsList.get(i).equals(selected)) {
                notSelectedClientsList.remove(i);
                selectedClientsList.add(selected);
                break;
            }
        }
        clientList.getSelectionModel().clearSelection();
    }

    // Moves the selected client from the selected list to the not selected list
    public void moveClientLeft(MouseEvent mouseEvent) {
        // Get the selected user
        String selected = (String) selectedClients.getSelectionModel().getSelectedItem();
        // Find the user and remove it from the selected list and add it to the not selected list
        for (int i = 0; i < selectedClientsList.size(); i++) {
            if (selectedClientsList.get(i).equals(selected)) {
                selectedClientsList.remove(i);
                notSelectedClientsList.add(selected);
                break;
            }
        }
        selectedClients.getSelectionModel().clearSelection();
    }

    // Checks to see if the required fields are filled in
    private boolean allFilledIn() {
        return !startTF.getText().isEmpty() && !stopTF.getText().isEmpty() && !titleTF.getText().isEmpty();
    }
}
