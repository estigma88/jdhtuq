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
import co.edu.uniquindio.storage.resource.ProgressStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;

@RunWith(MockitoJUnitRunner.class)
public class ChecksumInputStreamCalculatorTest {
    @Mock
    private ProgressStatus progressStatus;
    @InjectMocks
    private ChecksumInputStreamCalculator checksumInputStreamCalculator;

    @Test
    public void calculate() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{1, 2, 3});

        String checksum = checksumInputStreamCalculator.calculate(inputStream, 10L, progressStatus);

        assertThat(checksum).isEqualTo("5289DF737DF57326FCDD22597AFB1FAC");
    }
}