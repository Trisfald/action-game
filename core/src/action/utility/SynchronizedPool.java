package action.utility;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * A synchronized pool for multithread use
 * 
 * @author Andrea
 */
public abstract class SynchronizedPool<T> extends Pool<T> {

	public SynchronizedPool() {
		super();
	}
	
	public SynchronizedPool(int initialCapacity) {
		super(initialCapacity);
	}
	
	@Override
	protected abstract T newObject();
	
	@Override
	public synchronized T obtain() {
		return super.obtain();
	}
	
	@Override
	public synchronized void free (T object) {
		super.free(object);
	}
	
	@Override
	public synchronized void freeAll (Array<T> objects) {
		super.freeAll(objects);
	}
	

}
