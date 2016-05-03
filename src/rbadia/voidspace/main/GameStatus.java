package rbadia.voidspace.main;

/**
 * Container for game flags and/or status variables.
 */
public class GameStatus {
	// game flags
	private boolean gameStarted = false;
	private boolean gameStarting = false;
	private boolean gameLevel2 = false;
	private boolean gameOver = false;
	
	// status variables
	private boolean newShip;
	private boolean newEnemy;
	private boolean newAsteroid;
	private boolean newBigAsteroid;
	private long asteroidsDestroyed = 0;
	private int pointsGained = 0;
	private int shipsLeft;
	
	public GameStatus(){
		
	}
	
	/**
	 * Indicates if the game has already started or not.
	 * @return if the game has already started or not
	 */
	public synchronized boolean isGameStarted() {
		return gameStarted;
	}
	
	public synchronized void setGameStarted(boolean gameStarted) {
		this.gameStarted = gameStarted;
	}
	
	/**
	 * Indicates if the game is starting ("Get Ready" message is displaying) or not.
	 * @return if the game is starting or not.
	 */
	public synchronized boolean isGameStarting() {
		return gameStarting;
	}
	
	public synchronized void setGameStarting(boolean gameStarting) {
		this.gameStarting = gameStarting;
	}
	
	//Stuff
	
	public synchronized boolean isGameLevel2() {
		return gameLevel2;
	}
	
	public synchronized void setGameLevel2(boolean gameLevel2) {
		this.gameLevel2 = gameLevel2;
	}
	

	
	/**
	 * Indicates if the game has ended and the "Game Over" message is displaying.
	 * @return if the game has ended and the "Game Over" message is displaying.
	 */
	public synchronized boolean isGameOver() {
		return gameOver;
	}
	
	public synchronized void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}
	
	/**
	 * Indicates if a new ship should be created/drawn.
	 * @return if a new ship should be created/drawn
	 */
	public synchronized boolean isNewShip() {
		return newShip;
	}

	public synchronized void setNewShip(boolean newShip) {
		this.newShip = newShip;
	}

	/**
	 * Indicates if a new asteroid should be created/drawn.
	 * @return if a new asteroid should be created/drawn
	 */
	public synchronized boolean isNewAsteroid() 
	{
		return newAsteroid;
	}

	public synchronized void setNewAsteroid(boolean newAsteroid) {
		this.newAsteroid = newAsteroid;
	}
	
	public synchronized boolean isNewBigAsteroid()
	{
		return newBigAsteroid;
	}
	
	public synchronized void setNewBigAsteroid(boolean newBigAsteroid)
	{
		this.newBigAsteroid = newBigAsteroid;
	}
	
	/**
	 * Returns the number of asteroid destroyed. 
	 * @return the number of asteroid destroyed
	 */
	public synchronized long getAsteroidsDestroyed() {
		return asteroidsDestroyed;
	}

	public synchronized void setAsteroidsDestroyed(long asteroidsDestroyed) {
		this.asteroidsDestroyed = asteroidsDestroyed;
	}

	/**
	 * Returns the number ships/lives left.
	 * @return the number ships left
	 */
	public synchronized int getShipsLeft() {
		return shipsLeft;
	}

	public synchronized void setShipsLeft(int shipsLeft) {
		this.shipsLeft = shipsLeft;
	}

	public synchronized boolean isNewEnemy() {
		
		return newEnemy;
	}

	public synchronized void setNewEnemy(boolean b) {
		// TODO Auto-generated method stub
		this.newEnemy = b; 
	}
	
	/**
	 * Returns the number of points. 
	 * @return the number of points
	 */

	public synchronized int getPoints() 
	{
		return pointsGained;
	}

	public synchronized void setPoints(int pointsGained) {
		if(this.pointsGained + pointsGained >= 9999)
		{
			this.pointsGained += 0;
		}
		this.pointsGained = pointsGained;
	}


}
