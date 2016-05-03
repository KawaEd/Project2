package rbadia.voidspace.model;

import rbadia.voidspace.main.GameScreen;

public class BigAsteroid extends Asteroid 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1635254453946108874L;

	private static int DEFAULT_SPEED = 2;
	
	private int width = 36;
	private int height = 36;
	private int speed = DEFAULT_SPEED; 
	
	public BigAsteroid (GameScreen screen)
	{
		//Add GameScreen Argument
		super(screen);
		this.setSize(width, height);
		this.setSpeed(speed);
	}
	
	@Override
	public double getX()
	{
		return this.x;
	}
	
	@Override
	public double getY()
	{
		return this.y;
	}
	

}
