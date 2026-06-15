package com.example.smartbudgetsa10

import android.content.Context
import android.content.SharedPreferences
import com.example.smartbudgetsa10.util.BudgetManager
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class BudgetManagerTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockPrefs: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs)
        `when`(mockPrefs.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
    }

    @Test
    fun testCurrencySetting() {
        // Given a specific currency
        val currency = "$"
        
        // When setting the currency in BudgetManager
        BudgetManager.setCurrency(mockContext, currency)
        
        // Then verify that the correct preference key and value were saved
        verify(mockEditor).putString("currency_symbol", currency)
    }

    @Test
    fun testSaveCategory() {
        // Given a new category name
        val category = "Business"
        // Mock getCategories to return an existing list
        `when`(mockPrefs.getString(eq("categories"), isNull())).thenReturn("Food,Rent")
        
        // When saving a category
        BudgetManager.saveCategory(mockContext, category)
        
        // Then verify it tried to save the updated list containing the new category
        verify(mockEditor).putString(eq("categories"), contains(category))
    }
}
