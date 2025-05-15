package dwarf.entity.custom.findOres.structures;

import java.util.PriorityQueue;

public class DwarfPriorityQueue extends PriorityQueue {

    // returns the manhattan distance between two DwarfNodes
    public int manhattanDist(DwarfNode a, DwarfNode b) {
        return Math.abs(a.X - b.X) + Math.abs(a.Y - b.Y) + Math.abs(a.Z - b.Z);
    }


}
