package br.com.corelib

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.*

fun Any?.launchImage(
    uri: String?, size: Int,
    context: Context,
    callback: (bitmap: Bitmap) -> Unit,
) = GlobalScope.launch {
    this@launchImage.toString()
    withContext(Dispatchers.Main) {
        (context as Activity).runOnUiThread {
            Glide.with(context)
                .asBitmap()
                .load(uri)
                .placeholder(R.drawable.ic_notload)
                .apply(
                    RequestOptions().format(
                        DecodeFormat.PREFER_ARGB_8888
                    )
                ).skipMemoryCache(true)
                .into(object : CustomTarget<Bitmap?>(size, size) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?,
                    ) {
                        callback(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {}

                    override fun onLoadCleared(placeholder: Drawable?) {}

                })
        }
    }
}

fun Any?.share(
    context: Context,
    description: String,
    bitmap: Bitmap,
) {
    this.toString()
    val share = Intent(Intent.ACTION_SEND)
    share.type = "image/jpeg"

    val values = ContentValues().apply {
        put(MediaStore.Images.Media.TITLE, "title")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }

    val uri = context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        values
    )!!

    try {
        val stream = context.contentResolver.openOutputStream(uri)!!
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.close()
    } catch (e: Exception) {
        System.err.println(e.toString())
    }

    share.putExtra(Intent.EXTRA_TEXT, description)
    share.putExtra(Intent.EXTRA_STREAM, uri)
    context.startActivity(Intent.createChooser(share, "Compartilhar evento"))
}

 fun Any?.callLoad(context: Context, view: View, group: ViewGroup) {
     GlobalScope.launch {
        this@callLoad.run {


            (1..6000).forEach {
                Log.d("tag", "$it")
            }
            view.visibility = View.VISIBLE
            group.visibility = View.INVISIBLE
        }
    }
}

private val beforeTextChangedStub: (CharSequence, Int, Int, Int) -> Unit = { _, _, _, _ -> }
private val onTextChangedStub: (CharSequence, Int, Int, Int) -> Unit = { _, _, _, _ -> }
private val afterTextChangedStub: (Editable) -> Unit = {}

fun EditText.addChangedListener(
    beforeTextChanged: (CharSequence, Int, Int, Int) -> Unit = beforeTextChangedStub,
    onTextChanged: (CharSequence, Int, Int, Int) -> Unit = onTextChangedStub,
    afterTextChanged: (Editable) -> Unit = afterTextChangedStub,
) = addTextChangedListener(object : TextWatcher {
    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        beforeTextChanged(charSequence, i, i1, i2)
    }

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        onTextChanged(charSequence, i, i1, i2)
    }

    override fun afterTextChanged(editable: Editable) {
        afterTextChanged(editable)
    }
})

fun EditText?.clearAll(
    isAll: Boolean,
) {
    if (isAll) {
        this?.clearFocus()
        this?.text?.clear()
    } else {
        this?.clearFocus()
    }
}