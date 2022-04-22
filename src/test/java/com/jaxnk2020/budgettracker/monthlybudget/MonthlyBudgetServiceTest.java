package com.jaxnk2020.budgettracker.monthlybudget;

import com.jaxnk2020.budgettracker.user.ApplicationUser;
import com.jaxnk2020.budgettracker.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MonthlyBudgetServiceTest {
    @InjectMocks
    MonthlyBudgetService monthlyBudgetService;

    @Mock
    MonthlyBudgetRepository monthlyBudgetRepository;

    @Mock
    UserService userService;

    MonthlyBudget monthlyBudget;

    BCryptPasswordEncoder bCryptPasswordEncoder;

    ApplicationUser user;

    List<MonthlyBudget> monthlyBudgets;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        user = new ApplicationUser();

        user.setUserId(1);
        user.setUsername("bioround");
        user.setFirstname("Nikhil");
        user.setLastname("Kim");
        user.setPassword(bCryptPasswordEncoder.encode("Password"));
        user.setDateJoined(LocalDate.now());

        monthlyBudget = new MonthlyBudget();
        monthlyBudget.setBudgetGoal(3000);
        monthlyBudget.setUser(user);
        monthlyBudget.setMonth(3);
        monthlyBudget.setYear(2021);
        monthlyBudget.setIncome(0);
        monthlyBudget.setBalance(0);
        monthlyBudget.setBudgetId(1);
        monthlyBudget.setExpenses(0);
        monthlyBudget.setBudgetRemaining(3000);

        monthlyBudgets = new ArrayList<>();
        monthlyBudgets.add(monthlyBudget);
        user.setBudgets(monthlyBudgets);
    }

    @Test
    void shouldInitializeFirstMonthlyBudget() {
        assertEquals(4, monthlyBudgetService.initializeFirstMonthlyBudget(user).getMonth());
        assertEquals(2021, monthlyBudgetService.initializeFirstMonthlyBudget(user).getYear());
        assertEquals(0, monthlyBudgetService.initializeFirstMonthlyBudget(user).getBalance());
    }

    @Test
    void shouldGetRecentBudget() {
        when(monthlyBudgetRepository.findFirstByUserOrderByBudgetIdDesc(user)).thenReturn(monthlyBudget);
        assertEquals(4, monthlyBudgetService.getRecentBudget(user).getMonth());
        assertEquals(3000, monthlyBudgetService.getRecentBudget(user).getBudgetGoal());
    }

    @Test
    void getCurrentBudgetShouldReturnFirstBudget() {
        ApplicationUser newUser = new ApplicationUser();
        newUser.setUserId(2);
        newUser.setUsername("jaxnk2020");
        newUser.setFirstname("Jackson");
        newUser.setLastname("Suri");
        newUser.setPassword(bCryptPasswordEncoder.encode("Password"));
        newUser.setDateJoined(LocalDate.now());

        Authentication auth = Mockito.mock(Authentication.class);
        when(userService.getApplicationUser(auth)).thenReturn(newUser);
        assertEquals(0, monthlyBudgetService.getCurrentBudget(auth).getBudgetGoal());
    }

    @Test
    void getCurrentBudgetShouldReturnNewBudget() {
        Authentication auth = Mockito.mock(Authentication.class);
        when(userService.getApplicationUser(auth)).thenReturn(user);
        when(monthlyBudgetRepository.findByUserAndMonthAndYear(user, 4, 2021)).thenReturn(null);
        when(monthlyBudgetRepository.findFirstByUserOrderByBudgetIdDesc(user)).thenReturn(monthlyBudget);

        assertEquals(3000, monthlyBudgetService.getCurrentBudget(auth).getBudgetGoal());
        assertEquals(4, monthlyBudgetService.getCurrentBudget(auth).getMonth());
    }

    @Test
    void getCurrentBudgetShouldReturnCurrentBudget() {
        MonthlyBudget currentBudget = new MonthlyBudget();
        currentBudget.setBudgetGoal(3500);
        currentBudget.setUser(user);
        currentBudget.setMonth(4);
        currentBudget.setYear(2021);
        currentBudget.setIncome(0);
        currentBudget.setBalance(0);
        currentBudget.setBudgetId(2);
        currentBudget.setExpenses(0);
        currentBudget.setBudgetRemaining(3500);

        monthlyBudgets.add(currentBudget);
        user.setBudgets(monthlyBudgets);

        Authentication auth = Mockito.mock(Authentication.class);

        when(userService.getApplicationUser(auth)).thenReturn(user);
        when(monthlyBudgetRepository.findByUserAndMonthAndYear(user, 4, 2021)).thenReturn(currentBudget);

        assertEquals(3500, monthlyBudgetService.getCurrentBudget(auth).getBudgetGoal());
        assertEquals(4, monthlyBudgetService.getCurrentBudget(auth).getMonth());
    }

    @Test
    void shouldGetBudgetById() {
        when(monthlyBudgetRepository.findById(1L)).thenReturn(Optional.of(monthlyBudget));

        assertEquals(monthlyBudget.getBudgetGoal(), monthlyBudgetService.getBudgetById(1L).getBudgetGoal());
    }

    @Test
    void shouldChangeBudgetGoalAndUpdateRemaining() {
        HashMap<String, Double> body = new HashMap<>();
        body.put("budget_goal", 3500.00);
        when(monthlyBudgetRepository.findById(1L)).thenReturn(Optional.of(monthlyBudget));

        monthlyBudget.setExpenses(1480);
        assertEquals(3500, monthlyBudgetService.changeBudgetGoal(1L, body).getBudgetGoal());
        assertEquals(2020, monthlyBudget.getBudgetRemaining());

    }

    @Test
    void shouldUpdateBalance() {
        monthlyBudget.setIncome(2000);
        monthlyBudget.setExpenses(980);
        assertEquals(1020, monthlyBudgetService.updateBalance(monthlyBudget));

    }

    @Test
    void updateIncome() {
        monthlyBudget.setExpenses(980);
        assertEquals(2000, monthlyBudgetService.updateIncome(monthlyBudget, 2000));
        assertEquals(1020, monthlyBudget.getBalance());
    }

    @Test
    void updateExpenses() {
        monthlyBudget.setIncome(2000);
        assertEquals(980, monthlyBudgetService.updateExpenses(monthlyBudget, 980));
        assertEquals(1020, monthlyBudget.getBalance());
        assertEquals(2020, monthlyBudget.getBudgetRemaining());
    }

}