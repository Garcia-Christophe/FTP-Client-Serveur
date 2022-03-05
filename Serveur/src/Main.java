/*
 * TP JAVA RIP Min Serveur FTP
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main extends Thread {

  private ServerSocket serveurFTP;

  private Socket socket;

  private String name;

  private String userPath;

  public static void main(String[] args) {
    System.out.println("Le Serveur FTP");
    ServerSocket serveurFTP = null;

    // Création du serveur
    try {
      serveurFTP = new ServerSocket(2121);
    } catch (IOException e) {
      e.printStackTrace();
    }

    boolean fermerServeur = false;
    Socket socket;
    while (!fermerServeur) {
      // Attente d'un nouveau client
      try {
        socket = serveurFTP.accept();

        Main m = new Main(serveurFTP, socket);
        m.start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // Fermeture du serveur
    try {
      serveurFTP.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Main(ServerSocket ss, Socket s) {
    this.serveurFTP = ss;
    this.socket = s;
  }

  public String getClientName() {
    return this.name;
  }

  public void setClientName(String theName) {
    this.name = theName;
  }

  public String getUserPath() {
    return this.userPath;
  }

  public void setUserPath(String theUserPath) {
    this.userPath = theUserPath;
  }

  @Override
  public void run() {
    try {
      System.out.println("Un nouveau client s'est connecte !");

      BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintStream ps = new PrintStream(socket.getOutputStream());

      ps.println("1 Bienvenue ! ");
      ps.println("1 Serveur FTP Personnel.");
      ps.println("0 Authentification : ");

      // Attente de reception de commandes et leur execution
      String commande = "";
      while (!(commande = br.readLine()).equals("bye")) {
        System.out.println(">> " + commande);
        CommandExecutor.executeCommande(ps, commande, this);
      }

      // Affichage du départ du client
      System.out.println("Le client " + (name != null ? name + " " : "") + "s'est deconnecte !");

      // Fermeture de la socket et réinitialisation des paramètres de connexion
      socket.close();
      CommandExecutor.userOk = false;
      CommandExecutor.pwOk = false;
    } catch (IOException e) {
      // Départ subit du client
      System.out.println("Le client " + (name != null ? name + " " : "") + "s'est deconnecte !");

      // Fermeture de la socket et réinitialisation des paramètres de connexion
      try {
        socket.close();
        CommandExecutor.userOk = false;
        CommandExecutor.pwOk = false;
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
  }
}
