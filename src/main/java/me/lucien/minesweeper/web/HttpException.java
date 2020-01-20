package me.lucien.minesweeper.web;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class HttpException extends Exception {

    private HttpStatus status;
    private String message;

    public HttpException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
