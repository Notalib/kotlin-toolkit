/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.r2.navigator.epub

import android.webkit.WebResourceResponse

/**
 * Allow the response to be consumed by publication documents served
 * from any origin, including the package domain.
 */
internal fun WebResourceResponse.allowCors() {
    responseHeaders = responseHeaders.orEmpty() + mapOf("Access-Control-Allow-Origin" to "*")
}
