/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.indexmanagement.indexstatemanagement.transport.action.explain

import org.opensearch.common.io.stream.BytesStreamOutput
import org.opensearch.common.io.stream.StreamInput
import org.opensearch.common.unit.TimeValue
import org.opensearch.indexmanagement.indexstatemanagement.model.SearchParams
import org.opensearch.indexmanagement.indexstatemanagement.util.DEFAULT_INDEX_TYPE
import org.opensearch.test.OpenSearchTestCase

class ExplainRequestTests : OpenSearchTestCase() {

    fun `test explain request`() {
        val indices = listOf("index1", "index2")
        val local = true
        val masterTimeout = TimeValue.timeValueSeconds(30)
        val params = SearchParams(0, 20, "sort-field", "asc", "*")
        val showPolicy = false
        val req = ExplainRequest(indices, local, masterTimeout, params, showPolicy, DEFAULT_INDEX_TYPE)

        val out = BytesStreamOutput()
        req.writeTo(out)
        val sin = StreamInput.wrap(out.bytes().toBytesRef().bytes)
        val newReq = ExplainRequest(sin)
        assertEquals(indices, newReq.indices)
        assertEquals(local, newReq.local)
    }

    fun `test explain policy request with non default index type and multiple indices fails`() {
        val indices = listOf("index1", "index2")
        val local = true
        val masterTimeout = TimeValue.timeValueSeconds(30)
        val params = SearchParams(0, 20, "sort-field", "asc", "*")
        val showPolicy = false
        val req = ExplainRequest(indices, local, masterTimeout, params, showPolicy, "non-existent-index-type")

        val actualException: String? = req.validate()?.validationErrors()?.firstOrNull()
        val expectedException: String = ExplainRequest.MULTIPLE_INDICES_CUSTOM_INDEX_TYPE_ERROR
        assertEquals("Add policy request should have failed validation with specific exception", actualException, expectedException)
    }
}
