public class GameBoard 
{
	int BoardSize = 250;
	double Scale = 3;
	
	Cell[][] Board = new Cell[BoardSize][BoardSize];
	
	boolean Simulating = true;
	int SimulationAge = 0;
	
	
	Rules Rules = new Rules();
	
	public static void main(String[] args) throws InterruptedException
	{
		GameBoard Program = new GameBoard();
		Program.Start();
	}
	
	public void Start() throws InterruptedException
	{
		Window.createWindow((int)((BoardSize * Scale + (10 * Scale))),(int)((BoardSize * Scale + (10 * Scale))));
		Window.setBrushSize(Scale/2 );
		
		SetBlankBoard();
		
		Rules.Board = Board;
		Rules.BoardSize = BoardSize;
		
		Display();
		Update();
	}
	
	public void Update() throws InterruptedException
	{
		while(Simulating)
		{
			SimulationAge++;
			Rules.SimulationAge = SimulationAge;
			Rules.CheckRules();
			NextGeneration();
			Display();
		}
	}
	
	public void SetBlankBoard()
	{
		for(int y = 0; y < Board.length; y++)
			for(int x = 0; x < Board[y].length; x++)
			{

				Board[y][x] = new Cell();				
				Board[y][x].CurrentCellType = CellType.Dirt;
				Board[y][x].TempCellType = CellType.Dirt;
				
				
				
			}
		AddRiver();
	}
	
	public void AddRiver()
	{
		NoiseGenerator Perlin = new NoiseGenerator();
		Perlin.setSeed((double)(Math.random() * 1000 + 1));
		
		for(int y = 0; y < Board.length; y++)
			for (int x = 0; x < Board[y].length; x++)
			{
				double PerlinValue = Perlin.noise((double)x, (double)y, 0, 100);
				if (PerlinValue >= 0.15f  && PerlinValue <= 0.45f)
				{
					Board[y][x].CurrentCellType = CellType.Lake;
					Board[y][x].TempCellType = CellType.Lake;
					Board[y][x].CurrentWeather = Weather.None;
				}
			}
	}
	
	public void Display()
	{
		for(int y = 0; y < Board.length; y++)
			for(int x = 0; x < Board[y].length; x++)
			{
				if (Board[y][x].CurrentCellType == CellType.Sand)
				{
					Window.setColour(237, 201, 175);
				}
				if (Board[y][x].CurrentCellType == CellType.Dirt)
				{
					Window.setColour(155, 118, 83);
				}
				if (Board[y][x].CurrentCellType == CellType.Grass)
				{
					Window.setColour(69, 126, 91);
				}
				if (Board[y][x].CurrentCellType == CellType.Forest)
				{
						Window.setColour(49, 96, 51);
				}
				if (Board[y][x].CurrentCellType == CellType.DenseForest)
				{
						Window.setColour(33, 78, 37);
				}
				if (Board[y][x].CurrentCellType == CellType.Lake)
				{
					Window.setColour(0, 105, 148);
				}
				
				if(Board[y][x].CurrentWeather == Weather.Rain)
				{
					Window.setColour(0,0,240);
				}
				
				if(Board[y][x].CurrentWeather == Weather.Fire)
				{
					Window.setColour(240,0,0);
				}
				Window.fillRectangle(x * Scale + 10, y * Scale + 10, Scale, Scale);
			}
	}
	
	public void NextGeneration()
	{
		for(int y = 0; y < Board.length; y++)
			for (int x = 0; x < Board[y].length; x++)
			{
				if (Board[y][x].CurrentCellType == Board[y][x].TempCellType)
				{
					Board[y][x].AgeOfCell++;
				}
				else
				{
					Board[y][x].CurrentCellType = Board[y][x].TempCellType;
					Board[y][x].AgeOfCell = 0;
				}
				
				Board[y][x].TempCellType = Board[y][x].CurrentCellType;
				
			}
	}
}
