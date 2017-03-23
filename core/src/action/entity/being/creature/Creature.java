package action.entity.being.creature;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import action.ai.Ai;
import action.combat.Damage;
import action.combat.Impact;
import action.combat.Knockback;
import action.combat.effect.Effect;
import action.core.Game;
import action.entity.appearance.Appearance;
import action.entity.being.AbstractBeing;
import action.entity.being.creature.CreatureInventory.CreatureInventoryInfo;
import action.entity.being.creature.Graphics.GraphicsInfo;
import action.entity.being.creature.Stats.CreatureStatType;
import action.entity.being.creature.action.Action.ActionType;
import action.entity.enums.AttackType;
import action.entity.enums.BlockType;
import action.entity.enums.BusyLevel;
import action.entity.enums.MagicSkill;
import action.hitbox.DangerZone;
import action.interfaces.Mover;
import action.interfaces.StatsMemory;
import action.spell.SpellBook;
import action.utility.Body.BodyInfo;
import action.utility.Statistic.StatisticInfo;
import action.utility.enums.Direction;
import action.utility.form.Form.FormLoader;
import action.utility.interaction.Interaction;
import action.world.Faction;
import action.world.World;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Wrapper class to manage creatures
 *
 * @author Andrea
 */
public class Creature extends AbstractBeing implements Mover {
    
    private String name;
    private int id;
    private String storycode;

    private Stats stats;
    private Logic logic;
    private Graphics gfx;
    private Ai ai;
    private CreatureInventory inv;
    private CreatureUI ui;
    private SpellBook book;
    private Appearance appearance;
    private Mirror mirror;
    
    /**
     * @param x Starting position x
     * @param y Starting position y
     * @param dir Custom starting direction
     * @param world
     * @param info
     * @param ai Custom ai
     * @param storycode Custom storycode
     * @param name Custom name
     * @param faction
     */
    public Creature(float x, float y, Direction dir, World world, CreatureInfo info, int ai, String storycode, 
    		String name, Faction faction) {
        super(x, y, info.form, world, faction);
        this.id = info.id;
        this.name = (name != null) ? name : Game.assets.getDialog(info.name);
        this.storycode = (storycode != null) ? storycode : String.valueOf(id);
        loadSubParts(info);
        loadAi(ai, info);  
        logic.setDir(dir);
    }
    
    /** Player constructor */
    protected Creature(float x, float y, World world, CreatureInfo info, String name, Faction faction) {
        super(x, y, info.form, world, faction);
        this.id = info.id;
        this.name = (name != null) ? name : Game.assets.getDialog(info.name);
        loadSubParts(info);
    }
    
    /**
	 * Load standard AI or custom AI
     */
    private void loadAi(int id, CreatureInfo info) {
        if (id > -1) 
            ai = new Ai(Game.assets.getAi(id), this, mirror);
        else
            ai = new Ai(Game.assets.getAi(info.ai), this, mirror);
    }
    
    private void loadSubParts(CreatureInfo info) {
    	inv = new CreatureInventory(this, info.inventory);
    	stats = new Stats(this, info);
    	gfx = new Graphics(this, info.gfx);
    	logic = new Logic(this);
    	ui = new CreatureUI(this);	
    	book = new SpellBook(info.spells);
    	appearance = new CreatureAppearance(this);
    	mirror = new Mirror(this);
    }

	public String getName() {
    	return name;
    }
    
	protected Stats getStats() {
		return stats;
	}

	protected Logic getLogic() {
		return logic;
	}

	protected Graphics getGfx() {
		return gfx;
	}
	
	public String getStorycode() {
		return storycode;
	}
	
	public StatsMemory getStatsMemory() {
		return stats;
	}

	protected Ai getAi() {
		return ai;
	}
	
	protected CreatureInventory getInv() {
		return inv;
	}
	
	protected CreatureUI getUi() {
		return ui;
	}
	
	protected SpellBook getBook() {
		return book;
	}
    
    @Override
    protected void drawGfx(SpriteBatch batch) {
    	gfx.draw(batch);
    }
    
    @Override
	protected void drawUi(SpriteBatch batch) {
    	super.drawUi(batch);
    	ui.draw(batch);
    }
    
    @Override
    public void initialize() {
    	logic.initialize();
    }
    
    @Override
    public void update(float delta) {
    	inv.update(delta);
    	stats.update(delta);
        ai.behave(delta);
        logic.update(delta);
        gfx.update(delta);
        ui.update(delta);
    }
    
    /**
     * Try a normal attack
     */
    public void tryAttack(AttackType type) {
    	logic.tryAttack(type);
    }
    
    /**
     * Try a charged attack
     */
    public void tryChargedAttack(AttackType type) {
    	logic.tryChargedAttack(type);
    }
    
    public void tryQuickCast() {
    	logic.tryQuickCast();
    }
    
    public void tryBlock(BlockType type) {
    	logic.tryBlock(type);
    }
    
    /**
     * Try movement on the game world
     * @param dir Direction vector
     * @param speed Relative speed
     */
    public void tryMovement(Vector2 dir, float speed) {
    	logic.tryMovement(dir, speed);
    }
    
    /**
     * Try movement on the game world at full speed
     * @param dir Direction vector
     */
    public void tryMovement(Vector2 dir) {
    	logic.tryMovement(dir, 1);
    }
    
    public void tryRun() {
    	logic.tryRun();
    }
    
    public void tryHold(float duration) {
    	logic.tryHold(duration);
    }
    
    @Override
    public void takeImpact(Impact impact) {
        logic.takeImpact(impact);
    }
    
    @Override
	public boolean takeDamage(Damage damage) {
    	return stats.takeDamage(damage);
    }
    
    @Override
	public void takeEffect(Effect effect) {
    	stats.takeEffect(effect);
    }
    
	@Override
	public void takeEffect(Effect effect, AlterationTarget target) {
		switch (target) {
			case BODY: 
				takeEffect(effect);
				break;
			case WEAPON:
				inv.getAttackItem().takeEffect(effect);
				break;
			case SHIELD:
				inv.getBlockItem().takeEffect(effect);
				break;
		}
	}
    
    @Override
    public boolean isAlive() {
    	return stats.isAlive();
    }  
	
	@Override
	public boolean exist() {
		return logic.exist();
	}
    
    public boolean isBusyLevel(BusyLevel level) {
    	return logic.isBusyLevel(level);
    }
    
    public boolean terminateAction(boolean force) {
    	return logic.terminateAction(force);
    }
    
    public boolean isActionEqual(ActionType x) {
    	return logic.isActionEqual(x);
    }
    
	@Override
	public void apply(Effect effect) {
		stats.apply(effect);
	}

	@Override
	public void remove(Effect effect) {
		stats.remove(effect);
	}

	public void nextSpellType() {
		book.selectNextType();
	}
	
	public void previousSpellType() {
		book.selectPreviousType();
	}
	
	@Override
	public void takeKnockback(Knockback knockback) {
		logic.takeKnockback(knockback);
	}
	
	@Override
	public Vector2 getMovV() {
		return logic.getMovV();
	}

	@Override
	public void setMovV(float x, float y) {
		logic.setMovV(x, y);
	}
	
	public void setDir(Direction dir) {
		logic.setDir(dir);
	}

	@Override
	public boolean move(World world, float dx, float dy, boolean smooth) {
		return logic.move(world, dx, dy, smooth);
	}
	
	@Override
	public void shiftPosition(float dx, float dy) {
		super.shiftPosition(dx, dy);
		ai.shiftPosition(dx, dy);
	}
	
	/**
	 * @return The reach of the weapon-arm of this creature, horizontally (sum it to the weapon range)
	 */
	public float getArmReachH() {
		return getAverageRadius() / 2;
	}
	
	/**
	 * @return The reach of the weapon-arm of this creature, vertically (sum it to the weapon range)
	 */
	public float getArmReachV() {
		return 0;
	}
	
	@Override
	public Appearance getAppearance() {
		return appearance;
	}
	
	@Override
	public float getMassFactor() {
		return stats.getMassFactor() + inv.getMassFactor();
	}
	
	@Override
	public float getKnockbackResist() {
		return getMassFactor() * logic.getKnockbackResist() * stats.getKnockbackResist();
	}
	
	@Override
	public boolean sustainEffect(float cost) {
		return stats.sustainEffect(cost);
	}

	@Override
	public void removeToggleEffects() {
		logic.removeToggleEffects();
	}
	
    @Override
	public boolean hasActiveToggleEffects() {
    	return logic.hasActiveToggleEffects();
    }
    
    public void tryDodge(Vector2 v) {
    	logic.tryDodge(v);
    }
    
    /**
     * @return The ID of this type of creature
     */
    public int getID() {
    	return id;
    }
    
    @Override
	public void die() {
    	if (logic.isDying())
			return;
    	logic.die();
    }

    @Override
	public Interaction getInteraction() {
    	//TODO a chat interaction
		return null;
    }
	
	@Override
	public DangerZone getDangerZone() {
		return logic.getDangerZone();
	}
    
	public Direction getDir() {
		return logic.getDir();
	}
	
	@Override
	public void dispose() {
		gfx.dispose();
	}
	
		
    /**
     * Info class for creature
     *
     * @author Andrea
     */
    public static class CreatureInfo {
        
    	public final int id;
        public final FormLoader form;
        public final int name;
        public final BodyInfo body;
        public final CreatureInventoryInfo inventory;
        public final Map<CreatureStatType, StatisticInfo> stats;
        public final GraphicsInfo gfx;
        public final int ai;
        public final List<Integer> spells;
        public final MagicSkill magicSkill;
        
		public CreatureInfo(int id, FormLoader form, int name, BodyInfo body, CreatureInventoryInfo inventory,
				Map<CreatureStatType, StatisticInfo> stats, GraphicsInfo gfx, int ai,
				List<Integer> spells, MagicSkill magicSkill) {
			this.id = id;
			this.form = form;
			this.name = name;
			this.body = body;
			this.inventory = inventory;
			this.stats = stats;
			this.gfx = gfx;
			this.ai = ai;
			this.spells = spells;
			this.magicSkill = magicSkill;
		}
		
		public static CreatureInfo loadFromXml(Element element) {
			return new CreatureInfo(
        			element.getIntAttribute("id"),
        			FormLoader.loadFromXml(element.getChildByName("form")),
        			element.getIntAttribute("name"),
        			BodyInfo.loadFromXml(element.getChildByName("body")),
        			CreatureInventoryInfo.loadFromXml(element.getChildByName("inventory")),
                    loadStats(element.getChildByName("statistics")),
                    GraphicsInfo.loadFromXml(element.getChildByName("graphics")),
                    element.getIntAttribute("ai"),
                    loadSpells(element.getChildByName("spells")),
                    MagicSkill.valueOf(element.getAttribute("magic", "NONE"))
        			);
		}
	    
	    private static Map<CreatureStatType, StatisticInfo> loadStats(Element element) {
	    	Map<CreatureStatType, StatisticInfo> map = new EnumMap<CreatureStatType, StatisticInfo>(CreatureStatType.class);
	    	Element child;
	        for (int i = 0; i < element.getChildCount(); i++) {
	        	child = element.getChild(i);
	        	map.put(CreatureStatType.valueOf(child.getAttribute("type")), StatisticInfo.loadFromXML(child));
	        }
	    	return map;
	    }
	    
	    private static List<Integer> loadSpells(Element element)  {
	    	List<Integer> list = new ArrayList<Integer>(element.getChildCount());
	        for (int i = 0; i < element.getChildCount(); i++) {
	        	list.add(element.getChild(i).getIntAttribute("id"));
	        }
	    	return list;
	    }
	    
    }

}
