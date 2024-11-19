package io.springbatch.springbatch.batch.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//파일에서 데이터를 읽어서 저장할 객체(DTO)
public class ProductVO {

    private Long id;
    private String name;
    private int price;
    private String type;

}
