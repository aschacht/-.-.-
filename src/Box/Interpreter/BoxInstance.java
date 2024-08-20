package Box.Interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Box.Box.Box;
import Box.Token.Token;

public class BoxInstance {

	public BoxCallable boxClass;
	private final Map<String, Object> fields = new HashMap<>();

	public BoxInstance(BoxCallable boxClass) {
		this.boxClass = boxClass;
	}

	@Override
	public String toString() {

		return boxClass.toString();
	}

	public Object get(Token name) {
		if (fields.containsKey(name.lexeme)) {
			return fields.get(name.lexeme);

		}
		if (boxClass instanceof BoxClass) {
			Object object = ((BoxClass) boxClass).get(name.lexeme);

			return object;
		}
		if (boxClass instanceof BoxContainerClass) {
			Object object = ((BoxContainerClass) boxClass).get(name.lexeme);

			return object;
		}
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
		if (boxClass instanceof BoxKnotClass) {
			if (integer <= ((BoxKnotClass) boxClass).getPrimarys().size() - 1) {
				((BoxKnotClass) boxClass).setPrimaryAt(integer, value);

			} else if (integer == 0 && ((BoxKnotClass) boxClass).getPrimarys().size() - 1 == -1) {
				((BoxKnotClass) boxClass).setPrimaryAt(integer, value);
			} else {
				Box.error(null, "index out of bounds for Box Cup or Pocket.",true);
			}
		}

	}

	public Object get(Integer firstIndex, ArrayList<Integer> theIndexes) {
		Object firstBoxInstance = getAt(firstIndex);
		if (!theIndexes.isEmpty()) {
			if (firstBoxInstance instanceof BoxInstance) {
				Integer theNextIndex = theIndexes.get(theIndexes.size() - 1);
				theIndexes.remove(theIndexes.size() - 1);
				return ((BoxInstance) firstBoxInstance).get(theNextIndex, theIndexes);
			} else {
				Box.error(null, "did not find object at index. ",true);
			}
		}
		return firstBoxInstance;
	}

	public void set(Object value, Integer firstIndex, ArrayList<Integer> theIndexes) {
		if (!theIndexes.isEmpty()) {
			Object firstBoxInstance = getAt(firstIndex);

			if (firstBoxInstance instanceof BoxInstance) {
				Integer theNextIndex = theIndexes.get(theIndexes.size() - 1);
				theIndexes.remove(theIndexes.size() - 1);
				((BoxInstance) firstBoxInstance).set(value, theNextIndex, theIndexes);
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
		if (boxClass instanceof BoxKnotClass) {
			((BoxKnotClass) boxClass).setPrimaryAtEnd(data);

		}
	}

	public boolean contains(BoxInstance lookUpContents) {
		
		boolean compairPrimarys =false;
		if (boxClass instanceof BoxCallable) {
			if (lookUpContents.boxClass instanceof BoxCallable) {
				List<Object> contents = ((BoxCallable) boxClass).contents;
				for (Object object : contents) {
					if (object instanceof BoxInstance) {
						if (((BoxInstance) object).boxClass instanceof BoxCallable) {
							if (((BoxCallable) ((BoxInstance) object).boxClass).type == ((BoxCallable) lookUpContents.boxClass).type) {
								compairPrimarys = ((BoxCallable) ((BoxInstance) object).boxClass).compairPrimarys(((BoxCallable) lookUpContents.boxClass));
								if(compairPrimarys)return true;
							}
						}
					}
				}
			}
		}

		
		return false;
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
}
