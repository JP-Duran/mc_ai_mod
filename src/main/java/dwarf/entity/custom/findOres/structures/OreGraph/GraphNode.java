package dwarf.entity.custom.findOres.structures.OreGraph;

public class GraphNode {
    // NOTE: members are public for performance implications of direct access (less function calls)
    // corresponding dwarf node (ore location)
    public DwarfNode ore;
    // visited flag
    public boolean visited;

    // node constructor
    public GraphNode(DwarfNode correspondingOre) {
        this.ore = correspondingOre;
        this.visited = false;
    }

}
