package action.world.subworld;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import action.core.Game;
import action.entity.Entity;
import action.utility.enums.Direction;
import action.world.World;
import action.world.ambience.Light;
import action.world.map.MapWrapper;
import action.world.map.MapWrapper.MapLink;
import action.world.subworld.ShiftLoader.ShiftInfo;

public class ShiftLoader implements Callable<ShiftInfo> {

	private World world;
	private Direction newCenter;
	private Map<Direction, MapWrapper> maps;
	
	public ShiftLoader(World world, Direction newCenter, Map<Direction, MapWrapper> maps) {
		this.world = world;
		this.newCenter = newCenter;
		this.maps = maps;
	}
	
	/**
	 * Start loading the assets through the manager
	 */
	public void requestAssets() {
		MapWrapper newCentral = maps.get(newCenter);
		/* Get the links of the new central map and request their assets */
		List<MapLink> links = Game.assets.getMapInfo(newCentral.getId()).links;
		for (MapLink x : links) {
			Game.assets.getMapInfo(x.getDestination()).requestAssets();
		}
	}

	@Override
	public ShiftInfo call() throws Exception {
		MapWrapper newCentral, newMap;
		Map<Integer, MapWrapper> mapLoaded = new HashMap<Integer, MapWrapper>();
		Map<Direction, MapWrapper> newMaps = new EnumMap<Direction, MapWrapper>(Direction.class);
		List<MapWrapper> remove = new ArrayList<MapWrapper>();
		Set<Integer> newMapsIds = new HashSet<Integer>();
		List<Entity> entities = new ArrayList<Entity>();
		List<Light> lights = new ArrayList<Light>();
	
		newCentral = maps.get(newCenter);
		newMaps.put(Direction.CENTER, newCentral);
		
		/* Construct an hashmap containing all the map already loaded */
		for (MapWrapper x : maps.values())
			mapLoaded.put(x.getId(), x);

		/* Get the links of the new central map and form a new hashmap loading the maps that are not yet loaded */
		List<MapLink> links = Game.assets.getMapInfo(newCentral.getId()).links;
		for (MapLink x : links) {
			newMapsIds.add(x.getDestination());
			if (mapLoaded.containsKey(x.getDestination()))
				newMaps.put(x.getDir(), mapLoaded.get(x.getDestination()));
			else {
				newMap = new MapWrapper(Game.assets.getMapInfo(x.getDestination()), world.getBatch());
				newMaps.put(x.getDir(), newMap);
				entities.addAll(newMap.loadNewEntities(x.getDir().getMapOffsetXEff(), 
						(x.getDir().getMapOffsetYEff()), world));
				lights.addAll(newMap.loadNewLights(x.getDir().getMapOffsetXEff(), 
						(x.getDir().getMapOffsetYEff())));
			}
		}
		/* Get the maps that must be disposed */
		for (Entry<Integer, MapWrapper> x : mapLoaded.entrySet()) {
			if (!newMapsIds.contains(x.getKey()))
				remove.add(x.getValue());
		}

		return new ShiftInfo(newCenter, newMaps, remove, entities, lights);
	}
	
	
	public class ShiftInfo {
	
		/** Direction of the shift */
		public final Direction dir;
		public final Map<Direction, MapWrapper> maps;
		/** Loaded maps that won't be used anymore */
		public final List<MapWrapper> toRemove;
		public final List<Entity> entities;
		public final List<Light> lights;
		
		public ShiftInfo(Direction dir, Map<Direction, MapWrapper> maps, List<MapWrapper> toRemove, List<Entity> entities,
				List<Light> lights) {
			this.dir = dir;
			this.maps = maps;
			this.toRemove = toRemove;
			this.entities = entities;
			this.lights = lights;
		}
		
	}

}
