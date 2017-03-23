package action.input;

import action.ui.GameUI.GameUImode;
import action.utility.Vector2i;
import action.world.World;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

/**
 * Base class for game input
 * 
 * @author Andrea
 */
public abstract class GameInput extends InputAdapter {
	
	protected World world;
	protected Vector2i playerMov = new Vector2i(0, 0);
	protected boolean attacking;
	protected boolean blocking;
	
	/**
	 * @return The UI mode used by this input
	 */
	public abstract GameUImode getUImode();
	
	/**
	 * Initialize the input
	 */
	public void init(World world) {
		this.world = world;
		reset();
	}
	
	protected void reset() {
		playerMov.x = 0;
		playerMov.y = 0;
		attacking = false;
		blocking = false;
	}
	
	public Vector2i getPlayerMov() {
		return playerMov;
	}

	public void update(float delta) {

	}
	
	public boolean isAttacking() {
		return attacking;
	}
	
	public boolean isBlocking() {
		return blocking;
	}
	
	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}
	
	public void setBlocking(boolean blocking) {
		this.blocking = blocking;
	}
	
	/**
	 * Set the player movement vector from the touchpad data
	 */
	public void touchpadMov(Vector2 v) {

	}
	
}
