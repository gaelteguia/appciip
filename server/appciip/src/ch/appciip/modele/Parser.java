package modele;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import beans.Collaborateur;
import forms.ApplicationForm;
import forms.SeanceForm;
import forms.AdresseForm;
import forms.FonctionForm;
import forms.ComiteForm;
import forms.ServiceForm;
import forms.CollaborateurForm;

public class Parser implements Runnable {

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

	public Parser(int timer) {
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

		activite.start();

	}

	public void lectureApplication(String source) {

		try {

			int i = 0;
			int nbColonne = 6;
			String ligne;
			String courant = "";
			BufferedReader fichier = new BufferedReader(new FileReader(source));
			/* Pr�paration de l'objet formulaire */
			ApplicationForm form = new ApplicationForm(u, a, d, l, m, n, p, s, null);
			fichier.readLine();
			while ((ligne = fichier.readLine()) != null) {
				ArrayList<String> liste = new ArrayList<String>();
				for (i = 0; i < nbColonne; i++) {

					courant = ligne.substring(0, ligne.indexOf(';'));
					liste.add(courant);
					ligne = ligne.substring(ligne.indexOf(';') + 1, ligne.length());

				}
				try {
					// form.creerApplication(liste);
				} catch (DatabaseException e) {
					System.out.println("Erreur impr�vue lors de la cr�ation.");
					e.printStackTrace();
				}

			}
			fichier.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void lectureAdresse(String source) {

		try {
			int i = 0;
			int nbColonne = 7;
			String ligne;
			String courant = "";
			BufferedReader fichier = new BufferedReader(new FileReader(source));
			/* Pr�paration de l'objet formulaire */
			AdresseForm form = new AdresseForm(u, a, d, l, m, n, p, s);
			fichier.readLine();
			while ((ligne = fichier.readLine()) != null) {
				ArrayList<String> liste = new ArrayList<String>();
				for (i = 0; i < nbColonne; i++) {

					courant = ligne.substring(0, ligne.indexOf(';'));
					liste.add(courant);
					ligne = ligne.substring(ligne.indexOf(';') + 1, ligne.length());

				}
				try {
					// form.creerAdresse(liste);
				} catch (DatabaseException e) {
					System.out.println("Erreur impr�vue lors de la cr�ation.");
					e.printStackTrace();
				}

			}
			fichier.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void lectureService(String source) {

		try {
			int i = 0;
			int nbColonne = 5;
			String ligne;
			String courant = "";
			BufferedReader fichier = new BufferedReader(new FileReader(source));
			/* Pr�paration de l'objet formulaire */
			ServiceForm form = new ServiceForm(u, a, d, l, m, n, p, s);
			fichier.readLine();
			while ((ligne = fichier.readLine()) != null) {
				ArrayList<String> liste = new ArrayList<String>();
				for (i = 0; i < nbColonne; i++) {

					courant = ligne.substring(0, ligne.indexOf(';'));
					liste.add(courant);
					ligne = ligne.substring(ligne.indexOf(';') + 1, ligne.length());

				}
				try {
					// form.creerService(liste);
				} catch (DatabaseException e) {
					System.out.println("Erreur impr�vue lors de la cr�ation.");
					e.printStackTrace();
				}

			}
			fichier.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void lectureAdresseService(String source) {

		try {
			int i = 0;
			int nbColonne = 2;
			String ligne;
			String courant = "";
			BufferedReader fichier = new BufferedReader(new FileReader(source));
			/* Pr�paration de l'objet formulaire */
			AdresseForm form = new AdresseForm(u, a, d, l, m, n, p, s);
			fichier.readLine();
			while ((ligne = fichier.readLine()) != null) {
				ArrayList<String> liste = new ArrayList<String>();
				for (i = 0; i < nbColonne; i++) {

					courant = ligne.substring(0, ligne.indexOf(';'));
					liste.add(courant);
					ligne = ligne.substring(ligne.indexOf(';') + 1, ligne.length());

				}
				try {
					// form.associerService(liste);
				} catch (DatabaseException e) {
					System.out.println("Erreur impr�vue lors de la cr�ation.");
					e.printStackTrace();
				}

			}
			fichier.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void lectureFonction(String source) {

		try {
			int i = 0;
			int nbColonne = 7;
			String ligne;
			String courant = "";
			BufferedReader fichier = new BufferedReader(new FileReader(source));
			/* Pr�paration de l'objet formulaire */
			FonctionForm form = new FonctionForm(u, a, d, l, m, n, p, s);
			fichier.readLine();
			while ((ligne = fichier.readLine()) != null) {
				ArrayList<String> liste = new ArrayList<String>();
				for (i = 0; i < nbColonne; i++) {

					courant = ligne.substring(0, ligne.indexOf(';'));
					liste.add(courant);
					ligne = ligne.substring(ligne.indexOf(';') + 1, ligne.length());

				}
				try {
					// form.creerFonction(liste);
				} catch (DatabaseException e) {
					System.out.println("Erreur impr�vue lors de la cr�ation.");
					e.printStackTrace();
				}

			}
			fichier.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void lectureComite(String source) {

		try {
			int i = 0;
			int nbColonne = 2;
			String ligne;
			String courant = "";
			BufferedReader fichier = new BufferedReader(new FileReader(source));
			/* Pr�paration de l'objet formulaire */
			ComiteForm form = new ComiteForm(u, a, d, l, m, n, p, s);
			fichier.readLine();
			while ((ligne = fichier.readLine()) != null) {
				ArrayList<String> liste = new ArrayList<String>();
				for (i = 0; i < nbColonne; i++) {

					courant = ligne.substring(0, ligne.indexOf(';'));
					liste.add(courant);
					ligne = ligne.substring(ligne.indexOf(';') + 1, ligne.length());

				}
				try {
					// form.creerComite(liste);
				} catch (DatabaseException e) {
					System.out.println("Erreur impr�vue lors de la cr�ation.");
					e.printStackTrace();
				}

			}
			fichier.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void lectureFonctionComite(String source) {

		try {
			int i = 0;
			int nbColonne = 2;
			String ligne;
			String courant = "";
			BufferedReader fichier = new BufferedReader(new FileReader(source));
			/* Pr�paration de l'objet formulaire */
			FonctionForm form = new FonctionForm(u, a, d, l, m, n, p, s);
			fichier.readLine();
			while ((ligne = fichier.readLine()) != null) {
				ArrayList<String> liste = new ArrayList<String>();
				for (i = 0; i < nbColonne; i++) {

					courant = ligne.substring(0, ligne.indexOf(';'));
					liste.add(courant);
					ligne = ligne.substring(ligne.indexOf(';') + 1, ligne.length());

				}
				try {
					form.associerComite(liste);
				} catch (DatabaseException e) {
					System.out.println("Erreur impr�vue lors de la cr�ation.");
					e.printStackTrace();
				}

			}
			fichier.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void lectureSeance(String source) {

		try {
			int i = 0;
			int nbColonne = 1;
			String ligne;
			String courant = "";
			BufferedReader fichier = new BufferedReader(new FileReader(source));
			/* Pr�paration de l'objet formulaire */
			SeanceForm form = new SeanceForm(u, a, d, l, m, n, p, s);
			fichier.readLine();
			while ((ligne = fichier.readLine()) != null) {
				ArrayList<String> liste = new ArrayList<String>();
				for (i = 0; i < nbColonne; i++) {

					courant = ligne.substring(0, ligne.indexOf(';'));
					liste.add(courant);
					ligne = ligne.substring(ligne.indexOf(';') + 1, ligne.length());

				}
				try {
					// form.creerSeance(liste);
				} catch (DatabaseException e) {
					System.out.println("Erreur impr�vue lors de la cr�ation.");
					e.printStackTrace();
				}

			}
			fichier.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void lectureComiteSeance(String source) {

		try {
			int i = 0;
			int nbColonne = 2;
			String ligne;
			String courant = "";
			BufferedReader fichier = new BufferedReader(new FileReader(source));
			/* Pr�paration de l'objet formulaire */
			ComiteForm form = new ComiteForm(u, a, d, l, m, n, p, s);
			fichier.readLine();
			while ((ligne = fichier.readLine()) != null) {
				ArrayList<String> liste = new ArrayList<String>();
				for (i = 0; i < nbColonne; i++) {

					courant = ligne.substring(0, ligne.indexOf(';'));
					liste.add(courant);
					ligne = ligne.substring(ligne.indexOf(';') + 1, ligne.length());

				}
				try {
					form.associerSeance(liste);
				} catch (DatabaseException e) {
					System.out.println("Erreur impr�vue lors de la cr�ation.");
					e.printStackTrace();
				}

			}
			fichier.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void lectureCollaborateur(String source) {

		try {
			int i = 0;
			int nbColonne = 17;
			String ligne;
			String courant = "";

			BufferedReader fichier = new BufferedReader(new FileReader(source));
			/* Pr�paration de l'objet formulaire */
			CollaborateurForm form = new CollaborateurForm(u, a, d, l, m, n, p, s);
			fichier.readLine();
			while ((ligne = fichier.readLine()) != null) {
				ArrayList<String> liste = new ArrayList<String>();
				for (i = 0; i < nbColonne; i++) {
					courant = ligne.substring(0, ligne.indexOf(';'));
					liste.add(courant);
					ligne = ligne.substring(ligne.indexOf(';') + 1, ligne.length());

				}
				try {
					form.creerCollaborateur(liste);
				} catch (DatabaseException e) {
					System.out.println(form.getResultat());
					System.out.println("Erreur impr�vue lors de la cr�ation.");
					e.printStackTrace();
				}

			}
			fichier.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void traitementAD(ArrayList<Utilisateur> ancienneListe, ArrayList<Utilisateur> nouvelleListe) {
		/* Pr�paration de l'objet formulaire */
		CollaborateurForm form = new CollaborateurForm(u, a, d, l, m, n, p, s);
		Iterator<Utilisateur> it = nouvelleListe.iterator();

		boolean trouve;

		while (it.hasNext()) {
			Utilisateur s = it.next();

			trouve = false;
			Iterator<Utilisateur> it2 = ancienneListe.iterator();
			while (it2.hasNext()) {
				Utilisateur s2 = it2.next();
				if (s.getInitiales().equals(s2.getInitiales())) {
					if (!s.getNom().equals(s2.getNom()) || !s.getPrenom().equals(s2.getPrenom())
							|| !s.getEmail().equals(s2.getEmail()) || !s.getTelInterne().equals(s2.getTelInterne())) {

						form.lireAD(s);
					}
					trouve = true;
				}

			}
			if (!trouve) {

				form.lireAD(s);

			}

		}
		Iterator<Utilisateur> it2 = ancienneListe.iterator();
		while (it2.hasNext()) {
			Utilisateur s2 = it2.next();
			trouve = false;
			it = nouvelleListe.iterator();
			while (it.hasNext()) {
				Utilisateur s = it.next();
				if (s2.getInitiales().equals(s.getInitiales())) {

					trouve = true;
				}

			}
			if (!trouve) {

				form.supprimerAD(s2);

			}

		}
	}

	public void traitementOLY(ArrayList<Utilisateur> liste) {
		/* Pr�paration de l'objet formulaire */
		CollaborateurForm form = new CollaborateurForm(u, a, d, l, m, n, p, s);
		Iterator<Utilisateur> it = liste.iterator();
		try {
			while (it.hasNext()) {
				Utilisateur s = it.next();
				form.notifierOLY(s);

			}
		} catch (DatabaseException e) {
			System.out.println(form.getResultat());
			System.out.println("Erreur impr�vue lors de la cr�ation.");
			e.printStackTrace();
		}

	}

	public ArrayList<Utilisateur> lectureAD(String source) {
		ArrayList<Utilisateur> nouvelleListe = new ArrayList<Utilisateur>();
		try {
			int i = 0;
			int nbColonne = 3;
			String ligne;
			String courant = "";

			BufferedReader fichier = new BufferedReader(new FileReader(source));
			/* Pr�paration de l'objet formulaire */
			CollaborateurForm form = new CollaborateurForm(u, a, d, l, m, n, p, s);
			fichier.readLine();
			while ((ligne = fichier.readLine()) != null) {
				ArrayList<String> liste = new ArrayList<String>();
				for (i = 0; i < nbColonne; i++) {

					courant = ligne.substring(0, ligne.indexOf(','));

					liste.add(courant);
					ligne = ligne.substring(ligne.indexOf(',') + 1, ligne.length());

				}
				liste.add(ligne);
				try {

					Utilisateur utilisateur = new Utilisateur();
					if (liste.get(0).contains("@")) {
						utilisateur.setInitiales(liste.get(0).substring(0, liste.get(0).indexOf('@')));

						if (liste.get(1).contains(" ")) {
							utilisateur.setPrenom(liste.get(1).substring(0, liste.get(1).indexOf(' ')));
							utilisateur.setNom(
									liste.get(1).substring(liste.get(1).indexOf(' ') + 1, liste.get(1).length()));
							utilisateur.setEmail(liste.get(2));
							utilisateur.setTelInterne(liste.get(3));

							nouvelleListe.add(utilisateur);
						}

					}

				} catch (DatabaseException e) {
					System.out.println(form.getResultat());
					System.out.println("Erreur impr�vue lors de la cr�ation.");
					e.printStackTrace();
				}

			}
			fichier.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nouvelleListe;

	}

	public void run() {
		while (true) {
			System.out.println("Debut des traitements");

			System.out.println("Traitement des utilisateurs de l'Active Directory");
			traitementAD(lectureAD("c:/fichiers/ADold.csv"), lectureAD("c:/fichiers/ADnew.csv"));

			System.out.println("Traitement des utilisateurs de l'application OLYMPIC");

			traitementOLY(u.lectureOLY("c:/fichiers/OLY.csv"));

			System.out.println("Traitement des utilisateurs");
			lectureCollaborateur("c:/fichiers/Collaborateurs.csv");

			System.out.println("Traitement des services");
			lectureService("c:/fichiers/Services.csv");

			System.out.println("Traitement des localisations");
			lectureAdresse("c:/fichiers/Adresses.csv");

			System.out.println("Traitement des droits d'acc�s");
			lectureSeance("c:/fichiers/Seances.csv");

			System.out.println("Traitement des profils m�tiers");
			lectureFonction("c:/fichiers/Fonctions.csv");

			System.out.println("Traitement des applications");
			lectureApplication("c:/fichiers/Applications.csv");

			System.out.println("Traitement des profils d'acc�s");
			lectureComite("c:/fichiers/Comites.csv");

			System.out.println("Traitement des localisations des services");
			lectureAdresseService("Adresses_Services.csv");

			System.out.println("Traitement des profils d'acc�s des profils m�tiers");
			lectureFonctionComite("c:/fichiers/Fonctions_Comites.csv");

			System.out.println("Traitement des droits d'acc�s des profils d'acc�s");
			lectureComiteSeance("c:/fichiers/Comites_Seances.csv");

			try {
				// Pause de "timer" secondes
				Thread.sleep(timer * 1000);
			}

			catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public static void main(String[] argv) {
		new Parser(60);
	}

}
