package rbadia.voidspace.main;

import java.awt.Rectangle;
import java.util.List;

import rbadia.voidspace.model.Asteroid;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.EnemyShip;
import rbadia.voidspace.model.Ship;

public class CollisionDetection
{
	
	public CollisionDetection()
	{
		//Do something
	}
	
	public boolean astrBulletCollision(List<Asteroid> asteroids, List<Bullet> bullets)
	{
		boolean isColliding = false; 
		
		for(Bullet bull: bullets)
		{
			for(Asteroid astr: asteroids)
			{
				if(astr.intersects(bull))
				{
					isColliding = true;
				}
			}
		}
		return isColliding;
		
	}
	
	public boolean astrShipCollision(List<Asteroid> asteroids, Ship ship)
	{
			boolean isColliding = false;
			
			for(Asteroid astr: asteroids)
			{
				if(astr.intersects(ship))
				{
					isColliding = true;
				}
			}
		
		return isColliding;
	}
	
	public boolean enemyBulletCollision(EnemyShip enemy, List<Bullet> bullets)
	{
		boolean isColliding = false;
		
		for(Bullet bull: bullets)
		{
			
				if(enemy.intersects(bull))
				{
					isColliding = true;
					
				}
		
		}
		return isColliding;
	}
	
	public boolean enemyShipCollision(EnemyShip enemy, Ship ship)
	{
		boolean isColliding = false;
		
		if(enemy.intersects(ship))
			isColliding = true;
		
		return isColliding;
	}
}
