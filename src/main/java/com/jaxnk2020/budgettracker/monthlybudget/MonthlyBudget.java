package com.jaxnk2020.budgettracker.monthlybudget;

import com.jaxnk2020.budgettracker.transaction.Transaction;
import com.jaxnk2020.budgettracker.user.ApplicationUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity
@Table(name="budget")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyBudget implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="budget_id")
    private long budgetId;

    private double balance=0;
    private double income=0;
    private double expenses=0;
    private double budgetGoal=0;
    private double budgetRemaining=0;

    private int month;
    private int year;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private ApplicationUser user;

    @OneToMany(mappedBy="budget", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Transaction> transactions;

    public List<HashMap<String, Object>> transactionJsonResponse() {
        if(this.transactions == null){
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
            response.add(newJson);
        }
        return response;
    }
}
