package com.acco.life.service;

import com.acco.life.dto.TransactionCategoryDto;
import com.acco.life.enums.CategoryType;
import com.acco.life.enums.TransactionType;
import com.acco.life.enums.CategoryKeywordMapping;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * description: AI交易分类服务测试类
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@SpringBootTest
public class AiTransactionCategoryServiceTest {

    @Test
    public void testTransactionTypeEnum() {
        // 测试交易类型枚举
        assertEquals(TransactionType.INCOME, TransactionType.getByCode(1));
        assertEquals(TransactionType.EXPENSE, TransactionType.getByCode(2));
        assertEquals(TransactionType.TRANSFER_OUT, TransactionType.getByCode(3));
        assertEquals(TransactionType.TRANSFER_IN, TransactionType.getByCode(4));
        
        assertTrue(TransactionType.INCOME.isIncome());
        assertTrue(TransactionType.EXPENSE.isExpense());
        assertTrue(TransactionType.TRANSFER_OUT.isTransfer());
        assertTrue(TransactionType.TRANSFER_IN.isTransfer());
    }
    
    @Test
    public void testCategoryTypeEnum() {
        // 测试分类类型枚举
        assertEquals(CategoryType.FOOD, CategoryType.getByName("餐饮美食"));
        assertEquals(CategoryType.TRANSPORT, CategoryType.getByName("交通出行"));
        assertEquals(CategoryType.SHOPPING, CategoryType.getByName("购物消费"));
        assertEquals(CategoryType.SALARY, CategoryType.getByName("工资薪金"));
        
        assertTrue(CategoryType.SALARY.isIncome());
        assertTrue(CategoryType.FOOD.isExpense());
        assertTrue(CategoryType.ACCOUNT_TRANSFER.isTransfer());
    }
    
    @Test
    public void testCategoryKeywordMapping() {
        // 测试关键词映射
        CategoryType foodCategory = CategoryKeywordMapping.findCategoryByKeyword("餐饮");
        assertEquals(CategoryType.FOOD, foodCategory);
        
        CategoryType transportCategory = CategoryKeywordMapping.findCategoryByKeyword("地铁");
        assertEquals(CategoryType.TRANSPORT, transportCategory);
        
        CategoryType shoppingCategory = CategoryKeywordMapping.findCategoryByKeyword("超市");
        assertEquals(CategoryType.SHOPPING, shoppingCategory);
        
        CategoryType salaryCategory = CategoryKeywordMapping.findCategoryByKeyword("工资");
        assertEquals(CategoryType.SALARY, salaryCategory);
        
        // 测试不存在的关键词
        CategoryType unknownCategory = CategoryKeywordMapping.findCategoryByKeyword("不存在的关键词");
        assertNull(unknownCategory);
    }
    
    @Test
    public void testCategoryTypeMethods() {
        // 测试分类类型的方法
        List<CategoryType> incomeCategories = CategoryType.getIncomeCategories();
        assertNotNull(incomeCategories);
        assertTrue(incomeCategories.size() > 0);
        
        List<CategoryType> expenseCategories = CategoryType.getExpenseCategories();
        assertNotNull(expenseCategories);
        assertTrue(expenseCategories.size() > 0);
        
        List<CategoryType> transferCategories = CategoryType.getTransferCategories();
        assertNotNull(transferCategories);
        assertTrue(transferCategories.size() > 0);
        
        // 测试根据交易类型获取分类
        List<CategoryType> incomeByType = CategoryType.getByTransactionType(TransactionType.INCOME);
        assertNotNull(incomeByType);
        assertTrue(incomeByType.size() > 0);
        
        List<CategoryType> expenseByType = CategoryType.getByTransactionType(TransactionType.EXPENSE);
        assertNotNull(expenseByType);
        assertTrue(expenseByType.size() > 0);
    }
    
    @Test
    public void testCategoryKeywordMappingMethods() {
        // 测试关键词映射的方法
        List<String> foodKeywords = CategoryKeywordMapping.getKeywordsByCategory(CategoryType.FOOD);
        assertNotNull(foodKeywords);
        assertTrue(foodKeywords.size() > 0);
        assertTrue(foodKeywords.contains("餐饮"));
        
        List<String> transportKeywords = CategoryKeywordMapping.getKeywordsByCategory(CategoryType.TRANSPORT);
        assertNotNull(transportKeywords);
        assertTrue(transportKeywords.size() > 0);
        assertTrue(transportKeywords.contains("地铁"));
        
        List<String> allDescriptions = CategoryKeywordMapping.getAllDescriptions();
        assertNotNull(allDescriptions);
        assertTrue(allDescriptions.size() > 0);
    }
} 