package action.spawner;

import action.combat.Impact;
import action.core.Game;
import action.entity.Entity;
import action.entity.being.area.Projectile;
import action.entity.being.area.Projectile.ProjectileInfo;
import action.entity.being.area.Projectile.ProjectileLoader;
import action.interfaces.StatsMemory;
import action.utility.timer.Timer;
import action.world.World;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Spawner for projectiles
 * 
 * @author Andrea
 */
public class ProjectileLauncher extends BasicLauncher {

	private ProjectileLoader loader;
	/** Keeps info about the projectile */
	private ProjectileInfo proj;
	private Impact impact;
	/** Number of volleys */
	private int rounds;
	/** How many proj per volley */
	private int amount;
	/** Timer for how much seconds pass between two volleys */
	private Timer timer;
	
	public ProjectileLauncher(float x, float y, Vector2 dirV, World world, ProjectileLoader loader, Entity source, Impact impact, int rounds,
			float interval, int amount) {
		super(x, y, dirV, source, world);
		proj = Game.assets.getProjectileInfo(loader.projectile);
		this.loader = loader;
		this.impact = impact;
		this.rounds = rounds;
		this.amount = amount;
		timer = new Timer(Math.max(interval, computeInterval()));
		/* The first volley starts right away */
		timer.setCounter(timer.getLength());
	}
	
	public ProjectileLauncher(float x, float y, Vector2 dirV, World world, ProjectileLauncherInfo info, Entity source, StatsMemory stats) {
		this(x, y, dirV, world, info.loader, source, buildImpact(info.loader, stats, dirV), info.rounds, info.interval, info.amount);
	}
	
	private static Impact buildImpact(ProjectileLoader loader, StatsMemory stats, Vector2 dir) {
		return new Impact(computeDamages(loader.damages, stats), computeEffects(loader.effects, stats), 
				computeKnockback(loader.knockback, dir.nor()), null, loader.block, loader.crushPower, loader.selective);
	}

	@Override
	public void update(float delta) {
		timer.update(delta);
		if (rounds > 0 && timer.expired()) {
			launchRound(world);
			timer.restart();
			rounds--;
		}
	}
	
	/**
	 * @return The minimum interval such that one volley does not collide with the previous one
	 */
	private float computeInterval() {
		/* Multiply by a correction value to make the volleys a little bit distant */
		return (float) Math.ceil(proj.form.getHeight() / (loader.speed) * 1.1);
	}
	
	/**
	 * Fires a volley
	 */
	private void launchRound(World world) {
		/* Get the real angle in degrees */
		float angle = dirV.angle();
		/* Compute starting coordinates */
		float x = (this.x - proj.form.getWidth() / 2 +
				/* Correct the start position in length */
				proj.form.getHeight() / 2 * MathUtils.cosDeg(angle));
		float y = (this.y - proj.form.getWidth() +
				/* Correct the start position in length */
				proj.form.getHeight() / 2 * MathUtils.sinDeg(angle));
		
		/* Correct start for multiple projectiles */
		if (amount > 1) {
			x -= (amount-1) * proj.form.getWidth() / 2 * MathUtils.sinDeg(angle);
			y += (amount-1) * proj.form.getWidth() / 2 * MathUtils.cosDeg(angle);
		}
				
		/* Quick way if there's only one projectile */
		if (amount == 1) {
			launchProjectile(world, x, y, dirV);
			return;
		}

		for (int i = 0; i < amount; i++) {
			launchProjectile(world, x, y, dirV);
			x += proj.form.getWidth() * MathUtils.sinDeg(angle);
			y -= proj.form.getWidth() * MathUtils.cosDeg(angle);
		}
	}
	
	/**
	 * Fires a single projectile
	 */
	private void launchProjectile(World world, float x, float y, Vector2 dirV) {
		world.spawnEntity(new Projectile(x, y, dirV, world, loader, source, impact));
	}

	@Override
	public boolean isDone() {
		return rounds == 0;
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class ProjectileLauncherInfo extends SpawnerInfo {

		public final ProjectileLoader loader;
		public final int rounds;
		public final float interval;
		public final int amount;
		
		public ProjectileLauncherInfo(ProjectileLoader loader, int rounds, float interval, int amount) {
			super(SpawnerType.PROJECTILE);
			this.loader = loader;
			this.rounds = rounds;
			this.interval = interval;
			this.amount = amount;
		}
		
		@Override
		public float getRange() {
			return loader.range;
		}
		
		public static ProjectileLauncherInfo loadFromXml(Element element) {
			return new ProjectileLauncherInfo(
					ProjectileLoader.loadFromXml(element),
					element.getIntAttribute("rounds"),
					element.getFloatAttribute("interval"),
					element.getIntAttribute("amount")
					);
		}

	}
}
