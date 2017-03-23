package action.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

/**
 * Touchpad for player's movement
 * 
 * @author Andrea
 */
public class MoveTouchpad extends Touchpad {
	
	/** Cached mov vector used for computations */
	private Vector2 cachedMovVector;

	public MoveTouchpad(float deadzoneRadius, TouchpadStyle style) {
		super(deadzoneRadius, style);
	}
	
	public Vector2 getMovVector() {
		cachedMovVector = new Vector2(getKnobPercentX(), getKnobPercentY()).nor();
		cachedMovVector.x = MathUtils.round(cachedMovVector.x);
		cachedMovVector.y = MathUtils.round(cachedMovVector.y);
		return cachedMovVector; 
	}

}
