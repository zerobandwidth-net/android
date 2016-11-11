package net.zerobandwidth.android.lib.services;

/**
 * Defines the public API of the {@link SingletonService} class. This interface
 * is defined separately because the inner
 * {@link SingletonService.Connection}
 * class also provides it; comments about the methods of the interface can thus
 * be defined and shared by all implementations.
 */
public interface SingletonServiceInterface
{
    /**
     * Returns the singleton instance of the specified class. If no singleton
     * has been created yet, then {@code null} is returned. If a casting error
     * occurs internally, then the method returns {@code null} but also logs a
     * warning.
     * @param cls the class for which a singleton should be returned
     * @param <T> the class for which a singleton should be returned
     * @return the singleton instance of the class, or {@code null} if none
     *  can be obtained
     */
    <T> T get( Class<T> cls ) ;

    /**
     * Specifies the singleton instance of a given class. If no previous
     * singleton has been set, then {@code null} is returned. If a casting
     * error occurs internally while trying to pass back the previous instance,
     * then {@code null} is still returned, but an error is also written to the
     * logs.
     * @param cls the class for which a singleton should be set
     * @param oInstance the instance to be proclaimed as the singleton instance
     * @param <T> the class for which a singleton should be set
     * @return any previously-set singleton instance for that class, or
     *  {@code null} if none can be obtained
     */
    <T> T put( Class<T> cls, T oInstance ) ;

    /**
     * Gets the current singleton instance for the specified class, or stores
     * the supplied alternative as the singleton if none was previously set.
     * @param cls the class for which a singleton should be returned
     * @param oFallback an instance to set if no previous instance is found
     * @param <T> the class for which a singleton should be returned
     * @return the singleton instance of the class, or the supplied alternative
     *  if none can be obtained
     */
    <T> T getOrPut( Class<T> cls, T oFallback ) ;

    /**
     * Indicates whether a singleton instance has been set for the specified
     * class.
     * @param cls the class for which a singleton might be set
     * @param <T> the class for which a singleton might be set
     * @return {@code true} if an instance has been set for the class
     */
    <T> boolean hasInstanceFor( Class<T> cls ) ;
}
