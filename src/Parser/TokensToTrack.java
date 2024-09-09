package Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import Box.Token.Token;
import Box.Token.TokenType;

public class TokensToTrack {
	List<ArrayList<Token>> stackForward = new ArrayList<ArrayList<Token>>();
	List<ArrayList<Token>> stackBackward = new ArrayList<ArrayList<Token>>();
	List<Integer> currentStackForward = new ArrayList<Integer>();
	List<Integer> currentStackBackward = new ArrayList<Integer>();
	private boolean parseForward = true;
	private boolean backTracking = false;
	private int backTrackingCount = 0;
	private boolean expressionTracking = true;

	TokensToTrack(ArrayList<Token> baseTokens, int baseCurrent) {
		if (baseTokens.size() > 0) {
			Token eofToken = baseTokens.get(baseTokens.size() - 1);
			ArrayList<Token> newBaseToken = new ArrayList<Token>();

			newBaseToken.add(eofToken);
			for (int i = 0; i < baseTokens.size() - 1; i++) {
				newBaseToken.add(baseTokens.get(i));
			}
			stackForward.add(baseTokens);
			stackBackward.add(newBaseToken);

			currentStackForward.add(baseCurrent);
			currentStackBackward.add(newBaseToken.size() - 1);
		}
	}

	public List<Token> getcurrentTokens() {
		if (isParseForward() == true) {
			return stackForward.get(stackForward.size() - 1);
		} else {
			return	stackBackward.get(stackBackward.size() - 1);
		}
	}

	public boolean removeSubTeokens() {
		if (isParseForward() == true) {
			if (stackForward.size() > 1) {
				stackForward.remove(stackForward.size() - 1);
				currentStackForward.remove(currentStackForward.size() - 1);
				return true;
			}
		} else {
			if (stackBackward.size() > 1) {
				stackBackward.remove(stackBackward.size() - 1);
				currentStackBackward.remove(currentStackBackward.size() - 1);
				return true;
			}
		}
		return false;
	}

	public Token getToken() {
		if (isParseForward() == true) {
			int currentLocal = currentStackForward.get(currentStackForward.size() - 1);
			return (stackForward.get(stackForward.size() - 1)).get(currentLocal);
		} else {
			int currentLocal = currentStackBackward.get(currentStackBackward.size() - 1);
			return (stackBackward.get(stackBackward.size() - 1)).get(currentLocal);
		}
	}

	
	public void resetReverseExpression() {
		currentStackBackward = new ArrayList<>();
		stackBackward = new ArrayList<>();
		
	}

	public void reverseBackwards() {
		Stack<Token> stack = new Stack<>();
		for (Token arrayList :  (stackBackward.get(stackBackward.size() - 1))) {
			stack.push(arrayList);
		}
		
		ArrayList<Token> arr = new ArrayList<>();
		while(!stack.empty()) {
			arr.add(stack.pop());
		}
		
		stackBackward.remove(stackBackward.size() - 1);
		stackBackward.add(arr);
		
	}
	
	public void advance() {
		if (backTracking) {
			backTrackingCount++;
		} else
			backTrackingCount = 0;

		if (isParseForward() == true) {
			int currentLocal = currentStackForward.remove(currentStackForward.size() - 1);
			
			
			
			currentLocal++;
			currentStackForward.add(currentLocal);
			
		} else {
			int currentLocal = currentStackBackward.remove(currentStackBackward.size() - 1);
			currentLocal++;
			currentStackBackward.add(currentLocal);
		}
	}

	public void resetBackTracking() {
		if (isParseForward() == true) {
			
				int currentLocal = currentStackForward.remove(currentStackForward.size() - 1);
				currentLocal = currentLocal - backTrackingCount;
				currentStackForward.add(currentLocal);
				backTrackingCount = 0;
				backTracking = false;
			
		} else {
			
				int currentLocal = currentStackBackward.remove(currentStackBackward.size() - 1);
				currentLocal = currentLocal + backTrackingCount;
				currentStackBackward.add(currentLocal);
				backTrackingCount = 0;
				backTracking = false;
			
		}
	}

	public void turnOnBackTracking() {
		backTracking = true;
	}
	
	public void turnOffBackTracking() {
		backTracking = false;
	}
	
	public int getCurrent() {
		if (isParseForward() == true) {
			if (currentStackForward.size() > 0)
				return currentStackForward.get(currentStackForward.size() - 1);
			else
				return 0;
		} else {
			if (currentStackBackward.size() > 0)
				return currentStackBackward.get(currentStackBackward.size() - 1);
			else
				return 0;
		}
	}

	public int size() {
		if (isParseForward() == true) {
			if (stackForward.size() > 0)
				return (stackForward.get(stackForward.size() - 1)).size();
			else
				return 0;
		} else {
			if (stackBackward.size() > 0)
				return (stackBackward.get(stackBackward.size() - 1)).size();
			else
				return 0;
		}
	}

	public Token getPrevious() {
		if (isParseForward() == true) {
			int currentLocal = currentStackForward.get(currentStackForward.size() - 1);
			return (stackForward.get(stackForward.size() - 1)).get(currentLocal - 1);
		} else {
			int currentLocal = currentStackBackward.get(currentStackBackward.size() - 1);
			return (stackBackward.get(stackBackward.size() - 1)).get(currentLocal + 1);
		}
	}

	public Token getPeekNext() {
		if (isParseForward() == true) {
			int currentLocal = currentStackForward.get(currentStackForward.size() - 1);
			return (stackForward.get(stackForward.size() - 1)).get(currentLocal + 1);
		} else {
			int currentLocal = currentStackBackward.get(currentStackBackward.size() - 1);
			return (stackBackward.get(stackBackward.size() - 1)).get(currentLocal - 1);
		}
	}

	public void parseBackward() {
		setParseForward(false);
	}

	public void parseForward() {
		setParseForward(true);
	}

	public boolean isParseForward() {
		return parseForward;
	}

	public void setParseForward(boolean parseForward) {
		this.parseForward = parseForward;
	}

	public void regress() {
		if (isParseForward() == true) {
			int currentLocal = currentStackForward.get(currentStackForward.size() - 1);
			currentLocal--;
			currentStackForward.remove(currentStackForward.size() - 1);
			currentStackForward.add(currentLocal);
		} else {
			int currentLocal = currentStackBackward.get(currentStackBackward.size() - 1);
			currentLocal++;
			currentStackBackward.remove(currentStackBackward.size() - 1);
			currentStackBackward.add(currentLocal);
		}
	}

	public int currentIndex() {
		if (isParseForward() == true) {
			int currentLocal = currentStackForward.get(currentStackForward.size() - 1);
			return currentLocal;
		} else {
			int currentLocal= currentStackBackward.get(currentStackBackward.size() - 1);
			return currentLocal;
		}
	}

	public Token getToken(int index) {
		if (isParseForward() == true) {
			int currentLocal = currentStackForward.get(currentStackForward.size() - 1);
			return (stackForward.get(stackForward.size() - 1)).get(currentLocal + index);
		} else {
			int currentLocal = currentStackBackward.get(currentStackBackward.size() - 1);
			return (stackBackward.get(stackBackward.size() - 1)).get(currentLocal + index);
		}
	}

	public String getLexemeForRange(int start, int end) {
		if (isParseForward() == true) {
			ArrayList<Token> arrayList = stackForward.get(stackForward.size() - 1);
			String lexeme = "";
			for (int i = start; i <= end; i++) {
				lexeme += arrayList.get(i).lexeme;
			}
			return lexeme;
		} else {
			return "";
		}

	}

	public void setTrackerToIndex(int start) {
		if (isParseForward() == true) {
			currentStackForward.remove(currentStackForward.size() - 1);
			currentStackForward.add(start);
		} else {
			currentStackBackward.remove(currentStackBackward.size() - 1);
			currentStackBackward.add(start);
		}

	}

	public void turnOnExpressionTracking() {
		this.expressionTracking = true;

	}

	public void turnOffExpressionTracking() {
		this.expressionTracking = true;

	}

	public boolean isExpressionTracking() {
		return expressionTracking;
	}

}