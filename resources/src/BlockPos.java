package de.cyclonit.mixinawareness.test;

public class BlockPos {

    public static final BlockPos INSTANCE = new BlockPos(4, 5, 6);


    private int x;

    private int y;

    private int z;


    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public BlockPos clone() {
        return new BlockPos(x, y, z);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

}