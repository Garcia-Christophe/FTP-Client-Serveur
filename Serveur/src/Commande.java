import java.io.File;
import java.io.PrintStream;

public abstract class Commande {

  protected PrintStream ps;
  protected String commandeNom = "";
  protected String[] commandeArgs;
  public static String path;

  public Commande(PrintStream ps, String commandeStr) {
    this.ps = ps;
    String[] args = commandeStr.split(" ");
    commandeNom = args[0];
    commandeArgs = new String[args.length - 1];

    for (int i = 0; i < commandeArgs.length; i++) {
      commandeArgs[i] = args[i + 1] + " ";
    }
    if (commandeArgs.length > 0) {
      commandeArgs[commandeArgs.length - 1] = commandeArgs[commandeArgs.length - 1].substring(0,
          commandeArgs[commandeArgs.length - 1].length() - 1);
    }

    if (path == null) {
      File file = new File(".");
      path = file.getAbsoluteFile().toString();
      path = path.substring(0, path.length() - 1);
    }
  }

  public abstract void execute();

}
