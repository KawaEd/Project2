package rbadia.voidspace.main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

import rbadia.voidspace.graphics.GraphicsManager;
import rbadia.voidspace.model.Asteroid;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.EnemyShip;
import rbadia.voidspace.model.Ship;
import rbadia.voidspace.sounds.SoundManager;

/**
 * Main game screen. Handles all game graphics updates and some of the game logic.
 */
public class GameScreen extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private BufferedImage backBuffer;
	private Graphics2D g2d;
	
	private static final int NEW_ENEMY_DELAY = 100;
	private static final int NEW_SHIP_DELAY = 500;
	private static final int NEW_ASTEROID_DELAY = 500;
	private static final int BULLET_DELAY = 1000;
	
	private long lastEnemyTime;
	private long lastShipTime;
	private long lastAsteroidTime;
	
	private Rectangle enemyExplosion;
	private Rectangle asteroidExplosion;
	private Rectangle shipExplosion;
	
	private JLabel shipsValueLabel;
	private JLabel destroyedValueLabel;
	private JLabel pointsValueLabel;
	
	private Random rand;
	
	private Font originalFont;
	private Font bigFont;
	private Font biggestFont;
	
	private GameStatus status;
	private SoundManager soundMan;
	private GraphicsManager graphicsMan;
	private GameLogic gameLogic;

	public boolean yes = true;

	/**
	 * This method initializes 
	 * 
	 */
	public GameScreen() {
		super();
		// initialize random number generator
		rand = new Random();
		
		initialize();
		
		// init graphics manager
		graphicsMan = new GraphicsManager();
		
		// init back buffer image
		backBuffer = new BufferedImage(500, 400, BufferedImage.TYPE_INT_RGB);
		g2d = backBuffer.createGraphics();
	}

	/**
	 * Initialization method (for VE compatibility).
	 */
	private void initialize() {
		// set panel properties
        this.setSize(new Dimension(500, 400));
        this.setPreferredSize(new Dimension(500, 400));
        this.setBackground(Color.BLACK);
	}

	/**
	 * Update the game screen.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// draw current backbuffer to the actual game screen
		g.drawImage(backBuffer, 0, 0, this);
	}
	
	/**
	 * Update the game screen's backbuffer image.
	 */
	public void updateScreen(){
		Ship ship = gameLogic.getShip();
		EnemyShip enemy = gameLogic.getEnemy();
		Asteroid asteroid = gameLogic.getAsteroid();
		List<Bullet> bullets = gameLogic.getBullets();
		List<Bullet> enemyBullets = gameLogic.getEnemyBullets();
		
		// set orignal font - for later use
		if(this.originalFont == null){
			this.originalFont = g2d.getFont();
			this.bigFont = originalFont;
		}
		
		// erase screen
		g2d.setPaint(Color.BLACK);
		g2d.fillRect(0, 0, getSize().width, getSize().height);

		// draw 50 random stars
		drawStars(50);
		
		// if the game is starting, draw "Get Ready" message
		if(status.isGameStarting()){
			drawGetReady();
			return;
		}
		
		// if the game is over, draw the "Game Over" message
		if(status.isGameOver()){
			// draw the message
			drawGameOver();
			
			long currentTime = System.currentTimeMillis();
			// draw the explosions until their time passes
			if((currentTime - lastAsteroidTime) < NEW_ASTEROID_DELAY){
				graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
			}
			if((currentTime - lastShipTime) < NEW_SHIP_DELAY){
				graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
			}
			if((currentTime - lastEnemyTime) < NEW_ENEMY_DELAY)
			{
				graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
			}
			return;
			
		}
		
		// the game has not started yet
		if(!status.isGameStarted()){
			// draw game title screen
			initialMessage();
			return;
		}
//		else if(yes == false){
//			// draw game Level 2
//			GameScreen1();
//			return;
//		}
		// draw asteroid
		if(!status.isNewAsteroid())
		{
			System.out.println(asteroid.getY());
			System.out.println(asteroid.getSpeed());
			System.out.println(this.getHeight());
			// draw the asteroid until it reaches the bottom of the screen
			if(asteroid.getY() + asteroid.getSpeed() < getHeight())
			{
				asteroid.translate(0, asteroid.getSpeed());
				graphicsMan.drawAsteroid(asteroid, g2d, this);
			}
			else
			{
				asteroid.setLocation(rand.nextInt(getWidth() - asteroid.width), 0);
			}
		}
		else{
			long currentTime = System.currentTimeMillis();
			if((currentTime - lastAsteroidTime) > NEW_ASTEROID_DELAY){
				// draw a new asteroid
				lastAsteroidTime = currentTime;
				status.setNewAsteroid(false);
				asteroid.setLocation(rand.nextInt(getWidth() - asteroid.width), 0);
			}
			else{
				// draw explosion
				graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
			}
		}
		
		// draw bullets
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			graphicsMan.drawBullet(bullet, g2d, this);
			
			boolean remove = gameLogic.moveBullet(bullet);
			if(remove){
				bullets.remove(i);
				i--;
			}
		}
		
		// check bullet-asteroid collisions
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			if(asteroid.intersects(bullet)){
				// increase asteroids destroyed count
				status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 1);
				
				//Update Score
			
				status.setPoints(status.getPoints() + 100);
				
				
				//update points label
				pointsValueLabel.setText(Integer.toString(status.getPoints()));
				
				// "remove" asteroid
		        asteroidExplosion = new Rectangle(
		        		asteroid.x,
		        		asteroid.y,
		        		asteroid.width,
		        		asteroid.height);
				asteroid.setLocation(-asteroid.width, -asteroid.height);
				status.setNewAsteroid(true);
				lastAsteroidTime = System.currentTimeMillis();
				
				// play asteroid explosion sound
				soundMan.playAsteroidExplosionSound();
				
				// remove bullet
				bullets.remove(i);
				
				
			
				break;
			}
		}
		
		
		if(status.getAsteroidsDestroyed() >= 5)
		{
			
			dogameLevel2();
			
		}
		
		
		
		// draw ship
		if(!status.isNewShip()){
			// draw it in its current location
			graphicsMan.drawShip(ship, g2d, this);
		}
		else{
			// draw a new one
			long currentTime = System.currentTimeMillis();
			if((currentTime - lastShipTime) > NEW_SHIP_DELAY){
				lastShipTime = currentTime;
				status.setNewShip(false);
				ship = gameLogic.newShip(this);
			}
			else{
				// draw explosion
				graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
			}
		}
		
		// check ship-asteroid collisions
		if(asteroid.intersects(ship)){
			// decrease number of ships left
			status.setShipsLeft(status.getShipsLeft() - 1);
			
			status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 1);
			
			//Update Score
			
			status.setPoints(status.getPoints() + 100);

			//update points label
			pointsValueLabel.setText(Integer.toString(status.getPoints()));
			
			// "remove" asteroid
	        asteroidExplosion = new Rectangle(
	        		asteroid.x,
	        		asteroid.y,
	        		asteroid.width,
	        		asteroid.height);
			asteroid.setLocation(-asteroid.width, -asteroid.height);
			status.setNewAsteroid(true);
			lastAsteroidTime = System.currentTimeMillis();
			
			// "remove" ship
	        shipExplosion = new Rectangle(
	        		ship.x,
	        		ship.y,
	        		ship.width,
	        		ship.height);
			ship.setLocation(this.getWidth() + ship.width, -ship.height);
			status.setNewShip(true);
			lastShipTime = System.currentTimeMillis();
			
			// play ship explosion sound
			soundMan.playShipExplosionSound();
			// play asteroid explosion sound
			soundMan.playAsteroidExplosionSound();
		}
		
		// update asteroids destroyed label
		destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()));
		
		// update ships left label
		shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));
	}

	/**
	 * Draws the "Game Over" message.
	 */
	private void drawGameOver() {
		String gameOverStr = "GAME OVER";
		Font currentFont = biggestFont == null? bigFont : biggestFont;
		float fontSize = currentFont.getSize2D();
		bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD);
		FontMetrics fm = g2d.getFontMetrics(bigFont);
		int strWidth = fm.stringWidth(gameOverStr);
		if(strWidth > this.getWidth() - 10){
			biggestFont = currentFont;
			bigFont = biggestFont;
			fm = g2d.getFontMetrics(bigFont);
			strWidth = fm.stringWidth(gameOverStr);
		}
		int ascent = fm.getAscent();
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2;
		g2d.setFont(bigFont);
		g2d.setPaint(Color.WHITE);
		g2d.drawString(gameOverStr, strX, strY);
	}

	/**
	 * Draws the initial "Get Ready!" message.
	 */
	private void drawGetReady() {
		String readyStr = "Get Ready!";
		g2d.setFont(originalFont.deriveFont(originalFont.getSize2D() + 1));
		FontMetrics fm = g2d.getFontMetrics();
		int ascent = fm.getAscent();
		int strWidth = fm.stringWidth(readyStr);
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2;
		g2d.setPaint(Color.WHITE);
		g2d.drawString(readyStr, strX, strY);
	}

	/**
	 * Draws the specified number of stars randomly on the game screen.
	 * @param numberOfStars the number of stars to draw
	 */
	private void drawStars(int numberOfStars) {
		g2d.setColor(Color.WHITE);
		for(int i=0; i<numberOfStars; i++){
			int x = (int)(Math.random() * this.getWidth());
			int y = (int)(Math.random() * this.getHeight());
			g2d.drawLine(x, y, x, y);
		}
	}

	/**
	 * Display initial game title screen.
	 */
	private void initialMessage() {
		String gameTitleStr = "Void Space";
		
		Font currentFont = biggestFont == null? bigFont : biggestFont;
		float fontSize = currentFont.getSize2D();
		bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD).deriveFont(Font.ITALIC);
		FontMetrics fm = g2d.getFontMetrics(bigFont);
		int strWidth = fm.stringWidth(gameTitleStr);
		if(strWidth > this.getWidth() - 10){
			bigFont = currentFont;
			biggestFont = currentFont;
			fm = g2d.getFontMetrics(currentFont);
			strWidth = fm.stringWidth(gameTitleStr);
		}
		g2d.setFont(bigFont);
		int ascent = fm.getAscent();
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2 - ascent;
		g2d.setPaint(Color.YELLOW);
		g2d.drawString(gameTitleStr, strX, strY);
		
		g2d.setFont(originalFont);
		fm = g2d.getFontMetrics();
		String newGameStr = "Press <Space> to Start a New Game.";
		strWidth = fm.stringWidth(newGameStr);
		strX = (this.getWidth() - strWidth)/2;
		strY = (this.getHeight() + fm.getAscent())/2 + ascent + 16;
		g2d.setPaint(Color.WHITE);
		g2d.drawString(newGameStr, strX, strY);
		
		fm = g2d.getFontMetrics();
		String exitGameStr = "Press <Esc> to Exit the Game.";
		strWidth = fm.stringWidth(exitGameStr);
		strX = (this.getWidth() - strWidth)/2;
		strY = strY + 16;
		g2d.drawString(exitGameStr, strX, strY);
	}
	
	/**
	 * Prepare screen for game over.
	 */
	public void doGameOver(){
		shipsValueLabel.setForeground(new Color(128, 0, 0));
	}
	
	/**
	 * Prepare screen for a new game.
	 */
	public void doNewGame(){		
		lastAsteroidTime = -NEW_ASTEROID_DELAY;
		lastShipTime = -NEW_SHIP_DELAY;
				
		bigFont = originalFont;
		biggestFont = null;
				
        // set labels' text
		shipsValueLabel.setForeground(Color.BLACK);
		shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));
		destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()));
	}
//public void updateScreen1()
//	{
//		
//		Ship ship = gameLogic.getShip();
//		EnemyShip enemy = gameLogic.getEnemy();
//
////		BigAsteroid bigAsteroid = gameLogic.getBigAsteroid();
////		Asteroid asteroid = gameLogic.getAsteroid();
//	
//		List<EnemyShip> enemies = gameLogic.getEnemies();
//		List<Asteroid> asteroids = gameLogic.getAsteroids();
//		List<Bullet> bullets = gameLogic.getBullets();
//		List<Bullet> enemyBullets = gameLogic.getEnemyBullets();
//
//	}

	public void dogameLevel2(){		
		
		lastAsteroidTime = -NEW_ASTEROID_DELAY;
		lastShipTime = -NEW_SHIP_DELAY;
		
		EnemyShip enemy = gameLogic.getEnemy();
		Ship ship= gameLogic.getShip();;
		List<Bullet>bullets = gameLogic.getBullets();
		List<Bullet> enemyBullets = gameLogic.getEnemyBullets();
		List<Asteroid> asteroids = gameLogic.getAsteroids();
		List<EnemyShip> enemies = gameLogic.getEnemies();
//		updateScreen1();
//		GameScreen gs1 = new GameScreen1();
//		this.add(gs1);
//		status.setGameStarted(false);
		
		
			// set orignal font - for later use
			if(this.originalFont == null)
			{
				this.originalFont = g2d.getFont();
				this.bigFont = originalFont;
			}
			
			
				// erase screen
				g2d.setPaint(Color.BLACK);
				g2d.fillRect(0, 0, getSize().width, getSize().height);

				// draw 50 random stars
				//drawStars(50);
				
				//if the game is starting, draw "Get Ready" message
//				if(status.isGameStarting())
//				{
//					drawLevel2();
//					//status.isGameStarted();
//					return;
//				}
				
				// if the game is over, draw the "Game Over" message
				if(status.isGameOver())
				{
					// draw the message
					drawGameOver();
					
					long currentTime = System.currentTimeMillis();
					// draw the explosions until their time passes
					if((currentTime - lastAsteroidTime) < NEW_ASTEROID_DELAY)
					{
						graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
					}
					if((currentTime - lastShipTime) < NEW_SHIP_DELAY)
					{
						graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
					}
					if((currentTime - lastEnemyTime) < NEW_ENEMY_DELAY)
					{
						graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
					}
					return;
				}
//				
//				// the game has not started yet
//				if(!status.isGameStarted())
//				{
//					// draw game title screen
//					//initialMessage();
//					drawLevel2();
//					return;
//				}

				//Draw multiple asteroids
				if(!status.isNewAsteroid())
				{	
					long lastAsteroid = 0;
					long currentTime = System.currentTimeMillis();
					
					
					for(Asteroid astr:asteroids)
					{
						if(currentTime - lastAsteroid >= NEW_ASTEROID_DELAY)
						{
							
							graphicsMan.drawAsteroid(astr, g2d, this);
						}
						
						if(astr.getY() + astr.getSpeed()< this.getHeight())
						{
							astr.translate(rand.nextInt(10), astr.getSpeed());
							graphicsMan.drawAsteroid(astr, g2d, this);
						}
						
						else
						{
							astr.setLocation(- astr.width, -astr.height);
							graphicsMan.drawAsteroid(astr, g2d, this);
						}
						
						lastAsteroid = currentTime;
					}
			
				}
				
				else{
					
					long currentTime = System.currentTimeMillis();
					for(int i =0 ; i < asteroids.size();i++)
					{
						Asteroid asteroid1 = asteroids.get(i);
					
							// draw a new asteroid
							lastAsteroidTime = currentTime;
							status.setNewAsteroid(false);
							//modified where they spawn
							asteroid1.setLocation(rand.nextInt(getWidth() - asteroid1.width), rand.nextInt(getHeight() - asteroid1.height));
					
							// draw explosion
							graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
						
					}
				}
				


				// draw bullets
			for(int i=0; i<bullets.size(); i++){
					Bullet bullet = bullets.get(i);
					graphicsMan.drawBullet(bullet, g2d, this);
					
					boolean remove = gameLogic.moveBullet(bullet);
					if(remove)
					{
						bullets.remove(i);
						i--;
					}
				}
				
				if(enemyBullets.size() <= 5)
				{
					long bulletTime = 0;
					long currentTime = System.currentTimeMillis();
					if(currentTime - bulletTime > BULLET_DELAY)
					{
						bulletTime = currentTime;
						enemyAttack(enemy);
						
					}
						
				}
				
				//draw enemy bullets
				for(int i =0; i < enemyBullets.size();i++)
				{
					Bullet bullet = enemyBullets.get(i);
					graphicsMan.drawBullet(bullet, g2d, this);
					
					boolean remove = gameLogic.moveEnemyBullet(bullet);
					boolean collided = enemyBulletAgainstShip(bullet, ship);
					if(remove  || collided )
					{
						enemyBullets.remove(i);
						i--;
					}
					
				}
			

			////////////////////////////////	
				//draw enemy
				if(!status.isNewEnemy())
				{

					long currentTime = System.currentTimeMillis();
					
					//seems the delay is too long
					//Causes the enemy to flicker in and out
//					if((currentTime - lastEnemyTime) >= NEW_ENEMY_DELAY)
//					{
						lastEnemyTime = currentTime;
						status.setNewEnemy(false);
						enemy = gameLogic.newEnemy(this);
				
						
						if(enemy.getY() + enemy.getEnemySpeed()< this.getWidth())
						{
							enemy.translate(10, enemy.getEnemySpeed());
							graphicsMan.drawEnemy(enemy, g2d, this);
						}
						
						if(enemy.getX() + enemy.getEnemySpeed() > this.getWidth())
						{
							enemy.setLocation(0,0);
							graphicsMan.drawEnemy(enemy, g2d, this);
						}

//						else
//							{
//								enemy.setLocation(- enemy.width, -enemy.height);
//								graphicsMan.drawEnemy(enemy, g2d, this);
//							}
//							
//							lastEnemyTime = currentTime;
						}	
			
				else
				{
					//draw a new one
					long currentTime = System.currentTimeMillis();
					if((currentTime - lastEnemyTime) > NEW_ENEMY_DELAY)
					{
						lastEnemyTime = currentTime;
						status.setNewEnemy(false);
						enemy = gameLogic.newEnemy(this);
					}
					else
					{
						graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
					}
					
				}
				
				astrShipCollision(asteroids, ship);
				astrBulletCollision(asteroids, bullets);
				enemyShipCollision(enemy, ship);
				enemyBulletCollision(enemy, bullets);
				enemyBulletShipCollision(enemyBullets, ship);
			}
	

			/**
			 * Draws the initial "Get Ready!" message.
			 */
//			private void drawGetReady() {
//				String readyStr = "Get Ready!";
//				g2d.setFont(originalFont.deriveFont(originalFont.getSize2D() + 1));
//				FontMetrics fm = g2d.getFontMetrics();
//				int ascent = fm.getAscent();
//				int strWidth = fm.stringWidth(readyStr);
//				int strX = (this.getWidth() - strWidth)/2;
//				int strY = (this.getHeight() + ascent)/2;
//				g2d.setPaint(Color.WHITE);
//				g2d.drawString(readyStr, strX, strY);
//			}
		//	
//			private void drawLevel2() {
//				String readyStr = "Level 2!";
//				g2d.setFont(originalFont.deriveFont(originalFont.getSize2D() + 1));
//				FontMetrics fm = g2d.getFontMetrics();
//				int ascent = fm.getAscent();
//				int strWidth = fm.stringWidth(readyStr);
//				int strX = (this.getWidth() - strWidth)/2;
//				int strY = (this.getHeight() + ascent)/2;
//				g2d.setPaint(Color.WHITE);
//				g2d.drawString(readyStr, strX, strY);
//			}
//			/**
//			 * Draws the specified number of stars randomly on the game screen.
//			 * @param numberOfStars the number of stars to draw
//			 */
		//
//			/**
//			 * Display initial game title screen.
//			 */
//			private void initialMessage() {
//				String gameTitleStr = "Void Space";
//				
//				Font currentFont = biggestFont == null? bigFont : biggestFont;
//				float fontSize = currentFont.getSize2D();
//				bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD).deriveFont(Font.ITALIC);
//				FontMetrics fm = g2d.getFontMetrics(bigFont);
//				int strWidth = fm.stringWidth(gameTitleStr);
//				if(strWidth > this.getWidth() - 10){
//					bigFont = currentFont;
//					biggestFont = currentFont;
//					fm = g2d.getFontMetrics(currentFont);
//					strWidth = fm.stringWidth(gameTitleStr);
//				}
//				g2d.setFont(bigFont);
//				int ascent = fm.getAscent();
//				int strX = (this.getWidth() - strWidth)/2;
//				int strY = (this.getHeight() + ascent)/2 - ascent;
//				g2d.setPaint(Color.YELLOW);
//				g2d.drawString(gameTitleStr, strX, strY);
//				
//				g2d.setFont(originalFont);
//				fm = g2d.getFontMetrics();
//				String newGameStr = "Press <Space> to Start a New Game.";
//				strWidth = fm.stringWidth(newGameStr);
//				strX = (this.getWidth() - strWidth)/2;
//				strY = (this.getHeight() + fm.getAscent())/2 + ascent + 16;
//				g2d.setPaint(Color.WHITE);
//				g2d.drawString(newGameStr, strX, strY);
//				
//				fm = g2d.getFontMetrics();
//				String exitGameStr = "Press <Esc> to Exit the Game.";
//				strWidth = fm.stringWidth(exitGameStr);
//				strX = (this.getWidth() - strWidth)/2;
//				strY = strY + 16;
//				g2d.drawString(exitGameStr, strX, strY);
//			}
		//	


			/**
			 * Collision detection methods
			 */
			public void astrBulletCollision(List<Asteroid> asteroids, List<Bullet> bullets)
			{

				
				for(Bullet bull: bullets)
				{
					for(Asteroid astr: asteroids)
					{
						if(astr.intersects(bull))
						{
							
							asteroidExplosion = new Rectangle(astr.x,astr.y,astr.width, astr.height);
							graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
							
							//sets the current asteroids location to a new one
							//Because it was 'destroyed'...
							astr.setLocation(astr.width, astr.height);
							
							//Update Score
							status.setAsteroidsDestroyed(status.getAsteroidsDestroyed()+1);
							status.setPoints(status.getPoints() + 100);
							
//							//We need to ADD SCORES AND DESTROYED VALUES
							
							//update points label
							pointsValueLabel.setText(Integer.toString(status.getPoints()));
							
							// play ship explosion sound
							soundMan.playShipExplosionSound();
							
							
							
							// update asteroids destroyed label
							destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()));
							
							// update ships left label
							shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));
							
							boolean remove = gameLogic.moveBullet(bull);
							if(remove)
							{
								bullets.remove(bull);
								
							}
							
							
						}
					}
				}
				
				
			}
			
			public void astrShipCollision(List<Asteroid> asteroids, Ship ship)
			{
					
					
					for(Asteroid astr: asteroids)
					{
						if(astr.intersects(ship))
						{
							asteroidExplosion = new Rectangle(astr.x,astr.y,astr.width, astr.height);
							shipExplosion = new Rectangle(
					        		ship.x,
					        		ship.y,
					        		ship.width,
					        		ship.height);
						
							//Update screen
							graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
							graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
							astr.setLocation(astr.width, astr.height);
							
							ship.setLocation(this.getWidth()-ship.width,this.getHeight() -ship.height);
							graphicsMan.drawShip(ship, g2d, this);
							
							//Update score
							status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() +1);
							status.setShipsLeft(status.getShipsLeft() -1);
							status.setPoints(status.getPoints() +100);
							
							
							// play ship explosion sound
							soundMan.playShipExplosionSound();
							
							//update points label
							pointsValueLabel.setText(Integer.toString(status.getPoints()));
							
							// update asteroids destroyed label
							destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()));
							
							// update ships left label
							shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));
						}
					}
				
				
			}
			
			public void enemyBulletCollision(EnemyShip enemy, List<Bullet> bullets)
			{
				
				for(Bullet bull: bullets)
				{
					
						if(enemy.intersects(bull))
						{
							shipExplosion = new Rectangle(
					        		enemy.x,
					        		enemy.y,
					        		enemy.width,
					        		enemy.height);
							graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
							
							//We have to reset the enemy's location to where it originally spawns
							status.setPoints(status.getPoints() + 200);
							//bullets.remove(bull);
							
							enemy.setLocation(0, 0);
							
							// play ship explosion sound
							soundMan.playShipExplosionSound();
							
							//update points label
							pointsValueLabel.setText(Integer.toString(status.getPoints()));
							
							// update asteroids destroyed label
							destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()));
							
							// update ships left label
							shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));
						}
				
				}
				
			}
			
			public void enemyBulletShipCollision(List<Bullet> bullets,Ship ship)
			{
				for(Bullet bullet: bullets)
				{
					if(bullet.intersects(ship))
					{	
						shipExplosion = new Rectangle(
				        		ship.x,
				        		ship.y,
				        		ship.width,
				        		ship.height);
						graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
						
						//We have to reset the enemy's location to where it originally spawns
						
						
						ship.setLocation(this.getWidth()-ship.width,this.getHeight()-ship.height);
						
						// play ship explosion sound
						soundMan.playShipExplosionSound();	
						
						// update ships left label
						status.setShipsLeft(status.getShipsLeft()-1);
						shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));
						
						boolean remove = gameLogic.moveEnemyBullet(bullet);
						if(remove)
						{
							bullets.remove(bullet);
						}
						
					}
				}
			}
			
			public void enemyShipCollision(EnemyShip enemy, Ship ship)
			{
			
				if(enemy.intersects(ship))
				{
					
				 shipExplosion = new Rectangle(
			        		ship.x,
			        		ship.y,
			        		ship.width,
			        		ship.height);
					status.setShipsLeft(status.getShipsLeft() -1);
					status.setPoints(status.getPoints() + 200);
					
					enemyExplosion = new Rectangle( enemy.x,enemy.y, enemy.width, enemy.height);
					
					graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
					graphicsMan.drawShipExplosion(enemyExplosion, g2d, this);
					//Reset ship & enemy location
					ship.setLocation(-ship.width, -ship.height );
					graphicsMan.drawShip(ship, g2d, this);
					
					//We need to ADD SCORES AND DESTROYED VALUES
					
					// play ship explosion sound
					soundMan.playShipExplosionSound();
					
					//update points label
					pointsValueLabel.setText(Integer.toString(status.getPoints()));
					
					// update asteroids destroyed label
					destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()));
					
					// update ships left label
					shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));
				}
				
				
			}
			
			public boolean enemyBulletAgainstShip(Bullet bullet,Ship ship)
			{
				boolean collided =false;
				if(bullet.intersects(ship))
				{
					collided = true;
					
					
					graphicsMan.drawShip(ship, g2d, this);
					
					 shipExplosion = new Rectangle(
				        		ship.x,
				        		ship.y,
				        		ship.width,
				        		ship.height);
						status.setShipsLeft(status.getShipsLeft() -1);
						
						
						
						
						graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
						
						//Reset ship location
						ship.setLocation(this.getWidth()-ship.width/2, this.getHeight()-ship.height/2 );
						graphicsMan.drawShip(ship, g2d, this);
						
						//We need to ADD SCORES AND DESTROYED VALUES
						
						// play ship explosion sound
						soundMan.playShipExplosionSound();
						
						// update ships left label
						shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));
				}
				
				return collided;
			}

//			public void enemyAttack(EnemyShip enemy)
//			{
//				
//						gameLogic.fireEnemyBullet();
//				
//			}
//	

	/**
	 * Sets the game graphics manager.
	 * @param graphicsMan the graphics manager
	 */
	public void setGraphicsMan(GraphicsManager graphicsMan) {
		this.graphicsMan = graphicsMan;
	}

	/**
	 * Sets the game logic handler
	 * @param gameLogic the game logic handler
	 */
	public void setGameLogic(GameLogic gameLogic) {
		this.gameLogic = gameLogic;
		this.status = gameLogic.getStatus();
		this.soundMan = gameLogic.getSoundMan();
	}

	/**
	 * Sets the label that displays the value for asteroids destroyed.
	 * @param destroyedValueLabel the label to set
	 */
	public void setDestroyedValueLabel(JLabel destroyedValueLabel) {
		this.destroyedValueLabel = destroyedValueLabel;
	}
	
	/**
	 * Sets the label that displays the value for ship (lives) left
	 * @param shipsValueLabel the label to set
	 */
	public void setShipsValueLabel(JLabel shipsValueLabel) {
		this.shipsValueLabel = shipsValueLabel;
	}
	public void setPointsValueLabel(JLabel pointsValueLabel) {
		// TODO Auto-generated method stub
		this.pointsValueLabel = pointsValueLabel;
	}
	public void enemyAttack(EnemyShip enemy)
	{
		
				gameLogic.fireEnemyBullet();
		
	}
}
