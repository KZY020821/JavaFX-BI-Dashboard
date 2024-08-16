/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML.java to edit this template
 */
package dashboardbi;

import java.io.IOException;
import javaPackages.Net2Local;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 *
 * @author khorzeyi
 */
public class DashboardBI extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        showSplashScreen(primaryStage);
    }

    /* displaying progress bar to let user know 
    application is extracting data from internet */
    private void showSplashScreen(Stage primaryStage) {
       StackPane root = new StackPane();
        
        Label progressLabel = new Label("");
        progressLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20)); 
        StackPane.setAlignment(progressLabel, Pos.CENTER); 
        StackPane.setMargin(progressLabel, new Insets(-100, 0, 0, 0));


        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(1200);
        StackPane.setAlignment(progressBar, Pos.CENTER);

        root.getChildren().addAll(progressBar, progressLabel);

        Scene scene = new Scene(root, 1600, 1000);

        primaryStage.setTitle("Loading Data...");
        primaryStage.setScene(scene);
        primaryStage.show();

        /* Load the data in a background thread
        to achieve asynchronous */
        Task<Void> task;
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                
                updateMessage("Extracting data from the internet");
                updateProgress(0.1, 1);
                Net2Local transformData = new Net2Local();
                transformData.dataTransformation();
                
                updateMessage("Writing data into local JSON file");
                updateProgress(0.5, 1);

                updateMessage("Reading data from local JSON file");
                updateProgress(0.9, 1);
                
                Thread.sleep(500);
                
                Platform.runLater(() -> {
                    primaryStage.close();
                    showMainDashboard();
                });
                return null;
            }
        };
        
        progressBar.progressProperty().bind(task.progressProperty());

        progressLabel.textProperty().bind(task.messageProperty());

        new Thread(task).start();
    }

    // run .fxml file to show main dashboard
    private void showMainDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Khor Ze Yi | 985597 | BMC2109032 - BI Dashboard");
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading FXML file:");
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
