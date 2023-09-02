package com.ponderaAssembly.core.plugin.dependency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * This annotation is used to mark a method as factory for dependency. <br>
 * The method annotated with this annotation returns an instance of the specified dependency.
 *
 * <p> Constraints </p>
 * <ul>
 *  <li> Class has to have DependencyFactory annotation for this annotation to work. </li>
 *  <li> Method name is used as the identifier for the dependency. Only one instance of Dependency exist at runtime
 *      this means that you should have unique method names (at project level) - only ones marked with this annotation.
 *  </li>
 *  <li> Method must have no parameters.</li>
 * </ul>
 * Example:
 * <pre> {@code
 *
 * @DependencyFactory
 * public class MyDependency {
 *     @DependencyInstance()
 *     public String testIt() {
 *         return "Hello world";
 *     }
 * }
 *
 * @Plugin(id = "com.test.HttpSendExtended")
 * public class HttpSendExtended {
 *
 *     public HttpSendExtended(@Dependency(name = "testIt") String test) {
 *         System.out.println("HttpSendExtended constructor param " + test);
 *     }
 * }
 * }</pre>
 *
 * Output: {@code HttpSendExtended constructor param Hello world }
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DependencyInstance {
}
