import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

/**
 * This class <tt>TransactionRecord</tt> represents a piece of transaction record.
 *
 * @author Haoyuan Kevin Xia
 * @since 12/18/2021
 */

@Builder
@Getter
public class TransactionRecord {
	private int id;
	private Date date;
	private String seller;
	private String item;
	private String currencyLocal;
	private String currencySettlement;
	private double totalLocal;
	private double totalSettlement;
	@Getter(AccessLevel.NONE)
	private String payer;
	@Getter(AccessLevel.NONE)
	private String payee;

	public String getPayer() {
		return this.payer.isEmpty() ? "Unknown Payer" : this.payer;
	}

	public String getPayee() {
		return this.payee.isEmpty() ? "Unknown Payee" : this.payee;
	}

	public boolean isPayment() {
		return this.item.equalsIgnoreCase("PAYMENT");
	}

	@Override
	public String toString() {
		if (this.isPayment()) {
			return String.format("[%s] paid [%.2f %s (%.2f %s)] to [%s] at [%s] via [%s]", this.payer,
					this.totalSettlement, this.currencySettlement, this.totalLocal, this.currencyLocal, this.payee,
					this.date, this.seller);
		} else {
			return String.format("[%s] paid [%.2f %s (%.2f %s)] for [%s] at [%s] at [%s] for [%s]", this.payee,
					this.totalSettlement, this.currencySettlement, this.totalLocal, this.currencyLocal, this.payer,
					this.date, this.seller, this.item);
		}
	}
}
