package action.world.ambience;

import action.interfaces.GlobalVar;
import action.utility.timer.Timer;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.NumberUtils;

/**
 * @author Andrea
 */
public class Light {

	private float x;
	private float y;
	/** Original size */
	private float baseSize;
	/** Current size */
	private float size;
	/** Size variation */
	private float variation;
	/** Fluctuation timer */
	private Timer timer = new Timer(MathUtils.random(0, FLUCTUATION_TIME), FLUCTUATION_TIME, true);
	private Color color;
	
	private static final float FLUCTUATION_TIME = 10f;
	
	public Light(float x, float y, float size, float variation, Color color) {
		this.x = x;
		this.y = y;
		this.baseSize = size;
		this.color = color;
		this.variation = variation;
		
		this.size = baseSize;
	}

	public void shiftPosition(float offsetX, float offsetY) {
		x += offsetX;
		y += offsetY;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	public Color getColor() {
		return color;
	}

	public float getSize() {
		return size;
	}
	
	public void update(float delta) {
		if (variation == 0)
			return;
		
		timer.update(delta);
		size = baseSize + baseSize*variation*MathUtils.sin(timer.getProgress()*MathUtils.PI2);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + NumberUtils.floatToIntBits(x);
		result = prime * result + NumberUtils.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Light other = (Light)obj;
		if (NumberUtils.floatToIntBits(x) != NumberUtils.floatToIntBits(other.x)) return false;
		if (NumberUtils.floatToIntBits(y) != NumberUtils.floatToIntBits(other.y)) return false;
		return true;
	}
	
	public boolean isOnCamera(Camera camera) {
		float x = camera.position.x - camera.viewportWidth/2;
		float y = camera.position.y - camera.viewportHeight/2;

		if (this.x+size/2+GlobalVar.ONCAMERA_MARGIN < x) return false;
		if (this.x-size/2-GlobalVar.ONCAMERA_MARGIN > x+camera.viewportWidth) return false;
		if (this.y+size/2+GlobalVar.ONCAMERA_MARGIN < y) return false;
		if (this.y-size/2-GlobalVar.ONCAMERA_MARGIN > y+camera.viewportHeight) return false;
		
		return true;
	}
	
	
}
