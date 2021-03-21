import java.io.*;
import java.nio.charset.Charset;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * @author Haoyuan Kevin Xia
 * @since 09/2015
 */

public class TransactionUtility extends JPanel implements ActionListener {
	public static final int IDENTIFIER_LENGTH = 3;
	JButton openButton, calculateButton, saveButton;
	JLabel filterLabel;
	JTextField filterField;
	JTextArea textArea;
	JFileChooser fileChooser;
	File inputFile;

	public static void main(String[] args) {
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked
	 * from the event dispatch thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window
		JFrame frame = new JFrame("Transaction Utility");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window
		frame.add(new TransactionUtility());

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int height = screenSize.height;
		int width = screenSize.width;
		frame.setSize(width / 2, height / 2);

		// Center the window on screen
		frame.setLocationRelativeTo(null);

		// Display the window
		frame.pack();
		frame.setVisible(true);
	}

	public TransactionUtility() {
		super(new BorderLayout());

		// Create the log first because the action listeners need to refer to it
		this.textArea = new JTextArea(20, 60);
		this.textArea.setMargin(new Insets(5, 5, 5, 5));
		this.textArea.setEditable(false);
		this.textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		JScrollPane logScrollPane = new JScrollPane(this.textArea);

		this.textArea.setText("Please choose a transaction file.");
		this.textArea.setCaretPosition(this.textArea.getDocument().getLength());

		this.fileChooser = new JFileChooser();
		this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		this.openButton = new JButton("Open");
		this.openButton.addActionListener(this);

		this.calculateButton = new JButton("Calculate");
		this.calculateButton.addActionListener(this);

		this.saveButton = new JButton("Save");
		this.saveButton.addActionListener(this);

		this.filterLabel = new JLabel("Filter by Name");

		this.filterField = new JTextField(20);
		this.filterField.setText("");

		// For layout purposes, put the buttons in a separate panel
		JPanel buttonPanel = new JPanel(); // Use FlowLayout
		buttonPanel.add(this.openButton);
		buttonPanel.add(this.calculateButton);
		buttonPanel.add(this.saveButton);

		JPanel optionPanel = new JPanel();
		optionPanel.add(this.filterLabel);
		optionPanel.add(this.filterField);

		// Add the buttons and the log to this panel
		add(buttonPanel, BorderLayout.PAGE_START);
		add(optionPanel, BorderLayout.PAGE_END);
		add(logScrollPane, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		// Handle open button action
		if (e.getSource() == this.openButton) {
			int returnVal = this.fileChooser.showOpenDialog(TransactionUtility.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				this.inputFile = this.fileChooser.getSelectedFile();
				// This is where a real application would open the file
				this.textArea.setText("File opened: " + this.inputFile.getName() + ".\n");
			} else {
				this.textArea.setText("Open command cancelled by user.\n");
			}
			this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
		} else if (e.getSource() == this.calculateButton) {
			// Handle calculate button action
			if (this.inputFile == null) {
				this.textArea.setText("Please choose a transaction file.");
				this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
			} else {
				this.textArea.setText("");
				try {
					this.calculate();
				} catch (Exception exception) {
					this.textArea.append("Error\n\n");
					this.textArea.append(exception.getLocalizedMessage());
					exception.printStackTrace();
					this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
				}
			}
		} else if (e.getSource() == this.saveButton) {
			int returnVal = this.fileChooser.showSaveDialog(TransactionUtility.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File outputFile = this.fileChooser.getSelectedFile();
				try {
					this.saveLog(outputFile);
				} catch (Exception exception) {
				}
			}
		}
	}

	public void calculate() throws IOException {
		String filterText = this.filterField.getText();
		Set<String> filterNameSet = new HashSet<String>();
		if (filterText.contains(",")) {
			String[] filterNameArray = filterText.split(",");
			for (String token : filterNameArray) {
				filterNameSet.add(token.trim());
			}
		} else if (!filterText.isEmpty()) {
			filterNameSet.add(filterText);
		}

		Scanner scanner = new Scanner(this.inputFile, Charset.forName("UTF-8"));
		scanner.nextLine();

		Map<String, Double> first = new TreeMap<String, Double>();
		String date = "";

		int lineCount = 0;

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			lineCount++;
			System.out.printf("%5d: %s\n", lineCount, line);
			// Excel includes a leading space and a trailing space around the currency
			String[] tokens = line.split(",");
			date = tokens[0].trim();
			String seller = tokens[1].trim();
			String item = tokens[2].trim();
			String currencyLocal = tokens[6].trim();
			String currencySettlement = tokens[7].trim();
			Double totalLocal = Double.parseDouble(tokens[9].trim().substring(1));
			Double totalSettlement = Double.parseDouble(tokens[10].trim().substring(1));
			String payer = tokens[11].trim();
			String payee = tokens[12].trim();

			payer += "   ";
			payee += "   ";
			payer = payer.substring(0, IDENTIFIER_LENGTH);
			payee = payee.substring(0, IDENTIFIER_LENGTH);

			// Special cases
			if (payer.equals("XDD")) {
				payer = "XHY";
			}

			if (payer.equals(payee)) {
				this.textArea.append(String.format("%5d: Skipped (%3s paid for %3s)\n", lineCount, payee, payer));
				continue;
			}

			if (item.equals("PAYMENT")) {
				String temp = payer;
				payer = payee;
				payee = temp;
			}

			String key = payer + payee;

			if (!first.keySet().contains(key)) {
				first.put(key, Double.valueOf(0));
			}

			Double previousValue = first.get(key);
			first.put(key, previousValue + totalSettlement);

			if (filterNameSet.isEmpty() || filterNameSet.contains(payer) || filterNameSet.contains(payee)) {
				if (item.equals("PAYMENT")) {
					this.textArea.append(String.format("%5d: %3s paid %.2f %s (%.2f %s) to %3s at %s via %s\n",
							lineCount, payee, totalSettlement, currencySettlement, totalLocal, currencyLocal, payer,
							date, seller));
				} else {
					this.textArea.append(String.format("%5d: %3s paid %.2f %s (%.2f %s) for %3s at %s at %s\n",
							lineCount, payee, totalSettlement, currencySettlement, totalLocal, currencyLocal, payer,
							date, seller));
				}
			}
		}
		
		scanner.close();

		this.textArea.append("\n");

		long startTime = System.nanoTime();

		Map<String, Double> second = new TreeMap<String, Double>();

		for (String key : first.keySet()) {
			String reversedKey = "" + key.substring(IDENTIFIER_LENGTH, IDENTIFIER_LENGTH * 2)
					+ key.substring(0, IDENTIFIER_LENGTH);
			if (second.containsKey(reversedKey)) {
				second.put(reversedKey, second.get(reversedKey) - first.get(key));
			} else if (second.containsKey(key)) {
				second.put(key, second.get(reversedKey) + first.get(key));
			} else {
				second.put(key, first.get(key));
			}
		}

		Map<String, Double> third = new TreeMap<String, Double>();

		for (String key : second.keySet()) {
			double amount = second.get(key);
			if (amount < 0) {
				String reversedKey = "" + key.substring(IDENTIFIER_LENGTH, IDENTIFIER_LENGTH * 2)
						+ key.substring(0, IDENTIFIER_LENGTH);
				third.put(reversedKey, -amount);
			} else {
				third.put(key, amount);
			}
		}

		long endTime = System.nanoTime();
		this.textArea.append(String.format("Calculation finished in %.6f ms\n", (endTime - startTime) / 1000000.0));
		this.textArea.append("\n");

		for (String key : third.keySet()) {
			String party1 = key.substring(0, IDENTIFIER_LENGTH);
			String party2 = key.substring(IDENTIFIER_LENGTH, IDENTIFIER_LENGTH * 2);
			double amount = third.get(key);
			if ((filterNameSet.isEmpty() || filterNameSet.contains(party1) || filterNameSet.contains(party2))
					&& third.get(key) >= 0.01) {
				this.textArea.append(String.format("%s needs to pay %s %.2f USD\n", party1, party2, amount));
			}
		}

		this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
	}

	public void saveLog(File outputFile) throws FileNotFoundException {
		PrintStream output = new PrintStream(outputFile);
		output.print(this.textArea.getText());
		output.close();
	}
}
