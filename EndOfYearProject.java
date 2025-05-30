import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.Ellipse2D;
import java.awt.Rectangle;
import java.util.ArrayList;

public class EndOfYearProject extends JPanel implements KeyListener, Runnable
{
	//instance variables should be declared here
	//so that they work in all methods
	//you will need to add more as you go
	private int x,y,a,b,level,deaths,reverse,s,c,d,h,enemyDirection,coinState;
	private Rectangle r1, r2, r3, r4, r5, tl, tr, tu, td;
	public Polygon poly, poly2, startArea, endArea;
	private JFrame frame;
	private Thread t;
	private boolean gameOn, right, left, up, down, inside, won, checker;
	private Font f, g;
	private Color color;
	private ArrayList<Rectangle> coins;
	private int coinsCollected;


	// Enemy management with ArrayLists
	private ArrayList<Rectangle> enemies;
	private ArrayList<Integer> enemyDirections;

	public EndOfYearProject()
	{
		//this is the constructor
		//leave the stuff alone that you don't recognize
		//it is necessary to make the motion happen
		//be sure to initialize any object type variables
		//in this section (rectangles, polygons, etc)
		//or they will be null and things won't work

		frame=new JFrame();
		x = 100; // Player Start X
		y = 200; // Player Start Y
		a = 224; // Enemy Start X
		b = 190; // Enemy Start Y
		c = 30; // Checkerboard Start X
		d = 30; // Checkerboard	 Start Y
		s = 40; // Checkerboard Size
		level = 1;
		reverse = 1;
		gameOn = true;
		checker = true;
		h = 10; // Initialize coin animation variable


		enemies = new ArrayList<Rectangle>();
		enemyDirections = new ArrayList<Integer>();

		coins = new ArrayList<Rectangle>();
		coinsCollected = 0;


		r1 = new Rectangle(x,y,25,25);
		r2 = new Rectangle(a,b,25,25);
		r3 = new Rectangle(596-a+184,b+40,25,25);
		r4 = new Rectangle(a,b+80,25,25);
		r5 = new Rectangle(596-a+184,b+120,25,25);

		tl = new Rectangle(x-2,y,25,25);
		tr = new Rectangle(x+2,y,25,25);
		tu = new Rectangle(x,y-2,25,25);
		td = new Rectangle(x,y+2,25,25);
		int[] xPoints;
		int[] yPoints;

		//setup for a polygon, coordinates in order
		switch(level) {
			case 1:
				c = 35;
				d = 150;
				xPoints = new int[]{c, c+3*s, c+3*s, c+4*s, c+4*s, c+13*s, c+13*s, c+18*s, c+18*s, c+15*s, c+15*s, c+14*s, c+14*s, c+5*s, c+5*s, c};
				yPoints = new int[]{d, d, d+5*s, d+5*s, d+1*s, d+1*s, d, d, d+6*s, d+6*s, d+1*s, d+1*s, d+5*s, d+5*s, d+6*s, d+6*s};
				poly = new Polygon(xPoints, yPoints, xPoints.length);

				// Setup level 1 enemies
				enemies.add(new Rectangle(224, 190, 25, 25));
				enemies.add(new Rectangle(596-224+184, 190+40, 25, 25));
				enemies.add(new Rectangle(224, 190+80, 25, 25));
				enemies.add(new Rectangle(596-224+184, 190+120, 25, 25));

				break;
			case 2:
				c = 100;
				d = 55;
				xPoints = new int[]{c,c+1*s,c+1*s,c+4*s,c+4*s,c+6*s,c+6*s,c+9*s,c+9*s,c+10*s,c+10*s,c+9*s,c+9*s,c+6*s,c+6*s,c+4*s,c+4*s,c+1*s,c+1*s,c};
				yPoints = new int[]{d+4*s,d+4*s,d+1*s,d+1*s,d,d,d+1*s,d+1*s,d+4*s,d+4*s,d+6*s,d+6*s,d+9*s,d+9*s,d+10*s,d+10*s,d+9*s,d+9*s,d+6*s,d+6*s};
				poly = new Polygon(xPoints, yPoints, xPoints.length);

				// Setup level 2 enemies
				enemies.add(new Rectangle(104, 104, 25, 25));
				enemies.add(new Rectangle(428, 376, 25, 25));
				enemies.add(new Rectangle(154, 154, 25, 25));  // smaller path
				enemies.add(new Rectangle(378, 326, 25, 25));  // smaller path
				enemies.add(new Rectangle(204, 204, 25, 25));  // smallest path
				enemies.add(new Rectangle(328, 276, 25, 25));  // smallest path

				// Set initial directions for level 2
				enemyDirections.add(0);
				enemyDirections.add(2);
				enemyDirections.add(0);
				enemyDirections.add(2);
				enemyDirections.add(0);
				enemyDirections.add(2);

				// Add coins for level 2
				setupLevel2Coins();
				break;
		}

		if(level == 1) {
			int[] startXPoints = {c, c+3*s, c+3*s, c};
			int[] startYPoints = {d, d, d+6*s, d+6*s};
			startArea = new Polygon(startXPoints, startYPoints, startXPoints.length);
			int[] endXPoints = {c+15*s, c+18*s, c+18*s, c+15*s};
			int[] endYPoints = {d, d, d+6*s, d+6*s};
			endArea = new Polygon(endXPoints, endYPoints, endXPoints.length);
		} else if(level == 2) {
			int[] startXPoints = {c+4*s, c+6*s, c+6*s, c+4*s};
			int[] startYPoints = {d+4*s, d+4*s, d+6*s, d+6*s};
			startArea = new Polygon(startXPoints, startYPoints, startXPoints.length);
		}

		//can change font type/size as necessary
		f=new Font("SOURCE SANS PLAIN",Font.BOLD,28);
		g=new Font("SOURCE SANS PLAIN",Font.BOLD,50);

		//don't change anything below here except maybe...
		frame.addKeyListener(this);
		frame.add(this);
		//the size of the frame (xWidth, yWidth)
		frame.setSize(800,500);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		t=new Thread(this);
		t.start();
	}

	// Method to setup level 2 coins
	private void setupLevel2Coins() {
		coins.clear();
		coins.add(new Rectangle(240, 210, 20, 20));
		coins.add(new Rectangle(260, 230, 20, 20));
		coins.add(new Rectangle(280, 250, 20, 20));
		coins.add(new Rectangle(300, 270, 20, 20));
	}

	public void drawEnemy(Graphics2D g2d, int ex, int ey) {
		g2d.setColor(Color.BLACK);
		g2d.fillOval(ex,ey,25,25);
		g2d.setColor(Color.BLUE);
		g2d.fillOval(ex+3,ey+3,19,19);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;

		//all painting (AND ONLY PAINTING) happens here!
		//Don't use this method to deal with mathematics
		//The painting imps aren't fond of math

		//fill background
		g2d.setPaint(new Color(180,181,254));
		g2d.fillRect(0,0,800,500);

		//top bar background
		g2d.setPaint(Color.BLACK);
		g2d.fillRect(0,0,800,50);

		//draw scoreboard
		g2d.setColor(Color.WHITE);
		g2d.setFont(f);
		g2d.drawString("MENU",25,35);

		//draw level
		g2d.setColor(Color.WHITE);
		g2d.setFont(f);
		g2d.drawString((level)+"/30",325,35);

		//draw deaths
		g2d.setColor(Color.WHITE);
		g2d.setFont(f);
		g2d.drawString("DEATHS: "+deaths,575,35);

		//polygon
		g2d.setColor(new Color(247,247,255));
		g2d.fill(poly);

		// Draw the border for better visibility
		g2d.setColor(Color.BLACK);
		g2d.draw(poly);

		g2d.setColor(new Color(181, 254, 180));
		g2d.fill(startArea);
		g2d.setColor(Color.BLACK);
		g2d.draw(startArea);

		if(endArea != null) {
			g2d.setColor(new Color(181, 254, 180));
			g2d.fill(endArea);
			g2d.setColor(Color.BLACK);
			g2d.draw(endArea);
		}

		switch(level) {
		    case 1:
		        checker = true;
		        for(int r=0; r<10; r++) {
		            for(int col=0; col<4; col++) {
		                int xPos = 35+(4+r)*s;
		                int yPos = d+(1+col)*s;

		                if((r+col) % 2 == 0) {
		                    g2d.setColor(new Color(230, 230, 255));
		                } else {
		                    g2d.setColor(new Color(247, 247, 255));
		                }
		                g2d.fillRect(xPos, yPos, s, s);
		            }
		        }
		        break;

		    case 2:
		        checker = true;
		        for(int j=0; j<8; j++) {
		            for(int k=0; k<8; k++) {
		                int xPos = c+(1+j)*s;
		                int yPos = d+(1+k)*s;

		                if((j+k) % 2 == 0) {
		                    g2d.setColor(new Color(230, 230, 255));
		                } else {
		                    g2d.setColor(new Color(247, 247, 255));
		                }
		                g2d.fillRect(xPos, yPos, s, s);
		            }
		        }
		        break;
		}

		//Your character
		g2d.setColor(Color.BLACK);
		g2d.fillRect(x,y,25,25);
		g2d.setColor(Color.RED);
		g2d.fillRect(x+3,y+3,19,19);

		// Draw enemies from ArrayList
		for(Rectangle enemy : enemies) {
			drawEnemy(g2d, enemy.x, enemy.y);
		}

		//coin
		for(Rectangle coin : coins) {
		    g2d.setColor(Color.BLACK);
		    g2d.fillOval(coin.x, coin.y, 20, 20);
		    g2d.setColor(Color.YELLOW);
		    g2d.fillOval(coin.x+2, coin.y+2, 16, 16);
		}


		if(!gameOn) {
			g2d.setColor(Color.BLACK);
			g2d.setFont(f);
			g2d.drawString("You won!", 340, 250);
			g2d.drawString("Press Enter to Play Level "+(level+1)+"!", 240, 280);
		}
	}

	public void run()
	{
		while(true)
		{
			if(gameOn)
			{
				switch(coinState) {
					case 0:
						h+=2;
						if(h>=20)
							coinState = 1;
						break;
					case 1:
					h-=2;
					if(h<=2)
						coinState = 0;
						break;
				}

				//if the right key is pressed down it will move right
				//you can use boolean variables to determine the
				//direcstion of the movement of your character
				if (right && insideRight())
					x += 3;
				if (left && insideLeft())
					x -= 3;
				if (up && insideUp())
					y -= 3;
				if(down && insideDown()) {
						y += 3;
				}

				// Enemy movement logic
				if(level == 1) {
					// Move enemies with alternating directions
					for(int i = 0; i < enemies.size(); i++) {
						Rectangle enemy = enemies.get(i);
						// Even indexed enemies move with reverse, odd indexed move opposite
						if(i % 2 == 0) {
							enemy.x += 4 * reverse;
						} else {
							enemy.x += 4 * (-reverse);
						}
					}

					// Check boundaries for direction reversal
					if(enemies.size() > 0 && (enemies.get(0).x >= 560 || enemies.get(0).x <= 200)) {
						reverse *= -1;
					}
				}

				if (level == 2) {
    				for(int i = 0; i < 6 && i < enemyDirections.size(); i++) {
				        Rectangle enemy = enemies.get(i);
				        int direction = enemyDirections.get(i);

				        int maxX = 428;
				        int minX = 150;
				        int maxY = 380;
				        int minY = 102;

				        if(i == 2 || i == 3) {
							maxX = 386;
							minX = 188;
							maxY = 340;
							minY = 144;
						}
				        if(i == 4 || i == 5) {
							maxX = 328+22;
							minX = 204+20;
							maxY = 300;
							minY = 180;
						}

				        switch(direction) {
				            case 0: // moving right
				                enemy.x += 0;
				                if(enemy.x >= maxX) {
				                    enemyDirections.set(i, 1);
				                }
				                break;
				            case 1: // moving down
				                enemy.y += 0;
				                if(enemy.y >= maxY) {
				                    enemyDirections.set(i, 2);
				                }
				                break;
				            case 2: // moving left
				                enemy.x -= 0;
				                if(enemy.x <= minX) {
				                    enemyDirections.set(i, 3);
				                }
				                break;
				            case 3: // moving up
				                enemy.y -= 0;
				                if(enemy.y <= minY) {
				                    enemyDirections.set(i, 0);
				                }
				                break;
				        }
				    }
				}

				//intersection is true if even one point is shared
				//this can be used to determine collisions with obstacles
				//or movement into the winning portion of the level
				r1 = new Rectangle(x,y,25,25);

				tl = new Rectangle(x-3,y,25,25);
				tr = new Rectangle(x+3,y,25,25);
				tu = new Rectangle(x,y-3,25,25);
				td = new Rectangle(x,y+3,25,25);

				// Check collision with enemies from ArrayList
				for(Rectangle enemy : enemies) {
					if (r1.intersects(enemy))
						death();
				}

				// Check coin collection
				for(int i = coins.size()-1; i >= 0; i--) {
				    Rectangle coin = coins.get(i);
				    if(r1.intersects(coin)) {
				        coins.remove(i);
				        coinsCollected++;
				        System.out.println("Coin collected! Total: " + coinsCollected); // Debug output
				    }
				}

				// Check win conditions
				if(level == 1) {
				    if (endArea != null && endArea.intersects(r1.x, r1.y, r1.width, r1.height)) {
				        win();
				    }
				} else if(level == 2) {
				    // Must collect all 4 coins AND return to starting area
				    if(coinsCollected >= 4 && startArea.intersects(r1.x, r1.y, r1.width, r1.height)) {
				        System.out.println("Level 2 won with " + coinsCollected + " coins and returned to start!"); // Debug output
				        win();
				    }
				}

				//must be entirely inside for contains to be true
				//polygons are useful for shapes that are irregular
				//such as the outline of the level
				//you can use the method contains to assure your character
				//remains inside the outline of the level
				if (poly.contains(r1)) {
					inside = true;
				} else {
					inside = false;
				}

				//this is the code for delay
				try
				{
					t.sleep(10);
				}catch(InterruptedException e)
				{
				}
				repaint();
			}
		}
	}

	public void death() {
		deaths++;
		System.out.println("Player died! Resetting position and coins."); // Debug output

		// Reset player position
		if (level == 1) {
		    x = 100;
		    y = 200;
		} else if (level == 2) {
		    x = c + 5*s;
		    y = d + 5*s;
		}

		// Reset coins collected and restore all coins
		coinsCollected = 0;
		if(level == 2) {
			setupLevel2Coins(); // Restore all coins
		}

		repaint();
	}

	public void updatePolygon() {
	    int[] xPoints;
	    int[] yPoints;

	    // Clear enemies for new level
	    enemies.clear();
	    enemyDirections.clear();

	    switch(level) {
	        case 1:
	            c = 35;
	            d = 150;
	            xPoints = new int[]{c, c+3*s, c+3*s, c+4*s, c+4*s, c+13*s, c+13*s, c+18*s, c+18*s, c+15*s, c+15*s, c+14*s, c+14*s, c+5*s, c+5*s, c};
	            yPoints = new int[]{d, d, d+5*s, d+5*s, d+1*s, d+1*s, d, d, d+6*s, d+6*s, d+1*s, d+1*s, d+5*s, d+5*s, d+6*s, d+6*s};
	            poly = new Polygon(xPoints, yPoints, xPoints.length);

	            int[] startXPoints = {c, c+3*s, c+3*s, c};
	            int[] startYPoints = {d, d, d+6*s, d+6*s};
	            startArea = new Polygon(startXPoints, startYPoints, startXPoints.length);

	            int[] endXPoints = {c+15*s, c+18*s, c+18*s, c+15*s};
	            int[] endYPoints = {d, d, d+6*s, d+6*s};
	            endArea = new Polygon(endXPoints, endYPoints, endXPoints.length);

	            // Setup level 1 enemies
				enemies.add(new Rectangle(224, 190, 25, 25));
				enemies.add(new Rectangle(596-224+184, 190+40, 25, 25));
				enemies.add(new Rectangle(224, 190+80, 25, 25));
				enemies.add(new Rectangle(596-224+184, 190+120, 25, 25));

				// Clear coins for level 1
				coins.clear();
	            break;

	        case 2:
			    c = 100;
			    d = 55;
			    xPoints = new int[]{c,c+1*s,c+1*s,c+4*s,c+4*s,c+6*s,c+6*s,c+9*s,c+9*s,c+10*s,c+10*s,c+9*s,c+9*s,c+6*s,c+6*s,c+4*s,c+4*s,c+1*s,c+1*s,c};
			    yPoints = new int[]{d+4*s,d+4*s,d+1*s,d+1*s,d,d,d+1*s,d+1*s,d+4*s,d+4*s,d+6*s,d+6*s,d+9*s,d+9*s,d+10*s,d+10*s,d+9*s,d+9*s,d+6*s,d+6*s};
			    poly = new Polygon(xPoints, yPoints, xPoints.length);

			    int[] startXPoints2 = {c+4*s, c+6*s, c+6*s, c+4*s};
			    int[] startYPoints2 = {d+4*s, d+4*s, d+6*s, d+6*s};
			    startArea = new Polygon(startXPoints2, startYPoints2, startXPoints2.length);
			    endArea = null;

			    // Setup level 2 enemies - 6 moving
			    enemies.add(new Rectangle(104, 104, 25, 25));
			    enemies.add(new Rectangle(428, 376, 25, 25));
			    enemies.add(new Rectangle(154, 154, 25, 25));
			    enemies.add(new Rectangle(378, 326, 25, 25));
			    enemies.add(new Rectangle(204, 204, 25, 25));
			    enemies.add(new Rectangle(328, 276, 25, 25));

			    // Add 12 stationary enemies
			    enemies.add(new Rectangle(228+40, 142-40, 25, 25));
			    enemies.add(new Rectangle(268+40, 180-40, 25, 25));
			    enemies.add(new Rectangle(268+80, 222-40, 25, 25));
			    enemies.add(new Rectangle(228, 142+160, 25, 25));
				enemies.add(new Rectangle(268, 180+160, 25, 25));
			    enemies.add(new Rectangle(268+40, 222+160, 25, 25));

			    enemies.add(new Rectangle(228-80, 222+40, 25, 25));
				enemies.add(new Rectangle(268-80, 180+40, 25, 25));
				enemies.add(new Rectangle(268-40, 142+40, 25, 25));
				enemies.add(new Rectangle(268+40+120, 142+80, 25, 25));
				enemies.add(new Rectangle(268+120, 180+80, 25, 25));
			    enemies.add(new Rectangle(228+120, 222+80, 25, 25));

			    // Set initial directions for level 2 - only for moving enemies
			    enemyDirections.add(0);
			    enemyDirections.add(2);
			    enemyDirections.add(0);
			    enemyDirections.add(2);
			    enemyDirections.add(0);
			    enemyDirections.add(2);

			    // Setup coins for level 2
			    setupLevel2Coins();
			    break;
	    }
	}

	public void win() {
	    System.out.println("Win called! Current level: " + level); // Debug output
	    level++;

	    if (level == 1) {
	        x = 100;
	        y = 200;
	    } else if (level == 2) {
			System.out.println("Moving to level 2");
	        x = 250;
	        y = 250;
	    } else if (level == 3) {
			System.out.println("Resetting to level 1");
			level = 1;
			x = 100;
	        y = 200;
		}

		// Reset coins collected
		coinsCollected = 0;
		updatePolygon();
	}

	public boolean insideRight() {
		if(level == 1) {
			if(poly.contains(tr)) {
				return true;
			}
		}
		if (level == 2) {
			if(poly.contains(tr)) {
				return true;
			}
		}
		return false;
	}

	public boolean insideLeft() {
		if(poly.contains(tl)) {
			return true;
		}
		return false;
	}

	public boolean insideUp() {
		if(poly.contains(tu)) {
			return true;
		}
		return false;
	}

	public boolean insideDown() {
		if(poly.contains(td)) {
			return true;
		}
		return false;
	}

	public void keyPressed(KeyEvent ke)
	{
		//this method will do stuff if you press/hold a key down
		System.out.println(ke.getKeyCode());

		//39 is right arrow key
		if(ke.getKeyCode()==39 || ke.getKeyCode()==68)
			right = true;

		if(ke.getKeyCode()==38 || ke.getKeyCode()==87)
			up = true;

		if(ke.getKeyCode()==37 || ke.getKeyCode()==65)
			left = true;

		if(ke.getKeyCode()==40 || ke.getKeyCode()==83)
			down = true;
	}

	public void keyReleased(KeyEvent ke)
	{
		//this will do stuff if you let go of a key
		if(ke.getKeyCode()==39 || ke.getKeyCode()==68)
			right = false;

		if(ke.getKeyCode()==38 || ke.getKeyCode()==87)
			up = false;

		if(ke.getKeyCode()==37 || ke.getKeyCode()==65)
			left = false;

		if(ke.getKeyCode()==40 || ke.getKeyCode()==83)
			down = false;

	}

	public void keyTyped(KeyEvent ke)
	{
		//prob don't use this as it requires a key to be
		//pressed and released to do anything
		if(!gameOn) {
			if(ke.getKeyChar() == '\n') // Changed to use getKeyChar() for Enter key
				gameOn = true;
		}
	}

	public static void main(String args[])
	{
		EndOfYearProject app=new EndOfYearProject();
	}
}
