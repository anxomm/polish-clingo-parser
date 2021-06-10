package es.udc.fic.rcra.p1;

import java.io.File;
import java.io.FileNotFoundException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class IOParser {

	public static final String COMMENT_SYMBOL = "#";

	public static List<String> readPolishFile(String path) throws FileNotFoundException {
		List<String> formulas = new ArrayList<>();
		
		try (Scanner scanner = new Scanner(new File(path))) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				if (!line.isEmpty() && !line.startsWith((COMMENT_SYMBOL))) {
					int dot = line.indexOf('.');
					formulas.add(line.substring(0, dot).trim());  // without the final dot
				}
			}
		}
		return formulas;	
	}
	
	public static Set<String> extractAtoms(String formula) {
		Set<String> atoms = new HashSet<>();
		List<String> operators = Arrays.asList(PropositionalOperator.symbols());
		
		for (String s : formula.split(" ")) {
			if (!operators.contains(s) && !s.equals("0") && !s.equals("1")) {
				atoms.add(s);
			}
		}
		
		return atoms;
	}
	
	public static Set<String> extractAtoms(List<String> formulas) {
		Set<String> atoms = new HashSet<>();
		
		for (String formula : formulas) {
			atoms.addAll(extractAtoms(formula));
		}

		return atoms;
	}

	public static void writeClingoFile(String path, Set<String> atoms, List<String> originalFormulas,
									   List<Proposition> cnfFormulas) throws IOException {
		File output = new File(path);
		output.createNewFile();

		FileWriter writer = new FileWriter(output);
		writer.write(formatAtoms(atoms));

		for (int i=0; i<originalFormulas.size(); i++) {
			writer.write("% " + originalFormulas.get(i) + " .\n");
			writer.write(formatProposition(cnfFormulas.get(i)) + "\n");
		}
		writer.close();
	}

	private static String formatAtoms(Set<String> atoms) {
		String out = "{";

		for (String atom : atoms) {
			out += atom + ";";
		}

		out = out.substring(0, out.length()-1);
		return out + "}.\n\n";
	}

	private static String formatProposition(Proposition prop) {
		String out = "";
		Set<Proposition> clauses = prop.getClauses();

		for (Proposition clause : clauses) {
			out += ":- " + formatClause(clause) + ".\n";
		}

		return out;
	}

	private static String formatClause(Proposition prop) {
		if (prop.isSimple()) {
			return "not " + (prop.getAtom().equals("0") ? "#false" :
					(prop.getAtom().equals("1") ? "#true" : prop.getAtom()));
		} else if (prop.getOperator() == PropositionalOperator.NEGATION) {
			return formatClause(prop.getLeft()).replace("not ", "");
		} else {
			return formatClause(prop.getLeft()) + ", " + formatClause(prop.getRight());
		}
	}
}
