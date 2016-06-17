import java.util.*;
import java.io.*;
import javafx.util.Pair;

public class Main
{
	int numberofants = 50;
	double Q = 0;
	float alpha = 1;
	float beta = 5;
	double evaporate = 0.5;
	double [] probability_matrix = null;
	int size = 0;
	int index = 0;
	int max_trials = 100;
	double [][] Coords = new double[0][2];
	double [][] Pheromone_Matrix = null;
	int [] best_tour;
	double best_tour_length;
    Ant ants[] = null;
	Random r = new Random();
    Set<Pair<Double, Double>> points = new HashSet<Pair<Double,Double>>();
	
	
	 public String input_file() throws FileNotFoundException
	 {
		    String file_name;
		    System.out.println("Please enter file name");
		    Scanner sc = new Scanner(System.in);
			Scanner scan = new Scanner(System.in);
			Scanner user = new Scanner(System.in);
			file_name = user.nextLine();
			File file = new File(file_name);
			sc = new Scanner(file);
			scan = new Scanner(file);
			while(sc.hasNextLine())
			{	 
				
				if(sc.nextLine().equals("NODE_COORD_SECTION"))
				{
					while (sc.hasNextLine())
					{
						String line= sc.nextLine();
						String [] parts = line.split(" ");
						double x = Double.parseDouble(parts[1]);
						double y = Double.parseDouble(parts[2]);
						
						Pair<Double, Double> pair = new Pair<Double, Double>(x, y); 
						if(points.add(pair))
						{
					        	size++;	

						}

					}
				}
			}
			
			sc.close();
			
			Coords = new double[size][2];
			points.clear();
			Q = 14 * size;
			while(scan.hasNextLine())
			{	 
				
				  if(scan.nextLine().equals("NODE_COORD_SECTION"))
				  {
					int i=0;
					int j=0;
					while (scan.hasNextLine())
					{	
					String line= scan.nextLine();
					String [] parts = line.split(" ");
					double x = Double.parseDouble(parts[1]);
					double y = Double.parseDouble(parts[2]);

					Pair<Double, Double> pair = new Pair<Double, Double>(x, y); 
					if(points.add(pair))
					{
						Coords [i][j] = x;
						Coords [i][j+1] = y;
						i++;
						j=0;
					}
					}
				  }	
			}
			scan.close();
			
			Pheromone_Matrix = new double[size][size];
			probability_matrix = new double[size];
	        ants = new Ant[numberofants];
	        for (int i = 0; i < numberofants; i++)
	            ants[i] = new Ant();
       
	        return file_name;
	 }
		
		
    class Ant
    {
		int [] tour = new int [size];
		boolean visited[] = new boolean[size];	
		
		 public void visitCity(int city)
		 {
	            tour[index + 1] = city;
	            visited[city] = true;
	      }

	     public boolean visited(int i) 
	        {
	            return visited[i];
	        }
	     
		public double tourlength()
		{
			double length = Math.sqrt(pow(Coords[tour[size-1]][0] - Coords[tour[0]][0], 2) + 
					pow(Coords[tour[size-1]][1] - Coords[tour[0]][1],2));
		
			for (int i=0; i<size-1; i++)
			{
				length += Math.sqrt(pow(Coords[tour[i]][0] - Coords[tour[i+1]][0], 2) + 
						pow(Coords[tour[i]][1] - Coords[tour[i+1]][1],2));
			}
			
			return length;
		}
		
		 public void clear()
		 {
	            for (int i = 0; i < size; i++)
	                visited[i] = false;
	     }
		}
    
    public void compute_probability(Ant ant) 
    {
        int i = ant.tour[index];
       
        double denom = 0.0;
        for (int l = 0; l < size; l++)
        {
        	 double distance = Math.sqrt(pow(Coords[i][0] - Coords[l][0], 2) + 
     				pow(Coords[i][1] - Coords[l][1],2));
            if (!ant.visited(l))
                denom += pow(Pheromone_Matrix[i][l], alpha)
                        * pow(1.0 / distance, beta);
        }

        for (int j = 0; j < size; j++)
        {
        	double distance = Math.sqrt(pow(Coords[i][0] - Coords[j][0], 2) + 
     				pow(Coords[i][1] - Coords[j][1],2));
            if (ant.visited(j)) {
                probability_matrix[j] = 0.0;
            }
            else 
            {
                double numerator = pow(Pheromone_Matrix[i][j], alpha)
                        * pow(1.0 / distance, beta);
                probability_matrix[j] = numerator / denom;
            }
        }

    }
    
    public int selectNextCity(Ant ant)
    {
     
            int t = r.nextInt(size - index); 
            int j = -1;
            for (int i = 0; i < size; i++) 
            {
                if (!ant.visited(i))
                    j++;
                if (j == t)
                    return i;
            }

        
        compute_probability(ant);

        double rand = r.nextDouble();
        double total = 0;
        for (int i = 0; i < size; i++) 
        {
            total += probability_matrix[i];
            if (total >= rand)
                return i;
        }
	
               return 0;
    }
    
    public void updatePheromones() 
    {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                Pheromone_Matrix[i][j] *= evaporate;

        for (Ant a : ants) 
        {
            double contribution = Q / a.tourlength();
            for (int i = 0; i < size - 1; i++) 
            {
                Pheromone_Matrix[a.tour[i]][a.tour[i + 1]] += contribution;
            }
            Pheromone_Matrix[a.tour[size - 1]][a.tour[0]] += contribution;
        }
    }
    
    public void updateBestPheromone() 
    {
        if (best_tour == null)
        {
            best_tour = ants[0].tour;
            best_tour_length = ants[0].tourlength();
        }
        
        for (Ant a : ants) 
        {
            if (a.tourlength() < best_tour_length) {
                best_tour_length = a.tourlength();
                best_tour = a.tour.clone();
            }
        }
    }
    
    public static double pow(final double a, final double b)
	 {
	        final int x = (int) (Double.doubleToLongBits(a) >> 32);
	        final int y = (int) (b * (x - 1072632447) + 1072632447);
	        return Double.longBitsToDouble(((long) y) << 32);
	  }
    
    
    public int[] AntAlgo() 
    {
        
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                Pheromone_Matrix[i][j] = 1.0;
        
        int trials = 0;
       
        while(trials<max_trials)
        {
        index = -1;
        for (int i = 0; i < numberofants; i++) 
        {
            ants[i].clear(); 
            ants[i].visitCity(r.nextInt(size));
        }
        index++;
        
     
        while (index < size - 1) 
        {
            for (Ant a : ants)
                a.visitCity(selectNextCity(a));
            index++;
        }
        
            updatePheromones();
            updateBestPheromone();
            trials++;
        } 
        return best_tour.clone();
    }
    public static void main(String[] args) throws IOException
	{
    	
    	Main object = new Main();
    	String file_name = object.input_file();
    	Scanner input = new Scanner(System.in);
    	long time;
    	System.out.println("Enter CPU time in seconds");
    	time = input.nextLong();
    	time = time * 1000;
    	long start_time = System.currentTimeMillis();
    	while (System.currentTimeMillis() - start_time < time)
    	{
    			 
    	        object.AntAlgo();
    	       		
    	}
    	System.out.println(file_name + " " + (int)(object.best_tour_length));
    	PrintWriter writer = new PrintWriter("output.tour", "UTF-8");
    	for (int i=0; i<object.size; i++)
    	{
    		writer.println(object.best_tour[i] + 1);
    	
    	}
    	writer.flush();
    	writer.close();
	}
	}
	
	


