package action.entity.being.area;

import java.util.Iterator;

import action.combat.Damage;
import action.combat.Impact;
import action.entity.being.AbstractBeing;
import action.hitbox.DangerZone;
import action.utility.Body;
import action.utility.Body.BodyInfo;
import action.utility.Body.BodyStatType;
import action.utility.form.Form.FormLoader;
import action.world.World;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Area extends AbstractBeing {

    protected Impact impact;
    protected Body body;
    protected DangerZone dangerZone;
	
	public Area(float x, float y, World world, FormLoader form, BodyInfo body, Impact impact) {
		super(x, y, form, world, null);
		this.impact = impact;
		this.body = new Body(body);
	}
	
	/**
	 * Kill the entity
	 */
	protected void suicide() {
		body.getStat(BodyStatType.HP).setValueToZero();
	}
	
	@Override
	public boolean isAlive() {
		return !body.getStat(BodyStatType.HP).isZero();
	}

	@Override
	public void takeImpact(Impact impact) {
		for (Iterator<Damage> iter = impact.getDamages().iterator(); iter.hasNext() && isAlive();) {
			if (takeDamage(iter.next()))
				iter.remove();
		}
	}

	@Override
	public boolean takeDamage(Damage damage) {
		/* Reduce the damage */
		damage.reduce(body.getResistanceValue(damage.getDmgType()), 
				body.getReductionValue(damage.getDmgType()), true);
		/* Update HP */
		body.getStat(BodyStatType.HP).decreaseValue(damage.getPower());
		return !damage.exists();
	}	
	
	@Override
	protected void drawUi(SpriteBatch batch) {
		
	}
	
	@Override
	public boolean isBlockingMov() {
		return false;
	}
		
	@Override
	public float getMassFactor() {
		return body.getStat(BodyStatType.MASS).factor();
	}
	
	@Override
	public DangerZone getDangerZone() {
		return dangerZone;
	}
	
	@Override
	public void die() {
		if (dangerZone == null)
			return;
		dangerZone.setActive(false);
	}
	
	@Override
	public boolean exist() {
		return isAlive();
	}
	
}
