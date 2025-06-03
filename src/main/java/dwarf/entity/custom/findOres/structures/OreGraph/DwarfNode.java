package dwarf.entity.custom.findOres.structures.OreGraph;

import java.util.ArrayList;

public class DwarfNode {
    // NOTE: members are public for performance implications of direct access (less function calls)
    // coordinates (relative to 3d array index, not world coords)
    public int X;
    public int Y;
    public int Z;
    // block type
    public int type;
    // visited flag
    public boolean visited;
    // f(n) and g(n) scores
    public int fScore;
    public int gScore;
    // parent node (for path reconstruction)
    public DwarfNode parent;
    // neighbor list
    public ArrayList<DwarfNode> neighbors;

    // Variable used to increase the "cost" a block has
    // If a certain block is undesirable to pathfind through, this can be used to
    // make it pathfind around it
    public int extraCost;

    // node constructor
    public DwarfNode(int x, int y, int z, int type) {
        this.X = x;
        this.Y = y;
        this.Z = z;
        this.type = type;
        this.visited = false;
        this.fScore = Integer.MAX_VALUE;
        this.gScore = Integer.MAX_VALUE;
        this.parent = null;
        this.neighbors = new ArrayList<>();
        this.extraCost = 0;
    }

    // returns the manhattan distance between two DwarfNodes
    public static int manhattanDist(DwarfNode a, DwarfNode b) {
        return Math.abs(a.X - b.X) + Math.abs(a.Y - b.Y) + Math.abs(a.Z - b.Z);
    }

    // returns true if two DwarfNodes are equal (coordinates are equal)
    public static boolean isEqual(DwarfNode a, DwarfNode b) {
        return (a.X == b.X && a.Y == b.Y && a.Z == b.Z);
    }

    // prints node coordinates
    public void printNode() {
        System.out.println("(" + this.X + ", " + this.Y + ", " + this.Z + ")");
    }

    // fScore getter (for priority queue comparator)
    public int getfScore() {
        return fScore;
    }

}
