package org.jraf.android.cinetoday

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.jraf.android.cinetoday.repository.Theater
import org.jraf.android.cinetoday.repository.TheaterRepository
import org.jraf.android.cinetoday.util.logging.logd
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var theaterRepository: TheaterRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        runBlocking {
            val theaters: List<Theater> = theaterRepository.search("trappes")
            logd(theaters)
            theaterRepository.addToFavorites(theaters[0])
            val favorites: List<Theater> = theaterRepository.getFavorites().first()
            logd("Favorites: $favorites")
            theaterRepository.removeFromFavorites(favorites[0].id)
            val favoritesAfter: List<Theater> = theaterRepository.getFavorites().first()
            logd("Favorites after: $favoritesAfter")
        }
    }
}
