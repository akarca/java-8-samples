import static java.lang.Integer.max;
import static java.lang.Integer.sum;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Sets;

public class Streams {
	public static void main(String[] args) throws IOException {
		List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		
		//streams allow a functional style of data transformation
		//they also aim to make parallel bulk processing (map-reduce) transparent to the programmer
		
		//a stream can be obtained in different ways
		//from a collection
		ints.stream();
		//from an array
		Arrays.stream(new int []{1,2,3});
		//or shorter
		Stream.of(1,2,3);
		//or even I/O
		Files.lines(FileSystems.getDefault().getPath("foo.txt"));
		
		//streams have two kinds of methods
		
		//intermediate methods return a new stream object
		//they are lazy, so none of them actually do anything
		Stream<Integer> temp = ints.stream().filter(i-> i > 3).distinct().skip(2);
		
		//and there are terminating methods that actually consume the stream and produce a result
		Integer sum = temp.reduce(0, Integer::sum);

		//however, not all methods are equally "cheap", some are stateful
		ints.stream().distinct(); //needs to save all encountered elements
		
		//others are stateless and very parallelizable
		ints.stream().parallel().filter(i -> i > 5);
		
		//you can even work with infinite streams
		//also note that some methods return "Optional" to indicate that there may be no result
		Optional<Integer> first = Stream.iterate(0, i->i+1).filter(i-> i*i > 100*i).findFirst();
		
		//the most interesting functions are of course map and reduce
		ints.stream().mapToInt(i-> i*i).reduce(Integer::sum);
		
		//there is also a form that does both in one step
		//this is useful when knowing the last reduction speeds up the mapping´
		
		//works, but wasteful if unordered
		ints.stream().mapToInt(Streams::digitSum).reduce(Integer::max);
		
		//more efficient
		ints.stream().reduce(0, (max, i) -> {
			if (i < max) return max;
			return Integer.max(max, digitSum(i));
		}, Integer::max);

		//reduce is stateless, but this can be slow, because Java is mostly built around mutability
		//for instance, this is pretty slow, because we create many strings
		Stream.of("Foo", "Bar", "Baz").reduce("", String::concat);
		
		//that's what collect is for, it accumulates mutable state
		Stream.of("Foo", "Bar", "Baz").collect(ArrayList::new, List::add, List::addAll);
		//of course, this is a bit cumbersome, so there are many helper functions
		Stream.of("Foo", "Bar", "Baz").collect(Collectors.toList());
		// or write your own... no more fiddling around with builders =)
		Stream.of("Foo", "Bar", "Baz").collect(toImmutableList());
	}

	private static int digitSum(int i) {
		return String.valueOf(i).chars()
				.reduce(0, (sum, s) -> sum + Integer.valueOf(s));
	}
	private static <T> Collector<T, ?, ImmutableList<T>> toImmutableList() {
		return new Collector<T, ImmutableList.Builder<T>, ImmutableList<T>>() {

			@Override
			public Supplier<Builder<T>> supplier() {
				return ()-> ImmutableList.builder();
			}

			@Override
			public BiConsumer<Builder<T>, T> accumulator() {
				return (builder, element) -> builder.add(element);
			}

			@Override
			public BinaryOperator<Builder<T>> combiner() {
				return (a, b) -> a.addAll(b.build());
			}

			@Override
			public Function<Builder<T>, ImmutableList<T>> finisher() {
				return b -> b.build();
			}

			@Override
			public Set<Characteristics> characteristics() {
				return ImmutableSet.of();
			}
		};
	}
}
