package com.jaxnk2020.budgettracker.monthlybudget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/budget")
public class MonthlyBudgetController {
    @Autowired
    private MonthlyBudgetService monthlyBudgetService;

    public HashMap<String, Object> toJsonResponse(MonthlyBudget monthlyBudget) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("id", monthlyBudget.getBudgetId());
        response.put("balance", monthlyBudget.getBalance());
        response.put("income", monthlyBudget.getIncome());
        response.put("expenses", monthlyBudget.getExpenses());
        response.put("budget_goal", monthlyBudget.getBudgetGoal());
        response.put("budget_remaining", monthlyBudget.getBudgetRemaining());
        response.put("month", monthlyBudget.getMonth());
        response.put("year", monthlyBudget.getYear());
        response.put("user_id", monthlyBudget.getUser().getUserId());
        response.put("transactions", monthlyBudget.transactionJsonResponse());

        return response;
    }


    @PostMapping
    public ResponseEntity<HashMap<String,Object>> getCurrentBudget(Authentication auth) {
        HashMap<String, Object> response = new HashMap<>();
        MonthlyBudget currentBudget = monthlyBudgetService.getCurrentBudget(auth);
        if(currentBudget == null){
            response.put("message", "User cannot be found!");
            return ResponseEntity.badRequest().body(response);
        }
        else{
            return ResponseEntity.ok(toJsonResponse(currentBudget));
        }
    }
}
