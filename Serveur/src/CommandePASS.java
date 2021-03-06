import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class CommandePASS extends Commande {

  private Main main;

  public CommandePASS(PrintStream ps, String commandeStr, Main m) {
    super(ps, commandeStr);
    this.main = m;
  }

  public void execute() {
    // Récupération du mot de passe
    String mdp = "";
    try {
      FileReader fr = new FileReader(path + "/" + this.main.getClientName() + "/pw.txt");
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
    if (commandeArgs != null && commandeArgs.length > 0) {
      String paramMdp = "";
      for (int i = 0; i < commandeArgs.length; i++) {
        paramMdp += commandeArgs[i];
      }

      if (paramMdp.equals(mdp)) {
        main.pwOk = true;
        ps.println("1 Commande pass OK");
        ps.println("0 Vous etes bien connecte sur notre serveur");
      } else {
        ps.println("2 Le mot de passe est faux");
      }
    } else {
      ps.println("2 Le mot de passe est vide");
    }
  }

}
