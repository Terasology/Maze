// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.maze.model;

/**
 * Interface for maze generators. A maze is a 2d array of integers, where each bit==1 is a wall. Use the Direction enum
 * for the bit manipulations.
 *
 * @author synopia
 */
public interface MazeGenerator {
    int[][] generateMaze();

    enum Direction {
        N(1, 0, -1), S(2, 0, 1), E(4, 1, 0), W(8, -1, 0);
        // use the static initializer to resolve forward references
        static {
            N.opposite = S;
            S.opposite = N;
            E.opposite = W;
            W.opposite = E;
        }

        public final int bit;
        public final int dx;
        public final int dy;
        public Direction opposite;

        Direction(int bit, int dx, int dy) {
            this.bit = bit;
            this.dx = dx;
            this.dy = dy;
        }
    }
}
