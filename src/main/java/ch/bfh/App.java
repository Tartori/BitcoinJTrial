package ch.bfh;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Context;
import org.bitcoinj.utils.BlockFileLoader;

import java.io.File;
import java.util.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        App a = new App();
        a.trial();
    }

    private void trial(){
        List<File> files = getFiles();
        AbstractBitcoinNetParams params = new MainNetParams();
        Context.getOrCreate(params);
        Date time = new Date();
        int i = 0;
        try {
            for (File f : files) {
                System.out.println(f.getName());
                BlockFileLoader fl = new BlockFileLoader(params, Collections.singletonList(f));
                for (Block blk : fl) {
                    i++;
                    time=blk.getTime();
                }
                System.out.println(i);
                System.out.println(time);

            }
        }
        catch (Exception ex){
            System.out.println(i);
            System.out.println(time);
            System.out.println(ex.toString());
            ex.printStackTrace();
        }
    }

    private List<File> getFiles(){
        List<File>files = new ArrayList<>();
        try {
            File folder = new File("D:\\btc151\\data\\blocks");
            File[] listOfFiles = folder.listFiles();
            if(listOfFiles==null)return files;

            for (File file : listOfFiles) {
                if (file.isFile()&&file.getName().startsWith("blk")) {
                    files.add(file);
                }
            }
        }
        catch (Exception e)
        {}
        return files;
    }
}
