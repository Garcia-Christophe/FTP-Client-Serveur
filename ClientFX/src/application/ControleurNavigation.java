package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


public class ControleurNavigation implements Initializable {

  private ArrayList<String> commandes;

  @FXML
  private TextField inputCommande;
  @FXML
  private ScrollPane scrollPaneTerminal;
  @FXML
  private ListView<Label> listClient;
  @FXML
  private ListView<Label> listServeur;
  @FXML
  private ListView<TextFlow> listTerminal;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // Initialisation des commandes possibles
    commandes = new ArrayList<String>();
    commandes.add("bye");
    commandes.add("cd");
    commandes.add("get");
    commandes.add("ls");
    commandes.add("pwd");
    commandes.add("stor");
    commandes.add("clear");
    commandes.add("touch");
    commandes.add("mkdir");

    // Affichage fichiers/dossiers côté client
    File file = new File(Main.utilisateur.getName());
    File[] files = file.listFiles();
    for (File f : files) {
      // Image Source Fichier
      Image imageF = null;
      imageF = new Image("file:Images/fichier.png");
      ImageView imageViewF = new ImageView(imageF);
      imageViewF.setFitHeight(20);
      imageViewF.setFitWidth(20);
      // Image Source Dossier
      Image imageD = null;
      imageD = new Image("file:Images/dossier.png");
      ImageView imageViewD = new ImageView(imageD);
      imageViewD.setFitHeight(20);
      imageViewD.setFitWidth(20);

      Label label = new Label();
      if (f.isFile()) {
        label = new Label(f.isFile() ? f.getName() : f.getName() + "/", imageViewF);
        label.setGraphic(imageViewF);
      } else {
        label = new Label(f.isFile() ? f.getName() : f.getName() + "/", imageViewD);
        label.setGraphic(imageViewD);
      }

      listClient.getItems().add(label);
    }
    listClient.getStylesheets().add("/application/dossiers.css");

    // Affichage fichiers/dossiers côté serveur
    ArrayList<String> filesServeur = Main.utilisateur.commande("ls", "");
    for (String s : filesServeur) {
      // Image Source Fichier
      Image imageF = null;
      imageF = new Image("file:Images/fichier.png");
      ImageView imageViewF = new ImageView(imageF);
      imageViewF.setFitHeight(20);
      imageViewF.setFitWidth(20);
      // Image Source Dossier
      Image imageD = null;
      imageD = new Image("file:Images/dossier.png");
      ImageView imageViewD = new ImageView(imageD);
      imageViewD.setFitHeight(20);
      imageViewD.setFitWidth(20);

      Label label = new Label();
      if (s.charAt(s.length() - 1) == '/') {
        label = new Label(s.substring(2), imageViewD);
        label.setGraphic(imageViewD);
      } else if (s.charAt(s.length() - 1) != '*') {
        label = new Label(s.substring(2), imageViewF);
        label.setGraphic(imageViewF);
      }
      listServeur.getItems().add(label);
    }
    listServeur.getStylesheets().add("/application/dossiers.css");

    // Init terminal
    listTerminal.getStylesheets().add("/application/terminal.css");
  }

  @FXML
  public void entrerCommande() {
    // Affichage de la commande de l'utilisateur
    String path = Main.utilisateur.getPathClient();
    path = path.substring(0, path.length() - 1);
    String name = Main.utilisateur.getName();
    Text commande = new Text(inputCommande.getText());
    commande.setFill(Color.web("#FFFFFF"));
    Text deuxpoints = new Text(" : ");
    deuxpoints.setFill(Color.web("#FFFFFF"));
    Text nameText = new Text(name);
    nameText.setFill(Color.web("#ffd389"));
    Text pathText = new Text(path);
    pathText.setFill(Color.web("#dcf6f8"));
    Text hashtag = new Text("# ");
    hashtag.setFill(Color.web("#FFFFFF"));

    TextFlow commandeFlowPane = new TextFlow();
    commandeFlowPane.getChildren().addAll(nameText, deuxpoints, pathText, hashtag, commande);
    listTerminal.getItems().add(commandeFlowPane);
    scrollPaneTerminal.setVvalue(1);

    // Préparation des vérifications de la commande
    String[] tabCommande = inputCommande.getText().split("\\s+");
    String cmd = tabCommande[0];
    String param = "";
    for (int i = 1; i < tabCommande.length; i++) {
      param += tabCommande[i] + " ";
    }
    param = param.length() > 0 ? param.substring(0, param.length() - 1) : param;
    Pattern pattern = Pattern.compile("(.*[:*?\"<>|].*)");
    Matcher matcher = pattern.matcher(param);

    // Vérification de l'existence de la commande
    if (!this.commandes.contains(cmd)) {
      Label label = new Label("   " + cmd + " : commande inexistante");
      label.setTextFill(Color.web("#FFFFFF"));
      TextFlow txt = new TextFlow();
      txt.getChildren().add(label);
      listTerminal.getItems().add(txt);
    } else if (matcher.find()) {
      Label label = new Label("   " + param + " : paramètre invalide");
      label.setTextFill(Color.web("#FFFFFF"));
      TextFlow txt = new TextFlow();
      txt.getChildren().add(label);
      listTerminal.getItems().add(txt);
    } else {
      // Exécution de la commande
      if (cmd.equals("clear")) {
        listTerminal.getItems().clear();
      } else {
        ArrayList<String> reponses = new ArrayList<String>();
        if (cmd.equals("user")) {
          ArrayList<String> connexion = Main.utilisateur.connexion(Main.utilisateur.getHost());
          if (connexion.get(connexion.size() - 1).charAt(0) == 2) {
            reponses.add("2 Connexion au serveur impossible");
          } else {
            reponses = Main.utilisateur.commande(cmd, param);
          }
        } else {
          reponses = Main.utilisateur.commande(cmd, param);
        }

        // Affichage des réponses
        for (String s : reponses) {
          Label label = new Label("   " + s.substring(2));
          label.setTextFill(Color.web("#FFFFFF"));
          TextFlow txt = new TextFlow();
          txt.getChildren().add(label);
          listTerminal.getItems().add(txt);
        }

        // Potentielle adaptation de l'affichage des fichiers/dossiers
        if (reponses.get(reponses.size() - 1).charAt(0) == '0') {
          if (cmd.equals("cd") || cmd.equals("stor") || cmd.equals("touch")
              || cmd.equals("mkdir")) {
            // Mise à jour de l'affichage de l'espace serveur
            ArrayList<String> filesServeur = Main.utilisateur.commande("ls", "");
            listServeur.getItems().clear();
            for (String s : filesServeur) {
              // Image Source Fichier
              Image imageF = null;
              imageF = new Image("file:Images/fichier.png");
              ImageView imageViewF = new ImageView(imageF);
              imageViewF.setFitHeight(20);
              imageViewF.setFitWidth(20);
              // Image Source Dossier
              Image imageD = null;
              imageD = new Image("file:Images/dossier.png");
              ImageView imageViewD = new ImageView(imageD);
              imageViewD.setFitHeight(20);
              imageViewD.setFitWidth(20);

              Label label = new Label();
              if (s.charAt(s.length() - 1) == '/') {
                label = new Label(s.substring(2), imageViewD);
                label.setGraphic(imageViewD);
              } else if (s.charAt(s.length() - 1) != '*') {
                label = new Label(s.substring(2), imageViewF);
                label.setGraphic(imageViewF);
              }
              listServeur.getItems().add(label);
            }
          } else if (cmd.equals("get")) {
            // Mise à jour de l'affichage de l'espace client
            File file = new File(Main.utilisateur.getName());
            File[] files = file.listFiles();
            listClient.getItems().clear();
            for (File f : files) {
              // Image Source Fichier
              Image imageF = null;
              imageF = new Image("file:Images/fichier.png");
              ImageView imageViewF = new ImageView(imageF);
              imageViewF.setFitHeight(20);
              imageViewF.setFitWidth(20);
              // Image Source Dossier
              Image imageD = null;
              imageD = new Image("file:Images/dossier.png");
              ImageView imageViewD = new ImageView(imageD);
              imageViewD.setFitHeight(20);
              imageViewD.setFitWidth(20);

              Label label = new Label();
              if (f.isFile()) {
                label = new Label(f.isFile() ? f.getName() : f.getName() + "/", imageViewF);
                label.setGraphic(imageViewF);
              } else {
                label = new Label(f.isFile() ? f.getName() : f.getName() + "/", imageViewD);
                label.setGraphic(imageViewD);
              }

              listClient.getItems().add(label);
            }
          } else if (cmd.equals("user")) {
            // Impossibilité de refaire user, possibilité de faire bye
            commandes.remove("user");
            commandes.add("bye");
            commandes.add("pass");
          } else if (cmd.equals("pass")) {
            // Impossibilité de refaire, possibilité des autres commandes
            commandes.remove("pass");
            commandes.add("cd");
            commandes.add("get");
            commandes.add("ls");
            commandes.add("pwd");
            commandes.add("stor");
            commandes.add("clear");
            commandes.add("touch");
            commandes.add("mkdir");

            // Affichage de l'espace client/serveur du nouvel utilisateur
            ArrayList<String> filesServeur = Main.utilisateur.commande("ls", "");
            listServeur.getItems().clear();
            for (String s : filesServeur) {
              // Image Source Fichier
              Image imageF = null;
              imageF = new Image("file:Images/fichier.png");
              ImageView imageViewF = new ImageView(imageF);
              imageViewF.setFitHeight(20);
              imageViewF.setFitWidth(20);
              // Image Source Dossier
              Image imageD = null;
              imageD = new Image("file:Images/dossier.png");
              ImageView imageViewD = new ImageView(imageD);
              imageViewD.setFitHeight(20);
              imageViewD.setFitWidth(20);

              Label label = new Label();
              if (s.charAt(s.length() - 1) == '/') {
                label = new Label(s.substring(2), imageViewD);
                label.setGraphic(imageViewD);
              } else if (s.charAt(s.length() - 1) != '*') {
                label = new Label(s.substring(2), imageViewF);
                label.setGraphic(imageViewF);
              }
              listServeur.getItems().add(label);
            }
            File file = new File(Main.utilisateur.getName());
            File[] files = file.listFiles();
            for (File f : files) {
              // Image Source Fichier
              Image imageF = null;
              imageF = new Image("file:Images/fichier.png");
              ImageView imageViewF = new ImageView(imageF);
              imageViewF.setFitHeight(20);
              imageViewF.setFitWidth(20);
              // Image Source Dossier
              Image imageD = null;
              imageD = new Image("file:Images/dossier.png");
              ImageView imageViewD = new ImageView(imageD);
              imageViewD.setFitHeight(20);
              imageViewD.setFitWidth(20);

              Label labelI = new Label();
              if (f.isFile()) {
                labelI = new Label(f.isFile() ? f.getName() : f.getName() + "/", imageViewF);
                labelI.setGraphic(imageViewF);
              } else {
                labelI = new Label(f.isFile() ? f.getName() : f.getName() + "/", imageViewD);
                labelI.setGraphic(imageViewD);
              }

              listClient.getItems().add(labelI);
            }
          } else if (cmd.equals("bye")) {
            // Possibilité de se connecter
            commandes.clear();
            commandes.add("user");

            // Suppression de l'affichage de l'ancien utilisateur
            listClient.getItems().clear();
            listServeur.getItems().clear();
            listTerminal.getItems().clear();
          }
        }
      }
    }

    // Suppression de la commande écrite par l'utilisateur
    inputCommande.setText("");
  }
}
