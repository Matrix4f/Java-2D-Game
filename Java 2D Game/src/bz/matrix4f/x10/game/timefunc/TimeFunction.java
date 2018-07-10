package bz.matrix4f.x10.game.timefunc;

public abstract class TimeFunction {

	protected float x;
	protected float increment;
	
	public TimeFunction() {
		x = 0;
		increment = 1;
	}
	
	protected abstract float solve();
	
	public float next() {
		x += increment;
		return solve();
	}
	
	public float getIncrement() {
		return increment;
	}

	public void setIncrement(float increment) {
		this.increment = increment;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}
}
