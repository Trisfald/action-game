package action.entity.being;


import action.combat.HitLevel;
import action.combat.Knockback;
import action.combat.effect.ChargeTrigger;
import action.combat.effect.Effect;
import action.core.Game;
import action.entity.Entity;
import action.entity.appearance.Appearance;
import action.entity.appearance.FakeAppearance;
import action.hitbox.DangerZone;
import action.interfaces.GlobalVar;
import action.utility.form.Form;
import action.utility.form.Form.FormLoader;
import action.utility.geom.Shape;
import action.utility.interaction.Interaction;
import action.world.Faction;
import action.world.World;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Abstract class for living entities and simple objects
 *
 * @author Andrea
 */
public abstract class AbstractBeing implements Entity {
    
    protected Vector2 position;
    protected Form form;
    protected World world;
    protected Faction faction;
    
    public AbstractBeing(float x, float y, FormLoader form, World world, Faction faction) {
    	position = new Vector2(x, y);
        this.form = form.load();
        this.world = world;
        this.faction = faction;
    }
    
    protected Form getForm() {
    	return form;
    }
    
    @Override
	public float getX() {
        return position.x;
    }
    
    @Override
	public float getY() {
        return position.y;
    }
    
    @Override
	public float getTopY() {
    	return form.getShape(position.x, position.y).getMaxY();
    }
    
    @Override
	public Vector2 getPos() {
    	return new Vector2(position.x, position.y);
    }
    
    @Override
	public float getWidth() {
        return form.getWidth();
    }
    
    @Override
	public float getHeight() {
    	return form.getHeight();
    }
    
    @Override
	public float getPerspectiveHeight() {
    	return form.getPerspectiveHeight();
    }
    
    @Override
	public float getCenterX() {
        return form.getCenterX(position.x);
    }
    
    @Override
	public float getCenterY() {
        return form.getCenterY(position.y);
    }
    
    @Override
	public float getPerspectiveCenterY() {
        return form.getPerspectiveCenterY(position.y);
    }
    
    @Override
	public Faction getFaction() {
        return this.faction;
    }
    
    @Override
	public final void draw(SpriteBatch batch) {
    	drawGfx(batch);
    	/* Disable shader for rendering the UI */
		batch.setShader(null);
    	drawUi(batch);
    	batch.setShader(Game.shader);
    }
    
    /**
     * Draw the graphics components
     */
    protected void drawGfx(SpriteBatch batch) {
    	
    }
    
    /**
     * Draw the ui components
     */
    protected void drawUi(SpriteBatch batch) {

    }
    
    @Override
	public void setPos(float x, float y) {
    	position.set(x, y);
    }
    
    @Override
	public void updatePos(float dx, float dy) {
    	position.add(dx, dy);
    }

    @Override
	public float computeDistance(Entity target) {
        return computeDistance(target.getShape());
    }
    
    @Override
	public float computeDistance(Shape shape) {
    	double delta = Math.pow(getCenterX()-shape.getCenterX(), 2) + 
        		Math.pow(getCenterY()-shape.getCenterY(), 2);
    	if (delta <= 0f)
    		return 0f;
        return (float) Math.sqrt(delta);       
    }
    
    @Override
	public float computeBorderDistance(Entity target) {
    	double dst2 = Math.pow(getCenterX()-target.getCenterX(), 2) + 
        		Math.pow(getCenterY()-target.getCenterY(), 2);
    	if (dst2 <= 0f)
    		return 0f;
    	float result = (float) Math.sqrt(dst2) - target.getAverageRadius() - getAverageRadius();
        if (result <= 0f)
        	return 0f;
        return result;
    }
    
    @Override
	public Shape getPerspectiveShape(float dx, float dy) {
    	return form.getPerspectiveShape(position.x + dx, position.y + dy);
    }
    
	@Override
	public Shape getPerspectiveShape() {
		return form.getPerspectiveShape(position.x, position.y);
    }
    
    @Override
	public Shape getShape(float dx, float dy) {
    	return form.getShape(position.x + dx, position.y + dy);
    }
    
	@Override
	public Shape getShape() {
    	return form.getShape(position.x, position.y);
    }
	
	@Override
	public float getPerspectiveRatio() {
		return form.getPerspectiveRatio();
	}
    
    @Override
	public boolean isAggressiveTo(Entity target) {
    	/* Check for non-factioned entities */
    	if ((target.getFaction() == null) || getFaction() == null)
    		return false;
    	return faction.isAggresiveTo(target.getFaction());
    }
    
    @Override
	public void initialize() {
    	
    }
    
    @Override
	public void update(float delta) {
    	
    }
    
    @Override
	public World getWorld() {
    	return world;
    }
    
	@Override
	public void apply(Effect effect) {
		
	}

	@Override
	public void remove(Effect effect) {

	}
	
    @Override
	public void takeEffect(Effect effect) {

    }
    
	@Override
	public void takeEffect(Effect effect, AlterationTarget target) {
		takeEffect(effect);
	}
	

	@Override
	public void consumeCharge(ChargeTrigger trigger) {

	}
	
	@Override
	public boolean isBlockingMov() {
		return true;
	}

	@Override
	public boolean isHittable(HitLevel level) {
		return form.isHittable(level);
	}
	
	@Override
	public void takeKnockback(Knockback knockback) {
		
	}
	
	@Override
	public void shiftPosition(float dx, float dy) {
		position.add(dx, dy);
	}
	
	/**
	 * @return The value of radius that in average represents this entity
	 */
	@Override
	public float getAverageRadius() {
		return form.getAverageRadius();
	}
    
	@Override
	public Appearance getAppearance() {
		return new FakeAppearance();
	}
	
	@Override
	public boolean sustainEffect(float cost) {
		return false;
	}

	@Override
	public void removeToggleEffects() {
		
	}
	
    @Override
	public boolean hasActiveToggleEffects() {
    	return false;
    }
    
	@Override
	public float getKnockbackResist() {
		return getMassFactor();
	}
	
	@Override
	public void die() {
		
	}

	@Override
	public Interaction getInteraction() {
		return null;
	}
	
	@Override
	public DangerZone getDangerZone() {
		return null;
	}
	
	@Override
	public void dispose() {
		
	}
	
	@Override
	public boolean isOnCamera(Camera camera) {
		float x = camera.position.x - camera.viewportWidth/2;
		float y = camera.position.y - camera.viewportHeight/2;

		if (position.x+getWidth()+GlobalVar.ONCAMERA_MARGIN < x) return false;
		if (position.x-GlobalVar.ONCAMERA_MARGIN > x+camera.viewportWidth) return false;
		if (position.y+getHeight()+GlobalVar.ONCAMERA_MARGIN < y) return false;
		if (position.y-GlobalVar.ONCAMERA_MARGIN > y+camera.viewportHeight) return false;
		
		return true;
	}
	
}
