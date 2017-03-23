package action.entity.being.creature;

import action.entity.being.creature.Stats.CreatureStatType;
import action.entity.being.creature.action.Action.ActionType;
import action.entity.being.creature.action.Block;
import action.entity.being.creature.action.Shoot;
import action.entity.enums.AttackType;
import action.entity.enums.BlockType;
import action.item.weapon.Attacker;
import action.utility.Body.BodyStatType;
import action.utility.Statistic;

/**
 * Class to reflect the personal stats and capabilities of a creature
 * 
 * @author Andrea
 */
public class Mirror {
	
	private Creature owner;
	
	public Mirror(Creature owner) {
		this.owner = owner;
	}
	
    public Statistic getStat(CreatureStatType type) {
    	return owner.getStatsMemory().getStat(type);
    }
    
    public Statistic getStat(BodyStatType type) {
    	return owner.getStats().getStat(type);
    }
	
	public ActionType currentAction() {
		return owner.getLogic().getAction().getType();
	}
	
	/**
	 * Get the cost of an attack. This method does not check the presence of that attack
	 */
	public float getAttackCost(AttackType type) {
		return owner.getInv().getAttackItem().getAttackCost(type);
	}
	
	/**
	 * Get the effective range of an attack from the approximated border of the creature. 
	 * This method does not check the presence of that attack
	 */
	public float getAttackRange(AttackType type) {
		return (owner.getArmReachH() + owner.getArmReachV()) / 2 +
				owner.getInv().getAttackItem().getRange(type) -
				((owner.getShape().getWidth() + owner.getShape().getHeight()) / 4);
	}

	public boolean hasAttack(AttackType type) {
		return owner.getStats().hasAttack(type);
	}
	
	/**
	 * @return True if the attack is ranged. This method does not check the presence of that attack
	 */
	public boolean isAttackRanged(AttackType type) {
		return owner.getInv().getAttackItem().getAttack(type).isRanged();
	}

	/**
	 * Get the cost of an block. This method does not check the presence of that block
	 */
	public float getBlockCost(BlockType type) {
		return owner.getInv().getBlockItem().getBlockCost(type);
	}
	
	/**
	 * @return True if the creature is effectively blocking
	 */
	public boolean isBlocking() {
		if (owner.getLogic().getAction().getType() == ActionType.BLOCK)
			return ((Block) owner.getLogic().getAction()).isBlocking();
		return false;
	}
	
	/**
	 * @return True if the creature is effectively blocking or starting a block 
	 */
	public boolean hasStartedBlock() {
		if (owner.getLogic().getAction().getType() == ActionType.BLOCK)
			return ((Block) owner.getLogic().getAction()).hasStartedBlock();
		return false;
	}
	
	/**
	 * @return True if the creature has charged the ranged attack
	 */
	public boolean isReadyToShoot() {
		if (owner.getLogic().getAction().getType() == ActionType.SHOOT)
			return ((Shoot) owner.getLogic().getAction()).isCharged();
		return false;
	}
	
	/**
	 * @see Attacker#hasRanged()
	 */
	public boolean isWeaponRanged() {
		return owner.getInv().getAttackItem().hasRanged();
	}
	
	/**
	 * @see Attacker#hasMelee()
	 */
	public boolean isWeaponMelee() {
		return owner.getInv().getAttackItem().hasMelee();
	}

	public boolean canBlock() {
		return owner.getStats().canBlock();
	}
	
}
