package action.entity.being.inanimate;

import java.util.EnumMap;
import java.util.Map;

import action.animation.Motion;
import action.combat.Damage;
import action.combat.Impact;
import action.core.Game;
import action.entity.being.AbstractBeing;
import action.entity.enums.SimpleMotionType;
import action.utility.enums.Direction;
import action.utility.form.Form.FormLoader;
import action.world.World;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Template class for simple entities
 *
 * @author Andrea
 */
public abstract class Inanimate extends AbstractBeing {
    
    private String name;
    private Map<SimpleMotionType, Motion> motions;
    
    private final static Direction dir = Direction.CENTER;
        
    public Inanimate(float x, float y, World world, InanimateInfo info, String name) {
        super(x, y, info.form, world, null);
        
        /* Can have a custom name */
        if (name != null)
            this.name = name;
        else
            this.name = info.name;
        
        /* Load animations */
    	motions = new EnumMap<SimpleMotionType, Motion>(SimpleMotionType.class);
    	for (Map.Entry<SimpleMotionType, Integer> ani : info.idleAni.entrySet())
    		motions.put(ani.getKey(), Game.assets.getMotionLoader(ani.getValue()).load());
    }
    
    /**
     * Constructor with default name
     */
    public Inanimate(float x, float y, World world, InanimateInfo info) {
    	this(x, y, world, info, null);
    }
     
	public String getName() {
		return name;
	}
    
    private Motion getMotion(SimpleMotionType type) {
    	return motions.get(type);
    }
    
	protected void drawMotion(SpriteBatch batch, SimpleMotionType type, float x, float y) {
		getMotion(type).draw(batch, dir, x, y);
	}
	
	protected void updateMotion(SimpleMotionType type, float delta) {
		getMotion(type).update(delta);
	}
	
	@Override
	public void update(float delta) {
		updateMotion(SimpleMotionType.IDLE, delta);
	}

	@Override
	protected void drawGfx(SpriteBatch batch) {
		drawMotion(batch,SimpleMotionType.IDLE, position.x, position.y);
	}
    
    @Override
    public boolean isAlive() {
        return true;
    }
    
    @Override
	public void takeImpact(Impact impact) {
    	
    }
    
    @Override
	public boolean takeDamage(Damage damage) {
		return false;
    }
	
	@Override
	public float getMassFactor() {
		return 1;
	}
   
    
    /**
     * Info class for Inanimate
     *
     * @author Andrea
     */ 
    public static class InanimateInfo {
        
        public final FormLoader form;
        public final String name;
        public final Map<SimpleMotionType, Integer> idleAni;
        
		public InanimateInfo(FormLoader form, String name, Map<SimpleMotionType, Integer> idleAni) {
			this.form = form;
			this.name = name;
			this.idleAni = idleAni;
		}

    }
    
    
    /**
     * @author Andrea
     */
    public enum InanimateType {
    	
    	INFOPOST,
    	LOOT,
    	CHEST;
    	
    }

}
