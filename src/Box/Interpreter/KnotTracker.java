package Box.Interpreter;

import java.util.ArrayList;
import java.util.List;

import Box.Syntax.Expr;
import Box.Syntax.Expr.CupOpenLeft;
import Box.Syntax.Expr.CupOpenRight;
import Box.Syntax.Expr.PocketOpenLeft;
import Box.Syntax.Expr.PocketOpenRight;
import Box.Syntax.Stmt;

public class KnotTracker {
	ArrayList<KnotStack> stack = new ArrayList<KnotStack>();
	

	public void add(KnotStack knotToAdd) {
		stack.add(knotToAdd);
	}

	public void remove() {
		stack.remove(stack.size() - 1);
	}

	public KnotStack get() {
		return stack.get(stack.size() - 1);
	}

	public void replace(KnotStack knotToPut) {
		stack.set(stack.size() - 1, knotToPut);
	}

	public void addCupLeft(Expr.CupOpenLeft left) {
		stack.get(stack.size() - 1).addCupLeft(left);
	}

	public void addCupRight(Expr.CupOpenRight right) {
		stack.get(stack.size() - 1).addCupRight(right);
	}

	public void addPocketLeft(Expr.PocketOpenLeft left) {
		stack.get(stack.size() - 1).addPocketLeft(left);
	}

	public void addPocketRight(Expr.PocketOpenRight right) {
		stack.get(stack.size() - 1).addPocketRight(right);
	}

	public void removeCupLeft() {
		stack.get(stack.size() - 1).removeCupLeft();
	}

	public void removeCupRight() {
		stack.get(stack.size() - 1).removeCupRight();
	}

	public void removePocketLeft() {
		stack.get(stack.size() - 1).removePocketLeft();
	}

	public void removePocketRight() {
		stack.get(stack.size() - 1).removePocketRight();
	}

	public CupOpenLeft getCupLeft() {
		return stack.get(stack.size() - 1).getCupLeft();
	}

	public CupOpenRight getCupRight() {
		return stack.get(stack.size() - 1).getCupRight();
	}

	public PocketOpenLeft getPocketLeft() {
		return stack.get(stack.size() - 1).getPocketLeft();
	}

	public PocketOpenRight getPocketRight() {
		return stack.get(stack.size() - 1).getPocketRight();
	}

	public void clear() {
		stack.get(stack.size() - 1).clear();
	}

	public ArrayList<Expr.CupOpenRight> getAllCupRight() {
		return stack.get(stack.size() - 1).getallCupRight();
	}

	public ArrayList<Expr.CupOpenLeft> getAllCupLeft() {
		return stack.get(stack.size() - 1).getallCupLeft();
	}

	public ArrayList<Expr.PocketOpenRight> getAllPocketRight() {
		return stack.get(stack.size() - 1).getallPocketRight();
	}

	public ArrayList<Expr.PocketOpenLeft> getAllPocketLeft() {
		return stack.get(stack.size() - 1).getallPocketLeft();
	}

	public void removeDuplicates() {

		ArrayList<PocketOpenLeft> getallPocketLeft = stack.get(stack.size() - 1).getallPocketLeft();
		ArrayList<PocketOpenRight> getallPocketRight = stack.get(stack.size() - 1).getallPocketRight();
		ArrayList<CupOpenLeft> getallCupLeft = stack.get(stack.size() - 1).getallCupLeft();
		ArrayList<CupOpenRight> getallCupRight = stack.get(stack.size() - 1).getallCupRight();

		for (int i = 0; i < getallPocketLeft.size(); i++) {
			for (int j = i + 1; j < getallPocketLeft.size(); j++) {
				if (getallPocketLeft.get(i).Literal == getallPocketLeft.get(j).Literal) {
					getallPocketLeft.remove(j);
					i--;
					break;
				}

			}
		}
		for (int i = 0; i < getallPocketRight.size(); i++) {
			for (int j = i + 1; j < getallPocketRight.size(); j++) {
				if (getallPocketRight.get(i).Literal == getallPocketRight.get(j).Literal) {
					getallPocketRight.remove(j);
					i--;
					break;
				}

			}
		}
		for (int i = 0; i < getallCupLeft.size(); i++) {
			for (int j = i + 1; j < getallCupLeft.size(); j++) {
				if (getallCupLeft.get(i).Literal == getallCupLeft.get(j).Literal) {
					getallCupLeft.remove(j);
					i--;
					break;
				}

			}
		}
		for (int i = 0; i < getallCupRight.size(); i++) {
			for (int j = i + 1; j < getallCupRight.size(); j++) {
				if (getallCupRight.get(i).Literal == getallCupRight.get(j).Literal) {
					getallCupRight.remove(j);
					i--;
					break;
				}

			}
		}

		stack.get(stack.size() - 1).addall(getallCupLeft, getallCupRight, getallPocketLeft, getallPocketRight);
	}

	public void loadStatements(List<Stmt> statements) {
		stack.get(stack.size() - 1).loadStatements( statements);
	}

	public Stmt getStatement(CupOpenRight expr) {

		
		return stack.get(stack.size()-1).getStatement(expr);
	}

	public ArrayList<Stmt> getStatements() {
		
		return stack.get(stack.size()-1).getStatements();
	}

	public Stmt getStatement(PocketOpenRight expr) {
		
		return stack.get(stack.size()-1).getStatement(expr);
	}

	public Stmt getStatement(CupOpenLeft expr) {
		return stack.get(stack.size()-1).getStatement(expr);
	}

}