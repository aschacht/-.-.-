package Box.Interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Box.Syntax.Expr;
import Box.Syntax.Expr.Literal;
import Box.Syntax.Stmt;
import Box.Syntax.Stmt.Expression;
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
		this.contents = filterPrimarys(boxPrimarys);
		this.methods = methods;
		this.type = type;
		this.enforce = enforce;

		this.typesOfObject = typesOfObject;

	}

	private List<Object> filterPrimarys(ArrayList<Object> boxPrimarys) {
		ArrayList<Object> filtered = new ArrayList<Object>();
		for (Object object : boxPrimarys) {
			
				if (object instanceof BoxInstance) {
					filtered.add(object);

				}
				if(object instanceof Expr && !(object instanceof Expr.Lash)  ) {
					filtered.add(object);
					
				} 
				if (object instanceof Stmt.Expression) {
					if(!(((Stmt.Expression)object).expression instanceof Expr.Lash))
						filtered.add(object);
				}
			

		}

		return filtered;
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
			} else if (expression instanceof Expr.Knot) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Knot) {
					if (typesOfObject.checkKnotPrototype((Expr.Knot) expression)) {
						if (integer == 0 && contents.size() - 1 == -1) {
							this.contents.add(expression);
						} else
							this.contents.set(integer, expression);
					}
				}
			} else if (expression instanceof Expr.Cup) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Cup) {

					if (integer == 0 && contents.size() - 1 == -1) {
						this.contents.add(expression);
					} else
						this.contents.set(integer, expression);

				}
			} else if (expression instanceof Expr.Pocket) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Pocket) {

					if (integer == 0 && contents.size() - 1 == -1) {
						this.contents.add(expression);
					} else
						this.contents.set(integer, expression);

				}
			} else if (expression instanceof Expr.Boxx) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Box) {

					if (integer == 0 && contents.size() - 1 == -1) {
						this.contents.add(expression);
					} else
						this.contents.set(integer, expression);

				}
			} else if (expression instanceof Expr.Literal) {
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
			} else if (expression instanceof Expr.Laretil) {

				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Tni
						|| typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Elbuod
						|| typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Nib
						|| typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Gnirts
						|| typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Naeloob
						|| typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.LLUN) {

					if (integer == 0 && contents.size() - 1 == -1) {
						this.contents.add(expression);
					} else
						this.contents.set(integer, expression);

				}

			} else if (expression instanceof Expr.LiteralChar) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Char) {

					if (integer == 0 && contents.size() - 1 == -1) {
						this.contents.add(expression);
					} else
						this.contents.set(integer, expression);

				}
			} else if (expression instanceof Expr.LaretilChar) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Rahc) {

					if (integer == 0 && contents.size() - 1 == -1) {
						this.contents.add(expression);
					} else
						this.contents.set(integer, expression);

				}
			} else if (expression instanceof Expr.CupOpenRight) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.CupOpenRight) {

					if (integer == 0 && contents.size() - 1 == -1) {
						this.contents.add(expression);
					} else
						this.contents.set(integer, expression);

				}
			} else if (expression instanceof Expr.CupOpenLeft) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.CupOpenLeft) {

					if (integer == 0 && contents.size() - 1 == -1) {
						this.contents.add(expression);
					} else
						this.contents.set(integer, expression);

				}
			} else if (expression instanceof Expr.PocketOpenRight) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.PocketOpenRight) {

					if (integer == 0 && contents.size() - 1 == -1) {
						this.contents.add(expression);
					} else
						this.contents.set(integer, expression);

				}
			} else if (expression instanceof Expr.PocketOpenLeft) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.PocketOpenLeft) {

					if (integer == 0 && contents.size() - 1 == -1) {
						this.contents.add(expression);
					} else
						this.contents.set(integer, expression);

				}
			} else if (expression instanceof Expr.BoxOpenRight) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.BoxOpenRight) {

					if (integer == 0 && contents.size() - 1 == -1) {
						this.contents.add(expression);
					} else
						this.contents.set(integer, expression);

				}
			} else if (expression instanceof Expr.BoxOpenLeft) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.BoxOpenLeft) {

					if (integer == 0 && contents.size() - 1 == -1) {
						this.contents.add(expression);
					} else
						this.contents.set(integer, expression);

				}
			} else if (expression instanceof Expr.Lash) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Lash) {

					if (integer == 0 && contents.size() - 1 == -1) {
						this.contents.add(expression);
					} else
						this.contents.set(integer, expression);

				}
			} else if (expression instanceof Expr.Lid) {
				if (typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Lid) {

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
		Literal literal = new Expr.Literal(data);
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
