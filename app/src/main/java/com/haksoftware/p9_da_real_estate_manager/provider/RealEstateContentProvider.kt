package com.haksoftware.p9_da_real_estate_manager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.haksoftware.p9_da_real_estate_manager.data.manager.RealEstateManagerDatabase
import com.haksoftware.p9_da_real_estate_manager.data.dao.RealEstateDao
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateEntity
import javax.annotation.Nullable

/**
 * Content Provider for accessing Real Estate data.
 * Provides an interface to access Real Estate data through URIs.
 */
class RealEstateContentProvider : ContentProvider() {

    private lateinit var realEstateDao: RealEstateDao

    companion object {
        const val AUTHORITY = "com.haksoftware.p9_da_real_estate_manager.provider"
        val TABLE_NAME: String = RealEstateEntity::class.java.simpleName
        /** The match code for the directory of items in the real_estate table. */
        private const val CODE_REALESTATE_DIR = 1
        /** The match code for a single item in the real_estate table. */
        private const val CODE_REALESTATE_ITEM = 2
        /** The URI matcher for matching URIs to their respective codes. */
        private val MATCHER = UriMatcher(UriMatcher.NO_MATCH)

        init {
            MATCHER.addURI(AUTHORITY, TABLE_NAME, CODE_REALESTATE_DIR)
            MATCHER.addURI(AUTHORITY, "$TABLE_NAME/*", CODE_REALESTATE_ITEM)
        }
    }

    /**
     * Initializes the Content Provider.
     * Sets up the DAO for accessing the real estate database.
     *
     * @return True if the provider was successfully loaded, false otherwise.
     */
    override fun onCreate(): Boolean {
        context?.let {
            realEstateDao = RealEstateManagerDatabase.getDatabase(it).realEstateDao()
        }
        return true
    }

    /**
     * Handles query requests from clients.
     *
     * @param uri The URI to query.
     * @param projection The list of columns to put into the cursor.
     * @param selection A selection criteria to apply when filtering rows.
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs.
     * @param sortOrder How the rows in the cursor should be sorted.
     * @return A Cursor object, which is positioned before the first entry.
     */
    @Nullable
    override fun query(
        uri: Uri, @Nullable projection: Array<String>?, @Nullable selection: String?,
        @Nullable selectionArgs: Array<String>?, @Nullable sortOrder: String?
    ): Cursor? {

        val code = MATCHER.match(uri)
        return if (code == CODE_REALESTATE_DIR || code == CODE_REALESTATE_ITEM) {
            val context = context ?: return null
            val cursor: Cursor = if (code == CODE_REALESTATE_DIR) {
                realEstateDao.getAllRealEstateCursor()
            } else {
                realEstateDao.getRealEstateByIdCursor(ContentUris.parseId(uri).toInt())
            }
            cursor.setNotificationUri(context.contentResolver, uri)
            cursor
        } else {
            throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    /**
     * Gets the MIME type of the data at the given URI.
     *
     * @param uri The URI to query.
     * @return A MIME type string, or null if there is no type.
     */
    override fun getType(uri: Uri): String {
        return when (MATCHER.match(uri)) {
            CODE_REALESTATE_DIR -> "vnd.android.cursor.dir/vnd.$AUTHORITY.$TABLE_NAME"
            CODE_REALESTATE_ITEM -> "vnd.android.cursor.item/vnd.$AUTHORITY.$TABLE_NAME"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    /**
     * Handles requests to insert a new row.
     *
     * @param uri The content:// URI of the insertion request.
     * @param values A set of column_name/value pairs to add to the database.
     * @return The URI for the newly inserted item.
     */
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    /**
     * Handles requests to delete one or more rows.
     *
     * @param uri The full content:// URI to query.
     * @param selection An optional restriction to apply to rows when deleting.
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs.
     * @return The number of rows affected.
     */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    /**
     * Handles requests to update one or more rows.
     *
     * @param uri The URI to query.
     * @param values A set of column_name/value pairs to update in the database.
     * @param selection An optional filter to match rows to update.
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs.
     * @return The number of rows affected.
     */
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }
}
