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
import action.utility.Body.BodyInfo;
import action.utility.enums.Direction;
import action.utility.form.Form.FormLoader;
import action.utility.timer.Timer;
import action.world.Collision;
import action.world.World;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;


/**
 * @author Andrea
 */
public class Zone extends Area {

	/** All motions */
    private Map<ZoneMotionType, Motion> motions;
    /** Tells if the zone blocks movement of other entities */
    private boolean blockMov;
    /** Timer for how much time must pass before the zone is effectively active */
    private Timer setUp;
    
    private final static Direction dir = Direction.CENTER;
	
	public Zone(float x, float y, World world, ZoneInfo info, Impact impact, int setUpTime, float growth) {
		super(x, y, world, info.form, info.body, impact);
		blockMov = info.blockMov;
		setUp = new Timer(setUpTime);
		dangerZone = new DangerZone(new StaticHitBox(getShape()), impact.getBlockLevel(), null, false);
		updateDangerZone();
		
    	motions = new EnumMap<ZoneMotionType, Motion>(ZoneMotionType.class);
    	for (Map.Entry<ZoneMotionType, Integer> ani : info.motions.entrySet())
    		motions.put(ani.getKey(), Game.assets.getMotionLoader(ani.getValue()).load());
	}
	
    private Motion getMotion(ZoneMotionType type) {
    	return motions.get(type);
    }
    
	public void drawMotion(SpriteBatch batch, ZoneMotionType type, float x, float y) {
		getMotion(type).draw(batch, dir, x, y);
	}
	
	@Override
	protected void drawGfx(SpriteBatch batch) {
		if (setUp.expired())
			drawMotion(batch, ZoneMotionType.IDLE, position.x, position.y);
	}
	
	@Override
	public boolean isBlockingMov() {
		return blockMov;
	}
	
    @Override
    public void update(float delta) {
    	/* Check for set up */
    	setUp.update(delta);
    	
    	checkCollisions(world);
    	updateDangerZone();
	}
    
    private void checkCollisions(World world) {
    	Collision collision = new Collision(this, 0, 0, world, true);
    	damage(collision.getCollidingEntities());
    }
    
    private void damage(Array<Entity> targets) {
    	for (Entity x : targets)
    		if ((x.isHittable(HitLevel.ZONE)))
    			x.takeImpact(impact.copy(new StaticHitBox(getShape())));
    }
    
    private void updateDangerZone() {
		if (setUp.expired())
			dangerZone.setActive(true);
		else
			return;
		dangerZone.setHitBox(new StaticHitBox(getShape()));
    }
    
    
    
    /**
     * @author Andrea
     */
    public enum ZoneMotionType {
    	
    	SET_UP,
    	IDLE,
    	EXPLOSION;
    	
    }
    
    
	/**
	 * @author Andrea
	 */
	public static class ZoneInfo {
		
    	public final FormLoader form;
    	public final BodyInfo body;
        public final Map<ZoneMotionType, Integer> motions;
        public final boolean blockMov;
        
		public ZoneInfo(FormLoader form, BodyInfo body, Map<ZoneMotionType, Integer> motions, boolean blockMov) {
			this.form = form;
			this.body = body;
			this.motions = motions;
			this.blockMov = blockMov;
		}
    	
	}
	
}
