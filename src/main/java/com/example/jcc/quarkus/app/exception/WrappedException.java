package com.example.jcc.quarkus.app.exception;

/**
 * Simple wrapper that wraps Checked exceptions in unchecked one.
 */
public class WrappedException extends RuntimeException {

    public WrappedException(Exception cause) {
        super(cause);
    }

    /**
     * Returns the wrapped {@link Exception}
     *
     * @return the wrapped {@link Exception}
     */
    @Override
    public Exception getCause() {
        return (Exception) super.getCause();
    }

}
