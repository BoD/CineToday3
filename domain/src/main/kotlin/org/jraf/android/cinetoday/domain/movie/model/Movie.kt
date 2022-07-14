/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2022-present Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jraf.android.cinetoday.domain.movie.model

import android.content.Context
import org.jraf.android.cinetoday.util.datetime.formatDurationHourMinute
import java.time.LocalDate
import java.util.Date

data class Movie(
    val id: String,
    val title: String,
    val posterUrl: String?,
    val releaseDate: LocalDate?,
    val colorDark: Int?,
    val colorLight: Int?,
    val directors: String,
    val showtimes: List<Showtime>,
    val genres: String,
    val actors: String,
    val synopsis: String?,
    val runtimeMinutes: Int,
    val originalTitle: String,
) {
    context(Context)
    fun runtimeFormatted(): String = formatDurationHourMinute(runtimeMinutes)

    val showtimesPerTheater: Map<String, List<Showtime>> by lazy { showtimes.groupBy { it.theaterName } }
    val releaseYear: Int? = releaseDate?.year
}

fun fakeMovie() = Movie(
    id = "",
    title = "Jurassic Park",
    posterUrl = null,
    releaseDate = LocalDate.now(),
    colorDark = 0xFF000000.toInt(),
    colorLight = 0xFF000000.toInt(),
    showtimes = listOf(
        Showtime(
            id = "id",
            theaterId = "theaterId",
            theaterName = "UGC Gobelins",
            startsAt = Date(),
            projection = listOf(),
            languageVersion = "VF"
        ),
        Showtime(
            id = "id",
            theaterId = "theaterId",
            theaterName = "UGC Gobelins",
            startsAt = Date(),
            projection = listOf("3D"),
            languageVersion = "VF"
        ),
        Showtime(
            id = "id",
            theaterId = "theaterId",
            theaterName = "UGC Gobelins",
            startsAt = Date(),
            projection = listOf("3D", "Imax"),
            languageVersion = "VF"
        )


    ),
    directors = "Steven Spielberg",
    genres = "Thriller",
    actors = "Sam Neill, Laura Dern, Jeff Goldblum, Richard Attenborough",
    synopsis = "A wealthy entrepreneur is visiting his son's theme park, which is full of the undead. He spots a zombie and, unaware of its existence, attacks it. The zombie is killed, but the park is left in a state of disrepair. The park's director, John Hammond, has given the zombie a new name, and the zombie is now named Jurassic Park. The park is visited by a variety of visitors, including a beautiful, but eccentric, jurassic park ranger, Charles Bronson. The ranger is tasked with protecting the park from the dinosaurs, but the park's security is compromised when a dinosaur attack is thwarted by a young dinosaur named Colin, who is a trained hunter. The ranger must now protect the park from the growing number of dinosaurs, and the park's security is threatened by the arrival of a new dinosaur species. The park is plagued by a variety of dinosaurs, and the ranger must use his skills to save the park from these dinosaurs.",
    runtimeMinutes = 120,
    originalTitle = "Jurassic Park",
)
