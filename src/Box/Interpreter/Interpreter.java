package Box.Interpreter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import Box.Box.Box;
import Box.Token.Token;
import Box.Token.TokenType;
import Parser.Declaration;
import Parser.Expr;
import Parser.Stmt;
import Parser.Declaration.FunDecl;
import Parser.Declaration.StmtDecl;
import Parser.Expr.Additive;
import Parser.Expr.Addittidda;
import Parser.Expr.Assignment;
import Parser.Expr.Assignmenttnemgissa;
import Parser.Expr.Binary;
import Parser.Expr.Binaryyranib;
import Parser.Expr.BoxClosed;
import Parser.Expr.BoxOpen;
import Parser.Expr.Bus;
import Parser.Expr.Call;
import Parser.Expr.Callllac;
import Parser.Expr.Contains;
import Parser.Expr.Containssniatnoc;
import Parser.Expr.Cup;
import Parser.Expr.CupClosed;
import Parser.Expr.CupOpen;
import Parser.Expr.Evitidda;
import Parser.Expr.Expressiontmts;
import Parser.Expr.Factorial;
import Parser.Expr.Get;
import Parser.Expr.Gol;
import Parser.Expr.Knot;
import Parser.Expr.Lairotcaf;
import Parser.Expr.Link;
import Parser.Expr.Literal;
import Parser.Expr.LiteralBool;
import Parser.Expr.LiteralChar;
import Parser.Expr.LiteralLoob;
import Parser.Expr.Llac;
import Parser.Expr.Log;
import Parser.Expr.Loggol;
import Parser.Expr.Mono;
import Parser.Expr.Monoonom;
import Parser.Expr.NoPaCoOOoCaPoN;
import Parser.Expr.NonParamContOp;
import Parser.Expr.Onom;
import Parser.Expr.ParCoOppOoCraP;
import Parser.Expr.ParamContOp;
import Parser.Expr.PoTnocMarap;
import Parser.Expr.PoTnocMarapNon;
import Parser.Expr.Pocket;
import Parser.Expr.PocketClosed;
import Parser.Expr.PocketOpen;
import Parser.Expr.Set;
import Parser.Expr.Setat;
import Parser.Expr.Setattates;
import Parser.Expr.Sniatnoc;
import Parser.Expr.Sub;
import Parser.Expr.Subbus;
import Parser.Expr.Swap;
import Parser.Expr.Tates;
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
	Environment environment = globals;
	private Map<Expr, Integer> locals = new WesMap<>();
	private boolean fromCall = false;
	private boolean forward;
	HashMap<String, String> nameMap = new HashMap<>();
	HashMap<String, String> classMapToSuperClass = new HashMap<>();
	private ArrayList<Object> identifiers = new ArrayList<>();
	private ArrayList<String> classes = new ArrayList<>();
	private ArrayList<String> templates = new ArrayList<>();
	private ArrayList<String> links = new ArrayList<>();

	public Interpreter() {

		globals.define("clock", null, new BoxCallable() {

			@Override
			public int arity() {

				return 0;
			}

			@Override
			public String toString() {
				return "<native fn>";
			}

			@Override
			public Object call(Interpreter interpreter, List<Object> arguments) {
				return (double) System.currentTimeMillis() / 1000.0;
			}

			@Override
			public BoxFunction findMethod(String lexeme) {
				// TODO Auto-generated method stub
				return null;
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

		return stmt.accept(this);

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
		if (forward)
			return evaluate(stmt.expression);
		else if (stmt.noisserpxe != null)
			return evaluate(stmt.noisserpxe);
		else
			return null;
	}

	@Override
	public Object visitAssignmentExpr(Assignment expr) {
		if (forward) {
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
		Object left = null;
		Object right = null;

		if (expr.left instanceof Pocket || expr.left instanceof Cup) {
			left = evaluate(expr.left);
			right = evaluate(expr.right);
		} else if (expr.right instanceof Pocket || expr.right instanceof Cup) {
			right = evaluate(expr.right);
			left = evaluate(expr.left);
		} else {
			left = evaluate(expr.left);
			right = evaluate(expr.right);
		}

		left = parseBinData(left);
		right = parseBinData(right);

		switch (expr.operator.type) {
		case NOTEQUALS:
			return !isEqual(left, right);
		case EQUALSNOT:
			return !isEqual(left, right);
		case EQUALSEQUALS:
			return isEqual(left, right);

		case GREATERTHEN:
			return greaterthen(left, right);
		case GREATERTHENEQUAL:
			return greaterequalthen(left, right);
		case LESSTHEN:
			return lessthen(left, right);
		case LESSTHENEQUAL:
			return lessequalthen(left, right);
		case EQUALLESSTHEN:
			return greaterequalthen(left, right);
		case EQUALGREATERTHEN:
			return lessequalthen(left, right);

		case MINUS:

			return sub(left, right);
		case EQUALSMINUS:
			return sub(left, right, expr.left, expr.operator);
		case MINUSEQUALS:
			return sub(left, right, expr.right, expr.operator);

		case EQUALSPLUS:
			return add(left, right, expr.left, expr.operator);
		case PLUSEQUALS:
			return add(left, right, expr.right, expr.operator);
		case PLUS:
			return add(left, right, expr.left, expr.right);
		case MOD:
			return mod(left, right);
		case MODEQUAL:
			return mod(left, right, expr.right, expr.operator);
		case EQUALMOD:
			return mod(left, right, expr.left, expr.operator);

		case FORWARDSLASH:
			return div(left, right);
		case BACKSLASH:
			Object div = div(right, left);
			return div;
		case EQUALDIVIDEFORWARD:
			return div(right, left, expr.left, expr.operator);
		case EQUALDIVIDEBACKWARD:
			return div(right, left, expr.right, expr.operator);

		case TIMES:
			return times(left, right);
		case TIMESEQUAL:
			return times(left, right, expr.right, expr.operator);
		case EQUALTIMES:
			return times(left, right, expr.left, expr.operator);

		case POWER:
			return power(left, right);
		case EQUALPOWER:
			return power(left, right, expr.left, expr.operator);
		case POWEREQUAL:
			return power(left, right, expr.right, expr.operator);
		case YROOT:
			return null;
		case TOORY:
			return null;

		case DNA:
			if (!forward)
				return and(left, right);
			else
				return null;
		case AND:
			if (forward)
				return and(left, right);
			else
				return null;
		case RO:
			if (!forward)
				return or(left, right);
			else
				return false;
		case OR:
			if (forward)
				return or(left, right);
			else
				return false;
		default:
			return null;
		}

	}

	private Object power(Object left, Object right, Expr left2, Token operator) {
		Object toreturn = null;
		if (left instanceof Integer) {
			if (right instanceof Integer) {
				toreturn = powerIntegerInteger(left, right);
			} else if (right instanceof Double) {
				toreturn = powerIntegerDouble(left, right);
			} else if (right instanceof Bin) {
				toreturn = powerIntegerBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				toreturn = powerArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Double) {
			if (right instanceof Integer) {
				toreturn = timesDoubleInteger(left, right);
			} else if (right instanceof Double) {
				toreturn = timesDoubleDouble(left, right);
			} else if (right instanceof Bin) {
				toreturn = timesDoubleBin(left, right);
			} else if (right instanceof String) {
				toreturn = timesObjectString(left, right);
			} else if (right instanceof ArrayList<?>) {
				toreturn = powerArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Bin) {
			if (right instanceof Integer) {
				toreturn = timesBinInteger(left, right);
			} else if (right instanceof Double) {
				toreturn = timesBinDouble(left, right);
			} else if (right instanceof Bin) {
				toreturn = timesBinBin(left, right);
			} else if (right instanceof String) {
				toreturn = timesObjectString(left, right);
			} else if (right instanceof ArrayList<?>) {
				toreturn = powerArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof String) {
			if (right instanceof Integer) {
				toreturn = timesObjectString(right, left);
			} else if (right instanceof Double) {
				toreturn = timesObjectString(right, left);
			} else if (right instanceof Bin) {
				toreturn = timesObjectString(right, left);
			} else if (right instanceof ArrayList<?>) {
				toreturn = addStringArray(left, right);
			}

		} else if (left instanceof ArrayList<?>) {
			if (right instanceof ArrayList<?>)
				toreturn = powerArrayListArrayList(left, right, 1);
			else
				toreturn = powerBinaryArrayList(left, right, 1);
		}
		if (left2 instanceof Variable)
			setGlobalOrCurrentEnvironmentVariable(toreturn, ((Variable) left2));
		else
			throw new RuntimeError(operator, "either left or right must be a variable.");

		return toreturn;
	}

	private Object mod(Object left, Object right, Expr right2, Token operator) {
		Object toreturn = null;
		if (left instanceof Integer) {
			if (right instanceof Integer) {
				toreturn = modIntegerInteger(left, right);
			} else if (right instanceof Double) {
				toreturn = modIntegerDouble(left, right);
			} else if (right instanceof Bin) {
				toreturn = modIntegerBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				toreturn = modArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Double) {
			if (right instanceof Integer) {
				toreturn = modDoubleInteger(left, right);
			} else if (right instanceof Double) {
				toreturn = modDoubleDouble(left, right);
			} else if (right instanceof Bin) {
				toreturn = modDoubleBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				toreturn = modArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Bin) {
			if (right instanceof Integer) {
				toreturn = modBinInteger(left, right);
			} else if (right instanceof Double) {
				toreturn = modBinDouble(left, right);
			} else if (right instanceof Bin) {
				toreturn = modBinBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				toreturn = modArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof ArrayList<?>) {
			if (right instanceof ArrayList<?>)
				toreturn = modArrayListArrayList(left, right, 1);
			else
				toreturn = modBinaryArrayList(left, right, 1);
		}

		if (right2 instanceof Variable)
			setGlobalOrCurrentEnvironmentVariable(toreturn, ((Variable) right2));
		else
			throw new RuntimeError(operator, "either left or right must be a variable.");

		return toreturn;
	}

	private Object mod(Object left, Object right) {
		if (left instanceof Integer) {
			if (right instanceof Integer) {
				return modIntegerInteger(left, right);
			} else if (right instanceof Double) {
				return modIntegerDouble(left, right);
			} else if (right instanceof Bin) {
				return modIntegerBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				return modArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Double) {
			if (right instanceof Integer) {
				return modDoubleInteger(left, right);
			} else if (right instanceof Double) {
				return modDoubleDouble(left, right);
			} else if (right instanceof Bin) {
				return modDoubleBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				return modArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Bin) {
			if (right instanceof Integer) {
				return modBinInteger(left, right);
			} else if (right instanceof Double) {
				return modBinDouble(left, right);
			} else if (right instanceof Bin) {
				return modBinBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				return modArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof ArrayList<?>) {
			if (right instanceof ArrayList<?>)
				return modArrayListArrayList(left, right, 1);
			else
				return modBinaryArrayList(left, right, 1);
		}
		return null;
	}

	private Object times(Object left, Object right, Expr right2, Token operator) {
		Object toreturn = null;
		if (left instanceof Integer) {
			if (right instanceof Integer) {
				toreturn = timesIntegerInteger(left, right);
			} else if (right instanceof Double) {
				toreturn = timesIntegerDouble(left, right);
			} else if (right instanceof Bin) {
				toreturn = timesIntegerBin(left, right);
			} else if (right instanceof String) {
				toreturn = timesObjectString(left, right);
			} else if (right instanceof ArrayList<?>) {
				toreturn = timesArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Double) {
			if (right instanceof Integer) {
				toreturn = timesDoubleInteger(left, right);
			} else if (right instanceof Double) {
				toreturn = timesDoubleDouble(left, right);
			} else if (right instanceof Bin) {
				toreturn = timesDoubleBin(left, right);
			} else if (right instanceof String) {
				toreturn = timesObjectString(left, right);
			} else if (right instanceof ArrayList<?>) {
				toreturn = timesArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Bin) {
			if (right instanceof Integer) {
				toreturn = timesBinInteger(left, right);
			} else if (right instanceof Double) {
				toreturn = timesBinDouble(left, right);
			} else if (right instanceof Bin) {
				toreturn = timesBinBin(left, right);
			} else if (right instanceof String) {
				toreturn = timesObjectString(left, right);
			} else if (right instanceof ArrayList<?>) {
				toreturn = timesArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof String) {
			if (right instanceof Integer) {
				toreturn = timesObjectString(right, left);
			} else if (right instanceof Double) {
				toreturn = timesObjectString(right, left);
			} else if (right instanceof Bin) {
				toreturn = timesObjectString(right, left);
			} else if (right instanceof ArrayList<?>) {
				toreturn = addStringArray(left, right);
			}

		} else if (left instanceof ArrayList<?>) {
			if (right instanceof ArrayList<?>)
				toreturn = timesArrayListArrayList(left, right, 1);
			else
				toreturn = timesBinaryArrayList(left, right, 1);
		}
		if (right2 instanceof Variable)
			setGlobalOrCurrentEnvironmentVariable(toreturn, ((Variable) right2));
		else
			throw new RuntimeError(operator, "either left or right must be a variable.");

		return toreturn;
	}

	private Object div(Object right, Object left, Object lef, Token operator) {
		Object toreturn = null;
		if (left instanceof Integer) {
			if (right instanceof Integer) {
				toreturn = divIntegerInteger(left, right);
			} else if (right instanceof Double) {
				toreturn = divIntegerDouble(left, right);
			} else if (right instanceof Bin) {
				toreturn = divIntegerBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				toreturn = divArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Double) {
			if (right instanceof Integer) {
				toreturn = divDoubleInteger(left, right);
			} else if (right instanceof Double) {
				toreturn = divDoubleDouble(left, right);
			} else if (right instanceof Bin) {
				toreturn = divDoubleBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				toreturn = divArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Bin) {
			if (right instanceof Integer) {
				toreturn = divBinInteger(left, right);
			} else if (right instanceof Double) {
				toreturn = divBinDouble(left, right);
			} else if (right instanceof Bin) {
				toreturn = divBinBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				toreturn = divArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof ArrayList<?>) {
			if (right instanceof ArrayList<?>)
				toreturn = divArrayListArrayList(left, right, 1);
			else
				toreturn = divBinaryArrayList(left, right, 1);
		}

		if (lef instanceof Variable)
			setGlobalOrCurrentEnvironmentVariable(toreturn, ((Variable) lef));
		else
			throw new RuntimeError(operator, "either left or right must be a variable.");

		return toreturn;
	}

	private Object add(Object left, Object right, Expr left2, Token operator) {
		Object toreturn = null;
		if (left instanceof Integer) {
			if (right instanceof Integer) {
				toreturn = addIntegerInteger(left, right);
			} else if (right instanceof Double) {
				toreturn = addIntegerDouble(left, right);
			} else if (right instanceof Bin) {
				toreturn = addIntegerBin(left, right);
			} else if (right instanceof String) {
				toreturn = addObjectString(left, right);
			} else if (right instanceof ArrayList<?>) {
				toreturn = addArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Double) {
			if (right instanceof Integer) {
				toreturn = addDoubleInteger(left, right);
			} else if (right instanceof Double) {
				toreturn = addDoubleDouble(left, right);
			} else if (right instanceof Bin) {
				toreturn = addDoubleBin(left, right);
			} else if (right instanceof String) {
				toreturn = addObjectString(left, right);
			} else if (right instanceof ArrayList<?>) {
				toreturn = addArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Bin) {
			if (right instanceof Integer) {
				toreturn = addBinInteger(left, right);
			} else if (right instanceof Double) {
				toreturn = addBinDouble(left, right);
			} else if (right instanceof Bin) {
				toreturn = addBinBin(left, right);
			} else if (right instanceof String) {
				toreturn = addObjectString(left, right);
			} else if (right instanceof ArrayList<?>) {

				toreturn = addArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof String) {
			if (right instanceof Integer) {
				toreturn = addObjectString(left, right);
			} else if (right instanceof Double) {
				toreturn = addObjectString(left, right);
			} else if (right instanceof Bin) {
				toreturn = addObjectString(left, right);
			} else if (right instanceof String) {
				toreturn = addObjectString(left, right);
			} else if (right instanceof ArrayList<?>) {
				toreturn = addStringArray(left, right);
			}

		} else if (left instanceof ArrayList<?>) {
			if (right instanceof ArrayList<?>)
				toreturn = addArrayListArrayList(left, right, 1);
			else
				toreturn = addBinaryArrayList(left, right, 1);
		}

		if (left2 instanceof Variable)
			setGlobalOrCurrentEnvironmentVariable(toreturn, ((Variable) left2));
		else
			throw new RuntimeError(operator, "either left or right must be a variable.");

		return toreturn;
	}

	private Object sub(Object left, Object right, Object left2, Token operator) {
		Object toreturn = null;
		if (left instanceof Integer) {
			if (right instanceof Integer) {
				toreturn = addIntegerInteger(left, -1 * (Integer) right);
			} else if (right instanceof Double) {
				toreturn = addIntegerDouble(left, -1 * (Double) right);
			} else if (right instanceof Bin) {
				toreturn = addIntegerBin(left, Bin.times(new Bin(-1), (Bin) right));
			} else if (right instanceof ArrayList<?>) {
				toreturn = addArrayBinaryList(left, right, -1);

			}
		} else if (left instanceof Double) {
			if (right instanceof Integer) {
				toreturn = addDoubleInteger(left, -1 * (Integer) right);
			} else if (right instanceof Double) {
				toreturn = addDoubleDouble(left, -1 * (Double) right);
			} else if (right instanceof Bin) {
				toreturn = addDoubleBin(left, Bin.times(new Bin(-1), (Bin) right));
			} else if (right instanceof ArrayList<?>) {

				toreturn = addArrayBinaryList(left, right, -1);
			}
		} else if (left instanceof Bin) {
			if (right instanceof Integer) {
				toreturn = addBinInteger(left, -1 * (Integer) right);
			} else if (right instanceof Double) {
				toreturn = addBinDouble(left, -1 * (Double) right);
			} else if (right instanceof Bin) {
				toreturn = addBinBin(left, Bin.times(new Bin(-1), (Bin) right));
			} else if (right instanceof ArrayList<?>) {
				toreturn = addArrayBinaryList(left, right, -1);
			}
		} else if (left instanceof ArrayList<?>) {
			if (right instanceof ArrayList<?>)
				toreturn = addArrayListArrayList(left, right, -1);
			else
				toreturn = addBinaryArrayList(left, right, -1);
		}

		if (left2 instanceof Variable)
			setGlobalOrCurrentEnvironmentVariable(toreturn, ((Variable) left2));
		else
			throw new RuntimeError(operator, "either left or right must be a variable.");

		return toreturn;
	}

	private Object and(Object left, Object right) {
		ArrayList<Boolean> bolArr = new ArrayList<>();
		if (left instanceof Boolean && right instanceof Boolean) {
			return (Boolean) left && (Boolean) right;
		} else if (left == null && right == null) {
			return false;
		} else if (left instanceof Boolean && right == null) {
			return (Boolean) left;
		} else if (left == null && right instanceof Boolean) {
			return (Boolean) right;
		} else if (left instanceof ArrayList<?> && right == null) {
			return left;
		} else if (left == null && right instanceof ArrayList<?>) {
			return right;
		} else if (left instanceof ArrayList<?> && right instanceof Boolean) {
			ArrayList<?> arr = ((ArrayList<?>) left);
			for (Object object : arr) {
				if (object instanceof Boolean) {
					bolArr.add((Boolean) object && (Boolean) right);
				} else if (object instanceof ArrayList<?>) {
					bolArr.add((Boolean) and(object, right) && (Boolean) right);
				}
			}
			return bolArr;
		} else if (left instanceof Boolean && right instanceof ArrayList<?>) {
			ArrayList<?> arr = ((ArrayList<?>) right);
			for (Object object : arr) {
				if (object instanceof Boolean) {
					bolArr.add((Boolean) left && (Boolean) object);
				} else if (object instanceof ArrayList<?>) {
					bolArr.add((Boolean) and(left, object) && (Boolean) left);
				}
			}
			return bolArr;
		} else if (left instanceof ArrayList<?> && right instanceof ArrayList<?>) {
			ArrayList<?> arr = ((ArrayList<?>) left);
			ArrayList<?> arr1 = ((ArrayList<?>) right);
			for (Object object : arr) {
				for (Object object1 : arr1) {
					if (object instanceof Boolean && object1 instanceof Boolean) {
						bolArr.add((Boolean) object && (Boolean) object1);
					} else if (object instanceof Boolean && object1 instanceof ArrayList<?>) {
						bolArr.add((Boolean) object && (Boolean) and(object, object1));
					} else if (object instanceof ArrayList<?> && object1 instanceof Boolean) {
						bolArr.add((Boolean) and(object, object1) && (Boolean) object1);
					} else if (object instanceof ArrayList<?> && object1 instanceof ArrayList<?>) {
						bolArr.add((Boolean) and(object, object1) && (Boolean) and(object, object1));
					}
				}
			}
			return bolArr;

		}
		return null;
	}

	private Object or(Object left, Object right) {
		ArrayList<Boolean> bolArr = new ArrayList<>();
		if (left instanceof Boolean && right instanceof Boolean) {
			return (Boolean) left && (Boolean) right;
		} else if (left == null && right == null) {
			return false;
		} else if (left instanceof Boolean && right == null) {
			return (Boolean) left;
		} else if (left == null && right instanceof Boolean) {
			return (Boolean) right;
		} else if (left instanceof ArrayList<?> && right == null) {
			return left;
		} else if (left == null && right instanceof ArrayList<?>) {
			return right;
		} else if (left instanceof ArrayList<?> && right instanceof Boolean) {
			ArrayList<?> arr = ((ArrayList<?>) left);
			for (Object object : arr) {
				if (object instanceof Boolean) {
					bolArr.add((Boolean) object || (Boolean) right);
				} else if (object instanceof ArrayList<?>) {
					bolArr.add((Boolean) and(object, right) || (Boolean) right);
				}
			}
			return bolArr;
		} else if (left instanceof Boolean && right instanceof ArrayList<?>) {
			ArrayList<?> arr = ((ArrayList<?>) right);
			for (Object object : arr) {
				if (object instanceof Boolean) {
					bolArr.add((Boolean) left || (Boolean) object);
				} else if (object instanceof ArrayList<?>) {
					bolArr.add((Boolean) and(left, object) || (Boolean) left);
				}
			}
			return bolArr;
		} else if (left instanceof ArrayList<?> && right instanceof ArrayList<?>) {
			ArrayList<?> arr = ((ArrayList<?>) left);
			ArrayList<?> arr1 = ((ArrayList<?>) right);
			for (Object object : arr) {
				for (Object object1 : arr1) {
					if (object instanceof Boolean && object1 instanceof Boolean) {
						bolArr.add((Boolean) object || (Boolean) object1);
					} else if (object instanceof Boolean && object1 instanceof ArrayList<?>) {
						bolArr.add((Boolean) object || (Boolean) and(object, object1));
					} else if (object instanceof ArrayList<?> && object1 instanceof Boolean) {
						bolArr.add((Boolean) and(object, object1) || (Boolean) object1);
					} else if (object instanceof ArrayList<?> && object1 instanceof ArrayList<?>) {
						bolArr.add((Boolean) and(object, object1) || (Boolean) and(object, object1));
					}
				}
			}
			return bolArr;

		}
		return null;
	}

	private Object times(Object left, Object right) {
		if (left instanceof Integer) {
			if (right instanceof Integer) {
				return timesIntegerInteger(left, right);
			} else if (right instanceof Double) {
				return timesIntegerDouble(left, right);
			} else if (right instanceof Bin) {
				return timesIntegerBin(left, right);
			} else if (right instanceof String) {
				return timesObjectString(left, right);
			} else if (right instanceof ArrayList<?>) {
				return timesArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Double) {
			if (right instanceof Integer) {
				return timesDoubleInteger(left, right);
			} else if (right instanceof Double) {
				return timesDoubleDouble(left, right);
			} else if (right instanceof Bin) {
				return timesDoubleBin(left, right);
			} else if (right instanceof String) {
				return timesObjectString(left, right);
			} else if (right instanceof ArrayList<?>) {
				return timesArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Bin) {
			if (right instanceof Integer) {
				return timesBinInteger(left, right);
			} else if (right instanceof Double) {
				return timesBinDouble(left, right);
			} else if (right instanceof Bin) {
				return timesBinBin(left, right);
			} else if (right instanceof String) {
				return timesObjectString(left, right);
			} else if (right instanceof ArrayList<?>) {
				return timesArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof String) {
			if (right instanceof Integer) {
				return timesObjectString(right, left);
			} else if (right instanceof Double) {
				return timesObjectString(right, left);
			} else if (right instanceof Bin) {
				return timesObjectString(right, left);
			} else if (right instanceof ArrayList<?>) {
				return addStringArray(left, right);
			}

		} else if (left instanceof ArrayList<?>) {
			if (right instanceof ArrayList<?>)
				return timesArrayListArrayList(left, right, 1);
			else
				return timesBinaryArrayList(left, right, 1);
		}
		return null;
	}

	private Object power(Object left, Object right) {
		if (left instanceof Integer) {
			if (right instanceof Integer) {
				return powerIntegerInteger(left, right);
			} else if (right instanceof Double) {
				return powerIntegerDouble(left, right);
			} else if (right instanceof Bin) {
				return powerIntegerBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				return powerArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Double) {
			if (right instanceof Integer) {
				return timesDoubleInteger(left, right);
			} else if (right instanceof Double) {
				return timesDoubleDouble(left, right);
			} else if (right instanceof Bin) {
				return timesDoubleBin(left, right);
			} else if (right instanceof String) {
				return timesObjectString(left, right);
			} else if (right instanceof ArrayList<?>) {
				return powerArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Bin) {
			if (right instanceof Integer) {
				return timesBinInteger(left, right);
			} else if (right instanceof Double) {
				return timesBinDouble(left, right);
			} else if (right instanceof Bin) {
				return timesBinBin(left, right);
			} else if (right instanceof String) {
				return timesObjectString(left, right);
			} else if (right instanceof ArrayList<?>) {
				return powerArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof String) {
			if (right instanceof Integer) {
				return timesObjectString(right, left);
			} else if (right instanceof Double) {
				return timesObjectString(right, left);
			} else if (right instanceof Bin) {
				return timesObjectString(right, left);
			} else if (right instanceof ArrayList<?>) {
				return addStringArray(left, right);
			}

		} else if (left instanceof ArrayList<?>) {
			if (right instanceof ArrayList<?>)
				return powerArrayListArrayList(left, right, 1);
			else
				return powerBinaryArrayList(left, right, 1);
		}
		return null;
	}

	private Object sub(Object left, Object right) {
		if (left instanceof Integer) {
			if (right instanceof Integer) {
				return addIntegerInteger(left, -1 * (Integer) right);
			} else if (right instanceof Double) {
				return addIntegerDouble(left, -1 * (Double) right);
			} else if (right instanceof Bin) {
				return addIntegerBin(left, Bin.times(new Bin(-1), (Bin) right));
			} else if (right instanceof ArrayList<?>) {
				return addArrayBinaryList(left, right, -1);

			}
		} else if (left instanceof Double) {
			if (right instanceof Integer) {
				return addDoubleInteger(left, -1 * (Integer) right);
			} else if (right instanceof Double) {
				return addDoubleDouble(left, -1 * (Double) right);
			} else if (right instanceof Bin) {
				return addDoubleBin(left, Bin.times(new Bin(-1), (Bin) right));
			} else if (right instanceof ArrayList<?>) {

				return addArrayBinaryList(left, right, -1);
			}
		} else if (left instanceof Bin) {
			if (right instanceof Integer) {
				return addBinInteger(left, -1 * (Integer) right);
			} else if (right instanceof Double) {
				return addBinDouble(left, -1 * (Double) right);
			} else if (right instanceof Bin) {
				return addBinBin(left, Bin.times(new Bin(-1), (Bin) right));
			} else if (right instanceof ArrayList<?>) {
				return addArrayBinaryList(left, right, -1);
			}
		} else if (left instanceof ArrayList<?>) {
			if (right instanceof ArrayList<?>)
				return addArrayListArrayList(left, right, -1);
			else
				return addBinaryArrayList(left, right, -1);
		}

		return null;
	}

	private Object add(Object left, Object right) {
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
				return addArrayBinaryList(left, right, 1);
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
				return addArrayBinaryList(left, right, 1);
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

				return addArrayBinaryList(left, right, 1);
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
			} else {

				if (right != null)
					return left.toString() + right.toString();
				else
					return left.toString() + "null";
			}
		} else if (left instanceof ArrayList<?>) {
			if (right instanceof ArrayList<?>)
				return addArrayListArrayList(left, right, 1);
			else
				return addBinaryArrayList(left, right, 1);
		}
		return null;
	}

	private Object add(Object left, Object right, Expr left2, Expr right2) {
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
				return addArrayBinaryList(left, right, 1);
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
				return addArrayBinaryList(left, right, 1);
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

				return addArrayBinaryList(left, right, 1);
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
			} else {

				if (right instanceof Boolean)
					return left.toString() + right2.toString();
				else
					return left.toString() + right.toString();
			}
		} else if (left instanceof Boolean) {

			if (right instanceof String)
				return left2.toString() + right.toString();
		} else if (left instanceof ArrayList<?>) {
			if (right instanceof ArrayList<?>)
				return addArrayListArrayList(left, right, 1);
			else
				return addBinaryArrayList(left, right, 1);
		}
		return null;

	}

	private Object greaterthen(Object left, Object right) {
		if (left instanceof Integer) {
			if (right instanceof Integer) {
				return gtIntegerInteger(left, right);
			} else if (right instanceof Double) {
				return gtIntegerDouble(left, right);
			} else if (right instanceof Bin) {
				return gtIntegerBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				return gtArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Double) {
			if (right instanceof Integer) {
				return gtDoubleInteger(left, right);
			} else if (right instanceof Double) {
				return gtDoubleDouble(left, right);
			} else if (right instanceof Bin) {
				return gtDoubleBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				return gtArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Bin) {
			if (right instanceof Integer) {
				return gtBinInteger(left, right);
			} else if (right instanceof Double) {
				return gtBinDouble(left, right);
			} else if (right instanceof Bin) {
				return gtBinBin(left, right);
			} else if (right instanceof ArrayList<?>) {

				return gtArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof ArrayList<?>) {
			if (right instanceof ArrayList<?>)
				return gtArrayListArrayList(left, right, 1);
			else
				return gtBinaryArrayList(left, right, 1);
		}
		return null;
	}

	private Object greaterequalthen(Object left, Object right) {
		if (left instanceof Integer) {
			if (right instanceof Integer) {
				return gteIntegerInteger(left, right);
			} else if (right instanceof Double) {
				return gteIntegerDouble(left, right);
			} else if (right instanceof Bin) {
				return gteIntegerBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				return gteArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Double) {
			if (right instanceof Integer) {
				return gteDoubleInteger(left, right);
			} else if (right instanceof Double) {
				return gteDoubleDouble(left, right);
			} else if (right instanceof Bin) {
				return gteDoubleBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				return gteArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Bin) {
			if (right instanceof Integer) {
				return gteBinInteger(left, right);
			} else if (right instanceof Double) {
				return gteBinDouble(left, right);
			} else if (right instanceof Bin) {
				return gteBinBin(left, right);
			} else if (right instanceof ArrayList<?>) {

				return gteArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof ArrayList<?>) {
			if (right instanceof ArrayList<?>)
				return gteArrayListArrayList(left, right, 1);
			else
				return gteBinaryArrayList(left, right, 1);
		}
		return null;
	}

	private Object lessequalthen(Object left, Object right) {
		if (left instanceof Integer) {
			if (right instanceof Integer) {
				return lteIntegerInteger(left, right);
			} else if (right instanceof Double) {
				return lteIntegerDouble(left, right);
			} else if (right instanceof Bin) {
				return lteIntegerBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				return lteArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Double) {
			if (right instanceof Integer) {
				return lteDoubleInteger(left, right);
			} else if (right instanceof Double) {
				return lteDoubleDouble(left, right);
			} else if (right instanceof Bin) {
				return lteDoubleBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				return lteArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Bin) {
			if (right instanceof Integer) {
				return lteBinInteger(left, right);
			} else if (right instanceof Double) {
				return lteBinDouble(left, right);
			} else if (right instanceof Bin) {
				return lteBinBin(left, right);
			} else if (right instanceof ArrayList<?>) {

				return lteArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof ArrayList<?>) {
			if (right instanceof ArrayList<?>)
				return lteArrayListArrayList(left, right, 1);
			else
				return lteBinaryArrayList(left, right, 1);
		}
		return null;
	}

	private Object lessthen(Object left, Object right) {
		if (left instanceof Integer) {
			if (right instanceof Integer) {
				return ltIntegerInteger(left, right);
			} else if (right instanceof Double) {
				return ltIntegerDouble(left, right);
			} else if (right instanceof Bin) {
				return ltIntegerBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				return ltArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Double) {
			if (right instanceof Integer) {
				return ltDoubleInteger(left, right);
			} else if (right instanceof Double) {
				return ltDoubleDouble(left, right);
			} else if (right instanceof Bin) {
				return ltDoubleBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				return ltArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Bin) {
			if (right instanceof Integer) {
				return ltBinInteger(left, right);
			} else if (right instanceof Double) {
				return ltBinDouble(left, right);
			} else if (right instanceof Bin) {
				return ltBinBin(left, right);
			} else if (right instanceof ArrayList<?>) {

				return ltArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof ArrayList<?>) {
			if (right instanceof ArrayList<?>)
				return ltArrayListArrayList(left, right, 1);
			else
				return ltBinaryArrayList(left, right, 1);
		}
		return null;
	}

	private Object div(Object left, Object right) {
		if (left instanceof Integer) {
			if (right instanceof Integer) {
				return divIntegerInteger(left, right);
			} else if (right instanceof Double) {
				return divIntegerDouble(left, right);
			} else if (right instanceof Bin) {
				return divIntegerBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				return divArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Double) {
			if (right instanceof Integer) {
				return divDoubleInteger(left, right);
			} else if (right instanceof Double) {
				return divDoubleDouble(left, right);
			} else if (right instanceof Bin) {
				return divDoubleBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				return divArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof Bin) {
			if (right instanceof Integer) {
				return divBinInteger(left, right);
			} else if (right instanceof Double) {
				return divBinDouble(left, right);
			} else if (right instanceof Bin) {
				return divBinBin(left, right);
			} else if (right instanceof ArrayList<?>) {
				return divArrayBinaryList(left, right, 1);
			}
		} else if (left instanceof ArrayList<?>) {
			if (right instanceof ArrayList<?>)
				return divArrayListArrayList(left, right, 1);
			else
				return divBinaryArrayList(left, right, 1);
		}
		return null;
	}

	private ArrayList<?> addArrayBinaryList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		ArrayList<Object> arr2 = new ArrayList<>();

		if (left instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Integer) left + (i * (Integer) object));
				} else if (object instanceof Double) {
					arr2.add((Integer) left + (i * (Double) object));

				} else if (object instanceof Bin) {
					arr2.add(Bin.add(new Bin((Integer) left),
							new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(addArrayBinaryList(left, object, i));

				}
			}

		} else if (left instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Double) left + (i * (Integer) object));
				} else if (object instanceof Double) {
					arr2.add((Double) left + (i * (Double) object));

				} else if (object instanceof Bin) {
					arr2.add(Bin.add(new Bin(((Double) left).intValue()),
							new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(addArrayBinaryList(left, object, i));

				}
			}

		} else if (left instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(Bin.add(((Bin) left), new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

				} else if (object instanceof Double) {
					arr2.add(Bin.add(((Bin) left),
							new Bin(Bin.times(new Bin(i), new Bin(((Double) object).intValue())))));

				} else if (object instanceof Bin) {
					arr2.add(Bin.add(((Bin) left), new Bin(Bin.times(new Bin(i), (Bin) object))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(addArrayBinaryList(left, object, i));

				}
			}

		}
		return arr2;

	}

	private ArrayList<?> ltArrayBinaryList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		ArrayList<Object> arr2 = new ArrayList<>();

		if (left instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Integer) left < (i * (Integer) object));
				} else if (object instanceof Double) {
					arr2.add((Integer) left < (i * (Double) object));

				} else if (object instanceof Bin) {
					arr2.add((Integer) left < ((Bin) object).toInteger());

				} else if (object instanceof ArrayList<?>) {
					arr2.add(ltArrayBinaryList(left, object, i));

				}
			}

		} else if (left instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Double) left < (i * (Integer) object));
				} else if (object instanceof Double) {
					arr2.add((Double) left < (i * (Double) object));

				} else if (object instanceof Bin) {
					arr2.add(((Double) left) < ((Bin) object).toInteger());

				} else if (object instanceof ArrayList<?>) {
					arr2.add(ltArrayBinaryList(left, object, i));

				}
			}

		} else if (left instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(((Bin) left).toInteger() < Bin.times(new Bin(i), new Bin((Integer) object)));

				} else if (object instanceof Double) {
					arr2.add(((Bin) left).toInteger() < Bin.times(new Bin(i), new Bin(((Double) object).intValue())));

				} else if (object instanceof Bin) {
					arr2.add(((Bin) left).toInteger() < Bin.times(new Bin(i), (Bin) object));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(ltArrayBinaryList(left, object, i));

				}
			}

		}
		return arr2;

	}

	private ArrayList<?> gtArrayBinaryList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		ArrayList<Object> arr2 = new ArrayList<>();

		if (left instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Integer) left > (i * (Integer) object));
				} else if (object instanceof Double) {
					arr2.add((Integer) left > (i * (Double) object));

				} else if (object instanceof Bin) {
					arr2.add((Integer) left > ((Bin) object).toInteger());

				} else if (object instanceof ArrayList<?>) {
					arr2.add(gtArrayBinaryList(left, object, i));

				}
			}

		} else if (left instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Double) left > (i * (Integer) object));
				} else if (object instanceof Double) {
					arr2.add((Double) left > (i * (Double) object));

				} else if (object instanceof Bin) {
					arr2.add(((Double) left) > ((Bin) object).toInteger());

				} else if (object instanceof ArrayList<?>) {
					arr2.add(gtArrayBinaryList(left, object, i));

				}
			}

		} else if (left instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(((Bin) left).toInteger() > Bin.times(new Bin(i), new Bin((Integer) object)));

				} else if (object instanceof Double) {
					arr2.add(((Bin) left).toInteger() > Bin.times(new Bin(i), new Bin(((Double) object).intValue())));

				} else if (object instanceof Bin) {
					arr2.add(((Bin) left).toInteger() > Bin.times(new Bin(i), (Bin) object));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(gtArrayBinaryList(left, object, i));

				}
			}

		}
		return arr2;

	}

	private ArrayList<?> gteArrayBinaryList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		ArrayList<Object> arr2 = new ArrayList<>();

		if (left instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Integer) left >= (i * (Integer) object));
				} else if (object instanceof Double) {
					arr2.add((Integer) left >= (i * (Double) object));

				} else if (object instanceof Bin) {
					arr2.add((Integer) left >= ((Bin) object).toInteger());

				} else if (object instanceof ArrayList<?>) {
					arr2.add(gtArrayBinaryList(left, object, i));

				}
			}

		} else if (left instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Double) left >= (i * (Integer) object));
				} else if (object instanceof Double) {
					arr2.add((Double) left >= (i * (Double) object));

				} else if (object instanceof Bin) {
					arr2.add(((Double) left) >= ((Bin) object).toInteger());

				} else if (object instanceof ArrayList<?>) {
					arr2.add(gtArrayBinaryList(left, object, i));

				}
			}

		} else if (left instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(((Bin) left).toInteger() >= Bin.times(new Bin(i), new Bin((Integer) object)));

				} else if (object instanceof Double) {
					arr2.add(((Bin) left).toInteger() >= Bin.times(new Bin(i), new Bin(((Double) object).intValue())));

				} else if (object instanceof Bin) {
					arr2.add(((Bin) left).toInteger() >= Bin.times(new Bin(i), (Bin) object));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(gtArrayBinaryList(left, object, i));

				}
			}

		}
		return arr2;

	}

	private ArrayList<?> lteArrayBinaryList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		ArrayList<Object> arr2 = new ArrayList<>();

		if (left instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Integer) left <= (i * (Integer) object));
				} else if (object instanceof Double) {
					arr2.add((Integer) left <= (i * (Double) object));

				} else if (object instanceof Bin) {
					arr2.add((Integer) left <= ((Bin) object).toInteger());

				} else if (object instanceof ArrayList<?>) {
					arr2.add(ltArrayBinaryList(left, object, i));

				}
			}

		} else if (left instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Double) left <= (i * (Integer) object));
				} else if (object instanceof Double) {
					arr2.add((Double) left <= (i * (Double) object));

				} else if (object instanceof Bin) {
					arr2.add(((Double) left) <= ((Bin) object).toInteger());

				} else if (object instanceof ArrayList<?>) {
					arr2.add(ltArrayBinaryList(left, object, i));

				}
			}

		} else if (left instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(((Bin) left).toInteger() <= Bin.times(new Bin(i), new Bin((Integer) object)));

				} else if (object instanceof Double) {
					arr2.add(((Bin) left).toInteger() <= Bin.times(new Bin(i), new Bin(((Double) object).intValue())));

				} else if (object instanceof Bin) {
					arr2.add(((Bin) left).toInteger() <= Bin.times(new Bin(i), (Bin) object));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(ltArrayBinaryList(left, object, i));

				}
			}

		}
		return arr2;

	}

	private ArrayList<?> powerArrayBinaryList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		ArrayList<Object> arr2 = new ArrayList<>();

		if (left instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(
							Math.pow(((Integer) left).doubleValue(), ((Integer) (i * (Integer) object)).doubleValue()));
				} else if (object instanceof Double) {
					arr2.add(Math.pow(((Integer) left).doubleValue(), (Double) (i * (Double) object)));

				} else if (object instanceof Bin) {
					arr2.add(Math.pow((new Bin((Integer) left)).toDouble(),
							(new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))).toDouble()));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(powerArrayBinaryList(left, object, i));

				}
			}

		} else if (left instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Double) left + (i * (Integer) object));
				} else if (object instanceof Double) {
					arr2.add((Double) left + (i * (Double) object));

				} else if (object instanceof Bin) {
					arr2.add(Bin.add(new Bin(((Double) left).intValue()),
							new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(powerArrayBinaryList(left, object, i));

				}
			}

		} else if (left instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(Bin.add(((Bin) left), new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

				} else if (object instanceof Double) {
					arr2.add(Bin.add(((Bin) left),
							new Bin(Bin.times(new Bin(i), new Bin(((Double) object).intValue())))));

				} else if (object instanceof Bin) {
					arr2.add(Bin.add(((Bin) left), new Bin(Bin.times(new Bin(i), (Bin) object))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(powerArrayBinaryList(left, object, i));

				}
			}

		}
		return arr2;

	}

	private ArrayList<?> timesArrayBinaryList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		ArrayList<Object> arr2 = new ArrayList<>();

		if (left instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Integer) left * (i * (Integer) object));
				} else if (object instanceof Double) {
					arr2.add((Integer) left * (i * (Double) object));

				} else if (object instanceof Bin) {
					arr2.add(Bin.add(new Bin((Integer) left),
							new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(addArrayBinaryList(left, object, i));

				} else if (object instanceof String) {
					String str = "";

					for (int j = 0; j < ((Integer) left); j++) {
						str += object.toString();
					}
					arr2.add(str);
				}
			}
		} else if (left instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Double) left * (i * (Integer) object));
				} else if (object instanceof Double) {
					arr2.add((Double) left * (i * (Double) object));

				} else if (object instanceof Bin) {
					arr2.add(Bin.add(new Bin(((Double) left).intValue()),
							new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(addArrayBinaryList(left, object, i));

				} else if (object instanceof String) {
					String str = "";

					for (int j = 0; j < ((Double) left).intValue(); j++) {
						str += object.toString();
					}
					arr2.add(str);
				}
			}

		} else if (left instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(Bin.times(((Bin) left), new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

				} else if (object instanceof Double) {
					arr2.add(Bin.times(((Bin) left),
							new Bin(Bin.times(new Bin(i), new Bin(((Double) object).intValue())))));

				} else if (object instanceof Bin) {
					arr2.add(Bin.times(((Bin) left), new Bin(Bin.times(new Bin(i), (Bin) object))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(addArrayBinaryList(left, object, i));

				} else if (object instanceof String) {
					String str = "";

					for (int j = 0; j < ((Bin) left).toInteger(); j++) {
						str += object.toString();
					}
					arr2.add(str);
				}
			}

		}
		return arr2;

	}

	private ArrayList<?> divArrayBinaryList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		ArrayList<Object> arr2 = new ArrayList<>();

		if (left instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Integer) left / (i * (Integer) object));
				} else if (object instanceof Double) {
					arr2.add((Integer) left / (i * (Double) object));

				} else if (object instanceof Bin) {
					arr2.add(Bin.divide(new Bin((Integer) left),
							new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(divArrayBinaryList(left, object, i));

				}
			}
		} else if (left instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Double) left / (i * (Integer) object));
				} else if (object instanceof Double) {
					arr2.add((Double) left / (i * (Double) object));

				} else if (object instanceof Bin) {
					arr2.add(Bin.divide(new Bin(((Double) left).intValue()),
							new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(divArrayBinaryList(left, object, i));

				}
			}

		} else if (left instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(Bin.divide(((Bin) left), new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

				} else if (object instanceof Double) {
					arr2.add(Bin.divide(((Bin) left),
							new Bin(Bin.times(new Bin(i), new Bin(((Double) object).intValue())))));

				} else if (object instanceof Bin) {
					arr2.add(Bin.divide(((Bin) left), new Bin(Bin.times(new Bin(i), (Bin) object))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(divArrayBinaryList(left, object, i));

				}
			}

		}
		return arr2;

	}

	private ArrayList<?> modArrayBinaryList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		ArrayList<Object> arr2 = new ArrayList<>();

		if (left instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Integer) left % (i * (Integer) object));
				} else if (object instanceof Double) {
					arr2.add((Integer) left % (i * (Double) object));

				} else if (object instanceof Bin) {
					arr2.add((Integer) left % Bin.times(new Bin(i), new Bin((Integer) object)));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(modArrayBinaryList(left, object, i));

				}
			}
		} else if (left instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Double) left % (i * (Integer) object));
				} else if (object instanceof Double) {
					arr2.add((Double) left % (i * (Double) object));

				} else if (object instanceof Bin) {
					arr2.add(((Double) left).intValue() % Bin.times(new Bin(i), new Bin((Integer) object)));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(modArrayBinaryList(left, object, i));

				}
			}

		} else if (left instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(((Bin) left).toInteger() % Bin.times(new Bin(i), new Bin((Integer) object)));

				} else if (object instanceof Double) {
					arr2.add(((Bin) left).toInteger() % Bin.times(new Bin(i), new Bin(((Double) object).intValue())));

				} else if (object instanceof Bin) {
					arr2.add(((Bin) left).toInteger() % Bin.times(new Bin(i), (Bin) object));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(modArrayBinaryList(left, object, i));

				}
			}

		}
		return arr2;

	}

	private ArrayList<?> addBinaryArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) left;
		ArrayList<Object> arr2 = new ArrayList<>();

		if (right instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Integer) object + (i * (Integer) right));
				} else if (object instanceof Double) {
					arr2.add((Integer) object + (i * (Double) right));

				} else if (object instanceof Bin) {
					arr2.add(Bin.add(new Bin((Integer) object),
							new Bin(Bin.times(new Bin(i), new Bin((Integer) right)))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(addBinaryArrayList(object, right, i));

				}
			}

		} else if (right instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Double) object + (i * (Integer) right));
				} else if (object instanceof Double) {
					arr2.add((Double) object + (i * (Double) right));

				} else if (object instanceof Bin) {
					arr2.add(Bin.add(new Bin(((Double) object).intValue()),
							new Bin(Bin.times(new Bin(i), new Bin((Integer) right)))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(addBinaryArrayList(object, right, i));

				}
			}

		} else if (right instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(Bin.add(((Bin) object), new Bin(Bin.times(new Bin(i), new Bin((Integer) right)))));

				} else if (object instanceof Double) {
					arr2.add(Bin.add(((Bin) object),
							new Bin(Bin.times(new Bin(i), new Bin(((Double) right).intValue())))));

				} else if (object instanceof Bin) {
					arr2.add(Bin.add(((Bin) object), new Bin(Bin.times(new Bin(i), (Bin) right))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(addBinaryArrayList(object, right, i));

				}
			}

		} else if (right instanceof String) {
			String str = "";
			str += "[";
			for (int j = 0; j < arr.size(); j++) {
				if (arr.get(j) instanceof Integer) {
					if (j == arr.size() - 1)
						str += (Integer) arr.get(j);
					else
						str += (Integer) arr.get(j) + ", ";

				} else if (arr.get(j) instanceof Double) {
					if (j == arr.size() - 1)
						str += (Double) arr.get(j);
					else
						str += (Double) arr.get(j) + ", ";
				} else if (arr.get(j) instanceof Boolean) {
					if (j == arr.size() - 1)
						str += (Boolean) arr.get(j);
					else
						str += (Boolean) arr.get(j) + ", ";
				} else if (arr.get(j) instanceof Bin) {
					if (j == arr.size() - 1)
						str += ((Bin) arr.get(j)).toString();
					else
						str += ((Bin) arr.get(j)).toString() + ", ";
				} else if (arr.get(j) instanceof ArrayList<?>) {
					if (j == arr.size() - 1)
						str += arr.get(j).toString();
					else
						str += arr.get(j).toString() + ", ";
				}

			}
			str += "]";
			str = str + right;
			arr2.add(str);
		}
		return arr2;

	}

	private ArrayList<?> gtBinaryArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) left;
		ArrayList<Object> arr2 = new ArrayList<>();

		if (right instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Integer) object > (i * (Integer) right));
				} else if (object instanceof Double) {
					arr2.add((Integer) object > (i * (Double) right));

				} else if (object instanceof Bin) {
					arr2.add((Integer) object > Bin.times(new Bin(i), new Bin((Integer) right)));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(gtBinaryArrayList(object, right, i));

				}
			}

		} else if (right instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Double) object > (i * (Integer) right));
				} else if (object instanceof Double) {
					arr2.add((Double) object > (i * (Double) right));

				} else if (object instanceof Bin) {
					arr2.add(((Double) object) > Bin.times(new Bin(i), new Bin((Integer) right)));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(gtBinaryArrayList(object, right, i));

				}
			}

		} else if (right instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(((Bin) object).toInteger() > Bin.times(new Bin(i), new Bin((Integer) right)));

				} else if (object instanceof Double) {
					arr2.add(((Bin) object).toInteger() > Bin.times(new Bin(i), new Bin(((Double) right).intValue())));

				} else if (object instanceof Bin) {
					arr2.add(((Bin) object).toInteger() > Bin.times(new Bin(i), (Bin) right));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(gtBinaryArrayList(object, right, i));

				}
			}

		}
		return arr2;

	}

	private ArrayList<?> gteBinaryArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) left;
		ArrayList<Object> arr2 = new ArrayList<>();

		if (right instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Integer) object >= (i * (Integer) right));
				} else if (object instanceof Double) {
					arr2.add((Integer) object >= (i * (Double) right));

				} else if (object instanceof Bin) {
					arr2.add((Integer) object >= Bin.times(new Bin(i), new Bin((Integer) right)));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(gteBinaryArrayList(object, right, i));

				}
			}

		} else if (right instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Double) object >= (i * (Integer) right));
				} else if (object instanceof Double) {
					arr2.add((Double) object >= (i * (Double) right));

				} else if (object instanceof Bin) {
					arr2.add(((Double) object) >= Bin.times(new Bin(i), new Bin((Integer) right)));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(gteBinaryArrayList(object, right, i));

				}
			}

		} else if (right instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(((Bin) object).toInteger() >= Bin.times(new Bin(i), new Bin((Integer) right)));

				} else if (object instanceof Double) {
					arr2.add(((Bin) object).toInteger() >= Bin.times(new Bin(i), new Bin(((Double) right).intValue())));

				} else if (object instanceof Bin) {
					arr2.add(((Bin) object).toInteger() >= Bin.times(new Bin(i), (Bin) right));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(gteBinaryArrayList(object, right, i));

				}
			}

		}
		return arr2;

	}

	private ArrayList<?> lteBinaryArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) left;
		ArrayList<Object> arr2 = new ArrayList<>();

		if (right instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Integer) object <= (i * (Integer) right));
				} else if (object instanceof Double) {
					arr2.add((Integer) object <= (i * (Double) right));

				} else if (object instanceof Bin) {
					arr2.add((Integer) object <= Bin.times(new Bin(i), new Bin((Integer) right)));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(lteBinaryArrayList(object, right, i));

				}
			}

		} else if (right instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Double) object <= (i * (Integer) right));
				} else if (object instanceof Double) {
					arr2.add((Double) object <= (i * (Double) right));

				} else if (object instanceof Bin) {
					arr2.add(((Double) object) <= Bin.times(new Bin(i), new Bin((Integer) right)));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(lteBinaryArrayList(object, right, i));

				}
			}

		} else if (right instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(((Bin) object).toInteger() <= Bin.times(new Bin(i), new Bin((Integer) right)));

				} else if (object instanceof Double) {
					arr2.add(((Bin) object).toInteger() <= Bin.times(new Bin(i), new Bin(((Double) right).intValue())));

				} else if (object instanceof Bin) {
					arr2.add(((Bin) object).toInteger() <= Bin.times(new Bin(i), (Bin) right));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(lteBinaryArrayList(object, right, i));

				}
			}

		}
		return arr2;

	}

	private ArrayList<?> ltBinaryArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) left;
		ArrayList<Object> arr2 = new ArrayList<>();

		if (right instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Integer) object < (i * (Integer) right));
				} else if (object instanceof Double) {
					arr2.add((Integer) object < (i * (Double) right));

				} else if (object instanceof Bin) {
					arr2.add((Integer) object < Bin.times(new Bin(i), new Bin((Integer) right)));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(ltBinaryArrayList(object, right, i));

				}
			}

		} else if (right instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Double) object < (i * (Integer) right));
				} else if (object instanceof Double) {
					arr2.add((Double) object < (i * (Double) right));

				} else if (object instanceof Bin) {
					arr2.add(((Double) object) < Bin.times(new Bin(i), new Bin((Integer) right)));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(ltBinaryArrayList(object, right, i));

				}
			}

		} else if (right instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(((Bin) object).toInteger() < Bin.times(new Bin(i), new Bin((Integer) right)));

				} else if (object instanceof Double) {
					arr2.add(((Bin) object).toInteger() < Bin.times(new Bin(i), new Bin(((Double) right).intValue())));

				} else if (object instanceof Bin) {
					arr2.add(((Bin) object).toInteger() < Bin.times(new Bin(i), (Bin) right));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(ltBinaryArrayList(object, right, i));

				}
			}

		}
		return arr2;

	}

	private ArrayList<?> timesBinaryArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) left;
		ArrayList<Object> arr2 = new ArrayList<>();

		if (right instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Integer) object * (i * (Integer) right));
				} else if (object instanceof Double) {
					arr2.add((Integer) object * (i * (Double) right));

				} else if (object instanceof Bin) {
					arr2.add(Bin.add(new Bin((Integer) object),
							new Bin(Bin.times(new Bin(i), new Bin((Integer) right)))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(timesBinaryArrayList(object, right, i));

				} else if (object instanceof String) {
					String str = "";

					for (int j = 0; j < ((Integer) right); j++) {
						str += object.toString();
					}
					arr2.add(str);
				}
			}

		} else if (right instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Double) object * (i * (Integer) right));
				} else if (object instanceof Double) {
					arr2.add((Double) object * (i * (Double) right));

				} else if (object instanceof Bin) {
					arr2.add(Bin.add(new Bin(((Double) object).intValue()),
							new Bin(Bin.times(new Bin(i), new Bin((Integer) right)))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(timesBinaryArrayList(object, right, i));

				} else if (object instanceof String) {
					String str = "";

					for (int j = 0; j < ((Double) right).intValue(); j++) {
						str += object.toString();
					}
					arr2.add(str);
				}
			}

		} else if (right instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(Bin.times(((Bin) object), new Bin(Bin.times(new Bin(i), new Bin((Integer) right)))));

				} else if (object instanceof Double) {
					arr2.add(Bin.times(((Bin) object),
							new Bin(Bin.times(new Bin(i), new Bin(((Double) right).intValue())))));

				} else if (object instanceof Bin) {
					arr2.add(Bin.times(((Bin) object), new Bin(Bin.times(new Bin(i), (Bin) right))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(timesBinaryArrayList(object, right, i));

				} else if (object instanceof String) {
					String str = "";

					for (int j = 0; j < ((Bin) right).toInteger(); j++) {
						str += object.toString();
					}
					arr2.add(str);
				}
			}

		} else if (right instanceof String) {
			for (Object object : arr) {
				if (object instanceof Integer) {
					String str = "";

					for (int j = 0; j < ((Integer) object); j++) {
						str += right.toString();
					}
					arr2.add(str);

				} else if (object instanceof Double) {
					String str = "";

					for (int j = 0; j < ((Double) object).intValue(); j++) {
						str += right.toString();
					}
					arr2.add(str);

				} else if (object instanceof Bin) {
					String str = "";

					for (int j = 0; j < ((Bin) object).toInteger(); j++) {
						str += right.toString();
					}
					arr2.add(str);

				} else if (object instanceof ArrayList<?>) {
					arr2.add(timesBinaryArrayList(object, right, i));

				} else if (object instanceof String) {
					String str = "";

					str += object.toString() + right.toString();

					arr2.add(str);
				}
			}

		}
		return arr2;

	}

	private ArrayList<?> powerBinaryArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) left;
		ArrayList<Object> arr2 = new ArrayList<>();

		if (right instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(Math.pow(((Integer) object).doubleValue(),
							((Integer) (i * (Integer) right)).doubleValue()));
				} else if (object instanceof Double) {
					arr2.add(Math.pow((Double) object, (Integer) (i * (Integer) right)));

				} else if (object instanceof Bin) {
					arr2.add(Math.pow((Double) object,
							(new Bin(Bin.times(new Bin(i), new Bin((Integer) right)))).toDouble()));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(powerBinaryArrayList(object, right, i));

				}
			}

		} else if (right instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(Math.pow((Double) object, ((Integer) (i * (Integer) right)).doubleValue()));
				} else if (object instanceof Double) {
					arr2.add(Math.pow((Double) object, (i * (Double) right)));

				} else if (object instanceof Bin) {
					arr2.add(Math.pow(((Bin) object).toDouble(), ((Double) (i * (Double) right))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(powerBinaryArrayList(object, right, i));

				}
			}

		} else if (right instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(Math.pow(((Bin) object).toDouble(),
							(new Bin(Bin.times(new Bin(i), new Bin((Integer) right)))).toDouble()));

				} else if (object instanceof Double) {
					arr2.add(Math.pow(((Bin) object).toDouble(),
							(new Bin(Bin.times(new Bin(i), new Bin(((Double) right).intValue())))).toDouble()));

				} else if (object instanceof Bin) {
					arr2.add(Math.pow(((Bin) object).toDouble(),
							(new Bin(Bin.times(new Bin(i), (Bin) right))).toDouble()));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(powerBinaryArrayList(object, right, i));

				}
			}

		}
		return arr2;

	}

	private ArrayList<?> modBinaryArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) left;
		ArrayList<Object> arr2 = new ArrayList<>();

		if (right instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Integer) object % (i * (Integer) right));
				} else if (object instanceof Double) {
					arr2.add((Double) object % (i * (Integer) right));

				} else if (object instanceof Bin) {
					arr2.add(((Bin) object).toInteger() % Bin.times(new Bin(i), new Bin((Integer) right)));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(modBinaryArrayList(object, right, i));

				}
			}

		} else if (right instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Double) object % (i * (Integer) right));
				} else if (object instanceof Double) {
					arr2.add((Double) object % (i * (Double) right));

				} else if (object instanceof Bin) {
					arr2.add(((Double) object) % Bin.times(new Bin(i), new Bin((Integer) right)));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(modBinaryArrayList(object, right, i));

				}
			}

		} else if (right instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(((Bin) object).toInteger() % Bin.times(new Bin(i), new Bin((Integer) right)));

				} else if (object instanceof Double) {
					arr2.add(((Bin) object).toInteger() % Bin.times(new Bin(i), new Bin(((Double) right).intValue())));

				} else if (object instanceof Bin) {
					arr2.add(((Bin) object).toInteger() % Bin.times(new Bin(i), (Bin) right));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(modBinaryArrayList(object, right, i));

				}
			}

		}
		return arr2;

	}

	private ArrayList<?> divBinaryArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) left;
		ArrayList<Object> arr2 = new ArrayList<>();

		if (right instanceof Integer) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Integer) object / (i * (Integer) right));
				} else if (object instanceof Double) {
					arr2.add((Integer) object / (i * (Double) right));

				} else if (object instanceof Bin) {
					arr2.add(Bin.divide(new Bin((Integer) object),
							new Bin(Bin.times(new Bin(i), new Bin((Integer) right)))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(divBinaryArrayList(object, right, i));

				}
			}

		} else if (right instanceof Double) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add((Double) object / (i * (Integer) right));
				} else if (object instanceof Double) {
					arr2.add((Double) object / (i * (Double) right));

				} else if (object instanceof Bin) {
					arr2.add(Bin.divide(new Bin(((Double) object).intValue()),
							new Bin(Bin.times(new Bin(i), new Bin((Integer) right)))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(divBinaryArrayList(object, right, i));

				}
			}

		} else if (right instanceof Bin) {
			for (Object object : arr) {
				if (object instanceof Integer) {

					arr2.add(Bin.divide(((Bin) object), new Bin(Bin.times(new Bin(i), new Bin((Integer) right)))));

				} else if (object instanceof Double) {
					arr2.add(Bin.divide(((Bin) object),
							new Bin(Bin.times(new Bin(i), new Bin(((Double) right).intValue())))));

				} else if (object instanceof Bin) {
					arr2.add(Bin.divide(((Bin) object), new Bin(Bin.times(new Bin(i), (Bin) right))));

				} else if (object instanceof ArrayList<?>) {
					arr2.add(divBinaryArrayList(object, right, i));

				}
			}

		}
		return arr2;

	}

	private ArrayList<?> addArrayListArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		ArrayList<?> arr1 = (ArrayList<?>) left;
		ArrayList<Object> arr2 = new ArrayList<>();
		for (Object object1 : arr1) {
			if (object1 instanceof Integer) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Integer) object1 + (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Integer) object1 + (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add(Bin.add(new Bin((Integer) object1),
								new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(addBinaryArrayList(object1, object, i));

					}
				}

			} else if (object1 instanceof Double) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Double) object1 + (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Double) object1 + (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add(Bin.add(new Bin(((Double) object1).intValue()),
								new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(addBinaryArrayList(object1, object, i));

					}
				}

			} else if (object1 instanceof Bin) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add(Bin.add(((Bin) object1), new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

					} else if (object instanceof Double) {
						arr2.add(Bin.add(((Bin) object1),
								new Bin(Bin.times(new Bin(i), new Bin(((Double) object).intValue())))));

					} else if (object instanceof Bin) {
						arr2.add(Bin.add(((Bin) object1), new Bin(Bin.times(new Bin(i), (Bin) object))));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(addBinaryArrayList(object1, object, i));

					}
				}

			} else if (object1 instanceof ArrayList<?>) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Double) object1 + (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Double) object1 + (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add(Bin.add(new Bin(((Double) object1).intValue()),
								new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

					} else if (object instanceof ArrayList<?>) {

						ArrayList<?> temparr0 = ((ArrayList<?>) object1);
						ArrayList<?> temparr1 = ((ArrayList<?>) object);

						for (Object object2 : temparr0) {
							for (Object object3 : temparr1) {

								arr2.add(addBinaryArrayList(object2, object3, i));
							}
						}

					}
				}

			}
		}
		return arr2;

	}

	private ArrayList<?> gtArrayListArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		ArrayList<?> arr1 = (ArrayList<?>) left;
		ArrayList<Object> arr2 = new ArrayList<>();
		for (Object object1 : arr1) {
			if (object1 instanceof Integer) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Integer) object1 > (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Integer) object1 > (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add((Integer) object1 > Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(gtBinaryArrayList(object1, object, i));

					}
				}

			} else if (object1 instanceof Double) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Double) object1 > (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Double) object1 > (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add(((Double) object1) > Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(gtBinaryArrayList(object1, object, i));

					}
				}

			} else if (object1 instanceof Bin) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add(((Bin) object1).toInteger() > Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof Double) {
						arr2.add(((Bin) object1).toInteger() > Bin.times(new Bin(i),
								new Bin(((Double) object).intValue())));

					} else if (object instanceof Bin) {
						arr2.add(((Bin) object1).toInteger() > Bin.times(new Bin(i), (Bin) object));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(gtBinaryArrayList(object1, object, i));

					}
				}

			} else if (object1 instanceof ArrayList<?>) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Double) object1 > (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Double) object1 > (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add(((Double) object1).intValue() > Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof ArrayList<?>) {

						ArrayList<?> temparr0 = ((ArrayList<?>) object1);
						ArrayList<?> temparr1 = ((ArrayList<?>) object);

						for (Object object2 : temparr0) {
							for (Object object3 : temparr1) {

								arr2.add(gtBinaryArrayList(object2, object3, i));
							}
						}

					}
				}

			}
		}
		return arr2;

	}

	private ArrayList<?> gteArrayListArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		ArrayList<?> arr1 = (ArrayList<?>) left;
		ArrayList<Object> arr2 = new ArrayList<>();
		for (Object object1 : arr1) {
			if (object1 instanceof Integer) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Integer) object1 >= (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Integer) object1 >= (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add((Integer) object1 >= Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(gteBinaryArrayList(object1, object, i));

					}
				}

			} else if (object1 instanceof Double) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Double) object1 >= (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Double) object1 >= (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add(((Double) object1) >= Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(gteBinaryArrayList(object1, object, i));

					}
				}

			} else if (object1 instanceof Bin) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add(((Bin) object1).toInteger() >= Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof Double) {
						arr2.add(((Bin) object1).toInteger() >= Bin.times(new Bin(i),
								new Bin(((Double) object).intValue())));

					} else if (object instanceof Bin) {
						arr2.add(((Bin) object1).toInteger() >= Bin.times(new Bin(i), (Bin) object));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(gteBinaryArrayList(object1, object, i));

					}
				}

			} else if (object1 instanceof ArrayList<?>) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Double) object1 >= (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Double) object1 >= (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add(((Double) object1).intValue() >= Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof ArrayList<?>) {

						ArrayList<?> temparr0 = ((ArrayList<?>) object1);
						ArrayList<?> temparr1 = ((ArrayList<?>) object);

						for (Object object2 : temparr0) {
							for (Object object3 : temparr1) {

								arr2.add(gteBinaryArrayList(object2, object3, i));
							}
						}

					}
				}

			}
		}
		return arr2;

	}

	private ArrayList<?> lteArrayListArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		ArrayList<?> arr1 = (ArrayList<?>) left;
		ArrayList<Object> arr2 = new ArrayList<>();
		for (Object object1 : arr1) {
			if (object1 instanceof Integer) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Integer) object1 <= (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Integer) object1 <= (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add((Integer) object1 <= Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(lteBinaryArrayList(object1, object, i));

					}
				}

			} else if (object1 instanceof Double) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Double) object1 <= (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Double) object1 <= (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add(((Double) object1) <= Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(lteBinaryArrayList(object1, object, i));

					}
				}

			} else if (object1 instanceof Bin) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add(((Bin) object1).toInteger() <= Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof Double) {
						arr2.add(((Bin) object1).toInteger() <= Bin.times(new Bin(i),
								new Bin(((Double) object).intValue())));

					} else if (object instanceof Bin) {
						arr2.add(((Bin) object1).toInteger() <= Bin.times(new Bin(i), (Bin) object));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(lteBinaryArrayList(object1, object, i));

					}
				}

			} else if (object1 instanceof ArrayList<?>) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Double) object1 <= (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Double) object1 <= (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add(((Double) object1).intValue() <= Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof ArrayList<?>) {

						ArrayList<?> temparr0 = ((ArrayList<?>) object1);
						ArrayList<?> temparr1 = ((ArrayList<?>) object);

						for (Object object2 : temparr0) {
							for (Object object3 : temparr1) {

								arr2.add(lteBinaryArrayList(object2, object3, i));
							}
						}

					}
				}

			}
		}
		return arr2;

	}

	private ArrayList<?> ltArrayListArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		ArrayList<?> arr1 = (ArrayList<?>) left;
		ArrayList<Object> arr2 = new ArrayList<>();
		for (Object object1 : arr1) {
			if (object1 instanceof Integer) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Integer) object1 < (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Integer) object1 < (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add((Integer) object1 < Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(ltBinaryArrayList(object1, object, i));

					}
				}

			} else if (object1 instanceof Double) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Double) object1 < (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Double) object1 < (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add(((Double) object1) < Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(ltBinaryArrayList(object1, object, i));

					}
				}

			} else if (object1 instanceof Bin) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add(((Bin) object1).toInteger() < Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof Double) {
						arr2.add(((Bin) object1).toInteger() < Bin.times(new Bin(i),
								new Bin(((Double) object).intValue())));

					} else if (object instanceof Bin) {
						arr2.add(((Bin) object1).toInteger() < Bin.times(new Bin(i), (Bin) object));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(ltBinaryArrayList(object1, object, i));

					}
				}

			} else if (object1 instanceof ArrayList<?>) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Double) object1 < (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Double) object1 < (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add(((Double) object1) < Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof ArrayList<?>) {

						ArrayList<?> temparr0 = ((ArrayList<?>) object1);
						ArrayList<?> temparr1 = ((ArrayList<?>) object);

						for (Object object2 : temparr0) {
							for (Object object3 : temparr1) {

								arr2.add(ltBinaryArrayList(object2, object3, i));
							}
						}

					}
				}

			}
		}
		return arr2;

	}

	private ArrayList<?> timesArrayListArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		ArrayList<?> arr1 = (ArrayList<?>) left;
		ArrayList<Object> arr2 = new ArrayList<>();
		for (Object object1 : arr1) {
			if (object1 instanceof Integer) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Integer) object1 * (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Integer) object1 * (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add(Bin.times(new Bin((Integer) object1),
								new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(timesArrayBinaryList(object1, object, i));

					} else if (object instanceof String) {
						String str = "";

						for (int j = 0; j < ((Integer) object1); j++) {
							str += object.toString();
						}
						arr2.add(str);
					}
				}

			} else if (object1 instanceof Double) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Double) object1 * (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Double) object1 * (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add(Bin.times(new Bin(((Double) object1).intValue()),
								new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(timesArrayBinaryList(object1, object, i));

					} else if (object instanceof String) {
						String str = "";

						for (int j = 0; j < ((Double) object1).intValue(); j++) {
							str += object.toString();
						}
						arr2.add(str);
					}
				}

			} else if (object1 instanceof Bin) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add(Bin.times(((Bin) object1), new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

					} else if (object instanceof Double) {
						arr2.add(Bin.times(((Bin) object1),
								new Bin(Bin.times(new Bin(i), new Bin(((Double) object).intValue())))));

					} else if (object instanceof Bin) {
						arr2.add(Bin.times(((Bin) object1), new Bin(Bin.times(new Bin(i), (Bin) object))));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(timesArrayBinaryList(object1, object, i));

					} else if (object instanceof String) {
						String str = "";

						for (int j = 0; j < ((Bin) object1).toInteger(); j++) {
							str += object.toString();
						}
						arr2.add(str);
					}
				}

			} else if (object1 instanceof ArrayList<?>) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add(timesBinaryArrayList(object1, object, i));
					} else if (object instanceof Double) {
						arr2.add(timesBinaryArrayList(object1, object, i));

					} else if (object instanceof Bin) {
						arr2.add(timesBinaryArrayList(object1, object, i));
					} else if (object instanceof ArrayList<?>) {

						ArrayList<?> temparr0 = ((ArrayList<?>) object1);
						ArrayList<?> temparr1 = ((ArrayList<?>) object);

						for (Object object2 : temparr0) {
							for (Object object3 : temparr1) {
								if (object2 instanceof ArrayList<?> && !(object3 instanceof ArrayList<?>))
									arr2.add(timesBinaryArrayList(object2, object3, i));
								else if (object3 instanceof ArrayList<?> && !(object2 instanceof ArrayList<?>))
									arr2.add(timesArrayBinaryList(object2, object3, i));
								else if (object3 instanceof ArrayList<?> && (object2 instanceof ArrayList<?>))
									arr2.add(timesArrayListArrayList(object2, object3, i));
								else
									arr2.add(times(object2, object3));

							}
						}

					} else if (object instanceof String) {
						arr2.add(timesBinaryArrayList(object1, object, i));
					}
				}

			}
		}
		return arr2;

	}

	private ArrayList<?> powerArrayListArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		ArrayList<?> arr1 = (ArrayList<?>) left;
		ArrayList<Object> arr2 = new ArrayList<>();
		for (Object object1 : arr1) {
			if (object1 instanceof Integer) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add(Math.pow(((Integer) object1).doubleValue(),
								((Integer) (i * (Integer) object)).doubleValue()));
					} else if (object instanceof Double) {
						arr2.add(Math.pow(((Integer) object1).doubleValue(), (Double) (i * (Double) object)));

					} else if (object instanceof Bin) {
						arr2.add(Math.pow((new Bin((Integer) object1)).toDouble(),
								(new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))).toDouble()));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(powerArrayBinaryList(object1, object, i));

					}
				}

			} else if (object1 instanceof Double) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add(Math.pow(((Double) object1), ((Integer) (i * (Integer) object)).doubleValue()));
					} else if (object instanceof Double) {
						arr2.add(Math.pow(((Double) object1), (Double) (i * (Double) object)));

					} else if (object instanceof Bin) {
						arr2.add(Math.pow(((Double) object1),
								(new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))).toDouble()));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(powerArrayBinaryList(object1, object, i));

					}
				}

			} else if (object1 instanceof Bin) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add(Math.pow(((Bin) left).toDouble(),
								(new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))).toDouble()));

					} else if (object instanceof Double) {
						arr2.add(Math.pow(((Bin) left).toDouble(),
								(new Bin(Bin.times(new Bin(i), new Bin(((Double) object).intValue())))).toDouble()));

					} else if (object instanceof Bin) {
						arr2.add(Math.pow(((Bin) left).toDouble(),
								(new Bin(Bin.times(new Bin(i), (Bin) object))).toDouble()));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(powerArrayBinaryList(left, object, i));

					}
				}

			} else if (object1 instanceof ArrayList<?>) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add(timesBinaryArrayList(object1, object, i));
					} else if (object instanceof Double) {
						arr2.add(timesBinaryArrayList(object1, object, i));

					} else if (object instanceof Bin) {
						arr2.add(timesBinaryArrayList(object1, object, i));
					} else if (object instanceof ArrayList<?>) {

						ArrayList<?> temparr0 = ((ArrayList<?>) object1);
						ArrayList<?> temparr1 = ((ArrayList<?>) object);

						for (Object object2 : temparr0) {
							for (Object object3 : temparr1) {
								if (object2 instanceof ArrayList<?> && !(object3 instanceof ArrayList<?>))
									arr2.add(powerBinaryArrayList(object2, object3, i));
								else if (object3 instanceof ArrayList<?> && !(object2 instanceof ArrayList<?>))
									arr2.add(powerArrayBinaryList(object2, object3, i));
								else if (object3 instanceof ArrayList<?> && (object2 instanceof ArrayList<?>))
									arr2.add(powerArrayListArrayList(object2, object3, i));
								else
									arr2.add(power(object2, object3));

							}
						}

					}
				}

			}
		}
		return arr2;

	}

	private ArrayList<?> modArrayListArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		ArrayList<?> arr1 = (ArrayList<?>) left;
		ArrayList<Object> arr2 = new ArrayList<>();
		for (Object object1 : arr1) {
			if (object1 instanceof Integer) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Integer) object1 % (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Integer) object1 % (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add(((Integer) object1) % Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(modArrayBinaryList(object1, object, i));

					}
				}

			} else if (object1 instanceof Double) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Double) object1 % (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Double) object1 % (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add(((Double) object1) % Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(modArrayBinaryList(object1, object, i));

					}
				}

			} else if (object1 instanceof Bin) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add(((Bin) object1).toInteger() % Bin.times(new Bin(i), new Bin((Integer) object)));

					} else if (object instanceof Double) {
						arr2.add(((Bin) object1).toInteger()
								% Bin.times(new Bin(i), new Bin(((Double) object).intValue())));

					} else if (object instanceof Bin) {
						arr2.add(((Bin) object1).toInteger() % Bin.times(new Bin(i), (Bin) object));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(modArrayBinaryList(object1, object, i));

					}
				}

			} else if (object1 instanceof ArrayList<?>) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add(modBinaryArrayList(object1, object, i));
					} else if (object instanceof Double) {
						arr2.add(modBinaryArrayList(object1, object, i));

					} else if (object instanceof Bin) {
						arr2.add(modBinaryArrayList(object1, object, i));
					} else if (object instanceof ArrayList<?>) {

						ArrayList<?> temparr0 = ((ArrayList<?>) object1);
						ArrayList<?> temparr1 = ((ArrayList<?>) object);

						for (Object object2 : temparr0) {
							for (Object object3 : temparr1) {

								if (object2 instanceof ArrayList<?> && !(object3 instanceof ArrayList<?>))
									arr2.add(modBinaryArrayList(object2, object3, i));
								else if (object3 instanceof ArrayList<?> && !(object2 instanceof ArrayList<?>))
									arr2.add(modBinaryArrayList(object2, object3, i));
								else if (object3 instanceof ArrayList<?> && (object2 instanceof ArrayList<?>))
									arr2.add(modBinaryArrayList(object2, object3, i));
								else
									arr2.add(mod(object2, object3));

							}
						}

					}
				}

			}
		}
		return arr2;

	}

	private ArrayList<?> divArrayListArrayList(Object left, Object right, int i) {
		ArrayList<?> arr = (ArrayList<?>) right;
		ArrayList<?> arr1 = (ArrayList<?>) left;
		ArrayList<Object> arr2 = new ArrayList<>();
		for (Object object1 : arr1) {
			if (object1 instanceof Integer) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Integer) object1 / (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Integer) object1 / (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add(Bin.divide(new Bin((Integer) object1),
								new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(divArrayBinaryList(object1, object, i));

					}
				}

			} else if (object1 instanceof Double) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add((Double) object1 / (i * (Integer) object));
					} else if (object instanceof Double) {
						arr2.add((Double) object1 / (i * (Double) object));

					} else if (object instanceof Bin) {
						arr2.add(Bin.divide(new Bin(((Double) object1).intValue()),
								new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(divArrayBinaryList(object1, object, i));

					}
				}

			} else if (object1 instanceof Bin) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add(
								Bin.divide(((Bin) object1), new Bin(Bin.times(new Bin(i), new Bin((Integer) object)))));

					} else if (object instanceof Double) {
						arr2.add(Bin.divide(((Bin) object1),
								new Bin(Bin.times(new Bin(i), new Bin(((Double) object).intValue())))));

					} else if (object instanceof Bin) {
						arr2.add(Bin.divide(((Bin) object1), new Bin(Bin.times(new Bin(i), (Bin) object))));

					} else if (object instanceof ArrayList<?>) {
						arr2.add(divArrayBinaryList(object1, object, i));

					}
				}

			} else if (object1 instanceof ArrayList<?>) {
				for (Object object : arr) {
					if (object instanceof Integer) {

						arr2.add(divBinaryArrayList(object1, object, i));
					} else if (object instanceof Double) {
						arr2.add(divBinaryArrayList(object1, object, i));

					} else if (object instanceof Bin) {
						arr2.add(divBinaryArrayList(object1, object, i));
					} else if (object instanceof ArrayList<?>) {

						ArrayList<?> temparr0 = ((ArrayList<?>) object1);
						ArrayList<?> temparr1 = ((ArrayList<?>) object);

						for (Object object2 : temparr0) {
							for (Object object3 : temparr1) {

								if (object2 instanceof ArrayList<?> && !(object3 instanceof ArrayList<?>))
									arr2.add(divBinaryArrayList(object2, object3, i));
								else if (object3 instanceof ArrayList<?> && !(object2 instanceof ArrayList<?>))
									arr2.add(divArrayBinaryList(object2, object3, i));
								else if (object3 instanceof ArrayList<?> && (object2 instanceof ArrayList<?>))
									arr2.add(divArrayListArrayList(object2, object3, i));
								else
									arr2.add(div(object2, object3));

							}
						}

					}
				}

			}
		}
		return arr2;

	}

	private Object addStringArray(Object left, Object right) {
		ArrayList<?> arr = ((ArrayList<?>) right);
		String str = "";

		str += "[";
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i) instanceof Integer) {
				if (i == arr.size() - 1)
					str += ((Integer) arr.get(i));
				else
					str += ((Integer) arr.get(i)) + ", ";
			} else if (arr.get(i) instanceof Double) {
				if (i == arr.size() - 1)
					str += ((Double) arr.get(i));
				else
					str += ((Double) arr.get(i)) + ", ";

			} else if (arr.get(i) instanceof Bin) {
				if (i == arr.size() - 1)
					str += ((Bin) arr.get(i));
				else
					str += ((Bin) arr.get(i)) + ", ";
			} else if (arr.get(i) instanceof String) {
				if (i == arr.size() - 1)
					str += ((String) arr.get(i));
				else
					str += ((String) arr.get(i)) + ", ";
			} else if (arr.get(i) instanceof Boolean) {
				if (i == arr.size() - 1)
					str += ((Boolean) arr.get(i));
				else
					str += ((Boolean) arr.get(i)) + ", ";
			} else if (arr.get(i) instanceof ArrayList<?>) {
				if (i == arr.size() - 1) {
					str += "[";
					str += printArray("", (ArrayList<?>) arr.get(i));
					str += "]";
				} else {

					str += "[";
					str += printArray("", (ArrayList<?>) arr.get(i));
					str += "], ";
				}
			}
		}
		str += "]";
		str = (String) left + str;
		return str;
	}

	private String printArray(String str, ArrayList<?> object) {

		for (int i = 0; i < object.size(); i++) {
			if (object.get(i) instanceof ArrayList<?>) {
				if (i == object.size() - 1) {
					str += "[";
					str += printArray("", (ArrayList<?>) object.get(i));
					str += "]";
				} else {

					str += "[";
					str += printArray("", (ArrayList<?>) object.get(i));
					str += "], ";
				}
			} else {
				if (i == object.size() - 1)
					str += object.get(i).toString();
				else
					str += object.get(i).toString() + ", ";
			}
		}

		return str;
	}

	private Object addIntegerInteger(Object left, Object right) {
		return ((Integer) left) + ((Integer) right);
	}

	private Object gtIntegerInteger(Object left, Object right) {
		return ((Integer) left) > ((Integer) right);
	}

	private Object gteIntegerInteger(Object left, Object right) {
		return ((Integer) left) >= ((Integer) right);
	}

	private Object lteIntegerInteger(Object left, Object right) {
		return ((Integer) left) <= ((Integer) right);
	}

	private Object ltIntegerInteger(Object left, Object right) {
		return ((Integer) left) < ((Integer) right);
	}

	private Object timesIntegerInteger(Object left, Object right) {
		return ((Integer) left) * ((Integer) right);
	}

	private Object powerIntegerInteger(Object left, Object right) {
		return Math.pow(((Integer) left).doubleValue(), ((Integer) right).doubleValue());
	}

	private Object divIntegerInteger(Object left, Object right) {
		return ((Integer) left) / ((Integer) right);
	}

	private Object modIntegerInteger(Object left, Object right) {
		return ((Integer) left) % ((Integer) right);
	}

	private Object addDoubleInteger(Object left, Object right) {
		return ((Double) left) + ((Integer) right);
	}

	private Object gtDoubleInteger(Object left, Object right) {
		return ((Double) left) > ((Integer) right);
	}

	private Object gteDoubleInteger(Object left, Object right) {
		return ((Double) left) >= ((Integer) right);
	}

	private Object lteDoubleInteger(Object left, Object right) {
		return ((Double) left) <= ((Integer) right);
	}

	private Object ltDoubleInteger(Object left, Object right) {
		return ((Double) left) < ((Integer) right);
	}

	private Object timesDoubleInteger(Object left, Object right) {
		return ((Double) left) * ((Integer) right);
	}

	private Object divDoubleInteger(Object left, Object right) {
		return ((Double) left) / ((Integer) right);
	}

	private Object modDoubleInteger(Object left, Object right) {
		return ((Double) left) % ((Integer) right);
	}

	private Object addDoubleDouble(Object left, Object right) {
		return ((Double) left) + ((Double) right);
	}

	private Object gtDoubleDouble(Object left, Object right) {
		return ((Double) left) > ((Double) right);
	}

	private Object gteDoubleDouble(Object left, Object right) {
		return ((Double) left) >= ((Double) right);
	}

	private Object lteDoubleDouble(Object left, Object right) {
		return ((Double) left) <= ((Double) right);
	}

	private Object ltDoubleDouble(Object left, Object right) {
		return ((Double) left) < ((Double) right);
	}

	private Object timesDoubleDouble(Object left, Object right) {
		return ((Double) left) * ((Double) right);
	}

	private Object divDoubleDouble(Object left, Object right) {
		return ((Double) left) / ((Double) right);
	}

	private Object modDoubleDouble(Object left, Object right) {
		return ((Double) left) % ((Double) right);
	}

	private Object addIntegerDouble(Object left, Object right) {
		return ((Integer) left) + ((Double) right);
	}

	private Object gtIntegerDouble(Object left, Object right) {
		return ((Integer) left) > ((Double) right);
	}

	private Object gteIntegerDouble(Object left, Object right) {
		return ((Integer) left) >= ((Double) right);
	}

	private Object lteIntegerDouble(Object left, Object right) {
		return ((Integer) left) <= ((Double) right);
	}

	private Object ltIntegerDouble(Object left, Object right) {
		return ((Integer) left) < ((Double) right);
	}

	private Object timesIntegerDouble(Object left, Object right) {
		return ((Integer) left) * ((Double) right);
	}

	private Object powerIntegerDouble(Object left, Object right) {
		return Math.pow(((Integer) left).doubleValue(), ((Double) right));
	}

	private Object divIntegerDouble(Object left, Object right) {
		return ((Integer) left) / ((Double) right);
	}

	private Object modIntegerDouble(Object left, Object right) {
		return ((Integer) left) % ((Double) right);
	}

	private Object addDoubleBin(Object left, Object right) {
		return Bin.add(new Bin(((Double) left).intValue()), ((Bin) right));
	}

	private Object gtDoubleBin(Object left, Object right) {
		return ((Double) left).intValue() > ((Bin) right).toInteger();
	}

	private Object gteDoubleBin(Object left, Object right) {
		return ((Double) left).intValue() >= ((Bin) right).toInteger();
	}

	private Object lteDoubleBin(Object left, Object right) {
		return ((Double) left).intValue() <= ((Bin) right).toInteger();
	}

	private Object ltDoubleBin(Object left, Object right) {
		return ((Double) left).intValue() < ((Bin) right).toInteger();
	}

	private Object timesDoubleBin(Object left, Object right) {
		return Bin.times(new Bin(((Double) left).intValue()), ((Bin) right));
	}

	private Object divDoubleBin(Object left, Object right) {
		return Bin.divide(new Bin(((Double) left).intValue()), ((Bin) right));
	}

	private Object modDoubleBin(Object left, Object right) {
		return ((Double) left) % ((Bin) right).toDouble();
	}

	private Object addIntegerBin(Object left, Object right) {
		return Bin.add(new Bin((Integer) left), ((Bin) right));
	}

	private Object gtIntegerBin(Object left, Object right) {
		return ((Integer) left) > ((Bin) right).toInteger();
	}

	private Object gteIntegerBin(Object left, Object right) {
		return ((Integer) left) >= ((Bin) right).toInteger();
	}

	private Object lteIntegerBin(Object left, Object right) {
		return ((Integer) left) <= ((Bin) right).toInteger();
	}

	private Object ltIntegerBin(Object left, Object right) {
		return ((Integer) left) < ((Bin) right).toInteger();
	}

	private Object timesIntegerBin(Object left, Object right) {
		return Bin.add(new Bin((Integer) left), ((Bin) right));
	}

	private Object powerIntegerBin(Object left, Object right) {
		return Math.pow(((Integer) left).doubleValue(), ((Bin) right).toDouble());
	}

	private Object divIntegerBin(Object left, Object right) {
		return Bin.divide((new Bin((Integer) left)), ((Bin) right));
	}

	private Object modIntegerBin(Object left, Object right) {
		return ((Integer) left) % ((Bin) right).toInteger();
	}

	private Object addBinInteger(Object left, Object right) {
		return Bin.add((Bin) left, new Bin((Integer) right));
	}

	private Object gtBinInteger(Object left, Object right) {
		return ((Bin) left).toInteger() > (Integer) right;
	}

	private Object gteBinInteger(Object left, Object right) {
		return ((Bin) left).toInteger() >= (Integer) right;
	}

	private Object lteBinInteger(Object left, Object right) {
		return ((Bin) left).toInteger() <= (Integer) right;
	}

	private Object ltBinInteger(Object left, Object right) {
		return ((Bin) left).toInteger() < (Integer) right;
	}

	private Object timesBinInteger(Object left, Object right) {
		return Bin.times((Bin) left, new Bin((Integer) right));
	}

	private Object divBinInteger(Object left, Object right) {
		return Bin.divide((Bin) left, new Bin((Integer) right));
	}

	private Object modBinInteger(Object left, Object right) {
		return ((Bin) left).toInteger() % ((Integer) right);
	}

	private Object addBinDouble(Object left, Object right) {
		return Bin.add((Bin) left, new Bin(((Double) right).intValue()));
	}

	private Object gtBinDouble(Object left, Object right) {
		return ((Bin) left).toInteger() > ((Double) right);
	}

	private Object gteBinDouble(Object left, Object right) {
		return ((Bin) left).toInteger() >= ((Double) right);
	}

	private Object lteBinDouble(Object left, Object right) {
		return ((Bin) left).toInteger() <= ((Double) right);
	}

	private Object ltBinDouble(Object left, Object right) {
		return ((Bin) left).toInteger() < ((Double) right);
	}

	private Object timesBinDouble(Object left, Object right) {
		return Bin.times((Bin) left, new Bin(((Double) right).intValue()));
	}

	private Object divBinDouble(Object left, Object right) {
		return Bin.divide((Bin) left, new Bin(((Double) right).intValue()));
	}

	private Object modBinDouble(Object left, Object right) {
		return ((Bin) left).toInteger() % ((Double) right);
	}

	private Object addBinBin(Object left, Object right) {
		return Bin.add((Bin) left, (Bin) right);
	}

	private Object gtBinBin(Object left, Object right) {
		return ((Bin) left).toInteger() > ((Bin) right).toInteger();
	}

	private Object gteBinBin(Object left, Object right) {
		return ((Bin) left).toInteger() >= ((Bin) right).toInteger();
	}

	private Object lteBinBin(Object left, Object right) {
		return ((Bin) left).toInteger() <= ((Bin) right).toInteger();
	}

	private Object ltBinBin(Object left, Object right) {
		return ((Bin) left).toInteger() < ((Bin) right).toInteger();
	}

	private Object timesBinBin(Object left, Object right) {
		return Bin.times((Bin) left, (Bin) right);
	}

	private Object divBinBin(Object left, Object right) {
		return Bin.divide((Bin) left, (Bin) right);
	}

	private Object modBinBin(Object left, Object right) {
		return ((Bin) left).toInteger() % ((Bin) right).toInteger();
	}

	private Object addObjectString(Object left, Object right) {
		return left.toString() + right.toString();
	}

	private Object timesObjectString(Object left, Object right) {
		String str = "";
		if (left instanceof Integer) {

			for (int i = 0; i < ((Integer) left); i++) {
				str += right.toString();
			}

		} else if (left instanceof Double) {
			for (int i = 0; i < ((Double) left).intValue(); i++) {
				str += right.toString();
			}

		} else if (left instanceof Bin) {
			for (int i = 0; i < ((Bin) left).toInteger(); i++) {
				str += right.toString();
			}

		}
		return str;
	}

	private Object parse(Object left) {
		if (left instanceof Stmt.Expression) {
			Expr expression = ((Stmt.Expression) left).expression;
			if (expression instanceof Expr.Literal) {
				left = ((Expr.Literal) expression).value;
			}
		}
		return left;
	}

	private Object parseBinData(Object left) {
		if (left instanceof Stmt.Expression) {
			Expr expression = ((Stmt.Expression) left).expression;
			if (expression instanceof Expr.Literal) {
				left = ((Expr.Literal) expression).value;
			}
		} else if (left instanceof ArrayList<?>) {
			left = reformatArrayListDataForJustValues((ArrayList<?>) left);
		}
		// else
		// left = findValueOfInstanceFromIdentifier(left);
		return left;
	}

	private ArrayList<?> reformatArrayListDataForJustValues(ArrayList<?> left) {
		ArrayList<?> arr = left;
		ArrayList<Object> arr1 = new ArrayList<>();
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i) instanceof Stmt.Expression) {
				Expr expr = ((Stmt.Expression) arr.get(i)).expression;
				if (expr instanceof Literal) {
					arr1.add(((Literal) expr).value);
				} else {
					arr1.add(expr);
				}
			} else if (arr.get(i) instanceof ArrayList<?>) {
				arr1.add(reformatArrayListDataForJustValues((ArrayList<?>) arr.get(i)));
			} else {
				arr1.add(arr.get(i));
			}
		}

		return arr1;

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
			int valueToAssign = -1;
			if (right instanceof CupInstance) {
				Environment previous = environment;
				try {
					environment = new Environment(environment);
					for (int i = 0; i < ((CupInstance) right).body.size(); i++) {
						Object at = ((CupInstance) right).body.get(i);
						multiplyByValueAndAssign(at, valueToAssign);
					}
				} finally {
					this.environment = previous;
				}
				return right;

			}
			if (right instanceof PocketInstance) {
				Environment previous = environment;
				try {
					environment = new Environment(environment);
					for (int i = 0; i < ((PocketInstance) right).body.size(); i++) {
						Object at = ((PocketInstance) right).body.get(i);
						multiplyByValueAndAssign(at, valueToAssign);
					}
				} finally {
					this.environment = previous;
				}
				return right;

			}
			if (right instanceof BoxInstance) {
				Environment previous = environment;
				try {
					environment = new Environment(environment);
					for (int i = 0; i < ((BoxInstance) right).body.size(); i++) {
						Object at = ((BoxInstance) right).body.get(i);
						multiplyByValueAndAssign(at, valueToAssign);
					}
				} finally {
					this.environment = previous;
				}
				return right;

			}
			if (right instanceof KnotInstance) {
				Environment previous = environment;
				try {
					environment = new Environment(environment);
					for (int i = 0; i < ((KnotInstance) right).body.size(); i++) {
						Object at = ((KnotInstance) right).body.get(i);
						multiplyByValueAndAssign(at, valueToAssign);
					}
				} finally {
					this.environment = previous;
				}
				return right;

			}
			if (right instanceof TonkInstance) {
				Environment previous = environment;
				try {
					environment = new Environment(environment);
					for (int i = 0; i < ((TonkInstance) right).body.size(); i++) {
						Object at = ((TonkInstance) right).body.get(i);
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
				int valueToAssignpp = 1;

				Object pocketInstance = isPocketInstance(right, valueToAssignpp);
				Object cupInstance = isCupInstance(right, valueToAssignpp);
				Object knotInstance = isKnotInstance(right, valueToAssignpp);
				Object tonkInstance = isTonkInstance(right, valueToAssignpp);
				Object boxInstance = isBoxInstance(right, valueToAssignpp);

				if (pocketInstance != null)
					return pocketInstance;
				else if (cupInstance != null)
					return cupInstance;
				else if (boxInstance != null)
					return boxInstance;
				else if (knotInstance != null)
					return knotInstance;
				else if (tonkInstance != null)
					return tonkInstance;

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

				int valueToAssignmm = -1;

				Object pocketInstance = isPocketInstance(right, valueToAssignmm);
				Object cupInstance = isCupInstance(right, valueToAssignmm);
				Object knotInstance = isKnotInstance(right, valueToAssignmm);
				Object tonkInstance = isTonkInstance(right, valueToAssignmm);
				Object boxInstance = isBoxInstance(right, valueToAssignmm);

				if (pocketInstance != null)
					return pocketInstance;
				else if (cupInstance != null)
					return cupInstance;
				else if (boxInstance != null)
					return boxInstance;
				else if (knotInstance != null)
					return knotInstance;
				else if (tonkInstance != null)
					return tonkInstance;
			}
			return right;
		default:
			return null;
		}

	}

	private Object isBoxInstance(Object right, int valueToAssign) {
		if (right instanceof BoxInstance) {
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
							lookUpVariableAddToArray(arr, evaluate);
					}
				}
			} finally {
				this.environment = previous;
			}
			return right;
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
					if (at instanceof StmtDecl) {
						evaluate = evaluate(((StmtDecl) at));
						if (evaluate != null)
							lookUpVariableAddToArray(arr, evaluate);
					}
				}
			} finally {
				this.environment = previous;
			}

			Expr.Cup expr2 = (Cup) ((CupInstance) right).expr;
//			Token identifier = new Token(TokenType.IDENTIFIER, expr2.identifier.lexeme + "varravargssgra", null, null,
//					null, expr2.identifier.column, expr2.identifier.line, expr2.identifier.start,
//					expr2.identifier.finish);
			Token identifier = new Token(TokenType.IDENTIFIER, expr2.identifier.lexeme, null, null, null,
					expr2.identifier.column, expr2.identifier.line, expr2.identifier.start, expr2.identifier.finish);

			Expr.Variable variable = new Expr.Variable(identifier);
			setGlobalOrCurrentEnvironmentVariable(arr, variable);

			return arr;
		} else {
			return null;
		}
	}

	private void lookUpVariableAddToArray(ArrayList<Object> arr, Object evaluate) {
		if (evaluate instanceof CupInstance) {
			Cup cup = (Expr.Cup) ((CupInstance) evaluate).expr;
//			Token identifier = new Token(TokenType.IDENTIFIER, cup.identifier.lexeme + "varravargssgra", null, null,
//					null, cup.identifier.column, cup.identifier.line, cup.identifier.start, cup.identifier.finish);
			Token identifier = new Token(TokenType.IDENTIFIER, cup.identifier.lexeme, null, null, null,
					cup.identifier.column, cup.identifier.line, cup.identifier.start, cup.identifier.finish);

			Expr.Variable variable = new Expr.Variable(identifier);
			Object lookUpVariableByName = lookUpVariableByName(variable);
			arr.add(lookUpVariableByName);
		} else if (evaluate instanceof PocketInstance) {
			Pocket pocket = (Expr.Pocket) ((PocketInstance) evaluate).expr;
			Token identifier = new Token(TokenType.IDENTIFIER,

					pocket.identifier.lexeme, null, null, null, pocket.identifier.column, pocket.identifier.line,
					pocket.identifier.start, pocket.identifier.finish);
//			Token identifier = new Token(TokenType.IDENTIFIER,
//					
//					pocket.identifier.lexeme + "varravargssgra", null, null, null, pocket.identifier.column,
//					pocket.identifier.line, pocket.identifier.start, pocket.identifier.finish);

			Expr.Variable variable = new Expr.Variable(identifier);
			Object lookUpVariableByName = lookUpVariableByName(variable);
			arr.add(lookUpVariableByName);
		} else if (evaluate instanceof BoxInstance) {
			Parser.Expr.Box box = (Expr.Box) ((BoxInstance) evaluate).expr;
			Token identifier = new Token(TokenType.IDENTIFIER, box.identifier.lexeme, null, null, null,
					box.identifier.column, box.identifier.line, box.identifier.start, box.identifier.finish);
//			Token identifier = new Token(TokenType.IDENTIFIER, box.identifier.lexeme + "varravargssgra", null, null,
//					null, box.identifier.column, box.identifier.line, box.identifier.start, box.identifier.finish);

			Expr.Variable variable = new Expr.Variable(identifier);
			Object lookUpVariableByName = lookUpVariableByName(variable);
			arr.add(lookUpVariableByName);
		} else {
			arr.add(evaluate);

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
							lookUpVariableAddToArray(arr, evaluate);
					}
				}
			} finally {
				this.environment = previous;
			}
			return right;
		} else {
			return null;
		}
	}

	private Object isKnotInstance(Object right, int valueToAssign) {
		if (right instanceof PocketInstance) {
			Environment previous = environment;
			ArrayList<Object> arr = new ArrayList<>();
			try {
				environment = new Environment(environment);
				Object evaluate = null;
				for (int i = 0; i < ((KnotInstance) right).body.size(); i++) {
					Object at = ((KnotInstance) right).body.get(i);
					addOrSubtractAndAssign(at, valueToAssign);
					if (at instanceof Stmt.Expression) {
						evaluate = evaluate(((Stmt.Expression) at));
						if (evaluate != null)
							lookUpVariableAddToArray(arr, evaluate);
					}
				}
			} finally {
				this.environment = previous;
			}

			return right;
		} else {
			return null;
		}
	}

	private Object isTonkInstance(Object right, int valueToAssign) {
		if (right instanceof PocketInstance) {
			Environment previous = environment;
			ArrayList<Object> arr = new ArrayList<>();
			try {
				environment = new Environment(environment);
				Object evaluate = null;
				for (int i = 0; i < ((TonkInstance) right).body.size(); i++) {
					Object at = ((TonkInstance) right).body.get(i);
					addOrSubtractAndAssign(at, valueToAssign);
					if (at instanceof Stmt.Expression) {
						evaluate = evaluate(((Stmt.Expression) at));
						if (evaluate != null)
							lookUpVariableAddToArray(arr, evaluate);
					}
				}
			} finally {
				this.environment = previous;
			}
			return right;
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

		} else if (at instanceof Expr.Cup) {
			assignCupAdd(valueToAssign, (Expr) at);
		} else if (at instanceof Expr.Pocket) {
			assignPocketAdd(valueToAssign, (Expr) at);
		} else if (at instanceof Expr.Box) {
			assignBoxAdd(valueToAssign, (Expr) at);
		} else if (at instanceof Expr.Knot) {
			assignKnotAdd(valueToAssign, (Expr) at);
		} else if (at instanceof Expr.Tonk) {
			assignTonkAdd(valueToAssign, (Expr) at);
		} else if (at instanceof Expr.Literal) {
			if (((Literal) at).value instanceof Integer) {
				((Literal) at).value = ((Integer) ((Literal) at).value) + valueToAssign;
			} else if (((Literal) at).value instanceof Double) {
				((Literal) at).value = ((Double) ((Literal) at).value) + valueToAssign;
			} else if (((Literal) at).value instanceof Bin) {
				((Literal) at).value = Bin.add(((Bin) ((Literal) at).value), new Bin(valueToAssign));
			}
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
				} else if (right2 instanceof Expr.Box) {
					assignBoxAdd(valueToAssign, right2);
				} else if (expression instanceof Expr.Knot) {
					assignKnotAdd(valueToAssign, expression);
				} else if (expression instanceof Expr.Tonk) {
					assignTonkAdd(valueToAssign, expression);
				} else if (expression instanceof Expr.Binary) {
					assignBinaryAdd(valueToAssign, expression);
				} else if (at instanceof Expr.Literal) {
					if (((Literal) at).value instanceof Integer) {
						((Literal) at).value = ((Integer) ((Literal) at).value) + valueToAssign;
					} else if (((Literal) at).value instanceof Double) {
						((Literal) at).value = ((Double) ((Literal) at).value) + valueToAssign;
					} else if (((Literal) at).value instanceof Bin) {
						((Literal) at).value = Bin.add(((Bin) ((Literal) at).value), new Bin(valueToAssign));
					}
				}
			} else if (expression instanceof Expr.Binary) {
				assignBinaryAdd(valueToAssign, expression);
			} else if (expression instanceof Expr.Variable) {
				assignVarableSubtract((Expr.Variable) expression, valueToAssign);
			} else if (expression instanceof Expr.Cup) {
				assignCupAdd(valueToAssign, expression);
			} else if (expression instanceof Expr.Pocket) {
				assignPocketAdd(valueToAssign, expression);
			} else if (expression instanceof Expr.Box) {
				assignBoxAdd(valueToAssign, expression);
			} else if (expression instanceof Expr.Knot) {
				assignKnotAdd(valueToAssign, expression);
			} else if (expression instanceof Expr.Tonk) {
				assignTonkAdd(valueToAssign, expression);
			} else if (at instanceof Expr.Literal) {
				if (((Literal) at).value instanceof Integer) {
					((Literal) at).value = ((Integer) ((Literal) at).value) + valueToAssign;
				} else if (((Literal) at).value instanceof Double) {
					((Literal) at).value = ((Double) ((Literal) at).value) + valueToAssign;
				} else if (((Literal) at).value instanceof Bin) {
					((Literal) at).value = Bin.add(((Bin) ((Literal) at).value), new Bin(valueToAssign));
				}
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

	private void assignKnotAdd(int valueToAssign, Expr right2) {
		Environment previous = environment;
		try {
			environment = new Environment(environment);
			for (Declaration declaration : ((Knot) right2).expression) {
				addOrSubtractAndAssign(declaration, valueToAssign);
			}
		} finally {
			this.environment = previous;
		}
	}

	private void assignTonkAdd(int valueToAssign, Expr right2) {
		Environment previous = environment;
		try {
			environment = new Environment(environment);
			for (Declaration declaration : ((Tonk) right2).expression) {
				addOrSubtractAndAssign(declaration, valueToAssign);
			}
		} finally {
			this.environment = previous;
		}
	}

	private void assignBoxAdd(int valueToAssign, Expr right2) {
		Environment previous = environment;
		try {
			environment = new Environment(environment);
			for (Expr declaration : ((Expr.Box) right2).expression) {
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
				setGlobalOrCurrentEnvironmentVariable(lookUpVariableByName, variable);
			}
		} else if (lookUpVariableByName instanceof Integer) {
			int value = ((Integer) lookUpVariableByName) * valueToAssign;
			setGlobalOrCurrentEnvironmentVariable(value, variable);
		} else if (lookUpVariableByName instanceof Double) {
			double value = ((Double) lookUpVariableByName) * valueToAssign;
			locals.get(variable);
			setGlobalOrCurrentEnvironmentVariable(value, variable);
		} else if (lookUpVariableByName instanceof Bin) {
			Integer value = Bin.times(((Bin) lookUpVariableByName), new Bin(valueToAssign));
			setGlobalOrCurrentEnvironmentVariable(value, variable);
		}
	}

	private void assignVarableSubtract(Variable variable, int valueToAssign) {
		Object lookUpVariableByName = lookUpVariable(variable.name, variable);
		if (lookUpVariableByName instanceof Stmt.Expression) {
			Expr expression2 = ((Stmt.Expression) lookUpVariableByName).expression;
			if (expression2 instanceof Expr.Literal) {

				Object value = ((Literal) expression2).value;
				if (value instanceof Integer) {
					value = ((Integer) value) + valueToAssign;
				} else if (value instanceof Double) {
					value = ((Double) value) + valueToAssign;
				} else if (value instanceof Bin) {
					value = Bin.add(((Bin) value), new Bin(valueToAssign));
				}
				setGlobalOrCurrentEnvironmentVariable(value, variable);
			}
		} else if (lookUpVariableByName instanceof Integer) {
			int value = ((Integer) lookUpVariableByName) + valueToAssign;
			setGlobalOrCurrentEnvironmentVariable(value, variable);
		} else if (lookUpVariableByName instanceof Double) {
			int value = ((Integer) lookUpVariableByName) + valueToAssign;
			setGlobalOrCurrentEnvironmentVariable(value, variable);
		} else if (lookUpVariableByName instanceof Bin) {
			Bin value = Bin.add(((Bin) lookUpVariableByName), new Bin(+valueToAssign));
			setGlobalOrCurrentEnvironmentVariable(value, variable);
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
				Object knotInstance = isKnotInstance(left, valueToAssign);
				Object tonkInstance = isTonkInstance(left, valueToAssign);
				Object boxInstance = isBoxInstance(left, valueToAssign);

				if (pocketInstance != null)
					return pocketInstance;
				else if (cupInstance != null)
					return cupInstance;
				else if (boxInstance != null)
					return boxInstance;
				else if (knotInstance != null)
					return knotInstance;
				else if (tonkInstance != null)
					return tonkInstance;
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
				Object knotInstance = isKnotInstance(left, valueToAssign);
				Object tonkInstance = isTonkInstance(left, valueToAssign);
				Object boxInstance = isBoxInstance(left, valueToAssign);

				if (pocketInstance != null)
					return pocketInstance;
				else if (cupInstance != null)
					return cupInstance;
				else if (boxInstance != null)
					return boxInstance;
				else if (knotInstance != null)
					return knotInstance;
				else if (tonkInstance != null)
					return tonkInstance;
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
			setGlobalOrCurrentEnvironmentVariable(value, (Variable) expr2);
		} else if (expr2 instanceof Pocket) {
			Token identifier = new Token(TokenType.IDENTIFIER, ((Pocket) expr2).identifier.lexeme, null, null, null,
					((Pocket) expr2).identifier.column, ((Pocket) expr2).identifier.line,
					((Pocket) expr2).identifier.start, ((Pocket) expr2).identifier.finish);
//			Token identifier = new Token(TokenType.IDENTIFIER, ((Pocket) expr2).identifier.lexeme + "varravargssgra",
//					null, null, null, ((Pocket) expr2).identifier.column, ((Pocket) expr2).identifier.line,
//					((Pocket) expr2).identifier.start, ((Pocket) expr2).identifier.finish);

			Expr.Variable variable = new Expr.Variable(identifier);
			setGlobalOrCurrentEnvironmentVariable(value, variable);
		} else if (expr2 instanceof Cup) {
			Token identifier = new Token(TokenType.IDENTIFIER, ((Cup) expr2).identifier.lexeme, null, null, null,
					((Cup) expr2).identifier.column, ((Cup) expr2).identifier.line, ((Cup) expr2).identifier.start,
					((Cup) expr2).identifier.finish);
//			Token identifier = new Token(TokenType.IDENTIFIER, ((Cup) expr2).identifier.lexeme + "varravargssgra", null,
//					null, null, ((Cup) expr2).identifier.column, ((Cup) expr2).identifier.line,
//					((Cup) expr2).identifier.start, ((Cup) expr2).identifier.finish);

			Expr.Variable variable = new Expr.Variable(identifier);
			setGlobalOrCurrentEnvironmentVariable(value, variable);
		}

	}

	void setGlobalOrCurrentEnvironmentVariable(Object value, Expr.Variable variable) {
		Integer distance = locals.get(variable);
		if (distance != null)
			environment.assignAt(distance, variable.name, value, value, this);
		else
			globals.assign(variable.name, value, value, this);
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
				Pocket poc =((Expr.Pocket)expr.contents);
				if(poc.expression.size()<3) {
					throw new RuntimeException("expected one parameter found none");
				}else if(poc.expression.size()>3) {
					throw new RuntimeException("expected one parameter found more then one");
				}
				Stmt stmt = poc.expression.get(1);
				
				
				if (lookUpContainer instanceof BoxInstance) {

				} else if (lookUpContainer instanceof PocketInstance) {

					boolean contains = ((PocketInstance)lookUpContainer).contains(stmt);
					if (contains) {
					}
				} else if (lookUpContainer instanceof CupInstance) {

					boolean contains = ((CupInstance)lookUpContainer).contains(stmt);
					if (contains) {
					}
				} else if (lookUpContainer instanceof KnotInstance) {

					boolean contains = ((KnotInstance)lookUpContainer).contains( stmt);
					if (contains) {
					}
				} else if (lookUpContainer instanceof TonkInstance) {

					boolean contains = ((TonkInstance)lookUpContainer).contains(stmt);
					if (contains) {
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
			String evaluate = (String) evaluate(stmt.OringialfilePathAndFile);
			String[] split = evaluate.split("/");
			String fn = split[split.length - 1];
			File file = new File(evaluate);

			String evaluate2 = (String) evaluate(stmt.newfilePath);
			if (file.renameTo(new File(evaluate2 + "/" + fn))) {

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

				String str = "";
				Object boxInstance = evaluate(stmt.objecttosave);
				if (boxInstance != null) {
					str = boxInstance.toString();

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

				if (stmt.objectToReadInto instanceof Expr.Variable) {
					Object value = evaluate(new Expr.Literal(data));
					Integer distance = locals.get((Expr.Variable) stmt.objectToReadInto);
					if (distance != null)
						environment.assignAt(distance, ((Expr.Variable) stmt.objectToReadInto).name, value, value,
								this);
					else
						globals.assign(((Expr.Variable) stmt.objectToReadInto).name, value, value, this);
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
			String evaluate = (String) evaluate(stmt.OringialfilePathAndFile);
			String[] split = evaluate.split("/");
			String fn = split[split.length - 1];
			File file = new File(evaluate);

			String evaluate2 = (String) evaluate(stmt.newfilePath);
			if (file.renameTo(new File(evaluate2 + "/" + fn))) {

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
				environment.assignAt(distance, expr.name, value, value, this);
			else
				globals.assign(expr.name, value, value, this);
			return value;
		}
		return null;
	}

	@Override
	public Object visitYranibExpr(Yranib expr) {
		Object left = null;
		Object right = null;

		if (expr.left instanceof Pocket || expr.left instanceof Cup) {
			left = evaluate(expr.left);
			right = evaluate(expr.right);
		} else if (expr.right instanceof Pocket || expr.right instanceof Cup) {
			right = evaluate(expr.right);
			left = evaluate(expr.left);
		} else {
			left = evaluate(expr.left);
			right = evaluate(expr.right);
		}

		left = parseBinData(left);
		right = parseBinData(right);

		switch (expr.operator.type) {
		case NOTEQUALS:
			return !isEqual(left, right);
		case EQUALSNOT:
			return !isEqual(left, right);
		case EQUALSEQUALS:
			return isEqual(left, right);

		case GREATERTHEN:
			return greaterthen(left, right);
		case GREATERTHENEQUAL:
			return greaterequalthen(left, right);
		case LESSTHEN:
			return lessthen(left, right);
		case LESSTHENEQUAL:
			return lessequalthen(left, right);
		case EQUALLESSTHEN:
			return greaterequalthen(left, right);
		case EQUALGREATERTHEN:
			return lessequalthen(left, right);

		case MINUS:

			return sub(left, right);
		case EQUALSMINUS:
			return sub(left, right, expr.left, expr.operator);
		case MINUSEQUALS:
			return sub(left, right, expr.right, expr.operator);

		case EQUALSPLUS:
			return add(left, right, expr.left, expr.operator);
		case PLUSEQUALS:
			return add(left, right, expr.right, expr.operator);
		case PLUS:
			return add(left, right, expr.left, expr.right);
		case MOD:
			return mod(left, right);
		case MODEQUAL:
			return mod(left, right, expr.right, expr.operator);
		case EQUALMOD:
			return mod(left, right, expr.left, expr.operator);

		case FORWARDSLASH:
			return div(left, right);
		case BACKSLASH:
			Object div = div(right, left);
			return div;
		case EQUALDIVIDEFORWARD:
			return div(right, left, expr.left, expr.operator);
		case EQUALDIVIDEBACKWARD:
			return div(right, left, expr.right, expr.operator);

		case TIMES:
			return times(left, right);
		case TIMESEQUAL:
			return times(left, right, expr.right, expr.operator);
		case EQUALTIMES:
			return times(left, right, expr.left, expr.operator);

		case POWER:
			return power(left, right);
		case EQUALPOWER:
			return power(left, right, expr.left, expr.operator);
		case POWEREQUAL:
			return power(left, right, expr.right, expr.operator);
		case YROOT:
			return null;
		case TOORY:
			return null;

		case DNA:
			if (!forward)
				return and(left, right);
			else
				return null;
		case AND:
			if (forward)
				return and(left, right);
			else
				return null;
		case RO:
			if (!forward)
				return or(left, right);
			else
				return false;
		case OR:
			if (forward)
				return or(left, right);
			else
				return false;
		default:
			return null;
		}

	}

	@Override
	public Object visitOnomExpr(Onom expr) {
		if (!forward) {
			return findOnom(expr);
		}
		return null;
	}

	private Object findOnom(Expr expr) {
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
		case NL:
			result = Math.log1p(evaluateDouble - 1);
			break;
		case PXE:
			result = Math.exp(evaluateDouble);
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
		if (!forward) {
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

		return null;
	}

	@Override
	public Object visitIfStmt(If stmt) {
		if (forward) {
			executeIfStmt(stmt);
		}
		return null;
	}

	private void executeIfStmt(If stmt) {
		Object evaluate = evaluate(stmt.ifPocket);

		evaluate = findValueOfInstanceFromIdentifier(evaluate);
		if (evaluate instanceof ArrayList<?> && ((ArrayList) evaluate).size() == 1) {
			Object poc = ((ArrayList<?>) evaluate).get(0);
			if (poc instanceof Boolean) {
				if (((Boolean) poc) == true) {
					evaluate(stmt.ifCup);
				} else {
					if (stmt.elseIfStmt != null) {
						executeIfStmt((Stmt.If) stmt.elseIfStmt);
					} else if (stmt.elseCup != null) {
						evaluate(stmt.elseCup);
					}
				}
			}
		}
	}

	private Object findValueOfInstanceFromIdentifier(Object evaluate) {
		if (evaluate instanceof PocketInstance) {
			Token identifier = new Token(TokenType.IDENTIFIER,
					((Pocket) ((PocketInstance) evaluate).expr).identifier.lexeme, null, null, null,
					((Pocket) ((PocketInstance) evaluate).expr).identifier.column,
					((Pocket) ((PocketInstance) evaluate).expr).identifier.line,
					((Pocket) ((PocketInstance) evaluate).expr).identifier.start,
					((Pocket) ((PocketInstance) evaluate).expr).identifier.finish);
//			Token identifier = new Token(TokenType.IDENTIFIER,
//					((Pocket) ((PocketInstance) evaluate).expr).identifier.lexeme + "varravargssgra", null, null, null,
//					((Pocket) ((PocketInstance) evaluate).expr).identifier.column,
//					((Pocket) ((PocketInstance) evaluate).expr).identifier.line,
//					((Pocket) ((PocketInstance) evaluate).expr).identifier.start,
//					((Pocket) ((PocketInstance) evaluate).expr).identifier.finish);

			Expr.Variable variable = new Expr.Variable(identifier);
			evaluate = lookUpVariableByName(variable);
		} else if (evaluate instanceof CupInstance) {
			Token identifier = new Token(TokenType.IDENTIFIER, ((Cup) ((CupInstance) evaluate).expr).identifier.lexeme,
					null, null, null, ((Cup) ((CupInstance) evaluate).expr).identifier.column,
					((Cup) ((CupInstance) evaluate).expr).identifier.line,
					((Cup) ((CupInstance) evaluate).expr).identifier.start,
					((Cup) ((CupInstance) evaluate).expr).identifier.finish);
//			Token identifier = new Token(TokenType.IDENTIFIER,
//					((Cup) ((CupInstance) evaluate).expr).identifier.lexeme + "varravargssgra", null, null, null,
//					((Cup) ((CupInstance) evaluate).expr).identifier.column,
//					((Cup) ((CupInstance) evaluate).expr).identifier.line,
//					((Cup) ((CupInstance) evaluate).expr).identifier.start,
//					((Cup) ((CupInstance) evaluate).expr).identifier.finish);

			Expr.Variable variable = new Expr.Variable(identifier);
			evaluate = lookUpVariableByName(variable);
		}
		return evaluate;
	}

	private Object findValueOfInstanceFromReifitnedi(Object evaluate) {
		if (evaluate instanceof PocketInstance) {
			Token identifier = new Token(TokenType.IDENTIFIER,
					((Pocket) ((PocketInstance) evaluate).expr).reifitnedi.lexeme, null, null, null,
					((Pocket) ((PocketInstance) evaluate).expr).reifitnedi.column,
					((Pocket) ((PocketInstance) evaluate).expr).reifitnedi.line,
					((Pocket) ((PocketInstance) evaluate).expr).reifitnedi.start,
					((Pocket) ((PocketInstance) evaluate).expr).reifitnedi.finish);
//			((Pocket) ((PocketInstance) evaluate).expr).reifitnedi.lexeme + "varravargssgra", null, null, null,
//			((Pocket) ((PocketInstance) evaluate).expr).reifitnedi.column,
//			((Pocket) ((PocketInstance) evaluate).expr).reifitnedi.line,
//			((Pocket) ((PocketInstance) evaluate).expr).reifitnedi.start,
//			((Pocket) ((PocketInstance) evaluate).expr).reifitnedi.finish);

			Expr.Variable variable = new Expr.Variable(identifier);
			evaluate = lookUpVariableByName(variable);
		} else if (evaluate instanceof CupInstance) {
			Token identifier = new Token(TokenType.IDENTIFIER, ((Cup) ((CupInstance) evaluate).expr).reifitnedi.lexeme,
					null, null, null, ((Cup) ((CupInstance) evaluate).expr).reifitnedi.column,
					((Cup) ((CupInstance) evaluate).expr).reifitnedi.line,
					((Cup) ((CupInstance) evaluate).expr).reifitnedi.start,
					((Cup) ((CupInstance) evaluate).expr).reifitnedi.finish);
//			((Cup) ((CupInstance) evaluate).expr).reifitnedi.lexeme + "varravargssgra", null, null, null,
//			((Cup) ((CupInstance) evaluate).expr).reifitnedi.column,
//			((Cup) ((CupInstance) evaluate).expr).reifitnedi.line,
//			((Cup) ((CupInstance) evaluate).expr).reifitnedi.start,
//			((Cup) ((CupInstance) evaluate).expr).reifitnedi.finish);

			Expr.Variable variable = new Expr.Variable(identifier);
			evaluate = lookUpVariableByName(variable);
		}
		return evaluate;
	}

	@Override
	public Object visitSaveStmt(Save stmt) {
		if (forward) {
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

				String str = "";
				Object boxInstance = evaluate(stmt.objecttosave);
				if (boxInstance != null) {
					str = boxInstance.toString();

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
	public Object visitExpelStmt(Expel stmt) {
		if (forward) {
			try {
				String filePathAndName = (String) evaluate(stmt.filePath);
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

				String str = "";
				Object boxInstance = evaluate(stmt.toExpell);
				if (boxInstance != null) {
					str = boxInstance.toString();

					BufferedWriter writer = new BufferedWriter(new FileWriter(filePathAndName));
					writer.write(str);

					writer.close();
				}

				Integer integer = locals.get(stmt.toExpell);
				Token name = getNameForExpr(stmt.toExpell);

				if (integer != null) {
					environment.assignAt(integer, name, null, null, this);
				} else {
					globals.assign(name, null, null, this);
				}
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}

		} else {
		}
		return null;
	}

	private Token getNameForExpr(Expr expression) {
		if (expression instanceof Expr.Knot) {
			return ((Expr.Knot) expression).identifier.identifierToken;
		} else if (expression instanceof Expr.Cup) {
			return ((Expr.Cup) expression).identifier.identifierToken;
		} else if (expression instanceof Expr.Pocket) {
			return ((Expr.Pocket) expression).identifier.identifierToken;
		} else if (expression instanceof Expr.Box) {
			return ((Expr.Box) expression).identifier.identifierToken;
		} else if (expression instanceof Expr.Variable) {
			return ((Expr.Variable) expression).name;
		} else if (expression instanceof Expr.Tonk) {
			return ((Expr.Tonk) expression).identifier.identifierToken;
		}
		return null;
	}

	@Override
	public Object visitReadStmt(Read stmt) {
		if (forward) {
			try {
				File myObj = new File((String) evaluate(stmt.filePath));
				Scanner myReader = new Scanner(myObj);
				String data = "";
				while (myReader.hasNextLine()) {
					data += myReader.nextLine();

				}

				if (stmt.objectToReadInto instanceof Expr.Variable) {
					Object value = evaluate(new Expr.Literal(data));
					Integer distance = locals.get((Expr.Variable) stmt.objectToReadInto);
					if (distance != null)
						environment.assignAt(distance, ((Expr.Variable) stmt.objectToReadInto).name, value, value,
								this);
					else
						globals.assign(((Expr.Variable) stmt.objectToReadInto).name, value, value, this);
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
	public Object visitConsumeStmt(Consume stmt) {
		if (forward) {
		} else {
		}
		return null;
	}

	@Override
	public Object visitVarStmt(Var stmt) {

		if (stmt.initilizer != null) {
			Object evaluate = evaluate(stmt.initilizer);
			String reverse = reverse(stmt.name.lexeme);
			if (evaluate instanceof CupInstance) {
				Object open0 = ((CupInstance) evaluate).body.get(0);
				Object close1 = ((CupInstance) evaluate).body.get(((CupInstance) evaluate).body.size() - 1);
				if (open0 instanceof Stmt.Expression) {
					Expr expression = ((Stmt.Expression) open0).expression;
					if (expression instanceof Expr.CupOpen) {
						((Expr.CupOpen) expression).ctrl.identifierToken.lexeme = stmt.name.lexeme;
						((Expr.CupOpen) expression).ctrl.lexeme = stmt.name.lexeme + "{";
					}
				}
				if (close1 instanceof Stmt.Expression) {
					Expr expression = ((Stmt.Expression) close1).expression;
					if (expression instanceof Expr.CupClosed) {
						((Expr.CupClosed) expression).ctrl.reifitnediToken.lexeme = reverse;
						((Expr.CupClosed) expression).ctrl.lexeme = "}" + reverse;
					}
				}
				environment.define(stmt.name.lexeme, stmt.type, stmt.num, evaluate, this);
				environment.define(reverse, stmt.type, stmt.num, evaluate, this);
				Token reverseName=new Token(TokenType.IDENTIFIER, reverse, null, null, null, stmt.name.column, stmt.name.line, stmt.name.start, stmt.name.finish);
				environment.assign(stmt.name, null, evaluate);
				environment.assign(reverseName, null, evaluate);
			} else if (evaluate instanceof PocketInstance) {
				Object open0 = ((PocketInstance) evaluate).body.get(0);
				Object close1 = ((PocketInstance) evaluate).body.get(((PocketInstance) evaluate).body.size() - 1);
				if (open0 instanceof Stmt.Expression) {
					Expr expression = ((Stmt.Expression) open0).expression;
					if (expression instanceof Expr.PocketOpen) {
						((Expr.PocketOpen) expression).ctrl.identifierToken.lexeme = stmt.name.lexeme;
						((Expr.PocketOpen) expression).ctrl.lexeme = stmt.name.lexeme + "(";
					}
				}
				if (close1 instanceof Stmt.Expression) {
					Expr expression = ((Stmt.Expression) close1).expression;
					if (expression instanceof Expr.PocketClosed) {
						((Expr.PocketClosed) expression).ctrl.reifitnediToken.lexeme = reverse;
						((Expr.PocketClosed) expression).ctrl.lexeme = ")" + reverse;
					}
				}
				environment.define(stmt.name.lexeme, stmt.type, stmt.num, evaluate, this);
				environment.define(reverse, stmt.type, stmt.num, evaluate, this);
				Token reverseName=new Token(TokenType.IDENTIFIER, reverse, null, null, null, stmt.name.column, stmt.name.line, stmt.name.start, stmt.name.finish);
				environment.assign(stmt.name, null, evaluate);
				environment.assign(reverseName, null, evaluate);
			} else if (evaluate instanceof BoxInstance) {
				Object open0 = ((BoxInstance) evaluate).body.get(0);
				Object close1 = ((BoxInstance) evaluate).body.get(((BoxInstance) evaluate).body.size() - 1);
				if (open0 instanceof Stmt.Expression) {
					Expr expression = ((Stmt.Expression) open0).expression;
					if (expression instanceof Expr.BoxOpen) {
						((Expr.BoxOpen) expression).ctrl.identifierToken.lexeme = stmt.name.lexeme;
						((Expr.BoxOpen) expression).ctrl.lexeme = stmt.name.lexeme + "[";
					}
				}
				if (close1 instanceof Stmt.Expression) {
					Expr expression = ((Stmt.Expression) close1).expression;
					if (expression instanceof Expr.BoxClosed) {
						((Expr.BoxClosed) expression).ctrl.reifitnediToken.lexeme = reverse;
						((Expr.BoxClosed) expression).ctrl.lexeme = "]" + reverse;
					}
				}
				environment.define(stmt.name.lexeme, stmt.type, stmt.num, evaluate, this);
				environment.define(reverse, stmt.type, stmt.num, evaluate, this);
				Token reverseName=new Token(TokenType.IDENTIFIER, reverse, null, null, null, stmt.name.column, stmt.name.line, stmt.name.start, stmt.name.finish);
				environment.assign(stmt.name, null, evaluate);
				environment.assign(reverseName, null, evaluate);
			} else if (evaluate instanceof KnotInstance) {
				environment.define(stmt.name.lexeme, stmt.type, stmt.num, evaluate, this);
				environment.define(reverse, stmt.type, stmt.num, evaluate, this);
				Token reverseName=new Token(TokenType.IDENTIFIER, reverse, null, null, null, stmt.name.column, stmt.name.line, stmt.name.start, stmt.name.finish);
				environment.assign(stmt.name, null, evaluate);
				environment.assign(reverseName, null, evaluate);
			} else if (evaluate instanceof TonkInstance) {
				environment.define(stmt.name.lexeme, stmt.type, stmt.num, evaluate, this);
				environment.define(reverse, stmt.type, stmt.num, evaluate, this);
				Token reverseName=new Token(TokenType.IDENTIFIER, reverse, null, null, null, stmt.name.column, stmt.name.line, stmt.name.start, stmt.name.finish);
				environment.assign(stmt.name, null, evaluate);
				environment.assign(reverseName, null, evaluate);
			} else {
				environment.define(stmt.name.lexeme, stmt.type, stmt.num, evaluate, this);

			}
		} else {
			environment.define(stmt.name.lexeme, stmt.type, stmt.num, null, this);
		}

		return null;
	}

	private String reverse(String str) {
		String nstr = "";
		char ch;
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i); // extracts each character
			nstr = ch + nstr; // adds each character in front of the existing string
		}
		return nstr;
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

		if (stmt.initilizer != null) {
			Object evaluate = evaluate(stmt.initilizer);
			String reverse = reverse(stmt.name.lexeme);
			if (evaluate instanceof CupInstance) {
				Object open0 = ((CupInstance) evaluate).body.get(0);
				Object close1 = ((CupInstance) evaluate).body.get(((CupInstance) evaluate).body.size() - 1);
				if (open0 instanceof Stmt.Expression) {
					Expr expression = ((Stmt.Expression) open0).expression;
					if (expression instanceof Expr.CupOpen) {
						((Expr.CupOpen) expression).ctrl.identifierToken.lexeme = stmt.name.lexeme;
						((Expr.CupOpen) expression).ctrl.lexeme = stmt.name.lexeme + "{";
					}
				}
				if (close1 instanceof Stmt.Expression) {
					Expr expression = ((Stmt.Expression) close1).expression;
					if (expression instanceof Expr.CupClosed) {
						((Expr.CupClosed) expression).ctrl.reifitnediToken.lexeme = reverse;
						((Expr.CupClosed) expression).ctrl.lexeme = "}" + reverse;
					}
				}
				environment.define(stmt.name.lexeme, stmt.type, stmt.num, evaluate, this);
				environment.define(reverse, stmt.type, stmt.num, evaluate, this);
			} else if (evaluate instanceof PocketInstance) {
				Object open0 = ((PocketInstance) evaluate).body.get(0);
				Object close1 = ((PocketInstance) evaluate).body.get(((PocketInstance) evaluate).body.size() - 1);
				if (open0 instanceof Stmt.Expression) {
					Expr expression = ((Stmt.Expression) open0).expression;
					if (expression instanceof Expr.PocketOpen) {
						((Expr.PocketOpen) expression).ctrl.identifierToken.lexeme = stmt.name.lexeme;
						((Expr.PocketOpen) expression).ctrl.lexeme = stmt.name.lexeme + "(";
					}
				}
				if (close1 instanceof Stmt.Expression) {
					Expr expression = ((Stmt.Expression) close1).expression;
					if (expression instanceof Expr.PocketClosed) {
						((Expr.PocketClosed) expression).ctrl.reifitnediToken.lexeme = reverse;
						((Expr.PocketClosed) expression).ctrl.lexeme = ")" + reverse;
					}
				}
				environment.define(stmt.name.lexeme, stmt.type, stmt.num, evaluate, this);
				environment.define(reverse, stmt.type, stmt.num, evaluate, this);
			} else if (evaluate instanceof BoxInstance) {
				Object open0 = ((BoxInstance) evaluate).body.get(0);
				Object close1 = ((BoxInstance) evaluate).body.get(((BoxInstance) evaluate).body.size() - 1);
				if (open0 instanceof Stmt.Expression) {
					Expr expression = ((Stmt.Expression) open0).expression;
					if (expression instanceof Expr.BoxOpen) {
						((Expr.BoxOpen) expression).ctrl.identifierToken.lexeme = stmt.name.lexeme;
						((Expr.BoxOpen) expression).ctrl.lexeme = stmt.name.lexeme + "[";
					}
				}
				if (close1 instanceof Stmt.Expression) {
					Expr expression = ((Stmt.Expression) close1).expression;
					if (expression instanceof Expr.BoxClosed) {
						((Expr.BoxClosed) expression).ctrl.reifitnediToken.lexeme = reverse;
						((Expr.BoxClosed) expression).ctrl.lexeme = "]" + reverse;
					}
				}
				environment.define(stmt.name.lexeme, stmt.type, stmt.num, evaluate, this);
				environment.define(reverse, stmt.type, stmt.num, evaluate, this);
			} else if (evaluate instanceof KnotInstance) {
				environment.define(stmt.name.lexeme, stmt.type, stmt.num, evaluate, this);
				environment.define(reverse, stmt.type, stmt.num, evaluate, this);

			} else if (evaluate instanceof TonkInstance) {
				environment.define(stmt.name.lexeme, stmt.type, stmt.num, evaluate, this);
				environment.define(reverse, stmt.type, stmt.num, evaluate, this);

			} else {
				environment.define(stmt.name.lexeme, stmt.type, stmt.num, evaluate, this);

			}
		} else {
			environment.define(stmt.name.lexeme, stmt.type, stmt.num, null, this);
		}

		return null;
	}

	@Override
	public Object visitGetExpr(Get expr) {
		if (forward) {
			Object object = evaluate(expr.object);
			if (object instanceof Instance) {
				return ((Instance) object).get(expr.name);
			}

			if (object instanceof BoxClass) {
				Instance call = (Instance) ((BoxClass) object).call(this, new ArrayList<>());
				return call.get(expr.name);
			}

			return object;
		}
		return null;
	}

	@Override
	public Object visitSetExpr(Set expr) {
		if (forward) {
			Object evaluate = evaluate(expr.value);
			Integer integer = locals.get(expr.object);
			if (integer != null) {
				environment.assignAt(integer, expr.name, evaluate, evaluate, this);
			} else {
				globals.assign(expr.name, evaluate, evaluate, this);
			}

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

			Object object = evaluate(expr.object);
			if (object instanceof Instance) {
				return ((Instance) object).get(expr.name);
			}

			if (object instanceof BoxClass) {
				Instance call = (Instance) ((BoxClass) object).call(this, new ArrayList<>());
				return call.get(expr.name);
			}

			return object;
		}
		return null;
	}

	@Override
	public Object visitTesExpr(Tes expr) {
		if (!forward) {
			Object evaluate = evaluate(expr.value);
			Integer integer = locals.get(expr.object);
			if (integer != null) {
				environment.assignAt(integer, expr.name, evaluate, evaluate, this);
			} else {
				globals.assign(expr.name, evaluate, evaluate, this);
			}

		}
		return null;
	}

	@Override
	public Object visitLiteralCharExpr(LiteralChar expr) {
		return expr.value;
	}

	@Override
	public Object visitCupExpr(Cup expr) {
		Object name = lookUpVariable(expr.identifier, expr);

		if (name == null) {
			buildClass(expr);
			name = lookUpVariable(expr.identifier, expr);
			Environment previous = environment;
			try {

				environment = new Environment(environment);
				for (int i = 0; i < expr.expression.size(); i++) {
					execute(expr.expression.get(i));

				}
			} finally {
				this.environment = previous;
			}
		} else {

			Environment previous = environment;
			try {

				environment = new Environment(environment);
				for (int i = 0; i < expr.expression.size(); i++) {
					execute(expr.expression.get(i));

				}
			} finally {
				this.environment = previous;
			}
		}
		return null;
	}

	@Override
	public Object visitPocketExpr(Pocket expr) {
		Object name = lookUpVariable(expr.identifier, expr);
		ArrayList<Object> notnull = new ArrayList<>();
		if (name == null) {
			buildClass(expr);
			name = lookUpVariable(expr.identifier, expr);
			Environment previous = environment;
			try {

				environment = new Environment(environment);
				for (int i = 0; i < expr.expression.size(); i++) {
					Object execute = execute(expr.expression.get(i));
					if (execute != null)
						notnull.add(execute);

				}
			} finally {
				this.environment = previous;
			}
		} else {

			Environment previous = environment;
			try {

				environment = new Environment(environment);
				for (int i = 0; i < expr.expression.size(); i++) {
					Object execute = execute(expr.expression.get(i));
					if (execute != null)
						notnull.add(execute);

				}
			} finally {
				this.environment = previous;
			}
		}

		return notnull;

	}

	@Override
	public Object visitKnotExpr(Knot expr) {
		Object name = lookUpVariable(expr.identifier, expr);

		if (name == null) {
			buildClass(expr);
			name = lookUpVariable(expr.identifier, expr);
			Environment previous = environment;
			try {

				environment = new Environment(environment);

				KnotRunner knotRunner = new KnotRunner(expr, expr.expression, this);
				knotRunner.run();

			} finally {
				this.environment = previous;
			}
		} else {

			Environment previous = environment;
			try {

				environment = new Environment(environment);

				KnotRunner knotRunner = new KnotRunner(expr, expr.expression, this);
				knotRunner.run();

			} finally {
				this.environment = previous;
			}
		}

		return null;
	}

	@Override
	public Object visitTonkExpr(Tonk expr) {
		Object name = lookUpVariable(expr.identifier, expr);

		if (name == null) {
			buildClass(expr);
			name = lookUpVariable(expr.identifier, expr);
			Environment previous = environment;
			try {

				environment = new Environment(environment);

				KnotRunner knotRunner = new KnotRunner(expr, expr.expression, this);
				knotRunner.run();

			} finally {
				this.environment = previous;
			}
		} else {

			Environment previous = environment;
			try {

				environment = new Environment(environment);

				KnotRunner knotRunner = new KnotRunner(expr, expr.expression, this);
				knotRunner.run();

			} finally {
				this.environment = previous;
			}
		}

		return null;
	}

	@Override
	public Object visitBoxExpr(Expr.Box expr) {
		Object name = lookUpVariable(expr.identifier, expr);
		ArrayList<Object> notnull = new ArrayList<>();
		if (name == null) {
			buildClass(expr);
			name = lookUpVariable(expr.identifier, expr);
			Environment previous = environment;
			try {

				environment = new Environment(environment);
				for (int i = 0; i < expr.expression.size(); i++) {
					Object execute = execute(expr.expression.get(i));
					if (execute != null)
						notnull.add(execute);

				}
			} finally {
				this.environment = previous;
			}
		} else {

			Environment previous = environment;
			try {

				environment = new Environment(environment);
				for (int i = 0; i < expr.expression.size(); i++) {
					Object execute = execute(expr.expression.get(i));
					if (execute != null)
						notnull.add(execute);

				}
			} finally {
				this.environment = previous;
			}
		}

		return notnull;

	}

	@Override
	public Object visitFiStmt(Fi stmt) {
		if (!forward) {
			return executeFiStmt(stmt);

		}
		return null;
	}

	private Object executeFiStmt(Fi stmt) {
		Object evaluate = evaluate(stmt.ifPocket);

		evaluate = findValueOfInstanceFromReifitnedi(evaluate);
		if (evaluate instanceof ArrayList<?> && ((ArrayList) evaluate).size() == 1) {
			Object poc = ((ArrayList<?>) evaluate).get(0);
			if (poc instanceof Boolean) {
				if (((Boolean) poc) == true) {
					return evaluate(stmt.ifCup);
				} else {
					if (stmt.elseIfStmt != null) {
						return executeFiStmt((Stmt.Fi) stmt.elseIfStmt);
					} else if (stmt.elseCup != null) {
						return evaluate(stmt.elseCup);
					}
				}
			}
		}
		return null;
	}

	@Override
	public Object visitCallExpr(Call expr) {
		if (forward) {
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
		return null;
	}

	@Override
	public Object visitSwapExpr(Swap expr) {
		Object s1 = evaluate(expr.swap1);
		Object s2 = evaluate(expr.Swap2);
		Object s3 = s1;
		s1 = s2;
		s2 = s3;
		if (expr.swap1 instanceof Expr.Variable) {
			if (expr.Swap2 instanceof Expr.Variable) {
				Integer integer = locals.get((Expr.Variable) expr.swap1);
				Integer integer2 = locals.get((Expr.Variable) expr.Swap2);
				assignForSwap((Expr.Variable) expr.swap1, s1, integer);
				assignForSwap((Expr.Variable) expr.Swap2, s2, integer2);
			} else if (expr.Swap2 instanceof Expr.Get) {
				Integer integer = locals.get((Expr.Variable) expr.swap1);
				Integer integer2 = locals.get((Expr.Get) expr.Swap2);
				assignForSwap((Expr.Variable) expr.swap1, s1, integer);
				assignForSwap((Expr.Get) expr.Swap2, s2, integer2);

			} else if (expr.Swap2 instanceof Expr.Teg) {
				Integer integer = locals.get((Expr.Variable) expr.swap1);
				Integer integer2 = locals.get((Expr.Teg) expr.Swap2);
				assignForSwap((Expr.Variable) expr.swap1, s1, integer);
				assignForSwap((Expr.Teg) expr.Swap2, s2, integer2);

			}
		} else if (expr.swap1 instanceof Expr.Get) {
			if (expr.Swap2 instanceof Expr.Variable) {
				Integer integer = locals.get((Expr.Get) expr.swap1);
				Integer integer2 = locals.get((Expr.Variable) expr.Swap2);
				assignForSwap((Expr.Get) expr.swap1, s1, integer);
				assignForSwap((Expr.Variable) expr.Swap2, s2, integer2);
			} else if (expr.Swap2 instanceof Expr.Get) {
				Integer integer = locals.get((Expr.Get) expr.swap1);
				Integer integer2 = locals.get((Expr.Get) expr.Swap2);
				assignForSwap((Expr.Get) expr.swap1, s1, integer);
				assignForSwap((Expr.Get) expr.Swap2, s2, integer2);

			} else if (expr.Swap2 instanceof Expr.Teg) {
				Integer integer = locals.get((Expr.Get) expr.swap1);
				Integer integer2 = locals.get((Expr.Teg) expr.Swap2);
				assignForSwap((Expr.Get) expr.swap1, s1, integer);
				assignForSwap((Expr.Teg) expr.Swap2, s2, integer2);

			}
		} else if (expr.swap1 instanceof Expr.Teg) {
			if (expr.Swap2 instanceof Expr.Variable) {
				Integer integer = locals.get((Expr.Teg) expr.swap1);
				Integer integer2 = locals.get((Expr.Variable) expr.Swap2);
				assignForSwap((Expr.Teg) expr.swap1, s1, integer);
				assignForSwap((Expr.Variable) expr.Swap2, s2, integer2);
			} else if (expr.Swap2 instanceof Expr.Get) {
				Integer integer = locals.get((Expr.Teg) expr.swap1);
				Integer integer2 = locals.get((Expr.Get) expr.Swap2);
				assignForSwap((Expr.Teg) expr.swap1, s1, integer);
				assignForSwap((Expr.Get) expr.Swap2, s2, integer2);

			} else if (expr.Swap2 instanceof Expr.Teg) {
				Integer integer = locals.get((Expr.Teg) expr.swap1);
				Integer integer2 = locals.get((Expr.Teg) expr.Swap2);
				assignForSwap((Expr.Teg) expr.swap1, s1, integer);
				assignForSwap((Expr.Teg) expr.Swap2, s2, integer2);

			}
		}

		return null;
	}

	private void assignForSwap(Expr.Variable expr, Object s1, Integer integer) {
		if (integer != null) {
			environment.assignAt(integer, expr.name, s1, s1, this);
		} else {
			globals.assign(expr.name, s1, s1, this);
		}
	}

	private void assignForSwap(Expr.Get expr, Object s1, Integer integer) {
		if (integer != null) {
			environment.assignAt(integer, expr.name, s1, s1, this);
		} else {
			globals.assign(expr.name, s1, s1, this);
		}
	}

	private void assignForSwap(Expr.Teg expr, Object s1, Integer integer) {
		if (integer != null) {
			environment.assignAt(integer, expr.name, s1, s1, this);
		} else {
			globals.assign(expr.name, s1, s1, this);
		}
	}

	@Override
	public Object visitTemplatVarStmt(TemplatVar stmt) {
		if (forward) {
			if (!templates.contains(stmt.name.lexeme) && templates.contains(stmt.superclass.lexeme)) {
				Expr.Variable supclass = new Expr.Variable(stmt.superclass);
				BoxCallable superBoxClass = (BoxCallable)lookUpVariable(supclass.name,supclass);
				Object call = superBoxClass.call(this, new ArrayList<>());
				String reverse = reverse(stmt.name.lexeme);
				environment.define(stmt.name.lexeme, null, null);
				environment.define(reverse, null, null);
				if (call instanceof CupInstance) {
					Object open0 = ((CupInstance) call).body.get(0);
					Object close1 = ((CupInstance) call).body.get(((CupInstance) call).body.size() - 1);
					if (open0 instanceof Stmt.Expression) {
						Expr expression = ((Stmt.Expression) open0).expression;
						if (expression instanceof Expr.CupOpen) {
							((Expr.CupOpen) expression).ctrl.identifierToken.lexeme = stmt.name.lexeme;
							((Expr.CupOpen) expression).ctrl.lexeme = stmt.name.lexeme + "{";
						}
					}
					if (close1 instanceof Stmt.Expression) {
						Expr expression = ((Stmt.Expression) close1).expression;
						if (expression instanceof Expr.CupClosed) {
							((Expr.CupClosed) expression).ctrl.reifitnediToken.lexeme = reverse;
							((Expr.CupClosed) expression).ctrl.lexeme = "}" + reverse;
						}
					}
				} else if (call instanceof PocketInstance) {
					Object open0 = ((PocketInstance) call).body.get(0);
					Object close1 = ((PocketInstance) call).body.get(((PocketInstance) call).body.size() - 1);
					if (open0 instanceof Stmt.Expression) {
						Expr expression = ((Stmt.Expression) open0).expression;
						if (expression instanceof Expr.PocketOpen) {
							((Expr.PocketOpen) expression).ctrl.identifierToken.lexeme = stmt.name.lexeme;
							((Expr.PocketOpen) expression).ctrl.lexeme = stmt.name.lexeme + "(";
						}
					}
					if (close1 instanceof Stmt.Expression) {
						Expr expression = ((Stmt.Expression) close1).expression;
						if (expression instanceof Expr.PocketClosed) {
							((Expr.PocketClosed) expression).ctrl.reifitnediToken.lexeme = reverse;
							((Expr.PocketClosed) expression).ctrl.lexeme = ")" + reverse;
						}
					}
				}
				Token reifi=new Token(TokenType.IDENTIFIER, reverse, null, null, null, stmt.name.column, stmt.name.line, stmt.name.start, stmt.name.finish);
				environment.assign(stmt.name, null, call);
				environment.assign(reifi, null, call);
			}
		} else {
			if (templates.contains(stmt.name.lexeme) && !templates.contains(stmt.superclass.lexeme)) {
				Expr.Variable supclass = new Expr.Variable(stmt.name);
				BoxCallable superBoxClass = (BoxCallable)lookUpVariable(supclass.name,supclass);
				Object call = superBoxClass.call(this, new ArrayList<>());
				String reverse = reverse(stmt.superclass.lexeme);
				environment.define(stmt.superclass.lexeme, null, null);
				environment.define(reverse, null, null);
				if (call instanceof CupInstance) {
					Object open0 = ((CupInstance) call).body.get(0);
					Object close1 = ((CupInstance) call).body.get(((CupInstance) call).body.size() - 1);
					if (open0 instanceof Stmt.Expression) {
						Expr expression = ((Stmt.Expression) open0).expression;
						if (expression instanceof Expr.CupOpen) {
							((Expr.CupOpen) expression).ctrl.identifierToken.lexeme = stmt.name.lexeme;
							((Expr.CupOpen) expression).ctrl.lexeme = stmt.name.lexeme + "{";
						}
					}
					if (close1 instanceof Stmt.Expression) {
						Expr expression = ((Stmt.Expression) close1).expression;
						if (expression instanceof Expr.CupClosed) {
							((Expr.CupClosed) expression).ctrl.reifitnediToken.lexeme = reverse;
							((Expr.CupClosed) expression).ctrl.lexeme = "}" + reverse;
						}
					}
				} else if (call instanceof PocketInstance) {
					Object open0 = ((PocketInstance) call).body.get(0);
					Object close1 = ((PocketInstance) call).body.get(((PocketInstance) call).body.size() - 1);
					if (open0 instanceof Stmt.Expression) {
						Expr expression = ((Stmt.Expression) open0).expression;
						if (expression instanceof Expr.PocketOpen) {
							((Expr.PocketOpen) expression).ctrl.identifierToken.lexeme = stmt.name.lexeme;
							((Expr.PocketOpen) expression).ctrl.lexeme = stmt.name.lexeme + "(";
						}
					}
					if (close1 instanceof Stmt.Expression) {
						Expr expression = ((Stmt.Expression) close1).expression;
						if (expression instanceof Expr.PocketClosed) {
							((Expr.PocketClosed) expression).ctrl.reifitnediToken.lexeme = reverse;
							((Expr.PocketClosed) expression).ctrl.lexeme = ")" + reverse;
						}
					}
				}
				Token reifi=new Token(TokenType.IDENTIFIER, reverse, null, null, null, stmt.name.column, stmt.name.line, stmt.name.start, stmt.name.finish);
				environment.assign(stmt.superclass, null, call);
				environment.assign(reifi, null, call);
			}
		}
		return null;
	}

	@Override
	public Object visitIfiStmt(Ifi stmt) {
		if (forward) {
			executeIfiStmtForwards(stmt);
		} else {
			executeIfiStmtBackwards(stmt);
		}
		return null;
	}

	private void executeIfiStmtForwards(Ifi stmt) {
		Object evaluate = evaluate(stmt.ifPocket);

		evaluate = findValueOfInstanceFromReifitnedi(evaluate);
		if (evaluate instanceof ArrayList<?> && ((ArrayList) evaluate).size() == 1) {
			Object poc = ((ArrayList<?>) evaluate).get(0);
			if (poc instanceof Boolean) {
				if (((Boolean) poc) == true) {
					evaluate(((Stmt.Fi) stmt.elseIf).ifCup);
				} else {
					evaluateFiBackwards(((Stmt.Fi) stmt.elseIf));
				}
			}
		}
	}

	private void executeIfiStmtBackwards(Ifi stmt) {
		executeFiForIfi((Fi) stmt.elseIf);
	}

	private Object executeFiForIfi(Fi elseIf) {
		if (elseIf.elseIfStmt != null) {
			Object executeFiForIfi = executeFiForIfi((Fi) elseIf.elseIfStmt);
			if (executeFiForIfi == null) {
				Object evaluate = evaluate(elseIf);
				return evaluate;
			} else
				return executeFiForIfi;

		} else {
			Object evaluate = evaluate(elseIf);
			return evaluate;
		}

	}

	private void evaluateFiBackwards(Fi stmt) {
		Object evaluate;
		Expr ifPocket = ((Stmt.Fi) stmt).ifPocket;
		Stmt.Fi elseIfStmt = (Fi) ((Stmt.Fi) stmt).elseIfStmt;
		if (elseIfStmt != null) {
			evaluate = evaluate(ifPocket);

			evaluate = findValueOfInstanceFromReifitnedi(evaluate);
			if (evaluate instanceof ArrayList<?> && ((ArrayList) evaluate).size() == 1) {
				Object poc2 = ((ArrayList<?>) evaluate).get(0);
				if (poc2 instanceof Boolean) {
					if (((Boolean) poc2) == true) {
						evaluate(((Stmt.Fi) elseIfStmt).ifCup);
					} else {
						evaluateFiBackwards(elseIfStmt);
					}
				}
			}
		}
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
			Token identifier = ((Expr.Cup) expr).identifier;
			Token reifitnedi = ((Expr.Cup) expr).reifitnedi;
			this.classes.add(identifier.lexeme);
			this.classes.add(reifitnedi.lexeme);
			return buildCupClass(expr, superclass);
		} else if (expr instanceof Expr.Pocket) {
			Token identifier = ((Expr.Pocket) expr).identifier;
			Token reifitnedi = ((Expr.Pocket) expr).reifitnedi;
			this.classes.add(identifier.lexeme);
			this.classes.add(reifitnedi.lexeme);
			return buildPocketClass(expr, superclass);

		} else if (expr instanceof Expr.Box) {
			return buildBoxClass(expr, superclass);

		} else if (expr instanceof Expr.Knot) {
			return buildKnotClass(expr, superclass);

		} else if (expr instanceof Expr.Tonk) {
			return buildTonkClass(expr, superclass);

		} else if (expr instanceof Expr.Template) {

			Expr container = ((Template) expr).container;
			if (container instanceof Expr.Pocket) {
				Token identifier = ((Expr.Pocket) container).identifier;
				Token reifitnedi = ((Expr.Pocket) container).reifitnedi;
				this.templates.add(identifier.lexeme);
				this.templates.add(reifitnedi.lexeme);
			} else if (container instanceof Expr.Cup) {
				Token identifier = ((Expr.Cup) container).identifier;
				Token reifitnedi = ((Expr.Cup) container).reifitnedi;
				this.templates.add(identifier.lexeme);
				this.templates.add(reifitnedi.lexeme);

			}
			return buildClass(container);
		} else if (expr instanceof Expr.Link) {
			Expr container = ((Expr.Link) expr).container;
			if (container instanceof Expr.Pocket) {
				Token identifier = ((Expr.Pocket) container).identifier;
				Token reifitnedi = ((Expr.Pocket) container).reifitnedi;
				this.links.add(identifier.lexeme);
				this.links.add(reifitnedi.lexeme);
			} else if (container instanceof Expr.Cup) {
				Token identifier = ((Expr.Cup) container).identifier;
				Token reifitnedi = ((Expr.Cup) container).reifitnedi;
				this.links.add(identifier.lexeme);
				this.links.add(reifitnedi.lexeme);

			}
			return buildLink(expr);
		}
		return null;
	}

	private Token buildLink(Expr expr) {
		environment.define(((Tonk) expr).identifier.lexeme, ((Tonk) expr).identifier, null);
		environment.define(((Tonk) expr).reifitnedi.lexeme, ((Tonk) expr).reifitnedi, null);
		Map<String, BoxFunction> methods = new HashMap<>();
		for (Declaration method : ((Expr.Cup) expr).expression) {
			if (method instanceof Declaration.FunDecl) {
				if (((Function) ((Declaration.FunDecl) method).function).forwardIdentifier != null) {
					String fname = ((Function) ((Declaration.FunDecl) method).function).forwardIdentifier.lexeme;
					List<Token> fparamtypes = ((Function) ((Declaration.FunDecl) method).function).forwardPrametersType;
					List<Token> fparamsNames = ((Function) ((Declaration.FunDecl) method).function).forwardPrametersNames;
					BoxFunction boxFunction1 = new BoxFunction(null, fname, fparamtypes, fparamsNames, environment,
							false,true);
					methods.put(fname, boxFunction1);
				}
				if (((Function) ((Declaration.FunDecl) method).function).backwardIdentifier != null) {
					String bname = ((Function) ((Declaration.FunDecl) method).function).backwardIdentifier.lexeme;
					List<Token> bparamtypes = ((Function) ((Declaration.FunDecl) method).function).backwardPrametersType;
					List<Token> bparamsNames = ((Function) ((Declaration.FunDecl) method).function).backwardPrametersNames;

					BoxFunction boxFunction0 = new BoxFunction(null, bname, bparamtypes, bparamsNames, environment,
							false,true);
					methods.put(bname, boxFunction0);

				}
			}
		}
		BoxClass boxClass = new BoxClass(((Expr.Cup) expr).identifier.lexeme, null, null, methods,
				TokenType.CUPCONTAINER, false, null, environment, expr,true);


		environment.assign(((Expr.Cup) expr).identifier, null, boxClass);
		environment.assign(((Expr.Cup) expr).reifitnedi, null, boxClass);
		return ((Expr.Cup) expr).identifier;

	}

	private Token buildTonkClass(Expr expr, Object superclass) {
		if (((Tonk) expr).identifier.identifierToken != null) {
			superclass = lookUpVariableByName(new Variable(((Tonk) expr).identifier.identifierToken));

			if (!(superclass instanceof BoxClass)) {
				throw new RuntimeError(((Tonk) expr).identifier.identifierToken, "Superclass must be a class.");
			}

		}
		Token identifier = new Token(TokenType.IDENTIFIER, ((Expr.Tonk) expr).identifier.lexeme, null, null, null,
				((Expr.Tonk) expr).identifier.column, ((Expr.Tonk) expr).identifier.line,
				((Expr.Tonk) expr).identifier.start, ((Expr.Tonk) expr).identifier.finish);

		Token reifitnedi = new Token(TokenType.IDENTIFIER, ((Expr.Tonk) expr).reifitnedi.lexeme, null, null, null,
				((Expr.Tonk) expr).reifitnedi.column, ((Expr.Tonk) expr).reifitnedi.line,
				((Expr.Tonk) expr).reifitnedi.start, ((Expr.Tonk) expr).reifitnedi.finish);
//		Token identifier = new Token(TokenType.IDENTIFIER, ((Expr.Tonk) expr).identifier.lexeme + "varravargssgra",
//				null, null, null, ((Expr.Tonk) expr).identifier.column, ((Expr.Tonk) expr).identifier.line,
//				((Expr.Tonk) expr).identifier.start, ((Expr.Tonk) expr).identifier.finish);
//		
//		Token reifitnedi = new Token(TokenType.IDENTIFIER, ((Expr.Tonk) expr).reifitnedi.lexeme + "varravargssgra",
//				null, null, null, ((Expr.Tonk) expr).reifitnedi.column, ((Expr.Tonk) expr).reifitnedi.line,
//				((Expr.Tonk) expr).reifitnedi.start, ((Expr.Tonk) expr).reifitnedi.finish);

		environment.define(identifier.lexeme, null, null);
		environment.define(reifitnedi.lexeme, null, null);
		environment.define(((Tonk) expr).identifier.lexeme, ((Tonk) expr).identifier, null);
		environment.define(((Tonk) expr).reifitnedi.lexeme, ((Tonk) expr).reifitnedi, null);

		if (((Tonk) expr).identifier.identifierToken != null) {
			environment = new Environment(environment);

		}

		ArrayList<Object> primarys = new ArrayList<>();
		for (Stmt stmt : ((Expr.Tonk) expr).expression) {
			primarys.add(stmt);
		}

		BoxClass boxClass = new BoxClass(((Tonk) expr).identifier.lexeme, (BoxClass) superclass, primarys, null,
				TokenType.CUPCONTAINER, false, null, environment, expr,false);

		if (superclass != null) {
			environment = environment.enclosing;
		}
		Object call = boxClass.call(this, new ArrayList<Object>());
		environment.assign(identifier, null, call);
		environment.assign(reifitnedi, null, call);
		environment.assign(((Tonk) expr).identifier, null, boxClass);
		environment.assign(((Tonk) expr).reifitnedi, null, boxClass);

		return ((Tonk) expr).identifier;
	}

	private Token buildKnotClass(Expr expr, Object superclass) {
		if (((Knot) expr).identifier.identifierToken != null) {
			superclass = lookUpVariableByName(new Variable(((Knot) expr).identifier.identifierToken));

			if (!(superclass instanceof BoxClass)) {
				throw new RuntimeError(((Knot) expr).identifier.identifierToken, "Superclass must be a class.");
			}

		}
		Token identifier = new Token(TokenType.IDENTIFIER, ((Expr.Knot) expr).identifier.lexeme, null, null, null,
				((Expr.Knot) expr).identifier.column, ((Expr.Knot) expr).identifier.line,
				((Expr.Knot) expr).identifier.start, ((Expr.Knot) expr).identifier.finish);

		Token reifitnedi = new Token(TokenType.IDENTIFIER, ((Expr.Knot) expr).reifitnedi.lexeme, null, null, null,
				((Expr.Knot) expr).reifitnedi.column, ((Expr.Knot) expr).reifitnedi.line,
				((Expr.Knot) expr).reifitnedi.start, ((Expr.Knot) expr).reifitnedi.finish);
//		Token identifier = new Token(TokenType.IDENTIFIER, ((Expr.Knot) expr).identifier.lexeme + "varravargssgra",
//				null, null, null, ((Expr.Knot) expr).identifier.column, ((Expr.Knot) expr).identifier.line,
//				((Expr.Knot) expr).identifier.start, ((Expr.Knot) expr).identifier.finish);
//		
//		Token reifitnedi = new Token(TokenType.IDENTIFIER, ((Expr.Knot) expr).reifitnedi.lexeme + "varravargssgra",
//				null, null, null, ((Expr.Knot) expr).reifitnedi.column, ((Expr.Knot) expr).reifitnedi.line,
//				((Expr.Knot) expr).reifitnedi.start, ((Expr.Knot) expr).reifitnedi.finish);

		environment.define(identifier.lexeme, null, null);
		environment.define(reifitnedi.lexeme, null, null);
		environment.define(((Knot) expr).identifier.lexeme, ((Knot) expr).identifier, null);
		environment.define(((Knot) expr).reifitnedi.lexeme, ((Knot) expr).reifitnedi, null);

		if (((Knot) expr).identifier.identifierToken != null) {
			environment = new Environment(environment);

		}

		ArrayList<Object> primarys = new ArrayList<>();
		for (Stmt stmt : ((Expr.Knot) expr).expression) {

			primarys.add(stmt);
		}

		BoxClass boxClass = new BoxClass(((Knot) expr).identifier.lexeme, (BoxClass) superclass, primarys, null,
				TokenType.CUPCONTAINER, false, null, environment, expr,false);

		if (superclass != null) {
			environment = environment.enclosing;
		}
		Object call = boxClass.call(this, new ArrayList<Object>());
		environment.assign(identifier, null, call);
		environment.assign(reifitnedi, null, call);
		environment.assign(((Knot) expr).identifier, null, boxClass);
		environment.assign(((Knot) expr).reifitnedi, null, boxClass);
		return ((Knot) expr).identifier;

	}

	private Token buildBoxClass(Expr expr, Object superclass) {
		if (((Expr.Box) expr).identifier.identifierToken != null) {
			superclass = lookUpVariableByName(new Variable(((Expr.Box) expr).identifier.identifierToken));

			if (!(superclass instanceof BoxClass)) {
				throw new RuntimeError(((Expr.Box) expr).identifier.identifierToken, "Superclass must be a class.");
			}

		}
		Token identifier = new Token(TokenType.IDENTIFIER, ((Expr.Box) expr).identifier.lexeme, null, null, null,
				((Expr.Box) expr).identifier.column, ((Expr.Box) expr).identifier.line,
				((Expr.Box) expr).identifier.start, ((Expr.Box) expr).identifier.finish);
		Token reifitnedi = new Token(TokenType.IDENTIFIER, ((Expr.Box) expr).reifitnedi.lexeme, null, null, null,
				((Expr.Box) expr).reifitnedi.column, ((Expr.Box) expr).reifitnedi.line,
				((Expr.Box) expr).reifitnedi.start, ((Expr.Box) expr).reifitnedi.finish);
//		Token identifier = new Token(TokenType.IDENTIFIER, ((Expr.Box) expr).identifier.lexeme + "varravargssgra", null,
//				null, null, ((Expr.Box) expr).identifier.column, ((Expr.Box) expr).identifier.line,
//				((Expr.Box) expr).identifier.start, ((Expr.Box) expr).identifier.finish);
//		Token reifitnedi = new Token(TokenType.IDENTIFIER, ((Expr.Box) expr).reifitnedi.lexeme + "varravargssgra", null,
//				null, null, ((Expr.Box) expr).reifitnedi.column, ((Expr.Box) expr).reifitnedi.line,
//				((Expr.Box) expr).reifitnedi.start, ((Expr.Box) expr).reifitnedi.finish);

		environment.define(identifier.lexeme, null, null);
		environment.define(reifitnedi.lexeme, null, null);
		environment.define(((Expr.Box) expr).identifier.lexeme, ((Expr.Box) expr).identifier, null);
		environment.define(((Expr.Box) expr).reifitnedi.lexeme, ((Expr.Box) expr).reifitnedi, null);

		if (((Expr.Box) expr).identifier.identifierToken != null) {
			environment = new Environment(environment);

		}

		ArrayList<Object> primarys = new ArrayList<>();
		for (Expr stmt : ((Expr.Box) expr).expression) {
			primarys.add(stmt);
		}

		BoxClass boxClass = new BoxClass(((Expr.Box) expr).identifier.lexeme, (BoxClass) superclass, primarys, null,
				TokenType.POCKETCONTAINER, false, null, environment, expr,false);

		if (superclass != null) {
			environment = environment.enclosing;
		}
		Object call = boxClass.call(this, new ArrayList<Object>());
		environment.assign(identifier, null, call);
		environment.assign(reifitnedi, null, call);
		environment.assign(((Expr.Box) expr).identifier, null, boxClass);
		environment.assign(((Expr.Box) expr).reifitnedi, null, boxClass);
		return ((Expr.Box) expr).identifier;

	}

	private Token buildPocketClass(Expr expr, Object superclass) {
		if (((Expr.Pocket) expr).identifier.identifierToken != null) {
			superclass = lookUpVariableByName(new Variable(((Expr.Pocket) expr).identifier.identifierToken));

			if (!(superclass instanceof BoxClass)) {
				throw new RuntimeError(((Expr.Pocket) expr).identifier.identifierToken, "Superclass must be a class.");
			}

		}
		Token identifier = new Token(TokenType.IDENTIFIER, ((Expr.Pocket) expr).identifier.lexeme, null, null, null,
				((Expr.Pocket) expr).identifier.column, ((Expr.Pocket) expr).identifier.line,
				((Expr.Pocket) expr).identifier.start, ((Expr.Pocket) expr).identifier.finish);
		Token reifitnedi = new Token(TokenType.IDENTIFIER, ((Expr.Pocket) expr).reifitnedi.lexeme, null, null, null,
				((Expr.Pocket) expr).reifitnedi.column, ((Expr.Pocket) expr).reifitnedi.line,
				((Expr.Pocket) expr).reifitnedi.start, ((Expr.Pocket) expr).reifitnedi.finish);
//		Token identifier = new Token(TokenType.IDENTIFIER, ((Expr.Pocket) expr).identifier.lexeme + "varravargssgra",
//				null, null, null, ((Expr.Pocket) expr).identifier.column, ((Expr.Pocket) expr).identifier.line,
//				((Expr.Pocket) expr).identifier.start, ((Expr.Pocket) expr).identifier.finish);
//		Token reifitnedi = new Token(TokenType.IDENTIFIER, ((Expr.Pocket) expr).reifitnedi.lexeme + "varravargssgra",
//				null, null, null, ((Expr.Pocket) expr).reifitnedi.column, ((Expr.Pocket) expr).reifitnedi.line,
//				((Expr.Pocket) expr).reifitnedi.start, ((Expr.Pocket) expr).reifitnedi.finish);

		environment.define(identifier.lexeme, null, null);
		environment.define(reifitnedi.lexeme, null, null);
		environment.define(((Expr.Pocket) expr).identifier.lexeme, ((Expr.Pocket) expr).identifier, null);
		environment.define(((Expr.Pocket) expr).reifitnedi.lexeme, ((Expr.Pocket) expr).reifitnedi, null);

		if (((Expr.Pocket) expr).identifier.identifierToken != null) {
			environment = new Environment(environment);

		}

		ArrayList<Object> primarys = new ArrayList<>();
		for (Stmt stmt : ((Expr.Pocket) expr).expression) {
			primarys.add(stmt);
		}

		BoxClass boxClass = new BoxClass(((Pocket) expr).identifier.lexeme, (BoxClass) superclass, primarys, null,
				TokenType.POCKETCONTAINER, false, null, environment, expr,false);

		if (superclass != null) {
			environment = environment.enclosing;
		}

		Object call = boxClass.call(this, new ArrayList<Object>());
		environment.assign(identifier, null, call);
		environment.assign(reifitnedi, null, call);
		environment.assign(((Pocket) expr).identifier, null, boxClass);
		environment.assign(((Pocket) expr).reifitnedi, null, boxClass);
		return ((Pocket) expr).identifier;

	}

	private Token buildCupClass(Expr expr, Object superclass) {
		if (((Expr.Cup) expr).identifier.identifierToken != null) {
			superclass = lookUpVariableByName(new Variable(((Expr.Cup) expr).identifier.identifierToken));

			if (!(superclass instanceof BoxClass)) {
				throw new RuntimeError(((Expr.Cup) expr).identifier.identifierToken, "Superclass must be a class.");
			}

		}

		Token identifier = new Token(TokenType.IDENTIFIER, ((Expr.Cup) expr).identifier.lexeme, null, null, null,
				((Expr.Cup) expr).identifier.column, ((Expr.Cup) expr).identifier.line,
				((Expr.Cup) expr).identifier.start, ((Expr.Cup) expr).identifier.finish);
		Token reifitnedi = new Token(TokenType.IDENTIFIER, ((Expr.Cup) expr).reifitnedi.lexeme, null, null, null,
				((Expr.Cup) expr).reifitnedi.column, ((Expr.Cup) expr).reifitnedi.line,
				((Expr.Cup) expr).reifitnedi.start, ((Expr.Cup) expr).reifitnedi.finish);
//		Token identifier = new Token(TokenType.IDENTIFIER, ((Expr.Cup) expr).identifier.lexeme + "varravargssgra",
//				null, null, null, ((Expr.Cup) expr).identifier.column, ((Expr.Cup) expr).identifier.line,
//				((Expr.Cup) expr).identifier.start, ((Expr.Cup) expr).identifier.finish);
//		Token reifitnedi = new Token(TokenType.IDENTIFIER, ((Expr.Cup) expr).reifitnedi.lexeme + "varravargssgra",
//				null, null, null, ((Expr.Cup) expr).reifitnedi.column, ((Expr.Cup) expr).reifitnedi.line,
//				((Expr.Cup) expr).reifitnedi.start, ((Expr.Cup) expr).reifitnedi.finish);

		environment.define(identifier.lexeme, null, null);
		environment.define(reifitnedi.lexeme, null, null);
		environment.define(((Expr.Cup) expr).identifier.lexeme, ((Expr.Cup) expr).identifier, null);
		environment.define(((Expr.Cup) expr).reifitnedi.lexeme, ((Expr.Cup) expr).reifitnedi, null);

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
							false,false);
					methods.put(fname, boxFunction1);
				}
				if (((Function) ((Declaration.FunDecl) method).function).backwardIdentifier != null) {
					String bname = ((Function) ((Declaration.FunDecl) method).function).backwardIdentifier.lexeme;
					Expr body = ((Function) ((Declaration.FunDecl) method).function).sharedCup;
					List<Token> bparamtypes = ((Function) ((Declaration.FunDecl) method).function).backwardPrametersType;
					List<Token> bparamsNames = ((Function) ((Declaration.FunDecl) method).function).backwardPrametersNames;

					BoxFunction boxFunction0 = new BoxFunction(body, bname, bparamtypes, bparamsNames, environment,
							false,false);
					methods.put(bname, boxFunction0);

				}
			}
		}

		ArrayList<Object> primarys = new ArrayList<>();
		for (Declaration declaration : ((Expr.Cup) expr).expression) {

			primarys.add(declaration);
		}

		BoxClass boxClass = new BoxClass(((Expr.Cup) expr).identifier.lexeme, (BoxClass) superclass, primarys, methods,
				TokenType.CUPCONTAINER, false, null, environment, expr,false);

		if (superclass != null) {
			environment = environment.enclosing;
		}

		Object call = boxClass.call(this, new ArrayList<Object>());
		environment.assign(identifier, null, call);
		environment.assign(reifitnedi, null, call);
		environment.assign(((Expr.Cup) expr).identifier, null, boxClass);
		environment.assign(((Expr.Cup) expr).reifitnedi, null, boxClass);
		return ((Expr.Cup) expr).identifier;
	}

	@Override
	public Object visitFunctionLinkFun(FunctionLink fun) {

		return null;
	}

	@Override
	public Object visitLinkExpr(Link expr) {
		buildClass(expr);
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
		if (forward) {
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

				String str = "";
				Object boxInstance = evaluate(stmt.objecttosave);
				if (boxInstance != null) {
					str = boxInstance.toString();

					BufferedWriter writer = new BufferedWriter(new FileWriter(filePathAndName));
					writer.write(str);

					writer.close();
				}

			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		} else {

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

				String str = "";
				Object boxInstance = evaluate(stmt.objecttosave);
				if (boxInstance != null) {
					str = boxInstance.toString();

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
	public Object visitReaddaerStmt(Readdaer stmt) {
		if (forward) {
			try {
				File myObj = new File((String) evaluate(stmt.filePath));
				Scanner myReader = new Scanner(myObj);
				String data = "";
				while (myReader.hasNextLine()) {
					data += myReader.nextLine();

				}

				if (stmt.objectToReadInto instanceof Expr.Variable) {
					Object value = evaluate(new Expr.Literal(data));
					Integer distance = locals.get((Expr.Variable) stmt.objectToReadInto);
					if (distance != null)
						environment.assignAt(distance, ((Expr.Variable) stmt.objectToReadInto).name, value, value,
								this);
					else
						globals.assign(((Expr.Variable) stmt.objectToReadInto).name, value, value, this);
				}

				myReader.close();
			} catch (FileNotFoundException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		} else {
			try {
				File myObj = new File((String) evaluate(stmt.filePath));
				Scanner myReader = new Scanner(myObj);
				String data = "";
				while (myReader.hasNextLine()) {
					data += myReader.nextLine();

				}

				if (stmt.objectToReadInto instanceof Expr.Variable) {
					Object value = evaluate(new Expr.Literal(data));
					Integer distance = locals.get((Expr.Variable) stmt.objectToReadInto);
					if (distance != null)
						environment.assignAt(distance, ((Expr.Variable) stmt.objectToReadInto).name, value, value,
								this);
					else
						globals.assign(((Expr.Variable) stmt.objectToReadInto).name, value, value, this);
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
			return findOnom(expr);
		}
	}

	@Override
	public Object visitBinaryyranibExpr(Binaryyranib expr) {
		Object left = null;
		Object right = null;

		if (expr.left instanceof Pocket || expr.left instanceof Cup) {
			left = evaluate(expr.left);
			right = evaluate(expr.right);
		} else if (expr.right instanceof Pocket || expr.right instanceof Cup) {
			right = evaluate(expr.right);
			left = evaluate(expr.left);
		} else {
			left = evaluate(expr.left);
			right = evaluate(expr.right);
		}

		left = parseBinData(left);
		right = parseBinData(right);
		Object forwardReturn = null;
		switch (expr.operatorForward.type) {
		case NOTEQUALS:
			forwardReturn = !isEqual(left, right);
		case EQUALSNOT:
			forwardReturn = !isEqual(left, right);
		case EQUALSEQUALS:
			forwardReturn = isEqual(left, right);

		case GREATERTHEN:
			forwardReturn = greaterthen(left, right);
		case GREATERTHENEQUAL:
			forwardReturn = greaterequalthen(left, right);
		case LESSTHEN:
			forwardReturn = lessthen(left, right);
		case LESSTHENEQUAL:
			forwardReturn = lessequalthen(left, right);
		case EQUALLESSTHEN:
			forwardReturn = greaterequalthen(left, right);
		case EQUALGREATERTHEN:
			forwardReturn = lessequalthen(left, right);

		case MINUS:

			forwardReturn = sub(left, right);
		case EQUALSMINUS:
			forwardReturn = sub(left, right, expr.left, expr.operatorForward);
		case MINUSEQUALS:
			forwardReturn = sub(left, right, expr.right, expr.operatorForward);

		case EQUALSPLUS:
			forwardReturn = add(left, right, expr.left, expr.operatorForward);
		case PLUSEQUALS:
			forwardReturn = add(left, right, expr.right, expr.operatorForward);
		case PLUS:
			forwardReturn = add(left, right, expr.left, expr.right);
		case MOD:
			forwardReturn = mod(left, right);
		case MODEQUAL:
			forwardReturn = mod(left, right, expr.right, expr.operatorForward);
		case EQUALMOD:
			forwardReturn = mod(left, right, expr.left, expr.operatorForward);

		case FORWARDSLASH:
			forwardReturn = div(left, right);
		case BACKSLASH:
			Object div = div(right, left);
			forwardReturn = div;
		case EQUALDIVIDEFORWARD:
			forwardReturn = div(right, left, expr.left, expr.operatorForward);
		case EQUALDIVIDEBACKWARD:
			forwardReturn = div(right, left, expr.right, expr.operatorForward);

		case TIMES:
			forwardReturn = times(left, right);
		case TIMESEQUAL:
			forwardReturn = times(left, right, expr.right, expr.operatorForward);
		case EQUALTIMES:
			forwardReturn = times(left, right, expr.left, expr.operatorForward);

		case POWER:
			forwardReturn = power(left, right);
		case EQUALPOWER:
			forwardReturn = power(left, right, expr.left, expr.operatorForward);
		case POWEREQUAL:
			forwardReturn = power(left, right, expr.right, expr.operatorForward);
		case YROOT:
			forwardReturn = null;
		case TOORY:
			forwardReturn = null;

		case DNA:
			if (!forward)
				forwardReturn = and(left, right);
			else
				forwardReturn = null;
		case AND:
			if (forward)
				forwardReturn = and(left, right);
			else
				forwardReturn = null;
		case RO:
			if (!forward)
				forwardReturn = or(left, right);
			else
				forwardReturn = false;
		case OR:
			if (forward)
				forwardReturn = or(left, right);
			else
				forwardReturn = false;
		default:
			forwardReturn = null;
		}

		Object backwardReturn = null;
		switch (expr.operatorForward.type) {
		case NOTEQUALS:
			backwardReturn = !isEqual(left, right);
		case EQUALSNOT:
			backwardReturn = !isEqual(left, right);
		case EQUALSEQUALS:
			backwardReturn = isEqual(left, right);

		case GREATERTHEN:
			backwardReturn = greaterthen(left, right);
		case GREATERTHENEQUAL:
			backwardReturn = greaterequalthen(left, right);
		case LESSTHEN:
			backwardReturn = lessthen(left, right);
		case LESSTHENEQUAL:
			backwardReturn = lessequalthen(left, right);
		case EQUALLESSTHEN:
			backwardReturn = greaterequalthen(left, right);
		case EQUALGREATERTHEN:
			backwardReturn = lessequalthen(left, right);

		case MINUS:

			backwardReturn = sub(left, right);
		case EQUALSMINUS:
			backwardReturn = sub(left, right, expr.left, expr.operatorBackward);
		case MINUSEQUALS:
			backwardReturn = sub(left, right, expr.right, expr.operatorBackward);

		case EQUALSPLUS:
			backwardReturn = add(left, right, expr.left, expr.operatorBackward);
		case PLUSEQUALS:
			backwardReturn = add(left, right, expr.right, expr.operatorBackward);
		case PLUS:
			backwardReturn = add(left, right, expr.left, expr.right);
		case MOD:
			backwardReturn = mod(left, right);
		case MODEQUAL:
			backwardReturn = mod(left, right, expr.right, expr.operatorBackward);
		case EQUALMOD:
			backwardReturn = mod(left, right, expr.left, expr.operatorBackward);

		case FORWARDSLASH:
			backwardReturn = div(left, right);
		case BACKSLASH:
			Object div = div(right, left);
			backwardReturn = div;
		case EQUALDIVIDEFORWARD:
			backwardReturn = div(right, left, expr.left, expr.operatorBackward);
		case EQUALDIVIDEBACKWARD:
			backwardReturn = div(right, left, expr.right, expr.operatorBackward);

		case TIMES:
			backwardReturn = times(left, right);
		case TIMESEQUAL:
			backwardReturn = times(left, right, expr.right, expr.operatorBackward);
		case EQUALTIMES:
			backwardReturn = times(left, right, expr.left, expr.operatorBackward);

		case POWER:
			backwardReturn = power(left, right);
		case EQUALPOWER:
			backwardReturn = power(left, right, expr.left, expr.operatorBackward);
		case POWEREQUAL:
			backwardReturn = power(left, right, expr.right, expr.operatorBackward);
		case YROOT:
			backwardReturn = null;
		case TOORY:
			backwardReturn = null;

		case DNA:
			if (!forward)
				backwardReturn = and(left, right);
			else
				backwardReturn = null;
		case AND:
			if (forward)
				backwardReturn = and(left, right);
			else
				backwardReturn = null;
		case RO:
			if (!forward)
				backwardReturn = or(left, right);
			else
				backwardReturn = false;
		case OR:
			if (forward)
				backwardReturn = or(left, right);
			else
				backwardReturn = false;
		default:
			backwardReturn = null;
		}

		ArrayList<Declaration> expression = new ArrayList<>();
		if (forwardReturn instanceof Expr)
			expression.add((Expr) forwardReturn);
		else if (forwardReturn instanceof Stmt)
			expression.add((Stmt) forwardReturn);
		else if (forwardReturn instanceof Parser.Fun)
			expression.add((Parser.Fun) forwardReturn);
		else if (forwardReturn instanceof Declaration)
			expression.add((Declaration) forwardReturn);
		else if (forwardReturn instanceof Integer)
			expression.add(new Expr.Literal(forwardReturn));
		else if (forwardReturn instanceof Boolean)
			expression.add(new Expr.Literal(forwardReturn));

		if (backwardReturn instanceof Expr)
			expression.add((Expr) backwardReturn);
		else if (backwardReturn instanceof Stmt)
			expression.add((Stmt) backwardReturn);
		else if (backwardReturn instanceof Parser.Fun)
			expression.add((Parser.Fun) backwardReturn);
		else if (backwardReturn instanceof Declaration)
			expression.add((Declaration) backwardReturn);
		else if (backwardReturn instanceof Integer)
			expression.add(new Expr.Literal(backwardReturn));
		else if (backwardReturn instanceof Boolean)
			expression.add(new Expr.Literal(backwardReturn));
		BigInteger uniqueNum = generateUniqueNum();
		String reverse = reverse(uniqueNum.toString());
		Token identifier = new Token(TokenType.IDENTIFIER, uniqueNum.toString() + "cup", null, null, null, -1, -1, -1,
				-1);
		Token reifitnedi = new Token(TokenType.IDENTIFIER, "puc" + reverse, null, null, null, -1, -1, -1, -1);
		return new Expr.Cup(identifier, expression, "", reifitnedi);

	}

	private BigInteger generateUniqueNum() {

		Random rand = new Random();
		BigInteger upperLimit = new BigInteger("999999999");
		BigInteger result = new BigInteger(upperLimit.bitLength(), rand);
		result.add(upperLimit);
		while (identifiers.contains(result)) {
			rand = new Random();
			result = new BigInteger(upperLimit.bitLength(), rand);
			result.add(upperLimit);
		}
		this.identifiers.add(result);
		return result;
	}

	@Override
	public Object visitLoggolExpr(Loggol expr) {
		Double evaluateDoubleValue = findDoubleValue(expr.value) - 1;
		Double evaluateDoubleValueBase = findDoubleValue(expr.valueBase) - 1;

		return Math.log1p(evaluateDoubleValue) / Math.log1p(evaluateDoubleValueBase);

	}

	@Override
	public Object visitCallllacExpr(Callllac expr) {
		if (forward) {
			Object callee = evaluate(expr.calleeForward);
			List<Object> arguments = new ArrayList<>();
			for (Expr argument : expr.arguments) {
				arguments.add(evaluate(argument));
			}

			if (!(callee instanceof BoxCallable)) {
				throw new RuntimeError(expr.calleeTokenForward, "Can only call functions and classes.");
			}

			BoxCallable function = (BoxCallable) callee;
			if (arguments.size() != function.arity()) {
				throw new RuntimeError(expr.calleeTokenForward,
						"Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
			}

			return function.call(this, arguments);
		}else {
			Object callee = evaluate(expr.calleeBackward);
			List<Object> arguments = new ArrayList<>();
			for (Expr argument : expr.arguments) {
				arguments.add(evaluate(argument));
			}

			if (!(callee instanceof BoxCallable)) {
				throw new RuntimeError(expr.calleeTokenBackward, "Can only call functions and classes.");
			}

			BoxCallable function = (BoxCallable) callee;
			if (arguments.size() != function.arity()) {
				throw new RuntimeError(expr.calleeTokenBackward,
						"Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
			}

			return function.call(this, arguments);
					
			
			
			
		}
		
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
		case LN:
			result = Math.log1p(evaluateDouble - 1);
			break;
		case EXP:
			result = Math.exp(evaluateDouble);
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

		new KnotRunner(body, body.expression, this);

	}

	public void executeTonk(Tonk body, Environment environment1) {

		new KnotRunner(body, body.expression, this);

	}

	public void executeCupExpr(Cup cup, Environment environment1) {

		environment = environment1;
		for (int i = 0; i < cup.expression.size(); i = i + 1) {
			execute(cup.expression.get(i));
		}

	}

	@Override
	public Object visitAdditiveExpr(Additive expr) {
		Object object = lookUpVariable(((Expr.Variable) (expr.callee)).name, ((Expr.Variable) (expr.callee)));
		if (expr.operator.type == TokenType.ADD) {
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				cup.add(expr.operator, expr.toadd);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				cup.add(expr.operator, expr.toadd);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				cup.add(expr.operator, expr.toadd);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				cup.add(expr.operator, expr.toadd);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				cup.add(expr.operator, expr.toadd);
			}
		} else if (expr.operator.type == TokenType.PUSH) {
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				cup.push(expr.operator, expr.toadd);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				cup.push(expr.operator, expr.toadd);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				cup.push(expr.operator, expr.toadd);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				cup.push(expr.operator, expr.toadd);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				cup.push(expr.operator, expr.toadd);
			}
		}
		return null;
	}

	@Override
	public Object visitSetatExpr(Setat expr) {
		Object object2 = lookUpVariable(((Expr.Variable) (expr.callee)).name, ((Expr.Variable) (expr.callee)));
		if (object2 instanceof CupInstance) {
			CupInstance cup = ((CupInstance) object2);
			cup.setat(expr.index, expr.toset);
		}
		if (object2 instanceof PocketInstance) {
			PocketInstance cup = ((PocketInstance) object2);
			cup.setat(expr.index, expr.toset);
		}
		if (object2 instanceof BoxInstance) {
			BoxInstance cup = ((BoxInstance) object2);
			cup.setat(expr.index, expr.toset);
		}
		if (object2 instanceof KnotInstance) {
			KnotInstance cup = ((KnotInstance) object2);
			cup.setat(expr.index, expr.toset);
		}
		if (object2 instanceof TonkInstance) {
			TonkInstance cup = ((TonkInstance) object2);
			cup.setat(expr.index, expr.toset);
		}
		return null;
	}

	@Override
	public Object visitSubExpr(Sub expr) {
		Object object2 = lookUpVariable(((Expr.Variable) (expr.callee)).name, ((Expr.Variable) (expr.callee)));
		if (object2 instanceof CupInstance) {
			CupInstance cup = ((CupInstance) object2);
			return cup.sub(expr.start, expr.end);
		}
		if (object2 instanceof PocketInstance) {
			PocketInstance cup = ((PocketInstance) object2);
			return cup.sub(expr.start, expr.end);
		}
		if (object2 instanceof BoxInstance) {
			BoxInstance cup = ((BoxInstance) object2);
			return cup.sub(expr.start, expr.end);
		}
		if (object2 instanceof KnotInstance) {
			KnotInstance cup = ((KnotInstance) object2);
			return cup.sub(expr.start, expr.end);
		}
		if (object2 instanceof TonkInstance) {
			TonkInstance cup = ((TonkInstance) object2);
			return cup.sub(expr.start, expr.end);
		}
		return null;
	}

	@Override
	public Object visitBusExpr(Bus expr) {
		Object object2 = lookUpVariable(((Expr.Variable) (expr.callee)).name, ((Expr.Variable) (expr.callee)));
		if (object2 instanceof CupInstance) {
			CupInstance cup = ((CupInstance) object2);
			return cup.sub(expr.start, expr.end);
		}
		if (object2 instanceof PocketInstance) {
			PocketInstance cup = ((PocketInstance) object2);
			return cup.sub(expr.start, expr.end);
		}
		if (object2 instanceof BoxInstance) {
			BoxInstance cup = ((BoxInstance) object2);
			return cup.sub(expr.start, expr.end);
		}
		if (object2 instanceof KnotInstance) {
			KnotInstance cup = ((KnotInstance) object2);
			return cup.sub(expr.start, expr.end);
		}
		if (object2 instanceof TonkInstance) {
			TonkInstance cup = ((TonkInstance) object2);
			return cup.sub(expr.start, expr.end);
		}
		return null;
	}

	@Override
	public Object visitTatesExpr(Tates expr) {
		Object object2 = lookUpVariable(((Expr.Variable) (expr.callee)).name, ((Expr.Variable) (expr.callee)));
		if (object2 instanceof CupInstance) {
			CupInstance cup = ((CupInstance) object2);
			cup.setat(expr.index, expr.toset);
		}
		if (object2 instanceof PocketInstance) {
			PocketInstance cup = ((PocketInstance) object2);
			cup.setat(expr.index, expr.toset);
		}
		if (object2 instanceof BoxInstance) {
			BoxInstance cup = ((BoxInstance) object2);
			cup.setat(expr.index, expr.toset);
		}
		if (object2 instanceof KnotInstance) {
			KnotInstance cup = ((KnotInstance) object2);
			cup.setat(expr.index, expr.toset);
		}
		if (object2 instanceof TonkInstance) {
			TonkInstance cup = ((TonkInstance) object2);
			cup.setat(expr.index, expr.toset);
		}
		return null;
	}

	@Override
	public Object visitEvitiddaExpr(Evitidda expr) {
		Object object = lookUpVariable(((Expr.Variable) (expr.callee)).name, ((Expr.Variable) (expr.callee)));
		if (expr.operator.type == TokenType.DDA) {
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				cup.add(expr.operator, expr.toadd);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				cup.add(expr.operator, expr.toadd);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				cup.add(expr.operator, expr.toadd);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				cup.add(expr.operator, expr.toadd);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				cup.add(expr.operator, expr.toadd);
			}
		} else if (expr.operator.type == TokenType.HSUP) {
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				cup.push(expr.operator, expr.toadd);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				cup.push(expr.operator, expr.toadd);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				cup.push(expr.operator, expr.toadd);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				cup.push(expr.operator, expr.toadd);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				cup.push(expr.operator, expr.toadd);
			}
		}
		return null;
	}

	@Override
	public Object visitParamContOpExpr(ParamContOp expr) {
		if (expr.operator.type == TokenType.REMOVE) {
			Object object = lookUpVariable(((Expr.Variable) (expr.callee)).name, ((Expr.Variable) (expr.callee)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				return cup.remove(expr.operator, expr.index.value);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				return cup.remove(expr.operator, expr.index.value);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				return cup.remove(expr.operator, expr.index.value);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				return cup.remove(expr.operator, expr.index.value);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				return cup.remove(expr.operator, expr.index.value);
			}
		} else if (expr.operator.type == TokenType.GETAT) {
			Object object = lookUpVariable(((Expr.Variable) (expr.callee)).name, ((Expr.Variable) (expr.callee)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				return cup.getat(expr.operator, expr.index.value);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				return cup.getat(expr.operator, expr.index.value);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				return cup.getat(expr.operator, expr.index.value);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				return cup.getat(expr.operator, expr.index.value);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				return cup.getat(expr.operator, expr.index.value);
			}
		}

		return null;
	}

	@Override
	public Object visitNonParamContOpExpr(NonParamContOp expr) {
		if (expr.operator.type == TokenType.SIZE) {
			Object object = lookUpVariable(((Expr.Variable) (expr.callee)).name, ((Expr.Variable) (expr.callee)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				return cup.size(expr.operator);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				return cup.size(expr.operator);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				return cup.size(expr.operator);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				return cup.size(expr.operator);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				return cup.size(expr.operator);
			}
		} else if (expr.operator.type == TokenType.CLEAR) {
			Object object = lookUpVariable(((Expr.Variable) (expr.callee)).name, ((Expr.Variable) (expr.callee)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				return cup.clear(expr.operator);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				return cup.clear(expr.operator);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				return cup.clear(expr.operator);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				return cup.clear(expr.operator);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				return cup.clear(expr.operator);
			}
		} else if (expr.operator.type == TokenType.EMPTY) {
			Object object = lookUpVariable(((Expr.Variable) (expr.callee)).name, ((Expr.Variable) (expr.callee)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				return cup.empty(expr.operator);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				return cup.empty(expr.operator);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				return cup.empty(expr.operator);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				return cup.empty(expr.operator);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				return cup.empty(expr.operator);
			}
		} else if (expr.operator.type == TokenType.POP) {
			Object object = lookUpVariable(((Expr.Variable) (expr.callee)).name, ((Expr.Variable) (expr.callee)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				return cup.pop(expr.operator);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				return cup.pop(expr.operator);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				return cup.pop(expr.operator);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				return cup.pop(expr.operator);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				return cup.pop(expr.operator);
			}
		}
		return null;
	}

	@Override
	public Object visitPoTnocMarapNonExpr(PoTnocMarapNon expr) {
		if (expr.operator.type == TokenType.EZIS) {
			Object object = lookUpVariable(((Expr.Variable) (expr.callee)).name, ((Expr.Variable) (expr.callee)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				return cup.size(expr.operator);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				return cup.size(expr.operator);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				return cup.size(expr.operator);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				return cup.size(expr.operator);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				return cup.size(expr.operator);
			}
		} else if (expr.operator.type == TokenType.RAELC) {
			Object object = lookUpVariable(((Expr.Variable) (expr.callee)).name, ((Expr.Variable) (expr.callee)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				return cup.clear(expr.operator);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				return cup.clear(expr.operator);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				return cup.clear(expr.operator);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				return cup.clear(expr.operator);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				return cup.clear(expr.operator);
			}
		} else if (expr.operator.type == TokenType.YTPME) {
			Object object = lookUpVariable(((Expr.Variable) (expr.callee)).name, ((Expr.Variable) (expr.callee)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				return cup.empty(expr.operator);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				return cup.empty(expr.operator);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				return cup.empty(expr.operator);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				return cup.empty(expr.operator);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				return cup.empty(expr.operator);
			}
		} else if (expr.operator.type == TokenType.POP) {
			Object object = lookUpVariable(((Expr.Variable) (expr.callee)).name, ((Expr.Variable) (expr.callee)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				return cup.pop(expr.operator);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				return cup.pop(expr.operator);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				return cup.pop(expr.operator);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				return cup.pop(expr.operator);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				return cup.pop(expr.operator);
			}
		}
		return null;
	}

	@Override
	public Object visitPoTnocMarapExpr(PoTnocMarap expr) {
		if (expr.operator.type == TokenType.EVOMER) {
			Object object = lookUpVariable(((Expr.Variable) (expr.callee)).name, ((Expr.Variable) (expr.callee)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				return cup.remove(expr.operator, expr.index.value);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				return cup.remove(expr.operator, expr.index.value);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				return cup.remove(expr.operator, expr.index.value);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				return cup.remove(expr.operator, expr.index.value);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				return cup.remove(expr.operator, expr.index.value);
			}
		} else if (expr.operator.type == TokenType.TATEG) {
			Object object = lookUpVariable(((Expr.Variable) (expr.callee)).name, ((Expr.Variable) (expr.callee)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				return cup.getat(expr.operator, expr.index.value);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				return cup.getat(expr.operator, expr.index.value);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				return cup.getat(expr.operator, expr.index.value);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				return cup.getat(expr.operator, expr.index.value);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				return cup.getat(expr.operator, expr.index.value);
			}
		}
		return null;
	}

	@Override
	public Object visitAddittiddaExpr(Addittidda expr) {
		Object object = lookUpVariable(((Expr.Variable) (expr.calleeForward)).name,
				((Expr.Variable) (expr.calleeForward)));
		Object object2 = lookUpVariable(((Expr.Variable) (expr.calleeBackward)).name,
				((Expr.Variable) (expr.calleeBackward)));
		if (expr.operatorForward.type == TokenType.ADD) {
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				cup.add(expr.operatorForward, expr.toadd);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				cup.add(expr.operatorForward, expr.toadd);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				cup.add(expr.operatorForward, expr.toadd);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				cup.add(expr.operatorForward, expr.toadd);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				cup.add(expr.operatorForward, expr.toadd);
			}
		} else if (expr.operatorForward.type == TokenType.PUSH) {
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				cup.push(expr.operatorForward, expr.toadd);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				cup.push(expr.operatorForward, expr.toadd);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				cup.push(expr.operatorForward, expr.toadd);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				cup.push(expr.operatorForward, expr.toadd);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				cup.push(expr.operatorForward, expr.toadd);
			}
		}

		if (expr.operatorBackward.type == TokenType.DDA) {
			if (object2 instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object2);
				cup.add(expr.operatorBackward, expr.toadd);
			}
			if (object2 instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object2);
				cup.add(expr.operatorBackward, expr.toadd);
			}
			if (object2 instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object2);
				cup.add(expr.operatorBackward, expr.toadd);
			}
			if (object2 instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object2);
				cup.add(expr.operatorBackward, expr.toadd);
			}
			if (object2 instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object2);
				cup.add(expr.operatorBackward, expr.toadd);
			}
		} else if (expr.operatorBackward.type == TokenType.HSUP) {
			if (object2 instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object2);
				cup.push(expr.operatorBackward, expr.toadd);
			}
			if (object2 instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object2);
				cup.push(expr.operatorBackward, expr.toadd);
			}
			if (object2 instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object2);
				cup.push(expr.operatorBackward, expr.toadd);
			}
			if (object2 instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object2);
				cup.push(expr.operatorBackward, expr.toadd);
			}
			if (object2 instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object2);
				cup.push(expr.operatorBackward, expr.toadd);
			}
		}
		return null;
	}

	@Override
	public Object visitParCoOppOoCraPExpr(ParCoOppOoCraP expr) {
		Object forward = null;
		Object backward = null;
		if (expr.operatorForward.type == TokenType.REMOVE) {
			Object object = lookUpVariable(((Expr.Variable) (expr.calleeForward)).name,
					((Expr.Variable) (expr.calleeForward)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				forward = cup.remove(expr.operatorForward, expr.index.value);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				forward = cup.remove(expr.operatorForward, expr.index.value);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				forward = cup.remove(expr.operatorForward, expr.index.value);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				forward = cup.remove(expr.operatorForward, expr.index.value);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				forward = cup.remove(expr.operatorForward, expr.index.value);
			}
		} else if (expr.operatorForward.type == TokenType.GETAT) {
			Object object = lookUpVariable(((Expr.Variable) (expr.calleeForward)).name,
					((Expr.Variable) (expr.calleeForward)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				forward = cup.getat(expr.operatorForward, expr.index.value);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				forward = cup.getat(expr.operatorForward, expr.index.value);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				forward = cup.getat(expr.operatorForward, expr.index.value);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				forward = cup.getat(expr.operatorForward, expr.index.value);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				forward = cup.getat(expr.operatorForward, expr.index.value);
			}
		}
		if (expr.operatorBackward.type == TokenType.EVOMER) {
			Object object = lookUpVariable(((Expr.Variable) (expr.calleeBackward)).name,
					((Expr.Variable) (expr.calleeBackward)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				backward = cup.remove(expr.operatorBackward, expr.index.value);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				backward = cup.remove(expr.operatorBackward, expr.index.value);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				backward = cup.remove(expr.operatorBackward, expr.index.value);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				backward = cup.remove(expr.operatorBackward, expr.index.value);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				backward = cup.remove(expr.operatorBackward, expr.index.value);
			}
		} else if (expr.operatorBackward.type == TokenType.TATEG) {
			Object object = lookUpVariable(((Expr.Variable) (expr.calleeBackward)).name,
					((Expr.Variable) (expr.calleeBackward)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				backward = cup.getat(expr.operatorBackward, expr.index.value);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				backward = cup.getat(expr.operatorBackward, expr.index.value);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				backward = cup.getat(expr.operatorBackward, expr.index.value);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				backward = cup.getat(expr.operatorBackward, expr.index.value);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				backward = cup.getat(expr.operatorBackward, expr.index.value);
			}
		}

		ArrayList<Declaration> expression = new ArrayList<>();
		if (forward instanceof Expr)
			expression.add((Expr) forward);
		else if (forward instanceof Stmt)
			expression.add((Stmt) forward);
		else if (forward instanceof Parser.Fun)
			expression.add((Parser.Fun) forward);
		else if (forward instanceof Declaration)
			expression.add((Declaration) forward);

		if (backward instanceof Expr)
			expression.add((Expr) backward);
		else if (backward instanceof Stmt)
			expression.add((Stmt) backward);
		else if (backward instanceof Parser.Fun)
			expression.add((Parser.Fun) backward);
		else if (backward instanceof Declaration)
			expression.add((Declaration) backward);

		return new Expr.Cup(((Expr.Variable) (expr.calleeForward)).name, expression, "",
				((Expr.Variable) (expr.calleeBackward)).name);
	}

	@Override
	public Object visitNoPaCoOOoCaPoNExpr(NoPaCoOOoCaPoN expr) {
		Object forward = null;
		Object backward = null;
		if (expr.operatorForward.type == TokenType.SIZE) {
			Object object = lookUpVariable(((Expr.Variable) (expr.calleeForward)).name,
					((Expr.Variable) (expr.calleeForward)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				forward = cup.size(expr.operatorForward);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				forward = cup.size(expr.operatorForward);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				forward = cup.size(expr.operatorForward);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				forward = cup.size(expr.operatorForward);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				forward = cup.size(expr.operatorForward);
			}
		} else if (expr.operatorForward.type == TokenType.CLEAR) {
			Object object = lookUpVariable(((Expr.Variable) (expr.calleeForward)).name,
					((Expr.Variable) (expr.calleeForward)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				forward = cup.clear(expr.operatorForward);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				forward = cup.clear(expr.operatorForward);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				forward = cup.clear(expr.operatorForward);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				forward = cup.clear(expr.operatorForward);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				forward = cup.clear(expr.operatorForward);
			}
		} else if (expr.operatorForward.type == TokenType.EMPTY) {
			Object object = lookUpVariable(((Expr.Variable) (expr.calleeForward)).name,
					((Expr.Variable) (expr.calleeForward)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				forward = cup.empty(expr.operatorForward);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				forward = cup.empty(expr.operatorForward);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				forward = cup.empty(expr.operatorForward);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				forward = cup.empty(expr.operatorForward);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				forward = cup.empty(expr.operatorForward);
			}
		} else if (expr.operatorForward.type == TokenType.POP) {
			Object object = lookUpVariable(((Expr.Variable) (expr.calleeForward)).name,
					((Expr.Variable) (expr.calleeForward)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				forward = cup.pop(expr.operatorForward);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				forward = cup.pop(expr.operatorForward);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				forward = cup.pop(expr.operatorForward);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				forward = cup.pop(expr.operatorForward);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				forward = cup.pop(expr.operatorForward);
			}
		}

		if (expr.operatorBackward.type == TokenType.EZIS) {
			Object object = lookUpVariable(((Expr.Variable) (expr.calleeBackward)).name,
					((Expr.Variable) (expr.calleeBackward)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				backward = cup.size(expr.operatorBackward);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				backward = cup.size(expr.operatorBackward);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				backward = cup.size(expr.operatorBackward);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				backward = cup.size(expr.operatorBackward);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				backward = cup.size(expr.operatorBackward);
			}
		} else if (expr.operatorBackward.type == TokenType.RAELC) {
			Object object = lookUpVariable(((Expr.Variable) (expr.calleeBackward)).name,
					((Expr.Variable) (expr.calleeBackward)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				backward = cup.clear(expr.operatorBackward);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				backward = cup.clear(expr.operatorBackward);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				backward = cup.clear(expr.operatorBackward);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				backward = cup.clear(expr.operatorBackward);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				backward = cup.clear(expr.operatorBackward);
			}
		} else if (expr.operatorBackward.type == TokenType.YTPME) {
			Object object = lookUpVariable(((Expr.Variable) (expr.calleeBackward)).name,
					((Expr.Variable) (expr.calleeBackward)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				backward = cup.empty(expr.operatorBackward);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				backward = cup.empty(expr.operatorBackward);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				backward = cup.empty(expr.operatorBackward);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				backward = cup.empty(expr.operatorBackward);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				backward = cup.empty(expr.operatorBackward);
			}
		} else if (expr.operatorBackward.type == TokenType.POP) {
			Object object = lookUpVariable(((Expr.Variable) (expr.calleeBackward)).name,
					((Expr.Variable) (expr.calleeBackward)));
			if (object instanceof CupInstance) {
				CupInstance cup = ((CupInstance) object);
				backward = cup.pop(expr.operatorBackward);
			}
			if (object instanceof PocketInstance) {
				PocketInstance cup = ((PocketInstance) object);
				backward = cup.pop(expr.operatorBackward);
			}
			if (object instanceof BoxInstance) {
				BoxInstance cup = ((BoxInstance) object);
				backward = cup.pop(expr.operatorBackward);
			}
			if (object instanceof KnotInstance) {
				KnotInstance cup = ((KnotInstance) object);
				backward = cup.pop(expr.operatorBackward);
			}
			if (object instanceof TonkInstance) {
				TonkInstance cup = ((TonkInstance) object);
				backward = cup.pop(expr.operatorBackward);
			}
		}
		ArrayList<Declaration> expression = new ArrayList<>();
		if (forward instanceof Expr)
			expression.add((Expr) forward);
		else if (forward instanceof Stmt)
			expression.add((Stmt) forward);
		else if (forward instanceof Parser.Fun)
			expression.add((Parser.Fun) forward);
		else if (forward instanceof Declaration)
			expression.add((Declaration) forward);
		else if (forward instanceof Integer)
			expression.add(new Expr.Literal(forward));
		else if (forward instanceof Boolean)
			expression.add(new Expr.Literal(forward));

		if (backward instanceof Expr)
			expression.add((Expr) backward);
		else if (backward instanceof Stmt)
			expression.add((Stmt) backward);
		else if (backward instanceof Parser.Fun)
			expression.add((Parser.Fun) backward);
		else if (backward instanceof Declaration)
			expression.add((Declaration) backward);
		else if (backward instanceof Integer)
			expression.add(new Expr.Literal(backward));
		else if (backward instanceof Boolean)
			expression.add(new Expr.Literal(backward));

		BigInteger uniqueNum = generateUniqueNum();
		String reverse = reverse(uniqueNum.toString());
		Token identifier = new Token(TokenType.IDENTIFIER, uniqueNum.toString() + "cup", null, null, null, -1, -1, -1,
				-1);
		Token reifitnedi = new Token(TokenType.IDENTIFIER, "puc" + reverse, null, null, null, -1, -1, -1, -1);
		return new Expr.Cup(identifier, expression, "", reifitnedi);
	}

	@Override
	public Object visitSetattatesExpr(Setattates expr) {
		Object object2 = lookUpVariable(((Expr.Variable) (expr.calleeForward)).name,
				((Expr.Variable) (expr.calleeForward)));
		Object object3 = lookUpVariable(((Expr.Variable) (expr.calleeForward)).name,
				((Expr.Variable) (expr.calleeForward)));
		if (object2 instanceof CupInstance) {
			CupInstance cup = ((CupInstance) object2);
			cup.setat(expr.index, expr.toset);
		}
		if (object2 instanceof PocketInstance) {
			PocketInstance cup = ((PocketInstance) object2);
			cup.setat(expr.index, expr.toset);
		}
		if (object2 instanceof BoxInstance) {
			BoxInstance cup = ((BoxInstance) object2);
			cup.setat(expr.index, expr.toset);
		}
		if (object2 instanceof KnotInstance) {
			KnotInstance cup = ((KnotInstance) object2);
			cup.setat(expr.index, expr.toset);
		}
		if (object2 instanceof TonkInstance) {
			TonkInstance cup = ((TonkInstance) object2);
			cup.setat(expr.index, expr.toset);
		}

		if (object3 instanceof CupInstance) {
			CupInstance cup = ((CupInstance) object3);
			cup.setat(expr.index, expr.toset);
		}
		if (object3 instanceof PocketInstance) {
			PocketInstance cup = ((PocketInstance) object3);
			cup.setat(expr.index, expr.toset);
		}
		if (object3 instanceof BoxInstance) {
			BoxInstance cup = ((BoxInstance) object3);
			cup.setat(expr.index, expr.toset);
		}
		if (object3 instanceof KnotInstance) {
			KnotInstance cup = ((KnotInstance) object3);
			cup.setat(expr.index, expr.toset);
		}
		if (object3 instanceof TonkInstance) {
			TonkInstance cup = ((TonkInstance) object3);
			cup.setat(expr.index, expr.toset);
		}

		return null;
	}

	@Override
	public Object visitSubbusExpr(Subbus expr) {
		Object object2 = lookUpVariable(((Expr.Variable) (expr.calleeForward)).name,
				((Expr.Variable) (expr.calleeForward)));
		Object object3 = lookUpVariable(((Expr.Variable) (expr.calleeBackward)).name,
				((Expr.Variable) (expr.calleeBackward)));
		Object forwardReturn = null;
		Object backwardReturn = null;

		if (object2 instanceof CupInstance) {
			CupInstance cup = ((CupInstance) object2);
			forwardReturn = cup.sub(expr.start, expr.end);
		}
		if (object2 instanceof PocketInstance) {
			PocketInstance cup = ((PocketInstance) object2);
			forwardReturn = cup.sub(expr.start, expr.end);
		}
		if (object2 instanceof BoxInstance) {
			BoxInstance cup = ((BoxInstance) object2);
			forwardReturn = cup.sub(expr.start, expr.end);
		}
		if (object2 instanceof KnotInstance) {
			KnotInstance cup = ((KnotInstance) object2);
			forwardReturn = cup.sub(expr.start, expr.end);
		}
		if (object2 instanceof TonkInstance) {
			TonkInstance cup = ((TonkInstance) object2);
			forwardReturn = cup.sub(expr.start, expr.end);
		}

		if (object3 instanceof CupInstance) {
			CupInstance cup = ((CupInstance) object3);
			backwardReturn = cup.sub(expr.start, expr.end);
		}
		if (object3 instanceof PocketInstance) {
			PocketInstance cup = ((PocketInstance) object3);
			backwardReturn = cup.sub(expr.start, expr.end);
		}
		if (object3 instanceof BoxInstance) {
			BoxInstance cup = ((BoxInstance) object3);
			backwardReturn = cup.sub(expr.start, expr.end);
		}
		if (object3 instanceof KnotInstance) {
			KnotInstance cup = ((KnotInstance) object3);
			backwardReturn = cup.sub(expr.start, expr.end);
		}
		if (object3 instanceof TonkInstance) {
			TonkInstance cup = ((TonkInstance) object3);
			backwardReturn = cup.sub(expr.start, expr.end);
		}
		ArrayList<Declaration> expression = new ArrayList<>();
		if (forwardReturn instanceof Expr)
			expression.add((Expr) forwardReturn);
		else if (forwardReturn instanceof Stmt)
			expression.add((Stmt) forwardReturn);
		else if (forwardReturn instanceof Parser.Fun)
			expression.add((Parser.Fun) forwardReturn);
		else if (forwardReturn instanceof Declaration)
			expression.add((Declaration) forwardReturn);
		else if (forwardReturn instanceof Integer)
			expression.add(new Expr.Literal(forwardReturn));
		else if (forwardReturn instanceof Boolean)
			expression.add(new Expr.Literal(forwardReturn));

		if (backwardReturn instanceof Expr)
			expression.add((Expr) backwardReturn);
		else if (backwardReturn instanceof Stmt)
			expression.add((Stmt) backwardReturn);
		else if (backwardReturn instanceof Parser.Fun)
			expression.add((Parser.Fun) backwardReturn);
		else if (backwardReturn instanceof Declaration)
			expression.add((Declaration) backwardReturn);
		else if (backwardReturn instanceof Integer)
			expression.add(new Expr.Literal(backwardReturn));
		else if (backwardReturn instanceof Boolean)
			expression.add(new Expr.Literal(backwardReturn));

		BigInteger uniqueNum = generateUniqueNum();
		String reverse = reverse(uniqueNum.toString());
		Token identifier = new Token(TokenType.IDENTIFIER, uniqueNum.toString() + "cup", null, null, null, -1, -1, -1,
				-1);
		Token reifitnedi = new Token(TokenType.IDENTIFIER, "puc" + reverse, null, null, null, -1, -1, -1, -1);
		return new Expr.Cup(identifier, expression, "", reifitnedi);
	}

	@Override
	public Object visitContainssniatnocExpr(Containssniatnoc expr) {
		return null;
	}

	@Override
	public Object visitLiteralBoolExpr(LiteralBool expr) {
		return expr.value;
	}

	@Override
	public Object visitLiteralLoobExpr(LiteralLoob expr) {
		return expr.value;
	}

}
