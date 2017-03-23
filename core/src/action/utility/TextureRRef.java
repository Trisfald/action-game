package action.utility;

/**
 * Reference abtraction used to retrieve a texture region
 * 
 * @author Andrea
 */
public class TextureRRef {
	
	public final String atlasName;
	public final String regionName;
	
	public TextureRRef(String atlasName, String regionName) {
		this.atlasName = atlasName;
		this.regionName = regionName;
	}
	
	/**
	 * Create a TextureRRef from a code (atlasName,regionName)
	 */
	public TextureRRef(String code) {
		String[] ss = code.split(",");
		this.atlasName = ss[0];
		this.regionName = ss[1];
	}

}
