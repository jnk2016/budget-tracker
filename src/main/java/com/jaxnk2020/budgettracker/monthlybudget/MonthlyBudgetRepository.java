package com.jaxnk2020.budgettracker.monthlybudget;

import com.jaxnk2020.budgettracker.user.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonthlyBudgetRepository extends JpaRepository<MonthlyBudget, Long> {
    List<MonthlyBudget> findByUser(ApplicationUser user);
    MonthlyBudget findByUserAndMonthAndYear(ApplicationUser user, int month, int year);
    MonthlyBudget findFirstByUserOrderByBudgetIdDesc(ApplicationUser user);
}
