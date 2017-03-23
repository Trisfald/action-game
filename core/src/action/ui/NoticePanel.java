package action.ui;

import com.badlogic.gdx.scenes.scene2d.actions.AfterAction;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

/**
 * Displays in game messages
 *
 * @author Andrea
 */
public class NoticePanel extends VerticalGroup {
	
	private float x;
	private float y;
	private Skin skin;
	
	private static final float NOTICE_TIME = 6;
	private static final float NOTICE_VANISH_TIME = 1.5f;
	
	public NoticePanel(Skin skin) {
		this.skin = skin;
		left();
	}
	
	public void addNotice(String text) {
		if (text == null)
			return;
		if (text.isEmpty())
			return;
		addActor(new Notice(text, skin));
		pack();
	}
	
	@Override
	public void layout() {
		super.layout();
		setPosition(x, y-getHeight());
	}
	
	public void setTargetPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	
	/**
	 * @author Andrea
	 */
	private class Notice extends Label {

		public Notice(CharSequence text, Skin skin) {
			super(text, skin);
			startVanish();
		}
		
		/**
		 * Make the notice disappear after some time
		 */
		private void startVanish() {
			/* Prepare the delay action that will contain the alpha action */
			DelayAction delay = new DelayAction();
			delay.setDuration(NOTICE_TIME);	
			/* Make an action for transparency and add it to the delay action */
			AlphaAction alpha = new AlphaAction();
			alpha.setAlpha(0);
			alpha.setDuration(NOTICE_VANISH_TIME);
			delay.setAction(alpha);
			addAction(delay);
			/* Make an action for removing the actor when all other actions are done*/
			AfterAction after = new AfterAction();
			RemoveActorAction remove = new RemoveActorAction();
			remove.setActor(this);
			after.setAction(remove);
			addAction(after);
		}
		
	}

}
