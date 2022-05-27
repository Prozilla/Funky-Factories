package source.item;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;

import source.buildable.Buildable;
import source.buildable.Building;
import source.buildable.connectable.Conveyor;
import source.main.GamePanel;
import source.tile.TileManager;
import source.main.UI;
import source.main.Viewport;

public class Item {

	public final static int pixelSize = GamePanel.originalTileSize / 2;
	public static int size = (int)(pixelSize * GamePanel.itemScaleMultiplier);

	public String name;
	
	public int x = 0;
	public int y = 0;
	public Point offset = new Point(0, 0);
	public Point coordinate = new Point(0, 0);

	BufferedImage sprite;

	GamePanel gamePanel;
	TileManager tileManager;
	Viewport viewport;

	public Item(int x, int y, String name, BufferedImage sprite, GamePanel gamePanel, TileManager tileManager, Viewport viewport) {
		this.x = x;
		this.y = y;
		this.name = name;
		this.coordinate = TileManager.positionToCoordinate(new Point(x, y));
		this.sprite = sprite;
		this.gamePanel = gamePanel;
		this.tileManager = tileManager;
		this.viewport = viewport;
	}

	public void move(Graphics2D graphics2D) {
		Buildable buildable = tileManager.coordinateToBuildable.get(coordinate);
		Conveyor conveyor = (buildable != null && buildable instanceof Conveyor) ? (Conveyor)buildable : null;
		boolean buildingConveyor = false;

		if (conveyor == null && buildable != null && buildable.buildingConveyor != null) {
			conveyor = buildable.buildingConveyor;
			buildingConveyor = true;
		}

		if (conveyor != null) {
			int direction = !buildingConveyor ? conveyor.input > -1 ? (conveyor.input + 2) % 4 : conveyor.output : (conveyor.rotation / 90 + 1) % 4;
			Point offset = TileManager.moveInDirection(direction, GamePanel.tileSize / 2 + Item.size / 2);

			switch (direction) {
				case 0:
					offset.y += Item.size / 2;
					break;
				case 1:
					offset.x -= Item.size / 2;
					break;
				case 2:
					offset.y -= Item.size / 2;
					break;
				case 3:
					offset.x += Item.size / 2;
					break;
			}

			Point point = new Point(x + offset.x, y + offset.y);
			Point offsetCoordinate = TileManager.positionToCoordinate(point);

			// Get previous conveyor coordinate
			Point previousCoordinateOffset = TileManager.moveInDirection(direction, 1);
			Point previousCoordinate = new Point(coordinate.x + previousCoordinateOffset.x / 2, coordinate.y + previousCoordinateOffset.y / 2);

			// if (buildingConveyor) {
			// 	direction = (conveyor.rotation / 90 + 3) % 4;
			// }

			if (!offsetCoordinate.equals(previousCoordinate)) {
				if (!buildingConveyor && conveyor.output != -1) {
					direction = conveyor.output;
					graphics2D.setColor(Color.yellow);
				} else if (buildable instanceof Building) {
					// Only process item if it has passed the center of the building
					Point nextPositionOffset = TileManager.moveInDirection(direction, GamePanel.tileSize / 2 + Item.size / 2);
					Point nextPosition = new Point(x + nextPositionOffset.x, y + nextPositionOffset.y);

					Point nextCoordinate = TileManager.positionToCoordinate(nextPosition);

					if (!nextCoordinate.equals(coordinate)) {
						((Building)buildable).processItem(this);
						graphics2D.setColor(Color.green);
					}
				}
			} else {
				graphics2D.setColor(Color.cyan);
			}

			if (UI.showItemOrientation) {
				graphics2D.fillRect(point.x, point.y, (int)GamePanel.tileScaleMultiplier, (int)GamePanel.tileScaleMultiplier);
				graphics2D.setColor(Color.white);
				graphics2D.fillRect(x, y, (int)GamePanel.tileScaleMultiplier, (int)GamePanel.tileScaleMultiplier);
			}


			Point movement = TileManager.moveInDirection(direction, (int)Math.round(gamePanel.deltaTime * Conveyor.speed));

			x += movement.x;
			y += movement.y;
			coordinate = TileManager.positionToCoordinate(new Point(x, y));
		}
	}

	public void draw(Graphics2D graphics2D) {
		viewport.drawSprite(graphics2D, sprite, (int)((x + offset.x - size / 2)), (int)((y + offset.y - size / 2)), size, size, 1, true);
	}

}
