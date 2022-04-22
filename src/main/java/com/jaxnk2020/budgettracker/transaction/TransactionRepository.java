package com.jaxnk2020.budgettracker.transaction;

import com.jaxnk2020.budgettracker.monthlybudget.MonthlyBudget;
import com.jaxnk2020.budgettracker.user.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(ApplicationUser user);
    List<Transaction> findByBudget(MonthlyBudget budget);
    List<Transaction> findByBudgetAndType(MonthlyBudget budget, String type);
    List<Transaction> findByBudgetAndTypeAndCompletedAndTimestampAfterOrderByTimestampAsc(MonthlyBudget currentBudget, String type, boolean completed, LocalDateTime currentDateTime);
    List<Transaction> findByBudgetAndTypeAndCompletedOrderByTimestampAsc(MonthlyBudget currentBudget, String type, boolean completed);
    List<Transaction> findByUserAndTypeAndCompletedAndTimestampBeforeOrderByTimestampAsc(ApplicationUser user, String type, boolean completed, LocalDateTime currentDateTime);
}
