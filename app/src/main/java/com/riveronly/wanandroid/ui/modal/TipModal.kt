package com.riveronly.wanandroid.ui.modal

import android.view.View
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.riveronly.wanandroid.R

sealed class TipStatus(open val text: String) {
    data class Loading(override val text: String = "加载中...") : TipStatus(text)
    data class Info(override val text: String) : TipStatus(text)
    data class Done(override val text: String = "加载成功") : TipStatus(text)
    data class Error(override val text: String = "加载失败") : TipStatus(text)
}

@Composable
fun TipModal(status: TipStatus) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.6f)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val iconSize = 36.dp
            when (status) {
                is TipStatus.Loading -> {
                    Loading(
                        size = iconSize, lineColor = Color.White
                    )
                }

                is TipStatus.Done -> {
                    Image(
                        painter = painterResource(
                            id = R.mipmap.ic_tip_done
                        ),
                        contentDescription = status.text,
                        modifier = Modifier.size(iconSize),
                        contentScale = ContentScale.Fit
                    )
                }

                is TipStatus.Error -> {
                    Image(
                        painter = painterResource(
                            id = R.mipmap.ic_tip_error
                        ),
                        contentDescription = status.text,
                        modifier = Modifier.size(iconSize),
                        contentScale = ContentScale.Fit
                    )
                }

                is TipStatus.Info -> {
                    Image(
                        painter = painterResource(
                            id = R.mipmap.ic_tip_info
                        ),
                        contentDescription = status.text,
                        modifier = Modifier.size(iconSize),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            Text(
                text = status.text,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp)
            )
        }
    }
}

fun View.loadingModal(
    mask: Color = DefaultMaskColor,
    systemCancellable: Boolean = false,
    maskTouchBehavior: MaskTouchBehavior = MaskTouchBehavior.None,
    modalHostProvider: ModalHostProvider = DefaultModalHostProvider,
    enter: EnterTransition = fadeIn(tween(), 0f),
    exit: ExitTransition = fadeOut(tween(), 0f)
): EmoModal {
    return emoModal(
        mask,
        systemCancellable,
        maskTouchBehavior,
        modalHostProvider = modalHostProvider,
        enter = enter,
        exit = exit
    ) {
        TipModal(TipStatus.Loading())
    }
}
