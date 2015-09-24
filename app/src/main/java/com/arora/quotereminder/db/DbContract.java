package com.arora.quotereminder.db;

import android.provider.BaseColumns;

/**
 * Created by ahsanferoz on 22/09/15.
 */
public class DbContract {

    public static final String DB_NAME = "com.arora.quotereminder.db.quotes";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "quotes";

    public class Columns {
        public static final String QUOTE = "quote";
        public static final String _ID = BaseColumns._ID;
        public static final String VALIDITY = "validity";
        public static final String COUNTER = "counter";
    }
}
