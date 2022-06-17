package de.cyclonit.mixinawareness.test;

public class Main {

    public static void main(String[] args) {

        /*
         * Expected Transform:
         *   MCBlockPos blockPos = ((MCBlockPos) new BlockPos(1, 2, 3));
         */
        MCBlockPos blockPos = new BlockPos(1, 2, 3);

        /*
         * Expected Transform:
         *   acceptBlockPos((MCBlockPos) blockPos);
         */
        acceptBlockPos(blockPos);

        /*
         * Expected Transform:
         *   ((MCBlockPos) blockPos).asCubePos();
         */
        blockPos.asCubePos();

        /*
         * Transformation Steps
         *
         * 1. identify which classes are being mixed into and the respective interfaces
         *
         * 2.
         */
    }


    private static void acceptBlockPos(MCBlockPos blockPos) {
    }
}