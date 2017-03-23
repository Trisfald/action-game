package action.entity.being;

import java.util.List;

import action.core.Game;
import action.entity.Entity;
import action.entity.being.creature.Creature;
import action.entity.being.creature.CreatureInventory;
import action.entity.being.creature.Stats;
import action.entity.being.creature.action.Action;
import action.entity.being.creature.action.Action.ActionType;
import action.entity.being.creature.action.Block;
import action.entity.being.creature.action.Melee;
import action.entity.being.creature.action.Talk;
import action.entity.enums.AttackType;
import action.entity.enums.BlockType;
import action.hitbox.IndexedHitBox;
import action.input.GameInput;
import action.spell.Spell.SpellType;
import action.ui.GameUI;
import action.utility.Vector2i;
import action.world.Faction;
import action.world.World;

import com.badlogic.gdx.math.Vector2;

/**
 * Class to manage the player in-game
 *
 * @author Andrea
 */
public class Player extends Creature {
	
    public Player(float x, float y, World world, CreatureInfo info, Faction faction) {
        super(x, y, world, info, "Player", faction);
        getUi().setLifeBarEnabled(false);
        getUi().setChargeBarEnabled(true);
    }
    
    public void assignFaction(Faction f) {
        faction = f;
    }
    
    public boolean isTalking() {
    	return getLogic().isActionEqual(ActionType.TALK);
    }
    
    @Override
    public void update(float delta) {
    	getInv().update(delta);
    	getStats().update(delta);
    	processInput(Game.gameInput);
        getLogic().update(delta);
        getGfx().update(delta);
        getUi().update(delta);
    }
    
    public void processInput(GameInput input) {
    	if (input.isAttacking()) {
    		tryAttack(AttackType.WEAK);
    	}
    	
    	if (input.isBlocking()) {
    		tryBlock(BlockType.STANDARD);
    	}
    	
    	Vector2i movi = input.getPlayerMov();
    	if (!movi.isZero()) {
    		/** Get the vector's normal to take care of diagonal movement reduction */
    		Vector2 mov = movi.getVector2().nor();
    		tryMovement(mov);
    	}
    }
    
    public void setWorld(World world) {
    	super.world = world;
    }
	
	@Override
	public Stats getStats() {
		return super.getStats();
	}
	
	@Override
	public CreatureInventory getInv() {
		return super.getInv();
	}
	
	public SpellType getCurrentSpellType() {
		return super.getBook().getSelectedType();
	}
	
	/**
	 * For debugging
	 */
	public IndexedHitBox getAttackHitBox() {
		Action a = getLogic().getAction();
		if (a.getType() == ActionType.MELEE)
			return ((Melee) a).getHitBox(getLogic());
		return null;
	}
	
	/**
	 * For debugging
	 */
	public IndexedHitBox getBlockHitBox() {
		Action a = getLogic().getAction();
		if (a.getType() == ActionType.BLOCK)
			return ((Block) a).getHitBox(getLogic());
		return null;
	}
	
	@Override
	public void shiftPosition(float dx, float dy) {
		position.add(dx, dy);
	}
	
    public void tryInteraction(List<Entity> list, GameUI ui) {
        if (!getStats().canInteraction())
        	return;
        
        /* Check if there's an entity available for interaction */
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).isAggressiveTo(this) && !list.get(i).equals(this) && list.get(i).getInteraction() != null) {
                if (getLogic().intHitBoxCollide(list.get(i).getShape())) {
                	/* Player must be able to interact */
                    if (terminateAction(false)) {
                    	/* Choose which kind of interaction */
                    	switch (list.get(i).getInteraction().getType()) {
                    		case CHAT:
                    	    	getLogic().setAction(new Talk(this, getLogic(), ui));
                    	        ui.initialize(list.get(i).getInteraction());
                    	        /* Change player input */
                    	        initInteraction();
                    			break;
                    		case INVENTORY:
                    			getLogic().setAction(new Talk(this, getLogic(), ui));
                    			ui.initialize(list.get(i).getInteraction());
                    	        /* Change player input */
                    	        initInteraction();
                    			break;
                    	}
                    }
                }
            }
        }
    }

    public void initInteraction() {

    }
    
    public void finalizeInteraction() {

    }

}
