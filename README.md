# Polish to Clingo parser

## Introduction

The purpose of this parser is to convert a sequence of formulas given in
[Polish notation](https://en.wikipedia.org/wiki/Polish_notation), like the following:

```
> | rain - weekend - happy .
```

into Clingo's format:

```
{rain;happy;weekend}.
:- rain, happy.
:- not weekend, happy.
```

## How does it works?

The convertion takes 6 consecutive steps:

1. Extract each Polish formula from the input (e.g. "> | rain - weekend happy .")
2. Extract atoms from these formulas (e.g. "{rain;happy;weekend}")
3. Convert each formula to a single proposition. Each proposition is defined
recursively as a combination of 2 propositions and an operator, although other
two constructors are allowed in order to create a negation or a simple atom.
4. Convert each formula into its [Negation Normal Form (NNF)](https://en.wikipedia.org/wiki/Negation_normal_form)
5. Convert each formula into its [Conjunctive Normal Form (CNF)](https://en.wikipedia.org/wiki/Conjunctive_normal_form)
6. Write atoms and each clause into the output file with Clingo's format

## Input format

Formulas from the input file must follow the Polish notation. The operators
allowed are the following:

| Character | Description | Logical operator |
|---|---|---|
| & | conjunction | ∧ |
| \| | disjunction | ∨ |
| - | negation | ¬ |
| > | implication | → |
| = | equivalence | ↔ |
| % | exclusive or | xor |
| 0 | false | ⊥ |
| 1 | true | ⊤ |

## Compilation

Execute from src/:
```
javac es/udc/fic/rcra/p1/PolishToClingo.java
```

## Execution

By default, it reads 'input.txt' as input and write 'output.lp' as de output:
```
java -cp src es.udc.fic.rcra.p1.PolishToClingo
```

but if you prefer to use other files:
```
java -cp src es.udc.fic.rcra.p1.PolishToClingo <in.txt> <out.lp>
```
