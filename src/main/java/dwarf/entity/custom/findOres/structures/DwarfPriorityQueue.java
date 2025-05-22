package dwarf.entity.custom.findOres.structures;

import java.util.Comparator;
import java.util.PriorityQueue;

public class DwarfPriorityQueue extends PriorityQueue<DwarfNode> {

    // constructor with custom comparison function (sorts by lowest f score)
    public DwarfPriorityQueue() {
        super(Comparator.comparingInt(DwarfNode::getfScore));
    }

}
