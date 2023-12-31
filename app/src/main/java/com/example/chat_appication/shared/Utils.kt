package com.example.chat_appication.shared

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import android.util.Base64
import com.example.chat_appication.model.FriendShipStatus
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utils {
    companion object {
        val database = FirebaseFirestore.getInstance()

        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun encodeImage(bitmap: Bitmap): String {
            val previewWidth = 150
            val previewHeight = bitmap.height * previewWidth / bitmap.width
            val previewBitmap =
                Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
            val byteArrayOutput = ByteArrayOutputStream()
            previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutput)
            val bytes = byteArrayOutput.toByteArray()
            return Base64.encodeToString(bytes, Base64.DEFAULT)
        }

        fun decodeImage(image: String): Bitmap {
            val bytes = Base64.decode(image, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }


        fun getReadableDateTime(date: Date): String {
            return SimpleDateFormat("dd MMMM, yyyy - hh:mm a", Locale.getDefault()).format(date)
        }

        suspend fun fetchInvitedUser(
            currentUserId: String,
            callback: (data: QuerySnapshot) -> Unit
        ) {
            Utils.database.collection(Constants.KEY_FRIENDSHIP_COLLECTION)
                .whereEqualTo(Constants.KEY_SENDER_INVITE_ID, currentUserId)
                .whereEqualTo(Constants.KEY_FRIENDSHIP_STATUS, FriendShipStatus.SENDING).get()
                .addOnSuccessListener {
                    callback(it)
                }
        }

        suspend fun fetchSendInvitedUser(
            currentUserId: String,
            callback: (data: QuerySnapshot) -> Unit
        ) {
            Utils.database.collection(Constants.KEY_FRIENDSHIP_COLLECTION)
                .whereEqualTo(Constants.KEY_RECEIVER_INVITE_ID, currentUserId)
                .whereEqualTo(Constants.KEY_FRIENDSHIP_STATUS, FriendShipStatus.SENDING).get()
                .addOnSuccessListener {
                    callback(it)
                }
        }

        suspend fun fetchFriendUser1(
            currentUserId: String,
            callback: (data: QuerySnapshot) -> Unit
        ) {
            Database.friendshipCollection
                .whereEqualTo(Constants.KEY_SENDER_INVITE_ID, currentUserId)
                .whereEqualTo(Constants.KEY_FRIENDSHIP_STATUS, FriendShipStatus.ACCEPTED).get()
                .addOnSuccessListener {
                    callback(it)
                }
        }

        suspend fun fetchFriendUser2(
            currentUserId: String,
            callback: (data: QuerySnapshot) -> Unit
        ) {
            Database.friendshipCollection
                .whereEqualTo(Constants.KEY_RECEIVER_INVITE_ID, currentUserId)
                .whereEqualTo(Constants.KEY_FRIENDSHIP_STATUS, FriendShipStatus.ACCEPTED).get()
                .addOnSuccessListener {
                    callback(it)
                }
        }
    }
}