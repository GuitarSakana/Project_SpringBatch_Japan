package io.springbatch.springbatch.batch.classifier;

import io.springbatch.springbatch.batch.domain.ApiRequestVO;
import io.springbatch.springbatch.batch.domain.ProductVO;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

import java.util.HashMap;
import java.util.Map;

public class WriterClassifier<C,T> implements Classifier<C,T> {
    private Map<String, ItemWriter<ApiRequestVO>> wirterMap
            = new HashMap<>();

    @Override
    public T classify(C classifiable) {

        return (T)wirterMap.get(((ApiRequestVO)classifiable).getProductVO().getType());

    }

    public void setwriterMap(Map<String, ItemWriter<ApiRequestVO>> writeMap) {
        this.wirterMap = writeMap;
    }
}
