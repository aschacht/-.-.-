package Box.Interpreter;

import java.util.ArrayList;
import java.util.List;

import Box.Interpreter.KnotRunner.Conditions;
import Parser.Expr;
import Parser.Stmt;

public class KnotRunner {

	public class Conditions {
		private List<Condition> conds = new ArrayList<>();

		public void add(Condition conditions) {
			conds.add(conditions);

		}

		public boolean checkIfStartIncluded(int start) {
			for (Condition condition : conds) {
				if (condition.start == start)
					return true;
			}
			return false;
		}

		public boolean checkIfEndIncluded(int end) {
			for (Condition condition : conds) {
				if (condition.end == end)
					return true;
			}
			return false;
		}

		public int size() {
			return conds.size();
		}

		public Condition get(int i) {
			return conds.get(i);
		}

		public boolean isIncluded(int i, Condition object) {

			if (conds.get(i).start <= object.start && conds.get(i).end >= object.end)
				return true;

			return false;
		}

		public boolean IsStart(int condsIndex, int start) {
			if (conds.get(condsIndex).start == start)
				return true;
			return false;
		}

		@Override
		public String toString() {
			String str = "";
			for (Condition condition : conds) {
				str += condition.toString() + "\n";
			}
			return str;
		}

		public boolean notIncluded(Condition c) {
			for (Condition condition : conds) {
				if (condition.start == c.start && condition.end == c.end)
					return false;

			}
			return true;
		}

		public int getEnd(int start) {
			for (Condition condition : conds) {
				if (condition.start == start)
					return condition.end;

			}
			return -1;
		}

		public int getStartForMatchingIdent(String lexeme) {
			for (Condition condition : conds) {
				if (condition.startIdent.equals(lexeme))
					return condition.start;
			}
			return -1;
		}

		public int getEndForMatchingIdent(String lexeme) {
			for (Condition condition : conds) {
				if (condition.startIdent.equals(lexeme))
					return condition.end;
			}
			return -1;
		}

	}

	public class Condition {
		int start;
		int end;
		String startIdent;

		public Condition(String startIdent, int start, int end) {
			this.startIdent = startIdent;
			this.start = start;
			this.end = end;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "ident " + startIdent + " start " + start + " end " + end;
		}
	}

	private List<Stmt> expression;
	Conditions condForward = new Conditions();
	Conditions condBackward = new Conditions();
	private Interpreter interp;

	public KnotRunner(List<Stmt> expression, Interpreter interp) {
		this.expression = expression;
		this.interp = interp;
		findConditions();
		System.out.println();
		System.out.println(condForward.toString());
		System.out.println();
		System.out.println(condBackward.toString());
		int count = 0;
		if (!interp.isForward())
			count = expression.size() - 1;
		
		
		
		boolean firstForward = false;
		boolean firstBack = true;
		if(interp.isForward()) {
			firstForward = false;
			firstBack = true;
		}else {
			firstForward = true;
			firstBack = false;
		}
		while (true) {
			if (expression.get(count) instanceof Stmt.Expression) {
				if (((Stmt.Expression) expression.get(count)).expression instanceof Expr.CupClosed) {
					String lexeme = ((Expr.CupClosed) ((Stmt.Expression) expression
							.get(count)).expression).ctrl.reifitnediToken.lexeme;
					lexeme = lexeme.replace("}", "");
					String[] split = lexeme.split("_");
					if (split.length > 1) {
						lexeme = split[1];
					}
					lexeme = reverse(lexeme);
					if (firstBack) {
						if (checkConditionsForward(count, lexeme)) {
							interp.setForward(!interp.isForward());
						}
					}else {
						firstBack=true;
					}
				} else if (((Stmt.Expression) expression.get(count)).expression instanceof Expr.CupOpen) {
					String lexeme = ((Expr.CupOpen) ((Stmt.Expression) expression
							.get(count)).expression).ctrl.identifierToken.lexeme;
					lexeme = lexeme.replace("{", "");
					String[] split = lexeme.split("_");
					lexeme = split[0];
					lexeme = reverse(lexeme);
					if (firstForward) {
						if (checkConditionsBackward(count, lexeme)) {
							interp.setForward(!interp.isForward());
						}
					} else {
				
						firstForward = true;
					}
				} else {
					interp.execute(expression.get(count));
				}
			} else {
				interp.execute(expression.get(count));
			}
			if (interp.isForward())
				count++;
			else
				count--;
			if (count >= expression.size())
				break;
			if (count < 0)
				break;
		}

	}

	private Boolean checkConditionsForward(int count, String lexeme) {
		Boolean evaluate = true;

		int start = condForward.getStartForMatchingIdent(lexeme);
		int end = condForward.getEndForMatchingIdent(lexeme);
		if (start != -1 && end != -1) {
			evaluate = end - start > 1 ? (Boolean) interp.evaluate(expression.get(start + 1)) : true;
		}
		return evaluate;
	}

	private Boolean checkConditionsBackward(int count, String lexeme) {
		Boolean evaluate = true;

		int start = condBackward.getStartForMatchingIdent(lexeme);
		int end = condBackward.getEndForMatchingIdent(lexeme);
		if (start != -1 && end != -1) {
			evaluate = end - start > 1 ? (Boolean) interp.evaluate(expression.get(start - 1)) : true;
		}
		return evaluate;
	}

	private int moveConditions(int count) {
		for (int i = count + 1; i < condForward.getEnd(count); i++) {
			count = i;
		}
		return count;
	}

	private void findConditions() {

		findSectionsForward();
		findSectionsBackward();

	}

	private String reverse(String str) {
		String nstr = "";
		char ch;
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i); // extracts each character
			nstr = ch + nstr; // adds each character in front of the existing string
		}
		return nstr;
	}

	private void findSectionsForward() {

		for (int i = 0; i < expression.size(); i++) {
			if (expression.get(i) instanceof Stmt.Expression) {
				if (((Stmt.Expression) expression.get(i)).expression instanceof Expr.CupOpen) {

					int count = i + 1;

					count = findNextPocketOpen(count);
					String lexeme = ((Expr.CupOpen) ((Stmt.Expression) expression
							.get(i)).expression).ctrl.identifierToken.lexeme;
					lexeme = lexeme.replace("{", "");
					String[] split = lexeme.split("_");

					lexeme = split[0];

					if (count <= expression.size() - 1) {
						if (((Stmt.Expression) expression.get(count)).expression instanceof Expr.CupOpen) {
							condForward.add(new Condition(lexeme, i, count));

						} else {
							count = backTrackToLastClosedPocket(i, count - 1);
							if (((Stmt.Expression) expression.get(count)).expression instanceof Expr.PocketClosed) {
								condForward.add(new Condition(lexeme, i, count));

							}
						}
					} else {
						count = backTrackToLastClosedPocket(i, count - 1);
						if (((Stmt.Expression) expression.get(count)).expression instanceof Expr.PocketClosed) {
							condForward.add(new Condition(lexeme, i, count));

						}

					}
				}
			}
		}
	}

	private void findSectionsBackward() {

		for (int i = expression.size() - 1; i > 0; i--) {
			if (expression.get(i) instanceof Stmt.Expression) {
				if (((Stmt.Expression) expression.get(i)).expression instanceof Expr.CupClosed) {
					int count = i - 1;
					count = findNextPocketOpen(count);
					String lexeme = ((Expr.CupClosed) ((Stmt.Expression) expression
							.get(i)).expression).ctrl.reifitnediToken.lexeme;
					lexeme = lexeme.replace("}", "");
					if (count >= 0 && count < expression.size()) {
						if (((Stmt.Expression) expression.get(count)).expression instanceof Expr.CupClosed) {
							condBackward.add(new Condition(lexeme, count, i));

						} else {
							count = backTrackToLastOpenPocket(i, count - 1);
							if (((Stmt.Expression) expression.get(count)).expression instanceof Expr.PocketOpen) {
								condBackward.add(new Condition(lexeme, count, i));

							}
						}
					} else {
						count = backTrackToLastOpenPocket(i, count - 1);
						if (((Stmt.Expression) expression.get(count)).expression instanceof Expr.PocketOpen) {
							condBackward.add(new Condition(lexeme, count, i));

						}
					}
				}
			}
		}
	}

	private int backTrackToLastClosedPocket(int stop, int count) {
		for (int i = count; i > stop; i--) {
			if (expression.get(i) instanceof Stmt.Expression) {
				if (((Stmt.Expression) expression.get(i)).expression instanceof Expr.PocketClosed)
					return i;
			}
		}
		return count;
	}

	private int backTrackToLastOpenPocket(int stop, int count) {
		for (int i = count; i < stop; i++) {
			if (expression.get(i) instanceof Stmt.Expression) {
				if (((Stmt.Expression) expression.get(i)).expression instanceof Expr.PocketOpen)
					return i;
			}
		}
		return count;
	}

	private int findNextPocketOpen(int count) {
		while (count <= expression.size() - 1 && !pocketOpen(count)) {
			count++;
		}
		return count;
	}

	private int findNextPocketClosed(int count) {
		while (count > 0 && !pocketClosed(count)) {
			count--;
		}
		return count;
	}

	private boolean pocketClosed(int count) {
		if (expression.get(count) instanceof Stmt.Expression) {
			return ((Stmt.Expression) expression.get(count)).expression instanceof Expr.PocketClosed
					|| ((Stmt.Expression) expression.get(count)).expression instanceof Expr.CupClosed;
		}
		return false;
	}

	private boolean pocketOpen(int count) {
		if (expression.get(count) instanceof Stmt.Expression) {
			return ((Stmt.Expression) expression.get(count)).expression instanceof Expr.PocketOpen
					|| ((Stmt.Expression) expression.get(count)).expression instanceof Expr.CupOpen;
		}
		return false;
	}

}
