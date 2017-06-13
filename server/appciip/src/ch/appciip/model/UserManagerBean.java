package ch.appciip.model;

import static ch.appciip.model.Utility.fermeturesSilencieuses;
import static ch.appciip.model.Utility.initialisationRequetePreparee;

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

import ch.appciip.bean.Address;
import ch.appciip.bean.User;

public class UserManagerBean implements UserManager {

	private static final String SQL_SELECT_PAR_INITIALES = "SELECT * FROM user WHERE initiales =?";
	private static final String SQL_SELECT = "SELECT * FROM user";
	private static final String SQL_ANNIVERSAIRE = "SELECT * FROM user WHERE MONTH(date_anniversaire)=MONTH(CURRENT_TIMESTAMP) AND DAY(date_anniversaire)=DAY(CURRENT_TIMESTAMP)";
	private static final String SQL_PROCHAIN_ANNIVERSAIRE = "SELECT * FROM user WHERE (MONTH(date_anniversaire)=MONTH(CURRENT_TIMESTAMP) AND DAY(date_anniversaire)>DAY(CURRENT_TIMESTAMP)) OR (MONTH(date_anniversaire)=MONTH(CURRENT_TIMESTAMP+1) AND DAY(date_anniversaire)=DAY(CURRENT_TIMESTAMP+24))";

	private static final String SQL_SELECT_PAR_INITIALES_MDP = "SELECT * FROM user WHERE initiales =? AND mdp =?";
	private static final String SQL_SELECT_PAR_INITIALES_REPONSE = "SELECT * FROM user WHERE initiales =? AND reponse LIKE ?";
	private static final String SQL_SELECT_PAR_NOM_PRENOM = "SELECT * FROM user WHERE nom =? AND prenom LIKE ?";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM user WHERE id_user = ?";
	private static final String SQL_INSERT = "INSERT INTO user (nom,prenom,initiales,mdp,sexe,statut,email,tel_interne,tel_prive,image,admin,activites,date_anniversaire,date_debut_service,date_fin_service,date_fin_effective,taux,id_superieur_hierarchique, id_adresse, id_fonction) VALUES(UPPER(?),?,UPPER(?),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String SQL_UPDATE = "UPDATE user SET nom=UPPER(?),prenom=?,initiales=UPPER(?),sexe=?,statut=?,email=?,tel_interne=?,tel_prive=?,image=?,admin=?,activites=?,date_anniversaire=?,date_debut_service=?,date_fin_service=?,date_fin_effective=?,  taux=?,id_superieur_hierarchique=?, id_adresse=?, id_fonction=? WHERE id_user =?";
	private static final String SQL_DELETE_PAR_ID = "DELETE FROM User WHERE id_user = ?";
	private static final String SQL_UPDATE_MDP = "UPDATE user SET mdp =? WHERE id_user =?";
	private static final String SQL_UPDATE_CHALLENGE = "UPDATE user SET question =?, reponse =? WHERE id_user =?";
	private static final String SQL_UPDATE_ACTIF = "UPDATE user SET active =? WHERE id_user =?";
	private Factory factory;

	UserManagerBean(Factory factory) {
		this.factory = factory;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface UserManager
	 */
	@Override
	public User connect(User user) throws DatabaseException {

		return read(SQL_SELECT_PAR_INITIALES_MDP, user.getInitiales(), hasher(user.getMdp()));
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface UserManager
	 */
	@Override
	public User read(User user) throws DatabaseException {
		if (user.getId() != null && user.getId() != 0L)
			return read(SQL_SELECT_PAR_ID, user.getId());
		else if (user.getInitiales() != null) {
			// System.out.println(user.getForename());
			// System.out.println(user.getName());
			// System.out.println("-----------LOG--------");
			return read(SQL_SELECT_PAR_INITIALES, user.getInitiales());
		} else {
			// System.out.println(user.getForename());
			// System.out.println(user.getName());
			// System.out.println("--------------------");
			return read(SQL_SELECT_PAR_NOM_PRENOM, user.getName(), user.getForename());
		}
	}

	@Override
	public User challenge(User user) throws DatabaseException {

		return read(SQL_SELECT_PAR_INITIALES_REPONSE, user.getInitiales(), hasher(user.getReponse()));
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface UserManager
	 */

	@Override
	public User create(User user) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		Timestamp dateAnniversaire = null;
		if (user.getDateAnniversaire() != null)
			dateAnniversaire = new Timestamp(user.getDateAnniversaire().getMillis());

		Timestamp dateDebut = null;
		if (user.getDateDebutService() != null)
			dateDebut = new Timestamp(user.getDateDebutService().getMillis());
		Timestamp dateFin = null;
		if (user.getDateFinService() != null)
			dateFin = new Timestamp(user.getDateFinService().getMillis());

		Timestamp dateFinEff = null;
		if (user.getDateFinEffective() != null)
			dateFinEff = new Timestamp(user.getDateFinEffective().getMillis());

		Long idSuperviseur = null;
		Long idAddress = null;
		Long idFonction = null;
		String statut = null;
		if (user.getSuperviseur() != null)
			idSuperviseur = user.getSuperviseur().getId();
		if (user.getAddress() != null)
			idAddress = user.getAddress().getId();

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, user.getName(),
					user.getForename(), user.getInitiales(), hasher(user.getMdp()), user.getSexe(), statut,
					user.getEmail(), user.getTelInterne(), user.getTelPrive(), user.getImage(), user.isAdmin(),
					user.getActivites(), dateAnniversaire, dateDebut, dateFin, dateFinEff, user.getTaux(),
					idSuperviseur, idAddress, idFonction);
			int status = preparedStatement.executeUpdate();
			if (status == 0) {
				throw new DatabaseException(
						"�chec de la cr�ation de l'user, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {
				/*
				 * ManifestationManager evenement =
				 * factory.getManifestationManager();
				 * 
				 * user = read(new User((valeursAutoGenerees.getLong(1))));
				 * evenement.create(new Manifestation("Arrivée du user",
				 * "Bienvenue", new DateTime(new Date()), user));
				 */

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation de l'user en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return user;
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un user depuis la base
	 * de donn�es, correspondant � la requ�te SQL donn�e prenant en
	 * param�tres les objets pass�s en argument.
	 */
	private User read(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		User user = null;

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();

			/*
			 * Pr�paration de la requ�te avec les objets pass�s en
			 * arguments et ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, objets);
			resultSet = preparedStatement.executeQuery();
			/*
			 * Parcours de la ligne de donn�es retourn�e dans le ResultSet
			 */
			if (resultSet.next()) {
				user = map(resultSet);
				// user = consulterProfilsUser(user);
				// user = consulterManifestationsUser(user);

			}
		} catch (SQLException e) {

			throw new DatabaseException(e);
		} finally {

			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return user;
	}
	/*
	 * private User consulterProfilsUser(User user) throws DatabaseException {
	 * Connection connexion = null; PreparedStatement preparedStatement = null;
	 * ResultSet resultSet = null;
	 * 
	 * ArrayList<Profil> uneListe = new ArrayList<Profil>(); String sql =
	 * "SELECT profil.identifiant AS identifiant FROM fonction" +
	 * " INNER JOIN fonction_profil" +
	 * " ON fonction.id_fonction = fonction_profil.id_fonction" +
	 * " INNER JOIN profil" + " ON fonction_profil.id_profil = profil.id_profil"
	 * + " INNER JOIN user" + " ON user.id_fonction = fonction.id_fonction" +
	 * " WHERE user.id_user=?";
	 * 
	 * try {
	 * 
	 * connexion = factory.getConnection();
	 * 
	 * preparedStatement = initialisationRequetePreparee(connexion, sql, false,
	 * user.getId()); resultSet = preparedStatement.executeQuery();
	 * 
	 * while (resultSet.next()) { uneListe.add(new
	 * Profil(resultSet.getString("identifiant"))); } } catch (SQLException e) {
	 * throw new DatabaseException(e); } finally {
	 * fermeturesSilencieuses(resultSet, preparedStatement, connexion); }
	 * user.setProfils(uneListe); return user; }
	 * 
	 */

	private void update(String sql, Object... objets) throws DatabaseException {
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
	 * Impl�mentation de la m�thode d�finie dans l'interface UserManager
	 */
	@Override
	public void delete(User user) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		String sql = "UPDATE user  SET id_superieur_hierarchique =? WHERE id_user = ?";
		if (user.getSuperviseur() != null) {
			Long idSuperviseur = user.getSuperviseur().getId();
			List<User> listeUsers = list(user);

			for (User u : listeUsers) {
				update(sql, idSuperviseur, u.getId());

			}
		}

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, user.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression de l'user, aucune ligne supprim�e de la table.");
			} else {

				user.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface UserManager
	 */
	@Override
	public List<User> list() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<User> users = new ArrayList<User>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				User user = map(resultSet);

				users.add(user);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return users;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface UserManager
	 */
	@Override
	public List<User> anniversaire() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<User> users = new ArrayList<User>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_ANNIVERSAIRE);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				User user = map(resultSet);
				// System.out.println(user.getDateAnniversaire());
				// user = consulterProfilsUser(user);
				// user = consulterManifestationsUser(user);
				users.add(user);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return users;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface UserManager
	 */
	@Override
	public List<User> prochainAnniversaire() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<User> users = new ArrayList<User>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_PROCHAIN_ANNIVERSAIRE);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				User user = map(resultSet);

				// user = consulterProfilsUser(user);
				// user = consulterManifestationsUser(user);
				users.add(user);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return users;
	}

	@Override
	public List<User> list(User user) throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<User> users = new ArrayList<User>();
		String sql = "SELECT * FROM user WHERE id_superieur_hierarchique = " + user.getId()
				+ " AND id_user <> id_superieur_hierarchique";

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				User u = map(resultSet);
				// u = consulterProfilsUser(u);
				// u = consulterManifestationsUser(u);
				users.add(u);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return users;
	}

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des users (un ResultSet) et un
	 * bean User.
	 */
	private User map(ResultSet resultSet) throws SQLException {
		User user = new User();
		Timestamp dateDebut = resultSet.getTimestamp("date_debut_service");
		if (dateDebut != null)
			user.setDateDebutService(new DateTime(dateDebut));

		Timestamp dateFin = resultSet.getTimestamp("date_fin_service");
		if (dateFin != null)
			user.setDateFinService(new DateTime(dateFin));

		Timestamp dateFinEff = resultSet.getTimestamp("date_fin_effective");
		if (dateFinEff != null)
			user.setDateFinEffective(new DateTime(dateFinEff));

		Timestamp dateAnniversaire = resultSet.getTimestamp("date_anniversaire");

		if (dateAnniversaire != null)
			user.setDateAnniversaire((new DateTime(dateAnniversaire)).plusHours(1));

		user.setId(resultSet.getLong("id_user"));
		user.setEmail(resultSet.getString("email"));
		user.setInitiales(resultSet.getString("initiales"));
		user.setMdp(resultSet.getString("mdp"));
		user.setSexe(resultSet.getString("sexe"));
		user.setName(resultSet.getString("nom"));
		user.setForename(resultSet.getString("prenom"));
		user.setTelInterne(resultSet.getString("tel_interne"));
		user.setTelPrive(resultSet.getString("tel_prive"));
		user.setActive(resultSet.getBoolean("active"));
		user.setAdmin(resultSet.getBoolean("admin"));
		// user.setStatut(Statut.fromString(resultSet.getString("statut")));
		user.setImage(resultSet.getString("image"));
		user.setQuestion(resultSet.getString("question"));
		user.setReponse(resultSet.getString("reponse"));
		user.setActivites(resultSet.getString("activites"));
		user.setTaux(resultSet.getInt("taux"));
		Long id = resultSet.getLong("id_superieur_hierarchique");
		if (id != resultSet.getLong("id_user") && id != 0L) {

			User superviseur = read(new User(id));
			user.setSuperviseur(superviseur);
		}

		AddressManager adresse = factory.getAddressManager();

		user.setAddress(adresse.read(new Address(resultSet.getLong("id_adresse"))));

		return user;
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
	public void updateMdp(User user) throws DatabaseException {
		update(SQL_UPDATE_MDP, hasher(user.getMdp()), user.getId());

	}

	@Override
	public void updateChallenge(User user) throws DatabaseException {
		update(SQL_UPDATE_CHALLENGE, user.getQuestion(), hasher(user.getReponse()), user.getId());
	}

	@Override
	public void updateActive(User user) throws DatabaseException {
		update(SQL_UPDATE_ACTIF, user.isActive(), user.getId());
	}

	@Override
	public void update(User user) throws DatabaseException {

		Long idSuperviseur = null;
		Long idAddress = null;
		Long idFonction = null;
		String statut = null;

		Timestamp dateAnniversaire = null;
		if (user.getDateAnniversaire() != null)
			dateAnniversaire = new Timestamp(user.getDateAnniversaire().getMillis());

		Timestamp dateDebut = null;
		if (user.getDateDebutService() != null)
			dateDebut = new Timestamp(user.getDateDebutService().getMillis());
		Timestamp dateFin = null;
		if (user.getDateFinService() != null)
			dateFin = new Timestamp(user.getDateFinService().getMillis());

		Timestamp dateFinEff = null;
		if (user.getDateFinEffective() != null)
			dateFinEff = new Timestamp(user.getDateFinEffective().getMillis());
		if (user.getSuperviseur() != null)
			idSuperviseur = user.getSuperviseur().getId();
		if (user.getAddress() != null)
			idAddress = user.getAddress().getId();

		update(SQL_UPDATE, user.getName(), user.getForename(), user.getInitiales(), user.getSexe(), statut,
				user.getEmail(), user.getTelInterne(), user.getTelPrive(), user.getImage(), user.isAdmin(),
				user.getActivites(), dateAnniversaire, dateDebut, dateFin, dateFinEff, user.getTaux(), idSuperviseur,
				idAddress, idFonction, user.getId());

	}

	public ArrayList<User> read(String source) {
		ArrayList<User> nouvelleListe = new ArrayList<User>();
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

					User user = new User();
					user.setInitiales(liste.get(3));

					if (liste.get(2).contains(" ")) {
						String nomForename = liste.get(2).substring(liste.get(2).indexOf(' ') + 1,
								liste.get(2).length());
						if (nomForename.contains(" ")) {
							user.setName(nomForename.substring(0, nomForename.indexOf(' ')));
							user.setForename(nomForename.substring(nomForename.indexOf(' ') + 1, nomForename.length()));
						}
					}

					Date d2 = null;

					d2 = sdf.parse(liste.get(6).replace('.', '/'));
					long diff = new Date().getTime() - d2.getTime();
					long diffDays = diff / (24 * 60 * 60 * 1000);

					if (diffDays > 90L) {
						user = read(user);

						if (user != null) {
							nouvelleListe.add(user);
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
		Collections.sort(nouvelleListe, new Comparator<User>() {
			public int compare(User o1, User o2) {
				if (o2.getSuperviseur() == null) {

					if (o1.getSuperviseur() != null)
						return 1;
					else
						return o1

								.getName().compareToIgnoreCase(o2.getName());
				} else {
					if (o1.getSuperviseur() == null)
						return -1;
					else

						return o1.getSuperviseur().getName().compareToIgnoreCase(o2.getSuperviseur().getName());
				}

			}
		});

		return nouvelleListe;

	}

}
