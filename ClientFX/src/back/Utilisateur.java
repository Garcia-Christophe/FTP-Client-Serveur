package back;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilisateur {

  private String host;

  private ArrayList<String> commandes;

  private Socket socket;

  private PrintWriter pw;

  private BufferedReader br;

  private String pathClient;

  private String name;

  public Utilisateur() {
    // Liste des commandes possibles
    this.commandes = new ArrayList<String>();
    this.commandes.add("user");
    this.commandes.add("pass");
    this.commandes.add("pwd");
    this.commandes.add("cd");
    this.commandes.add("get");
    this.commandes.add("ls");
    this.commandes.add("stor");
    this.commandes.add("bye");
  }

  /**
   * Tente de se connecter à l'application serveur.
   * 
   * @param host nom de l'host
   * @param user nom de l'utilisateur
   * @param pass mot de passe de l'utilisateur
   * @return null si la connexion a réussi, le message d'erreur du serveur sinon
   */
  public String connexion(String host, String user, String pass) {
    String connexion = null;
    this.host = host;
    this.name = user;

    try {
      // Connexion avec le serveur
      this.socket = new Socket(this.host, 2121);

      // Création des flux
      this.pw = new PrintWriter(socket.getOutputStream());
      this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      // Récupération des messages de bienvenue
      String recu = "1 ";
      while (recu.split("\\s+")[0].equals("1")) {
        recu = br.readLine();
      }

      // Commande user
      recu = "1 ";
      String[] tabRecu = null;
      pw.println("user " + user);
      pw.flush();
      while (recu.split("\\s+")[0].equals("1")) {
        recu = br.readLine();
      }
      tabRecu = recu.split("\\s+");

      // Si la commande user est bonne
      if (tabRecu != null && tabRecu[0].equals("0")) {
        this.commandes.remove("pass");

        // Commande pass
        recu = "1 ";
        tabRecu = null;
        pw.println("pass " + pass);
        pw.flush();
        while (recu.split("\\s+")[0].equals("1")) {
          recu = br.readLine();
        }
        tabRecu = recu.split("\\s+");

        // Si la commande pass est bonne
        if (tabRecu != null && tabRecu[0].equals("0")) {
          // connexion OK
          this.commandes.remove("pass");
        } else {
          // Erreur pass
          connexion = "Erreur pass : ";
          for (int i = 1; i < tabRecu.length; i++) {
            connexion += tabRecu[i] + " ";
          }
        }
      } else {
        // Erreur user
        connexion = "Erreur user : ";
        for (int i = 1; i < tabRecu.length; i++) {
          connexion += tabRecu[i] + " ";
        }
      }
    } catch (IOException e) {
      // Erreur host
      connexion = "Erreur host : impossible de se connecter à l'application serveur";
    }

    // Fermeture des flux en cas d'erreur
    if (connexion != null) {
      try {
        if (this.br != null) {
          this.br.close();
        }
        if (this.pw != null) {
          this.pw.close();
        }
        if (this.socket != null) {
          this.socket.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return connexion;
  }

  public void communication() {
    // Pattern pour le nom des fichiers et dossiers
    Pattern pattern = Pattern.compile("(.*[:*?\"<>|].*)");
    Matcher matcher = null;
    Scanner scan = null;

    String envoi = "";
    String name = "";
    scan = new Scanner(System.in);
    boolean commandeValide = true;
    String destination = pathClient;

    // Boucle d'envoi des commandes
    while (!envoi.equals("bye")) {
      // Réponse du serveur
      String recu = "";
      boolean arreter = false;
      while (!arreter && commandeValide) {
        recu = br.readLine();
        System.out.println(recu);

        String[] val = recu.split("\\s+");
        if (!val[0].equals("1")) {
          arreter = true;
        }

        // Si le client doit télécharger un fichier (GET)
        String[] e = envoi.split("\\s+");
        if (e[0].equals("get") && !arreter) {
          Socket socketGet = new Socket(host, 5000);
          InputStream inputGet = socketGet.getInputStream();
          ByteArrayOutputStream byteArrayGet = new ByteArrayOutputStream();

          if (inputGet != null) {
            String[] s = e[1].split("/");
            FileOutputStream fos = new FileOutputStream(pathClient + s[s.length - 1]);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            byte[] aByte = new byte[1];

            while (inputGet.read(aByte, 0, aByte.length) != -1) {
              byteArrayGet.write(aByte);
            }
            bos.write(byteArrayGet.toByteArray());
            bos.flush();
            bos.close();
            socketGet.close();
          }
          // Si le client doit déposer un fichier (STOR)
        } else if (e[0].equals("stor") && !arreter) {
          try {
            Socket socketStor = new Socket(host, 4000);
            File file = new File(destination);
            BufferedOutputStream bos = new BufferedOutputStream(socketStor.getOutputStream());
            if (bos != null) {
              byte[] tabByte = new byte[(int) file.length()];
              FileInputStream fis = new FileInputStream(file);
              BufferedInputStream bis = new BufferedInputStream(fis);
              bis.read(tabByte, 0, tabByte.length);
              bos.write(tabByte, 0, tabByte.length);
              bos.flush();
              bis.close();
              fis.close();
              bos.close();
              socketStor.close();
            }

          } catch (IOException er) {
            er.printStackTrace();
          }
          // Si le client souhaite se connecter (USER)
        } else if (e[0].equals("user") && val[0].equals("0")) {
          // Initialisation du chemin client
          File file = new File(".");
          pathClient = file.getAbsoluteFile().toString();
          pathClient = pathClient.substring(0, pathClient.length() - 1);
          pathClient += "/" + name + "/";
          commandes.remove("user");
        } else if (e[0].equals("pass") && val[0].equals("0")) {
          commandes.remove("pass");
        }
      }

      // Récupération de la commande
      System.out.println("\nEntrez votre commande :");
      envoi = scan.nextLine();

      String[] tab = envoi.split("\\s+");
      boolean existe = false;
      int i = 0;
      while (!existe && i < commandes.size()) {
        if (commandes.get(i).equals(tab[0])) {
          existe = true;
        }
        i++;
      }

      if (tab.length > 1) {
        matcher = pattern.matcher(tab[1]);
        if (matcher.find()) {
          existe = false;
        }
      }

      // vérification du fichier en cas de commande stor
      if (tab[0].equals("stor") && tab.length > 1) {
        String base = tab[1];
        if (base.charAt(0) != '/') {
          base = "/" + base;
        }

        // Conversion des chemins relatifs
        destination = pathClient;
        if (base.equals("/")) {
          destination = pathClient;
        } else {
          String[] dossiers = base.split("/");
          for (String s : dossiers) {
            if (s.length() != 0) {
              if (s.equals("..")) {
                if (!destination.equals(pathClient)) {
                  String[] tabPathTmp = destination.split("/");
                  destination = "";
                  for (int j = 0; j < tabPathTmp.length - 1; j++) {
                    destination += tabPathTmp[j] + "/";
                  }
                }
              } else if (s.equals("~")) {
                destination = pathClient;
              } else if (!s.equals(".")) {
                destination = destination + s + "/";
              }
            }
          }
        }

        // Création de l'objet File
        File file = new File(destination);

        // Si le fichier existe
        if (!Files.exists(Paths.get(destination)) || !file.isFile()) {
          existe = false;
        }
      } else if (tab[0].equals("user") && tab.length > 1) {
        name = tab[1];
      }

      if (existe) {
        pw.println(envoi);
        pw.flush();
        commandeValide = true;
      } else {
        commandeValide = false;
        System.out.println("Commande " + tab[0] + " inexistante ou paramètre invalide");
      }
    }

    // Déconnexion du client
    this.deconnexion();
  }

  /**
   * Fermeture des flux et de la socket
   */
  public void deconnexion() {
    try {
      br.close();
      pw.close();
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getPathClient() {
    return pathClient;
  }

  public void setPathClient(String pathClient) {
    this.pathClient = pathClient;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
