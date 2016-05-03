package rbadia.voidspace.model;

import java.awt.Rectangle;
import java.util.Random;

import rbadia.voidspace.main.GameScreen;

public class EnemyShip extends Rectangle
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6981757460360023887L;
	
	private Random rand = new Random();
	
	//Our Custom Off-Set for the enemy ships to appear off-screen
	private static final int Y_OFFSET = 5;
	private static final int DEFAULT_SPEED = 1;
	
	private int shipWidth = 16;
	private int shipHeight = 16;
	private int speed = DEFAULT_SPEED;
	
	public EnemyShip(GameScreen screen)
	{
		/*
		 * Add gameScreen arg to super constructor
		 */
		
		this.setLocation(10, 10);
		this.setSize(shipWidth, shipHeight);
	}
	
	/*
	 * We utilize a Random Number Generator 
	 * To generate a unique movement pattern
	 * 
	 * For now the method has a hard-coded max of 100
	 */
	protected void setEnemyMovement()
	{
		switch(rand.nextInt(100))
		{
			case 0:
				break;
				
			default:
				break;
		}
	}
	/*
	 * Queries for the enemy's location
	 * So that another method may use this knowledge
	 */
	public int getEnemyLocation(Ship s)
	{
		s.getX();
		s.getY();
		
		return 0;
	}
	
	public int getEnemyHeight()
	{
		return shipHeight;
	}
	
	public int getEnemySpeed() 
	{
		return speed;
	}
	
	public void setEnemySpeed(int speed)
	{
		this.speed = speed;
	}
	
	public int getEnemyDefaultSpeed()
	{
		return DEFAULT_SPEED;
	}
}
