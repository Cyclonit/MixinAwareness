package de.cyclonit.mixinawareness.mixin;

import de.cyclonit.mixinawareness.test.BlockPos;

@Mixin(BlockPos.class)
public class MixinBlockPos implements de.cyclonit.mixinawareness.test.MCBlockPos {

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }


    public CubePos asCubePos() {
        return new CubePos(getX() >> 4, getY() >> 4, getZ() >> 4);
    }
}