package com.gmail.mrmioxin.ainfo;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by palchuk on 09.10.2015.
 * Agroinform progect
 */
 public final class ContractClass {
        public static final String AUTHORITY = "com.gmail.mrmioxin.ainfo.ContractClass";
        private ContractClass() {}
        public static final class Articles implements BaseColumns {
            private Articles() {}
            public static final String TAB_NAME ="article_table";
            private static final String SCHEME = "content://";
            private static final String PATH_ARTICLE = "/articles";
            private static final String PATH_ARTICLE_ID = "/articles/";
            public static final int ARTICLE_ID_PATH_POSITION = 1;
            public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_ARTICLE);
            public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_ARTICLE_ID);
            public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY + PATH_ARTICLE_ID + "/#");
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.gmail.mrmioxin.ainfo.articles";
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.gmail.mrmioxin.ainfo.articles";
            public static final String DEFAULT_SORT_ORDER = "date DESC";
            public static final String COLNAME_DATE   = "date";
            public static final String COLNAME_TITLE   = "title";
            public static final String COLNAME_DESCR   = "descr";
            public static final String COLNAME_CHECK   = "fk_class_id";
            public static final String COLNAME_CONTENT = "content";
            public static final String COLNAME_IMG     = "img";
            public static final String COLNAME_IMG_SRC = "img_src";
            public static final String COLNAME_TAGS    = "tags";
            public static final String[] DEFAULT_PROJECTION = new String[] {
                    ContractClass.Articles._ID,
                    ContractClass.Articles.COLNAME_DATE,
                    ContractClass.Articles.COLNAME_TITLE,
                    ContractClass.Articles.COLNAME_DESCR,
                    ContractClass.Articles.COLNAME_CHECK,
                    ContractClass.Articles.COLNAME_CONTENT,
                    ContractClass.Articles.COLNAME_IMG,
                    ContractClass.Articles.COLNAME_IMG_SRC,
                    ContractClass.Articles.COLNAME_TAGS
            };
        }

}
