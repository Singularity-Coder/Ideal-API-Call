package com.singularitycoder.viewmodelstuff2.helpers.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.io.IOException

fun shareViaContacts() {

}

fun Context.shareViaSms(phoneNum: String) {
    val smsIntent = Intent(Intent.ACTION_VIEW).apply {
        type = "vnd.android-dir/mms-sms"
        putExtra("address", phoneNum)
        putExtra("sms_body", "Message Body check")
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
    }
    if (smsIntent.resolveActivity(packageManager) != null) {
        startActivity(smsIntent)
    }
}

fun Context.shareViaWhatsApp(whatsApp: String) {
    try {
        // checks if such an app exists or not
        packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
        val uri = Uri.parse("smsto:$whatsApp")
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply { setPackage("com.whatsapp") }
        startActivity(Intent.createChooser(intent, "Dummy Title"))
    } catch (e: PackageManager.NameNotFoundException) {
        Toast.makeText(this, "WhatsApp not found. Install from PlayStore.", Toast.LENGTH_SHORT).show()
        val uri = Uri.parse("market://details?id=com.whatsapp")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET) }
        startActivity(intent)
    }
}

fun Context.shareViaEmail(email: String) {
    val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null)).apply {
        putExtra(Intent.EXTRA_SUBJECT, "Fav Anime")
        putExtra(Intent.EXTRA_TEXT, "Check this out...")
    }
    startActivity(Intent.createChooser(intent, "Send email..."))
}

fun Activity.shareViaApps(imageDrawableOrUrl: Any?, imageView: ImageView?, title: String, subtitle: String): Void? {
    if (null != imageDrawableOrUrl && null != imageView) {
        // Ask external storage permission
        shareImageAndText(imageDrawableOrUrl, imageView, title, subtitle)
    } else {
        shareOnlyText(title, subtitle)
    }
    return null
}

fun Activity.shareImageAndText(imageDrawableOrUrl: Any, imageView: ImageView, title: String, subtitle: String) {
    Glide.with(this)
        .asBitmap()
        .load(imageDrawableOrUrl)
        .into(object : CustomTarget<Bitmap?>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                imageView.setImageBitmap(resource)
            }
            override fun onLoadCleared(placeholder: Drawable?) = Unit
        })
    val bmpUri = getLocalBitmapUri(imageView) ?: Uri.EMPTY
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/.*"
        putExtra(Intent.EXTRA_STREAM, bmpUri)
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, subtitle)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
    }
    startActivity(Intent.createChooser(intent, "Share image using"))
}

fun Activity.shareOnlyText(title: String, subtitle: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, subtitle)
//        addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
    }
    startActivity(Intent.createChooser(intent, "Share to"))
}

@Throws(IOException::class)
fun Activity.shareApp() {
    try {
        val pm = packageManager
        val appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + appInfo.publicSourceDir))
            putExtra(Intent.EXTRA_TEXT, "Fav Anime")
        }
        startActivity(Intent.createChooser(intent, "Share it using"))
    } catch (e1: PackageManager.NameNotFoundException) {
        e1.printStackTrace()
    }
}