package Box.math.Interpreter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Hashtable;
import java.util.List;

import Box.math.Syntax.*;
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
import Box.Interpreter.RuntimeError;
import Box.math.*;

public class MathInterpreter implements Term.Visitor<Object> {

	private Hashtable<String, Term> values = new Hashtable<String, Term>();
	private Hashtable<String, Term> base = new Hashtable<String, Term>();
	int functionDepth = 0;

	public MathInterpreter() {

	}

	public void interpret(List<Term> statements) {
		try {
			for (Term trem : statements) {
				Object evaluate = evaluate(trem, false, false);

				System.out.println(" " + evaluate);
			}
		} catch (RuntimeError e) {
			System.out.println(e.getMessage());
		}

	}

	private Object evaluate(Term term, boolean derive, boolean integrate) {
		return term.accept(this, derive, integrate);
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

	@Override
	public Object visitBinaryTerm(Binary term, boolean derive, boolean integrate) {
		Object total = null;
		MathToken operator = term.operator;

		switch (operator.type) {
		case PLUS:
			if (derive && !integrate)
				total = plusDerive(term, derive);
			else if (!derive && !integrate)
				total = plusDerive(term, derive);
			else if (!derive && integrate) {
				total = plusIntegrate(term, integrate);
			} else {

			}

			System.out.println("totalPLUS: " + total);
			break;
		case MINUS:
			if (derive && !integrate)
				total = minusDerive(term, derive);
			else if (!derive && !integrate)
				total = minusDerive(term, derive);
			else if (!derive && integrate) {
				total = minusIntegrate(term, integrate);
			} else {

			}
			System.out.println("totalMINUS: " + total);
			break;
		case POWER:
			if (derive && !integrate) {
				total = powerDerive(term, derive);
			} else if (!derive && !integrate) {
				total = powerDontDerive(term);
			} else if (!derive && integrate) {
				total = powerIntegrate(term, integrate);

			} else {

			}
			System.out.println("TotalPOWER: " + total);
			break;
		case DEVIDE:

			if (derive && !integrate) {
				total = devideDerive(term, derive);
			} else if (!derive && !integrate) {

				total = devideDontDerive(term);
			} else if (!derive && integrate) {

			} else {

			}
			System.out.println("totalDEVIDE: " + total);
			break;
		case TIMES:
			if (derive && !integrate) {
				total = timesDerive(term, derive);
			} else if (!derive && !integrate) {
				total = timesDontDerive(term);
			} else if (!derive && integrate) {

			} else {

			}
			System.out.println("totalTIMES: " + total);
			break;
		default:
			break;
		}
		return total;

	}

	private Object powerIntegrate(Binary term, boolean integrate) {
		Object total = null;
		Object left = evaluate(term.left, false, integrate);
		Object right = evaluate(term.right, false, integrate);
		Object leftToUse = determinIntegralPower(integrate, left, right);

		if (leftToUse instanceof String) {
			total = leftToUse;
		} else {
			total = leftToUse;
		}

		return total;
	}

	private Object determinIntegralPower(boolean integrate, Object target, Object toThePowerOf) {
		Object toUse = null;
		if (target instanceof Variable) {
			Term lookUpLeft = lookUp(((Variable) target).name);
			if (lookUpLeft instanceof NotDefined && toThePowerOf instanceof Literal)
				toUse = "((" + ((NotDefined) lookUpLeft).name.lexeme + "^(" + ((Literal) toThePowerOf).literal
						+ "+1))/(" + ((Literal) toThePowerOf).literal + "+1))";
			else if (lookUpLeft instanceof NotDefined && toThePowerOf instanceof String)
				toUse = "((" + ((NotDefined) lookUpLeft).name.lexeme + "^(" + toThePowerOf + "+1))/(" + toThePowerOf
						+ "+1))";

			else {
				Object leftDerived = evaluate(lookUpLeft, false, integrate);
				if (leftDerived instanceof Literal && toThePowerOf instanceof BigDecimal)

					toUse = BigDecimal.valueOf((Double) ((Literal) leftDerived).literal)
							.pow(((BigDecimal) toThePowerOf).intValue() + 1)
							.divide(((BigDecimal) toThePowerOf).add(BigDecimal.ONE));
				else if (leftDerived instanceof Literal && toThePowerOf instanceof String)
					toUse = "((" + ((Literal) leftDerived).literal + "^(" + toThePowerOf + "+1))/(" + toThePowerOf
							+ "+1))";
				else if (leftDerived instanceof Literal && toThePowerOf instanceof Literal) {
					BigDecimal pow = BigDecimal.valueOf((Double) ((Literal) leftDerived).literal)
							.pow(BigDecimal.valueOf((Double) ((Literal) toThePowerOf).literal).intValue() + 1);
					toUse = pow.divide(
							BigDecimal.valueOf((Double) ((Literal) toThePowerOf).literal).add(BigDecimal.ONE),
							new MathContext(99));
				} else if (leftDerived instanceof Function)
					toUse = evaluate(((Function) leftDerived).functionBody, false, integrate);
				else
					toUse = "((" + leftDerived + "^(" + toThePowerOf + "+1))/(" + toThePowerOf + "+1))";
			}
		} else if (target instanceof Function) {
			toUse = evaluate(((Function) target).functionBody, false, integrate);
		} else if (target instanceof Literal) {

			toUse = BigDecimal.valueOf((Double) ((Literal) target).literal);
		} else if (target instanceof PI) {

			toUse = BigDecimal.valueOf(Math.PI);
		} else if (target instanceof E) {
			toUse = BigDecimal.valueOf(Math.E);

		} else {
			toUse = target;
		}
		return toUse;
	}

	private Object plusIntegrate(Binary term, boolean integrate) {
		Object total = null;
		Object left = evaluate(term.left, false, integrate);

		Object right = evaluate(term.right, false, integrate);

		Object leftDerToUse = determinIntegral(integrate, left);
		Object rightDerToUse = determinIntegral(integrate, right);

		if (leftDerToUse instanceof String || rightDerToUse instanceof String) {
			total = "(" + leftDerToUse + "+" + rightDerToUse + ")";
		} else {
			total = (((BigDecimal) leftDerToUse).add((BigDecimal) rightDerToUse));
		}
		return total;
	}

	private Object minusIntegrate(Binary term, boolean integrate) {
		Object total = null;
		Object left = evaluate(term.left, false, integrate);

		Object right = evaluate(term.right, false, integrate);

		Object leftDerToUse = determinIntegral(integrate, left);
		Object rightDerToUse = determinIntegral(integrate, right);

		if (leftDerToUse instanceof String || rightDerToUse instanceof String) {
			total = "(" + leftDerToUse + "-" + rightDerToUse + ")";
		} else {
			total = (((BigDecimal) leftDerToUse).subtract((BigDecimal) rightDerToUse));
		}
		return total;
	}

	private Object determinIntegral(boolean integrate, Object target) {
		Object toUse = null;
		if (target instanceof Variable) {
			Term lookUpLeft = lookUp(((Variable) target).name);
			if (lookUpLeft instanceof NotDefined)
				toUse = "((" + ((NotDefined) lookUpLeft).name.lexeme + "^" + "2)/2)";
			else {
				Object leftDerived = evaluate(lookUpLeft, false, integrate);
				if (leftDerived instanceof Literal)

					toUse = BigDecimal.valueOf((Double) ((Literal) leftDerived).literal).pow(2)
							.divide(BigDecimal.valueOf(2.0));

				else if (leftDerived instanceof Function)
					toUse = evaluate(((Function) leftDerived).functionBody, false, integrate);
				else
					toUse = leftDerived;
			}
		} else if (target instanceof Function) {
			toUse = evaluate(((Function) target).functionBody, false, integrate);
		} else if (target instanceof Literal) {

			toUse = BigDecimal.valueOf((Double) ((Literal) target).literal);
		} else if (target instanceof PI) {

			toUse = BigDecimal.valueOf(Math.PI);
		} else if (target instanceof E) {
			toUse = BigDecimal.valueOf(Math.E);

		} else {
			toUse = target;
		}
		return toUse;
	}

	private Object timesDontDerive(Binary term) {
		Object total = null;
		Object left = evaluate(term.left, false, false);

		Object right = evaluate(term.right, false, false);

		Object leftDerToUse = determinDerivitive(false, left);
		Object rightDerToUse = determinDerivitive(false, right);

		if (leftDerToUse instanceof String || rightDerToUse instanceof String) {
			total = "(" + leftDerToUse + "*" + rightDerToUse + ")";
		} else {
			total = (((BigDecimal) leftDerToUse).multiply((BigDecimal) rightDerToUse));
		}
		return total;
	}

	private Object timesDerive(Binary term, boolean derive) {
		Object total = null;
		Object left = evaluate(term.left, derive, false);
		Object leftunD = evaluate(term.left, false, false);

		Object right = evaluate(term.right, derive, false);
		Object rightunD = evaluate(term.right, false, false);

		Object leftDerToUse = determinDerivitive(true, left);
		Object leftunDerToUse = determinDerivitive(false, leftunD);
		Object rightDerToUse = determinDerivitive(true, right);
		Object rightunDerToUse = determinDerivitive(false, rightunD);

		if (leftDerToUse instanceof String || leftunDerToUse instanceof String || rightDerToUse instanceof String
				|| rightunDerToUse instanceof String) {
			total = "((" + leftunDerToUse + "*" + rightDerToUse + ")+(" + rightunDerToUse + "*" + leftDerToUse + "))";
		} else {
			BigDecimal bigDecimal = ((BigDecimal) leftunDerToUse).multiply((BigDecimal) rightDerToUse);
			BigDecimal multiply = ((BigDecimal) rightunDerToUse).multiply((BigDecimal) leftDerToUse);
			total = bigDecimal.add(multiply);
		}
		return total;
	}

	private Object devideDontDerive(Binary term) {
		Object total = null;
		Object left = evaluate(term.left, false, false);

		Object right = evaluate(term.right, false, false);

		Object leftDerToUse = determinDerivitive(false, left);
		Object rightDerToUse = determinDerivitive(false, right);

		if (leftDerToUse instanceof String || rightDerToUse instanceof String) {
			total = "(" + leftDerToUse + "/" + rightDerToUse + ")";
		} else {
			total = ((BigDecimal) leftDerToUse).divide((BigDecimal) rightDerToUse, new MathContext(99));
		}
		return total;
	}

	private Object devideDerive(Binary term, boolean derive) {
		Object total = null;
		Object left = evaluate(term.left, derive, false);
		Object leftunD = evaluate(term.left, false, false);

		Object right = evaluate(term.right, derive, false);
		Object rightunD = evaluate(term.right, false, false);

		Object leftDerToUse = determinDerivitive(derive, left);
		Object leftunDerToUse = determinDerivitive(false, leftunD);
		Object rightDerToUse = determinDerivitive(derive, right);
		Object rightunDerToUse = determinDerivitive(false, rightunD);

		if (leftDerToUse instanceof String || leftunDerToUse instanceof String || rightDerToUse instanceof String
				|| rightunDerToUse instanceof String) {
			total = "(((" + rightunDerToUse + "*" + leftDerToUse + ")-(" + leftunDerToUse + "*" + rightDerToUse + "))/("
					+ rightunDerToUse + ")^2)";
		} else {
			BigDecimal pow = ((BigDecimal) rightunDerToUse).pow(2);
			BigDecimal d = ((BigDecimal) leftunDerToUse).multiply((BigDecimal) rightDerToUse);
			BigDecimal e = ((BigDecimal) rightunDerToUse).multiply((BigDecimal) leftDerToUse);
			BigDecimal e2 = e.subtract(d);
			total = e2.divide(pow, new MathContext(99));
		}
		return total;
	}

	private Object powerDerive(Binary term, boolean derive) {
		Object total = null;
		Object left = evaluate(term.left, derive, false);
		Object leftunD = evaluate(term.left, false, false);
		Object right = evaluate(term.right, derive, false);
		Object rightunD = evaluate(term.right, false, false);
		Object leftToUse = determinDerivitive(false, left);
		Object leftunDToUse = determinDerivitive(false, leftunD);
		Object rightToUse = determinDerivitive(false, right);
		Object rightunDToUse = determinDerivitive(false, rightunD);

		if (left instanceof E && rightToUse instanceof BigDecimal) {
			total = ((BigDecimal) leftToUse).pow(((BigDecimal) rightToUse).toBigInteger().intValueExact());
		} else if (left instanceof E && rightToUse instanceof String) {
			total = leftToUse + "^" + rightToUse;
		} else {
			MathToken lnOp = new MathToken(BoxMathTokenType.LN, "ln", null, -1, -1, -1, -1);
			MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, -1);
			if (right instanceof Variable && (leftunDToUse instanceof String || rightunDToUse instanceof String)) {
				Ln ln = new Term.Ln(lnOp, term.left);
				Binary xtimesln = new Term.Binary((Term) rightunD, times, ln);
				Object xtimeslnevaluated = evaluate(xtimesln, true, false);
				Object xtimeslnevaluatedToUse = determinDerivitive(false, xtimeslnevaluated);

				total = leftunDToUse + "^" + rightunDToUse + "*" + xtimeslnevaluatedToUse;
			} else if (right instanceof Variable
					&& (leftunDToUse instanceof BigDecimal && rightunDToUse instanceof BigDecimal)) {
				Ln ln = new Term.Ln(lnOp, term.left);
				Binary xtimesln = new Term.Binary((Term) rightunD, times, ln);
				Object xtimeslnevaluated = evaluate(xtimesln, true, false);
				Object xtimeslnevaluatedToUse = determinDerivitive(false, xtimeslnevaluated);
				if (xtimeslnevaluatedToUse instanceof String)
					total = leftunDToUse + "^" + rightunDToUse + "*" + xtimeslnevaluatedToUse;
				else {
					BigDecimal valueOf = ((BigDecimal) leftunDToUse).pow(((BigDecimal) rightunDToUse).intValue());
					BigDecimal valueOf2 = (BigDecimal) xtimeslnevaluatedToUse;
					total = valueOf.multiply(valueOf2);
				}
			} else if (leftToUse instanceof String && rightToUse instanceof String) {
				total = "(" + rightToUse + "*" + leftToUse + "^(" + rightToUse + "-1))";
			} else if (leftToUse instanceof BigDecimal && rightToUse instanceof BigDecimal) {
				total = ((BigDecimal) rightToUse).multiply(((BigDecimal) leftToUse)
						.pow(((BigDecimal) rightToUse).subtract(BigDecimal.ONE).intValueExact()));
			} else if (leftToUse instanceof String && rightToUse instanceof BigDecimal) {
				total = "(" + rightToUse + "*" + leftToUse + "^(" + (((BigDecimal) rightToUse).subtract(BigDecimal.ONE))
						+ "))";
			} else if (leftToUse instanceof BigDecimal && rightToUse instanceof String) {

				total = "(" + rightToUse + "*" + leftToUse + "^(" + rightToUse + "-1))";
			}
		}
		return total;

	}

	private Object powerDontDerive(Binary term) {
		Object total = null;
		Object left = evaluate(term.left, false, false);
		Object right = evaluate(term.right, false, false);
		Object leftToUse = determinDerivitive(false, left);
		Object rightToUse = determinDerivitive(false, right);

		if (leftToUse instanceof String && rightToUse instanceof String) {
			total = leftToUse + "^(" + rightToUse + ")";
		} else if (leftToUse instanceof BigDecimal && rightToUse instanceof BigDecimal) {
			total = ((BigDecimal) leftToUse).pow(((BigDecimal) rightToUse).intValueExact());
		} else if (leftToUse instanceof String && rightToUse instanceof BigDecimal) {
			total = leftToUse + "^(" + ((BigDecimal) rightToUse) + ")";
		} else if (leftToUse instanceof BigDecimal && rightToUse instanceof String) {

			total = leftToUse + "^(" + rightToUse + ")";
		}
		return total;
	}

	private Object plusDerive(Binary term, boolean derive) {
		Object total = null;
		Object left = evaluate(term.left, derive, false);
		Object right = evaluate(term.right, derive, false);
		Object leftToUse = determinDerivitive(derive, left);
		Object rightToUse = determinDerivitive(derive, right);

		if (leftToUse instanceof String && rightToUse instanceof String) {
			total = leftToUse + "+" + rightToUse;
		} else if (leftToUse instanceof BigDecimal && rightToUse instanceof BigDecimal) {
			total = ((BigDecimal) leftToUse).add((BigDecimal) rightToUse);
		} else if (leftToUse instanceof String && rightToUse instanceof BigDecimal) {
			total = leftToUse + "+" + rightToUse;
		} else if (leftToUse instanceof BigDecimal && rightToUse instanceof String) {
			total = leftToUse + "+" + rightToUse;
		}
		return total;
	}

	private Object determinDerivitive(boolean derive, Object target) {
		Object toUse = null;
		if (target instanceof Variable) {
			Term lookUpLeft = lookUp(((Variable) target).name);
			if (lookUpLeft instanceof NotDefined)
				if (derive)
					toUse = BigDecimal.valueOf(1.0);
				else
					toUse = ((NotDefined) lookUpLeft).name.lexeme;
			else {
				Object leftDerived = evaluate(lookUpLeft, derive, false);
				if (leftDerived instanceof Literal)
					if (derive && !(target instanceof Variable))
						toUse = BigDecimal.valueOf(0.0);
					else if (!derive && !(target instanceof Variable))
						toUse = BigDecimal.valueOf((Double) ((Literal) leftDerived).literal);
					else if (!derive && (target instanceof Variable))
						toUse = BigDecimal.valueOf((Double) ((Literal) leftDerived).literal);
					else
						toUse = BigDecimal.valueOf(1.0);

				else if (leftDerived instanceof Function)
					toUse = evaluate(((Function) leftDerived).functionBody, derive, false);
				else
					toUse = leftDerived;
			}
		} else if (target instanceof Function) {
			toUse = evaluate(((Function) target).functionBody, derive, false);
		} else if (target instanceof Literal) {
			if (derive)
				toUse = BigDecimal.valueOf(0.0);
			else
				toUse = BigDecimal.valueOf((Double) ((Literal) target).literal);
		} else if (target instanceof PI) {
			if (derive)
				toUse = BigDecimal.valueOf(0.0);
			else
				toUse = BigDecimal.valueOf(Math.PI);
		} else if (target instanceof E) {
			toUse = BigDecimal.valueOf(Math.E);

		} else {
			toUse = target;
		}
		return toUse;
	}

	private Object minusDerive(Binary term, boolean derive) {
		Object total = null;
		Object left = evaluate(term.left, derive, false);
		Object right = evaluate(term.right, derive, false);
		Object leftToUse = determinDerivitive(derive, left);
		Object rightToUse = determinDerivitive(derive, right);

		rightToUse = determinDerivitive(derive, right);

		if (leftToUse instanceof String && rightToUse instanceof String) {
			total = leftToUse + "-" + rightToUse;
		} else if (leftToUse instanceof BigDecimal && rightToUse instanceof BigDecimal) {
			total = ((BigDecimal) leftToUse).subtract((BigDecimal) rightToUse);
		} else if (leftToUse instanceof String && rightToUse instanceof BigDecimal) {
			total = leftToUse + "-" + rightToUse;
		} else if (leftToUse instanceof BigDecimal && rightToUse instanceof String) {
			total = leftToUse + "-" + rightToUse;
		}
		return total;
	}

	private Object lookUp(Function function) {

		Term term = values.get(function.name.lexeme);
		if (term == null) {
			MathToken name = new MathToken(BoxMathTokenType.FUN, function.name.lexeme, null, -1, -1, -1, -1);
			return new Term.NotDefined(name);
		}

		return term;
	}

	private Term lookUp(MathToken name) {

		Term term = values.get(name.lexeme);
		if (term == null) {
			return new Term.NotDefined(name);
		}

		return term;
	}

	@Override
	public Object visitMonoTerm(Mono term, boolean derive, boolean integrate) {
		Object total = null;
		switch (term.operator.type) {
		case SIN:
			if (derive && !integrate) {
				total = sinDerive(term, derive, integrate);
			} else if (!derive && !integrate) {
				total = sinDontDerive(term, derive, integrate);
			} else if (!derive && integrate) {
				total = sinIntegrate(term, integrate);
			} else {

			}
			break;
		case COS:
			if (derive && !integrate) {
				total = cosDerive(term, derive, integrate);
			} else if (!derive && !integrate) {
				total = cosDontDerive(term, derive, integrate);
			} else if (!derive && integrate) {

			} else {

			}

			break;
		case TAN:
			if (derive && !integrate) {
				total = tanDerive(term, derive, integrate);
			} else if (!derive && !integrate) {
				total = tanDontDerive(term, derive, integrate);
			} else if (!derive && integrate) {

			} else {

			}

			break;
		case SINH:
			if (derive && !integrate) {
				total = sinhDerive(term, derive, integrate);
			} else if (!derive && !integrate) {
				total = sinhDontDerive(term, derive, integrate);
			} else if (!derive && integrate) {

			} else {

			}

			break;
		case COSH:
			if (derive && !integrate) {
				total = coshDerive(term, derive, integrate);
			} else if (!derive && !integrate) {
				total = coshDontDerive(term, derive, integrate);
			} else if (!derive && integrate) {

			} else {

			}

			break;
		case TANH:
			if (derive && !integrate) {
				total = tanhDerive(term, derive, integrate);
			} else if (!derive && !integrate) {
				total = tanhDontDerive(term, derive, integrate);
			} else if (!derive && integrate) {

			} else {

			}
			break;
		default:
			break;
		}

		return total;
	}

	public BigDecimal S(BigDecimal x) {
		BigDecimal total = BigDecimal.ZERO;
		int greaterOrLessThen = x.compareTo(BigDecimal.ZERO);
		boolean isNeg = false;
		if (greaterOrLessThen == -1) {
			isNeg = true;
			x = x.negate();
		}

		int ThreeBiggerOrSmaller = x.compareTo(BigDecimal.valueOf(3.0));

		if (ThreeBiggerOrSmaller == -1 || ThreeBiggerOrSmaller == 0) {
			BigDecimal n = BigDecimal.ZERO;
			

			BigDecimal two = powerBig(BigDecimal.valueOf(2.0), BigDecimal.valueOf(-1 - 2 * n.intValue()));
			BigDecimal add = BigDecimal.valueOf(Math.PI).pow(1 + 2 * n.intValue());
			BigDecimal pow = BigDecimal.ONE.negate().multiply(x.pow(4)).pow(n.intValue());
			BigDecimal numerator = two.multiply(add.multiply(pow));
			BigDecimal denominator = factorial(BigDecimal.valueOf(2.0).multiply(n).add(BigDecimal.ONE))
					.multiply(BigDecimal.valueOf(4.0).multiply(n).add(BigDecimal.valueOf(3.0)));
			BigDecimal c0 = numerator.divide(denominator, new MathContext(999));
			total = c0.add(f(x, n));
			if (isNeg)
				return (total.negate()).multiply(x.pow(3));
			else
				return total.multiply(x.pow(3));
		} else {
			BigDecimal cos = BigDecimal.valueOf(Math.cos(Math.PI*Math.pow(x.doubleValue(), 2)/2));
			BigDecimal sin = BigDecimal.valueOf(Math.sin(Math.PI*Math.pow(x.doubleValue(), 2)/2));
			BigDecimal term1 = BigDecimal.valueOf(1.0/2.0);
			BigDecimal term2 = cos.divide(BigDecimal.valueOf(Math.PI).multiply(x),new MathContext(999)).negate();
			BigDecimal term3 = sin.divide((BigDecimal.valueOf(Math.PI).pow(2)).multiply(x.pow(3)),new MathContext(999)).negate();
			BigDecimal term4 = BigDecimal.valueOf(3).multiply(cos.divide((BigDecimal.valueOf(Math.PI).pow(3)).multiply(x.pow(5)),new MathContext(999)));
			BigDecimal term5 = BigDecimal.valueOf(15).multiply(sin.divide((BigDecimal.valueOf(Math.PI).pow(4)).multiply(x.pow(7)),new MathContext(999)));
			BigDecimal term6 = BigDecimal.valueOf(105).multiply(cos.divide((BigDecimal.valueOf(Math.PI).pow(5)).multiply(x.pow(9)),new MathContext(999))).negate();
			BigDecimal term7 = BigDecimal.valueOf(945).multiply(sin.divide((BigDecimal.valueOf(Math.PI).pow(6)).multiply(x.pow(11)),new MathContext(999))).negate();
			total = term1.add(term2.add(term3.add(term4.add(term5.add(term6.add(term7))))));
			
			
			
			if (isNeg)
				return total.negate();
			else
				return total;
		}

	}

	private BigDecimal f(BigDecimal x, BigDecimal n) {

		BigDecimal total = BigDecimal.ZERO;
		n = n.add(BigDecimal.ONE);
		int compareTo = n.compareTo(BigDecimal.valueOf(67.0));
		if (compareTo == 0) {
			return BigDecimal.ZERO;
		} else {
			BigDecimal negativeOne = BigDecimal.ONE.negate();

			if (n.equals(BigDecimal.ONE)) {
				BigDecimal two = powerBig(BigDecimal.valueOf(2.0), BigDecimal.valueOf(-1 - 2 * n.intValue()));
				BigDecimal add = BigDecimal.valueOf(Math.PI).pow(1 + 2 * n.intValue());
				BigDecimal pow = (BigDecimal.ONE.negate().multiply(x.pow(4))).pow(n.intValue());
				BigDecimal numerator = two.multiply(add.multiply(pow));
				BigDecimal denominator = factorial(BigDecimal.valueOf(2.0).multiply(n).add(BigDecimal.ONE))
						.multiply(BigDecimal.valueOf(4.0).multiply(n).add(BigDecimal.valueOf(3.0)));
				BigDecimal b1 = numerator.divide(denominator, new MathContext(999));

				BigDecimal a1 = BigDecimal.ONE;
				BigDecimal f = f(x, n);
				BigDecimal add2 = a1.add(f);
				total = b1.divide(add2, new MathContext(999));
			} else {
				BigDecimal two = powerBig(BigDecimal.valueOf(2.0), BigDecimal.valueOf(-1 - 2 * n.intValue()));
				BigDecimal add = BigDecimal.valueOf(Math.PI).pow(1 + 2 * n.intValue());
				BigDecimal pow = (BigDecimal.ONE.negate().multiply(x.pow(4))).pow(n.intValue());
				BigDecimal numerator = two.multiply(add.multiply(pow));
				BigDecimal denominator = factorial(BigDecimal.valueOf(2.0).multiply(n).add(BigDecimal.ONE))
						.multiply(BigDecimal.valueOf(4.0).multiply(n).add(BigDecimal.valueOf(3.0)));
				BigDecimal c2 = numerator.divide(denominator, new MathContext(999));

				BigDecimal m = n.subtract(BigDecimal.ONE);

				BigDecimal two2 = powerBig(BigDecimal.valueOf(2.0), BigDecimal.valueOf(-1 - 2 * m.intValue()));
				BigDecimal add2 = BigDecimal.valueOf(Math.PI).pow(1 + 2 * m.intValue());
				BigDecimal pow2 = (BigDecimal.ONE.negate().multiply(x.pow(4))).pow(m.intValue());
				BigDecimal numerator2 = two2.multiply(add2.multiply(pow2));
				BigDecimal denominator2 = factorial(BigDecimal.valueOf(2.0).multiply(m).add(BigDecimal.ONE))
						.multiply(BigDecimal.valueOf(4.0).multiply(m).add(BigDecimal.valueOf(3.0)));
				BigDecimal c1 = numerator2.divide(denominator2, new MathContext(999));

				BigDecimal b2 = (c2.divide(c1, new MathContext(99))).negate();
				BigDecimal a2 = BigDecimal.ONE.add(c2.divide(c1, new MathContext(99)));
				total = b2.divide(a2.add(f(x, n)), new MathContext(999));

			}

			return total;
		}

	}

	public static BigDecimal powerBig(BigDecimal base, BigDecimal exponent) {

		BigDecimal ans = new BigDecimal(1.0);
		BigDecimal k = new BigDecimal(1.0);
		BigDecimal t = new BigDecimal(-1.0);
		BigDecimal no = new BigDecimal(0.0);

		if (exponent != no) {
			BigDecimal absExponent = exponent.signum() > 0 ? exponent : t.multiply(exponent);
			while (absExponent.signum() > 0) {
				ans = ans.multiply(base);
				absExponent = absExponent.subtract(BigDecimal.ONE);
			}

			if (exponent.signum() < 0) {
				// For negative exponent, must invert
				ans = k.divide(ans);
			}
		} else {
			// exponent is 0
			ans = k;
		}

		return ans;
	}

	private BigDecimal factorial(BigDecimal x) {
		BigDecimal i = BigDecimal.ONE;
		BigDecimal fact = BigDecimal.ONE;
		for (; x.compareTo(i) == 0 || x.compareTo(i) == 1; i = i.add(BigDecimal.valueOf(1.0))) {

			fact = fact.multiply(i);
		}

		return fact;
	}

	public static int factorial(int num) {
		int factorial = 1;
		for (int i = 1; i <= num; ++i) {
			// factorial = factorial * i;
			factorial *= i;
		}
		return factorial;
	}

	private Object sinIntegrate(Mono term, boolean integrate) {
		Object total = null;
		Object left = evaluate(term.value, false, integrate);
		Object leftUnDerived = evaluate(term.value, false, integrate);

		Object leftDerToUse = determinIntegral(integrate, left);
		Object leftunDerToUse = determinIntegral(integrate, leftUnDerived);

		if (leftDerToUse instanceof String || leftunDerToUse instanceof String) {
			total = leftDerToUse + "*cos(" + leftunDerToUse + ")";
		} else {
			total = ((BigDecimal) leftDerToUse)
					.multiply(BigDecimal.valueOf(Math.cos(((BigDecimal) leftunDerToUse).doubleValue())));
		}
		return total;
	}

	private Object tanhDontDerive(Mono term, boolean derive, boolean integrate) {
		Object total = null;
		Object left = evaluate(term.value, derive, integrate);

		Object leftDerToUse = determinDerivitive(false, left);

		if (leftDerToUse instanceof String) {
			total = "tanh(" + leftDerToUse + ")";
		} else {
			total = BigDecimal.valueOf(Math.tanh(((BigDecimal) leftDerToUse).doubleValue()));
		}
		return total;
	}

	private Object tanhDerive(Mono term, boolean derive, boolean integrate) {
		Object total = null;
		Object left = evaluate(term.value, derive, integrate);
		Object leftUnDerived = evaluate(term.value, false, false);

		Object leftDerToUse = determinDerivitive(true, left);
		Object leftunDerToUse = determinDerivitive(false, leftUnDerived);

		if (leftDerToUse instanceof String || leftunDerToUse instanceof String) {
			total = leftDerToUse + "*(1/(cosh(" + leftunDerToUse + "))^2)";
		} else {
			total = ((BigDecimal) leftDerToUse).multiply(BigDecimal.ONE.divide(
					BigDecimal.valueOf(Math.cosh(((BigDecimal) leftunDerToUse).doubleValue())).pow(2),
					new MathContext(99)));
		}
		return total;
	}

	private Object coshDontDerive(Mono term, boolean derive, boolean integrate) {
		Object total = null;
		Object left = evaluate(term.value, derive, integrate);

		Object leftDerToUse = determinDerivitive(false, left);

		if (leftDerToUse instanceof String) {
			total = "cosh(" + leftDerToUse + ")";
		} else {
			total = BigDecimal.valueOf(Math.cosh(((BigDecimal) leftDerToUse).doubleValue()));
		}
		return total;
	}

	private Object coshDerive(Mono term, boolean derive, boolean integrate) {
		Object total = null;
		Object left = evaluate(term.value, derive, integrate);
		Object leftUnDerived = evaluate(term.value, false, false);

		Object leftDerToUse = determinDerivitive(true, left);
		Object leftunDerToUse = determinDerivitive(false, leftUnDerived);

		if (leftDerToUse instanceof String || leftunDerToUse instanceof String) {
			total = leftDerToUse + "*sinh(" + leftunDerToUse + ")";
		} else {
			total = ((BigDecimal) leftDerToUse)
					.multiply(BigDecimal.valueOf(Math.sinh(((BigDecimal) leftunDerToUse).doubleValue())));
		}
		return total;
	}

	private Object sinhDontDerive(Mono term, boolean derive, boolean integrate) {
		Object total = null;
		Object left = evaluate(term.value, derive, integrate);

		Object leftDerToUse = determinDerivitive(false, left);

		if (leftDerToUse instanceof String) {
			total = "sinh(" + leftDerToUse + ")";
		} else {
			total = BigDecimal.valueOf(Math.sinh(((BigDecimal) leftDerToUse).doubleValue()));
		}
		return total;
	}

	private Object sinhDerive(Mono term, boolean derive, boolean integrate) {
		Object total = null;
		Object left = evaluate(term.value, derive, integrate);
		Object leftUnDerived = evaluate(term.value, false, false);

		Object leftDerToUse = determinDerivitive(true, left);
		Object leftunDerToUse = determinDerivitive(false, leftUnDerived);

		if (leftDerToUse instanceof String || leftunDerToUse instanceof String) {
			total = leftDerToUse + "*cosh(" + leftunDerToUse + ")";
		} else {
			total = ((BigDecimal) leftDerToUse)
					.multiply(BigDecimal.valueOf(Math.cosh(((BigDecimal) leftunDerToUse).doubleValue())));
		}
		return total;
	}

	private Object tanDontDerive(Mono term, boolean derive, boolean integrate) {
		Object total = null;
		Object left = evaluate(term.value, derive, integrate);

		Object leftDerToUse = determinDerivitive(derive, left);

		if (leftDerToUse instanceof String) {
			total = "tan(" + leftDerToUse + ")";
		} else {
			total = BigDecimal.valueOf(Math.tan(((BigDecimal) leftDerToUse).doubleValue()));
		}
		return total;
	}

	private Object tanDerive(Mono term, boolean derive, boolean integrate) {
		Object total = null;
		Object left = evaluate(term.value, derive, integrate);
		Object leftUnDerived = evaluate(term.value, false, false);

		Object leftDerToUse = determinDerivitive(true, left);
		Object leftunDerToUse = determinDerivitive(false, leftUnDerived);

		if (leftDerToUse instanceof String || leftunDerToUse instanceof String) {
			total = leftDerToUse + "*(1/(cos(" + leftunDerToUse + "))^2)";
		} else {
			total = ((BigDecimal) leftDerToUse).multiply(BigDecimal.ONE.divide(
					BigDecimal.valueOf(Math.cos(((BigDecimal) leftDerToUse).doubleValue())).pow(2),
					new MathContext(99)));
		}
		return total;
	}

	private Object sinDontDerive(Mono term, boolean derive, boolean integrate) {
		Object total = null;
		Object left = evaluate(term.value, derive, integrate);

		Object leftDerToUse = determinDerivitive(derive, left);

		if (leftDerToUse instanceof String) {
			total = "sin(" + leftDerToUse + ")";
		} else {
			total = BigDecimal.valueOf(Math.sin(((BigDecimal) leftDerToUse).doubleValue()));
		}
		return total;
	}

	private Object sinDerive(Mono term, boolean derive, boolean integrate) {
		Object total = null;
		Object left = evaluate(term.value, derive, integrate);
		Object leftUnDerived = evaluate(term.value, false, false);

		Object leftDerToUse = determinDerivitive(true, left);
		Object leftunDerToUse = determinDerivitive(false, leftUnDerived);

		if (leftDerToUse instanceof String || leftunDerToUse instanceof String) {
			total = leftDerToUse + "*cos(" + leftunDerToUse + ")";
		} else {
			total = ((BigDecimal) leftDerToUse)
					.multiply(BigDecimal.valueOf(Math.cos(((BigDecimal) leftunDerToUse).doubleValue())));
		}
		return total;

	}

	private Object cosDontDerive(Mono term, boolean derive, boolean integrate) {
		Object total = null;
		Object left = evaluate(term.value, derive, integrate);

		Object leftDerToUse = determinDerivitive(derive, left);

		if (leftDerToUse instanceof String) {
			total = "cos(" + leftDerToUse + ")";
		} else {
			double doubleValue = ((BigDecimal) leftDerToUse).doubleValue();
			double cos = Math.cos(doubleValue);
			total = BigDecimal.valueOf(cos);
		}
		return total;
	}

	private Object cosDerive(Mono term, boolean derive, boolean integrate) {
		Object total = null;
		Object left = evaluate(term.value, derive, integrate);
		Object leftUnDerived = evaluate(term.value, false, false);

		Object leftDerToUse = determinDerivitive(true, left);
		Object leftunDerToUse = determinDerivitive(false, leftUnDerived);

		if (leftDerToUse instanceof String || leftunDerToUse instanceof String) {
			total = "-" + leftDerToUse + "*" + "sin(" + leftunDerToUse + ")";
		} else {
			total = ((BigDecimal) leftDerToUse)
					.multiply(BigDecimal.valueOf(Math.sin(((BigDecimal) leftunDerToUse).doubleValue()))).negate();

		}
		return total;
	}

	@Override
	public Object visitLnTerm(Ln term, boolean derive, boolean integrate) {
		Object total = null;
		if (derive) {

			try {
				if (findX(term)) {
					if (term.value instanceof Binary) {

						if (((Binary) term.value).operator.type == BoxMathTokenType.POWER) {
							MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, -1);
							MathToken ln = new MathToken(BoxMathTokenType.LN, "ln", null, -1, -1, -1, -1);
							Ln lnOfleft = new Term.Ln(ln, ((Binary) term.value).left);
							Binary rightTimesLnOfLeft = new Term.Binary(((Binary) term.value).right, times, lnOfleft);
							return evaluate(rightTimesLnOfLeft, derive, integrate);
						} else if (((Binary) term.value).operator.type == BoxMathTokenType.DEVIDE) {
							MathToken ln = new MathToken(BoxMathTokenType.LN, "ln", null, -1, -1, -1, -1);
							Ln lnOfleft = new Term.Ln(ln, ((Binary) term.value).left);
							Ln lnOfright = new Term.Ln(ln, ((Binary) term.value).right);
							MathToken minus = new MathToken(BoxMathTokenType.MINUS, "-", null, -1, -1, -1, -1);
							Binary lnofleftMunusLnofRight = new Term.Binary(lnOfleft, minus, lnOfright);
							return evaluate(lnofleftMunusLnofRight, derive, integrate);
						} else if (((Binary) term.value).operator.type == BoxMathTokenType.TIMES) {
							MathToken ln = new MathToken(BoxMathTokenType.LN, "ln", null, -1, -1, -1, -1);
							Ln lnOfleft = new Term.Ln(ln, ((Binary) term.value).left);
							Ln lnOfright = new Term.Ln(ln, ((Binary) term.value).right);
							MathToken plus = new MathToken(BoxMathTokenType.PLUS, "+", null, -1, -1, -1, -1);
							Binary lnofleftPlusLnofRight = new Term.Binary(lnOfleft, plus, lnOfright);
							return evaluate(lnofleftPlusLnofRight, derive, integrate);
						} else {
							Object evaluate = evaluate(term.value, false, false);
							Object evaluate2 = evaluate(term.value, derive, integrate);

							Object evaluateToUse = determinDerivitive(false, evaluate);
							Object evaluate2ToUse = determinDerivitive(derive, evaluate2);

							if (evaluateToUse instanceof String || evaluate2ToUse instanceof String) {
								return "(1/(" + evaluateToUse + "))*(" + evaluate2ToUse + ")";
							} else {
								return (BigDecimal.ONE.divide((BigDecimal) evaluateToUse, new MathContext(99)))
										.multiply((BigDecimal) evaluate2ToUse);
							}

						}

					} else if (term.value instanceof E) {
						return BigDecimal.ONE;
					} else if (term.value instanceof Variable) {

						Term eval2Term = lookUp(((Variable) term.value).name);
						if (!(eval2Term instanceof NotDefined)) {

							Object evaluateB = evaluate(eval2Term, derive, integrate);
							Object evaluateBToUse = determinDerivitive(false, evaluateB);
							if (evaluateBToUse instanceof String) {
								total = "(1/(" + evaluateBToUse + "))";
							} else {
								total = BigDecimal.ONE.divide((BigDecimal) evaluateBToUse, new MathContext(99));
							}
						} else if (eval2Term instanceof NotDefined) {
							total = "(1/(" + ((NotDefined) eval2Term).name.lexeme + "))";
						}

						return total;
					} else if (term.value instanceof Log) {

						MathToken lnMathToken = new MathToken(BoxMathTokenType.LN, "ln", null, -1, -1, -1, -1);
						MathToken devide = new MathToken(BoxMathTokenType.DEVIDE, "/", null, -1, -1, -1, -1);
						MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, -1);

						Ln lnTen = new Term.Ln(lnMathToken, new Term.Literal(10.0));
						Binary denominator = new Term.Binary((Term) term.value, times, lnTen);
						Binary dontTakeDerivitiveOf = new Term.Binary(new Term.Literal(1.0), devide, denominator);

						Object derivitiveOfLog = evaluate(term.value, derive, integrate);

						Object notDerived = evaluate(dontTakeDerivitiveOf, false, false);

						Object dOfLogToUse = determinDerivitive(derive, derivitiveOfLog);
						Object notDToUse = determinDerivitive(false, notDerived);

						if (dOfLogToUse instanceof String) {
							total = "(" + dOfLogToUse + ")*(" + notDToUse + ")";
						} else {
							if (notDToUse instanceof String) {
								total = "(" + dOfLogToUse + ")*(" + notDToUse + ")";
							} else if (total instanceof String) {
								total = "(" + dOfLogToUse + ")*(" + notDToUse + ")";
							} else if (total instanceof BigDecimal)
								total = ((BigDecimal) total).multiply((BigDecimal) dOfLogToUse)
										.multiply(((BigDecimal) notDToUse));
							else
								total = ((BigDecimal) dOfLogToUse).multiply(((BigDecimal) notDToUse));
						}
						return total;

					} else {
						Object evaluate2 = evaluate(term.value, derive, integrate);
						Object evaluate3 = evaluate(term.value, false, false);
						Object evaluate2ToUse = determinDerivitive(derive, evaluate2);
						Object evaluate3ToUse = determinDerivitive(false, evaluate3);
						if (evaluate2ToUse instanceof String && evaluate3ToUse instanceof String) {
							total = "(1/(" + evaluate3ToUse + ")(" + evaluate2ToUse + ")";
						} else if (evaluate2ToUse instanceof String && evaluate3ToUse instanceof BigDecimal) {
							total = "(1/(" + evaluate3ToUse + ")(" + evaluate2ToUse + ")";
						} else if (evaluate2ToUse instanceof BigDecimal && evaluate3ToUse instanceof String) {
							total = "(1/(" + evaluate3ToUse + ")(" + evaluate2ToUse + ")";
						} else if (evaluate2ToUse instanceof BigDecimal && evaluate3ToUse instanceof BigDecimal) {
							BigDecimal divide = BigDecimal.valueOf(1.0);
							BigDecimal toTimes = divide.divide((BigDecimal) evaluate3ToUse, new MathContext(99));
							total = toTimes.multiply(((BigDecimal) evaluate2ToUse));
						}
						return total;
					}
				} else {
					Object evaluate = evaluate(term.value, false, false);
					Object evaluateToUse = determinDerivitive(false, evaluate);
					if (evaluateToUse instanceof String) {
						return "ln(" + evaluateToUse + ")";
					} else {
						return BigDecimal.valueOf(Math.log(((BigDecimal) evaluateToUse).doubleValue()));
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {

				e.printStackTrace();
			}
			return "howdy";

		} else {
			Object evaluate = evaluate(term.value, derive, integrate);
			Object evaluateToUse = determinDerivitive(derive, evaluate);
			if (evaluateToUse instanceof String) {
				return "ln(" + evaluateToUse + ")";
			} else {
				return BigDecimal.valueOf(Math.log(((BigDecimal) evaluateToUse).doubleValue()));
			}
		}

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
		Object total = null;
		if (derive) {
			try {
				if (findX(term)) {
					if (term.valueBase instanceof Binary) {

						if (((Binary) term.valueBase).operator.type == BoxMathTokenType.POWER) {
							MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, -1);
							MathToken ln = new MathToken(BoxMathTokenType.LN, "ln", null, -1, -1, -1, -1);
							Ln lnOfleft = new Term.Ln(ln, ((Binary) term.valueBase).left);
							Binary rightTimesLnOfLeft = new Term.Binary(((Binary) term.valueBase).right, times,
									lnOfleft);
							return evaluate(rightTimesLnOfLeft, derive, integrate);
						} else if (((Binary) term.valueBase).operator.type == BoxMathTokenType.DEVIDE) {
							MathToken ln = new MathToken(BoxMathTokenType.LN, "ln", null, -1, -1, -1, -1);
							Ln lnOfleft = new Term.Ln(ln, ((Binary) term.valueBase).left);
							Ln lnOfright = new Term.Ln(ln, ((Binary) term.valueBase).right);
							MathToken minus = new MathToken(BoxMathTokenType.MINUS, "-", null, -1, -1, -1, -1);
							Binary lnofleftMunusLnofRight = new Term.Binary(lnOfleft, minus, lnOfright);
							return evaluate(lnofleftMunusLnofRight, derive, integrate);
						} else if (((Binary) term.valueBase).operator.type == BoxMathTokenType.TIMES) {
							MathToken ln = new MathToken(BoxMathTokenType.LN, "ln", null, -1, -1, -1, -1);
							Ln lnOfleft = new Term.Ln(ln, ((Binary) term.valueBase).left);
							Ln lnOfright = new Term.Ln(ln, ((Binary) term.valueBase).right);
							MathToken plus = new MathToken(BoxMathTokenType.PLUS, "+", null, -1, -1, -1, -1);
							Binary lnofleftPlusLnofRight = new Term.Binary(lnOfleft, plus, lnOfright);
							return evaluate(lnofleftPlusLnofRight, derive, integrate);
						} else {
							MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, -1);
							MathToken ln = new MathToken(BoxMathTokenType.LN, "ln", null, -1, -1, -1, -1);
							Ln lnOfTen = new Term.Ln(ln, new Term.Literal(10.0));
							Binary lnTenTimesFofX = new Term.Binary(lnOfTen, times, term.valueBase);
							Object evaluate = evaluate(lnTenTimesFofX, false, false);
							Object evaluate2 = evaluate(term.valueBase, derive, integrate);

							Object evaluateToUse = determinDerivitive(false, evaluate);
							Object evaluate2ToUse = determinDerivitive(derive, evaluate2);
							if (evaluateToUse instanceof String || evaluate2ToUse instanceof String) {
								return "(1/(" + evaluateToUse + "))*(" + evaluate2ToUse + ")";
							} else {
								return (BigDecimal.ONE.divide((BigDecimal) evaluateToUse, new MathContext(99)))
										.multiply((BigDecimal) evaluate2ToUse);
							}

						}

					} else if (term.valueBase instanceof Variable) {

						Term eval2Term = lookUp(((Variable) term.valueBase).name);
						if (!(eval2Term instanceof NotDefined)) {

							Object evaluateB = evaluate(eval2Term, derive, integrate);
							Object evaluateBToUse = determinDerivitive(false, evaluateB);
							if (evaluateBToUse instanceof String) {
								total = "(1/(" + evaluateBToUse + ")";
							} else {
								total = (BigDecimal.ONE.divide((BigDecimal) evaluateBToUse, new MathContext(99)));
							}
						} else if (eval2Term instanceof NotDefined) {
							total = "(1/(" + ((NotDefined) eval2Term).name.lexeme + ")";
						}

						return total;
					} else if (term.valueBase instanceof Ln) {

						Object derivitiveOfLog = evaluate(term.valueBase, derive, integrate);

						Object notDerived = evaluate(term.valueBase, false, false);

						Object dOfLogToUse = determinDerivitive(derive, derivitiveOfLog);
						Object notDToUse = determinDerivitive(false, notDerived);
						if (derivitiveOfLog instanceof String) {
							total = "(1/(" + notDToUse + "))*(" + dOfLogToUse + ")";
						} else {
							if (notDToUse instanceof String) {
								total = "(1/(" + notDToUse + "))*(" + dOfLogToUse + ")";
							} else if (total instanceof String) {
								total = "(1/(" + notDToUse + "))*(" + dOfLogToUse + ")";
							} else
								total = (BigDecimal.ONE.divide((BigDecimal) notDToUse, new MathContext(99)))
										.multiply(((BigDecimal) dOfLogToUse));

						}
						return total;

					} else {

						MathToken lnMathToken = new MathToken(BoxMathTokenType.LN, "ln", null, -1, -1, -1, -1);
						MathToken devide = new MathToken(BoxMathTokenType.DEVIDE, "/", null, -1, -1, -1, -1);
						MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, -1);

						Ln lnTen = new Term.Ln(lnMathToken, new Term.Literal(10.0));

						Object derivitiveOfLog = evaluate(term.valueBase, derive, integrate);

						Binary denominator = new Term.Binary((Term) term.valueBase, times, lnTen);
						Binary dontTakeDerivitiveOf = new Term.Binary(new Term.Literal(1.0), devide, denominator);
						Object notDerived = evaluate(dontTakeDerivitiveOf, false, false);

						Object dOfLogToUse = determinDerivitive(derive, derivitiveOfLog);
						Object notDToUse = determinDerivitive(false, notDerived);
						if (dOfLogToUse instanceof String) {
							total = "(" + dOfLogToUse + ")*(" + notDToUse + ")";
						} else {
							if (notDerived instanceof String) {
								total = "(" + dOfLogToUse + ")*(" + notDToUse + ")";
							} else if (total instanceof String) {
								total = "(" + dOfLogToUse + ")*(" + notDToUse + ")";
							} else if (total instanceof BigDecimal)
								total = ((BigDecimal) total).multiply((BigDecimal) dOfLogToUse)
										.multiply(((BigDecimal) notDToUse));
							else
								total = ((BigDecimal) dOfLogToUse).multiply(((BigDecimal) notDToUse));
						}
						return total;
					}

				} else {
					Object evaluate = evaluate(term.valueBase, false, false);
					Object evaluateToUse = determinDerivitive(false, evaluate);
					if (evaluateToUse instanceof String) {
						return "log(" + evaluateToUse + ")";
					} else {
						return BigDecimal.valueOf(Math.log10(((BigDecimal) evaluateToUse).doubleValue()));
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {

				e.printStackTrace();
			}
			return null;
		} else {
			Object evaluate = evaluate(term.valueBase, derive, integrate);
			Object evaluateToUse = determinDerivitive(derive, evaluate);
			if (evaluateToUse instanceof String) {
				return "log(" + evaluateToUse + ")";
			} else {
				return BigDecimal.valueOf(Math.log10(((BigDecimal) evaluateToUse).doubleValue()));
			}
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
		Term functionBody = term.functionBody;
		if (derive && !integrate) {
			if (functionBody instanceof Term.Function) {

				Term equationForFunctionF = lookUp(term.name);
				Term equationForFunctionG = lookUp(((Term.Function) functionBody).name);
				Term.Variable baseForFunctionFVar = (Term.Variable) lookUpBase(term.name);
				Term.Variable baseForFunctionGVar = (Term.Variable) lookUpBase(((Term.Function) functionBody).name);

				Object gDerived = evaluate(equationForFunctionG, true, false);

				MathDeriver mathDeriver = new MathDeriver();
				MathToken token = new MathToken(BoxMathTokenType.IDENTIFIER, "p", null, -1, -1, -1, -1);
				Term.Variable variableToReplace = new Term.Variable(token);
				Object derivitiveOfF = mathDeriver.derive(equationForFunctionF, true, false, baseForFunctionFVar,
						variableToReplace, true);

				values.put((variableToReplace).name.lexeme, (Term) equationForFunctionG);
				Object fDerived = evaluate((Term) derivitiveOfF, false, false);
				values.remove((variableToReplace).name.lexeme, (Term) equationForFunctionG);

				if (fDerived instanceof String && gDerived instanceof String) {
					System.out.println(fDerived + "*" + gDerived);
					return fDerived + "*" + gDerived;
				} else if (fDerived instanceof BigDecimal && gDerived instanceof String) {
					System.out.println(fDerived + "*" + gDerived);
					return fDerived + "*" + gDerived;
				} else if (fDerived instanceof String && gDerived instanceof BigDecimal) {
					System.out.println(fDerived + "*" + gDerived);
					return fDerived + "*" + gDerived;
				} else {
					System.out.println(((BigDecimal) fDerived).multiply((BigDecimal) gDerived));
					return ((BigDecimal) fDerived).multiply((BigDecimal) gDerived);

				}
			}
		}
		return term;
	}

	private Term lookUpBase(MathToken name) {
		Term term = base.get(name.lexeme);
		return term;
	}

	@Override
	public Object visitDerivitiveTerm(Derivitive term, boolean derive, boolean integrate) {

		return null;
	}

	@Override
	public Object visitToDeriveTerm(ToDerive term, boolean derive, boolean integrate) {
		Object evaluate = evaluate(term.left, true, false);

		return evaluate;
	}

	@Override
	public Object visitVariableTerm(Variable term, boolean derive, boolean integrate) {

		return term;
	}

	@Override
	public Object visitLiteralTerm(Literal term, boolean derive, boolean integrate) {
		return term;
	}

	@Override
	public Object visitETerm(E term, boolean derive, boolean integrate) {

		return term;
	}

	@Override
	public Object visitPITerm(PI term, boolean derive, boolean integrate) {

		return term;
	}

	@Override
	public Object visitNotDefinedTerm(NotDefined term, boolean derive, boolean integrate) {

		return term;
	}

	@Override
	public Object visitIntegralTerm(Integral term, boolean derive, boolean integrate) {
		Object evaluate = null;
		if (term.function instanceof Binary) {
			if (((Binary) term.function).operator.type == BoxMathTokenType.TIMES) {
				evaluate = integrationByParts((Binary) term.function);
			}
		}
		return evaluate;
	}

	private Object integrationByParts(Binary function) {
		Object total = null;
		Term u = function.left;
		Term v = function.right;

		MathDeriver mathDeriver = new MathDeriver();
		Object derivedU = mathDeriver.derive(u, true, false, null, null, false);
		MathToken times = new MathToken(BoxMathTokenType.TIMES, "*", null, -1, -1, -1, -1);
		Object integratedV = mathDeriver.derive(v, false, true, null, null, false);
		Binary uTimesIntegralOfv = new Term.Binary((Term) u, times, (Term) integratedV);
		Object productUTimesIntegralV = evaluate(uTimesIntegralOfv, false, false);
		Binary integrateuDerivitiveTimesIntegralOfv = new Term.Binary((Term) derivedU, times, (Term) integratedV);
		Object integrateUderivTimesIntegralofv = evaluate(integrateuDerivitiveTimesIntegralOfv, false, true);
		if (productUTimesIntegralV instanceof String || integrateUderivTimesIntegralofv instanceof String) {

			total = productUTimesIntegralV + "-" + integrateUderivTimesIntegralofv;
		} else {

			total = ((BigDecimal) productUTimesIntegralV).subtract((BigDecimal) integrateUderivTimesIntegralofv);
		}
		return total;
	}

}
