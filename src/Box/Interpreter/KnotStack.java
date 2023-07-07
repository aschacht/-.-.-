package Box.Interpreter;

import java.util.ArrayList;
import java.util.List;

import Box.Syntax.Expr;
import Box.Syntax.Expr.CupOpenLeft;
import Box.Syntax.Expr.CupOpenRight;
import Box.Syntax.Expr.PocketOpenLeft;
import Box.Syntax.Expr.PocketOpenRight;
import Box.Syntax.Stmt;

public class KnotStack {
	ArrayList<Expr.CupOpenLeft> cupLeft = new ArrayList<Expr.CupOpenLeft>();
	ArrayList<Expr.CupOpenRight> cupRight = new ArrayList<Expr.CupOpenRight>();
	ArrayList<Expr.PocketOpenLeft> pocketLeft = new ArrayList<Expr.PocketOpenLeft>();
	ArrayList<Expr.PocketOpenRight> pocketRight = new ArrayList<Expr.PocketOpenRight>();
	private List<Stmt> statements;

	public void addCupLeft(Expr.CupOpenLeft left) {
		cupLeft.add(left);
	}

	public ArrayList<CupOpenRight> getallCupRight() {

		return cupRight;
	}

	public ArrayList<CupOpenLeft> getallCupLeft() {

		return cupLeft;
	}

	public ArrayList<PocketOpenRight> getallPocketRight() {

		return pocketRight;
	}

	public ArrayList<PocketOpenLeft> getallPocketLeft() {

		return pocketLeft;
	}

	public void clear() {
		cupLeft = new ArrayList<Expr.CupOpenLeft>();
		cupRight = new ArrayList<Expr.CupOpenRight>();
		pocketLeft = new ArrayList<Expr.PocketOpenLeft>();
		pocketRight = new ArrayList<Expr.PocketOpenRight>();
	}

	public void addall(ArrayList<Expr.CupOpenLeft> cl, ArrayList<Expr.CupOpenRight> cr,
			ArrayList<Expr.PocketOpenLeft> pl, ArrayList<Expr.PocketOpenRight> pr) {
		cupLeft = cl;
		cupRight = cr;
		pocketLeft = pl;
		pocketRight = pr;
	}

	public void addCupRight(Expr.CupOpenRight right) {
		cupRight.add(right);
	}

	public void addPocketLeft(Expr.PocketOpenLeft left) {
		pocketLeft.add(left);
	}

	public void addPocketRight(Expr.PocketOpenRight right) {
		pocketRight.add(right);
	}

	public void removeCupLeft() {
		cupLeft.remove(cupLeft.size() - 1);
	}

	public void removeCupRight() {
		cupRight.remove(cupRight.size() - 1);
	}

	public void removePocketLeft() {
		pocketLeft.remove(pocketLeft.size() - 1);
	}

	public void removePocketRight() {
		pocketRight.remove(pocketRight.size() - 1);
	}

	public CupOpenLeft getCupLeft() {
		return cupLeft.get(cupLeft.size() - 1);
	}

	public CupOpenRight getCupRight() {
		return cupRight.get(cupRight.size() - 1);
	}

	public PocketOpenLeft getPocketLeft() {
		return pocketLeft.get(pocketLeft.size() - 1);
	}

	public PocketOpenRight getPocketRight() {
		return pocketRight.get(pocketRight.size() - 1);
	}

	public void loadStatements(List<Stmt> statements) {
		this.statements = statements;
	}

	public Stmt getStatement(CupOpenRight expr) {
		for (Stmt stmt : statements) {
			if (stmt instanceof Stmt.Expression) {
				if (((Stmt.Expression) stmt).expression instanceof Expr.Cup) {
					if (((Expr.Cup) ((Stmt.Expression) stmt).expression).identifier.lexeme
							.equalsIgnoreCase(expr.Literal.identifierToken.lexeme)) {
						return stmt;
					}
				}
			}
			if (stmt instanceof Stmt.Noisserpxe) {
				if (((Stmt.Noisserpxe) stmt).noisserpex instanceof Expr.Cup) {
					if (((Expr.Cup) ((Stmt.Noisserpxe) stmt).noisserpex).identifier.lexeme
							.equalsIgnoreCase(expr.Literal.identifierToken.lexeme)) {
						return stmt;
					}
				}
			}
		}
		return null;
	}

	public ArrayList<Stmt> getStatements() {
		
		return (ArrayList<Stmt>) this.statements;
	}

	public Stmt getStatement(PocketOpenRight expr) {
		for (Stmt stmt : statements) {
			if (stmt instanceof Stmt.Expression) {
				if (((Stmt.Expression) stmt).expression instanceof Expr.Pocket) {
					if (((Expr.Pocket) ((Stmt.Expression) stmt).expression).identifier.lexeme
							.equalsIgnoreCase(expr.Literal.identifierToken.lexeme)) {
						return stmt;
					}
				}
			}
			if (stmt instanceof Stmt.Noisserpxe) {
				if (((Stmt.Noisserpxe) stmt).noisserpex instanceof Expr.Pocket) {
					if (((Expr.Pocket) ((Stmt.Noisserpxe) stmt).noisserpex).identifier.lexeme
							.equalsIgnoreCase(expr.Literal.identifierToken.lexeme)) {
						return stmt;
					}
				}
			}
		}
		return null;
	}

	public Stmt getStatement(CupOpenLeft expr) {
		for (Stmt stmt : statements) {
			if (stmt instanceof Stmt.Expression) {
				if (((Stmt.Expression) stmt).expression instanceof Expr.Cup) {
					if (((Expr.Cup) ((Stmt.Expression) stmt).expression).identifier.lexeme
							.equalsIgnoreCase(expr.Literal.reifitnediToken.lexeme)) {
						return stmt;
					}
				}
			}
			if (stmt instanceof Stmt.Noisserpxe) {
				if (((Stmt.Noisserpxe) stmt).noisserpex instanceof Expr.Cup) {
					if (((Expr.Cup) ((Stmt.Noisserpxe) stmt).noisserpex).identifier.lexeme
							.equalsIgnoreCase(expr.Literal.reifitnediToken.lexeme)) {
						return stmt;
					}
				}
			}
		}
		return null;
	}

}
