/*
 *    Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.executor.loader;

import java.io.ObjectStreamException;

/**
 * @author Eduardo Macarron
 */
public interface WriteReplaceInterface {

  /**
   *  it's invoked after {@link AbstractSerialStateHolder#readResolve()},
   *  likes {@link AbstractSerialStateHolder#readResolve()}
   *  实现writeReplace就不要实现writeObject了，因为writeReplace的返回值会被自动写入输出流中，就相当于自动这样调用：writeObject(writeReplace());
   *  并且writeReplace的返回值（对象）必须是可序列话的，
   * @return
   * @throws ObjectStreamException
   */
  Object writeReplace() throws ObjectStreamException;

}
