package router.air.api.ext

fun Any?.ifNull(block: ()->Unit) {
    if (this == null) {
        block.invoke()
    }
}