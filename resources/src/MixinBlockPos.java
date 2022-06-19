package de.cyclonit.mixinawareness.mixin;

import de.cyclonit.mixinawareness.test.Mixin;
import de.cyclonit.mixinawareness.test.MCBlockPos;
import de.cyclonit.mixinawareness.test.BlockPos;
import de.cyclonit.mixinawareness.test.CubePos;

@Mixin(BlockPos.class)
public class MixinBlockPos implements MCBlockPos {

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