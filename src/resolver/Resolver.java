package resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import Box.Box.Box;
import Box.Interpreter.Interpreter;
import Box.Interpreter.KnotTracker;
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
import Box.Syntax.Expr.Sniatnoc;
import Box.Syntax.Expr.Teg;
import Box.Syntax.Expr.TegBoxCupPocket;
import Box.Syntax.Expr.Tes;
import Box.Syntax.Expr.Tnemngissa;
import Box.Syntax.Expr.Tonk;
import Box.Syntax.Expr.Type;
import Box.Syntax.Expr.UnKnown;
import Box.Syntax.Expr.Unary;
import Box.Syntax.Expr.UnknownnwonknU;
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
import Box.Syntax.Stmt.Return;
import Box.Syntax.Stmt.Save;
import Box.Syntax.Stmt.Tnirp;
import Box.Syntax.Stmt.UnDetermined;
import Box.Syntax.Stmt.Var;
import Box.Syntax.Stmt.VarFB;
import Box.Token.Token;
import Box.Token.TokenType;

public class Resolver implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

	private Interpreter interpreter;
	private final Stack<Map<String, Boolean>> scopes = new Stack<>();
	private FunctionType currentFunction = FunctionType.NONE;
	private KnotTracker tracker = new KnotTracker();

	private ClassType currentClass = ClassType.NONE;

	public Resolver(Interpreter interpreter) {
		this.interpreter = interpreter;

	}

	private void endScope() {
		scopes.pop();
	}

	private void beginScope() {
		scopes.push(new HashMap<String, Boolean>());
	}

	public void resolve(List<List<Stmt>> statementLists) {

		for (List<Stmt> statements : statementLists) {
			
			for (Stmt stmt : statements) {
				resolve(stmt);
				
			}
			
		}

	}

	private void resolve(Stmt stmt) {
		stmt.accept(this);
	}

	private void resolve(Expr expr) {
		expr.accept(this);
	}

	@Override
	public Void visitFunctionStmt(Function stmt) {
		if (stmt.identifierfun0 instanceof Expr.Variable) {
			declare(((Expr.Variable) stmt.identifierfun0).name);
			define(((Expr.Variable) stmt.identifierfun0).name);
		}
		if (stmt.identifierfun0 instanceof Expr.Elbairav) {
			declare(((Expr.Elbairav) stmt.identifierfun0).name);
			define(((Expr.Elbairav) stmt.identifierfun0).name);
		}

		if (stmt.identifierfun1 instanceof Expr.Variable) {
			declare(((Expr.Variable) stmt.identifierfun1).name);
			define(((Expr.Variable) stmt.identifierfun1).name);
		}
		if (stmt.identifierfun1 instanceof Expr.Elbairav) {
			declare(((Expr.Elbairav) stmt.identifierfun1).name);
			define(((Expr.Elbairav) stmt.identifierfun1).name);
		}


		resolveFunction(stmt, FunctionType.FUNCTION);
		return null;
	}

	private void resolveFunction(Function stmt, FunctionType function) {
		FunctionType enclosingFunction = currentFunction;
		currentFunction = function;

		beginScope();
		if (stmt.paramsfun0 != null)
			for (Expr param : stmt.paramsfun0) {
				if (param instanceof Expr.Variable) {
					declare(((Expr.Variable) param).name);
					define(((Expr.Variable) param).name);
				}
				if (param instanceof Expr.Elbairav) {
					declare(((Expr.Elbairav) param).name);
					define(((Expr.Elbairav) param).name);
				}
			}
		if (((Expr.Knot) stmt.knotfun0) != null) {
			for (Stmt stmtExpression : ((Expr.Knot) stmt.knotfun0).expression) {
				resolve(stmtExpression);
				
			}
			
			
		}
		endScope();
		currentFunction = enclosingFunction;

		enclosingFunction = currentFunction;
		currentFunction = function;

		beginScope();
		if (stmt.paramsfun1 != null)
			for (Expr param : stmt.paramsfun1) {
				if (param instanceof Expr.Variable) {
					declare(((Expr.Variable) param).name);
					define(((Expr.Variable) param).name);
				}
				if (param instanceof Expr.Elbairav) {
					declare(((Expr.Elbairav) param).name);
					define(((Expr.Elbairav) param).name);
				}
			}
		if (((Expr.Knot) stmt.knotfun1) != null) {
			for (Stmt stmtExpression : ((Expr.Knot) stmt.knotfun1).expression) {
				resolve(stmtExpression);
				
			}
			
		}
		endScope();
		currentFunction = enclosingFunction;
	}

	@Override
	public Void visitIfStmt(If stmt) {

		resolve(stmt.ifPocket);
		resolve(stmt.ifCup);
		if (stmt.elseIfStmt != null) {
			resolve(stmt.elseIfStmt);
		}
		if (stmt.elseCup != null) {
			resolve(stmt.elseCup);
		}

		return null;
	}

	@Override
	public Void visitPrintStmt(Print stmt) {
		resolve(stmt.expression);
		return null;
	}

	@Override
	public Void visitReturnStmt(Return stmt) {

		if (currentFunction == FunctionType.NONE) {
			Box.error(stmt.keyWord, "Can't return from top-level code.");
		}
		if (stmt.expression != null) {
			if (currentFunction == FunctionType.INITILIZER) {
				Box.error(stmt.keyWord, "Can't return a value from an initilizer.");
			}
			resolve(stmt.expression);
		}
		return null;
	}

	@Override
	public Void visitVarStmt(Var stmt) {
		declare(stmt.name);
//		if (stmt.initializer != null) {
//			resolve(stmt.initializer);
//		}
		define(stmt.name);
		return null;
	}

	private void define(Token name) {
		if (scopes.isEmpty())
			return;
		scopes.peek().put(name.lexeme, true);
	}

	private void declare(Token name) {
		if (scopes.isEmpty())
			return;
		Map<String, Boolean> scope = scopes.peek();
		if (scope.containsKey(name.lexeme)) {
			Box.error(name, "Already variable with this name in this scope.");
		}
		scope.put(name.lexeme, false);

	}

	@Override
	public Void visitAssignmentExpr(Assignment expr) {
		resolve(expr.value);
		resolveLocal(expr, expr.name);
		return null;
	}

	@Override
	public Void visitBinaryExpr(Binary expr) {
		resolve(expr.left);
		resolve(expr.right);

		return null;
	}


	@Override
	public Void visitLiteralExpr(Literal expr) {

		return null;
	}

	@Override
	public Void visitLogicalExpr(Logical expr) {
		resolve(expr.left);
		resolve(expr.right);
		return null;
	}

	@Override
	public Void visitUnaryExpr(Unary expr) {
		resolve(expr.right);
		return null;
	}

	@Override
	public Void visitVariableExpr(Variable expr) {

		if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
			Box.error(expr.name, "Can't read local variable in its own initilizer.");
		}
		resolveLocal(expr, expr.name);
		return null;
	}

	private void resolveLocal(Expr expr, Token name) {
		for (int i = scopes.size() - 1; i >= 0; i--) {
			if (scopes.get(i).containsKey(name.lexeme)) {
				interpreter.resolve(expr, scopes.size() - 1 - i);
				return;
			}
		}
	}


	@Override
	public Void visitSetExpr(Set expr) {
		resolve(expr.value);
		resolve(expr.object);
		return null;
	}

	@Override
	public Void visitExpressionStmt(Expression stmt) {
		resolve(stmt.expression);
		return null;
	}

	@Override
	public Void visitSaveStmt(Save stmt) {
		resolve(stmt.filePathFileName);
		if (stmt.objecttosave != null)
			resolve(stmt.objecttosave);
		return null;
	}

	@Override
	public Void visitExpelStmt(Expel stmt) {
		resolve(stmt.filePath);
		resolve(stmt.toExpell);
		return null;
	}

	@Override
	public Void visitReadStmt(Read stmt) {
		resolve(stmt.filePath);
		resolve(stmt.objectToReadInto);
		return null;
	}

	@Override
	public Void visitConsumeStmt(Consume stmt) {
		resolve(stmt.boxToFill);
		resolve(stmt.filePath);
		return null;
	}

	@Override
	public Void visitRenameStmt(Rename stmt) {
		resolve(stmt.filenewname);
		resolve(stmt.filePathAndName);
		return null;
	}

	@Override
	public Void visitMoveStmt(Move stmt) {
		resolve(stmt.newfilePath);
		resolve(stmt.OringialfilePathAndFile);
		return null;
	}

	@Override
	public Void visitConstructorStmt(Constructor stmt) {
		resolve(stmt.prototype);
		return null;
	}

	@Override
	public Void visitNoisserpxeStmt(Noisserpxe stmt) {
		resolve(stmt.noisserpex);
		return null;
	}

	@Override
	public Void visitTnirpStmt(Tnirp stmt) {
		resolve(stmt.expression);
		return null;
	}

	@Override
	public Void visitNruterStmt(Nruter stmt) {
		if (currentFunction == FunctionType.NONE) {
			Box.error(stmt.keyword, "Can't return from top-level code.");
		}
		if (stmt.expression != null) {
			if (currentFunction == FunctionType.INITILIZER) {
				Box.error(stmt.keyword, "Can't return a value from an initilizer.");
			}
			resolve(stmt.expression);
		}
		return null;
	}

	@Override
	public Void visitEvasStmt(Evas stmt) {
		resolve(stmt.filePathFileName);
		if (stmt.objecttosave != null)
			resolve(stmt.objecttosave);
		return null;
	}

	@Override
	public Void visitDaerStmt(Daer stmt) {
		resolve(stmt.filePath);
		resolve(stmt.objectToReadInto);
		return null;
	}

	@Override
	public Void visitEmanerStmt(Emaner stmt) {
		resolve(stmt.filenewname);
		resolve(stmt.filePathAndName);
		return null;
	}

	@Override
	public Void visitEvomStmt(Evom stmt) {
		resolve(stmt.newfilePath);
		resolve(stmt.OringialfilePathAndFile);
		return null;
	}

	@Override
	public Void visitRavStmt(Rav stmt) {
		declare(stmt.name);
		if (stmt.initializer != null) {
			resolve(stmt.initializer);
		}
		define(stmt.name);
		return null;
	}

	@Override
	public Void visitPassThroughStmt(PassThrough stmt) {

		return null;
	}

	@Override
	public Object visitContainsExpr(Contains expr) {
		resolve(expr.container);
		resolve(expr.contents);
		return null;
	}

	@Override
	public Object visitMonoExpr(Mono expr) {
		resolve(expr.value);
		return null;
	}

	@Override
	public Object visitLogExpr(Log expr) {
		resolve(expr.value);
		resolve(expr.valueBase);

		return null;
	}

	@Override
	public Object visitFactorialExpr(Factorial expr) {
		resolve(expr.value);
		return null;
	}

	@Override
	public Object visitPocketExpr(Pocket expr) {
		declare(expr.identifier);
		define(expr.identifier);
		beginScope();
		
		for (Stmt stmtExpression : expr.expression) {
			resolve(stmtExpression);
			
		}
		endScope();
		resolveLocal(expr, expr.identifier);

		return null;
	}

	@Override
	public Object visitCupExpr(Cup expr) {
		declare(expr.identifier);
		define(expr.identifier);
		beginScope();
		for (Stmt stmtExpression : expr.expression) {
			resolve(stmtExpression);
			
		}
		endScope();
		resolveLocal(expr, expr.identifier);

		return null;
	}

	@Override
	public Object visitBoxxExpr(Boxx expr) {
		declare(expr.identifier);
		define(expr.identifier);
		beginScope();
		List<Expr> primarys = expr.primarys;
		for (Expr prim : primarys) {
			resolve(prim);
		}
		endScope();
		resolveLocal(expr, expr.identifier);

		return null;
	}

	@Override
	public Object visitKnotExpr(Knot expr) {
		beginScope();
		for (Stmt stmtExpression : expr.expression) {
			resolve(stmtExpression);
			
		}
		endScope();
		return null;
	}

	@Override
	public Object visitCupOpenRightExpr(CupOpenRight expr) {

		return null;
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
	public Object visitLashExpr(Lash expr) {

		return null;
	}

	@Override
	public Object visitLidExpr(Lid expr) {

		return null;
	}

	@Override
	public Object visitTnemngissaExpr(Tnemngissa expr) {
		resolve(expr.value);
		resolveLocal(expr, expr.name);
		return null;
	}

	@Override
	public Object visitSniatnocExpr(Sniatnoc expr) {
		resolve(expr.container);
		resolve(expr.contents);
		return null;
	}

	@Override
	public Object visitYranibExpr(Yranib expr) {
		resolve(expr.left);
		resolve(expr.right);
		return null;
	}

	@Override
	public Object visitOnomExpr(Onom expr) {
		resolve(expr.value);
		return null;
	}

	@Override
	public Object visitLacigolExpr(Lacigol expr) {
		resolve(expr.left);
		resolve(expr.right);
		return null;
	}

	@Override
	public Object visitGolExpr(Gol expr) {
		resolve(expr.value);
		resolve(expr.valueBase);
		return null;
	}

	@Override
	public Object visitYranuExpr(Yranu expr) {
		resolve(expr.left);
		return null;
	}

	@Override
	public Object visitLlacExpr(Llac expr) {
		resolve(expr.callee);

		for (Expr argument : expr.arguments) {
			resolve(argument);
		}
		return null;
	}

	@Override
	public Object visitTegExpr(Teg expr) {
		resolve(expr.object);
		return null;
	}

	@Override
	public Object visitTesExpr(Tes expr) {
		resolve(expr.value);
		resolve(expr.object);
		return null;
	}

	@Override
	public Object visitLaretilExpr(Laretil expr) {

		return null;
	}

	@Override
	public Object visitLairotcafExpr(Lairotcaf expr) {
		resolve(expr.value);
		return null;
	}

	@Override
	public Object visitElbairavExpr(Elbairav expr) {
		if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
			Box.error(expr.name, "Can't read local variable in its own initilizer.");
		}
		resolveLocal(expr, expr.name);
		return null;
	}

	@Override
	public Object visitParameterExpr(Parameter expr) {

		return null;
	}

	@Override
	public Void visitFiStmt(Fi stmt) {
		resolve(stmt.fiPocket);
		resolve(stmt.fiCup);
		if (stmt.fiesleStmt != null) {
			resolve(stmt.fiesleStmt);
		}
		if (stmt.esleCup != null) {
			resolve(stmt.esleCup);
		}
		return null;
	}

	@Override
	public Object visitLiteralCharExpr(LiteralChar expr) {

		return null;
	}

	@Override
	public Object visitLaretilCharExpr(LaretilChar expr) {

		return null;
	}

	@Override
	public Object visitTypeExpr(Type expr) {

		return null;
	}

	@Override
	public Object visitEpytExpr(Epyt expr) {

		return null;
	}

	@Override
	public Object visitGetBoxCupPocketExpr(GetBoxCupPocket expr) {
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
		resolve(boxCupOrPocket);

		return null;
	}

	@Override
	public Object visitTegBoxCupPocketExpr(TegBoxCupPocket expr) {

		return null;
	}



	@Override
	public Object visitPupExpr(Pup expr) {
		
		return null;
	}

	@Override
	public Object visitCocketExpr(Cocket expr) {
		
		return null;
	}

	@Override
	public Object visitLocketExpr(Locket expr) {
		
		return null;
	}

	@Override
	public Object visitLupExpr(Lup expr) {
		
		return null;
	}

	@Override
	public Object visitLilExpr(Lil expr) {
		
		return null;
	}

	@Override
	public Object visitPidExpr(Pid expr) {
		
		return null;
	}

	@Override
	public Object visitCidExpr(Cid expr) {
		
		return null;
	}

	@Override
	public Object visitPassThroughExpr(Expr.PassThrough expr) {
		
		return null;
	}



	@Override
	public Void visitUnDeterminedStmt(UnDetermined stmt) {
		
		return null;
	}

	@Override
	public Object visitCallExpr(Call expr) {
		
		return null;
	}

	@Override
	public Object visitGetExpr(Get expr) {
		
		return null;
	}

	@Override
	public Object visitUnKnownExpr(UnKnown expr) {
		
		return null;
	}

	@Override
	public Object visitUnknownnwonknUExpr(UnknownnwonknU expr) {
		
		return null;
	}

	@Override
	public Void visitVarFBStmt(VarFB stmt) {
		
		return null;
	}

	@Override
	public Object visitTonkExpr(Tonk expr) {
		
		return null;
	}




}
