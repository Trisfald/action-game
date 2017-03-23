package action.entity.being.creature.action;

import action.combat.Impact;
import action.entity.being.creature.Creature;
import action.entity.being.creature.CreatureInventory;
import action.entity.being.creature.Graphics;
import action.entity.being.creature.Logic;
import action.entity.being.creature.Stats;
import action.entity.being.creature.Stats.CreatureStatType;
import action.entity.enums.MagicSkill;
import action.entity.enums.SimpleMotionType;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Abstract class to manage actions
 *
 * @author Andrea
 */
public abstract class Action {

	protected Creature owner;
	protected ActionType actionType;
	
	protected static final float STAMINA_REGEN_BASEMOD = 0.075f;
	protected static final float FOCUS_REGEN_BASEMOD = 0.075f;
	protected static final float SPECIAL_DECAY = 0.5f;
	
	public Action(Creature owner, ActionType actionType) {
		this.actionType = actionType;
	}

	public ActionType getType() {
		return this.actionType;
	}
	
	public void initialize() {
		
	}
	
	public final void update(Graphics gfx, Logic logic, Stats stats, CreatureInventory inv, float delta) {
		maintenance(stats, delta);
		updateLogic(gfx, logic, stats, inv, delta);
		updateGfx(gfx, logic, stats, inv, delta);
	}
	
	/**
	 * Manage the cost/recover of mana and stamina
	 */
	protected void maintenance(Stats stats, float delta) {
		recoverStamina(stats, STAMINA_REGEN_BASEMOD, delta);
		recoverFocus(stats, FOCUS_REGEN_BASEMOD, delta);
	}
	
	protected void updateLogic(Graphics gfx, Logic logic, Stats stats, CreatureInventory inv, float delta) {
		
	}
	
	protected void updateGfx(Graphics gfx, Logic logic, Stats stats, CreatureInventory inv, float delta) {

	}

	public void conclude(Graphics gfx, Logic logic, CreatureInventory inv) {

	}
	
	public void updateRoute(Logic logic, Graphics gfx, Vector2 dir, float speed) {
		
	}
	
	public void drawBody(SpriteBatch batch, Graphics gfx, float x, float y) {
		gfx.drawSimple(batch, SimpleMotionType.IDLE, x, y);
	}
	
	public void takeImpact(Logic logic, Stats stats, Impact impact) {
		
	}
	
	protected void recoverStamina(Stats stats, float percentmod, float delta) {
		stats.getStat(CreatureStatType.STAMINA).recoverValuebyCurrent(
				percentmod * stats.getStat(CreatureStatType.STAMINA_REGEN).value(), 0.5f, delta);
	}
	
	protected void recoverFocus(Stats stats, float percentmod, float delta) {
		/* For standard magic users, recover normally */
		if (stats.getMagicSkill() == MagicSkill.STANDARD)
			stats.getStat(CreatureStatType.SPECIAL).recoverValuebyMaximum(
					percentmod * stats.getStat(CreatureStatType.SPECIAL_REGEN).value(), delta);
		
		/* For special magic users, the focus must try to be around the balance value */
		else if (stats.getMagicSkill() == MagicSkill.SPECIAL) {
			
			//float balance = stats.getStat(CreatureStatType.FOCUS_BALANCE).value();
			float balance = 1;
			
			if (stats.getStat(CreatureStatType.SPECIAL).value() < balance) {
				/* Focus less than balance, naturally recover */
				stats.getStat(CreatureStatType.SPECIAL).recoverValuebyMaximum(
						percentmod * stats.getStat(CreatureStatType.SPECIAL_REGEN).value(), balance, delta);
			}
			else {
				/* Focus higher than balance, check the combat timer. If it's expired, slowly decrease the focus */
				if (stats.getCombatTimer().expired()) 
					stats.getStat(CreatureStatType.SPECIAL).decayValuebyMaximum(SPECIAL_DECAY, balance, delta);
			}
		}
	}
	
	protected void updateStdMotions(Graphics gfx, Logic logic, CreatureInventory inv, SimpleMotionType type, float delta) {
		gfx.updateSimple(type, delta);
	}
	
	protected void resetStdMotions(Graphics gfx, Logic logic, CreatureInventory inv, SimpleMotionType type) {
		gfx.resetSimple(type);
	}
	
	public float getKnockbackResist() {
		return 1;
	}
	
	/**
	 * @return True if the action is currently in a 'recover' phase
	 */
	public boolean isRecovering() {
		return false;
	}
	
	
	
	/**
	 * Enumeration of all the possible action types
	 *
	 * @author Andrea
	 */
	public enum ActionType {
		
		IDLE,
		WALK,
		TALK,
		MELEE,
		EMPOWERED_MELEE,
		BLOCK,
		HOLD, 
		DODGE,
		SHOOT, 
		DEATH;

	}

}
