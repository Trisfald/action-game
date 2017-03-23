package action.entity.being.area;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import action.animation.BeamMotion;
import action.combat.BlockLevel;
import action.combat.Damage.DamageLoader;
import action.combat.HitLevel;
import action.combat.Impact;
import action.combat.Knockback.KnockbackLoader;
import action.combat.effect.Effect.EffectLoader;
import action.core.Game;
import action.database.Database;
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Class for beam-like missiles
 * 
 * @author Andrea
 */
public class Beam extends Area {

	private Map<BeamMotionType, BeamMotion> motions;
    /** Vector with direction */
	private Vector2 dirV;
	/** Direction that the beam is facing */
    private Direction dir;
	/** Speed at which the beam is propagating (per seconds) */
	private float growth;
    /** True if the beam goes through entities */
    private boolean goThrough; 
    /** Timer for beam's activity */
    private Timer timer;
    /** Current length of the whole beam */
    private float length;
    /** List of entites hitted by the impact */
    private HittedList hitted;
    /** Minimum length */
	private float lengthMin;
	
    /**
     * Create a beam
     * @param x
     * @param y
     * @param dirV Direction of propagation
     * @param world
     * @param info
     * @param growth Rate of propagation (per seconds)
     * @param duration For how much the beam propagates (seconds)
     * @param impact
     * @param frequency How often the impact is given to hitted entities (seconds)
     */
	public Beam(float x, float y, Vector2 dirV, World world, BeamInfo info, float growth, float duration, Impact impact, float frequency) {
		super(x, y, world, info.form, info.body, impact);
		this.dirV = dirV.nor();
		this.goThrough = info.goThrough;
		this.length = lengthMin = info.startingLength;
		this.growth = growth;
		hitted = new HittedList(frequency);
		timer = new Timer(duration);
		dangerZone = new DangerZone(new StaticHitBox(getShape()), impact.getBlockLevel(), null);
		
		/* Load motion */
    	motions = new EnumMap<BeamMotionType, BeamMotion>(BeamMotionType.class);
    	for (Map.Entry<BeamMotionType, Integer> entry : info.motions.entrySet())
    		motions.put(entry.getKey(), (BeamMotion) Game.assets.getMotionLoader(entry.getValue()).load());
    	
    	/* Get the form to the starting length */
    	getForm().setSize(length);
    	/* Fix the starting direction */
		calibrateDirection();
		/* Initialize danger zone */
		updateDangerZone();
	}
	
	public Beam(float x, float y, Vector2 dirV, World world, BeamLoader loader, Impact impact) {
		this(x, y, dirV, world, Game.assets.getBeamInfo(loader.beam), loader.growth, loader.duration, impact, loader.frequency);
	}
	
	private BeamMotion getMotion(BeamMotionType type) {
		return motions.get(type);
	}
	
	private void drawMotion(SpriteBatch batch, BeamMotionType type, float x, float y) {
		getMotion(type).setLength(length);
		getMotion(type).draw(batch, dir, x, y);
	}
	
    private void updateMotion(BeamMotionType type, float delta) {
    	getMotion(type).update(delta);
    }
	
    private void resetMotion(BeamMotionType type) {
    	getMotion(type).restart();
    }
    
    private void rotateMotion(BeamMotionType type) { 	
    	getMotion(type).setRotation(dirV.angle());
    }

	@Override
	protected void drawGfx(SpriteBatch batch) {
		drawMotion(batch, BeamMotionType.STANDARD, position.x, position.y);
	}

    @Override
    public void update(float delta) {
    	timer.update(delta);
    	checkCollision(world, delta);
    	updateMotion(BeamMotionType.STANDARD, delta);
    	hitted.update(delta);
    	updateDangerZone();
    }
    
    private void shrink(World world, float delta) {
    	float growAmount = growth * delta;
    	length -= growAmount;
    	/* Check for death of the beam */
    	if (length < lengthMin)
    		suicide();
    	
    	getMotion(BeamMotionType.STANDARD).setLength(length);
    	getForm().setSize(length);
    	
    	/* Must reset x and y to keep the tip on the same position */
    	setPos(position.x + dirV.x * growAmount, position.y + dirV.y * growAmount);
    }
    
    private void stretch(World world, float delta) {
    	length += growth * delta;
    	getMotion(BeamMotionType.STANDARD).setLength(length);
    	getForm().setSize(length);
    }
    
    private void checkCollision(World world, float delta) {
    	Collision collision = new Collision(this, dir, world);
    	/* First we update the dimensions */
    	grow(world, delta);
    	/* If it collides with a wall, undo the growth.
    	 * Check only if the beam is stretching, not shrinking */
        if (!collision.canMove(goThrough) && !timer.expired()) {
        	grow(world, -delta);
        }
        damage(collision.getCollidingEntities());
    }
    
    /*
     * Modify the dimension on the beam
     */
    private void grow(World world, float delta) {
    	if (timer.expired())
    		shrink(world, delta);
    	else
    		stretch(world, delta);
    }
    
	private void calibrateDirection() {
		resetMotion(BeamMotionType.STANDARD);
		dir = Direction.unitVectorToDir(dirV);
		rotateMotion(BeamMotionType.STANDARD);
		getForm().rotate(dirV.angle());
	}
	
    private void damage(Array<Entity> targets) {
    	for (Entity x : targets)
    		if ((x.isHittable(HitLevel.PROJECTILE)) && (!hitted.contains(x))) {
    			x.takeImpact(impact.copy(new StaticHitBox(getShape(), dirV, form.getSize())));
    			hitted.add(x);
    		}
    }
    
	private void updateDangerZone() {
		/* Save old size */
		float oldSize = form.getSize();
		/* Compute edges as a point far behind the effective shape, to ensure blocking is done in the right direction */
		Vector2 edge = new Vector2(-dirV.x * GlobalVar.DANGER_START_DISTANCE + getCenterX(), 
				-dirV.y * GlobalVar.DANGER_START_DISTANCE + getCenterY());
		form.setSize(oldSize + dirV.len() * growth);
		dangerZone.setHitBox(new StaticHitBox(ExtraMath.copyShape(getShape()), edge, edge));
		form.setSize(oldSize);
	}
    
    
    /**
     * @author Andrea
     */
    public static class BeamInfo {
    	
    	public final FormLoader form;
    	public final BodyInfo body;
        public final Map<BeamMotionType, Integer> motions;
        public final float startingLength;
        public final boolean goThrough;
        
		public BeamInfo(FormLoader form, BodyInfo body, Map<BeamMotionType, Integer> motions, float startingLength,
				boolean goThrough) {
			this.form = form;
			this.body = body;
			this.motions = motions;
			this.startingLength = startingLength;
			this.goThrough = goThrough;
		}

		public static BeamInfo loadFromXml(Element element) {
			return new BeamInfo(
					FormLoader.loadFromXml(element.getChildByName("form")),
					BodyInfo.loadFromXml(element.getChildByName("body")),
					loadMotions(element.getChildByName("motions")),
					element.getFloatAttribute("startingLength"),
					element.getBooleanAttribute("goThrough")
					);
		}
		
	    private static Map<BeamMotionType, Integer> loadMotions(Element element) {
	    	Map<BeamMotionType, Integer> map = new EnumMap<BeamMotionType, Integer>(BeamMotionType.class);
	    	Element child;
	        for (int i = 0; i < element.getChildCount(); i++) {
	        	child = element.getChild(i);
	        	map.put(BeamMotionType.valueOf(child.getAttribute("type")), child.getIntAttribute("id"));
	        }
	    	return map;
	    }
		
    }
    
    
    /**
     * @author Andrea
     */
    public static class BeamLoader {
    	
    	public final int beam;
    	public final float growth;
    	public final float duration;
	    public final List<DamageLoader> damages;
	    public final List<EffectLoader> effects;
	    public final KnockbackLoader knockback;
	    public final BlockLevel block;
	    public final float crushPower;
       	public final float frequency;
		public final boolean selective;
		
		public BeamLoader(int beam, float growth, float duration, List<DamageLoader> damages, List<EffectLoader> effects,
				KnockbackLoader knockback, BlockLevel block, float crushPower, float frequency, boolean selective) {
			this.beam = beam;
			this.growth = growth;
			this.duration = duration;
			this.damages = damages;
			this.effects = effects;
			this.knockback = knockback;
			this.block = block;
			this.crushPower = crushPower;
			this.frequency = frequency;
			this.selective = selective;
		}
		
		public static BeamLoader loadFromXml(Element element) {
			return new BeamLoader(
					element.getIntAttribute("beam"),
					element.getFloatAttribute("growth"),
					element.getFloatAttribute("duration"),
	    			Database.loadDamages(element.getChildByName("damages")),
	    			Database.loadEffects(element.getChildByName("effects")),
	    			KnockbackLoader.loadFromXML(element.getChildByName("knockback")),
					BlockLevel.valueOf(element.getAttribute("block")),
					element.getFloatAttribute("crushPower"),
					element.getFloatAttribute("frequency"),
					element.getBooleanAttribute("selective", true)
					);
		}

    }
    
    
    /**
     * @author Andrea
     */
    public enum BeamMotionType {
    	
    	STANDARD;
    	
    }
    
}
