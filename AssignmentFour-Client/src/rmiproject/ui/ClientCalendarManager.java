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
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientCalendarManager {
    @FXML
    public Button retrieveEvent;
    @FXML
    public TableView table;

    private ObservableList<EventRow> data = FXCollections.observableArrayList();
    private Util utils;

    @FXML
    public void initialize() {
        // Get an instance of the utils
        utils = Util.getInstance();

        // Get the selected client
        String selected = utils.getSelectedClient();

        // If there was a user selected then continue
        if (selected != null) {
            ConcurrentLinkedQueue<Event> events = null;
            try {
                // Get the events
                events = utils.getCalendar(selected).getEventList();

                // Loop over the events adding them to the table
                for (Event event : events) {
                    // Type is true so we can see everything
                    if (event.isType()) {
                        data.add(new EventRow(event.getOwnerName(), event.getTitle(), event.getStart(), event.getStop()));
                    } else {
                        data.add(new EventRow(event.getOwnerName(), "Private Event", event.getStart(), event.getStop()));
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

                // Put the events in the table
                table.setItems(data);
            } catch (RemoteException e) {
                e.printStackTrace();
                AlertBox.display("Error", "Failed to load events", false);
            }
        }
    }

    // Opens the event that was clicked on in the ui
    public void openEvent(MouseEvent mouseEvent) {
        try {
            // If a event was actually selected continue
            if (table.getSelectionModel().getSelectedCells().size() != 0) {
                // Get the event and put it in utils
                TablePosition pos = (TablePosition) table.getSelectionModel().getSelectedCells().get(0);
                int row = pos.getRow();
                EventRow eventRow = (EventRow) table.getItems().get(row);
                utils.setRetrievedEventRow(eventRow);

                // Load the retrieved event window
                Stage window = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("FXML/RetrievedEvent.fxml"));
                window.initModality(Modality.APPLICATION_MODAL);
                window.setTitle("New Event Open");
                window.setResizable(false);

                // Display the retrieved event
                Scene scene = new Scene(root);
                window.setScene(scene);
                window.show();
            }
        } catch (IOException e) {
            AlertBox.display("Error", "Failed to load event", false);
            e.printStackTrace();
        }
    }
}
