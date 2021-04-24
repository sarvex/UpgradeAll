package net.xzos.upgradeall.core.downloader

import net.xzos.upgradeall.core.module.app.FileAsset
import net.xzos.upgradeall.core.module.network.GrpcApi
import net.xzos.upgradeall.core.module.network.toMap

class PreDownload(private val fileAsset: FileAsset, private val downloadInfoList: List<DownloadInfoItem>? = null) {
    private var cancelled = false

    fun cancel() {
        cancelled = true
    }

    suspend fun startDownload(
            taskStartedFun: (Int) -> Unit,
            taskStartFailedFun: (Throwable) -> Unit,
            vararg downloadOb: DownloadOb,
    ): Downloader {
        return doDownload().apply {
            if (cancelled) throw DownloadCanceledError()
            start(taskStartedFun, taskStartFailedFun, *downloadOb)
        }
    }

    private suspend fun doDownload(): Downloader {
        if (cancelled) throw DownloadCanceledError()
        val list = downloadInfoList ?: getDownloadInfoList(fileAsset)
        if (cancelled) throw DownloadCanceledError()
        val downloader = Downloader().apply {
            list.forEach {
                addTask(it.name, it.url, it.headers, it.cookies)
            }
        }
        if (cancelled) throw DownloadCanceledError()
        return downloader
    }

    companion object {

        suspend fun getDownloadInfoList(fileAsset: FileAsset): List<DownloadInfoItem> {
            val appId = fileAsset.app.appId
            val hubUuid = fileAsset.hub.uuid
            val defName = fileAsset.name
            val downloadResponse = GrpcApi.getDownloadInfo(hubUuid, appId, mapOf(), fileAsset.assetIndex)
            var list = downloadResponse?.listList?.map { downloadPackage ->
                val fileName = if (downloadPackage.name.isNotBlank())
                    downloadPackage.name
                else {
                    defName
                }
                DownloadInfoItem(
                        fileName, downloadPackage.url, downloadPackage.headersList?.toMap()
                        ?: mapOf(), downloadPackage.cookiesList?.toMap() ?: mapOf()
                )
            }
            if (list.isNullOrEmpty())
                list = listOf(DownloadInfoItem(defName, fileAsset.downloadUrl, mapOf(), mapOf()))
            return list
        }
    }
}