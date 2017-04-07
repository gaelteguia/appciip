package modele;

import java.util.List;

import beans.Service;

public interface ServiceManager {
	Service retrouver(Service service) throws DatabaseException;

	void supprimer(Service service) throws DatabaseException;

	List<Service> lister() throws DatabaseException;

	void modifier(Service service) throws DatabaseException;

	Service creer(Service service) throws DatabaseException;

	List<Service> lister(Service service) throws DatabaseException;

}
