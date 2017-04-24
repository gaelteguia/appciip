package ch.appciip.form;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.appciip.bean.Address;
import ch.appciip.bean.Institution;
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
public class AddressForm extends Form {

	public AddressForm(AddressManager a) {
		this.a = a;
	}

	public AddressForm(UserManager u, AddressManager a, QueryManager q, LocalisationManager l, ManifestationManager m,
			WeatherManager w, CantonManager c, InstitutionManager i) {
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
	 * public Address readAddress(HttpServletRequest request) {
	 * 
	 * String name = getValueField(request, FIELD_NAME_ADDRESS); String id =
	 * getValueField(request, FIELD_ID);
	 * 
	 * Address address = new Address();
	 * 
	 * processId(address, id); address.setName(name); try {
	 * 
	 * if (getErreurs().isEmpty()) { address = a.read(address);
	 * 
	 * setResultat("Succès de la recherche de la address."); return address; }
	 * else
	 * 
	 * setResultat("Échec de la recherche. Informations invalides."); } catch
	 * (DatabaseException e) { setErreur(FIELD_ID,
	 * "Erreur lors de la recherche. Vérifier votre login.");
	 * setResultat("Échec de la recherche."); }
	 * 
	 * return address; }
	 */
	public Address associerInstitution(HttpServletRequest request) {
		/* Récupération de l'id de la address choisie */
		String idAddress = getValueField(request, FIELD_LIST_ADDRESSES);
		Long idLoc = null;
		String idInstitution = getValueField(request, FIELD_LIST_INSTITUTIONS);
		Long idSer = null;
		try {
			idLoc = Long.parseLong(idAddress);
		} catch (NumberFormatException e) {
			setErreur(FIELD_CHOICE_ADDRESS, "Address inconnue, merci d'utiliser le formulaire prévu à cet effet.");
			idLoc = 0L;
		}
		try {
			idSer = Long.parseLong(idInstitution);
		} catch (NumberFormatException e) {
			setErreur(FIELD_CHOICE_INSTITUTION,
					"Institution inconnu, merci d'utiliser le formulaire prévu à cet effet.");
			idSer = 0L;
		}
		/* Récupération de l'objet address correspondant dans la session */
		HttpSession session = request.getSession();
		Address address = ((Map<Long, Address>) session.getAttribute(SESSION_ADDRESSES)).get(idLoc);
		Institution institution = ((Map<Long, Institution>) session.getAttribute(SESSION_INSTITUTIONS)).get(idSer);
		try {

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					// address = a.associerInstitution(address, institution);
					setResultat("Succès de l'association.");
				} else {
					setResultat("Échec de l'association.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("imprévu", "Erreur imprévue lors de l'association.");
			setResultat(
					"Échec de l'association : une erreur imprévue est survenue, merci de réessayer dans quelques instants.");
			e.printStackTrace();
		}
		return address;
	}

	public Address deleteInstitution(HttpServletRequest request) {
		Address address = readAddress(request);

		// InstitutionForm institutionForm = new InstitutionForm(i);
		// Institution institution = institutionForm.readInstitution(request);
		// address = a.deleteInstitution(address, institution);
		return address;
	}

	public Address readAddress(HttpServletRequest request) {
		Address address = new Address();
		/* Récupération de l'id de la address choisie */
		String idAddress = getValueField(request, FIELD_LIST_ADDRESSES);
		Long id = null;
		try {
			id = Long.parseLong(idAddress);
		} catch (NumberFormatException e) {
			setErreur(FIELD_CHOICE_ADDRESS, "Address inconnue, merci d'utiliser le formulaire prévu à cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * Récupération de l'objet address correspondant dans la session
			 */
			HttpSession session = request.getSession();
			address = ((Map<Long, Address>) session.getAttribute(SESSION_ADDRESSES)).get(id);
		}

		return address;
	}

	public Address createAddress(HttpServletRequest request) {

		/*
		 * Ensuite, il suffit de procéder normalement avec le reste des champs
		 * spécifiques à une address.
		 */

		String name = getValueField(request, FIELD_NAME_ADDRESS);
		String city = getValueField(request, FIELD_STREET);
		String country = getValueField(request, FIELD_COUNTRY);
		String street = getValueField(request, FIELD_STREET);
		// String nameCourt = getValueField(request, FIELD_NAME_COURT);

		String npa = getValueField(request, FIELD_NPA);
		String number = getValueField(request, FIELD_NUMBER);

		Address address = new Address();
		if (npa != null && !npa.trim().isEmpty())
			processNpa(address, Integer.parseInt(npa));
		if (number != null && !number.trim().isEmpty())
			processNumber(address, Integer.parseInt(number));

		try {
			processName(address, name);

			processCity(address, city);
			processCountry(address, country);
			processStreet(address, street);
			// processNameCourt(address, nameCourt);

			if (getErreurs().isEmpty()) {

				if (a.read(address) == null) {
					address = a.create(address);
					setResultat("Succès de la création de la address.");
				} else {
					setResultat("Address déjà existante.");
				}
			} else {
				setResultat("Échec de la création de la address.");

			}
		} catch (DatabaseException e) {
			setErreur("imprévu", "Erreur imprévue lors de la création.");
			setResultat(
					"Échec de la création de la address : une erreur imprévue est survenue, merci de réessayer dans quelques instants.");
			e.printStackTrace();
		}

		return address;
	}

	public Address updateAddress(HttpServletRequest request) {

		/*
		 * Ensuite, il suffit de procéder normalement avec le reste des champs
		 * spécifiques à une address.
		 */

		String name = getValueField(request, FIELD_NAME_ADDRESS);
		String city = getValueField(request, FIELD_STREET);
		String country = getValueField(request, FIELD_COUNTRY);
		String street = getValueField(request, FIELD_STREET);
		// String nameCourt = getValueField(request, FIELD_NAME_COURT);

		Integer npa = Integer.parseInt(getValueField(request, FIELD_NPA));
		Integer number = Integer.parseInt(getValueField(request, FIELD_NUMBER));

		String id = getValueField(request, FIELD_ID);

		Address address = new Address();
		if (!id.trim().isEmpty())
			processId(address, id);

		try {
			processName(address, name);

			processCity(address, city);
			processCountry(address, country);
			processStreet(address, street);
			// processNameCourt(address, nameCourt);

			processNpa(address, npa);

			processNumber(address, number);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					a.update(address);
					setResultat("Succès de la modification de la address.");
				} else {
					setResultat("Échec de la modification de la address.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("imprévu", "Erreur imprévue lors de la modification.");
			setResultat(
					"Échec de la modification de la address : une erreur imprévue est survenue, merci de réessayer dans quelques instants.");
			e.printStackTrace();
		}

		return address;
	}

	public Address createAddress(ArrayList<String> liste) {

		String name = liste.get(0);
		String city = liste.get(6);
		String country = liste.get(4);
		String street = liste.get(5);
		String nameCourt = liste.get(1);

		Integer npa = Integer.parseInt(liste.get(3));
		Integer number = Integer.parseInt(liste.get(2));

		Address address = new Address();

		if (!name.trim().isEmpty()) {
			try {

				processName(address, name);
				processCity(address, city);
				processCountry(address, country);
				processStreet(address, street);
				// processNameCourt(address, nameCourt);

				processNpa(address, npa);

				processNumber(address, number);

				if (a.read(address) == null) {
					address = a.create(address);
					setResultat("Succès de la creation de la address.");

				} else {

					address = a.read(address);
					processCity(address, city);
					processCountry(address, country);
					processStreet(address, street);
					// processNameCourt(address, nameCourt);

					processNpa(address, npa);

					processNumber(address, number);
					a.update(address);
					setResultat("Address déjà existante.");

				}
			} catch (DatabaseException e) {

				setErreur("imprévu", "Erreur imprévue lors de la creation.");
				setResultat(
						"Échec de la creation de la address : une erreur imprévue est survenue, merci de réessayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("Échec de la creation de la address.");

		}
		return address;

	}

	public Address associerInstitution(ArrayList<String> liste) {
		Address address = new Address(liste.get(0));
		address = a.read(address);
		Institution institution = new Institution(liste.get(1));
		institution = i.read(institution);

		// address = a.associerInstitution(address, institution);

		return address;

	}

}
