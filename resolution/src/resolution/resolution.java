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

/**
 * A simple resolution calculating program
 * @author Vaphen
 */

public class resolution extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	/*
	 * Important elements
	 */
	private JLabel inputLabel;
//	private JLabel resultLabel;
	private JTextField inputField;
//	private JTextField resultField;
	private JButton calcButton;

	/**
	 * Set all elements of the UI
	 */
	public resolution() {
		super("Resolution");
		calcButton = new JButton("Berechnen");
		calcButton.addActionListener(this);
		inputField = new JTextField("", 40);
		inputLabel = new JLabel("Resolutionsmenge:");
	//	resultField = new JTextField();
		setLayout(new FlowLayout());
		setLocationRelativeTo(null);

		add(inputLabel);
		add(inputField);
		add(calcButton);
		pack();
		setVisible(true);
	}
	
	/**
	 * Create a new UI
	 * @param args Not used
	 */
	public static void main(String[] args) {
		resolution GUI = new resolution();
		GUI.setDefaultCloseOperation(EXIT_ON_CLOSE);
		GUI.setResizable(false);
	}

	/**
	 * Button push-event does the calculation
	 */
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

		/*
		 * Save all clauses in vectors.
		 * Put all of them in another vector.
		 * Negated variables are saved as lower-case characters
		 */
		Vector<Vector<Character>> vectorOfSets = new Vector<Vector<Character>>();
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == '{') {
				vectorOfSets.add(new Vector<Character>());
				continue;
			} else if (input.charAt(i) == '}') {
				continue;
			} else {
				Vector<Character> latestVector = vectorOfSets.lastElement();
				if (input.charAt(i) != '-' && input.charAt(i) != '¬') {
					latestVector.add(input.charAt(i));
				} else {
					i++;
					latestVector.add(Character.toLowerCase(input.charAt(i)));
				}
			}
		}

		/*
		 * Get the resoluted sets
		 */
		vectorOfSets = calculateResolution(vectorOfSets);

		/*
		 * Output the result of the resolution
		 */
		String outputSet = "";
		outputSet += "{";
		for (Vector<Character> vCh : vectorOfSets) {
			/*
			 * If the set is empty it is a unfulfillable formula
			 */
			if(vCh.size() == 0){
				JOptionPane.showMessageDialog(this, "Diese Formel ist unerfüllbar.", "Unerfüllbare Formel", JOptionPane.INFORMATION_MESSAGE);
			}
			outputSet += "{";
			for (char p : vCh) {
				if (Character.isLowerCase(p)) {
					outputSet += "¬";
				}
				outputSet += Character.toUpperCase(p);
				outputSet += ",";
			}
			outputSet = outputSet.substring(0, outputSet.length() - 1);
			outputSet += "},";
		}
		
		outputSet = outputSet.substring(0, outputSet.length() - 1);
		outputSet += "}";

		inputField.setText(outputSet);
	}

	private Vector<Vector<Character>> calculateResolution(Vector<Vector<Character>> vectorOfSets) {
		
		/*
		 * For test-usage:
		 * {{AB}{A-B}{-AC}{-CD}} 
		 * is resoluted
		 * {{A,B},{A,¬B},{¬A,C},{¬C,D},{A},{B,C},{C,¬B},{A,C},{D,¬A},{C},{B,D},{D,¬B},{A,D},{D},{C,D}}
		 */
		int startAmountOfSets = vectorOfSets.size();
		
		for (int i = 0; i < startAmountOfSets; i++) {
			// define primary clause (for comparison)
			Vector<Character> primClause = (Vector<Character>) vectorOfSets.elementAt(i);
			for (int j = i + 1; j < vectorOfSets.size(); j++) {
				// define second clause (to be compared)
				Vector<Character> secClause = (Vector<Character>) vectorOfSets.elementAt(j);
				// go through all letters
				for (int primClauseIter = 0; primClauseIter < primClause.size(); primClauseIter++) {
					for (int secClauseIter = 0; secClauseIter < secClause.size(); secClauseIter++) {
						/*
						 *  Check if primClause contains a character which can be resoluted by secClause
						 *  It is possible because not A is saved as small letter.
						 *  Thats why we can compare the ASCII-Values
						 */
						if (primClause.elementAt(primClauseIter) == (char) (secClause.elementAt(secClauseIter) + 32)
								|| primClause.elementAt(primClauseIter) == (char) (secClause.elementAt(secClauseIter) - 32)) {

							// new Set which may be a new resolution set 
							Vector<Character> newSet = new Vector<Character>();
							newSet.addAll(primClause);
							newSet.addAll(secClause);
							// remove resoluted atomic formulas; removeElement just deletes one element
							newSet.removeElement(primClause.elementAt(primClauseIter));
							newSet.removeElement(secClause.elementAt(secClauseIter));

							// remove duplicates in Vector
							Set<Character> set = new HashSet<Character>();
							set.addAll(newSet);
							newSet.clear();
							newSet.addAll(set);
							// sort the Vector that it becomes unique
							Collections.sort(newSet);
							
							// add the set to the vector if it doesn't exist
							if (!vectorOfSets.contains(newSet)) {
								vectorOfSets.add(newSet);
							}
						}
					}
				}
			}
		}
		
		for (int i = startAmountOfSets + 1; i < vectorOfSets.size(); i++) {
			// define primary clause (for comparison)
			Vector<Character> primClause = (Vector<Character>) vectorOfSets.elementAt(i);
			for (int j = startAmountOfSets + (i - startAmountOfSets - 1); j > 0; j--) {
				// define second clause (to be compared)
				Vector<Character> secClause = (Vector<Character>) vectorOfSets.elementAt(j);
				// go through all letters
				for (int primClauseIter = 0; primClauseIter < primClause.size(); primClauseIter++) {
					for (int secClauseIter = 0; secClauseIter < secClause.size(); secClauseIter++) {
						/*
						 *  Check if primClause contains a character which can be resoluted by secClause
						 *  It is possible because not A is saved as small letter.
						 *  Thats why we can compare the ASCII-Values
						 */
						if (primClause.elementAt(primClauseIter) == (char) (secClause.elementAt(secClauseIter) + 32)
								|| primClause.elementAt(primClauseIter) == (char) (secClause.elementAt(secClauseIter) - 32)) {

							// new Set which may be a new resolution set 
							Vector<Character> newSet = new Vector<Character>();
							newSet.addAll(primClause);
							newSet.addAll(secClause);
							// remove resoluted atomic formulas; removeElement just deletes one element
							newSet.removeElement(primClause.elementAt(primClauseIter));
							newSet.removeElement(secClause.elementAt(secClauseIter));

							// remove duplicates in Vector
							Set<Character> set = new HashSet<Character>();
							set.addAll(newSet);
							newSet.clear();
							newSet.addAll(set);
							// sort the Vector that it becomes unique
							Collections.sort(newSet);
							
							// add the set to the vector if it doesn't exist
							if (!vectorOfSets.contains(newSet)) {
								vectorOfSets.add(newSet);
								/*
								 * TODO: check if the program works without this line
								 */
								//calculateResolution(vectorOfSets);
							}
						}
					}
				}
			}
		}
		return vectorOfSets;
	}

};
