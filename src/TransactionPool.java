import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class <tt>TransactionPool</tt> represents a collection of transaction records.
 *
 * @author Haoyuan Kevin Xia
 * @since 12/18/2021
 */

public class TransactionPool {
	private final List<TransactionRecord> records;

	public TransactionPool() {
		this.records = new ArrayList<TransactionRecord>();
	}

	public void addRecord(@NonNull TransactionRecord record) {
		this.records.add(record);
	}

	public int size() {
		return this.records.size();
	}

	public List<TransactionRecord> getRecords() {
		return new ArrayList<TransactionRecord>(this.records);
	}

	public TransactionRecord getRecordWithIndex(int index) {
		if (index >= 0 && index < this.size()) {
			return this.records.get(index);
		} else {
			return null;
		}
	}

	public TransactionRecord getRecordWithId(int id) {
		for (TransactionRecord record : this.records) {
			if (record.getId() == id) {
				return record;
			}
		}
		return null;
	}

	public TransactionReport getReport(double threshold, Map<String, String> aliases, Set<String> filterNames) {
		TransactionReport report = new TransactionReport(threshold);

		for (TransactionRecord record : this.records) {
			report.addRecord(record);
		}

		if (aliases != null) {
			report.setAliases(aliases);
		}

		if (filterNames != null) {
			report.setFilterNames(filterNames);
		}

		return report;
	}
}
