package Parser;

import java.util.List;
import java.util.ArrayList;
import Box.Token.Token;
import com.fasterxml.jackson.annotation.*;
import Parser.Fun.*;
import Parser.Stmt.*;
import Parser.Expr.*;

public abstract class Declaration {
	public interface Visitor<R> {
	R visitFunDeclDeclaration(FunDecl declaration);
	R visitStmtDeclDeclaration(StmtDecl declaration);
	R visitFunctionFun(Function fun);
	R visitFunctionLinkFun(FunctionLink fun);
	R visitExpressionStmt(Expression stmt);
	R visitIfStmt(If stmt);
	R visitPrintStmt(Print stmt);
	R visitSaveStmt(Save stmt);
	R visitReadStmt(Read stmt);
	R visitRenameStmt(Rename stmt);
	R visitMoveStmt(Move stmt);
	R visitReturnStmt(Return stmt);
	R visitVarStmt(Var stmt);
	R visitTemplatVarStmt(TemplatVar stmt);
	R visitExpelStmt(Expel stmt);
	R visitConsumeStmt(Consume stmt);
	R visitIfiStmt(Ifi stmt);
	R visitStmttmtSStmt(StmttmtS stmt);
	R visitSaveevasStmt(Saveevas stmt);
	R visitReaddaerStmt(Readdaer stmt);
	R visitRenameemanerStmt(Renameemaner stmt);
	R visitMoveevomStmt(Moveevom stmt);
	R visitStmtnoisserpxeStmt(Stmtnoisserpxe stmt);
	R visitRavStmt(Rav stmt);
	R visitNruterStmt(Nruter stmt);
	R visitEvomStmt(Evom stmt);
	R visitEmanerStmt(Emaner stmt);
	R visitDaerStmt(Daer stmt);
	R visitEvasStmt(Evas stmt);
	R visitTnirpStmt(Tnirp stmt);
	R visitFiStmt(Fi stmt);
	R visitAssignmentExpr(Assignment expr);
	R visitContainsExpr(Contains expr);
	R visitAdditiveExpr(Additive expr);
	R visitParamContOpExpr(ParamContOp expr);
	R visitNonParamContOpExpr(NonParamContOp expr);
	R visitSetatExpr(Setat expr);
	R visitSubExpr(Sub expr);
	R visitBinaryExpr(Binary expr);
	R visitMonoExpr(Mono expr);
	R visitLogExpr(Log expr);
	R visitFactorialExpr(Factorial expr);
	R visitUnaryExpr(Unary expr);
	R visitCallExpr(Call expr);
	R visitGetExpr(Get expr);
	R visitSetExpr(Set expr);
	R visitKnotExpr(Knot expr);
	R visitCupExpr(Cup expr);
	R visitTemplateExpr(Template expr);
	R visitLinkExpr(Link expr);
	R visitPocketExpr(Pocket expr);
	R visitBoxExpr(Box expr);
	R visitMonoonomExpr(Monoonom expr);
	R visitAddittiddaExpr(Addittidda expr);
	R visitParCoOppOoCraPExpr(ParCoOppOoCraP expr);
	R visitNoPaCoOOoCaPoNExpr(NoPaCoOOoCaPoN expr);
	R visitSetattatesExpr(Setattates expr);
	R visitSubbusExpr(Subbus expr);
	R visitBinaryyranibExpr(Binaryyranib expr);
	R visitLoggolExpr(Loggol expr);
	R visitCallllacExpr(Callllac expr);
	R visitExpressiontmtsExpr(Expressiontmts expr);
	R visitAssignmenttnemgissaExpr(Assignmenttnemgissa expr);
	R visitSwapExpr(Swap expr);
	R visitVariableExpr(Variable expr);
	R visitLiteralCharExpr(LiteralChar expr);
	R visitLiteralExpr(Literal expr);
	R visitPocketOpenExpr(PocketOpen expr);
	R visitPocketClosedExpr(PocketClosed expr);
	R visitCupOpenExpr(CupOpen expr);
	R visitCupClosedExpr(CupClosed expr);
	R visitBoxOpenExpr(BoxOpen expr);
	R visitBoxClosedExpr(BoxClosed expr);
	R visitTonkExpr(Tonk expr);
	R visitTesExpr(Tes expr);
	R visitTegExpr(Teg expr);
	R visitLlacExpr(Llac expr);
	R visitGolExpr(Gol expr);
	R visitLairotcafExpr(Lairotcaf expr);
	R visitOnomExpr(Onom expr);
	R visitYranibExpr(Yranib expr);
	R visitYranuExpr(Yranu expr);
	R visitBusExpr(Bus expr);
	R visitTatesExpr(Tates expr);
	R visitPoTnocMarapNonExpr(PoTnocMarapNon expr);
	R visitPoTnocMarapExpr(PoTnocMarap expr);
	R visitEvitiddaExpr(Evitidda expr);
	R visitSniatnocExpr(Sniatnoc expr);
	R visitTnemngissaExpr(Tnemngissa expr);
	R visitContainssniatnocExpr(Containssniatnoc expr);
	R visitLiteralBoolExpr(LiteralBool expr);
	R visitLiteralLoobExpr(LiteralLoob expr);
	}
public static class FunDecl extends Declaration {
	 public FunDecl(Fun function) {
	this.function = function;
	}

	public  FunDecl(FunDecl other) {
	this.function = other.function;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitFunDeclDeclaration(this);
	}



	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Fun function;
	}
public static class StmtDecl extends Declaration {
	 public StmtDecl(Stmt statement) {
	this.statement = statement;
	}

	public  StmtDecl(StmtDecl other) {
	this.statement = other.statement;
	}


	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitStmtDeclDeclaration(this);
	}



	@JsonInclude(JsonInclude.Include.NON_NULL)
	public  Stmt statement;
	}

 public abstract <R> R accept(Visitor<R> visitor);
}
