package com.wms.product_service.exception;

public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " not found with id: " + id);
    }
}
