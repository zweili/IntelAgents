import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.Value2DDisplay;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.util.SimUtilities;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.reflector.RangePropertyDescriptor;
import uchicago.src.sim.analysis.DataSource;
import uchicago.src.sim.analysis.Sequence;

/**
 * Class that implements the simulation model for the rabbits grass
 * simulation.  This is the first class which needs to be setup in
 * order to run Repast simulation. It manages the entire RePast
 * environment and the simulation.
 *
 * @author 
 */


public class RabbitsGrassSimulationModel extends SimModelImpl {	
	
		// Default Values
		private static final int GRIDSIZE = 100;
		private static final int NUMINITRABBITS = 50;
		private static final int NUMINITGRASS = 300;
		private static final int GRASSGROWTHRATE = 10;
		private static final int BIRTHTHRESHOLD = 3000;
		private static final int MAXLIVETIME = 1000;
		private static final int AMOUNTENERGY = 50;
	
		//variables initializations for varying parameters
		private int gridSize = GRIDSIZE;
		private int numInitRabbits = NUMINITRABBITS;
		private int numInitGrass = NUMINITGRASS;
		private int grassGrowthRate = GRASSGROWTHRATE;
		private int birthThreshold = BIRTHTHRESHOLD;
		private int maxLiveTime = MAXLIVETIME;
		private int amountEnergyGrass = AMOUNTENERGY;
		
		private Schedule schedule;
		private RabbitsGrassSimulationSpace rabbitSpace;
		private DisplaySurface displaySurf;
		private ArrayList<RabbitsGrassSimulationAgent> agentList;
		
		private OpenSequenceGraph amountOfRabbitInSpace;
		
	    class RabbitInSpace implements DataSource, Sequence {

		    public Object execute() {
		      return new Double(getSValue());
		    }

		    public double getSValue() {
		      return (int)countLivingAgents();
		    }
		  }


		public static void main(String[] args) {
			
			System.out.println("Rabbit skeleton");

			SimInit init = new SimInit();
			RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
			// Do "not" modify the following lines of parsing arguments
			if (args.length == 0) // by default, you don't use parameter file nor batch mode 
				init.loadModel(model, "", false);
			else
				init.loadModel(model, args[0], Boolean.parseBoolean(args[1]));
			
		}
		
		public void setup() {
			System.out.println("Running setup");
			
			//sliders
			RangePropertyDescriptor gridsize = new RangePropertyDescriptor("GridSize", 20, 200, 50);
			RangePropertyDescriptor numrabbit = new RangePropertyDescriptor("NumInitRabbits", 20, 200, 50);
			RangePropertyDescriptor numgrass = new RangePropertyDescriptor("NumInitGrass", 100, 500, 100);
			RangePropertyDescriptor birththresh = new RangePropertyDescriptor("BirthThreshold", 1000, 5000, 1000);
			RangePropertyDescriptor grassrate = new RangePropertyDescriptor("GrassGrowthRate", 0, 200, 50);
			RangePropertyDescriptor maxlivetime = new RangePropertyDescriptor("MaxLiveTime", 0, 2000, 500);
			RangePropertyDescriptor amountenergy = new RangePropertyDescriptor("AmountEnergyGrass", 0, 100, 25);

			descriptors.put("GridSize", gridsize);
			descriptors.put("NumInitRabbits", numrabbit);
			descriptors.put("NumInitGrass", numgrass);
			descriptors.put("BirthThreshold", birththresh);
			descriptors.put("GrassGrowthRate", grassrate);
			descriptors.put("MaxLiveTime", maxlivetime);
			descriptors.put("AmountEnergyGrass", amountenergy);
			
			rabbitSpace = null;
			agentList = new ArrayList<RabbitsGrassSimulationAgent>();
			schedule = new Schedule(1);
			
		    if (displaySurf != null){
		        displaySurf.dispose();
		      }
		    displaySurf = null;
		    

		    if (amountOfRabbitInSpace != null){
		    	amountOfRabbitInSpace.dispose();
		    }
		    amountOfRabbitInSpace = null;

		    displaySurf = new DisplaySurface(this, "Rabbit Model Window 1");
		    amountOfRabbitInSpace = new OpenSequenceGraph("Amount Of Rabbit In Space",this);
		    
		    registerDisplaySurface("Rabbit Model Window 1", displaySurf);
		    this.registerMediaProducer("Plot", amountOfRabbitInSpace);

		}
			
		public void begin( ){
		    buildModel();
		    buildSchedule();
		    buildDisplay();
		    displaySurf.display();
		    amountOfRabbitInSpace.display();
		}

		public void buildModel() {
			System.out.println("Running BuildModel");
			rabbitSpace = new RabbitsGrassSimulationSpace(gridSize,gridSize);
			//rabbitSpace.spreadRabbit(numInitRabbits);
			rabbitSpace.spreadGrass(numInitGrass,amountEnergyGrass);
			
		    for(int i = 0; i < numInitRabbits; i++){
		        addNewAgent();
		      }
		}

	  	public void buildSchedule() {
	  		System.out.println("Running BuildSchedule");
	  		
	  	    class Rabbit_Step_and_birth extends BasicAction {
	  	      public void execute() {
	  	        SimUtilities.shuffle(agentList);
	  	        for(int i =0; i < agentList.size(); i++){
	  	        	RabbitsGrassSimulationAgent rabbit_a = (RabbitsGrassSimulationAgent)agentList.get(i);
	  	        	rabbit_a.step();
	  	        	
	  	        	// Birth of 2 rabbits if energy_level > birththreshold
	  	        	int current_energy_level = rabbit_a.getEnergyLevel();
	  	        	if(current_energy_level > birthThreshold) {
	  	        		
	  	        		for(int j=0; j < 2; j++) {
	  	        			addNewAgent();
	  	        		}
	  	        		
	  	        		rabbit_a.setEnergyLevel(Math.round(current_energy_level/2));
	  	        	}
	  	        	
	  	        }
	  	        
	  	        //remove deadAgents aka energy_level < 0
	  	        reapDeadAgents();
	  	        displaySurf.updateDisplay();
	  	      }
	  	    }

	  	    schedule.scheduleActionBeginning(0, new Rabbit_Step_and_birth());
	  	    
	  	    class Spread_Grass extends BasicAction {
	  	    	public void execute() {
	  	    		rabbitSpace.spreadGrass(grassGrowthRate,amountEnergyGrass);
	  	    	}
	  	    }
	  	    
	  	  schedule.scheduleActionBeginning(0, new Spread_Grass());
	  	    
	  	    class CountLiving_rabbits extends BasicAction {
	  	      public void execute(){
	  	        countLivingAgents();
	  	      }
	  	    }

	  	    schedule.scheduleActionAtInterval(10, new CountLiving_rabbits());
	  	    
	  	    
	  	    class UpdateRabbitInSpace extends BasicAction {
	  	      public void execute(){
	  	        amountOfRabbitInSpace.step();
	  	      }
	  	    }

	  	    schedule.scheduleActionAtInterval(10, new UpdateRabbitInSpace());
	  	  }


	  	public void buildDisplay() {
	  		System.out.println("Running BuildDisplay");
	  		
	  	    ColorMap map = new ColorMap();

	  	    map.mapColor(0, Color.black);
	  	    map.mapColor(amountEnergyGrass, Color.green); // grass

	  	    Value2DDisplay displayGrass =
	  	        new Value2DDisplay(rabbitSpace.getCurrentValueSpace(), map);
	  	    
	  	    Object2DDisplay displayAgents = new Object2DDisplay(rabbitSpace.getCurrentAgentSpace());
	  	    displayAgents.setObjectList(agentList);

	  	    displaySurf.addDisplayable(displayGrass, "Grass");
	  	    displaySurf.addDisplayable(displayAgents, "Agents");
	  	    
	  	    amountOfRabbitInSpace.addSequence("Rabbits In Space", new RabbitInSpace());
	  	    amountOfRabbitInSpace.addSequence("Grass In Space (Normalized by numInitGrass)", new RabbitInSpace() {
	  	    	public double getSValue() {
	  	    	    return  rabbitSpace.getTotalGrass()/numInitGrass; // normalized
	  	    	  }
	  	    });
		  
		}

		public String[] getInitParam() {
			// TODO Auto-generated method stub
			// Parameters to be set by users via the Repast UI slider bar
			// Do "not" modify the parameters names provided in the skeleton code, you can add more if you want 
			String[] params = { "GridSize", "NumInitRabbits", "NumInitGrass", "GrassGrowthRate", "BirthThreshold", "MaxLiveTime", "AmountEnergyGrass"};
			return params;
		}

		public String getName() {
			
			return "Rabbit-hole";
		}

		public Schedule getSchedule() {
			// TODO Auto-generated method stub
			return schedule;
		}
		
		private void addNewAgent(){
			RabbitsGrassSimulationAgent a = new RabbitsGrassSimulationAgent();
			agentList.add(a);
			rabbitSpace.addAgent(a);
		}
		
	    private int reapDeadAgents(){
		    int count = 0;
		    for(int i = (agentList.size() - 1); i >= 0 ; i--){
		    	RabbitsGrassSimulationAgent a = (RabbitsGrassSimulationAgent)agentList.get(i);
		      if((a.getEnergyLevel() < 0) || (a.getLiveTime() > maxLiveTime)){
		        rabbitSpace.removeAgentAt(a.getX(), a.getY());
		        agentList.remove(i);
		        count++;
		      }
		    }
		    return count;
		  }
		
	    private int countLivingAgents(){
		    int livingAgents = 0;
		    for(int i = 0; i < agentList.size(); i++){
		    	RabbitsGrassSimulationAgent a = (RabbitsGrassSimulationAgent)agentList.get(i);
		      if(a.getEnergyLevel() > 0) livingAgents++;
		    }
		    return livingAgents;
		  }

		//get methods for varying parameters
		public int getGridSize() {
			return gridSize;
		}
		
		public int getNumInitRabbits() {
			return numInitRabbits;
		}
		
		public int getNumInitGrass() {
			return numInitGrass;
		}
		
		public int getGrassGrowthRate() {
			return grassGrowthRate;
		}
		
		public int getBirthThreshold() {
			return birthThreshold;
		}
		
		public int getMaxLiveTime() {
			return maxLiveTime;
		}
		
		public int getAmountEnergyGrass() {
			return amountEnergyGrass;
		}
		
		// set methods for varying parameters
		public void setGridSize(int size_grid) {
			gridSize = size_grid;
		}
		
		public void setNumInitRabbits(int num_rabbit) {
			numInitRabbits = num_rabbit;
		}
		
		public void setNumInitGrass(int num_grass) {
			numInitGrass = num_grass;
		}
		
		public void setGrassGrowthRate(int grow_rate) {
			grassGrowthRate = grow_rate;
		}
		
		public void setBirthThreshold(int birth_thresh) {
			birthThreshold = birth_thresh;
		}
		
		public void setMaxLiveTime(int max_live) {
			maxLiveTime = max_live;
		}
		
		public void setAmountEnergyGrass(int e) {
			amountEnergyGrass = e;
		}
}
