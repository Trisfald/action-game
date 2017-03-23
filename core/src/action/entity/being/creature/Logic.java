package action.entity.being.creature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import action.combat.Damage;
import action.combat.Damage.DamageType;
import action.combat.Impact;
import action.combat.Knockback;
import action.entity.being.creature.action.Action;
import action.entity.being.creature.action.Action.ActionType;
import action.entity.being.creature.action.Block;
import action.entity.being.creature.action.Death;
import action.entity.being.creature.action.Dodge;
import action.entity.being.creature.action.EmpoweredMelee;
import action.entity.being.creature.action.Hold;
import action.entity.being.creature.action.Idle;
import action.entity.being.creature.action.Melee;
import action.entity.being.creature.action.Shoot;
import action.entity.being.creature.action.Walk;
import action.entity.enums.AttackType;
import action.entity.enums.BlockType;
import action.entity.enums.BusyLevel;
import action.entity.enums.SimpleMotionType;
import action.event.DeathEvent;
import action.hitbox.DangerZone;
import action.hitbox.IndexedHitBox;
import action.spell.Spell;
import action.utility.enums.Direction;
import action.utility.geom.Shape;
import action.world.Collision;
import action.world.World;

import com.badlogic.gdx.math.Vector2;

/**
 * An utility to manage creatures logic
 *
 * @author Andrea
 */
public class Logic {

	private Creature owner;
	/** Vector with direction */
	private Vector2 movV = new Vector2(0f, 0f);
	/** Direction the creature is facing */
    private Direction dir = Direction.UP;
    private Action action;
    /** HitBox for interacting with the world */
    private IndexedHitBox interactionHitBox;
    /** Tells how much the creature is occupied */
    private BusyLevel busy = BusyLevel.FREE;
    private List<Knockback> knockbacks = new ArrayList<Knockback>();
    /** Zone at risk caused by this creature */
    private DangerZone dangerZone;
	/** True if this creature exists */
	private boolean exist = true;
    
    /** Size of one side of the square of the interaction hb */
    private static final int INTERACTION_HB_SIZE = 64;
    
    
    public Logic(Creature owner) {
    	this.owner = owner;
    	action = new Idle(owner, this);
    	interactionHitBox = new IndexedHitBox(owner.getAverageRadius() / 2, 0, INTERACTION_HB_SIZE, INTERACTION_HB_SIZE);
    }
    
	public Direction getDir() {
		return dir;
	}

	public Action getAction() {
		return action;
	}
	
    public IndexedHitBox getInteractionHitBox() {
		return interactionHitBox;
	}

	public Shape getIntHitBoxShape() {
        return interactionHitBox.getShape(owner.getCenterX(), owner.getCenterY(), getDir());
    }
  
	/**
	 * @param force If true forces the end of the current action
	 * @return True if the action have been terminated
	 */
    public boolean terminateAction(boolean force) {
    	if (action.getType() == ActionType.IDLE)
    		return true;
    	if ((getBusy() != BusyLevel.HARD_BUSY) || (force)) {
	    	action.conclude(owner.getGfx(), this, owner.getInv());
	    	action = new Idle(owner, this);
	    	return true;
    	}
    	return false;
    }
    
    public void updateDir(Direction newOr) {
        if (newOr != dir) {
        	owner.getGfx().resetSimple(SimpleMotionType.WALK);
            dir = newOr;
        }
    }
    
    public void initialize() {
    	action.initialize();
    }
    
    public Vector2 getMovV() {
        return movV;
    }
    
    public void setMovV(float x, float y) {
        movV.set(x, y);
    }
    
	public void setDir(Direction dir) {
		this.dir = dir;
	}
    
    public void updateMovV(Vector2 dir) {
        movV.add(dir);
    }
    
    public void updateMovV(float x, float y) {
    	movV.x += x;
    	movV.y += y;
    }
    
    public void update(float delta) {
    	updateQuickCast();

    	action.update(owner.getGfx(), this, owner.getStats(), owner.getInv(), delta);
    	
    	/* Knockback management */
    	for (Iterator<Knockback> iter = knockbacks.iterator(); iter.hasNext();) {
    		Knockback knockback = iter.next();
    		applyKnockback(knockback, delta);
    		if (!knockback.update(delta))
    			iter.remove();
    	}
    		
    	World.moveEntity(owner.getWorld(), owner, delta, true);
    }
    
    public boolean isActionEqual(ActionType x) {
    	return (action.getType() == x);
    }
    
    protected BusyLevel getBusy() {
    	return busy;
    }
    
    public void setBusy(BusyLevel x) {
    	busy = x;
    }
    
    protected void tryBlock(BlockType type) {    
    	/* If we are blocking signal activity, otherwise try to start a block */
    	if (isActionEqual(ActionType.BLOCK))
    		((Block) action).signalActivity();
    	
    	/* Verify prerequisites */
    	else if ((owner.getStats().canBlock()) &&
    			(owner.getStats().hasStamina(owner.getInv().getBlockItem().getBlockCost(type)))) {
    		
    		/* If the actual action is walk, do not finalize */
    		if (isActionEqual(ActionType.WALK))
    			startBlock(type, ((Walk) action).getSoundID());
    		else if (terminateAction(false))
    			startBlock(type, -1);
        }
    }
    
    /**
     * @param soundID Walking sound ID in use (-1 for nothing)
     */
    private void startBlock(BlockType type, long soundID) {
    	owner.getStats().useStamina(owner.getInv().getBlockItem().getBlockCost(type));
		action = new Block(owner, this, type, owner.getInv().getBlockItem(), soundID);
    }
    
    protected void tryAttack(AttackType type) {
    	/* Do nothing if the weapon can't do this attack */
    	if (owner.getInv().getAttackItem().getAttack(type) == null)
    		return;	
    	
    	if (owner.getInv().getAttackItem().getAttack(type).isRanged())
    		tryShoot(type);
    	else
    		tryMelee(type);
    }
    
    protected void tryMelee(AttackType type) {
    	/* Verify prerequisites */
        if ((owner.getStats().canAttack(type)) &&
        		(owner.getStats().hasStamina(owner.getInv().getAttackItem().getAttackCost(type))) &&
        		(terminateAction(false))) {
        	owner.getStats().useStamina(owner.getInv().getAttackItem().getAttackCost(type));
        	action = new Melee(owner, owner.getGfx(), this, owner.getStats(), type, 
        			owner.getWorld().getEntities(), owner.getInv().getAttackItem());
        }
    }
    
    protected void tryShoot(AttackType type) {
    	/* If we are charging signal activity */
    	if (isActionEqual(ActionType.SHOOT))
    		((Shoot) action).signalActivity();
    	/* Otherwise try to start a new shoot */
    	else if ((owner.getStats().canAttack(type)) &&
        		(owner.getStats().hasStamina(owner.getInv().getAttackItem().getAttackCost(type))) &&
        		(terminateAction(false))) {
    		
    		owner.getStats().useStamina(owner.getInv().getAttackItem().getAttackCost(type));
    		action = new Shoot(owner, owner.getGfx(), this, owner.getStats(), type, owner.getInv().getAttackItem());
    	}
    }
    
    protected void tryChargedAttack(AttackType type) {
    	/* Do nothing if the weapon can't do this attack */
    	if (owner.getInv().getAttackItem().getAttack(type) == null)
    		return;	
    	
    	if (owner.getInv().getAttackItem().getAttack(type).isRanged())
    		tryShoot(type);
    	else
    		tryEmpoweredAttack(type);
    }
    
    protected void tryEmpoweredAttack(AttackType type) {
    	/* If we are charging signal activity */
    	if (isActionEqual(ActionType.EMPOWERED_MELEE))
    		((EmpoweredMelee) action).signalActivity();
    	/* Otherwise try to start a new attack */
    	else if (owner.getStats().canEmpoweredAttack(type)) {
    		Spell spell = owner.getBook().getSpell(type);
    		
    		if (owner.getStats().hasStamina(owner.getInv().getAttackItem().getAttackCost(type)) &&
    				(terminateAction(false))) {
    			/* Decrease stamina in any case */
    			owner.getStats().useStamina(owner.getInv().getAttackItem().getAttackCost(type));
    			/* Check focus */
    			if (owner.getStats().hasSpecial(spell.getCost())) {
    				owner.getStats().useSpecial(spell.getCost());
    				action = new EmpoweredMelee(owner, owner.getGfx(), this, owner.getStats(), type, 
    						owner.getWorld().getEntities(), owner.getInv().getAttackItem(), spell);
    			}
    			else
    				action = new Melee(owner, owner.getGfx(), this, owner.getStats(), type, 
    						owner.getWorld().getEntities(), owner.getInv().getAttackItem());
    		}
        }
        else
        	tryMelee(type);
    }
    
    protected void tryQuickCast() {	
    	/* Deactivation is always possible */
    	if (owner.getStats().isQuickCastActive()) {
    		removeToggleEffects();
    		owner.getStats().setQuickCast(false);
    	}
    	else {
        	/* Must be able to cast */
        	if (!owner.getStats().canQuickCast())
        		return;
        	
        	owner.getBook().getQuickSpell().cast(owner);
        	owner.getStats().setQuickCast(true);
    	}
    }
    
    public void removeToggleEffects() {
    	owner.getStats().removeToggleEffects();
    	owner.getInv().removeToggleEffects();
    }
    
    public boolean hasActiveToggleEffects() {
    	return owner.getStats().hasActiveToggleEffects() || owner.getInv().hasActiveToggleEffects();
    }
    
    /**
     * Put quick cast status on off if all toggle effects vanished
     */
    public void updateQuickCast() {
    	if (owner.getStats().isQuickCastActive())
    		if (!hasActiveToggleEffects())
    			owner.getStats().setQuickCast(false);
    }
    
    public boolean intHitBoxCollide(Shape shape) {
        return getInteractionHitBox().collide(owner.getCenterX(), owner.getCenterY(), dir, shape);
    }
    
    /**
     * @see Creature#tryMovement(Vector2)
     */
    protected void tryMovement(Vector2 dir, float speed) {
    	/* Update the route */
    	if (owner.getStats().canMove()) {
			action.updateRoute(this, owner.getGfx(), dir, speed);
			/* If it's doing nothing start a walk action */
	    	if (isBusyLevel(BusyLevel.FREE) && !isActionEqual(ActionType.WALK)) {
	        	if (terminateAction(false)) {
	        		action = new Walk(owner, owner.getGfx(), this, owner.getStats());
	        		action.updateRoute(this, null, dir, speed); 
	        	}
	    	}
    	}
    }
  
    protected void tryRun() {
    	if (owner.getStats().canRun()) {
    		((Walk) action).signalRun();
    	}
    }
    
    protected void tryHold(float duration) {
    	if (action.getType() == ActionType.HOLD)
    		((Hold) action).extend(duration);
    	else if (owner.isAlive()){
	    	terminateAction(true);
	    	action = new Hold(owner, this, duration);
    	}
    }
    
    protected void tryDodge(Vector2 v) {
    	/* Update the route since this is still a movement input */
    	if (owner.getStats().canMove()) {
			action.updateRoute(this, owner.getGfx(), v, 1);
			
			/* Try to start a dodge */
			float cost = owner.getStats().getDodgeCost();
			if ((owner.getStats().canDodge()) && 
					(terminateAction(false)) &&
					(owner.getStats().hasStamina(cost))) {
				
				owner.getStats().useStamina(cost);
        		action = new Dodge(owner, owner.getGfx(), this, owner.getStats(), owner.getInv(), v);
			}
    	}
    }

    protected boolean move(World world, float dx, float dy, boolean smooth) {
        Collision collision = new Collision(owner, dx, dy, world);
        if (collision.canMove(false)) {
            owner.updatePos(dx, dy);  
            return true;
        }
        else {
        	absorbKnockback();
            if (smooth) {
            	if (dy != 0) {
	                collision = new Collision(owner, 0, dy, world);
	                if (collision.canMove(false)) {
	                    owner.updatePos(0, dy);
	                    return true;
	                }
            	}
                if (dx != 0) {
                    collision = new Collision(owner, dx, 0, world);
                    if (collision.canMove(false)) {
                        owner.updatePos(dx, 0);
                        return true;
                    }
                }
            }
        }
        return false;
    }

	public void takeImpact(Impact impact) {
		/* Take knockback in anycase */
		if (impact.getKnockback() != null) {
			takeKnockback(impact.getKnockback());
			impact.removeKnockback();
		}
		
		/* Impact journey */
		if (!impact.exists())
			return;
		action.takeImpact(this, owner.getStats(), impact);
		
		if (!impact.exists())
			return;
    	owner.getGfx().visualizeHit();
		owner.getInv().takeImpact(impact);
		
		if (!impact.exists())
			return;
		owner.getStats().takeImpact(impact);
	}
	
	public Shape getShape() {
		return owner.getShape(0, 0);
	}

	/**
	 * @return true if the creature is less busy than the given amount
	 */
	public boolean isBusyLevel(BusyLevel level) {
		return (getBusy().value() <= level.value());
	}
	
	public void takeKnockback(Knockback knockback) {
		knockbacks.add(knockback);
	}
	
	private void applyKnockback(Knockback knockback, float delta) {
		this.updateMovV(knockback.getDisplacement(delta));
	}
	
	private void absorbKnockback() {
		for (Knockback kb : knockbacks)
			owner.takeDamage(new Damage(DamageType.PHYSICAL, kb.getDamage(), Knockback.DMG_VARIATION));
		knockbacks.clear();
	}
	
	/**
	 * @return Kb resistance due to current action
	 */
	public float getKnockbackResist() {
		return action.getKnockbackResist();
	}

	public void setAction(Action action) {
		this.action = action;
	}
	
	public void die() {
		/* Start death action */
		terminateAction(true);
		action = new Death(owner, this);
		/* Notice the world of this event */
		owner.getWorld().takeEvent(new DeathEvent(owner.getStorycode(), 1));
		/* Danger zone dies too */ 
		setDangerZone(null);
		/* No need for UI */
		owner.getUi().setVisible(false);
	}

	public DangerZone getDangerZone() {
		return dangerZone;
	}

	public void setDangerZone(DangerZone dangerZone) {
		/* In case there's a danger zone active, deactivate it before changing. This is done
		 * because other entities might be relying on it while blocking */
		if (this.dangerZone != null)
			this.dangerZone.setActive(false);
		this.dangerZone = dangerZone;
	}
	
	public boolean exist() {
		return exist;
	}

	public void ceaseExistance() {
		exist = false;
	}
	
	protected boolean isDying() {
		return action.getType() == ActionType.DEATH;
	}
	
}
