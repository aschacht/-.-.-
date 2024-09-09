package Box.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import Box.Grouper.ContainerIndexes;
import Box.Token.Token;
import Box.Token.TokenType;

public class Util {
	
	
	
	

	private List<Token> tokens;

	public ArrayList<ContainerIndexes> findContainers(int start,int finish) {

		ArrayList<ContainerIndexes> contIndexes = checkIfContainsKnot(getTokens(), start, finish);
		for (int i = 0; i < contIndexes.size(); i++) {
			ArrayList<ContainerIndexes> exclude = new ArrayList<>();
			buildExclude(contIndexes, contIndexes.get(i), exclude);
			ArrayList<ContainerIndexes> checkIfContainsKnotExclude = checkIfContainsKnotExclude(getTokens(),
					contIndexes.get(i).getStart(), contIndexes.get(i).getEnd(), exclude);

			for (ContainerIndexes containerIndexes : contIndexes) {
				for (ContainerIndexes containerIndexes0 : checkIfContainsKnotExclude) {
					if (containerIndexes.getStart() == containerIndexes0.getStart()
							&& containerIndexes.getEnd() == containerIndexes0.getEnd()) {
						containerIndexes.setKnot(containerIndexes0.isKnot());
					}
				}
			}
		}
		return contIndexes;
	}
	
	
	public ArrayList<ContainerIndexes> findBoxes(ArrayList<Token> tok) {

		ArrayList<ContainerIndexes> contIndexes = checkIfContainsKnot(tokens, 0, tok.size());
		for (int i = 0; i < contIndexes.size(); i++) {
			ArrayList<ContainerIndexes> exclude = new ArrayList<>();
			buildExclude(contIndexes, contIndexes.get(i), exclude);
			ArrayList<ContainerIndexes> checkIfContainsKnotExclude = checkIfContainsKnotExclude(tokens,
					contIndexes.get(i).getStart(), contIndexes.get(i).getEnd(), exclude);

			for (ContainerIndexes containerIndexes : contIndexes) {
				for (ContainerIndexes containerIndexes0 : checkIfContainsKnotExclude) {
					if (containerIndexes.getStart() == containerIndexes0.getStart()
							&& containerIndexes.getEnd() == containerIndexes0.getEnd()) {
						containerIndexes.setKnot(containerIndexes0.isKnot());
					}
				}
			}
		}
		return contIndexes;
	}
	
	
	
	
	private void buildExclude(ArrayList<ContainerIndexes> contIndexes, ContainerIndexes containerIndexes,
			ArrayList<ContainerIndexes> exclude) {

		for (ContainerIndexes containerIndexes1 : contIndexes) {
			if (containerIndexes.getStart() < containerIndexes1.getStart()
					&& containerIndexes.getEnd() > containerIndexes1.getEnd()) {
				exclude.add(containerIndexes1);
			}
		}
	}
	
	private ArrayList<ContainerIndexes> checkIfContainsKnot(List<Token> tokens2, int sta, int end) {
		boolean start = true;
		int startIndex = sta;
		int endIndex = startIndex;
		ArrayList<ContainerIndexes> contIndexes = new ArrayList<>();
		Stack<TokenType> stack = new Stack<>();
		Stack<TokenType> paren = new Stack<>();
		Stack<TokenType> brace = new Stack<>();
		int count = 0;
		boolean isKnotOrContainsKnot = false;

		while (count < end) {
			int countOpenClose = 0;
			for (int i = startIndex; i < end; i++) {
				if (tokens2.get(i).type == TokenType.OPENPAREN) {
					if (start) {
						start = false;
						startIndex = i;
					}
					stack.push(tokens2.get(i).type);
					paren.push(tokens2.get(i).type);
					countOpenClose++;
				} else if (tokens2.get(i).type == TokenType.OPENBRACE) {
					if (start) {
						start = false;
						startIndex = i;
					}
					stack.push(tokens2.get(i).type);
					brace.push(tokens2.get(i).type);
					countOpenClose++;
				} else if (tokens2.get(i).type == TokenType.CLOSEDBRACE) {
					endIndex = i;
					if (stack.size() > 0) {
						if (stack.peek() == TokenType.OPENBRACE)
							stack.pop();
						else
							isKnotOrContainsKnot = true;
					}
					if (brace.size() > 0) {
						brace.pop();
					}

					countOpenClose++;

					if (brace.size() == 0 && paren.size() == 0) {
						break;
					}
				} else if (tokens2.get(i).type == TokenType.CLOSEDPAREN) {
					endIndex = i;
					if (stack.size() > 0) {
						if (stack.peek() == TokenType.OPENPAREN)
							stack.pop();
						else
							isKnotOrContainsKnot = true;
					}
					if (paren.size() > 0) {
						paren.pop();
					}
					countOpenClose++;
					if (brace.size() == 0 && paren.size() == 0) {
						break;
					}
				}
				count++;
			}
			if (countOpenClose >= 2 && countOpenClose % 2 == 0 && isKnotOrContainsKnot && paren.size() == 0
					&& brace.size() == 0 && stack.size() > 0) {
				contIndexes.add(new ContainerIndexes(startIndex, endIndex, isKnotOrContainsKnot));
			} else if (countOpenClose >= 2 && !isKnotOrContainsKnot && paren.size() == 0 && brace.size() == 0) {
				contIndexes.add(new ContainerIndexes(startIndex, endIndex, isKnotOrContainsKnot));
			}
			paren.clear();
			brace.clear();
			stack.clear();
			count = startIndex;
			startIndex++;
			start = true;
			isKnotOrContainsKnot = false;
		}
		return contIndexes;
	}

	
	private ArrayList<ContainerIndexes> checkIfContainsKnotExclude(List<Token> tokens2, int sta, int end,
			ArrayList<ContainerIndexes> exclude) {
		boolean start = true;
		int startIndex = sta;
		int endIndex = startIndex;
		ArrayList<ContainerIndexes> contIndexes = new ArrayList<>();
		Stack<TokenType> stack = new Stack<>();
		Stack<TokenType> paren = new Stack<>();
		Stack<TokenType> brace = new Stack<>();
		int count = 0;
		boolean isKnotOrContainsKnot = false;

		while (count < end) {
			int countOpenClose = 0;
			for (int i = startIndex; i <= end; i++) {

				if (tokens2.get(i).type == TokenType.OPENPAREN) {
					boolean dontskip = true;
					if (exclude != null) {
						for (ContainerIndexes containerIndexes : exclude) {
							if (containerIndexes.getStart() <= i && containerIndexes.getEnd() >= i) {
								dontskip = false;
								break;
							}
						}
					}
					if (dontskip) {
						if (start) {
							start = false;
							startIndex = i;
						}
						stack.push(tokens2.get(i).type);
						paren.push(tokens2.get(i).type);
						countOpenClose++;
					}

				} else if (tokens2.get(i).type == TokenType.OPENBRACE) {
					boolean dontskip = true;
					if (exclude != null) {
						for (ContainerIndexes containerIndexes : exclude) {
							if (containerIndexes.getStart() <= i && containerIndexes.getEnd() >= i) {
								dontskip = false;
								break;
							}
						}
					}
					if (dontskip) {
						if (start) {
							start = false;
							startIndex = i;
						}
						stack.push(tokens2.get(i).type);
						brace.push(tokens2.get(i).type);
						countOpenClose++;
					}

				} else if (tokens2.get(i).type == TokenType.CLOSEDBRACE) {
					boolean dontskip = true;
					if (exclude != null) {
						for (ContainerIndexes containerIndexes : exclude) {
							if (containerIndexes.getStart() <= i && containerIndexes.getEnd() >= i) {
								dontskip = false;
								break;
							}
						}
					}
					if (dontskip) {
						endIndex = i;
						if (stack.size() > 0) {
							if (stack.peek() == TokenType.OPENBRACE)
								stack.pop();
							else
								isKnotOrContainsKnot = true;
						}
						if (brace.size() > 0) {
							brace.pop();
						}

						countOpenClose++;
					}
					if (brace.size() == 0 && paren.size() == 0) {
						break;
					}
				} else if (tokens2.get(i).type == TokenType.CLOSEDPAREN) {
					boolean dontskip = true;
					if (exclude != null) {
						for (ContainerIndexes containerIndexes : exclude) {
							if (containerIndexes.getStart() <= i && containerIndexes.getEnd() >= i) {
								dontskip = false;
								break;
							}
						}
					}
					if (dontskip) {
						endIndex = i;
						if (stack.size() > 0) {
							if (stack.peek() == TokenType.OPENPAREN)
								stack.pop();
							else
								isKnotOrContainsKnot = true;
						}
						if (paren.size() > 0) {
							paren.pop();
						}
						countOpenClose++;
						if (brace.size() == 0 && paren.size() == 0) {
							break;
						}
					}
				}
				count++;
			}
			if (countOpenClose >= 4 && countOpenClose % 2 == 0 && isKnotOrContainsKnot && paren.size() == 0
					&& brace.size() == 0 && stack.size() > 0) {
				contIndexes.add(new ContainerIndexes(startIndex, endIndex, isKnotOrContainsKnot));
			} else if (countOpenClose == 2 && !isKnotOrContainsKnot && paren.size() == 0 && brace.size() == 0) {
				contIndexes.add(new ContainerIndexes(startIndex, endIndex, isKnotOrContainsKnot));

			}
			paren.clear();
			brace.clear();
			stack.clear();
			count = startIndex;
			startIndex++;
			start = true;
			isKnotOrContainsKnot = false;
		}
		return contIndexes;
	}

	
	
	
	
	public List<Token> getTokens() {
		return tokens;
	}

	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}

	
}
