package es.udc.fic.rcra.p1;

import static es.udc.fic.rcra.p1.PropositionalOperator.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Proposition {
	private static int start = 0;

	private PropositionalOperator op;
	private Proposition left;
	private Proposition right;
	private String atom;
	
	public Proposition(String atom) {
		this.atom = atom;
	}
	
	public Proposition(PropositionalOperator op, Proposition left) {
		if (op != NEGATION) {
			throw new IllegalArgumentException("Invalid unary propositional operator: " + op);
		}
		
		this.op = op;
		this.left = left;
	}
	
	public Proposition(PropositionalOperator op, Proposition left, Proposition right) {
		if (op == NEGATION) {
			throw new IllegalArgumentException("Invalid binary propositional operator: " + op);
		}

		this.op = op;
		this.left = left;
		this.right = right;
	}

	public PropositionalOperator getOperator() { return op; }
	public Proposition getLeft() { return left; }
	public Proposition getRight() { return right; }
	public String getAtom() { return atom; }
	public boolean isSimple() { return atom != null; }

	public Proposition negated() {
		return new Proposition(NEGATION, this);
	}

	public static Proposition create(String[] tokens) {
		PropositionalOperator op = PropositionalOperator.operatorOf(tokens[start]);

		if (op == null) {
			return new Proposition(tokens[start]);
		} else if (op == NEGATION) {
			start++;
			return new Proposition(op, create(tokens));
		} else {
			start++;
			Proposition newLeft = create(tokens);
			start++;
			Proposition newRight = create(tokens);
			return new Proposition(op, newLeft, newRight);
		}
	}
	
	public static List<Proposition> createPropositions(List<String> formulas) {
		List<Proposition> propositions = new ArrayList<>();
		
		for (String formula : formulas) {
			propositions.add(create(formula.split(" ")));
			start = 0;
		}
		
		return propositions;
	}

	/*
	Replace implications, equivalences and xors by conjunctions and disjunctions; and
	moves negations towards atoms.
	 */
	public Proposition toNNF() {
		if (isSimple()) {
			return this;
		}

		else if (op == NEGATION) {
			if (left.isSimple()) {
				return this;
			}

			// - - p is equal to p
			else if (left.op == NEGATION) {
				return left.left.toNNF();
			}

			// - | a b is equal to & - a - b
			else if (left.op == CONJUNCTION) {
				return new Proposition(DISJUNCTION, left.left.negated(), left.right.negated()).toNNF();
			}

			// - & a b is equal to | - a - b
			else if (left.op == DISJUNCTION) {
				return new Proposition(CONJUNCTION, left.left.negated(), left.right.negated()).toNNF();
			}

			// NNF(- p) is equal to NNF(- NNF(p)): parses firstly >,= and %, then solves the -
			else {
				return new Proposition(NEGATION, left.toNNF()).toNNF();
			}
		}

		// > a b is equal to | - a b
		else if (op == IMPLICATION) {
			return new Proposition(DISJUNCTION, left.negated(), right).toNNF();
		}

		// = a b is equal to & | a - b | - a b
		else if (op == EQUIVALENCE) {
			Proposition newLeft = new Proposition(DISJUNCTION, left, right.negated()).toNNF();
			Proposition newRight = new Proposition(DISJUNCTION, left.negated(), right).toNNF();
			return new Proposition(CONJUNCTION, newLeft, newRight);
		}

		// % a b is equal to & | a b | - a - b
		else if (op == XOR) {
			Proposition newLeft = new Proposition(DISJUNCTION, left, right).toNNF();
			Proposition newRight = new Proposition(DISJUNCTION, left.negated(), right.negated()).toNNF();
			return new Proposition(CONJUNCTION, newLeft, newRight);
		}

		// conjunction or disconjunction
		else {
			return new Proposition(op, left.toNNF(), right.toNNF());
		}
	}

	/*
	Apply distribution in order to move CONJUNCTIONS inwards and DISJUNCTIONS outwards
		- Precondition: proposition in NNF
	 */
	public Proposition toCNF() {
		if (isSimple() || op == NEGATION) {
			return this;
		}

		if (op == DISJUNCTION) {

			// | a & b c is equal to & | a b | a c
			if (right.op == CONJUNCTION) {
				Proposition newLeft = new Proposition(DISJUNCTION, left, right.left).toCNF();
				Proposition newRight = new Proposition(DISJUNCTION, left, right.right).toCNF();
				return new Proposition(CONJUNCTION, newLeft, newRight);
			}

			// | & a b c is equal to & | a c | b c
			else if (left.op == CONJUNCTION) {
				Proposition newLeft = new Proposition(DISJUNCTION, left.left, right).toCNF();
				Proposition newRight = new Proposition(DISJUNCTION, left.right, right).toCNF();
				return new Proposition(CONJUNCTION, newLeft, newRight);
			}

			// Search inside the subpropositions for CONJUNCTIONS, in order to move them outwards
			else {
				Proposition newLeft = left.toCNF();
				Proposition newRight = right.toCNF();

				if (newLeft.equals(left) && newRight.equals(right)) {
					return this;
				} else {
					return new Proposition(DISJUNCTION, newLeft, newRight).toCNF();
				}
			}
		}

		// CONJUNCTION
		else {
			return new Proposition(op, left.toCNF(), right.toCNF());
		}
	}

	public Set<Proposition> getClauses() {
		Set<Proposition> clauses = new HashSet<>();

		if (op == DISJUNCTION || op == NEGATION || isSimple()) {
			clauses.add(this);
		} else {
			clauses.addAll(left.getClauses());
			clauses.addAll(right.getClauses());
		}

		return clauses;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Proposition)) return false;

		Proposition that = (Proposition) o;

		if (op != that.op) return false;
		if (left != null ? !left.equals(that.left) : that.left != null) return false;
		if (right != null ? !right.equals(that.right) : that.right != null) return false;
		return atom != null ? atom.equals(that.atom) : that.atom == null;
	}

	@Override
	public int hashCode() {
		int result = op != null ? op.hashCode() : 0;
		result = 31 * result + (left != null ? left.hashCode() : 0);
		result = 31 * result + (right != null ? right.hashCode() : 0);
		result = 31 * result + (atom != null ? atom.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return isSimple() ? atom : op + " " + left + (right == null ? "" : " " + right);
	}
	
}


