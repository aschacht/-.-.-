package Box.Interpreter;

import java.util.ArrayList;
import java.util.Iterator;

import Box.Syntax.Expr;
import Box.Syntax.Stmt;
import Box.Token.Token;

public class TypesOfObject {

	private RunTimeTypes runtimeTypeForObject;
	private Token containerType;
	private Object initilizer;
	private ArrayList<Expr> prototype;

	TypesOfObject(Token containerType, RunTimeTypes runTimeType, Object initilizer) {
		this.containerType = containerType;
		this.runtimeTypeForObject = runTimeType;
		this.initilizer = initilizer;
		if (runtimeTypeForObject == RunTimeTypes.Knot) {
			this.prototype = new ArrayList<Expr>();
			for (int i = 0; i < ((Expr.Knot) initilizer).unGrouped.size(); i++) {
				Stmt stmt = ((Expr.Knot) initilizer).unGrouped.get(i);
				if (stmt instanceof Stmt.Expression) {
					if (((Stmt.Expression) stmt).expression instanceof Expr.CupOpenLeft) {
						prototype.add(((Stmt.Expression) stmt).expression);
					} else if (((Stmt.Expression) stmt).expression instanceof Expr.CupOpenRight) {
						prototype.add(((Stmt.Expression) stmt).expression);

					} else if (((Stmt.Expression) stmt).expression instanceof Expr.PocketOpenLeft) {
						prototype.add(((Stmt.Expression) stmt).expression);

					} else if (((Stmt.Expression) stmt).expression instanceof Expr.PocketOpenRight) {
						prototype.add(((Stmt.Expression) stmt).expression);

					}
				} else if (stmt instanceof Stmt.Noisserpxe) {
					if (((Stmt.Noisserpxe) stmt).noisserpex instanceof Expr.CupOpenLeft) {
						prototype.add(((Stmt.Noisserpxe) stmt).noisserpex);

					} else if (((Stmt.Noisserpxe) stmt).noisserpex instanceof Expr.CupOpenRight) {

						prototype.add(((Stmt.Noisserpxe) stmt).noisserpex);
					} else if (((Stmt.Noisserpxe) stmt).noisserpex instanceof Expr.PocketOpenLeft) {

						prototype.add(((Stmt.Noisserpxe) stmt).noisserpex);
					} else if (((Stmt.Noisserpxe) stmt).noisserpex instanceof Expr.PocketOpenRight) {
						prototype.add(((Stmt.Noisserpxe) stmt).noisserpex);

					}
				}
			}
		}
	}

	public RunTimeTypes getRunTimeTypeForObject() {
		return runtimeTypeForObject;
	}

	public Token getContainerType() {
		return containerType;
	}

	public boolean checkKnotPrototype(Object exprValue2) {
		ArrayList<Expr> isValidprototype = new ArrayList<Expr>();
		for (int i = 0; i < ((Expr.Knot) exprValue2).unGrouped.size(); i++) {
			Stmt stmt = ((Expr.Knot) exprValue2).unGrouped.get(i);
			if (stmt instanceof Stmt.Expression) {
				if (((Stmt.Expression) stmt).expression instanceof Expr.CupOpenLeft) {
					isValidprototype.add(((Stmt.Expression) stmt).expression);
				} else if (((Stmt.Expression) stmt).expression instanceof Expr.CupOpenRight) {
					isValidprototype.add(((Stmt.Expression) stmt).expression);

				} else if (((Stmt.Expression) stmt).expression instanceof Expr.PocketOpenLeft) {
					isValidprototype.add(((Stmt.Expression) stmt).expression);

				} else if (((Stmt.Expression) stmt).expression instanceof Expr.PocketOpenRight) {
					isValidprototype.add(((Stmt.Expression) stmt).expression);

				}
			} else if (stmt instanceof Stmt.Noisserpxe) {
				if (((Stmt.Noisserpxe) stmt).noisserpex instanceof Expr.CupOpenLeft) {
					isValidprototype.add(((Stmt.Noisserpxe) stmt).noisserpex);

				} else if (((Stmt.Noisserpxe) stmt).noisserpex instanceof Expr.CupOpenRight) {

					isValidprototype.add(((Stmt.Noisserpxe) stmt).noisserpex);
				} else if (((Stmt.Noisserpxe) stmt).noisserpex instanceof Expr.PocketOpenLeft) {

					isValidprototype.add(((Stmt.Noisserpxe) stmt).noisserpex);
				} else if (((Stmt.Noisserpxe) stmt).noisserpex instanceof Expr.PocketOpenRight) {
					isValidprototype.add(((Stmt.Noisserpxe) stmt).noisserpex);

				}
			}
		}
		if(this.prototype.size()!=isValidprototype.size())
			return false;
		for (int i = 0; i < this.prototype.size(); i++) {
			if(this.prototype.get(i) != isValidprototype.get(i))
				return false;
		}
		
		
		return true;
	}

	public Object getInitilizer() {
		return initilizer;
	}

}
