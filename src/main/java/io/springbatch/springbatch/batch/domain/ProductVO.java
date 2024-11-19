package io.springbatch.springbatch.batch.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ProductVO {

    @Id
    @GeneratedValue
    private Long id;
}
