package com.beemer.movie.movie.dto

import com.fasterxml.jackson.annotation.JsonFormat

data class KMDbMovieDetailsDto(
    val Query: String,
    val KMAQuery: String,
    val TotalCount: Int,
    val Data: List<Data>
)

data class Data(
    val CollName: String,
    val TotalCount: Int,
    val Count: Int,
    val Result: List<Result>?
)

data class Result(
    val DOCID: String,
    val movieId: String,
    val movieSeq: String,
    val title: String,
    val titleEng: String,
    val titleOrg: String,
    val titleEtc: String,
    val prodYear: String,
    val directors: Directors,
    val actors: Actors,
    val nation: String,
    val company: String,
    val plots: Plots,
    val runtime: String,
    val rating: String,
    val genre: String,
    val kmdbUrl: String,
    val type: String,
    val use: String,
    val episodes: String,
    val ratedYn: String,
    val repRatDate: String,
    val repRlsDate: String,
    val ratings: Ratings,
    val keywords: String,
    val posters: String,
    val stlls: String,
    val staffs: Staffs,
    val vods: Vods,
    val openThtr: String,
    val stat: List<Stat>,
    val screenArea: String,
    val screenCnt: String,
    val salesAcc: String,
    val audiAcc: String,
    val statSouce: String,
    val statDate: String,
    val themeSong: String,
    val soundtrack: String,
    val fLocation: String,
    val Awards1: String,
    val Awards2: String,
    val regDate: String,
    val modDate: String,
    val Codes: Codes,
    val CommCodes: CommCodes,
    val ALIAS: String
)

data class Directors(
    val director: List<Director>
)

data class Director(
    val directorNm: String,
    val directorEnNm: String,
    val directorId: String
)

data class Actors(
    val actor: List<Actor>
)

data class Actor(
    val actorNm: String,
    val actorEnNm: String,
    val actorId: String
)

data class Plots(
    val plot: List<Plot>
)

data class Plot(
    val plotLang: String,
    val plotText: String
)

data class Ratings(
    val rating: List<Rating>
)

data class Rating(
    val ratingMain: String,
    val ratingDate: String,
    val ratingNo: String,
    val ratingGrade: String,
    val releaseDate: String,
    val runtime: String
)

data class Staffs(
    val staff: List<Staff>
)

data class Staff(
    val staffNm: String,
    val staffEnNm: String,
    val staffRoleGroup: String,
    val staffRole: String,
    val staffEtc: String,
    val staffId: String
)

data class Vods(
    val vod: List<Vod>
)

data class Vod(
    val vodClass: String,
    val vodUrl: String
)

data class Stat(
    val screenArea: String,
    val screenCnt: String,
    val salesAcc: String,
    val audiAcc: String,
    val statSouce: String,
    val statDate: String
)

data class Codes(
    @JsonFormat(with = [JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY])
    val Code: List<Code>
)

data class Code(
    val CodeNm: String?,
    val CodeNo: String?
)

data class CommCodes(
    @JsonFormat(with = [JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY])
    val CommCode: List<CommCode>
)

data class CommCode(
    val CodeNm: String?,
    val CodeNo: String?
)