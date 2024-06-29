package com.riveronly.wanandroid.ui.modal

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import com.riveronly.wanandroid.utils.extend.throttleClick

internal abstract class ModalPresent(
    private val rootLayout: FrameLayout,
    private val onBackPressedDispatcher: OnBackPressedDispatcher,
    val mask: Color = DefaultMaskColor,
    val systemCancellable: Boolean = true,
    val maskTouchBehavior: MaskTouchBehavior = MaskTouchBehavior.Dismiss,
    val themeWrapper: @Composable (@Composable () -> Unit) -> Unit
) : EmoModal {

    private val onShowListeners = arrayListOf<EmoModal.Action>()
    private val onDismissListeners = arrayListOf<EmoModal.Action>()
    private val visibleState = mutableStateOf(false)
    private var isShown = false
    private var isDismissing = false
    private var isRendered = false

    private val composeLayout = ComposeView(rootLayout.context).apply {
        visibility = View.GONE
        consumeWindowInsets = false
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (systemCancellable) {
                dismiss()
            }
        }
    }

    init {
        composeLayout.setContent {
            themeWrapper {
                Box(modifier = Modifier.fillMaxSize()) {
                    ModalContent(
                        visible = visibleState.value,
                        onRendered = {
                            isRendered = true
                        }
                    ) {
                        // this callback invoked when dismiss animation finished.
                        if (isDismissing) {
                            doAfterDismiss()
                        }
                    }
                }
            }
        }
    }

    private fun doAfterDismiss() {
        isDismissing = false
        composeLayout.visibility = View.GONE
        composeLayout.disposeComposition()
        rootLayout.removeView(composeLayout)
        onBackPressedCallback.remove()
        onDismissListeners.forEach {
            it.invoke(this)
        }
    }

    @Composable
    abstract fun ModalContent(
        visible: Boolean,
        onRendered: () -> Unit,
        dismissFinishAction: () -> Unit
    )

    override fun isShowing(): Boolean {
        return isShown
    }

    override fun show(): EmoModal {
        if (isShown || isDismissing) {
            return this
        }
        isShown = true
        rootLayout.addView(composeLayout, generateLayoutParams())
        composeLayout.visibility = View.VISIBLE
        visibleState.value = true
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        onShowListeners.forEach {
            it.invoke(this)
        }
        return this
    }

    open fun generateLayoutParams(): FrameLayout.LayoutParams {
        return FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun dismiss() {
        if (!isShown) {
            return
        }
        isShown = false
        if (!isRendered) {
            doAfterDismiss()
        } else {
            isDismissing = true
            visibleState.value = false
        }
    }

    override fun doOnShow(listener: EmoModal.Action): EmoModal {
        onShowListeners.add(listener)
        return this
    }

    override fun doOnDismiss(listener: EmoModal.Action): EmoModal {
        onDismissListeners.add(listener)
        return this
    }

    override fun removeOnShowAction(listener: EmoModal.Action): EmoModal {
        onShowListeners.remove(listener)
        return this
    }

    override fun removeOnDismissAction(listener: EmoModal.Action): EmoModal {
        onDismissListeners.remove(listener)
        return this
    }
}

internal class StillModalImpl(
    rootLayout: FrameLayout,
    onBackPressedDispatcher: OnBackPressedDispatcher,
    mask: Color = DefaultMaskColor,
    systemCancellable: Boolean = true,
    maskTouchBehavior: MaskTouchBehavior = MaskTouchBehavior.Dismiss,
    themeWrapper: @Composable (@Composable () -> Unit) -> Unit,
    val content: @Composable (modal: EmoModal) -> Unit
) : ModalPresent(
    rootLayout,
    onBackPressedDispatcher,
    mask,
    systemCancellable,
    maskTouchBehavior,
    themeWrapper
) {

    @Composable
    override fun ModalContent(
        visible: Boolean,
        onRendered: () -> Unit,
        dismissFinishAction: () -> Unit
    ) {
        if (visible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(mask)
                    .let {
                        if (maskTouchBehavior == MaskTouchBehavior.Penetrate) {
                            it
                        } else {
                            it.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                enabled = maskTouchBehavior == MaskTouchBehavior.Dismiss
                            ) {
                                dismiss()
                            }
                        }
                    }
            )
            content(this)
            SideEffect {
                onRendered()
            }
        } else {
            SideEffect {
                dismissFinishAction()
            }
        }
    }
}

internal class AnimateModalImpl(
    rootLayout: FrameLayout,
    onBackPressedDispatcher: OnBackPressedDispatcher,
    mask: Color = DefaultMaskColor,
    systemCancellable: Boolean = true,
    maskTouchBehavior: MaskTouchBehavior = MaskTouchBehavior.Dismiss,
    private val enter: EnterTransition = fadeIn(tween(), 0f),
    private val exit: ExitTransition = fadeOut(tween(), 0f),
    themeWrapper: @Composable (@Composable () -> Unit) -> Unit,
    val content: @Composable AnimatedVisibilityScope.(modal: EmoModal) -> Unit
) : ModalPresent(
    rootLayout,
    onBackPressedDispatcher,
    mask,
    systemCancellable,
    maskTouchBehavior,
    themeWrapper
) {

    @Composable
    override fun ModalContent(
        visible: Boolean,
        onRendered: () -> Unit,
        dismissFinishAction: () -> Unit
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = enter,
            exit = exit
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(mask)
                    .let {
                        if (maskTouchBehavior == MaskTouchBehavior.Penetrate) {
                            it
                        } else {
                            it.throttleClick(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                enabled = maskTouchBehavior == MaskTouchBehavior.Dismiss
                            ) {
                                dismiss()
                            }
                        }
                    }
            )
            content(this@AnimateModalImpl)
            DisposableEffect("") {
                onRendered()
                onDispose {
                    dismissFinishAction()
                }
            }
        }
    }
}
