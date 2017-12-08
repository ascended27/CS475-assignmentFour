package rmiproject.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {

    public static void display(String title, String message, boolean fatal) {
        // Create a new stage for the alert
        Stage window = new Stage();

        // Set some attributes about the window
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        // Set the label and message
        Label label = new Label();
        label.setFont(Font.font("GT Walsheim", 14));
        Label mytitle = new Label();
        mytitle.setFont(Font.font("GT Walsheim", 18));
        mytitle.setText(title+"\n\n");
        label.setText(message);


        // Notify the user of fatal error
        if(fatal)
        {
            label.setText(message+"\nThis is a FATAL error. The program will exit once you hit Close.");
        }

        // Setup the close button
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> {
            if(fatal)
                System.exit(1);
            else
                window.close();
        });

        // Position all the elements in the alert
        VBox layout = new VBox(20);
        VBox buttonBox = new VBox();
        buttonBox.setPadding(new Insets(10, 20, 10, 0));
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(closeButton);
        layout.setPrefWidth(600);
        VBox content = new VBox();
        content.setStyle("-fx-background-color: #f3f3f3");
        content.setPadding(new Insets(20, 20, 20, 20));
        layout.setStyle("-fx-background-color: white");
        content.getChildren().addAll(mytitle, label);
        layout.getChildren().addAll(content, buttonBox);
        layout.setAlignment(Pos.CENTER);

        // Display the alert
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

}
