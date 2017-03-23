package action.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import action.combat.Damage.DamageLoader;
import action.combat.effect.Effect.EffectLoader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Abstract class that serves as template for all databases
 *
 * @author Andrea
 */
public abstract class Database { 
    
    protected Element load(FileHandle file) throws IOException{
    	XmlReader reader = new XmlReader();
    	return reader.parse(file);
    } 
    
    protected abstract void fillData(Element root);
    
	public static List<DamageLoader> loadDamages(Element element) {
		if (element == null)
			return new ArrayList<DamageLoader>();
		Element child;
		List<DamageLoader> damages = new ArrayList<DamageLoader>(element.getChildCount());
	    for (int i = 0; i < element.getChildCount(); i++) {
	    	child = element.getChild(i);
	    	damages.add(DamageLoader.loadFromXML(child));
		}
		return damages;
	}
	
	public static List<EffectLoader> loadEffects(Element element) {
		if (element == null)
			return new ArrayList<EffectLoader>();
		Element child;
		List<EffectLoader> effects = new ArrayList<EffectLoader>(element.getChildCount());
	    for (int i = 0; i < element.getChildCount(); i++) {
	    	child = element.getChild(i);
	    	effects.add(EffectLoader.loadFromXML(child));
		}
		return effects;
	}
    
}