package ch.bfh;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Context;
import org.bitcoinj.utils.BlockFileLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
        AbstractBitcoinNetParams params = new TestNet3Params();
        Context.getOrCreate(params);
        for(File f : files)
        {
            System.out.println(f.getName());
            List<String> hashes = new LinkedList<>();
            BlockFileLoader fl = new BlockFileLoader(params, Collections.singletonList(f));
            for (Block blk : fl) {
                hashes.add(blk.getHashAsString());
            }

        }
    }

    private List<File> getFiles(){
        List<File>files = new ArrayList<>();
        try {
            File folder = new File("D:\\btc\\data\\testnet3\\blocks");
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
