/*
 * TP JAVA RIP Min Serveur FTP
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

  public static void main(String[] args) {
    System.out.println("Le Serveur FTP");

    ServerSocket serveurFTP = null;
    Socket socket = null;
    try {
      serveurFTP = new ServerSocket(2121);
    } catch (IOException e) {
      e.printStackTrace();
    }

    boolean fermerServeur = false;
    while (!fermerServeur) {
      try {
        // Attente d'un nouveau client
        socket = serveurFTP.accept();
        System.out.println("Un nouveau client s'est connecte !");

        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintStream ps = new PrintStream(socket.getOutputStream());

        ps.println("1 Bienvenue ! ");
        ps.println("1 Serveur FTP Personnel.");
        ps.println("0 Authentification : ");

        String commande = "";

        // Attente de reception de commandes et leur execution
        while (!(commande = br.readLine()).equals("bye")) {
          System.out.println(">> " + commande);
          CommandExecutor.executeCommande(ps, commande);
        }

        // Affichage du départ du client
        System.out.println("Le client " + (Commande.name != null ? Commande.name + " " : "")
            + "s'est deconnecte !");

        // Fermeture de la socket et réinitialisation des paramètres de connexion
        socket.close();
        CommandExecutor.userOk = false;
        CommandExecutor.pwOk = false;
      } catch (IOException e) {
        // Départ subit du client
        System.out.println("Le client " + (Commande.name != null ? Commande.name + " " : "")
            + "s'est deconnecte !");

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

    // Fermeture du serveur
    try {
      serveurFTP.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
