import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommandeCD extends Commande {

  private Main main;

  public CommandeCD(PrintStream ps, String commandeStr, Main m) {
    super(ps, commandeStr);
    this.main = m;
  }

  public void execute() {
    if (commandeArgs != null && commandeArgs.length > 0) {
      String destination = commandeArgs[0];
      if (destination.charAt(0) != '/') {
        destination = "/" + destination;
      }

      // Conversion des chemins relatifs
      String pathTmp = this.main.getUserPath();
      if (destination.equals("/")) {
        this.main.setUserPath(this.main.getClientName() + "/");
        ps.println("0 Le nouveau chemin est " + this.main.getUserPath());
      } else {
        String[] dossiers = destination.split("/");
        for (String s : dossiers) {
          if (s.length() != 0) {
            if (s.equals("..")) {
              if (!pathTmp.equals(this.main.getClientName() + "/")) {
                String[] tabPathTmp = pathTmp.split("/");
                pathTmp = "";
                for (int i = 0; i < tabPathTmp.length - 1; i++) {
                  pathTmp += tabPathTmp[i] + "/";
                }
              }
            } else if (s.equals("~")) {
              pathTmp = this.main.getClientName() + "/";
            } else if (!s.equals(".")) {
              pathTmp = pathTmp + s + "/";
            }
          }
        }

        // Mise à jour du dossier courant si le path est correct
        File fileDestination = new File(path + pathTmp);
        Path p = Paths.get(path + pathTmp);
        if (!Files.exists(p)) {
          ps.println("2 Le chemin " + destination + " n'existe pas");
        } else if (!fileDestination.isDirectory()) {
          ps.println("2 Le chemin " + destination + " n'est pas celui d'un dossier");
        } else {
          this.main.setUserPath(pathTmp);
          ps.println("0 Le nouveau chemin est " + this.main.getUserPath());
        }
      }
    } else {
      ps.println("2 Aucun chemin spécifié.");
    }
  }

}
