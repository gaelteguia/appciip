package ch.appciip.form;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.appciip.bean.Institution;
import ch.appciip.bean.User;
import ch.appciip.model.AddressManager;
import ch.appciip.model.CantonManager;
import ch.appciip.model.DatabaseException;
import ch.appciip.model.InstitutionManager;
import ch.appciip.model.LocalisationManager;
import ch.appciip.model.ManifestationManager;
import ch.appciip.model.QueryManager;
import ch.appciip.model.UserManager;
import ch.appciip.model.WeatherManager;

@SuppressWarnings("unchecked")
public class InstitutionForm extends Form {

	public InstitutionForm(InstitutionManager i) {

		this.i = i;
	}

	public InstitutionForm(UserManager u, AddressManager a, QueryManager q, LocalisationManager l,
			ManifestationManager m, WeatherManager w, CantonManager c, InstitutionManager i) {
		this.u = u;
		this.a = a;
		this.q = q;
		this.l = l;
		this.m = m;
		this.w = w;
		this.c = c;
		this.i = i;

	}

	/*
	 * public Institution readInstitution(HttpServletRequest request) {
	 * 
	 * String name = getValueField(request, FIELD_NAME_INSTITUTION); String id =
	 * getValueField(request, FIELD_ID);
	 * 
	 * Institution institution = new Institution(); processId(institution, id);
	 * 
	 * institution.setName(name);
	 * 
	 * try {
	 * 
	 * if (getErreurs().isEmpty()) { institution = s.read(institution);
	 * setResultat("Succ�s de la recherche du institution."); } else
	 * 
	 * setResultat("�chec de la recherche du institution. Informations invalides."
	 * ); } catch (DatabaseException e) { setErreur("impr�vu",
	 * "Erreur impr�vue lors de la recherche.");
	 * setResultat("�chec de la recherche."); }
	 * 
	 * return institution; }
	 */
	public Institution createInstitution(HttpServletRequest request) {
		User superviseur;
		Institution departement;

		/*
		 * Si l'utilisateur choisit un utilisateur d�j� existant, pas de
		 * validation � effectuer
		 */
		String choiceNouvelUser = getValueField(request, FIELD_CHOICE_USER);
		if (FORMER_USER.equals(choiceNouvelUser)) {
			/* R�cup�ration de l'id de l'utilisateur choisi */
			String idFormerUser = getValueField(request, FIELD_LIST_USERS);
			Long id = null;
			try {
				id = Long.parseLong(idFormerUser);
			} catch (NumberFormatException e) {
				setErreur(FIELD_CHOICE_USER, "User inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet utilisateur correspondant dans la session
			 */
			HttpSession session = request.getSession();
			superviseur = ((Map<Long, User>) session.getAttribute(SESSION_USERS)).get(id);
		} else {

			superviseur = new User();
		}

		/*
		 * Si l'utilisateur choisit un institution d�j� existant, pas de
		 * validation � effectuer
		 */
		String choiceNewInstitution = getValueField(request, FIELD_CHOICE_INSTITUTION);
		if (FORMER_INSTITUTION.equals(choiceNewInstitution)) {
			/* R�cup�ration de l'id du institution choisi */
			String idFormerInstitution = getValueField(request, FIELD_LIST_INSTITUTIONS);
			Long id = null;
			try {
				id = Long.parseLong(idFormerInstitution);
			} catch (NumberFormatException e) {
				setErreur(FIELD_CHOICE_INSTITUTION,
						"Institution inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet institution correspondant dans la session
			 */
			HttpSession session = request.getSession();
			departement = ((Map<Long, Institution>) session.getAttribute(SESSION_INSTITUTIONS)).get(id);
		} else {

			departement = new Institution();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � un institution.
		 */

		String name = getValueField(request, FIELD_NAME_INSTITUTION);
		String acronym = getValueField(request, FIELD_ACRONYM);

		Institution institution = new Institution();

		try {
			// processSuperviseur(institution, superviseur);
			// processDepartement(institution, departement);

			processName(institution, name);
			// processAcronym(institution, acronym);

			if (getErreurs().isEmpty()) {

				if (i.read(institution) == null) {
					// if (getErreurs().isEmpty()) {
					institution = i.create(institution);
					setResultat("Succ�s de la cr�ation du institution.");
				} else {

					setResultat("Institution d�j� existant.");
				}
			}

			else {
				setResultat("�chec de la cr�ation du institution.");
			}

		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation du institution : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}
		return institution;
	}

	public Institution updateInstitution(HttpServletRequest request) {
		User superviseur;
		Institution departement;

		/*
		 * Si l'utilisateur choisit un utilisateur d�j� existant, pas de
		 * validation � effectuer
		 */
		String choiceNouvelUser = getValueField(request, FIELD_CHOICE_USER);
		if (FORMER_USER.equals(choiceNouvelUser)) {
			/* R�cup�ration de l'id du utilisateur choisi */
			String idFormerUser = getValueField(request, FIELD_LIST_USERS);
			Long id = null;
			try {
				id = Long.parseLong(idFormerUser);
			} catch (NumberFormatException e) {
				setErreur(FIELD_CHOICE_USER, "User inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet utilisateur correspondant dans la session
			 */
			HttpSession session = request.getSession();
			superviseur = ((Map<Long, User>) session.getAttribute(SESSION_USERS)).get(id);
		} else {

			superviseur = new User();
		}

		/*
		 * Si l'utilisateur choisit un institution d�j� existant, pas de
		 * validation � effectuer
		 */
		String choiceNewInstitution = getValueField(request, FIELD_CHOICE_INSTITUTION);
		if (FORMER_INSTITUTION.equals(choiceNewInstitution)) {
			/* R�cup�ration de l'id du institution choisi */
			String idFormerInstitution = getValueField(request, FIELD_LIST_INSTITUTIONS);
			Long id = null;
			try {
				id = Long.parseLong(idFormerInstitution);
			} catch (NumberFormatException e) {
				setErreur(FIELD_CHOICE_INSTITUTION,
						"Institution inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet institution correspondant dans la session
			 */
			HttpSession session = request.getSession();
			departement = ((Map<Long, Institution>) session.getAttribute(SESSION_INSTITUTIONS)).get(id);
		} else {

			departement = new Institution();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � un institution.
		 */

		String name = getValueField(request, FIELD_NAME_INSTITUTION);
		String acronym = getValueField(request, FIELD_ACRONYM);
		String id = getValueField(request, FIELD_ID);

		Institution institution = new Institution();
		if (!id.trim().isEmpty())
			processId(institution, id);

		try {
			// processSuperviseur(institution, superviseur);
			// processDepartement(institution, departement);

			processName(institution, name);
			// processAcronym(institution, acronym);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					i.update(institution);
					setResultat("Succ�s de la modification du institution.");
				} else {
					setResultat("�chec de la modification du institution.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modification.");
			setResultat(
					"�chec de la modification du institution : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return institution;
	}

	public Institution readInstitution(HttpServletRequest request) {
		Institution institution = new Institution();
		/* R�cup�ration de l'id du institution choisi */
		String idInstitution = getValueField(request, FIELD_LIST_INSTITUTIONS);
		Long id = null;
		try {
			id = Long.parseLong(idInstitution);
		} catch (NumberFormatException e) {
			setErreur(FIELD_CHOICE_INSTITUTION,
					"Institution inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet institution correspondant dans la session
			 */
			HttpSession session = request.getSession();
			institution = ((Map<Long, Institution>) session.getAttribute(SESSION_INSTITUTIONS)).get(id);
		}
		return institution;
	}

	public Institution createInstitution(ArrayList<String> liste) {
		String name = liste.get(0);

		String acronym = liste.get(1);

		Institution departement = new Institution(liste.get(2));
		departement = i.read(departement);

		User superviseur = new User(liste.get(3), liste.get(4));
		superviseur = u.read(superviseur);

		Institution institution = new Institution();

		if (!name.trim().isEmpty()) {

			try {
				processName(institution, name);
				// processSuperviseur(institution, superviseur);
				// processDepartement(institution, departement);

				// processAcronym(institution, acronym);

				if (i.read(institution) == null) {

					institution = i.create(institution);
					setResultat("Succ�s de la cr�ation du institution.");
				} else {

					institution = i.read(institution);
					// processSuperviseur(institution, superviseur);
					// processDepartement(institution, departement);

					// processAcronym(institution, acronym);

					i.update(institution);
					setResultat("Institution d�j� existant.");
				}

			} catch (DatabaseException e) {
				setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
				setResultat(
						"�chec de la cr�ation du institution : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la cr�ation du institution.");
		}

		return institution;

	}

}
