import java.io.PrintStream;

public class CommandePWD extends Commande {

  public CommandePWD(PrintStream ps, String commandeStr) {
    super(ps, commandeStr);
  }

  public void execute() {
    // Affichage du path du dossier courant
    ps.println("0 " + userPath);
  }

}
