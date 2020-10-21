package se.kth.castor.data;

import java.math.BigInteger;

public class Simple {
	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public double getD() {
		return d;
	}

	public void setD(double d) {
		this.d = d;
	}

	public Long getL() {
		return l;
	}

	public void setL(Long l) {
		this.l = l;
	}

	public Float getF() {
		return f;
	}

	public void setF(Float f) {
		this.f = f;
	}

	public BigInteger getBi() {
		return bi;
	}

	public void setBi(BigInteger bi) {
		this.bi = bi;
	}

	String str;
	int i;
	double d;
	Long l;
	Float f;
	BigInteger bi;

	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		else if (!(o instanceof Simple)) return false;
		Simple s = (Simple) o;
		if(!s.bi.equals(bi) || !s.f.equals(f) || !s.l.equals(l) || s.d != d || s.i != i || !(s.str.compareTo(str) == 0)) return false;
		return true;
	}

	public Simple() {}

}
