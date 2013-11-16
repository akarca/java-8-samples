import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.LongAccumulator;

public class Other {
	public static void main(String[] args) {

		// java.time - if you know Joda-time, you know it
		LocalDate today = LocalDate.of(2013, 11, 15);
		System.out.println(today.getDayOfWeek());
		LocalDate firstOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
		System.out.println(firstOfMonth);
		
		//many additions to collections as a result of Mixins and Lambdas
		
		List<Integer> list = Arrays.asList(3,1,6,2);
		//sort is now an instance method and can be overriden
		//this allows big performance gains e.g. for array lists
		list.sort(Comparator.naturalOrder());
		System.out.println(list);
		
		//the same fact now allows synchronized collections to be iterated atomically
		Collections.synchronizedList(list).sort(Comparator.naturalOrder());//this is atomic
		
		//there are countless other API additions, especially for concurrency
		//I don't know much about that topic, so just a simple example
		//this can be used from many threads to compute a sum without locking
		new LongAccumulator(Long::sum, 0);
		
		//lots of APIs where improved to work with Lambdas
		Comparator<Person> comp = Comparator.comparing(Person::firstName)
				.thenComparing(Person::lastName);
		
		//and there are some things that should have been done ages ago ;)
		String.join(", ", "Foo", "Bar", "Baz");
	}
	
	public class Person {
		public String firstName() {
			return null;
		}
		public String lastName() {
			return null;
		}
	}
}
