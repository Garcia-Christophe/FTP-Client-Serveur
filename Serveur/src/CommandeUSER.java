import java.io.File;
import java.io.PrintStream;

public class CommandeUSER extends Commande {

  private Main main;

  public CommandeUSER(PrintStream ps, String commandeStr, Main m) {
    super(ps, commandeStr);
    this.main = m;
  }

  public void execute() {
    // Ce serveur accepte uniquement le user personne
    File file = new File(path + "/" + commandeArgs[0]);

    // Si l'utilisateur ne correspond pas à un dossier du projet Java
    if (!commandeArgs[0].equals("src") && !commandeArgs[0].equals("bin")
        && commandeArgs[0].charAt(0) != '.') {
      if (file.exists() && file.isDirectory()) {
        main.userOk = true;
        this.main.setClientName(commandeArgs[0]);
        this.main.setUserPath(this.main.getClientName() + "/");
        ps.println("0 Commande user OK");
      } else {
        ps.println("2 Le user " + commandeArgs[0] + " n'existe pas");
      }
    } else {
      ps.println("2 Le user " + commandeArgs[0] + " n'existe pas");
    }
  }

}
