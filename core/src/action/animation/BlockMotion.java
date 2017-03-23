package action.animation;

import action.animation.SuperAnimation.AnimationArrayInfo;
import action.entity.enums.BlockState;
import action.utility.enums.Direction;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Motion for blocks
 * 
 * @author Andrea
 */
public class BlockMotion extends SimpleMotion {

	private SuperAnimation[] setup;
	private SuperAnimation[] setoff;
	private BlockState state;
	
	public BlockMotion(BlockMotionLoader loader) {
		setup = loader.setup.load();
		animations = loader.ready.load();
		setoff = loader.setoff.load();
		offsetX = loader.offsetX;
		offsetY = loader.offsetY;
		detectMotionMode(animations.length);
	}
	
	@Override
	protected SuperAnimation getKeyAni(Direction dir) {
        switch (state) {
            case SET_UP:
            	return setup[dirToIndex(dir)];
            case READY:
            	return animations[dirToIndex(dir)];
            case SET_OFF:
            	return setoff[dirToIndex(dir)];
            default:
            	return null;
        }
	}
	
    public void setState(BlockState state) {
    	this.state = state;
    	stateTime = 0;
    }

	@Override
	public void restart() {
		setState(BlockState.SET_UP);
	}
	
	@Override
	public float getFrameDuration() {
		return getKeyAni(Direction.CENTER).frameDuration;
	}
	
	/**
	 * @return The duration of the given state
	 */
	public float getDuration(BlockState state) {
		switch (state) {
			case READY:
				return animations[0].animationDuration;
			case SET_OFF:
				return setoff[0].animationDuration;
			case SET_UP:
				return setup[0].animationDuration;
			default:
				return 0;
		}
	}

	
	/**
	 * @author Andrea
	 */
	public static class BlockMotionLoader extends MotionLoader {
		
		public final AnimationArrayInfo setup;
		public final AnimationArrayInfo ready;
		public final AnimationArrayInfo setoff;
		public final float offsetX;
		public final float offsetY;
		
		public BlockMotionLoader(AnimationArrayInfo setup, AnimationArrayInfo ready, AnimationArrayInfo setoff, 
				float offsetX, float offsetY) {
			super(MotionType.BLOCK);
			this.setup = setup;
			this.ready = ready;
			this.setoff = setoff;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
		}
	
		@Override
		public Motion load() {
			return new BlockMotion(this);
		}
		
		public static BlockMotionLoader loadFromXml(Element element) {
			return new BlockMotionLoader(
					AnimationArrayInfo.loadFromXml(element.getChildByName("setup")),
					AnimationArrayInfo.loadFromXml(element.getChildByName("ready")),
					AnimationArrayInfo.loadFromXml(element.getChildByName("setoff")),
					element.getFloatAttribute("offsetX", 0),
					element.getFloatAttribute("offsetY", 0)
					);
		}
	}
}
