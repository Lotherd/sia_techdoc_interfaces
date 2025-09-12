/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.utilities;

import java.util.concurrent.ConcurrentSkipListMap;
import trax.aero.pojo.GroupBuffer;

public class GroupBufferManager {
    private static final ConcurrentSkipListMap<String, GroupBuffer> groupMap =
            new ConcurrentSkipListMap<>();

    public static void put(String key, GroupBuffer buffer) {
        groupMap.put(key, buffer);
    }

    public static GroupBuffer get(String key) {
        return groupMap.get(key);
    }

    public static void remove(String key) {
        groupMap.remove(key);
    }
}
