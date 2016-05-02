package org.cache2k.configuration;

/*
 * #%L
 * cache2k API
 * %%
 * Copyright (C) 2000 - 2016 headissue GmbH, Munich
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.cache2k.Cache2kBuilder;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Helper class to capture generic types into a type descriptor. This is used to provide
 * the cache with detailed type information of the key and value objects.
 *
 * Example usage with {@link Cache2kBuilder}:<pre>   {@code
 *
 *   CacheBuilder.newCache().valueType(new CacheType<List<String>(){}).build()
 * }</pre>
 *
 * This constructs a cache with the known type <code>List&lt;String></code> for its value.
 *
 * @see <a href="https://github.com/google/guava/wiki/ReflectionExplained">Google Guava CacheType explaination</a>
 *
 * @author Jens Wilke
 */
public class CacheType<T> implements CacheTypeDescriptor<T> {

  private CacheTypeDescriptor descriptor;

  protected CacheType() {
    descriptor = of(((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
  }

  public static CacheTypeDescriptor of(Type t) {
    if (t instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType) t;
      Class c = (Class) pt.getRawType();
      CacheTypeDescriptor[] ta = new CacheTypeDescriptor[pt.getActualTypeArguments().length];
      for (int i = 0; i < ta.length; i++) {
        ta[i] = of(pt.getActualTypeArguments()[i]);
      }
      return new CacheTypeDescriptor.OfGeneric(c, ta);
    } else if (t instanceof GenericArrayType) {
      GenericArrayType gat = (GenericArrayType) t;
      return new CacheTypeDescriptor.OfArray(of(gat.getGenericComponentType()));
    }
    Class c = (Class) t;
    if (c.isArray()) {
      return new CacheTypeDescriptor.OfArray(of(c.getComponentType()));
    }
    return new CacheTypeDescriptor.OfClass(c);
  }

  @Override
  public CacheTypeDescriptor getBeanRepresentation() {
    return descriptor;
  }

  @Override
  public CacheTypeDescriptor getComponentType() {
    return descriptor.getComponentType();
  }

  @Override
  public Class<T> getType() {
    return descriptor.getType();
  }

  @Override
  public CacheTypeDescriptor[] getTypeArguments() {
    return descriptor.getTypeArguments();
  }

  @Override
  public String getTypeName() {
    return descriptor.getTypeName();
  }

  @Override
  public boolean hasTypeArguments() {
    return descriptor.hasTypeArguments();
  }

  @Override
  public boolean isArray() {
    return descriptor.isArray();
  }

  @Override
  public boolean equals(Object o) {
    return descriptor.equals(o);
  }

  @Override
  public int hashCode() {
    return descriptor.hashCode();
  }

}