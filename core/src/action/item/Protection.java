package action.item;

import java.util.Iterator;
import java.util.Random;

import action.combat.Alterable;
import action.combat.Damage;
import action.combat.Impact;
import action.combat.effect.ChargeTrigger;
import action.combat.effect.Effect;
import action.combat.effect.EffectManager;
import action.entity.being.creature.Creature;
import action.utility.Body;
import action.utility.Body.BodyInfo;
import action.utility.Body.BodyStatType;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * @author Andrea
 */
public class Protection extends Item implements Alterable, StatusItem {

	private Body body;
	private ProtectionSlot slot;
	private EffectManager effectManager = new ProtectionEffectManager(this);
	private Creature owner;
	
	public Protection(ProtectionInfo info, Creature owner) {
    	super(info);
    	this.body = new Body(info.body);
    	this.slot = info.slot;
    	this.owner = owner;
	}
	
	public Protection(ProtectionInfo info) {
		this(info, null);
	}
	
	public Body getBody() {
		return body;
	}
	
	public ProtectionSlot getSlot() {
		return slot;
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
				body.getReductionValue(damage.getDmgType()), true);
		/* Soak damage and update HP */
		body.getStat(BodyStatType.HP).decreaseValue(damage.reduce(
				/* Can't soak more than HP */
				Math.min(body.getSoakValue(damage.getDmgType()), body.getStat(BodyStatType.HP).value())));
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
	
	public boolean isAlive() {
		return body.isAlive();
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
    
    /**
     * Take a damage but do not reduce it
     */
    public void sufferDamage(Damage damage) {
		body.getStat(BodyStatType.HP).decreaseValue(
				/* Min between damage power and soak */
				Math.min(body.getSoakValue(damage.getDmgType()), damage.getPower()));
    }
    
    @Override
	public float getHpRatio() {
    	return body.getStat(BodyStatType.HP).ratio();
    }
	
	
	/**
	 * @author Andrea
	 */
	public static class ProtectionInfo extends ItemInfo {
		
		public final BodyInfo body;
		public final ProtectionSlot slot;
		
		public ProtectionInfo(ItemInfo itemInfo, BodyInfo body, ProtectionSlot slot) {
			super(itemInfo);
			this.body = body;
			this.slot = slot;
		}
		
		public static ProtectionInfo loadFromXml(Element element) {
		    return new ProtectionInfo(
		    		ItemInfo.loadFromXml(element),
		    		BodyInfo.loadFromXml(element.getChildByName("body")),
		    		ProtectionSlot.valueOf(element.getAttribute("slot"))
		    		);
		}
	}

	
	/**
	 * @author Andrea
	 */
	private class ProtectionEffectManager extends EffectManager {
		
		private ProtectionEffectManager(Protection owner) {
			super(owner);
		}
		
	}
	
	
	/**
	 * @author Andrea
	 */
	public enum ProtectionSlot {
		
		TORSO(8),
		LEGS(0),
		ARMS(0),
		HEAD(0);
		
		private final int weight;
		
		ProtectionSlot(int weight) {
			this.weight = weight;
		}
		
		public int weight() {
			return weight;
		}
		
		public static int totalWeight() {
			return ProtectionSlot.TORSO.weight +
					ProtectionSlot.ARMS.weight +
					ProtectionSlot.HEAD.weight +
					ProtectionSlot.LEGS.weight ;
		}
		
		public static ProtectionSlot selectRndBased() {
			Random randomGenerator = new Random();
			int randomInt = randomGenerator.nextInt(ProtectionSlot.totalWeight()) + 1;
			if (randomInt <= TORSO.weight)
				return TORSO;
			else if ((randomInt > TORSO.weight) && (randomInt <= (TORSO.weight + LEGS.weight)))
				return LEGS;
			else if ((randomInt > (TORSO.weight + LEGS.weight)) && (randomInt <= (TORSO.weight + LEGS.weight + ARMS.weight)))
				return ARMS;
			else
				return HEAD;
		}
		
	}

}
