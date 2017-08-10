package net.zerobandwidth.android.lib.nonsense;

import android.content.Context;

import net.zerobandwidth.android.lib.R;

import java.util.Random;

/**
 * Makes up silly soundalikes for a certain successful actor whose name is
 * apparently amusing to some people. Who can tell what will capture the fickle
 * imagination of the Internet?
 *
 * <p>This class makes use of several array resources:</p>
 *
 * <ul>
 *     <li>{@link R.array#asNonsenseBenedict}</li>
 *     <li>{@link R.array#asNonsensePrefixBene}</li>
 *     <li>{@link R.array#asNonsenseSuffixDict}</li>
 *     <li>{@link R.array#asNonsenseCumberbatch}</li>
 *     <li>{@link R.array#asNonsensePrefixCumber}</li>
 *     <li>{@link R.array#asNonsenseSuffixBatch}</li>
 * </ul>
 *
 * <p>Applications using this class may define competing resources which will
 * override the defaults when the APK is compiled.</p>
 *
 * @since zerobandwidth-net/android 0.1.5 (#33)
 * @see <a href="http://imgur.com/gallery/IhTQW">Imgur (gallery)</a>
 */
public class BuildertextJavaclass
implements NonsenseGenerator
{
	/** The internal RNG of the builder. */
	protected static final Random RANDOM = new Random() ;

	/** A context in which the string resources are available. */
	protected Context m_ctx = null ;

	/** Indicates whether the resource arrays are cached. */
	protected boolean m_bCached = false ;

	/** Caches one of the resource arrays. */
	protected String[] m_asBenedict = null ;
	/** Caches one of the resource arrays. */
	protected String[] m_asPrefixBene = null ;
	/** Caches one of the resource arrays. */
	protected String[] m_asSuffixDict = null ;
	/** Caches one of the resource arrays. */
	protected String[] m_asCumberbatch = null ;
	/** Caches one of the resource arrays. */
	protected String[] m_asPrefixCumber = null ;
	/** Caches one of the resource arrays. */
	protected String[] m_asSuffixBatch = null ;

	public BuildertextJavaclass( Context ctx )
	{ this.setContext(ctx) ; }

	@Override
	public NonsenseGenerator setContext( Context ctx )
	{ m_ctx = ctx ; return this ; }

	/**
	 * Caches the resource arrays in this instance.
	 * Consumed by {@link #getString()}, so that we don't bother building arrays
	 * until the first time the class is actually used.
	 * @return (fluid)
	 */
	protected BuildertextJavaclass cacheArrays()
	{
		if( m_bCached ) return this ; // trivially; it's already been done
		m_asBenedict = m_ctx.getResources()
				.getStringArray( R.array.asNonsenseBenedict ) ;
		m_asPrefixBene = m_ctx.getResources()
				.getStringArray( R.array.asNonsensePrefixBene ) ;
		m_asSuffixDict = m_ctx.getResources()
				.getStringArray( R.array.asNonsenseSuffixDict ) ;
		m_asCumberbatch = m_ctx.getResources()
				.getStringArray( R.array.asNonsenseCumberbatch ) ;
		m_asPrefixCumber = m_ctx.getResources()
				.getStringArray( R.array.asNonsensePrefixCumber ) ;
		m_asSuffixBatch = m_ctx.getResources()
				.getStringArray( R.array.asNonsenseSuffixBatch ) ;
		m_bCached = true ;
		return this ;
	}

	@Override
	public String getString()
	{
		StringBuilder sb = new StringBuilder() ;
		this.cacheArrays() ;
		int nCogTotal = m_asBenedict.length
				+ m_asPrefixBene.length + m_asSuffixDict.length ;
		int nCogRandom = RANDOM.nextInt( nCogTotal ) ;
		if( nCogRandom < m_asBenedict.length )
		{ // Use one of the whole-word replacements.
			sb.append( m_asBenedict[nCogRandom] ) ;
		}
		else
		{
			sb.append( m_asPrefixBene[
					RANDOM.nextInt( m_asPrefixBene.length ) ] ) ;
			sb.append( m_asSuffixDict[
					RANDOM.nextInt( m_asSuffixDict.length ) ] ) ;
		}
		sb.append( " " ) ;
		int nSurTotal = m_asCumberbatch.length
				+ m_asPrefixCumber.length + m_asSuffixBatch.length ;
		int nSurRandom = RANDOM.nextInt( nSurTotal ) ;
		if( nSurRandom < m_asCumberbatch.length )
		{ // Use one of the whole-word replacements.
			sb.append( m_asCumberbatch[nSurRandom] ) ;
		}
		else
		{
			sb.append( m_asPrefixCumber[
					RANDOM.nextInt( m_asPrefixCumber.length ) ] ) ;
			sb.append( m_asSuffixBatch[
					RANDOM.nextInt( m_asSuffixBatch.length ) ] ) ;
		}

		return sb.toString() ;
	}
}
