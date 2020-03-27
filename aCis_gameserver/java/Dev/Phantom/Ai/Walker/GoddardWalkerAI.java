package Dev.Phantom.Ai.Walker;

import net.sf.l2j.commons.random.Rnd;

import Dev.Phantom.FakePlayer;
import Dev.Phantom.Model.WalkNode;
import Dev.Phantom.Model.WalkerType;

public class GoddardWalkerAI extends WalkerAI {
	
	public GoddardWalkerAI(FakePlayer character) {
		super(character);
	}
	
	@Override
	protected WalkerType getWalkerType() {
		return WalkerType.RANDOM;
	}
	
	@Override
	protected void setWalkNodes() {
		_walkNodes.add(new WalkNode(147704, -56968, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(146792, -55672, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(146664, -55688, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(148104, -56008, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(147768, -56072, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(147416, -56056, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(147304, -56184, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(147560, -56392, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(147832, -56440, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(146872, -56088, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(146568, -56216, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(148040, -56664, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(147592, -56616, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(147816, -56600, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(148152, -56440, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(148120, -55928, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(148296, -55992, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(147528, -57240, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(147960, -57272, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(148008, -57144, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(147864, -56920, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(147576, -56424, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(148296, -55992, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(147816, -56280, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(147178, -56056, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(147000, -55592, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(146824, -55624, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(148504, -55464, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(148648, -55544, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(148504, -55656, -2776, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(148376, -55608, -2776, Rnd.get(1, 900)));
		return;
	}
}
