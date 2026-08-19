package org.example.event;

public class UserEvent {
    private Operation operation;
    private String email;

    public UserEvent(Operation operation, String email) {
        this.operation = operation;
        this.email = email;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
