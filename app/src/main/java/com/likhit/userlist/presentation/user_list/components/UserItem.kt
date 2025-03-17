package com.likhit.userlist.presentation.user_list.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.likhit.userlist.R
import com.likhit.userlist.data.remote.model.User
import com.likhit.userlist.utils.isNetworkAvailable

@Composable
fun UserItem(
    modifier: Modifier = Modifier,
    context: Context,
    user: User,
) {
    val placeholderImage =
        "https://cdn.pixabay.com/photo/2016/08/08/09/17/avatar-1577909_960_720.png"
    val isValidImage = user.image.isNotEmpty() && user.image != placeholderImage
    var showImageDialog by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(15.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(235.dp)
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(15.dp)),
                    contentAlignment = Alignment.Center
                ){
                    if(isNetworkAvailable(context)){
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(
                                    if(isValidImage) user.image else placeholderImage
                                )
                                .crossfade(true)
                                .build(),
                            contentDescription = "User's Image",
                            loading = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            },
                            onError = { error ->
                                Log.e("ImageError", "Error in image loading: ${error.result}")
                            },
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(150.dp)
                                .clickable {
                                    if (isValidImage) {
                                        showImageDialog = true
                                    }
                                }
                                .background(Color.LightGray.copy(alpha = 0.8f))
                        )
                    }else{
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .background(Color.LightGray.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "You are offline,\ngo online \nto see image",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                color = Color.Red.copy(alpha = 0.5f),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    UserInfoRow(
                        text = "${user.firstName} ${user.lastName}",
                        fontWeight = FontWeight.SemiBold,
                        textColor = Color.DarkGray,
                        painter = painterResource(R.drawable.user),
                        contentDescription = "user icon"
                    )
                    UserInfoRow(
                        text = user.gender,
                        textColor = Color.DarkGray,
                        painter = painterResource(R.drawable.gender),
                        contentDescription = "gender icon"
                    )
                    UserInfoRow(
                        text = user.birthDate,
                        textColor = Color.DarkGray,
                        painter = painterResource(R.drawable.dob),
                        contentDescription = "dob icon"
                    )
                    UserInfoRow(
                        text = user.phone,
                        textColor = Color.DarkGray,
                        painter = painterResource(R.drawable.phone),
                        contentDescription = "phone icon"
                    )
                }
            }
            Spacer(Modifier.heightIn(8.dp))
            UserInfoRow(
                text = "${user.address.address}, ${user.address.city}, ${user.address.state} (${user.address.stateCode}), ${user.address.country} - ${user.address.postalCode}",
                textColor = Color.DarkGray,
                painter = painterResource(R.drawable.address),
                contentDescription = "address icon"
            )
        }
    }

    if(showImageDialog){
        UserImageDialog(
            image = user.image,
            onDismiss = {
                showImageDialog = false
            }
        )
    }
}

@Composable
fun UserImageDialog(
    image: String,
    onDismiss: () -> Unit
) {
    var scale by remember {
        mutableStateOf(1f)
    }
    var offsetX by remember {
        mutableStateOf(0f)
    }
    var offsetY by remember {
        mutableStateOf(0f)
    }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)

        val maxTranslationX = (scale - 1f) * 200f
        val maxTranslationY = (scale - 1f) * 400f

        offsetX = (offsetX + panChange.x).coerceIn(-maxTranslationX, maxTranslationX)
        offsetY = (offsetY + panChange.y).coerceIn(-maxTranslationY, maxTranslationY)
    }

    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ){
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onDoubleTap = {
                            scale = 1f
                        })
                    }
                    .graphicsLayer(
                        scaleX = scale.coerceIn(1f, 5f),
                        scaleY = scale.coerceIn(1f, 5f),
                        translationX = offsetX.coerceIn(-300f, 300f),
                        translationY = offsetY.coerceIn(-300f, 300f)
                    )
                    .transformable(transformState),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = image,
                    contentDescription = "User's image",
                    modifier = Modifier
                        .clip(RoundedCornerShape(15.dp))
                )
            }
        }
    }
}

@Composable
fun UserInfoRow(
    text: String,
    fontWeight: FontWeight = FontWeight.Normal,
    textColor: Color = Color.Black,
    painter: Painter,
    contentDescription: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ){
        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(20.dp)
        )
        Spacer(Modifier.width(2.dp))
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = fontWeight,
            color = textColor
        )
    }
}