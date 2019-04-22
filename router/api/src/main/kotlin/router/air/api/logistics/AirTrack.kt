package router.air.api.logistics

/**
 * 跳转回调
 * @author wuxi
 * @since 2019/4/18
 */
class AirTrack: Track {
    var lostTrack: (()->Unit)? = null
    var foundTrack: (()->Unit)? = null
    var arrivalTrack: (()->Unit)? = null
    var interceptTrack: (()->Unit)? = null

    override fun onLost() {
        lostTrack?.invoke()
    }

    override fun onFound() {
        foundTrack?.invoke()
    }

    override fun onArrival() {
        arrivalTrack?.invoke()
    }

    override fun onIntercept() {
        interceptTrack?.invoke()
    }
}