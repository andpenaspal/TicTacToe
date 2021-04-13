package tttHttp.DAO;

public interface DAO<T, K> {

    T get(K k);

    void update(T t);

    void delete(T t);
}
