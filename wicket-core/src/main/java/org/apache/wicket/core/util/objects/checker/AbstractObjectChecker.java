package org.apache.wicket.core.util.objects.checker;

import java.util.List;

import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.lang.Generics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base class for IObjectChecker implementations which handles the logic
 * for checking type exclusions.
 */
public abstract class AbstractObjectChecker implements IObjectChecker
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractObjectChecker.class);

	private final List<Class<?>> exclusions;

	protected AbstractObjectChecker()
	{
		this(Generics.<Class<?>>newArrayList());
	}

	protected AbstractObjectChecker(List<Class<?>> exclusions)
	{
		this.exclusions = Args.notNull(exclusions, "exclusions");
	}

	@Override
	public Result check(Object object)
	{
		Result result = Result.SUCCESS;

		if (object != null && getExclusions().isEmpty() == false)
		{
			Class<?> objectType = object.getClass();
			for (Class<?> excludedType : getExclusions())
			{
				if (excludedType.isAssignableFrom(objectType))
				{
					LOGGER.debug("Object with type '{}' wont be checked because its type is excluded ({})",
							objectType, excludedType);
					return result;
				}
			}
		}

		result = doCheck(object);

		return result;
	}

	/**
	 * The implementations should make the specific check on the object.
	 * @param object
	 *      the object to check
	 * @return the {@link Result result} of the specific check
	 */
	protected Result doCheck(Object object)
	{
		return Result.SUCCESS;
	}

	@Override
	public List<Class<?>> getExclusions()
	{
		return exclusions;
	}
}
