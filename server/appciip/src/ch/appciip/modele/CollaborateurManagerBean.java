package modele;

import static modele.Utilitaire.fermeturesSilencieuses;
import static modele.Utilitaire.initialisationRequetePreparee;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import beans.Adresse;
import beans.Application;
import beans.Beamer;
import beans.Collaborateur;
import beans.Comite;
import beans.Distribution;
import beans.Evenement;
import beans.Fonction;
import beans.Horaire;
import beans.Imprimante;
import beans.Seance;
import beans.Service;
import beans.Statut;

public class CollaborateurManagerBean implements CollaborateurManager {

	private static final String SQL_SELECT_PAR_INITIALES = "SELECT * FROM collaborateur WHERE initiales =?";
	private static final String SQL_SELECT = "SELECT * FROM collaborateur";
	private static final String SQL_ANNIVERSAIRE = "SELECT * FROM collaborateur WHERE MONTH(date_anniversaire)=MONTH(CURRENT_TIMESTAMP) AND DAY(date_anniversaire)=DAY(CURRENT_TIMESTAMP)";
	private static final String SQL_PROCHAIN_ANNIVERSAIRE = "SELECT * FROM collaborateur WHERE (MONTH(date_anniversaire)=MONTH(CURRENT_TIMESTAMP) AND DAY(date_anniversaire)>DAY(CURRENT_TIMESTAMP)) OR (MONTH(date_anniversaire)=MONTH(CURRENT_TIMESTAMP+1) AND DAY(date_anniversaire)=DAY(CURRENT_TIMESTAMP+24))";

	private static final String SQL_SELECT_PAR_INITIALES_MDP = "SELECT * FROM collaborateur WHERE initiales =? AND mdp =?";
	private static final String SQL_SELECT_PAR_INITIALES_REPONSE = "SELECT * FROM collaborateur WHERE initiales =? AND reponse LIKE ?";
	private static final String SQL_SELECT_PAR_NOM_PRENOM = "SELECT * FROM collaborateur WHERE nom =? AND prenom LIKE ?";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM collaborateur WHERE id_collaborateur = ?";
	private static final String SQL_INSERT = "INSERT INTO collaborateur (nom,prenom,initiales,mdp,sexe,statut,email,tel_interne,tel_prive,image,admin,activites,date_anniversaire,date_debut_service,date_fin_service,date_fin_effective,taux,id_superieur_hierarchique, id_adresse, id_fonction) VALUES(UPPER(?),?,UPPER(?),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String SQL_UPDATE = "UPDATE collaborateur SET nom=UPPER(?),prenom=?,initiales=UPPER(?),sexe=?,statut=?,email=?,tel_interne=?,tel_prive=?,image=?,admin=?,activites=?,date_anniversaire=?,date_debut_service=?,date_fin_service=?,date_fin_effective=?,  taux=?,id_superieur_hierarchique=?, id_adresse=?, id_fonction=? WHERE id_collaborateur =?";
	private static final String SQL_DELETE_PAR_ID = "DELETE FROM Collaborateur WHERE id_collaborateur = ?";
	private static final String SQL_UPDATE_MDP = "UPDATE collaborateur SET mdp =? WHERE id_collaborateur =?";
	private static final String SQL_UPDATE_CHALLENGE = "UPDATE collaborateur SET question =?, reponse =? WHERE id_collaborateur =?";
	private static final String SQL_UPDATE_ACTIF = "UPDATE collaborateur SET actif =? WHERE id_collaborateur =?";
	private Factory factory;

	CollaborateurManagerBean(Factory factory) {
		this.factory = factory;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * CollaborateurManager
	 */
	@Override
	public Utilisateur connecter(Utilisateur collaborateur) throws DatabaseException {

		return retrouver(SQL_SELECT_PAR_INITIALES_MDP, collaborateur.getInitiales(), hasher(collaborateur.getMdp()));
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * CollaborateurManager
	 */
	@Override
	public Utilisateur retrouver(Utilisateur collaborateur) throws DatabaseException {
		if (collaborateur.getId() != null && collaborateur.getId() != 0L)
			return retrouver(SQL_SELECT_PAR_ID, collaborateur.getId());
		else if (collaborateur.getInitiales() != null) {
			// System.out.println(collaborateur.getPrenom());
			// System.out.println(collaborateur.getNom());
			// System.out.println("-----------LOG--------");
			return retrouver(SQL_SELECT_PAR_INITIALES, collaborateur.getInitiales());
		} else {
			// System.out.println(collaborateur.getPrenom());
			// System.out.println(collaborateur.getNom());
			// System.out.println("--------------------");
			return retrouver(SQL_SELECT_PAR_NOM_PRENOM, collaborateur.getNom(), collaborateur.getPrenom());
		}
	}

	@Override
	public Utilisateur challenger(Utilisateur collaborateur) throws DatabaseException {

		return retrouver(SQL_SELECT_PAR_INITIALES_REPONSE, collaborateur.getInitiales(),
				hasher(collaborateur.getReponse()));
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * CollaborateurManager
	 */

	@Override
	public Utilisateur creer(Utilisateur collaborateur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		Timestamp dateAnniversaire = null;
		if (collaborateur.getDateAnniversaire() != null)
			dateAnniversaire = new Timestamp(collaborateur.getDateAnniversaire().getMillis());

		Timestamp dateDebut = null;
		if (collaborateur.getDateDebutService() != null)
			dateDebut = new Timestamp(collaborateur.getDateDebutService().getMillis());
		Timestamp dateFin = null;
		if (collaborateur.getDateFinService() != null)
			dateFin = new Timestamp(collaborateur.getDateFinService().getMillis());

		Timestamp dateFinEff = null;
		if (collaborateur.getDateFinEffective() != null)
			dateFinEff = new Timestamp(collaborateur.getDateFinEffective().getMillis());

		Long idSuperviseur = null;
		Long idAdresse = null;
		Long idFonction = null;
		String statut = null;
		if (collaborateur.getSuperviseur() != null)
			idSuperviseur = collaborateur.getSuperviseur().getId();
		if (collaborateur.getAdresse() != null)
			idAdresse = collaborateur.getAdresse().getId();
		if (collaborateur.getFonction() != null)
			idFonction = collaborateur.getFonction().getId();
		if (collaborateur.getStatut() != null)
			statut = collaborateur.getStatut().toString();
		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, collaborateur.getNom(),
					collaborateur.getPrenom(), collaborateur.getInitiales(), hasher(collaborateur.getMdp()),
					collaborateur.getSexe(), statut, collaborateur.getEmail(), collaborateur.getTelInterne(),
					collaborateur.getTelPrive(), collaborateur.getImage(), collaborateur.isAdmin(),
					collaborateur.getActivites(), dateAnniversaire, dateDebut, dateFin, dateFinEff,
					collaborateur.getTaux(), idSuperviseur, idAdresse, idFonction);
			int status = preparedStatement.executeUpdate();
			if (status == 0) {
				throw new DatabaseException(
						"�chec de la cr�ation de l'collaborateur, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {
				/*
				 * EvenementManager evenement = factory.getEvenementManager();
				 * 
				 * collaborateur = retrouver(new
				 * Collaborateur((valeursAutoGenerees.getLong(1))));
				 * evenement.creer(new Evenement("Arrivée du collaborateur",
				 * "Bienvenue", new DateTime(new Date()), collaborateur));
				 */

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation de l'collaborateur en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return collaborateur;
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un collaborateur depuis la base
	 * de donn�es, correspondant � la requ�te SQL donn�e prenant en param�tres
	 * les objets pass�s en argument.
	 */
	private Utilisateur retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Utilisateur collaborateur = null;

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();

			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, objets);
			resultSet = preparedStatement.executeQuery();
			/* Parcours de la ligne de donn�es retourn�e dans le ResultSet */
			if (resultSet.next()) {
				collaborateur = map(resultSet);
				// collaborateur = consulterProfilsCollaborateur(collaborateur);
				collaborateur = consulterEvenementsCollaborateur(collaborateur);

			}
		} catch (SQLException e) {

			throw new DatabaseException(e);
		} finally {

			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return collaborateur;
	}
	/*
	 * private Collaborateur consulterProfilsCollaborateur(Collaborateur
	 * collaborateur) throws DatabaseException { Connection connexion = null;
	 * PreparedStatement preparedStatement = null; ResultSet resultSet = null;
	 * 
	 * ArrayList<Profil> uneListe = new ArrayList<Profil>(); String sql =
	 * "SELECT profil.identifiant AS identifiant FROM fonction" +
	 * " INNER JOIN fonction_profil" +
	 * " ON fonction.id_fonction = fonction_profil.id_fonction" +
	 * " INNER JOIN profil" + " ON fonction_profil.id_profil = profil.id_profil"
	 * + " INNER JOIN collaborateur" +
	 * " ON collaborateur.id_fonction = fonction.id_fonction" +
	 * " WHERE collaborateur.id_collaborateur=?";
	 * 
	 * try {
	 * 
	 * connexion = factory.getConnection();
	 * 
	 * preparedStatement = initialisationRequetePreparee(connexion, sql, false,
	 * collaborateur.getId()); resultSet = preparedStatement.executeQuery();
	 * 
	 * while (resultSet.next()) { uneListe.add(new
	 * Profil(resultSet.getString("identifiant"))); } } catch (SQLException e) {
	 * throw new DatabaseException(e); } finally {
	 * fermeturesSilencieuses(resultSet, preparedStatement, connexion); }
	 * collaborateur.setProfils(uneListe); return collaborateur; }
	 * 
	 */

	private Utilisateur consulterEvenementsCollaborateur(Utilisateur collaborateur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Evenement> uneListe = new ArrayList<Evenement>();
		String sql = "SELECT * FROM evenement" + " INNER JOIN collaborateur"
				+ " ON evenement.id_collaborateur = collaborateur.id_collaborateur"
				+ " WHERE collaborateur.id_collaborateur=?";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, collaborateur.getId());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				uneListe.add(new Evenement(resultSet.getString("titre"), resultSet.getString("description"),
						new DateTime(resultSet.getTimestamp("date")), collaborateur));

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		collaborateur.setEvenements(uneListe);
		return collaborateur;
	}

	private void modifier(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, objets);
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * CollaborateurManager
	 */
	@Override
	public void supprimer(Utilisateur collaborateur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		String sql = "UPDATE collaborateur  SET id_superieur_hierarchique =? WHERE id_collaborateur = ?";
		if (collaborateur.getSuperviseur() != null) {
			Long idSuperviseur = collaborateur.getSuperviseur().getId();
			List<Utilisateur> listeCollaborateurs = lister(collaborateur);

			for (Utilisateur u : listeCollaborateurs) {
				modifier(sql, idSuperviseur, u.getId());

			}
		}

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true,
					collaborateur.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression de l'collaborateur, aucune ligne supprim�e de la table.");
			} else {

				collaborateur.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * CollaborateurManager
	 */
	@Override
	public List<Utilisateur> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Utilisateur> collaborateurs = new ArrayList<Utilisateur>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Utilisateur collaborateur = map(resultSet);
				collaborateur = consulterServicesCollaborateur(collaborateur);
				collaborateur = consulterEvenementsCollaborateur(collaborateur);
				collaborateur = consulterHorairesCollaborateur(collaborateur);
				collaborateur = consulterSuppleantsCollaborateur(collaborateur);
				collaborateur = consulterSuppleesCollaborateur(collaborateur);
				collaborateur = consulterSuperieursCollaborateur(collaborateur);
				collaborateur = consulterApplicationsCollaborateur(collaborateur);
				collaborateurs.add(collaborateur);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return collaborateurs;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * CollaborateurManager
	 */
	@Override
	public List<Utilisateur> anniversaire() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Utilisateur> collaborateurs = new ArrayList<Utilisateur>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_ANNIVERSAIRE);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Utilisateur collaborateur = map(resultSet);
				// System.out.println(collaborateur.getDateAnniversaire());
				// collaborateur = consulterProfilsCollaborateur(collaborateur);
				collaborateur = consulterEvenementsCollaborateur(collaborateur);
				collaborateurs.add(collaborateur);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return collaborateurs;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * CollaborateurManager
	 */
	@Override
	public List<Utilisateur> prochainAnniversaire() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Utilisateur> collaborateurs = new ArrayList<Utilisateur>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_PROCHAIN_ANNIVERSAIRE);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Utilisateur collaborateur = map(resultSet);

				// collaborateur = consulterProfilsCollaborateur(collaborateur);
				collaborateur = consulterEvenementsCollaborateur(collaborateur);
				collaborateurs.add(collaborateur);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return collaborateurs;
	}

	@Override
	public List<Utilisateur> lister(Utilisateur collaborateur) throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Utilisateur> collaborateurs = new ArrayList<Utilisateur>();
		String sql = "SELECT * FROM collaborateur WHERE id_superieur_hierarchique = " + collaborateur.getId()
				+ " AND id_collaborateur <> id_superieur_hierarchique";

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Utilisateur u = map(resultSet);
				// u = consulterProfilsCollaborateur(u);
				u = consulterEvenementsCollaborateur(u);
				collaborateurs.add(u);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return collaborateurs;
	}

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des collaborateurs (un
	 * ResultSet) et un bean Collaborateur.
	 */
	private Utilisateur map(ResultSet resultSet) throws SQLException {
		Utilisateur collaborateur = new Utilisateur();
		Timestamp dateDebut = resultSet.getTimestamp("date_debut_service");
		if (dateDebut != null)
			collaborateur.setDateDebutService(new DateTime(dateDebut));

		Timestamp dateFin = resultSet.getTimestamp("date_fin_service");
		if (dateFin != null)
			collaborateur.setDateFinService(new DateTime(dateFin));

		Timestamp dateFinEff = resultSet.getTimestamp("date_fin_effective");
		if (dateFinEff != null)
			collaborateur.setDateFinEffective(new DateTime(dateFinEff));

		Timestamp dateAnniversaire = resultSet.getTimestamp("date_anniversaire");

		if (dateAnniversaire != null)
			collaborateur.setDateAnniversaire((new DateTime(dateAnniversaire)).plusHours(1));

		collaborateur.setId(resultSet.getLong("id_collaborateur"));
		collaborateur.setEmail(resultSet.getString("email"));
		collaborateur.setInitiales(resultSet.getString("initiales"));
		collaborateur.setMdp(resultSet.getString("mdp"));
		collaborateur.setSexe(resultSet.getString("sexe"));
		collaborateur.setNom(resultSet.getString("nom"));
		collaborateur.setPrenom(resultSet.getString("prenom"));
		collaborateur.setTelInterne(resultSet.getString("tel_interne"));
		collaborateur.setTelPrive(resultSet.getString("tel_prive"));
		collaborateur.setActif(resultSet.getBoolean("actif"));
		collaborateur.setAdmin(resultSet.getBoolean("admin"));
		collaborateur.setStatut(Statut.fromString(resultSet.getString("statut")));
		collaborateur.setImage(resultSet.getString("image"));
		collaborateur.setQuestion(resultSet.getString("question"));
		collaborateur.setReponse(resultSet.getString("reponse"));
		collaborateur.setActivites(resultSet.getString("activites"));
		collaborateur.setTaux(resultSet.getInt("taux"));
		Long id = resultSet.getLong("id_superieur_hierarchique");
		if (id != resultSet.getLong("id_collaborateur") && id != 0L) {

			Utilisateur superviseur = retrouver(new Utilisateur(id));
			collaborateur.setSuperviseur(superviseur);
		}

		FonctionManager fonction = factory.getFonctionManager();
		Fonction met = new Fonction(resultSet.getLong("id_fonction"));
		met = fonction.retrouver(met);
		if (met != null) {
			collaborateur.setFonction(met);
			collaborateur.setService(met.getService());
		}

		AdresseManager adresse = factory.getAdresseManager();

		collaborateur.setAdresse(adresse.retrouver(new Adresse(resultSet.getLong("id_adresse"))));

		return collaborateur;
	}

	private static String hasher(String s) {
		byte[] hash = null;

		MessageDigest sha;
		try {
			sha = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new DatabaseException(e);
		}

		hash = sha.digest(s.getBytes());

		String m = null;
		try {
			m = new String(hash, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new DatabaseException(e);
		}
		return m;

	}

	@Override
	public void modifierMdp(Utilisateur collaborateur) throws DatabaseException {
		modifier(SQL_UPDATE_MDP, hasher(collaborateur.getMdp()), collaborateur.getId());

	}

	@Override
	public void modifierChallenge(Utilisateur collaborateur) throws DatabaseException {
		modifier(SQL_UPDATE_CHALLENGE, collaborateur.getQuestion(), hasher(collaborateur.getReponse()),
				collaborateur.getId());
	}

	@Override
	public void modifierActif(Utilisateur collaborateur) throws DatabaseException {
		modifier(SQL_UPDATE_ACTIF, collaborateur.isActif(), collaborateur.getId());
	}

	@Override
	public void modifier(Utilisateur collaborateur) throws DatabaseException {

		Long idSuperviseur = null;
		Long idAdresse = null;
		Long idFonction = null;
		String statut = null;

		Timestamp dateAnniversaire = null;
		if (collaborateur.getDateAnniversaire() != null)
			dateAnniversaire = new Timestamp(collaborateur.getDateAnniversaire().getMillis());

		Timestamp dateDebut = null;
		if (collaborateur.getDateDebutService() != null)
			dateDebut = new Timestamp(collaborateur.getDateDebutService().getMillis());
		Timestamp dateFin = null;
		if (collaborateur.getDateFinService() != null)
			dateFin = new Timestamp(collaborateur.getDateFinService().getMillis());

		Timestamp dateFinEff = null;
		if (collaborateur.getDateFinEffective() != null)
			dateFinEff = new Timestamp(collaborateur.getDateFinEffective().getMillis());
		if (collaborateur.getSuperviseur() != null)
			idSuperviseur = collaborateur.getSuperviseur().getId();
		if (collaborateur.getAdresse() != null)
			idAdresse = collaborateur.getAdresse().getId();
		if (collaborateur.getFonction() != null)
			idFonction = collaborateur.getFonction().getId();
		if (collaborateur.getStatut() != null)
			statut = collaborateur.getStatut().toString();

		modifier(SQL_UPDATE, collaborateur.getNom(), collaborateur.getPrenom(), collaborateur.getInitiales(),
				collaborateur.getSexe(), statut, collaborateur.getEmail(), collaborateur.getTelInterne(),
				collaborateur.getTelPrive(), collaborateur.getImage(), collaborateur.isAdmin(),
				collaborateur.getActivites(), dateAnniversaire, dateDebut, dateFin, dateFinEff, collaborateur.getTaux(),
				idSuperviseur, idAdresse, idFonction, collaborateur.getId());

	}

	public ArrayList<Utilisateur> lectureOLY(String source) {
		ArrayList<Utilisateur> nouvelleListe = new ArrayList<Utilisateur>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		try {
			int i = 0;
			int nbColonne = 6;
			String ligne;
			String courant = "";

			BufferedReader fichier = new BufferedReader(new FileReader(source));

			while ((ligne = fichier.readLine()) != null) {
				ArrayList<String> liste = new ArrayList<String>();
				for (i = 0; i < nbColonne; i++) {

					courant = ligne.substring(0, ligne.indexOf(';'));
					liste.add(courant);
					ligne = ligne.substring(ligne.indexOf(';') + 1, ligne.length());

				}
				liste.add(ligne);
				try {

					Utilisateur collaborateur = new Utilisateur();
					collaborateur.setInitiales(liste.get(3));

					if (liste.get(2).contains(" ")) {
						String nomPrenom = liste.get(2).substring(liste.get(2).indexOf(' ') + 1, liste.get(2).length());
						if (nomPrenom.contains(" ")) {
							collaborateur.setNom(nomPrenom.substring(0, nomPrenom.indexOf(' ')));
							collaborateur
									.setPrenom(nomPrenom.substring(nomPrenom.indexOf(' ') + 1, nomPrenom.length()));
						}
					}

					Date d2 = null;

					d2 = sdf.parse(liste.get(6).replace('.', '/'));
					long diff = new Date().getTime() - d2.getTime();
					long diffDays = diff / (24 * 60 * 60 * 1000);

					if (diffDays > 90L) {
						collaborateur = retrouver(collaborateur);

						if (collaborateur != null) {
							nouvelleListe.add(collaborateur);
						}
					}

				} catch (ParseException e) {

					System.out.println("Erreur impr�vue lors de la cr�ation.");
					e.printStackTrace();
				}

			}
			fichier.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collections.sort(nouvelleListe, new Comparator<Utilisateur>() {
			public int compare(Utilisateur o1, Utilisateur o2) {
				if (o2.getSuperviseur() == null) {

					if (o1.getSuperviseur() != null)
						return 1;
					else
						return o1

								.getNom().compareToIgnoreCase(o2.getNom());
				} else {
					if (o1.getSuperviseur() == null)
						return -1;
					else

						return o1.getSuperviseur().getNom().compareToIgnoreCase(o2.getSuperviseur().getNom());
				}

			}
		});

		return nouvelleListe;

	}

	@Override
	public Utilisateur associerApplication(Utilisateur collaborateur, Application application)
			throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		String sql = "INSERT INTO collaborateur_application (id_collaborateur,id_application) VALUES(?,?)";
		if (collaborateur != null && application != null) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, sql, true, collaborateur.getId(),
						application.getId());
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException("�chec de la cr�ation du lien, aucune ligne ajout�e dans la table.");
				}

				else {
					collaborateur = consulterApplicationsCollaborateur(collaborateur);

				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		}
		return collaborateur;
	}

	@Override
	public Utilisateur associerBeamer(Utilisateur collaborateur, Beamer beamer) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		String sql = "INSERT INTO collaborateur_beamer (id_collaborateur,id_beamer) VALUES(?,?)";
		if (collaborateur != null && beamer != null) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, sql, true, collaborateur.getId(),
						beamer.getId());
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException("�chec de la cr�ation du lien, aucune ligne ajout�e dans la table.");
				}

				else {
					collaborateur = consulterBeamersCollaborateur(collaborateur);

				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		}
		return collaborateur;
	}

	@Override
	public Utilisateur associerComite(Utilisateur collaborateur, Localisation comite) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		String sql = "INSERT INTO collaborateur_comite (id_collaborateur,id_comite) VALUES(?,?)";
		if (collaborateur != null && comite != null) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, sql, true, collaborateur.getId(),
						comite.getId());
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException("�chec de la cr�ation du lien, aucune ligne ajout�e dans la table.");
				}

				else {
					collaborateur = consulterComitesCollaborateur(collaborateur);

				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		}
		return collaborateur;
	}

	@Override
	public Utilisateur associerDistribution(Utilisateur collaborateur, Distribution distribution)
			throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		String sql = "INSERT INTO collaborateur_distribution (id_collaborateur,id_distribution) VALUES(?,?)";
		if (collaborateur != null && distribution != null) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, sql, true, collaborateur.getId(),
						distribution.getId());
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException("�chec de la cr�ation du lien, aucune ligne ajout�e dans la table.");
				}

				else {
					collaborateur = consulterDistributionsCollaborateur(collaborateur);

				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		}
		return collaborateur;
	}

	@Override
	public Utilisateur associerImprimante(Utilisateur collaborateur, Imprimante imprimante)
			throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		String sql = "INSERT INTO collaborateur_imprimante (id_collaborateur,id_imprimante) VALUES(?,?)";
		if (collaborateur != null && imprimante != null) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, sql, true, collaborateur.getId(),
						imprimante.getId());
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException("�chec de la cr�ation du lien, aucune ligne ajout�e dans la table.");
				}

				else {
					collaborateur = consulterImprimantesCollaborateur(collaborateur);

				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		}
		return collaborateur;
	}

	@Override
	public Utilisateur associerSeance(Utilisateur collaborateur, Seance seance) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		String sql = "INSERT INTO collaborateur_seance (id_collaborateur,id_seance) VALUES(?,?)";
		if (collaborateur != null && seance != null) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, sql, true, collaborateur.getId(),
						seance.getId());
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException("�chec de la cr�ation du lien, aucune ligne ajout�e dans la table.");
				}

				else {
					collaborateur = consulterSeancesCollaborateur(collaborateur);

				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		}
		return collaborateur;
	}

	@Override
	public Utilisateur associerService(Utilisateur collaborateur, Service service) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		String sql = "INSERT INTO collaborateur_service (id_collaborateur,id_service) VALUES(?,?)";
		if (collaborateur != null && service != null) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, sql, true, collaborateur.getId(),
						service.getId());
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException("�chec de la cr�ation du lien, aucune ligne ajout�e dans la table.");
				}

				else {
					collaborateur = consulterServicesCollaborateur(collaborateur);

				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		}
		return collaborateur;
	}

	@Override
	public Utilisateur associerSuperieur(Utilisateur collaborateur, Utilisateur superieur)
			throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		String sql = "INSERT INTO collaborateur_superieur (id_collaborateur,id_superieur) VALUES(?,?)";
		if (collaborateur != null && superieur != null) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, sql, true, collaborateur.getId(),
						superieur.getId());
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException("�chec de la cr�ation du lien, aucune ligne ajout�e dans la table.");
				}

				else {
					collaborateur = consulterSuperieursCollaborateur(collaborateur);

				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		}
		return collaborateur;
	}

	@Override
	public Utilisateur associerSuppleant(Utilisateur collaborateur, Utilisateur suppleant)
			throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		String sql = "INSERT INTO collaborateur_suppleant (id_collaborateur,id_suppleant) VALUES(?,?)";
		if (collaborateur != null && suppleant != null) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, sql, true, collaborateur.getId(),
						suppleant.getId());
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException("�chec de la cr�ation du lien, aucune ligne ajout�e dans la table.");
				}

				else {
					collaborateur = consulterSuppleantsCollaborateur(collaborateur);

				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		}
		return collaborateur;
	}

	@Override
	public Utilisateur associerSupplee(Utilisateur collaborateur, Utilisateur supplee) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		String sql = "INSERT INTO collaborateur_supplee (id_collaborateur,id_supplee) VALUES(?,?)";
		if (collaborateur != null && supplee != null) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, sql, true, collaborateur.getId(),
						supplee.getId());
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException("�chec de la cr�ation du lien, aucune ligne ajout�e dans la table.");
				}

				else {
					collaborateur = consulterSuppleesCollaborateur(collaborateur);

				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		}
		return collaborateur;
	}

	private Utilisateur consulterApplicationsCollaborateur(Utilisateur collaborateur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Application> uneListe = new ArrayList<Application>();
		String sql = "SELECT DISTINCT * FROM application A WHERE id_application IN (SELECT id_application FROM collaborateur_application WHERE collaborateur_application.id_collaborateur=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, collaborateur.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				uneListe.add(new Application(resultSet.getString("nom")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		collaborateur.setApplications(uneListe);
		return collaborateur;
	}

	private Utilisateur consulterBeamersCollaborateur(Utilisateur collaborateur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Beamer> uneListe = new ArrayList<Beamer>();
		String sql = "SELECT DISTINCT * FROM beamer A WHERE id_beamer IN (SELECT id_beamer FROM collaborateur_beamer WHERE collaborateur_beamer.id_collaborateur=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, collaborateur.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				uneListe.add(new Beamer(resultSet.getString("numero")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		collaborateur.setBeamers(uneListe);
		return collaborateur;
	}

	private Utilisateur consulterComitesCollaborateur(Utilisateur collaborateur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Localisation> uneListe = new ArrayList<Localisation>();
		String sql = "SELECT DISTINCT * FROM comite A WHERE id_comite IN (SELECT id_comite FROM collaborateur_comite WHERE collaborateur_comite.id_collaborateur=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, collaborateur.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				uneListe.add(new Localisation(resultSet.getString("acronyme")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		collaborateur.setComites(uneListe);
		return collaborateur;
	}

	private Utilisateur consulterDistributionsCollaborateur(Utilisateur collaborateur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Distribution> uneListe = new ArrayList<Distribution>();
		String sql = "SELECT DISTINCT * FROM distribution A WHERE id_distribution IN (SELECT id_distribution FROM collaborateur_distribution WHERE collaborateur_distribution.id_collaborateur=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, collaborateur.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				uneListe.add(new Distribution(resultSet.getString("nom")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		collaborateur.setDistributions(uneListe);
		return collaborateur;
	}

	private Utilisateur consulterImprimantesCollaborateur(Utilisateur collaborateur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Imprimante> uneListe = new ArrayList<Imprimante>();
		String sql = "SELECT DISTINCT * FROM imprimante A WHERE id_imprimante IN (SELECT id_imprimante FROM collaborateur_imprimante WHERE collaborateur_imprimante.id_collaborateur=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, collaborateur.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				uneListe.add(new Imprimante(resultSet.getString("numero")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		collaborateur.setImprimantes(uneListe);
		return collaborateur;
	}

	private Utilisateur consulterSeancesCollaborateur(Utilisateur collaborateur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Seance> uneListe = new ArrayList<Seance>();
		String sql = "SELECT DISTINCT * FROM seance A WHERE id_seance IN (SELECT id_seance FROM collaborateur_seance WHERE collaborateur_seance.id_collaborateur=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, collaborateur.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				uneListe.add(new Seance(resultSet.getString("libelle")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		collaborateur.setSeances(uneListe);
		return collaborateur;
	}

	private Utilisateur consulterServicesCollaborateur(Utilisateur collaborateur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Service> uneListe = new ArrayList<Service>();
		String sql = "SELECT DISTINCT * FROM service A WHERE id_service IN (SELECT id_service FROM collaborateur_service WHERE collaborateur_service.id_collaborateur=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, collaborateur.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				uneListe.add(new Service(resultSet.getString("acronyme")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		collaborateur.setServices(uneListe);
		return collaborateur;
	}

	private Utilisateur consulterSuppleesCollaborateur(Utilisateur collaborateur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Utilisateur> uneListe = new ArrayList<Utilisateur>();
		String sql = "SELECT DISTINCT * FROM collaborateur A WHERE id_collaborateur IN (SELECT id_supplee FROM collaborateur_supplee WHERE collaborateur_supplee.id_collaborateur=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, collaborateur.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				uneListe.add(new Utilisateur(resultSet.getString("initiales"), resultSet.getString("prenom"),
						resultSet.getString("nom")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		collaborateur.setSupplees(uneListe);
		return collaborateur;
	}

	private Utilisateur consulterSuppleantsCollaborateur(Utilisateur collaborateur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Utilisateur> uneListe = new ArrayList<Utilisateur>();
		String sql = "SELECT DISTINCT * FROM collaborateur A WHERE id_collaborateur IN (SELECT id_suppleant FROM collaborateur_suppleant WHERE collaborateur_suppleant.id_collaborateur=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, collaborateur.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				uneListe.add(new Utilisateur(resultSet.getString("initiales"), resultSet.getString("prenom"),
						resultSet.getString("nom")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		collaborateur.setSuppleants(uneListe);
		return collaborateur;
	}

	private Utilisateur consulterSuperieursCollaborateur(Utilisateur collaborateur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Utilisateur> uneListe = new ArrayList<Utilisateur>();
		String sql = "SELECT DISTINCT * FROM collaborateur A WHERE id_collaborateur IN (SELECT id_superieur FROM collaborateur_superieur WHERE collaborateur_superieur.id_collaborateur=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, collaborateur.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				uneListe.add(new Utilisateur(resultSet.getString("initiales"), resultSet.getString("prenom"),
						resultSet.getString("nom")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		collaborateur.setSuperieurs(uneListe);
		return collaborateur;
	}

	private Utilisateur consulterHorairesCollaborateur(Utilisateur collaborateur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Horaire> uneListe = new ArrayList<Horaire>();
		String sql = "SELECT DISTINCT * FROM horaire A WHERE id_horaire IN (SELECT id_horaire FROM collaborateur_horaire WHERE collaborateur_horaire.id_collaborateur=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, collaborateur.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				uneListe.add(new Horaire(resultSet.getString("nom")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		collaborateur.setHoraires(uneListe);
		return collaborateur;
	}

}
