package org.wordpress.android.ui.stats.refresh.lists

import android.arch.lifecycle.LiveData
import android.support.annotation.StringRes
import kotlinx.coroutines.experimental.CoroutineDispatcher
import org.wordpress.android.R
import org.wordpress.android.viewmodel.ScopedViewModel

abstract class StatsListViewModel(defaultDispatcher: CoroutineDispatcher) : ScopedViewModel(defaultDispatcher) {
    enum class StatsListType(@StringRes val titleRes: Int) {
        INSIGHTS(R.string.stats_insights),
        DAYS(R.string.stats_timeframe_days),
        WEEKS(R.string.stats_timeframe_weeks),
        MONTHS(R.string.stats_timeframe_months);
    }

    abstract val navigationTarget: LiveData<NavigationTarget>

    abstract val data: LiveData<StatsUiState>
}

data class StatsUiState(val data: List<StatsBlock> = listOf(), val status: StatsListState) {
    enum class StatsListState {
        DONE,
        ERROR,
        FETCHING
    }
}