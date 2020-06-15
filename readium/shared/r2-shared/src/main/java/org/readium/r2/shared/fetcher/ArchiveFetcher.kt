/*
 * Module: r2-shared-kotlin
 * Developers: Quentin Gliosca
 *
 * Copyright (c) 2020. Readium Foundation. All rights reserved.
 * Use of this source code is governed by a BSD-style license which is detailed in the
 * LICENSE file present in the project repository where this source code is maintained.
 */

package org.readium.r2.shared.fetcher

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.readium.r2.shared.extensions.addPrefix
import org.readium.r2.shared.format.Format
import org.readium.r2.shared.publication.Link
import org.readium.r2.shared.util.Try
import org.readium.r2.shared.util.archive.Archive
import org.readium.r2.shared.util.archive.JavaZip
import java.io.File

/** Provides access to entries of an archive. */
class ArchiveFetcher private constructor(private val archive: Archive) : Fetcher {

    override suspend fun links(): List<Link> =
        archive.entries.map {
            Link(
                href = it.path.addPrefix("/"),
                type = Format.of(fileExtension = File(it.path).extension)?.mediaType?.toString()
            )
        }

    override fun get(link: Link): Resource =
        EntryResource(link, archive)

    override suspend fun close() = withContext(Dispatchers.IO) { archive.close() }

    companion object {

        suspend fun fromPath(path: String, open: (String) -> Archive? = (JavaZip)::open): ArchiveFetcher? =
            withContext(Dispatchers.IO) {
                open(path)
            }?.let { ArchiveFetcher(it) }
    }

    private class EntryResource(val originalLink: Link, val archive: Archive) : Resource {

        override suspend fun link(): Link =
            // Adds the compressed length to the original link.
            entry.getOrNull()
                ?.let { originalLink.addProperties(mapOf("compressedLength" to it)) }
                ?: originalLink

        override suspend fun read(range: LongRange?): ResourceTry<ByteArray> =
            entry.mapCatching {
                it.read(range) ?: throw Resource.Error.Other(Exception("Cannot read archive entry."))
            }

        override suspend fun length(): ResourceTry<Long>  =
            metadataLength?.let { Try.success(it) }
                ?: read().map { it.size.toLong() }

        override suspend fun close() {}

        private val metadataLength: Long? by lazy {
            entry.getOrNull()?.size
        }

        private val entry: ResourceTry<Archive.Entry> by lazy {
            archive.entry(originalLink.href.removePrefix("/"))
                ?.let { Try.success(it) }
                ?: Try.failure(Resource.Error.NotFound)
        }

    }

}



