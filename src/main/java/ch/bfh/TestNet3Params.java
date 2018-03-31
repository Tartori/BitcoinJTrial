//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ch.bfh;

import com.google.common.base.Preconditions;
import java.math.BigInteger;
import java.util.Date;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.Utils;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;

public class TestNet3Params extends AbstractBitcoinNetParams {
    private static TestNet3Params instance;
    private static final Date testnetDiffDate = new Date(1329264000000L);

    public TestNet3Params() {
        this.id = "org.bitcoin.test";
        this.packetMagic = 185665799L;
        this.interval = 2016;
        this.targetTimespan = 1209600;
        this.maxTarget = Utils.decodeCompactBits(486604799L);
        this.port = 18333;
        this.addressHeader = 111;
        this.p2shHeader = 196;
        this.acceptableAddressCodes = new int[]{this.addressHeader, this.p2shHeader};
        this.dumpedPrivateKeyHeader = 239;
        this.genesisBlock.setTime(1296688602L);
        this.genesisBlock.setDifficultyTarget(486604799L);
        this.genesisBlock.setNonce(414098458L);
        this.spendableCoinbaseDepth = 100;
        this.subsidyDecreaseBlockCount = 210000;
        String genesisHash = this.genesisBlock.getHashAsString();
        Preconditions.checkState(genesisHash.equals("000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943"));
        this.alertSigningKey = Utils.HEX.decode("04302390343f91cc401d56d68b123028bf52e5fca1939df127f63c6467cdf9c8e2c14b61104cf817d0b780da337893ecc4aaff1309e536162dabbdb45200ca2b0a");
        this.dnsSeeds = new String[]{"testnet-seed.bitcoin.jonasschnelli.ch", "testnet-seed.bluematt.me", "testnet-seed.bitcoin.petertodd.org", "testnet-seed.bitcoin.schildbach.de", "bitcoin-testnet.bloqseeds.net"};
        this.addrSeeds = null;
        this.bip32HeaderPub = 70617039;
        this.bip32HeaderPriv = 70615956;
        this.majorityEnforceBlockUpgrade = 51;
        this.majorityRejectBlockOutdated = 75;
        this.majorityWindow = 100;
    }

    public static synchronized TestNet3Params get() {
        if (instance == null) {
            instance = new TestNet3Params();
        }

        return instance;
    }

    public String getPaymentProtocolId() {
        return "test";
    }

    public void checkDifficultyTransitions(StoredBlock storedPrev, Block nextBlock, BlockStore blockStore) throws VerificationException, BlockStoreException {
        if (!this.isDifficultyTransitionPoint(storedPrev) && nextBlock.getTime().after(testnetDiffDate)) {
            Block prev = storedPrev.getHeader();
            long timeDelta = nextBlock.getTimeSeconds() - prev.getTimeSeconds();
            if (timeDelta >= 0L && timeDelta <= 1200L) {
                StoredBlock cursor;
                for(cursor = storedPrev; !cursor.getHeader().equals(this.getGenesisBlock()) && cursor.getHeight() % this.getInterval() != 0 && cursor.getHeader().getDifficultyTargetAsInteger().equals(this.getMaxTarget()); cursor = cursor.getPrev(blockStore)) {
                    ;
                }

                BigInteger cursorTarget = cursor.getHeader().getDifficultyTargetAsInteger();
                BigInteger newTarget = nextBlock.getDifficultyTargetAsInteger();
                if (!cursorTarget.equals(newTarget)) {
                    throw new VerificationException("Testnet block transition that is not allowed: " + Long.toHexString(cursor.getHeader().getDifficultyTarget()) + " vs " + Long.toHexString(nextBlock.getDifficultyTarget()));
                }
            }
        } else {
            super.checkDifficultyTransitions(storedPrev, nextBlock, blockStore);
        }

    }
}
