import java.io.File;
import java.io.PrintStream;

public class CommandeRMDIR extends Commande {

  private Main main;

  public CommandeRMDIR(PrintStream ps, String commandeStr, Main m) {
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
      if (file.isDirectory()) {
        if (!file.getAbsolutePath().equals(path + this.main.getClientName() + "/")) {
          if (file.delete()) {
            ps.println("0 Le dossier " + destination + " est supprimé !");
          } else {
            ps.println("2 Le dossier " + destination + " n'existe pas ou n'est pas vide");
          }
        } else {
          ps.println("2 Le chemin " + destination + " n'est pas un dossier supprimable");
        }
      } else {
        ps.println("2 Le chemin " + destination + " n'est pas un dossier");
      }

    } else {
      ps.println("2 Aucun dossier spécifié");
    }
  }

}
