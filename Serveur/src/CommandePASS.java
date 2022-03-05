import java.awt.image.BufferedImageFilter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class CommandePASS extends Commande {

  public CommandePASS(PrintStream ps, String commandeStr) {
    super(ps, commandeStr);
  }

  public void execute() {
    // Récupération du mot de passe
    String mdp = "";
    try {
      FileReader fr = new FileReader(path + "/" + name + "/pw.txt");
      BufferedReader br = new BufferedReader(fr);

      mdp = br.readLine();
      br.close();
      fr.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Vérification du mot de passe
    if (commandeArgs[0].equals(mdp)) {
      CommandExecutor.pwOk = true;
      ps.println("1 Commande pass OK");
      ps.println("0 Vous etes bien connecte sur notre serveur");
    } else {
      ps.println("2 Le mode de passe est faux");
    }
  }

}
