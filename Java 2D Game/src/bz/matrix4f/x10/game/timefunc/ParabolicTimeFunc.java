package bz.matrix4f.x10.game.timefunc;

public class ParabolicTimeFunc extends TimeFunction {

	private float a, b, c;
	
	public ParabolicTimeFunc(float a, float b, float c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	@Override
	protected float solve() {
		//ax^2 + bx + c
		return a * x * x + b * x + c;
	}

	
	public float getA() {
		return a;
	}

	public void setA(float a) {
		this.a = a;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	public float getC() {
		return c;
	}

	public void setC(float c) {
		this.c = c;
	}
}
