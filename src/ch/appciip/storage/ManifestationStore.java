package ch.appciip.storage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ch.appciip.bean.Address;
import ch.appciip.bean.Manifestation;

public class ManifestationStore {
	private static Map<String, Manifestation> store;
	private static ManifestationStore instance = null;

	private ManifestationStore() {
		store = new HashMap<String, Manifestation>();
		initOneManifestation();
	}

	public static Map<String, Manifestation> getStore() {
		if (instance == null) {
			instance = new ManifestationStore();
		}
		return store;
	}

	private static void initOneManifestation() {
		Address addr = new Address("Shanghai", "Long Hua Street");
		Manifestation cHuang = new Manifestation("huangyim", "Huang Yi Ming", addr);
		store.put(cHuang.getId(), cHuang);
	}
}
