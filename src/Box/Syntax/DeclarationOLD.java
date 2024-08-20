package Box.Syntax;

public abstract class DeclarationOLD {
	public interface Visitor<R> {
	R visitFunDeclDeclaration(FunDecl declaration);
	R visitStmtDeclDeclaration(StmtDecl declaration);
	R visitFunctionFun(FunOLD.Function function);
	R visitExpressionStmt(StmtOLD.Expression expression);
	R visitIfStmt(StmtOLD.If stmt);
	R visitPrintStmt(StmtOLD.Print stmt);
	R visitReturnStmt(StmtOLD.Return stmt);
	R visitSaveStmt(StmtOLD.Save stmt);
	R visitExpelStmt(StmtOLD.Expel stmt);
	R visitReadStmt(StmtOLD.Read stmt);
	R visitConsumeStmt(StmtOLD.Consume stmt);
	R visitRenameStmt(StmtOLD.Rename stmt);
	R visitMoveStmt(StmtOLD.Move stmt);
	R visitFiStmt(StmtOLD.Fi stmt);
	R visitTnirpStmt(StmtOLD.Tnirp stmt);
	R visitNruterStmt(StmtOLD.Nruter stmt);
	R visitEvasStmt(StmtOLD.Evas stmt);
	R visitDaerStmt(StmtOLD.Daer stmt);
	R visitEmanerStmt(StmtOLD.Emaner stmt);
	R visitEvomStmt(StmtOLD.Evom stmt);
	R visitVarStmt(StmtOLD.Var stmt);
	R visitRavStmt(StmtOLD.Rav stmt);
	R visitAssignmentExpr(ExprOLD.Assignment expr);
	R visitContainsExpr(ExprOLD.Contains expr);
	R visitBinaryExpr(ExprOLD.Binary expr);
	R visitMonoExpr(ExprOLD.Mono expr);
	R visitLogExpr(ExprOLD.Log expr);
	R visitFactorialExpr(ExprOLD.Factorial expr);
	R visitUnaryExpr(ExprOLD.Unary expr);
	R visitCallExpr(ExprOLD.Call call);
	R visitGetExpr(ExprOLD.Get get);
	R visitSetExpr(ExprOLD.Set expr);
	R visitTnemngissaExpr(ExprOLD.Tnemngissa expr);
	R visitSniatnocExpr(ExprOLD.Sniatnoc expr);
	R visitYranibExpr(ExprOLD.Yranib expr);
	R visitOnomExpr(ExprOLD.Onom expr);
	R visitGolExpr(ExprOLD.Gol expr);
	R visitLairotcafExpr(ExprOLD.Lairotcaf expr);
	R visitYranuExpr(ExprOLD.Yranu expr);
	R visitLlacExpr(ExprOLD.Llac expr);
	R visitTegExpr(ExprOLD.Teg expr);
	R visitTesExpr(ExprOLD.Tes expr);
	R visitVariableExpr(ExprOLD.Variable expr);
	R visitLiteralExpr(ExprOLD.Literal expr);
	R visitLiteralCharExpr(ExprOLD.LiteralChar expr);
	R visitCupExpr(ExprOLD.Cup expr);
	R visitPocketExpr(ExprOLD.Pocket expr);
	R visitKnotExpr(ExprOLD.Knot expr);
	R visitTonkExpr(ExprOLD.Tonk expr);
	R visitBoxExpr(ExprOLD.Box expr);
	}
public static class FunDecl extends DeclarationOLD {
	 public FunDecl(FunOLD function) {
	this.function = function;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFunDeclDeclaration(this);
	}

	public  FunOLD function;
	}
public static class StmtDecl extends DeclarationOLD {
	 public StmtDecl(StmtOLD statement) {
	this.statement = statement;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitStmtDeclDeclaration(this);
	}

	public  StmtOLD statement;
	}

 public abstract <R> R accept(Visitor<R> visitor);
}
