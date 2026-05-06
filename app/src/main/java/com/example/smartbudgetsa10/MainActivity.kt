package com.example.smartbudgetsa10

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartbudgetsa10.ui.screens.*
import com.example.smartbudgetsa10.ui.theme.*
import com.example.smartbudgetsa10.util.BiometricHelper
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartBudgetSA10Theme {
                var currentNavState by remember { mutableStateOf(value = NavState.Login) }
                var isUnlocked by remember { mutableStateOf(value = false) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(NightSkyStart, NightSkyEnd),
                            ),
                        )
                ) {
                    StarsBackground()

                    when (currentNavState) {
                        NavState.Login -> LoginScreen(
                            onLoginSuccess = { 
                                currentNavState = NavState.BiometricCheck
                            },
                            onNavigateToRegister = { currentNavState = NavState.Register },
                        )
                        NavState.Register -> RegisterScreen(
                            onRegisterSuccess = { currentNavState = NavState.Login },
                            onNavigateToLogin = { currentNavState = NavState.Login },
                        )
                        NavState.BiometricCheck -> {
                            val biometricAvailable = remember { BiometricHelper.isBiometricAvailable(this@MainActivity) }
                            if (!biometricAvailable || isUnlocked) {
                                MainApp()
                            } else {
                                BiometricUnlockScreen {
                                    isUnlocked = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class NavState {
    Login, Register, BiometricCheck
}

@Composable
fun BiometricUnlockScreen(onSuccess: () -> Unit) {
    val activity = LocalContext.current as AppCompatActivity

    LaunchedEffect(Unit) {
        BiometricHelper.showBiometricPrompt(
            activity = activity,
            onSuccess = onSuccess,
        ) {
            /* Handle error */
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "SmartBudget SA is locked", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(height = 16.dp))
        Button(
            onClick = {
                BiometricHelper.showBiometricPrompt(
                    activity = activity,
                    onSuccess = onSuccess,
                ) {
                    /* Handle error */
                }
            },
        ) {
            Text(text = "Unlock with Biometrics")
        }
    }
}

@Composable
fun MainApp() {
    var currentScreen by remember { mutableStateOf(value = Screen.Dashboard) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black.copy(alpha = 0.5f),
                contentColor = Color.White,
            ) {
                NavigationBarItem(
                    selected = currentScreen == Screen.Dashboard,
                    onClick = { currentScreen = Screen.Dashboard },
                    icon = { Icon(painterResource(id = R.drawable.ic_dashboard), contentDescription = null) },
                    label = { Text(text = stringResource(id = R.string.dashboard_title), color = Color.White) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = Color.White.copy(alpha = 0.7f),
                        indicatorColor = Color.White.copy(alpha = 0.1f),
                    ),
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.AddExpense,
                    onClick = { currentScreen = Screen.AddExpense },
                    icon = { Icon(painterResource(id = R.drawable.ic_add), contentDescription = null) },
                    label = { Text(text = stringResource(id = R.string.add_expense), color = Color.White) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = Color.White.copy(alpha = 0.7f),
                        indicatorColor = Color.White.copy(alpha = 0.1f),
                    ),
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.Reports,
                    onClick = { currentScreen = Screen.Reports },
                    icon = { Icon(painterResource(id = R.drawable.ic_reports), contentDescription = null) },
                    label = { Text(text = stringResource(id = R.string.reports), color = Color.White) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = Color.White.copy(alpha = 0.7f),
                        indicatorColor = Color.White.copy(alpha = 0.1f),
                    ),
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.Categories,
                    onClick = { currentScreen = Screen.Categories },
                    icon = { Icon(painterResource(id = R.drawable.ic_categories), contentDescription = null) },
                    label = { Text(text = stringResource(id = R.string.categories), color = Color.White) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = Color.White.copy(alpha = 0.7f),
                        indicatorColor = Color.White.copy(alpha = 0.1f),
                    ),
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.Profile,
                    onClick = { currentScreen = Screen.Profile },
                    icon = { Icon(painterResource(id = R.drawable.ic_profile), contentDescription = null) },
                    label = { Text(text = stringResource(id = R.string.profile), color = Color.White) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = Color.White.copy(alpha = 0.7f),
                        indicatorColor = Color.White.copy(alpha = 0.1f),
                    ),
                )
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(paddingValues = innerPadding)) {
            when (currentScreen) {
                Screen.Dashboard -> DashboardScreen()
                Screen.AddExpense -> AddExpenseScreen()
                Screen.Reports -> ReportsScreen()
                Screen.Categories -> CategoryScreen()
                Screen.Profile -> ProfileScreen()
            }
        }
    }
}

@Composable
fun StarsBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        repeat(times = 50) {
            val x = Random.nextFloat()
            val y = Random.nextFloat()
            val size = Random.nextInt(from = 1, until = 4).dp
            val alpha = Random.nextFloat()
            
            Box(
                modifier = Modifier
                    .offset(
                        x = (x * 400).dp,
                        y = (y * 800).dp,
                    )
                    .size(size = size)
                    .background(color = StarColor.copy(alpha = alpha), shape = CircleShape),
            )
        }
    }
}

enum class Screen {
    Dashboard, AddExpense, Reports, Categories, Profile
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    SmartBudgetSA10Theme {
        DashboardScreen()
    }
}
