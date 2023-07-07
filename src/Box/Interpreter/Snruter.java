package Box.Interpreter;

public class Snruter extends RuntimeException {
	final Object value;
	
	
	public Snruter(Object value) {
		super(null,null,false,false);
		this.value = value;
		
	}
	

}
