package modele;

import java.util.List;

import beans.CompteRendu;

public interface CompteRenduManager {
	CompteRendu retrouver(CompteRendu outil) throws DatabaseException;

	void supprimer(CompteRendu outil) throws DatabaseException;

	List<CompteRendu> lister() throws DatabaseException;

	void modifier(CompteRendu outil) throws DatabaseException;

	CompteRendu creer(CompteRendu outil) throws DatabaseException;

}
