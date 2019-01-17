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

package com.github.moko256.twitlatte

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.moko256.latte.client.base.entity.ListEntry

/**
 * Created by moko256 on 2019/01/12.
 */
class SelectListEntriesActivity: AppCompatActivity(), SelectListsEntriesFragment.ListEntrySelectionListener, BaseListFragment.GetViewForSnackBar {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.let {
            it.setTitle(R.string.add_to_list)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp)
        }

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(android.R.id.content, SelectListsEntriesFragment.newInstance(intent.getLongExtra("userId", -1)))
                    .commit()
        }
    }

    override fun onSelected(listEntry: ListEntry) {
        setResult(Activity.RESULT_OK, Intent().putExtra("listId", listEntry.listId))
        finish()
    }

    override fun getViewForSnackBar(): View {
        return findViewById(android.R.id.content)
    }

    override fun onSupportNavigateUp(): Boolean {
        setResult(Activity.RESULT_CANCELED)
        finish()
        return true
    }
}