package rmiproject.ui;

import rmiproject.Client;
import rmiproject.Event;
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

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class ClientCalendarManager {
    @FXML
    public Button retrieveEvent;
    @FXML
    public TableView table;

    private ObservableList<EventRow> data = FXCollections.observableArrayList();
    private Util utils;

    @FXML
    public void initialize() {
        utils = Util.getInstance();

        Client selected = utils.getClient(utils.getSelectedClient());
        if (selected != null) {
            ArrayList<Event> events = utils.getEventList(selected);
            try {
                for (Event event : events) {
                    // Type is true so we can see everything
                    if (event.isType()) {
                        data.add(new EventRow(event.getOwner().getName(), event.getTitle(), event.getStart(), event.getStop()));
                    } else {
                        data.add(new EventRow(event.getOwner().getName(), "Private Event",event.getStart(),event.getStop()));
                    }
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
            } catch (RemoteException e) {
                AlertBox.display("Error", "Failed to load event list");
                e.printStackTrace();
            }
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
}
