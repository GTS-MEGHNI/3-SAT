import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Game implements Runnable {
	private int fps = 200;
	private int limit = 0;
	private ArrayList<Integer> Xlist = new ArrayList<Integer>();
	private	ArrayList<Integer>	Ylist = new ArrayList<Integer>();
	private	ArrayList<Integer>	Slist = new ArrayList<Integer>();
	private int TheX,TheY,SolX= -10,SolY= -10,Prog=0;
	private static int searchY= -10,searchX = -10;
	private int counter = 0;
	private Window window;
	private int width, height;
	private String title;
	private String Progress = "0 from 91";
	private boolean running = false;
	private Thread thread;

	private BufferStrategy bs;
	private Graphics g;
	
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getTitle() {
		return title;
	}



	public Game(String title, int width, int height,int TheX, int TheY, int fps) {
		this.title = title;		
		this.width = width;
		this.height = height;
		this.TheX = TheX;
		this.TheY = TheY;
		this.fps = fps;
	}
	public Game(String title, int width, int height,int TheX, int TheY) {
		this.title = title;		
		this.width = width;
		this.height = height;
		this.TheX = TheX;
		this.TheY = TheY;
	}
	
	public void setCounter(int counter){
		this.counter = counter;
	}
	private void init() {
		window = new Window(title, width, height);
	//	BigImage = ImageLoader.loadImage("/images/Big.png");
	}
	
	public void tick(int fps) {
		int nano =  (int)(((float)1000/(float)fps)*1000);
		  nano -= ((int)(1000/fps))*1000;
	//	System.out.println(nano);
		try {
			Thread.sleep( 1000/fps, nano*100 );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void render(int TheX,int TheY,int counter) {
		bs = window.getCanvas().getBufferStrategy();
		if(bs == null) {
			window.getCanvas().createBufferStrategy(3);
			return;
		}
		
		g = bs.getDrawGraphics();
		
		g.clearRect(0, 0, width, height);	
		
		
		//drawing
		g.setColor(Color.yellow);
		g.fillRect(0, 10, Prog, 20);
		g.setColor(Color.black);
		g.drawString(Progress, Prog-160, 20);
		g.setColor(Color.red);
		g.fillRect(TheX, TheY, 10, 10);
		this.limit++;
		Xlist.add(searchX);
		Ylist.add(searchY);		
		// 			uncomment if you want to have Fun!
		//Integer state = 1024*searchY-1 +searchX;
		//Slist.add(state);
		for (int i = 0; i < Xlist.size(); i++) {
			g.setColor(Color.blue);
			g.fillRect(Xlist.get(i),Ylist.get(i), 3, 3);	
			//g.drawString(Integer.toBinaryString(Slist.get(i)), Xlist.get(i),Ylist.get(i));
		}
		
		
		g.setColor(Color.green);
		if(SolX!=-10) {
			g.drawString("Solution", SolX, SolY);
		}
		g.fillRect(SolX, SolY, 10, 10);
		bs.show();
	//	g.dispose();
	}
	
	public void setProg(int Progress) {
		this.Prog = Progress;
	}
	
	
	public void setProgress(String Progress) {
		this.Progress = Progress;
	}
	

	public void setSearchX(int searchX) {
		Game.searchX = searchX;
	}


	public void setSearchY(int searchY) {
		Game.searchY = searchY;
	}


	public void setSolX(int solX) {
		SolX = solX;
	}


	public void setSolY(int solY) {
		SolY = solY;
	}

	public void run() {
		init();
		
		while(running) {
			tick(this.fps);
			render(this.TheX, this.TheY,this.counter);
		}
	}
	
	public synchronized void start() {
		if(running) return; 
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop() {
		if(!running) return;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
}
