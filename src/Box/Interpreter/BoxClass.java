package Box.Interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Box.Parser.Stmt;
import Box.Parser.Stmt.Expression;
import Box.Syntax.ExprOLD;
import Box.Syntax.ExprOLD.Literal;
import Box.Token.Token;
import Box.Token.TokenType;

public class BoxClass extends BoxCallable {

	public String name;
	private Map<String, BoxFunction> methods;
	private BoxClass superclass;

	private boolean enforce;

	private TypesOfObject typesOfObject;

	public BoxClass(String name, BoxClass superclass, ArrayList<Object> boxPrimarys, Map<String, BoxFunction> methods,
			TokenType type, boolean enforce, TypesOfObject typesOfObject) {
		this.name = name;
		this.superclass = superclass;
		this.contents = boxPrimarys;
		this.methods = methods;
		this.type = type;
		this.enforce = enforce;

		this.typesOfObject = typesOfObject;

	}


	@Override
	public String toString() {
		String contents = "";
		StringBuilder sb = new StringBuilder(name);
		if (type == TokenType.CUPCONTAINER)
			contents = name + "{ ";
		if (type == TokenType.POCKETCONTAINER)
			contents = name + "( ";
		for (Object object : this.contents) {
			contents += object.toString() + " ";
		}
		if (type == TokenType.CUPCONTAINER)
			contents += " }" + sb.reverse().toString();
		if (type == TokenType.POCKETCONTAINER)
			contents += " )" + sb.reverse().toString();
		return contents;
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		BoxInstance instance = new BoxInstance(this);
		BoxFunction initilizer = findMethod(name);
		if (initilizer != null) {
			initilizer.bind(instance).call(interpreter, arguments);
		}
		return instance;

	}

	@Override
	public int arity() {
		BoxFunction initilizer = findMethod("init");
		if (initilizer == null)
			return 0;
		return initilizer.arity();
	}

	public BoxFunction findMethod(String lexeme) {
		if (methods != null) {
			if (methods.containsKey(lexeme)) {
				return methods.get(lexeme);
			}
		}
		if (superclass != null)
			return superclass.findMethod(lexeme);

		return null;
	}

	public void setContentsAt(Integer integer, Object expression) {
		if (enforce) {
			if (expression instanceof BoxInstance) {

				if (((BoxInstance) expression).boxClass instanceof BoxClass) {
					if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes
							.getTypeBasedOfTokenType(((BoxClass) ((BoxInstance) expression).boxClass).type)) {

						if (integer == 0 && contents.size() - 1 == -1) {
							this.contents.add(expression);
						} else
							this.contents.set(integer, expression);

					}
				} else if (((BoxInstance) expression).boxClass instanceof BoxContainerClass) {
					if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes
							.getTypeBasedOfTokenType(((BoxContainerClass) ((BoxInstance) expression).boxClass).type)) {

						if (integer == 0 && contents.size() - 1 == -1) {
							this.contents.add(expression);
						} else
							this.contents.set(integer, expression);

					}
				}
			} else if (expression instanceof ExprOLD.Knot) {
				
			} else if (expression instanceof ExprOLD.Cup) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Cup) {

					if (integer == 0 && contents.size() - 1 == -1) {
						this.contents.add(expression);
					} else
						this.contents.set(integer, expression);

				}
			} else if (expression instanceof ExprOLD.Pocket) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Pocket) {

					if (integer == 0 && contents.size() - 1 == -1) {
						this.contents.add(expression);
					} else
						this.contents.set(integer, expression);

				}
			} else if (expression instanceof ExprOLD.Box) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Box) {

					if (integer == 0 && contents.size() - 1 == -1) {
						this.contents.add(expression);
					} else
						this.contents.set(integer, expression);

				}
			} else if (expression instanceof ExprOLD.Literal) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Int
						|| typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Double
						|| typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Bin
						|| typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.String
						|| typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Boolean
						|| typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.NULL) {

					if (integer == 0 && contents.size() - 1 == -1) {
						this.contents.add(expression);
					} else
						this.contents.set(integer, expression);

				}
			} else if (expression instanceof ExprOLD.LiteralChar) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Char) {

					if (integer == 0 && contents.size() - 1 == -1) {
						this.contents.add(expression);
					} else
						this.contents.set(integer, expression);

				}
			}

		} else {
			if (integer == 0 && contents.size() - 1 == -1) {
				this.contents.add(expression);
			} else
				this.contents.set(integer, expression);

		}
	}

	public Object get(String lexeme) {
		for (Object expr : contents) {
			if (expr instanceof BoxInstance) {
				if ((((BoxInstance) expr).boxClass) instanceof BoxClass) {
					if (((BoxClass) ((BoxInstance) expr).boxClass).name.equalsIgnoreCase(lexeme)) {
						return expr;
					}
				} else if ((((BoxInstance) expr).boxClass) instanceof BoxContainerClass) {
					if (((BoxContainerClass) ((BoxInstance) expr).boxClass).name.equalsIgnoreCase(lexeme)) {
						return expr;
					}
				} else if ((((BoxInstance) expr).boxClass) instanceof BoxKnotClass) {
					if (((BoxKnotClass) ((BoxInstance) expr).boxClass).name.equalsIgnoreCase(lexeme)) {
						return expr;
					}
				}
			}
		}
		return null;
	}

	public void setContentsAtEnd(String data) {
		Literal literal = new ExprOLD.Literal(data);
		contents.add(literal);
	}

	public boolean compairPrimarys(BoxClass boxClass) {
		boolean contains = false;
		for (Object object : boxClass.contents) {
			contains = contents.contains(object);
			if (!contains)
				break;
		}
		return contains;
	}

	public boolean compairPrimarys(BoxContainerClass boxContainerClass) {
		boolean contains = false;
		for (Object object : boxContainerClass.contents) {
			contains = contents.contains(object);
			if (!contains)
				break;
		}
		return contains;
	}

	public int size() {

		return contents.size();
	}

}
