package action.ai.personality;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Properties of an AI personality
 * 
 * @author Andrea
 */
public class Properties {

	public final float wander;
	
	public Properties(float wander) {
		this.wander = wander;
	}
	
	
	
	public static Properties loadFromXml(Element element) {
		return new Properties(
				element.getFloatAttribute("wander")
				);
	}

}
