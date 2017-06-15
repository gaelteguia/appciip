package modele;

import java.util.List;

import beans.Manifestation;

public interface ManifestationManager {
	Manifestation retrouver(Manifestation manifestation) throws DatabaseException;

	void supprimer(Manifestation manifestation) throws DatabaseException;

	List<Manifestation> lister() throws DatabaseException;

	void modifier(Manifestation manifestation) throws DatabaseException;

	Manifestation creer(Manifestation manifestation) throws DatabaseException;

}
