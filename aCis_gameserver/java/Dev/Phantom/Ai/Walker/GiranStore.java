package Dev.Phantom.Ai.Walker;

import net.sf.l2j.commons.random.Rnd;

import Dev.Phantom.FakePlayer;
import Dev.Phantom.Model.WalkNode;
import Dev.Phantom.Model.WalkerType;

public class GiranStore extends WalkerAI {
	
	public GiranStore(FakePlayer character) {
		super(character);
	}
	
	@Override
	protected WalkerType getWalkerType() {
		return WalkerType.STORE;
	}
	
	@Override
	protected void setWalkNodes() {
		_walkNodes.add(new WalkNode(82840, 148168, -3464, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(82680, 148312, -3464, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(82888, 148456, -3464, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(82664, 148552, -3464, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(82936, 148776, -3464, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(82680, 148808, -3464, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(82936, 149016, -3464, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(82680, 149064, -3464, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(82808, 148904, -3464, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(83160, 149080, -3432, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(82792, 148616, -3464, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(82520, 148376, -3464, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(82536, 148680, -3464, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(82520, 148920, -3464, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(83016, 148616, -3464, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(83080, 148152, -3464, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(82616, 148120, -3464, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(82952, 148280, -3464, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(83192, 148616, -3416, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(82376, 148552, -3462, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(82376, 148792, -3462, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(82408, 148152, -3464, Rnd.get(100, 99999)));
		_walkNodes.add(new WalkNode(82344, 148344, -3464, Rnd.get(100, 99999)));
	}
	
}
