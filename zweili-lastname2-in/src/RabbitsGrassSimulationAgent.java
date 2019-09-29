import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import java.util.Random;
import uchicago.src.sim.space.Object2DGrid;


/**
 * Class that implements the simulation agent for the rabbits grass simulation.

 * @author
 */

public class RabbitsGrassSimulationAgent implements Drawable {

	  private int x;
	  private int y;
	  private int vX;
	  private int vY;
	  private int energy_level;
	  private int liveTime;
	  private RabbitsGrassSimulationSpace rabbitSpace;

	public RabbitsGrassSimulationAgent(){
	    x = -1;
	    y = -1;
	    energy_level = 500;
	    liveTime = 0; // time of life
	    setVxVy();
	}
	

	private void setVxVy(){
	    vX = 0;
	    vY = 0;
	    while((vX == 0) && ( vY == 0)){
			// Select random direction.
			Integer[][] list = {{0,-1},{-1,0},{0,1},{1,0}};
			Random r = new Random();
			Integer[] direction = list[r.nextInt(list.length)];
			vX = direction[0];
			vY = direction[1];
	    }
	}
	
	public void setRabbitsGrassSimulationSpace(RabbitsGrassSimulationSpace rs){
		   rabbitSpace = rs;
	}

	public void draw(SimGraphics arg0) {
		arg0.drawFastRoundRect(Color.white);
		
	}
	
	public int getEnergyLevel() {
		return energy_level;
	}

	public void setEnergyLevel(int e) {
		energy_level = e;
	}
	
	public int getLiveTime() {
		return liveTime;
	}
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public void setXY(int newX, int newY){
		x = newX;
		y = newY;
	}
	
	public void step(){
		
		
		// Choose a random direction for next step.
		setVxVy();
		int newX = x + vX;
		int newY = y + vY;
		
	    Object2DGrid grid = rabbitSpace.getCurrentAgentSpace();
	    
	    newX = (newX + grid.getSizeX()) % grid.getSizeX();
	    newY = (newY + grid.getSizeY()) % grid.getSizeY();
		
	    if(tryMove(newX, newY)){
			//if possible to move in this direction, take energy from it.
			energy_level += rabbitSpace.takeEnergyGrassAt(x, y);
		}
	    else{
		      setVxVy(); //Check another direction;
	    }
		//Decrease energy_level at each step.
		energy_level -=2;
		liveTime +=1;
		
	}
	
	private boolean tryMove(int newX, int newY){
		    return rabbitSpace.moveAgentAt(x, y, newX, newY);
	}

}
