import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class <tt>TransactionFileParser</tt> parses a well-formatted transaction file.
 *
 * @author Haoyuan Kevin Xia
 * @since 12/18/2021
 */

public class TransactionFileParser {
	private final File inputFile;

	public TransactionFileParser(File inputFile) {
		this.inputFile = inputFile;
	}

	public TransactionPool parse(boolean printLog) throws IOException {
		Scanner scanner = new Scanner(this.inputFile, StandardCharsets.UTF_8);
		String headerRow = scanner.nextLine();
		List<String> headerTitles = Arrays.asList(this.tokenize(headerRow));

		int indexDate = headerTitles.indexOf(Constants.TITLE_DATE);
		int indexTimeZone = headerTitles.indexOf(Constants.TITLE_TIME_ZONE);
		int indexSeller = headerTitles.indexOf(Constants.TITLE_SELLER);
		int indexItem = headerTitles.indexOf(Constants.TITLE_ITEM);
		int indexCurrencyLocal = headerTitles.indexOf(Constants.TITLE_CURRENCY_LOCAL);
		int indexCurrencySettlement = headerTitles.indexOf(Constants.TITLE_CURRENCY_SETTLEMENT);
		int indexTotalLocal = headerTitles.indexOf(Constants.TITLE_TOTAL_LOCAL);
		int indexTotalSettlement = headerTitles.indexOf(Constants.TITLE_TOTAL_SETTLEMENT);
		int indexPayer = headerTitles.indexOf(Constants.TITLE_PAYER);
		int indexPayee = headerTitles.indexOf(Constants.TITLE_PAYEE);

		TransactionPool pool = new TransactionPool();

		int lineCount = 0;

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			lineCount++;

			if (printLog) {
				System.out.printf("----- Line %05d -----\n", lineCount);
				System.out.printf("Original:  %s\n", line);
			}

			String[] tokens = this.tokenize(line);

			if (printLog) {
				System.out.printf("Tokenized: %s\n", Arrays.toString(tokens));
			}

			String dateString = tokens[indexDate].trim();
			String timeZoneString = tokens[indexTimeZone].trim().replace("UTC", "").replace("GMT", "");
			String seller = tokens[indexSeller].trim();
			String item = tokens[indexItem].trim();
			String currencyLocal = tokens[indexCurrencyLocal].trim();
			String currencySettlement = tokens[indexCurrencySettlement].trim();
			double totalLocal = this.extractNumber(tokens[indexTotalLocal]);
			double totalSettlement = this.extractNumber(tokens[indexTotalSettlement]);
			String payer = tokens[indexPayer].trim();
			String payee = tokens[indexPayee].trim();

			DateFormat dateFormat;
			String dateAndTimeZoneString;
			if (timeZoneString.isEmpty()) {
				dateFormat = new SimpleDateFormat("yyyy-MM-dd H:mm");
				dateAndTimeZoneString = dateString;
			} else {
				dateFormat = new SimpleDateFormat("yyyy-MM-dd H:mm XXX");
				dateAndTimeZoneString = dateString + " " + timeZoneString;
			}

			Date date;
			try {
				date = dateFormat.parse(dateAndTimeZoneString);
			} catch (ParseException e) {
				date = new Date();
			}

			TransactionRecord record = TransactionRecord.builder()
					.id(lineCount)
					.date(date)
					.seller(seller)
					.item(item)
					.currencyLocal(currencyLocal)
					.currencySettlement(currencySettlement)
					.totalLocal(totalLocal)
					.totalSettlement(totalSettlement)
					.payer(payer)
					.payee(payee)
					.build();
			pool.addRecord(record);

			if (printLog) {
				System.out.printf("Parsed:    %s\n", record);
				System.out.println();
			}
		}

		scanner.close();

		return pool;
	}

	private String[] tokenize(String line) {
		// Commas surrounded by double quotation marks are not delimiters
		String[] tokens = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			if (token.startsWith("\"") && token.endsWith("\"")) {
				token = token.substring(1, token.length() - 1);
				tokens[i] = token;
			}
		}
		return tokens;
	}

	private double extractNumber(String text) {
		Pattern numberPattern = Pattern.compile("(\\d+(\\.\\d+)?)");
		Matcher numberMatcher = numberPattern.matcher(text);
		if (numberMatcher.find()) {
			String numberString = numberMatcher.group();
			return Double.parseDouble(numberString);
		} else {
			return 0.0;
		}
	}
}
