package router.air.api.logistics

interface Track {
    fun onLost()

    fun onFound()

    fun onArrival()

    fun onIntercept()
}