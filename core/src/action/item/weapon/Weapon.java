package action.item.weapon;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import action.combat.BlockLevel;
import action.combat.Damage;
import action.combat.Damage.DamageLoader;
import action.combat.Impact;
import action.combat.Knockback.KnockbackLoader;
import action.combat.effect.BonusDamage;
import action.combat.effect.BonusEffect;
import action.combat.effect.ChargeTrigger;
import action.combat.effect.Effect;
import action.combat.effect.Effect.EffectLoader;
import action.combat.effect.EffectManager;
import action.database.Database;
import action.entity.being.creature.Creature;
import action.entity.enums.AttackType;
import action.entity.enums.AttackType.AttackMotionType;
import action.entity.enums.BlockType;
import action.entity.enums.BlockType.BlockMotionType;
import action.item.Item;
import action.item.StatusItem;
import action.item.shield.Blocker;
import action.item.shield.ShieldBlock;
import action.item.shield.ShieldBlock.ShieldBlockInfo;
import action.item.weapon.WeaponAttack.WeaponAttackInfo;
import action.spawner.SpawnerInfo;
import action.utility.Body;
import action.utility.Body.BodyInfo;
import action.utility.Body.BodyStatType;
import action.utility.enums.Direction;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * @author Andrea
 */
public class Weapon extends Item implements Attacker, Blocker, StatusItem {

	private Creature owner;
	private Body body;
    private Map<AttackType, WeaponAttack> attacks;
    private Map<BlockType, ShieldBlock> blocks;
    private WeaponAttack currentAttack;
    private ShieldBlock currentBlock;
	private WeaponEffectManager effectManager = new WeaponEffectManager(this);
	/** True if the weapon is part of the creature's body */
	private boolean innate;
	/** Indicates if the weapon is in use */
    private boolean busy;
	
	private float damageMultiplier = 1f;
	private float crushMultiplier = 1f;
	private float speedMultiplier = 1f;
    private List<DamageLoader> damages = new ArrayList<DamageLoader>();
    private List<EffectLoader> effects = new ArrayList<EffectLoader>();
	
	public Weapon(WeaponInfo info, Creature owner, boolean creature) {
		super(info);
		this.owner = owner;
		this.innate = creature;
    	this.body = new Body(info.body);
		
        attacks = new EnumMap<AttackType, WeaponAttack>(AttackType.class);
        for (int i = 0; i < info.attacks.length; i++)
        	attacks.put(info.attacks[i].type, new WeaponAttack(info.attacks[i]));
        
        blocks = new EnumMap<BlockType, ShieldBlock>(BlockType.class);
        for (int i = 0; i < info.blocks.length; i++)
        	blocks.put(info.blocks[i].type, new ShieldBlock(info.blocks[i]));
	}
	
	/**
	 * Create a weapon without owner
	 */
	public Weapon(WeaponInfo info) {
		this(info, null, false);
	}
	
	/**
	 * Create a stand alone weapon
	 */
	public Weapon(WeaponInfo info, Creature owner) {
		this(info, owner, false);
	}
	
    public boolean isBusy() {
    	return busy;
    }
    
    public void setBusy(boolean b) {
    	this.busy = b;
    }
	
	public Body getBody() {
		return body;
	}
	
	public boolean canAttack(AttackType type) {
		return (getAttack(type) != null);
	}
	
	@Override
	public AttackMotionType getAttackStyle() {
		return currentAttack.getStyle();
	}
	
	@Override
	public AttackMotionType getAttackStyle(AttackType type) {
		if (canAttack(type))
			return getAttack(type).getStyle();
		return null;
	}
    
    @Override
	public void resetAttack(Direction dir) {
    	setBusy(false);
    }
    
    @Override
	public WeaponAttack getAttack(AttackType attack) {
    	return attacks.get(attack);
    }
    
    @Override
	public void setCurrentAttack(AttackType attack) {
    	currentAttack = getAttack(attack);
		setBusy(true);
    }
    
    @Override
	public int getAttackHitBoxWidth() {
    	return currentAttack.getHitBoxWidth();
    }
    
    @Override
	public int getAttackHitBoxLength() {
    	return currentAttack.getHitBoxLength();
    }
    
    @Override
	public int getAttackHitBoxLength(AttackType type) {
    	return getAttack(type).getHitBoxLength();
    }
    
    @Override
	public List<EffectLoader> getAttackEffects() {
    	List<EffectLoader> list = new ArrayList<EffectLoader>(currentAttack.getAlters());
    	list.addAll(effects);
    	return list;
    }
    
    @Override
	public int getAttackTargets() {
    	return currentAttack.getAttackTargets();
    }
    
    @Override
	public List<DamageLoader> getDamages() {
    	List<DamageLoader> list = new ArrayList<DamageLoader>(currentAttack.getDamages());
    	list.addAll(damages);
    	
    	/* With a special damage multiplier modify the list  */
    	if (damageMultiplier != 1)
	    	for (DamageLoader x : list)
	    		x.multiplyPower(damageMultiplier);
    	return list;
    		
    }

	@Override
	public float getCrushPower() {
		return currentAttack.getCrushPower() * crushMultiplier;
	}
	
	@Override
	public float getDashLength() {
		return currentAttack.getDashLength();
	}
	
	@Override
	public float getDashLength(AttackType type) {
		return getAttack(type).getDashLength();
	}
	
	@Override
	public float getAttackSpeed() {
		return currentAttack.getSpeed() * speedMultiplier;
	}
	
	@Override
	public BlockLevel getBlockNeeded() {
		return currentAttack.getBlockLevel();
	}
	
	@Override
	public BlockLevel getBlockCapability() {
		return BlockLevel.WEAPON;
	}
    
	@Override
	public boolean isAlive() {
		return body.isAlive();
	}
	
	@Override
	public float getAttackCost(AttackType type) {
		return getAttack(type).getCost();
	}
	
	@Override
	public KnockbackLoader getKnockback() {
		return currentAttack.getKnockback();
	}
	
	@Override
	public void takeImpact(Impact impact) {
		for (Iterator<Damage> iter = impact.getDamages().iterator(); iter.hasNext() && isAlive();) {
			if (takeDamage(iter.next()))
				iter.remove();
		}
		for (Iterator<Effect> iter = impact.getEffects().iterator(); iter.hasNext() && isAlive();) {
			Effect effect = iter.next();
			takeEffect(effect);
			if (!effect.propagates())
				iter.remove();
		}
	}

	@Override
	public boolean takeDamage(Damage damage) {
		/* Reduce the damage */
		damage.reduce(body.getResistanceValue(damage.getDmgType()), 
				body.getReductionValue(damage.getDmgType()), false);
		
		/* Soak and absorb only if it's not an innate weapon */
		if (!innate) {
			/* Soak damage and update HP (VERSION WITH TAKE DAMAGE)
			body.getStat(BodyStatType.HP).decreaseValue(damage.reduce(
					/* Can't soak more than HP 
					Math.min(body.getSoakValue(damage.getDmgType()), body.getStat(BodyStatType.HP).value()))); */
			/* Soak but ignore damage taken */
			damage.reduce(body.getSoakValue(damage.getDmgType()));
		}
		
		return !damage.exists();
	}
		
	@Override
	public void takeEffect(Effect effect) {
    	if (!body.isImmune(effect))
    		effectManager.add(effect);
	}
	
	@Override
	public void takeEffect(Effect effect, AlterationTarget target) {
		takeEffect(effect);
	}
	
	@Override
	public void apply(Effect effect) {
		effectManager.apply(effect);
	}

	@Override
	public void remove(Effect effect) {
		effectManager.remove(effect);
	}
	
	public void update(float delta) {
		effectManager.update(delta);
	}
	
	@Override
	public void consumeCharge(ChargeTrigger trigger) {
		effectManager.consumeCharge(trigger);
	}
	
	@Override
	public boolean sustainEffect(float cost) {
		if (owner == null)
			return false;
		else
			return owner.sustainEffect(cost);
	}

	@Override
	public void removeToggleEffects() {
		effectManager.removeToggleEffects();
	}
	
	@Override
    public boolean hasActiveToggleEffects() {
    	return effectManager.hasActiveToggleEffects();
    }
	
	@Override
	public void setCurrentBlock(BlockType block) {
    	currentBlock = getBlock(block);
		setBusy(true);
	}
	
    private ShieldBlock getBlock(BlockType block) {
    	return blocks.get(block);
    }

	@Override
	public BlockMotionType getBlockStyle() {
		return currentBlock.getStyle();
	}

    @Override
	public int getBlockHitBoxWidth() {
    	return currentBlock.getHitBoxWidth();
    }
    
    @Override
	public int getBlockHitBoxLength() {
    	return currentBlock.getHitBoxLength();
    }

	@Override
	public float getBlockSpeed() {
		return currentBlock.getSpeed() * speedMultiplier;
	}

    @Override
	public void resetBlock(Direction dir) {
    	setBusy(false);
    }

	@Override
	public float getBlockCost(BlockType type) {
		return getBlock(type).getCost();
	}
	
	@Override
	public BlockMotionType getBlockStyle(BlockType type) {
		if (canBlock(type))
			return getBlock(type).getStyle();
		return null;
	}

	@Override
	public boolean canBlock(BlockType type) {
		return (getBlock(type) != null);
	}
	
	@Override
	public float getCrushResist() {
		return currentBlock.getCrushResist();
	}
	
    @Override
	public float getHpRatio() {
    	return body.getStat(BodyStatType.HP).ratio();
    }

	@Override
	public SpawnerInfo getSpawner() {
		return currentAttack.getSpawner();
	}
	
	@Override
	public boolean hasRanged() {
		for (WeaponAttack x : attacks.values())
			if (x.isRanged())
				return true;
		return false;
	}

	@Override
	public boolean hasMelee() {
		for (WeaponAttack x : attacks.values())
			if (!x.isRanged())
				return true;
		return false;
	}
	
	@Override
	public float getRange(AttackType type) {
		return getAttack(type).getRange();
	}
	
	@Override
	public float getChargeTime() {
		return currentAttack.getChargeTime();
	}

	
	
	/**
	 * @author Andrea
	 */
	public static class WeaponInfo extends ItemInfo {
		
		public final BodyInfo body;
        public final WeaponAttackInfo[] attacks;
        public final ShieldBlockInfo[] blocks;
        
		public WeaponInfo(ItemInfo itemInfo, BodyInfo body, WeaponAttackInfo[] attacks, ShieldBlockInfo[] blocks) {
			super(itemInfo);
			this.body = body;
			this.attacks = attacks;
			this.blocks = blocks;
		}
		
		public static WeaponInfo loadFromXml(Element element) {
		    return new WeaponInfo(
		    		ItemInfo.loadFromXml(element),
		    		BodyInfo.loadFromXml(element.getChildByName("body")),
		    		loadAttacks(element.getChildByName("attacks")),
		    		loadBlocks(element.getChildByName("blocks"))
		    		);
		}
		
		public static WeaponAttackInfo[] loadAttacks(Element element) {
			Element child;
			WeaponAttackInfo[] attacks = new WeaponAttackInfo[element.getChildCount()];
		    for (int i = 0; i < element.getChildCount(); i++) {
		    	child = element.getChild(i);
		    	attacks[i] = new WeaponAttackInfo(
		    			AttackType.valueOf(child.getAttribute("type")),
		    			AttackMotionType.valueOf(child.getAttribute("style")),
		    			BlockLevel.valueOf(child.getAttribute("level")),
		    			Database.loadDamages(child.getChildByName("damages")),
		    			Database.loadEffects(child.getChildByName("effects")),
		    			KnockbackLoader.loadFromXML(child.getChildByName("knockback")),
		    			child.getFloatAttribute("crushPower"),
		    			child.getFloatAttribute("dash"),
		    			child.getFloatAttribute("speed"),
		    			child.getFloatAttribute("charge"),
		    			child.getIntAttribute("HBwidth"),
		    			child.getIntAttribute("HBlength"),
		    			child.getIntAttribute("targets"),
		    			child.getFloatAttribute("cost"),
			            SpawnerInfo.loadFromXml(child.getChildByName("spawner"))
		    			);
		    }
		    return attacks;
		}

		public static ShieldBlockInfo[] loadBlocks(Element element) {
			Element child;
		    ShieldBlockInfo[] blocks = new ShieldBlockInfo[element.getChildCount()];
		    for (int i = 0; i < element.getChildCount(); i++) {
		    	child = element.getChild(i);
		    	blocks[i] = new ShieldBlockInfo(
		    			BlockType.valueOf(child.getAttribute("type")),
		    			BlockMotionType.valueOf(child.getAttribute("style")),
		    			child.getFloatAttribute("crushResist"),
		    			child.getFloatAttribute("speed"),
		    			child.getIntAttribute("HBwidth"),
		    			child.getIntAttribute("HBlength"),
		    			child.getFloatAttribute("cost")
		    			);
		    }
		    return blocks;
		}
	}
	
	
	/**
	 * @author Andrea
	 */
	private class WeaponEffectManager extends EffectManager {
		
		private WeaponEffectManager(Weapon owner) {
			super(owner);
		}
		
	    @Override
		protected void applyBonusDmg(BonusDamage bonus) {
			damages.add(bonus.getBonus());
		}
		
	    @Override
		public void removeBonusDmg(BonusDamage bonus) {
			damages.remove(bonus.getBonus());
		}
		
	    @Override
		public void applyBonusEffect(BonusEffect bonus) {
			Weapon.this.effects.add(bonus.getBonus());
		}
		
	    @Override
		public void removeBonusEffect(BonusEffect bonus) {
	    	Weapon.this.effects.remove(bonus.getBonus());
		}
		
	}

}