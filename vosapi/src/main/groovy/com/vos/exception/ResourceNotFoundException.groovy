package com.vos.exception

class ResourceNotFoundException extends RuntimeException {
    
    ResourceNotFoundException(String message) {
        super(message)
    }
    
    ResourceNotFoundException(String resource, Long id) {
        super("${resource} with id ${id} not found")
    }
}

