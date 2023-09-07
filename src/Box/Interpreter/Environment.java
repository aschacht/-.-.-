package Box.Interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Box.Box.Box;
import Box.Token.Token;
import Box.Token.TokenType;
import Box.Syntax.Expr;
import Box.Syntax.Stmt;

public class Environment {
	public final Environment enclosing;
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

	private void define(String name, Token type, Object value, Object initilizer,Interpreter interpreter) {
		values.put(name, value);
	
		types.put(name, new TypesOfObject(type, RunTimeTypes.getObjectType(initilizer, value, interpreter) , initilizer));
	}

	public void define(String name, Boolean enforce, Token type, Object initilizer, Object value,
			Interpreter interpreter) {
		ArrayList<Object> boxPrimarys = new ArrayList<Object>();
		Object boxInstance = null;
		if (initilizer instanceof Expr.Boxx) {
			boxInstance = interpreter.lookUpVariable(((Expr.Boxx) initilizer).identifier,
					new Expr.Variable(((Expr.Boxx) initilizer).identifier));
			boxPrimarys.add(boxInstance);
		} else if (initilizer instanceof Expr.Cup) {
			boxInstance = interpreter.lookUpVariable(((Expr.Cup) initilizer).identifier,
					new Expr.Variable(((Expr.Cup) initilizer).identifier));
			boxPrimarys.add(boxInstance);
		} else if (initilizer instanceof Expr.Pocket) {
			boxInstance = interpreter.lookUpVariable(((Expr.Pocket) initilizer).identifier,
					new Expr.Variable(((Expr.Pocket) initilizer).identifier));
			boxPrimarys.add(boxInstance);
		} else {
			boxPrimarys.add(initilizer);
		}
		if (enforce) {
			if (type.type == TokenType.CUP) {

				

				BoxClass boxClass = new BoxClass(name, null, boxPrimarys, null, TokenType.CUPCONTAINER, enforce,
						new TypesOfObject(type, RunTimeTypes.getTypeBasedOfToken(type), initilizer));

				StringBuilder sb = new StringBuilder(name);
				String reversedName = sb.reverse().toString();

				Token classDefinitionName = new Token(TokenType.IDENTIFIER, name + "_Class_Definition", null, null,null, -1,
						-1, -1, -1);
				Token classDefinitionEman = new Token(TokenType.IDENTIFIER, reversedName + "_Class_Definition", null,
						null,null, -1, -1, -1, -1);

				define(classDefinitionName.lexeme, type, null);
				define(classDefinitionEman.lexeme, type, null);
				assign(classDefinitionName, type, boxClass);
				assign(classDefinitionEman, type, boxClass);
				Object instance = boxClass.call(interpreter, null);

				define(name, type, instance, initilizer,interpreter);
				define(reversedName, type, instance, initilizer,interpreter);

				types.put(classDefinitionName.lexeme, new TypesOfObject(type,
						RunTimeTypes.getObjectType(initilizer, value, interpreter), (Expr) initilizer));
				types.put(classDefinitionEman.lexeme, new TypesOfObject(type,
						RunTimeTypes.getObjectType(initilizer, value, interpreter), (Expr) initilizer));
				types.put(name, new TypesOfObject(type, RunTimeTypes.getObjectType(initilizer, value, interpreter),
						(Expr) initilizer));
				types.put(reversedName, new TypesOfObject(type,
						RunTimeTypes.getObjectType(initilizer, value, interpreter), (Expr) initilizer));

			} else if (type.type == TokenType.POCKET) {

				

				BoxClass boxClass = new BoxClass(name, null, boxPrimarys, null, TokenType.POCKETCONTAINER, enforce,
						new TypesOfObject(type, RunTimeTypes.getObjectType(initilizer, value, interpreter), initilizer));

				StringBuilder sb = new StringBuilder(name);
				String reversedName = sb.reverse().toString();

				Token classDefinitionName = new Token(TokenType.IDENTIFIER, name + "_Class_Definition", null, null,null, -1,
						-1, -1, -1);
				Token classDefinitionEman = new Token(TokenType.IDENTIFIER, reversedName + "_Class_Definition", null,null,
						null, -1, -1, -1, -1);

				define(classDefinitionName.lexeme, type, null);
				define(classDefinitionEman.lexeme, type, null);
				assign(classDefinitionName, type, boxClass);
				assign(classDefinitionEman, type, boxClass);
				Object instance = boxClass.call(interpreter, null);

				define(name, type, instance, initilizer,interpreter);
				define(reversedName, type, instance, initilizer,interpreter);

				types.put(classDefinitionName.lexeme, new TypesOfObject(type,
						RunTimeTypes.getObjectType(initilizer, value, interpreter), (Expr) initilizer));
				types.put(classDefinitionEman.lexeme, new TypesOfObject(type,
						RunTimeTypes.getObjectType(initilizer, value, interpreter), (Expr) initilizer));
				types.put(name, new TypesOfObject(type, RunTimeTypes.getObjectType(initilizer, value, interpreter),
						(Expr) initilizer));
				types.put(reversedName, new TypesOfObject(type,
						RunTimeTypes.getObjectType(initilizer, value, interpreter), (Expr) initilizer));

			} else if (type.type == TokenType.BOX) {

				

				BoxContainerClass boxClass = new BoxContainerClass(name, boxPrimarys, TokenType.BOXCONTAINER, enforce,
						new TypesOfObject(type, RunTimeTypes.getTypeBasedOfToken(type), initilizer));

				StringBuilder sb = new StringBuilder(name);
				String reversedName = sb.reverse().toString();

				Token classDefinitionName = new Token(TokenType.IDENTIFIER, name + "_Class_Definition", null, null,null, -1,
						-1, -1, -1);
				Token classDefinitionEman = new Token(TokenType.IDENTIFIER, reversedName + "_Class_Definition", null,null,
						null, -1, -1, -1, -1);

				define(classDefinitionName.lexeme, type, null);
				define(classDefinitionEman.lexeme, type, null);
				assign(classDefinitionName, type, boxClass);
				assign(classDefinitionEman, type, boxClass);
				Object instance = boxClass.call(interpreter, null);

				define(name, type, instance, initilizer,interpreter);
				define(reversedName, type, instance, initilizer,interpreter);

				types.put(classDefinitionName.lexeme, new TypesOfObject(type,
						RunTimeTypes.getObjectType(initilizer, value, interpreter), (Expr) initilizer));
				types.put(classDefinitionEman.lexeme, new TypesOfObject(type,
						RunTimeTypes.getObjectType(initilizer, value, interpreter), (Expr) initilizer));
				types.put(name, new TypesOfObject(type, RunTimeTypes.getObjectType(initilizer, value, interpreter),
						(Expr) initilizer));
				types.put(reversedName, new TypesOfObject(type,
						RunTimeTypes.getObjectType(initilizer, value, interpreter), (Expr) initilizer));

			}else if(type.type == TokenType.KNOT) {

				BoxKnotClass knotClass = new BoxKnotClass(name, boxPrimarys, TokenType.KNOTCONTAINER, enforce,
						new TypesOfObject(type, RunTimeTypes.getTypeBasedOfToken(type), initilizer));

				StringBuilder sb = new StringBuilder(name);
				String reversedName = sb.reverse().toString();

				Token classDefinitionName = new Token(TokenType.IDENTIFIER, name + "_Class_Definition", null,null, null, -1,
						-1, -1, -1);
				Token classDefinitionEman = new Token(TokenType.IDENTIFIER, reversedName + "_Class_Definition", null,null,
						null, -1, -1, -1, -1);

				define(classDefinitionName.lexeme, type, null);
				define(classDefinitionEman.lexeme, type, null);
				assign(classDefinitionName, type, knotClass);
				assign(classDefinitionEman, type, knotClass);
				Object instance = knotClass.call(interpreter, null);

				define(name, type, instance, initilizer,interpreter);
				define(reversedName, type, instance, initilizer,interpreter);

				types.put(classDefinitionName.lexeme, new TypesOfObject(type,
						RunTimeTypes.getObjectType(initilizer, value, interpreter), (Expr) initilizer));
				types.put(classDefinitionEman.lexeme, new TypesOfObject(type,
						RunTimeTypes.getObjectType(initilizer, value, interpreter), (Expr) initilizer));
				types.put(name, new TypesOfObject(type, RunTimeTypes.getObjectType(initilizer, value, interpreter),
						(Expr) initilizer));
				types.put(reversedName, new TypesOfObject(type,
						RunTimeTypes.getObjectType(initilizer, value, interpreter), (Expr) initilizer));

			}

		} else {
			if (type.type == TokenType.CUP) {
				BoxClass boxClass = new BoxClass(name, null, boxPrimarys, null, TokenType.CUPCONTAINER, enforce,
						new TypesOfObject(type, RunTimeTypes.Any, (Expr) initilizer));
				StringBuilder sb = new StringBuilder(name);
				String reversedName = sb.reverse().toString();
				Object instance = boxClass.call(interpreter, null);
				define(name, type, instance);
				define(reversedName, type, instance);
			
				
			} else if (type.type == TokenType.POCKET) {
				BoxClass boxClass = new BoxClass(name, null, boxPrimarys, null, TokenType.POCKETCONTAINER, enforce,
						new TypesOfObject(type, RunTimeTypes.Any, (Expr) initilizer));
				StringBuilder sb = new StringBuilder(name);
				String reversedName = sb.reverse().toString();
				Object instance = boxClass.call(interpreter, null);
				define(name, type, instance);
				define(reversedName, type, instance);
				
			} else if (type.type == TokenType.BOX) {
				BoxContainerClass boxClass = new BoxContainerClass(name, boxPrimarys, TokenType.BOXCONTAINER, enforce,
						new TypesOfObject(type, RunTimeTypes.Any, (Expr) initilizer));
	
				StringBuilder sb = new StringBuilder(name);
				String reversedName = sb.reverse().toString();
				Object instance = boxClass.call(interpreter, null);
				define(name, type, instance);
				define(reversedName, type, instance);
				
			}else if (type.type == TokenType.KNOT) {
				BoxKnotClass boxClass = new BoxKnotClass(name, boxPrimarys, TokenType.BOXCONTAINER, enforce,
						new TypesOfObject(type, RunTimeTypes.Any, (Expr) initilizer));
	
				StringBuilder sb = new StringBuilder(name);
				String reversedName = sb.reverse().toString();
				Object instance = boxClass.call(interpreter, null);
				define(name, type, instance);
				define(reversedName, type, instance);
				
			}
		}
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
					if (types.get(name.lexeme).checkKnotPrototype(exprValue)) {
						values.put(name.lexeme, value);
					} else {
						Box.error(name, "Tried to assign Knot that is not of the same sturcture");
					}
				} else {
					Object objetToset = values.get(name.lexeme);
					if (objetToset instanceof BoxInstance) {
						if (((BoxInstance) objetToset).boxClass instanceof BoxClass) {

							Object lookUpVariable = null;
							if (exprValue instanceof Expr.Variable) {
								lookUpVariable = interpreter.lookUpVariable(((Expr.Variable) exprValue).name,
										((Expr.Variable) exprValue));
							} else if (exprValue instanceof Expr.Elbairav) {
								lookUpVariable = interpreter.lookUpVariable(((Expr.Elbairav) exprValue).name,
										((Expr.Elbairav) exprValue));

							} else if (exprValue instanceof Expr.Cup) {
								lookUpVariable = interpreter.lookUpVariable(((Expr.Cup) exprValue).identifier,
										((Expr.Cup) exprValue));
							} else if (exprValue instanceof Expr.Pocket) {
								lookUpVariable = interpreter.lookUpVariable(((Expr.Pocket) exprValue).identifier,
										((Expr.Pocket) exprValue));
							} else if (exprValue instanceof Expr.Boxx) {
								lookUpVariable = interpreter.lookUpVariable(((Expr.Boxx) exprValue).identifier,
										((Expr.Boxx) exprValue));
							}
							if (lookUpVariable instanceof BoxInstance) {
								ArrayList<Object> newContents = new ArrayList<>();
								newContents.add(lookUpVariable);
								((BoxClass) ((BoxInstance) objetToset).boxClass).contents = newContents;
							} else {
								ArrayList<Object> newContents = new ArrayList<>();
								newContents.add(value);
								((BoxClass) ((BoxInstance) objetToset).boxClass).contents = newContents;
							}
						} else if (((BoxInstance) objetToset).boxClass instanceof BoxContainerClass) {
							Object lookUpVariable = null;
							if (exprValue instanceof Expr.Variable) {
								lookUpVariable = interpreter.lookUpVariable(((Expr.Variable) exprValue).name,
										((Expr.Variable) exprValue));
							} else if (exprValue instanceof Expr.Elbairav) {
								lookUpVariable = interpreter.lookUpVariable(((Expr.Elbairav) exprValue).name,
										((Expr.Elbairav) exprValue));

							} else if (exprValue instanceof Expr.Cup) {
								lookUpVariable = interpreter.lookUpVariable(((Expr.Cup) exprValue).identifier,
										((Expr.Cup) exprValue));
							} else if (exprValue instanceof Expr.Pocket) {
								lookUpVariable = interpreter.lookUpVariable(((Expr.Pocket) exprValue).identifier,
										((Expr.Pocket) exprValue));
							} else if (exprValue instanceof Expr.Boxx) {
								lookUpVariable = interpreter.lookUpVariable(((Expr.Boxx) exprValue).identifier,
										((Expr.Boxx) exprValue));
							}
							if (lookUpVariable instanceof BoxInstance) {
								ArrayList<Object> newContents = new ArrayList<>();
								newContents.add(lookUpVariable);
								((BoxContainerClass) ((BoxInstance) objetToset).boxClass).contents = newContents;
							} else {
								ArrayList<Object> newContents = new ArrayList<>();
								newContents.add(value);
								((BoxClass) ((BoxInstance) objetToset).boxClass).contents = newContents;
							}
						}
					} else {
						values.put(name.lexeme, value);
					}
				}
			} else {
				Box.error(name, "Can not assign " + exprValue + " to object of type "
						+ types.get(name.lexeme).getRunTimeTypeForObject());
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
					if (types.get(name.lexeme).checkKnotPrototype(exprValue)) {
						values.put(name.lexeme, value);
					} else {
						Box.error(name, "Tried to assign Knot that is not of the same sturcture");
					}
				} else {
					values.put(name.lexeme, value);

				}
			} else {
				Box.error(name, "Can not assign " + value + " to object of type "
						+ types.get(name.lexeme).getRunTimeTypeForObject());
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
		TypesOfObject typesOfObject = ancestor(distance).types.get(name.lexeme);
		if (RunTimeTypes.getObjectType(exprValue, value, interpreter) == typesOfObject.getRunTimeTypeForObject())
			ancestor(distance).values.put(name.lexeme, value);
	}

	public Object getTypeAt(Integer distance, String lexeme) {

		return ancestor(distance).types.get(lexeme).getRunTimeTypeForObject();
	}

	public Object getType(Token name) {

		return types.get(name.lexeme).getRunTimeTypeForObject();
	}

}
