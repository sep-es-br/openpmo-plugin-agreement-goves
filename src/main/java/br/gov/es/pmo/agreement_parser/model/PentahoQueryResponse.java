package br.gov.es.pmo.agreement_parser.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PentahoQueryResponse {

    private List<List<Object>> resultset;
    private List<Map<String, Object>> metadata;

    public List<List<Object>> getResultset() {
        return resultset == null ? Collections.emptyList() : resultset;
    }

    public void setResultset(List<List<Object>> resultset) {
        this.resultset = resultset;
    }

    public List<Map<String, Object>> getMetadata() {
        return metadata == null ? Collections.emptyList() : metadata;
    }

    public void setMetadata(List<Map<String, Object>> metadata) {
        this.metadata = metadata;
    }
}
