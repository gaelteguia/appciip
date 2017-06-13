package modele;

import java.util.List;

import beans.Telephone;

public interface TelephoneManager {
	Telephone retrouver(Telephone application) throws DatabaseException;

	void supprimer(Telephone application) throws DatabaseException;

	List<Telephone> lister() throws DatabaseException;

	Telephone creer(Telephone application) throws DatabaseException;

	void modifier(Telephone application) throws DatabaseException;

}
