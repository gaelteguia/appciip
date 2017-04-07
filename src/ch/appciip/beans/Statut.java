/**
 * Classe de gestion des statuts
 * @author Ga�l TEGUIA TEGUIA
 */
package ch.appciip.beans;

public enum Statut {
	COLLABORATEUR_SCIENTIFIQUE("Scientifique"), COLLABORATEUR_ADMINISTRATIF("Administratif"), EXTERNE(
			"Externe"), STAGIAIRE("Stagiaire");
	private String name = "";

	// Constructeur
	Statut(String name) {
		this.name = name;

	}

	public String toString() {
		return name;
	}

	public static Statut fromString(String name) {
		if (name != null) {
			for (Statut s : Statut.values()) {
				if (name.equalsIgnoreCase(s.name)) {
					return s;
				}
			}
		}
		return null;
	}

}
