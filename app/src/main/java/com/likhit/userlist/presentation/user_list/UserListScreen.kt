@file:OptIn(ExperimentalMaterial3Api::class)

package com.likhit.userlist.presentation.user_list

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.likhit.userlist.data.remote.model.User
import com.likhit.userlist.presentation.user_list.components.UserItem
import com.likhit.userlist.presentation.utils.UIState
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun UserListScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: UserListViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeightPx = with(density){
        configuration.screenHeightDp.dp.toPx()
    }
    val estimatedItemHeight = with(density){
        235.dp.toPx()
    }
    val visibleItemCount = (screenHeightPx / estimatedItemHeight).toInt()

    val users = viewModel.users.collectAsState()
    val uiState = viewModel.uiState.collectAsState()

    val isSearchBarVisible = viewModel.isSearchBarVisible.collectAsState()
    val searchQuery = viewModel.searchQuery.collectAsState()
    val lazyListState = rememberLazyListState()

    UserListScreen(
        modifier = modifier,
        users = users.value,
        uiState = uiState.value,
        isSearchBarVisible = isSearchBarVisible.value,
        onToggleSearchBar = viewModel::toggleSearchBar,
        searchQuery = searchQuery.value,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onRefresh = viewModel::refreshUsers,
        context = context,
        focusManager = focusManager,
        keyboardController = keyboardController,
        visibleItemCount = visibleItemCount,
        lazyListState = lazyListState
    )
}

@Composable
fun UserListScreen(
    modifier: Modifier = Modifier,
    users: List<User>,
    uiState: UIState,
    isSearchBarVisible: Boolean,
    onToggleSearchBar: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    context: Context,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
    visibleItemCount: Int,
    lazyListState: LazyListState,
) {
    val isScrollingUp = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
                    || lazyListState.firstVisibleItemScrollOffset > 0
        }
    }

    var isFirstLoad by remember {
        mutableStateOf(true)
    }

    Scaffold(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Users",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        modifier = Modifier
                            .clickable {
                                onToggleSearchBar()
                            }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedVisibility(
                        visible = isSearchBarVisible && !isScrollingUp.value,
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut(),
                        modifier = Modifier.zIndex(1f)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            label = {
                                Text(
                                    text = "Search"
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Icon"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(15.dp)
                        )
                    }
                }

                if (uiState == UIState.LOADING) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                        return@Scaffold
                    }
                } else if (users.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "User does not exists")
                    }
                }

                PullToRefreshBox(
                    isRefreshing = uiState == UIState.REFRESHING,
                    onRefresh = onRefresh,
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        state = lazyListState,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        itemsIndexed(users) { index, user ->
                            val shouldAnimate = isFirstLoad && index < visibleItemCount

                            val offsetX = remember {
                                Animatable(
                                    if(isFirstLoad) -1000f else 0f
                                )
                            }

                            LaunchedEffect(key1 = user.id, key2 = isFirstLoad) {
                                if(shouldAnimate){
                                    delay(10L)
                                    offsetX.animateTo(
                                        targetValue = 0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        )
                                    )

                                    if(index == visibleItemCount - 1){
                                        isFirstLoad = false
                                    }
                                }
                            }

                            UserItem(
                                context = context,
                                user = user,
                                modifier = Modifier
                                    .offset {
                                        IntOffset(
                                            offsetX.value.roundToInt(),
                                            0
                                        )
                                    }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}