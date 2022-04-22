package com.jaxnk2020.budgettracker.monthlybudget;

import com.jaxnk2020.budgettracker.user.ApplicationUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MonthlyBudgetControllerTest {
    @InjectMocks
    MonthlyBudgetController monthlyBudgetController;

    @Mock
    MonthlyBudgetService monthlyBudgetService;

    MonthlyBudget monthlyBudget;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ApplicationUser user = new ApplicationUser();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        user.setUserId(1);
        user.setUsername("bioround");
        user.setFirstname("Nikhil");
        user.setLastname("Kim");
        user.setPassword(bCryptPasswordEncoder.encode("Password"));
        user.setDateJoined(LocalDate.now());
        monthlyBudget = new MonthlyBudget();monthlyBudget = new MonthlyBudget();
        monthlyBudget.setBudgetGoal(3000);
        monthlyBudget.setUser(user);
        monthlyBudget.setMonth(4);
        monthlyBudget.setYear(2021);
        monthlyBudget.setIncome(2000);
        monthlyBudget.setBalance(1020);
        monthlyBudget.setBudgetId(1);
        monthlyBudget.setExpenses(980);
        monthlyBudget.setBudgetRemaining(2020);

    }

    @Test
    void shouldConvertToJsonResponse() {
        HashMap<String, Object> response = monthlyBudgetController.toJsonResponse(monthlyBudget);
        assertEquals(1L,response.get("id"));
        assertEquals(1020.0,response.get("balance"));
        assertEquals(2000.0,response.get("income"));
        assertEquals(980.0,response.get("expenses"));
        assertEquals(3000.0,response.get("budget_goal"));
        assertEquals(2020.0,response.get("budget_remaining"));
        assertEquals(4,response.get("month"));
        assertEquals(2021,response.get("year"));
        assertEquals(1L,response.get("user_id"));
        assertNull(response.get("transactions"));
    }

    @Test
    void getCurrentBudget() {
    }
}