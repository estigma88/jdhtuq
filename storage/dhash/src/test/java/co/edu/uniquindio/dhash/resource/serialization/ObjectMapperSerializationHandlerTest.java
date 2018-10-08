/*
 *  DHash project implement a storage management
 *  Copyright (C) 2010 - 2018  Daniel Pelaez
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package co.edu.uniquindio.dhash.resource.serialization;

import co.edu.uniquindio.dhash.resource.FileResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Java6Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class ObjectMapperSerializationHandlerTest {
    private ObjectMapperSerializationHandler objectSerializationHandler;

    @Before
    public void before(){
        this.objectSerializationHandler = new ObjectMapperSerializationHandler(new ObjectMapper());
    }

    @Test
    public void encode() {
        FileResource resource = FileResource.withInputStream()
                .id("resource")
                .size(10L)
                .inputStream(new ByteArrayInputStream("data".getBytes()))
                .checkSum("checkSum")
                .build();

        String result = objectSerializationHandler.encode(resource);

        assertThat(result).isEqualTo("{\"co.edu.uniquindio.dhash.resource.FileResource\":{\"id\":\"resource\",\"size\":10,\"checkSum\":\"checkSum\"}}");
    }

    @Test
    public void decode() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("data".getBytes());

        FileResource resource = FileResource.withInputStream()
                .id("resource")
                .size(10L)
                .inputStream(inputStream)
                .checkSum("checkSum")
                .build();

        FileResource result = (FileResource) objectSerializationHandler.decode("{\"co.edu.uniquindio.dhash.resource.FileResource\":{\"id\":\"resource\",\"size\":10,\"checkSum\":\"checkSum\"}}", inputStream);

        assertThat(result).isEqualTo(resource);
    }

}