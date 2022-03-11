package application;

import java.net.URL;
import java.util.ResourceBundle;
import back.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class ControleurConnexion implements Initializable {
  private Utilisateur utilisateur;

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
    this.utilisateur = new Utilisateur();
  }

  public void connexion() {
    // Vérification de la saisie des champs
    if (inputHost.getText().isEmpty() || inputUser.getText().isEmpty()
        || inputPass.getText().isEmpty()) {
      labelErreur.setText("Erreur : Veuillez remplir tous les champs");
    } else {
      String connexion =
          this.utilisateur.connexion(inputHost.getText(), inputUser.getText(), inputPass.getText());
      if (connexion == null) {
        // Passage à la fenêtre de navigation
        labelErreur.setText("");
        System.out.println("OK"); // temporaire
      } else {
        labelErreur.setText(connexion);
      }
    }
  }

}
