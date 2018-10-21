package com.fanap.podchat.util;


import android.provider.DocumentsContract.Document;
import android.provider.DocumentsContract.Root;

public class LocalStorageProvider  {

    public static final String AUTHORITY = "ir.fanap.localstorage.documents";

    /**
     * Default root projection: everything but Root.COLUMN_MIME_TYPES
     */
    private final static String[] DEFAULT_ROOT_PROJECTION = new String[] {
            Root.COLUMN_ROOT_ID,
            Root.COLUMN_FLAGS, Root.COLUMN_TITLE, Root.COLUMN_DOCUMENT_ID, Root.COLUMN_ICON,
            Root.COLUMN_AVAILABLE_BYTES
    };
    /**
     * Default document projection: everything but Document.COLUMN_ICON and
     * Document.COLUMN_SUMMARY
     */
    private final static String[] DEFAULT_DOCUMENT_PROJECTION = new String[] {
            Document.COLUMN_DOCUMENT_ID,
            Document.COLUMN_DISPLAY_NAME, Document.COLUMN_FLAGS, Document.COLUMN_MIME_TYPE,
            Document.COLUMN_SIZE,
            Document.COLUMN_LAST_MODIFIED
    };
}