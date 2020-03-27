package Dev.Phantom.Ai.Walker;

import net.sf.l2j.commons.random.Rnd;

import Dev.Phantom.FakePlayer;
import Dev.Phantom.Model.WalkNode;
import Dev.Phantom.Model.WalkerType;

public class GiranWalkerAI extends WalkerAI {
	
	public GiranWalkerAI(FakePlayer character) {
		super(character);
	}
	
	@Override
	protected WalkerType getWalkerType() {
		return WalkerType.RANDOM;
	}
	
	@Override
	protected void setWalkNodes() {
		_walkNodes.add(new WalkNode(82248, 148600, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(82072, 147560, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(82792, 147832, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(81752, 147592, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(82760, 148600, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(82840, 148776, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(82808, 149064, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(82216, 147688, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(82568, 147656, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(82792, 147816, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(81864, 149416, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(82856, 149368, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(82216, 149464, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(81640, 149624, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(81512, 149192, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(81208, 149224, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(81032, 148680, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(81400, 148632, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(82440, 149240, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(82136, 149240, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(81336, 149208, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(81368, 148952, -3464, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(83320, 147976, -3400, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(83592, 148392, -3400, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(83688, 148472, -3400, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(83432, 148568, -3400, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(83576, 148888, -3400, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(83400, 149144, -3400, Rnd.get(1, 900)));
		_walkNodes.add(new WalkNode(83752, 149512, -3400, Rnd.get(1, 900)));
		return;
	}
	
}
