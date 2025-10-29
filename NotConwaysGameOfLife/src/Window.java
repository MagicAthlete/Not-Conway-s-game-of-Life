
import java.util.HashMap;
import java.util.List;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SpringLayout;
import java.util.HashMap;

public class Window // just a renamed file of NOOPDraw
{
	public static HashMap<String, Color> dictcolour = new HashMap<String, Color>();
	public static JFrame jf;
	private static BufferedImage onscreenImage;
	private static Graphics2D onscreen;
	private static double brushSize;
	private static Color bgColor;
	private static Color drawingColor;
	private static int width;
	private static int height;
	private static int fontSize;
	private static double brushsize;
	private static String fontFace;
	public static List<String> keys = new ArrayList<String>();

	//===================================================================================
	// Constructors
	//===================================================================================

	public static void createWindow()
	{
		createWindow(500, 500, "NOOPDraw Project");
	}

	public static void createWindow(int w, int h)
	{
		createWindow(w, h, "NOOPDraw Project");	
	}

	public static void createWindow(int w, int h, String title)
	{
		//Set static variables (state variables).

		bgColor = new Color(255,255,255);
		drawingColor = new Color(0,0,0);
		width = w;
		height = h;
		jf = new JFrame();
		jf.setTitle(title);
		jf.setSize(width,height);
		fontSize = 14;
		fontFace = "Arial";

		//Create image to draw to.

		onscreenImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		onscreen  = onscreenImage.createGraphics();
		onscreen.setColor(drawingColor);
		onscreen.setFont(new Font(fontFace, Font.PLAIN, fontSize));

		//Brush size.

		brushSize = 1.0;
		setBrushSize(brushSize);

		//Add image to JFrame.
		//Note: Without the SpringLayout, the image is placed at the very top left
		//      of the window underneath the border and top toolbar. However, it
		//      isn't necessary to add any constraints for this to work.

		ImageIcon icon = new ImageIcon(onscreenImage);
		JLabel draw = new JLabel(icon);
		Container cp = jf.getContentPane();
		SpringLayout myLayout = new SpringLayout();
		cp.setLayout(myLayout);        
		cp.add(draw);
		//myLayout.putConstraint(SpringLayout.WEST, draw, 0, SpringLayout.WEST, cp);  //not needed apparently
		//myLayout.putConstraint(SpringLayout.NORTH, draw, 0, SpringLayout.NORTH, cp); //not needed apparently

		//A few more settings.

		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);            // closes all windows
		jf.requestFocusInWindow();
		jf.setVisible(true);

		//clear screen (Sets the bgcolor)
		clearScreen();
	}
	
	//===================================================================================
	//Keyboard input
	//===================================================================================
	public static void startKeyLogging() throws InterruptedException
	{
		jf.addKeyListener(new KeyAdapter() 
		{
			public void keyPressed(KeyEvent e) 
			{
				int keyCode = e.getKeyCode();	
				keys.add(KeyEvent.getKeyText(keyCode));
			}
		});
	}
	
	public static ArrayList<String> getPressedKeys() 
	{
		ArrayList<String> output = new ArrayList<String>();
		for(String code: keys) 
		{
			output.add(code);
		}
		keys.clear();
		return output;
	}
	
	//===================================================================================
	// Drawing and filling functions.
	//===================================================================================

	public static void drawPoint(double x, double y)
	{
		brushsize = ((BasicStroke) onscreen.getStroke()).getLineWidth()+5;
		onscreen.fill(new Ellipse2D.Double(x-(brushsize/2),y-(brushsize/2),(brushsize),(brushsize)));
		jf.repaint();
	}

	public static void drawArc(double x, double y, double radius, double angle1, double angle2)
	{
		onscreen.draw(new Arc2D.Double(x,y,radius,radius,angle1,angle2,Arc2D.OPEN));
		jf.repaint();
	}

	public static void drawArc(double x, double y, double xradius, double yradius, double angle1, double angle2)
	{
		onscreen.draw(new Arc2D.Double(x,y,xradius,yradius,angle1,angle2,Arc2D.OPEN));
		jf.repaint();
	}

	public static void drawString(String s, double x, double y)
	{
		onscreen.drawString(s, (int)x, (int)y);   //need int coordinates
		jf.repaint();
	}

	public static void drawEllipse(double x, double y, double w, double h)
	{
		onscreen.draw(new Ellipse2D.Double(x,y,w,h));
		jf.repaint();
	}

	public static void fillEllipse(double x, double y, double w, double h)
	{
		onscreen.fill(new Ellipse2D.Double(x,y,w,h));
		jf.repaint();
	}

	public static void drawRectangle(double x, double y, double w, double h)
	{
		onscreen.draw(new Rectangle2D.Double(x,y,w,h));
		jf.repaint();
	}

	public static void fillRectangle(double x, double y, double w, double h)
	{
		onscreen.fill(new Rectangle2D.Double(x,y,w,h));
		jf.repaint();
	}

	public static void drawLine(double x1, double y1, double x2, double y2)
	{
		onscreen.draw(new Line2D.Double(x1, y1, x2, y2));
		jf.repaint();
	}

	public static void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3)
	{
		drawLine(x1,y1,x2,y2);
		drawLine(x2,y2,x3,y3);
		drawLine(x3,y3,x1,y1);
		jf.repaint();
	}

	public static void fillTriangle(double x1, double y1, double x2, double y2, double x3, double y3)
	{
		Path2D.Double p = new Path2D.Double();
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
		p.lineTo(x3, y3);
		p.closePath();
		onscreen.fill(p);
		jf.repaint();
	}

	public static void drawClosedShape(int... coords)
	{
		if(coords.length%2 != 0)
		{
			System.out.println("Error - Need an even # of coordinate values.");
		}
		else if(coords.length<6)
		{
			System.out.println("Error - Need at least three points (six coordinates) to make a closed shape.");
		}
		else
		{
			Path2D.Double p = new Path2D.Double();
			p.moveTo(coords[0], coords[1]);
			for (int x=2; x<coords.length;x=x+2)
			{
				int y = x+1;
				p.lineTo(coords[x],coords[y]);
			}	
			p.closePath();
			onscreen.draw(p);
			jf.repaint();			
		}

	}
	public static void fillClosedShape(int... coords)
	{
		if(coords.length%2 != 0)
		{
			System.out.println("Error - Need an even # of coordinate values.");
		}
		else if(coords.length<6)
		{
			System.out.println("Error - Need at least three points (six coordinates) to make a closed shape.");
		}
		else
		{
			Path2D.Double p = new Path2D.Double();
			p.moveTo(coords[0], coords[1]);
			for (int x=2; x<coords.length;x=x+2)
			{
				int y = x+1;
				p.lineTo(coords[x],coords[y]);
			}	
			p.closePath();
			onscreen.fill(p);
			jf.repaint();			
		}
	}

	//Draws an image (PNG or JPG).  Image must me in the project folder.
	//Make sure you can see the file in Eclipse (use Refresh on Project name)

	public static void drawImage(String filename, int x, int y)
	{
		BufferedImage img = null;
		try
		{
			img = ImageIO.read(new File(filename));  //image in project folder (Eclipse)
			onscreen.drawImage(img, null, x, y);
		}   
		catch (IOException e)
		{
			System.out.println("Error");
			e.printStackTrace();  //Optional - Shows the location of the error issue.
		}    
		jf.repaint();
	}

	//===================================================================================
	// Assorted utility functions.
	//===================================================================================

	public static void addColorName(String name, int r, int b, int g)  //ALEX CHECK: formerly newTTCalexK(...)
	{
		dictcolour.put(name.toLowerCase(), new Color(r,b,g));
	}

	public static void setFontSize(int size)
	{
		fontSize = size;
		onscreen.setFont(new Font(fontFace, Font.PLAIN, fontSize));
	}

	public static int getFontSize()
	{
		return fontSize;
	}

	public static void setFontFace(String face)
	{
		fontFace = face;
		onscreen.setFont(new Font(fontFace, Font.PLAIN, fontSize));
	}

	public static String getFontFace()
	{
		return fontFace;
	}

	public static void clearScreen()
	{
		Color tmp = drawingColor;
		setColour(bgColor);
		fillRectangle(0, 0, width, height);
		setColour(tmp);	
	}

	public static void setBrushSize(double r)
	{
		BasicStroke bs = new BasicStroke((float)r, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		brushSize = r;
		onscreen.setStroke(bs);
	}

	public static double getBrushSize()
	{
		return brushSize;
	}

	public static void setColour(String s)
	{
		setColour(stringToColour(s));
	}

	public static void setColour(int r, int g, int b)
	{
		onscreen.setColor(new Color(r,g,b));
	}

	public static void setRandomColour()
	{
		int r = (int)(Math.random() * 256);
		int g = (int)(Math.random() * 256);
		int b = (int)(Math.random() * 256);
		setColour(r,g,b);
	}

	private static void setColour(Color c)
	{
		onscreen.setColor(c);
		drawingColor = c;
	}

	private static Color stringToColour(String s)
	{
		for (HashMap.Entry<String, Color> entry : dictcolour.entrySet()) {
			String key = entry.getKey();
			Object val = entry.getValue();
			if (key.equals(s.toLowerCase())){
				return (Color) val;
			}
		}
		if (s.toLowerCase().equals("black"))
		{
			return Color.black;
		}
		else if (s.toLowerCase().equals("white"))
		{
			return Color.white;
		}
		else if (s.toLowerCase().equals("blue"))
		{
			return Color.blue;
		} 
		else if (s.toLowerCase().equals("green"))
		{
			return Color.green;
		}       
		else if (s.toLowerCase().equals("red"))
		{
			return Color.red;
		}
		else if (s.toLowerCase().equals("yellow"))
		{
			return Color.yellow;
		}
		else if (s.toLowerCase().equals("orange"))
		{
			return Color.orange;
		}
		else if (s.toLowerCase().equals("gray"))
		{
			return Color.gray;
		}
		else if (s.toLowerCase().equals("pink"))
		{
			return Color.pink;
		}
		else if (s.toLowerCase().equals("purple"))
		{
			return new Color(180,0,180);
		}
		else if (s.toLowerCase().equals("brown"))
		{
			return new Color(123,63,0);
		}	      
		return Color.black;

	}

	//===================================================================================
	// Load image from file.
	//===================================================================================

	public static void open(String filename)
	{
		BufferedImage onscreenImage = null;
		try
		{
			onscreenImage = ImageIO.read(new File(filename));  
		}
		catch(IOException e) 
		{
			System.out.println("Error");
			e.printStackTrace();  //Optional - Shows the location of the error issue.
		}
		onscreen = onscreenImage.createGraphics();
		onscreen.setColor(drawingColor);
		onscreen.setFont(new Font(fontFace, Font.PLAIN, fontSize));

		ImageIcon icon = new ImageIcon(onscreenImage);
		JLabel draw = new JLabel(icon);
		jf.getContentPane().removeAll();

		Container cp = jf.getContentPane();  //not repainting image
		SpringLayout myLayout = new SpringLayout();
		cp.setLayout(myLayout);
		cp.add(draw);
		cp.repaint();
		jf.repaint();
	}

	//===================================================================================
	// Saving image to file.
	// NOTE: This function was taken directly from StdDraw.java
	//===================================================================================

	public static void save(String filename) 
	{
		File file = new File(filename);
		String suffix = filename.substring(filename.lastIndexOf('.') + 1);

		// png files
		if (suffix.toLowerCase().equals("png")) {
			try { ImageIO.write(onscreenImage, suffix, file); }
			catch (IOException e) { e.printStackTrace(); }
		}
		// need to change from ARGB to RGB for jpeg
		// reference: http://archives.java.sun.com/cgi-bin/wa?A2=ind0404&L=java2d-interest&D=0&P=2727
		else if (suffix.toLowerCase().equals("jpg")) 
		{
			WritableRaster raster = onscreenImage.getRaster();
			int width = onscreenImage.getWidth();
			int height = onscreenImage.getHeight();
			WritableRaster newRaster;
			newRaster = raster.createWritableChild(0, 0, width, height, 0, 0, new int[] {0, 1, 2});
			DirectColorModel cm = (DirectColorModel) onscreenImage.getColorModel();
			DirectColorModel newCM = new DirectColorModel(cm.getPixelSize(),
					cm.getRedMask(),
					cm.getGreenMask(),
					cm.getBlueMask());
			BufferedImage rgbBuffer = new BufferedImage(newCM, newRaster, false,  null);
			try { ImageIO.write(rgbBuffer, suffix, file); }
			catch (IOException e) { e.printStackTrace(); }
		}

		else 
		{
			System.out.println("Invalid image file type: " + suffix);
		}
	}

}