package org.apache.ibatis.type;

import org.junit.Test;

import java.net.URI;
import java.util.List;

public class TypeReferenceTest {

    @Test
    public void testTypeReference() {
        TypeReference<List<URI>> type = new TypeReference<List<URI>>(){};
        type.getRawType();
    }

}
