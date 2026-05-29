package com.hivestudio.data.repository

import com.hivestudio.data.remote.model.ProfileDto
import com.hivestudio.ui.model.BeatCardUi
import com.hivestudio.ui.model.DashboardOverviewUi

object ClientDataCache {
    @Volatile
    var profile: ProfileDto? = null

    @Volatile
    var ownBeatCards: List<BeatCardUi>? = null

    @Volatile
    var dashboardOverview: DashboardOverviewUi? = null

    @Volatile
    var publicCatalogBeatCards: List<BeatCardUi>? = null

    fun clearAll() {
        profile = null
        ownBeatCards = null
        dashboardOverview = null
        publicCatalogBeatCards = null
    }
}
