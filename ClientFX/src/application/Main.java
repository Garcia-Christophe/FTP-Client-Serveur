package application;

import back.Utilisateur;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
  public static Stage stage;

  public static Utilisateur utilisateur;

  @Override
  public void start(Stage primaryStage) {
    try {
      Parent root = FXMLLoader.load(getClass().getResource("connexion.fxml"));
      stage = primaryStage;
      stage.setTitle("MyFTP");
      stage.setScene(new Scene(root));
      stage.setMinHeight(600);
      stage.setMinWidth(800);
      stage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
