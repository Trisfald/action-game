package action.world.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import action.core.Game;
import action.entity.Entity;
import action.entity.being.creature.Creature;
import action.interfaces.GlobalVar;
import action.utility.enums.Direction;
import action.world.World;
import action.world.ambience.Light;
import action.world.ambience.weather.MeteoInfo;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;


/**
 * Wrapper around a tiled map
 * 
 * @author Andrea
 */
public class MapWrapper {
	
	/** The real tiled map */
	private TiledMap tiledMap;
	/** Id of this map in the db */
	private int id;
	/** Tells if a certain node allow movement upon itself */
    private boolean[][] blocked;
    /** Width in tiles */
    private int width;
    /** Height in tiles */
    private int height;
    /** True if the map is not exposed to sunlight */
    private boolean indoor;
    /** List of transitions to change map */
    private List<Transition> transitions = new ArrayList<Transition>();
    /** The modificator to the illumination of the screen depending on this map */
    private Color illumination;
    /** Meteo informations */
    private MeteoInfo meteo;
    /** Renderer for this map */
    private TiledMapRenderer renderer;
	
    private static final String NAME_GROUP_TRANSITIONS = "Transition";
    private static final String NAME_GROUP_ENTITIES = "Entity";
    private static final String NAME_GROUP_LIGHTS = "Light";
    
	
	public MapWrapper(MapInfo info, SpriteBatch batch) {
		this.id = info.id;
		this.meteo = info.meteo;
		this.indoor = info.indoor;
		load(info);
    	renderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
	}
	
	private void load(MapInfo info) {
    	tiledMap = Game.assets.get(info.path, TiledMap.class);
    	width = tiledMap.getProperties().get("width", Integer.class);
    	height = tiledMap.getProperties().get("height", Integer.class);
    	loadBlocked();
    	loadTransitions();
    	loadIllumination(info.illumination);
	}
	
    /**
     *  Load in memory the 'blocked' property of each tile for quick access 
     */
    private void loadBlocked() {
        blocked = new boolean[width][height];
    	TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(GlobalVar.TILE_PROPERTIES_LAYER);
        
        for (int xAxis=0 ; xAxis<width; xAxis++) {
            for (int yAxis=0; yAxis<height; yAxis++) {
                blocked[xAxis][yAxis] = isTileBlocked(xAxis, yAxis, layer);
            }
        }
    }
    
    /**
     * @return True if the tile (on the map definition file) is blocked
     */
    private boolean isTileBlocked(int x, int y, TiledMapTileLayer layer) {
    	Cell cell = layer.getCell(x, y);
    	if (cell == null)
    		return false;
    	return ("true").equals(cell.getTile().getProperties().get("blocked"));
    }
	
    private void loadIllumination(String illumination) {
    	this.illumination = new Color(Game.assets.getColor(illumination));
    }
	
	public void dispose() {
		tiledMap.dispose();
	}
	
	public boolean isIndoor() {
		return indoor;
	}
    
	public TiledMap getTiledMap() {
		return tiledMap;
	}

	public int getId() {
		return id;
	}
	
    private void loadTransitions() {
    	for (MapObject object : tiledMap.getLayers().get(NAME_GROUP_TRANSITIONS).getObjects()) {
    		RectangleMapObject x = (RectangleMapObject) object;

    		transitions.add(new Transition(
    				x.getRectangle(),
    				Integer.parseInt(x.getProperties().get("dst", "-1", String.class)),
    				Integer.parseInt(x.getProperties().get("dstX", "0", String.class)),
    				Integer.parseInt(x.getProperties().get("dstY", "0", String.class))
    				));
    	}
    }

	public List<Transition> getTransitions() {
		return transitions;
	}
	
	public TiledMapRenderer getRenderer() {
		return renderer;
	}

	public Collection<? extends Entity> loadNewEntities(int xOffset, int yOffset, World world) {
    	List<Entity> list = new ArrayList<Entity>();

    	for (MapObject object : tiledMap.getLayers().get(NAME_GROUP_ENTITIES).getObjects()) {
    		RectangleMapObject x = (RectangleMapObject) object;
    		
        	/** Check if the entity will spawn or not (spawnrate is based on %) */
            if (Float.parseFloat(x.getProperties().get("spawnrate", "1", String.class)) > Math.random()){      	
            	switch (x.getProperties().get("type", String.class)) {
            		case "creature":
	                	list.add(new Creature(
	                            x.getRectangle().x + xOffset,
	                            x.getRectangle().y + yOffset,
	                            Direction.valueOf(x.getProperties().get("dir", "UP", String.class)),
	                            world,
	                            Game.assets.getCreatureInfo(Integer.parseInt(x.getProperties().get("id", String.class))),
	                            Integer.parseInt(x.getProperties().get("ai", "-1", String.class)),
	                            x.getProperties().get("name", String.class),
	                            x.getProperties().get("storycode", null, String.class), 
	                            Game.assets.getFaction(Integer.parseInt(x.getProperties().get("faction", String.class)))
	                            ));
	                	break;
                }
            }
        }
		return list;
    	
	}
	
	public Collection<? extends Light> loadNewLights(int xOffset, int yOffset) {
		List<Light> lights = new ArrayList<Light>();
		
    	for (MapObject object : tiledMap.getLayers().get(NAME_GROUP_LIGHTS).getObjects()) {
    		RectangleMapObject x = (RectangleMapObject) object;
    		
    		lights.add(new Light(
                    x.getRectangle().x + xOffset,
                    x.getRectangle().y + yOffset,
                    Integer.parseInt(x.getProperties().get("size", String.class)),
                    Float.parseFloat(x.getProperties().get("variation", "0", String.class)),
                    Game.assets.getColor(x.getProperties().get("color", String.class))
    				));
    	}
		
		return lights;
	}
	
	public boolean blockedNode(int tx, int ty) {
		if ((tx < 0) || (ty < 0) || (tx >= getWidthInNodes()) || (ty >= getHeightInNodes()))
			return true;
		return this.blocked[(int) Math.floor(tx / World.NODE_FACTOR)][(int) Math.floor(ty / World.NODE_FACTOR)];
	}
	
	public boolean blockedTile(int tx, int ty) {
		if ((tx < 0) || (ty < 0) || (tx >= width) || (ty >= height))
			return false;
		return this.blocked[tx][ty];
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	/**
	 * @return Width in nodes
	 */
	public int getWidthInNodes() {
		return width * World.NODE_FACTOR;
	}

	/**
	 * @return Height in nodes
	 */
	public int getHeightInNodes() {
		return height * World.NODE_FACTOR;
	}

	public Color getIllumination() {
		return illumination;
	}

	public MeteoInfo getMeteoInfo() {
		return meteo;
	}
    
	
    /**
     * Info class for Map
     *
     * @author Andrea
     */
    public static class MapInfo {
        
		public final int id;
        public final String path;
        public final boolean indoor;
    	public String illumination;
        public final List<MapLink> links;
        public final MeteoInfo meteo;
        
        public MapInfo(int id, String path, boolean indoor, String illumination, List<MapLink> links, MeteoInfo meteo) {
        	this.id = id;
            this.path = GlobalVar.MAP_PATH + path;
            this.indoor = indoor;
            this.illumination = illumination;
            this.links = links;
            this.meteo = meteo;
        }
        
        /**
         * Tell to the asset manager to load the assets needed by this map
         */
        public void requestAssets() {
        	Game.assets.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        	Game.assets.load(path, TiledMap.class);
        }
    }
    
    /*
     * @author Andrea
     */
    public static class MapLink {
    	
    	private Direction dir;
    	private int destination;
    	
		public MapLink(Direction dir, int destination) {
			this.dir = dir;
			this.destination = destination;
		}

		public Direction getDir() {
			return dir;
		}

		public int getDestination() {
			return destination;
		}
    }

}
