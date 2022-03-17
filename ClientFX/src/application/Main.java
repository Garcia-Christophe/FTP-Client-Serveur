/*
 * Main du Client (interface graphique)
 */

package application;

import back.Utilisateur;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
      stage.getIcons().add(new Image("file:Images/logo.png"));
      Scene s = new Scene(root);
      s.getStylesheets().add("/application/erreur.css");
      stage.setScene(s);
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
