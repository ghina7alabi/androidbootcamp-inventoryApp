package com.example.user.products.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by User on 16-Jul-18.
 */

public class ProductContract {

    private ProductContract() {
    }

    public final static String CONTENT_AUTHORITY = "com.example.user.products";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CLOTHES = "clothes";

    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CLOTHES);

        public final static String TABLE_NAME = "clothes";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "name";
        public static String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_SUPNAME = "supplierName";
        public final static String COLUMN_PRODUCT_SUPNUMBER = "number";
        public final static String COLUMN_PRODUCT_STATUS = "status";

        public static final int STATUS_ORDERED = 1;
        public static final int STATUS_DELIVERED = 2;
        public static final int STATUS_RECIEVED = 3;
        public static final int STATUS_UNKNOWN = 0;

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLOTHES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLOTHES;

        public static boolean isValidStatus(Integer status) {
            if (status== STATUS_UNKNOWN || status == STATUS_ORDERED || status == STATUS_DELIVERED || status == STATUS_RECIEVED) {
                return true;
            }
            return false;
        }
    }
}

