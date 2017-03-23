package action.ui;

import action.core.Game;
import action.entity.being.Player;
import action.entity.being.creature.Stats.CreatureStatType;
import action.ui.filledbar.FilledBar;
import action.ui.filledbar.FilledBar.FilledBarMargin;
import action.utility.Body.BodyStatType;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

/**
 * Overview of the player's status
 * 
 * @author Andrea
 */
public class StatusPanel extends Widget {
	
	private Player player;
	private FilledBar lifebar;
	private FilledBar staminabar;
	private ArmorIndicator armor;
	
	private static final int BAR_WIDTH = 200;
	private static final int BAR_HEIGHT = 10;
	private static final int BARS_DISTANCE_Y = 4;
	private static final int ARMOR_BAR_DISTANCE_X = 12;
	private static final int ARMOR_SIZE = 32;
	
	public StatusPanel() {
		createBars();
		armor = new ArmorIndicator();
		pack();
	}
	
	public void initialize(Player player) {
		this.player = player;
	}
	
	private void createBars() {
		lifebar = new FilledBar(BAR_WIDTH, BAR_HEIGHT, new FilledBarMargin(1),
				Game.assets.getTextureRegion("data/ui/ui.pack", "empty"), 
				Game.assets.getTextureRegion("data/ui/ui.pack", "life"),
				false);		

		staminabar = new FilledBar(BAR_WIDTH, BAR_HEIGHT,  new FilledBarMargin(1),
				Game.assets.getTextureRegion("data/ui/ui.pack", "empty"), 
				Game.assets.getTextureRegion("data/ui/ui.pack", "stamina"),
				false);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		armor.draw(batch, getX(), getY()-ARMOR_SIZE);
		lifebar.draw(batch);
		staminabar.draw(batch);
	}

	@Override
	public void act(float delta) {
		armor.update(player, delta);
		lifebar.setFilled(player.getStats().getStat(BodyStatType.HP).ratio());
		staminabar.setFilled(player.getStats().getStat(CreatureStatType.STAMINA).ratio());
	}
	
	@Override
	public float getPrefHeight() {
		return getMinHeight();
	}
	
	@Override
	public float getMinHeight() {
		return Math.max(ARMOR_SIZE, BAR_HEIGHT*2+BARS_DISTANCE_Y);
	}
	
	@Override
	public float getPrefWidth() {
		return getMinHeight();
	}
	
	@Override
	public float getMinWidth() {
		return Math.max(ARMOR_SIZE, BAR_WIDTH);
	}
	
	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		lifebar.setPosition(getX()+ARMOR_SIZE+ARMOR_BAR_DISTANCE_X, 
				getY()-BAR_HEIGHT-(ARMOR_SIZE-BAR_HEIGHT*2-BARS_DISTANCE_Y)/2);
		staminabar.setPosition(getX()+ARMOR_SIZE+ARMOR_BAR_DISTANCE_X, 
				getY()-2*BAR_HEIGHT-(ARMOR_SIZE-BAR_HEIGHT*2-BARS_DISTANCE_Y)/2-BARS_DISTANCE_Y);
	}
	
}
