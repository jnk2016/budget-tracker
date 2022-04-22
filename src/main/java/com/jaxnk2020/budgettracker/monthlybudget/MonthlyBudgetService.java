package com.jaxnk2020.budgettracker.monthlybudget;

import com.jaxnk2020.budgettracker.transaction.Transaction;
import com.jaxnk2020.budgettracker.user.ApplicationUser;
import com.jaxnk2020.budgettracker.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Service
public class MonthlyBudgetService {
    @Autowired
    MonthlyBudgetRepository monthlyBudgetRepository;
    @Autowired
    UserService userService;

    public MonthlyBudget initializeFirstMonthlyBudget (ApplicationUser user) {
        MonthlyBudget firstEntry = new MonthlyBudget();
        firstEntry.setMonth(LocalDate.now().getMonthValue());
        firstEntry.setYear(LocalDate.now().getYear());
        firstEntry.setUser(user);
        monthlyBudgetRepository.save(firstEntry);
        return firstEntry;
    }

    public MonthlyBudget getRecentBudget(ApplicationUser user) {
        MonthlyBudget mostRecent = monthlyBudgetRepository.findFirstByUserOrderByBudgetIdDesc(user);
        MonthlyBudget newEntry = new MonthlyBudget();
        newEntry.setUser(user);
        newEntry.setBudgetGoal(mostRecent.getBudgetGoal());
        newEntry.setMonth(LocalDate.now().getMonthValue());
        newEntry.setYear(LocalDate.now().getYear());
        monthlyBudgetRepository.save(newEntry);
        return newEntry;
    }

    public MonthlyBudget getCurrentBudget (Authentication auth) {
        ApplicationUser user = userService.getApplicationUser(auth);
        if(user == null){
            return null;
        }
        if(user.getBudgets() == null || user.getBudgets().size() == 0) {
            return initializeFirstMonthlyBudget(user);
        }
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();
        MonthlyBudget current = monthlyBudgetRepository.findByUserAndMonthAndYear(user, currentMonth, currentYear);
        if(current == null){
            current = getRecentBudget(user);
        }
        return current;
    }

    public MonthlyBudget changeBudgetGoal (long id, HashMap<String, Double> body) {
        MonthlyBudget entry = getBudgetById(id);
        entry.setBudgetGoal(body.get("budget_goal"));
        entry.setBudgetRemaining(updateBudgetRemaining(entry));
        monthlyBudgetRepository.save(entry);
        return entry;
    }

    public double updateBalance(MonthlyBudget budget) {
        return budget.getIncome()-budget.getExpenses();
    }

    public double updateBudgetRemaining(MonthlyBudget budget) {
        return budget.getBudgetGoal()-budget.getExpenses();
    }

    public double updateIncome(MonthlyBudget budget, double incomeAmount) {
        budget.setIncome(budget.getIncome()+incomeAmount);
        budget.setBalance(updateBalance(budget));
        monthlyBudgetRepository.save(budget);
        return budget.getIncome();
    }

    public double updateExpenses(MonthlyBudget budget, double expenseAmount) {
        budget.setExpenses(budget.getExpenses()+expenseAmount);
        budget.setBalance(updateBalance(budget));
        budget.setBudgetRemaining(updateBudgetRemaining(budget));
        monthlyBudgetRepository.save(budget);
        return budget.getExpenses();
    }

    public MonthlyBudget getBudgetById(long id) {
        return monthlyBudgetRepository.findById(id).orElse(null);
    }
}
