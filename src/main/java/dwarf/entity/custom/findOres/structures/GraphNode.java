package dwarf.entity.custom.findOres.structures;

import java.util.ArrayList;

public class GraphNode {
    // corresponding dwarf node (ore location)
    public DwarfNode ore;

    // node constructor
    public GraphNode(DwarfNode correspondingOre) {
        this.ore = correspondingOre;
    }

}
