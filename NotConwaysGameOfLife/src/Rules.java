import java.util.SplittableRandom;

public class Rules 
{
	public int BoardSize;
	public Cell[][] Board;
	public int SimulationAge = 0;
	
	public void CheckRules()
	{
		for(int y = 0; y < Board.length; y++)
			for (int x = 0; x < Board[y].length; x++)
			{
				NeighbouringCellData NCD = new NeighbouringCellData ();

				NoiseGenerator Perlin = new NoiseGenerator();
				Perlin.setSeed(123456789);
				double Moisture = Perlin.noise((double)x, (double)y, (double)SimulationAge * 0.5);

				Perlin.setSeed(918273645);
				double Fertility = Perlin.noise((double)x, (double)y, (double)SimulationAge * 0.5);

				Perlin.setSeed(987654321);
				double Temperature = Perlin.noise((double)x, (double)y, (double)SimulationAge * 0.5);
				
				for (int y2 = y-1; y2 <= y+1; y2++)
					for (int x2 = x-1; x2 <= x+1; x2++)
					{
						if (y2 >= BoardSize || x2 >= BoardSize || y2 < 0 || x2 < 0 );
						else if (y2 == y && x2 == x);
						else if (Board[y2][x2] != null)
						{
							if (Board[y2][x2].CurrentCellType == CellType.Sand)
								NCD.Sand++;
							if (Board[y2][x2].CurrentCellType == CellType.Dirt)
								NCD.Dirt++;
							if (Board[y2][x2].CurrentCellType == CellType.Grass)
								NCD.Grass++;
							if (Board[y2][x2].CurrentCellType == CellType.Forest)
								NCD.Forest++;
							if (Board[y2][x2].CurrentCellType == CellType.DenseForest)
								NCD.DenseForest++;
							if (Board[y2][x2].CurrentCellType == CellType.Lake)
								NCD.Lake++;
							
							if (Board[y2][x2].CurrentWeather == Weather.Rain)
								NCD.Rain++;
							if (Board[y2][x2].CurrentWeather == Weather.Fire)
								NCD.Fire++;
							
							
						}
					}

				if (Board[y][x].CurrentWeather == Weather.Rain)
				{
					Board[y][x].RainInfluence = 0.4f;
				}
				else if (Board[y][x].CurrentWeather == Weather.Fire)
				{
					Board[y][x].RainInfluence = 0.1f;
				}
				else
				{
					Board[y][x].RainInfluence = 0.1f;
				}
				
				if (Board[y][x].CurrentCellType == CellType.Sand)
					Board[y][x].TempCellType = RulesSand(x, y, NCD, Moisture, Fertility, Temperature);
				else if (Board[y][x].CurrentCellType == CellType.Dirt)
					Board[y][x].TempCellType = RulesDirt(x, y, NCD, Moisture, Fertility, Temperature);
				else if (Board[y][x].CurrentCellType == CellType.Grass)
					Board[y][x].TempCellType = RulesGrass(x, y, NCD, Moisture, Fertility, Temperature);
				else if (Board[y][x].CurrentCellType == CellType.Forest)
					Board[y][x].TempCellType = RulesForest(x, y, NCD, Moisture, Fertility, Temperature);
				else if (Board[y][x].CurrentCellType == CellType.DenseForest)
					Board[y][x].TempCellType = RulesDenseForest(x, y, NCD, Moisture, Fertility, Temperature);
				
				Board[y][x].CurrentWeather = WeatherRules(x, y, NCD, Moisture, Fertility, Temperature);
				Board[y][x].TempCellType = WeatherAffectsRules(x, y, NCD, Moisture, Fertility, Temperature);

				
			}
	}

	
	public CellType RulesSand(int x, int y, NeighbouringCellData NCD, double Moisture, double Fertility, double Temperature)
	{
		if (NCD.Lake >= 2 && Board[y][x].AgeOfCell > 3 && Chance(0.3f * Moisture + Board[y][x].RainInfluence))
		    return CellType.Dirt;
		
		if (Board[y][x].AgeOfCell > 5 && Chance(0.25f * Fertility + Board[y][x].RainInfluence))
		    return CellType.Dirt;
		
		return CellType.Sand;
	}
	
	public CellType RulesDirt(int x, int y, NeighbouringCellData NCD, double Moisture, double Fertility, double Temperature)
	{
		if (Board[y][x].AgeOfCell > 4 && Chance(0.3f * Fertility + Board[y][x].RainInfluence))
		    return CellType.Grass;
		
		if (NCD.Sand >= 4 && NCD.Lake == 0 && Board[y][x].AgeOfCell > 8 && Chance(0.4f * Temperature + Board[y][x].RainInfluence))
		    return CellType.Sand;
		
		return CellType.Dirt;
	}
	
	public CellType RulesGrass(int x, int y, NeighbouringCellData NCD, double Moisture, double Fertility, double Temperature)
	{
		if (NCD.Forest >= 3 && Board[y][x].AgeOfCell > 6 && Chance(0.25f * Fertility + Board[y][x].RainInfluence))
		    return CellType.Forest;
		
		if (NCD.Grass >= 5 && Board[y][x].AgeOfCell > 6 && Chance(0.25f * Fertility + Board[y][x].RainInfluence))
		    return CellType.Forest;
		
		
		return CellType.Grass;
	}

	public CellType RulesForest(int x, int y, NeighbouringCellData NCD, double Moisture, double Fertility, double Temperature)
	{
		if (NCD.Forest >= 7 && Board[y][x].AgeOfCell > 25 && Chance(0.3f * Fertility + Board[y][x].RainInfluence))
		    return CellType.DenseForest;
		
		if ((NCD.Sand + NCD.Dirt) >= 4 && Board[y][x].AgeOfCell > 6 && Chance(0.25f * Temperature + Board[y][x].RainInfluence))
		    return CellType.Grass;
		
		return CellType.Forest;
	}
	
	public CellType RulesDenseForest(int x, int y, NeighbouringCellData NCD, double Moisture, double Fertility, double Temperature)
	{
		if (NCD.DenseForest >= 5 && Board[y][x].AgeOfCell > 12 && Chance(0.25f + Board[y][x].RainInfluence))
		    return CellType.Forest;

		if (NCD.Lake >= 2 && Chance(0.4f * Moisture + Board[y][x].RainInfluence))
		    return CellType.Grass;
		
		
		return CellType.DenseForest;
	}
	
	
	public Weather WeatherRules(int x, int y, NeighbouringCellData NCD, double Moisture, double Fertility, double Temperature)
	{
		
		if (NCD.Lake <= 2 && Chance(0.9f * Temperature) && NCD.Fire > 0)
			if (Board[y][x].CurrentCellType != CellType.Sand && Board[y][x].CurrentCellType != CellType.Dirt) return Weather.Fire;
		if (NCD.Lake <= 2 && Chance(0.0001f * Temperature))
			if (Board[y][x].CurrentCellType != CellType.Sand && Board[y][x].CurrentCellType != CellType.Dirt) return Weather.Fire;

		if (NCD.Lake > 0 && Chance(0.1f * Moisture)) return Weather.Rain;
		
		if (Chance(0.8f * Moisture) && NCD.Rain > 0) return Weather.Rain;
		
		return Weather.None;
	}

	public CellType WeatherAffectsRules(int x, int y, NeighbouringCellData NCD, double Moisture, double Fertility, double Temperature)
	{
		
		if (Board[y][x].CurrentWeather == Weather.Fire && Chance(0.15f * Moisture) && Board[y][x].AgeOfCell > 10) return CellType.Dirt;
		
		if (Board[y][x].CurrentWeather == Weather.Fire && Chance(0.25f) && Board[y][x].AgeOfCell > 10) return CellType.Sand; 

		return Board[y][x].TempCellType;
	}
	private boolean Chance(double Chance)
	{
		double value = Math.random();
		if (Chance >= value) 
			return true;
		
		return false;
	}

}
