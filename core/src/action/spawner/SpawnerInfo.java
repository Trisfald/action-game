package action.spawner;

import action.entity.Entity;
import action.interfaces.StatsMemory;
import action.spawner.BeamLauncher.BeamLauncherInfo;
import action.spawner.ProjectileLauncher.ProjectileLauncherInfo;
import action.spawner.Spawner.SpawnerType;
import action.world.World;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader.Element;

public abstract class SpawnerInfo {

	public final SpawnerType type;
	
	SpawnerInfo(SpawnerType type) {
		this.type = type;
	}
	
	/**
	 * @return The range of effect of the being spawned
	 */
	public abstract float getRange();
	
	/**
	 * Create a spawner
	 */
	public Spawner spawn(float x, float y, Vector2 dirV, World world, Entity source, StatsMemory stats) {
		switch (type) {
			case PROJECTILE:
				return new ProjectileLauncher(x, y, dirV, world, (ProjectileLauncherInfo) this, source, stats);
			case BEAM:
				return new BeamLauncher(x, y, dirV, world, (BeamLauncherInfo) this, source, stats);
		}
		return null;
	}
	
    public static SpawnerInfo loadFromXml(Element element) {
    	if (element == null)
    		return null;

		switch (SpawnerType.valueOf(element.getAttribute("type"))) {
			case BEAM:
				return BeamLauncherInfo.loadFromXml(element);
			case PROJECTILE:
				return ProjectileLauncherInfo.loadFromXml(element);
			default: 
				return null;
		}
	}
	
}
