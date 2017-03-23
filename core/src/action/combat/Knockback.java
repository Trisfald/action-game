package action.combat;

import action.interfaces.GlobalVar;
import action.utility.Statistic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * @author Andrea
 */
public class Knockback {

	private Vector2 dir;
	private float speed;
	private float accel;
	public static final float DMG_VARIATION = 25;
	private static final float DMG_BASEMOD = 0.25f;
	
	/**
	 * Create a knobckback 
	 * @param loader
	 * @param dir Vector (must be normalized)
	 */
	public Knockback(KnockbackLoader loader, Vector2 dir) {
		this.dir = dir;
		this.speed = Statistic.randomizer(loader.speed, loader.variation);
		this.accel = Statistic.randomizer(loader.accel, loader.variation);
	}
	
	private Knockback(Vector2 dir, float speed, float accel) {
		this.dir = dir;
		this.speed = speed;
		this.accel = accel;
	}

	public Vector2 getDisplacement(float delta) {
		Vector2 v = new Vector2(dir);
		
		v.scl(speed * delta * GlobalVar.BASE_MOVESPEED);
		
		return v;
	}

	public float getDamage() {
		return speed * DMG_BASEMOD ;
	}
	
	public Vector2 getScaledDir() {
		return dir.scl(speed);
	}
	
	/**
	 * Reduces this knockback depending on the given mass
	 */
	public void reduce(float mass) {
		speed = speed / mass;
	}
	
	/**
	 * @return False if the speed reached 0
	 */
	public boolean update(float delta) {
		speed += accel * delta;
		if (speed <= 0)
			return false;
		return true;
	}
	
	/**
	 * @return A copy of the object
	 */
	public Knockback copy() {
		return new Knockback(dir.cpy(), speed, accel);
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class KnockbackLoader {
		
		public final float speed;
		public final float accel;
		public final float variation;
		
		public KnockbackLoader(float speed, float accel, float variation) {
			this.speed = speed;
			this.accel = accel;
			this.variation = variation;
		}	
		
		/**
		 * @param element Null allowed
		 * 
		 * @return A KnockbackLoader or null
		 */
		public static KnockbackLoader loadFromXML(Element element) {
			if (element == null)
				return null;

			return new KnockbackLoader(
					element.getFloatAttribute("speed"),
					element.getFloatAttribute("accel"),
					element.getFloatAttribute("variation")
					);
		}
		
	}
	
}
