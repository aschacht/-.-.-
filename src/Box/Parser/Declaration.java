package Box.Parser;



import Box.Parser.Expr.Assignment;
import Box.Parser.Expr.Binary;
import Box.Parser.Expr.Box;
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


public abstract class Declaration {
	public interface Visitor<R> {
	R visitFunDeclDeclaration(FunDecl declaration);
	R visitStmtDeclDeclaration(StmtDecl declaration);
	R visitFunctionFun(Function function);
	R visitExpressionStmt(Expression stmt);
	R visitIfStmt(If stmt);
	R visitPrintStmt(Print stmt);
	R visitReturnStmt(Return stmt);
	R visitSaveStmt(Save stmt);
	R visitExpelStmt(Expel stmt);
	R visitReadStmt(Read stmt);
	R visitConsumeStmt(Consume stmt);
	R visitRenameStmt(Rename stmt);
	R visitMoveStmt(Move stmt);
	R visitFiStmt(Fi stmt);
	R visitTnirpStmt(Tnirp stmt);
	R visitNruterStmt(Nruter stmt);
	R visitEvasStmt(Evas stmt);
	R visitDaerStmt(Daer stmt);
	R visitEmanerStmt(Emaner stmt);
	R visitEvomStmt(Evom stmt);
	R visitVarStmt(Var stmt);
	R visitRavStmt(Rav stmt);
	R visitAssignmentExpr(Assignment expr);
	R visitContainsExpr(Contains expr);
	R visitBinaryExpr(Binary expr);
	R visitMonoExpr(Mono mono);
	R visitLogExpr(Log expr);
	R visitFactorialExpr(Factorial expr);
	R visitUnaryExpr(Unary expr);
	R visitCallExpr(Call expr);
	R visitGetExpr(Get expr);
	R visitSetExpr(Set expr);
	R visitSwapExpr(Swap expr);
	R visitTnemngissaExpr(Tnemngissa expr);
	R visitSniatnocExpr(Sniatnoc expr);
	R visitYranibExpr(Yranib expr);
	R visitOnomExpr(Onom expr);
	R visitGolExpr(Gol expr);
	R visitLairotcafExpr(Lairotcaf expr);
	R visitYranuExpr(Yranu expr);
	R visitLlacExpr(Llac expr);
	R visitTegExpr(Teg expr);
	R visitTesExpr(Tes expr);
	R visitVariableExpr(Variable expr);
	R visitLiteralExpr(Literal expr);
	R visitLiteralCharExpr(LiteralChar expr);
	R visitCupExpr(Cup expr);
	R visitPocketExpr(Pocket expr);
	R visitKnotExpr(Knot expr);
	R visitTonkExpr(Tonk expr);
	R visitBoxExpr(Box expr);
	}
public static class FunDecl extends Declaration {
	 public FunDecl(Fun function) {
	this.function = function;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFunDeclDeclaration(this);
	}

	public  Fun function;
	}
public static class StmtDecl extends Declaration {
	 public StmtDecl(Stmt statement) {
	this.statement = statement;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitStmtDeclDeclaration(this);
	}

	public  Stmt statement;
	}

 public abstract <R> R accept(Visitor<R> visitor);
}
