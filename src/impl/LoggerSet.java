package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.api.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class LoggerSet extends ToggleableLogger implements Set<Logger> {
    private final Set<Logger> delegate = new HashSet<>();

    //<editor-fold desc="Delegated methods">
    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public Iterator<Logger> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean add(Logger logger) {
        return delegate.add(logger);
    }

    @Override
    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Logger> c) {
        return delegate.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
    //</editor-fold>

    @Override
    public void log(Level level, LazyMessage lazyMessage) {
        if (isDisabled()) return;
        for (Logger logger : this) {
            logger.log(level, lazyMessage);
        }
    }

    @Override
    public void log(Level level, Object message) {
        if (isDisabled()) return;
        for (Logger logger : this) {
            logger.log(level, message);
        }
    }

    @Override
    public LoggerSet clone() {
        LoggerSet clone = new LoggerSet();
        for (Logger logger : this) {
            clone.add(logger.clone());
        }
        return clone;
    }

    @Override
    protected void doLog(LogEntry entry) {
    }
}
