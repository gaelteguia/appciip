package modele;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.TimerTask;

import forms.AdresseForm;
import forms.AnnonceForm;
import forms.ApplicationForm;
import forms.BeamerForm;
import forms.BreveForm;
import forms.CollaborateurForm;
import forms.ComiteForm;
import forms.CompteRenduForm;
import forms.DirectiveForm;
import forms.DistributionForm;
import forms.EvenementForm;
import forms.FonctionForm;
import forms.FournisseurForm;
import forms.HoraireForm;
import forms.ImprimanteForm;
import forms.ManifestationForm;
import forms.OutilForm;
import forms.PartenaireForm;
import forms.SalleForm;
import forms.SeanceForm;
import forms.ServiceForm;
import forms.StationForm;
import forms.TelephoneForm;
import forms.VoitureForm;

public class DataParser extends TimerTask implements Runnable {

	private int timer;
	private Factory factory;
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

	public AnnonceManager an;

	public ManifestationManager ma;

	public DataParser(int timer) {
		this.setTimer(timer);
		activite = new Thread(this);
		this.factory = Factory.getInstance();
		this.u = factory.getCollaborateurManager();
		this.a = factory.getApplicationManager();
		this.d = factory.getSeanceManager();
		this.l = factory.getAdresseManager();
		this.m = factory.getFonctionManager();
		this.n = factory.getEvenementManager();
		this.p = factory.getComiteManager();
		this.s = factory.getServiceManager();
		this.v = factory.getVoitureManager();
		this.f = factory.getFournisseurManager();
		this.sa = factory.getSalleManager();
		this.b = factory.getBeamerManager();
		this.i = factory.getImprimanteManager();
		this.st = factory.getStationManager();
		this.t = factory.getTelephoneManager();
		this.di = factory.getDistributionManager();
		this.h = factory.getHoraireManager();
		this.dire = factory.getDirectiveManager();
		this.o = factory.getOutilManager();

		this.br = factory.getBreveManager();
		this.cr = factory.getCompteRenduManager();
		this.pa = factory.getPartenaireManager();

		this.an = factory.getAnnonceManager();

		this.ma = factory.getManifestationManager();

		activite.start();

	}

	private final static Charset ENCODING = StandardCharsets.UTF_8;

	private void parseAdresse() throws IOException {

		File dir = new File("C:/fichiers/adresses");
		File[] files = dir.listFiles();

		AdresseForm form = new AdresseForm(u, a, d, l, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerAdresse(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseDirective() throws IOException {

		File dir = new File("C:/fichiers/directives");
		File[] files = dir.listFiles();

		DirectiveForm form = new DirectiveForm(u, a, d, dire, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerDirective(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseAnnonce() throws IOException {

		File dir = new File("C:/fichiers/annonces");
		File[] files = dir.listFiles();

		AnnonceForm form = new AnnonceForm(u, a, d, m, an, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerAnnonce(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseOutil() throws IOException {

		File dir = new File("C:/fichiers/outils");
		File[] files = dir.listFiles();

		OutilForm form = new OutilForm(u, a, d, o, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerOutil(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseBreve() throws IOException {

		File dir = new File("C:/fichiers/breves");
		File[] files = dir.listFiles();

		BreveForm form = new BreveForm(u, a, d, br, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerBreve(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseCompteRendu() throws IOException {

		File dir = new File("C:/fichiers/comptes_rendus");
		File[] files = dir.listFiles();

		CompteRenduForm form = new CompteRenduForm(u, a, d, cr, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerCompteRendu(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parsePartenaire() throws IOException {

		File dir = new File("C:/fichiers/partenaires");
		File[] files = dir.listFiles();

		PartenaireForm form = new PartenaireForm(u, a, d, l, m, n, pa, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerPartenaire(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseAdresseService() throws IOException {

		File dir = new File("C:/fichiers/adresses_services");
		File[] files = dir.listFiles();

		AdresseForm form = new AdresseForm(u, a, d, l, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.associerService(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseApplication() throws IOException {

		File dir = new File("C:/fichiers/applications");
		File[] files = dir.listFiles();

		ApplicationForm form = new ApplicationForm(u, a, d, l, m, n, p, s, f);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerApplication(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseBeamer() throws IOException {

		File dir = new File("C:/fichiers/beamers");
		File[] files = dir.listFiles();

		BeamerForm form = new BeamerForm(u, a, b, d, l, m, n, p, s, f, sa);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerBeamer(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseImprimante() throws IOException {

		File dir = new File("C:/fichiers/imprimantes");
		File[] files = dir.listFiles();

		ImprimanteForm form = new ImprimanteForm(u, a, b, i, d, l, m, n, p, s, f, sa);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerImprimante(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseStation() throws IOException {

		File dir = new File("C:/fichiers/stations");
		File[] files = dir.listFiles();

		StationForm form = new StationForm(u, a, b, st, d, l, m, n, p, s, f, sa);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerStation(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseTelephone() throws IOException {

		File dir = new File("C:/fichiers/telephones");
		File[] files = dir.listFiles();

		TelephoneForm form = new TelephoneForm(t);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerTelephone(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseComite() throws IOException {

		File dir = new File("C:/fichiers/comites");
		File[] files = dir.listFiles();

		ComiteForm form = new ComiteForm(p);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerComite(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseDistribution() throws IOException {

		File dir = new File("C:/fichiers/distributions");
		File[] files = dir.listFiles();

		DistributionForm form = new DistributionForm(di);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerDistribution(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseFonction() throws IOException {

		File dir = new File("C:/fichiers/fonctions");
		File[] files = dir.listFiles();

		FonctionForm form = new FonctionForm(u, a, d, l, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerFonction(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseFournisseur() throws IOException {

		File dir = new File("C:/fichiers/fournisseurs");
		File[] files = dir.listFiles();

		FournisseurForm form = new FournisseurForm(u, a, d, l, f, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerFournisseur(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseSalle() throws IOException {

		File dir = new File("C:/fichiers/salles");
		File[] files = dir.listFiles();

		SalleForm form = new SalleForm(u, a, d, l, sa, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerSalle(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseSeance() throws IOException {

		File dir = new File("C:/fichiers/seances");
		File[] files = dir.listFiles();

		SeanceForm form = new SeanceForm(u, a, d, l, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerSeance(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseService() throws IOException {

		File dir = new File("C:/fichiers/services");
		File[] files = dir.listFiles();

		ServiceForm form = new ServiceForm(u, a, d, l, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerService(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseEvenement() throws IOException {

		File dir = new File("C:/fichiers/evenements");
		File[] files = dir.listFiles();

		/* Pr�paration de l'objet formulaire */
		EvenementForm form = new EvenementForm(u, a, d, l, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerEvenement(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseManifestation() throws IOException {

		File dir = new File("C:/fichiers/manifestations");
		File[] files = dir.listFiles();

		/* Pr�paration de l'objet formulaire */
		ManifestationForm form = new ManifestationForm(u, a, d, l, m, ma, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerManifestation(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseVoiture() throws IOException {

		File dir = new File("C:/fichiers/voitures");
		File[] files = dir.listFiles();

		/* Pr�paration de l'objet formulaire */
		VoitureForm form = new VoitureForm(u, a, d, l, m, n, p, s, v);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerVoiture(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseHoraire() throws IOException {

		File dir = new File("C:/fichiers/horaires");
		File[] files = dir.listFiles();

		/* Pr�paration de l'objet formulaire */
		HoraireForm form = new HoraireForm(u, a, h, d, l, m, n, p, s, v);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerHoraire(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseVoitureHoraire() throws IOException {

		File dir = new File("C:/fichiers/voitures_horaires");
		File[] files = dir.listFiles();

		HoraireForm form = new HoraireForm(u, a, h, d, l, m, n, p, s, v);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.associerVoiture(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseCollaborateurHoraire() throws IOException {

		File dir = new File("C:/fichiers/collaborateurs_horaires");
		File[] files = dir.listFiles();

		HoraireForm form = new HoraireForm(u, a, h, d, l, m, n, p, s, v);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.associerCollaborateur(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseCollaborateur() throws IOException {

		File dir = new File("C:/fichiers/collaborateurs");
		File[] files = dir.listFiles();

		CollaborateurForm form = new CollaborateurForm(u, a, d, l, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.creerCollaborateur(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseCollaborateurApplication() throws IOException {

		File dir = new File("C:/fichiers/collaborateurs_applications");
		File[] files = dir.listFiles();

		CollaborateurForm form = new CollaborateurForm(u, a, d, l, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.associerApplication(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseCollaborateurBeamer() throws IOException {

		File dir = new File("C:/fichiers/collaborateurs_beamers");
		File[] files = dir.listFiles();

		CollaborateurForm form = new CollaborateurForm(u, a, d, l, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.associerBeamer(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseCollaborateurComite() throws IOException {

		File dir = new File("C:/fichiers/collaborateurs_comites");
		File[] files = dir.listFiles();

		CollaborateurForm form = new CollaborateurForm(u, a, d, l, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.associerComite(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseCollaborateurDistribution() throws IOException {

		File dir = new File("C:/fichiers/collaborateurs_distributions");
		File[] files = dir.listFiles();

		CollaborateurForm form = new CollaborateurForm(u, a, d, l, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.associerDistribution(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseCollaborateurImprimante() throws IOException {

		File dir = new File("C:/fichiers/collaborateurs_imprimantes");
		File[] files = dir.listFiles();

		CollaborateurForm form = new CollaborateurForm(u, a, d, l, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.associerImprimante(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseCollaborateurSeance() throws IOException {

		File dir = new File("C:/fichiers/collaborateurs_seances");
		File[] files = dir.listFiles();

		CollaborateurForm form = new CollaborateurForm(u, a, d, l, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.associerSeance(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseCollaborateurService() throws IOException {

		File dir = new File("C:/fichiers/collaborateurs_services");
		File[] files = dir.listFiles();

		CollaborateurForm form = new CollaborateurForm(u, a, d, l, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.associerService(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseCollaborateurSuperieur() throws IOException {

		File dir = new File("C:/fichiers/collaborateurs_superieurs");
		File[] files = dir.listFiles();

		CollaborateurForm form = new CollaborateurForm(u, a, d, l, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.associerSuperieur(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseCollaborateurSuppleant() throws IOException {

		File dir = new File("C:/fichiers/collaborateurs_suppleants");
		File[] files = dir.listFiles();

		CollaborateurForm form = new CollaborateurForm(u, a, d, l, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.associerSuppleant(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void parseCollaborateurSupplee() throws IOException {

		File dir = new File("C:/fichiers/collaborateurs_supplees");
		File[] files = dir.listFiles();

		CollaborateurForm form = new CollaborateurForm(u, a, d, l, m, n, p, s);

		for (File file : files) {
			try (Scanner scanner = new Scanner(Paths.get(file.getCanonicalPath()), ENCODING.name())) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					String[] tokens = scanner.nextLine().split(";");
					for (String t : tokens)
						System.out.println(t);
					try {
						form.associerSupplee(tokens);
					} catch (DatabaseException e) {
						System.out.println(form.getResultat());
						System.out.println("Erreur impr�vue lors de la cr�ation.");
						e.printStackTrace();
					}
				}
			}

		}

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
			parseOutil();
			parseApplication();
			parseDirective();
			parseVoitureHoraire();
			parseService();
			parseSalle();
			parseEvenement();
			parseCollaborateur();
			parseCollaborateurSuperieur();
			parseCollaborateurSuppleant();
			parseCollaborateurSupplee();
			parseCollaborateurService();
			parseAdresseService();
			parseManifestation();

			parseComite();
			/*
			 * parseAdresse(); parseCollaborateurApplication();
			 * parseCollaborateurComite(); parseSeance(); parseBreve();
			 * parseStation(); parseAnnonce();
			 * 
			 * 
			 * parseFonction();
			 * 
			 * parseHoraire(); parseVoiture(); parseCollaborateurHoraire();
			 * 
			 * 
			 * //
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * parseBeamer();
			 * 
			 * parseDistribution();
			 * 
			 * parseFournisseur(); parseImprimante(); parseTelephone();
			 * parsePartenaire();
			 * 
			 * parseCompteRendu();
			 * 
			 * 
			 * 
			 * parseCollaborateurBeamer(); parseCollaborateurDistribution();
			 * parseCollaborateurImprimante(); parseCollaborateurSeance();
			 * 
			 * 
			 */

			// Pause de "timer" secondes
			Thread.sleep(timer * 1000);
		}

		catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}

	}

}
