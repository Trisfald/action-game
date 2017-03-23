package action.entity.being.creature;

import action.combat.Impact;
import action.core.Game;
import action.entity.being.creature.Stats.CreatureStatType;
import action.entity.being.creature.inventory.Armor;
import action.entity.being.creature.inventory.Armor.ArmorInfo;
import action.entity.enums.AttackType;
import action.entity.enums.AttackType.AttackMotionType;
import action.entity.enums.BlockType;
import action.entity.enums.BlockType.BlockMotionType;
import action.item.Inventory;
import action.item.ItemStatusSlot;
import action.item.StatusItem;
import action.item.shield.Blocker;
import action.item.shield.Shield;
import action.item.shield.Shield.ShieldInfo;
import action.item.weapon.Attacker;
import action.item.weapon.Weapon;
import action.item.weapon.Weapon.WeaponInfo;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * A more complex inventory for creatures
 *
 * @author Andrea
 */
public class CreatureInventory extends Inventory {

	private Creature owner;
	/** Set of equipped protections */
	private Armor armor;
	/** Equipped weapon */
	private Weapon weapon;
	/** Equipped shield */
	private Shield shield;
	/** A natural weapon part of the creature's body */
	private Weapon innateWeapon;
	
	public CreatureInventory(Creature owner, CreatureInventoryInfo info) {
		super(info.inv);
		this.owner = owner;
		armor = new Armor(info.armorInfo, owner);
		loadSpecialItems(info);
	}
	
	/**
	 * Loads natural weapon, weapon and shield
	 */
	private void loadSpecialItems(CreatureInventoryInfo info) {
		WeaponInfo w = (WeaponInfo) Game.assets.getItemInfo(info.creatureWeapon);
		if (w != null)
			innateWeapon = new Weapon(w, owner, true);
		
		w = (WeaponInfo) Game.assets.getItemInfo(info.weapon);
		if (w != null)
			weapon = new Weapon(w, owner);
		
		ShieldInfo s = (ShieldInfo) Game.assets.getItemInfo(info.shield);
		if (s != null)
			shield = new Shield(s, owner);
	}
	
	public void loadWeapon(int i) {
		if (unloadWeapon()) {
			weapon = (Weapon) getList().get(i);
			getList().remove(i);
		}
	}
	
	public void loadShield(int i) {
		if (unloadShield()) {
			shield = (Shield) getList().get(i);
			getList().remove(i);
		}
	}
	
	private boolean unloadWeapon() {
		if (weapon != null && !weapon.isBusy()) {
			getList().add(weapon);
			weapon = null;
			return true;
		}
		return false;
	}
	
	private boolean unloadShield() {
		if (shield != null && !shield.isBusy()) {
			getList().add(shield);
			shield = null;
			return true;
		}
		return false;
	}
	
	public Weapon getWeapon() {
		return weapon;
	}
	
	public Shield getShield() {
		return shield;
	}
	
	public boolean hasWeapon() {
		return (getWeapon() != null);
	}
	
	public boolean hasShield() {
		return (getShield() != null);
	}
	
	/**
	 * @return The attack motion needed to perform the attack or null if the attack is impraticable
	 */
	public AttackMotionType canAttackStyle(AttackType type) {
		return getAttackItem().getAttackStyle(type);
	}
	
	/**
	 * @return The block motion needed to perform the block or null if the block is impraticable
	 */
	public BlockMotionType canBlockStyle(BlockType type) {
		return getBlockItem().getBlockStyle(type);
	}
	
	public void takeImpact(Impact impact) {
		armor.takeImpact(impact);
	}

	public void update(float delta) {
		if (hasShield()) {
			shield.update(delta);
			if (!shield.isAlive())
				shield = null;
		}
		if (hasWeapon()) {
			weapon.update(delta);
			if (!weapon.isAlive())
				weapon = null;
		}
		innateWeapon.update(delta);
		armor.update(delta);
	}
	
	/**
	 * @return The total weight of the equipped items
	 */
	private float getEquipWeight() {
		return armor.getWeight() + getWeaponWeight() + getShieldWeight() + getCreatureWeaponWeight();
	}
	
	/**
	 * @return Equipment mass factor
	 */
	public float getMassFactor() {
		return 1 + (getEquipWeight() / 100);
	}
	
	/**
	 * Formula: e^(weight / (CONSTITUTION/2 + STRENGTH/2 + 20))
	 * 
	 * @return The burden factor caused by the weight of the items in the inventory
	 */
	public float getBurdenFactor() {
		return (float) Math.pow(Math.E, getEquipWeight() / 
				(owner.getStats().getStat(CreatureStatType.CONSTITUTION).value() / 2 + 
						owner.getStats().getStat(CreatureStatType.STRENGTH).value() / 2 + 20));
	}
	
	private float getWeaponWeight() {
		if (hasWeapon())
			return weapon.getWeight();
		return 0;
	}
	
	private float getShieldWeight() {
		if (hasShield())
			return shield.getWeight();
		return 0;
	}
	
	private float getCreatureWeaponWeight() {
		return innateWeapon.getWeight();
	}
	
	public void removeToggleEffects() {
		if (hasShield())
			shield.removeToggleEffects();
		if (hasWeapon())
			weapon.removeToggleEffects();
		innateWeapon.removeToggleEffects();
		armor.removeToggleEffects();
	}
	
    public boolean hasActiveToggleEffects() {
    	return armor.hasActiveToggleEffects() || 
    			weapon.hasActiveToggleEffects() ||
    			shield.hasActiveToggleEffects();
    }
    
    public Attacker getAttackItem() {
    	if (hasWeapon())
    		return weapon;
    	return innateWeapon;
    }
    
    public Blocker getBlockItem() {
    	if (hasShield())
    		return shield;
    	if (hasWeapon())
    		return weapon;
    	return innateWeapon;
    }
	
    /**
     * @return The item in the given slot, or null if the slot is empty
     */
    public StatusItem getItem(ItemStatusSlot slot) {
    	switch (slot) {
    		case WEAPON:
    			return getWeapon();
    		case SHIELD:
    			return getShield();
    		default:
    			return armor.getProtection(slot.getProtection());
    	}
    }
    
	
	/**
	 * @author Andrea
	 */
	public static class CreatureInventoryInfo {
		
		/** All standard items in the inventory */
		public final InventoryInfo inv;
		public final int creatureWeapon;
		public final int weapon;
		public final int shield;
		public final ArmorInfo armorInfo;
		
		public CreatureInventoryInfo(InventoryInfo inv, int creatureWeapon, int weapon, int shield, ArmorInfo armorInfo) {
			this.inv = inv;
			this.creatureWeapon = creatureWeapon;
			this.weapon = weapon;
			this.shield = shield;
			this.armorInfo = armorInfo;
		}
		
		public static CreatureInventoryInfo loadFromXml(Element element) {
			return new CreatureInventoryInfo(
					InventoryInfo.loadFromXml(element.getChildByName("items")),
					element.getIntAttribute("innateWeapon", -1),
					element.getIntAttribute("weapon", -1),
					element.getIntAttribute("shield", -1),
					ArmorInfo.loadFromXml(element.getChildByName("armor"))
					);
		}
		
	}

}
