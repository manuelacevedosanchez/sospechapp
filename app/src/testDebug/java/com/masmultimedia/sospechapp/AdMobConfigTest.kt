package com.masmultimedia.sospechapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdMobConfigTest {
    @Test
    fun debugUsesOnlyGoogleTestIds() {
        assertTrue(BuildConfig.DEBUG)
        assertEquals("ca-app-pub-3940256099942544~3347511713", BuildConfig.ADMOB_APP_ID)
        assertEquals("ca-app-pub-3940256099942544/6300978111", BuildConfig.ADMOB_BANNER_AD_UNIT_ID)
        assertEquals("ca-app-pub-3940256099942544/6300978111", BuildConfig.ADMOB_BANNER_READY_AD_UNIT_ID)
    }
}
