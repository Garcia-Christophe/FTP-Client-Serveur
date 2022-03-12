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

  private Socket socket;

  private PrintWriter pw;

  private BufferedReader br;

  private String pathClient;

  private String name;

  public Utilisateur() {}

  /**
   * Tente de se connecter à l'application serveur.
   * 
   * @param host nom de l'host
   * @return les réponses du serveur
   */
  public ArrayList<String> connexion(String host) {
    ArrayList<String> reponses = new ArrayList<String>();
    this.host = host;

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
        reponses.add(recu);
      }
    } catch (IOException e) {
      // Erreur host
      reponses.add("2 Impossible de se connecter à l'application serveur");
      this.deconnexion();
    }

    return reponses;
  }

  /**
   * Tente d'exécuter la commande sur le serveur.
   * 
   * @param cmd la commande é exécuter
   * @param param le potetiel paramètre de la commande
   * @return les réponses du serveur
   */
  public ArrayList<String> commande(String cmd, String param) {
    ArrayList<String> reponses = new ArrayList<String>();

    try {

      if (cmd.equals("bye")) {
        pw.println(cmd);
        pw.flush();

        this.deconnexion();
        reponses.add("0 Vous avez été déconnecté");
      } else {
        // Envoi de la commande
        String reponse = "1 ";
        pw.println(cmd + (param != null ? " " + param : ""));
        pw.flush();

        while (reponse.split("\\s+")[0].equals("1")) {
          reponse = br.readLine();
          reponses.add(reponse);
        }

        if (reponse.charAt(0) == '0') {
          // Si CD : mise à jour du path du client
          if (cmd.equals("cd")) {
            this.setPathClient(this.commande("pwd", "").get(0).substring(2));
          }
          // Si USER : initialisation du nom/pathClient
          else if (cmd.equals("user")) {
            this.name = param;
            this.pathClient = param + "/";
          }
        }
      }
    } catch (IOException e) {
      reponses.add("2 Impossible d'exécuter la commande");
      e.printStackTrace();
    }

    return reponses;
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
        }
      }

      // Récupération de la commande
      System.out.println("\nEntrez votre commande :");
      envoi = scan.nextLine();

      String[] tab = envoi.split("\\s+");
      boolean existe = false;
      int i = 0;

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
      if (br != null) {
        br.close();
      }
      if (pw != null) {
        pw.close();
      }
      if (socket != null) {
        socket.close();
      }
      this.name = "<Non identifié>";
      this.pathClient = "<utilisez 'user/pass'> ";
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getHost() {
    return host;
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
