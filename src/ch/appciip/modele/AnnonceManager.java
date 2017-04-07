package modele;

import java.util.List;

import beans.Annonce;

public interface AnnonceManager {
	Annonce retrouver(Annonce annonce) throws DatabaseException;

	void supprimer(Annonce annonce) throws DatabaseException;

	List<Annonce> lister() throws DatabaseException;

	void modifier(Annonce annonce) throws DatabaseException;

	Annonce creer(Annonce annonce) throws DatabaseException;

}
