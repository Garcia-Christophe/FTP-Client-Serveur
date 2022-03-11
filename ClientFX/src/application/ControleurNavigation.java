package application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;


public class ControleurNavigation implements Initializable {
  @FXML
  private TextField inputCommande;
  @FXML
  private ScrollPane scrollPaneTerminal;
  @FXML
  private ListView<Label> listClient;
  @FXML
  private ListView<Label> listServeur;
  @FXML
  private ListView<Label> listTerminal;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // Affichage fichiers/dossiers c�t� client
    File file = new File(Main.utilisateur.getName());
    File[] files = file.listFiles();
    for (File f : files) {
      listClient.getItems().add(new Label(f.isFile() ? f.getName() : f.getName() + "/"));
    }

    // Affichage fichiers/dossiers c�t� serveur
    // LS
  }

  @FXML
  public void entrerCommande() {
    Label label = new Label("> " + inputCommande.getText());
    listTerminal.getItems().add(label);
    scrollPaneTerminal.setVvalue(1);
    inputCommande.setText("");
  }
}
