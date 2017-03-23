package action.spell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import action.core.Game;
import action.entity.enums.AttackType;
import action.spell.Spell.SpellCategory;
import action.spell.Spell.SpellType;


/**
 * @author Andrea
 */
public class SpellBook {

	private Map<SpellType, SpellPage> spells = new EnumMap<SpellType, SpellPage>(SpellType.class);
    private SpellType selectedType = null;
	
    public SpellBook(List<Integer> spellsId) {
    	/* Create spell lists */
    	for (SpellType x : SpellType.values())
    		spells.put(x, new SpellPage());
    	/* Populate */
    	for (Integer x : spellsId)
    		addSpell(x);
    	selectNextType();
    }
    
    public void addSpell(Spell spell) {
    	spells.get(spell.getType()).add(spell);
    }
    
    public void addSpell(int spellId) {
    	addSpell(Game.assets.getSpell(spellId));
    }
    
    /**
     * @return A single spell
     */
    public Spell getSpell(SpellType type, int spellId) {
    	return spells.get(type).get(spellId);
    }
    
    /**
     * @return An empowered spell depending on the attack and the current type
     */
    public Spell getSpell(AttackType attack) {
    	if (selectedType == null)
    		return null;

    	return spells.get(selectedType).get(attack.getSpellCategory());
    }
	
    /**
     * @return A quick spell depending on the the current type
     */
    public Spell getQuickSpell() {
    	if (selectedType == null)
    		return null;
    	
    	return spells.get(selectedType).get(4);
    }
    
    /**
     * @return All the spells of a given type
     */
    public Collection<Spell> getSpells(SpellType type) {
    	return spells.get(type).values();
    }
    
    public boolean hasSpell(AttackType type) {
    	return (getSpell(type) != null);
    }
    
    public boolean hasQuickSpell() {
    	return (getQuickSpell() != null);
    }
    
    private EnumSet<SpellType> getTypesSet() {
    	EnumSet<SpellType> set = EnumSet.noneOf(SpellType.class);
    	for (Entry<SpellType, SpellPage> x : spells.entrySet()) {
    		if (!x.getValue().isEmpty()) 
    			set.add(x.getKey());
    	}
    	return set;
    }
    
    public void selectNextType() {
    	selectedType = nextType(selectedType);
    }
    
    public void selectPreviousType() {
    	selectedType = previousType(selectedType);
    }
    
    private SpellType nextType(SpellType current) {
    	List<SpellType> list = new ArrayList<SpellType>(getTypesSet());
    	int index;
    	ListIterator<SpellType> iter;
    	
    	if (list.isEmpty())
    		return null;
    	if (list.size() == 1)
    		return list.get(0);
    	
    	index = list.lastIndexOf(current);
    	iter = list.listIterator(index+1);
    	if (iter.hasNext())
    		return iter.next();
    	/* No next, return the first element */
    	return list.get(0);
    }
    
    private SpellType previousType(SpellType current) {
    	List<SpellType> list = new ArrayList<SpellType>(getTypesSet());
    	int index;
    	ListIterator<SpellType> iter;
    	
    	if (list.isEmpty())
    		return null;
    	if (list.size() == 1)
    		return list.get(0);
    	
    	index = list.lastIndexOf(current);
    	iter = list.listIterator(index);
    	if (iter.hasPrevious())
    		return iter.previous();
    	/* No previous, return the lest element */
    	return list.get(list.size()-1);
    }
    
	public SpellType getSelectedType() {
		return selectedType;
	}
	
	
	/**
	 * One page of the spell book, that holds all the spells of the same type
	 * 
	 * @author Andrea
	 */
	public static class SpellPage {
		
		/** Map of classic spells */
		private Map<Integer, Spell> spells = new HashMap<Integer, Spell>();
		/** Shortcuts to reach fast special type of spells */
		private Map<SpellCategory, Integer> shortcuts = new EnumMap<SpellCategory, Integer>(SpellCategory.class);
		
		public Spell get(int id) {
			return spells.get(id);
		}
		
		public void add(Spell spell) {
			spells.put(spell.getId(), spell);
			shortcuts.put(spell.getCategory(), spell.getId());
		}
		
		public Spell get(SpellCategory category) {
			return spells.get(shortcuts.get(category));
		}
		
		public Collection<Spell> values() {
			return spells.values();
		}
		
		public boolean isEmpty() {
			return spells.isEmpty();
		}
		
	}
}
