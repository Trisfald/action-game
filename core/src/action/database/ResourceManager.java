package action.database;



import java.io.IOException;

import action.ai.Ai.AiInfo;
import action.animation.Motion.MotionLoader;
import action.database.resolvers.DatabaseLoader;
import action.database.resolvers.DatabaseLoader.DatabaseParameter;
import action.diary.Quest.QuestInfo;
import action.entity.being.area.Beam.BeamInfo;
import action.entity.being.area.Explosion.ExplosionInfo;
import action.entity.being.area.Projectile.ProjectileInfo;
import action.entity.being.creature.Creature.CreatureInfo;
import action.item.Item.ItemInfo;
import action.spell.Spell;
import action.utility.TextureRRef;
import action.world.Faction;
import action.world.map.MapWrapper.MapInfo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader.ParticleEffectParameter;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * @author Andrea
 */
public class ResourceManager extends AssetManager {
	
	private TextureParameter textureParam;
	
	private static final String MapDB = "data/database/mapDB.xml";
	private static final String FactionDB = "data/database/factionDB.xml";
	private static final String ColorDB = "data/database/colorDB.xml";
	private static final String DialogDB = "data/database/dialogDB.xml";
	private static final String QuestDB = "data/database/questDB.xml";
	private static final String MotionDB = "data/database/motionDB.xml";
	private static final String ItemDB = "data/database/itemDB.xml";
	private static final String BeamDB = "data/database/beamDB.xml";
	private static final String ProjectileDB = "data/database/projectileDB.xml";
	private static final String ExplosionDB = "data/database/explosionDB.xml";
	private static final String SpellDB = "data/database/spellDB.xml";
	private static final String AiDB = "data/database/aiDB.xml";
	private static final String CreatureDB = "data/database/creatureDB.xml";
	private static final String resources = "data/resources.xml";
	
	public ResourceManager() {
		super();
		/* Set a loader for databases */
		setLoader(Database.class, new DatabaseLoader(new InternalFileHandleResolver()));
		/* Set texture loader parameter */
		textureParam = new TextureParameter();
		textureParam.minFilter = TextureFilter.Linear;
		textureParam.magFilter = TextureFilter.Linear;
	}
	
	public void load() {
		loadResources();
		loadDBs();
	}
	
	/**
	 * Load all normal resources
	 */
	private void loadResources() {
    	XmlReader reader = new XmlReader();
    	Element root;
		try {
			root = reader.parse(Gdx.files.internal(resources));
		    for (int i = 0; i < root.getChildCount(); i++) {
		    	loadResource(root.getChild(i));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load a single resource from an xml definition
	 */
	private void loadResource(Element element) {
		switch (element.getAttribute("type")) {
			case "textureatlas":
				load(element.getText(), TextureAtlas.class);
				break;
			case "music":
				load(element.getText(), Music.class);
				break;
			case "sound":
				load(element.getText(), Sound.class);
				break;
			case "particleeffect":
				ParticleEffectParameter param = new ParticleEffectParameter();
				param.atlasFile = element.getAttribute("atlas");
				load(element.getText(), ParticleEffect.class, param);
				break;
			case "texture":
				load(element.getText(), Texture.class, textureParam);
				break;
			default:
				break;
		}
	}

	/**
	 * Load all databases
	 */
	private void loadDBs() {
		load(MapDB, Database.class, new DatabaseParameter(MapDB.class));
		load(FactionDB, Database.class, new DatabaseParameter(FactionDB.class));
		load(ColorDB, Database.class, new DatabaseParameter(ColorDB.class));
		load(DialogDB, Database.class, new DatabaseParameter(DialogDB.class));
		load(QuestDB, Database.class, new DatabaseParameter(QuestDB.class));
		load(MotionDB, Database.class, new DatabaseParameter(MotionDB.class));
		load(ItemDB, Database.class, new DatabaseParameter(ItemDB.class));
		load(BeamDB, Database.class, new DatabaseParameter(BeamDB.class));
		load(ProjectileDB, Database.class, new DatabaseParameter(ProjectileDB.class));
		load(ExplosionDB, Database.class, new DatabaseParameter(ExplosionDB.class));
		load(SpellDB, Database.class, new DatabaseParameter(SpellDB.class));
		load(AiDB, Database.class, new DatabaseParameter(AiDB.class));
		load(CreatureDB, Database.class, new DatabaseParameter(CreatureDB.class));
	}
	
	public final synchronized MapInfo getMapInfo(int id) {
		return ((MapDB) get(MapDB, Database.class)).getElement(id);
	}
	
	public final synchronized String getDialog(int id) {
		return ((DialogDB) get(DialogDB, Database.class)).getElement(id);
	}
	
	public final synchronized Color getColor(String id) {
		return ((ColorDB) get(ColorDB, Database.class)).getElement(id);
	}
	
	public final synchronized Faction getFaction(int id) {
		return ((FactionDB)get(FactionDB, Database.class)).getElement(id);
	}
	
	public final synchronized QuestInfo getQuestInfo(int id) {
		return ((QuestDB) get(QuestDB, Database.class)).getElement(id);
	}
	
	public final synchronized MotionLoader getMotionLoader(int id) {
		return ((MotionDB) get(MotionDB, Database.class)).getElement(id);
	}
	
	public final synchronized TextureRegion getTextureRegion(TextureRRef ref) {
		return get(ref.atlasName, TextureAtlas.class).findRegion(ref.regionName);
	}
	
	public final synchronized TextureRegion getTextureRegion(String atlas, String region) {
		return get(atlas, TextureAtlas.class).findRegion(region);
	}
	
	public final synchronized ItemInfo getItemInfo(int id) {
		return ((ItemDB) get(ItemDB, Database.class)).getElement(id);
	}
	
	public final synchronized BeamInfo getBeamInfo(int id) {
		return ((BeamDB) get(BeamDB, Database.class)).getElement(id);
	}
	
	public final synchronized ProjectileInfo getProjectileInfo(int id) {
		return ((ProjectileDB) get(ProjectileDB, Database.class)).getElement(id);
	}
	
	public final synchronized ExplosionInfo getExplosionInfo(int id) {
		return ((ExplosionDB) get(ExplosionDB, Database.class)).getElement(id);
	}

	public final synchronized Spell getSpell(int id) {
		return ((SpellDB) get(SpellDB, Database.class)).getElement(id);
	}
	
	public final synchronized AiInfo getAi(int id) {
		return ((AiDB) get(AiDB, Database.class)).getElement(id);
	}
	
	public final synchronized CreatureInfo getCreatureInfo(int id) {
		return ((CreatureDB) get(CreatureDB, Database.class)).getElement(id);
	}
	
}