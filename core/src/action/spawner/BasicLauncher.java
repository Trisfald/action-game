package action.spawner;

import java.util.ArrayList;
import java.util.List;

import action.combat.Damage;
import action.combat.Damage.DamageLoader;
import action.combat.Knockback;
import action.combat.Knockback.KnockbackLoader;
import action.combat.effect.Effect;
import action.combat.effect.Effect.EffectLoader;
import action.entity.Entity;
import action.interfaces.StatsMemory;
import action.world.World;

import com.badlogic.gdx.math.Vector2;

public abstract class BasicLauncher implements Spawner {

	protected float x;
	protected float y;
	/** Original direction */
	protected Vector2 dirV;
	/** The source generating this launcher or null */
	protected Entity source;
	protected World world;
	
	public BasicLauncher(float x, float y, Vector2 dirV, Entity source, World world) {
		this.x = x;
		this.y = y;
		this.dirV = dirV;
		this.source = source;
		this.world = world;
	}
	
    public static List<Damage> computeDamages(List<DamageLoader> loader, StatsMemory stats) {
    	List<Damage> damages = new ArrayList<Damage>();
    	
    	for (DamageLoader x : loader)	
    		damages.add(Damage.computeDamage(stats, x));
    	
    	return damages;
    }
    
    public static List<Effect> computeEffects(List<EffectLoader> loader, StatsMemory stats) {
    	List<Effect> effects = new ArrayList<Effect>();
    	
        for (EffectLoader x : loader) {
        	if (Math.random() < x.probability)
        		effects.add(x.load());
        }
        return effects;
    }
    
    public static Knockback computeKnockback(KnockbackLoader loader, Vector2 dir) {
    	if (loader == null)
    		return null;
    	return new Knockback(loader, dir);	
    }
	
}
