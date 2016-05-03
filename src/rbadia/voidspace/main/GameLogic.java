package rbadia.voidspace.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.Timer;

import rbadia.voidspace.model.Asteroid;
import rbadia.voidspace.model.BigAsteroid;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.EnemyShip;
import rbadia.voidspace.model.Ship;
import rbadia.voidspace.sounds.SoundManager;


/**
 * Handles general game logic and status.
 */
public class GameLogic
{
	private Random rand;
	private GameScreen gameScreen;
	private GameStatus status;
	
	
	private SoundManager soundMan;
	
	private EnemyShip enemyShip;
	private Ship ship;
	private Asteroid asteroid;
	private BigAsteroid bigAsteroid;
	
	private List<Asteroid> asteroids;
	private List<Bullet> bullets;
	private List<Bullet> enemyBullets;
	private List<EnemyShip> enemies;
	
	public boolean yes;
	/**
	 * Craete a new game logic handler
	 * @param gameScreen the game screen
	 */
	public GameLogic(GameScreen gameScreen)
	{
		this.gameScreen = gameScreen;
		
		// initialize game status information
		status = new GameStatus();
		// initialize the sound manager
		soundMan = new SoundManager();
		
		// init some variables
		asteroid = new Asteroid(gameScreen);
		asteroids = new ArrayList<Asteroid>();
		bullets = new ArrayList<Bullet>();
		enemyBullets = new ArrayList<Bullet>();
		enemies = new ArrayList<EnemyShip>();
	}
	public GameLogic(GameScreen1 gameScreen)
	{
		this.gameScreen = gameScreen;
		
		// initialize game status information
		status = new GameStatus();
		// initialize the sound manager
		soundMan = new SoundManager();
		
		// init some variables
		asteroid = new Asteroid(gameScreen);
		asteroids = new ArrayList<Asteroid>();
		bullets = new ArrayList<Bullet>();
		enemyBullets = new ArrayList<Bullet>();
		enemies = new ArrayList<EnemyShip>();
	}
	/**
	 * Returns the game status
	 * @return the game status 
	 */
	public GameStatus getStatus() 
	{
		return status;
	}

	public SoundManager getSoundMan() 
	{
		return soundMan;
	}

	public GameScreen getGameScreen()
	{
		return gameScreen;
	}

	/**
	 * Prepare for a new game.
	 */
	public void newGame()
	{
		status.setGameStarting(true);
		
		// init game variables
		asteroids = new ArrayList<Asteroid>();
		bullets = new ArrayList<Bullet>();
		enemyBullets = new ArrayList<Bullet>();
		enemies = new ArrayList<EnemyShip>();

		
		
		status.setShipsLeft(3);
		status.setGameOver(false);
		status.setAsteroidsDestroyed(0);
		status.setNewAsteroid(false);
		status.setPoints(0);
				
		// init the ship and the asteroid
        newShip(gameScreen);
        

//        newEnemy(gameScreen);
        createRandomAsteroids(gameScreen, 5);

        
        // prepare game screen
        gameScreen.doNewGame();
        
        // delay to display "Get Ready" message for 1.5 seconds
		Timer timer = new Timer(1500, new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				status.setGameStarting(false);
				status.setGameStarted(true);
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
	
	/**
	 * Check game or level ending conditions.
	 */
	public void checkConditions()
	{
		// check game over conditions
		if(!status.isGameOver() && status.isGameStarted())
		{
			
			if(status.getShipsLeft() <= 0)
			{
				gameOver();
			}
			if(status.getAsteroidsDestroyed() >= 5)
			{
				//gameOver();
				gameLevel2(ship, enemyShip, bullets, bullets, asteroids);
			}
			
		}
	}
	
	public void gameLevel2(Ship ship, EnemyShip enemy, List<Bullet> bullets, List<Bullet> enemyBullets, List<Asteroid> asteroids) 
	{	
		gameScreen.removeAll();
		
		gameScreen = new GameScreen1();
		
//		status.setGameStarted(false);
//		status.setGameStarting(true);
		//gameScreen.doNewGame();
		
		//gameScreen.updateScreen1();
		//gameScreen.update(null);
		gameScreen.repaint();
		
		//status.setgameLevel2(true);
		
		
		// init game variables
//		asteroids = new ();
		bullets = new ArrayList<Bullet>();
		enemyBullets = new ArrayList<Bullet>();
		enemies = new ArrayList<EnemyShip>();
//		
//		newShip(gameScreen);
//		newAsteroid(gameScreen);
		newEnemy(gameScreen);
		
		//newAsteroid(gameScreen);
		//createRandomAsteroids(gameScreen, 5);
      
		// prepare game screen
		gameScreen.dogameLevel2();
		

		// delay to display "Get Ready" message for 1.5 seconds
		Timer timer = new Timer(2000, new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				status.setGameStarting(false);
				status.setGameStarted(true);
			}
		});
		timer.setRepeats(false);
		timer.start();
		
		
	}

	/**
	 * Actions to take when the game is over.
	 */
	public void gameOver()
	{
		status.setGameStarted(false);
		status.setGameOver(true);
		gameScreen.doGameOver();
		
        // delay to display "Game Over" message for 3 seconds
		Timer timer = new Timer(3000, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				status.setGameOver(false);
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
	
	/**
	 * Fire a bullet from ship.
	 */
	public void fireBullet()
	{

			Bullet bullet = new Bullet(ship);
			bullets.add(bullet);

		soundMan.playBulletSound();
	}
	
	public void fireEnemyBullet()
	{
		Bullet bullet = new Bullet(enemyShip);
		enemyBullets.add(bullet);
		
		soundMan.playBulletSound();
		
	}
	

	/**
	 * Move a bullet once fired.
	 * @param bullet the bullet to move
	 * @return if the bullet should be removed from screen
	 */
	public boolean moveBullet(Bullet bullet)
	{
		if(bullet.getY() - bullet.getSpeed() >= 0)
		{
			bullet.translate(0, -bullet.getSpeed());
			
			//returns false if the bullet is still on screen
			return false;
		}
		else
		{
			//returns true if the bullet is off screen
			return true;
		}
	}
	
	public boolean moveEnemyBullet(Bullet bullet)
	{
		
		if(bullet.getY() + bullet.getSpeed() <= this.gameScreen.getHeight())
		{
			bullet.translate(0, bullet.getSpeed());
			
			return false;
		}
		else
		{
			return true;
		}
	}
	
	/**
	 * Create a new ship (and replace current one).
	 */
	public Ship newShip(GameScreen screen)
	{
		this.ship = new Ship(screen);
		return ship;
	}
	
	public EnemyShip newEnemy(GameScreen screen)
	{
		this.enemyShip = new EnemyShip(screen);
		return enemyShip;
	}
	
	/**
	 * Create a new asteroid.
	 */
	public Asteroid newAsteroid(GameScreen screen)
	{
		this.asteroid = new Asteroid(screen);
		return asteroid;
	}
	
	public BigAsteroid newBigAsteroid(GameScreen screen)
	{
		this.bigAsteroid = new BigAsteroid(screen);
		return bigAsteroid;
	}
	
	public void createRandomAsteroids(GameScreen screen , int n)
	{
		/*
		 	Here we just generate a random integer,
			that will be used to generate the asteroids.
		*/
		for(int i = 0; i< n ; i++)
		{
			asteroids.add(new Asteroid(screen));
		}
	}
	/* 
	 * A test for gameScreen1 test
	 */
	
	
	/**
	 * Create a new ship (and replace current one).
	 */
	public Ship newShip(GameScreen1 screen)
	{
		this.ship = new Ship(screen);
		return ship;
	}
	
	public EnemyShip newEnemy(GameScreen1 screen)
	{
		this.enemyShip = new EnemyShip(screen);
		return enemyShip;
	}
	
	/**
	 * Create a new asteroid.
	 */
	public Asteroid newAsteroid(GameScreen1 screen)
	{
		this.asteroid = new Asteroid(screen);
		return asteroid;
	}
	
	public BigAsteroid newBigAsteroid(GameScreen1 screen)
	{
		this.bigAsteroid = new BigAsteroid(screen);
		return bigAsteroid;
	}

	/*
	 * We want to create multiple asteroids
	 */
	
	public void createRandomAsteroids(GameScreen1 screen , int n)
	{
		/*
		 	Here we just generate a random integer,
			that will be used to generate the asteroids.
		*/
		for(int i = 0; i< n ; i++)
		{
			asteroids.add(new Asteroid(screen));
		}
	}
	
	/**
	 * Returns the ship.
	 * @return the ship
	 */
	public Ship getShip() 
	{
		return ship;
	}
	public EnemyShip getEnemy()
	{
		return enemyShip;
	}
	/**
	 * Returns the asteroid.
	 * @return the asteroid
	 */
	public Asteroid getAsteroid()
	{
		return asteroid;
	}
	
	public BigAsteroid getBigAsteroid()
	{
		return bigAsteroid;
	}

	/**
	 * Returns the list of bullets.
	 * @return the list of bullets
	 */
	public List<Bullet> getBullets()
	{
		return bullets;
	}
	
	public List<Bullet> getEnemyBullets()
	{
		return enemyBullets;
	}
	
	public List<Asteroid> getAsteroids()
	{
		return asteroids;
	}
	
	public List<EnemyShip> getEnemies()
	{
		return enemies;
	}
}
