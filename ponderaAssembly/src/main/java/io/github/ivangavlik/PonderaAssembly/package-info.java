/**
 * The "PonderaAssembly" bridges the gap in the plugin framework landscape,
 * providing developers with a user-friendly, lightweight, and modern solution
 * for integrating plugins.
 *
 * <p> Has two components (packages) </p>
 * <ul>
 *    <li> Plugin </li>
 *    <li> Runtime </li>
 * </ul>
 *
 * <p>Plugin</p>
 *  Provides annotations for marking plugins and defining lifecycle hooks. Developers use these annotations in client code to identify and manage plugins.
 *
 *  <p>Runtime</p>
 *  Has {@link  io.github.ivangavlik.PonderaAssembly.runtime.PonderaMicrokernel}," a runtime responsible for
 *  managing plugin lifecycles. It coordinates plugin loading, initialization, and communication.<br>
 *  Event is Runtime sub-module responsible for facilitating event-based communication between plugins.
 *  Events enable plugins to interact, exchange data, and trigger actions.
 *
 */
package io.github.ivangavlik.PonderaAssembly;
