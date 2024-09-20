package Box.Interpreter;

import java.util.ArrayList;
import java.util.List;

import Box.Token.Token;
import Box.Token.TokenType;
import Parser.Declaration;
import Parser.Expr;
import Parser.Expr.Literal;
import Parser.Stmt;
import Parser.Declaration.StmtDecl;

public class BoxInstance extends Instance {

	List<Object> body;
	private Interpreter interpreter;

	public BoxInstance(BoxCallable boxClass, List<Object> body, Expr expr, Interpreter interpreter) {
		super(boxClass, expr);
		// TODO Auto-generated constructor stub
		this.body = body;
		this.interpreter = interpreter;
		evaluateBody();
	}

	private void evaluateBody() {
		ArrayList<Object> temp = new ArrayList<>();
		for (int i = 0; i < body.size(); i++) {
			Object object = body.get(i);
			if (!isControl(object)) {
				if (isExpression(object)) {
					temp.add(interpreter.execute((Expr) object));
				} else
					temp.add(object);

			} else
				temp.add(object);

		}
		body = temp;
	}

	private boolean isExpression(Object object) {
		if (object instanceof Expr)
			return true;
		return false;
	}

	private boolean isControl(Object object) {
		if (object instanceof Stmt.Expression) {
			Expr expr2 = ((Stmt.Expression) object).expression;
			if (expr2 instanceof Expr.PocketOpen || expr2 instanceof Expr.PocketClosed || expr2 instanceof Expr.CupOpen
					|| expr2 instanceof Expr.CupClosed || expr2 instanceof Expr.BoxOpen
					|| expr2 instanceof Expr.BoxClosed) {

				return true;
			}
		} else if (object instanceof StmtDecl) {
			Stmt state = ((StmtDecl) object).statement;
			if (state instanceof Stmt.Expression) {
				Expr expr2 = ((Stmt.Expression) state).expression;
				if (expr2 instanceof Expr.PocketOpen || expr2 instanceof Expr.PocketClosed
						|| expr2 instanceof Expr.CupOpen || expr2 instanceof Expr.CupClosed
						|| expr2 instanceof Expr.BoxOpen || expr2 instanceof Expr.BoxClosed) {

					return true;
				}

			}
		}

		return false;
	}

	@Override
	public String toString() {
		String str = "";
		for (Object object : body) {
			str += interpreter.evaluate((Expr) object) + " ";
		}
		return str;
	}

	public void add(Token operator, Object toadd) {

		if (toadd instanceof Expr.Literal || toadd instanceof Expr.Cup || toadd instanceof Expr.Pocket
				|| toadd instanceof Expr.Box || toadd instanceof Expr.Knot || toadd instanceof Expr.Tonk
				|| toadd instanceof Expr.Variable || toadd instanceof Expr.LiteralChar) {
			body.add(body.size() - 1, toadd);
			evaluateBody();
		} else
			throw new RuntimeError(operator, "not of acceptable type");
	}

	public Object remove(Token operator, Object value) {

		if (value instanceof Integer) {
			Integer index = ((Integer) value) + 1;
			if (index >= 1 && index <= bodySizeExclude() - 1) {
				return body.remove((int) index);
			} else
				throw new RuntimeError(operator, "index out of bounds");
		} else
			throw new RuntimeError(operator, "must pass an Integer to remove");

	}

	private int bodySizeExclude() {
		int count = 0;
		for (Object object : body) {
			if (object instanceof Stmt.Expression) {
				Expr expr = ((Stmt.Expression) object).expression;
				if (!(expr instanceof Expr.PocketOpen) && !(expr instanceof Expr.PocketClosed)
						&& !(expr instanceof Expr.CupOpen) && !(expr instanceof Expr.CupClosed)) {
					count++;
				}
			} else
				count++;
		}
		return count;
	}

	public Object getat(Token operator, Object value) {
		if (value instanceof Integer) {
			Integer index = ((Integer) value) + 1;
			if (index >= 1 && index <= bodySizeExclude() - 1) {
				return body.get((int) index);
			} else
				throw new RuntimeError(operator, "index out of bounds");
		} else
			throw new RuntimeError(operator, "must pass an Integer to remove");

	}

	public Object size(Token operator) {
		return bodySizeExclude();
	}

	public Object clear(Token operator) {
		List<Object> cleared = new ArrayList<>();
		cleared.add(body.get(0));
		cleared.add(body.get(body.size() - 1));
		body = cleared;
		return null;
	}

	public Object empty(Token operator) {

		return bodySizeExclude() == 0 ? true : false;
	}

	private boolean isIndexControl(int index) {
		Object object = body.get(index);
		if (object instanceof Stmt.Expression) {
			Expr expr = ((Stmt.Expression) object).expression;
			if (!(expr instanceof Expr.PocketOpen) && !(expr instanceof Expr.PocketClosed)
					&& !(expr instanceof Expr.CupOpen) && !(expr instanceof Expr.CupClosed)) {
				return false;
			} else
				return true;
		} else {
			return false;
		}
	}

	public Object pop(Token operator) {
		int index = 1;
		while (isIndexControl(index)) {
			index++;
		}
		if (index >= bodySizeExclude())
			return null;

		return body.remove(index);
	}

	public void push(Token operator, Expr toadd) {
		body.add(1, toadd);
		evaluateBody();
	}

	public void setat(Literal index, Expr toset) {
		if (index.value instanceof Integer) {
			Integer i = ((Integer) (index.value)) + 1;
			if (i >= 1 && i <= bodySizeExclude() - 1) {
				body.add((int) i, toset);
				body.remove(i + 1);
				evaluateBody();
			} else
				throw new RuntimeError(new Token(TokenType.SETAT, "", null, null, null, -1, -1, -1, -1),
						"index out of bounds");
		} else
			throw new RuntimeError(new Token(TokenType.SETAT, "", null, null, null, -1, -1, -1, -1),
					"invalid parameters to setat");

	}

	public Object sub(Literal start, Literal end) {
		if (start.value instanceof Integer && end.value instanceof Integer) {
			Integer i = ((Integer) (start.value)) + 1;
			Integer j = ((Integer) (end.value)) + 1;
			if (i >= 1 && i <= bodySizeExclude() - 1) {
				if (j >= i && j <= bodySizeExclude() - 1) {
					return body.subList(i, j);
				} else
					throw new RuntimeError(new Token(TokenType.SUB, "", null, null, null, -1, -1, -1, -1),
							"cross bounds mismatch in the call to sub ");
			} else
				throw new RuntimeError(new Token(TokenType.SUB, "", null, null, null, -1, -1, -1, -1),
						"index out of bounds in call to sub");
		} else
			throw new RuntimeError(new Token(TokenType.SUB, "", null, null, null, -1, -1, -1, -1),
					"invalid parameters to sub");

	}

	public boolean contains(Declaration contents) {
		if (contents instanceof Stmt.Expression) {
			Object value = ((Stmt.Expression) contents).expression;
			if (value instanceof Literal) {
				Object value2 = ((Literal) value).value;
				for (Object object : body) {

					if (value2.equals(object))
						return true;

				}
				return false;
			} else {
				Object execute = interpreter.execute(contents);
				return body.contains(execute);
			}
		} else {
			for (Object obj : body) {

				if (contents.equals(obj))
					return true;

			}
			return false;
		}

	}

}
