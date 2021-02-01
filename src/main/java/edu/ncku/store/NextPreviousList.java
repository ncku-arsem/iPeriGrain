package edu.ncku.store;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Optional;

public class NextPreviousList<T> {
    private LinkedList<T> list = new LinkedList<>();
    private ListIterator<T> iterator = list.listIterator();
    private int capacity;
    private Action preAction = Action.NONE;
    private enum Action{
        NEXT, PREVIOUS, NONE;
    }

    public NextPreviousList(int capacity){
        this.capacity = capacity;
    }

    public boolean isFull(){
        return list.size()>=capacity;
    }

    public void add(T o){
        if(isFull())
            list.removeLast();
        list.addFirst(o);
        iterator = list.listIterator();
    }

    public Optional<T> getLast(){
        return Optional.of(list.getLast());
    }

    public Optional<T> next(){
        if(preAction!=Action.NEXT){
            if(!iterator.hasPrevious())
                return Optional.empty();
            iterator.previous();
        }
        if(!iterator.hasPrevious())
            return Optional.empty();
        preAction = Action.NEXT;
        return Optional.of(iterator.previous());
    }

    public boolean hasNext(){
        int adjust = preAction!=Action.NEXT ? 1:0;
        return iterator.previousIndex()-adjust>=0;
    }

    public Optional<T> previous(){
        if(preAction!=Action.PREVIOUS){
            if(!iterator.hasNext())
                return Optional.empty();
            iterator.next();
        }
        if(!iterator.hasNext())
            return Optional.empty();
        preAction = Action.PREVIOUS;
        return Optional.of(iterator.next());
    }

    public boolean hasPrevious(){
        int adjust = preAction!=Action.PREVIOUS ? 1:0;
        return iterator.nextIndex()+adjust < list.size();
    }
}