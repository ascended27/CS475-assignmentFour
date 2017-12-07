package rmiproject.ui;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import rmiproject.Event;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class ClientUiController {

    @FXML
    public Button scheduleButton;
    @FXML
    public Button retrieveButton;
    @FXML
    public Button openCalendarButton;
    @FXML
    public TableView table;
    @FXML
    public Button refreshButton;

    private ObservableList<EventRow> data = FXCollections.observableArrayList();
    private static Util utils;

    @FXML
    public void initialize() {
        utils = Util.getInstance();
        if (utils != null) {
            utils.registerTableList(data);
        }
        System.out.println("Loading Events");
        ArrayList<Event> events = utils.getEventList(utils.getOwner());
        if (events != null) {
            for (Event event : events) {
                data.add(new EventRow(event.getOwnerName(), event.getTitle(), event.getStart(), event.getStop()));
            }
            System.out.println("Loaded Events");
        } else {
            System.out.println("New User");
        }

        TableColumn<EventRow, String> ownerColumn = (TableColumn<EventRow, String>) table.getColumns().get(0);
        TableColumn<EventRow, String> titleColumn = (TableColumn<EventRow, String>) table.getColumns().get(1);
        TableColumn<EventRow, Timestamp> startColumn = (TableColumn<EventRow, Timestamp>) table.getColumns().get(2);
        TableColumn<EventRow, Timestamp> stopsColumn = (TableColumn<EventRow, Timestamp>) table.getColumns().get(3);

        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        stopsColumn.setCellValueFactory(new PropertyValueFactory<>("stop"));

        table.setItems(data);
    }

    public void openScheduleDialog(MouseEvent mouseEvent) {
        try {
            Stage window = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("FXML/NewEventOpen.fxml"));
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("New Event Open");
            window.setResizable(false);

            Scene scene = new Scene(root);
            window.setScene(scene);
            window.show();
        } catch (IOException e) {
            AlertBox.display("Error", "Failed to open new event");
        }
    }

    public void openEvent(MouseEvent mouseEvent) {
        try {
            if (table.getSelectionModel().getSelectedCells().size() != 0) {
                TablePosition pos = (TablePosition) table.getSelectionModel().getSelectedCells().get(0);
                int row = pos.getRow();
                EventRow eventRow = (EventRow) table.getItems().get(row);
                utils.setRetrievedEventRow(eventRow);

                Stage window = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("FXML/RetrievedEvent.fxml"));
                window.initModality(Modality.APPLICATION_MODAL);
                window.setTitle("New Event Open");
                window.setResizable(false);

                Scene scene = new Scene(root);
                window.setScene(scene);
                window.show();
            }
        } catch (IOException e) {
            AlertBox.display("Error", "Failed to load event");
            e.printStackTrace();
        }
    }

    public void openCalendar(MouseEvent mouseEvent) {
        try {
            Stage window = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("FXML/SelectClient.fxml"));

            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Select Client");
            window.setResizable(false);

            Scene scene = new Scene(root);
            window.setScene(scene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshTable(MouseEvent mouseEvent) {
        data.clear();
        System.out.println("Loading Events");
        ArrayList<Event> events = utils.getEventList(utils.getOwner());
        if (events != null) {
            for (Event event : events) {
                data.add(new EventRow(event.getOwnerName(), event.getTitle(), event.getStart(), event.getStop()));
            }
            System.out.println("Loaded Events");
        } else {
            System.out.println("New User");
        }
    }
}
