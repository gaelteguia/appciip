package ch.appciip.form;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.appciip.bean.Address;
import ch.appciip.bean.Institution;
import ch.appciip.bean.Manifestation;
import ch.appciip.bean.User;
import ch.appciip.bean.Weather;
import ch.appciip.model.AddressManager;
import ch.appciip.model.DatabaseException;
import ch.appciip.model.ManifestationManager;
import ch.appciip.model.UserManager;

@SuppressWarnings("unchecked")
public class ManifestationForm extends Form {

	public ManifestationForm(UserManager u, AddressManager a, ManifestationManager m) {
		this.u = u;
		this.a = a;

		this.m = m;

	}

	public ManifestationForm(ManifestationManager m) {

		this.m = m;

	}

	public Manifestation readManifestation(HttpServletRequest request) {
		Manifestation manifestation = new Manifestation();
		/* Rï¿½cupï¿½ration de l'id de la manifestation choisie */
		String idManifestation = getValueField(request, FIELD_LIST_MANIFESTATIONS);
		Long id = null;
		try {
			id = Long.parseLong(idManifestation);
		} catch (NumberFormatException e) {
			setErreur(FIELD_CHOICE_MANIFESTATION,
					"Manifestation inconnue, merci d'utiliser le formulaire prï¿½vu ï¿½ cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * Rï¿½cupï¿½ration de l'objet manifestation correspondant dans la
			 * session
			 */
			HttpSession session = request.getSession();
			manifestation = ((Map<Long, Manifestation>) session.getAttribute(SESSION_MANIFESTATIONS)).get(id);
		}

		return manifestation;
	}

	public Manifestation createManifestation(HttpServletRequest request) {
		User user;

		/*
		 * Si l'user choisit un user dï¿½jï¿½ existant, pas de validation ï¿½
		 * effectuer
		 */
		String choiceNewUser = getValueField(request, FIELD_CHOICE_USER);
		if (FORMER_USER.equals(choiceNewUser)) {
			/* Rï¿½cupï¿½ration de l'id de l'user choisi */
			String idFormerUser = getValueField(request, FIELD_LIST_USERS);
			Long id = null;
			try {
				id = Long.parseLong(idFormerUser);
			} catch (NumberFormatException e) {
				setErreur(FIELD_CHOICE_USER, "User inconnu, merci d'utiliser le formulaire prï¿½vu ï¿½ cet effet.");
				id = 0L;
			}
			/*
			 * Rï¿½cupï¿½ration de l'objet user correspondant dans la session
			 */
			HttpSession session = request.getSession();
			user = ((Map<Long, User>) session.getAttribute(SESSION_USERS)).get(id);
		} else {

			user = new User();
		}

		Address address;

		/*
		 * Si l'address choisit un address dï¿½jï¿½ existant, pas de validation
		 * ï¿½ effectuer
		 */
		String choiceNewAddress = getValueField(request, FIELD_CHOICE_ADDRESS);
		if (FORMER_ADDRESS.equals(choiceNewAddress)) {
			/* Rï¿½cupï¿½ration de l'id de l'address choisi */
			String idFormerAddress = getValueField(request, FIELD_LIST_ADDRESSES);
			Long id = null;
			try {
				id = Long.parseLong(idFormerAddress);
			} catch (NumberFormatException e) {
				setErreur(FIELD_CHOICE_ADDRESS,
						"Address inconnu, merci d'utiliser le formulaire prï¿½vu ï¿½ cet effet.");
				id = 0L;
			}
			/*
			 * Rï¿½cupï¿½ration de l'objet address correspondant dans la session
			 */
			HttpSession session = request.getSession();
			address = ((Map<Long, Address>) session.getAttribute(SESSION_ADDRESSES)).get(id);
		} else {

			address = new Address();
		}
		Institution institution;

		/*
		 * Si l'institution choisit un institution dï¿½jï¿½ existant, pas de
		 * validation ï¿½ effectuer
		 */
		String choiceNewInstitution = getValueField(request, FIELD_CHOICE_INSTITUTION);
		if (FORMER_INSTITUTION.equals(choiceNewInstitution)) {
			/* Rï¿½cupï¿½ration de l'id de l'institution choisi */
			String idFormerInstitution = getValueField(request, FIELD_LIST_INSTITUTIONS);
			Long id = null;
			try {
				id = Long.parseLong(idFormerInstitution);
			} catch (NumberFormatException e) {
				setErreur(FIELD_CHOICE_INSTITUTION,
						"Institution inconnu, merci d'utiliser le formulaire prï¿½vu ï¿½ cet effet.");
				id = 0L;
			}
			/*
			 * Rï¿½cupï¿½ration de l'objet institution correspondant dans la
			 * session
			 */
			HttpSession session = request.getSession();
			institution = ((Map<Long, Institution>) session.getAttribute(SESSION_INSTITUTIONS)).get(id);
		} else {

			institution = new Institution();
		}
		/*
		 * Ensuite, il suffit de procï¿½der normalement avec le reste des champs
		 * spï¿½cifiques ï¿½ une manifestatioma.
		 */

		String title = getValueField(request, FIELD_TITLE);
		String description = getValueField(request, FIELD_DESCRIPTION);
		String startTime = getValueField(request, FIELD_START_TIME);
		String endTime = getValueField(request, FIELD_END_TIME);

		Manifestation manifestation = new Manifestation();

		try {
			processUser(manifestation, user);

			processTitle(manifestation, title);
			processDescription(manifestation, description);

			processStartTime(manifestation, startTime);
			processEndTime(manifestation, endTime);

			processAddress(manifestation, address);

			processInstitution(manifestation, institution);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					manifestation = m.create(manifestation);
					setResultat("Succï¿½s de la crï¿½ation de la manifestatioma.");
				} else {
					setResultat("ï¿½chec de la crï¿½ation de la manifestatioma.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("imprï¿½vu", "Erreur imprï¿½vue lors de la crï¿½atioma.");
			setResultat(
					"ï¿½chec de la crï¿½ation de la manifestation : une erreur imprï¿½vue est survenue, merci de rï¿½essayer dans quelques instants.");
			e.printStackTrace();
		}

		return manifestation;
	}

	public Manifestation createManifestation(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String title = tokens[0];

		String description = tokens[1];

		String startTime = tokens[2];

		// Address adresse = new Address(tokens[3]);

		// adresse.setVille(tokens[6]);
		// adresse.setPays(tokens[7]);

		// User collaborateur = new User(tokens[8],
		// tokens[9]);

		// collaborateur = u.read(collaborateur);

		Manifestation manifestation = new Manifestation();

		if (!title.trim().isEmpty()) {
			try {
				processTitle(manifestation, title);
				processStartTime(manifestation, startTime);

				// processNoRue(adresse, tokens[4]);
				// processNpa(adresse, tokens[5]);

				// processAddress(manifestation, adresse);
				// processUser(manifestation, collaborateur);

				processDescription(manifestation, description);

				if (m.read(manifestation) == null) {
					manifestation = m.create(manifestation);
					setResultat("Succï¿½s de la crï¿½ation de l'manifestatioma.");
				} else {

					manifestation = m.read(manifestation);
					// processUser(manifestation, collaborateur);
					processStartTime(manifestation, startTime);
					// processAddress(manifestation, adresse);
					processDescription(manifestation, description);
					m.update(manifestation);
					setResultat("Manifestation dÃ©jÃ  existant.");
				}

			} catch (DatabaseException e) {
				setErreur("imprï¿½vu", "Erreur imprï¿½vue lors de la crï¿½atioma.");
				setResultat(
						"ï¿½chec de la crï¿½ation de l'manifestation : une erreur imprï¿½vue est survenue, merci de rï¿½essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("ï¿½chec de la crï¿½ation de l'manifestatioma.");
		}

		return manifestation;

	}

	/*
	 * public Manifestation readManifestation(HttpServletRequest request) {
	 * 
	 * String id = getValueField(request, FIELD_ID);
	 * 
	 * Manifestation manifestation = new Manifestation();
	 * processId(manifestation, id);
	 * 
	 * try {
	 * 
	 * if (getErreurs().isEmpty()) { manifestation = m.read(manifestation);
	 * setResultat("Succï¿½s de la recherche de la manifestatioma."); } else
	 * 
	 * setResultat("ï¿½chec de la recherche de la manifestatioma. Informations invalides."
	 * ); } catch (DatabaseException e) { setErreur("imprï¿½vu",
	 * "Erreur imprï¿½vue lors de la recherche.");
	 * setResultat("ï¿½chec de la recherche."); }
	 * 
	 * return manifestation; }
	 */
	public Manifestation updateManifestation(HttpServletRequest request) {
		User user;

		/*
		 * Si l'user choisit un user dï¿½jï¿½ existant, pas de validation ï¿½
		 * effectuer
		 */
		String choiceNewUser = getValueField(request, FIELD_CHOICE_USER);
		if (FORMER_USER.equals(choiceNewUser)) {
			/* Rï¿½cupï¿½ration de l'id de l'user choisi */
			String idFormerUser = getValueField(request, FIELD_LIST_USERS);
			Long id = null;
			try {
				id = Long.parseLong(idFormerUser);
			} catch (NumberFormatException e) {
				setErreur(FIELD_CHOICE_USER, "User inconnu, merci d'utiliser le formulaire prï¿½vu ï¿½ cet effet.");
				id = 0L;
			}
			/*
			 * Rï¿½cupï¿½ration de l'objet user correspondant dans la session
			 */
			HttpSession session = request.getSession();
			user = ((Map<Long, User>) session.getAttribute(SESSION_USERS)).get(id);
		} else {

			user = new User();
		}

		Address address;

		/*
		 * Si l'address choisit un address dï¿½jï¿½ existant, pas de validation
		 * ï¿½ effectuer
		 */
		String choiceNewAddress = getValueField(request, FIELD_CHOICE_ADDRESS);
		if (FORMER_ADDRESS.equals(choiceNewAddress)) {
			/* Rï¿½cupï¿½ration de l'id de l'address choisi */
			String idFormerAddress = getValueField(request, FIELD_LIST_ADDRESSES);
			Long id = null;
			try {
				id = Long.parseLong(idFormerAddress);
			} catch (NumberFormatException e) {
				setErreur(FIELD_CHOICE_ADDRESS,
						"Address inconnu, merci d'utiliser le formulaire prï¿½vu ï¿½ cet effet.");
				id = 0L;
			}
			/*
			 * Rï¿½cupï¿½ration de l'objet address correspondant dans la session
			 */
			HttpSession session = request.getSession();
			address = ((Map<Long, Address>) session.getAttribute(SESSION_ADDRESSES)).get(id);
		} else {

			address = new Address();
		}

		/*
		 * Ensuite, il suffit de procï¿½der normalement avec le reste des champs
		 * spï¿½cifiques ï¿½ une manifestatioma.
		 */

		String title = getValueField(request, FIELD_TITLE);
		String description = getValueField(request, FIELD_DESCRIPTION);
		String startTime = getValueField(request, FIELD_START_TIME);

		String id = getValueField(request, FIELD_ID);

		Manifestation manifestation = new Manifestation();
		if (!id.trim().isEmpty())
			processId(manifestation, id);

		try {
			processUser(manifestation, user);

			processTitle(manifestation, title);
			processDescription(manifestation, description);
			processStartTime(manifestation, startTime);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					m.update(manifestation);
					setResultat("Succï¿½s de la modification de la manifestatioma.");
				} else {
					setResultat("ï¿½chec de la modification de la manifestatioma.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("imprï¿½vu", "Erreur imprï¿½vue lors de la modificatioma.");
			setResultat(
					"ï¿½chec de la modification de la manifestation : une erreur imprï¿½vue est survenue, merci de rï¿½essayer dans quelques instants.");
			e.printStackTrace();
		}

		return manifestation;
	}

	public Manifestation updateManifestation(HttpServletRequest request, String chemin) {
		// Manifestation superviseur;
		Institution institution;
		// Weather weather;
		Address address;
		/*
		 * Si l'manifestation choisit un manifestation déjà existant, pas de
		 * validation à effectuer
		 */
		String choiceNewManifestation = getValueField(request, FIELD_CHOICE_MANIFESTATION);
		if (FORMER_MANIFESTATION.equals(choiceNewManifestation)) {
			/* Récupération de l'id de l'manifestation choisi */
			String idAncienManifestation = getValueField(request, FIELD_LIST_MANIFESTATIONS);
			Long id = null;
			try {
				id = Long.parseLong(idAncienManifestation);
			} catch (NumberFormatException e) {
				setErreur(FIELD_CHOICE_MANIFESTATION,
						"Manifestation inconnu, merci d'utiliser le formulaire prévu à cet effet.");
				id = 0L;
			}
			/*
			 * Récupération de l'objet manifestation correspondant dans la
			 * session
			 */
			HttpSession session = request.getSession();
			// superviseur = ((Map<Long, Manifestation>)
			// session.getAttribute(SESSION_MANIFESTATIONS)).get(id);
		} else {

			// superviseur = new Manifestation();
		}

		/*
		 * Si l'manifestation choisit un institution déjà existant, pas de
		 * validation à effectuer
		 */
		String choiceNewInstitution = getValueField(request, FIELD_CHOICE_INSTITUTION);
		if (FORMER_INSTITUTION.equals(choiceNewInstitution)) {
			/* Récupération de l'id du institution choisi */
			String idAncienInstitution = getValueField(request, FIELD_LIST_INSTITUTIONS);
			Long id = null;
			try {
				id = Long.parseLong(idAncienInstitution);
			} catch (NumberFormatException e) {
				setErreur(FIELD_CHOICE_INSTITUTION,
						"Institution inconnu, merci d'utiliser le formulaire prévu à cet effet.");
				id = 0L;
			}
			/*
			 * Récupération de l'objet institution correspondant dans la session
			 */
			HttpSession session = request.getSession();
			institution = ((Map<Long, Institution>) session.getAttribute(SESSION_INSTITUTIONS)).get(id);
		} else {
			/*
			 * Sinon on garde l'ancien mode, pour la validation des champs.
			 * 
			 * L'objet métier pour valider la création d'un institution existe
			 * déjà, il est donc déconseillé de dupliquer ici son contenu ! À la
			 * place, il suffit de passer la requête courante à l'objet métier
			 * existant et de récupérer l'objet Institution créé.
			 */
			InstitutionForm institutionForm = new InstitutionForm(i);
			institution = institutionForm.createInstitution(request);

			/*
			 * Et très important, il ne faut pas oublier de récupérer le contenu
			 * de la map d'erreur créée par l'objet métier ManifestationForm
			 * dans la map d'erreurs courante, actuellement vide.
			 */
			erreurs = institutionForm.getErreurs();
		}

		/*
		 * Si l'manifestation choisit un weather déjà existant, pas de
		 * validation à effectuer
		 */
		String choiceNewWeather = getValueField(request, FIELD_CHOICE_WEATHER);
		if (FORMER_WEATHER.equals(choiceNewWeather)) {
			/* Récupération de l'id du weather choisi */
			String idAncienWeather = getValueField(request, FIELD_LIST_WEATHERS);
			Long id = null;
			try {
				id = Long.parseLong(idAncienWeather);
			} catch (NumberFormatException e) {
				setErreur(FIELD_CHOICE_WEATHER, "Weather inconnu, merci d'utiliser le formulaire prévu à cet effet.");
				id = 0L;
			}
			/* Récupération de l'objet weather correspondant dans la session */
			HttpSession session = request.getSession();
			// weather = ((Map<Long, Weather>)
			// session.getAttribute(SESSION_WEATHERS)).get(id);
		} else {
			/*
			 * Sinon on garde l'ancien mode, pour la validation des champs.
			 * 
			 * L'objet métier pour valider la création d'un weather existe déjà,
			 * il est donc déconseillé de dupliquer ici son contenu ! À la
			 * place, il suffit de passer la requête courante à l'objet métier
			 * existant et de récupérer l'objet Weather créé.
			 */
			// WeatherForm weatherForm = new WeatherForm(m);
			// weather = weatherForm.createWeather(request);

			/*
			 * Et très important, il ne faut pas oublier de récupérer le contenu
			 * de la map d'erreur créée par l'objet métier ManifestationForm
			 * dans la map d'erreurs courante, actuellement vide.
			 */
			// erreurs = weatherForm.getErreurs();
		}

		/*
		 * Si l'manifestation choisit une address déjà existante, pas de
		 * validation à effectuer
		 */
		String choiceNewAddress = getValueField(request, FIELD_CHOICE_ADDRESS);
		if (FORMER_ADDRESS.equals(choiceNewAddress)) {
			/* Récupération de l'id de la address choisie */
			String idAncienneAddress = getValueField(request, FIELD_LIST_ADDRESSES);
			Long id = null;
			try {
				id = Long.parseLong(idAncienneAddress);
			} catch (NumberFormatException e) {
				setErreur(FIELD_CHOICE_ADDRESS, "Address inconnue, merci d'utiliser le formulaire prévu à cet effet.");
				id = 0L;
			}
			/*
			 * Récupération de l'objet address correspondant dans la session
			 */
			HttpSession session = request.getSession();
			address = ((Map<Long, Address>) session.getAttribute(SESSION_ADDRESSES)).get(id);
		} else {
			/*
			 * Sinon on garde l'ancien mode, pour la validation des champs.
			 * 
			 * L'objet métier pour valider la création d'un address existe déjà,
			 * il est donc déconseillé de dupliquer ici son contenu ! À la
			 * place, il suffit de passer la requête courante à l'objet métier
			 * existant et de récupérer l'objet Address créé.
			 */

			/*
			 * Et très important, il ne faut pas oublier de récupérer le contenu
			 * de la map d'erreur créée par l'objet métier ManifestationForm
			 * dans la map d'erreurs courante, actuellement vide.
			 */
			address = new Address();
		}

		/*
		 * Ensuite, il suffit de procéder normalement avec le reste des champs
		 * spécifiques à un manifestation.
		 */

		String title = getValueField(request, FIELD_TITLE);
		String description = getValueField(request, FIELD_DESCRIPTION);
		String telephone = getValueField(request, FIELD_TELEPHONE);
		String email = getValueField(request, FIELD_EMAIL);
		String sexe = getValueField(request, FIELD_SEXE);
		// String login = getValueField(request, FIELD_LOGIN);

		String image = getValueField(request, FIELD_IMAGE);

		// Statut statut = Statut.fromString(getValueField(request,
		// FIELD_STATUT));
		Boolean admin = Boolean.parseBoolean(getValueField(request, FIELD_ADMIN));

		String startTime = getValueField(request, FIELD_START_TIME);
		// String dateFinEffective = getValueField(request,
		// FIELD_DATE_FIN_EFFECTIVE);
		String endTime = getValueField(request, FIELD_END_TIME);

		String id = getValueField(request, FIELD_ID);

		Manifestation manifestation = new Manifestation();

		try {
			processId(manifestation, id);

			// processSuperviseur(manifestation, superviseur);
			processInstitution(manifestation, institution);
			processAddress(manifestation, address);
			// processWeather(manifestation, weather);

			// processSexe(manifestation, sexe);
			// processLogin(manifestation, login);
			processImage(manifestation, request, image);

			// processAdmin(manifestation, admin);
			processTitle(manifestation, title);
			processDescription(manifestation, description);
			// processTelephone(manifestation, telephone);
			// processEmail(manifestation, email);
			// processStatut(manifestation, statut);
			processImage(manifestation, request, chemin);
			processStartTime(manifestation, startTime);
			processEndTime(manifestation, endTime);
			// processDateFinEffective(manifestation, dateFinEffective);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					m.update(manifestation);

					setResultat("Succès de la modification de l'manifestation.");
				} else {
					setResultat("Échec de la modification de l'manifestation.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("imprévu", "Erreur imprévue lors de la modification.");
			setResultat(
					"Échec de la modification de l'manifestation : une erreur imprévue est survenue, merci de réessayer dans quelques instants.");
			e.printStackTrace();
		}

		return manifestation;
	}

	public Manifestation createManifestation(HttpServletRequest request, String chemin) {

		Manifestation superviseur;
		Institution institution;
		Weather weather;
		Address address;
		/*
		 * Si l'manifestation choisit un manifestation déjà existant, pas de
		 * validation à effectuer
		 */
		String choiceNewManifestation = getValueField(request, FIELD_CHOICE_MANIFESTATION);
		if (FORMER_MANIFESTATION.equals(choiceNewManifestation)) {
			/* Récupération de l'id de l'manifestation choisi */
			String idAncienManifestation = getValueField(request, FIELD_LIST_MANIFESTATIONS);
			Long id = null;
			try {
				id = Long.parseLong(idAncienManifestation);
			} catch (NumberFormatException e) {
				setErreur(FIELD_CHOICE_MANIFESTATION,
						"Manifestation inconnu, merci d'utiliser le formulaire prévu à cet effet.");
				id = 0L;
			}
			/*
			 * Récupération de l'objet manifestation correspondant dans la
			 * session
			 */
			HttpSession session = request.getSession();
			superviseur = ((Map<Long, Manifestation>) session.getAttribute(SESSION_MANIFESTATIONS)).get(id);
		} else {

			superviseur = new Manifestation();
		}

		/*
		 * Si l'manifestation choisit un institution déjà existant, pas de
		 * validation à effectuer
		 */
		String choiceNewInstitution = getValueField(request, FIELD_CHOICE_INSTITUTION);
		if (FORMER_INSTITUTION.equals(choiceNewInstitution)) {
			/* Récupération de l'id du institution choisi */
			String idAncienInstitution = getValueField(request, FIELD_LIST_INSTITUTIONS);
			Long id = null;
			try {
				id = Long.parseLong(idAncienInstitution);
			} catch (NumberFormatException e) {
				setErreur(FIELD_CHOICE_INSTITUTION,
						"Institution inconnu, merci d'utiliser le formulaire prévu à cet effet.");
				id = 0L;
			}
			/*
			 * Récupération de l'objet institution correspondant dans la session
			 */
			HttpSession session = request.getSession();
			institution = ((Map<Long, Institution>) session.getAttribute(SESSION_INSTITUTIONS)).get(id);
		} else {

			/*
			 * Sinon on garde l'ancien mode, pour la validation des champs.
			 * 
			 * L'objet métier pour valider la création d'un institution existe
			 * déjà, il est donc déconseillé de dupliquer ici son contenu ! À la
			 * place, il suffit de passer la requête courante à l'objet métier
			 * existant et de récupérer l'objet Institution créé.
			 */
			InstitutionForm institutionForm = new InstitutionForm(i);
			institution = institutionForm.createInstitution(request);

			/*
			 * Et très important, il ne faut pas oublier de récupérer le contenu
			 * de la map d'erreur créée par l'objet métier ManifestationForm
			 * dans la map d'erreurs courante, actuellement vide.
			 */
			erreurs = institutionForm.getErreurs();
		}

		/*
		 * Si l'manifestation choisit un weather déjà existant, pas de
		 * validation à effectuer
		 */
		String choiceNewWeather = getValueField(request, FIELD_CHOICE_WEATHER);
		if (FORMER_WEATHER.equals(choiceNewWeather)) {
			/* Récupération de l'id du weather choisi */
			String idAncienWeather = getValueField(request, FIELD_LIST_WEATHERS);
			Long id = null;
			try {
				id = Long.parseLong(idAncienWeather);
			} catch (NumberFormatException e) {
				setErreur(FIELD_CHOICE_WEATHER, "Weather inconnu, merci d'utiliser le formulaire prévu à cet effet.");
				id = 0L;
			}
			/* Récupération de l'objet weather correspondant dans la session */
			HttpSession session = request.getSession();
			weather = ((Map<Long, Weather>) session.getAttribute(SESSION_WEATHERS)).get(id);
		} else {
			/*
			 * Sinon on garde l'ancien mode, pour la validation des champs.
			 * 
			 * L'objet métier pour valider la création d'un weather existe déjà,
			 * il est donc déconseillé de dupliquer ici son contenu ! À la
			 * place, il suffit de passer la requête courante à l'objet métier
			 * existant et de récupérer l'objet Weather créé.
			 */
			// WeatherForm weatherForm = new WeatherForm(m);
			// weather = weatherForm.createWeather(request);

			/*
			 * Et très important, il ne faut pas oublier de récupérer le contenu
			 * de la map d'erreur créée par l'objet métier ManifestationForm
			 * dans la map d'erreurs courante, actuellement vide.
			 */
			// erreurs = weatherForm.getErreurs();
		}

		/*
		 * Si l'manifestation choisit une address déjà existante, pas de
		 * validation à effectuer
		 */
		String choiceNewAddress = getValueField(request, FIELD_CHOICE_ADDRESS);
		if (FORMER_ADDRESS.equals(choiceNewAddress)) {
			/* Récupération de l'id de la address choisie */
			String idAncienneAddress = getValueField(request, FIELD_LIST_ADDRESSES);
			Long id = null;
			try {
				id = Long.parseLong(idAncienneAddress);
			} catch (NumberFormatException e) {
				setErreur(FIELD_CHOICE_ADDRESS, "Address inconnue, merci d'utiliser le formulaire prévu à cet effet.");
				id = 0L;
			}
			/*
			 * Récupération de l'objet address correspondant dans la session
			 */
			HttpSession session = request.getSession();
			address = ((Map<Long, Address>) session.getAttribute(SESSION_ADDRESSES)).get(id);
		} else {
			/*
			 * Sinon on garde l'ancien mode, pour la validation des champs.
			 * 
			 * L'objet métier pour valider la création d'un address existe déjà,
			 * il est donc déconseillé de dupliquer ici son contenu ! À la
			 * place, il suffit de passer la requête courante à l'objet métier
			 * existant et de récupérer l'objet Address créé.
			 */
			AddressForm addressForm = new AddressForm(a);
			address = addressForm.createAddress(request);

			/*
			 * Et très important, il ne faut pas oublier de récupérer le contenu
			 * de la map d'erreur créée par l'objet métier ManifestationForm
			 * dans la map d'erreurs courante, actuellement vide.
			 */
			erreurs = addressForm.getErreurs();
		}

		/*
		 * Ensuite, il suffit de procéder normalement avec le reste des champs
		 * spécifiques à un manifestation.
		 */

		String title = getValueField(request, FIELD_TITLE);
		String description = getValueField(request, FIELD_DESCRIPTION);
		String telephone = getValueField(request, FIELD_TELEPHONE);
		String email = getValueField(request, FIELD_EMAIL);
		String sexe = getValueField(request, FIELD_SEXE);
		// String login = getValueField(request, FIELD_LOGIN);
		String mdp = getValueField(request, FIELD_PASS);
		String confirmation = getValueField(request, FIELD_CONF);

		String image = getValueField(request, FIELD_IMAGE);

		// Statut statut = Statut.fromString(getValueField(request,
		// FIELD_STATUT));
		Boolean admin = Boolean.parseBoolean(getValueField(request, FIELD_ADMIN));

		String startTime = getValueField(request, FIELD_START_TIME);
		// String dateFinEffective = getValueField(request,
		// FIELD_DATE_FIN_EFFECTIVE);
		String endTime = getValueField(request, FIELD_END_TIME);

		Manifestation manifestation = new Manifestation();

		try {
			// processSuperviseur(manifestation, superviseur);
			processInstitution(manifestation, institution);
			processAddress(manifestation, address);
			// processWeather(manifestation, weather);

			// processSexe(manifestation, sexe);
			// processLogin(manifestation, login);
			// processMdp(manifestation, mdp);
			// processConfirmation(manifestation, confirmation);
			// processTelephone(manifestation, telephone);
			// processEmail(manifestation, email);
			// processStatut(manifestation, statut);

			// processDateFinEffective(manifestation, dateFinEffective);
			processImage(manifestation, request, image);

			// processAdmin(manifestation, admin);
			processTitle(manifestation, title);
			processDescription(manifestation, description);
			processImage(manifestation, request, chemin);

			processStartTime(manifestation, startTime);

			processEndTime(manifestation, endTime);

			if (getErreurs().isEmpty()) {
				if (m.read(manifestation) == null) {
					manifestation = m.create(manifestation);
					setResultat("Succès de la création de l'manifestation.");
				} else {
					setResultat("Manifestation déjà existant.");
				}
			} else {
				setResultat("Échec de la création de l'manifestation.");
			}

		} catch (DatabaseException e) {
			setErreur("imprévu", "Erreur imprévue lors de la création.");
			setResultat(
					"Échec de la création de l'manifestation : une erreur imprévue est survenue, merci de réessayer dans quelques instants.");
			e.printStackTrace();
		}

		return manifestation;
	}

}
