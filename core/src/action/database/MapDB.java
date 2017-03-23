package action.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader.Element;

import action.utility.enums.Direction;
import action.world.ambience.weather.MeteoInfo;
import action.world.ambience.weather.MeteoInfo.WeatherInfo;
import action.world.ambience.weather.Weather.WeatherType;
import action.world.map.MapWrapper.MapInfo;
import action.world.map.MapWrapper.MapLink;

/**
 * Database for Maps
 *
 * @author Andrea
 */
public class MapDB extends Database {
    
	private Map<Integer, MapInfo> data = new HashMap<Integer, MapInfo>();
	
    public MapDB(FileHandle file) throws IOException {
        fillData(load(file));
    }

    @Override
    protected void fillData(Element root) {
        Element element;
        for (int i = 0; i < root.getChildCount(); i++) {
            element = root.getChild(i);
            data.put(element.getIntAttribute("id"), (new MapInfo(
            		element.getIntAttribute("id"),
                    element.getAttribute("path"), 
                    element.getBoolean("indoor", false),
                    element.getAttribute("illumination", "WHITE"),
                    loadMapLinks(element.getChildByName("links")),
                    loadMeteo(element.getChildByName("meteo"))
                    )));
        }
    }
    
    public MapInfo getElement(int key) {
    	return data.get(key);
    }
    
    public Collection<MapInfo> getValues() {
    	return data.values();
    }
    
    private List<MapLink> loadMapLinks(Element element) {
    	Element child;
	    int size = element.getChildCount();
	   	List<MapLink> links = new ArrayList<MapLink>(size);
	    for (int i = 0; i < size; i++) {
	    	child = element.getChild(i);
	    	links.add(new MapLink(
	    			Direction.valueOf(child.getAttribute("direction")),
	    			child.getIntAttribute("destination")
	    			));
	    }  	
    	return links;
    }
    
    private MeteoInfo loadMeteo(Element element) {
    	Element child;
	    int size = element.getChildCount();
    	List<WeatherInfo> meteo = new ArrayList<WeatherInfo>(size);
	    for (int i = 0; i < size; i++) {
	    	child = element.getChild(i);
	    	meteo.add(new WeatherInfo(
	    			WeatherType.valueOf(child.getAttribute("type")),
	    			child.getFloatAttribute("probability")
	    			));
	    }
	    return new MeteoInfo(meteo);
    }
	
}
