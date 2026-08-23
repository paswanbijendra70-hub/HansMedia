package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.media3.common.Player
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MediaEntity
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun HansMediaSplash(onSplashFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1.1f else 0.8f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2200)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, Color(0xFF1F0D05))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Nepal Inspired Brand Logo Indicator
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .alpha(alpha)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(SaffronPrimary, CrimsonAccent)
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .border(2.dp, GoldStar, RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = "HansMedia Logo",
                    tint = Color.White,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "HansMedia",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldStar,
                    letterSpacing = 2.sp
                ),
                modifier = Modifier.alpha(alpha)
            )

            Text(
                text = "PREMIUM OFFLINE PLAYER",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DarkOnSurface.copy(alpha = 0.7f),
                    letterSpacing = 4.sp
                ),
                modifier = Modifier.alpha(alpha).padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Signature credits
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Made with devotion in Nepal 🇳🇵",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = DarkOnSurface.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.alpha(alpha)
                )
                Text(
                    text = "Created by ASP Studios",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldStar.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.alpha(alpha).padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun HansMediaDashboard(
    viewModel: MediaViewModel,
    onMediaSelected: (MediaEntity) -> Unit,
    onAdminClicked: () -> Unit = {}
) {
    val mediaList by viewModel.mediaList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val showFavoritesOnly by viewModel.showFavoritesOnly.collectAsState()
    val currentMedia by viewModel.currentMedia.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    val categories = listOf("All", "Manglacharan", "Nitya Niyam", "Rameni", "Aarti", "Mantra", "Satsang")

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                // Top header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "HansMedia",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = SaffronPrimary
                            )
                        )
                        Text(
                            text = "Devotional Offline Player",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Favorites filter icon button
                        IconButton(
                            onClick = { viewModel.toggleFavoritesFilter() },
                            modifier = Modifier
                                .testTag("favorites_filter_button")
                                .shadow(2.dp, CircleShape)
                                .background(
                                    if (showFavoritesOnly) CrimsonAccent else MaterialTheme.colorScheme.surfaceVariant,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = if (showFavoritesOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Show Favorites Only",
                                tint = if (showFavoritesOnly) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Admin Panel entry button
                        IconButton(
                            onClick = { onAdminClicked() },
                            modifier = Modifier
                                .testTag("admin_panel_button")
                                .shadow(2.dp, CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Admin Panel",
                                tint = SaffronPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input")
                        .shadow(1.dp, RoundedCornerShape(16.dp)),
                    placeholder = { Text("Search title or artist...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Horizontal Category Selection Row
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSelectedCategory(category) },
                            label = { Text(text = category, fontSize = 12.sp) },
                            modifier = Modifier.testTag("chip_$category"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SaffronPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            }
        },
        bottomBar = {
            // Nepal Studio Signature bar in dashboard footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Made in Nepal 🇳🇵  •  ASP Studios",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (mediaList.isEmpty()) {
                // Empty state view
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicOff,
                        contentDescription = "No Media found",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Devotional Media Published Yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Please tap the Admin Panel (Settings icon) at the top right to upload and publish your favorite video and music tracks from your gallery!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                // Media List View
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 90.dp, top = 8.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(mediaList) { item ->
                        val isCurrent = currentMedia?.id == item.id
                        MediaCard(
                            item = item,
                            isCurrent = isCurrent,
                            isPlaying = isCurrent && isPlaying,
                            onClick = { onMediaSelected(item) },
                            onFavToggle = { viewModel.toggleFavorite(item.id, !item.isFavorite) }
                        )
                    }
                }
            }

            // Floatable mini-player bar if media is loaded
            if (currentMedia != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp, start = 16.dp, end = 16.dp)
                ) {
                    MiniPlayerBar(
                        viewModel = viewModel,
                        onClick = { onMediaSelected(currentMedia!!) }
                    )
                }
            }
        }
    }
}

@Composable
fun MediaCard(
    item: MediaEntity,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onFavToggle: () -> Unit
) {
    val cardBg = if (isCurrent) {
        Brush.horizontalGradient(listOf(SaffronPrimary.copy(alpha = 0.15f), CrimsonAccent.copy(alpha = 0.05f)))
    } else {
        Brush.linearGradient(listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface))
    }
    val cardBorder = if (isCurrent) SaffronPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("media_card_${item.id}")
            .clickable { onClick() }
            .shadow(
                elevation = if (isCurrent) 4.dp else 1.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, cardBorder)
    ) {
        Box(
            modifier = Modifier
                .background(cardBg)
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Thumbnail / Icon box
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(SaffronPrimary.copy(alpha = 0.2f), CrimsonAccent.copy(alpha = 0.2f))
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPlaying) {
                        // Graphic equalizer animation
                        GraphicEqualizerBars()
                    } else {
                        Icon(
                            imageVector = if (isCurrent) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                            contentDescription = "Media item icon",
                            tint = if (isCurrent) SaffronPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Text labels block
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) SaffronPrimary else MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.artist,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        )
                        // Label item category
                        Box(
                            modifier = Modifier
                                .background(
                                    SaffronPrimary.copy(alpha = 0.08f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = item.category,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronPrimary
                            )
                        }
                    }
                }

                // Info columns (Duration & Favoriting)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.durationText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    IconButton(
                        onClick = { onFavToggle() },
                        modifier = Modifier.size(36.dp).testTag("fav_toggle_${item.id}")
                    ) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Toggle Favorite",
                            tint = if (item.isFavorite) CrimsonAccent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Offline Ready Status",
                        tint = GreenOffline,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MiniPlayerBar(
    viewModel: MediaViewModel,
    onClick: () -> Unit
) {
    val currentMedia by viewModel.currentMedia.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val position by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()

    val media = currentMedia ?: return

    val progress = if (duration > 0) position.toFloat() / duration.toFloat() else 0f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .testTag("mini_player_bar")
            .clickable { onClick() }
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.15f))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Small rotating vinyl icon inside miniplayer
                val infiniteTransition = rememberInfiniteTransition(label = "mini_rotate")
                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 8000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "rotation"
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .rotate(if (isPlaying) rotation else 0f)
                        .background(
                            Brush.radialGradient(colors = listOf(Color(0xFF2E2620), Color(0xFF100C08))),
                            CircleShape
                        )
                        .border(1.dp, SaffronPrimary.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(LightBackground, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(Color.Black, CircleShape)
                        )
                    }
                }

                // Text Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = media.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = media.artist,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Controls
                IconButton(
                    onClick = { viewModel.skipPrevious() },
                    modifier = Modifier.testTag("mini_prev_button")
                ) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Skip Previous")
                }

                IconButton(
                    onClick = {
                        if (isPlaying) viewModel.pauseMedia() else viewModel.resumeMedia()
                    },
                    modifier = Modifier
                        .testTag("mini_play_button")
                        .size(36.dp)
                        .background(SaffronPrimary, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play or Pause",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.skipNext() },
                    modifier = Modifier.testTag("mini_next_button")
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Skip Next")
                }
            }

            // Bottom-edge mini player progress track line
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = SaffronPrimary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            )
        }
    }
}

@Composable
fun ExpandedPlayerView(
    viewModel: MediaViewModel,
    onDismiss: () -> Unit
) {
    val currentMedia by viewModel.currentMedia.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val position by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val speed by viewModel.playbackSpeed.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()

    val media = currentMedia ?: return

    var notesText by remember(media.id) { mutableStateOf(media.userNotes) }
    var speedMenuExpanded by remember { mutableStateOf(false) }
    var isVideoMode by remember { mutableStateOf(true) }

    val progress = if (duration > 0) position.toFloat() / duration.toFloat() else 0f

    // Rotation animation for visual vinyl disc
    val infiniteTransition = rememberInfiniteTransition(label = "expanded_rotate")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onDismiss() },
                    modifier = Modifier.testTag("player_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to dashboard")
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PLAYING OFFLINE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                    )
                    Text(
                        text = "HansMedia Player",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleFavorite(media.id, !media.isFavorite) },
                    modifier = Modifier.testTag("player_favorite_button")
                ) {
                    Icon(
                        imageVector = if (media.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Toggle favorite",
                        tint = if (media.isFavorite) CrimsonAccent else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Premium Audio / Video Mode Toggle Switch
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(true to "Video View", false to "Audio Disc").forEach { (mode, label) ->
                    val isSelected = isVideoMode == mode
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) SaffronPrimary else Color.Transparent)
                            .clickable { isVideoMode = mode }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (mode) Icons.Default.Videocam else Icons.Default.MusicNote,
                                contentDescription = label,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Large rotating vinyl visualization (Creative Asymmetric depth block!)
            if (isVideoMode) {
                // Large High-Fidelity ExoPlayer Video Screen view!
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black)
                        .border(1.5.dp, SaffronPrimary, RoundedCornerShape(16.dp))
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = viewModel.player
                                useController = false // Custom overlays & tactile gestures
                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                            }
                        },
                        update = { playerView ->
                            playerView.player = viewModel.player
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Exclusive brand watermark overlay on video
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Row(
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(Icons.Default.LiveTv, contentDescription = "Watermark icon", tint = GoldStar, modifier = Modifier.size(12.dp))
                            Text("HansMedia • 1080p", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Large rotating vinyl visualization (Creative Asymmetric depth block!)
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    // Background aura ring
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .shadow(16.dp, CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        SaffronPrimary.copy(alpha = 0.15f),
                                        CrimsonAccent.copy(alpha = 0.05f),
                                        Color.Transparent
                                    )
                                ),
                                CircleShape
                            )
                    )

                    // The Vinyl Record
                    Box(
                        modifier = Modifier
                            .size(230.dp)
                            .rotate(if (isPlaying) rotation else 0f)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFF382F2A), Color(0xFF130E0B))
                                ),
                                CircleShape
                            )
                            .border(4.dp, WarmSandalwood, CircleShape)
                            .shadow(8.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Grooves inside vinyl disc
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val canvasSize = size.minDimension
                            drawCircle(
                                color = Color.White.copy(alpha = 0.05f),
                                radius = canvasSize / 2.6f,
                                style = Stroke(width = 1.dp.toPx())
                            )
                            drawCircle(
                                color = Color.White.copy(alpha = 0.05f),
                                radius = canvasSize / 3.4f,
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }

                        // Saffron-Crimson Center Hub Label
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    Brush.linearGradient(colors = listOf(SaffronPrimary, CrimsonAccent)),
                                    CircleShape
                                )
                                .border(1.5.dp, GoldStar, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Hub icon",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        // Needle pivot circle
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(LightBackground, CircleShape)
                        )
                    }
                }
            }

            // Info text block
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = media.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = media.artist,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = SaffronPrimary,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Offline ready badge",
                        tint = GreenOffline,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Offline • ${media.category}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = GreenOffline,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Audio Player Timeline Slider
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = progress,
                    onValueChange = {
                        val seekTarget = (it * duration).toLong()
                        viewModel.seekTo(seekTarget)
                    },
                    modifier = Modifier.testTag("player_timeline_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = SaffronPrimary,
                        activeTrackColor = SaffronPrimary,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(position),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = formatTime(duration),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Playback Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Playback speed control
                Box {
                    IconButton(
                        onClick = { speedMenuExpanded = true },
                        modifier = Modifier.testTag("speed_button")
                    ) {
                        Icon(Icons.Default.Speed, contentDescription = "Speed selector")
                    }
                    DropdownMenu(
                        expanded = speedMenuExpanded,
                        onDismissRequest = { speedMenuExpanded = false }
                    ) {
                        listOf(0.5f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { s ->
                            DropdownMenuItem(
                                text = { Text("${s}x" + (if (s == 1.0f) " (Normal)" else "")) },
                                onClick = {
                                    viewModel.setPlaybackSpeed(s)
                                    speedMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { viewModel.skipPrevious() },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("prev_button")
                ) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous Track", modifier = Modifier.size(32.dp))
                }

                // Big circular Play/Pause FAB
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .shadow(8.dp, CircleShape)
                        .background(
                            Brush.radialGradient(colors = listOf(SaffronPrimary, CrimsonAccent)),
                            CircleShape
                        )
                        .clickable {
                            if (isPlaying) viewModel.pauseMedia() else viewModel.resumeMedia()
                        }
                        .testTag("play_pause_fab"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.skipNext() },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("next_button")
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next Track", modifier = Modifier.size(32.dp))
                }

                // Repeat Modes
                val repeatIcon = when (repeatMode) {
                    Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOne
                    else -> Icons.Default.Repeat
                }
                IconButton(
                    onClick = { viewModel.toggleRepeatMode() },
                    modifier = Modifier.testTag("repeat_button")
                ) {
                    Icon(
                        imageVector = repeatIcon,
                        contentDescription = "Toggle repeat",
                        tint = if (repeatMode != Player.REPEAT_MODE_OFF) SaffronPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Notes / Bookmarks Panel (Professional offline feature!)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
                    .shadow(2.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Notes, contentDescription = "Notes Icon", tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                            Text(text = "My Devotional Notes", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }
                        
                        // Status indicator
                        Text(
                            text = "Auto-Saved",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = GreenOffline.copy(alpha = 0.8f)
                        )
                    }

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = {
                            notesText = it
                            viewModel.updateNotes(media.id, it)
                        },
                        placeholder = { Text("Write notes, bookmarks or track thoughts here...", fontSize = 13.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("notes_textfield"),
                        singleLine = false,
                        maxLines = 4,
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaffronPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}

// Format time string to MM:SS or HH:MM:SS
fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

// Graphic Equalizer visual animation when playing music
@Composable
fun GraphicEqualizerBars() {
    val infiniteTransition = rememberInfiniteTransition(label = "eq")
    
    val height1 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(animation = tween(400, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "h1"
    )
    val height2 by infiniteTransition.animateFloat(
        initialValue = 0.1f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(animation = tween(250, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "h2"
    )
    val height3 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(animation = tween(500, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "h3"
    )
    val height4 by infiniteTransition.animateFloat(
        initialValue = 0.15f, targetValue = 0.75f,
        animationSpec = infiniteRepeatable(animation = tween(330, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "h4"
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(modifier = Modifier.weight(1f).fillMaxHeight(height1).background(SaffronPrimary, RoundedCornerShape(2.dp)))
        Box(modifier = Modifier.weight(1f).fillMaxHeight(height2).background(SaffronPrimary, RoundedCornerShape(2.dp)))
        Box(modifier = Modifier.weight(1f).fillMaxHeight(height3).background(SaffronPrimary, RoundedCornerShape(2.dp)))
        Box(modifier = Modifier.weight(1f).fillMaxHeight(height4).background(SaffronPrimary, RoundedCornerShape(2.dp)))
    }
}

@Composable
fun HansMediaAdmin(
    viewModel: MediaViewModel,
    onBack: () -> Unit
) {
    val mediaList by viewModel.mediaList.collectAsState()
    val context = LocalContext.current

    var id by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("Sant Rampal Ji Maharaj") }
    var durationText by remember { mutableStateOf("10:00") }
    var category by remember { mutableStateOf("Satsang") }
    var fileName by remember { mutableStateOf("") }

    var statusMessage by remember { mutableStateOf("") }

    // Active Gallery/File Picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            statusMessage = "Copying media file safely into app storage..."
            val localPath = viewModel.copyMediaFromUri(uri)
            if (localPath != null) {
                fileName = localPath
                val file = java.io.File(localPath)
                if (title.isEmpty()) {
                    title = file.nameWithoutExtension.replace('_', ' ').replace('-', ' ').replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString()
                    }
                }
                statusMessage = "Imported successfully! Ready to publish."
            } else {
                statusMessage = "Failed to copy file from gallery. Try again."
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SaffronPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Admin Panel",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                    )
                    Text(
                        text = "Publish Devotional Content",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Publish New Audio/Video Content",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SaffronPrimary)
                        )

                        // GALLERY SELECTION BUTTON
                        Button(
                            onClick = {
                                // Launch standard content picker to allow selecting videos & audio from device gallery / files
                                filePickerLauncher.launch("*/*")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Movie, contentDescription = "Select from Gallery", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Select Video/Music from Gallery", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = artist,
                            onValueChange = { artist = it },
                            label = { Text("Artist") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = durationText,
                                onValueChange = { durationText = it },
                                label = { Text("Duration (e.g., 28:15)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = id,
                                onValueChange = { id = it },
                                label = { Text("ID (optional)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        // Category Selection Dropdown/Chips
                        Column {
                            Text(
                                text = "Category",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            val categories = listOf("Manglacharan", "Nitya Niyam", "Rameni", "Aarti", "Mantra", "Satsang")
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                categories.forEach { cat ->
                                    val isSelected = category == cat
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) SaffronPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable { category = cat }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = cat,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = fileName,
                            onValueChange = { fileName = it },
                            label = { Text("Source File Path (auto-filled by Gallery, or online URL)") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("e.g. sandhya_aarti.mp4 or https://...") },
                            singleLine = true
                        )

                        if (statusMessage.isNotEmpty()) {
                            Text(
                                text = statusMessage,
                                color = SaffronPrimary,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Button(
                            onClick = {
                                if (title.isEmpty()) {
                                    statusMessage = "Please provide at least a Title."
                                } else if (fileName.isEmpty()) {
                                    statusMessage = "Please select a file from your gallery first."
                                } else {
                                    viewModel.publishNewMedia(
                                        id = id,
                                        title = title,
                                        artist = artist,
                                        durationText = durationText,
                                        category = category,
                                        fileName = fileName
                                    )
                                    statusMessage = "Successfully published content changes!"
                                    // Reset fields
                                    id = ""
                                    title = ""
                                    artist = "Sant Rampal Ji Maharaj"
                                    durationText = "10:00"
                                    fileName = ""
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = "Publish", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Publish & Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Manage Published Content (${mediaList.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SaffronPrimary),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(mediaList) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${item.artist} • ${item.category}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            )
                            Text(
                                text = "Source: ${item.fileName}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)),
                                maxLines = 1
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.deleteMediaItem(item.id)
                                statusMessage = "Removed: ${item.title}"
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Item",
                                tint = CrimsonAccent
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                // Brand signature
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Created by ASP Studios",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = SaffronPrimary)
                    )
                    Text(
                        text = "Made in Nepal 🇳🇵",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
