package bot.action.groupUnit.movement;

import com.Com;

import bwapi.Unit;
import newAgent.agent.group.MarineGroupAgent;

public class MoveLeft extends MoveAction {

	public MoveLeft(MarineGroupAgent agent, Com com, Long maxFramesOfExecuting, boolean specialStart) {
		super(agent, com, maxFramesOfExecuting, specialStart);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected bot.action.singleUnit.movement.MoveAction getAction(Unit unit) {
		// TODO Auto-generated method stub
		return new bot.action.singleUnit.movement.MoveLeft(this, com, unit);
	}
}
