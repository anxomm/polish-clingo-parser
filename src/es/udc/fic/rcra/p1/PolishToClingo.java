package es.udc.fic.rcra.p1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PolishToClingo {

	public static void main(String[] args) throws IOException {
		String input = (args.length > 0 ? args[0] : "input.txt");
		String output = (args.length > 1 ? args[1] : "output.lp");
		
		// Supposing the path is passed, the file exists, each formula is well-formed and one per line
		List<String> formulas = IOParser.readPolishFile(input);
		
		// Supposing each atom is well-formed
		Set<String> atoms = IOParser.extractAtoms(formulas);
		
		// Create the propositions from the polish formulas
		List<Proposition> props = Proposition.createPropositions(formulas);

		// Create the CNF formulas
		List<Proposition> cnf = new ArrayList<>();
		for (Proposition p : props) {
			Proposition newProp = p.toNNF().toCNF();
			cnf.add(newProp);
		}

		// Convert to clingo format and write into a file
		IOParser.writeClingoFile(output, atoms, formulas, cnf);
	}
}
