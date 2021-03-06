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
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Utilisateur {

  private String host;

  private Socket socket;

  private PrintWriter pw;

  private BufferedReader br;

  private String pathClient;

  private String name;

  public Utilisateur() {}

  /**
   * Tente de se connecter ? l'application serveur.
   * 
   * @param host nom de l'host
   * @return les r?ponses du serveur
   */
  public ArrayList<String> connexion(String host) {
    ArrayList<String> reponses = new ArrayList<String>();
    this.host = host;

    try {
      // Connexion avec le serveur
      this.socket = new Socket(this.host, 2121);

      // Cr?ation des flux
      this.pw = new PrintWriter(socket.getOutputStream());
      this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      // R?cup?ration des messages de bienvenue
      String recu = "1 ";
      while (recu.split("\\s+")[0].equals("1")) {
        recu = br.readLine();
        reponses.add(recu);
      }
    } catch (IOException e) {
      // Erreur host
      reponses.add("2 Impossible de se connecter ? l'application serveur");
      this.deconnexion();
    }

    return reponses;
  }

  /**
   * Tente d'ex?cuter la commande sur le serveur.
   * 
   * @param cmd la commande ? ex?cuter
   * @param param le potetiel param?tre de la commande
   * @return les r?ponses du serveur
   */
  public ArrayList<String> commande(String cmd, String param) {
    ArrayList<String> reponses = new ArrayList<String>();

    try {
      // Envoi de la commande
      String reponse = "1 ";
      if (cmd.equals("bye")) {
        pw.println(cmd);
        pw.flush();

        this.deconnexion();
        reponses.add("0 Vous avez ?t? d?connect?");
      } else if (cmd.equals("get")) {
        pw.println(cmd + " " + param);
        pw.flush();

        while (reponse.split("\\s+")[0].equals("1")) {
          reponse = br.readLine();
          reponses.add(reponse);

          // T?l?chargement du fichier via une autre socket
          if (reponse.split("\\s+")[0].equals("1")) {
            Socket socketGet = new Socket(host, 5000);
            InputStream inputGet = socketGet.getInputStream();
            ByteArrayOutputStream byteArrayGet = new ByteArrayOutputStream();

            if (inputGet != null) {
              String[] s = param.split("/");
              FileOutputStream fos = new FileOutputStream(name + "/" + s[s.length - 1]);
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
          }
        }
      } else if (cmd.equals("stor")) {
        boolean paramOK = true;

        // Conversion des chemins relatifs
        String base = param;
        String destination = "";
        if (base.charAt(0) != '/') {
          base = "/" + base;
        }
        destination = name + "/";
        if (base.equals("/")) {
          destination = name + "/";
        } else {
          String[] dossiers = base.split("/");
          for (String s : dossiers) {
            if (s.length() != 0) {
              if (s.equals("..")) {
                if (!destination.equals(name + "/")) {
                  String[] tabPathTmp = destination.split("/");
                  destination = "";
                  for (int j = 0; j < tabPathTmp.length - 1; j++) {
                    destination += tabPathTmp[j] + "/";
                  }
                }
              } else if (s.equals("~")) {
                destination = name + "/";
              } else if (!s.equals(".")) {
                destination = destination + s + "/";
              }
            }
          }
        }

        // Cr?ation de l'objet File
        destination = destination.substring(0, destination.length() - 1);
        File file = new File(destination);

        // Si le fichier existe
        if (file != null && (!Files.exists(Paths.get(destination)) || !file.isFile())) {
          paramOK = false;
        }

        if (paramOK) {
          pw.println(cmd + " " + destination);
          pw.flush();

          while (reponse.split("\\s+")[0].equals("1")) {
            reponse = br.readLine();
            reponses.add(reponse);

            // Chargement du fichier
            if (reponse.split("\\s+")[0].equals("1")) {
              try {
                Socket socketStor = new Socket(host, 4000);
                File fileStor = new File(destination);
                BufferedOutputStream bos = new BufferedOutputStream(socketStor.getOutputStream());
                if (bos != null) {
                  byte[] tabByte = new byte[(int) fileStor.length()];
                  FileInputStream fis = new FileInputStream(fileStor);
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
        } else {
          reponses.add("2 " + destination + " : param?tre invalide");
        }
      } else {
        pw.println(cmd + (param != null ? " " + param : ""));
        pw.flush();

        while (reponse.split("\\s+")[0].equals("1")) {
          reponse = br.readLine();
          reponses.add(reponse);
        }

        if (reponse.charAt(0) == '0') {
          // Si CD : mise ? jour du path du client
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
      reponses.add("2 Le serveur est d?connect?");
    }

    return reponses;
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
      this.name = "<Non identifi?>";
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
