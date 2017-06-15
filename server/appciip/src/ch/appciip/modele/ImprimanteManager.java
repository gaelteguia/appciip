package modele;

import java.util.List;

import beans.Imprimante;

public interface ImprimanteManager {
	Imprimante retrouver(Imprimante application) throws DatabaseException;

	void supprimer(Imprimante application) throws DatabaseException;

	List<Imprimante> lister() throws DatabaseException;

	Imprimante creer(Imprimante application) throws DatabaseException;

	void modifier(Imprimante application) throws DatabaseException;

}
