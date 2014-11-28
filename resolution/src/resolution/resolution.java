package resolution;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String input = inputField.getText();
		/*
		 * delete first and last bracket
		 */
		input = input.substring(1, input.length());
		input = input.substring(0, input.length() - 1);

		int numberOfOpenBrackets = input.length()
				- input.replace("{", "").length();
		int numberOfCloseBrackets = input.length()
				- input.replace("}", "").length();
		if (numberOfOpenBrackets != numberOfCloseBrackets) {
			inputField.setText("Fehler in der Eingabe.");
			return;
		}

		// kick out unimportant chars
		input = input.replace(" ", "").replace(",", "");

		for (int i = 0; i < input.length(); i++) {
			/*
			 * Test if Set matches specifig patterns Scheme: {ABC}{-A-BC}{CD}
			 */
			if (!input.matches("([{]([\\-]*[A-Z])+[}])+")) {
				inputField.setText("Fehler in der Eingabe.");
				return;
			}
		}

		Vector vectorOfSets = new Vector();
		int indexOfSets = 0;
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == '{') {
				vectorOfSets.add(new HashSet<Character>());
				continue;
			} else if (input.charAt(i) == '}') {
				indexOfSets++;
				continue;
			} else {
				HashSet<Character> latestHashSet = (HashSet<Character>) vectorOfSets
						.elementAt(indexOfSets);
				if (input.charAt(i) != '-') {
					latestHashSet.add(input.charAt(i));
				} else {
					i++;
					latestHashSet.add(Character.toLowerCase(input.charAt(i)));
				}
			}
		}
		for (int i = 0; i < vectorOfSets.size(); i++) {
			HashSet<Character> hashSet = (HashSet<Character>) vectorOfSets
					.elementAt(i);
			for (char ch : hashSet) {
				System.out.println(i + ":" + ch);
			}
		}
		vectorOfSets = calculateResolution(vectorOfSets);
		for(int i = 0; i < vectorOfSets.size(); i++){
			System.out.print("{");
			for(char p : (HashSet<Character>) vectorOfSets.elementAt(i)){
					System.out.print(p);
			}
			System.out.print("}");
		}
	}

	private Vector calculateResolution(Vector vectorOfSets) {
		int vecSize = vectorOfSets.size();
		for (int i = 0; i < vecSize; i++) {
			System.out.println(vecSize);
			// define primary clause
			HashSet<Character> primClause = (HashSet<Character>) vectorOfSets
					.elementAt(i);
			for (int j = i; j < vecSize; j++) {
				System.out.println(i + ":" + j);
				// define second clause
				HashSet<Character> secClause = (HashSet<Character>) vectorOfSets
						.elementAt(j);
				// go through all letters
				for (char primClauseChar : primClause) {
					for (char secClauseChar : secClause) {
						if (primClauseChar == (char)(secClauseChar + 32)
								|| primClauseChar == (char)(secClauseChar - 32)) {
							HashSet<Character> tmpPrimHashSet = new HashSet<Character>(primClause);
							tmpPrimHashSet.remove(primClauseChar);
							HashSet<Character> tmpSecHashSet = new HashSet<Character>(secClause);
							tmpSecHashSet.remove(secClauseChar);
							HashSet<Character> newHashSet = new HashSet<Character>(tmpPrimHashSet);
							newHashSet.addAll(tmpSecHashSet);
							// add the set to the vector if it doesnt exists
							if(!vectorOfSets.contains(newHashSet)){
								vectorOfSets.add(newHashSet);
								++vecSize;
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
