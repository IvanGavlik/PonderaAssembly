package io.github.ivangavlik.PonderaAssembly.plugin.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Marks a field as an extension point. <br>
 * An extension point defines a place where other plugins can extend the functionality. <br>
 * Plugins that provide extensions for this extension point are injected into the field by framework.
 *
 * <p> Constraints </p>
 * <ul>
 *  <li> Filed has to be List. </li>
 *  <li> List generic parameter has to be interface (it defines contract).</li>
 * </ul>
 * Example:
 * <pre> <code>
 * // Defines the contract for sending messages.
 * public interface HttpSendInf {
 *     // Sends a message.
 *     void sendMsg()
 * }
 *
 * //Plugin class that manages the extension points for HttpSendInf extensions.
 * {@literal @}Plugin(id = "com.ig.plugin.HttpSend")
 * public class HttpSend {
 *  // The extension point where HttpSendInf extensions will be injected.
 *  {@literal @}ExtensionPoint
 *  List{@literal <}HttpSendInf{@literal >} inf;
 *
 *  void someAction() {
 *      inf.foreach(el {@literal ->} el.sendMsg())
 *  }
 * }
 *
 * // Extended implementation of HttpSendInf.
 * {@literal @}Plugin(id = "com.ig.plugin.HttpSendExtended")
 * public class HttpSendExtended implements HttpSendInf {
 *  // Overrides the sendMsg method from HttpSendInf to provide extended functionality.
 *  {@literal @}Override
 *  public void sendMsg() {
 *      System.out.println("HttpSendExtended extended");
 *  }
 * }
 * </code> </pre>
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtensionPoint {
}
