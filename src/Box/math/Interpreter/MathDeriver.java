package Box.math.Interpreter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.List;

import Box.Interpreter.RuntimeError;
import Box.math.BoxMathTokenType;
import Box.math.Syntax.Term;
import Box.math.Syntax.Term.Assignment;
import Box.math.Syntax.Term.Binary;
import Box.math.Syntax.Term.Derivitive;
import Box.math.Syntax.Term.E;
import Box.math.Syntax.Term.Factorial;
import Box.math.Syntax.Term.Function;
import Box.math.Syntax.Term.Integral;
import Box.math.Syntax.Term.Literal;
import Box.math.Syntax.Term.Ln;
import Box.math.Syntax.Term.Log;
import Box.math.Syntax.Term.Mono;
import Box.math.Syntax.Term.NotDefined;
import Box.math.Syntax.Term.PI;
import Box.math.Syntax.Term.ToDerive;
import Box.math.Syntax.Term.Unary;
import Box.math.Syntax.Term.Variable;
import Box.math.Token.MathToken;

public class MathDeriver implements Term.Visitor<Object> {
	private Hashtable<String, Term> values = new Hashtable<String, Term>();
	private Hashtable<String, Term> base = new Hashtable<String, Term>();
	private Variable replace = null;
	private Variable replaceWith = null;
	private boolean shouldReplace = false;

	public MathDeriver() {

	}

	public Object derive(Term term, boolean derive, boolean integrate, Term.Variable replace, Term.Variable replaceWith,
			boolean shouldReplace) {
		this.replace = replace;
		this.replaceWith = replaceWith;
		this.shouldReplace = shouldReplace;
		Object evaluate = null;
		try {

			evaluate = evaluate(term,  derive,  integrate);
			this.replace = null;
			this.replaceWith = null;
			this.shouldReplace = false;
		} catch (RuntimeError e) {
			System.out.println(e.getMessage());
		}
		return evaluate;

	}

	private Object evaluate(Term term, boolean derive, boolean integrate) {
		return term.accept(this,  derive,  integrate);
	}

	@Override
	public Object visitAssignmentTerm(Assignment term, boolean derive, boolean integrate) {
		if (term.name instanceof Term.Function) {
			Term functionBody = ((Term.Function) term.name).functionBody;
			values.put(((Term.Function) term.name).name.lexeme, term.value);
			base.put(((Term.Function) term.name).name.lexeme, ((Term.Function) term.name).functionBody);
		} else if (term.name instanceof Term.Variable) {
			values.put(((Term.Variable) term.name).name.lexeme, term.value);
		}
		return null;
	}

	private Object lookUp(Function function) {

		Term term = values.get(function.name.lexeme);
		if (term == null) {
			MathToken name = new MathToken(BoxMathTokenType.FUN, function.name.lexeme, null, -1, -1, -1, -1);
			return new Term.NotDefined(name);
		}

		return term;
	}

	@Override
	public Object visitBinaryTerm(Binary term, boolean derive, boolean integrate) {
		Object left = null;
		Object right = null;
		Object leftDerivitive = null;
		Object rightDerivitive = null;
		Object total = null;
		MathToken operator = term.operator;

		switch (operator.type) {
		case PLUS:
			left = evaluate(term.left,  derive,  integrate);
			right = evaluate(term.right,  derive,  integrate);
			if (derive) {
				if (left instanceof Term.Function && right instanceof Term.Function) {

					Object leftevaluate = lookUp(((Term.Function) left));
					Object leftBodyEval = evaluate((Term) leftevaluate,  derive,  integrate);
					Object rightevaluate = lookUp(((Term.Function) right));
					Object rightBodyEval = evaluate((Term) rightevaluate,  derive,  integrate);
					return new Term.Binary((Term) leftBodyEval, term.operator, (Term) rightBodyEval);

				} else if (left instanceof Term.Function && right instanceof Double) {

					Object leftevaluate = lookUp(((Term.Function) left));
					Object leftBodyEval = evaluate((Term) leftevaluate,  derive,  integrate);

					return (Term) leftBodyEval;

				} else if (left instanceof Double && right instanceof Term.Function) {

					Object rightevaluate = lookUp(((Term.Function) right));
					Object rightBodyEval = evaluate((Term) rightevaluate,  derive,  integrate);

					return (Term) rightBodyEval;

				} else if (left instanceof Double && right instanceof Double) {

					return new Term.Literal(0.0);
				} else if (left instanceof Term.Variable && right instanceof Double) {
					if (((Term.Variable) left).name.lexeme.equalsIgnoreCase(replace.name.lexeme)) {
						return new Term.Literal(1.0);
					} else {
						return (Term) left;
					}

				}
			} else {
				if (left instanceof Term.Function && right instanceof Term.Function) {

					Object leftevaluate = lookUp(((Term.Function) left));
					Object leftBodyEval = evaluate((Term) leftevaluate,  derive,  integrate);
					Object rightevaluate = lookUp(((Term.Function) right));
					Object rightBodyEval = evaluate((Term) rightevaluate,  derive,  integrate);
					return new Term.Binary((Term) leftBodyEval, term.operator, (Term) rightBodyEval);

				} else if (left instanceof Term.Function && right instanceof Double) {

					Object leftevaluate = lookUp(((Term.Function) left));
					Object leftBodyEval = evaluate((Term) leftevaluate,  derive,  integrate);

					return new Term.Binary((Term) leftBodyEval, term.operator, new Term.Literal((Double) right));

				} else if (left instanceof Double && right instanceof Term.Function) {

					Object rightevaluate = lookUp(((Term.Function) right));
					Object rightBodyEval = evaluate((Term) rightevaluate,  derive,  integrate);

					return new Term.Binary(new Term.Literal((Double) left), term.operator, (Term) rightBodyEval);

				} else if (left instanceof Double && right instanceof Double) {

					return new Term.Binary(new Term.Literal((Double) left), term.operator,
							new Term.Literal((Double) right));
				} else if (left instanceof Term.Variable && right instanceof Double) {
					if (((Term.Variable) left).name.lexeme.equalsIgnoreCase(replace.name.lexeme)) {
						if (shouldReplace)
							return new Term.Binary(replaceWith, term.operator, new Term.Literal((Double) right));
						else
							return new Term.Binary((Term) left, term.operator, new Term.Literal((Double) right));
					} else {
						return new Term.Binary((Term) left, term.operator, new Term.Literal((Double) right));

					}
				}

			}
			System.out.println("totalPLUS: " + total);
			break;
		case MINUS:
			left = evaluate(term.left,  derive,  integrate);
			right = evaluate(term.right,  derive,  integrate);
			if (derive) {
				if (left instanceof Term.Function && right instanceof Term.Function) {
					Object leftevaluate = lookUp(((Term.Function) left));
					Object leftBodyEval = evaluate((Term) leftevaluate,  derive,  integrate);
					Object rightevaluate = lookUp(((Term.Function) right));
					Object rightBodyEval = evaluate((Term) rightevaluate,  derive,  integrate);
					return new Term.Binary((Term) leftBodyEval, term.operator, (Term) rightBodyEval);

				} else if (left instanceof Term.Function && right instanceof Double) {

					Object leftevaluate = lookUp(((Term.Function) left));
					Object leftBodyEval = evaluate((Term) leftevaluate,  derive,  integrate);

					return (Term) leftBodyEval;

				} else if (left instanceof Double && right instanceof Term.Function) {

					Object rightevaluate = lookUp(((Term.Function) right));
					Object rightBodyEval = evaluate((Term) rightevaluate,  derive,  integrate);

					return (Term) rightBodyEval;

				} else if (left instanceof Double && right instanceof Double) {

					return new Term.Literal(0.0);
				} else if (left instanceof Term.Variable && right instanceof Double) {
					if (((Term.Variable) left).name.lexeme.equalsIgnoreCase(replace.name.lexeme)) {
						return new Term.Literal(1.0);
					} else {
						return (Term) left;
					}

				}
			} else {
				if (left instanceof Term.Function && right instanceof Term.Function) {
					Object leftevaluate = lookUp(((Term.Function) left));
					Object leftBodyEval = evaluate((Term) leftevaluate,  derive,  integrate);
					Object rightevaluate = lookUp(((Term.Function) right));
					Object rightBodyEval = evaluate((Term) rightevaluate,  derive,  integrate);
					return new Term.Binary((Term) leftBodyEval, term.operator, (Term) rightBodyEval);

				} else if (left instanceof Term.Function && right instanceof Double) {

					Object leftevaluate = lookUp(((Term.Function) left));
					Object leftBodyEval = evaluate((Term) leftevaluate,  derive,  integrate);

					return new Term.Binary((Term) leftBodyEval, term.operator, new Term.Literal((Double) right));

				} else if (left instanceof Double && right instanceof Term.Function) {

					Object rightevaluate = lookUp(((Term.Function) right));
					Object rightBodyEval = evaluate((Term) rightevaluate,  derive,  integrate);

					return new Term.Binary(new Term.Literal((Double) left), term.operator, (Term) rightBodyEval);

				} else if (left instanceof Double && right instanceof Double) {

					return new Term.Binary(new Term.Literal((Double) left), term.operator,
							new Term.Literal((Double) right));
				} else if (left instanceof Term.Variable && right instanceof Double) {
					if (((Term.Variable) left).name.lexeme.equalsIgnoreCase(replace.name.lexeme)) {
						if (shouldReplace)
							return new Term.Binary(replaceWith, term.operator, new Term.Literal((Double) right));
						else
							return new Term.Binary((Term) left, term.operator, new Term.Literal((Double) right));
					} else {
						return new Term.Binary((Term) left, term.operator, new Term.Literal((Double) right));

					}
				}

			}
			System.out.println("totalMINUS: " + total);
			break;
		case POWER:
			left = evaluate(term.left,  derive,  integrate);
			right = evaluate(term.right,  derive,  integrate);
			if (derive) {
				if (left instanceof Double && right instanceof Double) {

					return new Term.Literal(0.0);
				} else if (left instanceof Term.Variable && right instanceof Double) {
					if (((Term.Variable) left).name.lexeme.equalsIgnoreCase(replace.name.lexeme)) {
						if (shouldReplace) {
							Double n = (Double) right;
							MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, 1);
							MathToken thepower = new MathToken(BoxMathTokenType.POWER, "*", null, -1, -1, -1, 1);
							Term xToTheNminusOne = new Term.Binary(replaceWith, thepower,
									new Term.Literal((Double) n - 1));
							Term nTimeXtoThePower = new Term.Binary(new Term.Literal((Double) n), times,
									xToTheNminusOne);

							return nTimeXtoThePower;
						} else {
							Double n = (Double) right;
							MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, 1);
							MathToken thepower = new MathToken(BoxMathTokenType.POWER, "*", null, -1, -1, -1, 1);
							Term xToTheNminusOne = new Term.Binary((Term) left, thepower,
									new Term.Literal((Double) n - 1));
							Term nTimeXtoThePower = new Term.Binary(new Term.Literal((Double) n), times,
									xToTheNminusOne);

							return nTimeXtoThePower;
						}
					} else {

						return new Term.Binary((Term.Variable) left, term.operator, new Term.Literal((Double) right));
					}
				} else if (left instanceof Term.Function && right instanceof Double) {
					Object leftevaluate = lookUp(((Term.Function) left));
					Object leftBodyEval = evaluate((Term) leftevaluate,  derive,  integrate);

					Double n = (Double) right;
					MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, 1);
					MathToken thepower = new MathToken(BoxMathTokenType.POWER, "*", null, -1, -1, -1, 1);
					Term xToTheNminusOne = new Term.Binary((Term) leftBodyEval, thepower,
							new Term.Literal((Double) n - 1));
					Term nTimeXtoThePower = new Term.Binary(new Term.Literal((Double) n), times, xToTheNminusOne);

					return nTimeXtoThePower;
				}

			} else {

				if (left instanceof Double && right instanceof Double) {

					return new Term.Binary(new Term.Literal((Double) left), term.operator,
							new Term.Literal((Double) right));
				} else if (left instanceof Term.Variable && right instanceof Double) {
					if (((Term.Variable) left).name.lexeme.equalsIgnoreCase(replace.name.lexeme)) {
						if (shouldReplace)
							return new Term.Binary(replaceWith, term.operator, new Term.Literal((Double) right));
						else
							return new Term.Binary((Term) left, term.operator, new Term.Literal((Double) right));

					} else {
						return new Term.Binary((Term.Variable) left, term.operator, new Term.Literal((Double) right));
					}
				} else if (left instanceof Term.Function && right instanceof Double) {
					Object leftevaluate = lookUp(((Term.Function) left));
					Object leftBodyEval = evaluate((Term) leftevaluate,  derive,  integrate);

					Term nTimeXtoThePower = new Term.Binary((Term) leftBodyEval, term.operator,
							new Term.Literal((Double) right));

					return nTimeXtoThePower;
				}
			}
			System.out.println("TotalPOWER: " + total);
			break;
		case DEVIDE:

			left = evaluate(term.left,  derive,  integrate);
			right = evaluate(term.right,  derive,  integrate);
			if (derive) {
				if (left instanceof Double && right instanceof Double) {
					return new Term.Literal(0.0);
				} else if (left instanceof Term.Variable && right instanceof Double) {
					if (((Term.Variable) left).name.lexeme.equalsIgnoreCase(replace.name.lexeme)) {
						return new Term.Binary(new Term.Literal(1.0), term.operator, new Term.Literal((Double) right));
					} else {
						return new Term.Binary((Term) left, term.operator, new Term.Literal((Double) right));
					}
				} else if (left instanceof Term.Function && right instanceof Double) {
					Object leftevaluate = lookUp(((Term.Function) left));
					Object leftBodyEval = evaluate((Term) leftevaluate,  derive,  integrate);
					return new Term.Binary((Term) leftBodyEval, term.operator, new Term.Literal((Double) right));
				} else if (left instanceof Double && right instanceof Term.Function) {
					Object rightevaluate = lookUp(((Term.Function) right));
					Object rightBodyEval = evaluate((Term) rightevaluate,  derive,  integrate);
					return new Term.Binary(new Term.Literal((Double) left), term.operator, (Term) rightBodyEval);
				} else if (left instanceof Term.Function && right instanceof Term.Function) {
					Object leftevaluate = lookUp(((Term.Function) left));
					Object rightevaluate = lookUp(((Term.Function) right));
					Object leftBodyEvalDerived = evaluate((Term) leftevaluate,  derive,  integrate);
					Object leftBodyEval = evaluate((Term) leftevaluate, false,false);
					Object rightBodyEvalDerived = evaluate((Term) rightevaluate,  derive,  integrate);
					Object rightBodyEval = evaluate((Term) rightevaluate, false,false);
					MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, 1);
					Binary rightTimesLeftDerivitive = new Term.Binary((Term) rightBodyEval, times,
							(Term) leftBodyEvalDerived);
					Binary leftTimesRightDerivitive = new Term.Binary((Term) leftBodyEval, times,
							(Term) rightBodyEvalDerived);
					MathToken minus = new MathToken(BoxMathTokenType.MINUS, "-", null, -1, -1, -1, 1);
					Binary rightLeftDMinusrightleftD = new Term.Binary((Term) rightTimesLeftDerivitive, minus,
							(Term) leftTimesRightDerivitive);

					MathToken power = new MathToken(BoxMathTokenType.POWER, "^", null, -1, -1, -1, 1);
					Binary rightPowerTwo = new Term.Binary((Term) rightBodyEval, power, new Term.Literal(2.0));

					MathToken devide = new MathToken(BoxMathTokenType.DEVIDE, "/", null, -1, -1, -1, 1);
					return new Term.Binary((Term) rightLeftDMinusrightleftD, devide, (Term) rightPowerTwo);

				}
			} else {
				if (left instanceof Double && right instanceof Double) {
					return new Term.Binary(new Term.Literal((Double) left), term.operator,
							new Term.Literal((Double) right));
				} else if (left instanceof Term.Variable && right instanceof Double) {
					if (((Term.Variable) left).name.lexeme.equalsIgnoreCase(replace.name.lexeme)) {
						if(shouldReplace)
						return new Term.Binary(replaceWith, term.operator, new Term.Literal((Double) right));
						else
							return new Term.Binary((Term)left, term.operator, new Term.Literal((Double) right));
					} else {
						return new Term.Binary((Term.Variable) left, term.operator, new Term.Literal((Double) right));
					}
				} else if (left instanceof Term.Function && right instanceof Double) {
					Object leftevaluate = lookUp(((Term.Function) left));
					Object leftBodyEval = evaluate((Term) leftevaluate,  derive,  integrate);
					return new Term.Binary((Term) leftBodyEval, term.operator, new Term.Literal((Double) right));
				} else if (left instanceof Double && right instanceof Term.Function) {
					Object rightevaluate = lookUp(((Term.Function) right));
					Object rightBodyEval = evaluate((Term) rightevaluate,  derive,  integrate);
					return new Term.Binary(new Term.Literal((Double) left), term.operator, (Term) rightBodyEval);
				} else if (left instanceof Term.Function && right instanceof Term.Function) {
					Object leftevaluate = lookUp(((Term.Function) left));
					Object rightevaluate = lookUp(((Term.Function) right));
					Object leftBodyEval = evaluate((Term) leftevaluate,  derive,  integrate);
					Object rightBodyEval = evaluate((Term) rightevaluate,  derive,  integrate);

					return new Term.Binary((Term) leftBodyEval, term.operator, (Term) rightBodyEval);

				}
			}
			System.out.println("totalDEVIDE: " + total);
			break;
		case TIMES:
			left = evaluate(term.left,  derive,  integrate);
			right = evaluate(term.right,  derive,  integrate);
			if (derive) {
				if (left instanceof Term.Function && right instanceof Term.Function) {

					Object leftevaluate = lookUp(((Term.Function) left));
					Object rightevaluate = lookUp(((Term.Function) right));
					Object theleft = evaluate((Term) leftevaluate, false,false);
					Object theright = evaluate((Term) rightevaluate, false,false);
					leftDerivitive = evaluate((Term) leftevaluate,  derive,  integrate);
					rightDerivitive = evaluate((Term) rightevaluate,  derive,  integrate);
					MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, 1);
					Binary leftTimes = new Term.Binary((Term) theleft, times, (Term) rightDerivitive);
					Binary rightTimes = new Term.Binary((Term) theright, times, (Term) leftDerivitive);
					MathToken plus = new MathToken(BoxMathTokenType.PLUS, "+", null, -1, -1, -1, 1);
					return new Term.Binary(leftTimes, plus, rightTimes);
				} else if (left instanceof Term.Function && right instanceof Double) {

					Object leftevaluate = lookUp(((Term.Function) left));

					leftDerivitive = evaluate((Term) leftevaluate,  derive,  integrate);
					return new Term.Binary(new Term.Literal((Double) right), term.operator, (Term) leftDerivitive);
				} else if (left instanceof Double && right instanceof Term.Function) {

					Object rightevaluate = lookUp(((Term.Function) right));

					rightDerivitive = evaluate((Term) rightevaluate,  derive,  integrate);
					return new Term.Binary(new Term.Literal((Double) left), term.operator, (Term) rightDerivitive);
				} else if (left instanceof Double && right instanceof Double) {
					MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, 1);
					return new Term.Binary(new Term.Literal((Double) left), times, new Term.Literal((Double) right));
				} else if (left instanceof Term.Variable && right instanceof Double) {
					if (((Term.Variable) left).name.lexeme.equalsIgnoreCase(replace.name.lexeme)) {
						return new Term.Literal((Double) right);
					} else {
						MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, 1);
						return new Term.Binary((Term) left, times, new Term.Literal((Double) right));
					}
				}
			} else {
				if (left instanceof Term.Function && right instanceof Term.Function) {

					Object leftevaluate = lookUp(((Term.Function) left));
					Object rightevaluate = lookUp(((Term.Function) right));
					Object theleft = evaluate((Term) leftevaluate,  derive,  integrate);
					Object theright = evaluate((Term) rightevaluate,  derive,  integrate);
					MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, 1);
					return new Term.Binary((Term) theleft, times, (Term) theright);
				} else if (left instanceof Term.Function && right instanceof Double) {

					Object leftevaluate = lookUp(((Term.Function) left));
					Object theleft = evaluate((Term) leftevaluate,  derive,  integrate);
					MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, 1);
					return new Term.Binary(new Term.Literal((Double) right), times, (Term) theleft);
				} else if (left instanceof Double && right instanceof Term.Function) {

					Object rightevaluate = lookUp(((Term.Function) right));
					Object theright = evaluate((Term) rightevaluate,  derive,  integrate);
					MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, 1);
					return new Term.Binary(new Term.Literal((Double) left), times, (Term) theright);
				} else if (left instanceof Double && right instanceof Double) {
					return new Term.Binary(new Term.Literal((Double) left), term.operator,
							new Term.Literal((Double) right));
				} else if (left instanceof Term.Variable && right instanceof Double) {
					if (((Term.Variable) left).name.lexeme.equalsIgnoreCase(replace.name.lexeme)) {
						if(shouldReplace)
						return new Term.Binary(replaceWith, term.operator, new Term.Literal((Double) right));
						else
							return new Term.Binary((Term)left, term.operator, new Term.Literal((Double) right));
					} else {
						return new Term.Binary((Term.Variable) left, term.operator, new Term.Literal((Double) right));

					}
				}
			}
			System.out.println("totalTIMES: " + total);
			break;
		default:
			break;
		}
		return total;
	}

	@Override
	public Object visitMonoTerm(Mono term, boolean derive, boolean integrate) {
		MathToken operator = term.operator;

		Object total = null;
		switch (operator.type) {
		case SIN:
			if (derive && !integrate) {
				Object evaluate = evaluate(term.value,  derive,  integrate);
				Object evaluate2 = evaluate(term.value, false,false);
				MathToken cos = new MathToken(BoxMathTokenType.COS, "cos", null, -1, -1, -1, 1);
				Mono cosStmt = new Term.Mono((Term) evaluate2, cos);
				MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, 1);
				return new Term.Binary((Term) evaluate, times, cosStmt);
			} else if (!derive && !integrate) {
				Object evaluate2 = evaluate(term.value, false,false);
				MathToken sin = new MathToken(BoxMathTokenType.SIN, "sin", null, -1, -1, -1, 1);
				return new Term.Mono((Term) evaluate2, sin);
			} else if (!derive && integrate) {
				if(!(term.value instanceof Variable)) {
					return "dont know how to do this yet";
				}
				MathToken cos = new MathToken(BoxMathTokenType.COS, "cos", null, -1, -1, -1, 1);
				MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, 1);
				Mono cosStmt = new Term.Mono((Term) term.value, cos);
				Literal literal = new Term.Literal(BigDecimal.valueOf(-1.0));
				return new Term.Binary((Term) literal, times, cosStmt);
				
			}else {
				
			}

		case COS:
			if (derive && !integrate) {
				Object evaluate = evaluate(term.value,  derive,  integrate);

				Object evaluate2 = evaluate(term.value, false,false);
				MathToken sin = new MathToken(BoxMathTokenType.SIN, "sin", null, -1, -1, -1, 1);
				Mono sinStmt = new Term.Mono((Term) evaluate2, sin);
				MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, 1);

				Binary evaluateTimesNegOne = new Term.Binary(new Term.Literal(-1.0), times, (Term) evaluate);
				return new Term.Binary(evaluateTimesNegOne, times, sinStmt);

			} else if (!derive && !integrate) {

				Object evaluate2 = evaluate(term.value, false,false);
				MathToken cos = new MathToken(BoxMathTokenType.COS, "cos", null, -1, -1, -1, 1);
				return new Term.Mono((Term) evaluate2, cos);

			}else if (!derive && integrate) {
				if(!(term.value instanceof Variable)) {
					return "dont know how to do this yet";
				}
				MathToken sin = new MathToken(BoxMathTokenType.SIN, "sin", null, -1, -1, -1, 1);
				Mono sinStmt = new Term.Mono((Term) term.value, sin);
				
				return sinStmt;
			}else {
				
			}

		case TAN:

			if (derive && !integrate) {
				Object evaluate = evaluate(term.value,  derive,  integrate);

				Object evaluate2 = evaluate(term.value, false,false);
				MathToken cos = new MathToken(BoxMathTokenType.COS, "cos", null, -1, -1, -1, 1);
				Mono cosStmt = new Term.Mono((Term) evaluate2, cos);
				MathToken power = new MathToken(BoxMathTokenType.POWER, "^", null, -1, -1, -1, 1);
				Binary cosTothePowerOfTwo = new Term.Binary(cosStmt, power, new Term.Literal(2.0));
				MathToken devide = new MathToken(BoxMathTokenType.DEVIDE, "/", null, -1, -1, -1, 1);
				Binary oneOverCosPowerTwo = new Term.Binary(new Term.Literal(1.0), devide, cosTothePowerOfTwo);

				MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, 1);

				return new Term.Binary((Term) evaluate, times, oneOverCosPowerTwo);

			} else if (!derive && !integrate) {

				Object evaluate2 = evaluate(term.value, false,false);
				MathToken tan = new MathToken(BoxMathTokenType.TAN, "tan", null, -1, -1, -1, 1);
				return new Term.Mono((Term) evaluate2, tan);
			}else if (!derive && integrate) {
				if(!(term.value instanceof Variable)) {
					return "dont know how to do this yet";
				}
				MathToken cos = new MathToken(BoxMathTokenType.COS, "cos", null, -1, -1, -1, 1);
				MathToken ln = new MathToken(BoxMathTokenType.LN, "ln", null, -1, -1, -1, 1);
				Mono cosStmt = new Term.Mono((Term) term.value, cos);
				Ln lnStmt =new Term.Ln(ln,cosStmt);
				MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, 1);
				Binary binary = new Term.Binary(new Term.Literal(BigDecimal.valueOf(-1.0)), times, lnStmt);
				return binary;
			}else {
				
			}

		}

		return total;
	}

	@Override
	public Object visitLnTerm(Ln term, boolean derive, boolean integrate) {
		if (derive) {

			try {
				if (findX(term)) {
					if (term.value instanceof Binary) {

						if (((Binary) term.value).operator.type == BoxMathTokenType.POWER) {
							MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, -1);
							MathToken ln = new MathToken(BoxMathTokenType.LN, "ln", null, -1, -1, -1, -1);
							Ln lnOfleft = new Term.Ln(ln, ((Binary) term.value).left);
							return new Term.Binary(((Binary) term.value).right, times, lnOfleft);
							
						} else if (((Binary) term.value).operator.type == BoxMathTokenType.DEVIDE) {
							MathToken ln = new MathToken(BoxMathTokenType.LN, "ln", null, -1, -1, -1, -1);
							Ln lnOfleft = new Term.Ln(ln, ((Binary) term.value).left);
							Ln lnOfright = new Term.Ln(ln, ((Binary) term.value).right);
							MathToken minus = new MathToken(BoxMathTokenType.MINUS, "-", null, -1, -1, -1, -1);
							return new Term.Binary(lnOfleft, minus, lnOfright);
							
						} else if (((Binary) term.value).operator.type == BoxMathTokenType.TIMES) {
							MathToken ln = new MathToken(BoxMathTokenType.LN, "ln", null, -1, -1, -1, -1);
							Ln lnOfleft = new Term.Ln(ln, ((Binary) term.value).left);
							Ln lnOfright = new Term.Ln(ln, ((Binary) term.value).right);
							MathToken plus = new MathToken(BoxMathTokenType.PLUS, "+", null, -1, -1, -1, -1);
							return new Term.Binary(lnOfleft, plus, lnOfright);
						} else {
							Object evaluate = evaluate(term.value, false,false);
							Object evaluate2 = evaluate(term.value,  derive,  integrate);
							MathToken devide = new MathToken(BoxMathTokenType.DEVIDE, "/", null, -1, -1, -1, -1);
							Binary oneDevidedByEvaluate = new Term.Binary(new Term.Literal(1.0), devide, (Term)evaluate);
							MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, -1);
							return new Term.Binary( oneDevidedByEvaluate, times, (Term)evaluate2);
							

						}

					} else if (term.value instanceof E) {
						return term.value;
					} else if (term.value instanceof Variable) {

						Term eval2Term = lookUp(((Variable) term.value).name);
						if (!(eval2Term instanceof NotDefined)) {

							Object evaluateB = evaluate(eval2Term,  derive,  integrate);
							
							MathToken devide = new MathToken(BoxMathTokenType.DEVIDE, "/", null, -1, -1, -1, -1);
							return new Term.Binary(new Term.Literal(1.0), devide, (Term)evaluateB);
							
						} else if (eval2Term instanceof NotDefined) {
							MathToken devide = new MathToken(BoxMathTokenType.DEVIDE, "/", null, -1, -1, -1, -1);
							return new Term.Binary(new Term.Literal(1.0), devide, new Term.Variable(((NotDefined) eval2Term).name));
							
						}

					} else if (term.value instanceof Log) {

						MathToken lnMathToken = new MathToken(BoxMathTokenType.LN, "ln", null, -1, -1, -1, -1);
						MathToken devide = new MathToken(BoxMathTokenType.DEVIDE, "/", null, -1, -1, -1, -1);
						MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, -1);

						Ln lnTen = new Term.Ln(lnMathToken, new Term.Literal(10.0));
						Binary denominator = new Term.Binary((Term) term.value, times, lnTen);
						Binary dontTakeDerivitiveOf = new Term.Binary(new Term.Literal(1.0), devide, denominator);

						Object derivitiveOfLog = evaluate(term.value,  derive,  integrate);

						Object notDerived = evaluate(dontTakeDerivitiveOf, false,false);

						
						return new Term.Binary( (Term)derivitiveOfLog, times, (Term)notDerived);
						

					} else {
						Object evaluate2 = evaluate(term.value,  derive,  integrate);
						Object evaluate3 = evaluate(term.value, false,false);
						
						
						MathToken devide = new MathToken(BoxMathTokenType.DEVIDE, "/", null, -1, -1, -1, -1);
						Binary oneDevidedByEvaluate = new Term.Binary(new Term.Literal(1.0), devide, (Term)evaluate3);
						MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, -1);
						return new Term.Binary( oneDevidedByEvaluate, times, (Term)evaluate2);
						

					}
				} else {
					Object evaluate = evaluate(term.value, false,false);
					MathToken ln = new MathToken(BoxMathTokenType.LN, "ln", null, -1, -1, -1, -1);
					return new Term.Mono((Term)evaluate, ln);
					
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {

				e.printStackTrace();
			}
			return "howdy";

		} else {
			Object evaluate = evaluate(term.value,  derive,  integrate);
			MathToken ln = new MathToken(BoxMathTokenType.LN, "ln", null, -1, -1, -1, -1);
			return new Term.Mono((Term)evaluate, ln);
		}
	}

	private Term lookUp(MathToken name) {
		if(shouldReplace) {
			if(name.lexeme.equalsIgnoreCase(replace.name.lexeme)) {
				return replaceWith;
			}
		}
		Term term = values.get(name.lexeme);
		if (term == null) {
			return new Term.NotDefined(name);
		}

		return term;
	}
	
	private boolean findX(Term value) throws IllegalArgumentException, IllegalAccessException {

		if (value instanceof Variable) {
			if (((Variable) value).name.lexeme.equalsIgnoreCase("x"))
				return true;
		}

		Field[] declaredFields = value.getClass().getDeclaredFields();
		boolean found = false;
		for (Field field : declaredFields) {
			if (field.getType().equals(Term.class)) {
				found = findX((Term) field.get(value));
			}
			if (found)
				break;
		}

		return found;
	}
	@Override
	public Object visitLogTerm(Log term, boolean derive, boolean integrate) {
		if (derive) {

			try {
				if (findX(term)) {
					if (term.valueBase instanceof Binary) {

						if (((Binary) term.valueBase).operator.type == BoxMathTokenType.POWER) {
							MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, -1);
							MathToken log = new MathToken(BoxMathTokenType.LOG, "log", null, -1, -1, -1, -1);
							Log logOfleft = new Term.Log(log, ((Binary) term.valueBase).left,term.value);
							return new Term.Binary(((Binary) term.valueBase).right, times, logOfleft);
							
						} else if (((Binary) term.valueBase).operator.type == BoxMathTokenType.DEVIDE) {
							MathToken log = new MathToken(BoxMathTokenType.LOG, "log", null, -1, -1, -1, -1);
							Log logOfleft = new Term.Log(log, ((Binary) term.valueBase).left,term.value);
							Log logOfright = new Term.Log(log, ((Binary) term.valueBase).right,term.value);
							MathToken minus = new MathToken(BoxMathTokenType.MINUS, "-", null, -1, -1, -1, -1);
							return new Term.Binary(logOfleft, minus, logOfright);
							
						} else if (((Binary) term.valueBase).operator.type == BoxMathTokenType.TIMES) {
							MathToken log = new MathToken(BoxMathTokenType.LOG, "log", null, -1, -1, -1, -1);
							Log logOfleft = new Term.Log(log, ((Binary) term.valueBase).left,term.value);
							Log logOfright = new Term.Log(log, ((Binary) term.valueBase).right,term.value);
							MathToken plus = new MathToken(BoxMathTokenType.PLUS, "+", null, -1, -1, -1, -1);
							return new Term.Binary(logOfleft, plus, logOfright);
						} else {
							Object evaluate = evaluate(term.valueBase, false,false);
							Object evaluate2 = evaluate(term.valueBase,  derive,  integrate);
							MathToken devide = new MathToken(BoxMathTokenType.DEVIDE, "/", null, -1, -1, -1, -1);
							Binary oneDevidedByEvaluate = new Term.Binary(new Term.Literal(1.0), devide, (Term)evaluate);
							MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, -1);
							return new Term.Binary( oneDevidedByEvaluate, times, (Term)evaluate2);
							

						}

					} else if (term.valueBase instanceof Variable) {

						Term eval2Term = lookUp(((Variable) term.valueBase).name);
						if (!(eval2Term instanceof NotDefined)) {

							Object evaluateB = evaluate(eval2Term,  derive,  integrate);
							
							MathToken devide = new MathToken(BoxMathTokenType.DEVIDE, "/", null, -1, -1, -1, -1);
							return new Term.Binary(new Term.Literal(1.0), devide, (Term)evaluateB);
							
						} else if (eval2Term instanceof NotDefined) {
							MathToken devide = new MathToken(BoxMathTokenType.DEVIDE, "/", null, -1, -1, -1, -1);
							return new Term.Binary(new Term.Literal(1.0), devide, new Term.Variable(((NotDefined) eval2Term).name));
							
						}

					} else if (term.valueBase instanceof Ln) {

						MathToken devide = new MathToken(BoxMathTokenType.DEVIDE, "/", null, -1, -1, -1, -1);
						MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, -1);

						Object derivitiveOfLog = evaluate(term.valueBase,  derive,  integrate);

						Object notDerived = evaluate(term.valueBase, false,false);
						Binary dontTakeDerivitiveOf = new Term.Binary(new Term.Literal(1.0), devide,(Term) notDerived);
						
						return new Term.Binary( (Term)dontTakeDerivitiveOf, times, (Term)derivitiveOfLog);
						

					} else {
						
						MathToken lnMathToken = new MathToken(BoxMathTokenType.LN, "ln", null, -1, -1, -1, -1);
						MathToken devide = new MathToken(BoxMathTokenType.DEVIDE, "/", null, -1, -1, -1, -1);
						MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, -1);

						Ln lnTen = new Term.Ln(lnMathToken, new Term.Literal(10.0));
						Binary denominator = new Term.Binary((Term) term.value, times, lnTen);
						Binary dontTakeDerivitiveOf = new Term.Binary(new Term.Literal(1.0), devide, denominator);

						Object derivitiveOfLog = evaluate(term.value,  derive,  integrate);

						Object notDerived = evaluate(dontTakeDerivitiveOf, false,false);

						
						return new Term.Binary( (Term)derivitiveOfLog, times, (Term)notDerived);

					}
				} else {
					Object evaluate = evaluate(term.valueBase, false,false);
					MathToken log = new MathToken(BoxMathTokenType.LOG, "log", null, -1, -1, -1, -1);
					return new Term.Mono((Term)evaluate, log);
					
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {

				e.printStackTrace();
			}
			return "howdy";

		} else {
			Object evaluate = evaluate(term.valueBase,  derive,  integrate);
			MathToken log = new MathToken(BoxMathTokenType.LOG, "log", null, -1, -1, -1, -1);
			return new Term.Mono((Term)evaluate, log);
		}
	}

	@Override
	public Object visitFactorialTerm(Factorial term, boolean derive, boolean integrate) {

		return null;
	}

	@Override
	public Object visitUnaryTerm(Unary term, boolean derive, boolean integrate) {

		return null;
	}

	@Override
	public Object visitFunctionTerm(Function term, boolean derive, boolean integrate) {

		return term;
	}

	@Override
	public Object visitDerivitiveTerm(Derivitive term, boolean derive, boolean integrate) {

		return null;
	}

	@Override
	public Object visitToDeriveTerm(ToDerive term, boolean derive, boolean integrate) {
		return evaluate(term.left, true,false);

	}

	@Override
	public Object visitVariableTerm(Variable term, boolean derive, boolean integrate) {
		
		if(shouldReplace) {
			if(term.name.lexeme.equalsIgnoreCase(replace.name.lexeme)){
				return replaceWith;
			}else
				return term;
		}else
		return term;
	}

	@Override
	public Object visitLiteralTerm(Literal term, boolean derive, boolean integrate) {

		return term.literal;
	}

	@Override
	public Object visitETerm(E term, boolean derive, boolean integrate) {

		return Math.E;
	}

	@Override
	public Object visitPITerm(PI term, boolean derive, boolean integrate) {

		return Math.PI;
	}

	@Override
	public Object visitNotDefinedTerm(NotDefined term, boolean derive, boolean integrate) {

		return term;
	}

	@Override
	public Object visitIntegralTerm(Integral term, boolean derive, boolean integrate) {
		
		return null;
	}

}
