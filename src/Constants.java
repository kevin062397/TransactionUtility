/**
 * This class <tt>Constants</tt> defines all the constants used in this project.
 *
 * @author Haoyuan Kevin Xia
 * @since 11/26/2022
 */

public class Constants {
	// Transaction file schema
	public static final String TITLE_DATE = "Date";
	public static final String TITLE_TIME_ZONE = "Time Zone";
	public static final String TITLE_SELLER = "Seller";
	public static final String TITLE_ITEM = "Item";
	public static final String TITLE_CURRENCY_LOCAL = "Local Currency";
	public static final String TITLE_CURRENCY_SETTLEMENT = "Settlement Currency";
	public static final String TITLE_TOTAL_LOCAL = "Total (Local)";
	public static final String TITLE_TOTAL_SETTLEMENT = "Total (Settlement)";
	public static final String TITLE_PAYER = "Payer";
	public static final String TITLE_PAYEE = "Payee";

	// Persistence
	public static final String SYSTEM_PROPERTY_KEY_OS_NAME = "os.name";
	public static final String SYSTEM_PROPERTY_KEY_USER_HOME_DIRECTORY = "user.home";
	public static final String PERSISTENCE_FILE_NAME = ".transaction_utility";
	public static final String PERSISTENCE_KEY_ALIASES = "aliases";
	public static final String PERSISTENCE_KEY_FILTER_NAMES = "filterNames";
}
