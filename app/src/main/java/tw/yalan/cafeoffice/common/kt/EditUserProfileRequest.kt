package tw.yalan.cafeoffice.common.kt

import com.google.gson.annotations.SerializedName


data class EditUserProfileRequest(
        @SerializedName("member_id")
        var memberId: String? = null,
        @SerializedName("display_name")
        var displayName: String? = null,
        @SerializedName("photo_url")
        var photoUrl: String? = null,
        @SerializedName("default_location")
        var defaultLocation: String? = null,
        @SerializedName("intro")
        var intro: String? = null,
        @SerializedName("email")
        var email: String? = null
) {

    /**
     * member_id : M8okOL1VC5bHjjqVqYwsk2db1AK2
     * display_name : 快樂森林小熊
     * photo_url : https://scontent.xx.fbcdn.net/v/t1.0-1/p100x100/xxxxxx.jpg
     * default_location : taipei
     * intro : 我是kapi的忠實...
     * email : xxx@hotmail.com
     */


}
