package Box.Interpreter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import Box.Box.Box;
import Box.Token.TokenType;
import Box.math.BoxMath;
import Box.Syntax.Expr;
import Box.Syntax.Expr.Assignment;
import Box.Syntax.Expr.Binary;
import Box.Syntax.Expr.BoxOpenLeft;
import Box.Syntax.Expr.BoxOpenRight;
import Box.Syntax.Expr.Boxx;
import Box.Syntax.Expr.Call;
import Box.Syntax.Expr.Cid;
import Box.Syntax.Expr.Cocket;
import Box.Syntax.Expr.Contains;
import Box.Syntax.Expr.Cup;
import Box.Syntax.Expr.CupOpenLeft;
import Box.Syntax.Expr.CupOpenRight;
import Box.Syntax.Expr.Elbairav;
import Box.Syntax.Expr.Epyt;
import Box.Syntax.Expr.Factorial;
import Box.Syntax.Expr.Get;
import Box.Syntax.Expr.GetBoxCupPocket;
import Box.Syntax.Expr.Gol;
import Box.Syntax.Expr.Knot;
import Box.Syntax.Expr.Lacigol;
import Box.Syntax.Expr.Lairotcaf;
import Box.Syntax.Expr.Laretil;
import Box.Syntax.Expr.LaretilChar;
import Box.Syntax.Expr.Lash;
import Box.Syntax.Expr.Lid;
import Box.Syntax.Expr.Lil;
import Box.Syntax.Expr.Literal;
import Box.Syntax.Expr.LiteralChar;
import Box.Syntax.Expr.Llac;
import Box.Syntax.Expr.Locket;
import Box.Syntax.Expr.Log;
import Box.Syntax.Expr.Logical;
import Box.Syntax.Expr.Lup;
import Box.Syntax.Expr.Mono;
import Box.Syntax.Expr.Onom;
import Box.Syntax.Expr.Parameter;
import Box.Syntax.Expr.Pid;
import Box.Syntax.Expr.Pocket;
import Box.Syntax.Expr.PocketOpenLeft;
import Box.Syntax.Expr.PocketOpenRight;
import Box.Syntax.Expr.Pup;
import Box.Syntax.Expr.Set;
import Box.Syntax.Expr.SetBoxCupPocket;
import Box.Syntax.Expr.Sniatnoc;
import Box.Syntax.Expr.Teg;
import Box.Syntax.Expr.TegBoxCupPocket;
import Box.Syntax.Expr.Tes;
import Box.Syntax.Expr.TesBoxCupPocket;
import Box.Syntax.Expr.Tnemngissa;
import Box.Syntax.Expr.Type;
import Box.Syntax.Expr.Unary;
import Box.Syntax.Expr.Variable;
import Box.Syntax.Expr.Yranib;
import Box.Syntax.Expr.Yranu;
import Box.Syntax.Stmt;
import Box.Syntax.Stmt.Constructor;
import Box.Syntax.Stmt.Consume;
import Box.Syntax.Stmt.Daer;
import Box.Syntax.Stmt.Emaner;
import Box.Syntax.Stmt.Evas;
import Box.Syntax.Stmt.Evom;
import Box.Syntax.Stmt.Expel;
import Box.Syntax.Stmt.Expression;
import Box.Syntax.Stmt.Fi;
import Box.Syntax.Stmt.Function;
import Box.Syntax.Stmt.If;
import Box.Syntax.Stmt.Move;
import Box.Syntax.Stmt.Noisserpxe;
import Box.Syntax.Stmt.Nruter;
import Box.Syntax.Stmt.PassThrough;
import Box.Syntax.Stmt.Print;
import Box.Syntax.Stmt.Rav;
import Box.Syntax.Stmt.Read;
import Box.Syntax.Stmt.Rename;
import Box.Syntax.Stmt.Save;
import Box.Syntax.Stmt.Tnirp;
import Box.Syntax.Stmt.Var;
import Box.Token.Token;

public class Interpreter extends Thread implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

	public Environment globals = new Environment();
	private Environment environment = globals;
	private Map<Expr, Integer> locals = new HashMap<>();
	private boolean fromCall = false;
	private KnotTracker tracker = new KnotTracker();
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




	public void interpret(List<Stmt> statements) {
		try {
			for (Stmt stmt : statements) {
				execute(stmt);
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
				if (stmt instanceof Stmt.Noisserpxe) {
					if (((Stmt.Noisserpxe) stmt).noisserpex instanceof Expr.Pocket) {
						visitPocket.add(visitPocketExpr((Pocket) ((Stmt.Noisserpxe) stmt).noisserpex));
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
	public Void visitExpressionStmt(Stmt.Expression stmt) {
		evaluate(stmt.expression);
		return null;
	}

	@Override
	public Void visitFunctionStmt(Function stmt) {

		BoxFunction function0 = null;
		if (stmt.binFun0 instanceof Expr.Literal) {
			if (((Bin) ((Expr.Literal) stmt.binFun0).value).isValueEqualTo(new Bin("10"))) {
				if (stmt.identifierfun0 instanceof Expr.Variable)
					function0 = new BoxFunction(stmt.knotfun0, ((Expr.Variable) stmt.identifierfun0).name.lexeme,
							stmt.paramsfun0, environment, false);
				if (stmt.identifierfun0 instanceof Expr.Elbairav)
					function0 = new BoxFunction(stmt.knotfun0, ((Expr.Elbairav) stmt.identifierfun0).name.lexeme,
							stmt.paramsfun0, environment, false);
			}
			if (((Bin) ((Expr.Literal) stmt.binFun0).value).isValueEqualTo(new Bin("11"))) {
				if (stmt.identifierfun0 instanceof Expr.Variable)
					function0 = new BoxFunction(stmt.knotfun0, ((Expr.Variable) stmt.identifierfun0).name.lexeme,
							stmt.paramsfun1, environment, false);
				if (stmt.identifierfun0 instanceof Expr.Elbairav)
					function0 = new BoxFunction(stmt.knotfun0, ((Expr.Elbairav) stmt.identifierfun0).name.lexeme,
							stmt.paramsfun1, environment, false);
			}
			if (((Bin) ((Expr.Literal) stmt.binFun0).value).isValueEqualTo(new Bin("00"))) {
				if (stmt.identifierfun0 instanceof Expr.Variable)
					function0 = new BoxFunction(stmt.knotfun1, ((Expr.Variable) stmt.identifierfun0).name.lexeme,
							stmt.paramsfun0, environment, false);
				if (stmt.identifierfun0 instanceof Expr.Elbairav)
					function0 = new BoxFunction(stmt.knotfun1, ((Expr.Elbairav) stmt.identifierfun0).name.lexeme,
							stmt.paramsfun0, environment, false);
			}
			if (((Bin) ((Expr.Literal) stmt.binFun0).value).isValueEqualTo(new Bin("01"))) {
				if (stmt.identifierfun0 instanceof Expr.Variable)
					function0 = new BoxFunction(stmt.knotfun1, ((Expr.Variable) stmt.identifierfun0).name.lexeme,
							stmt.paramsfun1, environment, false);
				if (stmt.identifierfun0 instanceof Expr.Elbairav)
					function0 = new BoxFunction(stmt.knotfun1, ((Expr.Elbairav) stmt.identifierfun0).name.lexeme,
							stmt.paramsfun1, environment, false);
			}
		} else if (stmt.binFun0 instanceof Expr.Laretil) {
			if (((Bin) ((Expr.Laretil) stmt.binFun0).value).isValueEqualTo(new Bin("10"))) {
				if (stmt.identifierfun0 instanceof Expr.Variable)
					function0 = new BoxFunction(stmt.knotfun0, ((Expr.Variable) stmt.identifierfun0).name.lexeme,
							stmt.paramsfun0, environment, false);
				if (stmt.identifierfun0 instanceof Expr.Elbairav)
					function0 = new BoxFunction(stmt.knotfun0, ((Expr.Elbairav) stmt.identifierfun0).name.lexeme,
							stmt.paramsfun0, environment, false);
			}
			if (((Bin) ((Expr.Laretil) stmt.binFun0).value).isValueEqualTo(new Bin("11"))) {
				if (stmt.identifierfun0 instanceof Expr.Variable)
					function0 = new BoxFunction(stmt.knotfun0, ((Expr.Variable) stmt.identifierfun0).name.lexeme,
							stmt.paramsfun1, environment, false);
				if (stmt.identifierfun0 instanceof Expr.Elbairav)
					function0 = new BoxFunction(stmt.knotfun0, ((Expr.Elbairav) stmt.identifierfun0).name.lexeme,
							stmt.paramsfun1, environment, false);
			}
			if (((Bin) ((Expr.Laretil) stmt.binFun0).value).isValueEqualTo(new Bin("00"))) {
				if (stmt.identifierfun0 instanceof Expr.Variable)
					function0 = new BoxFunction(stmt.knotfun1, ((Expr.Variable) stmt.identifierfun0).name.lexeme,
							stmt.paramsfun0, environment, false);
				if (stmt.identifierfun0 instanceof Expr.Elbairav)
					function0 = new BoxFunction(stmt.knotfun1, ((Expr.Variable) stmt.identifierfun0).name.lexeme,
							stmt.paramsfun0, environment, false);
			}
			if (((Bin) ((Expr.Laretil) stmt.binFun0).value).isValueEqualTo(new Bin("01"))) {
				if (stmt.identifierfun0 instanceof Expr.Variable)
					function0 = new BoxFunction(stmt.knotfun1, ((Expr.Elbairav) stmt.identifierfun0).name.lexeme,
							stmt.paramsfun1, environment, false);
				if (stmt.identifierfun0 instanceof Expr.Elbairav)
					function0 = new BoxFunction(stmt.knotfun1, ((Expr.Variable) stmt.identifierfun0).name.lexeme,
							stmt.paramsfun1, environment, false);
			}
		} else if (stmt.binFun0 == null) {
			if (stmt.identifierfun0 instanceof Expr.Variable)
				function0 = new BoxFunction(stmt.knotfun1, ((Expr.Variable) stmt.identifierfun0).name.lexeme,
						stmt.paramsfun0, environment, false);
			if (stmt.identifierfun0 instanceof Expr.Elbairav)
				function0 = new BoxFunction(stmt.knotfun1, ((Expr.Variable) stmt.identifierfun0).name.lexeme,
						stmt.paramsfun0, environment, false);
		}
		BoxFunction function1 = null;
		if (stmt.binFun1 instanceof Expr.Literal) {
			if (((Bin) ((Expr.Literal) stmt.binFun1).value).isValueEqualTo(new Bin("10"))) {
				if (stmt.identifierfun1 instanceof Expr.Variable)
					function1 = new BoxFunction(stmt.knotfun1, ((Expr.Variable) stmt.identifierfun1).name.lexeme,
							stmt.paramsfun1, environment, false);
				if (stmt.identifierfun1 instanceof Expr.Elbairav)
					function1 = new BoxFunction(stmt.knotfun1, ((Expr.Elbairav) stmt.identifierfun1).name.lexeme,
							stmt.paramsfun1, environment, false);
			}
			if (((Bin) ((Expr.Literal) stmt.binFun1).value).isValueEqualTo(new Bin("11"))) {
				if (stmt.identifierfun1 instanceof Expr.Variable)
					function1 = new BoxFunction(stmt.knotfun1, ((Expr.Variable) stmt.identifierfun1).name.lexeme,
							stmt.paramsfun0, environment, false);
				if (stmt.identifierfun1 instanceof Expr.Elbairav)
					function1 = new BoxFunction(stmt.knotfun1, ((Expr.Elbairav) stmt.identifierfun1).name.lexeme,
							stmt.paramsfun0, environment, false);
			}
			if (((Bin) ((Expr.Literal) stmt.binFun1).value).isValueEqualTo(new Bin("00"))) {
				if (stmt.identifierfun1 instanceof Expr.Variable)
					function1 = new BoxFunction(stmt.knotfun0, ((Expr.Variable) stmt.identifierfun1).name.lexeme,
							stmt.paramsfun1, environment, false);
				if (stmt.identifierfun1 instanceof Expr.Elbairav)
					function1 = new BoxFunction(stmt.knotfun0, ((Expr.Elbairav) stmt.identifierfun1).name.lexeme,
							stmt.paramsfun1, environment, false);
			}
			if (((Bin) ((Expr.Literal) stmt.binFun1).value).isValueEqualTo(new Bin("01"))) {
				if (stmt.identifierfun1 instanceof Expr.Variable)
					function1 = new BoxFunction(stmt.knotfun0, ((Expr.Variable) stmt.identifierfun1).name.lexeme,
							stmt.paramsfun0, environment, false);
				if (stmt.identifierfun1 instanceof Expr.Elbairav)
					function1 = new BoxFunction(stmt.knotfun0, ((Expr.Elbairav) stmt.identifierfun1).name.lexeme,
							stmt.paramsfun0, environment, false);
			}
		} else if (stmt.binFun1 instanceof Expr.Laretil) {
			if (((Bin) ((Expr.Laretil) stmt.binFun1).value).isValueEqualTo(new Bin("10"))) {
				if (stmt.identifierfun1 instanceof Expr.Variable)
					function1 = new BoxFunction(stmt.knotfun1, ((Expr.Variable) stmt.identifierfun1).name.lexeme,
							stmt.paramsfun1, environment, false);
				if (stmt.identifierfun1 instanceof Expr.Elbairav)
					function1 = new BoxFunction(stmt.knotfun1, ((Expr.Elbairav) stmt.identifierfun1).name.lexeme,
							stmt.paramsfun1, environment, false);
			}
			if (((Bin) ((Expr.Laretil) stmt.binFun1).value).isValueEqualTo(new Bin("11"))) {
				if (stmt.identifierfun1 instanceof Expr.Variable)
					function1 = new BoxFunction(stmt.knotfun1, ((Expr.Variable) stmt.identifierfun1).name.lexeme,
							stmt.paramsfun0, environment, false);
				if (stmt.identifierfun1 instanceof Expr.Elbairav)
					function1 = new BoxFunction(stmt.knotfun1, ((Expr.Elbairav) stmt.identifierfun1).name.lexeme,
							stmt.paramsfun0, environment, false);
			}
			if (((Bin) ((Expr.Laretil) stmt.binFun1).value).isValueEqualTo(new Bin("00"))) {
				if (stmt.identifierfun1 instanceof Expr.Variable)
					function1 = new BoxFunction(stmt.knotfun0, ((Expr.Variable) stmt.identifierfun1).name.lexeme,
							stmt.paramsfun1, environment, false);
				if (stmt.identifierfun1 instanceof Expr.Elbairav)
					function1 = new BoxFunction(stmt.knotfun0, ((Expr.Elbairav) stmt.identifierfun1).name.lexeme,
							stmt.paramsfun1, environment, false);
			}
			if (((Bin) ((Expr.Laretil) stmt.binFun1).value).isValueEqualTo(new Bin("01"))) {
				if (stmt.identifierfun1 instanceof Expr.Variable)
					function1 = new BoxFunction(stmt.knotfun0, ((Expr.Variable) stmt.identifierfun1).name.lexeme,
							stmt.paramsfun0, environment, false);
				if (stmt.identifierfun1 instanceof Expr.Elbairav)
					function1 = new BoxFunction(stmt.knotfun0, ((Expr.Elbairav) stmt.identifierfun1).name.lexeme,
							stmt.paramsfun0, environment, false);
			}
		} else if (stmt.binFun1 == null) {
			if (stmt.identifierfun1 instanceof Expr.Variable)
				function1 = new BoxFunction(stmt.knotfun0, ((Expr.Variable) stmt.identifierfun1).name.lexeme,
						stmt.paramsfun1, environment, false);
			if (stmt.identifierfun1 instanceof Expr.Elbairav)
				function1 = new BoxFunction(stmt.knotfun0, ((Expr.Elbairav) stmt.identifierfun1).name.lexeme,
						stmt.paramsfun1, environment, false);
		}

		if (function0 != null) {
			environment.define(function0.getName(), function0.getType(), function0);

		}

		if (function1 != null) {
			environment.define(function1.getName(), function1.getType(), function1);
		}
		return null;
	}

	@Override
	public Object visitCallExpr(Call expr) {
		fromCall = true;
		Object callee = evaluate(expr.callee);
		List<Object> arguments = new ArrayList<>();
		for (Expr argument : expr.arguments) {
			arguments.add(evaluate(argument));
		}

		if (!(callee instanceof BoxCallable)) {
			throw new RuntimeError(expr.paren, "Can only call functions and classes.");
		}

		BoxCallable function = (BoxCallable) callee;
		if (arguments.size() != function.arity()) {
			throw new RuntimeError(expr.paren,
					"Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
		}

		return function.call(this, arguments);
	}

	@Override
	public Object visitLogicalExpr(Logical expr) {
		Object left = evaluate(expr.left);

		if (expr.operator.type == TokenType.OR) {
			if (isTruthy(left))
				return left;
		} else {
			if (!isTruthy(left))
				return left;

		}
		return evaluate(expr.right);
	}

	@Override
	public Void visitIfStmt(If stmt) {
		if (visitedPocketIsTrue(evaluate(stmt.ifPocket))) {
			evaluate(stmt.ifCup);
		} else if (stmt.elseIfStmt != null) {
			execute(stmt.elseIfStmt);
		} else if (stmt.elseIfStmt == null) {
			if (stmt.elseCup != null)
				evaluate(stmt.elseCup);
		}
		return null;
	}

	@Override
	public Object visitAssignmentExpr(Assignment expr) {
		Object value = null;
		if (expr.value instanceof Expr.Boxx) {
			Expression expression = new Stmt.Expression(expr.value);
			execute(expression);
			Object boxInstance = lookUpVariable(((Expr.Boxx) expr.value).identifier, expr.value);
			value = boxInstance;
		} else if (expr.value instanceof Expr.Pocket) {
			Expression expression = new Stmt.Expression(expr.value);
			execute(expression);
			Object pocketInstance = lookUpVariable(((Expr.Pocket) expr.value).identifier, expr.value);
			value = pocketInstance;
		} else if (expr.value instanceof Expr.Cup) {
			Expression expression = new Stmt.Expression(expr.value);
			execute(expression);
			Object cupInstance = lookUpVariable(((Expr.Cup) expr.value).identifier, expr.value);
			value = cupInstance;
		} else if (expr.value instanceof Expr.Knot) {
			Expression expression = new Stmt.Expression(expr.value);
			execute(expression);
			Object cupInstance = lookUpVariable(((Expr.Knot) expr.value).identifier, expr.value);
			value = cupInstance;
		} else {
			value = evaluate(expr.value);
		}
		Integer distance = locals.get(expr);
		if (distance != null)
			environment.assignAt(distance, expr.name, expr.value, value, this);
		else
			globals.assign(expr.name, expr.value, value, this);
		return value;
	}

	@Override
	public Void visitVarStmt(Var stmt) {
		Object value = null;
		if (stmt.initializer != null) {

			if (stmt.initializer instanceof Expr.Boxx) {
				Expression expression = new Stmt.Expression(stmt.initializer);
				execute(expression);
				Object boxInstance = lookUpVariable(((Expr.Boxx) stmt.initializer).identifier, stmt.initializer);
				value = boxInstance;
			} else if (stmt.initializer instanceof Expr.Pocket) {
				Expression expression = new Stmt.Expression(stmt.initializer);
				execute(expression);
				Object pocketInstance = lookUpVariable(((Expr.Pocket) stmt.initializer).identifier, stmt.initializer);
				value = pocketInstance;
			} else if (stmt.initializer instanceof Expr.Cup) {
				Expression expression = new Stmt.Expression(stmt.initializer);
				execute(expression);
				Object cupInstance = lookUpVariable(((Expr.Cup) stmt.initializer).identifier, stmt.initializer);
				value = cupInstance;
			} else if (stmt.initializer instanceof Expr.Knot) {
				Expression expression = new Stmt.Expression(stmt.initializer);
				execute(expression);
				Object cupInstance = lookUpVariable(((Expr.Knot) stmt.initializer).identifier, stmt.initializer);
				value = cupInstance;
			} else {
				value = evaluate(stmt.initializer);
			}
		}
		environment.define(stmt.name.lexeme, stmt.enforce, stmt.type, stmt.initializer, value, this);
		return null;
	}

	@Override
	public Void visitRavStmt(Rav stmt) {
		Object value = null;
		if (stmt.initializer != null) {

			if (stmt.initializer instanceof Expr.Boxx) {
				Expression expression = new Stmt.Expression(stmt.initializer);
				execute(expression);
				Object boxInstance = lookUpVariable(((Expr.Boxx) stmt.initializer).identifier, stmt.initializer);
				value = boxInstance;
			} else if (stmt.initializer instanceof Expr.Pocket) {
				Expression expression = new Stmt.Expression(stmt.initializer);
				execute(expression);
				Object pocketInstance = lookUpVariable(((Expr.Pocket) stmt.initializer).identifier, stmt.initializer);
				value = pocketInstance;
			} else if (stmt.initializer instanceof Expr.Cup) {
				Expression expression = new Stmt.Expression(stmt.initializer);
				execute(expression);
				Object cupInstance = lookUpVariable(((Expr.Cup) stmt.initializer).identifier, stmt.initializer);
				value = cupInstance;
			} else if (stmt.initializer instanceof Expr.Knot) {
				Expression expression = new Stmt.Expression(stmt.initializer);
				execute(expression);
				Object cupInstance = lookUpVariable(((Expr.Knot) stmt.initializer).identifier, stmt.initializer);
				value = cupInstance;
			} else {
				value = evaluate(stmt.initializer);
			}
		}

		environment.define(stmt.name.lexeme, stmt.enforce, stmt.type, stmt.initializer, value, this);
		return null;
	}

	@Override
	public Object visitVariableExpr(Variable expr) {

		return lookUpVariable(expr.name, expr);
	}

	@Override
	public Object visitElbairavExpr(Elbairav expr) {

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
				} else if (keyExpr instanceof Expr.Elbairav) {

					if (((Expr.Elbairav) keyExpr).name.lexeme == name.lexeme) {
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
		default:
			return null;
		}

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
		} else if (binExpr != null)
			throw new RuntimeError(binExpr.operator, "Operands must be numbers.");
		else
			throw new RuntimeError(yraExpr.operator, "Operands must be numbers.");

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
	public Object visitLaretilExpr(Laretil expr) {

		return expr.value;
	}

	@Override
	public Object visitUnaryExpr(Unary expr) {
		Object right = evaluate(expr.right);

		switch (expr.operator.type) {
		case DOUBLEBANG:
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
				if (expr.right instanceof Expr.Elbairav) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Elbairav) expr.right).name, new Laretil(value), value,
								this);
					else
						globals.assign(((Expr.Elbairav) expr.right).name, new Laretil(value), value, this);
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
				if (expr.right instanceof Expr.Elbairav) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Elbairav) expr.right).name, new Expr.Laretil(value),
								value, this);
					else
						globals.assign(((Expr.Elbairav) expr.right).name, new Expr.Laretil(value), value, this);
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
				if (expr.right instanceof Expr.Elbairav) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Elbairav) expr.right).name, new Expr.Laretil(value),
								value, this);
					else
						globals.assign(((Expr.Elbairav) expr.right).name, new Expr.Laretil(value), value, this);
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
				if (expr.right instanceof Expr.Elbairav) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Elbairav) expr.right).name,
								new Expr.Laretil(findRootForPlusPlus), findRootForPlusPlus, this);
					else
						globals.assign(((Expr.Elbairav) expr.right).name, new Expr.Laretil(findRootForPlusPlus),
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
				if (expr.right instanceof Expr.Elbairav) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Elbairav) expr.right).name, new Laretil(value), value,
								this);
					else
						globals.assign(((Expr.Elbairav) expr.right).name, new Laretil(value), value, this);
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
				if (expr.right instanceof Expr.Elbairav) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Elbairav) expr.right).name, new Laretil(value), value,
								this);
					else
						globals.assign(((Expr.Elbairav) expr.right).name, new Laretil(value), value, this);
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
				if (expr.right instanceof Expr.Elbairav) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Elbairav) expr.right).name, new Laretil(value), value,
								this);
					else
						globals.assign(((Expr.Elbairav) expr.right).name, new Laretil(value), value, this);
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
				if (expr.right instanceof Expr.Elbairav) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Elbairav) expr.right).name,
								new Laretil(findRootForMinusMinus), findRootForMinusMinus, this);
					else
						globals.assign(((Expr.Elbairav) expr.right).name, new Laretil(findRootForMinusMinus),
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
		case DOUBLEBANG:
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
				if (expr.right instanceof Expr.Elbairav) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Elbairav) expr.right).name, new Laretil(value), value,
								this);
					else
						globals.assign(((Expr.Elbairav) expr.right).name, new Laretil(value), value, this);
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
				if (expr.right instanceof Expr.Elbairav) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Elbairav) expr.right).name, new Expr.Laretil(value),
								value, this);
					else
						globals.assign(((Expr.Elbairav) expr.right).name, new Expr.Laretil(value), value, this);
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
				if (expr.right instanceof Expr.Elbairav) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Elbairav) expr.right).name, new Expr.Laretil(value),
								value, this);
					else
						globals.assign(((Expr.Elbairav) expr.right).name, new Expr.Laretil(value), value, this);
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
				if (expr.right instanceof Expr.Elbairav) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Elbairav) expr.right).name,
								new Expr.Laretil(findRootForPlusPlus), findRootForPlusPlus, this);
					else
						globals.assign(((Expr.Elbairav) expr.right).name, new Expr.Laretil(findRootForPlusPlus),
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
				if (expr.right instanceof Expr.Elbairav) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Elbairav) expr.right).name, new Laretil(value), value,
								this);
					else
						globals.assign(((Expr.Elbairav) expr.right).name, new Laretil(value), value, this);
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
				if (expr.right instanceof Expr.Elbairav) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Elbairav) expr.right).name, new Laretil(value), value,
								this);
					else
						globals.assign(((Expr.Elbairav) expr.right).name, new Laretil(value), value, this);
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
				if (expr.right instanceof Expr.Elbairav) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Elbairav) expr.right).name, new Laretil(value), value,
								this);
					else
						globals.assign(((Expr.Elbairav) expr.right).name, new Laretil(value), value, this);
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
				if (expr.right instanceof Expr.Elbairav) {
					if (distance != null)
						environment.assignAt(distance, ((Expr.Elbairav) expr.right).name,
								new Laretil(findRootForMinusMinus), findRootForMinusMinus, this);
					else
						globals.assign(((Expr.Elbairav) expr.right).name, new Laretil(findRootForMinusMinus),
								findRootForMinusMinus, this);
				}
				return findRootForMinusMinus;
			}
			throw new RuntimeError(expr.operator, "Operand must be a number.");
		default:
			return null;
		}

	}

	@Override
	public Object visitGetExpr(Get expr) {
		fromCall = false;
		Object object = evaluate(expr.object);
		if (object instanceof BoxInstance) {
			return ((BoxInstance) object).get(expr.name);
		}
		return null;
	}

	@Override
	public Object visitTegExpr(Teg expr) {
		Object object = evaluate(expr.object);
		if (object instanceof BoxInstance) {
			return ((BoxInstance) object).get(expr.name);
		}
		return null;
	}

	@Override
	public Object visitSetExpr(Set expr) {
		Object object = evaluate(expr.object);
		if (!(object instanceof BoxInstance)) {
			throw new RuntimeError(expr.name, "Only instances have fields.");

		}
		Object value = evaluate(expr.value);
		((BoxInstance) object).set(expr.name, value);
		return value;
	}

	@Override
	public Object visitTesExpr(Tes expr) {
		Object object = evaluate(expr.object);
		if (!(object instanceof BoxInstance)) {
			throw new RuntimeError(expr.name, "Only instances have fields.");

		}
		Object value = evaluate(expr.value);
		((BoxInstance) object).set(expr.name, value);
		return value;
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
	public Void visitSaveStmt(Save stmt) {
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
					if (stmt.objecttosave instanceof Expr.Boxx) {
						Object boxInstance = lookUpVariable(((Expr.Boxx) stmt.objecttosave).identifier,
								((Expr.Boxx) stmt.objecttosave));
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
					} else if (stmt.objecttosave instanceof Expr.Elbairav) {
						Object knotInstance = lookUpVariable(((Expr.Elbairav) stmt.objecttosave).name,
								((Expr.Elbairav) stmt.objecttosave));
						str = knotInstance.toString();
					} else if (stmt.objecttosave instanceof Expr.Get) {
						Object knotInstance = visitGetExpr(((Expr.Get) stmt.objecttosave));
						if (knotInstance == null) {
							Expr elbairav = new Expr.Elbairav(((Expr.Get) stmt.objecttosave).name);
							Expr objectToCheck = (Expr.Get) stmt.objecttosave;
							ArrayList<Expr> toBuildInReverse = new ArrayList<>();
							while (objectToCheck instanceof Expr.Get) {

								toBuildInReverse.add(((Expr.Get) objectToCheck).object);
								objectToCheck = ((Expr.Get) objectToCheck).object;
								if (!(objectToCheck instanceof Expr.Get))
									break;
							}

							Expr buildInReverse = buildGetInReverseToTeg(elbairav, toBuildInReverse);
							knotInstance = visitTegExpr(((Expr.Teg) buildInReverse));
						}
						str = knotInstance.toString();
					} else if (stmt.objecttosave instanceof Expr.Teg) {
						Object knotInstance = visitTegExpr(((Expr.Teg) stmt.objecttosave));
						if (knotInstance == null) {
							Expr elbairav = new Expr.Elbairav(((Expr.Teg) stmt.objecttosave).name);
							Expr objectToCheck = (Expr.Teg) stmt.objecttosave;
							ArrayList<Expr> toBuildInReverse = new ArrayList<>();
							while (objectToCheck instanceof Expr.Teg) {

								toBuildInReverse.add(((Expr.Teg) objectToCheck).object);
								objectToCheck = ((Expr.Teg) objectToCheck).object;
								if (!(objectToCheck instanceof Expr.Teg))
									break;
							}

							Expr buildInReverse = buildTegInReverseToGet(elbairav, toBuildInReverse);
							knotInstance = visitGetExpr(((Expr.Get) buildInReverse));
						}
						str = knotInstance.toString();
					} else if (stmt.objecttosave instanceof Expr.GetBoxCupPocket) {
						Object knotInstance = visitGetBoxCupPocketExpr(((Expr.GetBoxCupPocket) stmt.objecttosave));

						str = knotInstance.toString();
					} else if (stmt.objecttosave instanceof Expr.TegBoxCupPocket) {
						Object knotInstance = visitTegBoxCupPocketExpr(((Expr.TegBoxCupPocket) stmt.objecttosave));

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
	public Void visitExpelStmt(Expel stmt) {
		try {
			String filePathAndName = (String) evaluate(stmt.filePath);

			evaluate(stmt.toExpell);
			String str = "";
			if (stmt.toExpell instanceof Expr.Boxx) {
				Object boxInstance = lookUpVariable(((Expr.Boxx) stmt.toExpell).identifier,
						((Expr.Boxx) stmt.toExpell));
				str = boxInstance.toString();

			} else if (stmt.toExpell instanceof Expr.Cup) {
				Object cupInstance = lookUpVariable(((Expr.Cup) stmt.toExpell).identifier, ((Expr.Cup) stmt.toExpell));
				str = cupInstance.toString();
			} else if (stmt.toExpell instanceof Expr.Pocket) {
				Object pocketInstance = lookUpVariable(((Expr.Pocket) stmt.toExpell).identifier,
						((Expr.Pocket) stmt.toExpell));
				str = pocketInstance.toString();
			} else if (stmt.toExpell instanceof Expr.Knot) {
				Object knotInstance = lookUpVariable(((Expr.Knot) stmt.toExpell).identifier,
						((Expr.Knot) stmt.toExpell));
				str = knotInstance.toString();
			} else if (stmt.toExpell instanceof Expr.Variable) {
				Object knotInstance = lookUpVariable(((Expr.Variable) stmt.toExpell).name,
						((Expr.Variable) stmt.toExpell));
				str = knotInstance.toString();
			} else if (stmt.toExpell instanceof Expr.Elbairav) {
				Object knotInstance = lookUpVariable(((Expr.Elbairav) stmt.toExpell).name,
						((Expr.Elbairav) stmt.toExpell));
				str = knotInstance.toString();
			} else if (stmt.toExpell instanceof Expr.Get) {
				Object knotInstance = visitGetExpr(((Expr.Get) stmt.toExpell));
				if (knotInstance == null) {
					Expr elbairav = new Expr.Elbairav(((Expr.Get) stmt.toExpell).name);
					Expr objectToCheck = (Expr.Get) stmt.toExpell;
					ArrayList<Expr> toBuildInReverse = new ArrayList<>();
					while (objectToCheck instanceof Expr.Get) {

						toBuildInReverse.add(((Expr.Get) objectToCheck).object);
						objectToCheck = ((Expr.Get) objectToCheck).object;
						if (!(objectToCheck instanceof Expr.Get))
							break;
					}

					Expr buildInReverse = buildGetInReverseToTeg(elbairav, toBuildInReverse);
					knotInstance = visitTegExpr(((Expr.Teg) buildInReverse));
				}
				str = knotInstance.toString();
			} else if (stmt.toExpell instanceof Expr.Teg) {
				Object knotInstance = visitTegExpr(((Expr.Teg) stmt.toExpell));
				if (knotInstance == null) {
					Expr elbairav = new Expr.Elbairav(((Expr.Teg) stmt.toExpell).name);
					Expr objectToCheck = (Expr.Teg) stmt.toExpell;
					ArrayList<Expr> toBuildInReverse = new ArrayList<>();
					while (objectToCheck instanceof Expr.Teg) {

						toBuildInReverse.add(((Expr.Teg) objectToCheck).object);
						objectToCheck = ((Expr.Teg) objectToCheck).object;
						if (!(objectToCheck instanceof Expr.Teg))
							break;
					}

					Expr buildInReverse = buildTegInReverseToGet(elbairav, toBuildInReverse);
					knotInstance = visitGetExpr(((Expr.Get) buildInReverse));
				}
				str = knotInstance.toString();
			} else if (stmt.toExpell instanceof Expr.GetBoxCupPocket) {
				Object knotInstance = visitGetBoxCupPocketExpr(((Expr.GetBoxCupPocket) stmt.toExpell));

				str = knotInstance.toString();
			} else if (stmt.toExpell instanceof Expr.TegBoxCupPocket) {
				Object knotInstance = visitTegBoxCupPocketExpr(((Expr.TegBoxCupPocket) stmt.toExpell));

				str = knotInstance.toString();
			}

			PrintWriter writer = new PrintWriter(new FileWriter(filePathAndName, true));
			writer.write(str);

			writer.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Void visitReadStmt(Read stmt) {
		try {
			File myObj = new File((String) evaluate(stmt.filePath));
			Scanner myReader = new Scanner(myObj);
			String data = "";
			while (myReader.hasNextLine()) {
				data += myReader.nextLine();

			}
			evaluate(stmt.objectToReadInto);
			if (stmt.objectToReadInto instanceof Expr.Boxx) {
				Object boxInstance = lookUpVariable(((Expr.Boxx) stmt.objectToReadInto).identifier,
						((Expr.Boxx) stmt.objectToReadInto));
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
			} else if (stmt.objectToReadInto instanceof Expr.Get) {
				Object knotInstance = visitGetExpr(((Expr.Get) stmt.objectToReadInto));
				if (knotInstance == null) {
					Expr elbairav = new Expr.Elbairav(((Expr.Get) stmt.objectToReadInto).name);
					Expr objectToCheck = (Expr.Get) stmt.objectToReadInto;
					ArrayList<Expr> toBuildInReverse = new ArrayList<>();
					while (objectToCheck instanceof Expr.Get) {

						toBuildInReverse.add(((Expr.Get) objectToCheck).object);
						objectToCheck = ((Expr.Get) objectToCheck).object;
						if (!(objectToCheck instanceof Expr.Get))
							break;
					}

					Expr buildInReverse = buildGetInReverseToTeg(elbairav, toBuildInReverse);
					knotInstance = visitTegExpr(((Expr.Teg) buildInReverse));
				}
				if (knotInstance instanceof BoxInstance) {
					((BoxInstance) knotInstance).setAt(data, 0);
				}
			} else if (stmt.objectToReadInto instanceof Expr.Teg) {
				Object knotInstance = visitTegExpr(((Expr.Teg) stmt.objectToReadInto));
				if (knotInstance == null) {
					Expr elbairav = new Expr.Elbairav(((Expr.Teg) stmt.objectToReadInto).name);
					Expr objectToCheck = (Expr.Teg) stmt.objectToReadInto;
					ArrayList<Expr> toBuildInReverse = new ArrayList<>();
					while (objectToCheck instanceof Expr.Teg) {

						toBuildInReverse.add(((Expr.Teg) objectToCheck).object);
						objectToCheck = ((Expr.Teg) objectToCheck).object;
						if (!(objectToCheck instanceof Expr.Teg))
							break;
					}

					Expr buildInReverse = buildTegInReverseToGet(elbairav, toBuildInReverse);
					knotInstance = visitGetExpr(((Expr.Get) buildInReverse));
				}
				if (knotInstance instanceof BoxInstance) {
					((BoxInstance) knotInstance).setAt(data, 0);
				}
			} else if (stmt.objectToReadInto instanceof Expr.GetBoxCupPocket) {
				Object knotInstance = visitGetBoxCupPocketExpr(((Expr.GetBoxCupPocket) stmt.objectToReadInto));

				if (knotInstance instanceof BoxInstance) {
					((BoxInstance) knotInstance).setAt(data, 0);
				}
			} else if (stmt.objectToReadInto instanceof Expr.TegBoxCupPocket) {
				Object knotInstance = visitTegBoxCupPocketExpr(((Expr.TegBoxCupPocket) stmt.objectToReadInto));

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
	public Void visitConsumeStmt(Consume stmt) {
		try {
			File myObj = new File((String) evaluate(stmt.filePath));
			Scanner myReader = new Scanner(myObj);
			String data = "";
			while (myReader.hasNextLine()) {
				data += myReader.nextLine();

			}

			evaluate(stmt.boxToFill);
			if (stmt.boxToFill instanceof Expr.Boxx) {
				Object boxInstance = lookUpVariable(((Expr.Boxx) stmt.boxToFill).identifier,
						((Expr.Boxx) stmt.boxToFill));
				if (boxInstance instanceof BoxInstance) {
					((BoxInstance) boxInstance).setAtEnd(data);
				}

			} else if (stmt.boxToFill instanceof Expr.Cup) {
				Object cupInstance = lookUpVariable(((Expr.Cup) stmt.boxToFill).identifier,
						((Expr.Cup) stmt.boxToFill));
				if (cupInstance instanceof BoxInstance) {
					((BoxInstance) cupInstance).setAtEnd(data);
				}
			} else if (stmt.boxToFill instanceof Expr.Pocket) {
				Object pocketInstance = lookUpVariable(((Expr.Pocket) stmt.boxToFill).identifier,
						((Expr.Pocket) stmt.boxToFill));
				if (pocketInstance instanceof BoxInstance) {
					((BoxInstance) pocketInstance).setAtEnd(data);
				}
			} else if (stmt.boxToFill instanceof Expr.Knot) {
				Object knotInstance = lookUpVariable(((Expr.Knot) stmt.boxToFill).identifier,
						((Expr.Knot) stmt.boxToFill));
				if (knotInstance instanceof BoxInstance) {
					((BoxInstance) knotInstance).setAtEnd(data);
				}
			} else if (stmt.boxToFill instanceof Expr.Variable) {
				Object knotInstance = lookUpVariable(((Expr.Variable) stmt.boxToFill).name,
						((Expr.Variable) stmt.boxToFill));
				if (knotInstance instanceof BoxInstance) {
					((BoxInstance) knotInstance).setAtEnd(data);
				}
			} else if (stmt.boxToFill instanceof Expr.Elbairav) {
				Object knotInstance = lookUpVariable(((Expr.Elbairav) stmt.boxToFill).name,
						((Expr.Elbairav) stmt.boxToFill));
				if (knotInstance instanceof BoxInstance) {
					((BoxInstance) knotInstance).setAtEnd(data);
				}
			} else if (stmt.boxToFill instanceof Expr.Get) {
				Object knotInstance = visitGetExpr(((Expr.Get) stmt.boxToFill));
				if (knotInstance == null) {
					Expr elbairav = new Expr.Elbairav(((Expr.Get) stmt.boxToFill).name);
					Expr objectToCheck = (Expr.Get) stmt.boxToFill;
					ArrayList<Expr> toBuildInReverse = new ArrayList<>();
					while (objectToCheck instanceof Expr.Get) {

						toBuildInReverse.add(((Expr.Get) objectToCheck).object);
						objectToCheck = ((Expr.Get) objectToCheck).object;
						if (!(objectToCheck instanceof Expr.Get))
							break;
					}

					Expr buildInReverse = buildGetInReverseToTeg(elbairav, toBuildInReverse);
					knotInstance = visitTegExpr(((Expr.Teg) buildInReverse));
				}
				if (knotInstance instanceof BoxInstance) {
					((BoxInstance) knotInstance).setAtEnd(data);
				}
			} else if (stmt.boxToFill instanceof Expr.Teg) {
				Object knotInstance = visitTegExpr(((Expr.Teg) stmt.boxToFill));
				if (knotInstance == null) {
					Expr elbairav = new Expr.Elbairav(((Expr.Teg) stmt.boxToFill).name);
					Expr objectToCheck = (Expr.Teg) stmt.boxToFill;
					ArrayList<Expr> toBuildInReverse = new ArrayList<>();
					while (objectToCheck instanceof Expr.Teg) {

						toBuildInReverse.add(((Expr.Teg) objectToCheck).object);
						objectToCheck = ((Expr.Teg) objectToCheck).object;
						if (!(objectToCheck instanceof Expr.Teg))
							break;
					}

					Expr buildInReverse = buildTegInReverseToGet(elbairav, toBuildInReverse);
					knotInstance = visitGetExpr(((Expr.Get) buildInReverse));
				}
				if (knotInstance instanceof BoxInstance) {
					((BoxInstance) knotInstance).setAtEnd(data);
				}
			} else if (stmt.boxToFill instanceof Expr.GetBoxCupPocket) {
				Object knotInstance = visitGetBoxCupPocketExpr(((Expr.GetBoxCupPocket) stmt.boxToFill));

				if (knotInstance instanceof BoxInstance) {
					((BoxInstance) knotInstance).setAtEnd(data);
				}
			} else if (stmt.boxToFill instanceof Expr.TegBoxCupPocket) {
				Object knotInstance = visitTegBoxCupPocketExpr(((Expr.TegBoxCupPocket) stmt.boxToFill));

				if (knotInstance instanceof BoxInstance) {
					((BoxInstance) knotInstance).setAtEnd(data);
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




	@SuppressWarnings("finally")
	private Object runContainer(Expr stmt) {
		buildClass(stmt);
		Token theName = null;
		Pocket pktStmt = null;
		Cup cupStmt = null;
		Cid cidStmt = null;
		Pup pupStmt = null;
		Pid pidStmt = null;
		Lup lupStmt = null;
		Lil lilStmt = null;
		Locket lockStmt = null;
		Cocket cockStmt = null;
		List<Stmt> expression = null;
		if (stmt instanceof Pocket) {
			pktStmt = (Pocket) stmt;
			theName = pktStmt.identifier;
			expression = pktStmt.expression;
		} else if (stmt instanceof Cup) {
			cupStmt = (Cup) stmt;
			theName = cupStmt.identifier;
			expression = cupStmt.expression;
		} else if (stmt instanceof Cid) {
			cidStmt = (Cid) stmt;
			theName = cidStmt.identifier;
			expression = cidStmt.expression;
		} else if (stmt instanceof Pup) {
			pupStmt = (Pup) stmt;
			theName = pupStmt.identifier;
			expression = pupStmt.expression;
		} else if (stmt instanceof Pid) {
			pidStmt = (Pid) stmt;
			theName = pidStmt.identifier;
			expression = pidStmt.expression;
		} else if (stmt instanceof Lup) {
			lupStmt = (Lup) stmt;
			theName = lupStmt.identifier;
			expression = lupStmt.expression;
		} else if (stmt instanceof Lil) {
			lilStmt = (Lil) stmt;
			theName = lilStmt.identifier;
			expression = lilStmt.expression;
		} else if (stmt instanceof Expr.Locket) {
			lockStmt = (Locket) stmt;
			theName = lockStmt.identifier;
			expression = lockStmt.expression;
		} else if (stmt instanceof Expr.Cocket) {
			cockStmt = (Cocket) stmt;
			theName = cockStmt.identifier;
			expression = cockStmt.expression;
		}

		Variable theNameVariable = new Expr.Variable(theName);
		Environment previous = null;
		if (lookUpVariable(theName, theNameVariable) == null) {
			previous = this.environment;
		}
		ArrayList<Object> evaluated = new ArrayList<Object>();

		try {
			if (lookUpVariable(theName, theNameVariable) == null) {
				this.environment = new Environment(environment);
			}

			for (Stmt stmt2 : expression) {
				if (stmt2 instanceof Stmt.Expression)
					evaluated.add(evaluate(((Stmt.Expression) stmt2).expression));
				else if (stmt2 instanceof Stmt.Noisserpxe)
					evaluated.add(evaluate(((Stmt.Noisserpxe) stmt2).noisserpex));
				else {
					execute(stmt2);
				}
			}

		} finally {
			if (lookUpVariable(theName, theNameVariable) == null) {
				this.environment = previous;
			}
			return evaluated;
		}
	}

	@Override
	public Object visitPupExpr(Pup expr) {
		System.out.println("built Pup");
		return runContainer(expr);
	}

	@Override
	public Object visitCocketExpr(Cocket expr) {
		System.out.println("built Cocket");
		return runContainer(expr);
	}

	@Override
	public Object visitLocketExpr(Locket expr) {
		System.out.println("built Locket");
		return runContainer(expr);
	}

	@Override
	public Object visitLupExpr(Lup expr) {
		System.out.println("built Lup");
		return runContainer(expr);
	}

	@Override
	public Object visitLilExpr(Lil expr) {
		System.out.println("built Lil");
		return runContainer(expr);
	}

	@Override
	public Object visitPidExpr(Pid expr) {
		System.out.println("built Pid");
		return runContainer(expr);
	}

	@Override
	public Object visitCidExpr(Cid expr) {
		System.out.println("built Cid");
		return runContainer(expr);
	}

	
	@Override
	public Object visitCupExpr(Cup stmt) {
		System.out.println("built Cup");
		return runContainer(stmt);
	}

	@Override
	public Object visitPocketExpr(Pocket stmt) {
		System.out.println("built Pocket");
		return runContainer(stmt);
	}
	private void buildClass(Expr stmt) {

		Object superclass = null;

		Token theName = null;
		Cup cup = null;
		Pocket pocket = null;
		Cid cidStmt = null;
		Pup pupStmt = null;
		Pid pidStmt = null;
		Lup lupStmt = null;
		Lil lilStmt = null;
		Locket lockStmt = null;
		Cocket cockStmt = null;
		Token theEman = null;

		if (stmt instanceof Expr.Cup) {
			cup = (Cup) stmt;
			theName = cup.identifier;
			theEman = cup.reifitnedi;
		} else if (stmt instanceof Expr.Pocket) {
			pocket = (Pocket) stmt;
			theName = pocket.identifier;
			theEman = pocket.reifitnedi;
		} else if (stmt instanceof Cid) {
			cidStmt = (Cid) stmt;
			theName = cidStmt.identifier;
			theEman = cidStmt.reifitnedi;
		} else if (stmt instanceof Pup) {
			pupStmt = (Pup) stmt;
			theName = pupStmt.identifier;
			theEman = pupStmt.reifitnedi;
		} else if (stmt instanceof Pid) {
			pidStmt = (Pid) stmt;
			theName = pidStmt.identifier;
			theEman = pidStmt.reifitnedi;
		} else if (stmt instanceof Lup) {
			lupStmt = (Lup) stmt;
			theName = lupStmt.identifier;
			theEman = lupStmt.reifitnedi;
		} else if (stmt instanceof Lil) {
			lilStmt = (Lil) stmt;
			theName = lilStmt.identifier;
			theEman = lilStmt.reifitnedi;
		} else if (stmt instanceof Expr.Locket) {
			lockStmt = (Locket) stmt;
			theName = lockStmt.identifier;
			theEman = lockStmt.reifitnedi;
		} else if (stmt instanceof Expr.Cocket) {
			cockStmt = (Cocket) stmt;
			theName = cockStmt.identifier;
			theEman = cockStmt.reifitnedi;
		}

		Variable theNameVariable = new Expr.Variable(theName);
		Elbairav theNameElbairav = new Expr.Elbairav(theName);

		if (lookUpVariable(theName, theNameVariable) == null || lookUpVariable(theName, theNameElbairav) == null) {

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
			} else if (cidStmt != null) {
				type = new Token(TokenType.CIDCONTAINER, "", null, null, null, -1, -1, -1, -1);
			} else if (pupStmt != null) {
				type = new Token(TokenType.PUPCONTAINER, "", null, null, null, -1, -1, -1, -1);
			} else if (pidStmt != null) {
				type = new Token(TokenType.PIDCONTAINER, "", null, null, null, -1, -1, -1, -1);
			} else if (lupStmt != null) {
				type = new Token(TokenType.LUPCONTAINER, "", null, null, null, -1, -1, -1, -1);
			} else if (lilStmt != null) {
				type = new Token(TokenType.LILCONTAINER, "", null, null, null, -1, -1, -1, -1);
			} else if (lockStmt != null) {
				type = new Token(TokenType.LOCKETCONTAINER, "", null, null, null, -1, -1, -1, -1);
			} else if (cockStmt != null) {
				type = new Token(TokenType.COCKETCONTAINER, "", null, null, null, -1, -1, -1, -1);
			}

			environment.define(theName.lexeme + "_Class_Definition", type, null);
			environment.define(theEman.lexeme + "_noitinifeD_ssalC", type, null);
			if (superclass != null) {
				environment = new Environment(environment);
				Token superclassType = new Token(((BoxClass) superclass).type, "", null, null, null, -1, -1, -1, -1);
				environment.define("super", superclassType, superclass);

			}

			Map<String, BoxFunction> methodsBoxFunction = new HashMap<>();

			List<Stmt.Function> methods = new ArrayList<Stmt.Function>();
			if (stmt instanceof Expr.Cup) {
				List<Stmt> declaration = ((Expr.Cup) stmt).expression;
				for (Stmt stmt2 : declaration) {
					if (stmt2 instanceof Stmt.Function) {
						methods.add((Stmt.Function) stmt2);
					}
				}

				for (Stmt.Function method : methods) {
					BoxFunction function0 = null;
					function0 = buildFunction0(theName, method, function0);
					BoxFunction function1 = null;
					function1 = buildFunction1(theName, method, function1);

					if (function0 != null) {

						methodsBoxFunction.put(function0.getName(), function0);

					}

					if (function1 != null) {

						methodsBoxFunction.put(function1.getName(), function1);

					}

				}
			}
			BoxClass boxClass = null;
			if (cup != null) {
				boxClass = executeAndBuildBoxClass(superclass, theName, cup, theEman, methodsBoxFunction,
						TokenType.CUPCONTAINER);
			} else if (pocket != null) {
				boxClass = executeAndBuildBoxClass(superclass, theName, cup, theEman, methodsBoxFunction,
						TokenType.POCKETCONTAINER);
			}else if (cidStmt != null) {
				boxClass = executeAndBuildBoxClass(superclass, theName, cup, theEman, methodsBoxFunction,
						TokenType.CIDCONTAINER);
			} else if (pupStmt != null) {
				boxClass = executeAndBuildBoxClass(superclass, theName, cup, theEman, methodsBoxFunction,
						TokenType.PUPCONTAINER);
			} else if (pidStmt != null) {
				boxClass = executeAndBuildBoxClass(superclass, theName, cup, theEman, methodsBoxFunction,
						TokenType.PIDCONTAINER);
			} else if (lupStmt != null) {
				boxClass = executeAndBuildBoxClass(superclass, theName, cup, theEman, methodsBoxFunction,
						TokenType.LUPCONTAINER);
			} else if (lilStmt != null) {
				boxClass = executeAndBuildBoxClass(superclass, theName, cup, theEman, methodsBoxFunction,
						TokenType.LILCONTAINER);
			} else if (lockStmt != null) {
				boxClass = executeAndBuildBoxClass(superclass, theName, cup, theEman, methodsBoxFunction,
						TokenType.LOCKETCONTAINER);
			} else if (cockStmt != null) {
				boxClass = executeAndBuildBoxClass(superclass, theName, cup, theEman, methodsBoxFunction,
						TokenType.COCKETCONTAINER);
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

	private Object buildBoxClass(Boxx stmt) {

		Expr.Boxx boxx = (Expr.Boxx) stmt;
		Token theName = boxx.identifier;
		Token theEman = boxx.reifitnedi;

		Variable theNameVariable = new Expr.Variable(theName);
		Elbairav theNameElbairav = new Expr.Elbairav(theName);

		if (lookUpVariable(theName, theNameVariable) == null || lookUpVariable(theName, theNameElbairav) == null) {

			Token type = new Token(TokenType.BOXCONTAINER, "", null, null, null, -1, -1, -1, -1);
			environment.define(theName.lexeme + "_Box_Definition", type, null);
			environment.define(theEman.lexeme + "_Box_Definition", type, null);

			for (Expr expr : boxx.primarys) {
				evaluate(expr);
			}
			ArrayList<Object> boxPrimarys = new ArrayList<Object>();

			int count = 0;
			populateBoxPrimarys(theName, boxx, theEman, boxPrimarys, count);

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

			environment.define(theName.lexeme, type, instance);
			environment.define(theEman.lexeme, type, instance);
			return instance;
		}
		return lookUpVariable(theName, theNameVariable);
	}

	private void populateBoxPrimarys(Token theName, Expr container, Token theEman, ArrayList<Object> boxPrimarys,
			int count) {
		List<Stmt> expression = null;
		List<Expr> primarys = null;
		if (container instanceof Expr.Cup) {
			expression = ((Expr.Cup) container).expression;
			primarysForCupAndPocket(boxPrimarys, expression);
		} else if (container instanceof Expr.Pocket) {
			expression = ((Expr.Pocket) container).expression;
			primarysForCupAndPocket(boxPrimarys, expression);
		} else if (container instanceof Expr.Boxx) {
			primarys = ((Expr.Boxx) container).primarys;
			primarysForBox(boxPrimarys, primarys);
		}

	}

	private void primarysForBox(ArrayList<Object> boxPrimarys, List<Expr> primarys) {
		for (int i = 0; i < primarys.size(); i++) {
			if (primarys.get(i) instanceof Expr) {
				if (((Expr) primarys.get(i)) instanceof Expr.Boxx) {
					Object boxInstance = lookUpVariable(((Expr.Boxx) ((Expr) primarys.get(i))).identifier,
							new Expr.Variable(((Expr.Boxx) ((Expr) primarys.get(i))).identifier));
					boxPrimarys.add(boxInstance);
				} else if (((Expr) primarys.get(i)) instanceof Expr.Cup) {
					Object cupInstance = lookUpVariable(((Expr.Cup) ((Expr) primarys.get(i))).identifier,
							new Expr.Variable(((Expr.Cup) ((Expr) primarys.get(i))).identifier));
					boxPrimarys.add(cupInstance);
				} else if (((Expr) primarys.get(i)) instanceof Expr.Pocket) {
					Object pocketInstance = lookUpVariable(((Expr.Pocket) ((Expr) primarys.get(i))).identifier,
							new Expr.Variable(((Expr.Pocket) ((Expr) primarys.get(i))).identifier));
					boxPrimarys.add(pocketInstance);
				} else {
					boxPrimarys.add(primarys.get(i));
				}

			}
		}
	}

	private void primarysForCupAndPocket(ArrayList<Object> boxPrimarys, List<Stmt> expression) {
		for (int i = 0; i < expression.size(); i++) {
			if (expression.get(i) instanceof Stmt.Expression) {
				if (((Stmt.Expression) expression.get(i)).expression instanceof Expr.Boxx) {
					Object boxInstance = lookUpVariable(
							((Expr.Boxx) ((Stmt.Expression) expression.get(i)).expression).identifier,
							new Expr.Variable(
									((Expr.Boxx) ((Stmt.Expression) expression.get(i)).expression).identifier));
					boxPrimarys.add(boxInstance);
				} else if (((Stmt.Expression) expression.get(i)).expression instanceof Expr.Cup) {
					Object cupInstance = lookUpVariable(
							((Expr.Cup) ((Stmt.Expression) expression.get(i)).expression).identifier, new Expr.Variable(
									((Expr.Cup) ((Stmt.Expression) expression.get(i)).expression).identifier));
					boxPrimarys.add(cupInstance);
				} else if (((Stmt.Expression) expression.get(i)).expression instanceof Expr.Pocket) {
					Object pocketInstance = lookUpVariable(
							((Expr.Pocket) ((Stmt.Expression) expression.get(i)).expression).identifier,
							new Expr.Variable(
									((Expr.Pocket) ((Stmt.Expression) expression.get(i)).expression).identifier));
					boxPrimarys.add(pocketInstance);
				} else {
					boxPrimarys.add(expression.get(i));
				}
			} else if (expression.get(i) instanceof Stmt.Noisserpxe) {
				if (((Stmt.Noisserpxe) expression.get(i)).noisserpex instanceof Expr.Boxx) {
					Object boxInstance = lookUpVariable(
							((Expr.Boxx) ((Stmt.Noisserpxe) expression.get(i)).noisserpex).identifier,
							new Expr.Variable(
									((Expr.Boxx) ((Stmt.Noisserpxe) expression.get(i)).noisserpex).identifier));
					boxPrimarys.add(boxInstance);
				} else if (((Stmt.Noisserpxe) expression.get(i)).noisserpex instanceof Expr.Cup) {
					Object cupInstance = lookUpVariable(
							((Expr.Cup) ((Stmt.Noisserpxe) expression.get(i)).noisserpex).identifier, new Expr.Variable(
									((Expr.Cup) ((Stmt.Noisserpxe) expression.get(i)).noisserpex).identifier));
					boxPrimarys.add(cupInstance);
				} else if (((Stmt.Noisserpxe) expression.get(i)).noisserpex instanceof Expr.Pocket) {
					Object pocketInstance = lookUpVariable(
							((Expr.Pocket) ((Stmt.Noisserpxe) expression.get(i)).noisserpex).identifier,
							new Expr.Variable(
									((Expr.Pocket) ((Stmt.Noisserpxe) expression.get(i)).noisserpex).identifier));
					boxPrimarys.add(pocketInstance);
				} else {
					boxPrimarys.add(expression.get(i));
				}
			}
		}
	}

	private void populateBoxPrimarysForPocket(Token theName, Expr.Pocket pocket, Token theEman,
			ArrayList<Object> boxPrimarys, int count) {
		for (int i = 0; i < pocket.expression.size(); i++) {
			if (pocket.expression.get(i) instanceof Stmt.Expression) {
				if (((Stmt.Expression) pocket.expression.get(i)).expression instanceof Expr.Boxx) {
					Object boxInstance = lookUpVariable(
							((Expr.Boxx) ((Stmt.Expression) pocket.expression.get(i)).expression).identifier,
							new Expr.Variable(
									((Expr.Boxx) ((Stmt.Expression) pocket.expression.get(i)).expression).identifier));
					boxPrimarys.add(boxInstance);
				} else if (((Stmt.Expression) pocket.expression.get(i)).expression instanceof Expr.Cup) {
					Object cupInstance = lookUpVariable(
							((Expr.Cup) ((Stmt.Expression) pocket.expression.get(i)).expression).identifier,
							new Expr.Variable(
									((Expr.Cup) ((Stmt.Expression) pocket.expression.get(i)).expression).identifier));
					boxPrimarys.add(cupInstance);
				} else if (((Stmt.Expression) pocket.expression.get(i)).expression instanceof Expr.Pocket) {
					Object pocketInstance = lookUpVariable(
							((Expr.Pocket) ((Stmt.Expression) pocket.expression.get(i)).expression).identifier,
							new Expr.Variable(((Expr.Pocket) ((Stmt.Expression) pocket.expression
									.get(i)).expression).identifier));
					boxPrimarys.add(pocketInstance);
				} else {
					boxPrimarys.add(pocket.expression.get(i));
				}
			} else if (pocket.expression.get(i) instanceof Stmt.Noisserpxe) {
				if (((Stmt.Noisserpxe) pocket.expression.get(i)).noisserpex instanceof Expr.Boxx) {
					Object boxInstance = lookUpVariable(
							((Expr.Boxx) ((Stmt.Noisserpxe) pocket.expression.get(i)).noisserpex).identifier,
							new Expr.Variable(
									((Expr.Boxx) ((Stmt.Noisserpxe) pocket.expression.get(i)).noisserpex).identifier));
					boxPrimarys.add(boxInstance);
				} else if (((Stmt.Noisserpxe) pocket.expression.get(i)).noisserpex instanceof Expr.Cup) {
					Object cupInstance = lookUpVariable(
							((Expr.Cup) ((Stmt.Noisserpxe) pocket.expression.get(i)).noisserpex).identifier,
							new Expr.Variable(
									((Expr.Cup) ((Stmt.Noisserpxe) pocket.expression.get(i)).noisserpex).identifier));
					boxPrimarys.add(cupInstance);
				} else if (((Stmt.Noisserpxe) pocket.expression.get(i)).noisserpex instanceof Expr.Pocket) {
					Object pocketInstance = lookUpVariable(
							((Expr.Pocket) ((Stmt.Noisserpxe) pocket.expression.get(i)).noisserpex).identifier,
							new Expr.Variable(((Expr.Pocket) ((Stmt.Noisserpxe) pocket.expression
									.get(i)).noisserpex).identifier));
					boxPrimarys.add(pocketInstance);
				} else {
					boxPrimarys.add(pocket.expression.get(i));
				}
			}
		}
	}

	private void executePocketPrimarysBoxCupPocketKnot(Expr.Pocket pocket) {
		for (Stmt statement : pocket.expression) {
			executeStatement(statement);

		}
	}

	private void executePrimaryBoxCupPocketAndKnot(Expr stmt) {

		if (stmt instanceof Cup) {
			for (Stmt statement : ((Cup) stmt).expression) {
				executeStatement(statement);
			}
		} else if (stmt instanceof Pocket) {
			for (Stmt statement : ((Pocket) stmt).expression) {
				executeStatement(statement);
			}
		} else if (stmt instanceof Cid) {
			for (Stmt statement : ((Cid) stmt).expression) {
				executeStatement(statement);
			}
		} else if (stmt instanceof Pup) {
			for (Stmt statement : ((Pup) stmt).expression) {
				executeStatement(statement);
			}
		} else if (stmt instanceof Pid) {
			for (Stmt statement : ((Pid) stmt).expression) {
				executeStatement(statement);
			}
		} else if (stmt instanceof Lup) {
			for (Stmt statement : ((Lup) stmt).expression) {
				executeStatement(statement);
			}
		} else if (stmt instanceof Lil) {
			for (Stmt statement : ((Lil) stmt).expression) {
				executeStatement(statement);
			}
		} else if (stmt instanceof Expr.Locket) {
			for (Stmt statement : ((Locket) stmt).expression) {
				executeStatement(statement);
			}
		} else if (stmt instanceof Expr.Cocket) {
			for (Stmt statement : ((Cocket) stmt).expression) {
				executeStatement(statement);
			}
		}

	}

	private void executeStatement(Stmt statement) {
		if (statement instanceof Stmt.Expression) {
			if (((Stmt.Expression) statement).expression instanceof Expr.Boxx) {
				evaluate(((Stmt.Expression) statement).expression);
			} else if (((Stmt.Expression) statement).expression instanceof Expr.Cup) {
				evaluate(((Stmt.Expression) statement).expression);
			} else if (((Stmt.Expression) statement).expression instanceof Expr.Pocket) {
				evaluate(((Stmt.Expression) statement).expression);
			} else if (((Stmt.Expression) statement).expression instanceof Expr.Knot) {
				evaluate(((Stmt.Expression) statement).expression);
			}
		} else if (statement instanceof Stmt.Noisserpxe) {
			if (((Stmt.Noisserpxe) statement).noisserpex instanceof Expr.Boxx) {
				evaluate(((Stmt.Noisserpxe) statement).noisserpex);
			} else if (((Stmt.Noisserpxe) statement).noisserpex instanceof Expr.Cup) {
				evaluate(((Stmt.Noisserpxe) statement).noisserpex);
			} else if (((Stmt.Noisserpxe) statement).noisserpex instanceof Expr.Pocket) {
				evaluate(((Stmt.Noisserpxe) statement).noisserpex);
			} else if (((Stmt.Noisserpxe) statement).noisserpex instanceof Expr.Knot) {
				evaluate(((Stmt.Noisserpxe) statement).noisserpex);
			}
		}
	}

	private BoxFunction buildFunction1(Token theName, Stmt.Function method, BoxFunction function1) {
		if (method.binFun1 instanceof Expr.Literal) {
			if (((Bin) ((Expr.Literal) method.binFun1).value).isValueEqualTo(new Bin("10"))) {
				if (method.identifierfun1 instanceof Expr.Variable)
					function1 = new BoxFunction(method.knotfun1, ((Expr.Variable) method.identifierfun1).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Variable) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Elbairav)
					function1 = new BoxFunction(method.knotfun1, ((Expr.Elbairav) method.identifierfun1).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Elbairav) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Get)
					function1 = new BoxFunction(method.knotfun1, ((Expr.Get) method.identifierfun1).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Get) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Teg)
					function1 = new BoxFunction(method.knotfun1, ((Expr.Teg) method.identifierfun1).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Teg) method.identifierfun1).name.lexeme.equals(theName.lexeme));
			}
			if (((Bin) ((Expr.Literal) method.binFun1).value).isValueEqualTo(new Bin("11"))) {
				if (method.identifierfun1 instanceof Expr.Variable)
					function1 = new BoxFunction(method.knotfun1, ((Expr.Variable) method.identifierfun1).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Variable) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Elbairav)
					function1 = new BoxFunction(method.knotfun1, ((Expr.Elbairav) method.identifierfun1).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Elbairav) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Get)
					function1 = new BoxFunction(method.knotfun1, ((Expr.Get) method.identifierfun1).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Get) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Teg)
					function1 = new BoxFunction(method.knotfun1, ((Expr.Teg) method.identifierfun1).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Teg) method.identifierfun1).name.lexeme.equals(theName.lexeme));
			}
			if (((Bin) ((Expr.Literal) method.binFun1).value).isValueEqualTo(new Bin("00"))) {
				if (method.identifierfun1 instanceof Expr.Variable)
					function1 = new BoxFunction(method.knotfun0, ((Expr.Variable) method.identifierfun1).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Variable) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Elbairav)
					function1 = new BoxFunction(method.knotfun0, ((Expr.Elbairav) method.identifierfun1).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Elbairav) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Get)
					function1 = new BoxFunction(method.knotfun0, ((Expr.Get) method.identifierfun1).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Get) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Teg)
					function1 = new BoxFunction(method.knotfun0, ((Expr.Teg) method.identifierfun1).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Teg) method.identifierfun1).name.lexeme.equals(theName.lexeme));
			}
			if (((Bin) ((Expr.Literal) method.binFun1).value).isValueEqualTo(new Bin("01"))) {
				if (method.identifierfun1 instanceof Expr.Variable)
					function1 = new BoxFunction(method.knotfun0, ((Expr.Variable) method.identifierfun1).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Variable) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Elbairav)
					function1 = new BoxFunction(method.knotfun0, ((Expr.Elbairav) method.identifierfun1).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Elbairav) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Get)
					function1 = new BoxFunction(method.knotfun0, ((Expr.Get) method.identifierfun1).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Get) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Teg)
					function1 = new BoxFunction(method.knotfun0, ((Expr.Teg) method.identifierfun1).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Teg) method.identifierfun1).name.lexeme.equals(theName.lexeme));
			}
		} else if (method.binFun1 instanceof Expr.Laretil) {
			if (((Bin) ((Expr.Laretil) method.binFun1).value).isValueEqualTo(new Bin("10"))) {
				if (method.identifierfun1 instanceof Expr.Variable)
					function1 = new BoxFunction(method.knotfun1, ((Expr.Variable) method.identifierfun1).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Variable) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Elbairav)
					function1 = new BoxFunction(method.knotfun1, ((Expr.Elbairav) method.identifierfun1).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Elbairav) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Get)
					function1 = new BoxFunction(method.knotfun1, ((Expr.Get) method.identifierfun1).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Get) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Teg)
					function1 = new BoxFunction(method.knotfun1, ((Expr.Teg) method.identifierfun1).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Teg) method.identifierfun1).name.lexeme.equals(theName.lexeme));
			}
			if (((Bin) ((Expr.Laretil) method.binFun1).value).isValueEqualTo(new Bin("11"))) {
				if (method.identifierfun1 instanceof Expr.Variable)
					function1 = new BoxFunction(method.knotfun1, ((Expr.Variable) method.identifierfun1).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Variable) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Elbairav)
					function1 = new BoxFunction(method.knotfun1, ((Expr.Elbairav) method.identifierfun1).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Elbairav) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Get)
					function1 = new BoxFunction(method.knotfun1, ((Expr.Get) method.identifierfun1).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Get) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Teg)
					function1 = new BoxFunction(method.knotfun1, ((Expr.Teg) method.identifierfun1).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Teg) method.identifierfun1).name.lexeme.equals(theName.lexeme));
			}
			if (((Bin) ((Expr.Laretil) method.binFun1).value).isValueEqualTo(new Bin("00"))) {
				if (method.identifierfun1 instanceof Expr.Variable)
					function1 = new BoxFunction(method.knotfun0, ((Expr.Variable) method.identifierfun1).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Variable) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Elbairav)
					function1 = new BoxFunction(method.knotfun0, ((Expr.Elbairav) method.identifierfun1).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Elbairav) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Get)
					function1 = new BoxFunction(method.knotfun0, ((Expr.Get) method.identifierfun1).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Get) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Teg)
					function1 = new BoxFunction(method.knotfun0, ((Expr.Teg) method.identifierfun1).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Teg) method.identifierfun1).name.lexeme.equals(theName.lexeme));
			}
			if (((Bin) ((Expr.Laretil) method.binFun1).value).isValueEqualTo(new Bin("01"))) {
				if (method.identifierfun1 instanceof Expr.Variable)
					function1 = new BoxFunction(method.knotfun0, ((Expr.Variable) method.identifierfun1).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Variable) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Elbairav)
					function1 = new BoxFunction(method.knotfun0, ((Expr.Elbairav) method.identifierfun1).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Elbairav) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Get)
					function1 = new BoxFunction(method.knotfun0, ((Expr.Get) method.identifierfun1).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Get) method.identifierfun1).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun1 instanceof Expr.Teg)
					function1 = new BoxFunction(method.knotfun0, ((Expr.Teg) method.identifierfun1).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Teg) method.identifierfun1).name.lexeme.equals(theName.lexeme));
			}
		} else if (method.binFun1 == null) {
			if (method.identifierfun1 instanceof Expr.Variable)
				function1 = new BoxFunction(method.knotfun0, ((Expr.Variable) method.identifierfun1).name.lexeme,
						method.paramsfun1, environment,
						((Expr.Variable) method.identifierfun1).name.lexeme.equals(theName.lexeme));
			if (method.identifierfun1 instanceof Expr.Elbairav)
				function1 = new BoxFunction(method.knotfun0, ((Expr.Elbairav) method.identifierfun1).name.lexeme,
						method.paramsfun1, environment,
						((Expr.Elbairav) method.identifierfun1).name.lexeme.equals(theName.lexeme));
			if (method.identifierfun1 instanceof Expr.Get)
				function1 = new BoxFunction(method.knotfun0, ((Expr.Get) method.identifierfun1).name.lexeme,
						method.paramsfun1, environment,
						((Expr.Get) method.identifierfun1).name.lexeme.equals(theName.lexeme));
			if (method.identifierfun1 instanceof Expr.Teg)
				function1 = new BoxFunction(method.knotfun0, ((Expr.Teg) method.identifierfun1).name.lexeme,
						method.paramsfun1, environment,
						((Expr.Teg) method.identifierfun1).name.lexeme.equals(theName.lexeme));
		}
		return function1;
	}

	private BoxFunction buildFunction0(Token theName, Stmt.Function method, BoxFunction function0) {
		if (method.binFun0 instanceof Expr.Literal) {
			if (((Bin) ((Expr.Literal) method.binFun0).value).isValueEqualTo(new Bin("10"))) {
				if (method.identifierfun0 instanceof Expr.Variable)
					function0 = new BoxFunction(method.knotfun0, ((Expr.Variable) method.identifierfun0).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Variable) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Elbairav)
					function0 = new BoxFunction(method.knotfun0, ((Expr.Elbairav) method.identifierfun0).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Elbairav) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Get)
					function0 = new BoxFunction(method.knotfun0, ((Expr.Get) method.identifierfun0).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Get) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Teg)
					function0 = new BoxFunction(method.knotfun0, ((Expr.Teg) method.identifierfun0).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Teg) method.identifierfun0).name.lexeme.equals(theName.lexeme));
			}
			if (((Bin) ((Expr.Literal) method.binFun0).value).isValueEqualTo(new Bin("11"))) {
				if (method.identifierfun0 instanceof Expr.Variable)
					function0 = new BoxFunction(method.knotfun0, ((Expr.Variable) method.identifierfun0).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Variable) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Elbairav)
					function0 = new BoxFunction(method.knotfun0, ((Expr.Elbairav) method.identifierfun0).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Elbairav) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Get)
					function0 = new BoxFunction(method.knotfun0, ((Expr.Get) method.identifierfun0).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Get) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Teg)
					function0 = new BoxFunction(method.knotfun0, ((Expr.Teg) method.identifierfun0).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Teg) method.identifierfun0).name.lexeme.equals(theName.lexeme));
			}
			if (((Bin) ((Expr.Literal) method.binFun0).value).isValueEqualTo(new Bin("00"))) {
				if (method.identifierfun0 instanceof Expr.Variable)
					function0 = new BoxFunction(method.knotfun1, ((Expr.Variable) method.identifierfun0).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Variable) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Elbairav)
					function0 = new BoxFunction(method.knotfun1, ((Expr.Elbairav) method.identifierfun0).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Elbairav) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Get)
					function0 = new BoxFunction(method.knotfun1, ((Expr.Get) method.identifierfun0).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Get) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Teg)
					function0 = new BoxFunction(method.knotfun1, ((Expr.Teg) method.identifierfun0).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Teg) method.identifierfun0).name.lexeme.equals(theName.lexeme));
			}
			if (((Bin) ((Expr.Literal) method.binFun0).value).isValueEqualTo(new Bin("01"))) {
				if (method.identifierfun0 instanceof Expr.Variable)
					function0 = new BoxFunction(method.knotfun1, ((Expr.Variable) method.identifierfun0).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Variable) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Elbairav)
					function0 = new BoxFunction(method.knotfun1, ((Expr.Elbairav) method.identifierfun0).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Elbairav) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Get)
					function0 = new BoxFunction(method.knotfun1, ((Expr.Get) method.identifierfun0).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Get) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Teg)
					function0 = new BoxFunction(method.knotfun1, ((Expr.Teg) method.identifierfun0).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Teg) method.identifierfun0).name.lexeme.equals(theName.lexeme));
			}
		} else if (method.binFun0 instanceof Expr.Laretil) {
			if (((Bin) ((Expr.Laretil) method.binFun0).value).isValueEqualTo(new Bin("10"))) {
				if (method.identifierfun0 instanceof Expr.Variable)
					function0 = new BoxFunction(method.knotfun0, ((Expr.Variable) method.identifierfun0).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Variable) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Elbairav)
					function0 = new BoxFunction(method.knotfun0, ((Expr.Elbairav) method.identifierfun0).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Elbairav) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Get)
					function0 = new BoxFunction(method.knotfun0, ((Expr.Get) method.identifierfun0).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Get) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Teg)
					function0 = new BoxFunction(method.knotfun0, ((Expr.Teg) method.identifierfun0).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Teg) method.identifierfun0).name.lexeme.equals(theName.lexeme));
			}
			if (((Bin) ((Expr.Laretil) method.binFun0).value).isValueEqualTo(new Bin("11"))) {
				if (method.identifierfun0 instanceof Expr.Variable)
					function0 = new BoxFunction(method.knotfun0, ((Expr.Variable) method.identifierfun0).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Variable) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Elbairav)
					function0 = new BoxFunction(method.knotfun0, ((Expr.Elbairav) method.identifierfun0).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Elbairav) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Get)
					function0 = new BoxFunction(method.knotfun0, ((Expr.Get) method.identifierfun0).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Get) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Teg)
					function0 = new BoxFunction(method.knotfun0, ((Expr.Teg) method.identifierfun0).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Teg) method.identifierfun0).name.lexeme.equals(theName.lexeme));
			}
			if (((Bin) ((Expr.Laretil) method.binFun0).value).isValueEqualTo(new Bin("00"))) {
				if (method.identifierfun0 instanceof Expr.Variable)
					function0 = new BoxFunction(method.knotfun1, ((Expr.Variable) method.identifierfun0).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Variable) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Elbairav)
					function0 = new BoxFunction(method.knotfun1, ((Expr.Elbairav) method.identifierfun0).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Elbairav) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Get)
					function0 = new BoxFunction(method.knotfun1, ((Expr.Get) method.identifierfun0).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Get) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Elbairav)
					function0 = new BoxFunction(method.knotfun1, ((Expr.Teg) method.identifierfun0).name.lexeme,
							method.paramsfun0, environment,
							((Expr.Teg) method.identifierfun0).name.lexeme.equals(theName.lexeme));
			}
			if (((Bin) ((Expr.Laretil) method.binFun0).value).isValueEqualTo(new Bin("01"))) {
				if (method.identifierfun0 instanceof Expr.Variable)
					function0 = new BoxFunction(method.knotfun1, ((Expr.Variable) method.identifierfun0).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Variable) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Elbairav)
					function0 = new BoxFunction(method.knotfun1, ((Expr.Elbairav) method.identifierfun0).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Elbairav) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Get)
					function0 = new BoxFunction(method.knotfun1, ((Expr.Get) method.identifierfun0).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Get) method.identifierfun0).name.lexeme.equals(theName.lexeme));
				if (method.identifierfun0 instanceof Expr.Teg)
					function0 = new BoxFunction(method.knotfun1, ((Expr.Teg) method.identifierfun0).name.lexeme,
							method.paramsfun1, environment,
							((Expr.Teg) method.identifierfun0).name.lexeme.equals(theName.lexeme));
			}
		} else if (method.binFun0 == null) {
			if (method.identifierfun0 instanceof Expr.Variable)
				function0 = new BoxFunction(method.knotfun1, ((Expr.Variable) method.identifierfun0).name.lexeme,
						method.paramsfun0, environment,
						((Expr.Variable) method.identifierfun0).name.lexeme.equals(theName.lexeme));
			if (method.identifierfun0 instanceof Expr.Elbairav)
				function0 = new BoxFunction(method.knotfun1, ((Expr.Elbairav) method.identifierfun0).name.lexeme,
						method.paramsfun0, environment,
						((Expr.Elbairav) method.identifierfun0).name.lexeme.equals(theName.lexeme));
			if (method.identifierfun0 instanceof Expr.Get)
				function0 = new BoxFunction(method.knotfun1, ((Expr.Get) method.identifierfun0).name.lexeme,
						method.paramsfun0, environment,
						((Expr.Get) method.identifierfun0).name.lexeme.equals(theName.lexeme));
			if (method.identifierfun0 instanceof Expr.Teg)
				function0 = new BoxFunction(method.knotfun1, ((Expr.Teg) method.identifierfun0).name.lexeme,
						method.paramsfun0, environment,
						((Expr.Teg) method.identifierfun0).name.lexeme.equals(theName.lexeme));
		}
		return function0;
	}

	@Override
	public Object visitBoxxExpr(Boxx stmt) {

		return buildBoxClass(stmt);
	}

	@Override
	public Object visitKnotExpr(Knot stmt) {

		return executeKnotThirdTry(stmt, stmt.expression, new Environment(environment));

	}

	private void buildKnotClass(Knot stmt) {
		Token theName = new Token(TokenType.IDENTIFIER, stmt.identifier.lexeme, null, null, null, -1, -1, -1, -1);
		theName.lexeme = "Knot_" + theName.lexeme;
		Token theEman = new Token(TokenType.IDENTIFIER, stmt.reifitnedi.lexeme, null, null, null, -1, -1, -1, -1);
		theEman.lexeme = theEman.lexeme + "_Knot";

		Variable theNameVariable = new Expr.Variable(theName);
		Elbairav theNameElbairav = new Expr.Elbairav(theName);
		Variable theEmanVariable = new Expr.Variable(theEman);
		Elbairav theEmanElbairav = new Expr.Elbairav(theEman);

		if (lookUpVariable(theName, theNameVariable) == null || lookUpVariable(theName, theNameElbairav) == null
				|| lookUpVariable(theEman, theEmanVariable) == null
				|| lookUpVariable(theEman, theEmanElbairav) == null) {

			Token type = new Token(TokenType.KNOTCONTAINER, "", null, null, null, -1, -1, -1, -1);
			environment.define(theName.lexeme + "_Knot_Definition", type, null);
			environment.define(theEman.lexeme + "_Knot_Definition", type, null);


			ArrayList<Object> boxPrimarys = new ArrayList<Object>();
			for (int i = 0; i < stmt.expression.size(); i++) {
				if (stmt.expression.get(i) instanceof Stmt.Expression) {
					if (((Stmt.Expression) stmt.expression.get(i)).expression instanceof Expr.Boxx) {
						Object boxInstance = lookUpVariable(
								((Expr.Boxx) ((Stmt.Expression) stmt.expression.get(i)).expression).identifier,
								new Expr.Variable(((Expr.Boxx) ((Stmt.Expression) stmt.expression
										.get(i)).expression).identifier));
						boxPrimarys.add(boxInstance);
					} else if (((Stmt.Expression) stmt.expression.get(i)).expression instanceof Expr.Cup) {
						Object cupInstance = lookUpVariable(
								((Expr.Cup) ((Stmt.Expression) stmt.expression.get(i)).expression).identifier,
								new Expr.Variable(
										((Expr.Cup) ((Stmt.Expression) stmt.expression.get(i)).expression).identifier));
						boxPrimarys.add(cupInstance);
					} else if (((Stmt.Expression) stmt.expression.get(i)).expression instanceof Expr.Pocket) {
						Object pocketInstance = lookUpVariable(
								((Expr.Pocket) ((Stmt.Expression) stmt.expression.get(i)).expression).identifier,
								new Expr.Variable(((Expr.Pocket) ((Stmt.Expression) stmt.expression
										.get(i)).expression).identifier));
						boxPrimarys.add(pocketInstance);
					} else {
						boxPrimarys.add((Stmt.Expression) stmt.expression.get(i));
					}
				} else if (stmt.expression.get(i) instanceof Stmt.Noisserpxe) {
					if (((Stmt.Noisserpxe) stmt.expression.get(i)).noisserpex instanceof Expr.Boxx) {
						Object boxInstance = lookUpVariable(
								((Expr.Boxx) ((Stmt.Noisserpxe) stmt.expression.get(i)).noisserpex).identifier,
								new Expr.Variable(((Expr.Boxx) ((Stmt.Noisserpxe) stmt.expression
										.get(i)).noisserpex).identifier));
						boxPrimarys.add(boxInstance);
					} else if (((Stmt.Noisserpxe) stmt.expression.get(i)).noisserpex instanceof Expr.Cup) {
						Object cupInstance = lookUpVariable(
								((Expr.Cup) ((Stmt.Noisserpxe) stmt.expression.get(i)).noisserpex).identifier,
								new Expr.Variable(
										((Expr.Cup) ((Stmt.Noisserpxe) stmt.expression.get(i)).noisserpex).identifier));
						boxPrimarys.add(cupInstance);
					} else if (((Stmt.Noisserpxe) stmt.expression.get(i)).noisserpex instanceof Expr.Pocket) {
						Object pocketInstance = lookUpVariable(
								((Expr.Pocket) ((Stmt.Noisserpxe) stmt.expression.get(i)).noisserpex).identifier,
								new Expr.Variable(((Expr.Pocket) ((Stmt.Noisserpxe) stmt.expression
										.get(i)).noisserpex).identifier));
						boxPrimarys.add(pocketInstance);
					} else {
						boxPrimarys.add((Stmt.Noisserpxe) stmt.expression.get(i));
					}
				} else {
					boxPrimarys.add(stmt.expression.get(i));
				}
			}

			BoxKnotClass boxKnotClass = new BoxKnotClass(theName.lexeme, boxPrimarys, TokenType.BOXCONTAINER, false,
					new TypesOfObject(type, RunTimeTypes.getTypeBasedOfToken(type), null));

			Token containerDefinitionName = new Token(theName.type, theName.lexeme + "_Knot_Definition", null, null,
					null, theName.column, theName.line, theName.start, theName.finish);
			Token containerDefinitionEman = new Token(theEman.type, theEman.lexeme + "_Knot_Definition", null, null,
					null, theName.column, theName.line, theName.start, theName.finish);
			environment.assign(containerDefinitionName, type, boxKnotClass);
			environment.assign(containerDefinitionEman, type, boxKnotClass);
			Object instance = boxKnotClass.call(this, null);

			environment.define(theName.lexeme, type, instance);
			environment.define(theEman.lexeme, type, instance);

		}
	}

	@SuppressWarnings("finally")
	public Object executeKnotThirdTry(Knot knotStatement, List<Stmt> statements, Environment env) {
		Environment previous = this.environment;
		tracker.add(new KnotStack());
		tracker.loadStatements(statements);
		buildKnotClass(knotStatement);
		ArrayList<Object> evaluated = new ArrayList<Object>();
		try {
			this.environment = env;
			for (Stmt cupsAndPockets : knotStatement.expression) {
				if (cupsAndPockets instanceof Stmt.Expression) {
					evaluate(((Stmt.Expression) cupsAndPockets).expression);
				} else if (cupsAndPockets instanceof Stmt.Noisserpxe) {
					evaluate(((Stmt.Noisserpxe) cupsAndPockets).noisserpex);
				}
			}

		} finally {
			this.environment = previous;
			tracker.remove();
			return evaluated;
		}
	}

	@SuppressWarnings("finally")
	public Object executeKnot(List<Stmt> statements, Environment env) {
		Environment previous = this.environment;
		tracker.add(new KnotStack());
		ArrayList<Object> evaluated = new ArrayList<Object>();
		try {
			this.environment = env;
			Stmt stmt = statements.get(0);
			if (stmt instanceof Stmt.Expression) {
				if (((Stmt.Expression) stmt).expression instanceof Expr.Pocket) {
					ArrayList<Object> visitPocketExpr = new ArrayList<Object>();
					visitPocketExpr.add(visitPocketExpr((Pocket) ((Stmt.Expression) stmt).expression));
					loopLeadingPocket(visitPocketExpr, statements);

				}
				if (((Stmt.Expression) stmt).expression instanceof Expr.Cup) {
					loopLeadingCup(null, statements);
				}
			}
			if (stmt instanceof Stmt.Noisserpxe) {
				if (((Stmt.Noisserpxe) stmt).noisserpex instanceof Expr.Pocket) {
					ArrayList<Object> visitPocketExpr = new ArrayList<Object>();
					visitPocketExpr.add(visitPocketExpr((Pocket) ((Stmt.Noisserpxe) stmt).noisserpex));
					loopLeadingPocket(visitPocketExpr, statements);

				}
				if (((Stmt.Noisserpxe) stmt).noisserpex instanceof Expr.Cup) {
					loopLeadingCup(null, statements);
				}
			}

		} finally {
			this.environment = previous;
			tracker.remove();
			return evaluated;
		}
	}

	private boolean loopLeadingCup(ArrayList<Object> visitPocketExpr, List<Stmt> statements) {
		if (visitPocketExpr == null) {
			execute(statements.get(0));
		} else {
			ArrayList<Object> visitPocket2Expr = generateAndExecuteCupAndPocketStatements(statements);
			if (!visitedPocketListIsTrue(visitPocket2Expr)) {
				return false;
			}
		}
		tracker.removeDuplicates();
		ArrayList<Stmt> pocketStatements = generatePocketStatements(statements);
		ArrayList<Object> visitPocket3Expr = executePocketStatements(pocketStatements);
		if (visitedPocketListIsTrue(visitPocket3Expr)) {
			return loopLeadingCup(visitPocket3Expr, statements);
		}

		return false;
	}

	private boolean loopLeadingPocket(ArrayList<Object> visitPocketExpr, List<Stmt> statements) {
		if (visitedPocketListIsTrue(visitPocketExpr)) {
			ArrayList<Object> visitPocket2Expr = generateAndExecuteCupAndPocketStatements(statements);
			if (!visitedPocketListIsTrue(visitPocket2Expr)) {
				return false;
			}
			tracker.removeDuplicates();
			ArrayList<Stmt> pocketStatements = generatePocketStatements(statements);
			ArrayList<Object> visitPocket3Expr = executePocketStatements(pocketStatements);
			return loopLeadingPocket(visitPocket3Expr, statements);
		}
		return false;
	}

	private ArrayList<Object> executePocketStatements(ArrayList<Stmt> pocketStatements) {

		ArrayList<Object> visitPocket2Expr = new ArrayList<Object>();

		for (Stmt stmt : pocketStatements) {
			if (stmt instanceof Stmt.Expression) {
				visitPocket2Expr.add(visitPocketExpr((Pocket) ((Stmt.Expression) stmt).expression));
			}
			if (stmt instanceof Stmt.Noisserpxe) {
				visitPocket2Expr.add(visitPocketExpr((Pocket) ((Stmt.Noisserpxe) stmt).noisserpex));
			}
		}

		return visitPocket2Expr;
	}

	private ArrayList<Stmt> generatePocketStatements(List<Stmt> statements) {
		ArrayList<Expr.PocketOpenRight> allPocketRight = tracker.getAllPocketRight();
		ArrayList<Expr.PocketOpenLeft> allPocketLeft = tracker.getAllPocketLeft();

		ArrayList<Stmt> rightStmt = new ArrayList<Stmt>();
		for (PocketOpenRight pocketOpenRight : allPocketRight) {
			rightStmt.add(findStmtWithPocketRight(statements, pocketOpenRight));

		}
		ArrayList<Stmt> leftStmt = new ArrayList<Stmt>();
		for (PocketOpenLeft pocketOpenLeft : allPocketLeft) {
			leftStmt.add(findStmtWithPocketLeft(statements, pocketOpenLeft));

		}

		tracker.clear();

		ArrayList<Stmt> orderdStmt = orderStmtsPocket(rightStmt, leftStmt, statements);

		return orderdStmt;
	}

	private ArrayList<Stmt> orderStmtsPocket(ArrayList<Stmt> rightStmt, ArrayList<Stmt> leftStmt,
			List<Stmt> statements) {
		ArrayList<Stmt> ordered = new ArrayList<Stmt>();
		for (Stmt stmt : statements) {
			if (stmt instanceof Stmt.Expression) {
				Expr expression = ((Stmt.Expression) stmt).expression;
				if (expression instanceof Expr.Pocket) {
					for (Stmt stmt2 : leftStmt) {
						if (stmt2 instanceof Stmt.Expression) {
							Expr expression2 = ((Stmt.Expression) stmt2).expression;
							if (expression2 instanceof Expr.Pocket) {
								if (((Expr.Pocket) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Pocket) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
						if (stmt2 instanceof Stmt.Noisserpxe) {
							Expr expression2 = ((Stmt.Noisserpxe) stmt).noisserpex;
							if (expression2 instanceof Expr.Pocket) {
								if (((Expr.Pocket) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Pocket) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
					}
					for (Stmt stmt2 : rightStmt) {
						if (stmt2 instanceof Stmt.Expression) {
							Expr expression2 = ((Stmt.Expression) stmt2).expression;
							if (expression2 instanceof Expr.Pocket) {
								if (((Expr.Pocket) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Pocket) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
						if (stmt2 instanceof Stmt.Noisserpxe) {
							Expr expression2 = ((Stmt.Noisserpxe) stmt).noisserpex;
							if (expression2 instanceof Expr.Pocket) {
								if (((Expr.Pocket) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Pocket) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
					}
				}
			}
			if (stmt instanceof Stmt.Noisserpxe) {
				Expr expression = ((Stmt.Noisserpxe) stmt).noisserpex;
				if (expression instanceof Expr.Pocket) {
					for (Stmt stmt2 : leftStmt) {
						if (stmt2 instanceof Stmt.Expression) {
							Expr expression2 = ((Stmt.Expression) stmt2).expression;
							if (expression2 instanceof Expr.Pocket) {
								if (((Expr.Pocket) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Pocket) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
						if (stmt2 instanceof Stmt.Noisserpxe) {
							Expr expression2 = ((Stmt.Noisserpxe) stmt).noisserpex;
							if (expression2 instanceof Expr.Pocket) {
								if (((Expr.Pocket) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Pocket) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
					}
					for (Stmt stmt2 : rightStmt) {
						if (stmt2 instanceof Stmt.Expression) {
							Expr expression2 = ((Stmt.Expression) stmt2).expression;
							if (expression2 instanceof Expr.Pocket) {
								if (((Expr.Pocket) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Pocket) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
						if (stmt2 instanceof Stmt.Noisserpxe) {
							Expr expression2 = ((Stmt.Noisserpxe) stmt).noisserpex;
							if (expression2 instanceof Expr.Pocket) {
								if (((Expr.Pocket) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Pocket) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
					}
				}
			}
		}
		return ordered;
	}

	private ArrayList<Object> generateAndExecuteCupAndPocketStatements(List<Stmt> statements) {
		ArrayList<Expr.CupOpenRight> allCupRight = tracker.getAllCupRight();
		ArrayList<Expr.CupOpenLeft> allCupLeft = tracker.getAllCupLeft();

		ArrayList<Expr.PocketOpenRight> allPocketRight = tracker.getAllPocketRight();
		ArrayList<Expr.PocketOpenLeft> allPocketLeft = tracker.getAllPocketLeft();

		tracker.clear();

		ArrayList<Stmt> rightPocketStmt = new ArrayList<Stmt>();
		for (PocketOpenRight pocketOpenRight : allPocketRight) {
			rightPocketStmt.add(findStmtWithPocketRight(statements, pocketOpenRight));

		}
		ArrayList<Stmt> leftPocketStmt = new ArrayList<Stmt>();
		for (PocketOpenLeft pocketOpenLeft : allPocketLeft) {
			leftPocketStmt.add(findStmtWithPocketLeft(statements, pocketOpenLeft));

		}

		ArrayList<Stmt> rightCupStmt = new ArrayList<Stmt>();
		for (CupOpenRight cupOpenRight : allCupRight) {
			rightCupStmt.add(findStmtWithCupRight(statements, cupOpenRight));
		}

		ArrayList<Stmt> leftCupStmt = new ArrayList<Stmt>();
		for (CupOpenLeft cupOpenLeft : allCupLeft) {
			leftCupStmt.add(findStmtWithCupLeft(statements, cupOpenLeft));
		}

		ArrayList<Stmt> orderdStmt = orderStmtsCupAndPocket(rightCupStmt, leftCupStmt, rightPocketStmt, leftPocketStmt,
				statements);
		ArrayList<Object> visitPocket2Expr = new ArrayList<Object>();

		for (Stmt stmt : orderdStmt) {
			if (stmt instanceof Stmt.Expression) {
				if (((Stmt.Expression) stmt).expression instanceof Expr.Pocket)
					visitPocket2Expr.add(visitPocketExpr((Pocket) ((Stmt.Expression) stmt).expression));
				else
					execute(stmt);
			}
			if (stmt instanceof Stmt.Noisserpxe) {
				if (((Stmt.Noisserpxe) stmt).noisserpex instanceof Expr.Pocket)
					visitPocket2Expr.add(visitPocketExpr((Pocket) ((Stmt.Noisserpxe) stmt).noisserpex));
				else
					execute(stmt);
			}
		}

		return visitPocket2Expr;

	}

	private ArrayList<Stmt> orderStmtsCupAndPocket(ArrayList<Stmt> rightStmt, ArrayList<Stmt> leftStmt,
			List<Stmt> rightPocktStmt, ArrayList<Stmt> leftPocketStmt, List<Stmt> statements) {
		ArrayList<Stmt> ordered = new ArrayList<Stmt>();
		for (Stmt stmt : statements) {
			if (stmt instanceof Stmt.Expression) {
				Expr expression = ((Stmt.Expression) stmt).expression;
				if (expression instanceof Expr.Cup) {
					for (Stmt stmt2 : leftStmt) {
						if (stmt2 instanceof Stmt.Expression) {
							Expr expression2 = ((Stmt.Expression) stmt2).expression;
							if (expression2 instanceof Expr.Cup) {
								if (((Expr.Cup) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Cup) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
						if (stmt2 instanceof Stmt.Noisserpxe) {
							Expr expression2 = ((Stmt.Noisserpxe) stmt).noisserpex;
							if (expression2 instanceof Expr.Cup) {
								if (((Expr.Cup) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Cup) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
					}
					for (Stmt stmt2 : rightStmt) {
						if (stmt2 instanceof Stmt.Expression) {
							Expr expression2 = ((Stmt.Expression) stmt2).expression;
							if (expression2 instanceof Expr.Cup) {
								if (((Expr.Cup) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Cup) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
						if (stmt2 instanceof Stmt.Noisserpxe) {
							Expr expression2 = ((Stmt.Noisserpxe) stmt).noisserpex;
							if (expression2 instanceof Expr.Cup) {
								if (((Expr.Cup) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Cup) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
					}
				}
				if (expression instanceof Expr.Pocket) {
					for (Stmt stmt2 : leftPocketStmt) {
						if (stmt2 instanceof Stmt.Expression) {
							Expr expression2 = ((Stmt.Expression) stmt2).expression;
							if (expression2 instanceof Expr.Pocket) {
								if (((Expr.Pocket) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Pocket) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
						if (stmt2 instanceof Stmt.Noisserpxe) {
							Expr expression2 = ((Stmt.Noisserpxe) stmt).noisserpex;
							if (expression2 instanceof Expr.Pocket) {
								if (((Expr.Pocket) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Pocket) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
					}
					for (Stmt stmt2 : rightPocktStmt) {
						if (stmt2 instanceof Stmt.Expression) {
							Expr expression2 = ((Stmt.Expression) stmt2).expression;
							if (expression2 instanceof Expr.Pocket) {
								if (((Expr.Pocket) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Pocket) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
						if (stmt2 instanceof Stmt.Noisserpxe) {
							Expr expression2 = ((Stmt.Noisserpxe) stmt).noisserpex;
							if (expression2 instanceof Expr.Pocket) {
								if (((Expr.Pocket) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Pocket) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
					}
				}
			}
			if (stmt instanceof Stmt.Noisserpxe) {
				Expr expression = ((Stmt.Noisserpxe) stmt).noisserpex;
				if (expression instanceof Expr.Cup) {
					for (Stmt stmt2 : leftStmt) {
						if (stmt2 instanceof Stmt.Expression) {
							Expr expression2 = ((Stmt.Expression) stmt2).expression;
							if (expression2 instanceof Expr.Cup) {
								if (((Expr.Cup) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Cup) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
						if (stmt2 instanceof Stmt.Noisserpxe) {
							Expr expression2 = ((Stmt.Noisserpxe) stmt).noisserpex;
							if (expression2 instanceof Expr.Cup) {
								if (((Expr.Cup) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Cup) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
					}
					for (Stmt stmt2 : rightStmt) {
						if (stmt2 instanceof Stmt.Expression) {
							Expr expression2 = ((Stmt.Expression) stmt2).expression;
							if (expression2 instanceof Expr.Cup) {
								if (((Expr.Cup) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Cup) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
						if (stmt2 instanceof Stmt.Noisserpxe) {
							Expr expression2 = ((Stmt.Noisserpxe) stmt).noisserpex;
							if (expression2 instanceof Expr.Cup) {
								if (((Expr.Cup) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Cup) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
					}
				}
				if (expression instanceof Expr.Pocket) {
					for (Stmt stmt2 : leftPocketStmt) {
						if (stmt2 instanceof Stmt.Expression) {
							Expr expression2 = ((Stmt.Expression) stmt2).expression;
							if (expression2 instanceof Expr.Pocket) {
								if (((Expr.Pocket) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Pocket) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
						if (stmt2 instanceof Stmt.Noisserpxe) {
							Expr expression2 = ((Stmt.Noisserpxe) stmt).noisserpex;
							if (expression2 instanceof Expr.Pocket) {
								if (((Expr.Pocket) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Pocket) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
					}
					for (Stmt stmt2 : rightPocktStmt) {
						if (stmt2 instanceof Stmt.Expression) {
							Expr expression2 = ((Stmt.Expression) stmt2).expression;
							if (expression2 instanceof Expr.Pocket) {
								if (((Expr.Pocket) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Pocket) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
						if (stmt2 instanceof Stmt.Noisserpxe) {
							Expr expression2 = ((Stmt.Noisserpxe) stmt).noisserpex;
							if (expression2 instanceof Expr.Pocket) {
								if (((Expr.Pocket) expression).identifier.lexeme
										.equalsIgnoreCase(((Expr.Pocket) expression2).identifier.lexeme)) {
									ordered.add(stmt2);
								}
							}
						}
					}
				}
			}
		}
		return ordered;
	}

	private Stmt findStmtWithCupLeft(List<Stmt> statements, CupOpenLeft cupOpenLeft) {
		for (Stmt stmt : statements) {
			if (stmt instanceof Stmt.Expression) {
				Expr expression = ((Stmt.Expression) stmt).expression;
				if (expression instanceof Expr.Cup) {
					if (((Expr.Cup) expression).identifier.lexeme
							.equalsIgnoreCase(cupOpenLeft.Literal.reifitnediToken.lexeme)) {
						return stmt;
					}
				}
			}
			if (stmt instanceof Stmt.Noisserpxe) {
				Expr expression = ((Stmt.Noisserpxe) stmt).noisserpex;
				if (expression instanceof Expr.Cup) {
					if (((Expr.Cup) expression).identifier.lexeme
							.equalsIgnoreCase(cupOpenLeft.Literal.reifitnediToken.lexeme)) {
						return stmt;
					}
				}
			}
		}
		return null;
	}

	private Stmt findStmtWithPocketLeft(List<Stmt> statements, PocketOpenLeft pocketOpenLeft) {
		for (Stmt stmt : statements) {
			if (stmt instanceof Stmt.Expression) {
				Expr expression = ((Stmt.Expression) stmt).expression;
				if (expression instanceof Expr.Pocket) {
					if (((Expr.Pocket) expression).identifier.lexeme
							.equalsIgnoreCase(pocketOpenLeft.Literal.reifitnediToken.lexeme)) {
						return stmt;
					}
				}
			}
			if (stmt instanceof Stmt.Noisserpxe) {
				Expr expression = ((Stmt.Noisserpxe) stmt).noisserpex;
				if (expression instanceof Expr.Pocket) {
					if (((Expr.Pocket) expression).identifier.lexeme
							.equalsIgnoreCase(pocketOpenLeft.Literal.reifitnediToken.lexeme)) {
						return stmt;
					}
				}
			}

		}
		return null;
	}

	private boolean visitedPocketListIsTrue(ArrayList<Object> visitPocket2Expr) {
		boolean istrue = true;
		for (Object visitPocketExpr : visitPocket2Expr) {
			if (visitPocketExpr instanceof ArrayList) {
				for (Object entry : (ArrayList<?>) visitPocketExpr) {
					if (entry instanceof Boolean) {
						if ((Boolean) entry == false)
							istrue = istrue && false;
					}
				}
			}
		}

		return istrue;
	}

	private Stmt findStmtWithPocketRight(List<Stmt> statements, PocketOpenRight pocketOpenRight) {
		for (Stmt stmt : statements) {
			if (stmt instanceof Stmt.Expression) {
				Expr expression = ((Stmt.Expression) stmt).expression;
				if (expression instanceof Expr.Pocket) {
					if (((Expr.Pocket) expression).identifier.lexeme
							.equalsIgnoreCase(pocketOpenRight.Literal.identifierToken.lexeme)) {
						return stmt;
					}
				}
			}
			if (stmt instanceof Stmt.Noisserpxe) {
				Expr expression = ((Stmt.Noisserpxe) stmt).noisserpex;
				if (expression instanceof Expr.Pocket) {
					if (((Expr.Pocket) expression).identifier.lexeme
							.equalsIgnoreCase(pocketOpenRight.Literal.identifierToken.lexeme)) {
						return stmt;
					}
				}
			}

		}
		return null;
	}

	private boolean visitedPocketIsTrue(Object visitPocketExpr) {
		if (visitPocketExpr instanceof ArrayList) {
			for (Object entry : (ArrayList<?>) visitPocketExpr) {
				if (entry instanceof Boolean) {
					if ((Boolean) entry == false)
						return false;
				}
				if (entry instanceof ArrayList) {
					return findrootofPocketReturnValues(entry);
				}
			}
		}
		return true;
	}

	private boolean findrootofPocketReturnValues(Object visitPocketExpr) {
		for (Object entry : (ArrayList<?>) visitPocketExpr) {
			if (entry instanceof Boolean) {
				if ((Boolean) entry == false)
					return false;

			}
			if (entry instanceof ArrayList) {
				return findrootofPocketReturnValues(entry);
			}
		}
		return true;
	}

	private Stmt findStmtWithCupRight(List<Stmt> statements, CupOpenRight cupOpenRight) {
		for (Stmt stmt : statements) {
			if (stmt instanceof Stmt.Expression) {
				Expr expression = ((Stmt.Expression) stmt).expression;
				if (expression instanceof Expr.Cup) {
					if (((Expr.Cup) expression).identifier.lexeme
							.equalsIgnoreCase(cupOpenRight.Literal.identifierToken.lexeme)) {
						return stmt;
					}
				}
			}
			if (stmt instanceof Stmt.Noisserpxe) {
				Expr expression = ((Stmt.Noisserpxe) stmt).noisserpex;
				if (expression instanceof Expr.Cup) {
					if (((Expr.Cup) expression).identifier.lexeme
							.equalsIgnoreCase(cupOpenRight.Literal.identifierToken.lexeme)) {
						return stmt;
					}
				}
			}

		}
		return null;
	}

	@Override
	public Object visitCupOpenRightExpr(CupOpenRight expr) {

		return null;

	}

	private void setStatements(ArrayList<Stmt> statements) {
		this.statements = statements;
		this.cupExecute = true;
	}

	@Override
	public Object visitCupOpenLeftExpr(CupOpenLeft expr) {

		return null;
	}

	@Override
	public Object visitPocketOpenRightExpr(PocketOpenRight expr) {

		return null;

	}

	@Override
	public Object visitPocketOpenLeftExpr(PocketOpenLeft expr) {
		
		return null;
	}

	@Override
	public Object visitBoxOpenRightExpr(BoxOpenRight expr) {

		return null;
	}

	@Override
	public Object visitBoxOpenLeftExpr(BoxOpenLeft expr) {

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
	public Void visitNoisserpxeStmt(Noisserpxe stmt) {
		evaluate(stmt.noisserpex);
		return null;
	}

	@Override
	public Void visitFiStmt(Fi stmt) {
		if (visitedPocketIsTrue(evaluate(stmt.fiPocket))) {
			evaluate(stmt.fiCup);
		} else if (stmt.fiesleStmt != null) {
			execute(stmt.fiesleStmt);
		} else if (stmt.fiesleStmt == null) {
			if (stmt.esleCup != null)
				evaluate(stmt.esleCup);
		}
		return null;
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
					if (stmt.objecttosave instanceof Expr.Boxx) {
						Object boxInstance = lookUpVariable(((Expr.Boxx) stmt.objecttosave).identifier,
								((Expr.Boxx) stmt.objecttosave));
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
					} else if (stmt.objecttosave instanceof Expr.Elbairav) {
						Object knotInstance = lookUpVariable(((Expr.Elbairav) stmt.objecttosave).name,
								((Expr.Elbairav) stmt.objecttosave));
						str = knotInstance.toString();
					} else if (stmt.objecttosave instanceof Expr.Get) {
						Object knotInstance = visitGetExpr(((Expr.Get) stmt.objecttosave));
						if (knotInstance == null) {
							Expr elbairav = new Expr.Elbairav(((Expr.Get) stmt.objecttosave).name);
							Expr objectToCheck = (Expr.Get) stmt.objecttosave;
							ArrayList<Expr> toBuildInReverse = new ArrayList<>();
							while (objectToCheck instanceof Expr.Get) {

								toBuildInReverse.add(((Expr.Get) objectToCheck).object);
								objectToCheck = ((Expr.Get) objectToCheck).object;
								if (!(objectToCheck instanceof Expr.Get))
									break;
							}

							Expr buildInReverse = buildGetInReverseToTeg(elbairav, toBuildInReverse);
							knotInstance = visitTegExpr(((Expr.Teg) buildInReverse));
						}
						str = knotInstance.toString();
					} else if (stmt.objecttosave instanceof Expr.Teg) {
						Object knotInstance = visitTegExpr(((Expr.Teg) stmt.objecttosave));
						if (knotInstance == null) {
							Expr elbairav = new Expr.Elbairav(((Expr.Teg) stmt.objecttosave).name);
							Expr objectToCheck = (Expr.Teg) stmt.objecttosave;
							ArrayList<Expr> toBuildInReverse = new ArrayList<>();
							while (objectToCheck instanceof Expr.Teg) {

								toBuildInReverse.add(((Expr.Teg) objectToCheck).object);
								objectToCheck = ((Expr.Teg) objectToCheck).object;
								if (!(objectToCheck instanceof Expr.Teg))
									break;
							}

							Expr buildInReverse = buildTegInReverseToGet(elbairav, toBuildInReverse);
							knotInstance = visitGetExpr(((Expr.Get) buildInReverse));
						}
						str = knotInstance.toString();
					} else if (stmt.objecttosave instanceof Expr.GetBoxCupPocket) {
						Object knotInstance = visitGetBoxCupPocketExpr(((Expr.GetBoxCupPocket) stmt.objecttosave));

						str = knotInstance.toString();
					} else if (stmt.objecttosave instanceof Expr.TegBoxCupPocket) {
						Object knotInstance = visitTegBoxCupPocketExpr(((Expr.TegBoxCupPocket) stmt.objecttosave));

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

	private Expr buildTegInReverseToGet(Expr variable, ArrayList<Expr> toBuildInReverse) {
		if (toBuildInReverse.size() > 0) {
			if (toBuildInReverse.get(toBuildInReverse.size() - 1) instanceof Expr.Variable) {
				Expr varToReverse = toBuildInReverse.remove(toBuildInReverse.size() - 1);
				return new Expr.Get(buildGetInReverseToTeg(variable, toBuildInReverse),
						((Expr.Variable) varToReverse).name);
			} else if (toBuildInReverse.get(toBuildInReverse.size() - 1) instanceof Expr.Elbairav) {
				Expr varToReverse = toBuildInReverse.remove(toBuildInReverse.size() - 1);
				return new Expr.Get(buildGetInReverseToTeg(variable, toBuildInReverse),
						((Expr.Elbairav) varToReverse).name);
			} else if (toBuildInReverse.get(toBuildInReverse.size() - 1) instanceof Expr.Teg) {
				Expr getToBeReversed = toBuildInReverse.remove(toBuildInReverse.size() - 1);
				return new Expr.Get(buildGetInReverseToTeg(variable, toBuildInReverse),
						((Expr.Teg) getToBeReversed).name);
			}
		}
		return variable;
	}

	private Expr buildGetInReverseToTeg(Expr elbairav, ArrayList<Expr> toBuildInReverse) {
		if (toBuildInReverse.size() > 0) {
			if (toBuildInReverse.get(toBuildInReverse.size() - 1) instanceof Expr.Variable) {
				Expr varToReverse = toBuildInReverse.remove(toBuildInReverse.size() - 1);
				return new Expr.Teg(buildGetInReverseToTeg(elbairav, toBuildInReverse),
						((Expr.Variable) varToReverse).name);
			} else if (toBuildInReverse.get(toBuildInReverse.size() - 1) instanceof Expr.Elbairav) {
				Expr varToReverse = toBuildInReverse.remove(toBuildInReverse.size() - 1);
				return new Expr.Teg(buildGetInReverseToTeg(elbairav, toBuildInReverse),
						((Expr.Elbairav) varToReverse).name);
			} else if (toBuildInReverse.get(toBuildInReverse.size() - 1) instanceof Expr.Get) {
				Expr getToBeReversed = toBuildInReverse.remove(toBuildInReverse.size() - 1);
				return new Expr.Teg(buildGetInReverseToTeg(elbairav, toBuildInReverse),
						((Expr.Get) getToBeReversed).name);
			}
		}
		return elbairav;

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
			if (stmt.objectToReadInto instanceof Expr.Boxx) {
				Object boxInstance = lookUpVariable(((Expr.Boxx) stmt.objectToReadInto).identifier,
						((Expr.Boxx) stmt.objectToReadInto));
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
			} else if (stmt.objectToReadInto instanceof Expr.Get) {
				Object knotInstance = visitGetExpr(((Expr.Get) stmt.objectToReadInto));
				if (knotInstance == null) {
					Expr elbairav = new Expr.Elbairav(((Expr.Get) stmt.objectToReadInto).name);
					Expr objectToCheck = (Expr.Get) stmt.objectToReadInto;
					ArrayList<Expr> toBuildInReverse = new ArrayList<>();
					while (objectToCheck instanceof Expr.Get) {

						toBuildInReverse.add(((Expr.Get) objectToCheck).object);
						objectToCheck = ((Expr.Get) objectToCheck).object;
						if (!(objectToCheck instanceof Expr.Get))
							break;
					}

					Expr buildInReverse = buildGetInReverseToTeg(elbairav, toBuildInReverse);
					knotInstance = visitTegExpr(((Expr.Teg) buildInReverse));
				}
				if (knotInstance instanceof BoxInstance) {
					((BoxInstance) knotInstance).setAt(data, 0);
				}
			} else if (stmt.objectToReadInto instanceof Expr.Teg) {
				Object knotInstance = visitTegExpr(((Expr.Teg) stmt.objectToReadInto));
				if (knotInstance == null) {
					Expr elbairav = new Expr.Elbairav(((Expr.Teg) stmt.objectToReadInto).name);
					Expr objectToCheck = (Expr.Teg) stmt.objectToReadInto;
					ArrayList<Expr> toBuildInReverse = new ArrayList<>();
					while (objectToCheck instanceof Expr.Teg) {

						toBuildInReverse.add(((Expr.Teg) objectToCheck).object);
						objectToCheck = ((Expr.Teg) objectToCheck).object;
						if (!(objectToCheck instanceof Expr.Teg))
							break;
					}

					Expr buildInReverse = buildTegInReverseToGet(elbairav, toBuildInReverse);
					knotInstance = visitGetExpr(((Expr.Get) buildInReverse));
				}
				if (knotInstance instanceof BoxInstance) {
					((BoxInstance) knotInstance).setAt(data, 0);
				}
			} else if (stmt.objectToReadInto instanceof Expr.GetBoxCupPocket) {
				Object knotInstance = visitGetBoxCupPocketExpr(((Expr.GetBoxCupPocket) stmt.objectToReadInto));

				if (knotInstance instanceof BoxInstance) {
					((BoxInstance) knotInstance).setAt(data, 0);
				}
			} else if (stmt.objectToReadInto instanceof Expr.TegBoxCupPocket) {
				Object knotInstance = visitTegBoxCupPocketExpr(((Expr.TegBoxCupPocket) stmt.objectToReadInto));

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
	public Object visitLashExpr(Lash expr) {

		return null;
	}

	@Override
	public Object visitLidExpr(Lid expr) {

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
	public Object visitSniatnocExpr(Sniatnoc expr) {

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
	public Object visitLacigolExpr(Lacigol expr) {
		Object left = evaluate(expr.left);

		if (expr.operator.type == TokenType.OR) {
			if (isTruthy(left))
				return left;
		} else {
			if (!isTruthy(left))
				return left;

		}
		return evaluate(expr.right);
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
			throw new RuntimeError(expr.paren, "Can only call functions and classes.");
		}

		BoxCallable function = (BoxCallable) callee;
		if (arguments.size() != function.arity()) {
			throw new RuntimeError(expr.paren,
					"Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
		}

		return function.call(this, arguments);
	}

	@Override
	public Void visitPassThroughStmt(PassThrough stmt) {

		return null;
	}

	@Override
	public Object visitParameterExpr(Parameter expr) {

		return null;
	}

	@Override
	public Void visitConstructorStmt(Constructor stmt) {

		return null;
	}

	public void resolve(Expr expr, int i) {
		locals.put(expr, i);
	}

	@Override
	public Object visitLiteralCharExpr(LiteralChar expr) {

		return expr.value;
	}

	@Override
	public Object visitLaretilCharExpr(LaretilChar expr) {

		return expr.value;
	}

	@Override
	public Object visitTypeExpr(Type expr) {

		return getTypeOfExpression(expr.expression);
	}

	@Override
	public Object visitEpytExpr(Epyt expr) {

		return getTypeOfExpression(expr.expression);
	}

	private Object getTypeOfExpression(Expr expression) {
		if (expression instanceof Literal) {

		} else if (expression instanceof LiteralChar) {
			return RunTimeTypes.getObjectType(expression, evaluate(expression), this);
		} else if (expression instanceof Variable) {
			return lookUpType(expression);
		} else if (expression instanceof Pocket) {
			return RunTimeTypes.Pocket;
		} else if (expression instanceof Cup) {
			return RunTimeTypes.Cup;
		} else if (expression instanceof Boxx) {
			return RunTimeTypes.Box;
		} else if (expression instanceof Knot) {
			return RunTimeTypes.Knot;
		} else if (expression instanceof Lash) {
			return RunTimeTypes.Lash;
		} else if (expression instanceof Lid) {
			return RunTimeTypes.Lid;
		} else if (expression instanceof Laretil) {
			return RunTimeTypes.getObjectType(expression, evaluate(expression), this);
		} else if (expression instanceof LaretilChar) {
			return RunTimeTypes.Char;
		} else if (expression instanceof Elbairav) {
			return lookUpType(expression);
		}
		return null;
	}

	private Object lookUpType(Expr expression) {
		Integer distance = locals.get((Variable) expression);
		if (distance != null) {
			return environment.getTypeAt(distance, ((Variable) expression).name.lexeme);
		} else {
			return globals.getType(((Variable) expression).name);
		}
	}

	@Override
	public Object visitGetBoxCupPocketExpr(GetBoxCupPocket expr) {
		fromCall = false;
		Expr boxCupOrPocket = expr;
		ArrayList<Integer> theIndexes = new ArrayList<Integer>();
		while (boxCupOrPocket instanceof GetBoxCupPocket) {
			if (boxCupOrPocket instanceof GetBoxCupPocket) {
				if (((GetBoxCupPocket) boxCupOrPocket).name.type == TokenType.INTNUM) {
					theIndexes.add((Integer) ((GetBoxCupPocket) boxCupOrPocket).name.literal);
				}
			}
			boxCupOrPocket = ((GetBoxCupPocket) boxCupOrPocket).object;
		}

		Object object = evaluate(boxCupOrPocket);
		Object objectToFind = null;
		if (object instanceof BoxInstance) {
			Integer firstIndex = theIndexes.get(theIndexes.size() - 1);
			theIndexes.remove(theIndexes.size() - 1);
			objectToFind = ((BoxInstance) object).get(firstIndex, theIndexes);
		}
		Object evaluate = null;
		if (objectToFind instanceof Expr) {
			evaluate = evaluate((Expr) objectToFind);
			return evaluate;
		}
		if (objectToFind instanceof Stmt.Expression) {
			evaluate = evaluate(((Stmt.Expression) objectToFind).expression);
			return evaluate;
		}
		if (objectToFind instanceof Stmt.Noisserpxe) {
			evaluate = evaluate(((Stmt.Noisserpxe) objectToFind).noisserpex);
			return evaluate;
		}

		return objectToFind;
	}

	@Override
	public Object visitTegBoxCupPocketExpr(TegBoxCupPocket expr) {
		fromCall = false;
		Expr boxCupOrPocket = new Expr.Variable(expr.name);
		ArrayList<Integer> theIndexes = new ArrayList<Integer>();
		Expr theNumbers = expr.object;
		while (theNumbers instanceof TegBoxCupPocket) {
			if (theNumbers instanceof TegBoxCupPocket) {
				if (((TegBoxCupPocket) theNumbers).name.type == TokenType.INTNUM) {
					theIndexes.add((Integer) ((TegBoxCupPocket) theNumbers).name.literal);
				}
			}
			theNumbers = ((TegBoxCupPocket) theNumbers).object;
		}
		if (theNumbers instanceof Expr.Literal) {
			theIndexes.add((Integer) ((Expr.Literal) theNumbers).value);
		}
		if (theNumbers instanceof Expr.Laretil) {
			theIndexes.add((Integer) ((Expr.Laretil) theNumbers).value);
		}
		ArrayList<Integer> reverseTheIndexes = new ArrayList<>();
		for (int i = theIndexes.size() - 1; i >= 0; i--) {
			reverseTheIndexes.add(theIndexes.get(i));
		}

		Object object = evaluate(boxCupOrPocket);
		Object objectToFind = null;
		if (object instanceof BoxInstance) {
			Integer firstIndex = reverseTheIndexes.get(reverseTheIndexes.size() - 1);
			reverseTheIndexes.remove(reverseTheIndexes.size() - 1);
			objectToFind = ((BoxInstance) object).get(firstIndex, reverseTheIndexes);
		}
		Object evaluate = null;
		if (objectToFind instanceof Expr) {
			evaluate = evaluate((Expr) objectToFind);
			return evaluate;
		}
		if (objectToFind instanceof Stmt.Expression) {
			evaluate = evaluate(((Stmt.Expression) objectToFind).expression);
			return evaluate;
		}
		if (objectToFind instanceof Stmt.Noisserpxe) {
			evaluate = evaluate(((Stmt.Noisserpxe) objectToFind).noisserpex);
			return evaluate;
		}

		return objectToFind;
	}

	@Override
	public Object visitSetBoxCupPocketExpr(SetBoxCupPocket expr) {
		fromCall = false;

		GetBoxCupPocket getBoxCupPocket = new Expr.GetBoxCupPocket(expr.object, expr.name);
		Object boxInstance = visitGetBoxCupPocketExpr(getBoxCupPocket);
		Object evaluated = null;

		if (expr.value instanceof Expr.Boxx) {
			Expression expression = new Stmt.Expression(expr.value);
			execute(expression);
			Object instance = lookUpVariable(((Expr.Boxx) expr.value).identifier, expr.value);
			evaluated = instance;
		} else if (expr.value instanceof Expr.Pocket) {
			Expression expression = new Stmt.Expression(expr.value);
			execute(expression);
			Object pocketInstance = lookUpVariable(((Expr.Pocket) expr.value).identifier, expr.value);
			evaluated = pocketInstance;
		} else if (expr.value instanceof Expr.Cup) {
			Expression expression = new Stmt.Expression(expr.value);
			execute(expression);
			Object cupInstance = lookUpVariable(((Expr.Cup) expr.value).identifier, expr.value);
			evaluated = cupInstance;
		} else if (expr.value instanceof Expr.Knot) {
			Expression expression = new Stmt.Expression(expr.value);
			execute(expression);
			Object cupInstance = lookUpVariable(((Expr.Knot) expr.value).identifier, expr.value);
			evaluated = cupInstance;
		} else {
			evaluated = evaluate(expr.value);
		}

		((BoxInstance) boxInstance).setAt(evaluated, 0);

		return null;
	}

	@Override
	public Object visitTesBoxCupPocketExpr(TesBoxCupPocket expr) {
		fromCall = false;

		TegBoxCupPocket tegBoxCupPocket = new Expr.TegBoxCupPocket(expr.object, expr.name);
		Object boxInstance = visitTegBoxCupPocketExpr(tegBoxCupPocket);
		Object evaluated = null;

		if (expr.value instanceof Expr.Boxx) {
			Expression expression = new Stmt.Expression(expr.value);
			execute(expression);
			Object instance = lookUpVariable(((Expr.Boxx) expr.value).identifier, expr.value);
			evaluated = instance;
		} else if (expr.value instanceof Expr.Pocket) {
			Expression expression = new Stmt.Expression(expr.value);
			execute(expression);
			Object pocketInstance = lookUpVariable(((Expr.Pocket) expr.value).identifier, expr.value);
			evaluated = pocketInstance;
		} else if (expr.value instanceof Expr.Cup) {
			Expression expression = new Stmt.Expression(expr.value);
			execute(expression);
			Object cupInstance = lookUpVariable(((Expr.Cup) expr.value).identifier, expr.value);
			evaluated = cupInstance;
		} else if (expr.value instanceof Expr.Knot) {
			Expression expression = new Stmt.Expression(expr.value);
			execute(expression);
			Object cupInstance = lookUpVariable(((Expr.Knot) expr.value).identifier, expr.value);
			evaluated = cupInstance;
		} else {
			evaluated = evaluate(expr.value);
		}
		((BoxInstance) boxInstance).setAt(evaluated, 0);
		return null;
	}




	public void setForward(boolean forward) {
		this.forward= forward;
	}




	public void setBackward(boolean backward) {
		this.backward =backward;
	}

}
