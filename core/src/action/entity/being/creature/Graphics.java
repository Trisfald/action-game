package action.entity.being.creature;

import java.util.EnumMap;
import java.util.Map;

import action.animation.AttackMotion;
import action.animation.Motion;
import action.animation.SimpleMotion;
import action.core.Game;
import action.entity.enums.AttackState;
import action.entity.enums.AttackType.AttackMotionType;
import action.entity.enums.SimpleMotionType;
import action.sound.EmptySound;
import action.world.World;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * An utility to manage creature rendering
 *
 * @author Andrea
 */
public class Graphics {

	private Creature owner;
    private Map<SimpleMotionType, GfxData<SimpleMotion>> simple;
    private Map<AttackMotionType, GfxData<AttackMotion>> attack;
    /** Color tint applied to the rendering */
    private Color tint = new Color(Color.WHITE);
    private PooledEffect blood;
    
    public Graphics(Creature owner, GraphicsInfo info) {
    	this.owner = owner;   
    	
    	simple = new EnumMap<SimpleMotionType, GfxData<SimpleMotion>>(SimpleMotionType.class);
    	for (Map.Entry<SimpleMotionType, GfxInfo> x : info.simple.entrySet())
    		simple.put(x.getKey(), new GfxData<SimpleMotion>(x.getValue()));
    	
        attack = new EnumMap<AttackMotionType, GfxData<AttackMotion>>(AttackMotionType.class);
    	for (Map.Entry<AttackMotionType, GfxInfo> x : info.attack.entrySet())
    		attack.put(x.getKey(), new GfxData<AttackMotion>(x.getValue()));
    	
    	/* Create particle effect */
		blood = World.BLOOD_EFFECT_POOL.obtain();
		blood.allowCompletion();
    }
    
    public Color getTint() {
    	return tint;
    }
    
    /**
     * Signal graphically that an hit has been taken
     */
    public void visualizeHit() {
		blood.start();
    }
    
    public void update(float delta) {
		blood.setPosition(owner.getCenterX(), owner.getCenterY());
    	blood.update(delta);
    	if (blood.isComplete())
    		blood.free();
    }

	public SimpleMotion getSimpleMotion(SimpleMotionType type) {
		return simple.get(type).motion;
	}

	public AttackMotion getAttackMotion(AttackMotionType type) {
		return attack.get(type).motion;
	}
	
	public void draw(SpriteBatch batch) {
		batch.setColor(tint);
		owner.getLogic().getAction().drawBody(batch, this, owner.getCenterX(), owner.getCenterY());
		blood.draw(batch);
		batch.setColor(Color.WHITE);
	}

	public void drawSimple(SpriteBatch batch, SimpleMotionType type, float x, float y) {
		getSimpleMotion(type).drawCentered(batch, owner.getLogic().getDir(), x, y);
	}
	
    public void updateSimple(SimpleMotionType type, float delta) {
    	getSimpleMotion(type).update(delta);
    }
	
    public void resetSimple(SimpleMotionType type) {
    	getSimpleMotion(type).restart();
    }
    
    public void drawAttack(SpriteBatch batch, AttackMotionType type, float x, float y) {
    	getAttackMotion(type).drawCentered(batch, owner.getLogic().getDir(), x, y);
    }
    
    public void updateAttack(AttackMotionType type, float delta) {
    	getAttackMotion(type).update(delta);
    }
    
    public void setAttackState(AttackMotionType type, AttackState state) {
    	getAttackMotion(type).setState(state);
    }
    
    public void resetAttack(AttackMotionType type) {
    	getAttackMotion(type).restart();
    } 

    public boolean hasSimpleMotion(SimpleMotionType type) {
    	return simple.containsKey(type);
    }
    
    public boolean hasAttackMotion(AttackMotionType type) {
    	return attack.containsKey(type);
    }
    
    public Sound getSound(AttackMotionType type) {
    	return attack.get(type).sound;
    }
    
    public Sound getSound(SimpleMotionType type) {
    	return simple.get(type).sound;
    }
    
    public void dispose() {
    	blood.dispose();
    }
    
    
    /**
     * @author Andrea
     */
    public static class GraphicsInfo {

        public final Map<SimpleMotionType, GfxInfo> simple;
        public final Map<AttackMotionType, GfxInfo> attack;
    	
        public GraphicsInfo(Map<SimpleMotionType, GfxInfo> simple, Map<AttackMotionType, GfxInfo> attack) {
        	this.simple = simple;
        	this.attack = attack;
        }
        
		public static GraphicsInfo loadFromXml(Element element) {
			return new GraphicsInfo(
                    loadSimpleMotions(element.getChildByName("simple")),
                    loadAttackMotions(element.getChildByName("attack"))
					);
		}
    	
	    private static Map<SimpleMotionType, GfxInfo> loadSimpleMotions(Element element) {
	    	Map<SimpleMotionType, GfxInfo> map = new EnumMap<SimpleMotionType, GfxInfo>(SimpleMotionType.class);
	    	Element child;
	        for (int i = 0; i < element.getChildCount(); i++) {
	        	child = element.getChild(i);
	        	map.put(SimpleMotionType.valueOf(child.getAttribute("type")), 
	        			new GfxInfo(child.getIntAttribute("id"), child.getAttribute("sound", null)));
	        }
	    	return map;
	    }
	    
	    private static Map<AttackMotionType, GfxInfo> loadAttackMotions(Element element) {
	    	Map<AttackMotionType, GfxInfo> map = new EnumMap<AttackMotionType, GfxInfo>(AttackMotionType.class);
	    	Element child;
	        for (int i = 0; i < element.getChildCount(); i++) {
	        	child = element.getChild(i);
	        	map.put(AttackMotionType.valueOf(child.getAttribute("type")), 
	        			new GfxInfo(child.getIntAttribute("id"), child.getAttribute("sound", null)));
	        }
	    	return map;
	    }
    	
    }
    
    
	/**
	 * @author Andrea
	 */
	public static class GfxData<T extends Motion> {
		
		public final T motion;
		public final Sound sound;
		
		@SuppressWarnings("unchecked")
		private GfxData(GfxInfo info)  {
			motion = (T) Game.assets.getMotionLoader(info.motion).load();
			sound = (info.sound != null) ? Game.assets.get(info.sound, Sound.class) : new EmptySound();
		}
		
	}
	
	
	/**
	 * Info for audio/video
	 * 
	 * @author Andrea
	 */
	public static class GfxInfo {
		
		public final int motion;
		public final String sound;
		
		public GfxInfo(int motion, String sound) {
			this.motion = motion;
			this.sound = sound;
		}
		
	}

}
