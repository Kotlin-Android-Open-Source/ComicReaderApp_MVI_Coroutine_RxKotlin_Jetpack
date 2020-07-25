package com.hoc.comicapp.ui.search_comic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hoc.comicapp.GlideApp
import com.hoc.comicapp.R
import com.hoc.comicapp.activity.main.MainActivity
import com.hoc.comicapp.ui.search_comic.SearchComicContract.SingleEvent
import com.hoc.comicapp.ui.search_comic.SearchComicContract.ViewIntent
import com.hoc.comicapp.utils.isOrientationPortrait
import com.hoc.comicapp.utils.observe
import com.hoc.comicapp.utils.observeEvent
import com.hoc.comicapp.utils.snack
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_search_comic.*
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import timber.log.Timber

class SearchComicFragment : Fragment() {
  private val viewModel by lifecycleScope.viewModel<SearchComicViewModel>(owner = this)
  private val compositeDisposable = CompositeDisposable()
  private val mainActivity get() = requireActivity() as MainActivity

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View =
    inflater.inflate(R.layout.fragment_search_comic, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val searchComicAdapter = SearchComicAdapter(
      GlideApp.with(this),
      compositeDisposable
    )
    initView(searchComicAdapter)
    bind(searchComicAdapter)
  }

  private fun initView(searchComicAdapter: SearchComicAdapter) {
    view?.post { mainActivity.showSearch() }

    val maxSpanCount = if (requireContext().isOrientationPortrait) 2 else 4
    recycler_search_comic.run {
      setHasFixedSize(true)
      layoutManager = GridLayoutManager(context, maxSpanCount).apply {
        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
          override fun getSpanSize(position: Int): Int {
            return when (searchComicAdapter.getItemViewType(position)) {
              R.layout.item_recycler_search_comic_load_more -> maxSpanCount
              else -> 1
            }
          }
        }
      }
      adapter = searchComicAdapter
    }

    searchComicAdapter
      .clickComicObservable
      .subscribeBy {
        val toComicDetailFragment =
          SearchComicFragmentDirections.actionSearchComicFragmentToComicDetailFragment(
            comic = it,
            title = it.title,
            isDownloaded = false
          )
        findNavController().navigate(toComicDetailFragment)
      }
      .addTo(compositeDisposable)
  }

  private fun bind(adapter: SearchComicAdapter) {
    viewModel.singleEvent.observeEvent(owner = viewLifecycleOwner) {
      when (it) {
        is SingleEvent.MessageEvent -> {
          view?.snack(it.message)
        }
      }
    }
    viewModel.state.observe(owner = viewLifecycleOwner) { (isLoading, comics, errorMessage) ->
      Timber.d("[STATE] comics.length=${comics.size} isLoading=$isLoading errorMessage=$errorMessage")

      if (isLoading) {
        progress_bar.visibility = View.VISIBLE
      } else {
        progress_bar.visibility = View.INVISIBLE
      }

      if (errorMessage == null) {
        group_error.visibility = View.GONE
      } else {
        group_error.visibility = View.VISIBLE
        text_error_message.text = errorMessage
      }

      adapter.submitList(comics)

      empty_layout.isVisible = !isLoading && errorMessage === null && comics.isEmpty()
    }
    viewModel.processIntents(
      Observable.mergeArray(
        mainActivity
          .textSearchChanges()
          .map { ViewIntent.SearchIntent(it) },
        button_retry
          .clicks()
          .map { ViewIntent.RetryFirstIntent },
        adapter
          .clickButtonRetryOrLoadMoreObservable
          .map { if (it) ViewIntent.RetryNextPage else ViewIntent.LoadNextPage }
      )
    ).addTo(compositeDisposable)
  }

  override fun onDestroyView() {
    super.onDestroyView()

    mainActivity.hideSearchIfNeeded()
    compositeDisposable.clear()
  }
}