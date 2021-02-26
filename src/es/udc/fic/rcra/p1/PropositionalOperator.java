package es.udc.fic.rcra.p1;

public enum PropositionalOperator {

	CONJUNCTION("&"), DISJUNCTION("|"), NEGATION("-"), IMPLICATION(">"), 
	EQUIVALENCE("="), XOR("%");
	
	public final String symbol;
	
	PropositionalOperator(String symbol) {
		this.symbol = symbol;
	}
	
	public static String[] symbols() { return new String[] {"&","|","-",">","=","%"}; }
	
	public static PropositionalOperator operatorOf(String symbol) {
		for (PropositionalOperator op : values()) {
			if (op.symbol.equals(symbol)) {
				return op;
			}
		}
		return null;
	}
	
	public String toString() { return symbol; }
}
