package br.com.zup.propostas.compartilhado.exception;

import java.util.ArrayList;
import java.util.List;

public class ApiErrors {
    private List<String> globalErrors = new ArrayList<>();
    private List<FieldErrorResponse> fieldErrors = new ArrayList<>();

    public void addGlobalError(String message) {
        globalErrors.add(message);
    }

    public void addFieldError(String field, String message) {
        fieldErrors.add(new FieldErrorResponse(field, message));
    }

    public List<String> getGlobalErrors() {
        return globalErrors;
    }

    public List<FieldErrorResponse> getFieldErrors() {
        return fieldErrors;
    }

    public int getNumberOfErrors() {
        return this.fieldErrors.size() + this.globalErrors.size();
    }
}
