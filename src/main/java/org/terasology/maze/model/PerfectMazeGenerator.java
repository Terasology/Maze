// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.maze.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Generator for perfect mazes. In a perfect maze, from each position in the maze there is always a path to each other
 * position.
 *
 * @author synopia
 */
public class PerfectMazeGenerator implements MazeGenerator {
    private final int[][] maze;
    private final int width;
    private final int height;
    private final Random random;

    public PerfectMazeGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        maze = new int[width][height];
        random = new Random();
    }

    private static boolean between(int v, int upper) {
        return (v >= 0) && (v < upper);
    }

    @Override
    public int[][] generateMaze() {
        generateMaze(0, 0);
        return maze;
    }

    private void generateMaze(int cx, int cy) {
        Direction[] dirs = Direction.values();
        Collections.shuffle(Arrays.asList(dirs), random);
        for (Direction dir : dirs) {
            int nx = cx + dir.dx;
            int ny = cy + dir.dy;
            if (between(nx, width) && between(ny, height) && (maze[nx][ny] == 0)) {
                maze[cx][cy] |= dir.bit;
                maze[nx][ny] |= dir.opposite.bit;
                generateMaze(nx, ny);
            }
        }
    }

}
