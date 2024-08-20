package resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import Box.Box.Box;
import Box.Interpreter.Interpreter;
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
import Box.Parser.Stmt.Expression;
import Box.Parser.Stmt.Fi;
import Box.Parser.Stmt.If;
import Box.Parser.Stmt.Move;
import Box.Parser.Stmt.Nruter;
import Box.Parser.Stmt.Print;
import Box.Parser.Stmt.Rav;
import Box.Parser.Stmt.Read;
import Box.Parser.Stmt.Rename;
import Box.Parser.Stmt.Return;
import Box.Parser.Stmt.Save;
import Box.Parser.Stmt.Tnirp;
import Box.Parser.Stmt.Var;
import Box.Token.Token;
import Box.Token.TokenType;
import Box.Syntax.*;

public class Resolver implements Declaration.Visitor<Void> {
	private Interpreter interpreter;
	private final Stack<Map<String, Boolean>> scopes = new Stack<>();
	private FunctionType currentFunction = FunctionType.NONE;
	private ClassType currentClass = ClassType.NONE;

	public Resolver(Interpreter interpreter) {
		this.interpreter = interpreter;
	}

	private void beginScope() {
		scopes.push(new HashMap<String, Boolean>());
	}

	private void endScope() {
		scopes.pop();
	}

	public void resolve(List<List<Declaration>> statementLists) {

		for (List<Declaration> statements : statementLists) {

			for (Declaration stmt : statements) {
				resolve(stmt);

			}

		}

	}

	private void resolve(Expr expr) {
		expr.accept(this);
	}

	private void resolve(Stmt stmt) {
		stmt.accept(this);
	}

	private void resolve(Fun func) {
		func.accept(this);
	}

	private void resolve(Declaration dec) {
		dec.accept(this);
	}

	private void resolveLocal(Expr expr, Token name) {
		for (int i = scopes.size() - 1; i >= 0; i--) {
			if (scopes.get(i).containsKey(name.lexeme)) {
				interpreter.resolve(expr, scopes.size() - 1 - i);
				return;
			}
		}
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
			Box.error(name, "Already variable with this name in this scope.",true);
		}
		scope.put(name.lexeme, false);

	}

	@Override
	public Void visitFunDeclDeclaration(FunDecl dec) {
		resolve(dec.function);
		return null;
	}

	@Override
	public Void visitStmtDeclDeclaration(StmtDecl dec) {
		resolve(dec.statement);
		return null;
	}

	@Override
	public Void visitFunctionFun(Function fun) {
		if (fun.forwardIdentifier != null) {
			declare(fun.forwardIdentifier);
			define(fun.forwardIdentifier);
			FunctionType enclosingFunction = currentFunction;
			currentFunction = FunctionType.FUNCTION;

			beginScope();
			for (Token param : fun.forwardPrametersNames) {
				declare(param);
				define(param);
			}
			resolve(fun.sharedCupOrPocketOrKnot);
			endScope();
			currentFunction = enclosingFunction;
		}
		if (fun.backwardIdentifier != null) {
			declare(fun.backwardIdentifier);
			define(fun.backwardIdentifier);
			FunctionType enclosingFunction = currentFunction;
			currentFunction = FunctionType.FUNCTION;

			beginScope();
			for (Token param : fun.backwardPrametersNames) {
				declare(param);
				define(param);
			}
			resolve(fun.sharedCupOrPocketOrKnot);
			endScope();
			currentFunction = enclosingFunction;
		}
		return null;
	}

	@Override
	public Void visitExpressionStmt(Expression stmt) {
		if(stmt.expression!=null)
		resolve(stmt.expression);
		if(stmt.noisserpxe!=null)
			resolve(stmt.noisserpxe);
		return null;
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
			Box.error(stmt.keyWord, "Can't return from top-level code.",true);
		}
		if (stmt.expression != null) {
			if (currentFunction == FunctionType.INITILIZER) {
				Box.error(stmt.keyWord, "Can't return a value from an initilizer.",true);
			}
			resolve(stmt.expression);
		}
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
	public Void visitFiStmt(Fi stmt) {
		// TODO Auto-generated method stub
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
			Box.error(stmt.keyWord, "Can't return from top-level code.",true);
		}
		if (stmt.expression != null) {
			if (currentFunction == FunctionType.INITILIZER) {
				Box.error(stmt.keyWord, "Can't return a value from an initilizer.",true);
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
	public Void visitVarStmt(Var stmt) {
		declare(stmt.name);
		if (stmt.initilizer != null) {
			resolve(stmt.initilizer);
		}
		define(stmt.name);
		return null;
	}

	@Override
	public Void visitRavStmt(Rav stmt) {
		declare(stmt.name);
		if (stmt.initilizer != null) {
			resolve(stmt.initilizer);
		}
		define(stmt.name);
		return null;
	}

	@Override
	public Void visitAssignmentExpr(Assignment expr) {
		resolve(expr.value);
		resolveLocal(expr, expr.name);
		return null;
	}

	@Override
	public Void visitContainsExpr(Contains expr) {
		resolve(expr.container);
		resolve(expr.contents);
		return null;
	}

	@Override
	public Void visitBinaryExpr(Binary expr) {
		resolve(expr.left);
		resolve(expr.right);

		return null;
	}

	@Override
	public Void visitMonoExpr(Mono expr) {
		resolve(expr.value);
		return null;
	}

	@Override
	public Void visitLogExpr(Log expr) {
		resolve(expr.value);
		resolve(expr.valueBase);

		return null;
	}

	@Override
	public Void visitFactorialExpr(Factorial expr) {
		resolve(expr.value);
		return null;
	}

	@Override
	public Void visitUnaryExpr(Unary expr) {
		resolve(expr.right);
		return null;
	}

	@Override
	public Void visitCallExpr(Call expr) {
		return null;
	}

	@Override
	public Void visitGetExpr(Get expr) {
		return null;
	}

	@Override
	public Void visitSetExpr(Set expr) {
		resolve(expr.value);
		resolve(expr.object);
		return null;
	}

	@Override
	public Void visitTnemngissaExpr(Tnemngissa expr) {
		resolve(expr.value);
		resolveLocal(expr, expr.name);
		return null;
	}

	@Override
	public Void visitSniatnocExpr(Sniatnoc expr) {
		resolve(expr.container);
		resolve(expr.contents);
		return null;
	}

	@Override
	public Void visitYranibExpr(Yranib expr) {
		resolve(expr.left);
		resolve(expr.right);
		return null;
	}

	@Override
	public Void visitOnomExpr(Onom expr) {
		resolve(expr.value);
		return null;
	}

	@Override
	public Void visitGolExpr(Gol expr) {
		resolve(expr.value);
		resolve(expr.valueBase);
		return null;
	}

	@Override
	public Void visitLairotcafExpr(Lairotcaf expr) {
		resolve(expr.value);
		return null;
	}

	@Override
	public Void visitYranuExpr(Yranu expr) {
		resolve(expr.right);
		return null;
	}

	@Override
	public Void visitLlacExpr(Llac expr) {
		resolve(expr.callee);

		for (Expr argument : expr.arguments) {
			resolve(argument);
		}
		return null;
	}

	@Override
	public Void visitTegExpr(Teg expr) {
		resolve(expr.object);
		return null;
	}

	@Override
	public Void visitTesExpr(Tes expr) {
		resolve(expr.value);
		resolve(expr.object);
		return null;
	}

	@Override
	public Void visitVariableExpr(Variable expr) {
		if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
			Box.error(expr.name, "Can't read local variable in its own initilizer.",true);
		}
		resolveLocal(expr, expr.name);
		return null;
	}

	@Override
	public Void visitLiteralExpr(Literal expr) {
		
		return null;
	}

	@Override
	public Void visitLiteralCharExpr(LiteralChar expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitCupExpr(Cup expr) {
		declare(expr.identifier);
		define(expr.identifier);
		declare(expr.reifitnedi);
		define(expr.reifitnedi);
		beginScope();
		for (Declaration stmtExpression : expr.expression) {
			resolve(stmtExpression);

		}
		endScope();
		resolveLocal(expr, expr.identifier);
		resolveLocal(expr, expr.reifitnedi);

		return null;
	}

	@Override
	public Void visitPocketExpr(Pocket expr) {
		declare(expr.identifier);
		define(expr.identifier);
		declare(expr.reifitnedi);
		define(expr.reifitnedi);
		beginScope();

		for (Stmt stmtExpression : expr.expression) {
			resolve(stmtExpression);

		}
		endScope();
		resolveLocal(expr, expr.identifier);
		resolveLocal(expr, expr.reifitnedi);

		return null;
	}

	@Override
	public Void visitKnotExpr(Knot expr) {
		beginScope();
		for (Declaration stmtExpression : expr.expression) {
			resolve(stmtExpression);

		}
		endScope();
		return null;
	}

	@Override
	public Void visitTonkExpr(Tonk expr) {
		beginScope();
		for (Declaration stmtExpression : expr.expression) {
			resolve(stmtExpression);

		}
		endScope();
		return null;
	}

	@Override
	public Void visitBoxExpr(Expr.Box expr) {
		beginScope();
		declare(expr.identifier);
		declare(expr.reifitnedi);
		List<Stmt> primarys = expr.expression;
		for (Stmt prim : primarys) {
			resolve(prim);
		}
		define(expr.identifier);
		define(expr.reifitnedi);
		resolveLocal(expr, expr.identifier);
		resolveLocal(expr, expr.reifitnedi);
		endScope();

		return null;
	}

	@Override
	public Void visitSwapExpr(Swap expr) {
		// TODO Auto-generated method stub
		return null;
	}

}
