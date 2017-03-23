package action.spawner;

import action.combat.Impact;
import action.core.Game;
import action.entity.Entity;
import action.entity.being.area.Beam;
import action.entity.being.area.Beam.BeamLoader;
import action.interfaces.StatsMemory;
import action.world.World;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Spawner for beams
 * 
 * @author Andrea
 */
public class BeamLauncher extends BasicLauncher {

	private float x;
	private float y;
	private BeamLoader loader;
	private Impact impact;
	/** True if finished */
	private boolean done;
	
	public BeamLauncher(float x, float y, Vector2 dirV, World world, BeamLoader loader, Entity source, Impact impact) {
		super(x, y, dirV, source, world);
		this.x = x;
		this.y = y;
		this.loader = loader;
		this.impact = impact;
	}
	
	public BeamLauncher(float x, float y, Vector2 dirV, World world, BeamLauncherInfo info, Entity source, Impact impact) {
		this(x, y, dirV, world, info.loader, source, impact);
	}
	
	public BeamLauncher(float x, float y, Vector2 dirV, World world, BeamLauncherInfo info, Entity source, StatsMemory stats) {
		this(x, y, dirV, world, info.loader, source, buildImpact(info.loader, stats, dirV));
	}
	
	/**
	 * Assembly an impact starting from the creature stats and the beam stats
	 */
	private static Impact buildImpact(BeamLoader loader, StatsMemory stats, Vector2 dir) {
		return new Impact(computeDamages(loader.damages, stats), computeEffects(loader.effects, stats), 
				computeKnockback(loader.knockback, dir.nor()), null, loader.block, loader.crushPower, loader.selective);
	}

	@Override
	public void update(float delta) {
		if (!done) {
			launch(world);
			done = true;
		}
	}
	
	private void launch(World world) {
		float width = Game.assets.getBeamInfo(loader.beam).form.getWidth();
		/* Compute starting coordinates */
		float x = this.x - width / 2;
		float y = this.y;
		
		launchBeam(world, x, y, dirV);
	}
	
	private void launchBeam(World world, float x, float y, Vector2 dirV) {
		world.spawnEntity(new Beam(x, y, dirV, world, loader, impact));
	}
	
	@Override
	public boolean isDone() {
		return done;
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class BeamLauncherInfo extends SpawnerInfo {
		
		public final BeamLoader loader;

		public BeamLauncherInfo(BeamLoader loader) {
			super(SpawnerType.BEAM);
			this.loader = loader;
		}

		@Override
		public float getRange() {
			return loader.growth * loader.duration / 1000;
		}
		
		public static BeamLauncherInfo loadFromXml(Element element) {
			return new BeamLauncherInfo(BeamLoader.loadFromXml(element));
		}

	}
	
}
