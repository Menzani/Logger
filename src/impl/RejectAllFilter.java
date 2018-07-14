package it.menzani.logger.impl;

import it.menzani.logger.api.Filter;

public final class RejectAllFilter implements Filter {
    @Override
    public boolean reject(LogEntry entry) {
        return true;
    }
}
