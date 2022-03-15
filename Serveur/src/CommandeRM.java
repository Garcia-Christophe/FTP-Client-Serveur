import java.io.File;
import java.io.PrintStream;

public class CommandeRM extends Commande {

  private Main main;

  public CommandeRM(PrintStream ps, String commandeStr, Main m) {
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

      // Création du fichier
      destination = destination.substring(0, destination.length() - 1);
      File file = new File(path + destination);
      if (file.isFile()) {
        if (!file.getAbsolutePath().equals(path + this.main.getClientName() + "\\pw.txt")) {
          if (file.delete()) {
            ps.println("0 Le fichier " + destination + " est supprimé !");
          } else {
            ps.println("2 Le fichier " + destination + " n'existe pas");
          }
        } else {
          ps.println("2 Le chemin " + destination + " n'est pas un fichier supprimable");
        }
      } else {
        ps.println("2 Le chemin " + destination + " n'est pas un fichier");
      }

    } else {
      ps.println("2 Aucun fichier spécifié");
    }
  }

}
