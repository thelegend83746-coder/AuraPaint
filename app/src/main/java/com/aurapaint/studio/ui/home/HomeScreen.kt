package com.aurapaint.studio.ui.home

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurapaint.studio.R
import com.aurapaint.studio.core.AppConfig
import com.aurapaint.studio.core.theme.*
import com.aurapaint.studio.project.ProjectMetadata
import com.aurapaint.studio.project.ProjectRepository
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    repository: ProjectRepository,
    onOpenProject: (projectId: String) -> Unit,
    onOpenSettings: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val projects by repository.projects.collectAsState()

    var showNewDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var filterFavoritesOnly by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        repository.refreshProjects()
    }

    val filteredProjects = remember(projects, searchQuery, filterFavoritesOnly) {
        projects.filter {
            it.name.contains(searchQuery, ignoreCase = true) &&
                    (!filterFavoritesOnly || it.isFavorite)
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewDialog = true },
                containerColor = ElectricBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Artwork", modifier = Modifier.size(28.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // App Bar Header with Original Mascot Logo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Original Mascot Logo
                    Image(
                        painter = painterResource(id = R.drawable.ic_aurapaint_logo),
                        contentDescription = "AuraPaint Logo",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = AppConfig.APP_NAME,
                                style = Typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = ElectricBlue.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "v${AppConfig.APP_VERSION}",
                                    color = ElectricBlue,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = AppConfig.APP_TAGLINE,
                            style = Typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Search Bar & Filter Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search artworks...", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = PanelBorder
                    )
                )

                // Favorite toggle filter button
                IconButton(
                    onClick = { filterFavoritesOnly = !filterFavoritesOnly },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (filterFavoritesOnly) VioletAccent.copy(alpha = 0.2f) else PanelSurface)
                        .border(1.dp, if (filterFavoritesOnly) VioletAccent else PanelBorder, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = if (filterFavoritesOnly) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorites",
                        tint = if (filterFavoritesOnly) VioletAccent else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Projects Grid or Empty State
            if (filteredProjects.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_aurapaint_logo),
                            contentDescription = null,
                            modifier = Modifier.size(96.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No artworks found" else "No artworks yet",
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Start sketching, painting, or animating your first creation!",
                            style = Typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { showNewDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Create Artwork", color = Color.White)
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredProjects, key = { it.id }) { project ->
                        ProjectCard(
                            project = project,
                            repository = repository,
                            onClick = { onOpenProject(project.id) },
                            onDelete = {
                                coroutineScope.launch {
                                    repository.deleteProject(project.id)
                                }
                            },
                            onDuplicate = {
                                coroutineScope.launch {
                                    repository.duplicateProject(project.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showNewDialog) {
        NewArtworkDialog(
            onDismiss = { showNewDialog = false },
            onCreate = { name, width, height, dpi, isAnim ->
                showNewDialog = false
                coroutineScope.launch {
                    val created = repository.createNewProject(name, width, height, dpi, isAnim)
                    onOpenProject(created.id)
                }
            }
        )
    }
}

@Composable
fun ProjectCard(
    project: ProjectMetadata,
    repository: ProjectRepository,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val projectDir = remember(project.id) { repository.getProjectDir(project.id) }
    val thumbnailFile = remember(projectDir) { File(projectDir, "thumbnail.png") }

    val thumbnailBitmap = remember(project.modifiedAt) {
        if (thumbnailFile.exists()) {
            BitmapFactory.decodeFile(thumbnailFile.absolutePath)
        } else null
    }

    val formattedDate = remember(project.modifiedAt) {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(project.modifiedAt))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, PanelBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = PanelSurface
    ) {
        Column {
            // Thumbnail Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(CanvasWorkspace),
                contentAlignment = Alignment.Center
            ) {
                if (thumbnailBitmap != null) {
                    Image(
                        bitmap = thumbnailBitmap.asImageBitmap(),
                        contentDescription = project.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = PanelBorder,
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Animation badge if animation project
                if (project.isAnimation) {
                    Surface(
                        color = VioletAccent,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "ANIMATION",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Info Area
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = project.name,
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(PanelSurfaceElevated)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Duplicate Artwork", color = TextPrimary) },
                                onClick = {
                                    showMenu = false
                                    onDuplicate()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TextSecondary)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete", color = StatusError) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = StatusError)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${project.width} × ${project.height} px • ${project.layerCount} layers",
                    style = Typography.bodyMedium,
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Text(
                    text = formattedDate,
                    style = Typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }
}
