package action.entity.being.creature.inventory;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import action.combat.BlockLevel;
import action.combat.Damage;
import action.combat.Damage.DamageType;
import action.combat.Impact;
import action.combat.effect.Effect;
import action.core.Game;
import action.entity.being.creature.Creature;
import action.item.Protection;
import action.item.Protection.ProtectionInfo;
import action.item.Protection.ProtectionSlot;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Armor system of a creature
 * 
 * @author Andrea
 */
public class Armor {

	Map<ProtectionSlot, Protection> parts = new EnumMap<ProtectionSlot, Protection>(ProtectionSlot.class);
	private static final BlockLevel BLOCK_LEVEL = BlockLevel.ARMOR;
	
	public Armor(ArmorInfo info, Creature owner) {
		/* For each part in the info, create an object protection and insert it in parts */
		for (Entry<ProtectionSlot, Integer> x : info.parts.entrySet()) {
			ProtectionInfo i = (ProtectionInfo) Game.assets.getItemInfo(x.getValue());
			if (i != null)
				parts.put(x.getKey(), new Protection(i, owner));
		}
	}
	
	public void equipPart(Protection part) {
		parts.put(part.getSlot(), part);
	}
	
	public Protection getProtection(ProtectionSlot slot) {
		return parts.get(slot);
	}
	
	public void takeImpact(Impact impact) {
		if (BLOCK_LEVEL.value() >= impact.getBlockLevel().value())
			if (impact.isSelective())
				takeImpactSelective(impact);
			else
				takeImpactTotal(impact);
	}
	
	/**
	 * Take an impact directed towards a single piece of protection
	 */
	private void takeImpactSelective(Impact impact) {
		Protection part = getProtection(ProtectionSlot.selectRndBased());
		if (part == null)
			return;
		part.takeImpact(impact);
	}
	
	/**
	 * Take an impact directed towards all parts
	 */
	private void takeImpactTotal(Impact impact) {
		/* Give damage to all parts */
		for (Iterator<Damage> iter = impact.getDamages().iterator(); iter.hasNext();) {
			Damage damage = iter.next();
			
			/* Give the original damage to all parts */
			takeDamageTotal(damage);
			/* Reduce damage using mean values */
			damage.reduce(getMeanResistance(damage.getDmgType()),
					getMeanReduction(damage.getDmgType()), true);
			/* Soak damage using mean values */
			damage.reduce(getMeanSoak(damage.getDmgType()));
			
			/* Remove damage if it has been totally nullified */
			if (!damage.exists())
				iter.remove();
		}
		
		/* Give effects to all parts */
		for (Iterator<Effect> iter = impact.getEffects().iterator(); iter.hasNext();) {
			Effect effect = iter.next();
			takeEffectTotal(effect);
			/* Make a single check for propagation */
			if (!effect.propagates())
				iter.remove();
		}
	}
	
	/**
	 * Give a damage to all parts
	 */
	private void takeDamageTotal(Damage damage) {
		for (Protection part : parts.values())
			/* Give a copy because original damage must not be altered */
			part.takeDamage(damage.copy());
	}
	
	/**
	 * Give the effect to all parts
	 */
	private void takeEffectTotal(Effect effect) {
		for (Protection part : parts.values())
			part.takeEffect(effect);
	}

	public void update(float delta) {
		for (Protection part : parts.values())
			part.update(delta);
		maintenance();
	}

	private void maintenance() {
        for (Iterator<Protection> iter = parts.values().iterator(); iter.hasNext();) {
        	if (!iter.next().isAlive())
        		iter.remove();
        }
	}
	
	public void removeToggleEffects() {
		for (Protection part : parts.values())
			part.removeToggleEffects();
	}
	
    public boolean hasActiveToggleEffects() {
    	boolean b = false;
		for (Protection part : parts.values())
			b = b || part.hasActiveToggleEffects();
		return b;
    }
    
    /**
     * @return The mean value of resistance considering all parts according to their importance
     */
    private float getMeanResistance(DamageType type) {
    	float total = 0;
		for (Entry<ProtectionSlot, Protection> x : parts.entrySet())
			if (x.getValue().isAlive())
				total += x.getValue().getBody().getResistanceValue(type) * x.getKey().weight();
		return total / ProtectionSlot.totalWeight();
    }
    
    /**
     * @return The mean value of reduction considering all parts according to their importance
     */
    private float getMeanReduction(DamageType type) {
    	float total = 0;
		for (Entry<ProtectionSlot, Protection> x : parts.entrySet())
			if (x.getValue().isAlive())
				total += x.getValue().getBody().getReductionValue(type) * x.getKey().weight();
		return total / ProtectionSlot.totalWeight();
    }
    
    /**
     * @return The mean value of soak considering all parts according to their importance
     */
    private float getMeanSoak(DamageType type) {
    	float total = 0;
		for (Entry<ProtectionSlot, Protection> x : parts.entrySet())
			if (x.getValue().isAlive())
				total += x.getValue().getBody().getSoakValue(type) * x.getKey().weight();
		return total / ProtectionSlot.totalWeight();
    }
    
    /**
     * @return The total weight of all armor parts
     */
    public float getWeight() {
    	float total = 0;
		for (Protection part : parts.values())
			total += part.getWeight();
		return total;
    }
    
	
	/**
	 * @author Andrea
	 */
	public static class ArmorInfo {
		
		public final Map<ProtectionSlot, Integer> parts;

		public ArmorInfo(Map<ProtectionSlot, Integer> parts) {
			this.parts = parts;
		}
		
		public static ArmorInfo loadFromXml(Element element) {
	    	Map<ProtectionSlot, Integer> map = new EnumMap<ProtectionSlot, Integer>(ProtectionSlot.class);
	    	Element child;
	        for (int i = 0; i < element.getChildCount(); i++) {
	        	child = element.getChild(i);
	        	map.put(ProtectionSlot.valueOf(child.getAttribute("slot")), child.getIntAttribute("id"));
	        }
	    	return new ArmorInfo(map);
		}
	}
	
}
