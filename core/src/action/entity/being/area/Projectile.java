package action.entity.being.area;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import action.animation.Motion;
import action.combat.BlockLevel;
import action.combat.Damage.DamageLoader;
import action.combat.HitLevel;
import action.combat.Impact;
import action.combat.Knockback;
import action.combat.Knockback.KnockbackLoader;
import action.combat.effect.Effect.EffectLoader;
import action.core.Game;
import action.database.Database;
import action.entity.Entity;
import action.hitbox.DangerZone;
import action.hitbox.StaticHitBox;
import action.interfaces.GlobalVar;
import action.interfaces.Mover;
import action.sound.EmptySound;
import action.utility.Body.BodyInfo;
import action.utility.ExtraMath;
import action.utility.HittedList;
import action.utility.enums.Direction;
import action.utility.form.Form.FormLoader;
import action.world.Collision;
import action.world.World;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader.Element;


/**
 * @author Andrea
 */
public class Projectile extends Area implements Mover {

    private Map<ProjectileMotionType, Motion> motions;
    /** Sound of the hit */
    private Sound sound;
    /** Vector with direction */
	private Vector2 dirV;
	/** Direction that the proj is facing */
    private Direction dir;
    /** Vector with movement displacement */
	private Vector2 movV = new Vector2(0f, 0f);
	/** Move speed */
    private float speed;
    /** Maximum distance that the proj can travel */
    private float range;
    /** Distance travelled */
    private float travelled = 0;
    /** How many entity can be hitten before disappearing (-2 infinite) */
    private int goThrough;
    /** ID of explosion (if not present: negative value) */
    private int explosion;
    /** List of entites hitted by the impact */
    private HittedList hitted = new HittedList();
    private Entity source;
	
    /**
     * Create a projectile
     */
	public Projectile(float x, float y, Vector2 dirV, World world, ProjectileInfo info, float speed,
			float range, Entity source, Impact impact){
		super(x, y, world, info.form, info.body, impact);
		this.dirV = dirV.nor();
		this.speed = speed;
		this.range = range;
		this.goThrough = info.goThrough;
		this.explosion = info.explosion;
		this.source = source;
		dangerZone = new DangerZone(new StaticHitBox(getShape()), impact.getBlockLevel(), null);
		
		/* Load motion */
    	motions = new EnumMap<ProjectileMotionType, Motion>(ProjectileMotionType.class);
    	for (Map.Entry<ProjectileMotionType, Integer> entry : info.motions.entrySet())
    		motions.put(entry.getKey(), Game.assets.getMotionLoader(entry.getValue()).load());
    	
    	/* Load sound */
    	sound = (info.sound != null) ? Game.assets.get(info.sound, Sound.class) : new EmptySound();
    	
    	/* Fix the starting direction */
		calibrateDirection();
		/* Initialize danger zone */
		updateDangerZone();
	}
	
	/**
	 * Create a projectile from an appropriate loader
	 */
	public Projectile(float x, float y, Vector2 dirV, World world, ProjectileLoader loader, Entity source, Impact impact) {
		this(x, y, dirV, world, Game.assets.getProjectileInfo(loader.projectile), loader.speed, loader.range, source, impact);
	}
	
	private boolean canExplode() {
		return explosion >= 0;
	}

	private Motion getMotion(ProjectileMotionType type) {
		return motions.get(type);
	}
	
	private void drawMotion(SpriteBatch batch, ProjectileMotionType type, float x, float y) {
		getMotion(type).draw(batch, dir, x, y);
	}
	
    private void updateMotion(ProjectileMotionType type, float delta) {
    	getMotion(type).update(delta);
    }
	
    private void resetMotion(ProjectileMotionType type) {
    	getMotion(type).restart();
    }
    
    private void rotateMotion(ProjectileMotionType type) { 	
    	getMotion(type).setRotation(dirV.angle() + GlobalVar.SHAPE_IMG_ANGLE_OFFSET);
    }
	
	private void computeDisplacement() {
		movV.set(dirV.x * speed, dirV.y * speed);
	}
	
	@Override
	protected void suicide() {
		super.suicide();
		if (canExplode())
			explode();
	}
	
	private void explode() {
		world.spawnEntity(new Explosion(getCenterX(), getCenterY(), world, Game.assets.getExplosionInfo(explosion), impact));
	}
	
	/**
	 * Try to damage the given targets
	 */
    private DamageResult damage(Array<Entity> targets) {
    	for (Entity target : targets)
    		if (!target.equals(source) && (target.isHittable(HitLevel.PROJECTILE)) && (!hitted.contains(target))) {
    			if (canExplode()) {
    				suicide();
    				return DamageResult.EXPLOSION;
    			}
    			target.takeImpact(impact.copy(new StaticHitBox(getShape(), dirV, form.getSize())));
    			playHitSound();
    			hitted.add(target);
    			if (goThrough >= 0)
    				goThrough--;
    			return DamageResult.IMPACT;
    		}
    	return DamageResult.NOTHING;
    }
    
    @Override
    public void update(float delta) {
    	computeDisplacement();
    	updateMotion(ProjectileMotionType.STANDARD, delta);
    	World.moveEntity(world, this, delta, false);
    	updateDangerZone();
    }
    
	@Override
	protected void drawGfx(SpriteBatch batch) {
		drawMotion(batch, ProjectileMotionType.STANDARD, position.x, position.y);
	}

	private void playHitSound() {
		sound.play(Game.sounds.getSfxVolume() * getWorld().getSoundIntensity(getCenterX(), getCenterY()));
	}
    
	@Override
	public void takeKnockback(Knockback knockback) {
		/* Add to the vector representing the movement (dir * speed) the vector of the effective knockback
		 * (scaled dir / mass factor)
		 */
		Vector2 v = dirV.scl(speed).add(knockback.getScaledDir().scl(1 / getMassFactor()));
		speed = v.len();
		
		/* The dir vector must always be normalized */
		dirV = v.nor();
		calibrateDirection();
	}
	
	/**
	 * Adjusts the current direction, body orientation, and animations starting from the
	 * current direction vector
	 */
	private void calibrateDirection() {
		resetMotion(ProjectileMotionType.STANDARD);
		dir = Direction.unitVectorToDir(dirV);
		rotateMotion(ProjectileMotionType.STANDARD);
		getForm().rotate(dirV.angle());
	}

	@Override
	public Vector2 getMovV() {
		return movV;
	}

	@Override
	public void setMovV(float x, float y) {
		movV.set(x, y);
	}

	@Override
	public boolean move(World world, float dx, float dy, boolean smooth) {
        Collision collision = new Collision(this, dx, dy, world, true);
        /* Move if it can, otherwise die */
        if (collision.canMove(goThrough != -1)) {
            updatePos(dx, dy);  
        }
        else {
        	/* If no target has been damaged it means that the projectile has hitted some map obstacle */
        	if (damage(collision.getCollidingEntities()) == DamageResult.NOTHING)
            	playHitSound();
        	suicide();
        	return false;
        }

        /* Entity collision check */
        if (collision.getCollidingEntities().size > 0) {	
        	/* Hit entities and see if it exploded */
        	if (damage(collision.getCollidingEntities()) == DamageResult.EXPLOSION) {
        		return false;
        	}

        	/* If it can't pierce anymore entities, die */
        	if (goThrough == -1) {
        		suicide();
        		return false;
        	}
        }
        
        /* If it travelled more than the range, die */
        travelled += Vector2.len(dx, dy);
        if (travelled >= range) {
        	suicide();
        	return false;
        }

		return true;
	}
	
	private void updateDangerZone() {
		/* Save old size */
		float oldSize = form.getSize();
		/* Compute edges as a point far behind the effective shape, to ensure blocking is done in the right direction */
		Vector2 edge = new Vector2(-dirV.x * GlobalVar.DANGER_START_DISTANCE + getCenterX(), 
				-dirV.y * GlobalVar.DANGER_START_DISTANCE + getCenterY());
		form.setSize(oldSize + dirV.len() * speed);
		dangerZone.setHitBox(new StaticHitBox(ExtraMath.copyShape(getShape()), edge, edge));
		form.setSize(oldSize);
	}
	
	
	
    /**
     * @author Andrea
     */
	public enum ProjectileMotionType {
		
		STANDARD;
		
	}
	
	
	/**
	 * Result of a damage application
	 * 
	 * @author Andrea
	 */
	private enum DamageResult {
		
		/** The projectile has exploded */
		EXPLOSION,
		/** The projectile has given an impact to a target */
		IMPACT,
		/** Nothing happened */
		NOTHING;
		
	}
	
    
    /**
     * @author Andrea
     */
    public static class ProjectileInfo {
    	
    	public final FormLoader form;
    	public final BodyInfo body;
        public final Map<ProjectileMotionType, Integer> motions;
        public final String sound;
        public final int goThrough;
        public final int explosion;
        
		public ProjectileInfo(FormLoader form, BodyInfo body, Map<ProjectileMotionType, Integer> motions, String sound, int goThrough,
				int explosion) {
			this.form = form;
			this.body = body;
			this.motions = motions;
			this.sound = sound;
			this.goThrough = goThrough;
			this.explosion = explosion;
		}

		public static ProjectileInfo loadFromXml(Element element) {
			return new ProjectileInfo(
					FormLoader.loadFromXml(element.getChildByName("form")),
					BodyInfo.loadFromXml(element.getChildByName("body")),
					loadMotions(element.getChildByName("motions")),
					element.getAttribute("sound", null),
					element.getIntAttribute("goThrough", 0),
					element.getIntAttribute("explosion", -1)
					);
		}
		
	    private static Map<ProjectileMotionType, Integer> loadMotions(Element element) {
	    	Map<ProjectileMotionType, Integer> map = new EnumMap<ProjectileMotionType, Integer>(ProjectileMotionType.class);
	    	Element child;
	        for (int i = 0; i < element.getChildCount(); i++) {
	        	child = element.getChild(i);
	        	map.put(ProjectileMotionType.valueOf(child.getAttribute("type")), child.getIntAttribute("id"));
	        }
	    	return map;
	    }

    }
    
    
    /**
     * @author Andrea
     */
    public static class ProjectileLoader {
    	
    	public final int projectile;
    	public final float speed;
    	public final float range;
	    public final List<DamageLoader> damages;
	    public final List<EffectLoader> effects;
	    public final KnockbackLoader knockback;
	    public final BlockLevel block;
	    public final float crushPower;
		public final boolean selective;
		
		public ProjectileLoader(int projectile, float speed, float range, List<DamageLoader> damages, List<EffectLoader> effects,
				KnockbackLoader knockback, BlockLevel block, float crushPower, boolean selective) {
			this.projectile = projectile;
			this.speed = speed;
			this.range = range;
			this.damages = damages;
			this.effects = effects;
			this.knockback = knockback;
			this.block = block;
			this.crushPower = crushPower;
			this.selective = selective;
		}
		
		public static ProjectileLoader loadFromXml(Element element) {
			return new ProjectileLoader(
					element.getIntAttribute("projectile"),
					element.getFloatAttribute("speed"),
					element.getFloatAttribute("range"),
	    			Database.loadDamages(element.getChildByName("damages")),
	    			Database.loadEffects(element.getChildByName("effects")),
	    			KnockbackLoader.loadFromXML(element.getChildByName("knockback")),
					BlockLevel.valueOf(element.getAttribute("block")),
					element.getFloatAttribute("crushPower"),
					element.getBooleanAttribute("selective", true)
					);
		}

    }
    
}
