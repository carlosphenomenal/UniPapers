package com.unipapers.unipapers_frontend.core.data.remote

import android.content.Context
import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import com.unipapers.unipapers_frontend.BuildConfig
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.GeminiResult
import com.unipapers.unipapers_frontend.feature.filemanagement.utils.GeminiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.FileOutputStream

class GeminiServiceTest {

    private lateinit var targetContext: Context
    private lateinit var testContext: Context
    private lateinit var geminiService: GeminiService

    @Before
    fun setup() {
        targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        testContext = InstrumentationRegistry.getInstrumentation().context
        // Use the API key from your BuildConfig (configured in gradle.properties)
        geminiService = GeminiService(targetContext, BuildConfig.GEMINI_API_KEY)
    }

    @Test
    fun testRealFileAnalysis() = runBlocking {
        // 1. Prepare the real file from assets
        // assets in src/androidTest/assets are in the testContext
        val testFileName = "test_paper.pdf"
        val file = File(targetContext.cacheDir, testFileName)

        try {
            testContext.assets.open(testFileName).use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to load asset '$testFileName' from test context. " +
                    "Make sure it's in src/androidTest/assets/", e)
        }

        val uri = Uri.fromFile(file)

        // 2. Call the real service
        val result = geminiService.analysePaper(uri)

        // 3. Verify the result
        if (result is GeminiResult.Error) {
            println("Gemini Error: ${result.message}")
        }

        assertTrue("Expected Success but got $result", result is GeminiResult.Success)

        if (result is GeminiResult.Success) {
            println("Extracted Tags: ${result.response.topicTags}")
            println("Confidence: ${result.response.confidence}")
            assertTrue("Expected tags to be extracted", result.response.topicTags.isNotEmpty())
        }
    }
}
