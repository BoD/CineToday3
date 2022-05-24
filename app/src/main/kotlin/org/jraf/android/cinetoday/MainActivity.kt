package org.jraf.android.cinetoday

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import org.jraf.android.cinetoday.repository.TheaterRepository
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var theaterRepository: TheaterRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        runBlocking {
            println(theaterRepository.searchTheaters("trappes"))
        }
    }
}
