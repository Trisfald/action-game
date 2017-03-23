package action.input;

import com.badlogic.gdx.math.Vector2;

import action.ui.GameUI.GameUImode;

/**
 * Input for touchscreen devices
 * 
 * @author Andrea
 */
public class TouchInput extends GameInput {

	@Override
	public GameUImode getUImode() {
		return GameUImode.TOUCHSCREEN;
	}
	
	@Override
	public void touchpadMov(Vector2 v) {
		playerMov.x = (int) v.x;
		playerMov.y = (int) v.y;
	}

}
