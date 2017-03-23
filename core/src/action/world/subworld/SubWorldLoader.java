package action.world.subworld;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import action.core.Game;
import action.entity.Entity;
import action.utility.enums.Direction;
import action.world.World;
import action.world.ambience.Light;
import action.world.map.MapWrapper;
import action.world.map.MapWrapper.MapInfo;
import action.world.map.MapWrapper.MapLink;
import action.world.map.Transition;
import action.world.subworld.SubWorld.SubWorldInfo;

public class SubWorldLoader implements Callable<SubWorldInfo> {

	private Transition transition;
	private World world;
	
	public SubWorldLoader(World world, Transition transition) {
		this.world = world;
		this.transition = transition;
	}
	
	/**
	 * Start loading the assets through the manager
	 */
	public void requestAssets() {
		MapInfo info = Game.assets.getMapInfo(transition.getDstMap());
		info.requestAssets();
		for (MapLink x : info.links)
			Game.assets.getMapInfo(x.getDestination()).requestAssets();
	}

	@Override
	public SubWorldInfo call() throws Exception {
		MapWrapper map;
		MapInfo info = Game.assets.getMapInfo(transition.getDstMap());
		Map<Direction, MapWrapper> maps = new EnumMap<Direction, MapWrapper>(Direction.class);
		List<Entity> entities = new ArrayList<Entity>();
		List<Light> lights = new ArrayList<Light>();
		
		/* Add central map */
		map = new MapWrapper(info, world.getBatch());
		maps.put(Direction.CENTER, map);

		if (info.links.size() == 0) {
			/* Single map mode */
			entities.addAll(map.loadNewEntities(0, 0, world));
			lights.addAll(map.loadNewLights(0, 0));
			
			return new SubWorldInfo(
					maps,
					entities,
					lights,
					transition.getDstX(),
					transition.getDstY()
					);
		}
		else {
			/* Multi map mode */
			entities.addAll(map.loadNewEntities(World.MAP_SIZE_EFFECTIVE, World.MAP_SIZE_EFFECTIVE, world));
			lights.addAll(map.loadNewLights(World.MAP_SIZE_EFFECTIVE, World.MAP_SIZE_EFFECTIVE));
			/* Add all other maps */
			for (MapLink x : info.links) {
				map =  new MapWrapper(Game.assets.getMapInfo(x.getDestination()), world.getBatch());
				entities.addAll(map.loadNewEntities(x.getDir().getMapOffsetXEff(), 
						(x.getDir().getMapOffsetYEff()), world));
				lights.addAll(map.loadNewLights(x.getDir().getMapOffsetXEff(), 
						(x.getDir().getMapOffsetYEff())));
				maps.put(x.getDir(), map);
			}
			
			return new SubWorldInfo(
					maps,
					entities,
					lights,
					transition.getDstX() + World.MAP_SIZE_EFFECTIVE,
					transition.getDstY() + World.MAP_SIZE_EFFECTIVE
					);
		}
	}	
}
