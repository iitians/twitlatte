/*
 * Copyright 2015-2018 The twitlatte authors
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

package com.github.moko256.twitlatte

import android.os.Bundle

import twitter4j.PagableResponseList
import twitter4j.TwitterException
import twitter4j.User

/**
 * Created by moko256 on 2016/03/29.
 *
 * @author moko256
 */
class UserFollowsFragment : BaseUsersFragment(), ToolbarTitleInterface, NavigationPositionInterface {

    private var userId = -1L

    override val titleResourceId = R.string.following

    override val navigationPosition = R.id.nav_follow_and_follower

    override fun onInitializeList() {
        if (userId == -1L) {
            userId = arguments!!.getLong("userId", -1L)
        }
        super.onInitializeList()
    }

    @Throws(TwitterException::class)
    override fun getResponseList(cursor: Long): PagableResponseList<User> {
        return client.twitter.getFriendsList(userId, cursor)
    }

    companion object {
        fun newInstance(userId: Long): UserFollowsFragment {
            return UserFollowsFragment().apply {
                arguments = Bundle().also { args ->
                    args.putLong("userId", userId)
                }
            }
        }
    }

}