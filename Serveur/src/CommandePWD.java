import java.io.PrintStream;

public class CommandePWD extends Commande {

  private Main main;

  public CommandePWD(PrintStream ps, String commandeStr, Main m) {
    super(ps, commandeStr);
    this.main = m;
  }

  public void execute() {
    // Affichage du path du dossier courant
    ps.println("0 " + this.main.getUserPath());
  }

}
