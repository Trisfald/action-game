package action.sound;

import com.badlogic.gdx.audio.Sound;

import action.core.Game;
import action.interfaces.GlobalVar;

/**
 * Manage sounds in general
 * 
 * @author Andrea
 */
public class SoundManager {
	
	public MusicBox music = new MusicBox();
	private float sfxVolume = GlobalVar.VOLUME_SFX;
	
	public float getSfxVolume() {
		return sfxVolume;
	}
	public void setSfxVolume(float sfxVolume) {
		this.sfxVolume = sfxVolume;
	}
	
	/**
	 * @return The sound to be played when a block is successful
	 */
	public Sound getBlockSound() {
		return Game.assets.get("data/sound/block.ogg", Sound.class);
	}
	
}
