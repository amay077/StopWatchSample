﻿using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using System.Reflection;

namespace StopWatchApp.iOS.Extensions.Internal
{
	internal static class AccessorCache<TType>
	{
		private static readonly Dictionary<string, Delegate> getCache = new Dictionary<string, Delegate>();
		private static readonly Dictionary<string, Delegate> setCache = new Dictionary<string, Delegate>();

		public static Func<TType, TProperty> LookupGet<TProperty>(Expression<Func<TType, TProperty>> propertySelector, out string propertyName)
		{
			propertyName = ((MemberExpression)propertySelector.Body).Member.Name;
			Delegate accessor;

			lock (getCache)
			{
				if (!getCache.TryGetValue(propertyName, out accessor))
				{
					accessor = propertySelector.Compile();
					getCache.Add(propertyName, accessor);
				}
			}

			return (Func<TType, TProperty>)accessor;
		}

		public static Action<TType, TProperty> LookupSet<TProperty>(Expression<Func<TType, TProperty>> propertySelector, out string propertyName)
		{
			propertyName = ((MemberExpression)propertySelector.Body).Member.Name;
			Delegate accessor;

			lock (setCache)
			{
				if (!setCache.TryGetValue(propertyName, out accessor))
				{
					accessor = CreateSetAccessor(propertySelector);
					setCache.Add(propertyName, accessor);
				}
			}

			return (Action<TType, TProperty>)accessor;
		}

		private static Delegate CreateSetAccessor<TProperty>(Expression<Func<TType, TProperty>> propertySelector)
		{
			var propertyInfo = (PropertyInfo)((MemberExpression)propertySelector.Body).Member;
			var selfParameter = Expression.Parameter(typeof(TType), "self");
			var valueParameter = Expression.Parameter(typeof(TProperty), "value");
			var body = Expression.Assign(Expression.Property(selfParameter, propertyInfo), valueParameter);
			var lambda = Expression.Lambda<Action<TType, TProperty>>(body, selfParameter, valueParameter);
			return lambda.Compile();
		}
	}
}
