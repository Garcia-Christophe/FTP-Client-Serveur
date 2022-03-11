package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import back.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;;


public class ControleurConnexion implements Initializable {
  @FXML
  private TextField inputHost;
  @FXML
  private TextField inputUser;
  @FXML
  private TextField inputPass;
  @FXML
  private Label labelErreur;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Main.utilisateur = new Utilisateur();
  }

  /**
   * Appelée lorsque l'utilisateur clique sur le bouton Valider. Tente de connecter l'utilisateur au
   * serveur.
   */
  public void connexion() {
    boolean vides = false;
    // Vérification de la saisie des champs
    if (inputHost.getText().isEmpty()) {
      labelErreur.setText("Erreur : Veuillez remplir tous les champs");
      inputHost.getStyleClass().add("errorTextField");
      vides = true;
    } else {
      inputHost.getStyleClass().remove("errorTextField");
    }
    if (inputUser.getText().isEmpty()) {
      labelErreur.setText("Erreur : Veuillez remplir tous les champs");
      inputUser.getStyleClass().add("errorTextField");
      vides = true;
    } else {
      inputUser.getStyleClass().remove("errorTextField");
    }
    if (inputPass.getText().isEmpty()) {
      labelErreur.setText("Erreur : Veuillez remplir tous les champs");
      inputPass.getStyleClass().add("errorTextField");
      vides = true;
    } else {
      inputPass.getStyleClass().remove("errorTextField");
    }

    if (!vides) {
      String connexion =
          Main.utilisateur.connexion(inputHost.getText(), inputUser.getText(), inputPass.getText());
      if (connexion == null) {
        labelErreur.setText("");
        try {
          Parent root = FXMLLoader.load(getClass().getResource("navigation.fxml"));
          Main.stage.setScene(new Scene(root));
          Main.stage.setMinHeight(600);
          Main.stage.setMinWidth(800);
          Main.stage.show();
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        labelErreur.setText(connexion);
        if (connexion.split("\\s+")[1].equals("host")) {
          inputHost.getStyleClass().add("errorTextField");
        } else {
          inputHost.getStyleClass().remove("errorTextField");
        }

        if (connexion.split("\\s+")[1].equals("user")) {
          inputUser.getStyleClass().add("errorTextField");
        } else {
          inputUser.getStyleClass().remove("errorTextField");
        }

        if (connexion.split("\\s+")[1].equals("pass")) {
          inputPass.getStyleClass().add("errorTextField");
        } else {
          inputPass.getStyleClass().remove("errorTextField");
        }

      }
    }
  }

}
