package org.leucam.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductDTO implements Comparable<ProductDTO> {
    private Long productId;
    private String name;
    private String description;
    private String fileId;
    private String filePath;
    private Boolean active = Boolean.TRUE;

    @Override
    public String toString() {
        return  " Nome :'" + name + '\'' +
                "\nDescrizione :'" + description + '\'';
    }

    @Override
    public int compareTo(ProductDTO productDTO) {
        return this.name.compareTo(productDTO.name);
    }
}
