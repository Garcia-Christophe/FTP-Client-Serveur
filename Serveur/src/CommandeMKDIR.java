import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CommandeMKDIR extends Commande {

  private Main main;

  public CommandeMKDIR(PrintStream ps, String commandeStr, Main m) {
    super(ps, commandeStr);
    this.main = m;
  }

  public void execute() {
    if (commandeArgs != null && commandeArgs.length > 0) {
      String base = commandeArgs[0];
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

      // Création du fichier
      File file = new File(path + destination);

      if (file.mkdir()) {
        ps.println("0 Dossier " + destination + " créé !");
      } else {
        ps.println("2 Dossier " + destination + " déjà existant ou invalide");
      }
    } else {
      ps.println("2 Aucun fichier spécifié");
    }
  }

}
