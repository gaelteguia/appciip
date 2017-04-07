package modele;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.TimerTask;

public class FileName extends TimerTask implements Runnable {

	private int timer;
	private Thread activite;
	public CollaborateurManager u;
	public ApplicationManager a;
	public SeanceManager d;
	public AdresseManager l;
	public FonctionManager m;
	public ComiteManager p;
	public ServiceManager s;
	public EvenementManager n;
	public VoitureManager v;
	public HoraireManager h;
	public FournisseurManager f;
	public SalleManager sa;
	public BeamerManager b;
	public ImprimanteManager i;
	public StationManager st;
	public TelephoneManager t;
	public DistributionManager di;
	public DirectiveManager dire;

	public OutilManager o;

	public BreveManager br;

	public CompteRenduManager cr;

	public PartenaireManager pa;

	public FileName(int timer) {
		this.setTimer(timer);
		activite = new Thread(this);
		activite.start();

	}

	private final static Charset ENCODING = StandardCharsets.UTF_8;

	private void parse() throws IOException {
		Path repertoire = FileSystems.getDefault().getPath("C:/index");

		if (Files.exists(repertoire) && Files.isDirectory(repertoire)) {
			try {
				Files.walkFileTree(repertoire, new SimpleFileVisitor<Path>() {

					// Peut être utilisé pour effectuer des actions sur le
					// répertoire et les sous-répertoires en début de visite.
					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

						System.out.println(dir.toString());

						File d = new File(dir.toString());
						File[] files = d.listFiles();

						// AdresseForm form = new AdresseForm(u, a, d, l, m, n,
						// p, s);

						for (File file : files) {
							if (file.isFile() && file.getName().toString().length() > 10) {
								Path source = Paths.get(file.getCanonicalPath());

								System.out.println(file.getName().toString());

								String dest = dir.toString() + "\\"
										+ file.getName().toString().substring(0, 11).replaceAll("[\\W]", "_") + ".jpg";

								Path destination = Paths.get(dest);
								try {
									copier(source, destination);
								} catch (FileAlreadyExistsException e) {

									System.out.println("Fichier a regarder" + source);

								}
							}

						}

						return FileVisitResult.CONTINUE;
					}

					// Peut être utilisé pour effectuer des actions sur les
					// fichiers dans chaque répertoire au cours de la visite.
					// @Override
					// public FileVisitResult visitFile(Path file,
					// BasicFileAttributes attrs) throws IOException {
					// return FileVisitResult.CONTINUE;
					// }

					// Peut être utilisé pour effectuer des actions sur le
					// répertoire et les sous-répertoires en fin de visite.
					// @Override
					// public FileVisitResult postVisitDirectory(Path dir,
					// IOException exc) throws IOException {
					// return FileVisitResult.CONTINUE;
					// }
				});
			} catch (IOException e) {
				System.err.println(repertoire + " : Erreur de lecture.");
			}
		}

	}

	public static boolean copier(Path source, Path destination) throws FileAlreadyExistsException {
		try {
			Files.copy(source, destination);
			// Il est également possible de spécifier des options de copie.
			// Ici : écrase le fichier destination s'il existe et copie les
			// attributs de la source sur la destination.
			// Files.copy(source, destination,
			// StandardCopyOption.REPLACE_EXISTING,
			// StandardCopyOption.COPY_ATTRIBUTES);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public void run() {

		System.out.println("Debut des traitements");
		try {
			parse();

			Thread.sleep(timer * 1000);
		}

		catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}

	}

}
