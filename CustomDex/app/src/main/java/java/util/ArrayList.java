package java.util;

public class ArrayList<E> extends HashSet<E> {
    @Override
    public boolean add(E object) {
        System.out.println("New ArrayList");
        return super.add(object);
    }
}
