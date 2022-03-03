import java.io.File;
import java.io.PrintStream;

public class CommandeUSER extends Commande {

  public CommandeUSER(PrintStream ps, String commandeStr) {
    super(ps, commandeStr);
  }

  public void execute() {
    // Ce serveur accepte uniquement le user personne
    File file = new File(path + "/" + commandeArgs[0]);

    // Si l'utilisateur ne correspond pas à un dossier du projet Java
    if (!commandeArgs[0].equals("src") && !commandeArgs[0].equals("bin")
        && commandeArgs[0].charAt(0) != '.') {
      if (file.exists() && file.isDirectory()) {
        name = commandeArgs[0];
        userPath = name + "/";
        CommandExecutor.userOk = true;
        ps.println("0 Commande user OK");
      } else {
        ps.println("2 Le user " + commandeArgs[0] + " n'existe pas");
      }
    } else {
      ps.println("2 Le user " + commandeArgs[0] + " n'existe pas");
    }
  }

}
