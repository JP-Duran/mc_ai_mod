package dwarf.entity.custom.findOres.structures.OreGraph;

public class GraphNode {
    // corresponding dwarf node (ore location)
    public DwarfNode ore;

    // node constructor
    public GraphNode(DwarfNode correspondingOre) {
        this.ore = correspondingOre;
    }

}
