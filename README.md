# SmartBudget SA 1.0

SmartBudget SA 1.0 is a comprehensive personal finance management application designed to help users track expenses, set budget goals, and gain insights into their spending habits through visual reports and gamified achievements.

## Key Features

### 1. Expense Tracking & Categorization
- Log daily expenses with descriptions and amounts.
- Categorize spending into groups like Groceries, Transport, Entertainment, etc.
- Attach receipt images (mocked for gallery selection).

### 2. Budget Goals (Min/Max)
- Set monthly budget limits.
- **New:** Set specific Minimum and Maximum spending goals per category.
- Visual indicators show whether you are "On Target," "Below Minimum," or "Over Maximum" for each category.

### 3. Visual Financial Reports
- **Spending Trends:** Line chart showing recent spending activity.
- **Category Distribution:** Pie chart showing the breakdown of expenses by category.
- **Category Comparison:** Bar chart comparing actual spending against your set min/max goals.
- **Time-Period Selection:** Filter reports by Week, Month, Year, or All time.

### 4. Gamification (Achievements)
- Earn badges for consistent logging and smart spending.
- Badges include:
    - **Budget Beginner:** Logged your first expense.
    - **Consistent Logger:** Logged 5 expenses.
    - **Saving Master:** Spent less than 50% of your total budget.
- View earned badges in your User Profile.

### 5. Custom Features (Part 3 Highlights)
- **Multi-Currency Support:** Change the app's currency symbol (R, $, €, £) in the Profile settings. All financial data updates instantly to reflect your chosen currency.
- **Advanced Expense Search:** Easily find specific transactions using the search bar on the Dashboard. Search by description or category.

### 6. Security
- **Biometric Unlock:** Secure the app using Fingerprint or Face ID via Android BiometricPrompt.
- **Profile Protection:** Password-protected access to user settings and personal data.

## Final POE Requirements Checklist

- [x] **Graph with Min/Max Goals:** Implemented in the Reports screen.
- [x] **Visual Goal Tracking (Past Month):** Implemented via status indicators and bar markers in Reports.
- [x] **Gamification:** Badges system implemented and visible in Profile.
- [x] **Two Own Features:** 1. Multi-Currency Support, 2. Advanced Search.
- [x] **Logging & Comments:** Comprehensive logging added to source code.
- [x] **Automated Testing:** Unit tests for `BudgetManager` logic.
- [x] **App Icon & Assets:** Custom icons used throughout the app.

## Video Demonstration
[Link to Video Presentation](https://your-video-link-here.com)

## Design Considerations
The app utilizes a modern Jetpack Compose UI with a "Night Sky" theme, featuring a stars background and high-contrast Material 3 components for better visibility and aesthetics. Persistence is handled through SharedPreferences with JSON serialization for simplicity and efficiency in a mobile context.

---
Developed for OPSC6311 POE.
