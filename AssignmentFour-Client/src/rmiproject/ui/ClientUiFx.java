package rmiproject.ui;

import rmiproject.Calendar;
import rmiproject.ClientImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import rmiproject.Event;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientUiFx extends Application {

    private Util utils;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        System.setSecurityManager(new SecurityManager());
        utils = Util.getInstance();
        primaryStage.setTitle("Scheduler");
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> close(primaryStage));

        // First get username and rmiproject manager
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label userLabel = new Label("Username:");
        grid.add(userLabel, 0, 0);

        TextField usernameTF = new TextField();
        grid.add(usernameTF, 1, 0);

        Button continueBtn = new Button();
        continueBtn.setText("Next");
        grid.add(continueBtn, 1, 1);
        continueBtn.setOnMouseClicked(e -> {
            if (usernameTF.getText().equals("")) {
                AlertBox.display("Error", "Username is required", false);
            } else {
                String username = usernameTF.getText();

                if (utils.checkUser(username)) {
                    try {
                        // Open Scheduler
                        System.out.println("Server: Registering Client Service");
                        ClientImpl client = null;
                        client = new ClientImpl(username);
                        Naming.rebind("rmi://localhost:6246/ClientService-" + utils.getSelectedClient(), client);
                        // Refresh the user's calendar's client object
                        Calendar cal = utils.getCalendar(username);
                        if(cal != null) {
                            cal.setOwner(client);
                            ConcurrentLinkedQueue<Event> events = cal.getEventList();
                            for(Event event : events) {
                                if(event.getOwnerName().equals(username)){
                                    event.setOwner(client);
                                }
                            }
                        }
                    } catch (RemoteException | MalformedURLException e1) {
                        e1.printStackTrace();
                        AlertBox.display("Error", "Failed to start rmiproject service", true);
                        System.exit(1);
                    }

                    System.out.println("Server: Ready...");
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("FXML/ClientUiFXML.fxml"));
                        Scene scene = new Scene(root, 800, 500);
                        primaryStage.setScene(scene);
                    } catch (IOException e1) {
                        AlertBox.display("Error","Failed to load interface", true);
                        e1.printStackTrace();
                    }
                } else {
                    AlertBox.display("Error", "Failed to retrieve user: " + username, false);
                }

            }
        });

        Scene initScene = new Scene(grid, 400, 275);
        primaryStage.setScene(initScene);
        primaryStage.show();

    }

    private void close(Stage window) {
        // Handle any closing requirements here
        utils.killClock();
        window.close();
        System.exit(1);
    }
}
