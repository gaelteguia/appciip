package modele;

import java.util.List;

import beans.Application;

public interface ApplicationManager {
	Application retrouver(Application application) throws DatabaseException;

	void supprimer(Application application) throws DatabaseException;

	List<Application> lister() throws DatabaseException;

	Application creer(Application application) throws DatabaseException;

	void modifier(Application application) throws DatabaseException;

}
