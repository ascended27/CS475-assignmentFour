package rmiproject.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class NewEventOpenController {
    @FXML
    public ToggleGroup openGroup;
    @FXML
    public RadioButton yesOpen;
    @FXML
    public RadioButton noOpen;
    @FXML
    public Button cancelButton;
    @FXML
    public Button nextButton;

    public void next(MouseEvent mouseEvent) {
        try {
            Parent root;
            Stage window = new Stage();
            if (yesOpen.isSelected())
                root = FXMLLoader.load(getClass().getResource("FXML/OpenEvent.fxml"));
            else
                root = FXMLLoader.load(getClass().getResource("FXML/NewEvent.fxml"));
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("New Event Open");
            window.setResizable(false);

            Scene scene = new Scene(root);
            window.setScene(scene);
            Stage stage = (Stage) nextButton.getScene().getWindow();
            stage.close();
            window.show();
        } catch (IOException e) {
            AlertBox.display("Error", "Failed to open new event");
        }
    }

    public void cancel(MouseEvent mouseEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
