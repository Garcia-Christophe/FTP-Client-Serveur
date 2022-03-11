package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
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
    // Affichage fichiers/dossiers côté client
    File file = new File(Main.utilisateur.getName());
    File[] files = file.listFiles();
    for (File f : files) {
      listClient.getItems().add(new Label(f.isFile() ? f.getName() : f.getName() + "/"));
    }

    // Affichage fichiers/dossiers côté serveur
    ArrayList<String> filesServeur = Main.utilisateur.commandeLS(null);
    for (String s : filesServeur) {
      listServeur.getItems().add(new Label(s));
    }
  }

  @FXML
  public void entrerCommande() {
    String path = Main.utilisateur.getPathClient();
    path = path.substring(0, path.length() - 1);
    listTerminal.getItems().add(new Label(path + "# " + inputCommande.getText()));
    scrollPaneTerminal.setVvalue(1);
    String[] tabCommande = inputCommande.getText().split("\\s+");

    // Commande CD
    if (tabCommande[0].equals("cd")) {
      // Récupération du dossier dans lequel il faut se déplacer
      String dossier = "";
      for (int i = 1; i < tabCommande.length; i++) {
        dossier += tabCommande[i] + " ";
      }

      String oldClientPath = Main.utilisateur.getPathClient();
      String cd = Main.utilisateur.commandeCD(dossier);
      listTerminal.getItems().add(new Label("   " + cd));

      // Afficher le nouveau contenu du dossier courant
      if (!Main.utilisateur.getPathClient().equals(oldClientPath)) {
        ArrayList<String> filesServeur = Main.utilisateur.commandeLS(".");
        listServeur.getItems().clear();
        for (String s : filesServeur) {
          listServeur.getItems().add(new Label(s));
        }
      }
    }

    // Commande GET
    else if (tabCommande[0].equals("get")) {
      System.out.println("get");
    }

    // Commande LS
    else if (tabCommande[0].equals("ls")) {
      // Récupération du potentiel dossier dont il faut afficher le contenu
      String dossier = "";
      for (int i = 1; i < tabCommande.length; i++) {
        dossier += tabCommande[i] + " ";
      }

      // Execution de la commande
      ArrayList<String> files = Main.utilisateur.commandeLS(dossier);

      // Afichage dans le terminal
      if (files.get(0).equals("*")) {
        listTerminal.getItems().add(new Label("   " + files.get(1)));
      } else {
        for (String s : files) {
          listTerminal.getItems().add(new Label("   " + s));
        }
      }
    }

    // Commande PWD
    else if (tabCommande[0].equals("pwd")) {
      String pwd = Main.utilisateur.commandePWD();
      listTerminal.getItems().add(new Label("   " + pwd));
    }

    // Commande STOR
    else if (tabCommande[0].equals("stor")) {
      System.out.println("stor");
    }

    // Commandes inexistante
    else {
      System.out.println("autre");
    }

    inputCommande.setText("");
  }
}
