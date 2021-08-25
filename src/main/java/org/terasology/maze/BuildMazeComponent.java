// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.maze;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Attach this component and BlockSelection to entity items, to make them into maze builder items. When a selection
 * is finished using such an item, a maze will be constructed in the selection region.
 *
 * @author synopia
 */
public class BuildMazeComponent implements Component<BuildMazeComponent> {
    public String blockType;

    @Override
    public void copyFrom(BuildMazeComponent other) {
        this.blockType = other.blockType;
    }
}
