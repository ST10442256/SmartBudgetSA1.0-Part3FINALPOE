# SmartBudget SA - Architecture & Design

This document describes the architecture, design patterns, and technical decisions used in SmartBudget SA.

## 📐 High-Level Architecture

SmartBudget SA follows modern Android architecture principles with clear separation of concerns:

```
┌─────────────────────────────────────┐
│     Presentation Layer              │
│  (Jetpack Compose UI Screens)       │
│  - Dashboard                        │
│  - ExpenseEntry                     │
│  - Reports                          │
│  - CategoryManagement               │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     Business Logic Layer            │
│  (Managers & ViewModels)            │
│  - BudgetManager                    │
│  - UserManager                      │
│  - BiometricHelper                  │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     Data Layer                      │
│  (Local Persistence)                │
│  - SharedPreferences                │
│  - JSON File Storage                │
│  - Data Models                      │
└─────────────────────────────────────┘
```

## 🏛️ Architectural Layers

### 1. Presentation Layer (UI)

**Technology**: Jetpack Compose with Material 3 Design System

**Key Components**:

```kotlin
// Screens are composable functions
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel())

@Composable
fun ExpenseEntryScreen(
    onExpenseSaved: (Expense) -> Unit,
    onCancel: () -> Unit
)

@Composable
fun ReportsScreen(viewModel: ReportsViewModel = viewModel())
```

**Responsibilities**:
- Display UI to users
- Collect user input
- Handle navigation
- Observe state changes from ViewModels
- Render dynamic data

**Design Pattern**: Unidirectional Data Flow (MVI/MVVM)

```
User Action → ViewModel → State Update → UI Recomposition
```

### 2. Business Logic Layer (Managers)

**Key Classes**:

#### BudgetManager
```kotlin
class BudgetManager {
    // Budget CRUD operations
    fun addBudget(category: String, limit: Double)
    fun updateBudget(budgetId: String, newLimit: Double)
    fun getBudget(category: String): Double
    fun getAllBudgets(): List<BudgetItem>
    
    // Budget calculations
    fun calculateRemainingBudget(category: String): Double
    fun checkBudgetExceeded(category: String): Boolean
    fun getMonthlySpending(category: String): Double
}
```

#### UserManager
```kotlin
class UserManager {
    // User authentication
    fun registerUser(email: String, password: String): Boolean
    fun loginUser(email: String, password: String): Boolean
    fun logoutUser()
    fun isUserAuthenticated(): Boolean
    
    // User profile
    fun getUserProfile(): UserProfile?
    fun updateUserProfile(profile: UserProfile)
    fun updatePassword(oldPassword: String, newPassword: String): Boolean
}
```

#### BiometricHelper
```kotlin
class BiometricHelper(context: Context) {
    fun canAuthenticateWithBiometrics(): Boolean
    fun startBiometricAuthentication(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    )
    fun isBiometricEnabled(): Boolean
    fun disableBiometric()
}
```

**Responsibilities**:
- Implement business rules
- Data validation
- Calculations and aggregations
- Data transformation
- User authentication

### 3. Data Layer

**Data Models**:

```kotlin
data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val category: String,
    val description: String,
    val date: LocalDate,
    val receiptImagePath: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class BudgetItem(
    val id: String = UUID.randomUUID().toString(),
    val category: String,
    val monthlyLimit: Double,
    val createdDate: LocalDate
)

data class UserProfile(
    val userId: String,
    val email: String,
    val fullName: String,
    val createdDate: LocalDate,
    val currency: String = "ZAR"
)
```

**Storage Strategy**:

```kotlin
// SharedPreferences for simple key-value data
class PreferenceManager(context: Context) {
    fun saveUserToken(token: String)
    fun getUserToken(): String?
    fun saveBiometricEnabled(enabled: Boolean)
    fun getBiometricEnabled(): Boolean
}

// JSON files for complex data structures
class JsonDataStorage {
    fun saveExpenses(expenses: List<Expense>)
    fun loadExpenses(): List<Expense>
    
    fun saveBudgets(budgets: List<BudgetItem>)
    fun loadBudgets(): List<BudgetItem>
}
```

**Responsibilities**:
- Local data persistence
- Data serialization/deserialization
- File management
- Data security

## 🔄 Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│ User Interaction                                            │
│ (Tap, Input, Swipe)                                        │
└─────────────┬───────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ Compose UI Event Handler                                    │
│ (onClick, onValueChange, etc.)                             │
└─────────────┬───────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ ViewModel / Manager                                         │
│ - Validate input                                            │
│ - Call business logic                                       │
│ - Update state                                              │
└─────────────┬───────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ Data Layer                                                  │
│ - Persist to SharedPreferences                              │
│ - Persist to JSON files                                     │
└─────────────┬───────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ State Update (Flow/StateFlow)                               │
│ Emit new UI state                                           │
└─────────────┬───────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│ Compose Recomposition                                       │
│ UI reflects new state                                       │
└─────────────────────────────────────────────────────────────┘
```

## 🎯 Design Patterns Used

### 1. Model-View-ViewModel (MVVM)
- UI observes ViewModel state
- ViewModel holds business logic
- Decoupled from UI framework

### 2. Repository Pattern
- Single source of truth for data
- Abstracts data access logic
- Easier to test

### 3. Singleton Pattern
- Managers instantiated once
- Shared across app
- Thread-safe access to data

```kotlin
class BudgetManager private constructor() {
    companion object {
        @Volatile
        private var instance: BudgetManager? = null
        
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: BudgetManager().also { instance = it }
            }
    }
}
```

### 4. Observer Pattern
- StateFlow for reactive updates
- Composables subscribe to state
- Automatic recomposition on changes

```kotlin
class DashboardViewModel : ViewModel() {
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()
    
    // UI observes expenses
    @Composable
    fun ExpenseList() {
        val expenses by viewModel.expenses.collectAsState()
        LazyColumn {
            items(expenses) { expense ->
                ExpenseItem(expense)
            }
        }
    }
}
```

## 🔐 Security Considerations

### 1. Authentication
- Biometric authentication for app access
- Password validation for registration
- Session management

### 2. Data Protection
- Sensitive data not logged
- Encrypted storage for passwords (future)
- No data transmission to external servers

### 3. Permissions
- Biometric permission for authentication
- Camera permission for receipt images
- Requested at runtime on Android 6.0+

## 🧪 Testing Strategy

### Unit Testing
```kotlin
@Test
fun testBudgetCalculation() {
    val manager = BudgetManager()
    manager.addBudget("Food", 1000.0)
    manager.recordExpense("Food", 300.0)
    
    assert(manager.calculateRemainingBudget("Food") == 700.0)
}
```

### Instrumented Testing
```kotlin
@RunWith(AndroidRunner::class)
class ExpenseScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun testExpenseEntryValidation() {
        composeTestRule.setContent {
            ExpenseEntryScreen()
        }
        
        composeTestRule
            .onNodeWithText("Save").performClick()
        
        composeTestRule
            .onNodeWithText("Amount required")
            .assertIsDisplayed()
    }
}
```

## 📊 Database Schema (JSON Structure)

### expenses.json
```json
[
  {
    "id": "uuid-123",
    "amount": 250.50,
    "category": "Food",
    "description": "Lunch at Nandos",
    "date": "2026-05-09",
    "receiptImagePath": "/storage/images/receipt_123.jpg",
    "timestamp": 1715305255000
  }
]
```

### budgets.json
```json
[
  {
    "id": "uuid-456",
    "category": "Food",
    "monthlyLimit": 1500.00,
    "createdDate": "2026-05-01"
  }
]
```

## 🚀 Performance Optimization

### 1. Compose Performance
- Use `LazyColumn` for long lists
- Implement `key()` for list items
- Memoize expensive calculations with `remember`

```kotlin
@Composable
fun ExpenseList(expenses: List<Expense>) {
    val categorizedExpenses = remember(expenses) {
        expenses.groupBy { it.category }
    }
    
    LazyColumn {
        items(
            items = categorizedExpenses.toList(),
            key = { (category, _) -> category }
        ) { (category, items) ->
            CategorySection(category, items)
        }
    }
}
```

### 2. Memory Management
- Properly dispose resources
- Limit kept data in memory
- Clean up old expenses periodically

### 3. I/O Optimization
- Batch file operations
- Use coroutines for I/O
- Implement caching

## 🔮 Future Improvements

### Short Term
- [ ] Cloud backup for data
- [ ] Export to CSV/PDF reports
- [ ] Budget alerts and notifications
- [ ] Recurring expenses
- [ ] Multi-currency support

### Medium Term
- [ ] Room database instead of JSON
- [ ] Synchronization across devices
- [ ] Advanced analytics and trends
- [ ] Custom categories and tags
- [ ] Budget sharing with family

### Long Term
- [ ] Backend API integration
- [ ] Machine learning for expense prediction
- [ ] Real-time notifications
- [ ] Integration with banking APIs
- [ ] Investment tracking

## 📚 Dependencies

### AndroidX
- `androidx.compose:compose-bom`: Jetpack Compose
- `androidx.compose.material3`: Material 3 design
- `androidx.biometric`: Biometric authentication
- `androidx.lifecycle`: ViewModel and lifecycle

### Kotlin
- `kotlin:stdlib-jdk8`: Kotlin standard library
- `kotlinx:coroutines`: Async operations

### Testing
- `junit:junit`: Unit testing framework
- `androidx.test.espresso`: UI testing
- `androidx.compose.ui:ui-test-junit4`: Compose testing

---

**Last Updated**: May 9, 2026

For more information, refer to:
- [README.md](README.md) - Getting started
- [CONTRIBUTING.md](CONTRIBUTING.md) - Contributing guidelines
