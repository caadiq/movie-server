package com.beemer.movie.movie.dto

data class WeeklyBoxOfficeListDto(
    val boxOfficeResult: WeeklyBoxOfficeResult
)

data class WeeklyBoxOfficeResult(
    val boxofficeType: String,
    val showRange: String,
    val yearWeekTime: String,
    val weeklyBoxOfficeList: List<WeeklyBoxOffice>
)

data class WeeklyBoxOffice(
    val rnum: String,
    val rank: String,
    val rankInten: String,
    val rankOldAndNew: String,
    val movieCd: String,
    val movieNm: String,
    val openDt: String,
    val salesAmt: String,
    val salesShare: String,
    val salesInten: String,
    val salesChange: String,
    val salesAcc: String,
    val audiCnt: String,
    val audiInten: String,
    val audiChange: String,
    val audiAcc: String,
    val scrnCnt: String,
    val showCnt: String
)