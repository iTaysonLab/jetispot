package bruhcollective.itaysonlab.jetispot.core.collection.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import bruhcollective.itaysonlab.jetispot.core.collection.SpCollectionManager
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionAlbum

class CollectionAlbumsPagingSource(private val collection: SpCollectionManager): PagingSource<Int, CollectionAlbum>() {
  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CollectionAlbum> {
    TODO()
  }

  override fun getRefreshKey(state: PagingState<Int, CollectionAlbum>): Int? {
    TODO()
  }
}