package io.springbatch.springbatch.batch.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table
public class Product {

    @Id
    private Long id;
    private String name;
    private int price;
    private String type;
}
