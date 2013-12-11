/* Copyright 2012 predic8 GmbH, www.predic8.com
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License. */

package com.predic8.wsdl.diff

import com.predic8.wsdl.*
import com.predic8.xml.util.*

class OnlyOnePortTypeDiffGeneratorTest extends GroovyTestCase {

	def parser
	Definitions wsdl1
	Definitions wsdl2

	void setUp() {
		parser = new WSDLParser(resourceResolver : new ClasspathResolver())
		wsdl1 = parser.parse('diff/message-parts/BLZService1.wsdl')
		wsdl2 = parser.parse('diff/message-parts/BLZService2.wsdl')
	}

	void testPortTypeOperationChanges() {
		def diffs = compare(wsdl2, wsdl1)
		assert diffs*.dump().toString().contains('PortType BLZServicePortType:')
		assert diffs*.dump().toString().contains('Element has changed from {http://thomas-bayer.com/blz/}getBank to an invalid element.')
	}

	void testPortTypeNameAndOperationChanges() {
		wsdl2.portTypes[0].name = 'Test'
		def diffs = compare(wsdl2, wsdl1)
		assert diffs*.dump().toString().contains('PortType name has changed from Test to BLZServicePortType:')
		assert diffs*.dump().toString().contains('Element has changed from {http://thomas-bayer.com/blz/}getBank to an invalid element.')
	}
	
	void testPTNameChanges() {
		wsdl2 = parser.parse('diff/message-parts/BLZService1.wsdl')
		wsdl2.portTypes[0].name = 'Test'
		def diffs = compare(wsdl1, wsdl2)
		assert diffs[0].diffs[0].description == "PortType name has changed from BLZServicePortType to Test."
		diffs = compare(wsdl2, wsdl1)
		assert diffs[0].diffs[0].description == "PortType name has changed from Test to BLZServicePortType."
	}
	
	void testPTWithNoChanges() {
		wsdl2 = parser.parse('diff/message-parts/BLZService1.wsdl')
		def diffs = compare(wsdl1, wsdl2)
		assert !diffs
		diffs = compare(wsdl2, wsdl1)
		assert !diffs
	}
	
	private def compare(a, b) {
		new WsdlDiffGenerator(a: a, b: b).compare()
	}
}
