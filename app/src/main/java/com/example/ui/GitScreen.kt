package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.git.DiffLineType
import com.example.git.GitDiffLine
import com.example.model.GitCommitEntity
import com.example.model.WorkspaceFileEntity
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DeepBackground
import com.example.ui.theme.GreenNeon
import com.example.ui.theme.PinkNeon
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkVariant
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GitScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val files by viewModel.files.collectAsState()
    val commits by viewModel.commits.collectAsState()
    val branches by viewModel.branches.collectAsState()
    val remote by viewModel.remote.collectAsState()
    val clipboard = LocalClipboardManager.current

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Staging & Commit, 1: History, 2: Diff Viewer
    var commitTitle by remember { mutableStateOf("") }
    var commitDescription by remember { mutableStateOf("") }
    var showBranchDialog by remember { mutableStateOf(false) }
    var showNewBranchDialog by remember { mutableStateOf(false) }
    var diffTargetFile by remember { mutableStateOf<WorkspaceFileEntity?>(null) }
    var selectedCommitDetail by remember { mutableStateOf<GitCommitEntity?>(null) }
    var historySearchQuery by remember { mutableStateOf("") }

    val currentBranch = branches.firstOrNull { it.isCurrent }?.name ?: "main"
    val stagedFiles = files.filter { it.gitStatus == "STAGED" }
    val unstagedFiles = files.filter { it.gitStatus == "MODIFIED" || it.gitStatus == "UNTRACKED" }

    val conventionalPills = listOf("feat:", "fix:", "refactor:", "docs:", "test:", "chore:", "perf:", "style:")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
            .testTag("git_screen")
    ) {
        // Top Git Repository Bar
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxWidth().testTag("git_repo_header_bar")
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Branch Selector Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDarkVariant)
                            .border(1.dp, PinkNeon.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .clickable { showBranchDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("git_branch_selector_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.CallSplit,
                                contentDescription = "Active Branch",
                                tint = PinkNeon,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentBranch,
                                color = PinkNeon,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Push / Pull Remote Sync Controls
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { viewModel.pushToRemote() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PinkNeon.copy(alpha = 0.15f),
                                contentColor = PinkNeon
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("git_quick_push_button")
                        ) {
                            Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Push", modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Push", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Button(
                            onClick = { viewModel.pullFromRemote() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceHighlight,
                                contentColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("git_quick_pull_button")
                        ) {
                            Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Pull", modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pull", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "origin: ${remote?.url?.substringAfterLast("/") ?: "remote-devbox.git"}",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${stagedFiles.size} staged • ${unstagedFiles.size} working",
                        fontSize = 11.sp,
                        color = if (stagedFiles.isNotEmpty()) GreenNeon else TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Sub-navigation Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceDark,
            contentColor = PinkNeon,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PinkNeon,
                    height = 2.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Stage & Commit",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            color = if (selectedTab == 0) PinkNeon else TextMuted
                        )
                        if (stagedFiles.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GreenNeon)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(text = "${stagedFiles.size}", color = DeepBackground, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                modifier = Modifier.testTag("git_tab_staging")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = "Commit History (${commits.size})",
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp,
                        color = if (selectedTab == 1) PinkNeon else TextMuted
                    )
                },
                modifier = Modifier.testTag("git_tab_history")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Text(
                        text = "Diff Inspector",
                        fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp,
                        color = if (selectedTab == 2) PinkNeon else TextMuted
                    )
                },
                modifier = Modifier.testTag("git_tab_diff")
            )
        }

        // Tab Content
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (selectedTab) {
                0 -> {
                    // TAB 0: STAGING & COMMIT UI
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .testTag("git_staging_tab_content"),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Commit Composer Card
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, if (commitTitle.isNotBlank() && stagedFiles.isNotEmpty()) PinkNeon.copy(alpha = 0.5f) else SurfaceHighlight, RoundedCornerShape(12.dp))
                                    .testTag("commit_composer_card")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Commit Composer",
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${commitTitle.length}/50 chars",
                                            fontSize = 11.sp,
                                            color = if (commitTitle.length > 50) Color(0xFFFFB86C) else TextMuted
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Conventional Commits Pills
                                    val pillsScroll = rememberScrollState()
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(pillsScroll)
                                            .padding(bottom = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        conventionalPills.forEach { prefix ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(SurfaceDarkVariant)
                                                    .border(1.dp, SurfaceHighlight, RoundedCornerShape(4.dp))
                                                    .clickable {
                                                        commitTitle = if (commitTitle.startsWith(prefix)) {
                                                            commitTitle
                                                        } else {
                                                            "$prefix ${commitTitle.substringAfter(": ").trimStart()}"
                                                        }
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                                    .testTag("pill_$prefix")
                                            ) {
                                                Text(
                                                    text = prefix,
                                                    color = CyanNeon,
                                                    fontSize = 11.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }

                                    // Commit Subject input
                                    OutlinedTextField(
                                        value = commitTitle,
                                        onValueChange = { commitTitle = it },
                                        placeholder = { Text("e.g. feat: add local code server SSL certs", color = TextMuted) },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PinkNeon,
                                            unfocusedBorderColor = SurfaceHighlight,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        modifier = Modifier.fillMaxWidth().testTag("commit_title_field")
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Optional Commit Body / Extended Description
                                    OutlinedTextField(
                                        value = commitDescription,
                                        onValueChange = { commitDescription = it },
                                        placeholder = { Text("Optional description / extended release notes...", color = TextMuted) },
                                        maxLines = 3,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PinkNeon,
                                            unfocusedBorderColor = SurfaceHighlight,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        modifier = Modifier.fillMaxWidth().testTag("commit_desc_field")
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {
                                            if (commitTitle.isNotBlank()) {
                                                viewModel.commitChanges(commitTitle.trim(), commitDescription.trim())
                                                commitTitle = ""
                                                commitDescription = ""
                                            }
                                        },
                                        enabled = commitTitle.isNotBlank() && stagedFiles.isNotEmpty(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = GreenNeon,
                                            contentColor = DeepBackground,
                                            disabledContainerColor = SurfaceHighlight,
                                            disabledContentColor = TextMuted
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("submit_commit_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Commit, contentDescription = "Commit", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (stagedFiles.isEmpty()) "No Changes Staged" else "Commit ${stagedFiles.size} Staged ${if (stagedFiles.size == 1) "File" else "Files"}",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Staged Changes Section Header
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "STAGED CHANGES",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenNeon,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(GreenNeon.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "${stagedFiles.size}", color = GreenNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (stagedFiles.isNotEmpty()) {
                                    Text(
                                        text = "Unstage All",
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .clickable { viewModel.unstageAllFiles() }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                            .testTag("unstage_all_button")
                                    )
                                }
                            }
                        }

                        if (stagedFiles.isEmpty()) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp, horizontal = 12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = "No files staged for commit.", fontSize = 12.sp, color = TextMuted)
                                        Text(text = "Click 'Stage' on working files below to prepare your commit.", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                            }
                        } else {
                            items(stagedFiles) { file ->
                                StagedFileCard(
                                    file = file,
                                    onUnstage = { viewModel.unstageFile(file.id) },
                                    onInspectDiff = {
                                        diffTargetFile = file
                                        selectedTab = 2
                                    }
                                )
                            }
                        }

                        // Working Tree / Unstaged Section Header
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "WORKING DIRECTORY CHANGES",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFB86C),
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFFFB86C).copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "${unstagedFiles.size}", color = Color(0xFFFFB86C), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (unstagedFiles.isNotEmpty()) {
                                    Text(
                                        text = "Stage All (+)",
                                        color = CyanNeon,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .clickable { viewModel.stageAllFiles() }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                            .testTag("stage_all_button")
                                    )
                                }
                            }
                        }

                        if (unstagedFiles.isEmpty()) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp, horizontal = 12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = "Working tree clean.", fontSize = 12.sp, color = GreenNeon)
                                        Text(text = "No untracked or modified files in current branch.", fontSize = 11.sp, color = TextMuted)
                                    }
                                }
                            }
                        } else {
                            items(unstagedFiles) { file ->
                                WorkingFileCard(
                                    file = file,
                                    onStage = { viewModel.stageFile(file.id) },
                                    onDiscard = { viewModel.discardFileChanges(file.id) },
                                    onInspectDiff = {
                                        diffTargetFile = file
                                        selectedTab = 2
                                    }
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: COMMIT HISTORY & INSPECT
                    val filteredCommits = if (historySearchQuery.isBlank()) {
                        commits
                    } else {
                        commits.filter {
                            it.message.contains(historySearchQuery, ignoreCase = true) ||
                                    it.id.contains(historySearchQuery, ignoreCase = true) ||
                                    it.author.contains(historySearchQuery, ignoreCase = true)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .testTag("git_history_tab_content")
                    ) {
                        // Search / Filter Input
                        OutlinedTextField(
                            value = historySearchQuery,
                            onValueChange = { historySearchQuery = it },
                            placeholder = { Text("Filter commits by message, hash, or author...", color = TextMuted) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextMuted, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                if (historySearchQuery.isNotBlank()) {
                                    IconButton(onClick = { historySearchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PinkNeon,
                                unfocusedBorderColor = SurfaceHighlight,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("history_search_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredCommits) { commit ->
                                val isSelected = selectedCommitDetail?.id == commit.id
                                CommitTimelineItem(
                                    commit = commit,
                                    isSelected = isSelected,
                                    onClick = {
                                        selectedCommitDetail = if (isSelected) null else commit
                                    },
                                    onCopySha = {
                                        clipboard.setText(AnnotatedString(commit.id))
                                        viewModel.showMessage("Copied commit SHA ${commit.id}")
                                    },
                                    onRevert = {
                                        viewModel.revertCommit(commit)
                                    }
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: INTERACTIVE DIFF INSPECTOR
                    val targetFile = diffTargetFile ?: stagedFiles.firstOrNull() ?: unstagedFiles.firstOrNull() ?: files.firstOrNull()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                            .testTag("git_diff_tab_content")
                    ) {
                        // File Selector Dropdown Strip
                        val diffFilesScroll = rememberScrollState()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(diffFilesScroll)
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            files.forEach { file ->
                                val isCurrentTarget = targetFile?.id == file.id
                                val isStaged = file.gitStatus == "STAGED"
                                val isModified = file.gitStatus == "MODIFIED" || file.gitStatus == "UNTRACKED"
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isCurrentTarget) SurfaceHighlight else SurfaceDark)
                                        .border(1.dp, if (isCurrentTarget) CyanNeon else Color.Transparent, RoundedCornerShape(6.dp))
                                        .clickable { diffTargetFile = file }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .testTag("diff_selector_${file.name}")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = file.name,
                                            fontSize = 12.sp,
                                            color = if (isCurrentTarget) CyanNeon else TextPrimary,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = if (isCurrentTarget) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (isStaged) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = "STAGED", color = GreenNeon, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        } else if (isModified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = "MOD", color = Color(0xFFFFB86C), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        if (targetFile != null) {
                            // Diff Viewer Header
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "diff --git a/${targetFile.name} b/${targetFile.name}",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = "Status: ${targetFile.gitStatus}",
                                            fontSize = 11.sp,
                                            color = if (targetFile.gitStatus == "STAGED") GreenNeon else Color(0xFFFFB86C),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            if (targetFile.gitStatus == "STAGED") {
                                                viewModel.unstageFile(targetFile.id)
                                            } else {
                                                viewModel.stageFile(targetFile.id)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (targetFile.gitStatus == "STAGED") SurfaceHighlight else GreenNeon,
                                            contentColor = if (targetFile.gitStatus == "STAGED") TextPrimary else DeepBackground
                                        ),
                                        shape = RoundedCornerShape(4.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (targetFile.gitStatus == "STAGED") "Unstage File" else "Stage File (+)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Diff Lines Renderer
                            val sampleOldText = "// Initial baseline implementation\nconst PORT = 8080;\nconsole.log('Server init');"
                            val diffLines = viewModel.gitManager.computeDiff(sampleOldText, targetFile.content.take(800))

                            Card(
                                colors = CardDefaults.cardColors(containerColor = DeepBackground),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .border(1.dp, SurfaceHighlight, RoundedCornerShape(8.dp))
                            ) {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                ) {
                                    items(diffLines) { diffLine ->
                                        DiffLineRow(diffLine)
                                    }
                                }
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Select a file above to inspect visual diff.", color = TextMuted)
                            }
                        }
                    }
                }
            }
        }
    }

    // Branch Switcher Dialog
    if (showBranchDialog) {
        AlertDialog(
            onDismissRequest = { showBranchDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Git Branches", fontWeight = FontWeight.Bold, color = TextPrimary)
                    IconButton(onClick = { showBranchDialog = false }, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }
            },
            text = {
                Column {
                    branches.forEach { branch ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (branch.isCurrent) SurfaceHighlight else Color.Transparent)
                                .clickable {
                                    viewModel.switchBranch(branch.name)
                                    showBranchDialog = false
                                }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.CallSplit,
                                    contentDescription = null,
                                    tint = if (branch.isCurrent) PinkNeon else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = branch.name,
                                    color = TextPrimary,
                                    fontWeight = if (branch.isCurrent) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            if (branch.isCurrent) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Active", tint = PinkNeon, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            showBranchDialog = false
                            showNewBranchDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkNeon, contentColor = DeepBackground),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().testTag("open_new_branch_dialog_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "New Branch", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Create New Branch", fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {},
            containerColor = SurfaceDark
        )
    }

    // New Branch Creation Dialog
    if (showNewBranchDialog) {
        var branchName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNewBranchDialog = false },
            title = { Text("Create New Branch", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column {
                    Text("Branch will be created from '$currentBranch':", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = branchName,
                        onValueChange = { branchName = it },
                        placeholder = { Text("feat/mobile-terminal-tabs", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PinkNeon,
                            unfocusedBorderColor = SurfaceHighlight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("create_branch_text_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (branchName.isNotBlank()) {
                            viewModel.createBranch(branchName.trim())
                            showNewBranchDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkNeon, contentColor = DeepBackground),
                    modifier = Modifier.testTag("confirm_branch_creation_btn")
                ) {
                    Text("Create & Switch", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showNewBranchDialog = false }) { Text("Cancel") }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun StagedFileCard(
    file: WorkspaceFileEntity,
    onUnstage: () -> Unit,
    onInspectDiff: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GreenNeon.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .testTag("staged_file_${file.name}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(GreenNeon)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = file.name,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (file.parentPath.isNotEmpty()) "${file.parentPath}/ • +12 -3 lines" else "+12 -3 lines",
                        color = GreenNeon,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onInspectDiff, modifier = Modifier.size(30.dp)) {
                    Icon(imageVector = Icons.Default.Difference, contentDescription = "Inspect Diff", tint = CyanNeon, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(4.dp))
                Button(
                    onClick = onUnstage,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceHighlight,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = "Unstage", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun WorkingFileCard(
    file: WorkspaceFileEntity,
    onStage: () -> Unit,
    onDiscard: () -> Unit,
    onInspectDiff: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceHighlight, RoundedCornerShape(8.dp))
            .testTag("working_file_${file.name}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (file.gitStatus == "UNTRACKED") Color(0xFFFF79C6) else Color(0xFFFFB86C))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = file.name,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = file.gitStatus,
                        color = if (file.gitStatus == "UNTRACKED") Color(0xFFFF79C6) else Color(0xFFFFB86C),
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onInspectDiff, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Difference, contentDescription = "Diff", tint = CyanNeon, modifier = Modifier.size(15.dp))
                }
                IconButton(onClick = onDiscard, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Undo, contentDescription = "Discard Changes", tint = TextMuted, modifier = Modifier.size(15.dp))
                }
                Spacer(modifier = Modifier.width(4.dp))
                Button(
                    onClick = onStage,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanNeon.copy(alpha = 0.2f),
                        contentColor = CyanNeon
                    ),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = "Stage (+)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CommitTimelineItem(
    commit: GitCommitEntity,
    isSelected: Boolean,
    onClick: () -> Unit,
    onCopySha: () -> Unit,
    onRevert: () -> Unit
) {
    val fmt = SimpleDateFormat("MMM dd, HH:mm", Locale.US)
    val title = commit.message.lines().firstOrNull() ?: ""
    val body = commit.message.lines().drop(1).joinToString("\n").trim()

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isSelected) PinkNeon else SurfaceHighlight, RoundedCornerShape(10.dp))
            .testTag("commit_timeline_item_${commit.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(PinkNeon)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        maxLines = if (isSelected) 3 else 1
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceDarkVariant)
                        .clickable { onCopySha() }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = commit.id,
                            color = CyanNeon,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy SHA", tint = TextMuted, modifier = Modifier.size(10.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${commit.author.substringBefore("<").trim()} • ${fmt.format(Date(commit.timestamp))}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = commit.diffSummary,
                    color = GreenNeon,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Expanded Commit Inspection
            if (isSelected) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SurfaceHighlight))
                Spacer(modifier = Modifier.height(8.dp))

                if (body.isNotBlank()) {
                    Text(text = body, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Branch: ${commit.branch}",
                        color = PinkNeon,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Button(
                        onClick = onRevert,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceHighlight,
                            contentColor = Color(0xFFFF7B72)
                        ),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Undo, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Revert Commit", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DiffLineRow(line: GitDiffLine) {
    val bgColor = when (line.type) {
        DiffLineType.ADDED -> Color(0xFF1B3D2F)
        DiffLineType.REMOVED -> Color(0xFF3F1D23)
        DiffLineType.HEADER -> Color(0xFF1E2638)
        DiffLineType.SAME -> Color.Transparent
    }

    val textColor = when (line.type) {
        DiffLineType.ADDED -> GreenNeon
        DiffLineType.REMOVED -> Color(0xFFFF7B72)
        DiffLineType.HEADER -> PurpleNeon
        DiffLineType.SAME -> TextSecondary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(vertical = 1.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Line number gutters
        val lineNoStr = when {
            line.newLineNo != null -> "${line.newLineNo}"
            line.oldLineNo != null -> "${line.oldLineNo}"
            else -> " "
        }
        Text(
            text = lineNoStr.padStart(3),
            color = TextMuted,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.width(28.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = line.lineText,
            color = textColor,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 15.sp
        )
    }
}
