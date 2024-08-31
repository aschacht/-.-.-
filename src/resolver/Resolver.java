package resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import Box.Box.Box;
import Box.Interpreter.Interpreter;
import Box.Token.Token;
import Box.Token.TokenType;
import Parser.Declaration;
import Parser.Expr;
import Parser.Fun;
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
import Parser.Stmt.Expression;
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
import Parser.Stmt.Return;
import Parser.Stmt.Save;
import Parser.Stmt.Saveevas;
import Parser.Stmt.StmttmtS;
import Parser.Stmt.TemplatVar;
import Parser.Stmt.Tnirp;
import Parser.Stmt.Var;
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

	public void resolve(List<Declaration> statementLists) {
		beginScope();
		for (Declaration stmt : statementLists) {
			resolve(stmt);

		}
		endScope();

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
				
				interpreter.resolve(expr, (scopes.size() - 1) - i);
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
			Box.error(name, "Already variable with this name in this scope.", true);
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
			
			FunctionType enclosingFunction = currentFunction;
			currentFunction = FunctionType.FUNCTION;

			beginScope();
			for (Token param : fun.forwardPrametersNames) {
				declare(param);
				define(param);
			}
			resolve(fun.sharedCup);
			endScope();
			declare(fun.forwardIdentifier);
			define(fun.forwardIdentifier);
			currentFunction = enclosingFunction;
		}
		if (fun.backwardIdentifier != null) {
			
			FunctionType enclosingFunction = currentFunction;
			currentFunction = FunctionType.FUNCTION;

			beginScope();
			for (Token param : fun.backwardPrametersNames) {
				declare(param);
				define(param);
			}
			resolve(fun.sharedCup);
			endScope();
			declare(fun.backwardIdentifier);
			define(fun.backwardIdentifier);
			currentFunction = enclosingFunction;
		}
		return null;
	}

	@Override
	public Void visitExpressionStmt(Expression stmt) {
		if (stmt.expression != null)
			resolve(stmt.expression);

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
			Box.error(stmt.keyword, "Can't return from top-level code.", true);
		}
		if (stmt.expression != null) {
			if (currentFunction == FunctionType.INITILIZER) {
				Box.error(stmt.keyword, "Can't return a value from an initilizer.", true);
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
			Box.error(stmt.keyword, "Can't return from top-level code.", true);
		}
		if (stmt.expression != null) {
			if (currentFunction == FunctionType.INITILIZER) {
				Box.error(stmt.keyword, "Can't return a value from an initilizer.", true);
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
			Box.error(expr.name, "Can't read local variable in its own initilizer.", true);
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
		
		beginScope();
		for (Declaration stmtExpression : expr.expression) {
			resolve(stmtExpression);

		}
		endScope();
		declare(expr.identifier);
		define(expr.identifier);
		declare(expr.reifitnedi);
		define(expr.reifitnedi);
		resolveLocal(expr, expr.identifier);
		resolveLocal(expr, expr.reifitnedi);

		return null;
	}

	@Override
	public Void visitPocketExpr(Pocket expr) {
		
		beginScope();

		for (Stmt stmtExpression : expr.expression) {
			resolve(stmtExpression);

		}
		endScope();
		declare(expr.identifier);
		declare(expr.reifitnedi);
		define(expr.identifier);
		define(expr.reifitnedi);
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
		declare(expr.identifier);
		declare(expr.reifitnedi);
		define(expr.identifier);
		define(expr.reifitnedi);
		resolveLocal(expr, expr.identifier);
		resolveLocal(expr, expr.reifitnedi);
		return null;
	}

	@Override
	public Void visitTonkExpr(Tonk expr) {
		
		beginScope();
		for (Declaration stmtExpression : expr.expression) {
			resolve(stmtExpression);

		}
		endScope();
		declare(expr.identifier);
		declare(expr.reifitnedi);
		define(expr.identifier);
		define(expr.reifitnedi);
		resolveLocal(expr, expr.identifier);
		resolveLocal(expr, expr.reifitnedi);
		return null;
	}

	@Override
	public Void visitBoxExpr(Expr.Box expr) {
		
		beginScope();

		List<Stmt> primarys = expr.expression;
		for (Stmt prim : primarys) {
			resolve(prim);
		}

		
		endScope();
		declare(expr.identifier);
		declare(expr.reifitnedi);
		define(expr.identifier);
		define(expr.reifitnedi);
		resolveLocal(expr, expr.identifier);
		resolveLocal(expr, expr.reifitnedi);
		return null;
	}

	@Override
	public Void visitSwapExpr(Swap expr) {
		resolve(expr);
		return null;
	}

	@Override
	public Void visitTemplatVarStmt(TemplatVar stmt) {
		declare(stmt.name);

		define(stmt.name);
		return null;
	}

	@Override
	public Void visitIfiStmt(Ifi stmt) {
		resolve(stmt.ifPocket);
		resolve(stmt.elseIf);
		return null;
	}

	@Override
	public Void visitTemplateExpr(Template expr) {
		resolve(expr.container);
		return null;
	}

	@Override
	public Void visitFunctionLinkFun(FunctionLink fun) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitLinkExpr(Link expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitPocketOpenExpr(PocketOpen expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitPocketClosedExpr(PocketClosed expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitCupOpenExpr(CupOpen expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitCupClosedExpr(CupClosed expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitBoxOpenExpr(BoxOpen expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitBoxClosedExpr(BoxClosed expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitSaveevasStmt(Saveevas stmt) {
		resolve(stmt.filePathFileName);
		if (stmt.objecttosave != null)
			resolve(stmt.objecttosave);
		return null;
	}

	@Override
	public Void visitReaddaerStmt(Readdaer stmt) {
		resolve(stmt.filePath);
		resolve(stmt.objectToReadInto);
		return null;
	}

	@Override
	public Void visitRenameemanerStmt(Renameemaner stmt) {
		resolve(stmt.filenewname);
		resolve(stmt.filePathAndName);
		return null;
	}

	@Override
	public Void visitMoveevomStmt(Moveevom stmt) {
		resolve(stmt.newfilePath);
		resolve(stmt.OringialfilePathAndFile);
		return null;
	}

	@Override
	public Void visitMonoonomExpr(Monoonom expr) {
		resolve(expr.value);
		return null;
	}

	@Override
	public Void visitBinaryyranibExpr(Binaryyranib expr) {
		resolve(expr.left);
		resolve(expr.right);

		return null;
	}

	@Override
	public Void visitLoggolExpr(Loggol expr) {
		resolve(expr.value);
		resolve(expr.valueBase);
		return null;
	}

	@Override
	public Void visitCallllacExpr(Callllac expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitExpressiontmtsExpr(Expressiontmts expr) {
		resolve(expr.expression);

		if (expr.tnemetatsToken.type == TokenType.NRUTER) {
			if (currentFunction == FunctionType.NONE) {
				Box.error(expr.tnemetatsToken, "Can't return from top-level code.", true);
			}
			if (expr.expression != null) {
				if (currentFunction == FunctionType.INITILIZER) {
					Box.error(expr.tnemetatsToken, "Can't return a value from an initilizer.", true);
				}
				resolve(expr.expression);
			}
		}
		return null;
	}

	@Override
	public Void visitStmttmtSStmt(StmttmtS stmt) {

		if (stmt.keywordForward.type == TokenType.RETURN) {
			if (currentFunction == FunctionType.NONE) {
				Box.error(stmt.keywordForward, "Can't return from top-level code.", true);
			}
			if (stmt.expression != null) {
				if (currentFunction == FunctionType.INITILIZER) {
					Box.error(stmt.keywordForward, "Can't return a value from an initilizer.", true);
				}
				resolve(stmt.expression);
			}
		} else {
			resolve(stmt.expression);
		}

		if (stmt.keywordBackward.type == TokenType.NRUTER) {
			if (currentFunction == FunctionType.NONE) {
				Box.error(stmt.keywordBackward, "Can't return from top-level code.", true);
			}
			if (stmt.expression != null) {
				if (currentFunction == FunctionType.INITILIZER) {
					Box.error(stmt.keywordBackward, "Can't return a value from an initilizer.", true);
				}
				resolve(stmt.expression);
			}
		} else {
			resolve(stmt.expression);
		}
		return null;
	}

	@Override
	public Void visitStmtnoisserpxeStmt(Parser.Stmt.Stmtnoisserpxe stmt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitAssignmenttnemgissaExpr(Assignmenttnemgissa expr) {
		resolve(expr.value);
		resolveLocal(expr, expr.nameForward);
		resolveLocal(expr, expr.nameBackward);
		return null;
	}

}
