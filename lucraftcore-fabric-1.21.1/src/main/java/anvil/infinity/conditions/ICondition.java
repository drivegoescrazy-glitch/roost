package anvil.infinity.conditions;

/** Predicate that gates an ability, as in the original mod. */
public interface ICondition<T> {

    boolean isFulfilled(T information);
}
