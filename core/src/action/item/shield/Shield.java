package action.item.shield;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import action.combat.Alterable;
import action.combat.BlockLevel;
import action.combat.Damage;
import action.combat.Impact;
import action.combat.effect.ChargeTrigger;
import action.combat.effect.Effect;
import action.combat.effect.EffectManager;
import action.entity.being.creature.Creature;
import action.entity.enums.BlockType;
import action.entity.enums.BlockType.BlockMotionType;
import action.item.Item;
import action.item.StatusItem;
import action.item.shield.ShieldBlock.ShieldBlockInfo;
import action.item.weapon.Weapon.WeaponInfo;
import action.utility.Body;
import action.utility.Body.BodyInfo;
import action.utility.Body.BodyStatType;
import action.utility.enums.Direction;

import com.badlogic.gdx.utils.XmlReader.Element;

public class Shield extends Item implements Alterable, Blocker, StatusItem {

	private Creature owner;
	private Body body;
    private Map<BlockType, ShieldBlock> blocks;
    private ShieldBlock currentBlock;
    private float speedMultiplier = 1f;
	private EffectManager effectManager = new ShieldEffectManager(this);
	/** Indicates if the shield is in use */
    private boolean busy;
	
	public Shield(ShieldInfo info, Creature owner) {
		super(info);
		this.owner = owner;
    	this.body = new Body(info.body);
		
        blocks = new EnumMap<BlockType, ShieldBlock>(BlockType.class);
        for (int i = 0; i < info.blocks.length; i++)
        	blocks.put(info.blocks[i].type, new ShieldBlock(info.blocks[i]));
	}
	
	public Shield(ShieldInfo info) {
		this(info, null);
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
	
	@Override
	public BlockLevel getBlockCapability() {
		return BlockLevel.SHIELD;
	}
	
	@Override
	public BlockMotionType getBlockStyle() {
		return currentBlock.getStyle();
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
	public float getBlockSpeed() {
		return currentBlock.getSpeed() * speedMultiplier;
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
	public void resetBlock(Direction dir) {
    	setBusy(false);
    }
	
	@Override
	public boolean isAlive() {
		return body.isAlive();
	}
	
	@Override
	public float getBlockCost(BlockType type) {
		return getBlock(type).getCost();
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
		
		/* Soak damage and update HP (VERSION WITH TAKE DAMAGE)
		body.getStat(BodyStatType.HP).decreaseValue(damage.reduce(
				/* Can't soak more than HP 
				Math.min(body.getSoakValue(damage.getDmgType()), body.getStat(BodyStatType.HP).value())));
		*/
		/* Soak but ignore damage taken */
		damage.reduce(body.getSoakValue(damage.getDmgType()));
				
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
	public float getCrushResist() {
		return currentBlock.getCrushResist();
	}
	
    @Override
	public float getHpRatio() {
    	return body.getStat(BodyStatType.HP).ratio();
    }
	
    
	/**
	 * @author Andrea
	 */
	public static class ShieldInfo extends ItemInfo {
		
		public final BodyInfo body;
        public final ShieldBlockInfo[] blocks;
        
		public ShieldInfo(ItemInfo itemInfo, BodyInfo body, ShieldBlockInfo[] blocks) {
			super(itemInfo);
			this.body = body;
			this.blocks = blocks;
		}
		
		public static ShieldInfo loadFromXml(Element element) {
		    return new ShieldInfo(
		    		ItemInfo.loadFromXml(element),
		    		BodyInfo.loadFromXml(element.getChildByName("body")),
		    		WeaponInfo.loadBlocks(element.getChildByName("blocks"))
		    		);
		}
	}
	
	
	/**
	 * @author Andrea
	 */
	private class ShieldEffectManager extends EffectManager {

		private ShieldEffectManager(Shield owner) {
			super(owner);
		}
		
	}
	
}
