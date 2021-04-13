package tttHttp.DAO;

public interface DAO<T, K> {

    T get(K k);

    K save(T t);

    void update(T t);

    void delete(T t);
}
