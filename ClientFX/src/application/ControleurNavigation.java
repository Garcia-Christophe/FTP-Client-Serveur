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
import javafx.scene.paint.Color;


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
    // Affichage fichiers/dossiers côté client
    File file = new File(Main.utilisateur.getName());
    File[] files = file.listFiles();
    for (File f : files) {
      listClient.getItems().add(new Label(f.isFile() ? f.getName() : f.getName() + "/"));
      listClient.getStylesheets().add("/application/dossiers.css");
    }

    // Affichage fichiers/dossiers côté serveur
    // LS
  }

  @FXML
  public void entrerCommande() {
    Label label = new Label("> " + inputCommande.getText());
    label.setTextFill(Color.web("#FFFFFF"));
    listTerminal.getItems().add(label);
    listTerminal.getStylesheets().add("/application/terminal.css");
    scrollPaneTerminal.setVvalue(1);
    inputCommande.setText("");
  }
}
