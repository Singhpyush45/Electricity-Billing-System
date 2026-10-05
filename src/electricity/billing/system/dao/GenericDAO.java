package electricity.billing.system.dao;

import electricity.billing.system.exception.DatabaseException;
import java.util.List;
import java.util.Optional;

/**
 * GENERIC INTERFACE for database operations (DAO = Data Access Object).
 *
 * T = the type of object stored (Customer, Meter, BillRecord...)
 * K = the type of its key (String meter number, BillKey...)
 *
 * Writing it once with generics means every DAO has the same basic methods,
 * but each works with its own type and the compiler checks the types for us.
 */
public interface GenericDAO<T, K> {

    void add(T entity) throws DatabaseException;

    /** Optional = "maybe a value": empty if nothing was found, instead of null. */
    Optional<T> findById(K id) throws DatabaseException;

    List<T> findAll() throws DatabaseException;

    boolean update(T entity) throws DatabaseException;
}
