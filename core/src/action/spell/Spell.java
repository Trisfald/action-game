package action.spell;

import java.util.List;

import action.combat.effect.Effect.EffectLoader;
import action.database.Database;
import action.entity.Entity;
import action.entity.being.creature.Stats;
import action.spawner.SpawnerInfo;
import action.world.World;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * @author Andrea
 */
public class Spell {

	private final int id;
	private SpellType type;
	private SpellCategory category;
	/** List of self affecting effects */
	private List<EffectLoader> effects;
	private SpawnerInfo spawner;
	private float chargeTime;
	/** Focus/Mana spent to cast */
	private float cost;
	
	public Spell(int id, SpellType type, SpellCategory category, List<EffectLoader> effects, SpawnerInfo spawner, float chargeTime, float cost) {
		this.id = id;
		this.type = type;
		this.category = category;
		this.effects = effects;
		this.spawner = spawner;
		this.chargeTime = chargeTime;
		this.cost = cost;
	}
	
	/**
	 * Cast only the self effects
	 */
	public void cast(Entity caster) {
		for (EffectLoader x : effects)
			caster.takeEffect(x.load(), x.target);
	}
	
	/**
	 * Cast the whole spell
	 */
	public void cast(float x, float y, Vector2 dirV, World world, Entity caster, Stats stats) {
		cast(caster);
		if (spawner != null)
			world.spawnSpawner(spawner.spawn(x, y, dirV, world, caster, stats));
	}
	
	public int getId() {
		return id;
	}
	
	public SpellType getType() {
		return type;
	}
	
	public SpellCategory getCategory() {
		return category;
	}
	
	public float getChargeTime() {
		return chargeTime;
	}
	
	public float getCost() {
		return cost;
	}
	
	
	public static Spell loadFromXml(Element element) {
		return new Spell(
        		element.getIntAttribute("id"),
                SpellType.valueOf(element.getAttribute("type")),
                SpellCategory.valueOf(element.getAttribute("category")),
    			Database.loadEffects(element.getChildByName("effects")),
    			SpawnerInfo.loadFromXml(element.getChildByName("spawner")),
    			element.getFloatAttribute("chargetime"),
    			element.getFloatAttribute("cost")
        		);
	}
	
	
	/**
	 * @author Andrea
	 */
	public enum SpellType {
		
		FIRE,
		ICE,
		LIGHTNING,
		WIND;
		
	}
	
	
	/**
	 * @author Andrea
	 */
	public enum SpellCategory {
		
		EMPOWER_WEAK(0),
		EMPOWER_STRONG(1),
		QUICKCAST(2),
		SPELLCRAFT(3);
		
		private final int id;
		
		SpellCategory(int id) {
			this.id = id;
		}

		public int id() {
			return id;
		}
		
	}
}
