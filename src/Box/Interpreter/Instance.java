package Box.Interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Box.Box.Box;
import Box.Token.Token;
import Parser.Expr;
import Parser.Expr.Cup;
import Parser.Expr.Knot;
import Parser.Expr.Literal;
import Parser.Expr.LiteralChar;
import Parser.Expr.Pocket;
import Parser.Expr.Tonk;

public abstract class Instance {

	public BoxCallable boxClass;
	private final Map<String, Object> fields = new HashMap<>();
	public List<?> body;
	public Expr expr;
	

	public Instance(BoxCallable boxClass,List<?> body,Expr expr) {
		this.boxClass = boxClass;
		this.body = body;
		this.expr = expr;
		
	}

	@Override
	public String toString() {
		return boxClass.toString();
	}

	public Object get(Token name) {
		if (fields.containsKey(name.lexeme)) {
			return fields.get(name.lexeme);

		}
		BoxFunction method = boxClass.findMethod(name.lexeme);
		if(method!=null)return method.bind(this);
		throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
	}

	public void set(Token name, Object value) {
		fields.put(name.lexeme, value);
	}

	
	
	public Object getAt(int i) {
		if (boxClass instanceof BoxClass) {
			if (i <= ((BoxClass) boxClass).contents.size() - 1) {
				Object stmt = ((BoxClass) boxClass).contents.get(i);
				return stmt;
			} else {
				Box.error(null, "index out of bounds for Box Cup or Pocket.",true);
			}
		}
		if (boxClass instanceof BoxContainerClass) {
			if (i <= ((BoxContainerClass) boxClass).getPrimarys().size() - 1) {
				Object expr = ((BoxContainerClass) boxClass).get(i);
				return expr;
			} else {
				Box.error(null, "index out of bounds for Box Cup or Pocket.",true);
			}
		}
		return null;
	}

	public void setAt(Object value, Integer integer) {
		if (boxClass instanceof BoxClass) {
			if (integer <= ((BoxClass) boxClass).contents.size() - 1) {
				((BoxClass) boxClass).setContentsAt(integer, value);

			} else if (integer == 0 && ((BoxClass) boxClass).contents.size() - 1 == -1) {
				((BoxClass) boxClass).setContentsAt(integer, value);
			} else {
				Box.error(null, "index out of bounds for Box Cup or Pocket.",true);
			}
		}
		if (boxClass instanceof BoxContainerClass) {
			if (integer <= ((BoxContainerClass) boxClass).getPrimarys().size() - 1) {
				((BoxContainerClass) boxClass).setPrimaryAt(integer, value);

			} else if (integer == 0 && ((BoxContainerClass) boxClass).getPrimarys().size() - 1 == -1) {
				((BoxContainerClass) boxClass).setPrimaryAt(integer, value);
			} else {
				Box.error(null, "index out of bounds for Box Cup or Pocket.",true);
			}
		}
		

	}

	public Object get(Integer firstIndex, ArrayList<Integer> theIndexes) {
		Object firstBoxInstance = getAt(firstIndex);
		if (!theIndexes.isEmpty()) {
			if (firstBoxInstance instanceof Instance) {
				Integer theNextIndex = theIndexes.get(theIndexes.size() - 1);
				theIndexes.remove(theIndexes.size() - 1);
				return ((Instance) firstBoxInstance).get(theNextIndex, theIndexes);
			} else {
				Box.error(null, "did not find object at index. ",true);
			}
		}
		return firstBoxInstance;
	}

	public void set(Object value, Integer firstIndex, ArrayList<Integer> theIndexes) {
		if (!theIndexes.isEmpty()) {
			Object firstBoxInstance = getAt(firstIndex);

			if (firstBoxInstance instanceof Instance) {
				Integer theNextIndex = theIndexes.get(theIndexes.size() - 1);
				theIndexes.remove(theIndexes.size() - 1);
				((Instance) firstBoxInstance).set(value, theNextIndex, theIndexes);
			} else {
				Box.error(null, "did not find object at index. ",true);
			}
		} else {
			setAt(value, firstIndex);
		}
	}

	public void setAtEnd(String data) {
		if (boxClass instanceof BoxClass) {
			((BoxClass) boxClass).setContentsAtEnd(data);

		}
		if (boxClass instanceof BoxContainerClass) {
			((BoxContainerClass) boxClass).setPrimaryAtEnd(data);

		}
		
	}


	public int size() {
		
		if (boxClass instanceof BoxClass) {
			return ((BoxClass) boxClass).size();
		}
		if (boxClass instanceof BoxContainerClass) {

			return ((BoxContainerClass) boxClass).size();
		}
		
		return -1;
		
		
		
	}
	public boolean contains(Instance lookUpContents) {
		
		System.out.println("Does the container contain"+lookUpContents.toString() );
		
		return false;
	}

	public boolean contains(Literal contents) {
		System.out.println("Does the container contain"+contents.toString() );
		
		return false;
	}

	public boolean contains(LiteralChar contents) {
		System.out.println("Does the container contain"+contents.toString() );
		return false;
	}



	public boolean contains(Pocket contents) {
		System.out.println("Does the container contain"+contents.toString() );
		return false;
	}

	public boolean contains(Cup contents) {
		System.out.println("Does the container contain"+contents.toString() );
		return false;
	}

	public boolean contains(Knot contents) {
		System.out.println("Does the container contain"+contents.toString() );
		return false;
	}

	public boolean contains(Tonk contents) {
		System.out.println("Does the container contain"+contents.toString() );
		return false;
	}

	
}
