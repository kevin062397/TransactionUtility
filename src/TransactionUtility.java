import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;

/**
 * This class is the entry point and the main user interface for <tt>TransactionUtility</tt>.
 *
 * @author Haoyuan Kevin Xia
 * @since 12/18/2021
 */

public class TransactionUtility extends JPanel implements ActionListener {
	public static final boolean PRINT_LOG = false;

	private final JButton openButton;
	private final JButton calculateButton;
	private final JButton saveButton;
	private final JTextArea textArea;
	private final JTextField filterNamesTextField;

	private final JFileChooser fileChooser;
	private File inputFile;

	private final FilterNamesManager filterNamesManager;

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
	 * Create the GUI and show it. For thread safety, this method should be invoked from the event dispatch thread.
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
		// frame.pack(); // Will override the frame size and location
		frame.setVisible(true);
	}

	public TransactionUtility() {
		super(new BorderLayout());

		// Top panel
		JPanel buttonsPanel = new JPanel();
		this.add(buttonsPanel, BorderLayout.NORTH);

		// Top panel - open button
		this.openButton = new JButton("Open");
		this.openButton.addActionListener(this);
		buttonsPanel.add(this.openButton);

		// Top panel - calculate button
		this.calculateButton = new JButton("Calculate");
		this.calculateButton.addActionListener(this);
		buttonsPanel.add(this.calculateButton);

		// Top panel - save button
		this.saveButton = new JButton("Save");
		this.saveButton.addActionListener(this);
		buttonsPanel.add(this.saveButton);

		// Center panel
		JScrollPane scrollPane = new JScrollPane();
		this.add(scrollPane, BorderLayout.CENTER);

		// Center panel - text area
		this.textArea = new JTextArea();
		this.textArea.setMargin(new Insets(5, 5, 5, 5));
		this.textArea.setEditable(false);
		this.textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		scrollPane.setViewportView(this.textArea);

		this.textArea.setText("Please choose a transaction file.");
		this.textArea.setCaretPosition(this.textArea.getDocument().getLength());

		// Bottom panel
		JPanel optionsPanel = new JPanel();
		this.add(optionsPanel, BorderLayout.SOUTH);
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

		// Bottom panel - filter name
		JPanel filterNamesPanel = new JPanel();
		optionsPanel.add(filterNamesPanel);

		JLabel filterNamesLabel = new JLabel("Filter by Name");
		filterNamesPanel.add(filterNamesLabel);

		this.filterNamesTextField = new JTextField();
		this.filterNamesTextField.setEditable(false);
		this.filterNamesTextField.setColumns(20);
		filterNamesPanel.add(this.filterNamesTextField);

		// Other initialization steps
		this.fileChooser = new JFileChooser();
		this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		this.filterNamesManager = FilterNamesManager.sharedInstance();
	}

	public void actionPerformed(ActionEvent e) {
		// Handle open button action
		if (e.getSource() == this.openButton) {
			int returnVal = this.fileChooser.showOpenDialog(TransactionUtility.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				this.inputFile = this.fileChooser.getSelectedFile();
				// This is where a real application would open the file
				this.textArea.setText("File opened: " + this.inputFile.getName() + "\n");
			} else {
				this.textArea.setText("Open command canceled by user.\n");
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
					this.textArea.append("Error\n\n");
					this.textArea.append(exception.getLocalizedMessage());
					exception.printStackTrace();
					this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
				}
			}
		}
	}

	public void calculate() throws IOException {
		// Parse file
		long startTime = System.nanoTime();

		TransactionFileParser parser = new TransactionFileParser(this.inputFile);
		TransactionPool pool = parser.parse(PRINT_LOG);

		int i = 0;
		for (TransactionRecord record : pool.getRecords()) {
			i++;
			this.textArea.append(String.format("%5d: %s\n", i, record.toString()));
		}

		long endTime = System.nanoTime();
		this.textArea.append("\n");
		this.textArea.append(String.format("Parsing finished in %.6f ms\n", (endTime - startTime) / 1000000.0));

		this.textArea.append("\n");
		this.textArea.append("\n");

		// Generate report
		startTime = System.nanoTime();

		this.filterNamesManager.setString(this.filterNamesTextField.getText());
		Set<String> filterNames = this.filterNamesManager.getAll();

		TransactionReport report = pool.getReport(0.1, null, filterNames);
		this.textArea.append(report.toString());

		endTime = System.nanoTime();
		this.textArea.append("\n");
		this.textArea.append(String.format("Calculation finished in %.6f ms\n", (endTime - startTime) / 1000000.0));

		// Finish
		this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
	}

	public void saveLog(File outputFile) throws FileNotFoundException {
		PrintStream output = new PrintStream(outputFile);
		output.print(this.textArea.getText());
		output.close();
	}
}
