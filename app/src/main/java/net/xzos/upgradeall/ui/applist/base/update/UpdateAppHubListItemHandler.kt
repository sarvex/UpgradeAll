package net.xzos.upgradeall.ui.applist.base.update

import android.view.View
import androidx.appcompat.widget.PopupMenu
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.xzos.upgradeall.R
import net.xzos.upgradeall.core.manager.AppManager
import net.xzos.upgradeall.core.module.app.App
import net.xzos.upgradeall.ui.applist.base.AppHubListItemHandler
import net.xzos.upgradeall.ui.detail.setting.AppSettingActivity
import net.xzos.upgradeall.wrapper.download.startDownload

class UpdateAppHubListItemHandler : AppHubListItemHandler() {
    fun clickDownload(app: App, view: View) {
        val fileAsset = app.versionList.firstOrNull()
            ?.assetList?.firstOrNull()
            ?.fileAssetList?.firstOrNull()
            ?: return
        GlobalScope.launch {
            startDownload(
                view.context, false,
                app, fileAsset,
            )
        }
    }

    override fun showPopup(app: App, v: View): Boolean {
        PopupMenu(v.context, v).apply {
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.ignore_update -> {
                        runBlocking { app.versionList.firstOrNull()?.switchIgnoreStatus() }
                        true
                    }
                    else -> false
                }
            }
            inflate(R.menu.menu_app_item_update)
        }.show()
        return true
    }
}