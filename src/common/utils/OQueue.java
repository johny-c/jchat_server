package common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public final class OQueue<E> extends Observable implements BlockingQueue<Message> {

    private ArrayBlockingQueue<Message> queue;
    private List<String> observersNames;
    private int capacity;

    public OQueue(int c) {
        capacity = c;
        queue = new ArrayBlockingQueue<>(capacity);
        observersNames = new ArrayList<>();
    }

    @Override
    public void addObserver(Observer ob) {
        super.addObserver(ob);
        observersNames.add(ob.getClass().getSimpleName());
        printObservers();
    }

    @Override
    public void deleteObserver(Observer ob) {
        super.deleteObserver(ob);
        observersNames.remove(ob.getClass().getSimpleName());
        printObservers();
    }

    public void printObservers() {
        String currentlyObserving = "Current GUI observers of incoming messages:\n";
        int count = 1;
        for (String s : observersNames) {
            currentlyObserving += "\n" + (count++) + "  " + s;
        }
    }

    public boolean offerNotify(Message e) {
        boolean b = queue.offer(e);
        this.notifyObservers();
        return b;
    }

    public boolean offerNotify(Message e, Object arg) {
        boolean b = queue.offer(e);
        this.notifyObservers(arg);
        return b;
    }

    @Override
    public void notifyObservers(Object arg) {
        this.setChanged();
        super.notifyObservers(arg);
    }

    @Override
    public boolean add(Message e) {
        return queue.add(e);
    }

    @Override
    public boolean offer(Message e) {
        boolean b = queue.offer(e);
        this.setChanged();
        this.notifyObservers();
        return b;
    }

    @Override
    public void put(Message e) throws InterruptedException {
        queue.put(e);
    }

    @Override
    public boolean offer(Message e, long timeout, TimeUnit unit) throws InterruptedException {
        return queue.offer(e, timeout, unit);
    }

    @Override
    public Message take() throws InterruptedException {
        return queue.take();
    }

    @Override
    public Message poll(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }

    @Override
    public int remainingCapacity() {
        return queue.remainingCapacity();
    }

    public boolean remove(Message o) {
        return queue.remove(o);
    }

    public boolean contains(Message o) {
        return queue.contains(o);
    }

    @Override
    public int drainTo(Collection<? super Message> c) {
        return queue.drainTo(c);
    }

    @Override
    public int drainTo(Collection<? super Message> c, int maxElements) {
        return queue.drainTo(c, maxElements);
    }

    @Override
    public Message remove() {
        return queue.remove();
    }

    @Override
    public Message poll() {
        return queue.poll();
    }

    @Override
    public Message element() {
        return queue.element();
    }

    @Override
    public Message peek() {
        return queue.peek();
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public Iterator<Message> iterator() {
        return queue.iterator();
    }

    @Override
    public Message[] toArray() {
        return (Message[]) queue.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return queue.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return queue.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Message> c) {
        return queue.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return queue.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return queue.retainAll(c);
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public boolean remove(Object o) {
        return queue.remove(o);
    }

    @Override
    public boolean contains(Object o) {
        return queue.contains(o);
    }
}
