package me.lucien.minesweeper.web;

import org.springframework.http.HttpStatus;

public class HttpException extends Exception {

    private HttpStatus status;
    private String message;

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
