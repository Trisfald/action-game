package action.database.resolvers;

import action.database.Database;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.ReflectionException;

/**
 * Loader for database instances
 * 
 * @author Andrea
 */
public class DatabaseLoader extends AsynchronousAssetLoader<Database, DatabaseLoader.DatabaseParameter>{
	
	public DatabaseLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	Database db;

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, DatabaseParameter parameter) {
		db = null;

		try {
			Constructor constructor = ClassReflection.getConstructor(parameter.clazz, FileHandle.class);
			db = (Database) constructor.newInstance(file);
		} catch (ReflectionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Database loadSync(AssetManager manager, String fileName, FileHandle file, DatabaseParameter parameter) {
		Database db = this.db;
		this.db = null;
		return db;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, DatabaseParameter parameter) {
		return null;
	}

	static public class DatabaseParameter extends AssetLoaderParameters<Database> {
		
		/** Class of the database */
		public Class<?> clazz;
		
		public DatabaseParameter(Class<?> clazz) {
			this.clazz = clazz;
		}

	}
}
