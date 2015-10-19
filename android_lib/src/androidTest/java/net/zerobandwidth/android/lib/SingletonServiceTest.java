package net.zerobandwidth.android.lib;

import android.content.Intent;
import android.test.ServiceTestCase;

/**
 * Exercises {@link SingletonService}.
 * TODO (incomplete... not sure how to proceed)
 */
public class SingletonServiceTest
extends ServiceTestCase<SingletonService>
{
    public SingletonServiceTest()
    { super(SingletonService.class) ; }

    public void testAcrossActivities()
    throws Exception
    {
        Intent in = new Intent( this.getContext(), SingletonService.class ) ;
        SingletonService.Binder bind =
                (SingletonService.Binder)(this.bindService(in)) ;
        SingletonService srv = bind.getService() ;
    }
}
