import util.SpicaWrapper;

public class Main {

    public static void main(String[] args) {
        SpicaWrapper spica = new SpicaWrapper("192.168.10.101",10000);


        //////////////////この下にプログラムを書こう！/////////////////////
        spica.front(1000,100);
        spica.back(1000,80);

        // しきい値 int型, 以上以下 String型
        //spica.ifStartBlock(20,"up");
        spica.left(1000,100);
        //spica.elseBlock();
        spica.right(1000,100);
        //spica.ifEndBlock();
        //spica.back(1500,100);


        ///////////////////////////ここまで！///////////////////////////

        spica.run();
    }
}
