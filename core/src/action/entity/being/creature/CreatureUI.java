package action.entity.being.creature;

import action.core.Game;
import action.ui.filledbar.FadingBar;
import action.ui.filledbar.FilledBar.FilledBarMargin;
import action.utility.Body.BodyStatType;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * An utility to manage UI of Creatures on the map
 *
 * @author Andrea
 */
public class CreatureUI {

	private Creature owner;
	private FadingBar lifeBar;
	private FadingBar chargeBar;
	private boolean visible = true;
	private boolean lifeBarEnabled = true;
	private boolean chargeBarEnabled = false;
	
	private static final int LIFEBAR_OFFY = 6;
	private static final int LIFEBAR_WIDTH = 36;
	private static final int LIFEBAR_HEIGHT = 4;
	private static final float LIFEBAR_ACTIVE_TIME = 4f;
	private static final float LIFEBAR_FADE_TIME = 1.5f;
	
	private static final int CHARGEBAR_OFFY = -5;
	private static final int CHARGEBAR_WIDTH = 36;
	private static final int CHARGEBAR_HEIGHT = 4;
	private static final float CHARGEBAR_ACTIVE_TIME = 0;
	private static final float CHARGEBAR_FADE_TIME = 1.5f;
	

	public CreatureUI(Creature owner) {
		this.owner = owner;
		
		/* Prepare lifebar */
		lifeBar = new FadingBar(LIFEBAR_WIDTH, LIFEBAR_HEIGHT, new FilledBarMargin(1),
				Game.assets.getTextureRegion("data/ui/ui.pack", "creaturebar.blank"), 
				Game.assets.getTextureRegion("data/ui/ui.pack", "creaturebar.life"),
				false, LIFEBAR_ACTIVE_TIME, LIFEBAR_FADE_TIME);
		lifeBar.init(owner.getStats().getStat(BodyStatType.HP).ratio());
		lifeBar.setVisible(false);
		
		/* Prepare chargebar */
		chargeBar = new FadingBar(CHARGEBAR_WIDTH, CHARGEBAR_HEIGHT, new FilledBarMargin(1),
				Game.assets.getTextureRegion("data/ui/ui.pack", "creaturebar.blank"), 
				Game.assets.getTextureRegion("data/ui/ui.pack", "creaturebar.charge"),
				false, CHARGEBAR_ACTIVE_TIME, CHARGEBAR_FADE_TIME);
		chargeBar.init(0);
		chargeBar.setVisible(false);
	}
	
	public void update(float delta) {
		if (lifeBarEnabled)
			lifeBar.update(owner.getStats().getStat(BodyStatType.HP).ratio(), delta);
		if (chargeBarEnabled)
			chargeBar.update(owner.getStats().getChargeProgress(), delta);
	}
	
	public void draw(SpriteBatch batch) {
		if (!visible)
			return;
		if (lifeBarEnabled)
			lifeBar.draw(batch, owner.getX() + (owner.getWidth() - LIFEBAR_WIDTH) / 2, 
					owner.getY() + owner.getHeight() + LIFEBAR_OFFY - lifeBar.getHeight());
		if (chargeBarEnabled)
			chargeBar.draw(batch, owner.getX() + (owner.getWidth() - CHARGEBAR_WIDTH) / 2,
					owner.getY() + CHARGEBAR_OFFY);
	}
	
	public void setLifeBarEnabled(boolean enabled) {
		this.lifeBarEnabled = enabled;
	}
	
	public void setChargeBarEnabled(boolean enabled) {
		this.chargeBarEnabled = enabled;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
}
