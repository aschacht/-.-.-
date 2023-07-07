package Box.Syntax;
import Box.Syntax.Stmt;

public class StmtKnot{
	Stmt statement;
	int position;
	public StmtKnot(Stmt statement,int pos){
		this.statement=statement;
		position=pos;
	}
	
	public Stmt getExpressionnoisserpxe() {
		return statement;
	}
	
	public int getPosition() {
		return position;
	}
	
}
