package application;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
      int erreurConnexion = 1;
      ArrayList<String> reponses = Main.utilisateur.connexion(inputHost.getText());

      // Si host OK
      if (reponses.get(reponses.size() - 1).charAt(0) == '0') {
        erreurConnexion = 2;
        reponses = Main.utilisateur.commande("user", inputUser.getText());
        // Si user OK
        if (reponses.get(reponses.size() - 1).charAt(0) == '0') {
          erreurConnexion = 3;
          reponses = Main.utilisateur.commande("pass", inputPass.getText());

          // Si pass OK
          if (reponses.get(reponses.size() - 1).charAt(0) == '0') {
            erreurConnexion = 0;
          }
        }
      }

      if (erreurConnexion == 0) {
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
        labelErreur.setText(reponses.get(reponses.size() - 1).substring(2));
        if (erreurConnexion == 1) {
          inputHost.getStyleClass().add("errorTextField");
        } else {
          inputHost.getStyleClass().remove("errorTextField");
        }

        if (erreurConnexion == 2) {
          inputUser.getStyleClass().add("errorTextField");
        } else {
          inputUser.getStyleClass().remove("errorTextField");
        }

        if (erreurConnexion == 3) {
          inputPass.getStyleClass().add("errorTextField");
        } else {
          inputPass.getStyleClass().remove("errorTextField");
        }

      }
    }
  }

}
