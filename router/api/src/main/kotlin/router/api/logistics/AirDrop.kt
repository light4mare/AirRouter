package router.api.logistics

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import router.api.Router
import java.io.Serializable
import java.util.*

/**
 * 空投包，用于传递参数资源
 * @author wuxi
 * @since 2019/4/18
 */
class AirDrop(private val uri: String) {
    private var extras = Bundle()
    private var flags = -1

    private var track: AirTrack? = null

    fun post(context: Context) {
        Router.post(context, this, track)
    }

    fun getUri(): String {
        return uri
    }

    fun getExtras(): Bundle {
        return extras
    }

    fun getFlags(): Int {
        return flags
    }

    fun addFlags(flags: Int): AirDrop {
        this.flags = this.flags or flags
        return this
    }

    private fun getTrack(): AirTrack {
        return track ?: AirTrack().apply {
            track = this
        }
    }

    fun withFlags(flag: Int): AirDrop {
        this.flags = flag
        return this
    }

    fun with(bundle: Bundle?): AirDrop {
        if (null != bundle) {
            extras = bundle
        }
        return this
    }

    fun withInt(key: String?, int: Int): AirDrop {
        extras.putInt(key, int)
        return this
    }

    fun withIntArray(key: String?, int: IntArray?): AirDrop {
        extras.putIntArray(key, int)
        return this
    }

    fun withIntArrayList(key: String?, int: ArrayList<Int>?): AirDrop {
        extras.putIntegerArrayList(key, int)
        return this
    }

    fun withLong(key: String?, long: Long): AirDrop {
        extras.putLong(key, long)
        return this
    }

    fun withLongArray(key: String?, long: LongArray?): AirDrop {
        extras.putLongArray(key, long)
        return this
    }

    fun withString(key: String?, string: String?): AirDrop {
        extras.putString(key, string)
        return this
    }

    fun withStringArray(key: String?, string: Array<String>?): AirDrop {
        extras.putStringArray(key, string)
        return this
    }

    fun withStringList(key: String?, stringList: ArrayList<String>?): AirDrop {
        extras.putStringArrayList(key, stringList)
        return this
    }

    fun withBoolean(key: String?, boolean: Boolean): AirDrop {
        extras.putBoolean(key, boolean)
        return this
    }

    fun withBooleanArray(key: String?, booleanArray: BooleanArray?): AirDrop {
        extras.putBooleanArray(key, booleanArray)
        return this
    }

    fun withDouble(key: String?, double: Double): AirDrop {
        extras.putDouble(key, double)
        return this
    }

    fun withDoubleArray(key: String?, double: DoubleArray?): AirDrop {
        extras.putDoubleArray(key, double)
        return this
    }

    fun withByte(key: String?, byte: Byte): AirDrop {
        extras.putByte(key, byte)
        return this
    }

    fun withByteArray(key: String?, byte: ByteArray?): AirDrop {
        extras.putByteArray(key, byte)
        return this
    }

    fun withShort(key: String?, short: Short): AirDrop {
        extras.putShort(key, short)
        return this
    }

    fun withShortArray(key: String?, short: ShortArray?): AirDrop {
        extras.putShortArray(key, short)
        return this
    }

    fun withChar(key: String?, char: Char): AirDrop {
        extras.putChar(key, char)
        return this
    }

    fun withCharArray(key: String?, char: CharArray?): AirDrop {
        extras.putCharArray(key, char)
        return this
    }

    fun withFloat(key: String?, float: Float): AirDrop {
        extras.putFloat(key, float)
        return this
    }

    fun withFloatArray(key: String?, float: FloatArray?): AirDrop {
        extras.putFloatArray(key, float)
        return this
    }

    fun withCharSequence(key: String?, charSequence: CharSequence?): AirDrop {
        extras.putCharSequence(key, charSequence)
        return this
    }

    fun withCharSequenceArray(key: String?, charSequence: Array<CharSequence>?): AirDrop {
        extras.putCharSequenceArray(key, charSequence)
        return this
    }

    fun withCharSequenceArrayList(key: String?, charSequence: ArrayList<CharSequence>?): AirDrop {
        extras.putCharSequenceArrayList(key, charSequence)
        return this
    }

    fun withParcelable(key: String?, parcelable: Parcelable?): AirDrop {
        extras.putParcelable(key, parcelable)
        return this
    }

    fun withParcelableArray(key: String?, parcelable: Array<Parcelable>?): AirDrop {
        extras.putParcelableArray(key, parcelable)
        return this
    }

    fun withParcelableArrayList(key: String?, parcelable: ArrayList<Parcelable>?): AirDrop {
        extras.putParcelableArrayList(key, parcelable)
        return this
    }

    fun <T : Parcelable> withSparseParcelableArray(key: String?, parcelable: SparseArray<T>?): AirDrop {
        extras.putSparseParcelableArray(key, parcelable)
        return this
    }

    fun withSerializable(key: String?, seralizable: Serializable?): AirDrop {
        extras.putSerializable(key, seralizable)
        return this
    }

    fun lost(block: ()->Unit): AirDrop {
        getTrack().arrivalTrack = block
        return this
    }

    fun found(block: ()->Unit): AirDrop {
        getTrack().foundTrack = block
        return this
    }

    fun arrival(block: ()->Unit): AirDrop {
        getTrack().arrivalTrack = block
        return this
    }

    fun intercept(block: ()->Unit): AirDrop {
        getTrack().interceptTrack = block
        return this
    }
}