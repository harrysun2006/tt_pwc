package com.pwc.repository;

import com.pwc.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Slf4j
@SuppressWarnings("unchecked")
public class AbstractRepository<JooqPojo extends Serializable, JooqUpdatableRecord extends UpdatableRecord, Id> {

    public static final String NOT_FOUND_MESSAGE = "%s with ID %s not found";
    protected static final org.jooq.Field[] EMPTY_FIELDS = {};

    /**
     * JOOQ DSL context.
     */
    @Autowired
    protected DSLContext dsl;

    /**
     * Class of the entity's POJO, which is used in / returned from CRUD queries.
     */
    private Class<JooqPojo> pojoType;

    /**
     * Class of the entity's UpdatableRecord, which is returned from DSL queries.
     */
    private Class<JooqUpdatableRecord> recordType;

    private Class<Id> idType;

    /**
     * Database table on which CRUD queries should be run.
     */
    private Table<JooqUpdatableRecord> table;

    /**
     * Constructor. Gets references to types and classes required to run CRUD queries.
     *
     * @TODO: Could probably use annotations to cut down on reflection usage.
     */
    public AbstractRepository() {
        // Get the actual POJO type passed into the generic list.
        this.pojoType = (Class<JooqPojo>)
                ((ParameterizedType) getClass()
                        .getGenericSuperclass())
                        .getActualTypeArguments()[0];

        // Get the actual UpdatableRecord type passed into the generic list.
        this.recordType = (Class<JooqUpdatableRecord>)
                ((ParameterizedType) getClass()
                        .getGenericSuperclass())
                        .getActualTypeArguments()[1];

        // Get the actual ID type passed into the generic list.
        this.idType = (Class<Id>)
                ((ParameterizedType) getClass()
                        .getGenericSuperclass())
                        .getActualTypeArguments()[2];

        // Get the table type from the record type.
        try {
            this.table = this.recordType.getDeclaredConstructor().newInstance().getTable();
        } catch (Exception ex) {
            // Exception will only ever happen on application start, so just throw a RuntimeException.
            throw new RuntimeException(ex.getMessage());
        }

        // Throw exception on startup if the ID setter name is invalid.
        try {
            this.pojoType.getMethod(getPojoIdSetterName(), idType);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(
                    String.format(
                            "Could not find setter method %s, which is required by RepositoryBase. "
                                    + "Check the column name in the DB schema or override setPojoId() in your repository.",
                            getPojoIdSetterName()
                    )
            );
        }
    }

    /**
     * Returns the name of the POJO's ID setter method, which is expected to be "setMyEntityId()".
     *
     * @return ID setter method name
     */
    protected String getPojoIdSetterName() {
        return String.format("set%sId", pojoType.getSimpleName());
    }

    /**
     * Overridable method for setting the ID (i.e. Primary Key column) of the entity's POJO.
     * By default, the ID name is assumed to be (camel-cased entity name + "Id"), e.g. "userId".
     * This is required for building a 'delta' record to be persisted to the database in update queries.
     *
     * @param pojo  The entity POJO for which the ID needs to be set
     * @param value The ID value
     * @throws NoSuchMethodException     if the setter method cannot be found
     * @throws InvocationTargetException if the setter method itself threw an exception
     * @throws IllegalAccessException    if the setter couldn't be called
     */
    protected void setPojoId(JooqPojo pojo, Id value) {
        // Get setter name, which is assumed to be setEntityId
        String idSetterName = getPojoIdSetterName();

        try {
            // Invoke the setter.
            pojoType.getMethod(idSetterName, value.getClass()).invoke(pojo, value);
        } catch (ReflectiveOperationException e) {
            // Should never happen, since we checked this in the constructor.
            throw new ApplicationException("Internal Server Error");
        }
    }

    /**
     * Returns the name of the POJO's ID getter method, which is expected to be "getMyEntityId()".
     *
     * @return ID getter method name
     */
    protected String getPojoIdGetterName() {
        return String.format("get%sId", pojoType.getSimpleName());
    }

    /**
     * Overridable method for returning a JOOQ condition that checks if a record's
     * ID matches the value provided.
     *
     * @param id The ID value to check
     * @return A JOOQ condition that checks if a record has a matching ID
     */
    protected Condition idMatches(Id id) {
        TableField idField = table.getPrimaryKey().getFields().get(0);
        return idField.eq(id);
    }

    /**
     * CRUD method for querying the existence of a record with the given ID.
     *
     * @param id The ID of the record to find
     * @return true if the record exists
     */
    public boolean exists(Id id) {
        return dsl.fetchExists(dsl.select().from(table).where(idMatches(id)));
    }

    /**
     * CRUD method for selecting all entities from the table.
     *
     * @return A list of entity POJOs for each record
     */
    public List<JooqPojo> findAll() {
        return dsl.select().from(table).fetchInto(pojoType);
    }

    /**
     * CRUD method selecting a single entity by ID.
     *
     * @return A POJO representing the found record
     */
    public JooqPojo findOne(Id id) {
        JooqUpdatableRecord record = dsl.fetchOne(table, idMatches(id));
        if (record == null) {
            throw new ApplicationException(
                    String.format(NOT_FOUND_MESSAGE, pojoType.getSimpleName(), id.toString())
            );
        }

        JooqPojo result = record.into(pojoType);
        return result;
    }

    /**
     * CRUD method for creating a new entity.
     *
     * @return A POJO representing the persisted record
     */
    public JooqPojo create(JooqPojo pojo) {
        JooqUpdatableRecord record = dsl.newRecord(table);
        record.from(pojo);
        record.store();
        return record.into(pojoType);
    }

    /**
     * CRUD method for updating the entity with the given ID.
     *
     * @return A POJO representing the persisted record
     */
    public JooqPojo update(Id id, JooqPojo pojo) {
        setPojoId(pojo, id);
        JooqUpdatableRecord delta = dsl.newRecord(table, pojo);
        dsl.executeUpdate(delta);
        return findOne(id);
    }

    /**
     * A convenience method that creates or updates an entity.
     *
     * @param pojo The entity to be persisted
     * @return A POJO representing the persisted record
     */
    public JooqPojo save(JooqPojo pojo) {
        // Get the POJO's Id value
        Id pojoId;

        try {
            pojoId = (Id) pojoType.getMethod(getPojoIdGetterName()).invoke(pojo);
        } catch (Exception e) {
            throw new ApplicationException("Internal Server Error");
        }

        // If the POJO's Id is not set we assume that we need to create the db record
        if (pojoId == null) {
            return create(pojo);
        }

        // If a db record for the POJO already exists update the existing db record otherwise create a new db record
        if (exists(pojoId)) {
            return update(pojoId, pojo);
        } else {
            return create(pojo);
        }
    }

    /**
     * CRUD method for deleting the entity with the given ID.
     */
    public void destroy(Id id) {
        JooqUpdatableRecord record = dsl.fetchOne(table, idMatches(id));

        if (record != null) {
            record.delete();
        } else {
            throw new ApplicationException(
                    String.format(NOT_FOUND_MESSAGE, pojoType.getSimpleName(), id.toString())
            );
        }
    }

    // region Spring Data/JOOQ paging support
    // based on https://www.petrikainulainen.net/programming/jooq/using-jooq-with-spring-sorting-and-pagination/

    /**
     * JOOQ/Spring Paging support
     * <p>
     * Gets sort fields for use in a JOOQ query's orderBy clause.
     * Achieves this by translating {@link Pageable#getSort()} data into a Collection of {@link org.jooq.SortField} objects.
     *
     * @param sortSpecification From a {@link org.springframework.data.domain.Pageable object}
     * @return A Collection of {@link org.jooq.SortField} objects
     */
    protected Collection<SortField<?>> getSortFields(Sort sortSpecification) {
        Collection<SortField<?>> querySortFields = new ArrayList<>();

        if (sortSpecification == null) {
            return querySortFields;
        }

        Iterator<Sort.Order> specifiedFields = sortSpecification.iterator();

        while (specifiedFields.hasNext()) {
            Sort.Order specifiedField = specifiedFields.next();

            String sortFieldName = specifiedField.getProperty();
            Sort.Direction sortDirection = specifiedField.getDirection();

            TableField tableField = getTableField(sortFieldName.toUpperCase());
            SortField<?> querySortField = convertTableFieldToSortField(tableField, sortDirection);
            querySortFields.add(querySortField);
        }

        return querySortFields;
    }

    /**
     * Use reflection to get the {@link TableField} object that provides information about
     * the requested field of the repository's related db table.
     *
     * @param sortFieldName the sort field for which we want to find a {@link TableField} match
     * @return a Jooq Table field
     */
    protected TableField getTableField(String sortFieldName) {
        TableField sortField = null;
        try {
            Field tableField = table.getClass().getField(sortFieldName);
            sortField = (TableField) tableField.get(table);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            String errorMessage = String.format("Could not find table field: %s", sortFieldName);
            throw new InvalidDataAccessApiUsageException(errorMessage, ex);
        }

        return sortField;
    }

    /**
     * Convert a {@link TableField} object to a {@link SortField} object
     * for use in the {@link DSLContext} select query's orderBy clause.
     *
     * @param tableField    The {@link TableField} to be converted
     * @param sortDirection The direction of the sort
     * @return A {@link SortField} object
     **/
    protected SortField<?> convertTableFieldToSortField(TableField tableField, Sort.Direction sortDirection) {
        if (sortDirection == Sort.Direction.ASC) {
            return tableField.asc();
        } else {
            return tableField.desc();
        }
    }

    // endregion
}
