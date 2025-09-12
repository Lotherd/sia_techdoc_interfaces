/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.pojo;

import java.util.concurrent.ConcurrentSkipListMap;
import trax.aero.pojo.xml.ROOT;

public class GroupBuffer {

    private ConcurrentSkipListMap<Long, ROOT> buffer = new ConcurrentSkipListMap<>();
    private long totalCount = 0;
    private long nextExpectedSeq = 1L;

    public ConcurrentSkipListMap<Long, ROOT> getBuffer() {
        return buffer;
    }

    public void setBuffer(ConcurrentSkipListMap<Long, ROOT> buffer) {
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
