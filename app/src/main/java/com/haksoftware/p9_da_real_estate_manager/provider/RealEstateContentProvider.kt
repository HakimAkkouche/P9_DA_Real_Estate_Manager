package com.haksoftware.p9_da_real_estate_manager.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.haksoftware.p9_da_real_estate_manager.data.RealEstateManagerDatabase
import com.haksoftware.p9_da_real_estate_manager.data.dao.RealEstateDao

class RealEstateContentProvider : ContentProvider() {
    private lateinit var realEstateDao: RealEstateDao

    override fun onCreate(): Boolean {
        val context = context ?: return false
        val database = RealEstateManagerDatabase.getDatabase(context)
        realEstateDao = database.realEstateDao()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val cursor: Cursor
        when (MATCHER.match(uri)) {
            REAL_ESTATE_DIR -> {
                cursor = realEstateDao.getAllRealEstateCursor()
                cursor.setNotificationUri(context?.contentResolver, uri)
                return cursor
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String {
        return when (MATCHER.match(uri)) {
            REAL_ESTATE_DIR -> "vnd.android.cursor.dir/$AUTHORITY.$REAL_ESTATE_PATH"
            REAL_ESTATE_ITEM -> "vnd.android.cursor.item/$AUTHORITY.$REAL_ESTATE_PATH"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        TODO("Not yet implemented")
    }


    companion object {
        private const val AUTHORITY = "com.haksoftware.p9_da_real_estate_manager.provider"
        private const val REAL_ESTATE_PATH = "real_estate"
        private const val REAL_ESTATE_DIR = 1
        private const val REAL_ESTATE_ITEM = 2

        private val MATCHER = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, REAL_ESTATE_PATH, REAL_ESTATE_DIR)
            addURI(AUTHORITY, "$REAL_ESTATE_PATH/#", REAL_ESTATE_ITEM)
        }
    }
}