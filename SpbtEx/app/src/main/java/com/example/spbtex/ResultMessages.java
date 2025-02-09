package com.example.spbtex;

import java.util.List;

public class ResultMessages {

    private String result;
    private List<Error> errors;

    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public List<Error> getErrors() {
        return errors;
    }
    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }


    public static class Error {

        private String message;

        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
