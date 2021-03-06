package net.zer0bandwidth.android.lib.database.sqlitehouse.refractor;

import java.util.ArrayList;

/**
 * A canonical implementation of {@link StringCollectionLens} in which the
 * collection type is an {@link ArrayList} and the delimiter is a form feed
 * character ({@code '\f'}).
 * @since zer0bandwidth-net/android 0.1.5 (#42)
 */
public class FormFeedDelimStringsListLens
extends StringCollectionLens<ArrayList<String>>
implements Refractor<ArrayList<String>>
{
	@Override
	protected String getDelimiter()
	{ return "\f" ; }

	@Override
	protected ArrayList<String> getCollectionInstance()
	{ return new ArrayList<>() ; }
}
