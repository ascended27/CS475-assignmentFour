package rmiproject.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;

public class SelectClientController {
    @FXML
    public Button cancelButton;
    @FXML
    public Button nextButton;
    @FXML
    public ListView clientList;

    private Util utils;
    private ObservableList<String> availableClients = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Get an instance of utils
        utils = Util.getInstance();

        // Set the available clients to the client list
        clientList.setItems(availableClients);

        // Loop over the clients and add them to the available clients
        for (String client : utils.getUsers()) {
            try {
                if (!client.equals(utils.getOwner().getName()))
                    availableClients.add(client);
            } catch (RemoteException e) {
                AlertBox.display("Error", "Failed to load users", false);
                e.printStackTrace();
            }
        }
    }

    // Closes this window
    public void cancel(MouseEvent mouseEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    // Opens the calendar for the selected client
    public void next(MouseEvent mouseEvent) {
        // Get the name of the selected client
        String selected = (String) clientList.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.equals("")) {
            try {
                // Set the selected client
                utils.setSelectedClient(selected);

                // Load the scene
                Parent root = FXMLLoader.load(getClass().getResource("FXML/ClientCalendar.fxml"));

                // Set the window to the new scene
                Scene scene = new Scene(root);
                Stage stage = (Stage) nextButton.getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException e) {
                AlertBox.display("Error", "Failed to load calendar for " + utils.getSelectedClient(), false);
                e.printStackTrace();
            }
        } else {
            AlertBox.display("Error", "Must select a user", false);
        }
    }
}
