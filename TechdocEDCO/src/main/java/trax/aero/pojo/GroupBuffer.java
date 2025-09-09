package trax.aero.pojo;

import trax.aero.pojo.xml.ROOT;

import java.util.TreeMap;

public class GroupBuffer {

    private TreeMap<Long, ROOT> buffer = new TreeMap<>();
    private long totalCount  = 0;
    private long nextExpectedSeq  = 1L;

    public TreeMap<Long, ROOT> getBuffer() {
        return buffer;
    }

    public void setBuffer(TreeMap<Long, ROOT> buffer) {
        this.buffer = buffer;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getNextExpectedSeq() {
        return nextExpectedSeq;
    }

    public void setNextExpectedSeq(long nextExpectedSeq) {
        this.nextExpectedSeq = nextExpectedSeq;
    }
}
