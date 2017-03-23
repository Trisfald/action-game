package action.animation;

import action.core.Game;
import action.utility.TextureRRef;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Custom animation class
 * 
 * @author Andrea
 */
public class SuperAnimation extends Animation {
	
	/** Width of a single frame */
	public final int width;
	/** Height of a single frame */
	public final int height;
	
	public SuperAnimation(float frameDuration, TextureRegion[] keyFrames, PlayMode playMode, int width, int height) {
		super(frameDuration, keyFrames);
		this.setPlayMode(playMode);
		this.width = width;
		this.height = height;
	}
	
	public void drawCentered(SpriteBatch batch, float x, float y, float stateTime, float rotation) {
		batch.draw(getKeyFrame(stateTime), 
				x+width/2, 
				y+height/2,
				width/2,
				height/2,
				width,
				height,
				1f,
				1f,
				rotation);
	}
	
	
	/**
	 * Info class for a complete array of animations
	 * 
	 * @author Andrea
	 */
	public static class AnimationArrayInfo {
		
		public final float frameDuration;
		public final TextureRRef texture;
		public PlayMode playMode;
		public final int width;
		public final int height;
		
		public AnimationArrayInfo(float frameDuration, TextureRRef texture, PlayMode playMode, int width, int height) {
			this.frameDuration = frameDuration;
			this.texture = texture;
			this.playMode = playMode;
			this.width = width;
			this.height = height;
		}
		
		public SuperAnimation[] load() {
			TextureRegion[][] regions = Game.assets.getTextureRegion(texture).split(width, height);
			SuperAnimation[] array = new SuperAnimation[regions.length];
			
			for (int i = 0; i < regions.length; i++)
				array[i] = new SuperAnimation(frameDuration, regions[i], playMode, width, height);
			
			return array;
		}
		
		public static AnimationArrayInfo loadFromXml(Element element) {
			return new AnimationArrayInfo(
					element.getFloat("duration"),
					new TextureRRef(element.get("texture")),
					PlayMode.valueOf(element.get("playMode", PlayMode.NORMAL.name())),
					element.getInt("width"),
					element.getInt("height")
					);
		}
	}
	
}
