import static java.util.Arrays.asList;

import java.util.*;

public class Mixins {
	public static void main(String[] args) {
		Predicate<String> predicate = s -> s.length() == 3;
		boolean all3Letters = predicate.matchesAll(asList("Foo", "Bar", "Baz"));
	}
	//interfaces can now have default methods
	//implementors only need to implement abstract methods
	//but they can also override default methods
	//this greatly reduces the "Interface+Skeleton Class" pattern
	@FunctionalInterface
	public interface Predicate<T> {
		//only this needs to be implemented
		boolean matches(T instance);

		default boolean matchesAll(Collection<T> instances) {
			return instances.stream().allMatch(this::matches);
		}

		default Predicate<T> and(Predicate<T> other) {
			return it -> matches(it) && other.matches(it);
		}

		default Predicate<T> or(Predicate<T> other) {
			return it -> matches(it) || other.matches(it);
		}
		//this can be overriden if the implementor has a more efficient/meaningful version
		default Predicate<T> negate() {
			return it -> !matches(it);
		}
		
		//interfaces can now have static methods 
		//so you no longer need an additional utility class to hold your utility functions
		static <T> Predicate<T> alwaysTrue() {
			return t -> true;
		}

		static <T> Predicate<T> alwaysFalse() {
			return t -> false;
		}
	}

}

