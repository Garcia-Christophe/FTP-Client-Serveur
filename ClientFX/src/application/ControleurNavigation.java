package application;

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
  private ListView<Label> listTerminal;

  @Override
  public void initialize(URL location, ResourceBundle resources) {}

  @FXML
  public void entrerCommande() {
    Label label = new Label("> " + inputCommande.getText());
    listTerminal.getItems().add(label);
    scrollPaneTerminal.setVvalue(1);
    inputCommande.setText("");
  }
}
