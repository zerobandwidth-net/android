package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import java.util.GregorianCalendar;

/**
 * Provides a lens for {@link GregorianCalendar}. Since the superclass is
 * templatized, there is no additional implementation required.
 */
public class GregorianCalendarLens
extends CalendarLens<GregorianCalendar>
implements Refractor<GregorianCalendar>
{}
