package Box.Syntax;

import java.util.ArrayList;

import Box.Token.Token;

public class ArrayTree {

	Node baseNode;
	Node current;

	public class Node {

		ArrayList<Node> nodes = new ArrayList<Node>();
		private Token number;
		private Node parent;

		public Node(Node parent, Token number) {
			this.parent = parent;
			this.number = number;

		}

		public ArrayList<Node> getNodes() {
			return nodes;
		}

		public Token getNumber() {
			return number;
		}

		public Node getParent() {
			return parent;
		}

		public void add(Node n) {
			nodes.add(n);
		}

	}

	public Node getBaseNode() {
		return baseNode;
	}

	public ArrayTree() {
		baseNode = new Node(null, null);
		current = baseNode;
	}

	public void push(Token number) {
		Node n = new Node(current, number);
		current.add(n);
		current = n;

	}

	public void pop() {
		current = current.parent;

	}

}
