package io.springbatch.springbatch.batch.job.api;

import io.springbatch.springbatch.batch.domain.ProductVO;
import io.springbatch.springbatch.batch.rowmapper.ProductRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryGenerator {


    public static ProductVO[] getProductList(DataSource dataSource){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        List<ProductVO> productList = jdbcTemplate.query("select type from product group by type", new ProductRowMapper() {
            //재정의해서 타입만 가지고 옴
            @Override
            public ProductVO mapRow(ResultSet rs, int rowNum) throws SQLException {
                return ProductVO.builder()
                        .type(rs.getString("type"))
                        .build();
            }
        });
        return productList.toArray(new ProductVO[]{});
    }

    public static Map<String, Object> getParameterForQuery(String parameter, String value) {

        HashMap<String,Object>parameters = new HashMap<>();
        parameters.put(parameter,value);
        return parameters;
    }
}
