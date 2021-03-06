/*
 * Main du Client (interface console)
 */

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
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {
  static public String pathClient;

  public static void main(String[] args) {
    System.out.println("Le client FTP");
    String host = "localhost";

    // Liste des commandes possibles
    ArrayList<String> commandes = new ArrayList<String>();
    commandes.add("user");
    commandes.add("pass");
    commandes.add("pwd");
    commandes.add("cd");
    commandes.add("get");
    commandes.add("ls");
    commandes.add("stor");
    commandes.add("bye");
    commandes.add("touch");
    commandes.add("mkdir");

    Pattern pattern = Pattern.compile("(.*[:*?\"<>|].*)");
    Matcher matcher = null;

    // Connexion au serveur
    Socket socket = null;
    PrintWriter pw = null;
    Scanner scan = null;
    BufferedReader br = null;
    try {
      socket = new Socket(host, 2121);

      String envoi = "";
      String name = "";
      pw = new PrintWriter(socket.getOutputStream());
      scan = new Scanner(System.in);
      br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      boolean commandeValide = true;
      String destination = pathClient;

      // Boucle d'envoi des commandes
      while (!envoi.equals("bye")) {
        // R?ponse du serveur
        String recu = "";
        boolean arreter = false;
        while (!arreter && commandeValide) {
          recu = br.readLine();
          System.out.println(recu);

          String[] val = recu.split("\\s+");
          if (!val[0].equals("1")) {
            arreter = true;
          }

          // Si le client doit t?l?charger un fichier (GET)
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
            // Si la commande demand? est un stor
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

        // R?cup?ration de la commande
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

        // v?rification du fichier en cas de commande stor
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

          // Cr?ation de l'objet File
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
          System.out.println("Commande " + tab[0] + " inexistante ou param?tre invalide");
        }
      }

      System.out.println("Au revoir !");

      // Fermeture des flux et de la socket
      br.close();
      scan.close();
      pw.close();
      socket.close();
    } catch (IOException e1) {
      // Fermeture subite du serveur
      System.out.println("Le serveur a ferme. Vous allez etre deconnecte. Au revoir !");
      e1.printStackTrace();

      // Fermeture des flux et de la socket
      try {
        br.close();
        scan.close();
        pw.close();
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
