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

package co.edu.uniquindio.dhash.resource.checksum;

import co.edu.uniquindio.dhash.resource.FileResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ChecksumInputStreamCalculatorTest {
    @InjectMocks
    private ChecksumInputStreamCalculator checksumInputStreamCalculator;

    @Test
    public void calculate() {
        FileResource fileResource = FileResource.withInputStream()
                .id("resource")
                .inputStream(new ByteArrayInputStream(new byte[]{1, 2, 3}))
                .build();

        String checksum = checksumInputStreamCalculator.calculate(fileResource);

        assertThat(checksum).isEqualTo("5289df737df57326fcdd22597afb1fac");
    }
}