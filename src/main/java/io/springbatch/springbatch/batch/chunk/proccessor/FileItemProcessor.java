package io.springbatch.springbatch.batch.chunk.proccessor;

import io.springbatch.springbatch.batch.domain.Product;
import io.springbatch.springbatch.batch.domain.ProductVO;
import org.modelmapper.ModelMapper;
import org.springframework.batch.item.ItemProcessor;

public class FileItemProcessor implements ItemProcessor<ProductVO, Product> {

    @Override
    //processor 비즈니스 로직
    public Product process(ProductVO item) throws Exception {

        //객체간 변환 자동화 해주는 객체 (의존관계 추가 필요)pop.xml
        ModelMapper modelMapper = new ModelMapper();
        Product product = modelMapper.map(item, Product.class);

        return product;
    }
}
