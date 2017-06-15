package ch.appciip.model;

import java.util.List;

import ch.appciip.bean.User;

public interface UserManager {

	User read(User user) throws DatabaseException;

	User connect(User user) throws DatabaseException;

	User challenge(User user) throws DatabaseException;

	void delete(User user) throws DatabaseException;

	List<User> list() throws DatabaseException;

	List<User> anniversaire() throws DatabaseException;

	List<User> prochainAnniversaire() throws DatabaseException;

	void updateMdp(User user) throws DatabaseException;

	void updateChallenge(User user) throws DatabaseException;

	User create(User user) throws DatabaseException;

	void update(User user) throws DatabaseException;

	void updateActive(User user) throws DatabaseException;

	List<User> list(User user) throws DatabaseException;

}
