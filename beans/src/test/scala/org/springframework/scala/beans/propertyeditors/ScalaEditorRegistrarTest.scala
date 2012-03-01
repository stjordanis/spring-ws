/*
 * Copyright 2011-2012 the original author or authors.
 *
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
 */

package org.springframework.scala.beans.propertyeditors

import org.scalatest.FunSuite
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * @author Arjen Poutsma
 */
class ScalaEditorRegistrarTest extends FunSuite {

	val applicationContext = new ClassPathXmlApplicationContext("scalaEditorRegistrarTest.xml", getClass)

	test("primitives") {
		val bean = applicationContext.getBean("primitivesBean", classOf[PrimitivesBean])
		assert(bean.byte == 42)
		assert(bean.short == 42)
		assert(bean.int == 42)
		assert(bean.long == 42)
		assert(bean.char == '4')
		assert(bean.float == 42)
		assert(bean.double == 42)
		assert(bean.bool)
		assert(bean.string == "foo")
	}

	test("types") {
		val bean = applicationContext.getBean("typesBean", classOf[ScalaTypesBean])
		assert(bean.regex.toString() == "\\d")
	}

	test("collections") {
		val bean = applicationContext.getBean("collectionBean", classOf[ScalaCollectionsBean])
		assert(!bean.seq.isEmpty)
		assert(bean.list.contains("one"))
		assert(bean.list.contains("two"))
		assert(bean.list.contains("three"))

		bean.arrayBuffer1.remove(1)

		assert(bean.arrayBuffer1.contains("one"))
		assert(!bean.arrayBuffer1.contains("two"))
		assert(bean.arrayBuffer1.contains("three"))

		assert(bean.arrayBuffer2.contains("one"))
		assert(bean.arrayBuffer2.contains("two"))
		assert(bean.arrayBuffer2.contains("three"))
	}


}