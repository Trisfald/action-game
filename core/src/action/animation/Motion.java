package action.animation;

import action.animation.AttackMotion.AttackMotionLoader;
import action.animation.BeamMotion.BeamMotionLoader;
import action.animation.BlockMotion.BlockMotionLoader;
import action.animation.SimpleMotion.SimpleMotionLoader;
import action.utility.enums.Direction;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Manages a group of animations that defines a 'motion'
 *
 * @author Andrea
 */
public interface Motion {

	public void draw(SpriteBatch batch, Direction dir, float x, float y);
	
	public void drawCentered(SpriteBatch batch, Direction dir, float x, float y);
	
	public void update(float delta);
	
	public void restart();
	
	/**
	 * Set the angle (in degrees) to rotate this motion to
	 */
	public void setRotation(float degrees);
	
	public float getFrameDuration();
	
	
	
	
	
	
	
	/**
	 * Defines how many animations are in the motion
	 * 
	 * @author Andrea
	 */
	public enum MotionMode {
		/** Default mode */
		EIGHT_COORD,
		/** Simplified with only main directions */
		FOUR_COORD,
		/** Only one direction */
		ONE_COORD,
		/** No animation is present */
		EMPTY;
	}
	
	
	/**
	 * @author Andrea
	 */
	public enum MotionType {
		
		SIMPLE,
		ATTACK,
		BEAM, 
		BLOCK;
		
	}
	
	
	/**
	 * @author Andrea
	 */
	public static abstract class MotionLoader {
		
		public final MotionType type;
		
		public MotionLoader(MotionType type) {
			this.type = type;
		}
		
		public abstract Motion load();
		
		public static MotionLoader loadFromXml(Element element) {
	    	switch (MotionType.valueOf(element.getAttribute("type"))) {
				case ATTACK:
					return AttackMotionLoader.loadFromXml(element);
				case BEAM:
					return BeamMotionLoader.loadFromXml(element);
				case BLOCK:
					return BlockMotionLoader.loadFromXml(element);
				case SIMPLE:
					return SimpleMotionLoader.loadFromXml(element);
				default:
					return null;
	    	}
		}
	}
}
