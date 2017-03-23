package action.sound;

import action.core.Game;
import action.interfaces.GlobalVar;

import com.badlogic.gdx.audio.Music;

/**
 * Manage music tracks
 * 
 * @author Andrea
 */
public class MusicBox {

	private Music exploration;
	private float volume;

	public MusicBox() {
		exploration = Game.assets.get("data/music/veridia battle 2.ogg", Music.class);
		exploration.setLooping(true);
		setVolume(GlobalVar.VOLUME_MUSIC);
	}
	
	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
		exploration.setVolume(volume);
	}
	
	public void playExploration() {
		exploration.play();
	}
	
	public void pauseExploration() {
		exploration.pause();
	}

}
