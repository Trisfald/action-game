package action.ai.behaviour.micro;

import action.ai.Ai;
import action.ai.behaviour.GenericBehaviour;
import action.entity.Entity;
import action.entity.enums.BlockType;
import action.utility.enums.Direction;

/**
 * Turn towards something
 * 
 * @author Andrea
 */
public class Face extends GenericBehaviour {
	
	private Entity target;
	private boolean guarded;

	public Face(Ai ai, Entity target, boolean guarded) {
		super(ai);
		this.target = target;
		this.guarded = guarded;
	}
	
	@Override
	protected void doProgress(float delta) {
		owner.setDir(Direction.getDirTowards(owner.getPos(), target.getPos()));
		if (guarded)
			owner.tryBlock(BlockType.STANDARD);
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.FACE;
	}
	
	public void setGuarded(boolean b) {
		guarded = b;
	}

}
