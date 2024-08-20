package Box.Grouper;

public class ContainerIndexes {

	
	private int start;
	private int end;
	private boolean isKnot;

	public ContainerIndexes(int start, int end, boolean isKnot) {
		this.start = start;
		this.end = end;
		// TODO Auto-generated constructor stub
		this.isKnot = isKnot;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public boolean isKnot() {
		return isKnot;
	}

	public void setKnot(boolean isKnot) {
		this.isKnot = isKnot;
	}
	
	
}
