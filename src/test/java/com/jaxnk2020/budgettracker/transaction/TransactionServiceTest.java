package com.jaxnk2020.budgettracker.transaction;

import com.jaxnk2020.budgettracker.monthlybudget.MonthlyBudget;
import com.jaxnk2020.budgettracker.monthlybudget.MonthlyBudgetService;
import com.jaxnk2020.budgettracker.user.ApplicationUser;
import com.jaxnk2020.budgettracker.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {
    @InjectMocks
    TransactionService transactionService;

    @Mock
    TransactionRepository transactionRepository;
    @Mock
    MonthlyBudgetService monthlyBudgetService;
    @Mock
    UserService userService;

    ApplicationUser user1;
    ApplicationUser user2;

    MonthlyBudget monthlyBudget1;
    MonthlyBudget monthlyBudget2;
    MonthlyBudget monthlyBudget3;

    List<MonthlyBudget> budgets1;
    List<MonthlyBudget> budgets2;

    List<Transaction> transactions1;
    List<Transaction> transactions2;
    List<Transaction> transactions3;

    void initializeBudgetsAndUsers() {
        user1 = new ApplicationUser();
        user2 = new ApplicationUser();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        monthlyBudget1 = new MonthlyBudget();
        monthlyBudget2 = new MonthlyBudget();
        monthlyBudget3 = new MonthlyBudget();

        budgets1 = new ArrayList<>();
        budgets2 = new ArrayList<>();

        user1.setUserId(1);
        user1.setUsername("bioround");
        user1.setFirstname("Nikhil");
        user1.setLastname("Kim");
        user1.setPassword(bCryptPasswordEncoder.encode("Password"));
        user1.setDateJoined(LocalDate.now());

        monthlyBudget1.setUser(user1);
        monthlyBudget1.setMonth(3);
        monthlyBudget1.setYear(2021);
        monthlyBudget1.setBudgetGoal(3000);
        monthlyBudget1.setBudgetId(1);

        monthlyBudget2.setUser(user1);
        monthlyBudget2.setMonth(4);
        monthlyBudget2.setYear(2021);
        monthlyBudget2.setBudgetGoal(2500);
        monthlyBudget2.setBudgetId(2);

        budgets1.add(monthlyBudget1);
        budgets1.add(monthlyBudget2);
        user1.setBudgets(budgets1);

        user2.setUserId(2);
        user2.setUsername("jaxnk2020");
        user2.setFirstname("Jackson");
        user2.setLastname("Suri");
        user2.setPassword(bCryptPasswordEncoder.encode("Password"));
        user2.setDateJoined(LocalDate.now());

        monthlyBudget3.setUser(user2);
        monthlyBudget3.setMonth(4);
        monthlyBudget3.setYear(2021);
        monthlyBudget3.setBudgetGoal(3500);
        monthlyBudget3.setBudgetId(3);

        budgets2.add(monthlyBudget3);
        user2.setBudgets(budgets2);
    }

    void initializeTransactions() {
        transactions1 = new ArrayList<>();
        transactions2 = new ArrayList<>();
        transactions3 = new ArrayList<>();
        long i = 1;

        transactions1.add(new Transaction(i++, "1",500, "expense", false, LocalDateTime.now().minusDays(5), monthlyBudget1, user1));
        transactions1.add(new Transaction(i++,"2",100, "income", true, LocalDateTime.now().minusDays(10), monthlyBudget1, user1));

        monthlyBudget1.setTransactions(transactions1);

        transactions2.add(new Transaction(i++,"3",600, "expense", false, LocalDateTime.now().plusDays(5), monthlyBudget2, user1));
        transactions2.add(new Transaction(i++,"4",100, "expense", false, LocalDateTime.now().plusDays(6), monthlyBudget2, user1));
        transactions2.add(new Transaction(i++,"5",70, "expense", false, LocalDateTime.now().plusDays(6), monthlyBudget2, user1));
        transactions2.add(new Transaction(i++,"6",25, "expense", false, LocalDateTime.now().plusDays(10), monthlyBudget2, user1));
        transactions2.add(new Transaction(i++,"7",50, "expense", false, LocalDateTime.now().plusDays(20), monthlyBudget2, user1));
        transactions2.add(new Transaction(i++,"8",25, "expense", true, LocalDateTime.now().plusDays(4), monthlyBudget2, user1));
        transactions2.add(new Transaction(i++,"9",500, "income", true, LocalDateTime.now().minusDays(21), monthlyBudget2, user1));

        monthlyBudget2.setTransactions(transactions2);
        List<Transaction> user1Transactions = new ArrayList<>();
        user1Transactions.addAll(transactions1);
        user1Transactions.addAll(transactions2);
        user1.setTransactions(user1Transactions);

        transactions3.add(new Transaction(i++, "10",6400, "income", true, LocalDateTime.now().minusDays(3), monthlyBudget3, user2));
        monthlyBudget3.setTransactions(transactions3);
        user2.setTransactions(transactions3);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        initializeBudgetsAndUsers();
        initializeTransactions();
    }

    @Test
    void shouldConvertToJsonResponse() {
        List<HashMap<String, Object>> result = transactionService.toJsonResponse(transactions2);
        for(HashMap<String, Object> response : result) {
            assertEquals(1L, response.get("user_id"));
            assertEquals(2L, response.get("budget_id"));
        }
    }

    @Test
    void shouldNOTConvertToJsonResponse() {
        assertNull(transactionService.toJsonResponse(null));
    }

    @Test
    void shouldGetAllUserPayments() {
        List<Transaction> completed = new ArrayList<>();
        completed.add(transactions2.get(5));
        List<Transaction> upcoming = new ArrayList<>();
        upcoming.addAll(transactions2);
        upcoming.remove(6);
        upcoming.remove(5);
        List<Transaction> overdue = new ArrayList<>();
        overdue.add(transactions1.get(0));

        Authentication auth = mock(Authentication.class);
        when(monthlyBudgetService.getCurrentBudget(auth)).thenReturn(monthlyBudget2);
        when(userService.getApplicationUser(auth)).thenReturn(user1);
        when(transactionRepository.findByBudgetAndTypeAndCompletedOrderByTimestampAsc(monthlyBudget2, "expense", true))
                .thenReturn(completed);
        when(transactionRepository.findByBudgetAndTypeAndCompletedAndTimestampAfterOrderByTimestampAsc(eq(monthlyBudget2), eq("expense"), eq(false), any(LocalDateTime.class)))
                .thenReturn(upcoming);
        when(transactionRepository.findByUserAndTypeAndCompletedAndTimestampBeforeOrderByTimestampAsc(eq(user1), eq("expense"), eq(false), any(LocalDateTime.class)))
                .thenReturn(overdue);

        HashMap<String, Object> result = transactionService.getUserPayments(auth);
        assertEquals(7, (int)result.get("total_payments"));
        assertEquals(5, (int)((HashMap<String,Object>) result.get("pending_payments")).get("total"));
        assertEquals(1, (int)((HashMap<String,Object>) result.get("completed_payments")).get("total"));
        assertEquals(1, (int)((HashMap<String,Object>) result.get("overdue_payments")).get("total"));
    }

    @Test
    void shouldReturnNullItemsWhenGetUserPayments() {
        Authentication auth = mock(Authentication.class);
        when(monthlyBudgetService.getCurrentBudget(auth)).thenReturn(monthlyBudget2);
        when(userService.getApplicationUser(auth)).thenReturn(user1);
        when(transactionRepository.findByBudgetAndTypeAndCompletedOrderByTimestampAsc(monthlyBudget2, "expense", true))
                .thenReturn(null);
        when(transactionRepository.findByBudgetAndTypeAndCompletedAndTimestampAfterOrderByTimestampAsc(eq(monthlyBudget2), eq("expense"), eq(false), any(LocalDateTime.class)))
                .thenReturn(null);
        when(transactionRepository.findByUserAndTypeAndCompletedAndTimestampBeforeOrderByTimestampAsc(eq(user1), eq("expense"), eq(false), any(LocalDateTime.class)))
                .thenReturn(null);

        HashMap<String, Object> result = transactionService.getUserPayments(auth);

        assertEquals(0, (int)result.get("total_payments"));
        assertEquals(0, (int)((HashMap<String,Object>) result.get("pending_payments")).get("total"));
        assertEquals(0, (int)((HashMap<String,Object>) result.get("completed_payments")).get("total"));
        assertEquals(0, (int)((HashMap<String,Object>) result.get("overdue_payments")).get("total"));
        assertNull(((HashMap<String, Object>) result.get("pending_payments")).get("items"));
        assertNull(((HashMap<String, Object>) result.get("completed_payments")).get("items"));
        assertNull(((HashMap<String, Object>) result.get("overdue_payments")).get("items"));
    }

    @Test
    void whenNewIncomeShouldReturnUpdatedIncome() {
        HashMap<String, Object> body = new HashMap<>();
        body.put("name", "work");
        body.put("amount", 800.00);
        body.put("completed", true);
        body.put("type", "income");
        body.put("timestamp", LocalDateTime.now().minusDays(10));
        Authentication auth = mock(Authentication.class);
        when(userService.getApplicationUser(auth)).thenReturn(user1);
        when(monthlyBudgetService.getCurrentBudget(auth)).thenReturn(monthlyBudget2);
        when(monthlyBudgetService.updateIncome(monthlyBudget2, 800)).thenReturn(800.00);
        assertEquals(800.00, transactionService.newTransaction(auth, body));
    }

    @Test
    void whenNewTransactionShouldReturnUpdatedTransaction() {
        HashMap<String, Object> body = new HashMap<>();
        body.put("name", "groceries");
        body.put("amount", 30.00);
        body.put("completed", false);
        body.put("type", "expense");
        body.put("timestamp", LocalDateTime.now().minusDays(4));
        Authentication auth = mock(Authentication.class);
        when(userService.getApplicationUser(auth)).thenReturn(user1);
        when(monthlyBudgetService.getCurrentBudget(auth)).thenReturn(monthlyBudget2);
        when(monthlyBudgetService.updateExpenses(monthlyBudget2, 30)).thenReturn(900.00);
        assertEquals(900.00, transactionService.newTransaction(auth, body));
    }

    @Test
    void shouldRemoveIncomeTransaction() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transactions1.get(0)));
        when(monthlyBudgetService.updateIncome(monthlyBudget1, -500.00)).thenReturn(0.00);
        assertEquals(0.00, transactionService.removeTransaction(1L));
    }

    @Test
    void shouldRemoveExpenseTransaction() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transactions1.get(1)));
        when(monthlyBudgetService.updateIncome(monthlyBudget1, -100.00)).thenReturn(0.00);
        assertEquals(0.00, transactionService.removeTransaction(1L));
    }

    @Test
    void shouldReturnNullWhenRemoveTransaction() {
        when(transactionRepository.findById(1L)).thenReturn(null);
        assertEquals(-1, transactionService.removeTransaction(1L));
    }
}