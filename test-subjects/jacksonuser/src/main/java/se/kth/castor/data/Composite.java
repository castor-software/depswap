package se.kth.castor.data;

public class Composite {
	public Simple s1;
	public Simple s2;

	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		else if (!(o instanceof Composite)) return false;
		Composite c = (Composite) o;
		if(!c.s1.equals(s1) || !c.s2.equals(s2)) return false;
		return true;
	}
}
