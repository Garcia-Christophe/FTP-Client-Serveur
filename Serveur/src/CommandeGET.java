import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CommandeGET extends Commande {

  private Main main;

  public CommandeGET(PrintStream ps, String commandeStr, Main m) {
    super(ps, commandeStr);
    this.main = m;
  }

  public void execute() {
    if (commandeArgs != null && commandeArgs.length > 0) {
      String base = "";
      for (int i = 0; i < commandeArgs.length; i++) {
        base += commandeArgs[i];
      }
      if (base.charAt(0) != '/') {
        base = "/" + base;
      }

      // Conversion des chemins relatifs
      String destination = this.main.getUserPath();
      if (base.equals("/")) {
        destination = this.main.getClientName() + "/";
      } else {
        String[] dossiers = base.split("/");
        for (String s : dossiers) {
          if (s.length() != 0) {
            if (s.equals("..")) {
              if (!destination.equals(this.main.getClientName() + "/")) {
                String[] tabPathTmp = destination.split("/");
                destination = "";
                for (int i = 0; i < tabPathTmp.length - 1; i++) {
                  destination += tabPathTmp[i] + "/";
                }
              }
            } else if (s.equals("~")) {
              destination = this.main.getClientName() + "/";
            } else if (!s.equals(".")) {
              destination = destination + s + "/";
            }
          }
        }
      }

      // Création de l'objet File
      File file = new File(path + destination);

      // Si le fichier existe
      if (!Files.exists(Paths.get(path + destination))) {
        ps.println("2 Le chemin " + destination + " n'existe pas");
      } else if (!file.isFile()) {
        ps.println("2 Le chemin " + destination + " n'est pas celui d'un fichier.");
      } else {
        ps.println("1 Téléchargement du fichier en cours...");

        ServerSocket serveurFTP;
        try {
          serveurFTP = new ServerSocket(5000);
          Socket socket = serveurFTP.accept();

          BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
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
          }

          socket.close();
          serveurFTP.close();
        } catch (IOException e) {
          e.printStackTrace();
        }

        ps.println("0 Fin du téléchargement ! ");
      }
    } else {
      ps.println("2 Aucun fichier spécifié.");
    }
  }

}
