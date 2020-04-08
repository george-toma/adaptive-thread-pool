/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adaptive.threadpool.management.exception;

/**
 *
 * @author spykee
 */
public class UnknownCommandException extends RuntimeException{

    public UnknownCommandException(String message) {
        super(message);
    }
    
}
