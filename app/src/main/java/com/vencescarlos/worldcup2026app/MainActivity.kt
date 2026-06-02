package com.vencescarlos.worldcup2026app



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vencescarlos.worldcup2026app.data.model.PlayerInfo
import com.vencescarlos.worldcup2026app.data.model.SquadItem
import com.vencescarlos.worldcup2026app.data.model.TeamItem
import com.vencescarlos.worldcup2026app.data.repository.FootballRepository
import com.vencescarlos.worldcup2026app.ui.theme.WorldCup2026AppTheme
import com.vencescarlos.worldcup2026app.utils.UiState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults


import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.scale
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.sp
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.saveable.rememberSaveable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WorldCup2026AppTheme {
                WorldCupHomeScreen()
            }
        }
    }
}

@Composable
fun WorldCupHomeScreen() {
    val repository = remember { FootballRepository() }

    val loadingTeamsMessage = stringResource(R.string.loading_teams)
    val emptyTeamsMessage = stringResource(R.string.empty_teams)
    val connectionErrorMessage = stringResource(R.string.connection_error_message)

    var selectedTeam by remember {
        mutableStateOf<TeamItem?>(null)
    }

    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    var teamsState by remember {
        mutableStateOf<UiState<List<TeamItem>>>(UiState.Loading)
    }

    fun loadTeams() {
        teamsState = UiState.Loading
    }

    LaunchedEffect(teamsState) {
        if (teamsState is UiState.Loading) {
            teamsState = try {
                val response = repository.getTeams()

                if (response.response.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(response.response)
                }
            } catch (e: Exception) {
                UiState.Error(connectionErrorMessage)
            }
        }
    }

    AnimatedContent(
        targetState = selectedTeam,
        transitionSpec = {
            if (targetState != null) {
                slideInHorizontally(
                    animationSpec = tween(durationMillis = 350)
                ) { fullWidth -> fullWidth } + fadeIn(
                    animationSpec = tween(durationMillis = 350)
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(durationMillis = 300)
                ) { fullWidth -> -fullWidth / 3 } + fadeOut(
                    animationSpec = tween(durationMillis = 250)
                )
            } else {
                slideInHorizontally(
                    animationSpec = tween(durationMillis = 350)
                ) { fullWidth -> -fullWidth / 3 } + fadeIn(
                    animationSpec = tween(durationMillis = 350)
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(durationMillis = 300)
                ) { fullWidth -> fullWidth } + fadeOut(
                    animationSpec = tween(durationMillis = 250)
                )
            }
        },
        label = "screen_transition"
    ) { currentTeam ->

        if (currentTeam == null) {
            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF2F5FA))
                        .padding(innerPadding)
                ) {
                    HeaderSection()

                    when (val state = teamsState) {
                        is UiState.Loading -> {
                            LoadingContent(message = loadingTeamsMessage)
                        }

                        is UiState.Success -> {
                            TeamSearchBar(
                                searchQuery = searchQuery,
                                onSearchChange = { newValue ->
                                    searchQuery = newValue
                                }
                            )

                            val filteredTeams = state.data.filter { teamItem ->
                                val team = teamItem.team
                                val venue = teamItem.venue

                                searchQuery.isBlank() ||
                                        team?.name.orEmpty().contains(searchQuery, ignoreCase = true) ||
                                        team?.code.orEmpty().contains(searchQuery, ignoreCase = true) ||
                                        team?.country.orEmpty().contains(searchQuery, ignoreCase = true) ||
                                        venue?.name.orEmpty().contains(searchQuery, ignoreCase = true)
                            }

                            if (filteredTeams.isEmpty()) {
                                NoSearchResultsContent(searchQuery = searchQuery)
                            } else {
                                TeamsList(
                                    teams = filteredTeams,
                                    onTeamClick = { team ->
                                        selectedTeam = team
                                    }
                                )
                            }
                        }

                        is UiState.Error -> {
                            ErrorContent(
                                message = state.message,
                                onRetry = {
                                    loadTeams()
                                }
                            )
                        }

                        is UiState.Empty -> {
                            EmptyContent(
                                message = emptyTeamsMessage,
                                onRetry = {
                                    loadTeams()
                                }
                            )
                        }
                    }
                }
            }
        } else {
            PlayersScreen(
                teamItem = currentTeam,
                repository = repository,
                onBack = {
                    selectedTeam = null
                }
            )
        }
    }
}

@Composable
fun HeaderSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(185.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.world_cup_header),
                contentDescription = stringResource(R.string.app_banner_description),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xDD000000)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.main_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = stringResource(R.string.teams_subtitle),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = stringResource(R.string.main_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun TeamSearchBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 14.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            placeholder = {
                Text(text = stringResource(R.string.search_teams_hint))
            },
            leadingIcon = {
                Text(text = "🔎")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.clear_search_button),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF165DAD),
                        modifier = Modifier
                            .clickable {
                                onSearchChange("")
                            }
                            .padding(horizontal = 8.dp)
                    )
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun NoSearchResultsContent(
    searchQuery: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5FA)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.search_no_results_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B1D29)
            )

            Text(
                text = stringResource(R.string.search_no_results_message, searchQuery),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4E5668),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
@Composable
fun TeamsList(
    teams: List<TeamItem>,
    onTeamClick: (TeamItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(teams) { teamItem ->
            TeamCard(
                teamItem = teamItem,
                onClick = {
                    onTeamClick(teamItem)
                }
            )
        }
    }
}

@Composable
fun TeamCard(
    teamItem: TeamItem,
    onClick: () -> Unit
) {
    val team = teamItem.team
    val venue = teamItem.venue

    val notAvailable = stringResource(R.string.not_available)
    val teamNoName = stringResource(R.string.team_no_name)
    val flagDescription = stringResource(R.string.team_flag_description)

    val teamName = team?.name ?: teamNoName
    val teamCode = team?.code ?: notAvailable
    val teamFounded = team?.founded?.toString() ?: notAvailable
    val stadiumName = venue?.name ?: notAvailable

    val teamImageModel: Any = if (team?.logo.isNullOrBlank()) {
        R.drawable.ic_flag_placeholder
    } else {
        team.logo
    }

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isPressed by interactionSource.collectIsPressedAsState()

    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "team_card_press_animation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(cardScale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = teamImageModel,
                contentDescription = team?.name ?: flagDescription,
                placeholder = painterResource(id = R.drawable.ic_flag_placeholder),
                error = painterResource(id = R.drawable.ic_flag_placeholder),
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = teamName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1D29)
                )

                Text(
                    text = stringResource(R.string.team_fifa_code, teamCode),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4E5668)
                )

                Text(
                    text = stringResource(R.string.team_founded, teamFounded),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4E5668)
                )

                Text(
                    text = stringResource(R.string.team_stadium, stadiumName),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4E5668)
                )

                Text(
                    text = stringResource(R.string.see_players),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF165DAD),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun PlayersScreen(
    teamItem: TeamItem,
    repository: FootballRepository,
    onBack: () -> Unit
) {
    val team = teamItem.team

    val playersErrorMessage = stringResource(R.string.players_error_message)
    val loadingPlayersMessage = stringResource(R.string.loading_players)
    val emptyPlayersMessage = stringResource(R.string.empty_players)
    val genericTeamName = stringResource(R.string.team_generic_name)
    val connectionErrorMessage = stringResource(R.string.connection_error_message)

    var playersState by remember {
        mutableStateOf<UiState<List<SquadItem>>>(UiState.Loading)
    }

    fun loadPlayers() {
        playersState = UiState.Loading
    }

    LaunchedEffect(playersState) {
        if (playersState is UiState.Loading) {
            playersState = try {
                val teamId = team?.id ?: 16
                val response = repository.getSquad(teamId)

                if (response.response.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(response.response)
                }
            } catch (e: Exception) {
                UiState.Error(connectionErrorMessage)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F5FA))
                .padding(innerPadding)
        ) {
            PlayersHeader(
                teamName = team?.name ?: genericTeamName,
                teamLogo = team?.logo,
                onBack = onBack
            )

            when (val state = playersState) {
                is UiState.Loading -> {
                    LoadingContent(message = loadingPlayersMessage)
                }

                is UiState.Success -> {
                    val players = state.data.firstOrNull()?.players ?: emptyList()

                    if (players.isEmpty()) {
                        EmptyContent(
                            message = emptyPlayersMessage,
                            onRetry = {
                                loadPlayers()
                            }
                        )
                    } else {
                        PlayersList(players = players)
                    }
                }

                is UiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = {
                            loadPlayers()
                        }
                    )
                }

                is UiState.Empty -> {
                    EmptyContent(
                        message = emptyPlayersMessage,
                        onRetry = {
                            loadPlayers()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PlayersHeader(
    teamName: String,
    teamLogo: String?,
    onBack: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Button(
                onClick = onBack
            ) {
                Text(text = stringResource(R.string.back_to_teams))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = teamLogo,
                    contentDescription = teamName,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = stringResource(R.string.players_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = teamName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PlayersList(
    players: List<PlayerInfo>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(players) { player ->
            PlayerCard(player = player)
        }
    }
}

@Composable
fun PlayerCard(
    player: PlayerInfo
) {
    val notAvailable = stringResource(R.string.not_available)

    val playerPosition = player.position ?: notAvailable
    val playerAge = player.age?.toString() ?: notAvailable
    val playerNumber = player.number?.toString() ?: notAvailable

    val playerImageModel: Any = if (player.photo.isNullOrBlank()) {
        R.drawable.ic_player_placeholder
    } else {
        player.photo!!
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = playerImageModel,
                contentDescription = player.name,
                placeholder = painterResource(id = R.drawable.ic_player_placeholder),
                error = painterResource(id = R.drawable.ic_player_placeholder),
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1D29)
                )

                Text(
                    text = stringResource(R.string.player_position, playerPosition),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4E5668)
                )

                Text(
                    text = stringResource(R.string.player_age, playerAge),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4E5668)
                )

                Text(
                    text = stringResource(R.string.player_number, playerNumber),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4E5668)
                )
            }
        }
    }
}

@Composable
fun LoadingContent(
    message: String
) {
    val infiniteTransition = rememberInfiniteTransition(
        label = "loading_animation"
    )

    val ballScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ball_scale"
    )

    val ballAlpha by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ball_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5FA)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚽",
                fontSize = 44.sp,
                modifier = Modifier
                    .scale(ballScale)
                    .alpha(ballAlpha)
            )

            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B1D29),
                modifier = Modifier.padding(top = 18.dp)
            )

            Text(
                text = "Preparando información del Mundial 2026",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4E5668),
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.error_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = message,
                modifier = Modifier.padding(top = 8.dp)
            )

            Button(
                onClick = onRetry,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = stringResource(R.string.retry_button))
            }
        }
    }
}

@Composable
fun EmptyContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onRetry,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = stringResource(R.string.retry_button))
            }
        }
    }
}