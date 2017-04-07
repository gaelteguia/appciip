package modele;

import java.util.List;

import beans.Directive;

public interface DirectiveManager {
	Directive retrouver(Directive directive) throws DatabaseException;

	void supprimer(Directive directive) throws DatabaseException;

	List<Directive> lister() throws DatabaseException;

	void modifier(Directive directive) throws DatabaseException;

	Directive creer(Directive directive) throws DatabaseException;

}
