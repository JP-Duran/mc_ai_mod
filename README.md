# Cal Poly SLO CSC 480 Spring 2025 Minecraft Mob Project
### Professor: *Dr. Rodrigo Canaan*
### Students: *Julian Duran, Colby Watts, Katelyn Hallinan, Jackson Nelson, Grant Robinson*
### Credits: *Fabric Minecraft modding API, BlockBench*
### *Based Code modified  github repo: https://github.com/Tutorials-By-Kaupenjoe/Fabric-Tutorial-1.21.X*

### Setup

#### For setting up this repository you will need:  
- Minecraft Java Edition 1.21.5
- Fabric 0.16.13
- Fabric API installed in minecraft  
  - [Fabric](https://fabricmc.net/use/installer/)
- IntelliJ IDEA Community Edition with Minecraft Mod Development plugin
  - [instructions for settings up IntelliJ for minecraft mod development](https://docs.fabricmc.net/develop/getting-started/setting-up-a-development-environment)

Import the project into IntelliJ, let Gradle sync, and download any dependencies that you need. You should then be able to click the 'Minecraft Client' Run button in the top right to get the mod working (see notes).  

**NOTE**: I had to switch the build and run tool from Gradle to the IntelliJ IDEA tools to get this to work. Otherwise it would get stuck in an infinite cycle of missing imports. To do this, go to Settings->Build Tools->Gradle and set the "Build and run using" to IntelliJ IDEA  

**NOTE**: Make sure you do all the quick fix class-path imports in IntelliJ IDEA if you're running into missing package errors -- it seems like this is a required step  

### Usage Instructions

#### Commands
`/dwarf nearestneighbor` - set the TSP algorithm to nearest neighbor  
`/dwarf 2op` - set the TSP algorithm to 2-opt  
`/dwarf greedyfloodfill` - set the pathfinding algorithm to Greedy Flood Fill (does not use TSP -- only paths to nearest ore)
`/dwarf evaluate <iterations>` - test the pathing lengths of the different algorithms the input number of times

**NOTE:** The dwarf will use a custom 3-dimensional A* pathfinding algorithm regardless of the TSP algorithm that is selected

#### Spawning
The dwarf can be spawned from a spawn egg. In creative mode, the required item will appear in the 'Spawn Eggs' section of
the item search. The player then simply uses the spawn egg and the dwarf will appear. If there are any diamond ores present within
`scanRadius` blocks of the dwarf (defined in src/main/java/dwarf/entity/custom/findOres/FindOre.java) the pathfinding algorithm will run.    

**Order of operations**  
1. The environment around the dwarf will be scanned into a 3d-array of 'DwarfNodes' with each node representing a block
2. The diamond ores will be identified in this array and a complete weighted graph will be created with nodes representing the ores and edge
weights representing manhattan distances between the pair of ores
3. The selected TSP algorithm will run to determine the shortest-distance tour to visit all the ores
4. A* pathfinding will be run between each pair of ores in the sequence to create one path that visits all ores
5. The dwarf will start action, moving along the determined path mining each ore in sequence. Torches will be placed by the dwarf to light the path
6. Once all ores are collected, the dwarf will stop. If more ore are placed down within its radius, it will begin the process again

**NOTE**: If more than one dwarf is present in a given minecraft world they will not be functional  
**NOTE**: The dwarf is able to place blocks and mine blocks as part of its pathfinding. It has been manually tuned to display 'desired' behavior
(such that it does not overuse bridging/towering as part of its pathing strategy)
