package Box.Syntax;

import java.util.List;
import java.util.ArrayList;
import Box.Token.Token;
import Box.Parser.LogicalOrStorage;

public abstract class StmtextendsDeclaration {
	public interface Visitor<R> {
	R visitExpressionStmt(Expression stmt);
	}
public static class Expression extends Stmt {
	 public Expression(ExprOLD expression) {
	this.expression = expression;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
	 	return visitor.visitExpressionStmt(this);
	}

	public  ExprOLD expression;
	}

 public abstract <R> R accept(Visitor<R> visitor);
}
