package com.beemer.movie.movie.dto

data class DailyBoxOfficeListDto(
    val boxOfficeResult: DailyBoxOfficeResult
)

data class DailyBoxOfficeResult(
    val boxofficeType: String,
    val showRange: String,
    val dailyBoxOfficeList: List<DailyBoxOffice>
)

data class DailyBoxOffice(
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