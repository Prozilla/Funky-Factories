package source.buildable.building;

import source.buildable.Building;
import source.item.Item;
import source.item.ItemManager;
import source.main.GamePanel;
import source.tile.Tile;
import source.tile.TileManager;

public class Exporter extends Building {

	public Exporter(int x, int y, int rotation, Tile tile, GamePanel gamePanel, TileManager tileManager, ItemManager itemManager) {
		super(x, y, tile, gamePanel, tileManager, itemManager);
		this.rotation = rotation;
		this.output = -2;
		this.addConveyor(this.rotation);
	}

	@Override
	public void processItem(Item item) {
		itemManager.items.remove(item);
		gamePanel.score++;
	}
	
}