import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class CommandeSTOR extends Commande {

  private Main main;

  public CommandeSTOR(PrintStream ps, String commandeStr, Main m) {
    super(ps, commandeStr);
    this.main = m;
  }

  public void execute() {
    if (commandeArgs != null && commandeArgs.length > 0) {
      ps.println("1 Téléchargement du fichier en cours");
      ServerSocket serveurFTP;
      try {
        serveurFTP = new ServerSocket(4000);
        Socket socket = serveurFTP.accept();
        InputStream inputGet = socket.getInputStream();
        ByteArrayOutputStream byteArrayGet = new ByteArrayOutputStream();

        if (inputGet != null) {
          String base = "";
          for (int i = 0; i < commandeArgs.length; i++) {
            base += commandeArgs[i];
          }
          String[] s = base.split("/");
          FileOutputStream fos =
              new FileOutputStream(path + this.main.getUserPath() + s[s.length - 1]);
          BufferedOutputStream bos = new BufferedOutputStream(fos);
          byte[] aByte = new byte[1];

          while (inputGet.read(aByte, 0, aByte.length) != -1) {
            byteArrayGet.write(aByte);
          }
          bos.write(byteArrayGet.toByteArray());
          bos.flush();
          ps.println("0 Fin du téléchargement du fichier");
          bos.close();
        }
        socket.close();
        serveurFTP.close();
      } catch (IOException e) {
        ps.println("2 Erreur lors du téléchargement du fichier");
        e.printStackTrace();
      }
    } else {
      ps.println("2 Aucun fichier spécifié.");
    }
  }

}
