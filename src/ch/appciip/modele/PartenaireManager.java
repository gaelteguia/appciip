package modele;

import java.util.List;

import beans.Partenaire;

public interface PartenaireManager {
	Partenaire retrouver(Partenaire partenaire) throws DatabaseException;

	void supprimer(Partenaire partenaire) throws DatabaseException;

	List<Partenaire> lister() throws DatabaseException;

	void modifier(Partenaire partenaire) throws DatabaseException;

	Partenaire creer(Partenaire partenaire) throws DatabaseException;

}
