package com.jaxnk2020.budgettracker.user;
import com.jaxnk2020.budgettracker.monthlybudget.MonthlyBudget;
import com.jaxnk2020.budgettracker.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private long userId;

    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private LocalDate dateJoined;

    public ApplicationUser(String username, String password, String firstname, String lastname, LocalDate dateJoined){
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.dateJoined = dateJoined;
    }

    @OneToMany(mappedBy="user", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<MonthlyBudget> budgets;

    @OneToMany(mappedBy="user", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Transaction> transactions;
}