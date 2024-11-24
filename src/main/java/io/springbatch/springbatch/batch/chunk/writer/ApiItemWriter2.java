package io.springbatch.springbatch.batch.chunk.writer;

import io.springbatch.springbatch.batch.domain.ApiRequestVO;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class ApiItemWriter2 implements ItemWriter<ApiRequestVO> {
    @Override
    public void write(Chunk<? extends ApiRequestVO> chunk) throws Exception {

    }
}
