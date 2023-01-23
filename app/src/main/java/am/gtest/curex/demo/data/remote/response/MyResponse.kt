package am.gtest.curex.demo.data.remote.response

sealed class MyResponse<T>(val data: T? = null) {
    class Success<T>(data: T) : MyResponse<T>(data)
    class Logout<T>(val errorText: String) : MyResponse<T>()
    class Error<T>(data: T? = null, val errorText: String = "") : MyResponse<T>(data)
    class Loading<T> : MyResponse<T>()
    class Empty<T> : MyResponse<T>()
}