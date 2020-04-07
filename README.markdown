# Terasology Maze module

This little module allows you to build mazes of any size.

The player is given a special item to designate the area, where the maze should be placed. It must have a size of 6x6x1 blocks at least.
Having multiple block in height (y) will build 2 blocks maze and 1 block floor for the next maze level. No ladders or stairs are build into
the maze yet.

## Contribution
1. Run `groovyw module recurse Maze` command while in the Terasology root folder to fetch and store the module locally. Then fork the module and add a remote reference using the command `git remote add <remote-name> <your fork>` in the `/module/Maze` directory.  
2. To **create a maze**, create a prefab similar to the ones [here]https://github.com/Terasology/Maze/tree/master/assets/prefabs).
