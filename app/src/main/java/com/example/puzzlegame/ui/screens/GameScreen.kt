package com.example.puzzlegame.ui.screens

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
                .background(MaterialTheme.colorScheme.background)
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
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A1209),
                        contentColor = Color(0xFFFFD54F)
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 14.dp, vertical = 8.dp
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "↻",
                            fontSize = MaterialTheme.typography.labelMedium.fontSize * 3,
                            textAlign = TextAlign.Center,
                            lineHeight = (MaterialTheme.typography.labelMedium.fontSize * 3.2)
                        )
                        Text(
                            "500pts",
                            style = MaterialTheme.typography.labelMedium,
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
            FloatingDragShape(
                dragState = dragState,
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

    val fingerInParent = dragState.fingerRootOffset - parentRootOffset
    val dragCenterX = with(density) { fingerInParent.x.toDp() }
    val dragCenterY = with(density) { fingerInParent.y.toDp() } - heightDp / 2 - liftDp

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

    val t = dropProgress.value
    val centerX = if (dragState.isDropAnimating) {
        dragCenterX + (targetCenterX - dragCenterX) * t
    } else {
        dragCenterX
    }
    val centerY = if (dragState.isDropAnimating) {
        dragCenterY + (targetCenterY - dragCenterY) * t
    } else {
        dragCenterY
    }

    val offsetX = centerX - canvasSizeDp / 2
    val offsetY = centerY - canvasSizeDp / 2

    val scale = if (dragState.isDropAnimating) 1f else liftScale.value
    val alpha = if (dragState.isDropAnimating) {
        0.8f + 0.2f * t
    } else {
        0.8f
    }

    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
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