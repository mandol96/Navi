package com.cho.navi.data

object Storage {

    val postList: List<Post> = getDummyData()

    private fun getDummyData(): List<Post> {
        return listOf(
            Post("풍경", listOf("#000000", "#facfac"), "게시글1", "내용1", "ㅇㅇ시ㅇㅇ구ㅇㅇ동"),
            Post("놀이", listOf("#a00faa", "#EEC08A"), "게시글2", "내용2", "ㅇㅇ시ㅇㅇ구ㅇㅇ동"),
            Post("맛집", listOf("#966363", "#2395C5"), "게시글3", "내용3", "ㅇㅇ시ㅇㅇ구ㅇㅇ동")
        )
    }
}