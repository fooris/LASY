package lstatcore;

class Test {

    public static void main(String[] args) throws Exception {
        // "/home/fooris/Documents/repos/LSTAT/test.wav"
        String fileName = args[0];


        Stat stat = StatGen.getStats(args[0], Language.DE,15,15);




    }



}
