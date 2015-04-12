
package com.mizo0203.BlueSprinkler;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.UUID;

public class Util {

    static ArrayList<Long> mIDList;

    @SuppressLint("UseValueOf")
    public static Long idFromUUID(UUID uuid) {
        if (uuid.getMostSignificantBits() == 2641465042253400551L) {
            Long id = new Long(uuid.getLeastSignificantBits());
            if (!mIDList.contains(id)) {
                mIDList.add(id);
                return id;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static UUID uuidFromID(long id) {
        return new UUID(2641465042253400551L, id);
    }

    public static void init() {
        mIDList = new ArrayList<Long>();
    }

}
