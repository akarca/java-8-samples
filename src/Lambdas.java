import static java.lang.Integer.sum;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;

import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.common.cache.CacheLoader;

public class Lambdas {
	public static void main(String[] args) {
		List<Integer> ints = asList(1, 2, 3, 4, 5, 6, 7, 8, 9 ,10);
		
		///////////
		//Syntax//
		//////////
		
		//1 to 10
		ints.forEach(i -> {System.out.println(i);});
		
		ints.stream()
			//simple syntax for small expressions
			.filter(i-> i % 2 == 0)
			//block syntax for complicated expressions
			.mapToInt(i-> {
				int square = i * i;
				return square - 1;
			})
			//you can explicitly specify types
			.filter((int i) -> i <100)
			//multiple parameters need parentheses
			.reduce((i, j) -> i * j);
		
		///////////////////////
		//Function references//
		///////////////////////
		
		//we already have lots of functions, so we want to reuse them
		//this is what function references are for
		ints.forEach(System.out::println);
		
		//all kinds of callable things can be used as lambdas
		Function<Integer, String> staticReference = String::valueOf;
		Function<Object, String> nonStaticReference = Object::toString;
		Function<String, BigDecimal> constructorReference = BigDecimal::new;
		StringBuilder builder = new StringBuilder();
		Consumer<String> append = builder::append;
		
		////////////////////////
		//Functional interfaces/
		////////////////////////
		
		//lambdas are coerced to interfaces with one abstract method
		//this eliminates most anonymous inner classes
		Runnable run = ()->{System.out.println("FooBar");};
		run.run();
		
		Button button = new Button();
		//compare this
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getWhen());
			}
		});
		//to this
		button.addActionListener(e->{
			System.out.println(e.getWhen());
		});
		
		
		//or even
		new ThreadLocal<String>() {
			protected String initialValue() {
				return "Foo";
			};
		};
		//versus
		ThreadLocal.withInitial(()->"Foo");
		
		//java.util.function has lots of generally useful interfaces
		Function<String, Integer> fun = (s)-> s.length();
		BinaryOperator<Integer> plus = Integer::sum;
		Supplier<String> greeter = ()-> "Hello";
		Predicate<Integer> even = i-> i % 2 == 0;
		//the list goes on...
		
		//////////
		//Gotchas/
		//////////
		
		//captured variables must be final or effectively final
		int i = 0;
//		ints.forEach(j-> {i += j;});
		
		//not transparent to exceptions
		CharArrayWriter out = new CharArrayWriter();
		try {
//			Stream.of("1", "2", "3").forEach(out::write);
		} catch (Exception e1) {
			// can't catch it, needs to be handled inside the closure
		}
		
		//this does not compile, even though CacheLoader has exactly one abstract method
//		CacheLoader<String, Integer> loader = s->s.length();
		
		//however, there is a workaround:
		//note that Eclipse is still very Beta, so type inference breaks all over the place ;)
		CacheLoader.from((String s)->s.length());
		
	}
}
