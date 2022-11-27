import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Haoyuan Kevin Xia
 * @since 11/26/2022
 */

public class AliasesManager {
	/**
	 * Singleton
	 */
	public static AliasesManager sharedInstance;
	private Map<String, String> aliases;

	private AliasesManager() {
		this.aliases = new HashMap<>();
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
	}

	public void add(String alias, String name) {
		this.aliases.put(alias, name);
	}

	public void add(Map<String, String> aliases) {
		this.aliases.putAll(aliases);
	}

	public void remove(String alias) {
		this.aliases.remove(alias);
	}

	public void clear() {
		this.aliases.clear();
	}

	public Map<String, String> getAll() {
		return new TreeMap<>(this.aliases);
	}
}
