package it.menzani.logger.impl;

import it.menzani.logger.LogEntry;
import it.menzani.logger.api.Cloneable;
import it.menzani.logger.api.*;

import java.util.*;

public final class LoggerGroup extends ToggleableLogger implements List<Logger> {
    private final List<Logger> delegate = new ArrayList<>();

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
    public boolean addAll(int index, Collection<? extends Logger> c) {
        return delegate.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
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

    @Override
    public Logger get(int index) {
        return delegate.get(index);
    }

    @Override
    public Logger set(int index, Logger element) {
        return delegate.set(index, element);
    }

    @Override
    public void add(int index, Logger element) {
        delegate.add(index, element);
    }

    @Override
    public Logger remove(int index) {
        return delegate.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<Logger> listIterator() {
        return delegate.listIterator();
    }

    @Override
    public ListIterator<Logger> listIterator(int index) {
        return delegate.listIterator(index);
    }

    @Override
    public List<Logger> subList(int fromIndex, int toIndex) {
        return delegate.subList(fromIndex, toIndex);
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
    public LoggerGroup clone() {
        LoggerGroup clone = new LoggerGroup();
        this.stream()
                .map(Cloneable::clone)
                .forEach(clone::add);
        return clone;
    }

    @Override
    protected void doLog(LogEntry entry) {
    }
}
