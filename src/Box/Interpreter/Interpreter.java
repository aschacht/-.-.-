package Box.Interpreter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import Box.Box.Box;
import Box.Token.Token;
import Box.Token.TokenType;
import Parser.Declaration;
import Parser.Expr;
import Parser.Stmt;
import Parser.Declaration.FunDecl;
import Parser.Declaration.StmtDecl;
import Parser.Expr.Assignment;
import Parser.Expr.Assignmenttnemgissa;
import Parser.Expr.Binary;
import Parser.Expr.Binaryyranib;
import Parser.Expr.BoxClosed;
import Parser.Expr.BoxOpen;
import Parser.Expr.Call;
import Parser.Expr.Callllac;
import Parser.Expr.Contains;
import Parser.Expr.Cup;
import Parser.Expr.CupClosed;
import Parser.Expr.CupOpen;
import Parser.Expr.Expressiontmts;
import Parser.Expr.Factorial;
import Parser.Expr.Get;
import Parser.Expr.Gol;
import Parser.Expr.Knot;
import Parser.Expr.Lairotcaf;
import Parser.Expr.Link;
import Parser.Expr.Literal;
import Parser.Expr.LiteralChar;
import Parser.Expr.Llac;
import Parser.Expr.Log;
import Parser.Expr.Loggol;
import Parser.Expr.Mono;
import Parser.Expr.Monoonom;
import Parser.Expr.Onom;
import Parser.Expr.Pocket;
import Parser.Expr.PocketClosed;
import Parser.Expr.PocketOpen;
import Parser.Expr.Set;
import Parser.Expr.Sniatnoc;
import Parser.Expr.Swap;
import Parser.Expr.Teg;
import Parser.Expr.Template;
import Parser.Expr.Tes;
import Parser.Expr.Tnemngissa;
import Parser.Expr.Tonk;
import Parser.Expr.Unary;
import Parser.Expr.Variable;
import Parser.Expr.Yranib;
import Parser.Expr.Yranu;
import Parser.Fun.Function;
import Parser.Fun.FunctionLink;
import Parser.Stmt.Consume;
import Parser.Stmt.Daer;
import Parser.Stmt.Emaner;
import Parser.Stmt.Evas;
import Parser.Stmt.Evom;
import Parser.Stmt.Expel;
import Parser.Stmt.Fi;
import Parser.Stmt.If;
import Parser.Stmt.Ifi;
import Parser.Stmt.Move;
import Parser.Stmt.Moveevom;
import Parser.Stmt.Nruter;
import Parser.Stmt.Print;
import Parser.Stmt.Rav;
import Parser.Stmt.Read;
import Parser.Stmt.Readdaer;
import Parser.Stmt.Rename;
import Parser.Stmt.Renameemaner;
import Parser.Stmt.Save;
import Parser.Stmt.Saveevas;
import Parser.Stmt.StmttmtS;
import Parser.Stmt.TemplatVar;
import Parser.Stmt.Tnirp;
import Parser.Stmt.Var;

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
	private Map<Expr, Integer> locals = new WesMap<>();
	private boolean fromCall = false;
	private boolean forward;
	HashMap<String, String> nameMap = new HashMap<>();
	HashMap<String, String> classMapToSuperClass = new HashMap<>();

	public Interpreter() {

		globals.define("clock", null, new BoxCallable() {

			@Override
			public Object call(Interpreter interpreter) {

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

	public boolean isForward() {
		return forward;
	}

	public void interpret(List<Declaration> statements2) {
		try {

			for (int i = forward ? 0 : statements2.size() - 1; i >= 0 && i < statements2.size();) {
				execute(statements2.get(i));
				if (forward)
					i++;
				else
					i--;
			}

		} catch (RuntimeError e) {
			Box.runtimeError(e);
		}

	}

	Object execute(Stmt stmt) {
		return stmt.accept(this);
	}

	Object execute(Declaration stmt) {
		return stmt.accept(this);

	}

	Object evaluate(Expr expression) {
		return expression.accept(this);
	}

	public Object evaluate(Stmt stmt) {
		if (stmt instanceof Stmt.Expression) {
			return ((Stmt.Expression) stmt).expression.accept(this);
		}
		return null;

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
		if (forward) {
			Object value = expr.value;
			Integer distance = locals.get(expr);
			if (distance != null)
				environment.assignAt(distance, expr.name, expr.value, value, this);
			else
				globals.assign(expr.name, expr.value, value, this);
			return value;
		}
		return null;
	}

	@Override
	public Object visitVariableExpr(Variable expr) {

		return lookUpVariable(expr.name, expr);
	}

	Object lookUpVariable(Token name, Expr expr) {

		Integer distance = null;
		String tokenName = null;
		boolean inmap = false;
		if (nameMap.keySet().contains(name.lexeme)) {
			java.util.Set<Expr> keySet = locals.keySet();
			for (Expr expr2 : keySet) {
				tokenName = getTokenName(expr2);
				if (nameMap.get(name.lexeme).equals(tokenName)) {
					distance = locals.get(expr2);
					inmap = true;
					break;
				}
			}
		} else {
			distance = locals.get(expr);

		}

		if (distance != null) {
			if (inmap)
				return environment.getAt(distance, tokenName);
			else
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

	private String getTokenName(Expr evaluate) {
		if (evaluate instanceof Expr.Box) {
			return ((Expr.Box) evaluate).identifier.lexeme;
		} else if (evaluate instanceof Expr.Pocket) {
			return ((Expr.Pocket) evaluate).identifier.lexeme;
		} else if (evaluate instanceof Expr.Cup) {
			return ((Expr.Cup) evaluate).identifier.lexeme;
		} else if (evaluate instanceof Expr.Knot) {
			return ((Expr.Knot) evaluate).identifier.lexeme;
		} else if (evaluate instanceof Expr.Tonk) {
			return ((Tonk) evaluate).identifier.lexeme;
		} else if (evaluate instanceof Expr.Variable) {
			return ((Variable) evaluate).name.lexeme;
		}
		return null;
	}

	@Override
	public Object visitPrintStmt(Print stmt) {
		Object value = "";
		if (forward) {
			value = evaluate(stmt.expression);
			System.out.println(stringify(value));
		}
		return value;
	}

	@Override
	public Object visitBinaryExpr(Binary expr) {

		Object left = evaluate(expr.left);
		Object right = evaluate(expr.right);

		left = parse(left);
		right = parse(right);

		switch (expr.operator.type) {
		case NOTEQUALS:
			return !isEqual(left, right);
		case EQUALSNOT:
			return !isEqual(left, right);
		case EQUALSEQUALS:
			return isEqual(left, right);
		case MINUSEQUALS:
			return null;
		case PLUSEQUALS:
			return null;
		case GREATERTHEN:
			return null;
		case GREATERTHENEQUAL:
			return null;
		case LESSTHEN:
			return null;
		case LESSTHENEQUAL:
			return null;
		case MINUS:

			if (left instanceof Integer) {
				if (right instanceof Integer) {
					return addIntegerInteger(left, -1 * (Integer) right);
				} else if (right instanceof Double) {
					return addIntegerDouble(left, -1 * (Double) right);
				} else if (right instanceof Bin) {
					return addIntegerBin(left, Bin.times(new Bin(-1), (Bin) right));
				} else if (right instanceof ArrayList<?>) {
					addArrayList(left, right, -1);
					 return right;
				}
			} else if (left instanceof Double) {
				if (right instanceof Integer) {
					return addDoubleInteger(left, -1 * (Integer) right);
				} else if (right instanceof Double) {
					return addDoubleDouble(left, -1 * (Double) right);
				} else if (right instanceof Bin) {
					return addDoubleBin(left, Bin.times(new Bin(-1), (Bin) right));
				} else if (right instanceof ArrayList<?>) {
					addArrayList(left, right, -1);
					 return right;
				}
			} else if (left instanceof Bin) {
				if (right instanceof Integer) {
					return addBinInteger(left, -1 * (Integer) right);
				} else if (right instanceof Double) {
					return addBinDouble(left, -1 * (Double) right);
				} else if (right instanceof Bin) {
					return addBinBin(left, Bin.times(new Bin(-1), (Bin) right));
				} else if (right instanceof ArrayList<?>) {
					addArrayList(left, right, -1);
					 return right;
				}
			}

			return null;

		case PLUS:
			if (left instanceof Integer) {
				if (right instanceof Integer) {
					return addIntegerInteger(left, right);
				} else if (right instanceof Double) {
					return addIntegerDouble(left, right);
				} else if (right instanceof Bin) {
					return addIntegerBin(left, right);
				} else if (right instanceof String) {
					return addObjectString(left, right);
				} else if (right instanceof ArrayList<?>) {
					addArrayList(left, right, 1);
					 return right;
				}
			} else if (left instanceof Double) {
				if (right instanceof Integer) {
					return addDoubleInteger(left, right);
				} else if (right instanceof Double) {
					return addDoubleDouble(left, right);
				} else if (right instanceof Bin) {
					return addDoubleBin(left, right);
				} else if (right instanceof String) {
					return addObjectString(left, right);
				} else if (right instanceof ArrayList<?>) {
					addArrayList(left, right, 1);
					 return right;
				}
			} else if (left instanceof Bin) {
				if (right instanceof Integer) {
					return addBinInteger(left, right);
				} else if (right instanceof Double) {
					return addBinDouble(left, right);
				} else if (right instanceof Bin) {
					return addBinBin(left, right);
				} else if (right instanceof String) {
					return addObjectString(left, right);
				} else if (right instanceof ArrayList<?>) {
					 addArrayList(left, right, 1);
					 return right;
				}
			} else if (left instanceof String) {
				if (right instanceof Integer) {
					return addObjectString(left, right);
				} else if (right instanceof Double) {
					return addObjectString(left, right);
				} else if (right instanceof Bin) {
					return addObjectString(left, right);
				} else if (right instanceof String) {
					return addObjectString(left, right);
				} else if (right instanceof ArrayList<?>) {
					return addStringArray(left, right);
				}

			} else if (left instanceof ArrayList<?>) {
				if (right instanceof Integer) {
					return addArrayInteger(left, right);
				} else if (right instanceof Double) {
					return addArrayDouble(left, right);
				} else if (right instanceof Bin) {
					return addArrayBin(left, right);
				} else if (right instanceof String) {
					return addObjectString(left, right);
				}
			}
			return null;

		case FORWARDSLASH:
			return null;
		case BACKSLASH:
			return null;
		case TIMES:
			return null;

		case POWER:
			return null;
		case YROOT:
			return null;

		case DNA:
			return null;
		case AND:
			return null;
		case RO:
			return null;
		case OR:
			return null;
		default:
			return null;
		}

	}

	private void addArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		if (left instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					object = (Integer) left + (i * (Integer) object);
				} else if (object instanceof Double) {
					object = (Integer) left + (i * (Double) object);

				} else if (object instanceof Bin) {
					object = Bin.add(new Bin((Integer) left),
							new Bin(Bin.times(new Bin(i), new Bin((Integer) object))));

				}else if(object instanceof ArrayList<?>) {
					addArrayList(left, object, i);
				}
			}

		} else if (left instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					object = (Double) left + (i * (Integer) object);
				} else if (object instanceof Double) {
					object = (Double) left + (i * (Double) object);

				} else if (object instanceof Bin) {
					object = Bin.add(new Bin(((Double) left).intValue()),
							new Bin(Bin.times(new Bin(i), new Bin((Integer) object))));

				}else if(object instanceof ArrayList<?>) {
					addArrayList(left, object, i);
				}
			}

		} else if (left instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					object = Bin.add(((Bin) left), new Bin(Bin.times(new Bin(i), new Bin((Integer) object))));

				} else if (object instanceof Double) {
					object = Bin.add(((Bin) left),
							new Bin(Bin.times(new Bin(i), new Bin(((Double) object).intValue()))));

				} else if (object instanceof Bin) {
					object = Bin.add(((Bin) left), new Bin(Bin.times(new Bin(i), (Bin) object)));

				}else if(object instanceof ArrayList<?>) {
					addArrayList(left, object, i);
				}
			}

		}
		
	}

	private Object addStringArray(Object left, Object right) {
		ArrayList<?> arr = ((ArrayList<?>) right);
		String str = "";

		str += "[";
		for (Object object : arr) {
			if (object instanceof Integer) {
				str += ((Integer) object);
			} else if (object instanceof Double) {
				str += ((Double) object);
			} else if (object instanceof Bin) {
				str += ((Bin) object);
			} else if (object instanceof String) {
				str += ((String) object);
			} else if (object instanceof ArrayList<?>) {
				str += "[";
				str += printArray("", (ArrayList<?>) object);
				str += "]";
			}
		}
		str += "]";
		str = (String) left + str;
		return str;
	}

	private String printArray(String str, ArrayList<?> object) {

		for (int i = 0; i < object.size(); i++) {
			if (object.get(i) instanceof ArrayList<?>) {
				str += "[";
				str += printArray("", (ArrayList<?>) object.get(i));
				str += "]";
			} else {
				if (i == object.size() - 1)
					str += object.get(i).toString();
				else
					str += object.get(i).toString() + " ";
			}
		}

		return str;
	}

	private Object addIntegerInteger(Object left, Object right) {
		return ((Integer) left) + ((Integer) right);
	}

	private Object addArrayInteger(Object left, Object right) {
		ArrayList<?> arr = ((ArrayList<?>) left);
		for (Object object : arr) {
			if (object instanceof Integer) {
				object = ((Integer) object) + ((Integer) right);
			} else if (object instanceof Double) {
				object = ((Double) object) + ((Integer) right);
			} else if (object instanceof Bin) {
				object = Bin.add(((Bin) object), new Bin((Integer) right));
			} else if (object instanceof String) {
				object = ((String) object) + ((Integer) right);
			}
		}

		return arr;
	}

	private Object addArrayDouble(Object left, Object right) {
		ArrayList<?> arr = ((ArrayList<?>) left);
		for (Object object : arr) {
			if (object instanceof Integer) {
				object = ((Integer) object) + ((Double) right);
			} else if (object instanceof Double) {
				object = ((Double) object) + ((Double) right);
			} else if (object instanceof Bin) {
				object = Bin.add(((Bin) object), new Bin(((Double) right).intValue()));
			} else if (object instanceof String) {
				object = ((String) object) + ((Double) right);
			}
		}

		return arr;
	}

	private Object addArrayBin(Object left, Object right) {
		ArrayList<?> arr = ((ArrayList<?>) left);
		for (Object object : arr) {
			if (object instanceof Integer) {
				object = Bin.add(new Bin((Integer) object), ((Bin) right));
			} else if (object instanceof Double) {
				object = Bin.add(new Bin((Integer) object), ((Bin) right));
			} else if (object instanceof Bin) {
				object = Bin.add(((Bin) object), (Bin) right);
			} else if (object instanceof String) {
				object = ((String) object) + ((Bin) right);
			}
		}

		return arr;
	}

	private Object addDoubleInteger(Object left, Object right) {
		return ((Double) left) + ((Integer) right);
	}

	private Object addDoubleDouble(Object left, Object right) {
		return ((Double) left) + ((Double) right);
	}

	private Object addIntegerDouble(Object left, Object right) {
		return ((Integer) left) + ((Double) right);
	}

	private Object addDoubleBin(Object left, Object right) {
		return Bin.add(new Bin(((Double) left).intValue()), ((Bin) right));
	}

	private Object addIntegerBin(Object left, Object right) {
		return Bin.add(new Bin((Integer) left), ((Bin) right));
	}

	private Object addBinInteger(Object left, Object right) {
		return Bin.add((Bin) left, new Bin((Integer) right));
	}

	private Object addBinDouble(Object left, Object right) {
		return Bin.add((Bin) left, new Bin(((Double) right).intValue()));
	}

	private Object addBinBin(Object left, Object right) {
		return Bin.add((Bin) left, (Bin) right);
	}

	private Object addObjectString(Object left, Object right) {
		return left.toString() + right.toString();
	}

	private Object parse(Object left) {
		if (left instanceof Stmt.Expression) {
			Expr expression = ((Stmt.Expression) left).expression;
			if (expression instanceof Expr.Literal) {
				left = ((Expr.Literal) expression).value;
			}
		} else if (left instanceof PocketInstance) {
			Token identifier = new Token(TokenType.IDENTIFIER,
					((Pocket) ((PocketInstance) left).expr).identifier.lexeme + "varravargssgra", null, null, null,
					((Pocket) ((PocketInstance) left).expr).identifier.column,
					((Pocket) ((PocketInstance) left).expr).identifier.line,
					((Pocket) ((PocketInstance) left).expr).identifier.start,
					((Pocket) ((PocketInstance) left).expr).identifier.finish);

			Expr.Variable variable = new Expr.Variable(identifier);
			left = lookUpVariableByName(variable);
		} else if (left instanceof CupInstance) {
			Token identifier = new Token(TokenType.IDENTIFIER,
					((Cup) ((CupInstance) left).expr).identifier.lexeme + "varravargssgra", null, null, null,
					((Cup) ((CupInstance) left).expr).identifier.column,
					((Cup) ((CupInstance) left).expr).identifier.line,
					((Cup) ((CupInstance) left).expr).identifier.start,
					((Cup) ((CupInstance) left).expr).identifier.finish);

			Expr.Variable variable = new Expr.Variable(identifier);
			left = lookUpVariableByName(variable);
		}
		return left;
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

	@Override
	public Object visitLiteralExpr(Literal expr) {

		return expr.value;
	}

	@Override
	public Object visitUnaryExpr(Unary expr) {

		Object right = evaluate(expr.right);

		right = parse(right);
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
			if (right instanceof Instance) {
				Environment previous = environment;
				try {
					environment = new Environment(environment);
					int valueToAssign = -1;
					for (int i = 0; i < ((Instance) right).body.size(); i++) {
						Object at = ((Instance) right).body.get(i);
						multiplyByValueAndAssign(at, valueToAssign);
					}
				} finally {
					this.environment = previous;
				}
				return right;

			}
			throw new RuntimeError(expr.operator, "Operand must be a number.");

		case PLUSPLUS:
			if (forward) {
				if (right instanceof Double) {

					double value = (double) right + 1.0;
					assignValue(expr, value);

					return value;
				}
				if (right instanceof Integer) {

					int value = (int) right + 1;
					assignValue(expr, value);

					return value;
				}
				if (right instanceof Bin) {

					Bin value = Bin.add((Bin) right, new Bin("1"));
					assignValue(expr, value);

					return value;
				}
				int valueToAssign = 1;

				Object pocketInstance = isPocketInstance(right, valueToAssign);
				Object cupInstance = isCupInstance(right, valueToAssign);
				Object boxInstance = isBoxInstance(right, valueToAssign);

				if (pocketInstance != null)
					return pocketInstance;
				else if (cupInstance != null)
					return cupInstance;
				else if (boxInstance != null)
					return boxInstance;

				throw new RuntimeError(expr.operator, "Operand must be a number.");
			}
			return right;
		case MINUSMINUS:
			if (forward) {
				if (right instanceof Double) {

					double value = (double) right - 1.0;
					assignValue(expr, value);

					return value;
				}
				if (right instanceof Integer) {

					int value = (int) right - 1;
					assignValue(expr, value);

					return value;
				}
				if (right instanceof Bin) {

					Bin value = Bin.subtract((Bin) right, new Bin("1"));
					assignValue(expr, value);

					return value;
				}

				int valueToAssign = -1;

				Object pocketInstance = isPocketInstance(right, valueToAssign);
				Object cupInstance = isCupInstance(right, valueToAssign);
				Object boxInstance = isBoxInstance(right, valueToAssign);

				if (pocketInstance != null)
					return pocketInstance;
				else if (cupInstance != null)
					return cupInstance;
				else if (boxInstance != null)
					return boxInstance;

				throw new RuntimeError(expr.operator, "Operand must be a number.");
			}
			return right;
		default:
			return null;
		}

	}

	private Object isBoxInstance(Object right, int valueToAssign) {
		if (right instanceof CupInstance) {
			Environment previous = environment;
			ArrayList<Object> arr = new ArrayList<>();
			try {
				environment = new Environment(environment);
				Object evaluate = null;
				for (int i = 0; i < ((BoxInstance) right).body.size(); i++) {
					Object at = ((BoxInstance) right).body.get(i);
					addOrSubtractAndAssign(at, valueToAssign);
					if (at instanceof Stmt.Expression) {
						evaluate = evaluate(((Stmt.Expression) at));
						if (evaluate != null)
							if (evaluate instanceof BoxInstance) {
								Token identifier = new Token(TokenType.IDENTIFIER,
										((Expr.Box) ((Stmt.Expression) at).expression).identifier.lexeme
												+ "varravargssgra",
										null, null, null,
										((Expr.Box) ((Stmt.Expression) at).expression).identifier.column,
										((Expr.Box) ((Stmt.Expression) at).expression).identifier.line,
										((Expr.Box) ((Stmt.Expression) at).expression).identifier.start,
										((Expr.Box) ((Stmt.Expression) at).expression).identifier.finish);

								Expr.Variable variable = new Expr.Variable(identifier);
								Object lookUpVariableByName = lookUpVariableByName(variable);
								arr.add(lookUpVariableByName);
							} else {
								arr.add(evaluate);

							}
					}
				}
			} finally {
				this.environment = previous;
			}

			Expr.Box expr2 = (Expr.Box) ((BoxInstance) right).expr;
			Token identifier = new Token(TokenType.IDENTIFIER, expr2.identifier.lexeme + "varravargssgra", null, null,
					null, expr2.identifier.column, expr2.identifier.line, expr2.identifier.start,
					expr2.identifier.finish);

			Expr.Variable variable = new Expr.Variable(identifier);
			Integer distance = locals.get(variable);
			if (distance != null) {
				environment.assignAt(distance, identifier, arr, arr, null);
			} else {
				globals.assignAt(distance, identifier, arr, arr, null);
			}

			return arr;
		} else {
			return null;
		}
	}

	private Object isCupInstance(Object right, int valueToAssign) {
		if (right instanceof CupInstance) {
			Environment previous = environment;
			ArrayList<Object> arr = new ArrayList<>();
			try {
				environment = new Environment(environment);
				Object evaluate = null;
				for (int i = 0; i < ((CupInstance) right).body.size(); i++) {
					Object at = ((CupInstance) right).body.get(i);
					addOrSubtractAndAssign(at, valueToAssign);
					if (at instanceof Stmt.Expression) {
						evaluate = evaluate(((Stmt.Expression) at));
						if (evaluate != null)
							if (evaluate instanceof CupInstance) {
								Token identifier = new Token(TokenType.IDENTIFIER,
										((Expr.Cup) ((Stmt.Expression) at).expression).identifier.lexeme
												+ "varravargssgra",
										null, null, null,
										((Expr.Cup) ((Stmt.Expression) at).expression).identifier.column,
										((Expr.Cup) ((Stmt.Expression) at).expression).identifier.line,
										((Expr.Cup) ((Stmt.Expression) at).expression).identifier.start,
										((Expr.Cup) ((Stmt.Expression) at).expression).identifier.finish);

								Expr.Variable variable = new Expr.Variable(identifier);
								Object lookUpVariableByName = lookUpVariableByName(variable);
								arr.add(lookUpVariableByName);
							} else {
								arr.add(evaluate);

							}
					}
				}
			} finally {
				this.environment = previous;
			}

			Expr.Cup expr2 = (Cup) ((CupInstance) right).expr;
			Token identifier = new Token(TokenType.IDENTIFIER, expr2.identifier.lexeme + "varravargssgra", null, null,
					null, expr2.identifier.column, expr2.identifier.line, expr2.identifier.start,
					expr2.identifier.finish);

			Expr.Variable variable = new Expr.Variable(identifier);
			Integer distance = locals.get(variable);
			if (distance != null) {
				environment.assignAt(distance, identifier, arr, arr, null);
			} else {
				globals.assignAt(distance, identifier, arr, arr, null);
			}

			return arr;
		} else {
			return null;
		}
	}

	private Object isPocketInstance(Object right, int valueToAssign) {
		if (right instanceof PocketInstance) {
			Environment previous = environment;
			ArrayList<Object> arr = new ArrayList<>();
			try {
				environment = new Environment(environment);
				Object evaluate = null;
				for (int i = 0; i < ((PocketInstance) right).body.size(); i++) {
					Object at = ((PocketInstance) right).body.get(i);
					addOrSubtractAndAssign(at, valueToAssign);
					if (at instanceof Stmt.Expression) {
						evaluate = evaluate(((Stmt.Expression) at));
						if (evaluate != null)
							if (evaluate instanceof PocketInstance) {
								Token identifier = new Token(TokenType.IDENTIFIER,
										((Expr.Pocket) ((Stmt.Expression) at).expression).identifier.lexeme
												+ "varravargssgra",
										null, null, null,
										((Expr.Pocket) ((Stmt.Expression) at).expression).identifier.column,
										((Expr.Pocket) ((Stmt.Expression) at).expression).identifier.line,
										((Expr.Pocket) ((Stmt.Expression) at).expression).identifier.start,
										((Expr.Pocket) ((Stmt.Expression) at).expression).identifier.finish);

								Expr.Variable variable = new Expr.Variable(identifier);
								Object lookUpVariableByName = lookUpVariableByName(variable);
								arr.add(lookUpVariableByName);
							} else {
								arr.add(evaluate);

							}
					}
				}
			} finally {
				this.environment = previous;
			}

			Expr.Pocket expr2 = (Pocket) ((PocketInstance) right).expr;
			Token identifier = new Token(TokenType.IDENTIFIER, expr2.identifier.lexeme + "varravargssgra", null, null,
					null, expr2.identifier.column, expr2.identifier.line, expr2.identifier.start,
					expr2.identifier.finish);

			Expr.Variable variable = new Expr.Variable(identifier);
			Integer distance = locals.get(variable);
			if (distance != null) {
				environment.assignAt(distance, identifier, arr, arr, null);
			} else {
				globals.assignAt(distance, identifier, arr, arr, null);
			}

			return arr;
		} else {
			return null;
		}
	}

	private Object evaluate(StmtDecl stmtDecl) {
		return stmtDecl.statement.accept(this);

	}

	private void addOrSubtractAndAssign(Object at, int valueToAssign) {
		if (at instanceof StmtDecl) {
			assignExpression(((StmtDecl) at).statement, valueToAssign);
		} else if (at instanceof Stmt.Expression) {
			assignExpression(at, valueToAssign);

		}
	}

	private void multiplyByValueAndAssign(Object at, int valueToAssign) {
		if (at instanceof StmtDecl) {
			multByValueAndAssignExpression(((StmtDecl) at).statement, valueToAssign);
		} else if (at instanceof Stmt.Expression) {
			multByValueAndAssignExpression(at, valueToAssign);

		}
	}

	private void multByValueAndAssignExpression(Object at, int valueToAssign) {

		if (at instanceof Stmt.Expression) {
			Expr expression = ((Stmt.Expression) at).expression;
			if (expression instanceof Expr.Unary) {
				Expr right2 = ((Expr.Unary) expression).right;
				if (right2 instanceof Expr.Variable) {
					Variable variable = (Expr.Variable) right2;
					assignVarableMult(variable, valueToAssign);
				} else if (right2 instanceof Expr.Cup) {
					assignCupMultiply(valueToAssign, right2);
				} else if (right2 instanceof Expr.Pocket) {
					assignPocketMultiply(valueToAssign, right2);
				} else if (expression instanceof Expr.Binary) {
					assignBinaryMultiply(valueToAssign, expression);
				}
			}
		}
	}

	private void assignExpression(Object at, int valueToAssign) {

		if (at instanceof Stmt.Expression) {
			Expr expression = ((Stmt.Expression) at).expression;
			if (expression instanceof Expr.Unary) {
				Expr right2 = ((Expr.Unary) expression).right;
				if (right2 instanceof Expr.Variable) {
					Variable variable = (Expr.Variable) right2;
					assignVarableSubtract(variable, valueToAssign);
				} else if (right2 instanceof Expr.Cup) {
					assignCupAdd(valueToAssign, right2);
				} else if (right2 instanceof Expr.Pocket) {
					assignPocketAdd(valueToAssign, right2);
				} else if (expression instanceof Expr.Binary) {
					assignBinaryAdd(valueToAssign, expression);
				}
			} else if (expression instanceof Expr.Binary) {
				assignBinaryAdd(valueToAssign, expression);
			}
		}
	}

	private void assignBinaryAdd(int valueToAssign, Expr expression) {
		Expr right2 = ((Expr.Binary) expression).right;
		Expr left2 = ((Expr.Binary) expression).left;
		if (right2 instanceof Expr.Variable && left2 instanceof Expr.Variable) {
			Variable variable1 = (Expr.Variable) right2;
			assignVarableSubtract(variable1, valueToAssign);
			Variable variable = (Expr.Variable) left2;
			assignVarableSubtract(variable, valueToAssign);
		} else if (right2 instanceof Expr.Cup && left2 instanceof Expr.Variable) {
			assignCupAdd(valueToAssign, right2);
			Variable variable = (Expr.Variable) left2;
			assignVarableSubtract(variable, valueToAssign);
		} else if (right2 instanceof Expr.Variable && left2 instanceof Expr.Cup) {
			assignCupAdd(valueToAssign, left2);
			Variable variable = (Expr.Variable) right2;
			assignVarableSubtract(variable, valueToAssign);
		} else if (right2 instanceof Expr.Cup && left2 instanceof Expr.Cup) {
			assignCupAdd(valueToAssign, right2);
			assignCupAdd(valueToAssign, left2);
		} else if (right2 instanceof Expr.Pocket && left2 instanceof Expr.Variable) {
			assignPocketAdd(valueToAssign, right2);
			Variable variable = (Expr.Variable) left2;
			assignVarableSubtract(variable, valueToAssign);
		} else if (right2 instanceof Expr.Variable && left2 instanceof Expr.Pocket) {
			assignPocketAdd(valueToAssign, left2);
			Variable variable = (Expr.Variable) right2;
			assignVarableSubtract(variable, valueToAssign);
		} else if (right2 instanceof Expr.Pocket && left2 instanceof Expr.Pocket) {
			assignPocketAdd(valueToAssign, right2);
			assignPocketAdd(valueToAssign, left2);
		} else if (right2 instanceof Expr.Pocket && left2 instanceof Expr.Cup) {
			assignPocketAdd(valueToAssign, right2);
			assignCupAdd(valueToAssign, left2);
		} else if (right2 instanceof Expr.Cup && left2 instanceof Expr.Pocket) {
			assignCupAdd(valueToAssign, right2);
			assignPocketAdd(valueToAssign, left2);
		}

	}

	private void assignBinaryMultiply(int valueToAssign, Expr expression) {
		Expr right2 = ((Expr.Binary) expression).right;
		Expr left2 = ((Expr.Binary) expression).left;
		if (right2 instanceof Expr.Variable && left2 instanceof Expr.Variable) {
			Variable variable1 = (Expr.Variable) right2;
			assignVarableMult(variable1, valueToAssign);
			Variable variable = (Expr.Variable) left2;
			assignVarableMult(variable, valueToAssign);
		} else if (right2 instanceof Expr.Cup && left2 instanceof Expr.Variable) {
			assignCupMultiply(valueToAssign, right2);
			Variable variable = (Expr.Variable) left2;
			assignVarableMult(variable, valueToAssign);
		} else if (right2 instanceof Expr.Variable && left2 instanceof Expr.Cup) {
			assignCupMultiply(valueToAssign, left2);
			Variable variable = (Expr.Variable) right2;
			assignVarableMult(variable, valueToAssign);
		} else if (right2 instanceof Expr.Cup && left2 instanceof Expr.Cup) {
			assignCupMultiply(valueToAssign, right2);
			assignCupMultiply(valueToAssign, left2);
		} else if (right2 instanceof Expr.Pocket && left2 instanceof Expr.Variable) {
			assignPocketMultiply(valueToAssign, right2);
			Variable variable = (Expr.Variable) left2;
			assignVarableMult(variable, valueToAssign);
		} else if (right2 instanceof Expr.Variable && left2 instanceof Expr.Pocket) {
			assignPocketMultiply(valueToAssign, left2);
			Variable variable = (Expr.Variable) right2;
			assignVarableSubtract(variable, valueToAssign);
		} else if (right2 instanceof Expr.Pocket && left2 instanceof Expr.Pocket) {
			assignPocketMultiply(valueToAssign, right2);
			assignPocketMultiply(valueToAssign, left2);
		} else if (right2 instanceof Expr.Pocket && left2 instanceof Expr.Cup) {
			assignPocketMultiply(valueToAssign, right2);
			assignCupMultiply(valueToAssign, left2);
		} else if (right2 instanceof Expr.Cup && left2 instanceof Expr.Pocket) {
			assignCupMultiply(valueToAssign, right2);
			assignPocketMultiply(valueToAssign, left2);
		}

	}

	private void assignCupMultiply(int valueToAssign, Expr right2) {
		Environment previous = environment;
		try {
			environment = new Environment(environment);
			for (Declaration declaration : ((Cup) right2).expression) {
				multiplyByValueAndAssign(declaration, valueToAssign);
			}
		} finally {
			this.environment = previous;
		}
	}

	private void assignCupAdd(int valueToAssign, Expr right2) {
		Environment previous = environment;
		try {
			environment = new Environment(environment);
			for (Declaration declaration : ((Cup) right2).expression) {
				addOrSubtractAndAssign(declaration, valueToAssign);
			}
		} finally {
			this.environment = previous;
		}
	}

	private void assignPocketMultiply(int valueToAssign, Expr right2) {
		Environment previous = environment;
		try {
			environment = new Environment(environment);
			for (Declaration declaration : ((Pocket) right2).expression) {
				multiplyByValueAndAssign(declaration, valueToAssign);
			}
		} finally {
			this.environment = previous;
		}
	}

	private void assignPocketAdd(int valueToAssign, Expr right2) {
		Environment previous = environment;
		try {
			environment = new Environment(environment);
			for (Declaration declaration : ((Pocket) right2).expression) {
				addOrSubtractAndAssign(declaration, valueToAssign);
			}
		} finally {
			this.environment = previous;
		}
	}

	private void assignVarableMult(Variable variable, int valueToAssign) {
		Object lookUpVariableByName = lookUpVariable(variable.name, variable);
		if (lookUpVariableByName instanceof Stmt.Expression) {
			Expr expression2 = ((Stmt.Expression) lookUpVariableByName).expression;
			if (expression2 instanceof Expr.Literal) {

				if (((Literal) expression2).value instanceof Integer) {
					((Literal) expression2).value = ((Integer) ((Literal) expression2).value) * valueToAssign;
				} else if (((Literal) expression2).value instanceof Double) {
					((Literal) expression2).value = ((Double) ((Literal) expression2).value) * valueToAssign;
				} else if (((Literal) expression2).value instanceof Bin) {
					((Literal) expression2).value = Bin.times(((Bin) ((Literal) expression2).value),
							new Bin(valueToAssign));
				}
				Integer distance = locals.get(variable);
				if (distance != null)
					environment.assignAt(distance, variable.name, lookUpVariableByName, lookUpVariableByName, this);
				else
					globals.assign(variable.name, lookUpVariableByName, lookUpVariableByName, this);
			}
		} else if (lookUpVariableByName instanceof Integer) {
			Integer distance = locals.get(variable);
			if (distance != null)
				environment.assignAt(distance, variable.name, ((Integer) lookUpVariableByName) * valueToAssign,
						((Integer) lookUpVariableByName) * valueToAssign, this);
			else
				globals.assign(variable.name, ((Integer) lookUpVariableByName) * valueToAssign,
						((Integer) lookUpVariableByName) * valueToAssign, this);
		} else if (lookUpVariableByName instanceof Double) {
			Integer distance = locals.get(variable);
			if (distance != null)
				environment.assignAt(distance, variable.name, ((Double) lookUpVariableByName) * valueToAssign,
						((Double) lookUpVariableByName) * valueToAssign, this);
			else
				globals.assign(variable.name, ((Double) lookUpVariableByName) * valueToAssign,
						((Double) lookUpVariableByName) * valueToAssign, this);
		} else if (lookUpVariableByName instanceof Bin) {
			Integer distance = locals.get(variable);
			if (distance != null)
				environment.assignAt(distance, variable.name,
						Bin.times(((Bin) lookUpVariableByName), new Bin(valueToAssign)),
						Bin.times(((Bin) lookUpVariableByName), new Bin(valueToAssign)), this);
			else
				globals.assign(variable.name, Bin.times(((Bin) lookUpVariableByName), new Bin(valueToAssign)),
						Bin.times(((Bin) lookUpVariableByName), new Bin(valueToAssign)), this);
		}
	}

	private void assignVarableSubtract(Variable variable, int valueToAssign) {
		Object lookUpVariableByName = lookUpVariable(variable.name, variable);
		if (lookUpVariableByName instanceof Stmt.Expression) {
			Expr expression2 = ((Stmt.Expression) lookUpVariableByName).expression;
			if (expression2 instanceof Expr.Literal) {

				if (((Literal) expression2).value instanceof Integer) {
					((Literal) expression2).value = ((Integer) ((Literal) expression2).value) + valueToAssign;
				} else if (((Literal) expression2).value instanceof Double) {
					((Literal) expression2).value = ((Double) ((Literal) expression2).value) + valueToAssign;
				} else if (((Literal) expression2).value instanceof Bin) {
					((Literal) expression2).value = Bin.add(((Bin) ((Literal) expression2).value),
							new Bin(valueToAssign));
				}
				Integer distance = locals.get(variable);
				if (distance != null)
					environment.assignAt(distance, variable.name, lookUpVariableByName, lookUpVariableByName, this);
				else
					globals.assign(variable.name, lookUpVariableByName, lookUpVariableByName, this);
			}
		} else if (lookUpVariableByName instanceof Integer) {
			Integer distance = locals.get(variable);
			if (distance != null)
				environment.assignAt(distance, variable.name, ((Integer) lookUpVariableByName) + valueToAssign,
						((Integer) lookUpVariableByName) + valueToAssign, this);
			else
				globals.assign(variable.name, ((Integer) lookUpVariableByName) + valueToAssign,
						((Integer) lookUpVariableByName) + valueToAssign, this);
		} else if (lookUpVariableByName instanceof Double) {
			Integer distance = locals.get(variable);
			if (distance != null)
				environment.assignAt(distance, variable.name, ((Double) lookUpVariableByName) + valueToAssign,
						((Double) lookUpVariableByName) + valueToAssign, this);
			else
				globals.assign(variable.name, ((Double) lookUpVariableByName) + valueToAssign,
						((Double) lookUpVariableByName) + valueToAssign, this);
		} else if (lookUpVariableByName instanceof Bin) {
			Integer distance = locals.get(variable);
			if (distance != null)
				environment.assignAt(distance, variable.name,
						Bin.add(((Bin) lookUpVariableByName), new Bin(+valueToAssign)),
						Bin.add(((Bin) lookUpVariableByName), new Bin(+valueToAssign)), this);
			else
				globals.assign(variable.name, Bin.add(((Bin) lookUpVariableByName), new Bin(+valueToAssign)),
						Bin.add(((Bin) lookUpVariableByName), new Bin(+valueToAssign)), this);
		}
	}

	private Object updateBoxClass(Object right, int toupdateby) {
		BoxClass boxClass = (BoxClass) right;

		Object call = boxClass.instance(this);

		if (call instanceof CupInstance) {
			CupInstance cupInstance = (CupInstance) call;
			for (Object object : cupInstance.body) {
				findVariablePocketCupKnotTonkandAdd(object, toupdateby);
			}
		}

		return call;
	}

	private void findVariablePocketCupKnotTonkandAdd(Object object, int toupdateby) {
		Object target = object;
		Variable name = null;
		while (true) {
			if (target instanceof StmtDecl) {
				target = ((StmtDecl) target).statement;
			} else if (target instanceof Stmt.Expression) {
				target = ((Stmt.Expression) target).expression;
			} else if (target instanceof Expr.Unary) {
				target = ((Expr.Unary) target).right;
			} else if (target instanceof Expr.Yranu) {
				target = ((Expr.Yranu) target).right;
			} else if (target instanceof Expr.Variable) {
				name = ((Expr.Variable) target);
				break;
			} else if (target instanceof Expr.Pocket) {
				name = new Variable(((Expr.Pocket) target).identifier);
				break;
			} else if (target instanceof Expr.Cup) {
				name = new Variable(((Expr.Cup) target).identifier);
				break;
			} else if (target instanceof Expr.Knot) {
				name = new Variable(((Expr.Knot) target).identifier);
				break;
			} else if (target instanceof Expr.Tonk) {
				name = new Variable(((Expr.Tonk) target).identifier);
				break;
			}

			else if (target instanceof Expr.CupOpen) {
				break;
			} else if (target instanceof Expr.CupClosed) {
				break;
			} else if (target instanceof Expr.PocketClosed) {
				break;
			} else if (target instanceof Expr.PocketOpen) {
				break;
			} else if (target instanceof Expr.BoxOpen) {
				break;
			} else if (target instanceof Expr.BoxClosed) {
				break;
			} else {
				break;
			}

		}
		if (name != null) {
			Object lookUpVariableByName = lookUpVariable(name.name, name);
			if (lookUpVariableByName instanceof BoxClass) {
				updateBoxClass(lookUpVariableByName, toupdateby);
			} else if (lookUpVariableByName instanceof Stmt.Expression) {
				Expr expression = ((Stmt.Expression) lookUpVariableByName).expression;
				if (expression instanceof Expr.Literal) {

					if (((Literal) expression).value instanceof Integer) {
						((Literal) expression).value = ((Integer) ((Literal) expression).value) + toupdateby;
					} else if (((Literal) expression).value instanceof Double) {
						((Literal) expression).value = ((Double) ((Literal) expression).value) + toupdateby;
					} else if (((Literal) expression).value instanceof Bin) {
						((Literal) expression).value = Bin.add(((Bin) ((Literal) expression).value),
								new Bin(toupdateby));
					}
					Integer distance = locals.get(target);
					if (distance != null)
						environment.assignAt(distance, name.name, lookUpVariableByName, lookUpVariableByName, this);
					else
						globals.assign(name.name, lookUpVariableByName, lookUpVariableByName, this);
				}
			} else if (lookUpVariableByName instanceof Integer) {
				Integer distance = locals.get(target);
				if (distance != null)
					environment.assignAt(distance, name.name, lookUpVariableByName, lookUpVariableByName, this);
				else
					globals.assign(name.name, lookUpVariableByName, lookUpVariableByName, this);
			} else if (lookUpVariableByName instanceof Double) {
				Integer distance = locals.get(target);
				if (distance != null)
					environment.assignAt(distance, name.name, lookUpVariableByName, lookUpVariableByName, this);
				else
					globals.assign(name.name, lookUpVariableByName, lookUpVariableByName, this);
			} else if (lookUpVariableByName instanceof Bin) {
				Integer distance = locals.get(target);
				if (distance != null)
					environment.assignAt(distance, name.name, lookUpVariableByName, lookUpVariableByName, this);
				else
					globals.assign(name.name, lookUpVariableByName, lookUpVariableByName, this);
			}

		}

	}

	private ArrayList<Object> addBoxInstance(Object theRight) {
		ArrayList<Object> returnedObject = new ArrayList<Object>();

		int rightSize = 1;
		if (theRight instanceof Instance) {
			rightSize = ((Instance) theRight).size();
		}

		for (int rightindex = 0; rightindex < rightSize; rightindex++) {

			Object rightatindex = theRight;
			if (theRight instanceof Instance) {
				rightatindex = ((Instance) theRight).getAt(rightindex);
				if (rightatindex instanceof Stmt.Expression) {
					Expr expression = ((Stmt.Expression) rightatindex).expression;
					rightatindex = evaluate(expression);
				}
			}
			Object rightvalue = rightatindex;
			if (rightatindex instanceof Expr.Literal) {
				rightvalue = ((Expr.Literal) rightatindex).value;
			}

			Object findRootForLeftAndRightAndAdd = findRootForRightAndAdd(rightvalue);
			returnedObject.add(findRootForLeftAndRightAndAdd);
		}

		return returnedObject;
	}

	private ArrayList<Object> subBoxInstance(Object theRight) {
		ArrayList<Object> returnedObject = new ArrayList<Object>();

		int rightSize = 1;
		if (theRight instanceof Instance) {
			rightSize = ((Instance) theRight).size();
		}

		for (int rightindex = 0; rightindex < rightSize; rightindex++) {

			Object rightatindex = theRight;
			if (theRight instanceof Instance) {
				rightatindex = ((Instance) theRight).getAt(rightindex);
				if (rightatindex instanceof Stmt.Expression) {
					Expr expression = ((Stmt.Expression) rightatindex).expression;
					rightatindex = evaluate(expression);
				}
			}
			Object rightvalue = rightatindex;
			if (rightatindex instanceof Expr.Literal) {
				rightvalue = ((Expr.Literal) rightatindex).value;
			}

			Object findRootForLeftAndRightAndAdd = findRootForRightAndSub(rightvalue);
			returnedObject.add(findRootForLeftAndRightAndAdd);
		}

		return returnedObject;
	}

	private Object findRootForRightAndAdd(Object right) {

		right = parse(right);

		Object theRight = right;
		if (theRight instanceof Double) {
			return (double) theRight + 1;
		} else if (theRight instanceof Integer) {
			return (int) theRight + 1;
		} else if (theRight instanceof Bin) {
			return ((Bin) theRight).toInteger() + 1;
		} else if (theRight instanceof Instance) {
			return addBoxInstance(theRight);
		}

		throw new RuntimeError(null, "Operands must be numbers.");

	}

	private Object findRootForRightAndSub(Object right) {

		right = parse(right);

		Object theRight = right;
		if (theRight instanceof Double) {
			return (double) theRight - 1;
		} else if (theRight instanceof Integer) {
			return (int) theRight - 1;
		} else if (theRight instanceof Bin) {
			return ((Bin) theRight).toInteger() - 1;
		} else if (theRight instanceof Instance) {
			return subBoxInstance(theRight);
		}

		throw new RuntimeError(null, "Operands must be numbers.");

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

	@Override
	public Object visitYranuExpr(Yranu expr) {

		Object left = evaluate(expr.right);
		left = parse(left);
		switch (expr.operator.type) {
		case QMARK:
			return !isTruthy(left);
		case MINUS:
			if (left instanceof Double)
				return -(double) left;
			if (left instanceof Integer)
				return -(int) left;

			if (left instanceof Bin)
				return ((Bin) left).negate();
			if (left instanceof ArrayList) {

				return findRootForMinus(left);
			}

			throw new RuntimeError(expr.operator, "Operand must be a number.");

		case PLUSPLUS:
			if (!forward) {
				if (left instanceof Double) {

					double value = (double) left + 1.0;
					assignValue(expr, value);

					return value;
				}
				if (left instanceof Integer) {

					int value = (int) left + 1;
					assignValue(expr, value);

					return value;
				}
				if (left instanceof Bin) {

					Bin value = Bin.add((Bin) left, new Bin("1"));
					assignValue(expr, value);

					return value;
				}
				int valueToAssign = 1;

				Object pocketInstance = isPocketInstance(left, valueToAssign);
				Object cupInstance = isCupInstance(left, valueToAssign);
				Object boxInstance = isBoxInstance(left, valueToAssign);

				if (pocketInstance != null)
					return pocketInstance;
				else if (cupInstance != null)
					return cupInstance;
				else if (boxInstance != null)
					return boxInstance;
				throw new RuntimeError(expr.operator, "Operand must be a number.");
			}
			return left;
		case MINUSMINUS:
			if (!forward) {
				if (left instanceof Double) {

					double value = (double) left - 1.0;
					assignValue(expr, value);

					return value;
				}
				if (left instanceof Integer) {

					int value = (int) left - 1;
					assignValue(expr, value);

					return value;
				}
				if (left instanceof Bin) {

					Bin value = Bin.subtract((Bin) left, new Bin("1"));
					assignValue(expr, value);

					return value;
				}
				int valueToAssign = -1;

				Object pocketInstance = isPocketInstance(left, valueToAssign);
				Object cupInstance = isCupInstance(left, valueToAssign);
				Object boxInstance = isBoxInstance(left, valueToAssign);

				if (pocketInstance != null)
					return pocketInstance;
				else if (cupInstance != null)
					return cupInstance;
				else if (boxInstance != null)
					return boxInstance;
				throw new RuntimeError(expr.operator, "Operand must be a number.");
			}
			return left;
		default:
			return null;
		}

	}

	private void assignValue(Expr expr, Object value) {

		Expr expr2 = expr;
		while ((expr2 instanceof Expr.Yranu) || (expr2 instanceof Expr.Unary)) {

			if (expr2 instanceof Expr.Yranu) {
				expr2 = ((Expr.Yranu) expr2).right;
			}

			if (expr2 instanceof Expr.Unary) {
				expr2 = ((Expr.Unary) expr2).right;
			}

		}
		if (expr2 instanceof Expr.Variable) {
			Integer distance = locals.get(expr2);
			if (distance != null)
				environment.assignAt(distance, ((Expr.Variable) expr2).name, value, value, this);
			else
				globals.assign(((Expr.Variable) expr2).name, value, value, this);
		}

	}

	@Override
	public Void visitReturnStmt(Stmt.Return stmt) {
		if (forward) {
			Object value = null;
			if (stmt.expression != null)
				value = evaluate(stmt.expression);
			throw new Returns(value);
		}
		return null;
	}

	@Override
	public Object visitContainsExpr(Contains expr) {
		if (forward) {
			if (expr.container instanceof Expr.Variable) {
				Object lookUpVariable = lookUpVariable(((Expr.Variable) expr.container).name, expr.container);
				Instance lookUpContainer = (Instance) lookUpVariable;
				System.out.println("hey");
				if (expr.contents instanceof Expr.Variable) {
					Instance lookUpContents = (Instance) lookUpVariable(((Expr.Variable) expr.contents).name,
							expr.contents);
					System.out.println("hi");
					boolean contains = lookUpContainer.contains(lookUpContents);
					if (contains) {
						System.out.println("hey");
					}
				} else if (expr.contents instanceof Expr.Literal) {

					boolean contains = lookUpContainer.contains((Expr.Literal) expr.contents);
					if (contains) {
						System.out.println("hey");
					}
				} else if (expr.contents instanceof Expr.LiteralChar) {

					boolean contains = lookUpContainer.contains((Expr.LiteralChar) expr.contents);
					if (contains) {
						System.out.println("hey");
					}
				} else if (expr.contents instanceof Expr.Box) {

				} else if (expr.contents instanceof Expr.Pocket) {

					boolean contains = lookUpContainer.contains((Expr.Pocket) expr.contents);
					if (contains) {
						System.out.println("hey");
					}
				} else if (expr.contents instanceof Expr.Cup) {

					boolean contains = lookUpContainer.contains((Expr.Cup) expr.contents);
					if (contains) {
						System.out.println("hey");
					}
				} else if (expr.contents instanceof Expr.Knot) {

					boolean contains = lookUpContainer.contains((Expr.Knot) expr.contents);
					if (contains) {
						System.out.println("hey");
					}
				} else if (expr.contents instanceof Expr.Tonk) {

					boolean contains = lookUpContainer.contains((Expr.Tonk) expr.contents);
					if (contains) {
						System.out.println("hey");
					}
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
		if (forward) {

			return findMono(expr);
		}
		return null;
	}

	private Double convertStringToDouble(String evaluate) {
		String regex = "^\\d*\\.?\\d+$";

		if (evaluate == null || !evaluate.matches(regex)) {
			throw new IllegalArgumentException(
					"Invalid input: The string must contain only numerical values and at most one decimal point.");
		}

		return Double.parseDouble(evaluate);
	}

	@Override
	public Object visitLogExpr(Log expr) {
		if (forward) {
			Double evaluateDoubleValue = findDoubleValue(expr.value) - 1;
			Double evaluateDoubleValueBase = findDoubleValue(expr.valueBase) - 1;

			return Math.log1p(evaluateDoubleValue) / Math.log1p(evaluateDoubleValueBase);
		}
		return null;
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
		if (forward) {
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
		return null;
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
		if (forward) {
			File file = new File((String) evaluate(stmt.filePathAndName));

			if (file.renameTo(new File((String) evaluate(stmt.filenewname)))) {
				System.out.println("File Renamed successfully");
			} else {
				System.out.println("Failed to Rename the file");
			}
		}
		return null;
	}

	@Override
	public Void visitMoveStmt(Move stmt) {
		if (forward) {
			File file = new File((String) evaluate(stmt.OringialfilePathAndFile));

			if (file.renameTo(new File((String) evaluate(stmt.newfilePath)))) {
				file.delete();
				System.out.println("File moved successfully");
			} else {
				System.out.println("Failed to move the file");
			}
		}
		return null;
	}

	@Override
	public Object visitTnirpStmt(Tnirp stmt) {
		Object value = "";
		if (!forward) {
			value = evaluate(stmt.expression);
			System.out.println(stringify(value));
		}
		return value;
	}

	@Override
	public Void visitNruterStmt(Nruter stmt) {
		if (!forward) {
			Object value = null;
			if (stmt.expression != null)
				value = evaluate(stmt.expression);
			throw new Snruter(value);
		}
		return null;
	}

	@Override
	public Void visitEvasStmt(Evas stmt) {
		if (!forward) {
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
				}
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Void visitDaerStmt(Daer stmt) {
		if (!forward) {
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
					if (boxInstance instanceof Instance) {
						((Instance) boxInstance).setAt(data, 0);
					}

				} else if (stmt.objectToReadInto instanceof Expr.Cup) {
					Object cupInstance = lookUpVariable(((Expr.Cup) stmt.objectToReadInto).identifier,
							((Expr.Cup) stmt.objectToReadInto));
					if (cupInstance instanceof Instance) {
						((Instance) cupInstance).setAt(data, 0);
					}
				} else if (stmt.objectToReadInto instanceof Expr.Pocket) {
					Object pocketInstance = lookUpVariable(((Expr.Pocket) stmt.objectToReadInto).identifier,
							((Expr.Pocket) stmt.objectToReadInto));
					if (pocketInstance instanceof Instance) {
						((Instance) pocketInstance).setAt(data, 0);
					}
				} else if (stmt.objectToReadInto instanceof Expr.Knot) {
					Object knotInstance = lookUpVariable(((Expr.Knot) stmt.objectToReadInto).identifier,
							((Expr.Knot) stmt.objectToReadInto));
					if (knotInstance instanceof Instance) {
						((Instance) knotInstance).setAt(data, 0);
					}
				}
				myReader.close();
			} catch (FileNotFoundException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Void visitEmanerStmt(Emaner stmt) {
		if (!forward) {
			File file = new File((String) evaluate(stmt.filePathAndName));

			if (file.renameTo(new File((String) evaluate(stmt.filenewname)))) {
				System.out.println("File Renamed successfully");
			} else {
				System.out.println("Failed to Rename the file");
			}
		}
		return null;
	}

	@Override
	public Void visitEvomStmt(Evom stmt) {
		if (!forward) {
			File file = new File((String) evaluate(stmt.OringialfilePathAndFile));

			if (file.renameTo(new File((String) evaluate(stmt.newfilePath)))) {
				file.delete();
				System.out.println("File moved successfully");
			} else {
				System.out.println("Failed to move the file");
			}
		}
		return null;
	}

	@Override
	public Object visitTnemngissaExpr(Tnemngissa expr) {
		if (!forward) {
			Object value = evaluate(expr.value);
			Integer distance = locals.get(expr);
			if (distance != null)
				environment.assignAt(distance, expr.name, expr.value, value, this);
			else
				globals.assign(expr.name, expr.value, value, this);
			return value;
		}
		return null;
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
		case EQUALSMINUS:
			return null;
		case EQUALSPLUS:
			return null;
		case GREATERTHEN:
			return null;
		case GREATERTHENEQUAL:
			return null;
		case LESSTHEN:
			return null;
		case LESSTHENEQUAL:
			return null;
		case MINUS:
			if (left instanceof Integer) {
				if (right instanceof Integer) {
					return addIntegerInteger(left, -1 * (Integer) right);
				} else if (right instanceof Double) {
					return addIntegerDouble(left, -1 * (Double) right);
				} else if (right instanceof Bin) {
					return addIntegerBin(left, Bin.times(new Bin(-1), (Bin) right));
				}
			} else if (left instanceof Double) {
				if (right instanceof Integer) {
					return addDoubleInteger(left, -1 * (Integer) right);
				} else if (right instanceof Double) {
					return addDoubleDouble(left, -1 * (Double) right);
				} else if (right instanceof Bin) {
					return addDoubleBin(left, Bin.times(new Bin(-1), (Bin) right));
				}
			} else if (left instanceof Bin) {
				if (right instanceof Integer) {
					return addBinInteger(left, -1 * (Integer) right);
				} else if (right instanceof Double) {
					return addBinDouble(left, -1 * (Double) right);
				} else if (right instanceof Bin) {
					return addBinBin(left, Bin.times(new Bin(-1), (Bin) right));
				}
			}

			return null;

		case PLUS:
			if (left instanceof Integer) {
				if (right instanceof Integer) {
					return addIntegerInteger(left, right);
				} else if (right instanceof Double) {
					return addIntegerDouble(left, right);
				} else if (right instanceof Bin) {
					return addIntegerBin(left, right);
				} else if (right instanceof String) {
					return addObjectString(left, right);
				}
			} else if (left instanceof Double) {
				if (right instanceof Integer) {
					return addDoubleInteger(left, right);
				} else if (right instanceof Double) {
					return addDoubleDouble(left, right);
				} else if (right instanceof Bin) {
					return addDoubleBin(left, right);
				} else if (right instanceof String) {
					return addObjectString(left, right);
				}
			} else if (left instanceof Bin) {
				if (right instanceof Integer) {
					return addBinInteger(left, right);
				} else if (right instanceof Double) {
					return addBinDouble(left, right);
				} else if (right instanceof Bin) {
					return addBinBin(left, right);
				} else if (right instanceof String) {
					return addObjectString(left, right);
				}
			} else if (left instanceof String) {
				return addObjectString(left, right);

			}
			return null;
		case FORWARDSLASH:
			return null;
		case BACKSLASH:
			return null;
		case TIMES:
			return null;

		case POWER:
			return null;
		case TOORY:
			return null;
		default:
			return null;

		}

	}

	@Override
	public Object visitOnomExpr(Onom expr) {
		if (!forward) {
			return findOnomExpr(expr);
		}
		return null;
	}

	private Object findOnomExpr(Expr expr) {
		Double result = 0.0;

		Object evaluate = null;
		TokenType type = null;
		if (expr instanceof Monoonom) {
			evaluate = evaluate(((Monoonom) expr).value);
			type = ((Monoonom) expr).operatorBackward.type;

		} else if (expr instanceof Onom) {
			evaluate = evaluate(((Onom) expr).value);
			type = ((Onom) expr).operator.type;

		}
		Double evaluateDouble = 0.0;
		if (evaluate instanceof Integer) {
			evaluateDouble = Double.valueOf(Integer.toString((Integer) evaluate));
		} else if (evaluate instanceof Double) {
			evaluateDouble = (Double) evaluate;
		} else if (evaluate instanceof Bin) {
			evaluateDouble = ((Bin) evaluate).toDouble();

		} else if (evaluate instanceof String) {
			evaluateDouble = convertStringToDouble((String) evaluate);

		}

		switch (type) {
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
		if (!forward) {
			Double evaluateDoubleValue = findDoubleValue(expr.value) - 1;
			Double evaluateDoubleValueBase = findDoubleValue(expr.valueBase) - 1;

			return Math.log1p(evaluateDoubleValue) / Math.log1p(evaluateDoubleValueBase);
		}
		return null;
	}

	@Override
	public Object visitLairotcafExpr(Lairotcaf expr) {
		if (!forward) {
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
		return null;
	}

	@Override
	public Object visitLlacExpr(Llac expr) {

		return null;
	}

	public void resolve(Expr expr, int i) {
		if (!locals.containsKey(expr))
			locals.put(expr, i);
	}

	public void setForward(boolean forward) {
		this.forward = forward;
	}

	@Override
	public Object visitFunDeclDeclaration(FunDecl declaration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitStmtDeclDeclaration(StmtDecl declaration) {
		
		return declaration.statement.accept(this);
	}

	@Override
	public Object visitFunctionFun(Function fun) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIfStmt(If stmt) {
		if (forward) {
		}
		return null;
	}

	@Override
	public Object visitSaveStmt(Save stmt) {
		if (forward) {
		}
		return null;
	}

	@Override
	public Object visitExpelStmt(Expel stmt) {
		if (forward) {
		} else {
		}
		return null;
	}

	@Override
	public Object visitReadStmt(Read stmt) {
		if (forward) {
		}
		return null;
	}

	@Override
	public Object visitConsumeStmt(Consume stmt) {
		if (forward) {
		} else {
		}
		return null;
	}

	@Override
	public Object visitVarStmt(Var stmt) {

		environment.define(stmt.name.lexeme, stmt.type, stmt.num, stmt.initilizer, this);
		if (stmt.initilizer instanceof Stmt.Expression)
			if (((Stmt.Expression) stmt.initilizer).expression != null) {
				mapVarNameToInitilizer(stmt.name, ((Stmt.Expression) stmt.initilizer).expression);
				evaluate(((Stmt.Expression) stmt.initilizer).expression);

			}

		return null;
	}

	private void mapVarNameToInitilizer(Token name, Expr evaluate) {
		if (evaluate instanceof Expr.Box) {
			nameMap.put(name.lexeme, ((Expr.Box) evaluate).identifier.lexeme);
		} else if (evaluate instanceof Expr.Pocket) {
			nameMap.put(name.lexeme, ((Expr.Pocket) evaluate).identifier.lexeme);
		} else if (evaluate instanceof Expr.Cup) {
			nameMap.put(name.lexeme, ((Expr.Cup) evaluate).identifier.lexeme);
		} else if (evaluate instanceof Expr.Knot) {
			nameMap.put(name.lexeme, ((Expr.Knot) evaluate).identifier.lexeme);
		} else if (evaluate instanceof Expr.Tonk) {
			nameMap.put(name.lexeme, ((Expr.Tonk) evaluate).identifier.lexeme);
		}

	}

	@Override
	public Object visitRavStmt(Rav stmt) {
		environment.define(stmt.name.lexeme, stmt.type, stmt.num, stmt.initilizer, this);
		if (stmt.initilizer instanceof Stmt.Expression)
			if (((Stmt.Expression) stmt.initilizer).expression != null) {
				mapVarNameToInitilizer(stmt.name, ((Stmt.Expression) stmt.initilizer).expression);
				evaluate(((Stmt.Expression) stmt.initilizer).expression);

			}
		return null;
	}

	@Override
	public Object visitGetExpr(Get expr) {
		if (forward) {
		}
		return null;
	}

	@Override
	public Object visitSetExpr(Set expr) {
		if (forward) {
		}
		return null;
	}

	@Override
	public Object visitSniatnocExpr(Sniatnoc expr) {
		if (!forward) {
		}
		return null;
	}

	@Override
	public Object visitTegExpr(Teg expr) {
		if (!forward) {
		}
		return null;
	}

	@Override
	public Object visitTesExpr(Tes expr) {
		if (!forward) {
		}
		return null;
	}

	@Override
	public Object visitLiteralCharExpr(LiteralChar expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCupExpr(Cup expr) {
		Environment previous = environment;
		Object name = lookUpVariable(expr.identifier, expr);
		ArrayList<Object> notnull = new ArrayList<>();
		try {

			if (name == null) {
				buildClass(expr);
				name = lookUpVariable(expr.identifier, expr);

			}
			environment = new Environment(environment);

			for (Declaration stmt : expr.expression) {
				Object execute = execute(stmt);
				if (execute != null)
					if (execute instanceof PocketInstance) {
						Token identifier = new Token(TokenType.IDENTIFIER,
								((Expr.Pocket) ((Stmt.Expression) stmt).expression).identifier.lexeme
										+ "varravargssgra",
								null, null, null, ((Expr.Pocket) ((Stmt.Expression) stmt).expression).identifier.column,
								((Expr.Pocket) ((Stmt.Expression) stmt).expression).identifier.line,
								((Expr.Pocket) ((Stmt.Expression) stmt).expression).identifier.start,
								((Expr.Pocket) ((Stmt.Expression) stmt).expression).identifier.finish);

						Expr.Variable variable = new Expr.Variable(identifier);
						Object lookUpVariableByName = lookUpVariableByName(variable);
						notnull.add(lookUpVariableByName);
					}else if (execute instanceof CupInstance) {
						Token identifier = new Token(TokenType.IDENTIFIER,
								((Expr.Cup) ((Stmt.Expression) stmt).expression).identifier.lexeme
										+ "varravargssgra",
								null, null, null, ((Expr.Cup) ((Stmt.Expression) stmt).expression).identifier.column,
								((Expr.Cup) ((Stmt.Expression) stmt).expression).identifier.line,
								((Expr.Cup) ((Stmt.Expression) stmt).expression).identifier.start,
								((Expr.Cup) ((Stmt.Expression) stmt).expression).identifier.finish);

						Expr.Variable variable = new Expr.Variable(identifier);
						Object lookUpVariableByName = lookUpVariableByName(variable);
						notnull.add(lookUpVariableByName);
					} else {
						notnull.add(execute);

					}
			}

		} finally {
			this.environment = previous;
		}
		Token identifier = new Token(TokenType.IDENTIFIER, expr.identifier.lexeme + "varravargssgra", null, null, null,
				expr.identifier.column, expr.identifier.line, expr.identifier.start, expr.identifier.finish);

		Expr.Variable variable = new Expr.Variable(identifier);
		Integer distance = locals.get(variable);
		if (distance != null) {
			environment.assignAt(distance, identifier, notnull, notnull, null);
		} else {
			globals.assignAt(distance, identifier, notnull, notnull, null);
		}
		name = lookUpVariable(expr.identifier, expr);
		BoxCallable knot = (BoxCallable) name;

		return knot.call(this);
	}

	@Override
	public Object visitPocketExpr(Pocket expr) {
		Environment previous = environment;
		Object name = lookUpVariable(expr.identifier, expr);
		ArrayList<Object> notnull = new ArrayList<>();
		try {

			if (name == null) {
				buildClass(expr);
				name = lookUpVariable(expr.identifier, expr);

			}
			environment = new Environment(environment);

			for (Stmt stmt : expr.expression) {
				Object execute = execute(stmt);
				if (execute != null)
					if (execute instanceof PocketInstance) {
						Token identifier = new Token(TokenType.IDENTIFIER,
								((Expr.Pocket) ((Stmt.Expression) stmt).expression).identifier.lexeme
										+ "varravargssgra",
								null, null, null, ((Expr.Pocket) ((Stmt.Expression) stmt).expression).identifier.column,
								((Expr.Pocket) ((Stmt.Expression) stmt).expression).identifier.line,
								((Expr.Pocket) ((Stmt.Expression) stmt).expression).identifier.start,
								((Expr.Pocket) ((Stmt.Expression) stmt).expression).identifier.finish);

						Expr.Variable variable = new Expr.Variable(identifier);
						Object lookUpVariableByName = lookUpVariableByName(variable);
						notnull.add(lookUpVariableByName);
					}else if (execute instanceof CupInstance) {
						Token identifier = new Token(TokenType.IDENTIFIER,
								((Expr.Cup) ((Stmt.Expression) stmt).expression).identifier.lexeme
										+ "varravargssgra",
								null, null, null, ((Expr.Cup) ((Stmt.Expression) stmt).expression).identifier.column,
								((Expr.Cup) ((Stmt.Expression) stmt).expression).identifier.line,
								((Expr.Cup) ((Stmt.Expression) stmt).expression).identifier.start,
								((Expr.Cup) ((Stmt.Expression) stmt).expression).identifier.finish);

						Expr.Variable variable = new Expr.Variable(identifier);
						Object lookUpVariableByName = lookUpVariableByName(variable);
						notnull.add(lookUpVariableByName);
					} else {
						notnull.add(execute);

					}
			}

		} finally {
			this.environment = previous;
		}
		Token identifier = new Token(TokenType.IDENTIFIER, expr.identifier.lexeme + "varravargssgra", null, null, null,
				expr.identifier.column, expr.identifier.line, expr.identifier.start, expr.identifier.finish);

		Expr.Variable variable = new Expr.Variable(identifier);
		Integer distance = locals.get(variable);
		if (distance != null) {
			environment.assignAt(distance, identifier, notnull, notnull, null);
		} else {
			globals.assignAt(distance, identifier, notnull, notnull, null);
		}
		name = lookUpVariable(expr.identifier, expr);
		BoxCallable knot = (BoxCallable) name;

		return knot.call(this);

	}

	@Override
	public Object visitKnotExpr(Knot expr) {
		Environment previous = environment;
		Object name = lookUpVariable(expr.identifier, expr);
		try {

			if (name == null) {
				buildClass(expr);
				name = lookUpVariable(expr.identifier, expr);

			}
			environment = new Environment(environment);
			executeKnot(expr, environment);
		} finally {
			this.environment = previous;
		}
		name = lookUpVariable(expr.identifier, expr);
		BoxCallable knot = (BoxCallable) name;

		return knot.call(this);
	}

	@Override
	public Object visitTonkExpr(Tonk expr) {
		Environment previous = environment;
		Object name = lookUpVariable(expr.identifier, expr);
		ArrayList<Object> notnull = new ArrayList<>();
		try {

			if (name == null) {
				buildClass(expr);
				name = lookUpVariable(expr.identifier, expr);

			}
			environment = new Environment(environment);

			new KnotRunner(expr.expression, this);

		} finally {
			this.environment = previous;
		}
		Token identifier = new Token(TokenType.IDENTIFIER, expr.identifier.lexeme + "varravargssgra", null, null, null,
				expr.identifier.column, expr.identifier.line, expr.identifier.start, expr.identifier.finish);

		Expr.Variable variable = new Expr.Variable(identifier);
		Integer distance = locals.get(variable);
		if (distance != null) {
			environment.assignAt(distance, identifier, notnull, notnull, null);
		} else {
			globals.assignAt(distance, identifier, notnull, notnull, null);
		}
		name = lookUpVariable(expr.identifier, expr);
		BoxCallable knot = (BoxCallable) name;

		return knot.call(this);
	}

	@Override
	public Object visitBoxExpr(Expr.Box expr) {

		return null;
	}

	@Override
	public Object visitFiStmt(Fi stmt) {
		if (!forward) {
		}
		return null;
	}

	@Override
	public Object visitCallExpr(Call expr) {
		if (forward) {
		}
		return null;
	}

	@Override
	public Object visitSwapExpr(Swap expr) {
		return null;
	}

	@Override
	public Object visitTemplatVarStmt(TemplatVar stmt) {
		if (forward) {
		} else {
		}
		return null;
	}

	@Override
	public Object visitIfiStmt(Ifi stmt) {
		if (forward) {
		} else {
		}
		return null;
	}

	@Override
	public Object visitTemplateExpr(Template expr) {

		return buildClass(expr);
	}

	private Object lookUpVariableByName(Variable name) {
//		java.util.Set<Expr> keySet = locals.keySet();
//		Expr exprToFind = null;
//		if (name != null) {
//			for (Expr keyExpr : keySet) {
//				if (keyExpr instanceof Expr.Variable) {
//
//					if (((Expr.Variable) keyExpr).name.lexeme == name.lexeme) {
//						exprToFind = keyExpr;
//					}
//				}
//			}
//		}
		Integer distance = locals.get(name);
		if (name != null) {
			if (distance != null) {
				return environment.getAt(distance, name.name.lexeme);
			} else {
				return globals.get(name.name, false);
			}
		}
		return null;
	}

	private Token buildClass(Expr expr) {
		Object superclass = null;
		if (expr instanceof Expr.Cup) {
			return buildCupClass(expr, superclass);
		} else if (expr instanceof Expr.Pocket) {
			return buildPocketClass(expr, superclass);

		} else if (expr instanceof Expr.Knot) {
			return buildKnotClass(expr, superclass);

		} else if (expr instanceof Expr.Tonk) {
			return buildTonkClass(expr, superclass);

		} else if (expr instanceof Expr.Template) {
			return buildClass(((Template) expr).container);
		}
		return null;
	}

	private Token buildTonkClass(Expr expr, Object superclass) {
		if (((Tonk) expr).identifier.identifierToken != null) {
			superclass = lookUpVariableByName(new Variable(((Tonk) expr).identifier.identifierToken));

			if (!(superclass instanceof BoxClass)) {
				throw new RuntimeError(((Tonk) expr).identifier.identifierToken, "Superclass must be a class.");
			}

		}
		Token identifier = new Token(TokenType.IDENTIFIER, ((Expr.Tonk) expr).identifier.lexeme + "varravargssgra",
				null, null, null, ((Expr.Tonk) expr).identifier.column, ((Expr.Tonk) expr).identifier.line,
				((Expr.Tonk) expr).identifier.start, ((Expr.Tonk) expr).identifier.finish);

		environment.define(identifier.lexeme, null, null);
		environment.define(((Tonk) expr).identifier.lexeme, ((Tonk) expr).identifier, null);

		if (((Tonk) expr).identifier.identifierToken != null) {
			environment = new Environment(environment);

		}

		ArrayList<Object> primarys = new ArrayList<>();
		for (Stmt stmt : ((Expr.Tonk) expr).expression) {
			primarys.add(stmt);
		}

		BoxClass boxClass = new BoxClass(((Tonk) expr).identifier.lexeme, (BoxClass) superclass, primarys, null,
				TokenType.CUPCONTAINER, false, null, environment, expr);

		if (superclass != null) {
			environment = environment.enclosing;
		}
		environment.assign(((Tonk) expr).identifier, null, boxClass);

		return ((Tonk) expr).identifier;
	}

	private Token buildKnotClass(Expr expr, Object superclass) {
		if (((Knot) expr).identifier.identifierToken != null) {
			superclass = lookUpVariableByName(new Variable(((Knot) expr).identifier.identifierToken));

			if (!(superclass instanceof BoxClass)) {
				throw new RuntimeError(((Knot) expr).identifier.identifierToken, "Superclass must be a class.");
			}

		}
		Token identifier = new Token(TokenType.IDENTIFIER, ((Expr.Knot) expr).identifier.lexeme + "varravargssgra",
				null, null, null, ((Expr.Knot) expr).identifier.column, ((Expr.Knot) expr).identifier.line,
				((Expr.Knot) expr).identifier.start, ((Expr.Knot) expr).identifier.finish);

		environment.define(identifier.lexeme, null, null);
		environment.define(((Knot) expr).identifier.lexeme, ((Knot) expr).identifier, null);

		if (((Knot) expr).identifier.identifierToken != null) {
			environment = new Environment(environment);

		}

		ArrayList<Object> primarys = new ArrayList<>();
		for (Stmt stmt : ((Expr.Knot) expr).expression) {

			primarys.add(stmt);
		}

		BoxClass boxClass = new BoxClass(((Knot) expr).identifier.lexeme, (BoxClass) superclass, primarys, null,
				TokenType.CUPCONTAINER, false, null, environment, expr);

		if (superclass != null) {
			environment = environment.enclosing;
		}
		environment.assign(((Knot) expr).identifier, null, boxClass);
		return ((Knot) expr).identifier;

	}

	private Token buildPocketClass(Expr expr, Object superclass) {
		if (((Expr.Pocket) expr).identifier.identifierToken != null) {
			superclass = lookUpVariableByName(new Variable(((Expr.Pocket) expr).identifier.identifierToken));

			if (!(superclass instanceof BoxClass)) {
				throw new RuntimeError(((Expr.Pocket) expr).identifier.identifierToken, "Superclass must be a class.");
			}

		}
		Token identifier = new Token(TokenType.IDENTIFIER, ((Expr.Pocket) expr).identifier.lexeme + "varravargssgra",
				null, null, null, ((Expr.Pocket) expr).identifier.column, ((Expr.Pocket) expr).identifier.line,
				((Expr.Pocket) expr).identifier.start, ((Expr.Pocket) expr).identifier.finish);

		environment.define(identifier.lexeme, null, null);
		environment.define(((Expr.Pocket) expr).identifier.lexeme, ((Expr.Pocket) expr).identifier, null);

		if (((Expr.Pocket) expr).identifier.identifierToken != null) {
			environment = new Environment(environment);

		}

		ArrayList<Object> primarys = new ArrayList<>();
		for (Stmt stmt : ((Expr.Pocket) expr).expression) {
			primarys.add(stmt);
		}

		BoxClass boxClass = new BoxClass(((Pocket) expr).identifier.lexeme, (BoxClass) superclass, primarys, null,
				TokenType.POCKETCONTAINER, false, null, environment, expr);

		if (superclass != null) {
			environment = environment.enclosing;
		}
		environment.assign(((Pocket) expr).identifier, null, boxClass);
		return ((Pocket) expr).identifier;

	}

	private Token buildCupClass(Expr expr, Object superclass) {
		if (((Expr.Cup) expr).identifier.identifierToken != null) {
			superclass = lookUpVariableByName(new Variable(((Expr.Cup) expr).identifier.identifierToken));

			if (!(superclass instanceof BoxClass)) {
				throw new RuntimeError(((Expr.Cup) expr).identifier.identifierToken, "Superclass must be a class.");
			}

		}
		Token identifier = new Token(TokenType.IDENTIFIER, ((Expr.Cup) expr).identifier.lexeme + "varravargssgra", null,
				null, null, ((Expr.Cup) expr).identifier.column, ((Expr.Cup) expr).identifier.line,
				((Expr.Cup) expr).identifier.start, ((Expr.Cup) expr).identifier.finish);

		environment.define(identifier.lexeme, null, null);
		environment.define(((Expr.Cup) expr).identifier.lexeme, ((Expr.Cup) expr).identifier, null);

		if (((Expr.Cup) expr).identifier.identifierToken != null) {
			environment = new Environment(environment);

		}

		Map<String, BoxFunction> methods = new HashMap<>();
		for (Declaration method : ((Expr.Cup) expr).expression) {
			if (method instanceof Declaration.FunDecl) {
				if (((Function) ((Declaration.FunDecl) method).function).forwardIdentifier != null) {
					String fname = ((Function) ((Declaration.FunDecl) method).function).forwardIdentifier.lexeme;
					Expr body = ((Function) ((Declaration.FunDecl) method).function).sharedCup;
					List<Token> fparamtypes = ((Function) ((Declaration.FunDecl) method).function).forwardPrametersType;
					List<Token> fparamsNames = ((Function) ((Declaration.FunDecl) method).function).forwardPrametersNames;
					BoxFunction boxFunction1 = new BoxFunction(body, fname, fparamtypes, fparamsNames, environment,
							false);
					methods.put(fname, boxFunction1);
				}
				if (((Function) ((Declaration.FunDecl) method).function).backwardIdentifier != null) {
					String bname = ((Function) ((Declaration.FunDecl) method).function).backwardIdentifier.lexeme;
					Expr body = ((Function) ((Declaration.FunDecl) method).function).sharedCup;
					List<Token> bparamtypes = ((Function) ((Declaration.FunDecl) method).function).backwardPrametersType;
					List<Token> bparamsNames = ((Function) ((Declaration.FunDecl) method).function).backwardPrametersNames;

					BoxFunction boxFunction0 = new BoxFunction(body, bname, bparamtypes, bparamsNames, environment,
							false);
					methods.put(bname, boxFunction0);

				}
			}
		}

		ArrayList<Object> primarys = new ArrayList<>();
		for (Declaration declaration : ((Expr.Cup) expr).expression) {

			primarys.add(declaration);
		}

		BoxClass boxClass = new BoxClass(((Expr.Cup) expr).identifier.lexeme, (BoxClass) superclass, primarys, methods,
				TokenType.CUPCONTAINER, false, null, environment, expr);

		if (superclass != null) {
			environment = environment.enclosing;
		}
		environment.assign(((Expr.Cup) expr).identifier, null, boxClass);
		return ((Expr.Cup) expr).identifier;
	}

	@Override
	public Object visitFunctionLinkFun(FunctionLink fun) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLinkExpr(Link expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPocketOpenExpr(PocketOpen expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPocketClosedExpr(PocketClosed expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCupOpenExpr(CupOpen expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCupClosedExpr(CupClosed expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBoxOpenExpr(BoxOpen expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBoxClosedExpr(BoxClosed expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSaveevasStmt(Saveevas stmt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitReaddaerStmt(Readdaer stmt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitRenameemanerStmt(Renameemaner stmt) {
		File file = new File((String) evaluate(stmt.filePathAndName));

		if (file.renameTo(new File((String) evaluate(stmt.filenewname)))) {
			System.out.println("File Renamed successfully");
		} else {
			System.out.println("Failed to Rename the file");
		}
		return null;
	}

	@Override
	public Object visitMoveevomStmt(Moveevom stmt) {

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
	public Object visitMonoonomExpr(Monoonom expr) {
		if (forward) {
			return findMono(expr);
		} else {
			return findOnomExpr(expr);
		}
	}

	@Override
	public Object visitBinaryyranibExpr(Binaryyranib expr) {

		Object left = evaluate(expr.left);
		Object right = evaluate(expr.right);

		switch (expr.operatorForward.type) {
		case NOTEQUALS:
			return !isEqual(left, right);
		case EQUALSNOT:
			return !isEqual(left, right);
		case EQUALSEQUALS:
			return isEqual(left, right);
		case MINUSEQUALS:

			return null;
		case PLUSEQUALS:

			return null;
		case EQUALSMINUS:

			return null;
		case EQUALSPLUS:

			return null;
		case GREATERTHEN:
			return null;
		case GREATERTHENEQUAL:
			return null;
		case LESSTHEN:
			return null;
		case LESSTHENEQUAL:
			return null;
		case MINUS:
			if (left instanceof Integer) {
				if (right instanceof Integer) {
					return addIntegerInteger(left, -1 * (Integer) right);
				} else if (right instanceof Double) {
					return addIntegerDouble(left, -1 * (Double) right);
				} else if (right instanceof Bin) {
					return addIntegerBin(left, Bin.times(new Bin(-1), (Bin) right));
				}
			} else if (left instanceof Double) {
				if (right instanceof Integer) {
					return addDoubleInteger(left, -1 * (Integer) right);
				} else if (right instanceof Double) {
					return addDoubleDouble(left, -1 * (Double) right);
				} else if (right instanceof Bin) {
					return addDoubleBin(left, Bin.times(new Bin(-1), (Bin) right));
				}
			} else if (left instanceof Bin) {
				if (right instanceof Integer) {
					return addBinInteger(left, -1 * (Integer) right);
				} else if (right instanceof Double) {
					return addBinDouble(left, -1 * (Double) right);
				} else if (right instanceof Bin) {
					return addBinBin(left, Bin.times(new Bin(-1), (Bin) right));
				}
			}

			return null;

		case PLUS:
			if (left instanceof Integer) {
				if (right instanceof Integer) {
					return addIntegerInteger(left, right);
				} else if (right instanceof Double) {
					return addIntegerDouble(left, right);
				} else if (right instanceof Bin) {
					return addIntegerBin(left, right);
				} else if (right instanceof String) {
					return addObjectString(left, right);
				}
			} else if (left instanceof Double) {
				if (right instanceof Integer) {
					return addDoubleInteger(left, right);
				} else if (right instanceof Double) {
					return addDoubleDouble(left, right);
				} else if (right instanceof Bin) {
					return addDoubleBin(left, right);
				} else if (right instanceof String) {
					return addObjectString(left, right);
				}
			} else if (left instanceof Bin) {
				if (right instanceof Integer) {
					return addBinInteger(left, right);
				} else if (right instanceof Double) {
					return addBinDouble(left, right);
				} else if (right instanceof Bin) {
					return addBinBin(left, right);
				} else if (right instanceof String) {
					return addObjectString(left, right);
				}
			} else if (left instanceof String) {
				return addObjectString(left, right);

			}
			return null;

		case FORWARDSLASH:
			return null;
		case BACKSLASH:
			return null;
		case TIMES:
			return null;

		case POWER:
			return null;
		case YROOT:
			return null;

		case DNA:
			return null;
		case AND:
			return null;
		case RO:
			return null;
		case OR:
			return null;
		default:
			return null;
		}
	}

	@Override
	public Object visitLoggolExpr(Loggol expr) {
		Double evaluateDoubleValue = findDoubleValue(expr.value) - 1;
		Double evaluateDoubleValueBase = findDoubleValue(expr.valueBase) - 1;

		return Math.log1p(evaluateDoubleValue) / Math.log1p(evaluateDoubleValueBase);

	}

	@Override
	public Object visitCallllacExpr(Callllac expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitExpressiontmtsExpr(Expressiontmts expr) {
		if (forward) {
			Double result = findMono(expr);

			return result;
		} else {
			if (expr.tnemetatsToken.type == TokenType.TNIRP) {
				Object value = findMono(expr);
				System.out.println(stringify(value));
				return value;
			} else if (expr.tnemetatsToken.type == TokenType.NRUTER) {
				Object value = null;
				if (expr.expression != null)
					value = findMono(expr);
				throw new Snruter(value);
			}
		}
		return null;
	}

	private Double findMono(Expr expr) {
		Double result = 0.0;

		Object evaluate = null;
		TokenType type = null;
		if (expr instanceof Expressiontmts) {
			type = ((Expressiontmts) expr).expressionToken.type;
			evaluate = evaluate(((Expressiontmts) expr).expression);
		} else if (expr instanceof Monoonom) {
			evaluate = evaluate(((Monoonom) expr).value);
			type = ((Monoonom) expr).operatorForward.type;

		} else if (expr instanceof Mono) {
			evaluate = evaluate(((Mono) expr).value);
			type = ((Mono) expr).operator.type;

		}
		Double evaluateDouble = 0.0;
		if (evaluate instanceof Integer) {
			evaluateDouble = Double.valueOf(Integer.toString((Integer) evaluate));
		} else if (evaluate instanceof Double) {
			evaluateDouble = (Double) evaluate;
		} else if (evaluate instanceof Bin) {
			evaluateDouble = ((Bin) evaluate).toDouble();

		} else if (evaluate instanceof String) {
			evaluateDouble = convertStringToDouble((String) evaluate);

		}

		switch (type) {
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
	public Object visitStmttmtSStmt(StmttmtS stmt) {
		if (forward) {
			if (stmt.keywordForward.type == TokenType.PRINT) {
				Object value = evaluate(stmt.expression);
				System.out.println(stringify(value));
				return value;
			} else if (stmt.keywordForward.type == TokenType.RETURN) {
				Object value = null;
				if (stmt.expression != null)
					value = evaluate(stmt.expression);
				throw new Returns(value);
			}
		} else {
			if (stmt.keywordBackward.type == TokenType.TNIRP) {
				Object value = evaluate(stmt.expression);
				System.out.println(stringify(value));
				return value;
			} else if (stmt.keywordBackward.type == TokenType.NRUTER) {
				Object value = null;
				if (stmt.expression != null)
					value = evaluate(stmt.expression);
				throw new Snruter(value);
			}
		}
		return null;
	}

	@Override
	public Object visitStmtnoisserpxeStmt(Parser.Stmt.Stmtnoisserpxe stmt) {
		if (forward) {
			if (stmt.statementToken.type == TokenType.PRINT) {
				Object value = findOnomStmt(stmt);
				System.out.println(stringify(value));
				return value;
			} else if (stmt.statementToken.type == TokenType.RETURN) {
				Object value = null;
				if (stmt.expression != null)
					value = findOnomStmt(stmt);
				throw new Returns(value);
			}
		} else {
			return findOnomStmt(stmt);
		}
		return null;
	}

	private Object findOnomStmt(Parser.Stmt.Stmtnoisserpxe stmt) {
		Double result = 0.0;
		Object evaluate = evaluate(stmt.expression);
		Double evaluateDouble = 0.0;
		if (evaluate instanceof Integer) {
			evaluateDouble = Double.valueOf(Integer.toString((Integer) evaluate));
		} else if (evaluate instanceof Double) {
			evaluateDouble = (Double) evaluate;
		} else if (evaluate instanceof Bin) {
			evaluateDouble = ((Bin) evaluate).toDouble();

		} else if (evaluate instanceof String) {
			evaluateDouble = convertStringToDouble((String) evaluate);

		}

		switch (stmt.noisserpxeToken.type) {
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

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public Object visitAssignmenttnemgissaExpr(Assignmenttnemgissa expr) {
		if (forward) {
			Object value = evaluate(expr.value);
			Integer distance = locals.get(expr.nameForward);
			if (distance != null)
				environment.assignAt(distance, expr.nameForward, expr.value, value, this);
			else
				globals.assign(expr.nameForward, expr.value, value, this);
			return value;
		} else {
			Object value = evaluate(expr.value);
			Integer distance = locals.get(expr.nameBackward);
			if (distance != null)
				environment.assignAt(distance, expr.nameBackward, expr.value, value, this);
			else
				globals.assign(expr.nameBackward, expr.value, value, this);
			return value;
		}
	}

	public void executeExprBlock(List<Stmt> expression, Environment environment1) {

		for (Stmt stmt : expression) {
			execute(stmt);
		}

	}

	public void executeCup(List<Declaration> expression, Environment environment1) {

		for (Declaration stmt : expression) {
			execute(stmt);
		}

	}

	public void executeKnot(Knot body, Environment environment1) {

		new KnotRunner(body.expression, this);

	}

	public void executeTonk(Tonk body, Environment environment1) {

		new KnotRunner(body.expression, this);

	}

}
