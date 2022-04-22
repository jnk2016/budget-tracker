package com.jaxnk2020.budgettracker.transaction;

import com.jaxnk2020.budgettracker.monthlybudget.MonthlyBudget;
import com.jaxnk2020.budgettracker.monthlybudget.MonthlyBudgetService;
import com.jaxnk2020.budgettracker.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    MonthlyBudgetService monthlyBudgetService;
    @Autowired
    UserService userService;

    public List<HashMap<String, Object>> toJsonResponse(List<Transaction> transactions) {
        if(transactions == null){
            return null;
        }
        List<HashMap<String, Object>> response = new ArrayList<>();
        for(Transaction transaction : transactions) {
            HashMap<String, Object> newJson = new HashMap<>();
            newJson.put("id", transaction.getTransactionId());
            newJson.put("name", transaction.getName());
            newJson.put("amount", transaction.getAmount());
            newJson.put("type", transaction.getType());
            newJson.put("completed", transaction.isCompleted());
            newJson.put("timestamp", transaction.getTimestamp());
            newJson.put("user_id", transaction.getUser().getUserId());
            newJson.put("budget_id", transaction.getBudget().getBudgetId());
            response.add(newJson);
        }
        return response;
    }

    public List<HashMap<String, Object>> getAllPaymentsInCurrentBudget(Authentication auth) {
        MonthlyBudget currentBudget = monthlyBudgetService.getCurrentBudget(auth);
        return toJsonResponse(transactionRepository.findByBudgetAndType(currentBudget, "expense"));
    }

    public List<HashMap<String, Object>> getAllIncomeInCurrentBudget(Authentication auth) {
        MonthlyBudget currentBudget = monthlyBudgetService.getCurrentBudget(auth);
        return toJsonResponse(transactionRepository.findByBudgetAndType(currentBudget, "income"));
    }

    public HashMap<String, Object> getUserPayments(Authentication auth) {
        HashMap<String, Object> response = new HashMap<>();
        MonthlyBudget currentBudget = monthlyBudgetService.getCurrentBudget(auth);

        List<HashMap<String, Object>> completed = toJsonResponse(
                transactionRepository.findByBudgetAndTypeAndCompletedOrderByTimestampAsc(
                        currentBudget,"expense", true));
        HashMap<String, Object> completedResponse = new HashMap<>();
        completedResponse.put("total", (completed==null ? 0 : completed.size()));
        completedResponse.put("items", completed);

        List<HashMap<String, Object>> pending = toJsonResponse(transactionRepository
                .findByBudgetAndTypeAndCompletedAndTimestampAfterOrderByTimestampAsc(
                        currentBudget,"expense", false, LocalDateTime.now()));
        HashMap<String, Object> pendingResponse = new HashMap<>();
        pendingResponse.put("total", (pending==null ? 0 : pending.size()));
        pendingResponse.put("items", pending);

        List<HashMap<String, Object>> overdue = toJsonResponse(transactionRepository
                .findByUserAndTypeAndCompletedAndTimestampBeforeOrderByTimestampAsc(
                        userService.getApplicationUser(auth), "expense", false, LocalDateTime.now()));
        HashMap<String, Object> overdueResponse = new HashMap<>();
        overdueResponse.put("total", (overdue==null ? 0 : overdue.size()));
        overdueResponse.put("items", overdue);

        response.put("overdue_payments", overdueResponse);
        response.put("pending_payments", pendingResponse);
        response.put("completed_payments", completedResponse);
        response.put("total_payments", (int)overdueResponse.get("total") + (int)pendingResponse.get("total") + (int)completedResponse.get("total"));
        return response;
    }

    public double newTransaction(Authentication auth, HashMap<String, Object> body) {
        Transaction newEntry = new Transaction();
        newEntry.setUser(userService.getApplicationUser(auth));
        newEntry.setName((String)body.get("name"));
        newEntry.setAmount((double)body.get("amount"));
        newEntry.setBudget(monthlyBudgetService.getCurrentBudget(auth));
        newEntry.setCompleted((boolean)body.get("completed"));
        newEntry.setType((String)body.get("type"));
        newEntry.setTimestamp((LocalDateTime)body.get("timestamp"));
        transactionRepository.save(newEntry);

        if(newEntry.getType().equals("income")){
            return monthlyBudgetService.updateIncome(newEntry.getBudget(), newEntry.getAmount());
        }
        else{
            return monthlyBudgetService.updateExpenses(newEntry.getBudget(), newEntry.getAmount());
        }
    }

    public double removeTransaction(long id) {
        try{
            Transaction entry = transactionRepository.findById(id).orElse(null);
            if (entry.getType().equals("income")) {
                return monthlyBudgetService.updateIncome(entry.getBudget(), entry.getAmount() * (-1));
            } else {
                return monthlyBudgetService.updateExpenses(entry.getBudget(), entry.getAmount() * (-1));
            }
        }catch(Exception e){
            return -1;
        }
    }
}
