import java.io.PrintStream;

public class CommandExecutor {

  public static void executeCommande(PrintStream ps, String commande, Main main) {
    if (main.userOk && main.pwOk) {
      // Changer de repertoire. Un (..) permet de revenir au repertoire superieur
      if (commande.split(" ")[0].equals("cd"))
        (new CommandeCD(ps, commande, main)).execute();

      // Telecharger un fichier
      if (commande.split(" ")[0].equals("get"))
        (new CommandeGET(ps, commande, main)).execute();

      // Afficher la liste des fichiers et des dossiers du repertoire courant
      if (commande.split(" ")[0].equals("ls"))
        (new CommandeLS(ps, commande, main)).execute();

      // Afficher le repertoire courant
      if (commande.split(" ")[0].equals("pwd"))
        (new CommandePWD(ps, commande, main)).execute();

      // Envoyer (uploader) un fichier
      if (commande.split(" ")[0].equals("stor"))
        (new CommandeSTOR(ps, commande, main)).execute();

      // Créer un fichier
      if (commande.split(" ")[0].equals("touch"))
        (new CommandeTOUCH(ps, commande, main)).execute();

      // Créer un répertoire
      if (commande.split(" ")[0].equals("mkdir"))
        (new CommandeMKDIR(ps, commande, main)).execute();
    } else {
      if (commande.split(" ")[0].equals("pass") || commande.split(" ")[0].equals("user")) {
        // Le mot de passe pour l'authentification
        if (commande.split(" ")[0].equals("pass"))
          (new CommandePASS(ps, commande, main)).execute();

        // Le login pour l'authentification
        if (commande.split(" ")[0].equals("user"))
          (new CommandeUSER(ps, commande, main)).execute();
      } else
        ps.println("2 Vous n'Ãªtes pas connectÃ© !");
    }
  }

}
