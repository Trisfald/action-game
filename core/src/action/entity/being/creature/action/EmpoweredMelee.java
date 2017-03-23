package action.entity.being.creature.action;

import java.util.List;

import action.entity.Entity;
import action.entity.being.creature.Creature;
import action.entity.being.creature.Graphics;
import action.entity.being.creature.Logic;
import action.entity.being.creature.Stats;
import action.entity.enums.AttackState;
import action.entity.enums.AttackType;
import action.item.weapon.Attacker;
import action.spell.Spell;

public class EmpoweredMelee extends Melee {

	private boolean active = true;
	private boolean chargeSuccess = false;
	private Spell spell;
	
	public EmpoweredMelee(Creature owner, Graphics gfx, Logic logic, Stats stats, AttackType attackType, 
			List<Entity> targets, Attacker item, Spell spell) {
		super(owner, gfx, logic, stats, attackType, targets, item);
		setState(gfx, logic, AttackState.CHARGE);
		actionType = ActionType.EMPOWERED_MELEE;
		this.spell = spell;
	}

	@Override
	public void initialize() {
		active = false;
	}
	
	@Override
	protected void updateStateCharge(Graphics gfx, Logic logic, Stats stats) {
		/* Set the global charge meter for the UI */
		stats.setChargeProgress(timer.getLength() / spell.getChargeTime());
		
		if ((active) && (timer.expired(spell.getChargeTime()))) {
			setState(gfx, logic, AttackState.SET_UP);
			chargeSuccess = true;
			stats.setChargeProgress(-1);
		}
		if (!active) {
			setState(gfx, logic, AttackState.SET_UP);
			stats.setChargeProgress(-1);
		}
	}
	
	public void signalActivity() {
		active = true;
	}
	
    @Override
	protected void executeHit(Logic logic, Stats stats) {
    	super.executeHit(logic, stats);
    	if (chargeSuccess)
    		spell.cast(hitBox.getCentralPosition().x, hitBox.getCentralPosition().y, logic.getDir().getVector(), owner.getWorld(), owner, stats);
    }
	
}
