package com.adarsh7665.famelackindia

import com.lagradost.cloudstream3.plugins.BasePlugin
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin

@CloudstreamPlugin
class FamelackIndiaPlugin : BasePlugin() {
    override fun load() {
        registerMainAPI(FamelackIndiaProvider())
    }
}
