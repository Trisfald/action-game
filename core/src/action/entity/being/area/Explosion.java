package action.entity.being.area;

import java.util.EnumMap;
import java.util.Map;

import action.animation.Motion;
import action.combat.HitLevel;
import action.combat.Impact;
import action.core.Game;
import action.entity.Entity;
import action.hitbox.DangerZone;
import action.hitbox.StaticHitBox;
import action.interfaces.GlobalVar;
import action.utility.Body.BodyInfo;
import action.utility.ExtraMath;
import action.utility.HittedList;
import action.utility.enums.Direction;
import action.utility.form.Form.FormLoader;
import action.utility.timer.Timer;
import action.world.Collision;
import action.world.World;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * A circle explosion
 * 
 * @author Andrea
 */
public class Explosion extends Area {

	private Map<ExplosionMotionType, Motion> motions;
	/** Rate at which the explosion expands */
	private float growth;
	/** Size of the explosion */
	private float size;
    /** List of entites hitted by the impact */
    private HittedList hitted = new HittedList();
    /** Timer for total duration */
    private Timer duration;
    /** Timer for damage time duration */
    private Timer activity;
	
	public Explosion(float x, float y, World world, ExplosionInfo info, Impact impact) {
		super(x, y, world, info.form, info.body, impact);
		this.growth = info.growth;
		this.size = form.getHeight();
		form.setSize(size);
		duration = new Timer(info.duration);
		activity = new Timer(info.activity);
		dangerZone = new DangerZone(new StaticHitBox(getShape()), impact.getBlockLevel(), null);
		updateDangerZone();
		
		/* Load motion */
    	motions = new EnumMap<ExplosionMotionType, Motion>(ExplosionMotionType.class);
    	for (Map.Entry<ExplosionMotionType, Integer> entry : info.motions.entrySet())
    		motions.put(entry.getKey(), Game.assets.getMotionLoader(entry.getValue()).load());
	}
	
	private Motion getMotion(ExplosionMotionType type) {
		return motions.get(type);
	}
	
	private void drawMotion(SpriteBatch batch, ExplosionMotionType type, float x, float y) {
		getMotion(type).drawCentered(batch, Direction.CENTER, x + size / 2, y + size / 2);
	}
	
    private void updateMotion(ExplosionMotionType type, float delta) {
    	getMotion(type).update(delta);
    }

	@Override
	public void update(float delta) {
		duration.update(delta);
		activity.update(delta);
		maintenance();
		updateMotion(ExplosionMotionType.STANDARD, delta);
		/* Check for collisions and grow only in the period in which the explosion deals damage */
		if (!activity.expired()) {
			grow(delta);
			checkCollision(world, delta);
		}
		updateDangerZone();
	}

	private void updateDangerZone() {
		/* The explosion must be active */
		if (activity.expired()) {
			dangerZone.setActive(false);
			return;
		}
		
		/* If there isn't any growth, do nothing */
		if (growth <= 0)
			return;
		
		/* Grow shape, set danger, restore shape */
		grow(GlobalVar.DANGER_EVOLUTION_TIME);
		dangerZone.setHitBox(new StaticHitBox(ExtraMath.copyShape(getShape())));
		grow(-GlobalVar.DANGER_EVOLUTION_TIME);
	}

	@Override
	protected void drawGfx(SpriteBatch batch) {
		drawMotion(batch, ExplosionMotionType.STANDARD, position.x, position.y);
	}
	
	private void checkCollision(World world, float delta) {
		Collision collision = new Collision(this, 0, 0, world, true);
		damage(collision.getCollidingEntities());
	}
	
	private void damage(Array<Entity> targets) {
    	for (Entity x : targets)
    		if ((x.isHittable(HitLevel.ZONE)) && (!hitted.contains(x))) {
    			x.takeImpact(impact.copy(new StaticHitBox(getShape())));
    			hitted.add(x);
    		}
	}
	
	/* Grow the explosion if needed */
	private void grow(float delta) {
		/* Get center position */
		float centerX = form.getCenterX(position.x);
		float centerY = form.getCenterY(position.y);
		
		/* Change size */
		size += growth * delta;
		form.setSize(size);
		
		/* Restore central position */
		this.updatePos((centerX - form.getCenterX(position.x)), (centerY - form.getCenterY(position.y)));
	}
	
	private void maintenance() {
		/* Check for duration */
		if (duration.expired())
			suicide();
	}


	/**
	 * @author Andrea
	 */
	public enum ExplosionMotionType {
		
		STANDARD;
		
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class ExplosionInfo {
		
    	public final FormLoader form;
    	public final BodyInfo body;
        public final Map<ExplosionMotionType, Integer> motions;
        public final float duration;
        public final float activity;
        public final float growth;
        
		public ExplosionInfo(FormLoader form, BodyInfo body, Map<ExplosionMotionType, Integer> motions,
				float duration, float activity, float growth) {
			this.form = form;
			this.body = body;
			this.motions = motions;
			this.duration = duration;
			this.activity = activity;
			this.growth = growth;
		}

		public static ExplosionInfo loadFromXml(Element element) {
			return new ExplosionInfo(
					FormLoader.loadFromXml(element.getChildByName("form")),
					BodyInfo.loadFromXml(element.getChildByName("body")),
					loadMotions(element.getChildByName("motions")),
					element.getFloatAttribute("duration"),
					element.getFloatAttribute("activity"),
					element.getFloatAttribute("growth", 0)
					);
		}

	    private static Map<ExplosionMotionType, Integer> loadMotions(Element element) {
	    	Map<ExplosionMotionType, Integer> map = new EnumMap<ExplosionMotionType, Integer>(ExplosionMotionType.class);
	    	Element child;
	        for (int i = 0; i < element.getChildCount(); i++) {
	        	child = element.getChild(i);
	        	map.put(ExplosionMotionType.valueOf(child.getAttribute("type")), child.getIntAttribute("id"));
	        }
	    	return map;
	    }
		
	}

}
