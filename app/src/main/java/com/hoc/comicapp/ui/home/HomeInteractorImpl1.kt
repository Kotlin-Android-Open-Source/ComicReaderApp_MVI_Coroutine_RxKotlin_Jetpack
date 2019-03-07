package com.hoc.comicapp.ui.home

import com.hoc.comicapp.data.ComicRepository
import com.hoc.comicapp.utils.flatMap
import com.hoc.comicapp.utils.fold
import com.hoc.comicapp.utils.map
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.rx2.rxObservable

@ExperimentalCoroutinesApi
class HomeInteractorImpl1(
  private val comicRepository: ComicRepository,
  private val homeInteractorImpl: HomeInteractorImpl
) : HomeInteractor by homeInteractorImpl {
  override fun refreshAllPartialChanges(coroutineScope: CoroutineScope): Observable<HomePartialChange> {
    return coroutineScope.rxObservable<HomePartialChange> {
      send(HomePartialChange.RefreshPartialChange.Loading)

      val suggestAsync = async { comicRepository.getSuggest() }
      val topMonthAsync = async { comicRepository.getTopMonth() }
      val updatedAsync = async { comicRepository.getUpdate() }

      val suggest = suggestAsync.await()
      val topMonth = topMonthAsync.await()
      val updated = updatedAsync.await()

      suggest.flatMap { suggestList ->
        topMonth.flatMap { topMonthList ->
          updated.map { updatedList ->
            HomePartialChange.RefreshPartialChange.RefreshSuccess(
              suggestComics = suggestList,
              topMonthComics = topMonthList,
              updatedComics = updatedList
            )
          }
        }
      }.fold({ HomePartialChange.RefreshPartialChange.RefreshFailure(it) }, { it })
        .let { send(it) }
    }
  }
}