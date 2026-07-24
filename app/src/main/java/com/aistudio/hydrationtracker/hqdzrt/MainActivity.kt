package com.aistudio.hydrationtracker.hqdzrt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.Opacity
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.*
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    WaterTrackerScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun WaterTrackerScreen(
    modifier: Modifier = Modifier,
    viewModel: HydrationViewModel = viewModel()
) {
    val currentIntake by viewModel.currentIntakeMl.collectAsStateWithLifecycle()
    val target by viewModel.targetMl.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .background(DarkBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Title Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Opacity,
                    contentDescription = null,
                    tint = TurquoiseAccent,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Hydration",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextLight,
                        letterSpacing = (-0.5).sp
                    )
                )
            }

            // Quick Info/Goal Target Banner
            Text(
                text = "OBJECTIF: 2.0L",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = TurquoiseAccent,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier
                    .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Center Piece: Beautiful Animated Water Wave Circle Progress
        AnimatedWaterCircle(
            currentIntake = currentIntake,
            target = target,
            modifier = Modifier.padding(12.dp)
        )

        // Daily Insight Card (Immersive UI theme feature)
        val glassesLeft = ((target - currentIntake).coerceAtLeast(0) + 249) / 250
        val isCompleted = currentIntake >= target
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .background(DarkSurface, RoundedCornerShape(24.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(24.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(TurquoisePrimary.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💧",
                    fontSize = 18.sp
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isCompleted) "Vous y êtes !" else "Sur la bonne voie !",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                )
                Text(
                    text = if (isCompleted) "Objectif quotidien atteint avec succès !" else "Encore environ $glassesLeft verres pour atteindre l'objectif.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Addition buttons ("Boutons '+' pour ajouter 250ml")
        Text(
            text = "Ajouter de l'eau",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = TextLight.copy(alpha = 0.8f),
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main addition button: +250ml (Primary action, rounded pill)
            Button(
                onClick = { viewModel.addWater(250) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TurquoisePrimary,
                    contentColor = OnTurquoisePrimary
                ),
                shape = RoundedCornerShape(28.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .weight(1.5f)
                    .height(54.dp)
                    .testTag("add_250_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.WaterDrop,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "+ 250 ml",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Secondary option: +100ml
            OutlinedButton(
                onClick = { viewModel.addWater(100) },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TurquoisePrimary
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = BorderColor
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("add_100_button")
            ) {
                Text(
                    text = "+ 100 ml",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            // Secondary option: +500ml
            OutlinedButton(
                onClick = { viewModel.addWater(500) },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TurquoisePrimary
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = BorderColor
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("add_500_button")
            ) {
                Text(
                    text = "+ 500 ml",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Reset Button (Bouton de reset)
        Button(
            onClick = { viewModel.resetWater() },
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkSurface,
                contentColor = TextLight.copy(alpha = 0.85f)
            ),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(1.dp, BorderColor, RoundedCornerShape(28.dp))
                .testTag("reset_button")
        ) {
            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Réinitialiser la journée",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // History Log Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Historique d'aujourd'hui",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
            )
            Text(
                text = "${history.size} gorgées",
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // History Log List / Empty State
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(DarkSurface, RoundedCornerShape(24.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(24.dp))
                .padding(4.dp)
        ) {
            if (history.isEmpty()) {
                // Beautiful Empty State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocalDrink,
                        contentDescription = null,
                        tint = TurquoiseSecondary.copy(alpha = 0.15f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aucune gorgée enregistrée",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextLight.copy(alpha = 0.8f)
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ajoutez de l'eau pour commencer votre suivi !",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Interactive Scrollable List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = history,
                        key = { it.id }
                    ) { log ->
                        HistoryLogItem(
                            log = log,
                            onDelete = { viewModel.deleteLog(log) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedWaterCircle(
    currentIntake: Int,
    target: Int,
    modifier: Modifier = Modifier
) {
    val progressFraction = (currentIntake.toFloat() / target.toFloat()).coerceIn(0f, 1f)

    // Smoothly animate the target progress fraction using a Spring
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "water_level_spring"
    )

    // Infinite wave phase multiplier for animated ripples
    val infiniteTransition = rememberInfiniteTransition(label = "wave_phase_inf")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_offset_f"
    )

    Box(
        modifier = modifier
            .size(240.dp)
            .shadow(20.dp, CircleShape, spotColor = TurquoisePrimary, ambientColor = TurquoisePrimary)
            .border(4.dp, Brush.linearGradient(listOf(TurquoisePrimary, TurquoiseSecondary)), CircleShape)
            .padding(6.dp)
            .clip(CircleShape)
            .background(DarkSurface),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Crop canvas drawing to perfect circle
            val clipPath = Path().apply {
                addOval(Rect(0f, 0f, width, height))
            }

            clipPath(clipPath) {
                // Draw background of water sphere
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DarkSurface,
                            DarkSurfaceVariant
                        )
                    )
                )

                // Draw secondary wave (in back, lighter, transparent)
                val wavePathBehind = Path()
                val targetWaterHeight = height * (1f - animatedProgress)

                wavePathBehind.moveTo(0f, height)
                for (x in 0..width.toInt() step 6) {
                    // Offset phase slightly for back wave to create offset dimensional depth
                    val angle = (x.toFloat() / width) * 2 * Math.PI.toFloat() + waveOffset + Math.PI.toFloat() / 2f
                    val y = targetWaterHeight + sin(angle).toFloat() * 12.dp.toPx()
                    wavePathBehind.lineTo(x.toFloat(), y)
                }
                wavePathBehind.lineTo(width, height)
                wavePathBehind.close()

                drawPath(
                    path = wavePathBehind,
                    color = TurquoiseSecondary.copy(alpha = 0.35f)
                )

                // Draw primary wave (in front, full turquoise gradient)
                val wavePathFront = Path()
                wavePathFront.moveTo(0f, height)
                for (x in 0..width.toInt() step 6) {
                    val angle = (x.toFloat() / width) * 2 * Math.PI.toFloat() + waveOffset
                    val y = targetWaterHeight + sin(angle).toFloat() * 9.dp.toPx()
                    wavePathFront.lineTo(x.toFloat(), y)
                }
                wavePathFront.lineTo(width, height)
                wavePathFront.close()

                drawPath(
                    path = wavePathFront,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            TurquoiseSecondary,
                            TurquoisePrimary
                        )
                    )
                )
            }
        }

        // Overlay Text Info (Dynamically adjust text color based on level depth for high-contrast accessibility!)
        val textOnWater = animatedProgress > 0.48f
        val textColor = if (textOnWater) DarkBackground else TextLight
        val mutedTextColor = if (textOnWater) DarkBackground.copy(alpha = 0.7f) else TextMuted
        val accentColor = if (textOnWater) DarkBackground else TurquoiseAccent

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.WaterDrop,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Current Intake (Large, light text)
            Text(
                text = String.format("%,d", currentIntake),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-1).sp
                ),
                color = textColor,
                textAlign = TextAlign.Center
            )

            // Target Ml (Muted tracking label)
            Text(
                text = "SUR ${String.format("%,d", target)} ML",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = mutedTextColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Percentage Done Badge
            val percentVal = (progressFraction * 100).toInt()
            val badgeBgColor = if (textOnWater) DarkBackground.copy(alpha = 0.15f) else TurquoisePrimary.copy(alpha = 0.12f)
            val badgeBorderColor = if (textOnWater) DarkBackground.copy(alpha = 0.3f) else TurquoisePrimary.copy(alpha = 0.25f)
            
            Box(
                modifier = Modifier
                    .background(badgeBgColor, RoundedCornerShape(12.dp))
                    .border(1.dp, badgeBorderColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (currentIntake >= target) "COMPLÉTÉ 🎉" else "$percentVal% ATTEINT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = accentColor
                )
            }
        }
    }
}

@Composable
fun HistoryLogItem(
    log: WaterLog,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("log_item_${log.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Cute Glass Water cup styled layout
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(TurquoisePrimary.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WaterDrop,
                        contentDescription = null,
                        tint = TurquoiseAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "+ ${log.amountMl} ml",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                    )
                    Text(
                        text = "Ajouté à ${log.formattedTime}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted
                        )
                    )
                }
            }

            // Quick Delete option
            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_log_btn_${log.id}")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Supprimer cette gorgée",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
