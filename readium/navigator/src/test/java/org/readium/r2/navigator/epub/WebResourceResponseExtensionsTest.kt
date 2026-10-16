/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.r2.navigator.epub

import android.webkit.WebResourceResponse
import java.util.Collections
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WebResourceResponseExtensionsTest {

    @Test
    fun `adds CORS header when response headers are null`() {
        val response = response(headers = null)

        response.allowCors()

        assertEquals(mapOf("Access-Control-Allow-Origin" to "*"), response.responseHeaders)
    }

    @Test
    fun `preserves existing headers without mutating an immutable input map`() {
        val headers = Collections.unmodifiableMap(mapOf("Content-Type" to "application/xhtml+xml"))
        val response = response(headers = headers)

        response.allowCors()

        assertEquals(
            mapOf(
                "Content-Type" to "application/xhtml+xml",
                "Access-Control-Allow-Origin" to "*"
            ),
            response.responseHeaders
        )
        assertEquals(mapOf("Content-Type" to "application/xhtml+xml"), headers)
    }

    private fun response(headers: Map<String, String>?): WebResourceResponse =
        WebResourceResponse("text/html", "UTF-8", null).apply {
            responseHeaders = headers
        }
}
