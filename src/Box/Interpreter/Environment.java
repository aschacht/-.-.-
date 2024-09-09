package Box.Interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Box.Box.Box;
import Box.Token.Token;
import Box.Token.TokenType;
import Parser.Expr;
import Parser.Stmt;

public class Environment {
	public Environment enclosing;
	private final Map<String, Object> values = new HashMap<>();
	private final Map<String, TypesOfObject> types = new HashMap<>();

	public Environment(Environment enclosing) {
		this.enclosing = enclosing;

	}

	public Environment() {
		this.enclosing = null;

	}

	public void define(String name, Token type, Object value) {
		values.put(name, value);
		types.put(name, new TypesOfObject(type, RunTimeTypes.getTypeBasedOfToken(type), null));
	}

	public void define(String name, Token type, Object value, Object initilizer,Interpreter interpreter) {
		values.put(name, initilizer);
	
		types.put(name, new TypesOfObject(type, RunTimeTypes.getObjectType(initilizer, initilizer, interpreter) , initilizer));
	}

	public void define(String name, Boolean enforce, Token type, Object initilizer, Object value,
			Interpreter interpreter) {
		ArrayList<Object> boxPrimarys = new ArrayList<Object>();
		Object boxInstance = null;
		
	}

	public Object get(Token name, boolean fromCall) {

		if (fromCall) {
			if (values.containsKey(name.lexeme + "Class_Definition")) {
				return values.get(name.lexeme + "Class_Definition");
			}
		} else {
			if (values.containsKey(name.lexeme)) {
				return values.get(name.lexeme);
			}
		}

		if (enclosing != null)
			return enclosing.get(name, fromCall);

		return null;
	}

	public void assign(Token name, Object exprValue, Object value, Interpreter interpreter) {
		if (values.containsKey(name.lexeme)) {
			if (types.get(name.lexeme).getRunTimeTypeForObject() == RunTimeTypes.Any) {
				values.put(name.lexeme, value);
			} else if (RunTimeTypes.getObjectType(exprValue, value, interpreter) == types.get(name.lexeme)
					.getRunTimeTypeForObject()) {
				if (types.get(name.lexeme).getRunTimeTypeForObject() == RunTimeTypes.Knot) {
					
				} else {
					Object objetToset = values.get(name.lexeme);
					if (objetToset instanceof Instance) {
						if (((Instance) objetToset).boxClass instanceof BoxClass) {

							Object lookUpVariable = null;
							if (exprValue instanceof Expr.Variable) {
								lookUpVariable = interpreter.lookUpVariable(((Expr.Variable) exprValue).name,
										((Expr.Variable) exprValue));
							} else if (exprValue instanceof Expr.Cup) {
								lookUpVariable = interpreter.lookUpVariable(((Expr.Cup) exprValue).identifier,
										((Expr.Cup) exprValue));
							} else if (exprValue instanceof Expr.Pocket) {
								lookUpVariable = interpreter.lookUpVariable(((Expr.Pocket) exprValue).identifier,
										((Expr.Pocket) exprValue));
							} else if (exprValue instanceof Expr.Box) {
								lookUpVariable = interpreter.lookUpVariable(((Expr.Box) exprValue).identifier,
										((Expr.Box) exprValue));
							}
							
						} else if (((Instance) objetToset).boxClass instanceof BoxContainerClass) {
							Object lookUpVariable = null;
							if (exprValue instanceof Expr.Variable) {
								lookUpVariable = interpreter.lookUpVariable(((Expr.Variable) exprValue).name,
										((Expr.Variable) exprValue));
							}  else if (exprValue instanceof Expr.Cup) {
								lookUpVariable = interpreter.lookUpVariable(((Expr.Cup) exprValue).identifier,
										((Expr.Cup) exprValue));
							} else if (exprValue instanceof Expr.Pocket) {
								lookUpVariable = interpreter.lookUpVariable(((Expr.Pocket) exprValue).identifier,
										((Expr.Pocket) exprValue));
							} else if (exprValue instanceof Expr.Box) {
								lookUpVariable = interpreter.lookUpVariable(((Expr.Box) exprValue).identifier,
										((Expr.Box) exprValue));
							}
							
						}
					} else {
						values.put(name.lexeme, value);
					}
				}
			} else {
				Box.error(name, "Can not assign " + exprValue + " to object of type "
						+ types.get(name.lexeme).getRunTimeTypeForObject(),true);
			}
			return;
		}

		if (enclosing != null) {
			enclosing.assign(name, exprValue, value, interpreter);
			return;
		}

		throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
	}

	public void assign(Token name, Token exprValue, Object value) {
		if (values.containsKey(name.lexeme)) {
			if (types.get(name.lexeme).getRunTimeTypeForObject() == RunTimeTypes.Any) {
				values.put(name.lexeme, value);
			} else if (RunTimeTypes.getTypeBasedOfToken(exprValue) == types.get(name.lexeme)
					.getRunTimeTypeForObject()) {
				if (types.get(name.lexeme).getRunTimeTypeForObject() == RunTimeTypes.Knot) {
					
				} else {
					values.put(name.lexeme, value);

				}
			} else {
				Box.error(name, "Can not assign " + value + " to object of type "
						+ types.get(name.lexeme).getRunTimeTypeForObject(),true);
			}
			return;
		}

		if (enclosing != null) {
			enclosing.assign(name, exprValue, value);
			return;
		}

		throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
	}

	public Object getAt(Integer distance, String lexeme) {

		return ancestor(distance).values.get(lexeme);
	}

	Environment ancestor(int distance) {
		Environment environment = this;
		for (int i = 0; i < distance; i++) {
			environment = environment.enclosing;
		}
		return environment;
	}

	public void assignAt(Integer distance, Token name, Object exprValue, Object value, Interpreter interpreter) {
		Environment ancestor = ancestor(distance);
		TypesOfObject typesOfObject = ancestor.types.get(name.lexeme);
		if (RunTimeTypes.getObjectType(exprValue, value, interpreter) == typesOfObject.getRunTimeTypeForObject() || typesOfObject.getRunTimeTypeForObject() == RunTimeTypes.Any)
			ancestor.values.put(name.lexeme, value);
	}

	public Object getTypeAt(Integer distance, String lexeme) {

		return ancestor(distance).types.get(lexeme).getRunTimeTypeForObject();
	}

	public Object getType(Token name) {

		return types.get(name.lexeme).getRunTimeTypeForObject();
	}

}
