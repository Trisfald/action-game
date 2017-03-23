package action.animation;

import action.animation.SuperAnimation.AnimationArrayInfo;
import action.utility.enums.Direction;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Motion for beams
 * 
 * @author Andrea
 */
public class BeamMotion extends SimpleMotion {

	private SuperAnimation[] head;
	private SuperAnimation[] body;
	/** Total length */
	private float length;
	
	public BeamMotion(BeamMotionLoader loader) {
		head = loader.head.load();
		body = loader.body.load();
		detectMotionMode(animations.length);
	}
	
	public void setLength(float length) {
		this.length = length;
	}
	
	@Override
	public void draw(SpriteBatch batch, Direction dir, float x, float y) {
		SuperAnimation bodyAni = body[dirToIndex(dir)];
		SuperAnimation headAni = head[dirToIndex(dir)];
		/* Adjust x */
		x += bodyAni.width / 2;
		
		drawBody(batch, bodyAni, x, y, length - headAni.height);
		drawHead(batch, headAni, x, y);
	}

	/**
	 * Draws the head
	 */
	private void drawHead(SpriteBatch batch, SuperAnimation ani, float x, float y) {
		/* Get center coordinates */
		float cX = x + (length - ani.height/2) * MathUtils.cosDeg(rotation);
		float cY = y + (length - ani.height/2) * MathUtils.sinDeg(rotation);

		/* Draw the head centered */
		ani.drawCentered(batch, cX, cY, stateTime, rotation);
	}
	
	private void drawBody(SpriteBatch batch, SuperAnimation ani, float x, float y, float length) {
		if (length <= 0)
			return;
		
		if (length <= ani.height) {
			/* Keep size even */
			int size = 2 + (int) length + (int) length % 2;
			/* Get center coordinates */
			float cX = x + (size/2) * MathUtils.cosDeg(rotation);
			float cY = y + (size/2) * MathUtils.sinDeg(rotation);
			
			/* Draw the body centered */
			ani.drawCentered(batch, cX, cY, stateTime, rotation);
		}
		else {
			/* Get center coordinates */
			float cX = x + (length - ani.height/2) * MathUtils.cosDeg(rotation);
			float cY = y + (length - ani.height/2) * MathUtils.sinDeg(rotation);
			
			/* Draw the body centered */
			ani.drawCentered(batch, cX, cY, stateTime, rotation);
			
			/* Draw the next part of the body recursively */
			drawBody(batch, ani, x, y, length - ani.height);
		}
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class BeamMotionLoader extends MotionLoader {
		
		public final AnimationArrayInfo head;
		public final AnimationArrayInfo body;
		
		public BeamMotionLoader(AnimationArrayInfo head, AnimationArrayInfo body) {
			super(MotionType.BEAM);
			this.head = head;
			this.body = body;
		}
		
		@Override
		public Motion load() {
			return new BeamMotion(this);
		}
	
		public static BeamMotionLoader loadFromXml(Element element) {
			return new BeamMotionLoader(
					AnimationArrayInfo.loadFromXml(element.getChildByName("head")),
					AnimationArrayInfo.loadFromXml(element.getChildByName("body"))
					);
		}
		
	}
}
