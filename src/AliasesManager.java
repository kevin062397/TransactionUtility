import java.util.*;

/**
 * @author Haoyuan Kevin Xia
 * @since 11/26/2022
 */

public class AliasesManager {
	/**
	 * Singleton
	 */
	public static AliasesManager sharedInstance;

	private final PersistenceManager persistenceManager;

	private Map<String, String> aliases;

	private AliasesManager() {
		this.persistenceManager = PersistenceManager.sharedInstance();
		this.loadFromPersistence();
	}

	public static AliasesManager sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new AliasesManager();
		}
		return sharedInstance;
	}

	public void setString(String aliasesString) {
		Map<String, String> aliases = new HashMap<>();

		String[] tokens = aliasesString.split("[,，]");
		for (String token : tokens) {
			String[] subtokens = token.split("[:：]");
			if (subtokens.length == 2) {
				String alias = subtokens[0].trim();
				String name = subtokens[1].trim();
				if (!alias.isEmpty() && !name.isEmpty()) {
					aliases.put(alias, name);
				}
			}
		}

		this.aliases = aliases;
		this.saveToPersistence();
	}

	public void add(String alias, String name) {
		this.aliases.put(alias, name);
		this.saveToPersistence();
	}

	public void add(Map<String, String> aliases) {
		this.aliases.putAll(aliases);
		this.saveToPersistence();
	}

	public void remove(String alias) {
		this.aliases.remove(alias);
		this.saveToPersistence();
	}

	public void clear() {
		this.aliases.clear();
		this.saveToPersistence();
	}

	public Map<String, String> getAll() {
		return new TreeMap<>(this.aliases);
	}

	@Override
	public String toString() {
		// {alias1=name1, alias2=name2, alias3=name3}
		String result = this.aliases.toString().replace("=", ": ");
		return result.substring(1, result.length() - 1);
		// alias1: name1, alias2: name2, alias3: name3
	}

	private void loadFromPersistence() {
		List persistenceData = this.persistenceManager.getList(Constants.PERSISTENCE_KEY_ALIASES);
		this.aliases = decodePersistenceData(persistenceData);
	}

	private void saveToPersistence() {
		List<Map<String, String>> data = this.encodePersistenceData(this.aliases);
		this.persistenceManager.put(Constants.PERSISTENCE_KEY_ALIASES, data);
	}

	private Map<String, String> decodePersistenceData(List data) {
		Map<String, String> result = new HashMap<>();
		if (data != null) {
			for (Object object : data) {
				if (object instanceof Map map) {
					Object aliasObject = map.get("alias");
					Object nameObject = map.get("name");
					if (aliasObject instanceof String alias && nameObject instanceof String name) {
						result.put(alias, name);
					}
				}
			}
		}
		return result;
	}

	private List<Map<String, String>> encodePersistenceData(Map<String, String> data) {
		List<Map<String, String>> result = new ArrayList<>();
		for (String alias : data.keySet()) {
			String name = data.get(alias);
			Map<String, String> map = Map.ofEntries(Map.entry("alias", alias), Map.entry("name", name));
			result.add(map);
		}
		return result;
	}
}
