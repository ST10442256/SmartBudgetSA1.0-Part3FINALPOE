# Contributing to SmartBudget SA

Thank you for your interest in contributing to SmartBudget SA! This document provides guidelines and instructions for contributing to the project.

## 📋 Code of Conduct

- Be respectful and inclusive
- Focus on constructive feedback
- Help others learn and grow

## 🐛 Reporting Issues

When reporting a bug, please include:

1. **Description**: Clear description of the issue
2. **Steps to Reproduce**: How to reproduce the bug
3. **Expected Behavior**: What should happen
4. **Actual Behavior**: What actually happens
5. **Environment**: 
   - Android version
   - Device model
   - SmartBudget SA version

Example:
```
Title: Login fails with biometric on Samsung A12

Description:
Biometric login doesn't work on Samsung Galaxy A12 devices.

Steps to Reproduce:
1. Open app
2. Tap biometric login
3. Use fingerprint
4. App crashes

Expected: Login succeeds
Actual: App displays error and closes

Environment:
- Android 12
- Samsung Galaxy A12
- SmartBudget SA v1.0
```

## ✨ Feature Requests

Suggest features by opening an issue with:
- **Title**: Brief feature description
- **Use Case**: Why this feature is needed
- **Proposed Solution**: How it could work
- **Alternatives**: Other approaches considered

## 🔧 Development Setup

1. **Fork and Clone**:
   ```bash
   git clone https://github.com/ST10442256/SmartBudgetSA1.0-Part2OPSC.git
   cd SmartBudgetSA1.0-Part2OPSC
   ```

2. **Create Feature Branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make Changes**: Follow Kotlin conventions and Compose best practices

4. **Test Locally**:
   ```bash
   ./gradlew clean build
   ./gradlew test
   ```

## 📝 Commit Guidelines

Follow conventional commits format:

```
type(scope): brief description

Detailed explanation if needed.

Fixes #123
```

**Types**:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation updates
- `style`: Code style changes
- `refactor`: Code refactoring
- `test`: Test additions/updates
- `ci`: CI/CD configuration

**Examples**:
```
feat(auth): add biometric login retry logic
fix(expenses): resolve category filter crash
docs(readme): add troubleshooting section
test(budget): add unit tests for BudgetManager
```

## ✅ Pull Request Process

1. **Update your branch**: `git pull origin master`
2. **Push changes**: `git push origin feature/your-feature-name`
3. **Open PR** on GitHub with:
   - Clear title describing changes
   - Description of what was changed and why
   - Reference to related issues (#123)
   - Screenshots for UI changes

4. **Respond to feedback**: Address code review comments promptly

5. **Ensure tests pass**: GitHub Actions will run automated checks

## 🎨 Code Style

- **Kotlin**: Follow [Kotlin Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- **Compose**: Use [Compose Best Practices](https://developer.android.com/jetpack/compose/best-practices)
- **Naming**: Use descriptive names for variables, functions, and classes
- **Comments**: Add comments for complex logic

### Example:
```kotlin
// ✅ Good
fun calculateMonthlyBudgetRemaining(categoryId: String): Double {
    val totalSpent = expenses
        .filter { it.categoryId == categoryId && it.isCurrentMonth }
        .sumOf { it.amount }
    return monthlyBudget[categoryId] - totalSpent
}

// ❌ Avoid
fun calc(id: String): Double {
    // calculate remaining
    return total - spent
}
```

## 🧪 Testing Requirements

- Add unit tests for new functions
- Test edge cases and error conditions
- Run tests before submitting PR:
  ```bash
  ./gradlew test
  ```

## 📚 Documentation

- Update README.md for user-facing changes
- Add code comments for complex logic
- Document new public APIs
- Keep CONTRIBUTING.md updated

## 🚀 Release Process

Releases follow semantic versioning (MAJOR.MINOR.PATCH):

- **MAJOR**: Breaking changes
- **MINOR**: New features (backwards compatible)
- **PATCH**: Bug fixes

## ❓ Questions?

- Check existing issues and discussions
- Review the README.md and project documentation
- Open a discussion on GitHub

---

**Thank you for contributing to SmartBudget SA! 🎉**
