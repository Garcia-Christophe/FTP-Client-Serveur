import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CommandeLS extends Commande {

  private Main main;

  public CommandeLS(PrintStream ps, String commandeStr, Main m) {
    super(ps, commandeStr);
    this.main = m;
  }

  public void execute() {
    String userPathLS = this.main.getUserPath();
    if (commandeArgs != null && commandeArgs.length != 0) {
      String base = commandeArgs[0];
      if (base.charAt(0) != '/') {
        base = "/" + base;
      }

      if (base.equals("/")) {
        userPathLS = this.main.getClientName() + "/";
      } else {
        String[] dossiers = base.split("/");
        for (String s : dossiers) {
          if (s.length() != 0) {
            if (s.equals("..")) {
              if (!userPathLS.equals(this.main.getClientName() + "/")) {
                String[] tabPathTmp = userPathLS.split("/");
                userPathLS = "";
                for (int i = 0; i < tabPathTmp.length - 1; i++) {
                  userPathLS += tabPathTmp[i] + "/";
                }
              }
            } else if (s.equals("~")) {
              userPathLS = this.main.getClientName() + "/";
            } else if (!s.equals(".")) {
              userPathLS = userPathLS + s + "/";
            }
          }
        }
      }
    }

    File file = new File(path + userPathLS);
    if (!Files.exists(Paths.get(path + userPathLS))) {
      ps.println("2 Le chemin " + userPathLS + " n'existe pas");
    } else {
      File[] files = file.listFiles();

      // Affichage des fichiers et dossiers
      if (files.length == 0) {
        ps.println("0 Dossier vide");
      } else {
        for (int i = 0; i < files.length; i++) {
          if (i != files.length - 1) {
            ps.println("1 " + (files[i].isFile() ? files[i].getName() : files[i].getName() + "/"));
          } else {
            ps.println("0 " + (files[i].isFile() ? files[i].getName() : files[i].getName() + "/"));
          }
        }
      }
    }
  }

}
