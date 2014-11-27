package resolution;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class resolution extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	private JLabel inputLabel;
	private JTextField inputField;
	private JButton calcButton;

	public resolution() {
		super("Resolution");
		calcButton = new JButton("Berechnen");
		calcButton.addActionListener(this);
		inputField = new JTextField("", 40);
		inputLabel = new JLabel("Resolutionsmenge:");
		setLayout(new FlowLayout());
		setLocationRelativeTo(null);

		add(inputLabel);
		add(inputField);
		add(calcButton);
		pack();
		setVisible(true);
	}

	public static void main(String[] args) {
		resolution GUI = new resolution();
		GUI.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String input = inputField.getText();
		/*
		 * delete first and last bracket
		 */
		input = input.substring(1, input.length());
		input = input.substring(0, input.length() - 1);

		// kick out unimportant chars
		input = input.replace(" ", "").replace(",", "");

		/*
		 * Test if Set matches specific patterns Scheme: {ABC}{-A-BC}{CD}
		 */
		if (!input.matches("([{]([\\-]*[¬]*[A-Z])+[}])+")) {
			inputField.setText("Fehler in der Eingabe.");
			return;
		}

		Vector<Vector<Character>> vectorOfSets = new Vector<Vector<Character>>();
		int indexOfSets = 0;
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == '{') {
				vectorOfSets.add(new Vector<Character>());
				continue;
			} else if (input.charAt(i) == '}') {
				indexOfSets++;
				continue;
			} else {
				Vector<Character> latestVector = (Vector<Character>) vectorOfSets
						.elementAt(indexOfSets);
				if (input.charAt(i) != '-' && input.charAt(i) != '¬') {
					latestVector.add(input.charAt(i));
				} else {
					i++;
					latestVector.add(Character.toLowerCase(input.charAt(i)));
				}
			}
		}

		vectorOfSets = calculateResolution(vectorOfSets);

		String outputSet = "";
		outputSet += "{";
		for (Vector<Character> vCh : vectorOfSets) {
			if(vCh.size() == 0){
				JOptionPane.showMessageDialog(this, "Diese Formel ist unerfüllbar.", "Unerfüllbare Formel", JOptionPane.INFORMATION_MESSAGE);
			}
			outputSet += "{";
			for (char p : vCh) {
				if (Character.isLowerCase(p)) {
					outputSet += "¬";
				}
				outputSet += Character.toUpperCase(p);
			}
			outputSet += "}";
		}
		outputSet += "}";
		inputField.setText(outputSet);
	}

	private Vector<Vector<Character>> calculateResolution(
			Vector<Vector<Character>> vectorOfSets) {
		for (int i = 0; i < vectorOfSets.size(); i++) {
			for (int j = i; j < vectorOfSets.size(); j++) {
				// define primary clause
				Vector<Character> primClause = (Vector<Character>) vectorOfSets
						.elementAt(i);
				// define second clause
				Vector<Character> secClause = (Vector<Character>) vectorOfSets
						.elementAt(j);
				// go through all letters
				Vector<Character> newSet;
				for (int primClauseIter = 0; primClauseIter < primClause.size(); primClauseIter++) {
					for (int secClauseIter = 0; secClauseIter < secClause
							.size(); secClauseIter++) {
						if (primClause.elementAt(primClauseIter) == (char) (secClause
								.elementAt(secClauseIter) + 32)
								|| primClause.elementAt(primClauseIter) == (char) (secClause
										.elementAt(secClauseIter) - 32)) {

							newSet = new Vector<Character>();
							newSet.addAll(primClause);
							newSet.addAll(secClause);
							newSet.removeElement(primClause
									.elementAt(primClauseIter));
							newSet.removeElement(secClause
									.elementAt(secClauseIter));

							// add the set to the vector if it doesn't exists

							Set<Character> set = new HashSet<Character>();
							set.addAll(newSet);
							newSet.clear();
							newSet.addAll(set);
							Collections.sort(newSet);

							if (!vectorOfSets.contains(newSet)) {
								// System.out.println(newSet.toString());
								vectorOfSets.add(newSet);
								calculateResolution(vectorOfSets);
							}
						}
					}
				}
			}
		}
		return vectorOfSets;
	}

};
