package Dev.Phantom.Ai.Walker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import net.sf.l2j.commons.random.Rnd;

import Dev.Phantom.FakePlayer;
import Dev.Phantom.FakePlayerManager;
import Dev.Phantom.Ai.FakePlayerAI;
import Dev.Phantom.Model.WalkNode;
import Dev.Phantom.Model.WalkerType;

public abstract class WalkerAI extends FakePlayerAI {

	protected Queue<WalkNode> _walkNodes;
	private WalkNode _currentWalkNode;
	private int currentStayIterations = 1;
	protected boolean isWalking = false;
	
	public WalkerAI(FakePlayer character) {
		super(character);
	}
	
	public Queue<WalkNode> getWalkNodes(){
		return _walkNodes;
	}
	
	protected void addWalkNode(WalkNode walkNode) {
		_walkNodes.add(walkNode);
	}
	
	@Override
	public void setup() {
		super.setup();		
		_walkNodes = new LinkedList<>();
		
		setWalkNodes();
	}
	
	@Override
	public void thinkAndAct() {
		setBusyThinking(true);		
		handleDeath();
		
		if(_walkNodes.isEmpty())
			return;
		
		if(isWalking) {
			if(userReachedDestination(_currentWalkNode)) {
				if(currentStayIterations < _currentWalkNode.getStayIterations() ) {
					currentStayIterations++;
					setBusyThinking(false);
					return;
				}				
				_currentWalkNode = null;
				currentStayIterations = 1;
				isWalking = false;
			}			
		}
		
		if(!isWalking && _currentWalkNode == null) {
			switch(getWalkerType()) {
				case RANDOM:
					_currentWalkNode = (WalkNode) getWalkNodes().toArray()[Rnd.get(0, getWalkNodes().size() - 1)];
					break;
				case LINEAR:
					_currentWalkNode = getWalkNodes().poll();
					_walkNodes.add(_currentWalkNode);
					break;
				case STORE:
					_currentWalkNode = (WalkNode) getWalkNodes().toArray()[Rnd.get(0, getWalkNodes().size() - 1)];
					break;
			}
			_fakePlayer.getFakeAi().moveTo(_currentWalkNode.getX(), _currentWalkNode.getY(), _currentWalkNode.getZ());	
			isWalking = true;
		}
		
		setBusyThinking(false);
	}

    @Override
    protected ArrayList<Integer> getBuffs() {
        return FakePlayerManager.getFighterBuffs();
    }

	protected boolean userReachedDestination(WalkNode targetWalkNode) {
		// Improve this with approximate equality and not strict
		if(_fakePlayer.getX() == targetWalkNode.getX()
			&& _fakePlayer.getY() == targetWalkNode.getY() 
			&& _fakePlayer.getZ() == targetWalkNode.getZ())
			return true;
		
		return false;
	}
	
	protected abstract WalkerType getWalkerType();
	protected abstract void setWalkNodes();
}
