import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Haoyuan Kevin Xia
 * @since 11/26/2022
 */

public class FilterNamesManager {
	/**
	 * Singleton
	 */
	public static FilterNamesManager sharedInstance;

	private final PersistenceManager persistenceManager;

	private Set<String> filterNames;

	private FilterNamesManager() {
		this.persistenceManager = PersistenceManager.sharedInstance();
		this.loadFromPersistence();
	}

	public static FilterNamesManager sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new FilterNamesManager();
		}
		return sharedInstance;
	}

	public void setString(String filterNamesString) {
		String[] filterNamesArray = filterNamesString.split("[,，]");
		Set<String> filterNames = Arrays.stream(filterNamesArray)
				.map(String::trim)
				.filter(filterName -> !filterName.isEmpty())
				.collect(Collectors.toSet());
		this.filterNames = new HashSet<>(filterNames);
		this.saveToPersistence();
	}

	public void add(String filterName) {
		this.filterNames.add(filterName);
		this.saveToPersistence();
	}

	public void add(Collection<String> filterNames) {
		this.filterNames.addAll(filterNames);
		this.saveToPersistence();
	}

	public void remove(String filterName) {
		this.filterNames.remove(filterName);
		this.saveToPersistence();
	}

	public void clear() {
		this.filterNames.clear();
		this.saveToPersistence();
	}

	public Set<String> getAll() {
		return new TreeSet<>(this.filterNames);
	}

	@Override
	public String toString() {
		// [name1, name2, name3]
		String result = this.filterNames.toString();
		return result.substring(1, result.length() - 1);
		// name1, name2, name3
	}

	private void loadFromPersistence() {
		List persistenceData = this.persistenceManager.getList(Constants.PERSISTENCE_KEY_FILTER_NAMES);
		this.filterNames = decodePersistenceData(persistenceData);
	}

	private void saveToPersistence() {
		List<String> data = this.encodePersistenceData(this.filterNames);
		this.persistenceManager.put(Constants.PERSISTENCE_KEY_FILTER_NAMES, data);
	}

	private Set<String> decodePersistenceData(List data) {
		Set<String> result = new HashSet<>();
		if (data != null) {
			for (Object object : data) {
				if (object instanceof String filterName) {
					result.add(filterName);
				}
			}
		}
		return result;
	}

	private List<String> encodePersistenceData(Set<String> data) {
		return new ArrayList<>(data);
	}
}
