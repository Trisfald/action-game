package action.utility;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * An array that can be pooled and reused
 * 
 * @author Andrea
 */
public class PoolableArray<T> extends Array<T> implements Poolable {

	private Pool<PoolableArray<T>> pool;
	
	public PoolableArray(Pool<PoolableArray<T>> pool) {
		this.pool = pool;
	}

	@Override
	public void reset() {
		clear();
	}
	
	public void free() {
		pool.free(this);
	}

}
