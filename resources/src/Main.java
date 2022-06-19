package de.cyclonit.mixinawareness.test;

public class Main {

    public static void main(String[] args) {

        /*
         * method: visitVariable
         * node: JCVariableDecl
         *   vartype:
         *   init:
         */
        MCBlockPos mcBlockPos = returnBlockPos();

        /*
         * method: visitVariable
         * node: JCVariableDecl
         *   vartype:
         *   init:
         */
        MCBlockPos mcBlockPos2 = new BlockPos(1, 2, 3);

        /*
         * method: visitVariable
         * node: JCVariableDecl
         *   vartype:
         *   init:
         */
        MCBlockPos mcBlockPos3 = blockPos2.clone();


        /*
         * method: visitExpressionStatement
         * node: JCExpression
         *   expr: JCAssign
         *
         */
        mcBlockPos = returnBlockPos();

        /*
         *
         *   vartype:
         *   init:
         */
        mcBlockPos = new BlockPos(1, 2, 3);

        /*
         *
         *
         */
        mcBlockPos = mcBlockPos.clone();


        Block block = new Block(new BlockPos(1, 2, 3));


        block.pos = mcBlockPos;


        acceptBlockPos(new BlockPos(1, 2, 3));
    }


    private static void acceptBlockPos(MCBlockPos blockPos) {
    }

    private static BlockPos returnBlockPos() {
        return new BlockPos(7, 8, 9);
    }
}