package dwarf.entity.custom.findOres.structures;

public class DwarfNode {
    // coordinates (relative to 3d array index, not world coords)
    public int X;
    public int Y;
    public int Z;
    // visited flag
    private boolean visited;
    // f(n) and g(n) scores
    private int fscore;
    private int gscore;
    // parent node (for path reconstruction)
    DwarfNode parent;

    // node constructor
    public DwarfNode(int x, int y, int z) {
        this.X = x;
        this.Y = y;
        this.Z = z;
        this.visited = false;
        this.fscore = Integer.MAX_VALUE;
        this.gscore = Integer.MAX_VALUE;
        this.parent = null;
    }

    // getter functions
    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public int getZ() {
        return Z;
    }

    public boolean isVisited() {
        return visited;
    }

    public int getFscore() {
        return fscore;
    }

    public int getGscore() {
        return gscore;
    }

    public DwarfNode getParent() {
        return parent;
    }

    // setter functions
    public void setX(int x) {
        this.X = x;
    }

    public void setY(int y) {
        this.Y = y;
    }

    public void setZ(int z) {
        this.Z = z;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setFscore(int fscore) {
        this.fscore = fscore;
    }

    public void setGscore(int gscore) {
        this.gscore = gscore;
    }

    public void setParent(DwarfNode parent) {
        this.parent = parent;
    }
}
