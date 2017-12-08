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
        // Get an instance of the utils
        utils = Util.getInstance();
        if (utils != null) {
            // Register the table list so things can be added to it from other areas
            utils.registerTableList(data);
        }

        // Retrieve the event list
        ArrayList<Event> events = utils.getEventList(utils.getOwner());
        // Add the event list to the table
        if (events != null) {
            for (Event event : events) {
                data.add(new EventRow(event.getOwnerName(), event.getTitle(), event.getStart(), event.getStop()));
            }
        }

        // Setup the table columns
        TableColumn<EventRow, String> ownerColumn = (TableColumn<EventRow, String>) table.getColumns().get(0);
        TableColumn<EventRow, String> titleColumn = (TableColumn<EventRow, String>) table.getColumns().get(1);
        TableColumn<EventRow, Timestamp> startColumn = (TableColumn<EventRow, Timestamp>) table.getColumns().get(2);
        TableColumn<EventRow, Timestamp> stopsColumn = (TableColumn<EventRow, Timestamp>) table.getColumns().get(3);
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        stopsColumn.setCellValueFactory(new PropertyValueFactory<>("stop"));

        // Put the event list into the table
        table.setItems(data);
    }

    // Opens the dialog for scheduling a new event
    public void openScheduleDialog(MouseEvent mouseEvent) {
        try {
            // Load the dialog window
            Stage window = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("FXML/NewEventOpen.fxml"));
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("New Event Open");
            window.setResizable(false);

            // Show the window
            Scene scene = new Scene(root);
            window.setScene(scene);
            window.show();
        } catch (IOException e) {
            AlertBox.display("Error", "Failed to open new event", false);
        }
    }

    // Opens the event that is selected
    public void openEvent(MouseEvent mouseEvent) {
        try {
            // If there is an event actually selected
            if (table.getSelectionModel().getSelectedCells().size() != 0) {
                // Get the event
                TablePosition pos = (TablePosition) table.getSelectionModel().getSelectedCells().get(0);
                int row = pos.getRow();
                EventRow eventRow = (EventRow) table.getItems().get(row);
                // Store the event in utils
                utils.setRetrievedEventRow(eventRow);

                // Load the window
                Stage window = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("FXML/RetrievedEvent.fxml"));
                window.initModality(Modality.APPLICATION_MODAL);
                window.setTitle("New Event Open");
                window.setResizable(false);

                // Show the window
                Scene scene = new Scene(root);
                window.setScene(scene);
                window.show();
            }
        } catch (IOException e) {
            AlertBox.display("Error", "Failed to load event", false);
            e.printStackTrace();
        }
    }

    // Opens the window to select another user's calendar
    public void openCalendar(MouseEvent mouseEvent) {
        try {
            // Load the window
            Stage window = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("FXML/SelectClient.fxml"));
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Select Client");
            window.setResizable(false);

            // Show the window
            Scene scene = new Scene(root);
            window.setScene(scene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Reloads the events in the table
    public void refreshTable(MouseEvent mouseEvent) {
        // Clear out the current events
        data.clear();

        // Get the current event list
        ArrayList<Event> events = utils.getEventList(utils.getOwner());

        // If there are events load them into the table
        if (events != null) {
            for (Event event : events) {
                data.add(new EventRow(event.getOwnerName(), event.getTitle(), event.getStart(), event.getStop()));
            }
        }
    }
}
