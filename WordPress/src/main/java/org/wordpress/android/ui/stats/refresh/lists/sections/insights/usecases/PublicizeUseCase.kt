package org.wordpress.android.ui.stats.refresh.lists.sections.insights.usecases

import kotlinx.coroutines.experimental.CoroutineDispatcher
import org.wordpress.android.R.string
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.fluxc.model.stats.PublicizeModel
import org.wordpress.android.fluxc.store.InsightsStore
import org.wordpress.android.fluxc.store.StatsStore.InsightsTypes.PUBLICIZE
import org.wordpress.android.modules.UI_THREAD
import org.wordpress.android.ui.stats.refresh.lists.NavigationTarget.ViewPublicizeStats
import org.wordpress.android.ui.stats.refresh.lists.StatsBlock
import org.wordpress.android.ui.stats.refresh.lists.sections.BaseStatsUseCase
import org.wordpress.android.ui.stats.refresh.lists.sections.BlockListItem
import org.wordpress.android.ui.stats.refresh.lists.sections.BlockListItem.Empty
import org.wordpress.android.ui.stats.refresh.lists.sections.BlockListItem.Label
import org.wordpress.android.ui.stats.refresh.lists.sections.BlockListItem.Link
import org.wordpress.android.ui.stats.refresh.lists.sections.BlockListItem.Title
import org.wordpress.android.ui.stats.refresh.utils.ServiceMapper
import javax.inject.Inject
import javax.inject.Named

private const val PAGE_SIZE = 5

class PublicizeUseCase
@Inject constructor(
    @Named(UI_THREAD) private val mainDispatcher: CoroutineDispatcher,
    private val insightsStore: InsightsStore,
    private val mapper: ServiceMapper
) : BaseStatsUseCase(PUBLICIZE, mainDispatcher) {
    override suspend fun loadCachedData(site: SiteModel): StatsBlock? {
        return insightsStore.getPublicizeData(site,
                PAGE_SIZE
        )?.let { buildPublicizeUiModel(site, it) }
    }

    override suspend fun fetchRemoteData(site: SiteModel, forced: Boolean): StatsBlock? {
        val response = insightsStore.fetchPublicizeData(site,
                PAGE_SIZE, forced)
        val model = response.model
        val error = response.error

        return when {
            error != null -> createFailedItem(
                    string.stats_view_publicize,
                    error.message ?: error.type.name
            )
            else -> model?.let { buildPublicizeUiModel(site, model) }
        }
    }

    private fun buildPublicizeUiModel(site: SiteModel, model: PublicizeModel): StatsBlock {
        val items = mutableListOf<BlockListItem>()
        items.add(Title(string.stats_view_publicize))
        if (model.services.isEmpty()) {
            items.add(Empty)
        } else {
            items.add(Label(string.stats_publicize_service_label, string.stats_publicize_followers_label))
            items.addAll(model.services.let { mapper.map(it) })
            if (model.hasMore) {
                items.add(Link(text = string.stats_insights_view_more) {
                    navigateTo(ViewPublicizeStats(site.siteId))
                })
            }
        }
        return createDataItem(items)
    }
}