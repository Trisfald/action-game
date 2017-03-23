package action.animation;

import action.animation.SuperAnimation.AnimationArrayInfo;
import action.utility.enums.Direction;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Simple motion
 * 
 * @author Andrea
 */
public class SimpleMotion implements Motion {
	
	/** Array with all the animations. There are as many animations as the directions of the motion. Default is 8. */
	protected SuperAnimation[] animations;
	/** Current MotionMode */
	private MotionMode mode;
	protected float stateTime;
	/** Angle of rotation (in degrees) */
	protected float rotation;
	protected float offsetX;
	protected float offsetY;
	
	public SimpleMotion(SimpleMotionLoader loader) {
		animations = loader.animations.load();
		offsetX = loader.offsetX;
		offsetY = loader.offsetY;
		detectMotionMode(animations.length);	
	}
	
	/**
	 * Empty constructor. Must build the motion afterwards.
	 */
	protected SimpleMotion() {
		
	}
	
	/**
	 * @return The current animation to draw
	 */
	protected SuperAnimation getKeyAni(Direction dir) {
		return animations[dirToIndex(dir)];
	}

	protected void detectMotionMode(int rows) {
		switch (rows) {
			case 8:
				mode = MotionMode.EIGHT_COORD;
				break;
			case 4:
				mode = MotionMode.FOUR_COORD;
				break;
			case 1:
				mode = MotionMode.ONE_COORD;
				break;
			default:
				mode = MotionMode.EMPTY;
				break;
		}
	}
	
	/**
	 * Convert a direction into an 8 type index or 4 type index
	 */
	public int dirToIndex(Direction dir) {
		switch (mode) {
			case FOUR_COORD:
				return dir.fourCoordValue();
			case ONE_COORD:
				return 0;
			default:
				return dir.value();
		}
	}
	
	@Override
	public void restart() {
		stateTime = 0;
	}
	
	/**
	 * Set the angle (in degrees) to rotate this motion to
	 */
	@Override
	public void setRotation(float degrees) {
		this.rotation = degrees;
	}
	
	@Override
	public void draw(SpriteBatch batch, Direction dir, float x, float y) {
		SuperAnimation ani = getKeyAni(dir);
		batch.draw(ani.getKeyFrame(stateTime), 
				x+offsetX, 
				y+offsetY,
				ani.width/2,
				ani.height/2,
				ani.width,
				ani.height,
				1f,
				1f,
				rotation
				);
	}
	
	@Override
	public void drawCentered(SpriteBatch batch, Direction dir, float x, float y) {
		SuperAnimation ani = getKeyAni(dir);
		draw(batch, dir, x - ani.width/2 - offsetX/2, y - ani.height/2 - offsetY/2);
	}
	
	@Override
	public void update(float delta) {
		this.stateTime += delta;
	}
	
	@Override
	public float getFrameDuration() {
		return animations[0].frameDuration;
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class SimpleMotionLoader extends MotionLoader {
		
		public final AnimationArrayInfo animations;
		public final float offsetX;
		public final float offsetY;

		public SimpleMotionLoader(AnimationArrayInfo animations, float offsetX, float offsetY) {
			super(MotionType.SIMPLE);
			this.animations = animations;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
		}

		@Override
		public Motion load() {
			return new SimpleMotion(this);
		}
		
		public static MotionLoader loadFromXml(Element element) {
			return new SimpleMotionLoader(
					AnimationArrayInfo.loadFromXml(element.getChildByName("animations")),
					element.getFloatAttribute("offsetX", 0),
					element.getFloatAttribute("offsetY", 0)
					);
		}
		
	}

}
