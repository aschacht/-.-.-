package Parser;

import java.util.List;
import java.util.ArrayList;
import Box.Token.Token;
import Parser.Fun.*;
import Parser.Stmt.*;
import Parser.Expr.*;

public abstract class Declaration {
	public interface Visitor<R> {
	R visitFunDeclDeclaration(FunDecl declaration);
	R visitStmtDeclDeclaration(StmtDecl declaration);
	R visitFunctionFun(Function fun);
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
	R visitMonoExpr(Mono expr);
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
