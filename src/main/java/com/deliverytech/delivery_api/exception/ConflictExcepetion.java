package com.deliverytech.delivery_api.exception;

public class ConflictExcepetion extends BusinessException {

    private String conflictField;
    private Object conflictValue;

    public ConflictExcepetion(String message) {
        super(message);
        this.setErrorCode(("CONFLICT"));
    }

    public ConflictExcepetion(String message, String conflictField, Object conflictValue) {
        super(message);
        this.conflictField = conflictField;
        this.conflictValue = conflictValue;
        this.setErrorCode("CONFLICT");
    }

    public String getConflictField() {
        return conflictField;
    }

    public Object getConflictValue() {
        return conflictValue;
    }
}
