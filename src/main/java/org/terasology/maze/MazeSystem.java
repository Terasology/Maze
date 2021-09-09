/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.maze;

import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.engine.logic.selection.ApplyBlockSelectionEvent;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.BlockRegionc;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.maze.model.MazeGenerator;
import org.terasology.maze.model.PerfectMazeGenerator;
import org.terasology.module.inventory.components.InventoryComponent;
import org.terasology.module.inventory.events.GiveItemAction;
import org.terasology.module.inventory.systems.InventoryManager;

/**
 * @author synopia
 */
@RegisterSystem
public class MazeSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(MazeSystem.class);

    private Block solid;

    @In
    private WorldProvider worldProvider;
    @In
    private EntityManager entityManager;
    @In
    private InventoryManager inventoryManager;

    @ReceiveEvent
    public void onPlayerSpawn(OnPlayerSpawnedEvent event, EntityRef player, InventoryComponent inventory) {
        createAndGiveItemToPlayerIfPossible("Maze:buildMaze", player);
    }

    private void createAndGiveItemToPlayerIfPossible(String uri, EntityRef player) {
        EntityRef item = entityManager.create(uri);
        GiveItemAction action = new GiveItemAction(EntityRef.NULL, item);
        player.send(action);
        if (!action.isConsumed()) {
            logger.warn(uri + " could not be created and given to player.");
            item.destroy();
        }

    }

    @ReceiveEvent
    public void onSelection(ApplyBlockSelectionEvent event, EntityRef entity) {
        EntityRef itemEntity = event.getSelectedItemEntity();
        BuildMazeComponent mazeComponent = itemEntity.getComponent(BuildMazeComponent.class);
        if (mazeComponent == null) {
            return;
        }
        solid = CoreRegistry.get(BlockManager.class).getBlock(mazeComponent.blockType);
        BlockRegionc selection = event.getSelection();
        Vector3i size = selection.getSize(new Vector3i());
        Vector3i min = selection.getMin(new Vector3i());
        int mazeWidth = size.x / 3;
        int mazeHeight = size.z / 3;
        if (mazeWidth < 2 || mazeHeight < 2 || size.y < 1) {
            return;
        }
        int[][] maze = null;
        for (int y = 0; y < size.y; y++) {
            switch (y % 3) {
                case 0:
                    MazeGenerator generator = new PerfectMazeGenerator(mazeWidth, mazeHeight);
                    maze = generator.generateMaze();
                    // fall through
                case 1:
                    buildLevel(min.x, y + min.y, min.z, mazeWidth, mazeHeight, maze);
                    break;
                case 2:
                    buildRoof(min.x, y + min.y, min.z, mazeWidth, mazeHeight);
                    break;
            }
        }
    }

    private void buildRoof(int x, int y, int z, int w, int h) {
        for (int i = 0; i <= h * 3; i++) {
            for (int j = 0; j <= w * 3; j++) {
                setBlock(x + j, y, z + i);
            }
        }
    }

    private void buildLevel(int x, int y, int z, int w, int h, int[][] maze) {
        for (int i = 0; i < h; i++) {
            // draw the north edge
            for (int j = 0; j < w; j++) {
                if ((maze[j][i] & 1) == 0) {
                    setBlock(x + j * 3, y, z + i * 3);
                    setBlock(x + j * 3 + 1, y, z + i * 3);
                    setBlock(x + j * 3 + 2, y, z + i * 3);
                } else {
                    setBlock(x + j * 3, y, z + i * 3);
                }
            }
            // draw the west edge
            for (int j = 0; j < w; j++) {
                if ((maze[j][i] & 8) == 0) {
                    setBlock(x + j * 3, y, z + i * 3 + 1);
                    setBlock(x + j * 3, y, z + i * 3 + 2);
                }
            }
            setBlock(x + w * 3, y, z + i * 3);
            setBlock(x + w * 3, y, z + i * 3 + 1);
            setBlock(x + w * 3, y, z + i * 3 + 2);
        }
        // draw the bottom line
        for (int j = 0; j <= w * 3; j++) {
            setBlock(x + j, y, z + h * 3);
        }
    }

    private void setBlock(int x, int y, int z) {
        worldProvider.setBlock(new Vector3i(x, y, z), solid);
    }

    @Override
    public void initialise() {
    }

    @Override
    public void shutdown() {

    }
}
