import lombok.NonNull;

import java.util.*;

/**
 * This class <tt>TransactionReport</tt> generates a report for a collection of transaction records.
 *
 * @author Haoyuan Kevin Xia
 * @since 2021-12-18
 */

public class TransactionReport {
	private final Set<TransactionReportItem> reportItems;
	private final double reportThreshold;
	private Set<String> filterNames;
	private Map<String, String> aliases;

	public TransactionReport(double reportThreshold) {
		/*
		 * The order cannot be maintained with a TreeSet because the order of the tree is determined when the object
		 * is inserted. If the names are reversed afterwards, the order cannot be guaranteed.
		 */
		this.reportItems = new HashSet<>();
		this.reportThreshold = reportThreshold;
		this.filterNames = new HashSet<>();
		this.aliases = new HashMap<>();
	}

	public void addRecord(@NonNull TransactionRecord record) {
		TransactionReportItem newReportItem = new TransactionReportItem(record.getPayer(), record.getPayee(),
				record.getTotalSettlement(), record.getCurrencySettlement());
		if (record.isPayment()) {
			newReportItem.amount = -newReportItem.amount;
		}

		if (this.aliases.containsKey(newReportItem.payer)) {
			newReportItem.payer = this.aliases.get(newReportItem.payer);
		}
		if (this.aliases.containsKey(newReportItem.payee)) {
			newReportItem.payee = this.aliases.get(newReportItem.payee);
		}

		if (newReportItem.payer.equals(newReportItem.payee)) {
			return;
		}

		for (TransactionReportItem reportItem : this.reportItems) {
			if (reportItem.mergeWith(newReportItem)) {
				return;
			}
		}
		this.reportItems.add(newReportItem);
	}

	public void setAliases(@NonNull Map<String, String> aliases) {
		this.aliases = new HashMap<>(aliases);
	}

	public void setFilterNames(@NonNull Set<String> filterNames) {
		this.filterNames = new HashSet<>(filterNames);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		Set<TransactionReportItem> reportItemsSorted = new TreeSet<>(this.reportItems);
		for (TransactionReportItem reportItem : reportItemsSorted) {
			if (reportItem.amount > this.reportThreshold && (this.filterNames.isEmpty()
					|| this.filterNames.contains(reportItem.payer) || this.filterNames.contains(reportItem.payee))) {
				result.append(reportItem).append("\n");
			}
		}

		return result.toString();
	}

	private static class TransactionReportItem implements Comparable<TransactionReportItem> {
		public String payer;
		public String payee;
		public double amount;
		public final String currency;

		public TransactionReportItem(String payer, String payee, double amount, String currency) {
			this.payer = payer;
			this.payee = payee;
			this.amount = amount;
			this.currency = currency;
		}

		private boolean hasSameCurrency(TransactionReportItem other) {
			return this.currency.equals(other.currency);
		}

		private boolean hasSameNames(TransactionReportItem other) {
			return this.payer.equals(other.payer) && this.payee.equals(other.payee);
		}

		private boolean hasReverseNames(TransactionReportItem other) {
			return this.payer.equals(other.payee) && this.payee.equals(other.payer);
		}

		public boolean canMergeWith(TransactionReportItem other) {
			return this.hasSameCurrency(other) && (this.hasSameNames(other) || this.hasReverseNames(other));
		}

		public boolean mergeWith(TransactionReportItem other) {
			if (!this.canMergeWith(other)) {
				return false;
			}

			if (this.hasSameNames(other)) {
				this.amount += other.amount;
			} else {
				this.amount -= other.amount;
			}

			if (this.amount < 0) {
				this.reverse();
			}

			return true;
		}

		private void reverse() {
			this.amount = -this.amount;

			String temp = this.payer;
			this.payer = this.payee;
			this.payee = temp;
		}

		@Override
		public int compareTo(TransactionReportItem other) {
			if (!this.payer.equals(other.payer)) {
				return this.payer.compareTo(other.payer);
			} else if (!this.payee.equals(other.payee)) {
				return this.payee.compareTo(other.payee);
			} else if (!this.currency.equals(other.currency)) {
				return this.currency.compareTo(other.currency);
			} else {
				return Double.compare(this.amount, other.amount);
			}
		}

		@Override
		public String toString() {
			return String.format("[%s] needs to pay [%s] [%.2f %s]", this.payer, this.payee, this.amount,
					this.currency);
		}
	}
}
