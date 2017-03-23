package action.ui;

import action.core.Game;
import action.entity.being.Player;
import action.item.ItemStatusSlot;
import action.item.StatusItem;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Visual indicator of the armor's condition
 * 
 * @author Andrea
 */
public class ArmorIndicator {
	
	private TextureRegion texture;
	private Color tint = new Color();
	
	public ArmorIndicator() {
		texture = Game.assets.getTextureRegion("data/ui/ui.pack", "armor");
	}
	
	public void draw(Batch batch, float x, float y) {
		batch.setColor(tint);
		batch.draw(texture, x, y);
		batch.setColor(Color.WHITE);
	}
	
	public void update(Player player, float delta) {
		StatusItem item = player.getInv().getItem(ItemStatusSlot.TORSO);
		/* Don't display if no item is present */
		if (item == null) {
			tint.a = 0;
			return;
		}
		float dmgRatio = 1 - item.getHpRatio();
		/* Compute a color tint depending on the item's hp */
		if (dmgRatio <= 0.25f) {
			tint.set(1, 1, 1, 1);
			tint.lerp(Color.YELLOW, dmgRatio/0.25f);
		}
		else {
			tint.set(Color.YELLOW);
			tint.lerp(Color.RED, (dmgRatio-0.25f)/0.75f);
		}
	}

}
