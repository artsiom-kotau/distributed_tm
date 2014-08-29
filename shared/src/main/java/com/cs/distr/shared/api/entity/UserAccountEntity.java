package com.cs.distr.shared.api.entity;

import com.cs.distr.shared.api.AbstractTransactionEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author: artsiom.kotov
 */
@Document(collection ="account")
public class UserAccountEntity extends AbstractTransactionEntity {

    private static final long serialVersionUID = 2955703093087874337L;

    @Id
    private String id;
    private String accountNumber;
    private String cardNumber;
    private String password;

    public UserAccountEntity(String id, String accountNumber, String cardNumber, String password) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.cardNumber = cardNumber;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
