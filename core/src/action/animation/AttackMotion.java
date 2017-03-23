package action.animation;

import action.animation.SuperAnimation.AnimationArrayInfo;
import action.entity.enums.AttackState;
import action.utility.enums.Direction;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Motion for attacks
 * 
 * @author Andrea
 */
public class AttackMotion extends SimpleMotion {

	private SuperAnimation[] charge;
	private SuperAnimation[] setup;
	private SuperAnimation[] setoff;
	private AttackState state;
	
	public AttackMotion(AttackMotionLoader loader) {
		charge = loader.charge.load();
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
        	case CHARGE:
        		return charge[dirToIndex(dir)];
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
	
    public void setState(AttackState state) {
    	this.state = state;
    	stateTime = 0;
    }

	@Override
	public void restart() {
		setState(AttackState.CHARGE);
	}
	
	/**
	 * @return The duration of the given state
	 */
	public float getDuration(AttackState state) {
		switch (state) {
			case CHARGE:
				return 0;
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
	
	@Override
	public float getFrameDuration() {
		return getKeyAni(Direction.CENTER).frameDuration;
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class AttackMotionLoader extends MotionLoader {
		
		public final AnimationArrayInfo charge;
		public final AnimationArrayInfo setup;
		public final AnimationArrayInfo ready;
		public final AnimationArrayInfo setoff;
		public final float offsetX;
		public final float offsetY;
		
		public AttackMotionLoader(AnimationArrayInfo charge, AnimationArrayInfo setup, AnimationArrayInfo ready,
				AnimationArrayInfo setoff, float offsetX, float offsetY) {
			super(MotionType.ATTACK);
			this.charge = charge;
			this.setup = setup;
			this.ready = ready;
			this.setoff = setoff;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
		}
		
		@Override
		public Motion load() {
			return new AttackMotion(this);
		}
		
		public static AttackMotionLoader loadFromXml(Element element) {
			return new AttackMotionLoader(
					AnimationArrayInfo.loadFromXml(element.getChildByName("charge")),
					AnimationArrayInfo.loadFromXml(element.getChildByName("setup")),
					AnimationArrayInfo.loadFromXml(element.getChildByName("ready")),
					AnimationArrayInfo.loadFromXml(element.getChildByName("setoff")),
					element.getFloatAttribute("offsetX", 0),
					element.getFloatAttribute("offsetY", 0)
					);
		}
		
	}
	
}
