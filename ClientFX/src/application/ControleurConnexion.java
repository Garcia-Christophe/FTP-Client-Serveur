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
import javafx.scene.control.TextField;


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
    // Vérification de la saisie des champs
    if (inputHost.getText().isEmpty() || inputUser.getText().isEmpty()
        || inputPass.getText().isEmpty()) {
      labelErreur.setText("Erreur : Veuillez remplir tous les champs");
    } else {
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
      }
    }
  }

}
