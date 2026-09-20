package com.example.puzzlegame.ui.screens

import androidx.compose.ui.unit.sp

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.puzzlegame.logic.GameEngine
import com.example.puzzlegame.ui.components.DraggableShapePreview
import com.example.puzzlegame.ui.components.GameGrid
import com.example.puzzlegame.ui.components.HoldBox
import com.example.puzzlegame.ui.components.ScoreBar
import com.example.puzzlegame.ui.components.ScorePopOverlay
import com.example.puzzlegame.ui.components.ShapePreview
import com.example.puzzlegame.viewModel.DragSource
import com.example.puzzlegame.viewModel.DragState
import com.example.puzzlegame.viewModel.GameViewModel
import com.example.puzzlegame.viewModel.HapticEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()
    val dragState by viewModel.dragState.collectAsState()
    val scorePops by viewModel.scorePops.collectAsState()
    val graceCountdown by viewModel.graceCountdown.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val trayOffsetX = remember { Animatable(0f) }
    var isRefreshing by remember { mutableStateOf(false) }
    val screenDensity = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(screenDensity) { configuration.screenWidthDp.dp.toPx() }

    val haptic = LocalHapticFeedback.current
    LaunchedEffect(Unit) {
        viewModel.hapticEvents.collect { event ->
            when (event) {
                HapticEvent.PLACE -> haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                HapticEvent.LINE_CLEAR -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    delay(80)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                HapticEvent.WARNING_TICK -> haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

    var gridPositionInRoot by remember { mutableStateOf(Offset.Zero) }
    var boxPositionInRoot by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coords ->
                boxPositionInRoot = coords.positionInRoot()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            com.example.puzzlegame.ui.theme.BackgroundDark,
                            com.example.puzzlegame.ui.theme.GlowBlue.copy(alpha = 0.2f),
                            com.example.puzzlegame.ui.theme.GlowPurple.copy(alpha = 0.3f),
                            com.example.puzzlegame.ui.theme.BackgroundDark
                        )
                    )
                )
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            ScoreBar(
                score = gameState.score,
                highScore = gameState.highScore,
                onSettingsClick = onSettingsClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            GameGrid(
                grid = gameState.grid,
                clearingCells = gameState.clearingCells,
                highlightCells = dragState.highlightCells,
                ghostShape = dragState.shape,
                ghostRow = dragState.ghostRow,
                ghostCol = dragState.ghostCol,
                ghostValid = dragState.ghostValid,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .onGloballyPositioned { coords ->
                        gridPositionInRoot = coords.positionInRoot()
                    },
                onGridLayout = { gridOffset, cellSizePx ->
                    viewModel.gridScreenOffset = gridOffset
                    viewModel.cellSizePx = cellSizePx
                }
            )

            val density = LocalDensity.current
            val liftBasePx = with(density) { 96.dp.toPx() }
            viewModel.liftBasePx = liftBasePx

            Box(modifier = Modifier.fillMaxWidth().clipToBounds()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(trayOffsetX.value.roundToInt(), 0) },
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    gameState.currentShapes.forEachIndexed { index, shape ->
                        val canFit = shape != null && GameEngine.canFitAnywhere(gameState.grid, shape)
                        DraggableShapePreview(
                            shape = shape,
                            modifier = Modifier.weight(1f).testTag("tray_shape_$index"),
                            onDragStart = { s -> viewModel.onDragStart(index, s) },
                            onDrag = { rootOffset ->
                                val gridRelative = rootOffset - gridPositionInRoot
                                val s = dragState.shape
                                val floatingCenterYOffset = if (s != null) {
                                    val gridCellPx = viewModel.cellSizePx
                                    val liftPx = liftBasePx + gridCellPx
                                    val floatingHeightPx = s.height * gridCellPx
                                    -floatingHeightPx / 2f - liftPx
                                } else 0f
                                viewModel.onDrag(rootOffset, gridRelative, floatingCenterYOffset)
                            },
                            onDragEnd = { viewModel.onDragEnd() },
                            onDragCancel = { viewModel.onDragCancel() },
                            onDoubleTap = { viewModel.rotateShape(index) },
                            isDragging = dragState.source == DragSource.TRAY && dragState.shapeIndex == index,
                            dimmed = !canFit
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            val holdCanFit = gameState.holdShape != null &&
                    GameEngine.canFitAnywhere(gameState.grid, gameState.holdShape!!)
            Box(modifier = Modifier.fillMaxWidth()) {
                HoldBox(
                    modifier = Modifier.align(Alignment.BottomCenter).testTag("hold_box"),
                    holdShape = gameState.holdShape,
                    isDragging = dragState.source == DragSource.HOLD && dragState.shape != null,
                    dimmed = !holdCanFit && gameState.holdShape != null,
                    onDragStart = { s -> viewModel.onHoldDragStart(s) },
                    onDrag = { rootOffset ->
                        val gridRelative = rootOffset - gridPositionInRoot
                        val s = dragState.shape
                        val floatingCenterYOffset = if (s != null) {
                            val gridCellPx = viewModel.cellSizePx
                            val liftPx = liftBasePx + gridCellPx
                            val floatingHeightPx = s.height * gridCellPx
                            -floatingHeightPx / 2f - liftPx
                        } else 0f
                        viewModel.onDrag(rootOffset, gridRelative, floatingCenterYOffset)
                    },
                    onDragEnd = { viewModel.onDragEnd() },
                    onDragCancel = { viewModel.onDragCancel() },
                    onDoubleTap = { viewModel.rotateHoldShape() },
                    onGloballyPositioned = { coords ->
                        val pos = coords.positionInRoot()
                        viewModel.holdBoxScreenRect = Rect(
                            offset = pos,
                            size = Size(
                                coords.size.width.toFloat(),
                                coords.size.height.toFloat()
                            )
                        )
                    }
                )
                    Button(
                        onClick = {
                            if (!isRefreshing && dragState.shape == null) {
                                isRefreshing = true
                                coroutineScope.launch {
                                    trayOffsetX.animateTo(screenWidthPx, tween(200, easing = FastOutSlowInEasing))
                                    viewModel.refreshTray()
                                    trayOffsetX.snapTo(-screenWidthPx)
                                    trayOffsetX.animateTo(0f, tween(200, easing = FastOutSlowInEasing))
                                    isRefreshing = false
                                }
                            }
                        },
                        enabled = dragState.shape == null && gameState.score >= 500, 
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(bottom = 12.dp, start = 12.dp)
                            .size(72.dp)
                            .border(1.dp, com.example.puzzlegame.ui.theme.GlassBorder, androidx.compose.foundation.shape.CircleShape),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = com.example.puzzlegame.ui.theme.GlassSurface,
                            contentColor = com.example.puzzlegame.ui.theme.AccentPrimary,
                            disabledContainerColor = com.example.puzzlegame.ui.theme.GlassSurface.copy(alpha = 0.05f),
                            disabledContentColor = com.example.puzzlegame.ui.theme.TextPrimary.copy(alpha = 0.45f)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text(
                                "↻",
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "500pts",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
            }
        }

        if (scorePops.isNotEmpty()) {
            val gridInBox = gridPositionInRoot - boxPositionInRoot
            val density = LocalDensity.current
            val gridOffsetDp = with(density) {
                DpOffset(gridInBox.x.toDp(), gridInBox.y.toDp())
            }
            Box(modifier = Modifier.offset(x = gridOffsetDp.x, y = gridOffsetDp.y)) {
                ScorePopOverlay(
                    pops = scorePops,
                    gridOffset = viewModel.gridScreenOffset,
                    cellSizePx = viewModel.cellSizePx,
                    onDismiss = { id -> viewModel.dismissScorePop(id) }
                )
            }
        }

        graceCountdown?.let { seconds ->
            GameOverWarningOverlay(
                secondsRemaining = seconds,
                dimmed = dragState.shape != null
            )
        }

        if (dragState.shape != null) {
            val dragOffsetState = viewModel.dragOffset.collectAsState()
            FloatingDragShape(
                dragState = dragState,
                dragOffsetState = dragOffsetState,
                gridCellSizePx = viewModel.cellSizePx,
                parentRootOffset = boxPositionInRoot,
                gridRootOffset = gridPositionInRoot,
                gridPaddingOffset = viewModel.gridScreenOffset,
                onDropAnimationDone = { viewModel.onDropAnimationDone() }
            )
        }
        val confettiTrigger by viewModel.confettiTrigger.collectAsState()
        if (confettiTrigger > 0) {
            key(confettiTrigger) {
                ConfettiEffect()
            }
        }

        if (gameState.isGameOver) {
            GameOverOverlay(
                score = gameState.score,
                highScore = gameState.highScore,
                onNewGame = { viewModel.startNewGame() }
            )
        }
    }
}

@Composable
private fun FloatingDragShape(
    dragState: DragState,
    dragOffsetState: androidx.compose.runtime.State<Offset>,
    gridCellSizePx: Float,
    parentRootOffset: Offset,
    gridRootOffset: Offset,
    gridPaddingOffset: Offset,
    onDropAnimationDone: () -> Unit
) {
    val shape = dragState.shape ?: return
    val density = LocalDensity.current
    val cellSizeDp = with(density) { gridCellSizePx.toDp() }

    val heightDp = cellSizeDp * shape.height
    val maxDim = 5
    val canvasSizeDp = cellSizeDp * maxDim

    val liftDp = 96.dp + cellSizeDp

    val gridInParent = gridRootOffset - parentRootOffset
    val targetCenterX = with(density) { (gridInParent.x + gridPaddingOffset.x).toDp() } +
            cellSizeDp * (dragState.ghostCol + shape.width / 2f)
    val targetCenterY = with(density) { (gridInParent.y + gridPaddingOffset.y).toDp() } +
            cellSizeDp * (dragState.ghostRow + shape.height / 2f)

    val liftScale = remember(shape) { Animatable(0.5f) }
    LaunchedEffect(shape) {
        liftScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
        )
    }

    val dropProgress = remember { Animatable(0f) }
    LaunchedEffect(dragState.isDropAnimating) {
        if (dragState.isDropAnimating) {
            dropProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
            )
            onDropAnimationDone()
        } else {
            dropProgress.snapTo(0f)
        }
    }

    val scale = if (dragState.isDropAnimating) 1f else liftScale.value
    val t = dropProgress.value
    val alpha = if (dragState.isDropAnimating) {
        0.8f + 0.2f * t
    } else {
        0.8f
    }

    Box(
        modifier = Modifier
            .offset {
                val fingerInParent = dragOffsetState.value - parentRootOffset
                val dragCenterXPx = fingerInParent.x
                val dragCenterYPx = fingerInParent.y - with(density) { (heightDp / 2 + liftDp).toPx() }

                val targetCenterXPx = with(density) { targetCenterX.toPx() }
                val targetCenterYPx = with(density) { targetCenterY.toPx() }

                val centerX = if (dragState.isDropAnimating) {
                    dragCenterXPx + (targetCenterXPx - dragCenterXPx) * t
                } else {
                    dragCenterXPx
                }
                val centerY = if (dragState.isDropAnimating) {
                    dragCenterYPx + (targetCenterYPx - dragCenterYPx) * t
                } else {
                    dragCenterYPx
                }

                val canvasSizePx = with(density) { canvasSizeDp.toPx() }
                IntOffset(
                    x = (centerX - canvasSizePx / 2f).roundToInt(),
                    y = (centerY - canvasSizePx / 2f).roundToInt()
                )
            }
            .size(canvasSizeDp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
    ) {
        ShapePreview(
            shape = shape,
            cellSize = cellSizeDp
        )
    }
}