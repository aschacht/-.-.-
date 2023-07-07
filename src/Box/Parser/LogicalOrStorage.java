package Box.Parser;

public class LogicalOrStorage<T,K>{
	T operator;
	K stment;
	public LogicalOrStorage(T operator, K stment ){
		this.operator = operator;
		this.stment=stment;
	}
	public T getOperator() {
		return operator;
	}
	public K getStment() {
		return stment;
	}
}