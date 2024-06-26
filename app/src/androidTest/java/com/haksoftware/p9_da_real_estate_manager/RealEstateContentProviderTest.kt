package com.haksoftware.p9_da_real_estate_manager

import com.haksoftware.p9_da_real_estate_manager.provider.RealEstateContentProvider

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.provider.ProviderTestRule
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RealEstateContentProviderTest {
    @get:Rule
    val providerRule: ProviderTestRule = ProviderTestRule.Builder(
        RealEstateContentProvider::class.java, RealEstateContentProvider.AUTHORITY
    ).build()

    @Test
    fun query_allRealEstate() {
        val cursor = providerRule.resolver.query(
            Uri.parse("content://${RealEstateContentProvider.AUTHORITY}/${RealEstateContentProvider.TABLE_NAME}"),
            null, null, null, null
        )

        assertNotNull(cursor)
        cursor?.close()
    }
}
