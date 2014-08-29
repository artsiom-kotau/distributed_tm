package com.cs.distr.shared.api.entity;

import com.cs.distr.shared.api.AbstractTransactionEntity;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author: artsiom.kotov
 */
@Document(collection = "user")
public class UserEntity extends AbstractTransactionEntity {

    private static final long serialVersionUID = -7703069141155602434L;

    @Id
    private String id;
    private String firstName;
    private String secondName;
    private String email;

    public UserEntity(String id, String firstName, String secondName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
