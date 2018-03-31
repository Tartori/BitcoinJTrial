//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ch.bfh;

import com.google.common.base.Stopwatch;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import org.bitcoinj.core.BitcoinSerializer;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Utils;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.core.NetworkParameters.ProtocolVersion;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.utils.MonetaryFormat;

public abstract class AbstractBitcoinNetParams extends NetworkParameters {
    public static final String BITCOIN_SCHEME = "bitcoin";

    public AbstractBitcoinNetParams() {
    }

    protected boolean isDifficultyTransitionPoint(StoredBlock storedPrev) {
        return (storedPrev.getHeight() + 1) % this.getInterval() == 0;
    }

    public void checkDifficultyTransitions(StoredBlock storedPrev, Block nextBlock, BlockStore blockStore) throws VerificationException, BlockStoreException {
        Block prev = storedPrev.getHeader();
        if (!this.isDifficultyTransitionPoint(storedPrev)) {
            if (nextBlock.getDifficultyTarget() != prev.getDifficultyTarget()) {
                throw new VerificationException("Unexpected change in difficulty at height " + storedPrev.getHeight() + ": " + Long.toHexString(nextBlock.getDifficultyTarget()) + " vs " + Long.toHexString(prev.getDifficultyTarget()));
            }
        } else {
            Stopwatch watch = Stopwatch.createStarted();
            StoredBlock cursor = blockStore.get(prev.getHash());

            for(int i = 0; i < this.getInterval() - 1; ++i) {
                if (cursor == null) {
                    throw new VerificationException("Difficulty transition point but we did not find a way back to the genesis block.");
                }

                cursor = blockStore.get(cursor.getHeader().getPrevBlockHash());
            }

            watch.stop();
            if (watch.elapsed(TimeUnit.MILLISECONDS) > 50L) {
            }

            Block blockIntervalAgo = cursor.getHeader();
            int timespan = (int)(prev.getTimeSeconds() - blockIntervalAgo.getTimeSeconds());
            int targetTimespan = this.getTargetTimespan();
            if (timespan < targetTimespan / 4) {
                timespan = targetTimespan / 4;
            }

            if (timespan > targetTimespan * 4) {
                timespan = targetTimespan * 4;
            }

            BigInteger newTarget = Utils.decodeCompactBits(prev.getDifficultyTarget());
            newTarget = newTarget.multiply(BigInteger.valueOf((long)timespan));
            newTarget = newTarget.divide(BigInteger.valueOf((long)targetTimespan));
            if (newTarget.compareTo(this.getMaxTarget()) > 0) {
                newTarget = this.getMaxTarget();
            }

            int accuracyBytes = (int)(nextBlock.getDifficultyTarget() >>> 24) - 3;
            long receivedTargetCompact = nextBlock.getDifficultyTarget();
            BigInteger mask = BigInteger.valueOf(16777215L).shiftLeft(accuracyBytes * 8);
            newTarget = newTarget.and(mask);
            long newTargetCompact = Utils.encodeCompactBits(newTarget);
            if (newTargetCompact != receivedTargetCompact) {
                throw new VerificationException("Network provided difficulty bits do not match what was calculated: " + Long.toHexString(newTargetCompact) + " vs " + Long.toHexString(receivedTargetCompact));
            }
        }
    }

    public Coin getMaxMoney() {
        return MAX_MONEY;
    }

    public Coin getMinNonDustOutput() {
        return Transaction.MIN_NONDUST_OUTPUT;
    }

    public MonetaryFormat getMonetaryFormat() {
        return new MonetaryFormat();
    }

    public int getProtocolVersionNum(ProtocolVersion version) {
        return version.getBitcoinProtocolVersion();
    }

    public BitcoinSerializer getSerializer(boolean parseRetain) {
        return new BitcoinSerializer(this, parseRetain);
    }

    public String getUriScheme() {
        return "bitcoin";
    }

    public boolean hasMaxMoney() {
        return true;
    }
}
