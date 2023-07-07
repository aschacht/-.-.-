 package Box.Syntax;

public class ifStatmentHolder <T,K> {
	T ifcondition;
	K thenCodeBlock;
	public ifStatmentHolder(T ifcond,K thenCode){
		ifcondition=ifcond;
		thenCodeBlock=thenCode;
	}
	
	public T getIfcondition() {
		return ifcondition;
	}
	public K getThenCodeBlock() {
		return thenCodeBlock;
	}
}
