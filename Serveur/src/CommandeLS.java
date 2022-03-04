import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CommandeLS extends Commande {

  public CommandeLS(PrintStream ps, String commandeStr) {
    super(ps, commandeStr);
  }

  public void execute() {
    String userPathLS = userPath;
    if (commandeArgs.length != 0) {
      String base = commandeArgs[0];
      if (base.charAt(0) != '/') {
        base = "/" + base;
      }

      if (base.equals("/")) {
        userPathLS = name + "/";
      } else {
        String[] dossiers = base.split("/");
        for (String s : dossiers) {
          if (s.length() != 0) {
            if (s.equals("..")) {
              if (!userPathLS.equals(name + "/")) {
                String[] tabPathTmp = userPathLS.split("/");
                userPathLS = "";
                for (int i = 0; i < tabPathTmp.length - 1; i++) {
                  userPathLS += tabPathTmp[i] + "/";
                }
              }
            } else if (s.equals("~")) {
              userPathLS = name + "/";
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
            ps.println("1 " + files[i].getName());
          } else {
            ps.println("0 " + files[i].getName());
          }
        }
      }
    }
  }

}
