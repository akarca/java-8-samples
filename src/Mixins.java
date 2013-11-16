import static java.util.Arrays.asList;

import java.util.*;

public class Mixins {
	public static void main(String[] args) {
		Predicate<String> predicate = s -> s.length() == 3;
		boolean all3Letters = predicate.matchesAll(asList("Foo", "Bar", "Baz"));
		System.out.println(all3Letters);
	}
	
	@FunctionalInterface
	public interface Predicate<T> {
		static <T> Predicate<T> alwaysTrue() {
			return t -> true;
		}

		static <T> Predicate<T> alwaysFalse() {
			return t -> false;
		}
		
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

		default Predicate<T> negate() {
			return it -> !matches(it);
		}
	}

}

