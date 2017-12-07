package rmiproject.ui;

import rmiproject.Client;
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
        utils = Util.getInstance();

        clientList.setItems(availableClients);

        for (String client : utils.getUsers()) {
            try {
                if(!client.equals(utils.getOwner().getName()))
                    availableClients.add(client);
            } catch (RemoteException e) {
                AlertBox.display("Error", "Failed to load users",false);
                e.printStackTrace();
            }
        }
    }

    public void cancel(MouseEvent mouseEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void next(MouseEvent mouseEvent) {
        String selected = (String) clientList.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.equals("")) {
            try {
                utils.setSelectedClient(selected);
                Parent root = FXMLLoader.load(getClass().getResource("FXML/ClientCalendar.fxml"));
                Scene scene = new Scene(root);
                Stage stage = (Stage) nextButton.getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException e) {
                AlertBox.display("Error", "Failed to load calendar for " + utils.getSelectedClient(),false);
                e.printStackTrace();
            }
        } else{
            AlertBox.display("Error","Must select a user",false);
        }
    }
}
