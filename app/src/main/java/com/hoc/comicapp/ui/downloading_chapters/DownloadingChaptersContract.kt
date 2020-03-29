package com.hoc.comicapp.ui.downloading_chapters

import com.hoc.comicapp.base.Intent
import com.hoc.comicapp.domain.models.ComicAppError
import com.hoc.comicapp.domain.models.DownloadedChapter
import java.util.*
import com.hoc.comicapp.base.SingleEvent as BaseSingleEvent
import com.hoc.comicapp.base.ViewState as BaseViewState

interface DownloadingChaptersContract {

  sealed class ViewIntent : Intent {
    object Initial : ViewIntent()
    data class CancelDownload(val chapter: ViewState.Chapter) : ViewIntent()
  }

  data class ViewState(
    val isLoading: Boolean,
    val error: String?,
    val chapters: List<Chapter>,
  ) : BaseViewState {

    companion object {
      fun initial(): ViewState {
        return ViewState(
          isLoading = true,
          error = null,
          chapters = emptyList()
        )
      }
    }

    data class Chapter(
      val title: String,
      val link: String,
      val comicTitle: String,
      val progress: Int,
    ) {
      fun isSameExceptProgress(other: Chapter): Boolean {
        if (this === other) return true
        if (title != other.title) return false
        if (link != other.link) return false
        if (comicTitle != other.comicTitle) return false
        return true
      }

      fun toDomain(): DownloadedChapter {
        return DownloadedChapter(
          chapterLink = link,
          chapterName = title,
          view = "",
          time = "",
          comicLink = "",
          downloadedAt = Date(),
          images = emptyList(),
          chapters = emptyList(),
          nextChapterLink = null,
          prevChapterLink = null
        )
      }
    }
  }

  sealed class PartialChange {
    data class Data(val chapters: List<ViewState.Chapter>) : PartialChange()

    data class Error(val error: ComicAppError) : PartialChange()

    object Loading : PartialChange()
  }

  sealed class SingleEvent : BaseSingleEvent {
    data class Message(val message: String) : SingleEvent()

    data class Deleted(val chapter: ViewState.Chapter) : SingleEvent()

    data class DeleteError(
      val chapter: ViewState.Chapter,
      val error: ComicAppError,
    ) : SingleEvent()
  }
}