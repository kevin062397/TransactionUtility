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
	private Set<String> filterNames;

	private FilterNamesManager() {
		this.filterNames = new HashSet<>();
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
				.map(filterName -> filterName.trim())
				.filter(filterName -> !filterName.isEmpty())
				.collect(Collectors.toSet());
		this.filterNames = new TreeSet<>(filterNames);
	}

	public void add(String filterName) {
		this.filterNames.add(filterName);
	}

	public void add(Collection<String> filterNames) {
		this.filterNames.addAll(filterNames);
	}

	public void remove(String filterName) {
		this.filterNames.remove(filterName);
	}

	public void clear() {
		this.filterNames.clear();
	}

	public Set<String> getAll() {
		return new TreeSet<>(this.filterNames);
	}
}
