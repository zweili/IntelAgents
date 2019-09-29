/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * @author 
 */
import uchicago.src.sim.space.Object2DGrid;


public class RabbitsGrassSimulationSpace {
	
	private Object2DGrid rabbitSpace;
	private Object2DGrid agentSpace;
	
	@SuppressWarnings("deprecation")
	public RabbitsGrassSimulationSpace(int xSize, int ySize){
		rabbitSpace = new Object2DGrid(xSize, ySize);
		agentSpace = new Object2DGrid(xSize, ySize);
		
		for(int i = 0; i < xSize; i++){
			for(int j = 0; j < ySize; j++){
				rabbitSpace.putObjectAt(i,j,new Integer(0));
		      }
		    }
		  }
	
    @SuppressWarnings("deprecation")
	public void spreadGrass(int numInitGrass, int amountEnergyGrass){
	    // Randomly place rabbits in rabbitSpace
    	for(int i = 0; i < numInitGrass; i++){

      // Choose coordinates
        int x = (int)(Math.random()*(rabbitSpace.getSizeX()));
        int y = (int)(Math.random()*(rabbitSpace.getSizeY()));
        
     // Get the value of the object at those coordinates
        // int currentValue = getValueAt(x, y);
        // Replace the Integer object with the value of grass (50)
        rabbitSpace.putObjectAt(x,y,new Integer(amountEnergyGrass));
    	}
    }

    public int getGrassEnergyAt(int x, int y){
        int i;
        if(rabbitSpace.getObjectAt(x,y)!= null){
          i = ((Integer)rabbitSpace.getObjectAt(x,y)).intValue();
        }
        else{
          i = 0;
        }
        return i;
      }
    
    public Object2DGrid getCurrentValueSpace(){
        return rabbitSpace;
      }
    
    public Object2DGrid getCurrentAgentSpace(){
        return agentSpace;
      }
    
    public boolean isCellOccupied(int x, int y){
        boolean retVal = false;
        if(agentSpace.getObjectAt(x, y)!=null) retVal = true;
        return retVal;
      }

      public boolean addAgent(RabbitsGrassSimulationAgent agent){
        boolean retVal = false;
        int count = 0;
        int countLimit = 10 * agentSpace.getSizeX() * agentSpace.getSizeY(); // max number of tentative to put an agent.

        while((retVal==false) && (count < countLimit)){
          int x = (int)(Math.random()*(agentSpace.getSizeX()));
          int y = (int)(Math.random()*(agentSpace.getSizeY()));
          if(isCellOccupied(x,y) == false){
            agentSpace.putObjectAt(x,y,agent);
            agent.setXY(x,y);
            agent.setRabbitsGrassSimulationSpace(this);
            retVal = true;
          }
          count++;
        }

        return retVal;
      }
      
      public void removeAgentAt(int x, int y){
    	    agentSpace.putObjectAt(x, y, null);
      }
      
      @SuppressWarnings("deprecation")
      public int takeEnergyGrassAt(int x, int y){
    	    int grassEnergy = getGrassEnergyAt(x, y);
    	    rabbitSpace.putObjectAt(x, y, new Integer(0));
    	    return grassEnergy;
      }
      
      public boolean moveAgentAt(int x, int y, int newX, int newY){
    	    boolean retVal = false;
    	    if(!isCellOccupied(newX, newY)){
    	    	RabbitsGrassSimulationAgent a = (RabbitsGrassSimulationAgent)agentSpace.getObjectAt(x, y);
    	    	removeAgentAt(x,y);
    	    	a.setXY(newX, newY);
    	    	agentSpace.putObjectAt(newX, newY, a);
    	    	retVal = true;
    	    }
    	    return retVal;
    }
      
      public int getTotalGrass(){
    	    int totalGrass = 0;
    	    for(int i = 0; i < agentSpace.getSizeX(); i++){
    	      for(int j = 0; j < agentSpace.getSizeY(); j++){
    	    	  totalGrass += getGrassEnergyAt(i,j);

    	    	  }
    	      }
    	    return totalGrass;
    	  }



}
