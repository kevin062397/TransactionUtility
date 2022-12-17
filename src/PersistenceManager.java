import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * @author Haoyuan Kevin Xia
 * @since 2022-11-26
 */

public class PersistenceManager {
	/**
	 * Singleton
	 */
	public static PersistenceManager sharedInstance;

	private final File persistenceFile;

	private JSONObject rootObject;

	private PersistenceManager() {
		String userHomeDirectoryPath = System.getProperty(Constants.SYSTEM_PROPERTY_KEY_USER_HOME_DIRECTORY);
		this.persistenceFile = new File(userHomeDirectoryPath, Constants.PERSISTENCE_FILE_NAME);

		this.loadFromFile();
	}

	public static PersistenceManager sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new PersistenceManager();
		}
		return sharedInstance;
	}

	private void loadFromFile() {
		JSONParser parser = new JSONParser();
		try {
			FileReader fileReader = new FileReader(this.persistenceFile, StandardCharsets.UTF_8);
			Object parsedObject = parser.parse(fileReader);
			fileReader.close();
			this.rootObject = (JSONObject) parsedObject;
		} catch (Exception exception) {
			exception.printStackTrace();
			this.rootObject = new JSONObject();
		}
	}

	private void saveToFile() {
		this.setFileHidden(false);

		try {
			FileWriter fileWriter = new FileWriter(this.persistenceFile, StandardCharsets.UTF_8);
			fileWriter.write(this.rootObject.toJSONString());
			fileWriter.flush();
			fileWriter.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		this.setFileHidden(true);
	}

	private void setFileHidden(boolean hidden) {
		if (System.getProperty(Constants.SYSTEM_PROPERTY_KEY_OS_NAME).toLowerCase().contains("win")) {
			Path path = this.persistenceFile.toPath();
			try {
				Files.setAttribute(path, "dos:hidden", hidden, LinkOption.NOFOLLOW_LINKS);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public Object getObject(String key) {
		return this.rootObject.get(key);
	}

	public String getString(String key) {
		Object object = this.getObject(key);
		if (object instanceof String) {
			return (String) object;
		}
		return null;
	}

	public Number getNumber(String key) {
		Object object = this.getObject(key);
		if (object instanceof Number) {
			return (Number) object;
		}
		return null;
	}

	public Boolean getBoolean(String key) {
		Object object = this.getObject(key);
		if (object instanceof Boolean) {
			return (Boolean) object;
		}
		return null;
	}

	public List getList(String key) {
		Object object = this.getObject(key);
		if (object instanceof List) {
			return (List) object;
		}
		return null;
	}

	public Map getMap(String key) {
		Object object = this.getObject(key);
		if (object instanceof Map) {
			return (Map) object;
		}
		return null;
	}

	public void put(String key, Object value) {
		this.rootObject.put(key, value);
		this.saveToFile();
	}

	public void clear() {
		this.rootObject.clear();
		this.saveToFile();
	}
}
