package Box.Interpreter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import Box.Box.Box;
import Box.Parser.Declaration;
import Box.Parser.Expr;
import Box.Parser.Fun;
import Box.Parser.Stmt;
import Box.Parser.Declaration.FunDecl;
import Box.Parser.Declaration.StmtDecl;
import Box.Parser.Expr.Assignment;
import Box.Parser.Expr.Binary;
import Box.Parser.Expr.Call;
import Box.Parser.Expr.Contains;
import Box.Parser.Expr.Cup;
import Box.Parser.Expr.Factorial;
import Box.Parser.Expr.Get;
import Box.Parser.Expr.Gol;
import Box.Parser.Expr.Knot;
import Box.Parser.Expr.Lairotcaf;
import Box.Parser.Expr.Literal;
import Box.Parser.Expr.LiteralChar;
import Box.Parser.Expr.Llac;
import Box.Parser.Expr.Log;
import Box.Parser.Expr.Mono;
import Box.Parser.Expr.Onom;
import Box.Parser.Expr.Pocket;
import Box.Parser.Expr.Set;
import Box.Parser.Expr.Sniatnoc;
import Box.Parser.Expr.Swap;
import Box.Parser.Expr.Teg;
import Box.Parser.Expr.Tes;
import Box.Parser.Expr.Tnemngissa;
import Box.Parser.Expr.Tonk;
import Box.Parser.Expr.Unary;
import Box.Parser.Expr.Variable;
import Box.Parser.Expr.Yranib;
import Box.Parser.Expr.Yranu;
import Box.Parser.Fun.Function;
import Box.Parser.Stmt.Consume;
import Box.Parser.Stmt.Daer;
import Box.Parser.Stmt.Emaner;
import Box.Parser.Stmt.Evas;
import Box.Parser.Stmt.Evom;
import Box.Parser.Stmt.Expel;
import Box.Parser.Stmt.Fi;
import Box.Parser.Stmt.If;
import Box.Parser.Stmt.Move;
import Box.Parser.Stmt.Nruter;
import Box.Parser.Stmt.Print;
import Box.Parser.Stmt.Rav;
import Box.Parser.Stmt.Read;
import Box.Parser.Stmt.Rename;
import Box.Parser.Stmt.Save;
import Box.Parser.Stmt.Tnirp;
import Box.Parser.Stmt.Var;
import Box.Syntax.*;
import Box.Token.Token;
import Box.Token.TokenType;

public class Interpreter extends Thread implements Declaration.Visitor<Object> {

	public class KnotMap<refrence, expression> {
		ArrayList<refrence> ref = new ArrayList<refrence>();
		ArrayList<expression> expr = new ArrayList<expression>();

		public void put(refrence lexeme, expression expression) {
			ref.add(lexeme);
			expr.add(expression);
		}

		public refrence getRef(int i) {

			return ref.get(i);
		}

		public expression getExpr(int i) {

			return expr.get(i);
		}

	}

	public Environment globals = new Environment();
	private Environment environment = globals;
	private Map<Expr, Integer> locals = new HashMap<>();
	private boolean fromCall = false;
	private ArrayList<Stmt> statements;
	private boolean cupExecute = false;
	private boolean pocketExecute = false;
	private boolean forward;
	private boolean backward;

	public Interpreter() {

		globals.define("clock", null, new BoxCallable() {

			@Override
			public Object call(Interpreter interpreter, List<Object> arguments) {

				return (double) System.currentTimeMillis() / 1000.0;
			}

			@Override
			public int arity() {

				return 0;
			}

			@Override
			public String toString() {
				return "<native fn>";
			}

		});

	}

	public void interpret(List<List<Declaration>> statements2) {
		try {
			for (List<Declaration> statements : statements2) {

				for (Declaration stmt : statements) {
					execute(stmt);
				}

			}
		} catch (RuntimeError e) {
			Box.runtimeError(e);
		}

	}

	private Object interpretKnot(List<Stmt> statements) {
		pocketExecute = true;
		ArrayList<Object> visitPocket = new ArrayList<Object>();
		try {
			for (Stmt stmt : statements) {
				if (stmt instanceof Stmt.Expression) {
					if (((Stmt.Expression) stmt).expression instanceof Expr.Pocket) {
						visitPocket.add(visitPocketExpr((Pocket) ((Stmt.Expression) stmt).expression));
					} else {
						execute(stmt);
					}
				}

			}

		} catch (RuntimeError e) {
			Box.runtimeError(e);
		}
		return visitPocket;

	}

	private void execute(Stmt stmt) {
		stmt.accept(this);
	}

	private void execute(Declaration stmt) {
		stmt.accept(this);

	}

	private Object evaluate(Expr expression) {
		return expression.accept(this);
	}

	private String stringify(Object object) {
		if (object == null)
			return "null";

		if (object instanceof Double) {
			String text = object.toString();
			if (text.endsWith(".0")) {
				text = text.substring(0, text.length() - 2);
			}
			return text;
		}
		if (object instanceof Bin) {
			String text = ((Bin) object).toString();
			return text;
		}
		if (object instanceof Literal) {
			String text = ((Literal) object).value.toString();
			return text;
		}
		if (object instanceof ArrayList) {
			String total = "";
			for (Object entry : (ArrayList<?>) object) {

				if (entry instanceof ArrayList) {
					for (Object subEntry : (ArrayList<?>) entry) {
						total += stringify(subEntry);
					}
				} else if (!(entry instanceof ArrayList) && entry != null) {

					total += entry.toString();
				}
			}
			return total;
		}

		return object.toString();
	}

	@Override
	public Object visitExpressionStmt(Stmt.Expression stmt) {

		return evaluate(stmt.expression);
	}

	@Override
	public Object visitAssignmentExpr(Assignment expr) {
		Object value = expr.value;
		Integer distance = locals.get(expr);
		if (distance != null)
			environment.assignAt(distance, expr.name, expr.value, value, this);
		else
			globals.assign(expr.name, expr.value, value, this);
		return value;
	}

	@Override
	public Object visitVariableExpr(Variable expr) {

		return lookUpVariable(expr.name, expr);
	}

	Object lookUpVariable(Token name, Expr expr) {
		Integer distance = locals.get(expr);
		if (distance != null) {
			return environment.getAt(distance, name.lexeme);
		} else {
			if (fromCall) {
				fromCall = false;
				return globals.get(name, true);
			} else {
				return globals.get(name, false);
			}
		}
	}

	private Object lookUpVariableByName(Token name) {
		java.util.Set<Expr> keySet = locals.keySet();
		Expr exprToFind = null;
		if (name != null) {
			for (Expr keyExpr : keySet) {
				if (keyExpr instanceof Expr.Variable) {

					if (((Expr.Variable) keyExpr).name.lexeme == name.lexeme) {
						exprToFind = keyExpr;
					}
				}
			}
		}
		Integer distance = locals.get(exprToFind);
		if (name != null) {
			if (distance != null) {
				return environment.getAt(distance, name.lexeme);
			} else {
				return globals.get(name, false);
			}
		}
		return null;
	}

	@Override
	public Void visitPrintStmt(Print stmt) {
		// Object value = evaluate(stmt.expression);
		// System.out.println(stringify(value));

//		BoxMath.test("x=25 integral from 0 to 25 (x^2)dx");
//		Double x = 25.0;
//		Double numerator = (3*x+5)*Math.sin(x);
//		Double dnominator = Math.pow(Math.cos(x),2);
//		Double other = 3 /Math.cos(x);
//		Double result =  (numerator/dnominator)+other;
//		System.out.println("result: " + result);
		Object value = evaluate(stmt.expression);
		System.out.println(stringify(value));

		return null;
	}

	@Override
	public Object visitBinaryExpr(Binary expr) {
		Object left = evaluate(expr.left);
		Object right = evaluate(expr.right);

		switch (expr.operator.type) {
		case NOTEQUALS:
			return !isEqual(left, right);
		case EQUALSNOT:
			return !isEqual(left, right);
		case EQUALSEQUALS:
			return isEqual(left, right);
		case MINUSEQUALS:
			return findRootForLeftAndRightAndMinusEquals(left, right, expr);
		case PLUSEQUALS:
			return findRootForLeftAndRightAndPlusEquals(left, right, expr);
		case GREATERTHEN:
			return findRootForLeftAndRightAndGreaterThen(left, right, expr);
		case GREATERTHENEQUAL:
			return findRootForLeftAndRightAndGreaterThenEquals(left, right, expr);
		case LESSTHEN:
			return findRootForLeftAndRightAndLessThen(left, right, expr);
		case LESSTHENEQUAL:
			return findRootForLeftAndRightAndLessThenEquals(left, right, expr);
		case MINUS:

			return findRootForLeftAndRightAndSubtract(left, right, expr);

		case PLUS:

			return findRootForLeftAndRightAndAdd(left, right, expr);

		case FORWARDSLASH:
			return findRootForLeftAndRightAndDivide(left, right, expr);
		case BACKSLASH:
			return findRootForLeftAndRightAndDivide(left, right, expr);
		case TIMES:
			return findRootForLeftAndRightAndTimes(left, right, expr);

		case POWER:
			return findRootForLeftAndRightAndPower(left, right, expr);
		case YROOT:
			return findRootForLeftAndRightAndYroot(left, right, expr);
		case TOORY:
			return findRootForLeftAndRightAndYroot(right, left, expr);
		case DNA:
			return findRootForLeftAndRightAndAnd(left, right, expr);
		case AND:
			return findRootForLeftAndRightAndAnd(left, right, expr);
		case RO:
			return findRootForLeftAndRightAndOr(left, right, expr);
		case OR:
			return findRootForLeftAndRightAndOr(left, right, expr);
		default:
			return null;
		}

	}

	private Object findRootForLeftAndRightAndOr(Object left, Object right, Expr expr) {
		Expr.Binary binExpr = null;
		Expr.Yranib yraExpr = null;
		if (expr instanceof Expr.Binary)
			binExpr = (Expr.Binary) expr;
		if (expr instanceof Expr.Yranib)
			yraExpr = (Expr.Yranib) expr;

		Object theLeft = left;
		Object theRight = right;
		if (left instanceof ArrayList) {
			if (((ArrayList<?>) left).size() > 0) {
				if (((ArrayList<?>) left).get(0) instanceof ArrayList) {
					theLeft = findRoot(((ArrayList<?>) left).get(0));
				}

			}
		}
		if (right instanceof ArrayList) {
			if (((ArrayList<?>) right).size() > 0) {
				if (((ArrayList<?>) right).get(0) instanceof ArrayList) {
					theRight = findRoot(((ArrayList<?>) right).get(0));
				}

			}
		}
		if (theLeft instanceof Boolean && theRight instanceof Boolean) {
			return (boolean) theLeft || (boolean) theRight;
		} else if (binExpr != null)
			throw new RuntimeError(binExpr.operator, "Operands must be boolean.");
		else
			throw new RuntimeError(yraExpr.operator, "Operands must be boolean.");

	}

	private Object findRootForLeftAndRightAndAnd(Object left, Object right, Expr expr) {
		Expr.Binary binExpr = null;
		Expr.Yranib yraExpr = null;
		if (expr instanceof Expr.Binary)
			binExpr = (Expr.Binary) expr;
		if (expr instanceof Expr.Yranib)
			yraExpr = (Expr.Yranib) expr;

		Object theLeft = left;
		Object theRight = right;
		if (left instanceof ArrayList) {
			if (((ArrayList<?>) left).size() > 0) {
				if (((ArrayList<?>) left).get(0) instanceof ArrayList) {
					theLeft = findRoot(((ArrayList<?>) left).get(0));
				}

			}
		}
		if (right instanceof ArrayList) {
			if (((ArrayList<?>) right).size() > 0) {
				if (((ArrayList<?>) right).get(0) instanceof ArrayList) {
					theRight = findRoot(((ArrayList<?>) right).get(0));
				}

			}
		}
		if (theLeft instanceof Boolean && theRight instanceof Boolean) {
			return (boolean) theLeft && (boolean) theRight;
		} else if (binExpr != null)
			throw new RuntimeError(binExpr.operator, "Operands must be boolean.");
		else
			throw new RuntimeError(yraExpr.operator, "Operands must be boolean.");

	}

	private Object findRootForLeftAndRightAndYroot(Object left, Object right, Expr expr) {
		Expr.Binary binExpr = null;
		Expr.Yranib yraExpr = null;
		if (expr instanceof Expr.Binary)
			binExpr = (Expr.Binary) expr;
		if (expr instanceof Expr.Yranib)
			yraExpr = (Expr.Yranib) expr;

		Object theLeft = left;
		Object theRight = right;
		if (left instanceof ArrayList) {
			if (((ArrayList<?>) left).size() > 0) {
				if (((ArrayList<?>) left).get(0) instanceof ArrayList) {
					theLeft = findRoot(((ArrayList<?>) left).get(0));
				}

			}
		}
		if (right instanceof ArrayList) {
			if (((ArrayList<?>) right).size() > 0) {
				if (((ArrayList<?>) right).get(0) instanceof ArrayList) {
					theRight = findRoot(((ArrayList<?>) right).get(0));
				}

			}
		}

		if (theLeft instanceof Double && theRight instanceof Double) {
			Double remainder = (Double) theLeft;

			Double xsubone = findNthRootOfRemainder((Double) theRight, remainder);

			return xsubone;
		} else if (theLeft instanceof Integer && theRight instanceof Integer) {
			Double remainder = ((Integer) theLeft).doubleValue();

			Double xsubone = findNthRootOfRemainder(((Integer) theRight).doubleValue(), remainder);

			return xsubone;
		} else if (theLeft instanceof Integer && theRight instanceof Double) {
			Double remainder = ((Integer) theLeft).doubleValue();

			Double xsubone = findNthRootOfRemainder((Double) theRight, remainder);

			return xsubone;
		} else if (theLeft instanceof Double && theRight instanceof Integer) {
			Double remainder = ((Double) theLeft);

			Double xsubone = findNthRootOfRemainder(((Integer) theRight).doubleValue(), remainder);

			return xsubone;
		} else if (theLeft instanceof Bin && theRight instanceof Integer) {
			Double remainder = ((Bin) theLeft).toDouble();

			Double xsubone = findNthRootOfRemainder(((Integer) theRight).doubleValue(), remainder);

			return xsubone;
		} else if (theLeft instanceof Integer && theRight instanceof Bin) {
			Double remainder = ((Integer) theLeft).doubleValue();

			Double xsubone = findNthRootOfRemainder(((Bin) theRight).toDouble(), remainder);

			return xsubone;
		} else if (theLeft instanceof Double && theRight instanceof Bin) {
			Double remainder = ((Double) theLeft);

			Double xsubone = findNthRootOfRemainder(((Bin) theRight).toDouble(), remainder);

			return xsubone;
		} else if (theLeft instanceof Bin && theRight instanceof Double) {
			Double remainder = ((Bin) theLeft).toDouble();

			Double xsubone = findNthRootOfRemainder((Double) theRight, remainder);

			return xsubone;
		} else if (theLeft instanceof Bin && theRight instanceof Bin) {
			Double remainder = ((Bin) theLeft).toDouble();

			Double xsubone = findNthRootOfRemainder(((Bin) theRight).toDouble(), remainder);

			return xsubone;
		} else if (binExpr != null)
			throw new RuntimeError(binExpr.operator, "Operands must be numbers.");
		else
			throw new RuntimeError(yraExpr.operator, "Operands must be numbers.");
	}

	private Double findNthRootOfRemainder(Double theRight, Double remainder) {
		int i = 0;
		int ii = 0;
		boolean shouldbreak = false;
		for (i = 0; i <= remainder.intValue() * 1000; i++) {

			if (fPrime(theRight, i, remainder) < 0) {
				for (ii = i; ii < 1000; ii++) {
					if (fPrime(theRight, ii, remainder) > 0) {
						shouldbreak = true;
						break;
					}
				}
			}
			if (shouldbreak)
				break;

		}

		Double xsubzero = (double) (((double) i + (double) ii) / 2);
		Double fprime = fPrime(theRight, xsubzero, remainder);
		Double fprimeFirstDirivitive = firstDerivitive(theRight, xsubzero);
		Double fprimeSecondDirivitive = secondDerivitive(theRight, xsubzero);
		Double xsubone = xsubzero - ((2 * fprime * fprimeFirstDirivitive)
				/ (2 * Math.pow(fprimeFirstDirivitive, 2) - fprime * fprimeSecondDirivitive));

		for (int j = 0; j < 10; j++) {
			fprime = fPrime(theRight, xsubone, remainder);
			fprimeFirstDirivitive = firstDerivitive(theRight, xsubone);
			fprimeSecondDirivitive = secondDerivitive(theRight, xsubone);
			xsubone = xsubone - ((2 * fprime * fprimeFirstDirivitive)
					/ (2 * Math.pow(fprimeFirstDirivitive, 2) - fprime * fprimeSecondDirivitive));

		}
		return xsubone;
	}

	private double secondDerivitive(Double theRight, double x) {
		Double doubleValue = theRight;
		Double d = doubleValue - 2;
		Double e = doubleValue * (doubleValue - 1);
		return e * Math.pow(x, d);
	}

	private double firstDerivitive(Double theRight, double x) {
		Double doubleValue = theRight;
		Double d = doubleValue - 1;
		return doubleValue * Math.pow(x, d);
	}

	private double fPrime(Double theRight, double x, Double remainder) {
		return Math.pow(x, theRight) - remainder;
	}

	private ArrayList<Integer> factorPrimes(Double remainder) {
		ArrayList<Integer> thePrimeFactors = new ArrayList<>();
		Integer thePrime = remainder.intValue();
		for (int i = 2; i <= remainder; i++) {

			boolean prime = isPrime(i);
			if (prime) {
				while (thePrime % i == 0) {
					thePrime = thePrime / i;
					thePrimeFactors.add(i);
				}

			}
		}

		return thePrimeFactors;
	}

	private boolean isPrime(int num) {
		if (num < 1) {
			return false;
		}
		if (num == 1) {
			return true;
		}
		for (int i = 2; i <= num / 2; i++) {
			if ((num % i) == 0)
				return false;
		}
		return true;
	}

	private Object findRootForLeftAndRightAndPower(Object left, Object right, Expr expr) {
		Expr.Binary binExpr = null;
		Expr.Yranib yraExpr = null;
		if (expr instanceof Expr.Binary)
			binExpr = (Expr.Binary) expr;
		if (expr instanceof Expr.Yranib)
			yraExpr = (Expr.Yranib) expr;

		Object theLeft = left;
		Object theRight = right;
		if (left instanceof ArrayList) {
			if (((ArrayList<?>) left).size() > 0) {
				if (((ArrayList<?>) left).get(0) instanceof ArrayList) {
					theLeft = findRoot(((ArrayList<?>) left).get(0));
				}

			}
		}
		if (right instanceof ArrayList) {
			if (((ArrayList<?>) right).size() > 0) {
				if (((ArrayList<?>) right).get(0) instanceof ArrayList) {
					theRight = findRoot(((ArrayList<?>) right).get(0));
				}

			}
		}

		if (theLeft instanceof Double && theRight instanceof Double) {
			double power = 1;
			for (int i = 0; i < (int) theRight; i++) {
				power = power * (double) theLeft;
			}
			return power;
		} else if (theLeft instanceof Integer && theRight instanceof Integer) {
			int power = 1;
			for (int i = 0; i < (int) theRight; i++) {
				power = power * (int) theLeft;
			}
			return power;

		} else if (theLeft instanceof Integer && theRight instanceof Double) {
			int power = 1;
			for (int i = 0; i < (int) theRight; i++) {
				power = power * (int) theLeft;
			}
			return power;
		} else if (theLeft instanceof Double && theRight instanceof Integer) {
			double power = 1;
			for (int i = 0; i < (int) theRight; i++) {
				power = power * (double) theLeft;
			}
			return power;
		} else if (theLeft instanceof Bin && theRight instanceof Integer) {
			int power = 1;
			for (int i = 0; i < (int) theRight; i++) {
				power = power * (int) ((Bin) theLeft).toInteger();
			}
			return power;

		} else if (theLeft instanceof Integer && theRight instanceof Bin) {
			int power = 1;
			for (int i = 0; i < (int) ((Bin) theRight).toInteger(); i++) {
				power = power * (int) theLeft;
			}
			return power;
		} else if (theLeft instanceof Double && theRight instanceof Bin) {
			double power = 1;
			for (int i = 0; i < (int) ((Bin) theRight).toInteger(); i++) {
				power = power * (double) theLeft;
			}
			return power;
		} else if (theLeft instanceof Bin && theRight instanceof Double) {
			double power = 1;
			for (int i = 0; i < (int) theRight; i++) {
				power = power * (int) ((Bin) theLeft).toInteger();
			}
			return power;
		} else if (theLeft instanceof Bin && theRight instanceof Bin) {
			double power = 1;
			for (int i = 0; i < (int) ((Bin) theRight).toInteger(); i++) {
				power = power * (int) ((Bin) theLeft).toInteger();
			}
			return power;

		} else if (binExpr != null)
			throw new RuntimeError(binExpr.operator, "Operands must be numbers.");
		else
			throw new RuntimeError(yraExpr.operator, "Operands must be numbers.");

	}

	private Object findRootForLeftAndRightAndTimes(Object left, Object right, Expr expr) {
		Expr.Binary binExpr = null;
		Expr.Yranib yraExpr = null;
		if (expr instanceof Expr.Binary)
			binExpr = (Expr.Binary) expr;
		if (expr instanceof Expr.Yranib)
			yraExpr = (Expr.Yranib) expr;

		Object theLeft = left;
		Object theRight = right;
		if (left instanceof ArrayList) {
			if (((ArrayList<?>) left).size() > 0) {
				if (((ArrayList<?>) left).get(0) instanceof ArrayList) {
					theLeft = findRoot(((ArrayList<?>) left).get(0));
				}

			}
		}
		if (right instanceof ArrayList) {
			if (((ArrayList<?>) right).size() > 0) {
				if (((ArrayList<?>) right).get(0) instanceof ArrayList) {
					theRight = findRoot(((ArrayList<?>) right).get(0));
				}

			}
		}
		if (theLeft instanceof Double && theRight instanceof Double) {
			return (double) theLeft * (double) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Integer) {
			return (int) theLeft * (int) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Double) {
			return (int) theLeft * (double) theRight;
		} else if (theLeft instanceof Double && theRight instanceof Integer) {
			return (double) theLeft * (int) theRight;
		} else if (theLeft instanceof Bin && theRight instanceof Integer) {
			return ((Bin) theLeft).toInteger() * (Integer) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Bin) {
			return (int) theLeft * ((Bin) theRight).toInteger();
		} else if (theLeft instanceof Double && theRight instanceof Bin) {
			return ((Double) theLeft) * (((Bin) theRight).toDouble());
		} else if (theLeft instanceof Bin && theRight instanceof Double) {
			return (((Bin) theLeft).toDouble()) * ((Double) theRight);
		} else if (theLeft instanceof Bin && theRight instanceof Bin) {
			return Bin.times((Bin) theLeft, (Bin) theRight);
		} else if (binExpr != null)
			throw new RuntimeError(binExpr.operator, "Operands must be numbers.");
		else
			throw new RuntimeError(yraExpr.operator, "Operands must be numbers.");
	}

	private Object findRootForLeftAndRightAndDivide(Object left, Object right, Expr expr) {
		Expr.Binary binExpr = null;
		Expr.Yranib yraExpr = null;
		if (expr instanceof Expr.Binary)
			binExpr = (Expr.Binary) expr;
		if (expr instanceof Expr.Yranib)
			yraExpr = (Expr.Yranib) expr;

		Object theLeft = left;
		Object theRight = right;
		if (left instanceof ArrayList) {
			if (((ArrayList<?>) left).size() > 0) {
				if (((ArrayList<?>) left).get(0) instanceof ArrayList) {
					theLeft = findRoot(((ArrayList<?>) left).get(0));
				}

			}
		}
		if (right instanceof ArrayList) {
			if (((ArrayList<?>) right).size() > 0) {
				if (((ArrayList<?>) right).get(0) instanceof ArrayList) {
					theRight = findRoot(((ArrayList<?>) right).get(0));
				}

			}
		}
		if (theLeft instanceof Double && theRight instanceof Double) {
			return (double) theLeft / (double) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Integer) {
			return (int) theLeft / (int) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Double) {
			return (int) theLeft / (double) theRight;
		} else if (theLeft instanceof Double && theRight instanceof Integer) {
			return (double) theLeft / (int) theRight;
		} else if (theLeft instanceof Bin && theRight instanceof Integer) {
			return ((Bin) theLeft).toInteger() / (Integer) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Bin) {
			return (int) theLeft / ((Bin) theRight).toInteger();
		} else if (theLeft instanceof Double && theRight instanceof Bin) {
			return ((Double) theLeft) / (((Bin) theRight).toDouble());
		} else if (theLeft instanceof Bin && theRight instanceof Double) {
			return (((Bin) theLeft).toDouble()) / ((Double) theRight);
		} else if (theLeft instanceof Bin && theRight instanceof Bin) {
			return Bin.divide((Bin) theLeft, (Bin) theRight);
		} else if (binExpr != null)
			throw new RuntimeError(binExpr.operator, "Operands must be numbers.");
		else
			throw new RuntimeError(yraExpr.operator, "Operands must be numbers.");

	}

	private Object findRootForLeftAndRightAndMinusEquals(Object left, Object right, Expr expr) {

		Expr.Binary binExpr = null;
		Expr.Yranib yraExpr = null;
		if (expr instanceof Expr.Binary)
			binExpr = (Expr.Binary) expr;
		if (expr instanceof Expr.Yranib)
			yraExpr = (Expr.Yranib) expr;

		Object theLeft = left;
		Object theRight = right;
		if (left instanceof ArrayList) {
			if (((ArrayList<?>) left).size() > 0) {
				if (((ArrayList<?>) left).get(0) instanceof ArrayList) {
					theLeft = findRoot(((ArrayList<?>) left).get(0));
				}

			}
		}
		if (right instanceof ArrayList) {
			if (((ArrayList<?>) right).size() > 0) {
				if (((ArrayList<?>) right).get(0) instanceof ArrayList) {
					theRight = findRoot(((ArrayList<?>) right).get(0));
				}

			}
		}
		if (theLeft instanceof Double && theRight instanceof Double) {
			return (double) theLeft - (double) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Integer) {
			return (int) theLeft - (int) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Double) {
			return (int) theLeft - (double) theRight;
		} else if (theLeft instanceof Double && theRight instanceof Integer) {
			return (double) theLeft - (int) theRight;
		} else if (theLeft instanceof Bin && theRight instanceof Integer) {
			return ((Bin) theLeft).toInteger() - (Integer) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Bin) {
			return (int) theLeft - ((Bin) theRight).toInteger();
		} else if (theLeft instanceof Double && theRight instanceof Bin) {
			return ((Double) theLeft) - (((Bin) theRight).toDouble());
		} else if (theLeft instanceof Bin && theRight instanceof Double) {
			return (((Bin) theLeft).toDouble()) - ((Double) theRight);
		} else if (theLeft instanceof Bin && theRight instanceof Bin) {
			return Bin.subtract((Bin) theLeft, (Bin) theRight);
		} else if (binExpr != null)
			throw new RuntimeError(binExpr.operator, "Operands must be numbers.");
		else
			throw new RuntimeError(yraExpr.operator, "Operands must be numbers.");

	}

	private Object findRootForLeftAndRightAndPlusEquals(Object left, Object right, Expr expr) {
		Expr.Binary binExpr = null;
		Expr.Yranib yraExpr = null;
		if (expr instanceof Expr.Binary)
			binExpr = (Expr.Binary) expr;
		if (expr instanceof Expr.Yranib)
			yraExpr = (Expr.Yranib) expr;

		Object theLeft = left;
		Object theRight = right;
		if (left instanceof ArrayList) {
			if (((ArrayList<?>) left).size() > 0) {
				if (((ArrayList<?>) left).get(0) instanceof ArrayList) {
					theLeft = findRoot(((ArrayList<?>) left).get(0));
				}

			}
		}
		if (right instanceof ArrayList) {
			if (((ArrayList<?>) right).size() > 0) {
				if (((ArrayList<?>) right).get(0) instanceof ArrayList) {
					theRight = findRoot(((ArrayList<?>) right).get(0));
				}

			}
		}
		if (theLeft instanceof Double && theRight instanceof Double) {
			return (double) theLeft + (double) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Integer) {
			return (int) theLeft + (int) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Double) {
			return (int) theLeft + (double) theRight;
		} else if (theLeft instanceof Double && theRight instanceof Integer) {
			return (double) theLeft + (int) theRight;
		} else if (theLeft instanceof Bin && theRight instanceof Integer) {
			return ((Bin) theLeft).toInteger() + (Integer) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Bin) {
			return (int) theLeft + ((Bin) theRight).toInteger();
		} else if (theLeft instanceof Double && theRight instanceof Bin) {
			return ((Double) theLeft) + (((Bin) theRight).toDouble());
		} else if (theLeft instanceof Bin && theRight instanceof Double) {
			return (((Bin) theLeft).toDouble()) + ((Double) theRight);
		} else if (theLeft instanceof Bin && theRight instanceof Bin) {
			return Bin.add((Bin) theLeft, (Bin) theRight);
		} else if (theLeft instanceof Stmt.Expression && theRight instanceof Integer) {
			return evaluateBoxPocketCup(((Stmt.Expression) theLeft), theRight);
		} else if (binExpr != null)
			throw new RuntimeError(binExpr.operator, "Operands must be numbers.");
		else
			throw new RuntimeError(yraExpr.operator, "Operands must be numbers.");

	}

	private Object evaluateBoxPocketCup(Stmt.Expression theLeft, Object theRight) {
		
		if(theLeft.expression!=null) {
		Object evaluate = evaluate(theLeft.expression);
		ArrayList<?> arr = null;
		ArrayList<Integer> arr1 = new ArrayList<>();
		if(evaluate instanceof ArrayList) {
			arr = ((ArrayList)evaluate);
			for (Object object : arr) {
				if(object instanceof Integer) {
					Integer teger = ((Integer)object); 
					
					teger +=(Integer)theRight;
					arr1.add(teger);
				}
			}
			
		
		}
		
		return arr1;
		}
		if(theLeft.noisserpxe!=null) {
			Object evaluate = evaluate(theLeft.noisserpxe);
			ArrayList<?> arr = null;
			ArrayList<Integer> arr1 = new ArrayList<>();
			if(evaluate instanceof ArrayList) {
				arr = ((ArrayList)evaluate);
				for (Object object : arr) {
					if(object instanceof Integer) {
						Integer teger = ((Integer)object); 
						
						teger +=(Integer)theRight;
						arr1.add(teger);
					}
				}
				
			
			}
			return arr1;
		}
			return null;
	}

	private Object findRootForLeftAndRightAndGreaterThen(Object left, Object right, Expr expr) {
		Expr.Binary binExpr = null;
		Expr.Yranib yraExpr = null;
		if (expr instanceof Expr.Binary)
			binExpr = (Expr.Binary) expr;
		if (expr instanceof Expr.Yranib)
			yraExpr = (Expr.Yranib) expr;

		Object theLeft = left;
		Object theRight = right;
		if (left instanceof ArrayList) {
			if (((ArrayList<?>) left).size() > 0) {
				if (((ArrayList<?>) left).get(0) instanceof ArrayList) {
					theLeft = findRoot(((ArrayList<?>) left).get(0));
				}

			}
		}
		if (right instanceof ArrayList) {
			if (((ArrayList<?>) right).size() > 0) {
				if (((ArrayList<?>) right).get(0) instanceof ArrayList) {
					theRight = findRoot(((ArrayList<?>) right).get(0));
				}

			}
		}
		if (theLeft instanceof Double && theRight instanceof Double) {
			return (double) theLeft > (double) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Integer) {
			return (int) theLeft > (int) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Double) {
			return (int) theLeft > (double) theRight;
		} else if (theLeft instanceof Double && theRight instanceof Integer) {
			return (double) theLeft > (int) theRight;
		} else if (theLeft instanceof Bin && theRight instanceof Integer) {
			return ((Bin) theLeft).toInteger() > (Integer) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Bin) {
			return (int) theLeft > ((Bin) theRight).toInteger();
		} else if (theLeft instanceof Double && theRight instanceof Bin) {
			return ((Double) theLeft) > (((Bin) theRight).toDouble());
		} else if (theLeft instanceof Bin && theRight instanceof Double) {
			return (((Bin) theLeft).toDouble()) > ((Double) theRight);
		} else if (theLeft instanceof Bin && theRight instanceof Bin) {
			return Bin.greaterThen((Bin) theLeft, (Bin) theRight);
		} else if (binExpr != null)
			throw new RuntimeError(binExpr.operator, "Operands must be numbers.");
		else
			throw new RuntimeError(yraExpr.operator, "Operands must be numbers.");

	}

	private Object findRootForLeftAndRightAndGreaterThenEquals(Object left, Object right, Expr expr) {
		Expr.Binary binExpr = null;
		Expr.Yranib yraExpr = null;
		if (expr instanceof Expr.Binary)
			binExpr = (Expr.Binary) expr;
		if (expr instanceof Expr.Yranib)
			yraExpr = (Expr.Yranib) expr;

		Object theLeft = left;
		Object theRight = right;
		if (left instanceof ArrayList) {
			if (((ArrayList<?>) left).size() > 0) {
				if (((ArrayList<?>) left).get(0) instanceof ArrayList) {
					theLeft = findRoot(((ArrayList<?>) left).get(0));
				}

			}
		}
		if (right instanceof ArrayList) {
			if (((ArrayList<?>) right).size() > 0) {
				if (((ArrayList<?>) right).get(0) instanceof ArrayList) {
					theRight = findRoot(((ArrayList<?>) right).get(0));
				}

			}
		}
		if (theLeft instanceof Double && theRight instanceof Double) {
			return (double) theLeft >= (double) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Integer) {
			return (int) theLeft >= (int) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Double) {
			return (int) theLeft >= (double) theRight;
		} else if (theLeft instanceof Double && theRight instanceof Integer) {
			return (double) theLeft >= (int) theRight;
		} else if (theLeft instanceof Bin && theRight instanceof Integer) {
			return ((Bin) theLeft).toInteger() >= (Integer) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Bin) {
			return (int) theLeft >= ((Bin) theRight).toInteger();
		} else if (theLeft instanceof Double && theRight instanceof Bin) {
			return ((Double) theLeft) >= (((Bin) theRight).toDouble());
		} else if (theLeft instanceof Bin && theRight instanceof Double) {
			return (((Bin) theLeft).toDouble()) >= ((Double) theRight);
		} else if (theLeft instanceof Bin && theRight instanceof Bin) {
			return Bin.greaterThenEquals((Bin) theLeft, (Bin) theRight);
		} else if (binExpr != null)
			throw new RuntimeError(binExpr.operator, "Operands must be numbers.");
		else
			throw new RuntimeError(yraExpr.operator, "Operands must be numbers.");

	}

	private Object findRootForLeftAndRightAndLessThen(Object left, Object right, Expr expr) {
		Expr.Binary binExpr = null;
		Expr.Yranib yraExpr = null;
		if (expr instanceof Expr.Binary)
			binExpr = (Expr.Binary) expr;
		if (expr instanceof Expr.Yranib)
			yraExpr = (Expr.Yranib) expr;

		Object theLeft = left;
		Object theRight = right;
		if (left instanceof ArrayList) {
			if (((ArrayList<?>) left).size() > 0) {
				if (((ArrayList<?>) left).get(0) instanceof ArrayList) {
					theLeft = findRoot(((ArrayList<?>) left).get(0));
				}

			}
		}
		if (right instanceof ArrayList) {
			if (((ArrayList<?>) right).size() > 0) {
				if (((ArrayList<?>) right).get(0) instanceof ArrayList) {
					theRight = findRoot(((ArrayList<?>) right).get(0));
				}

			}
		}
		if (theLeft instanceof Double && theRight instanceof Double) {
			return (double) theLeft < (double) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Integer) {
			return (int) theLeft < (int) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Double) {
			return (int) theLeft < (double) theRight;
		} else if (theLeft instanceof Double && theRight instanceof Integer) {
			return (double) theLeft < (int) theRight;
		} else if (theLeft instanceof Bin && theRight instanceof Integer) {
			return ((Bin) theLeft).toInteger() < (Integer) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Bin) {
			return (int) theLeft < ((Bin) theRight).toInteger();
		} else if (theLeft instanceof Double && theRight instanceof Bin) {
			return ((Double) theLeft) < (((Bin) theRight).toDouble());
		} else if (theLeft instanceof Bin && theRight instanceof Double) {
			return (((Bin) theLeft).toDouble()) < ((Double) theRight);
		} else if (theLeft instanceof Bin && theRight instanceof Bin) {
			return Bin.lessThen((Bin) theLeft, (Bin) theRight);
		} else if (binExpr != null)
			throw new RuntimeError(binExpr.operator, "Operands must be numbers.");
		else
			throw new RuntimeError(yraExpr.operator, "Operands must be numbers.");

	}

	private Object findRootForLeftAndRightAndLessThenEquals(Object left, Object right, Expr expr) {
		Expr.Binary binExpr = null;
		Expr.Yranib yraExpr = null;
		if (expr instanceof Expr.Binary)
			binExpr = (Expr.Binary) expr;
		if (expr instanceof Expr.Yranib)
			yraExpr = (Expr.Yranib) expr;

		Object theLeft = left;
		Object theRight = right;
		if (left instanceof ArrayList) {
			if (((ArrayList<?>) left).size() > 0) {
				if (((ArrayList<?>) left).get(0) instanceof ArrayList) {
					theLeft = findRoot(((ArrayList<?>) left).get(0));
				}

			}
		}
		if (right instanceof ArrayList) {
			if (((ArrayList<?>) right).size() > 0) {
				if (((ArrayList<?>) right).get(0) instanceof ArrayList) {
					theRight = findRoot(((ArrayList<?>) right).get(0));
				}

			}
		}
		if (theLeft instanceof Double && theRight instanceof Double) {
			return (double) theLeft <= (double) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Integer) {
			return (int) theLeft <= (int) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Double) {
			return (int) theLeft <= (double) theRight;
		} else if (theLeft instanceof Double && theRight instanceof Integer) {
			return (double) theLeft <= (int) theRight;
		} else if (theLeft instanceof Bin && theRight instanceof Integer) {
			return ((Bin) theLeft).toInteger() <= (Integer) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Bin) {
			return (int) theLeft <= ((Bin) theRight).toInteger();
		} else if (theLeft instanceof Double && theRight instanceof Bin) {
			return ((Double) theLeft) <= (((Bin) theRight).toDouble());
		} else if (theLeft instanceof Bin && theRight instanceof Double) {
			return (((Bin) theLeft).toDouble()) <= ((Double) theRight);
		} else if (theLeft instanceof Bin && theRight instanceof Bin) {
			return Bin.lessThenEquals((Bin) theLeft, (Bin) theRight);
		} else if (binExpr != null)
			throw new RuntimeError(binExpr.operator, "Operands must be numbers.");
		else
			throw new RuntimeError(yraExpr.operator, "Operands must be numbers.");

	}

	private Object findRootForLeftAndRightAndSubtract(Object left, Object right, Expr expr) {
		Expr.Binary binExpr = null;
		Expr.Yranib yraExpr = null;
		if (expr instanceof Expr.Binary)
			binExpr = (Expr.Binary) expr;
		if (expr instanceof Expr.Yranib)
			yraExpr = (Expr.Yranib) expr;

		Object theLeft = left;
		Object theRight = right;
		if (left instanceof ArrayList) {
			if (((ArrayList<?>) left).size() > 0) {
				if (((ArrayList<?>) left).get(0) instanceof ArrayList) {
					theLeft = findRoot(((ArrayList<?>) left).get(0));
				}

			}
		}
		if (right instanceof ArrayList) {
			if (((ArrayList<?>) right).size() > 0) {
				if (((ArrayList<?>) right).get(0) instanceof ArrayList) {
					theRight = findRoot(((ArrayList<?>) right).get(0));
				}

			}
		}
		if (theLeft instanceof Double && theRight instanceof Double) {
			return (double) theLeft - (double) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Integer) {
			return (int) theLeft - (int) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Double) {
			return (int) theLeft - (double) theRight;
		} else if (theLeft instanceof Double && theRight instanceof Integer) {
			return (double) theLeft - (int) theRight;
		} else if (theLeft instanceof Bin && theRight instanceof Integer) {
			return ((Bin) theLeft).toInteger() - (Integer) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Bin) {
			return (int) theLeft - ((Bin) theRight).toInteger();
		} else if (theLeft instanceof Double && theRight instanceof Bin) {
			return ((Double) theLeft) - (((Bin) theRight).toDouble());
		} else if (theLeft instanceof Bin && theRight instanceof Double) {
			return (((Bin) theLeft).toDouble()) - ((Double) theRight);
		} else if (theLeft instanceof Bin && theRight instanceof Bin) {
			return Bin.subtract((Bin) theLeft, (Bin) theRight);
		} else if (binExpr != null)
			throw new RuntimeError(binExpr.operator, "Operands must be numbers.");
		else
			throw new RuntimeError(yraExpr.operator, "Operands must be numbers.");

	}

	private Object findRootForLeftAndRightAndAdd(Object left, Object right, Expr expr) {
		Expr.Binary binExpr = null;
		Expr.Yranib yraExpr = null;
		if (expr instanceof Expr.Binary)
			binExpr = (Expr.Binary) expr;
		if (expr instanceof Expr.Yranib)
			yraExpr = (Expr.Yranib) expr;

		Object theLeft = left;
		Object theRight = right;
		if (left instanceof ArrayList) {
			if (((ArrayList<?>) left).size() > 0) {
				if (((ArrayList<?>) left).get(0) instanceof ArrayList) {
					theLeft = findRoot(((ArrayList<?>) left).get(0));
				}

			}
		}
		if (right instanceof ArrayList) {
			if (((ArrayList<?>) right).size() > 0) {
				if (((ArrayList<?>) right).get(0) instanceof ArrayList) {
					theRight = findRoot(((ArrayList<?>) right).get(0));
				}

			}
		}
		if (theLeft instanceof Double && theRight instanceof Double) {
			return (double) theLeft + (double) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Integer) {
			return (int) theLeft + (int) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Double) {
			return (int) theLeft + (double) theRight;
		} else if (theLeft instanceof Double && theRight instanceof Integer) {
			return (double) theLeft + (int) theRight;
		} else if (theLeft instanceof Bin && theRight instanceof Integer) {
			return ((Bin) theLeft).toInteger() + (Integer) theRight;
		} else if (theLeft instanceof Integer && theRight instanceof Bin) {
			return (int) theLeft + ((Bin) theRight).toInteger();
		} else if (theLeft instanceof Double && theRight instanceof Bin) {
			return ((Double) theLeft) + (((Bin) theRight).toDouble());
		} else if (theLeft instanceof Bin && theRight instanceof Double) {
			return (((Bin) theLeft).toDouble()) + ((Double) theRight);
		} else if (theLeft instanceof Bin && theRight instanceof Bin) {
			return Bin.add((Bin) theLeft, (Bin) theRight);
		} else if (theLeft instanceof String && theRight instanceof String) {
			return (String) theLeft + (String) theRight;
		} else if (theLeft instanceof String && theRight instanceof Integer) {
			return (String) theLeft + ((Integer) theRight).toString();
		} else if (theLeft instanceof Integer && theRight instanceof String) {
			return ((Integer) theLeft).toString() + (String) theRight;
		} else if (theLeft instanceof String && theRight instanceof Double) {
			return (String) theLeft + ((Double) theRight).toString();
		} else if (theLeft instanceof Double && theRight instanceof String) {
			return ((Double) theLeft).toString() + (String) theRight;
		} else if (theLeft instanceof String && theRight instanceof Bin) {
			return (String) theLeft + ((Bin) theRight).toString();
		} else if (theLeft instanceof Bin && theRight instanceof String) {
			return ((Bin) theLeft).toString() + (String) theRight;
		} else if (theLeft instanceof BoxInstance && theRight instanceof String) {
			return theLeft.toString() + (String) theRight;
		} else if (theLeft instanceof String && theRight instanceof BoxInstance) {
			return (String) theLeft + theRight.toString();
		} else if (theLeft instanceof BoxInstance && theRight instanceof Integer) {
			return addBoxInstance(expr, theLeft, theRight);
		} else if (theLeft instanceof Integer && theRight instanceof BoxInstance) {
			return addBoxInstance(expr, theLeft, theRight);
		} else if (theLeft instanceof BoxInstance && theRight instanceof Double) {
			return addBoxInstance(expr, theLeft, theRight);
		} else if (theLeft instanceof Double && theRight instanceof BoxInstance) {
			return addBoxInstance(expr, theLeft, theRight);
		} else if (theLeft instanceof BoxInstance && theRight instanceof Bin) {
			return addBoxInstance(expr, theLeft, theRight);
		} else if (theLeft instanceof Bin && theRight instanceof BoxInstance) {
			return addBoxInstance(expr, theLeft, theRight);
		} else if (theLeft instanceof BoxInstance && theRight instanceof BoxInstance) {
			System.out.println("the Left: " + theLeft);
			System.out.println("the Right: " + theRight);
			return addBoxInstance(expr, theLeft, theRight);

		}
		if (binExpr != null)
			throw new RuntimeError(binExpr.operator, "Operands must be numbers.");
		else
			throw new RuntimeError(yraExpr.operator, "Operands must be numbers.");

	}

	private ArrayList<Object> addBoxInstance(Expr expr, Object theLeft, Object theRight) {
		ArrayList<Object> returnedObject = new ArrayList<Object>();
		int leftSize = 1;
		if (theLeft instanceof BoxInstance) {
			leftSize = ((BoxInstance) theLeft).size();
		}
		int rightSize = 1;
		if (theRight instanceof BoxInstance) {
			rightSize = ((BoxInstance) theRight).size();
		}

		for (int leftindex = 0; leftindex < leftSize; leftindex++) {
			for (int rightindex = 0; rightindex < rightSize; rightindex++) {
				Object leftatindex = theLeft;
				if (theLeft instanceof BoxInstance) {
					leftatindex = ((BoxInstance) theLeft).getAt(leftindex);
					if (leftatindex instanceof Stmt.Expression) {
						Expr expression = ((Stmt.Expression) leftatindex).expression;
						leftatindex = evaluate(expression);
					}
				}

				Object leftvalue = leftatindex;
				if (leftatindex instanceof Expr.Literal) {
					leftvalue = ((Expr.Literal) leftatindex).value;
				}

				Object rightatindex = theRight;
				if (theRight instanceof BoxInstance) {
					rightatindex = ((BoxInstance) theRight).getAt(rightindex);
					if (rightatindex instanceof Stmt.Expression) {
						Expr expression = ((Stmt.Expression) rightatindex).expression;
						rightatindex = evaluate(expression);
					}
				}
				Object rightvalue = rightatindex;
				if (rightatindex instanceof Expr.Literal) {
					rightvalue = ((Expr.Literal) rightatindex).value;
				}

				Object findRootForLeftAndRightAndAdd = findRootForLeftAndRightAndAdd(leftvalue, rightvalue, expr);
				returnedObject.add(findRootForLeftAndRightAndAdd);
				System.out.println(findRootForLeftAndRightAndAdd);
			}
		}
		return returnedObject;
	}

	private Object findRoot(Object object) {
		if (object instanceof ArrayList) {
			if (((ArrayList<?>) object).size() > 0) {
				return findRoot(((ArrayList<?>) object).get(0));
			}
		}
		return object;
	}

	@Override
	public Object visitLiteralExpr(Literal expr) {

		return expr.value;
	}

	@Override
	public Object visitUnaryExpr(Unary expr) {
		Object right = evaluate(expr.right);

		switch (expr.operator.type) {
		case QMARK:
			return !isTruthy(right);
		case MINUS:
			if (right instanceof Double)
				return -(double) right;
			if (right instanceof Integer)
				return -(int) right;

			if (right instanceof Bin)
				return ((Bin) right).negate();
			if (right instanceof ArrayList) {

				return findRootForMinus(right);

			}
			throw new RuntimeError(expr.operator, "Operand must be a number.");

		case PLUSPLUS:
			if (right instanceof Double) {
				Integer distance = locals.get(expr.right);
				double value = (double) right + 1.0;
				if (expr.right instanceof Expr.Variable) {
					if (distance != null) {
						environment.assignAt(distance, ((Expr.Variable) expr.right).name, new Expr.Literal(value),
								value, this);
					} else
						globals.assign(((Expr.Variable) expr.right).name, new Literal(value), value, this);
				}

				return (double) right + 1.0;
			}
			if (right instanceof Integer) {
				Integer distance = locals.get(expr.right);
				int value = (int) right + 1;
				if (expr.right instanceof Expr.Variable) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Variable) expr.right).name, new Expr.Literal(value),
								value, this);
					else
						globals.assign(((Expr.Variable) expr.right).name, new Literal(value), value, this);
				}

				return value;
			}
			if (right instanceof Bin) {
				Integer distance = locals.get(expr.right);
				Bin value = Bin.add((Bin) right, new Bin("1"));
				if (expr.right instanceof Expr.Variable) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Variable) expr.right).name, new Literal(value), value,
								this);
					else
						globals.assign(((Expr.Variable) expr.right).name, new Literal(value), value, this);
				}

				return value;
			}
			if (right instanceof ArrayList) {
				Object findRootForPlusPlus = findRootForPlusPlus(right);
				Integer distance = locals.get(expr.right);
				if (expr.right instanceof Expr.Variable) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Variable) expr.right).name,
								new Expr.Literal(findRootForPlusPlus), findRootForPlusPlus, this);
					else
						globals.assign(((Expr.Variable) expr.right).name, new Expr.Literal(findRootForPlusPlus),
								findRootForPlusPlus, this);
				}

				return findRootForPlusPlus;

			}
			throw new RuntimeError(expr.operator, "Operand must be a number.");
		case MINUSMINUS:
			if (right instanceof Double) {
				Integer distance = locals.get(expr.right);
				double value = (double) right - 1.0;
				if (expr.right instanceof Expr.Variable) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Variable) expr.right).name, new Literal(value), value,
								this);
					else
						globals.assign(((Expr.Variable) expr.right).name, new Literal(value), value, this);
				}

				return value;
			}
			if (right instanceof Integer) {
				Integer distance = locals.get(expr.right);
				int value = (int) right - 1;
				if (expr.right instanceof Expr.Variable) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Variable) expr.right).name, new Literal(value), value,
								this);
					else
						globals.assign(((Expr.Variable) expr.right).name, new Literal(value), value, this);
				}

				return value;
			}
			if (right instanceof Bin) {
				Integer distance = locals.get(expr.right);
				Bin value = Bin.subtract((Bin) right, new Bin("1"));
				if (expr.right instanceof Expr.Variable) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Variable) expr.right).name, new Literal(value), value,
								this);
					else
						globals.assign(((Expr.Variable) expr.right).name, new Literal(value), value, this);
				}

				return value;
			}
			if (right instanceof ArrayList) {
				Object findRootForMinusMinus = findRootForMinusMinus(right);
				Integer distance = locals.get(expr.right);
				if (expr.right instanceof Expr.Variable) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Variable) expr.right).name,
								new Literal(findRootForMinusMinus), findRootForMinusMinus, this);
					else
						globals.assign(((Expr.Variable) expr.right).name, new Literal(findRootForMinusMinus),
								findRootForMinusMinus, this);
				}

				return findRootForMinusMinus;
			}
			throw new RuntimeError(expr.operator, "Operand must be a number.");
		default:
			return null;
		}

	}

	private Object findRootForMinus(Object right) {
		if (((ArrayList<?>) right).size() > 0) {
			if (((ArrayList<?>) right).get(0) instanceof ArrayList) {
				return findRootForMinus(((ArrayList<?>) right).get(0));
			} else if (((ArrayList<?>) right).get(0) instanceof Bin) {
				return ((Bin) ((ArrayList<?>) right).get(0)).negate();
			} else if (((ArrayList<?>) right).get(0) instanceof Double) {
				return -(double) ((ArrayList<?>) right).get(0);
			} else if (((ArrayList<?>) right).get(0) instanceof Integer) {
				return -(int) ((ArrayList<?>) right).get(0);
			}

		}
		return null;
	}

	private Object findRootForPlus(Object right) {
		if (((ArrayList<?>) right).size() > 0) {
			if (((ArrayList<?>) right).get(0) instanceof ArrayList) {
				return findRootForMinus(((ArrayList<?>) right).get(0));
			} else if (((ArrayList<?>) right).get(0) instanceof Bin) {
				return ((Bin) ((ArrayList<?>) right).get(0));
			} else if (((ArrayList<?>) right).get(0) instanceof Double) {
				return (double) ((ArrayList<?>) right).get(0);
			} else if (((ArrayList<?>) right).get(0) instanceof Integer) {
				return (int) ((ArrayList<?>) right).get(0);
			}

		}
		return null;
	}

	private Object findRootForPlusPlus(Object right) {
		if (((ArrayList<?>) right).size() > 0) {
			if (((ArrayList<?>) right).get(0) instanceof ArrayList) {
				return findRootForPlus(((ArrayList<?>) right).get(0));
			} else if (((ArrayList<?>) right).get(0) instanceof Bin) {
				return Bin.add(((Bin) ((ArrayList<?>) right).get(0)), new Bin("1"));
			} else if (((ArrayList<?>) right).get(0) instanceof Double) {
				return ((double) ((ArrayList<?>) right).get(0)) + 1.0;
			} else if (((ArrayList<?>) right).get(0) instanceof Integer) {
				return ((int) ((ArrayList<?>) right).get(0)) + 1;
			}

		}
		return null;
	}

	private Object findRootForMinusMinus(Object right) {
		if (((ArrayList<?>) right).size() > 0) {
			if (((ArrayList<?>) right).get(0) instanceof ArrayList) {
				return findRootForMinus(((ArrayList<?>) right).get(0));
			} else if (((ArrayList<?>) right).get(0) instanceof Bin) {
				return Bin.subtract(((Bin) ((ArrayList<?>) right).get(0)), new Bin("1"));
			} else if (((ArrayList<?>) right).get(0) instanceof Double) {
				return ((double) ((ArrayList<?>) right).get(0)) - 1.0;
			} else if (((ArrayList<?>) right).get(0) instanceof Integer) {
				return ((int) ((ArrayList<?>) right).get(0)) - 1;
			}

		}
		return null;
	}

	@Override
	public Object visitYranuExpr(Yranu expr) {
		Object right = evaluate(expr.right);

		switch (expr.operator.type) {
		case QMARK:
			return !isTruthy(right);
		case MINUS:
			if (right instanceof Double)
				return -(double) right;
			if (right instanceof Integer)
				return -(int) right;

			if (right instanceof Bin)
				return ((Bin) right).negate();
			if (right instanceof ArrayList) {

				return findRootForMinus(right);
			}

			throw new RuntimeError(expr.operator, "Operand must be a number.");

		case PLUSPLUS:
			if (right instanceof Double) {
				Integer distance = locals.get(expr.right);
				double value = (double) right + 1.0;
				if (expr.right instanceof Expr.Variable) {
					if (distance != null) {
						environment.assignAt(distance, ((Expr.Variable) expr.right).name, new Expr.Literal(value),
								value, this);
					} else
						globals.assign(((Expr.Variable) expr.right).name, new Literal(value), value, this);
				}

				return (double) right + 1.0;
			}
			if (right instanceof Integer) {
				Integer distance = locals.get(expr.right);
				int value = (int) right + 1;
				if (expr.right instanceof Expr.Variable) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Variable) expr.right).name, new Expr.Literal(value),
								value, this);
					else
						globals.assign(((Expr.Variable) expr.right).name, new Literal(value), value, this);
				}

				return value;
			}
			if (right instanceof Bin) {
				Integer distance = locals.get(expr.right);
				Bin value = Bin.add((Bin) right, new Bin("1"));
				if (expr.right instanceof Expr.Variable) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Variable) expr.right).name, new Literal(value), value,
								this);
					else
						globals.assign(((Expr.Variable) expr.right).name, new Literal(value), value, this);
				}

				return value;
			}
			if (right instanceof ArrayList) {
				Object findRootForPlusPlus = findRootForPlusPlus(right);
				Integer distance = locals.get(expr.right);
				if (expr.right instanceof Expr.Variable) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Variable) expr.right).name,
								new Expr.Literal(findRootForPlusPlus), findRootForPlusPlus, this);
					else
						globals.assign(((Expr.Variable) expr.right).name, new Expr.Literal(findRootForPlusPlus),
								findRootForPlusPlus, this);
				}

				return findRootForPlusPlus;

			}
			throw new RuntimeError(expr.operator, "Operand must be a number.");
		case MINUSMINUS:
			if (right instanceof Double) {
				Integer distance = locals.get(expr.right);
				double value = (double) right - 1.0;
				if (expr.right instanceof Expr.Variable) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Variable) expr.right).name, new Literal(value), value,
								this);
					else
						globals.assign(((Expr.Variable) expr.right).name, new Literal(value), value, this);
				}

				return value;
			}
			if (right instanceof Integer) {
				Integer distance = locals.get(expr.right);
				int value = (int) right - 1;
				if (expr.right instanceof Expr.Variable) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Variable) expr.right).name, new Literal(value), value,
								this);
					else
						globals.assign(((Expr.Variable) expr.right).name, new Literal(value), value, this);
				}

				return value;
			}
			if (right instanceof Bin) {
				Integer distance = locals.get(expr.right);
				Bin value = Bin.subtract((Bin) right, new Bin("1"));
				if (expr.right instanceof Expr.Variable) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Variable) expr.right).name, new Literal(value), value,
								this);
					else
						globals.assign(((Expr.Variable) expr.right).name, new Literal(value), value, this);
				}

				return value;
			}
			if (right instanceof ArrayList) {
				Object findRootForMinusMinus = findRootForMinusMinus(right);
				Integer distance = locals.get(expr.right);
				if (expr.right instanceof Expr.Variable) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Variable) expr.right).name,
								new Literal(findRootForMinusMinus), findRootForMinusMinus, this);
					else
						globals.assign(((Expr.Variable) expr.right).name, new Literal(findRootForMinusMinus),
								findRootForMinusMinus, this);
				}

				return findRootForMinusMinus;
			}
			throw new RuntimeError(expr.operator, "Operand must be a number.");
		default:
			return null;
		}

	}

	public void executeBlock(List<Stmt> statements, Environment env) {
		Environment previous = this.environment;
		try {
			this.environment = env;

			for (Stmt stmt : statements) {
				execute(stmt);
			}
		} finally {
			this.environment = previous;
		}
	}

	@Override
	public Void visitReturnStmt(Stmt.Return stmt) {
		Object value = null;
		if (stmt.expression != null)
			value = evaluate(stmt.expression);
		throw new Returns(value);
	}

	@Override
	public Object visitContainsExpr(Contains expr) {

		if (expr.container instanceof Expr.Variable) {
			BoxInstance lookUpContainer = (BoxInstance) lookUpVariable(((Expr.Variable) expr.container).name,
					expr.container);
			System.out.println("hey");
			if (expr.contents instanceof Expr.Variable) {
				BoxInstance lookUpContents = (BoxInstance) lookUpVariable(((Expr.Variable) expr.contents).name,
						expr.contents);
				System.out.println("hi");
				boolean contains = lookUpContainer.contains(lookUpContents);
				if (contains) {
					System.out.println("hey");
				}
			}
		}
		return null;
	}

	private boolean isEqual(Object a, Object b) {
		if (a == null && b == null)
			return true;
		if (a == null)
			return false;

		return a.equals(b);
	}

	@Override
	public Object visitMonoExpr(Mono expr) {
		Double result = 0.0;
		Object evaluate = evaluate(expr.value);
		Double evaluateDouble = 0.0;
		if (evaluate instanceof Integer) {
			evaluateDouble = Double.valueOf(Integer.toString((Integer) evaluate));
		} else if (evaluate instanceof Double) {
			evaluateDouble = (Double) evaluate;
		} else if (evaluate instanceof Bin) {
			evaluateDouble = ((Bin) evaluate).toDouble();

		}

		switch (expr.operator.type) {
		case SIN:
			result = Math.sin(evaluateDouble);
			break;
		case COS:
			result = Math.cos(evaluateDouble);
			break;
		case TAN:
			result = Math.tan(evaluateDouble);
			break;

		case SINH:
			result = Math.sinh(evaluateDouble);
			break;

		case COSH:
			result = Math.cosh(evaluateDouble);
			break;

		case TANH:
			result = Math.tanh(evaluateDouble);
			break;

		default:
			break;
		}

		return result;
	}

	@Override
	public Object visitLogExpr(Log expr) {
		Double evaluateDoubleValue = findDoubleValue(expr.value) - 1;
		Double evaluateDoubleValueBase = findDoubleValue(expr.valueBase) - 1;

		return Math.log1p(evaluateDoubleValue) / Math.log1p(evaluateDoubleValueBase);
	}

	private Double findDoubleValue(Expr value) {
		Object evaluateValue = evaluate(value);
		Double evaluateDoubleValue = 0.0;
		if (evaluateValue instanceof Integer) {
			evaluateDoubleValue = Double.valueOf(Integer.toString((Integer) evaluateValue));
		} else if (evaluateValue instanceof Double) {
			evaluateDoubleValue = (Double) evaluateValue;
		} else if (evaluateValue instanceof Bin) {
			evaluateDoubleValue = ((Bin) evaluateValue).toDouble();

		}
		return evaluateDoubleValue;
	}

	@Override
	public Object visitFactorialExpr(Factorial expr) {
		Object evaluate = evaluate(expr.value);
		Double evaluateDouble = 0.0;
		Integer evaluateInteger = 0;
		if (evaluate instanceof Integer) {
			evaluateInteger = (Integer) evaluate;

			int i, fact = 1;
			for (i = 1; i <= evaluateInteger; i++) {
				fact = fact * i;
			}
			return fact;
		} else if (evaluate instanceof Double) {
			evaluateDouble = (Double) evaluate;
			Double nfactorial = factorial(evaluateDouble);

			return nfactorial;
		} else if (evaluate instanceof Bin) {
			evaluateInteger = ((Bin) evaluate).toInteger();
			int i;
			int fact = 1;
			for (i = 1; i <= evaluateInteger; i++) {
				fact = fact * i;
			}
			return fact;
		}

		return -1;
	}

	private double factorial(Double evaluateDouble) {
		return evaluateDouble * gammaOfN(evaluateDouble);
	}

	private Double gammaOfN(Double evaluateDouble) {
		if (evaluateDouble == .5) {
			return findNthRootOfRemainder(2.0, Math.PI);
		} else if (evaluateDouble < 0) {
			return 1.0;
		}
		Double gamma = factorial(evaluateDouble - 1);
		return gamma;
	}

	private boolean isTruthy(Object right) {
		if (right == null)
			return false;
		if (right instanceof Boolean)
			return (boolean) right;

		return true;
	}

	@Override
	public Void visitRenameStmt(Rename stmt) {
		File file = new File((String) evaluate(stmt.filePathAndName));

		if (file.renameTo(new File((String) evaluate(stmt.filenewname)))) {
			System.out.println("File Renamed successfully");
		} else {
			System.out.println("Failed to Rename the file");
		}
		return null;
	}

	@Override
	public Void visitMoveStmt(Move stmt) {

		File file = new File((String) evaluate(stmt.OringialfilePathAndFile));

		if (file.renameTo(new File((String) evaluate(stmt.newfilePath)))) {
			file.delete();
			System.out.println("File moved successfully");
		} else {
			System.out.println("Failed to move the file");
		}
		return null;
	}

	@Override
	public Void visitTnirpStmt(Tnirp stmt) {
		Object value = evaluate(stmt.expression);
		System.out.println(stringify(value));
		return null;
	}

	@Override
	public Void visitNruterStmt(Nruter stmt) {
		Object value = null;
		if (stmt.expression != null)
			value = evaluate(stmt.expression);
		throw new Snruter(value);
	}

	@Override
	public Void visitEvasStmt(Evas stmt) {
		try {
			String filePathAndName = (String) evaluate(stmt.filePathFileName);
			String[] split = filePathAndName.split("/");
			String folderPath = "";
			if (split[split.length - 1].contains(".")) {
				for (int i = 0; i < split.length - 1; i++) {
					folderPath += split[i] + "/";
				}
			} else {
				for (int i = 0; i < split.length; i++) {
					folderPath += split[i] + "/";
				}
			}
			folderPath = folderPath.substring(0, folderPath.length() - 1);
			File myObj = new File(filePathAndName);
			File myObjFolderPath = new File(folderPath);
			myObjFolderPath.mkdir();
			if (split[split.length - 1].contains(".")) {
				if (myObj.createNewFile()) {
					evaluate(stmt.objecttosave);
					String str = "";
					if (stmt.objecttosave instanceof Expr.Box) {
						Object boxInstance = lookUpVariable(((Expr.Box) stmt.objecttosave).identifier,
								((Expr.Box) stmt.objecttosave));
						str = boxInstance.toString();

					} else if (stmt.objecttosave instanceof Expr.Cup) {
						Object cupInstance = lookUpVariable(((Expr.Cup) stmt.objecttosave).identifier,
								((Expr.Cup) stmt.objecttosave));
						str = cupInstance.toString();
					} else if (stmt.objecttosave instanceof Expr.Pocket) {
						Object pocketInstance = lookUpVariable(((Expr.Pocket) stmt.objecttosave).identifier,
								((Expr.Pocket) stmt.objecttosave));
						str = pocketInstance.toString();
					} else if (stmt.objecttosave instanceof Expr.Knot) {
						Object knotInstance = lookUpVariable(((Expr.Knot) stmt.objecttosave).identifier,
								((Expr.Knot) stmt.objecttosave));
						str = knotInstance.toString();
					} else if (stmt.objecttosave instanceof Expr.Variable) {
						Object knotInstance = lookUpVariable(((Expr.Variable) stmt.objecttosave).name,
								((Expr.Variable) stmt.objecttosave));
						str = knotInstance.toString();
					}

					BufferedWriter writer = new BufferedWriter(new FileWriter(filePathAndName));
					writer.write(str);

					writer.close();

				} else {
					System.out.println("File already exists.");
				}
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Void visitDaerStmt(Daer stmt) {
		try {
			File myObj = new File((String) evaluate(stmt.filePath));
			Scanner myReader = new Scanner(myObj);
			String data = "";
			while (myReader.hasNextLine()) {
				data += myReader.nextLine();

			}
			evaluate(stmt.objectToReadInto);
			if (stmt.objectToReadInto instanceof Expr.Box) {
				Object boxInstance = lookUpVariable(((Expr.Box) stmt.objectToReadInto).identifier,
						((Expr.Box) stmt.objectToReadInto));
				if (boxInstance instanceof BoxInstance) {
					((BoxInstance) boxInstance).setAt(data, 0);
				}

			} else if (stmt.objectToReadInto instanceof Expr.Cup) {
				Object cupInstance = lookUpVariable(((Expr.Cup) stmt.objectToReadInto).identifier,
						((Expr.Cup) stmt.objectToReadInto));
				if (cupInstance instanceof BoxInstance) {
					((BoxInstance) cupInstance).setAt(data, 0);
				}
			} else if (stmt.objectToReadInto instanceof Expr.Pocket) {
				Object pocketInstance = lookUpVariable(((Expr.Pocket) stmt.objectToReadInto).identifier,
						((Expr.Pocket) stmt.objectToReadInto));
				if (pocketInstance instanceof BoxInstance) {
					((BoxInstance) pocketInstance).setAt(data, 0);
				}
			} else if (stmt.objectToReadInto instanceof Expr.Knot) {
				Object knotInstance = lookUpVariable(((Expr.Knot) stmt.objectToReadInto).identifier,
						((Expr.Knot) stmt.objectToReadInto));
				if (knotInstance instanceof BoxInstance) {
					((BoxInstance) knotInstance).setAt(data, 0);
				}
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Void visitEmanerStmt(Emaner stmt) {
		File file = new File((String) evaluate(stmt.filePathAndName));

		if (file.renameTo(new File((String) evaluate(stmt.filenewname)))) {
			System.out.println("File Renamed successfully");
		} else {
			System.out.println("Failed to Rename the file");
		}
		return null;
	}

	@Override
	public Void visitEvomStmt(Evom stmt) {
		File file = new File((String) evaluate(stmt.OringialfilePathAndFile));

		if (file.renameTo(new File((String) evaluate(stmt.newfilePath)))) {
			file.delete();
			System.out.println("File moved successfully");
		} else {
			System.out.println("Failed to move the file");
		}
		return null;
	}

	@Override
	public Object visitTnemngissaExpr(Tnemngissa expr) {
		Object value = evaluate(expr.value);
		Integer distance = locals.get(expr);
		if (distance != null)
			environment.assignAt(distance, expr.name, expr.value, value, this);
		else
			globals.assign(expr.name, expr.value, value, this);
		return value;
	}

	@Override
	public Object visitYranibExpr(Yranib expr) {
		Object left = evaluate(expr.left);
		Object right = evaluate(expr.right);

		switch (expr.operator.type) {
		case NOTEQUALS:
			return !isEqual(right, left);
		case EQUALSNOT:
			return !isEqual(right, left);
		case EQUALSEQUALS:
			return isEqual(right, left);
		case MINUSEQUALS:
			return findRootForLeftAndRightAndMinusEquals(right, left, expr);
		case PLUSEQUALS:
			return findRootForLeftAndRightAndPlusEquals(right, left, expr);
		case GREATERTHEN:
			return findRootForLeftAndRightAndGreaterThen(right, left, expr);
		case GREATERTHENEQUAL:
			return findRootForLeftAndRightAndGreaterThenEquals(right, left, expr);
		case LESSTHEN:
			return findRootForLeftAndRightAndLessThen(right, left, expr);
		case LESSTHENEQUAL:
			return findRootForLeftAndRightAndLessThenEquals(right, left, expr);
		case MINUS:

			return findRootForLeftAndRightAndSubtract(right, left, expr);

		case PLUS:

			return findRootForLeftAndRightAndAdd(right, left, expr);

		case FORWARDSLASH:
			return findRootForLeftAndRightAndDivide(right, left, expr);
		case BACKSLASH:
			return findRootForLeftAndRightAndDivide(right, left, expr);
		case TIMES:
			return findRootForLeftAndRightAndTimes(right, left, expr);

		case POWER:
			return findRootForLeftAndRightAndPower(right, left, expr);
		case TOORY:
			return findRootForLeftAndRightAndYroot(right, left, expr);
		default:
			return null;

		}

	}

	@Override
	public Object visitOnomExpr(Onom expr) {
		Double result = 0.0;
		Object evaluate = evaluate(expr.value);
		Double evaluateDouble = 0.0;
		if (evaluate instanceof Integer) {
			evaluateDouble = Double.valueOf(Integer.toString((Integer) evaluate));
		} else if (evaluate instanceof Double) {
			evaluateDouble = (Double) evaluate;
		} else if (evaluate instanceof Bin) {
			evaluateDouble = ((Bin) evaluate).toDouble();

		}

		switch (expr.operator.type) {
		case NIS:
			result = Math.sin(evaluateDouble);
			break;
		case SOC:
			result = Math.cos(evaluateDouble);
			break;
		case NAT:
			result = Math.tan(evaluateDouble);
			break;

		case HNIS:
			result = Math.sinh(evaluateDouble);
			break;

		case HSOC:
			result = Math.cosh(evaluateDouble);
			break;

		case HNAT:
			result = Math.tanh(evaluateDouble);
			break;

		default:
			break;
		}

		return result;
	}

	@Override
	public Object visitGolExpr(Gol expr) {
		Double evaluateDoubleValue = findDoubleValue(expr.value) - 1;
		Double evaluateDoubleValueBase = findDoubleValue(expr.valueBase) - 1;

		return Math.log1p(evaluateDoubleValue) / Math.log1p(evaluateDoubleValueBase);
	}

	@Override
	public Object visitLairotcafExpr(Lairotcaf expr) {
		Object evaluate = evaluate(expr.value);
		Double evaluateDouble = 0.0;
		Integer evaluateInteger = 0;
		if (evaluate instanceof Integer) {
			evaluateInteger = (Integer) evaluate;

			int i, fact = 1;
			for (i = 1; i <= evaluateInteger; i++) {
				fact = fact * i;
			}
			return fact;
		} else if (evaluate instanceof Double) {
			evaluateDouble = (Double) evaluate;
			int i;
			double fact = 1.0;
			for (i = 1; i <= evaluateDouble; i++) {
				fact = fact * i;
			}
			return fact;
		} else if (evaluate instanceof Bin) {
			evaluateInteger = ((Bin) evaluate).toInteger();
			int i;
			int fact = 1;
			for (i = 1; i <= evaluateInteger; i++) {
				fact = fact * i;
			}
			return fact;
		}

		return -1;
	}

	@Override
	public Object visitLlacExpr(Llac expr) {
		Object callee = evaluate(expr.callee);
		List<Object> arguments = new ArrayList<>();
		for (Expr argument : expr.arguments) {
			arguments.add(evaluate(argument));
		}

		if (!(callee instanceof BoxCallable)) {
			throw new RuntimeError(expr.calleeToken, "Can only call functions and classes.");
		}

		BoxCallable function = (BoxCallable) callee;
		if (arguments.size() != function.arity()) {
			throw new RuntimeError(expr.calleeToken,
					"Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
		}

		return function.call(this, arguments);
	}

	public void resolve(Expr expr, int i) {
		locals.put(expr, i);
	}

	private Object lookUpType(Expr expression) {
		Integer distance = locals.get((Variable) expression);
		if (distance != null) {
			return environment.getTypeAt(distance, ((Variable) expression).name.lexeme);
		} else {
			return globals.getType(((Variable) expression).name);
		}
	}

	public void setForward(boolean forward) {
		this.forward = forward;
	}

	public void setBackward(boolean backward) {
		this.backward = backward;
	}

	@Override
	public Object visitFunDeclDeclaration(FunDecl declaration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitStmtDeclDeclaration(StmtDecl declaration) {
		declaration.statement.accept(this);
		return null;
	}

	@Override
	public Object visitFunctionFun(Function fun) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIfStmt(If stmt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSaveStmt(Save stmt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitExpelStmt(Expel stmt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitReadStmt(Read stmt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitConsumeStmt(Consume stmt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitVarStmt(Var stmt) {
		environment.define(stmt.name.lexeme, stmt.type, stmt.num, stmt.initilizer, this);
		return null;
	}

	@Override
	public Object visitRavStmt(Rav stmt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitGetExpr(Get expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSetExpr(Set expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSniatnocExpr(Sniatnoc expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTegExpr(Teg expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTesExpr(Tes expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLiteralCharExpr(LiteralChar expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCupExpr(Cup expr) {
		// TODO Auto-generated method stub
		return runContainer(expr);
	}

	@Override
	public Object visitPocketExpr(Pocket expr) {
		// TODO Auto-generated method stub
		return runContainer(expr);
	}

	@SuppressWarnings("finally")
	private Object runContainer(Expr stmt) {
		buildClass(stmt);
		Token theName = null;
		Pocket pktStmt = null;
		Cup cupStmt = null;

		List<Stmt> expression = null;
		List<Declaration> expression1 = null;

		ArrayList<Object> evaluated = new ArrayList<Object>();
		if (stmt instanceof Pocket) {
			pktStmt = (Pocket) stmt;
			theName = pktStmt.identifier;
			expression = pktStmt.expression;

			Variable theNameVariable = new Expr.Variable(theName);
			Environment previous = null;
			if (lookUpVariable(theName, theNameVariable) == null) {
				previous = this.environment;
			}

			try {
				if (lookUpVariable(theName, theNameVariable) == null) {
					this.environment = new Environment(environment);
				}

				for (Stmt stmt2 : expression) {
					if (stmt2 instanceof Stmt.Expression)
						if (((Stmt.Expression) stmt2).expression != null)
							evaluated.add(evaluate(((Stmt.Expression) stmt2).expression));
					if (((Stmt.Expression) stmt2).noisserpxe != null)
						evaluated.add(evaluate(((Stmt.Expression) stmt2).noisserpxe));
					else {
						execute(stmt2);
					}
				}
			} finally {
				if (lookUpVariable(theName, theNameVariable) == null) {
					this.environment = previous;
				}
			}

		} else if (stmt instanceof Cup) {
			cupStmt = (Cup) stmt;
			theName = cupStmt.identifier;
			expression1 = cupStmt.expression;

			Variable theNameVariable = new Expr.Variable(theName);
			Environment previous = null;
			if (lookUpVariable(theName, theNameVariable) == null) {
				previous = this.environment;
			}

			try {
				if (lookUpVariable(theName, theNameVariable) == null) {
					this.environment = new Environment(environment);
				}

				for (Declaration stmt2 : expression1) {
					if (stmt2 instanceof Declaration.StmtDecl) {
						if ((((Declaration.StmtDecl) stmt2).statement) instanceof Stmt.Expression) {
							if (((Stmt.Expression) ((((Declaration.StmtDecl) stmt2).statement))).expression != null) {
								evaluated.add(evaluate(
										((Stmt.Expression) ((((Declaration.StmtDecl) stmt2).statement))).expression));
							}
							if (((Stmt.Expression) ((((Declaration.StmtDecl) stmt2).statement))).noisserpxe != null) {
								evaluated.add(evaluate(
										((Stmt.Expression) ((((Declaration.StmtDecl) stmt2).statement))).noisserpxe));
							}
						}

					} else {
						execute(stmt2);
					}
				}
			} finally {
				if (lookUpVariable(theName, theNameVariable) == null) {
					this.environment = previous;
				}
			}

		}
		return evaluated;
	}

	private void buildClass(Expr stmt) {

		Object superclass = null;

		Token theName = null;
		Cup cup = null;
		Pocket pocket = null;
		Token theEman = null;

		if (stmt instanceof Expr.Cup) {
			cup = (Cup) stmt;
			theName = cup.identifier;
			theEman = cup.reifitnedi;
		} else if (stmt instanceof Expr.Pocket) {
			pocket = (Pocket) stmt;
			theName = pocket.identifier;
			theEman = pocket.reifitnedi;
		}

		Variable theNameVariable = new Expr.Variable(theName);

		if (lookUpVariable(theName, theNameVariable) == null) {

			Token superclassToken = null;
			if (theName.identifierToken != null)
				superclassToken = new Token(theName.type, theName.identifierToken.lexeme + "Class_Definition", null,
						null, null, theName.column, theName.line, theName.start, theName.finish);

			BoxClass superclassVariable = (BoxClass) lookUpVariableByName(superclassToken);

			if (superclassVariable != null) {
				superclass = superclassVariable;
			}
			Token type = null;
			if (cup != null) {
				type = new Token(TokenType.CUPCONTAINER, "", null, null, null, -1, -1, -1, -1);
			} else if (pocket != null) {
				type = new Token(TokenType.POCKETCONTAINER, "", null, null, null, -1, -1, -1, -1);
			}

			environment.define(theName.lexeme + "_Class_Definition", type, null);
			environment.define(theEman.lexeme + "_noitinifeD_ssalC", type, null);
			if (superclass != null) {
				environment = new Environment(environment);
				Token superclassType = new Token(((BoxClass) superclass).type, "", null, null, null, -1, -1, -1, -1);
				environment.define("super", superclassType, superclass);

			}

			Map<String, BoxFunction> methodsBoxFunction = new HashMap<>();

			List<Fun.Function> methods = new ArrayList<Fun.Function>();
			if (stmt instanceof Expr.Cup) {
				List<Declaration> declaration = ((Expr.Cup) stmt).expression;
				for (Declaration stmt2 : declaration) {
					if (stmt2 instanceof Fun.Function) {
						methods.add((Fun.Function) stmt2);
					}
				}

				for (Fun.Function method : methods) {
					System.out.println("functions not implemented yet");
				}
			}
			BoxClass boxClass = null;
			if (cup != null) {
				boxClass = executeAndBuildBoxClass(superclass, theName, cup, theEman, methodsBoxFunction,
						TokenType.CUPCONTAINER);
			} else if (pocket != null) {
				boxClass = executeAndBuildBoxClass(superclass, theName, cup, theEman, methodsBoxFunction,
						TokenType.POCKETCONTAINER);
			}

			if (superclass != null) {
				environment = environment.enclosing;
			}

			Token classDefinitionName = new Token(theName.type, theName.lexeme + "_Class_Definition", null, null, null,
					theName.column, theName.line, theName.start, theName.finish);
			Token classDefinitionEman = new Token(theEman.type, theEman.lexeme + "_noitinifeD_ssalC", null, null, null,
					theName.column, theName.line, theName.start, theName.finish);
			environment.assign(classDefinitionName, type, boxClass);
			environment.assign(classDefinitionEman, type, boxClass);
			Object instance = boxClass.call(this, null);

			environment.define(theName.lexeme, type, instance);
			environment.define(theEman.lexeme, type, instance);

		}

	}

	private BoxClass executeAndBuildBoxClass(Object superclass, Token theName, Cup cup, Token theEman,
			Map<String, BoxFunction> methodsBoxFunction, TokenType containerType) {
		BoxClass boxClass;
		executePrimaryBoxCupPocketAndKnot(cup);
		ArrayList<Object> boxPrimarys = new ArrayList<Object>();
		int count = 0;
		populateBoxPrimarys(theName, cup, theEman, boxPrimarys, count);

		boxClass = new BoxClass(theName.lexeme, (BoxClass) superclass, boxPrimarys, methodsBoxFunction, containerType,
				false, null);
		return boxClass;
	}

	private void populateBoxPrimarys(Token theName, Expr container, Token theEman, ArrayList<Object> boxPrimarys,
			int count) {
		List<Stmt> expression = null;
		List<Declaration> expression1 = null;
		List<Expr> primarys = null;
		if (container instanceof Expr.Cup) {
			expression1 = ((Expr.Cup) container).expression;
			primarysForCup(boxPrimarys, expression1);
		} else if (container instanceof Expr.Pocket) {
			expression = ((Expr.Pocket) container).expression;
			primarysForBoxAndPocket(boxPrimarys, expression);
		}

	}

	private void primarysForBoxAndPocket(ArrayList<Object> boxPrimarys, List<Stmt> expression) {
		for (int i = 0; i < expression.size(); i++) {
			if (expression.get(i) instanceof Stmt.Expression) {
				if (((Stmt.Expression) expression.get(i)).expression instanceof Expr.Box) {
					Object boxInstance = lookUpVariable(
							((Expr.Box) ((Stmt.Expression) expression.get(i)).expression).identifier, new Expr.Variable(
									((Expr.Box) ((Stmt.Expression) expression.get(i)).expression).identifier));
					boxPrimarys.add(boxInstance);
				} else if (((Stmt.Expression) expression.get(i)).expression instanceof Expr.Pocket) {
					Object pocketInstance = lookUpVariable(
							((Expr.Pocket) ((Stmt.Expression) expression.get(i)).expression).identifier,
							new Expr.Variable(
									((Expr.Pocket) ((Stmt.Expression) expression.get(i)).expression).identifier));
					boxPrimarys.add(pocketInstance);
				} else {
					boxPrimarys.add(expression.get(i));
				}
			}
		}
	}

	private void primarysForCup(ArrayList<Object> boxPrimarys, List<Declaration> expression) {
		for (int i = 0; i < expression.size(); i++) {
			if (expression.get(i) instanceof Stmt.Expression) {
				if (((Stmt.Expression) expression.get(i)).expression instanceof Expr.Cup) {
					Object cupInstance = lookUpVariable(
							((Expr.Cup) ((Stmt.Expression) expression.get(i)).expression).identifier, new Expr.Variable(
									((Expr.Cup) ((Stmt.Expression) expression.get(i)).expression).identifier));
					boxPrimarys.add(cupInstance);
				} else {
					boxPrimarys.add(expression.get(i));
				}
			}
		}
	}

	private void executePrimaryBoxCupPocketAndKnot(Expr stmt) {

		if (stmt instanceof Cup) {
			for (Declaration statement : ((Cup) stmt).expression) {
				executeStatement(statement);
			}
		} else if (stmt instanceof Pocket) {
			for (Stmt statement : ((Pocket) stmt).expression) {
				executeStatement(statement);
			}
		}

	}

	private void executeStatement(Stmt statement) {
		if (statement instanceof Stmt.Expression) {
			if (((Stmt.Expression) statement).expression instanceof Expr.Box) {
				evaluate(((Stmt.Expression) statement).expression);
			} else if (((Stmt.Expression) statement).expression instanceof Expr.Pocket) {
				evaluate(((Stmt.Expression) statement).expression);
			} else if (((Stmt.Expression) statement).expression instanceof Expr.Knot) {
				evaluate(((Stmt.Expression) statement).expression);
			}
		}
	}

	private void executeStatement(Declaration statement) {
		if (statement instanceof Declaration.StmtDecl) {
			if (((Declaration.StmtDecl) statement).statement instanceof Stmt.Expression) {
				if (((Stmt.Expression) ((Declaration.StmtDecl) statement).statement).expression != null)
					evaluate(((Stmt.Expression) ((Declaration.StmtDecl) statement).statement).expression);
				if (((Stmt.Expression) ((Declaration.StmtDecl) statement).statement).noisserpxe != null)
					evaluate(((Stmt.Expression) ((Declaration.StmtDecl) statement).statement).noisserpxe);
			}
		}
	}

	@Override
	public Object visitKnotExpr(Knot expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTonkExpr(Tonk expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBoxExpr(Expr.Box expr) {
		buildBoxClass(expr);
		Token theName = expr.identifier;
		Variable theNameVariable = new Expr.Variable(theName);
		Environment previous = null;
		ArrayList<Object> evaluated = new ArrayList<Object>();
		if (lookUpVariable(theName, theNameVariable) == null) {
			previous = this.environment;
		}

		try {
			if (lookUpVariable(theName, theNameVariable) == null) {
				this.environment = new Environment(environment);
			}

			for (Stmt stmt2 : expr.expression) {
				if (stmt2 instanceof Stmt.Expression)
					if (((Stmt.Expression) stmt2).expression != null)
						evaluated.add(evaluate(((Stmt.Expression) stmt2).expression));
				if (((Stmt.Expression) stmt2).noisserpxe != null)
					evaluated.add(evaluate(((Stmt.Expression) stmt2).noisserpxe));
				else {
					execute(stmt2);
				}
			}
		} finally {
			if (lookUpVariable(theName, theNameVariable) == null) {
				this.environment = previous;
			}
		}

		return evaluated;
	}

	private Object buildBoxClass(Expr.Box stmt) {

		Token theName = stmt.identifier;
		Token theEman = stmt.reifitnedi;

		if (lookUpVariable(theName, stmt) == null) {

			Token type = new Token(TokenType.BOXCONTAINER, "", null, null, null, -1, -1, -1, -1);
			environment.define(theName.lexeme + "_Box_Definition", type, null);
			environment.define(theEman.lexeme + "_Box_Definition", type, null);

			for (Stmt expr : stmt.expression) {
				evaluate(expr);
			}
			ArrayList<Object> boxPrimarys = new ArrayList<Object>();

			int count = 0;
			populateBoxPrimarys(theName, stmt, theEman, boxPrimarys, count);

			BoxContainerClass boxContainerClass = new BoxContainerClass(theName.lexeme, boxPrimarys,
					TokenType.BOXCONTAINER, false,
					new TypesOfObject(type, RunTimeTypes.getTypeBasedOfToken(type), null));

			Token containerDefinitionName = new Token(theName.type, theName.lexeme + "_Box_Definition", null, null,
					null, theName.column, theName.line, theName.start, theName.finish);
			Token containerDefinitionEman = new Token(theEman.type, theEman.lexeme + "_Box_Definition", null, null,
					null, theName.column, theName.line, theName.start, theName.finish);
			environment.assign(containerDefinitionName, type, boxContainerClass);
			environment.assign(containerDefinitionEman, type, boxContainerClass);
			Object instance = boxContainerClass.call(this, null);

			environment.define(stmt.identifier.lexeme, type, instance);
			environment.define(stmt.reifitnedi.lexeme, type, instance);
			return instance;
		}
		return lookUpVariable(theName, stmt);
	}

	private Object evaluate(Stmt expr) {
		// TODO Auto-generated method stub
		return expr.accept(this);
	}

	@Override
	public Object visitFiStmt(Fi stmt) {
		// TODO Auto-generated method stubevaluate(stmt.expression);
		return null;
	}

	@Override
	public Object visitCallExpr(Call expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSwapExpr(Swap expr) {
		// TODO Auto-generated method stub
		return null;
	}

}
