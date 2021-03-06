/*
 * Copyright 2015-2019 The twitlatte authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.moko256.latte.client.mastodon

import com.github.moko256.latte.client.base.entity.*
import com.github.moko256.latte.client.mastodon.date.toISO8601Date
import com.github.moko256.latte.html.convertHtmlToContentAndLinks
import com.sys1yagi.mastodon4j.api.entity.Attachment

/**
 * Created by moko256 on 2018/12/01.
 *
 * @author moko256
 */

internal fun com.sys1yagi.mastodon4j.api.entity.Status.convertToCommonStatus(): Post {
    val status: Status
    val statusUser: User
    val repeat: Repeat?
    val repeatUser: User?

    val baseRepeat = reblog
    if (baseRepeat == null) {
        status = convertToStatus()
        statusUser = account.convertToCommonUser()
        repeat = null
        repeatUser = null
    } else {
        repeat = convertToRepeat()
        repeatUser = account.convertToCommonUser()

        status = baseRepeat.convertToStatus()
        statusUser = baseRepeat.account.convertToCommonUser()
    }

    return Post(
            id = id,
            status = status,
            user = statusUser,
            repeat = repeat,
            repeatedUser = repeatUser
    )
}

private fun com.sys1yagi.mastodon4j.api.entity.Status.convertToStatus(): Status {
    val urls = content.convertHtmlToContentAndLinks()

    return Status(
            id = id,
            userId = account.id,
            text = urls.first,
            sourceName = application?.name,
            sourceWebsite = application?.website,
            createdAt = createdAt.toISO8601Date(),
            inReplyToStatusId = inReplyToId.convertIfZero(),
            inReplyToUserId = inReplyToAccountId.convertIfZero(),
            inReplyToScreenName = if (inReplyToAccountId != 0L) {
                ""
            } else {
                null
            },
            isFavorited = isFavourited,
            isRepeated = isReblogged,
            favoriteCount = favouritesCount,
            repeatCount = reblogsCount,
            repliesCount = repliesCount,
            isSensitive = isSensitive,
            lang = language,
            medias = mediaAttachments.takeIf { it.isNotEmpty() }?.map {
                val resultUrl: String
                val thumbnailUrl: String?
                val type: Media.MediaType

                when (it.type) {
                    Attachment.Type.Video.value -> {
                        resultUrl = it.url
                        thumbnailUrl = it.previewUrl
                        type = Media.MediaType.VIDEO_ONE
                    }
                    Attachment.Type.Gifv.value -> {
                        resultUrl = it.url
                        thumbnailUrl = it.previewUrl
                        type = Media.MediaType.GIF
                    }
                    Attachment.Type.Image.value -> {
                        resultUrl = it.url
                        thumbnailUrl = null
                        type = Media.MediaType.PICTURE
                    }
                    Attachment.Type.Audio.value -> {
                        resultUrl = it.url
                        thumbnailUrl = it.previewUrl
                        type = Media.MediaType.AUDIO
                    }
                    else -> { //It must be Attachment.Type.Unknown.value or undefined type
                        resultUrl = it.remoteUrl ?: it.url
                        thumbnailUrl = null
                        type = Media.MediaType.UNKNOWN
                    }
                }

                Media(
                        thumbnailUrl = thumbnailUrl,
                        originalUrl = resultUrl,
                        mediaType = type.value
                )
            }?.toTypedArray(),
            urls = urls.second,
            mentions = mentions.takeIf { it.isNotEmpty() }?.map { it.acct }?.toTypedArray(),
            emojis = emojis.takeIf { it.isNotEmpty() }?.map {
                Emoji(url = it.url, shortCode = it.shortcode)
            }?.toTypedArray(),
            url = url,
            spoilerText = spoilerText.takeIf { it.isNotEmpty() },
            quotedStatusId = -1,
            visibility = visibility,
            card = card?.let {
                Card(
                        title = it.title,
                        description = it.description,
                        url = it.url,
                        imageUrl = it.image
                )
            },
            poll = poll?.let { poll ->
                Poll(
                        poll.id,
                        poll.expiresAt?.toISO8601Date(),
                        poll.expired,
                        poll.multiple,
                        poll.votesCount,
                        poll.options.map { it.title },
                        poll.options.map { it.votesCount ?: -1 },
                        poll.voted ?: false
                )
            }
    )
}

private fun com.sys1yagi.mastodon4j.api.entity.Status.convertToRepeat(): Repeat {
    return Repeat(
            id = id,
            userId = account.id,
            repeatedStatusId = reblog!!.id,
            createdAt = createdAt.toISO8601Date()
    )
}

private fun Long.convertIfZero() = if (this == 0L) {
    -1
} else {
    this
}