package router.api.logistics

interface Track {
    fun onLost()

    fun onFound()

    fun onArrival()

    fun onIntercept()
}