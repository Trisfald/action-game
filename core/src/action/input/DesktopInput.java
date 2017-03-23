package action.input;

import action.ui.GameUI.GameUImode;
import action.utility.flag.Flag;

import com.badlogic.gdx.Input;

/**
 * Input for desktop
 * 
 * @author Andrea
 */
public class DesktopInput extends GameInput {
	
	/** Flag used to check that no movement button is released before being pressed */
	private Flag move = new Flag();

	@Override
	protected void reset() {
		super.reset();
		move.reset();
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if (keyDownAttack(keycode))
			return true;
		if (keyDownBlock(keycode))
			return true;
		if (keyDownMovement(keycode))
			return true;
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keyUpAttack(keycode))
			return true;
		if (keyUpBlock(keycode))
			return true;		
		if (keyUpMovement(keycode))
			return true;
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
	/**
	 * @return True if the input has been processed
	 */
    private boolean keyDownMovement(int keycode) {
    	if (keycode == Input.Keys.NUMPAD_8) {
    		playerMov.y += 1;
    		move.hold();
    		return true;
    	}
    	if (keycode == Input.Keys.NUMPAD_5) {
    		playerMov.y -= 1;
    		move.hold();
    		return true;
    	}
    	else if (keycode == Input.Keys.NUMPAD_6) {
    		playerMov.x += 1;
    		move.hold();
    		return true;
    	}
    	else if (keycode == Input.Keys.NUMPAD_4) {
    		playerMov.x -= 1;
    		move.hold();
    		return true;
    	}
		return false;
    }
    
	/**
	 * @return True if the input has been processed
	 */
    private boolean keyUpMovement(int keycode) {
    	/* Ignore keyup when the keydown has not been detected previusly */
    	if (!move.active())
    		return false;
    	if (keycode == Input.Keys.NUMPAD_8) {
    		playerMov.y -= 1;
    		move.release();
    		return true;
    	}
    	if (keycode == Input.Keys.NUMPAD_5) {
    		playerMov.y += 1;
    		move.release();
    		return true;
    	}
    	else if (keycode == Input.Keys.NUMPAD_6) {
    		playerMov.x -= 1;
    		move.release();
    		return true;
    	}
    	else if (keycode == Input.Keys.NUMPAD_4) {
    		playerMov.x += 1;
    		move.release();
    		return true;
    	}
		return false;
    }
    
	/**
	 * @return True if the input has been processed
	 */
    private boolean keyDownAttack(int keycode) {
    	if (keycode == Input.Keys.A) {
    		attacking = true;
    		return true;
    	}
    	return false;
    }
    
	/**
	 * @return True if the input has been processed
	 */
    private boolean keyDownBlock(int keycode) {
    	if (keycode == Input.Keys.S) {
    		blocking = true;
    		return true;
    	}
    	return false;
    }
    
	/**
	 * @return True if the input has been processed
	 */
    private boolean keyUpAttack(int keycode) {
    	if (keycode == Input.Keys.A) {
    		attacking = false;
    		return true;
    	}
    	return false;
    }
    
	/**
	 * @return True if the input has been processed
	 */
    private boolean keyUpBlock(int keycode) {
    	if (keycode == Input.Keys.S) {
    		blocking = false;
    		return true;
    	}
    	return false;
    }

	@Override
	public GameUImode getUImode() {
		return GameUImode.DESKTOP;
	}

}
